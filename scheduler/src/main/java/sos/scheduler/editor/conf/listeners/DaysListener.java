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
package sos.scheduler.editor.conf.listeners;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.jdom.Element;
import sos.scheduler.editor.app.Editor;
import sos.scheduler.editor.app.Options;
import sos.scheduler.editor.app.TreeData;
import sos.scheduler.editor.app.Utils;
import sos.scheduler.editor.conf.SchedulerDom;
import sos.scheduler.editor.conf.forms.SchedulerForm;;


public class DaysListener {


	public static final int   WEEKDAYS     = 0;

	public static final int   MONTHDAYS    = 1;

	public static final int   ULTIMOS      = 2;

	public static final int   SPECIFIC_MONTHS    = 3;

	public static final int   SPECIFIC_DAY    = 6;        

	private SchedulerDom      _dom;

	private Element           _runtime;

	/** 0 = weekdays 1 = monthdays 2 = ultimos */
	private int               _type        = 0;

	private static String[]   _elementName = { "weekdays", "monthdays", "ultimos", "month" };

	private static String[]   _weekdays    = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};


	private static String[]   _monthdays   = { "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th",
		"11th", "12th", "13th", "14th", "15th", "16th", "17th", "18th", "19th", "20th", "21st", "22nd", "23rd",
		"24th", "25th", "26th", "27th", "28th", "29th", "30th", "31st" };

	private static String[]   _ultimos     = { "last day", "1 day", "2 days", "3 days", "4 days", "5 days", "6 days",
		"7 days", "8 days", "9 days", "10 days", "11 days", "12 days", "13 days", "14 days", "15 days", "16 days",
		"17 days", "18 days", "19 days", "20 days", "21 days", "22 days", "23 days", "24 days", "25 days",
		"26 days", "27 days", "28 days", "29 days", "30 days" };


	private static String[]   _month       = { "january", "february", "march", "april", "may", "june", "july", "august", "september", 
		"october", "november", "december", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };

	private static String[][] _days        = { _weekdays, _monthdays, _ultimos, _month };

	private static int[]      _offset      = { 1, 1, 0, 1 };
	//private static int[]      _offset      = { 0, 1, 0, 1 };

	private String[]          _usedDays    = null;

	private Element[]         _dayElements = null;

	private boolean          _isWeekdaysHoliday = false;

	public DaysListener(SchedulerDom dom, Element runtime, int type,  boolean isWeekdaysHoliday) {
		if (type != WEEKDAYS && type != MONTHDAYS && type != ULTIMOS && type != SPECIFIC_MONTHS)
			throw new IllegalArgumentException("type must be 0, 1 or 6!");
		
		_isWeekdaysHoliday = isWeekdaysHoliday;
		_dom = dom;
		_type = type;
		_runtime = runtime;

		/*if(_runtime.getName().equals("config")){    		
			if(_runtime.getChild("holidays") != null)
				_runtime = _runtime.getChild("holidays");    	
		} 
		*/       

		setUsedDays();
	}


	public static String[] getWeekdays() {
		return _weekdays;
	}


	public static String[] getMonthdays() {
		return _monthdays;
	}

    public static String[] getMonth() {
        return _month;
    }
    
	public static String[] getUltimos() {
		return _ultimos;
	}


	public String[] getUnusedDays() {

		if (_usedDays == null)
			setUsedDays();
		ArrayList unused = new ArrayList();
		for (int i = 0; i < _days[_type].length; i++) {
			boolean found = false;
			for (int j = 0; j < _usedDays.length; j++) {
				if (_days[_type][i].equalsIgnoreCase(_usedDays[j])) {
					found = true;
				}
			}
			if (!found)
				unused.add(_days[_type][i]);
		}
		return (String[]) unused.toArray(new String[0]);
	}


	public String[] getUsedDays() {
		if (_usedDays == null)
			setUsedDays();
		return _usedDays;
	}


	public String[] getUsedDaysInString() {

		String[] used = getUsedDays();
		String a = "";
		for(int i = 0; i < used.length; i++) {
			a = "";
			String[] groupUsedDay = used[i].split(" ");
			if(groupUsedDay.length == 1) {
				a = groupUsedDay[0];
			} else
				for(int j = 0; j < groupUsedDay.length; j++) {
					try {
						//if(_type == 0)
						//a = (a.length() == 0? a : a + " ") + getAllDays()[Integer.parseInt(groupUsedDay[j])];
						//a = (a.length() == 0? a : a + " ") + groupUsedDay[j];
						//else
						a = (a.length() == 0? a : a + " ") + getAllDays()[Integer.parseInt(groupUsedDay[j])- _offset[_type]];
						//a = (a.length() == 0? a : a + " ") + getAllDays()[Integer.parseInt(groupUsedDay[j])];
					} catch (Exception e) {
						//falls groupUsedDay[j] bereits in STring konvertiert ist
						a = (a.length() == 0? a : a + " ") + groupUsedDay[j];
					}
				}
			used[i] = a;    		
			//System.out.println(a);

		}

		return used;
	}

	public String[] getUsedUltimosDaysInString() {
		String[] used = getUsedDays() ;

		if(used.length > 0) {
			for (int i = 0; _dayElements != null && i < _dayElements.length; i++) {
				Element e = _dayElements[i];
				String str = "";
				String[] group = Utils.getAttributeValue("day", e).split(" ");
				if(group.length == 1) {
					str = _days[_type][Integer.parseInt(group[0]) - _offset[_type]];
				} else
					for (int j = 0; j < group.length; j++) {
						str = (str.length() == 0 ? str : str + " ") + _days[_type][Integer.parseInt(group[j]) - _offset[_type]]; 
					}
				used[i] = str;    	
			}
		}

		return used;
	}


	public Element[] getDayElements() {
		return _dayElements;
	}

	public String[] getUsedMonth() {
		String[] retVal = new String[0];
		if(_type == SPECIFIC_MONTHS) {
			List l = _runtime.getChildren("month");
			retVal = new String[l.size()];
			for(int i = 0; i < l.size(); i++) {
				Element e = (Element)l.get(i);
				retVal[i] = Utils.getAttributeValue("month", e);
			}
		}
		return retVal;
	}

	public String[] getUnUsedMonth() {

		String[] usedMonth = getUsedMonth();
		ArrayList unused = new ArrayList(); 

		if(_type == SPECIFIC_MONTHS) {
			for (int i = 0; i < _days[_type].length; i++) {
				boolean found = false;
				for (int j = 0; j < usedMonth.length; j++) {
					if (_days[_type][i].equalsIgnoreCase(usedMonth[j])) {
						found = true;
					}
				}
				if (!found)
					unused.add(_days[_type][i]);    			    			
			}
		}
		return (String[]) unused.toArray(new String[0]);
	}

	private void setUsedDays() {
		
		if(_runtime.getChild("holidays") != null || _isWeekdaysHoliday)
			isHolidayWeeksdayParent();
		
		if (_runtime != null && _runtime.getChild(_elementName[_type]) != null) {
			
			Element daylist = _runtime.getChild(_elementName[_type]);

			List list = daylist.getChildren("day"); 
			int size = list.size();   
			String[] days = new String[size];
			Element[] elements = new Element[size];
			Iterator it = list.iterator();
			int i = 0;
			while (it.hasNext()) {
				Element e = (Element) it.next();
				try {
					if(Utils.getAttributeValue("day",e).indexOf(" ") == -1) {                		
						int day = 0;
						if(!Utils.isNumeric(e.getAttributeValue("day"))) {
							day = getDayNumber(e.getAttributeValue("day"));
							e.setAttribute("day", String.valueOf(day));
						} else {
							day = new Integer(e.getAttributeValue("day")).intValue();
						}
						if(_type == WEEKDAYS && day == 0) {
							day = 7; //0 und 7 werden als Sonntag interpretiert
							e.setAttribute("day", String.valueOf(day));
						}
						days[i] = _days[_type][day - _offset[_type]];  
						elements[i++] = e;                		
					} else {
						String[] split = Utils.getAttributeValue("day",e).split(" ");                		
						String attr = "";
						for(int j = 0; j < split.length ; j++) {
							int day = 0;
							if(!Utils.isNumeric(split[j])) {
								day =  getDayNumber(split[j]); 
							} else {
								day = new Integer(split[j]).intValue();
							}
							if(_type == WEEKDAYS && day == 0) {
								day = 7; //0 und 7 werden als Sonntag interpretiert
							}
							attr = (attr == null || attr.length() == 0? "" : attr + " ") + day;                			
						}

						e.setAttribute("day", attr);
						elements[i++] = e;

					}                	
				} catch (Exception ex) {
					try {
						new sos.scheduler.editor.app.ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName() + " ;Invalid day element in " + _elementName[_type], ex);
					} catch(Exception ee) {
						//tu nichts
					}

					System.out.println("Invalid day element in " + _elementName[_type]);
				}               
			}
			_usedDays = sort(days, elements);

		} else
			_usedDays = new String[0];
	}


	private String[] sort(String[] daylist, Element[] elements) {
		int size = elements.length;

		String[] sorted = new String[size];
		try {

			_dayElements = new Element[size];
			int index = 0;
			for (int i = 0; i < _days[_type].length; i++) {
				for (int j = 0; j < daylist.length; j++) {
					if (_days[_type][i].equalsIgnoreCase(daylist[j])) {
						sorted[index] = _days[_type][i];
						_dayElements[index++] = elements[j];
					}    			
				}
			}
			for (int j = 0; j < elements.length ; j++) {
				Element e = elements[j];
				if(Utils.getAttributeValue("day", e).indexOf(" ") > -1) {
					sorted[index] = e.getAttributeValue("day") ;
					_dayElements[index++] = elements[j];
				}
			}


			return sorted;
		} catch (Exception e) {
			try {
				new sos.scheduler.editor.app.ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName() , e);
			} catch(Exception ee) {
				//tu nichts
			}
			System.err.println("error in daysListener.sort: " + e.getMessage());
			return sorted;
		}

	}


	private int getDayNumber(String day) {


		for (int i = 0; i < _days[_type].length; i++) {
			if (_days[_type][i].equalsIgnoreCase(day))
				return i + _offset[_type];
		}                  

		return -1;
	}

	/*private String getDayNumber(int day) {
    	return _days[_type][day];
    }*/

	private String getDayGroupNumbers (String day) {
		String retVal = "";
		String[] group = null;
		if(_type == ULTIMOS) {
			group = getNormalizedUltimos(day);
		} else {
			group = day.split(" ");
		}
		for (int j = 0; j < group.length; j++) {
			for (int i = 0; i < _days[_type].length; i++) {
				if (_days[_type][i].equalsIgnoreCase(group[j]))
					retVal = (retVal.length() == 0 ? retVal : retVal + " ") +  (i + _offset[_type]);
			}   
		}
		return retVal;
	}


	public void addDay(String day) {

		//Wenn Vaterknoten holidays ist
		isHolidayWeeksdayParent();

		Element daylist = _runtime.getChild(_elementName[_type]);
		if (daylist == null && _type != SPECIFIC_MONTHS) {
			daylist = new Element(_elementName[_type]);
			_runtime.addContent(daylist);
		}

		if(_type == SPECIFIC_MONTHS) {
			List l = _runtime.getChildren("month");
			boolean found = false;
			for(int i = 0; i < l.size(); i++) {
				Element e = (Element)l.get(i);
				if(Utils.getAttributeValue("month", e).equals(day) )
					found = true;        		
			}
			if(!found) {
				daylist = new Element(_elementName[_type]);
				_runtime.addContent(daylist);
				Utils.setAttribute("month", day, daylist);
			}
		} else { 
			daylist.addContent(new Element("day").setAttribute("day", "" + getDayNumber(day)));
		}

		_dom.setChanged(true);
		if(_runtime != null && _runtime.getParentElement() != null )
			//_dom.setChangedForDirectory("job", Utils.getAttributeValue("name",_runtime.getParentElement()), SchedulerDom.MODIFY);
			_dom.setChangedForDirectory(_runtime, SchedulerDom.MODIFY);


		setUsedDays();
	}

	public void addGroup (String group) {
		String[] split = null;
		//Wenn Vaterknoten holidays ist
		isHolidayWeeksdayParent();
		
		Element daylist = _runtime.getChild(_elementName[_type]);
		if (daylist == null && _type != SPECIFIC_MONTHS) {
			daylist = new Element(_elementName[_type]);
			_runtime.addContent(daylist);
		}
		if(_type == SPECIFIC_MONTHS) {
			List l = _runtime.getChildren("month");
			boolean found = false;
			for(int i = 0; i < l.size(); i++) {
				Element e = (Element)l.get(i);
				if(Utils.getAttributeValue("month", e).equals(group) )
					found = true;        		
			}
			if(!found) {
				daylist = new Element(_elementName[_type]);
				_runtime.addContent(daylist);
				Utils.setAttribute("month", group, daylist);
			}
		} else {

			if(_type == ULTIMOS) {
				split= getNormalizedUltimos(group);        	        	
			} else {
				split = group.split(" ");
			}
			String attr = "";
			for(int i = 0; i < split.length; i++) {
				attr = (attr.length() == 0 ? "" : attr + " ") + getDayNumber(split[i]);
			}
			daylist.addContent(new Element("day").setAttribute("day", attr));	
		}

		_dom.setChangedForDirectory(_runtime, SchedulerDom.MODIFY);
		setUsedDays();
	}

	public void updateGroup (String newGroup, String oldGroup) {
		String[] split = null;
		Element daylist = _runtime.getChild(_elementName[_type]);

		if (daylist == null && _type != SPECIFIC_MONTHS) {
			daylist = new Element(_elementName[_type]);
			_runtime.addContent(daylist);
		}

		if(_type == SPECIFIC_MONTHS) {
			List l = _runtime.getChildren("month");
			boolean found = false;
			for(int i = 0; i < l.size(); i++) {
				Element e = (Element)l.get(i);
				if(Utils.getAttributeValue("month", e).equals(oldGroup) ) {
					found = true;        		
					e.setAttribute("month", newGroup);
				}
			}
			if(!found) {
				daylist = new Element(_elementName[_type]);
				_runtime.addContent(daylist);
				Utils.setAttribute("month", newGroup, daylist);
			}    		

		} else {

			boolean found = false;
			String[] used = getUsedDays() ;

			if(used.length > 0) {
				for (int i = 0; _dayElements != null && i < _dayElements.length; i++) {
					Element e = _dayElements[i];
					String str = "";
					String[] group = Utils.getAttributeValue("day", e).split(" ");
					if(group.length == 1) {
						str = _days[_type][Integer.parseInt(group[0]) - _offset[_type]];
					} else
						for (int j = 0; j < group.length; j++) {
							str = (str.length() == 0 ? str : str + " ") + _days[_type][Integer.parseInt(group[j]) - _offset[_type]]; 
						}
					if(str.equals(oldGroup)) {    	    			    	
						e.setAttribute("day", getDayGroupNumbers(newGroup));
						found = true;
					}    	    			
				}
				if(!found) {
					split= getNormalizedUltimos(newGroup); 
					String attr = "";
					for(int i = 0; i < split.length; i++) {
						attr = (attr.length() == 0 ? "" : attr + " ") + getDayNumber(split[i]);
					}
					daylist.addContent(new Element("day").setAttribute("day", attr));
				}

			}

		}
		_dom.setChangedForDirectory(_runtime, SchedulerDom.MODIFY);

		setUsedDays();
	}

	public void deleteMonth(String month) {
		Element daylist = _runtime.getChild(_elementName[_type]);
		if (daylist != null) {
			List list = _runtime.getChildren(_elementName[_type]);;
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element e = (Element) it.next();
				if (e.getAttributeValue("month") != null && e.getAttributeValue("month").equals(month)) {
					e.detach();

					// remove empty tag
					if (list.size() == 0)
						_runtime.removeChild(_elementName[_type]);

					_dom.setChanged(true);
					if(_runtime != null && _runtime.getParentElement() != null )
						//_dom.setChangedForDirectory("job", Utils.getAttributeValue("name",_runtime.getParentElement()), SchedulerDom.MODIFY);
						_dom.setChangedForDirectory(_runtime, SchedulerDom.MODIFY);
					setUsedDays();
					break;
				}
			}
		}
	}

	public void deleteDay(String day) {
		if(_type == SPECIFIC_MONTHS) {
			deleteMonth(day);
			return;
		}

		Element daylist = _runtime.getChild(_elementName[_type]);
		if (daylist != null) {
			List list = daylist.getChildren("day");
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element e = (Element) it.next(); 

				//if(e.getName().equals("day")) {
				if (e.getName().equals("day") && 
						(e.getAttributeValue("day") != null && (e.getAttributeValue("day").equals("" + getDayNumber(day))
								|| e.getAttributeValue("day").equals(day) || e.getAttributeValue("day").equals(getDayGroupNumbers(day))))) { 
					e.detach();



					// remove empty tag
					boolean isEmpty = true;
					List _list = _runtime.getChildren(_elementName[_type]);
					for(int i = 0; i < _list.size(); i++) {
						Element s = (Element)_list.get(i);
						if(s.getChildren().size() > 0) {
							//_elementName[_type] wird noch woanders verwendet
							isEmpty = false;
							break;
						}
					}

					if (list.size() == 0 && isEmpty) 
						//((Element)_runtime.getChildren(_elementName[_type]).get(0)).getChildren().size() == 0)                    
						_runtime.removeChild(_elementName[_type]);

					_dom.setChanged(true);
					if(_runtime != null && _runtime.getParentElement() != null )
						//_dom.setChangedForDirectory("job", Utils.getAttributeValue("name",_runtime.getParentElement()), SchedulerDom.MODIFY);
						_dom.setChangedForDirectory(_runtime, SchedulerDom.MODIFY);
					setUsedDays();
					break;
				}
			}
		}
	}


	//test
	public void updateDay(String newDay, String oldDay) {


		Element daylist = _runtime.getChild(_elementName[_type]);
		if (daylist != null) {
			List list = daylist.getChildren("day");
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element e = (Element) it.next(); 

				if (e.getAttributeValue("day") != null && (e.getAttributeValue("day").equals("" + getDayNumber(oldDay))
						|| e.getAttributeValue("day").equals(oldDay) || e.getAttributeValue("day").equals(getDayGroupNumbers(oldDay)))) { 
					e.setAttribute("day", getDayGroupNumbers(newDay));


					// remove empty tag
					//if (list.size() == 0)
					//    _runtime.removeChild(_elementName[_type]);

					_dom.setChanged(true);
					if(_runtime != null && _runtime.getParentElement() != null )
						//_dom.setChangedForDirectory("job", Utils.getAttributeValue("name",_runtime.getParentElement()), SchedulerDom.MODIFY);
						_dom.setChangedForDirectory(_runtime, SchedulerDom.MODIFY);
					setUsedDays();
					break;
				}
			}
		}
	}

	public void fillTreeDays(TreeItem parent, boolean expand) {
		try {
		String[] used = null;
		parent.removeAll();

		if (_usedDays == null)
			setUsedDays();


		used = getUsedDays(); 

		for (int i = 0; used!= null && i < used.length; i++) {
			TreeItem item = new TreeItem(parent, SWT.NONE);
			item.setText(used[i]); 
			
			item.setData("max_occur", "1");
			item.setData("key",  used[i] +"_@_"+ _dayElements[i].getName());
			item.setData("copy_element", _dayElements[i]);
			
			item.setData(new TreeData(Editor.PERIODS, _dayElements[i], Options.getHelpURL("periods")));
			if(_runtime != null && !Utils.isElementEnabled("job", _dom, _runtime)) {
				item.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
			}
		}
		parent.setExpanded(expand);
		} catch (Exception e) {
			try {
				new sos.scheduler.editor.app.ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName() + " ; could not open file on Webdav Server", e);
			} catch(Exception ee) {
				//tu nichts
			}
			
		}
	}

	public String[] getAllDays() {
		return _days[_type];
	}

	
	public static java.util.HashMap getDays() {
		java.util.HashMap l = new java.util.HashMap();
		l.put("weekdays",_weekdays); 
		l.put("monthdays", _monthdays);
		l.put("ultimos", _ultimos);
		l.put("month", _month);
		return l;
	}
	
	/*public String[] getNormalizedUltimos(String group) {
    	String[] allUltimos =  getUltimos();
    	ArrayList l = new ArrayList();
    	//for (int i = 0; i < allUltimos.length; i++) {
    	//for (int i = allUltimos.length - 1; i != 0; i--) {
    		if(group.indexOf(allUltimos[i].concat(" ")) > -1 || group.endsWith(allUltimos[i])) {
    			l.add(allUltimos[i]);
    		}
    	}
    	String[] split = new String[l.size()];
    	for(int i = 0; i < l.size(); i++)
    	    split[i] = l.get(i).toString()
    	//for(int i = l.size()-1 ; i >= 0; i--)
    		//split[l.size() - i - 1] = l.get(i).toString();
    	return split;
    }
	 */

	public String[] getNormalizedUltimos(String group) {
		String[] allUltimos =  getUltimos();
		ArrayList l = new ArrayList();
		for (int i = 0; i < allUltimos.length; i++) {
			//if(group.indexOf(allUltimos[i].concat(" ")) > -1 || group.endsWith(" ".concat(allUltimos[i]))) {
			if(group.startsWith(allUltimos[i]) || group.indexOf(" " + allUltimos[i].concat(" ")) > -1 || group.endsWith(" ".concat(allUltimos[i]))) {
				l.add(allUltimos[i]);
			}
		}
		String[] split = new String[l.size()];
		for(int i = 0; i < l.size(); i++)
			split[i] = l.get(i).toString();
		return split;
	}

	
	//wenn Vaterknoten holiday ist
	private void isHolidayWeeksdayParent() {

		if(_runtime.getName().equals("holidays"))
			return;
		
		if(sos.scheduler.editor.app.MainWindow.getContainer().getCurrentEditor() instanceof sos.scheduler.editor.doc.forms.DocumentationForm)
			return;
			
		SchedulerForm f = (SchedulerForm)(sos.scheduler.editor.app.MainWindow.getContainer().getCurrentEditor());
		
		if(f == null)
			return;
		
		Tree tree = f.getTree();
		if(tree != null && tree.getSelectionCount() > 0) {
			TreeItem item = f.getTree().getSelection()[0];
            if(item.getParentItem() != null && (item.getParentItem().getText().equalsIgnoreCase("Holidays") ||
            		(item.getParentItem().getData("key") != null && item.getParentItem().getData("key").equals("holidays")))) {
			//if(_runtime.getName().equals("config")){
				if(_runtime.getChild("holidays") != null)
					_runtime = _runtime.getChild("holidays");
				else {
					_runtime.addContent(new Element("holidays"));
					_runtime = _runtime.getChild("holidays");
				}
			}
		}
	}


}
