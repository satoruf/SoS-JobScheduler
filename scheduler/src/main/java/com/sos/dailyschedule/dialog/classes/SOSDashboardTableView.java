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
package com.sos.dailyschedule.dialog.classes;

import java.io.File;
import java.util.Date;
import java.util.prefs.Preferences;

import com.sos.dailyschedule.classes.SosDailyScheduleTableItem;
import com.sos.dailyschedule.db.DailyScheduleDBItem;
import com.sos.dashboard.globals.DashBoardConstants;
import com.sos.dashboard.globals.SOSDashboardOptions;
import com.sos.dialog.classes.SOSTable;
import com.sos.dialog.comparators.DateComperator;
import com.sos.dialog.comparators.SortBaseComparator;
import com.sos.dialog.comparators.StringComparator;
import com.sos.dialog.components.SOSTableColumn.ColumnType;
import com.sos.dialog.interfaces.ITableView;
import com.sos.hibernate.classes.DbItem;
import com.sos.hibernate.classes.SosSortTableItem;
import com.sos.hibernate.interfaces.ISOSDashboardDataProvider;
import com.sos.hibernate.interfaces.ISOSTableItem;
import com.sos.jobnet.db.JobNetDBLayer;
import com.sos.scheduler.db.SchedulerInstancesDBItem;
import com.sos.scheduler.db.SchedulerInstancesDBLayer;
import com.sos.scheduler.history.SchedulerHistoryDataProvider;
import com.sos.scheduler.history.db.SchedulerOrderHistoryDBItem;
import com.sos.scheduler.history.db.SchedulerOrderHistoryDBLayer;
import com.sos.scheduler.history.db.SchedulerTaskHistoryDBItem;
import com.sos.scheduler.history.db.SchedulerTaskHistoryDBLayer;
import com.sos.scheduler.model.SchedulerObjectFactory;
import com.sos.scheduler.model.commands.JSCmdModifyOrder;
import com.sos.scheduler.model.commands.JSCmdStartJob;
import com.sos.scheduler.model.objects.Spooler;

import oracle.jdbc.dbaccess.DBItem;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Listener;

public class SOSDashboardTableView extends SOSDashboardMainView implements ITableView {
	private static final String conSettingREFRESHDefault = "60";
	private static final String SOS_DASHBOARD_HEADER = "sosDashboardHeader";
	private static final String conSettingREFRESH = "refresh";
	private static Logger logger = Logger.getLogger(SOSDashboardTableView.class);
	protected SOSTable tableList = null;
	protected ISOSDashboardDataProvider tableDataProvider = null;
	private SortBaseComparator[][] comparables = null;
	private ISOSTableItem lastItem = null;

	protected SchedulerOrderHistoryDBLayer schedulerOrderHistoryDBLayer = null;
	protected SchedulerTaskHistoryDBLayer schedulerTaskHistoryDBLayer = null;
	protected SchedulerInstancesDBLayer schedulerInstancesDBLayer;

	public SOSDashboardTableView(Composite composite_) {
		super(composite_);
	}

	@Override
	public void getTableData() {
		logger.debug("...getList");
		if (tableList != null) {
			tableDataProvider.getData();
			buildTable();
		}
	}

	@Override
	public void buildTable() {
		this.showWaitCursor();
		if (tableList != null) {
			if (tableDataProvider.getFilter() != null && left != null) {
				left.setText(tableDataProvider.getFilter().getTitle());
			}
			clearTable(tableList);
			tableDataProvider.fillTable(tableList);
			SosSortTableItem sosSortTableItem = null;
			sosSortTableItem = null;
			int ll = tableList.getItemCount();
			comparables = new SortBaseComparator[tableList.getColumnCount()][ll];
			for (int i = 0; i < tableList.getItemCount(); i++) {
				for (int k = 0; k < tableList.getColumnCount(); k++) {
					sosSortTableItem = new SosSortTableItem((ISOSTableItem) tableList.getItems()[i]);
					if (this.tableList.getSOSTableColumn(k).getColumnType() == ColumnType.DATE) {
						comparables[k][i] = new DateComperator(sosSortTableItem, i, k);
					} else {
						comparables[k][i] = new StringComparator(sosSortTableItem, i, k);
					}
				}
			}
			sortTable(tableList, comparables);
		}
		this.RestoreCursor();
	}

	@Override
	public void createTable() {
		sosDashboardHeader.getCbSchedulerId().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				tableDataProvider.setSchedulerId(sosDashboardHeader.getCbSchedulerId().getText());
				getList();
			}
		});
		sosDashboardHeader.getToDate().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableDataProvider.setTo(sosDashboardHeader.getTo());
				getList();
			}
		});
		sosDashboardHeader.getFromDate().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableDataProvider.setFrom(sosDashboardHeader.getFrom());
				getList();
			}
		});
		sosDashboardHeader.getRefreshInterval().addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent arg0) {
				sosDashboardHeader.setRefresh(getIntValue(sosDashboardHeader.getRefreshInterval().getText(), 10));
				sosDashboardHeader.resetRefreshTimer();
				prefs.node(SOS_DASHBOARD_HEADER).put(conSettingREFRESH, sosDashboardHeader.getRefreshInterval().getText());
			}
		});
		sosDashboardHeader.getRefreshInterval().setText(prefs.node(SOS_DASHBOARD_HEADER).get(conSettingREFRESH, conSettingREFRESHDefault));
		sosDashboardHeader.getSearchField().addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				if (sosDashboardHeader.getSearchField() != null) {
					tableDataProvider.setSearchField(sosDashboardHeader.getSearchField().getText());
					sosDashboardHeader.resetInputTimer();
				}
			}
		});
		sosDashboardHeader.getRefreshButton().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				getList();
				getSchedulerIds();
			}
		});
		/*tableList.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				setRightMausclick(event);
				Point coords = new Point(event.x, event.y);
				ISOSTableItem row = (ISOSTableItem) tableList.getItem(coords);
				if (((lastItem != null) && (!lastItem.isDisposed()))) {
					lastItem.setColor();
				}

				if (row != null) {
					lastItem = (ISOSTableItem) row;
					row.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
				}
			}
		});

		tableList.addListener(SWT.MouseHover, new Listener() {
			public void handleEvent(Event e) {
				Point coords = new Point(e.x, e.y);
				ISOSTableItem row = (ISOSTableItem) tableList.getItem(coords);
				if (((lastItem != null) && (!lastItem.isDisposed()))) {
					lastItem.setColor();
				}
				if (row != null) {
					lastItem = row;
					row.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
				}

			}
		});
*/
		tableList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				if (!isRightMouseclick()) {
					TableItem t = tableList.getItem(tableList.getSelectionIndex());
					showLog(tableList);
					DbItem d = (DbItem) t.getData();
					right.setText(d.getTitle());
					detailHistoryDataProvider.setFrom(sosDashboardHeader.getFrom());
					detailHistoryDataProvider.setTo(sosDashboardHeader.getTo());
					detailHistoryDataProvider.setSchedulerId(d.getSchedulerId());
					detailHistoryDataProvider.setJobname(d.getJob());
					detailHistoryDataProvider.setJobchain(d.getJobChain());
					detailHistoryDataProvider.setOrderid(d.getOrderId());
					detailHistoryDataProvider.getData();
					clearTable(tableHistoryDetail);
					detailHistoryDataProvider.fillTableShort(tableHistoryDetail, d.isStandalone());
				}
			}
		});

		this.setColumnsListener();
		this.tableResize();
	}

	private void tableResize() {
		mainViewComposite.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = mainViewComposite.getClientArea();
				Point size = tableList.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				TableColumn lastColumn = tableList.getColumns()[tableList.getColumnCount() - 1];
				int colWidth = 0;
				for (int i = 0; i < tableList.getColumns().length; i++) {
					colWidth = colWidth + tableList.getColumns()[i].getWidth();
				}
				colWidth = colWidth - lastColumn.getWidth() - 5;
				ScrollBar vBar = tableList.getVerticalBar();
				int width = area.width - tableList.computeTrim(0, 0, 0, 0).width;
				if (size.y > area.height + tableList.getHeaderHeight()) {
					Point vBarSize = vBar.getSize();
					if (vBar.isVisible()) {
						width -= vBarSize.x;
					}
				}
				Point oldSize = tableList.getSize();
				if (oldSize.x > area.width) {
					lastColumn.setWidth(width - colWidth);
					tableList.setSize(area.width, area.height);
				} else {
					tableList.setSize(area.width, area.height);
					lastColumn.setWidth(width - colWidth);
				}
			}
		});
	}

	public void setColumnsListener() {
		TableColumn[] columns = tableList.getColumns();
		for (int i = 0; i < columns.length; i++) {
			final int _i = i;
			columns[i].addListener(SWT.Selection, new Listener() {
				private int colPos = -1;
				private boolean sortFlag;
				{
					colPos = _i;
				}

				public void handleEvent(Event event) {
					sortFlag = !sortFlag;
					tableList.setSortColumn(tableList.getColumn(colPos));
					if (sortFlag) {
						tableList.setSortDirection(SWT.UP);
					} else {
						tableList.setSortDirection(SWT.DOWN);
					}
					colPosForSort = colPos;
					colSortFlag = sortFlag;
					sortTable(tableList, comparables);
				}
			});
		}
	}

	
	   protected SchedulerInstancesDBItem start(DbItem dbItem) {
	        this.showWaitCursor();
  
	        SchedulerInstancesDBItem schedulerInstanceDBItem = schedulerInstancesDBLayer.getInstanceById(dbItem.getSchedulerId());
	        if (schedulerInstanceDBItem != null) {
	            SchedulerObjectFactory objSchedulerObjectFactory = new SchedulerObjectFactory(schedulerInstanceDBItem.getHostName(), schedulerInstanceDBItem.getTcpPort());
	            objSchedulerObjectFactory.initMarshaller(Spooler.class);
	            if (dbItem.isOrderJob()) {
	                try {
	                    JSCmdModifyOrder objOrder = objSchedulerObjectFactory.StartOrder(dbItem.getJobChain(), dbItem.getOrderId(), false);
 	                } catch (Exception ee) {
 	                    ee.printStackTrace();
	                } finally {
	                    this.RestoreCursor();
	                }

	            } else {
	                try {
	                    JSCmdStartJob objStartJob = objSchedulerObjectFactory.StartJob(dbItem.getJobName(), false);
	                }
	                catch (Exception ee) {
	                    ee.printStackTrace();
	                } finally {
	                    getList();
	                    this.RestoreCursor();
	                }
	            }
	        }
	     return schedulerInstanceDBItem;

	    }
	
	protected void showLog(Table table) {
		this.showWaitCursor();
		if (table.getSelectionIndex() >= 0 && table.getSelectionIndex() >= 0) {
			SosTabLogItem logItem = (SosTabLogItem) logTabFolder.getSelection();
			if (logItem == null) {
				logTabFolder.setSelection(0);
				logItem = (SosTabLogItem) logTabFolder.getSelection();
			}
			TableItem t = table.getItem(table.getSelectionIndex());
			DbItem d = (DbItem) t.getData();
			logItem.addLog(table, d.getTitle(), detailHistoryDataProvider.getLogAsString(d));
		}
		this.RestoreCursor();
	}

	@Override
	public void getList() {
		logger.debug("...getList");
		if (tableList != null && tableDataProvider != null) {
	        int i = tableList.getTopIndex();
			tableDataProvider.getData();
			buildTable();
			tableList.setTopIndex(i);		
			}
	}

	public void getSchedulerIds() {
		logger.debug("...getSchedulerIds");
		if (tableList != null && tableDataProvider != null && sosDashboardHeader != null) {
			tableDataProvider.fillSchedulerIds(sosDashboardHeader.getCbSchedulerId());
		}
	}

	@Override
	public void actualizeList() {
		buildTable();
	}

	public void setLeftTabFolder(CTabFolder leftTabFolder) {
		this.leftTabFolder = leftTabFolder;
	}

	public void setPrefs(Preferences prefs) {
		this.prefs = prefs;
	}

	public void setDetailHistoryDataProvider(SchedulerHistoryDataProvider detailHistoryDataProvider) {
		this.detailHistoryDataProvider = detailHistoryDataProvider;
	}

	public void setSosDashboardHeaderplanned(SosDashboardHeader sosDashboardHeader) {
		this.sosDashboardHeader = sosDashboardHeader;
	}

	public void setComparablesplanned(SortBaseComparator[][] comparablesplanned) {
		this.comparables = comparablesplanned;
	}

	public void setLogTabFolder(CTabFolder logTabFolder) {
		this.logTabFolder = logTabFolder;
	}

	public void setTableHistoryDetail(Table tableHistoryDetail) {
		this.tableHistoryDetail = tableHistoryDetail;
	}

	public SosDashboardHeader getSosDashboardHeader() {
		return sosDashboardHeader;
	}

	public void setObjOptions(SOSDashboardOptions objOptions) {
		this.objOptions = objOptions;
	}

	@Override
	public void createMenue() {
		logger.info("No menu is defined");
	}

	public void setRight(Group right) {
		this.right = right;
	}

	public void setLeft(Group left) {
		this.left = left;
	}

	public Composite getTableComposite() {
		return mainViewComposite;
	}

	public void setTableDataProvider(ISOSDashboardDataProvider tableDataProvider) {
		this.tableDataProvider = tableDataProvider;
	}

	public SOSTable getTableList() {
		return tableList;
	}
	   public void setDBLayer(File configurationFile)  {
	        schedulerInstancesDBLayer = new SchedulerInstancesDBLayer(configurationFile);
	        schedulerOrderHistoryDBLayer = new SchedulerOrderHistoryDBLayer(configurationFile);
	        schedulerTaskHistoryDBLayer = new SchedulerTaskHistoryDBLayer(configurationFile);
	    }
	    
}
