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
 * JobSchedulerSubmitEventJob.java
 * Created on 19.05.2008
 *
 */
package sos.scheduler.job;

import java.io.File;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sos.scheduler.command.SOSSchedulerCommand;
import sos.spooler.Job;
import sos.spooler.Log;
import sos.spooler.Spooler;
import sos.spooler.Supervisor_client;
import sos.spooler.Task;
import sos.spooler.Variable_set;
import sos.util.SOSDate;
import sos.xml.SOSXMLXPath;

import com.sos.JSHelper.Exceptions.JobSchedulerException;

@SuppressWarnings("deprecation")
public class JobSchedulerSubmitEventJob extends JobSchedulerJobAdapter {

	private static final String	conParamSCHEDULER_EVENT_HANDLER_PORT		= "scheduler_event_handler_port";
	private static final String	conParamSCHEDULER_EVENT_HANDLER_HOST		= "scheduler_event_handler_host";
	private static final String	conParamSCHEDULER_EVENT_EXPIRATION_PERIOD	= "scheduler_event_expiration_period";
	private static final String	conParamSCHEDULER_EVENT_EXPIRATION_CYCLE	= "scheduler_event_expiration_cycle";
	private static final String	conParamSCHEDULER_EVENT_EXPIRES				= "scheduler_event_expires";
	private static final String	conParamSCHEDULER_EVENT_EXIT_CODE			= "scheduler_event_exit_code";
	private static final String	conParamSCHEDULER_EVENT_JOB					= "scheduler_event_job";
	private static final String	conParamSUPERVISOR_JOB_CHAIN				= "supervisor_job_chain";
	private static final String	conParamSCHEDULER_EVENT_ID					= "scheduler_event_id";
	private static final String	conActionREMOVE								= "remove";
	private static final String	conParameterSCHEDULER_EVENT_ACTION			= "scheduler_event_action";
	private static final String	conParamSCHEDULER_EVENT_CLASS				= "scheduler_event_class";

	private final String		conSVNVersion								= "$Id: JobSchedulerSubmitEventJob.java 18778 2013-01-17 20:53:42Z kb $";

	@Override
	public boolean spooler_process() throws Exception {

		try {
			spooler_log.info(conSVNVersion);
			processEvent(spooler, spooler_job, spooler_task, spooler_log);
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			spooler_log.warn("Error occured in event job: " + e);
			return signalFailure();
		}
		return signalSuccess();
	}

	@SuppressWarnings("hiding")
	private static Log	logger;

	protected static void processEvent(final Spooler spooler, final Job spooler_job, final Task spooler_task, final Log spooler_log) throws Exception {
		logger = spooler_log;
		// event attributes
		boolean orderJob = !(spooler_task.job().order_queue() == null);
		// TODO als globale Variable im JS definieren (kb 2012-11-23)
		String supervisorJobChain = "/sos/events/scheduler_event_service";
		String jobChain = "";
		String orderId = "";
		File jobPath = new File(spooler_job.name());
		String jobName = jobPath.getName();
		String schedulerHost = spooler.hostname();
		String schedulerTCPPort = "" + spooler.tcp_port();
		String eventHandlerHost = "";
		int eventHandlerTCPPort = 0;
		String action = "add";
		HashMap<String, String> eventParameters = new HashMap<String, String>();
		String expires = "";
		String expCycle = "";
		String expPeriod = "";
		String exitCode = "" + spooler_task.exit_code();
		String eventClass = "";
		String eventId = "" + spooler_task.id();

		Variable_set parameters = spooler.create_variable_set();
		parameters.merge(spooler_task.params());
		if (orderJob) {
			jobChain = spooler_task.order().job_chain().name();
			orderId = spooler_task.order().id();
			parameters.merge(spooler_task.order().params());
			eventId = orderId;
		}
		try {
			HashSet<String> parameterNames = new HashSet<String>();
			spooler_log.debug1("reading parameters:");
			eventClass = parameters.var(conParamSCHEDULER_EVENT_CLASS);
			if (eventClass == null || eventClass.length() == 0) {
				throw new JobSchedulerException("Parameter scheduler_event_class is missing.");
			}

			if (parameters.var(conParameterSCHEDULER_EVENT_ACTION) != null && parameters.var(conParameterSCHEDULER_EVENT_ACTION).length() > 0) {
				action = parameters.var(conParameterSCHEDULER_EVENT_ACTION);
				spooler_log.debug1("...parameter[scheduler_event_action]: " + action);
				parameterNames.add(conParameterSCHEDULER_EVENT_ACTION);
			}

			if (action.equalsIgnoreCase(conActionREMOVE)) {
				orderId = "";
				jobChain = "";
				jobName = "";
				schedulerHost = "";
				schedulerTCPPort = "";
				exitCode = "";
				eventId = "";

				if (parameters.var(conParamSCHEDULER_EVENT_JOB) != null && parameters.var(conParamSCHEDULER_EVENT_JOB).length() > 0) {
					jobName = parameters.var(conParamSCHEDULER_EVENT_JOB);
					spooler_log.debug1("...parameter[scheduler_event_job]: " + jobName);
					parameterNames.add(conParamSCHEDULER_EVENT_JOB);
				}

				if (parameters.var("scheduler_event_host") != null && parameters.var("scheduler_event_host").length() > 0) {
					schedulerHost = parameters.var("scheduler_event_host");
					spooler_log.debug1("...parameter[scheduler_event_host]: " + schedulerHost);
					parameterNames.add("scheduler_event_host");
				}

				if (parameters.var("scheduler_event_port") != null && parameters.var("scheduler_event_port").length() > 0) {
					schedulerTCPPort = parameters.var("scheduler_event_port");
					spooler_log.debug1("...parameter[scheduler_event_port]: " + schedulerTCPPort);
					parameterNames.add("scheduler_event_port");
				}

				if (parameters.var(conParamSCHEDULER_EVENT_EXIT_CODE) != null && parameters.var(conParamSCHEDULER_EVENT_EXIT_CODE).length() > 0) {
					exitCode = parameters.var(conParamSCHEDULER_EVENT_EXIT_CODE);
					spooler_log.debug1("...parameter[scheduler_event_exit_code]: " + exitCode);
					parameterNames.add(conParamSCHEDULER_EVENT_EXIT_CODE);
				}
			}

			parameterNames.add(conParamSCHEDULER_EVENT_CLASS);
			spooler_log.debug1("...parameter[scheduler_event_class]: " + eventClass);

			if (parameters.var(conParamSCHEDULER_EVENT_ID) != null && parameters.var(conParamSCHEDULER_EVENT_ID).length() > 0) {
				eventId = parameters.var(conParamSCHEDULER_EVENT_ID);
				spooler_log.debug1("...parameter[scheduler_event_id]: " + eventId);
				parameterNames.add(conParamSCHEDULER_EVENT_ID);
			}

			if (parameters.var(conParamSUPERVISOR_JOB_CHAIN) != null && parameters.var(conParamSUPERVISOR_JOB_CHAIN).length() > 0) {
				supervisorJobChain = parameters.var(conParamSUPERVISOR_JOB_CHAIN);
				spooler_log.debug1("...parameter[supervisor_job_chain]: " + supervisorJobChain);
				parameterNames.add(conParamSUPERVISOR_JOB_CHAIN);
			}

			if (parameters.var(conParamSCHEDULER_EVENT_EXPIRES) != null && parameters.var(conParamSCHEDULER_EVENT_EXPIRES).length() > 0) {
				expires = parameters.var(conParamSCHEDULER_EVENT_EXPIRES);
				spooler_log.debug1("...parameter[scheduler_event_expires]: " + expires);
				parameterNames.add(conParamSCHEDULER_EVENT_EXPIRES);
			}

			if (parameters.var(conParamSCHEDULER_EVENT_EXPIRATION_CYCLE) != null && parameters.var(conParamSCHEDULER_EVENT_EXPIRATION_CYCLE).length() > 0) {
				expCycle = parameters.var(conParamSCHEDULER_EVENT_EXPIRATION_CYCLE);
				spooler_log.debug1("...parameter[scheduler_event_expiration_cycle]: " + expCycle);
				parameterNames.add(conParamSCHEDULER_EVENT_EXPIRATION_CYCLE);
			}

			if (parameters.var(conParamSCHEDULER_EVENT_EXPIRATION_PERIOD) != null && parameters.var(conParamSCHEDULER_EVENT_EXPIRATION_PERIOD).length() > 0) {
				expPeriod = parameters.var(conParamSCHEDULER_EVENT_EXPIRATION_PERIOD);
				spooler_log.debug1("...parameter[scheduler_event_expiration_period]: " + expPeriod);
				parameterNames.add(conParamSCHEDULER_EVENT_EXPIRATION_PERIOD);
			}

			if (parameters.var(conParamSCHEDULER_EVENT_HANDLER_HOST) != null && parameters.var(conParamSCHEDULER_EVENT_HANDLER_HOST).length() > 0) {
				eventHandlerHost = parameters.var(conParamSCHEDULER_EVENT_HANDLER_HOST);
				spooler_log.debug1("...parameter[scheduler_event_handler_host]: " + eventHandlerHost);
				parameterNames.add(conParamSCHEDULER_EVENT_HANDLER_HOST);
			}

			if (parameters.var(conParamSCHEDULER_EVENT_HANDLER_PORT) != null && parameters.var(conParamSCHEDULER_EVENT_HANDLER_PORT).length() > 0) {
				eventHandlerTCPPort = Integer.parseInt(parameters.var(conParamSCHEDULER_EVENT_HANDLER_PORT));
				spooler_log.debug1("...parameter[scheduler_event_handler_port]: " + eventHandlerTCPPort);
				parameterNames.add(conParamSCHEDULER_EVENT_HANDLER_PORT);
			}

			if (expires.length() == 0 && (expCycle.length() > 0 || expPeriod.length() > 0)) {
				Calendar exp = JobSchedulerEventJob.calculateExpirationDate(expCycle, expPeriod);
				expires = SOSDate.getTimeAsString(exp.getTime());
			}
			// use all other parameters as event parameters:
			String[] paramNames = parameters.names().split(";");
			for (String paramName2 : paramNames) {
				String paramName = paramName2;
				if (!parameterNames.contains(paramName)) {
					String paramValue = parameters.var(paramName);
					spooler_log.debug1("...event parameter[" + paramName + "]: " + paramValue);
					eventParameters.put(paramName, paramValue);
				}
			}
		}
		catch (Exception e) {
			throw new JobSchedulerException("Error reading parameters: " + e, e);
		}

		try {
			String strA[] = eventId.split(";");
			for (String strEventID : strA) {
				String addOrder = createAddOrder(eventClass, strEventID, jobChain, orderId, jobName, schedulerHost, schedulerTCPPort, action, expires,
						exitCode, eventParameters, supervisorJobChain);
				submitToSupervisor(addOrder, spooler_log, spooler, eventHandlerHost, eventHandlerTCPPort);
			}
			// Check for del_events
			if (parameters.var("del_events") != null && parameters.var("del_events").length() > 0) {
				String strEvents2Delete = parameters.var("del_events");
				strA = strEvents2Delete.split(";");
				action = conActionREMOVE;
				expires = "";
				for (String strEventID : strA) {
					String addOrder = createAddOrder(eventClass, strEventID, jobChain, orderId, jobName, schedulerHost, schedulerTCPPort, action, expires,
							exitCode, eventParameters, supervisorJobChain);
					submitToSupervisor(addOrder, spooler_log, spooler, eventHandlerHost, eventHandlerTCPPort);
				}
			}
		}
		catch (Exception e) {
			throw new JobSchedulerException("Error submitting event order: " + e, e);
		}
	}

	private static String createAddOrder(final String eventClass, final String eventId, final String jobChain, final String orderId, final String jobName,
			final String schedulerHost, final String schedulerTCPPort, final String action, final String expires, final String exitCode,
			final Map eventParameters, final String supervisorJobChain) throws Exception {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document addOrderDocument = docBuilder.newDocument();
			Element addOrderElement = addOrderDocument.createElement("add_order");
			addOrderDocument.appendChild(addOrderElement);
			addOrderElement.setAttribute("job_chain", supervisorJobChain);
			Element paramsElement = addOrderDocument.createElement("params");
			addOrderElement.appendChild(paramsElement);
			addParam(paramsElement, "action", action);
			addParam(paramsElement, "remote_scheduler_host", schedulerHost);
			addParam(paramsElement, "remote_scheduler_port", "" + schedulerTCPPort);
			addParam(paramsElement, "job_chain", jobChain);
			addParam(paramsElement, "order_id", orderId);
			addParam(paramsElement, "job_name", jobName);
			addParam(paramsElement, "event_class", eventClass);
			addParam(paramsElement, "event_id", eventId);
			addParam(paramsElement, "exit_code", exitCode);
			String now = SOSDate.getCurrentTimeAsString();
			addParam(paramsElement, "created", now);
			addParam(paramsElement, "expires", expires);

			Iterator keyIterator = eventParameters.keySet().iterator();
			while (keyIterator.hasNext()) {
				String name = keyIterator.next().toString();
				String value = eventParameters.get(name).toString();
				addParam(paramsElement, name, value);
			}
			StringWriter out = new StringWriter();
			OutputFormat of = new OutputFormat(addOrderDocument);
			of.setEncoding("iso-8859-1");
			XMLSerializer serializer = new XMLSerializer(out, of);
			serializer.serialize(addOrderDocument);
			String strOrdertxt = out.toString();
			logger.debug(strOrdertxt);
			return out.toString();
		}
		catch (Exception e) {
			throw new JobSchedulerException("Error creating add_order xml: " + e, e);
		}
	}

	private static void addParam(final Element paramsElement, final String name, final String value) {
		if (value != null && value.length() > 0) {
			Element paramElement = paramsElement.getOwnerDocument().createElement("param");
			paramElement.setAttribute("name", name);
			paramElement.setAttribute("value", value);
			paramsElement.appendChild(paramElement);
		}
	}

	private static void submitToSupervisor(String xml, final Log spooler_log, final Spooler spooler, final String host, final int port) throws Exception {
		try {
			if (xml.indexOf("<?xml") == -1) {
				xml = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>" + xml;
			}
			spooler_log.debug7("Sending xml:\n" + xml);
			Supervisor_client supervisor = null;
			try {
				supervisor = spooler.supervisor_client();
			}
			catch (Exception e) {
			} // there is no supervisor
			String answer;
			if (host.length() > 0 && port != 0) {
				SOSSchedulerCommand schedulerCommand = new SOSSchedulerCommand();
				schedulerCommand.setHost(host);
				schedulerCommand.setPort(port);
				schedulerCommand.setProtocol("tcp");
				spooler_log.debug1(".. connecting to JobScheduler " + schedulerCommand.getHost() + ":" + schedulerCommand.getPort());
				schedulerCommand.connect();
				schedulerCommand.sendRequest(xml);
				answer = schedulerCommand.getResponse();
			}
			else
				if (supervisor != null && supervisor.hostname() != null && supervisor.hostname().length() > 0) {
					SOSSchedulerCommand schedulerCommand = new SOSSchedulerCommand();
					schedulerCommand.setHost(supervisor.hostname());
					schedulerCommand.setPort(supervisor.tcp_port());
					schedulerCommand.setProtocol("tcp");
					spooler_log.debug1(".. connecting to JobScheduler " + schedulerCommand.getHost() + ":" + schedulerCommand.getPort());
					schedulerCommand.connect();
					schedulerCommand.sendRequest(xml);
					answer = schedulerCommand.getResponse();
				}
				else {
					spooler_log.info("No supervisor configured, submitting event to this JobScheduler.");
					answer = spooler.execute_xml(xml);
				}
			SOSXMLXPath xAnswer = new SOSXMLXPath(new StringBuffer(answer));
			String errorText = xAnswer.selectSingleNodeValue("//ERROR/@text");
			if (errorText != null && errorText.length() > 0) {
				throw new JobSchedulerException("supervisor returned error: " + errorText);
			}
		}
		catch (Exception e) {
			throw new JobSchedulerException("Failed to submit event: " + e, e);
		}
	}
}
