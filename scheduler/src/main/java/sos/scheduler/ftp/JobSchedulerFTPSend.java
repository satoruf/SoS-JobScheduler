/**
 * Copyright (C) 2014 BigLoupe http://bigloupe.github.io/SoS-JobScheduler/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
/********************************************************* begin of preamble
**
** Copyright (C) 2003-2012 Software- und Organisations-Service GmbH. 
** All rights reserved.
**
** This file may be used under the terms of either the 
**
**   GNU General Public License version 2.0 (GPL)
**
**   as published by the Free Software Foundation
**   http://www.gnu.org/licenses/gpl-2.0.txt and appearing in the file
**   LICENSE.GPL included in the packaging of this file. 
**
** or the
**  
**   Agreement for Purchase and Licensing
**
**   as offered by Software- und Organisations-Service GmbH
**   in the respective terms of supply that ship with this file.
**
** THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
** IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
** THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
** PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
** BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
** CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
** SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
** INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
** CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
** ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
** POSSIBILITY OF SUCH DAMAGE.
********************************************************** end of preamble*/
package sos.scheduler.ftp;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import sos.configuration.SOSConfiguration;
import sos.net.sosftp.SOSFTPCommandSend;
import sos.scheduler.job.JobSchedulerJobAdapter;
import sos.spooler.Order;
import sos.spooler.Variable_set;
import sos.util.SOSSchedulerLogger;
import sos.util.SOSString;

import com.sos.JSHelper.Basics.JSVersionInfo;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.JSHelper.Options.JSOptionsClass;
import com.sos.i18n.annotation.I18NResourceBundle;

/**
 * FTP File Transfer
 *
 * @author Andreas P�schel
 * @author M�r�vet �ks�z
 * 
 * 2009-02-22: added SOSCommand.getExternalPassword
 * 2009-02-00: Redesign from Configuration and call SOSFTP
 * 
 * @Version $Id: JobSchedulerFTPSend.java 16869 2012-03-26 10:08:16Z oh $
 */
@I18NResourceBundle(baseName = "com_sos_net_messages", defaultLocale = "en")
public class JobSchedulerFTPSend extends JobSchedulerJobAdapter {

	
	private static final String	conOrderParameterFTP_RESULT_ZERO_BYTE_FILES	= "ftp_result_zero_byte_files";
	private static final String	conOrderParameterFTP_RESULT_FILES	= "ftp_result_files";

	private static final String	conParameterPARALLEL		= "parallel";
	private static final String	conParameterCHECK_PARALLEL	= "check_parallel";
	private static final String	conParameterPROFILE	= "profile";
	private static final String	conParameterSETTINGS	= "settings";

	private SOSString			sosString					= new SOSString();
	private final String		conSVNVersion				= "$Id: JobSchedulerFTPSend.java 16869 2012-03-26 10:08:16Z oh $";
	private JSOptionsClass		objOptions = new JSOptionsClass();
	
	public boolean spooler_process() {

		try {
			super.spooler_process();
		}
		catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		boolean checkParallel = false;
		boolean parallelTransfer = false;
		String parallelTransferCheckSetback = "00:00:60";
		int parallelTransferCheckRetry = 60;
		Variable_set params = null;
		boolean rc = false;
		boolean isFilePath = false;
		boolean orderSelfDestruct = false;
//		Properties schedulerParams = null;
		HashMap <String, String> schedulerParams = null;

		try {
			this.setLogger(new SOSSchedulerLogger(spooler_log));
			getLogger().info(JSVersionInfo.getVersionString());
			getLogger().info(conSVNVersion);

			try { // to get the job and order parameters
				params = getParameters();
				schedulerParams = objOptions.DeletePrefix(super.getSchedulerParameterAsProperties(params), "ftp_");
				schedulerParams.putAll(getParameterDefaults(params));
				checkParallel = sosString.parseToBoolean(sosString.parseToString(schedulerParams.get(conParameterCHECK_PARALLEL)));
				parallelTransfer = sosString.parseToBoolean(sosString.parseToString(schedulerParams.get(conParameterPARALLEL)));

			}
			catch (Exception e) {
				throw new JobSchedulerException("could not process job parameters: " + e.getMessage(), e);
			}

			try {
				if (checkParallel && spooler_job.order_queue() != null) {
					boolean bSuccess = true;
					String[] paramNames = sosString.parseToString(spooler.variables().names()).split(";");
					for (int i = 0; i < paramNames.length; i++) {

						if (paramNames[i].startsWith("ftp_check_send_" + normalize(spooler_task.order().id()) + ".")) {
							if (sosString.parseToString(spooler.var(paramNames[i])).equals("0")) {
								// Anzahl der Wiederholungen merken
								String sRetry = sosString.parseToString(spooler.variables().var("cur_transfer_retry" + normalize(spooler_task.order().id())));
								int retry = sRetry.length() == 0 ? 0 : Integer.parseInt(sRetry);
								--retry;
								spooler.variables().set_var("cur_transfer_retry" + normalize(spooler_task.order().id()), String.valueOf(retry));
								if (retry == 0) {
									getLogger().debug("terminated cause max order setback reached: " + paramNames[i]);
									spooler.variables().set_var("terminated_cause_max_order_setback_" + normalize(spooler_task.order().id()), "1");
									return false;
								}
								getLogger().debug("launching setback: " + parallelTransferCheckRetry + " * " + parallelTransferCheckSetback);
								spooler_task.order().setback();
								return false;
							}
							else
								if (sosString.parseToString(spooler.var(paramNames[i])).equals("1")) {
									getLogger().debug("successfully terminated: " + paramNames[i]);
								}
								else
									if (sosString.parseToString(spooler.var(paramNames[i])).equals("2")) {
										bSuccess = false;
										getLogger().debug("terminated with error : " + paramNames[i]);
									}
						}
					}
					return bSuccess;
				}
				else
					if (sosString.parseToString(params.var("ftp_parent_order_id")).length() > 0) {
						// Hauptauftrag wurde wegen Erreichens von ftp_parallel_check_retry beendet -> die restlichen Unterauftr�ge sollen
						// nicht durchlaufen
						String state = spooler.variables().var("terminated_cause_max_order_setback_" + normalize(params.var("ftp_parent_order_id")));
						if (state.equals("1"))
							return false;
					}

				if (sosString.parseToString(schedulerParams.get("file_path")).length() > 0) {
					isFilePath = true;
				}
				else {
					isFilePath = false;
				}

			}
			catch (Exception e) {
				throw (new Exception("invalid or insufficient parameters: " + e.getMessage()));
			}

			try { // to process ftp

				if (parallelTransfer && !isFilePath) {
					// nur die filelist holen um Parallelen transfer zu erm�glichen
					Properties p = new Properties();
					p.putAll((Properties) schedulerParams.clone());
					p.put("skip_transfer", "yes");
					// kb 2011-04--27 no longer needed due to too much trouble with this file / concept
//					 createIncludeConfigurationFile("sos/net/sosftp/Configuration.xml", "sos.net.sosftp.Configuration.xml");//Alle
					// Parametern sind hier auch g�ltig
					SOSConfiguration con = new SOSConfiguration(null, p, sosString.parseToString(schedulerParams.get(conParameterSETTINGS)),
							sosString.parseToString(schedulerParams.get(conParameterPROFILE)),
//							"sos/scheduler/ftp/SOSFTPConfiguration.xml", new SOSSchedulerLogger(spooler_log));
					null, new SOSSchedulerLogger(spooler_log));
					con.checkConfigurationItems();

					sos.net.sosftp.SOSFTPCommandSend ftpCommand = new sos.net.sosftp.SOSFTPCommandSend(con, new SOSSchedulerLogger(spooler_log));
					ftpCommand.setSchedulerJob(this);
					rc = ftpCommand.transfer();
					Vector <File> filelist = ftpCommand.getTransferredFilelist();
					Iterator iterator = filelist.iterator();

					if (isJobchain() == false) {
						// parallel transfer for standalone job
						while (iterator.hasNext()) {
							File fileName = (File) iterator.next();
							Variable_set newParams = params;
							newParams.set_var("ftp_file_path", fileName.getCanonicalPath());
							newParams.set_var("ftp_local_dir", "");
							getLogger().info("launching job for parallel transfer with parameter ftp_file_path: " + fileName.getCanonicalPath());
							spooler.job(spooler_task.job().name()).start(params);
						}
						return signalSuccess();
					}
					else {
						// parallel transfer for order job
						while (iterator.hasNext()) {
							File fileName = (File) iterator.next();
							Variable_set newParams = spooler.create_variable_set();
							if (spooler_task.params() != null)
								newParams.merge(params);

							newParams.set_var("ftp_file_path", fileName.getCanonicalPath());
							newParams.set_var("ftp_parent_order_id", spooler_task.order().id());
							newParams.set_var("ftp_order_self_destruct", "1");

							Order newOrder = spooler.create_order();
							newOrder.set_state(spooler_task.order().state());
							newOrder.set_params(newParams);

							spooler.job_chain(spooler_task.order().job_chain().name()).add_order(newOrder);

							getLogger().info("launching order for parallel transfer with parameter ftp_file_path: " + fileName.getCanonicalPath());

							spooler.variables().set_var("ftp_order", normalize(spooler_task.order().id()) + "." + normalize(newOrder.id()) + "." + "0");
							spooler.variables().set_var("ftp_check_send_" + normalize(spooler_task.order().id()) + "." + normalize(newOrder.id()), "0");

						}
						// am aktuellen Auftrag speichern, dass im Wiederholungsfall per setback() nicht erneut Auftr�ge erzeugt werden
						// sollen, sondern dass deren Erledigungszustand gepr�ft wird:
						spooler_task.order().params().set_var("ftp_check_parallel", "yes");
						spooler_job.set_delay_order_after_setback(1, parallelTransferCheckSetback);
						spooler_job.set_max_order_setbacks(parallelTransferCheckRetry);
						spooler_task.order().setback();
						spooler.variables().set_var("cur_transfer_retry" + normalize(spooler_task.order().id()), String.valueOf(parallelTransferCheckRetry));
						return false;
					}
				}
				// end Parallel Transfer
//				createIncludeConfigurationFile("sos/net/sosftp/Configuration.xml", "sos.net.sosftp.Configuration.xml");// Alle Parametern
//																														// sind hier auch
//																														// g�ltig
				SOSConfiguration con = new SOSConfiguration(null, mapToProperties(schedulerParams), sosString.parseToString(schedulerParams.get(conParameterSETTINGS)),
						sosString.parseToString(schedulerParams.get(conParameterPROFILE)),
//						"sos/scheduler/ftp/SOSFTPConfiguration.xml", new SOSSchedulerLogger(spooler_log));
				null, new SOSSchedulerLogger(spooler_log));
				con.checkConfigurationItems();

				sos.net.sosftp.SOSFTPCommandSend ftpCommand = new sos.net.sosftp.SOSFTPCommandSend(con, new SOSSchedulerLogger(spooler_log));
				ftpCommand.setSchedulerJob(this);
				rc = ftpCommand.transfer();

				// return the number of transferred files
				createReturnParameter(ftpCommand);

				if (parallelTransfer && isFilePath && spooler_job.order_queue() != null) {
					spooler.variables().set_var("ftp_check_send_" + normalize(params.var("ftp_parent_order_id")) + "." + normalize(spooler_task.order().id()),
							"1");
				}
				processResult(rc, "");

				spooler_job.set_state_text(ftpCommand.getState() != null ? ftpCommand.getState() : "");
				return (spooler_task.job().order_queue() == null) ? false : rc;

			}
			catch (Exception e) {
				rc = false;
				if (parallelTransfer && isFilePath && spooler_job.order_queue() != null) {
					spooler.variables().set_var("ftp_check_send_" + normalize(params.var("ftp_parent_order_id")) + "." + normalize(spooler_task.order().id()),
							"2");
				}
				throw (new Exception("could not process file transfer: " + e, e));
			}
			finally {
				if (parallelTransfer) {
					if (orderSelfDestruct) {
						// find positive end state for parallel orders
						String state = "";
						sos.spooler.Job_chain_node node = spooler_task.order().job_chain_node();
						while (node != null) {
							node = node.next_node();
							if (node != null)
								state = node.state();
						}
						this.getLogger().debug9("..set state for parallel order job: " + state);
						// find positive end state
						spooler_task.order().set_state(state);
					}
				}

			}

		}
		catch (Exception e) {
			processResult(false, e.toString());
			spooler_job.set_state_text("ftp processing failed: " + e);
			spooler_log.warn("ftp processing failed: " + e);
			return false;
		}
	}

	protected void processResult(boolean rc, String message) {
		// do nothing, entry point for subclasses

	}

	private String normalize(String str) {
		return str.replaceAll(",", "_");
	}

	/**
	 * Convert parameters from sos.spooler.Variable_set in java.util.Properties.
	 * 
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private HashMap <String, String> getParameterDefaults(Variable_set params) throws Exception {
		HashMap <String, String> schedulerParams = new HashMap <String, String>();// hier variable_set in Properties casten
		try {
//			String[] names = params.names().split(";");
//			getLogger().debug9("names " + params.names());
//			for (int i = 0; i < names.length; i++) {
//				String key = names[i];
//				String val = params.var(names[i]);
//
//				if (key.startsWith("ftp_") && key.length() > "ftp_".length()) {
//					key = key.substring("ftp_".length());
//				}
////				getLogger().debug("param [" + key + "=" + val + "]");
//				schedulerParams.put(key, val);
//			}

			// Einige Defaults hinzuf�gen
			schedulerParams.put("operation", "send");

			try {
				schedulerParams.put("mail_smtp", spooler_log.mail().smtp());
				schedulerParams.put("mail_queue_dir", spooler_log.mail().queue_dir());
				schedulerParams.put("mail_from", spooler_log.mail().from());
			}
			catch (Exception e) {
				schedulerParams.put("mail_smtp", "localhost");
				schedulerParams.put("mail_queue_dir", "");
				schedulerParams.put("mail_from", "SOSFTP");
			}

			String fileNotificationTo = sosString.parseToString(schedulerParams.get("file_notification_to"));
			String fileNotificationSubject = sosString.parseToString(schedulerParams.get("file_notification_subject"));
			String fileNotificationBody = sosString.parseToString(schedulerParams.get("file_notification_body"));

			if (fileNotificationTo != null && fileNotificationTo.length() > 0) {
				if (fileNotificationSubject == null || fileNotificationSubject.length() == 0) {
					if (spooler_job.order_queue() != null) {
						fileNotificationSubject = "[info] Job Chain: " + spooler_task.order().job_chain().name() + ", Order: " + spooler_task.order().id()
								+ ", Job: " + spooler_job.name() + " (" + spooler_job.title() + "), Task: " + spooler_task.id();
					}
					else {
						fileNotificationSubject = "[info] Job: " + spooler_job.name() + " (" + spooler_job.title() + "), Task: " + spooler_task.id();
					}
					schedulerParams.put("file_notification_subject", fileNotificationSubject);
				}

				if (fileNotificationBody == null || fileNotificationBody.length() == 0) {
					fileNotificationBody = "The following files have been send:\n\n";
					schedulerParams.put("file_notification_body", fileNotificationBody);
				}
			}

			String fileZeroByteNotificationTo = sosString.parseToString(schedulerParams.get("file_zero_byte_notification_to"));
			String fileZeroByteNotificationSubject = sosString.parseToString(schedulerParams.get("file_zero_byte_notification_subject"));
			String fileZeroByteNotificationBody = sosString.parseToString(schedulerParams.get("file_zero_byte_notification_body"));
			if (fileZeroByteNotificationTo != null && fileZeroByteNotificationTo.length() > 0) {
				if (fileZeroByteNotificationSubject == null || fileZeroByteNotificationSubject.length() == 0) {
					if (spooler_job.order_queue() != null) {
						fileZeroByteNotificationSubject = "[warning] Job Chain: " + spooler_task.order().job_chain().name() + ", Order: "
								+ spooler_task.order().id() + ", Job: " + spooler_job.name() + " (" + spooler_job.title() + "), Task: " + spooler_task.id();
					}
					else {
						fileZeroByteNotificationSubject = "[warning] Job: " + spooler_job.name() + " (" + spooler_job.title() + "), Task: " + spooler_task.id();
					}
					schedulerParams.put("file_zero_byte_notification_subject", fileZeroByteNotificationSubject);
				}

				if (fileZeroByteNotificationBody == null || fileZeroByteNotificationBody.length() == 0) {
					fileZeroByteNotificationBody = "The following files have been send and were removed due to zero byte constraints:\n\n";
					schedulerParams.put("file_zero_byte_notification_body", fileZeroByteNotificationBody);
				}
			}

			return schedulerParams;
		}
		catch (Exception e) {
			throw new Exception("error occurred reading Parameter: " + e.getMessage());
		}
	}

	private void createReturnParameter(SOSFTPCommandSend ftpCommand) throws Exception {
		try {
			int count = ftpCommand.getOfTransferFilesCount();
			int zeroByteCount = ftpCommand.getZeroByteCount();
			if (isJobchain()) {
				spooler_task.order().params().set_var(conOrderParameterFTP_RESULT_FILES, Integer.toString(count));
				spooler_task.order().params().set_var(conOrderParameterFTP_RESULT_ZERO_BYTE_FILES, Integer.toString(zeroByteCount));
			}
			else {
				spooler_task.params().set_var(conOrderParameterFTP_RESULT_FILES, Integer.toString(count));
				spooler_task.params().set_var(conOrderParameterFTP_RESULT_ZERO_BYTE_FILES, Integer.toString(zeroByteCount));
			}

		}
		catch (Exception e) {
			throw new Exception("error occurred creating order Patameter: " + e.getMessage());
		}
	}
}
