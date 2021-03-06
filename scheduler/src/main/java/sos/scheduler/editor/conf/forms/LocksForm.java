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
/**
 * 
 */
package sos.scheduler.editor.conf.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.jdom.Element;
import org.jdom.JDOMException;

import sos.scheduler.editor.app.IUnsaved;
import sos.scheduler.editor.app.IUpdateLanguage;
import sos.scheduler.editor.app.SOSJOEMessageCodes;
import sos.scheduler.editor.app.Utils;
import sos.scheduler.editor.conf.SchedulerDom;
import sos.scheduler.editor.conf.listeners.LocksListener;

/**
 * @author
 */
public class LocksForm extends SOSJOEMessageCodes implements IUnsaved, IUpdateLanguage {



	private LocksListener          listener                 = null;

	private Group                  locksGroup               = null;

	private static Table           tableLocks               = null;

	private Label                  label1                   = null;

	private Button                 bRemove                  = null;

	private Button                 bNew                     = null;

	private Button                 bApply                   = null;
    
	private Text                   tLock                    = null;

	private Label                  label5                   = null;

	private Spinner                sMaxNonExclusive         = null;

	private Label                  label                    = null;

	private Label                  label2                   = null;

	private SchedulerDom           dom                      = null;

	//Begrenzung der nicht-exklusiven Belegungen: Defaults ist unbegrenzt
	private Button                 butUnlimitedNonExclusive = null;		


	/**
	 * @param parent
	 * @param style
	 * @throws JDOMException
	 */
	public LocksForm(Composite parent, int style, SchedulerDom dom_, Element config) throws JDOMException {
		super(parent, style);
		dom = dom_;
		listener = new LocksListener(dom, config);

		initialize();
		setToolTipText();

		listener.fillTable(tableLocks);
		if(dom.isLifeElement()) {
			tableLocks.setVisible(false);
			label.setVisible(false);
			label2.setVisible(false);
			bNew.setVisible(false);
			bRemove.setVisible(false);

			listener.selectLock(0);
			setInput(true);
			tLock.setBackground(null);
		}

	}


	public void apply() {
		if (isUnsaved())
			applyLock();
	}


	public boolean isUnsaved() {
		return bApply.isEnabled();
	}


	private void initialize() {
		this.setLayout(new FillLayout());
		createGroup();
		setSize(new org.eclipse.swt.graphics.Point(694, 294));

	}


	/**
	 * This method initializes group
	 */
	 private void createGroup() {
		 GridData gridData8 = new org.eclipse.swt.layout.GridData();
		 gridData8.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		 gridData8.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		 GridData gridData7 = new org.eclipse.swt.layout.GridData(GridData.FILL, GridData.CENTER, false, false, 5, 1);
		 gridData7.heightHint = 10;
		 GridData gridData5 = new org.eclipse.swt.layout.GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1);
		 GridData gridData3 = new org.eclipse.swt.layout.GridData();
		 gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		 gridData3.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		 GridData gridData2 = new org.eclipse.swt.layout.GridData();
		 gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		 gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		 GridData gridData1 = new org.eclipse.swt.layout.GridData();
		 gridData1.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		 gridData1.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		 GridLayout gridLayout = new GridLayout();
		 gridLayout.numColumns = 5;
		 
		 locksGroup = JOE_G_LocksForm_Locks.Control(new Group(this, SWT.NONE));
		 locksGroup.setLayout(gridLayout);
		 
		 label1 = JOE_L_LocksForm_Lock.Control(new Label(locksGroup, SWT.NONE));
		 
		 tLock = JOE_T_LocksForm_Lock.Control(new Text(locksGroup, SWT.BORDER));
		 
		 bApply = JOE_B_LocksForm_Apply.Control(new Button(locksGroup, SWT.NONE));
		 
		 label5 = JOE_L_LocksForm_MaxNonExclusive.Control(new Label(locksGroup, SWT.NONE));
		 final GridData gridData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1);
		 gridData.horizontalIndent = 5;
		 label5.setLayoutData(gridData);
		 
		 GridData gridData4 = new GridData(20, SWT.DEFAULT);
		 butUnlimitedNonExclusive = JOE_B_LocksForm_UnlimitedNonExclusive.Control(new Button(locksGroup, SWT.CHECK));
		 butUnlimitedNonExclusive.addSelectionListener(new SelectionAdapter() {
		 	public void widgetSelected(final SelectionEvent e) {
		 		sMaxNonExclusive.setEnabled(!butUnlimitedNonExclusive.getSelection());
		 		bApply.setEnabled(true);
		 	}
		 });
		 butUnlimitedNonExclusive.setSelection(true);
		 butUnlimitedNonExclusive.setEnabled(false);
		 
		 sMaxNonExclusive = JOE_Sp_LocksForm_MaxNonExclusive.Control(new Spinner(locksGroup, SWT.NONE));
		 sMaxNonExclusive.setMaximum(99999999);
		 sMaxNonExclusive.setLayoutData(gridData4);
		 sMaxNonExclusive.setEnabled(false);
		 sMaxNonExclusive.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {
			 public void keyPressed(org.eclipse.swt.events.KeyEvent e) {
				 if (e.keyCode == SWT.CR)
					 applyLock();
			 }
		 });
		 sMaxNonExclusive.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			 public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				 bApply.setEnabled(true);
			 }
		 });
		 
		 new Label(locksGroup, SWT.NONE);
		 
		 label = new Label(locksGroup, SWT.SEPARATOR | SWT.HORIZONTAL);
//		 label.setText("Label");
		 label.setLayoutData(gridData7);
		 
		 createTable();
		 
		 bNew = JOE_B_LocksForm_NewLock.Control(new Button(locksGroup, SWT.NONE));
		 bNew.setLayoutData(gridData1);
		 getShell().setDefaultButton(bNew);

		 label2 = new Label(locksGroup, SWT.SEPARATOR | SWT.HORIZONTAL);
//		 label2.setText("Label");
		 label2.setLayoutData(gridData8);
		 
		 bNew.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			 public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				 apply();
				 listener.newLock();
				 /*
				 tLock.setEnabled(true);
				 sMaxNonExclusive.setEnabled(false);
				 butUnlimitedNonExclusive.setEnabled(true);
				 butUnlimitedNonExclusive.setSelection(true);
				 tableLocks.deselectAll();
				 bApply.setEnabled(false);
				 bRemove.setEnabled(false);
				 tLock.setBackground(null);
				 */
				 
				 setInput(true);
				 bApply.setEnabled(listener.isValidLock(tLock.getText()));
			 }
		 });
		 
		 bRemove = JOE_B_LocksForm_RemoveLock.Control(new Button(locksGroup, SWT.NONE));
		 bRemove.setEnabled(false);
		 bRemove.setLayoutData(gridData2);
		 bRemove.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			 public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				 if (tableLocks.getSelectionCount() > 0) {
					 if(Utils.checkElement(tableLocks.getSelection()[0].getText(0), dom, sos.scheduler.editor.app.Editor.LOCKS, null)) {
						 int index = tableLocks.getSelectionIndex();
						 listener.removeLock(index);
						 tableLocks.remove(index);
						 if (index >= tableLocks.getItemCount())
							 index--;
						 if (tableLocks.getItemCount() > 0) {
							 tableLocks.select(index);
							 listener.selectLock(index);
							 setInput(true);
						 } else
							 setInput(false);
					 }
				 }
				 bRemove.setEnabled(tableLocks.getSelectionCount() > 0);
				 tLock.setBackground(null);
			 }
		 });
		 
		 tLock.setLayoutData(gridData5);
		 tLock.setEnabled(false);
		 tLock.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {
			 public void keyPressed(org.eclipse.swt.events.KeyEvent e) {
				 
				 if (e.keyCode == SWT.CR && tLock.getText().length() > 0) {
					 applyLock();
					 setInput(true);
				 }
			 }
		 });
		 tLock.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			 public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				 
				 boolean valid = listener.isValidLock(tLock.getText()) || dom.isLifeElement();;
				 if (valid)
					 tLock.setBackground(null);
				 else
					 tLock.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
				 bApply.setEnabled(valid);
			 }
		 });
		 
		 bApply.setLayoutData(gridData3);
		 bApply.setEnabled(false);
		 bApply.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			 public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				 applyLock();
			 }
		 });
	 }


	 /**
	  * This method initializes table
	  */
	 private void createTable() {
		 GridData gridData = new org.eclipse.swt.layout.GridData(GridData.FILL, GridData.FILL, true, true, 4, 3);
		 tableLocks = JOE_Tbl_LocksForm_Table.Control(new Table(locksGroup, SWT.FULL_SELECTION | SWT.BORDER));
		 tableLocks.setHeaderVisible(true);
		 tableLocks.setLayoutData(gridData);
		 tableLocks.setLinesVisible(true);
		 tableLocks.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			 public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {

				 Element currLock = listener.getLock(tableLocks.getSelectionIndex());
				 listener.selectLock(tableLocks.getSelectionIndex());
				 if(currLock != null && !Utils.isElementEnabled("lock", dom, currLock)) {
					 setInput(false);
					 bRemove.setEnabled(false);
					 bApply.setEnabled(false);
				 } else {

					 boolean selection = tableLocks.getSelectionCount() > 0;
					 bRemove.setEnabled(selection);
					 if (selection) {
						 listener.selectLock(tableLocks.getSelectionIndex());
						 setInput(true);
						 tLock.setBackground(null);
					 }
				 }
			 }
		 });
		 
		 TableColumn lockTableColumn = JOE_TCl_LocksForm_Lock.Control(new TableColumn(tableLocks, SWT.NONE));
		 lockTableColumn.setWidth(200);
		 
		 TableColumn tableColumn1 = JOE_TCl_LocksForm_MaxNonExclusive.Control(new TableColumn(tableLocks, SWT.NONE));
		 tableColumn1.setWidth(150);
	 }


	 private void applyLock() {
		 
		 boolean _continue = true;
		 if(listener.getLock().length() > 0 &&  !Utils.checkElement(listener.getLock(), dom, sos.scheduler.editor.app.Editor.LOCKS, null))
			 _continue = false;

		 if(tableLocks.getSelectionCount() > 0)
			 listener.selectLock(tableLocks.getSelectionIndex());
		 
		 if(_continue)		 
			 listener.applyLock(tLock.getText(), sMaxNonExclusive.getSelection(), butUnlimitedNonExclusive.getSelection());
		 
		 
		 listener.fillTable(tableLocks);        
		 setInput(false);
		 getShell().setDefaultButton(bNew);
		 tLock.setBackground(null);
		 
		 if(dom.isLifeElement())
			 setInput(true);
		 
		 //if(dom.isDirectory() || dom.isLifeElement())dom.setChangedForDirectory("lock", listener.getLock(), SchedulerDom.MODIFY);

	 }


	 private void setInput(boolean enabled) {
		 tLock.setEnabled(enabled);
		 sMaxNonExclusive.setEnabled(enabled);
		 butUnlimitedNonExclusive.setEnabled(enabled);
		 if (enabled) {			 
			 tLock.setText(listener.getLock());
			 butUnlimitedNonExclusive.setSelection(listener.hasUnlimitedNonExclusiveLock());
			 if(!butUnlimitedNonExclusive.getSelection())
				 sMaxNonExclusive.setSelection(listener.getMax_non_exclusive());				 
			 tLock.setFocus();
			 
		 } else {
			 tLock.setText("");			 
			 butUnlimitedNonExclusive.setSelection(true);			 	
			 sMaxNonExclusive.setSelection(0);
		 }
		 sMaxNonExclusive.setEnabled(!butUnlimitedNonExclusive.getSelection());
		 bApply.setEnabled(false);
		 bRemove.setEnabled(tableLocks.getSelectionCount() > 0);
		 		 
	 }


	 public void setToolTipText() {
//
	 }
	 
	 public static Table getTable() {
			return tableLocks;
		}
} // @jve:decl-index=0:visual-constraint="10,10"
