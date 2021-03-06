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
package sos.scheduler.editor.actions.listeners;


import org.jdom.Element;
import org.jdom.xpath.XPath;

import sos.scheduler.editor.actions.ActionsDom;
import sos.scheduler.editor.app.Editor;
import sos.scheduler.editor.app.MainWindow;
import sos.scheduler.editor.app.Utils;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;

import java.util.ArrayList;
import java.util.List;

public class EventListener {
   
	private ActionsDom           _dom                 = null;

    private Element              _eventGroup              = null;
    
    private int                  type                 = -1;
   
    public EventListener(ActionsDom dom, Element eventGroup, int type_) {
        _dom = dom;
        _eventGroup = eventGroup;
        type = type_;
        
    }


	
 
	public void fillEvent(Table table){
		table.removeAll();
		//if(_events != null) {
		List l = null;
		//if(_eventGroup.getName().equals("commands")) {
		try {
			if(type == Editor.ADD_EVENT_GROUP) {		
				XPath x3 = XPath.newInstance("add_event/event");
				l = x3.selectNodes(_eventGroup);
			} else if(type == Editor.REMOVE_EVENT_GROUP) {
			
				XPath x3 = XPath.newInstance("remove_event/event");				 
				l = x3.selectNodes(_eventGroup);			
				/*XPath x3 = XPath.newInstance("//remove_event/event");				 
				l = x3.selectNodes(_dom.getDoc());*/
							 
			} else {
				l = _eventGroup.getChildren("event");		
			}
		} catch(Exception e) {
			MainWindow.message(e.getMessage(), SWT.ICON_WARNING);	
		}
		//}
		
			for(int i = 0; i < l.size(); i++){
				Element event = (Element)l.get(i);
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(0, Utils.getAttributeValue("event_name", event));
				item.setText(1, Utils.getAttributeValue("event_id", event));
				item.setText(2, Utils.getAttributeValue("event_title", event));
				item.setText(3, Utils.getAttributeValue("event_class", event));
				
				item.setText(4, Utils.getAttributeValue("job_name", event));
				item.setText(5, Utils.getAttributeValue("job_chain", event));
				item.setText(6, Utils.getAttributeValue("order_id", event));
				item.setText(7, Utils.getAttributeValue("comment", event));
				item.setText(8, Utils.getAttributeValue("exit_code", event));
				item.setText(9, Utils.getAttributeValue("expiration_period", event));
				item.setText(10, Utils.getAttributeValue("expiration_cycle", event));
				item.setData(event);
			}
		}
	
   
	public void apply(String eventName, String eventId, String eventClass, String eventTitle,
			String jobname, String jobChain, String orderId, String comment, String exitCode,
			String expirationPeriod, String expirationCycle,
			Table table) {
		
		Element event = null;
		
		if(table.getSelectionCount() > 0) {
			event = (Element)table.getSelection()[0].getData();
		} else if(table.getItemCount() > 0 && 
			((Element)table.getItem(0).getData() != null && ((Element)table.getItem(0).getData()).getAttributes().isEmpty())) {
			//Defaultm�ssig wird ein leeres Element genommen. Der soll gef�llt werden
			event = (Element)table.getItem(0).getData();
		} else {					
			event = new Element("event");
			
			if(type == Editor.ADD_EVENT_GROUP) {
			
				Element addEvent = new Element("add_event");
				addEvent.addContent(event);
				_eventGroup.addContent(addEvent);   
			
			}else if(type == Editor.REMOVE_EVENT_GROUP){
			
					Element removeEvent = new Element("remove_event");
					removeEvent.addContent(event);
					_eventGroup.addContent(removeEvent);            					   
								
			} else {
				_eventGroup.addContent(event);
			}
		}

		Utils.setAttribute("event_name", eventName, event);
		Utils.setAttribute("event_id", eventId, event);
		Utils.setAttribute("event_class", eventClass, event);
		Utils.setAttribute("event_title", eventTitle, event);
		Utils.setAttribute("job_name", jobname, event);
		Utils.setAttribute("job_chain", jobChain, event);
		Utils.setAttribute("order_id", orderId, event);
		Utils.setAttribute("comment", comment, event);
		Utils.setAttribute("exit_code", exitCode, event);
		Utils.setAttribute("expiration_period", expirationPeriod, event);
		Utils.setAttribute("expiration_cycle", expirationCycle, event);

		fillEvent(table);

		_dom.setChanged(true);
	}
	
	public void removeEvent(Table table) {
	        if(table.getSelectionCount() > 0) {
	        	TableItem item = table.getSelection()[0];
	        	Element elem = (Element)item.getData();
	        	if(type == Editor.REMOVE_EVENT_GROUP || type == Editor.ADD_EVENT_GROUP) {
        			if(elem.getParentElement() != null)
        			elem.getParentElement().detach();
        		} else
        			elem.detach();
	        	table.remove(table.getSelectionIndex());
	        	
	        	
	        	
	        	fillEvent(table);

	        	//if(table.getItemCount() == 0 && _eventGroup != null) {
	        		if(type == Editor.REMOVE_EVENT_GROUP || type == Editor.ADD_EVENT_GROUP) {
	        			if(elem.getParentElement() != null)
	        			elem.getParentElement().detach();
	        		}

	        	_dom.setChanged(true);
	        }
	}
	
	public String getEventGroupName() {
		if(_eventGroup != null)			
			return Utils.getAttributeValue("group", _eventGroup);
		else 
			return "";
	}
	
	public String getActionName() {

		Element action = _eventGroup;
		boolean loop = true;
		while(loop){
			if(action == null)
				loop = false;
			
			if(action.getName().equals("action")){
				loop = false;
			} else{
				action = action.getParentElement();		
			}
		}
		
		/*if(_eventGroup != null && _eventGroup.getParentElement() != null && _eventGroup.getParentElement().getParentElement() != null && _eventGroup.getParentElement().getParentElement().equals("action")) {			
				return Utils.getAttributeValue("name", _eventGroup.getParentElement().getParentElement());
		} else if(_eventGroup != null && 
				  _eventGroup.getParentElement() != null && _eventGroup.getParentElement().getParentElement() != null && _eventGroup.getParentElement().getParentElement().equals("action"))
		} else
		*/
			if(action !=  null)
				return Utils.getAttributeValue("name", action);
			else
				return "";
	}
	
	public String[] getEventClasses() {
		String[] retVal = new String[]{};
		try {
			if(_eventGroup != null) {
				List l = _eventGroup.getChildren("event");
				ArrayList list = new ArrayList();
				for(int i=0; i < l.size(); i++) {					
					Element event = (Element)l.get(i);
					String eventClass = Utils.getAttributeValue("event_class", event); 
					if(eventClass.length() > 0 && !list.contains(eventClass)) {
						list.add(Utils.getAttributeValue("event_class", event));	
					}
				}
				if(list.size() > 0)
					retVal = new String[list.size()];
				for(int i = 0; i < list.size(); i++) {
					retVal[i] = list.get(i).toString(); 
				}
			}
		} catch (Exception e) {
           //tu nichts
			System.out.println(e.toString());
		}
		return retVal;
	}
	
}
