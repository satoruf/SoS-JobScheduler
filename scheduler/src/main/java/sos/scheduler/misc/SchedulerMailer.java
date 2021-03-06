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
/*
 * SchedulerMailer.java
 * Created on 21.08.2007
 *
 */
package sos.scheduler.misc;

import java.util.Properties;

import sos.net.SOSMail;
import sos.scheduler.job.JobSchedulerJob;
import sos.settings.SOSProfileSettings;
import sos.settings.SOSSettings;
import sos.spooler.Spooler;
import sos.spooler.Variable_set;
import sos.util.SOSLogger;
import sos.util.SOSSchedulerLogger;

import com.sos.JSHelper.Exceptions.JobSchedulerException;

/**
 * This class provides an easy way to send emails from a JobScheduler Job or Monitor.<br>
 * It initializes a SOSMail object with mail settings of the JobScheduler and automatically
 * reads the following job/order parameters and sets applies them to the SOSMail object:<br>
 * <ul>
 *  <li><b>to</b> - mail recipient(s)</li>
 *  <li><b>from</b> - mail sender</li>
 *  <li><b>from_name</b> - name of the sender</li>
 *  <li><b>reply_to</b> - reply address</li>
 *  <li><b>cc</b> - cc recipient(s)</li>
 *  <li><b>bcc</b> - bcc recipient(s)</li>
 *  <li><b>subject</b> - mail subject</li>
 *  <li><b>host</b> - mail server host</li>
 *  <li><b>port</b> - mail server port</li>
 *  <li><b>smtp_user</b> - smtp username</li>
 *  <li><b>smtp_password</b> - smtp password</li>
 *  <li><b>queue_directory</b> - directory for enqueued mails</li>
 *  <li><b>body</b> - mail body</li>
 *  <li><b>content_type</b> - content_type of the mail (text/plain, text/html...)</li>
 *  <li><b>encoding</b> - encoding of the mail (7bit, Quoted-Printable, Base64)</li>
 *  <li><b>charset</b> - charset of the mail</li>
 *  <li><b>attachment_content_type</b> - content_type of attachments (application/octet-stream, application/pdf...)</li>
 *  <li><b>attachment_encoding</b> - encoding of attachments (7bit, Quoted-Printable, Base64)</li>
 *  <li><b>attachment_charset</b> - charset of attachments</li>
 *  <li><b>attachment</b> - Filename and path of the attachment(s) (multiple attachments separated by ";"</li>
 * </ul>
 * <br>
 * All parameters are optional. Before sending the mail, it may be altered/enhanced by
 * the job using Api calls to the SOSMail object.<br>
 *
 * <h2>Usage</h2>
 * The SchedulerMailer constructors work for job and monitor implementations in
 * the same way. The given example is for a job:<br>
 * <br>
 * <code>
 * public boolean spooler_process() throws Exception  {<br>
 *   ...<br>
 *   SchedulerMailer mailer = new SchedulerMailer(this);<br>
 *   // mailer is now initialized with factory.ini defaults and settings<br>
 *   // from job/order parameters<br>
 *   <br>
 *   // get mail object<br>
 *   SOSMail mail = mailer.getSosMail();<br>
 *   <br>
 *   // change properties of the mail if needed:<br>
 *   mail.setBody("Hello World!");<br>
 *   <br>
 *   //send mail<br>
 *   mail.send();<br>
 *   <br>
 *   ...<br>
 * }<br>
 * </code>
 * <h2>javascript version</h2>
 * There is also a javascript version of Scheduler Mailer in job/SchedulerMailer.js.
 * This file can be included in javascript Jobs and used in the same way as in
 * Java jobs. Example:<br>
 * <code><pre>
 * &lt;job stop_on_error=&quot;yes&quot; title=&quot;Javascript Mail&quot; order=&quot;no&quot;&gt;
 *      &lt;params&gt;
 *       &lt;param name=&quot;to&quot;           value=&quot;name@yourdomain.com&quot;/&gt;
 *       &lt;param name=&quot;from_name&quot;    value=&quot;John Doe&quot;/&gt;
 *       &lt;param name=&quot;body&quot;         value=&quot;This is the mail body&quot;/&gt;
 *      &lt;/params&gt;
 *      &lt;script language=&quot;javascript&quot;&gt;
 *        &lt;include file=&quot;jobs/SchedulerMailer.js&quot;/&gt;
 *
 *      &lt;![CDATA[
 *        function spooler_process(){
 *          spooler_log.info(&quot;Creating mailer object&quot;);
 *          var mailer = new schedulerMailer();
 *          if (1==1){
 *            mailer.sosMail.setSubject(&quot;OK&quot;);
 *          } else{
 *            mailer.sosMail.setSubject(&quot;NOK&quot;);
 *          }
 *          mailer.sosMail.send();
 *          return false;
 *        }
 *
 *       ]]&gt;&lt;/script&gt;
 *  &lt;run_time&gt;
 *    &lt;period repeat=&quot;01:00&quot;/&gt;
 *  &lt;/run_time&gt;
 *
 * &lt;/job&gt;
 * </pre></code>
 * <h2>Mail settings from the database</h2>
 * If the database contains the JobScheduler SETTINGS table with a section for
 * email settings, the mail defaults can also be read from there (instead of using
 * the factory.ini defaults):<br>
 * This only works for jobs which are subclassed from sos.scheduler.JobSchedulerJob.<br>
 * If a job or order parameter <code>read_mail_settings</code> is set to "true", the
 * mail defaults will be read from the SETTINGS table. (Afterwards, job and order
 * parameters will be applied, and the SOSMail object can be changed).
 *
 * @see sos.net.SOSMail
 * @author Andreas Liebert
 */
public class SchedulerMailer {

	@SuppressWarnings("unused")
	private final String				conSVNVersion		= "$Id: SchedulerMailer.java 18378 2012-11-09 15:39:03Z kb $";

	private SOSMail			sosMail;

	private SOSLogger		logger;

	private final Spooler	spooler;

	/**
	 * constructs and initializes a SchedulerMailer, using the mail settings
	 * from factory.ini and job/order parameters
	 * @param job the job which uses the SchedulerMailer (usually "<code>this</code>")
	 * @throws Exception
	 */
	public SchedulerMailer(final sos.spooler.Job_impl job) throws Exception {
		spooler = job.spooler;
		init(job.spooler_log);
		Variable_set params = job.spooler.create_variable_set();
		params.merge(job.spooler_task.params());
		if (job.spooler_job.order_queue() != null)
			params.merge(job.spooler_task.order().params());
		readParams(params);
	}

	/**
	 * constructs and initializes a SchedulerMailer, using the mail settings
	 * from factory.ini and job/order parameters
	 * @param monitor the monitor which uses the SchedulerMailer (usually "<code>this</code>")
	 * @throws Exception
	 */
	public SchedulerMailer(final sos.spooler.Monitor_impl monitor) throws Exception {
		spooler = monitor.spooler;
		init(monitor.spooler_log);
		Variable_set params = monitor.spooler.create_variable_set();
		params.merge(monitor.spooler_task.params());
		if (monitor.spooler_job.order_queue() != null)
			params.merge(monitor.spooler_task.order().params());
		readParams(params);
	}

	/**
	 * constructs and initializes a SchedulerMailer, using the mail settings
	 * from
	 * <ul>
	 *   <li>factory.ini or</li>
	 *   <li>the SETTINGS table if a job or order parameter
	 *   <code>read_mail_settings</code> is set to "true"</li>
	 * </ul>
	 * and job/order parameters
	 * @param job the job which uses the SchedulerMailer (usually "<code>this</code>")
	 * @throws Exception
	 */
	public SchedulerMailer(final JobSchedulerJob job) throws Exception {
		spooler = job.spooler;
		logger = job.getLogger();
		Variable_set params = job.spooler.create_variable_set();
		params.merge(job.spooler_task.params());
		if (job.spooler_job.order_queue() != null && job.spooler_task.order() != null) {
			params.merge(job.spooler_task.order().params());
		}
		String readSettings = params.value("read_mail_settings");
		if (readSettings != null && (readSettings.equalsIgnoreCase("yes") || readSettings.equalsIgnoreCase("1") || readSettings.equalsIgnoreCase("true"))) {
			if (job.getConnectionSettings() != null && job.getConnectionSettings().getSection("email", "mail_server").size() > 0) {
				sosMail = new SOSMail(job.getConnectionSettings());
				sosMail.setSOSLogger(logger);
			}
			else {
				throw new Exception("Mail Settings could not be found.");
			}
		}
		else {
			init(job.spooler_log);
		}
		readParams(params);
	}

	private void init(final sos.spooler.Log spooler_log) throws Exception {
		try {
			sosMail = new SOSMail(spooler_log.mail().smtp());
			if (logger == null)
				logger = new SOSSchedulerLogger(spooler_log);
			sosMail.setSOSLogger(logger);
			sosMail.setQueueDir(spooler_log.mail().queue_dir());
			sosMail.setFrom(spooler_log.mail().from());
			sosMail.addRecipient(spooler_log.mail().to());
			sosMail.addCC(spooler_log.mail().cc());
			sosMail.addBCC(spooler_log.mail().bcc());
			SOSSettings smtpSettings = new SOSProfileSettings(spooler.ini_path());
			Properties smtpProperties = smtpSettings.getSection("smtp");
			if (!smtpProperties.isEmpty()) {
				if (smtpProperties.getProperty("mail.smtp.user") != null && smtpProperties.getProperty("mail.smtp.user").length() > 0) {
					sosMail.setUser(smtpProperties.getProperty("mail.smtp.user"));
				}
				if (smtpProperties.getProperty("mail.smtp.password") != null && smtpProperties.getProperty("mail.smtp.password").length() > 0) {
					sosMail.setPassword(smtpProperties.getProperty("mail.smtp.password"));
				}
			}
		}
		catch (Exception e) {
			throw new JobSchedulerException("Error initializing SOSMail: " + e, e);
		}
	}

	private void readParams(final Variable_set params) throws Exception {
		try {
			logger.debug1("Setting mail parameters:");
			if (params.value("to") != null && params.value("to").length() > 0) {
				sosMail.clearRecipients();
				sosMail.addRecipient(params.value("to"));
				debugParameter(params, "to");
			}

			if (params.value("from") != null && params.value("from").length() > 0) {
				sosMail.setFrom(params.value("from"));
				debugParameter(params, "from");
			}

			if (params.value("from_name") != null && params.value("from_name").length() > 0) {
				sosMail.setFromName(params.value("from_name"));
				debugParameter(params, "from_name");
			}

			if (params.value("reply_to") != null && params.value("reply_to").length() > 0) {
				sosMail.setReplyTo(params.value("reply_to"));
				debugParameter(params, "reply_to");
			}

			if (params.value("cc") != null && params.value("cc").length() > 0) {
				sosMail.addCC(params.value("cc"));
				debugParameter(params, "cc");
			}

			if (params.value("bcc") != null && params.value("bcc").length() > 0) {
				sosMail.addBCC(params.value("bcc"));
				debugParameter(params, "bcc");
			}

			if (params.value("subject") != null && params.value("subject").length() > 0) {
				sosMail.setSubject(params.value("subject"));
				debugParameter(params, "subject");
			}

			if (params.value("host") != null && params.value("host").length() > 0) {
				sosMail.setHost(params.value("host"));
				debugParameter(params, "host");
			}

			if (params.value("port") != null && params.value("port").length() > 0) {
				try {
					int port = Integer.parseInt(params.value("port"));
					sosMail.setPort("" + port);
					debugParameter(params, "port");
				}
				catch (Exception e) {
					throw new Exception("illegal, non-numeric value [" + params.value("port") + "] for parameter [port]: " + e.getMessage());
				}
			}

			if (params.value("smtp_user") != null && params.value("smtp_user").length() > 0) {
				sosMail.setUser(params.value("smtp_user"));
				debugParameter(params, "smtp_user");
			}

			if (params.value("smtp_password") != null && params.value("smtp_password").length() > 0) {
				sosMail.setPassword(params.value("smtp_password"));
				debugParameter(params, "smtp_password");
			}

			if (params.value("queue_directory") != null && params.value("queue_directory").length() > 0) {
				sosMail.setQueueDir(params.value("queue_directory"));
				debugParameter(params, "queue_director");
			}

			if (params.value("body") != null && params.value("body").length() > 0) {
				sosMail.setBody(params.value("body"));
				debugParameter(params, "body");
			}

			if (params.value("content_type") != null && params.value("content_type").length() > 0) {
				sosMail.setContentType(params.value("content_type"));
				debugParameter(params, "content_type");
			}

			if (params.value("encoding") != null && params.value("encoding").length() > 0) {
				sosMail.setEncoding(params.value("encoding"));
				debugParameter(params, "encoding");
			}

			if (params.value("charset") != null && params.value("charset").length() > 0) {
				sosMail.setCharset(params.value("charset"));
				debugParameter(params, "charset");
			}

			if (params.value("attachment_charset") != null && params.value("attachment_charset").length() > 0) {
				sosMail.setAttachmentCharset(params.value("attachment_charset"));
				debugParameter(params, "attachment_charset");
			}

			if (params.value("attachment_content_type") != null && params.value("attachment_content_type").length() > 0) {
				sosMail.setAttachmentContentType(params.value("attachment_content_type"));
				debugParameter(params, "attachment_content_type");
			}

			if (params.value("attachment_encoding") != null && params.value("attachment_encoding").length() > 0) {
				sosMail.setAttachmentEncoding(params.value("attachment_encoding"));
				debugParameter(params, "attachment_encoding");
			}

			if (params.value("attachment") != null && params.value("attachment").length() > 0) {
				String[] attachments = params.value("attachment").split(";");
				for (int i = 0; i < attachments.length; i++) {
					String attFile = attachments[i];
					logger.debug1(".. mail attachment [" + i + "]: " + attFile);
					sosMail.addAttachment(attFile);
				}
			}

		}
		catch (Exception e) {
			throw new Exception("Error occured reading parameters: " + e, e);
		}
	}

	private void debugParameter(final Variable_set params, final String paramName) {
		try {
			logger.debug1(".. mail parameter [" + paramName + "]: " + params.value(paramName));
		}
		catch (Exception e) {
		} //No error handling

	}

	/**
	 * Returns an initialized SOSMail Object, which may be altered by
	 * the job/monitor before sending (<code>getSosMail.send()</code>)
	 *
	 * @return the sosMail
	 */
	public SOSMail getSosMail() {
		return sosMail;
	}
}
