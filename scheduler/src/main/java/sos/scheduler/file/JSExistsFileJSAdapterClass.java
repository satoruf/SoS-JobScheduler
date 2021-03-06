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
package sos.scheduler.file;

import static com.sos.scheduler.messages.JSMessages.JSJ_E_0020;
import static com.sos.scheduler.messages.JSMessages.JSJ_E_0041;
import static com.sos.scheduler.messages.JSMessages.JSJ_E_0120;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0017;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0018;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0019;
import static com.sos.scheduler.messages.JSMessages.JSJ_I_0090;

import java.io.File;
import java.util.Vector;

import org.apache.log4j.Logger;

import sos.scheduler.job.JobSchedulerJobAdapter;
import sos.spooler.Job_chain;
import sos.spooler.Order;
import sos.spooler.Variable_set;

import com.sos.JSHelper.Exceptions.JobSchedulerException;

/**
 * \class 		JSExistsFileJSAdapterClass - JobScheduler Adapter for "check wether a file exist"
 *
 * \brief AdapterClass of JSExistFile for the SOSJobScheduler
 *
 * This Class JSExistsFileJSAdapterClass works as an adapter-class between the SOS
 * JobScheduler and the worker-class JSExistFile.
 *

 *
 * see \see C:\Users\KB\Documents\xmltest\JSExistFile.xml for more details.
 *
 * \verbatim ;
 * mechanicaly created by C:\Users\KB\eclipse\xsl\JSJobDoc2JSAdapterClass.xsl from http://www.sos-berlin.com at 20110820120939
 * \endverbatim
 */
public class JSExistsFileJSAdapterClass extends JobSchedulerJobAdapter {
	private final String		conClassName	= "JSExistsFileJSAdapterClass";						//$NON-NLS-1$
	private static Logger		logger			= Logger.getLogger(JSExistsFileJSAdapterClass.class);
	private JSExistsFileOptions	objO			= null;

	public void init() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::init"; //$NON-NLS-1$
		doInitialize();
	}

	private void doInitialize() {
	} // doInitialize

	@Override
	public boolean spooler_init() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::spooler_init"; //$NON-NLS-1$
		return super.spooler_init();
	}

	@Override
	public boolean spooler_process() throws Exception {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::spooler_process"; //$NON-NLS-1$

		try {
			super.spooler_process();
			return doProcessing();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new JobSchedulerException();
		}
		finally {
		} // finally

	} // spooler_process

	@Override
	public void spooler_exit() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::spooler_exit"; //$NON-NLS-1$
		super.spooler_exit();
	}

	private boolean doProcessing() throws Exception {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::doProcessing"; //$NON-NLS-1$

		JSExistsFile objR = new JSExistsFile();
		objO = objR.Options();
		objO.setAllOptions(getSchedulerParameterAsProperties(getParameters()));
		// objO.CheckMandatory();
		objR.setJSJobUtilites(this);

		boolean flgResult = objR.Execute();

		Vector<File> lstResultList = objR.getResultList();
		int intNoOfHitsInResultSet = lstResultList.size();
		String strOrderJobChainName = null;
		boolean flgCreateOrders4AllFiles = false;

		boolean count_files = objO.count_files.value();
		if (isJobchain()) {
			if (count_files == true) {
				setOrderParameter(objO.count_files.getKey(), String.valueOf(intNoOfHitsInResultSet));
			}
			Variable_set objP = spooler_task.order().params();
			if (isNotNull(objP)) {
				String strT = "";
				for (File objFile : lstResultList) {
					strT += objFile.getAbsolutePath() + ";";
				}

				setOrderParameter(objO.scheduler_sosfileoperations_resultset.getKey(), strT);
				setOrderParameter(objO.scheduler_sosfileoperations_resultsetsize.getKey(), String.valueOf(intNoOfHitsInResultSet));
			}

			String strOnEmptyResultSet = objO.on_empty_result_set.Value();
			if (isNotEmpty(strOnEmptyResultSet) && intNoOfHitsInResultSet <= 0) {
				logger.info(JSJ_I_0090.params(strOnEmptyResultSet));
				spooler_task.order().set_state(strOnEmptyResultSet);
			}
		}
		else {
			if (count_files == true) {
				logger.warn(JSJ_E_0120.params(objO.count_files.getKey()));
			}
		}

		if (flgResult) {

			flgCreateOrders4AllFiles = objO.create_orders_for_all_files.value();
			boolean flgCreateOrder = objO.create_order.value();
			if (flgCreateOrder == true && intNoOfHitsInResultSet > 0) {
				strOrderJobChainName = objO.order_jobchain_name.Value();
				if (isNull(strOrderJobChainName)) {
					throw new JobSchedulerException(JSJ_E_0020.params(objO.order_jobchain_name.getKey()));
				}
				if (spooler.job_chain_exists(strOrderJobChainName) == false) {
					throw new JobSchedulerException(JSJ_E_0041.params(strOrderJobChainName));
				}

				for (File objFile : lstResultList) {
					createOrder(objFile.getAbsolutePath(), strOrderJobChainName);
					if (flgCreateOrders4AllFiles == false) {
						break;
					}
				}
			}
		}

		return setReturnResult(flgResult);
	} // doProcessing

	private void createOrder(final String pstrOrder4FileName, final String pstrOrderJobChainName) {

		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::createOrder";

		Order objOrder = spooler.create_order();
		Variable_set objOrderParams = spooler.create_variable_set();

		objOrderParams.set_value(objO.scheduler_file_path.getKey(), pstrOrder4FileName);
		objOrderParams.set_value(objO.scheduler_file_parent.getKey(), new File(pstrOrder4FileName).getParent());
		objOrderParams.set_value(objO.scheduler_file_name.getKey(), new File(pstrOrder4FileName).getName());

		String strNextState = objO.next_state.Value();
		if (isNotEmpty(strNextState)) {
			objOrder.set_state(strNextState);
		}
		objOrder.set_params(objOrderParams);
		objOrder.set_id(pstrOrder4FileName);
		objOrder.set_title(JSJ_I_0017.params(conClassName)); // "Order created by %1$s"

		Job_chain objJobchain = spooler.job_chain(pstrOrderJobChainName);
		objJobchain.add_order(objOrder);
		String strT = JSJ_I_0018.params(pstrOrder4FileName, pstrOrderJobChainName); // "Order '%1$s' created for JobChain '%2$s'."
		if (isNotEmpty(strNextState)) {
			strT += " " + JSJ_I_0019.params(strNextState); // "Next State is '%1$s'."
		}
		logger.info(strT);

	} // private void createOrder

	public boolean setReturnResult(final boolean pflgResult) {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::setReturnResult";
		boolean rc1 = pflgResult;

		if (rc1 == false && objO.gracious.isGraciousAll()) {
			return signalSuccess();
		}
		else {
			if (rc1 == false && objO.gracious.isGraciousAll()) {
				if (isJobchain()) {
					return conJobChainFailure;
				}
				return conJobSuccess;
			}
			else {
				if (rc1 == true) {
					return signalSuccess();
				}
				else {
					return signalFailure();
				}
			}
		}
	} // private boolean setReturnResult
}
