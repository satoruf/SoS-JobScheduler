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
package sos.scheduler.editor.app;
import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.jdom.input.SAXBuilder;

import sos.scheduler.editor.actions.forms.ActionsForm;
import sos.scheduler.editor.conf.SchedulerDom;
import sos.scheduler.editor.conf.forms.JobChainConfigurationForm;
import sos.scheduler.editor.conf.forms.SchedulerForm;
import sos.scheduler.editor.doc.forms.DocumentationForm;

public class TabbedContainer implements IContainer {

	private static final String	conImageEDITOR_SMALL_PNG	= "/sos/scheduler/editor/editor-small.png";
	@SuppressWarnings("unused")
	private final String		conClassName				= "TabbedContainer";
	@SuppressWarnings("unused")
	private final String		conSVNVersion				= "$Id: TabbedContainer.java 17397 2012-06-21 14:22:46Z ur $";
	private static final Logger	logger						= Logger.getLogger(TabbedContainer.class);
	private static final String	NEW_SCHEDULER_TITLE			= "Unknown";
	private static final String	NEW_DOCUMENTATION_TITLE		= "Unknown";
	private static final String	NEW_DETAIL_TITLE			= "Unknown";
	private CTabFolder			folder						= null;
	private ArrayList			filelist					= new ArrayList();
	class TabData {
		protected String	title	= "";
		protected String	caption	= "";
		protected int		cnt		= 0;

		public TabData(String title, String caption) {
			this.title = title;
			this.caption = caption;
		}
	}

	// public TabbedContainer(MainWindow window, Composite parent) {
	public TabbedContainer(Composite parent) {
		// this.window = window;
		folder = new CTabFolder(parent, SWT.TOP | SWT.CLOSE);
		folder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		initialize();
	}

	private void initialize() {
		folder.setSimple(false);
		folder.setSize(new Point(690, 478));
		folder.setLayout(new FillLayout());
		// on tab selection
		folder.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setWindowTitle();
				MainWindow.setMenuStatus();
				MainWindow.shellActivated_();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		// on tab close
		folder.addCTabFolder2Listener(new CTabFolder2Adapter() {
			public void close(CTabFolderEvent event) {
				// IEditor editor = getCurrentEditor();
				IEditor editor = (IEditor) ((CTabItem) (event.item)).getControl();
				if (editor.hasChanges()) {
					event.doit = editor.close();
				}
				if (event.doit)
					filelist.remove(editor.getFilename());
			}
		});
		folder.addTraverseListener(new TraverseListener() {
			public void keyTraversed(final TraverseEvent e) {
				/*if(e.detail == SWT.TRAVERSE_ESCAPE) {		
					System.out.println(folder.getChildren().length);
					IEditor editor = (IEditor)folder.getSelection().getControl();
					filelist.remove(editor.getFilename());
					editor.close();
					folder.getSelection().dispose();
					folder.removeControlListener(listener)
				}*/
			}
		});
	}

	public SchedulerForm newScheduler() {
		SchedulerForm scheduler = new SchedulerForm(this, folder, SWT.NONE);
		scheduler.openBlank();
		CTabItem tab = newItem(scheduler, NEW_SCHEDULER_TITLE);
		tab.setImage(new Image(tab.getDisplay(), getClass().getResourceAsStream(conImageEDITOR_SMALL_PNG)));
		return scheduler;
	}

	public SchedulerForm newScheduler(int type) {
		SchedulerForm scheduler = new SchedulerForm(this, folder, SWT.NONE, type);
		scheduler.openBlank(type);
		CTabItem tab = newItem(scheduler, NEW_SCHEDULER_TITLE);
		tab.setImage(new Image(tab.getDisplay(), getClass().getResourceAsStream(conImageEDITOR_SMALL_PNG)));
		return scheduler;
	}

	public JobChainConfigurationForm newDetails() {
		JobChainConfigurationForm detailForm = new JobChainConfigurationForm(this, folder, SWT.NONE);
		detailForm.openBlank();
		CTabItem tab = newItem(detailForm, NEW_DETAIL_TITLE);
		tab.setImage(new Image(tab.getDisplay(), getClass().getResourceAsStream(conImageEDITOR_SMALL_PNG)));
		return detailForm;
	}

	public JobChainConfigurationForm openDetails() {
		JobChainConfigurationForm detailForm = new JobChainConfigurationForm(this, folder, SWT.NONE);
		if (detailForm.open(filelist)) {
			CTabItem tab = newItem(detailForm, detailForm.getFilename());
			tab.setImage(new Image(tab.getDisplay(), getClass().getResourceAsStream(conImageEDITOR_SMALL_PNG)));
			return detailForm;
		}
		else
			return null;
	}

	public JobChainConfigurationForm openDetails(String filename) {
		JobChainConfigurationForm detailForm = new JobChainConfigurationForm(this, folder, SWT.NONE);
		if (detailForm.open(filename, filelist)) {
			CTabItem tab = newItem(detailForm, detailForm.getFilename());
			tab.setImage(new Image(tab.getDisplay(), getClass().getResourceAsStream(conImageEDITOR_SMALL_PNG)));
			return detailForm;
		}
		else
			return null;
	}

	public ActionsForm openActions(String filename) {
		ActionsForm actionsForm = new ActionsForm(this, folder, SWT.NONE);
		if (actionsForm.open(filename, filelist)) {
			CTabItem tab = newItem(actionsForm, actionsForm.getFilename());
			tab.setImage(new Image(tab.getDisplay(), getClass().getResourceAsStream(conImageEDITOR_SMALL_PNG)));
			return actionsForm;
		}
		else
			return null;
	}

	public SchedulerForm openScheduler() {
		SchedulerForm scheduler = new SchedulerForm(this, folder, SWT.NONE);
		if (scheduler.open(filelist)) {
			CTabItem tab = newItem(scheduler, scheduler.getFilename());
			tab.setImage(ResourceManager.getImageFromResource(conImageEDITOR_SMALL_PNG));
			return scheduler;
		}
		else
			return null;
	}

	public SchedulerForm openScheduler(String filename) {
		SchedulerForm scheduler = new SchedulerForm(this, folder, SWT.NONE);
		if (scheduler.open(filename, filelist)) {
			CTabItem tab = newItem(scheduler, scheduler.getFilename());
			tab.setImage(ResourceManager.getImageFromResource(conImageEDITOR_SMALL_PNG));
			return scheduler;
		}
		else
			return null;
	}

	public DocumentationForm newDocumentation() {
		DocumentationForm doc = new DocumentationForm(this, folder, SWT.NONE);
		doc.openBlank();
		newItem(doc, NEW_DOCUMENTATION_TITLE);
		return doc;
	}

	public DocumentationForm openDocumentation() {
		try {
			DocumentationForm doc = new DocumentationForm(this, folder, SWT.NONE);
			if (doc.open(filelist)) {
				// CTabItem tab = newItem(doc, doc.getFilename());
				newItem(doc, doc.getFilename());
				return doc;
			}
			else
				return null;
		}
		catch (Exception e) {
			try {
				new ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName(), e);
			}
			catch (Exception ee) {
				// tu nichts
			}
			System.out.println("error in TabbedContainer.openDocumentation()" + e.getMessage());
			return null;
		}
	}

	public DocumentationForm openDocumentation(String filename) {
		try {
			DocumentationForm doc = new DocumentationForm(this, folder, SWT.NONE);
			if (doc.open(filename, filelist)) {
				// CTabItem tab = newItem(doc, doc.getFilename());
				newItem(doc, doc.getFilename());
				// tab.setImage(ResourceManager.getImageFromResource("/sos/scheduler/editor/editor-small.png"));
				return doc;
			}
			else
				return null;
		}
		catch (Exception e) {
			try {
				new ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName(), e);
			}
			catch (Exception ee) {
				// tu nichts
			}
			System.out.println("error in TabbedContainer.openDocumentation()" + e.getMessage());
			return null;
		}
	}

	public String openDocumentationName() {
		try {
			DocumentationForm doc = new DocumentationForm(this, folder, SWT.NONE);
			if (doc.open(filelist)) {
				// CTabItem tab = newItem(doc, doc.getFilename());
				// tab.setImage(ResourceManager.getImageFromResource("/sos/scheduler/editor/editor-small.png"));
				return doc.getFilename();
			}
			else
				return null;
		}
		catch (Exception e) {
			try {
				new ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName(), e);
			}
			catch (Exception ee) {
				// tu nichts
			}
			System.out.println("error in TabbedContainer.openDocumentation()" + e.getMessage());
			return null;
		}
	}

	private String shortCaption(String caption) {
		File f = new File(caption);
		if (caption.length() > 30 && f.getParentFile() != null && f.getParentFile().getParentFile() != null) {
			String s = "..." + f.getParentFile().getParentFile().getName() + "/" + f.getParentFile().getName() + "/" + f.getName();
			if (s.length() > 30) {
				return caption;
			}
			else {
				return s;
			}
		}
		else {
			return caption;
		}
	}

	private String	strTitleText	= "";

	public void setTitleText(final String pstrTitle) {
		strTitleText = pstrTitle;
	}

	private CTabItem newItem(Control control, String filename) {
		CTabItem tab = new CTabItem(folder, SWT.NONE);
		tab.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e) {
				MainWindow.getSShell().setText(strTitleText /* "Job Scheduler Editor" */);
				MainWindow.setSaveStatus();
			}
		});
		tab.setControl(control);
		folder.setSelection(folder.indexOf(tab));
		String actFilename = Utils.getFileFromURL(filename);
		tab.setData(new TabData(actFilename, strTitleText));
		String title = setSuffix(tab, actFilename);
		TabData t = (TabData) tab.getData();
		t.caption = shortCaption(title);
		tab.setToolTipText(filename);
		tab.setText(title);
		if (!filename.equals(NEW_DOCUMENTATION_TITLE) && !filename.equals(NEW_SCHEDULER_TITLE))
			filelist.add(filename);
		return tab;
	}

	public CTabItem getCurrentTab() {
		if (folder.getItemCount() == 0)
			return null;
		else
			return folder.getItem(folder.getSelectionIndex());
	}

	public CTabItem getFolderTab(String filename) {
		if (folder.getItemCount() == 0)
			return null;
		else {
			for (int i = 0; i < folder.getItemCount(); i++) {
				if (filelist.get(i).equals(filename))
					return folder.getItem(i);
			}
			return null;
		}
	}

	public IEditor getCurrentEditor() {
		if (folder.getItemCount() == 0)
			return null;
		else
			return (IEditor) getCurrentTab().getControl();
	}

	public IEditor getEditor(String filename) {
		if (folder.getItemCount() == 0)
			return null;
		else
			return ((SchedulerForm) (IEditor) getFolderTab(filename).getControl());
		// SchedulerForm f = ((SchedulerForm)(IEditor) folder.getItem(0).getControl()).getDom().getFilename()
	}

	public void setStatusInTitle() {
		if (folder.getItemCount() == 0)
			return;
		CTabItem tab = getCurrentTab();
		TabData t = (TabData) tab.getData();
		String title = t.caption;
		if (tab.getData("ftp_profile_name") != null && tab.getData("ftp_profile_name").toString().length() > 0 && tab.getData("ftp_remote_directory") != null
				&& tab.getData("ftp_remote_directory").toString().length() > 0)
			title = tab.getData("ftp_remote_directory").toString();
		if (tab.getData("webdav_profile_name") != null && tab.getData("webdav_profile_name").toString().length() > 0
				&& tab.getData("webdav_remote_directory") != null && tab.getData("webdav_remote_directory").toString().length() > 0)
			title = tab.getData("webdav_remote_directory").toString();
		tab.setText(getCurrentEditor().hasChanges() == false ? title : "*" + title);
		setWindowTitle();
		MainWindow.setMenuStatus();
	}

	public void setStatusInTitle(CTabItem tab) {
		if (folder.getItemCount() == 0)
			return;
		TabData t = (TabData) tab.getData();
		String title = t.caption;
		if (tab.getData("ftp_profile_name") != null && tab.getData("ftp_profile_name").toString().length() > 0 && tab.getData("ftp_remote_directory") != null
				&& tab.getData("ftp_remote_directory").toString().length() > 0)
			title = tab.getData("ftp_remote_directory").toString();
		if (tab.getData("webdav_profile_name") != null && tab.getData("webdav_profile_name").toString().length() > 0
				&& tab.getData("webdav_remote_directory") != null && tab.getData("webdav_remote_directory").toString().length() > 0)
			title = tab.getData("webdav_remote_directory").toString();
		tab.setText(getCurrentEditor().hasChanges() == false ? title : "*" + title);
		setWindowTitle();
		MainWindow.setMenuStatus();
	}

	public void setNewFilename(String oldFilename) {
		if (folder.getItemCount() == 0)
			return;
		String filename = getCurrentEditor().getFilename();
		CTabItem tab = getCurrentTab();
		if (oldFilename != null) {
			filelist.remove(oldFilename);
			filelist.add(filename);
		}
		String title = setSuffix(tab, Utils.getFileFromURL(filename));
		if (tab.getData("ftp_remote_directory") != null && tab.getData("ftp_remote_directory").toString().length() > 0
				&& tab.getData("ftp_profile_name") != null && tab.getData("ftp_profile_name").toString().length() > 0)
			title = tab.getData("ftp_remote_directory").toString();
		tab.setText(title);
		tab.setToolTipText(filename);
		tab.setData(new TabData(Utils.getFileFromURL(filename), shortCaption(title)));
		setWindowTitle();
	}

	public void setNewFilename(String oldFilename, String newFilename) {
		if (folder.getItemCount() == 0)
			return;
		CTabItem tab = getCurrentTab();
		if (oldFilename != null) {
			filelist.remove(oldFilename);
			filelist.add(newFilename);
		}
		String title = setSuffix(tab, Utils.getFileFromURL(newFilename));
		tab.setText(title);
		tab.setToolTipText(newFilename);
		tab.setData(new TabData(Utils.getFileFromURL(newFilename), shortCaption(title)));
		setWindowTitle();
	}

	private void setWindowTitle() {
		Shell shell = folder.getShell();
		String ftp = getCurrentTab().getData("ftp_title") != null ? getCurrentTab().getData("ftp_title").toString() + "\\" : "";
		String webdav = getCurrentTab().getData("webdav_title") != null ? getCurrentTab().getData("webdav_title").toString() + "\\" : "";
		/*if(ftp != null && ftp.length() > 0  ) {
			String f = new File(getCurrentTab().getText()).getName();
		}*/
		// / shell.setText((String) shell.getData() + webdav + ftp + " " + getCurrentTab().getText());
		shell.setText(strTitleText + webdav + ftp + " " + getCurrentTab().getText());
		// shell.setText((String) shell.getData() + " [" + getCurrentTab().getText() + "]");
	}

	private String setSuffix(CTabItem tab, String title) {
		int sameTitles = getSameTitles(title);
		TabData t = (TabData) tab.getData();
		if (t != null) {
			t.cnt = sameTitles;
			if (sameTitles > 0)
				title = title + "(" + (sameTitles + 1) + ")";
		}
		return title;
	}

	private boolean isFreeIndex(int index, String title) {
		boolean found = false;
		CTabItem[] items = folder.getItems();
		int i = 0;
		while (i < items.length && !found) {
			TabData t = (TabData) items[i].getData();
			if (items[i].getData() != null && t.title.equals(title) && t.cnt == index && !items[i].equals(getCurrentTab())) {
				found = true;
			}
			i++;
		}
		return !found;
	}

	private int getSameTitles(String title) {
		int cnt = -1;
		int i = 0;
		// boolean found = false;
		while (cnt == -1) {
			if (isFreeIndex(i, title)) {
				// found = true;
				cnt = i;
			}
			i++;
		}
		return cnt;
	}

	public boolean closeAll() {
		boolean doit = true;
		for (int i = 0; i < folder.getItemCount(); i++) {
			CTabItem tab = folder.getItem(i);
			folder.setSelection(i);
			if (((IEditor) tab.getControl()).close()) {
				tab.dispose();
				i--;
			}
			else {
				doit = false;
			}
		}
		return doit;
	}

	public void closeCurrentTab() {
		if (folder.getSelectionIndex() > -1) {
			CTabItem tab = folder.getSelection();
			tab.dispose();
		}
		else {
			getCurrentTab().dispose();
		}
	}

	public void updateLanguages() {
		for (int i = 0; i < folder.getItemCount(); i++) {
			CTabItem tab = folder.getItem(i);
			((IEditor) tab.getControl()).updateLanguage();
		}
	}

	/* public SchedulerForm openDirectory() {
	    SchedulerForm scheduler = new SchedulerForm(this, folder, SWT.NONE, SchedulerDom.DIRECTORY);

	    if (scheduler.openDirectory(null, filelist)) {
	        CTabItem tab = newItem(scheduler, scheduler.getFilename());
	        tab.setImage(ResourceManager.getImageFromResource("/sos/scheduler/editor/editor-small.png"));
	        return scheduler;
	    } else
	        return null;
	}*/
	public SchedulerForm openDirectory(String filename) {
		SchedulerForm scheduler = new SchedulerForm(this, folder, SWT.NONE, SchedulerDom.DIRECTORY);
		if (scheduler.openDirectory(filename, filelist)) {
			CTabItem tab = newItem(scheduler, scheduler.getFilename());
			Options.setLastFolderName(scheduler.getFilename());
			tab.setImage(ResourceManager.getImageFromResource(conImageEDITOR_SMALL_PNG));
			return scheduler;
		}
		else
			return null;
	}

	public SchedulerForm openLiveElement(String filename, int type) {
		SchedulerForm scheduler = new SchedulerForm(this, folder, SWT.NONE, type);
		if (scheduler.open(filename, filelist, type)) {
			CTabItem tab = newItem(scheduler, scheduler.getFilename());
			tab.setImage(ResourceManager.getImageFromResource(conImageEDITOR_SMALL_PNG));
			return scheduler;
		}
		else
			return null;
	}

	public Composite openQuick(String xmlFilename) {

		final String conMethodName = conClassName + "::openQuick";

		logger.trace(String.format("Enter procedure %1$s ", conMethodName));

		try {
			if (xmlFilename != null && xmlFilename.length() > 0) {
				SAXBuilder builder = new SAXBuilder();
				org.jdom.Document doc = builder.build(new File(xmlFilename));
				org.jdom.Element root = doc.getRootElement();
				String strRootName = root.getName();
				
				if (strRootName.equalsIgnoreCase("description")) {
					return openDocumentation(xmlFilename);
				}
				if (strRootName.equalsIgnoreCase("spooler")) {
					return openScheduler(xmlFilename);
				}
				if (strRootName.equalsIgnoreCase("actions")) {
					return openActions(xmlFilename);
				}
				if (strRootName.equalsIgnoreCase("job")) {
					return openLiveElement(xmlFilename, SchedulerDom.LIVE_JOB);
				}
				if (strRootName.equalsIgnoreCase("job_chain")) {
					return openLiveElement(xmlFilename, SchedulerDom.LIVE_JOB_CHAIN);
				}
				if (strRootName.equalsIgnoreCase("process_class")) {
					return openLiveElement(xmlFilename, SchedulerDom.LIFE_PROCESS_CLASS);
				}
				if (strRootName.equalsIgnoreCase("lock")) {
					return openLiveElement(xmlFilename, SchedulerDom.LIFE_LOCK);
				}
				if (strRootName.equalsIgnoreCase("order") || strRootName.equalsIgnoreCase("add_order")) {
					return openLiveElement(xmlFilename, SchedulerDom.LIFE_ORDER);
				}
				if (strRootName.equalsIgnoreCase("schedule")) {
					return openLiveElement(xmlFilename, SchedulerDom.LIFE_SCHEDULE);
				}
				
				MainWindow.message("Unknown root Element: " + root.getName() + " from filename " + xmlFilename, SWT.NONE);
			}
		}
		catch (Exception e) {
			try {
				new ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName() + " ; could not open file " + xmlFilename, e);
			}
			catch (Exception ee) {
				// tu nichts
			}
			MainWindow.message("could not open file cause" + e.getMessage(), SWT.NONE);
		}
		return null;
	}

	public org.eclipse.swt.widgets.Composite openQuick() {
		final String conMethodName = conClassName + "::openQuick";

		logger.trace(String.format("Enter procedure %1$s ", conMethodName));

		
		String xmlFilename = "";
		try {
			FileDialog fdialog = new FileDialog(MainWindow.getSShell(), SWT.OPEN);
			fdialog.setFilterPath(Options.getLastDirectory());
			fdialog.setText("Open");
			fdialog.setFilterExtensions(new String[] { "*.xml" });
			xmlFilename = fdialog.open();
			return openQuick(xmlFilename);
		}
		catch (Exception e) {
			try {
				new ErrorLog("error in " + sos.util.SOSClassUtil.getMethodName() + " ; could not open File ", e);
			}
			catch (Exception ee) {
				// tu nichts
			}
			MainWindow.message("could not open file cause" + e.getMessage(), SWT.NONE);
		}
		return null;
	}

	public ActionsForm newActions() {
		ActionsForm actions = new ActionsForm(this, folder, SWT.NONE);
		actions.openBlank();
		newItem(actions, NEW_DOCUMENTATION_TITLE);
		return actions;
	}

	/**
	 * @return the filelist
	 */
	public ArrayList getFilelist() {
		return filelist;
	}
}
