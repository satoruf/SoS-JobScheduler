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
 * JobSchedulerEventExistsJob.java
 * Created on 25.06.2008
 *
 */
package sos.scheduler.job;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import sos.spooler.Variable_set;

import com.sos.JSHelper.Exceptions.JobSchedulerException;

/**
 *
 * @author andreas.liebert@sos-berlin.com
 *
 *    This job is used to check if certain events exist
 *
 */
public class JobSchedulerExistsEventJob extends JobSchedulerJob {

	@Override
	public boolean spooler_process() throws JobSchedulerException {
		boolean rc = true;
		try {

			// merge params
			Variable_set params = spooler.create_variable_set();
			if (spooler_task.params() != null)
				params.merge(spooler_task.params());
			if (spooler_job.order_queue() != null && spooler_task.order().params() != null)
				params.merge(spooler_task.order().params());

			String eventSpec = "";
			if (params.var("scheduler_event_spec") != null && params.var("scheduler_event_spec").length() > 0) {
				eventSpec = params.var("scheduler_event_spec");
			}
			else {
				throw new JobSchedulerException("parameter scheduler_event_spec is missing");
			}
			getLogger().debug3(".. job parameter [scheduler_event_spec]: " + eventSpec);

			getLogger().debug("Checking events for: " + eventSpec);

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document eventDocument = docBuilder.newDocument();
			eventDocument.appendChild(eventDocument.createElement("events"));

			JobSchedulerEventJob.readEventsFromDB(getConnection(), spooler, eventDocument, getLogger());

			NodeList nodes = XPathAPI.selectNodeList(eventDocument, eventSpec);
			if (nodes == null || nodes.getLength() == 0) {
				getLogger().info("No matching events were found.");
				rc = false;
			}
			else {
				getLogger().info("Matching events were found.");
				rc = true;
			}
		}
		catch (Exception e) {
			throw new JobSchedulerException("Error checking events: " + e, e);
		}
		return rc;
	}
}