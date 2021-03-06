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
package sos.scheduler.editor.doc.forms;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.jdom.Element;

import sos.scheduler.editor.app.IUnsaved;
import sos.scheduler.editor.app.IUpdateLanguage;
import sos.scheduler.editor.app.SOSJOEMessageCodes;
import sos.scheduler.editor.app.Utils;
import sos.scheduler.editor.doc.DocumentationDom;
import sos.scheduler.editor.doc.listeners.DocumentationListener;
import sos.scheduler.editor.doc.listeners.ParamsListener;

public class ParamsForm extends SOSJOEMessageCodes implements IUpdateLanguage, IUnsaved {
	private final static String	conSVNVersion		= "$Id: ParamsForm.java 18487 2012-11-27 21:00:56Z kb $";

	private ParamsListener		listener			= null;													// @jve:decl-index=0:
	private DocumentationDom	dom					= null;													// @jve:decl-index=0:
	private Group				group				= null;
	@SuppressWarnings("unused")
	private Label				label				= null;
	private Text				tParamsID			= null;
	@SuppressWarnings("unused")
	private Label				label1				= null;
	private Combo				cParamsReference	= null;
	private Button				bParamsNotes		= null;
	private Group				group1				= null;
	@SuppressWarnings("unused")
	private Label				label2				= null;
	@SuppressWarnings("unused")
	private Label				label3				= null;
	@SuppressWarnings("unused")
	private Label				label4				= null;
	private Text				tName				= null;
	private Text				tDefault			= null;
	private Button				cRequired			= null;
	@SuppressWarnings("unused")
	private Label				label5				= null;
	private Text				tID					= null;
	@SuppressWarnings("unused")
	private Label				label6				= null;
	private Combo				cReference			= null;
	private Button				bApply				= null;
	private Label				label7				= null;
	private Table				tParams				= null;
	private Button				bNew				= null;
	private Label				label8				= null;
	private Button				bRemove				= null;
	private Button				bNotes				= null;

	public ParamsForm(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	public ParamsForm(Composite parent, int style, DocumentationDom dom1, Element parentElement) {
		super(parent, style);
		initialize();

		setParams(dom1, parentElement);
	}

	public void setParams(DocumentationDom dom1, Element parentElement) {
		this.dom = dom1;

		listener = new ParamsListener(dom1, parentElement);

		listener.fillParams(tParams);
		bRemove.setEnabled(false);
		tParamsID.setText(listener.getID());
		DocumentationListener.setCheckbox(cParamsReference, listener.getReferences(listener.getID()), listener.getReference());
	}

	private void initialize() {
		createGroup();
		setSize(new Point(743, 429));
		setLayout(new FillLayout());

		setParamStatus(false);
		tParamsID.setFocus();
		setToolTipText();
	}

	public void setParamNote(boolean enabled) {
		bParamsNotes.setVisible(enabled);
	}

	/**
	 * This method initializes group
	 */
	private void createGroup() {
		GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);

		GridLayout gridLayout = new GridLayout(5, false);

		group = JOE_G_ParamsForm_Parameter.Control(new Group(this, SWT.NONE));
		group.setLayout(gridLayout); // Generated

		label = JOE_L_ParamsForm_ID.Control(new Label(group, SWT.NONE));

		tParamsID = JOE_T_ParamsForm_ID.Control(new Text(group, SWT.BORDER));
		tParamsID.setLayoutData(gridData); // Generated
		tParamsID.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				listener.setID(tParamsID.getText());
			}
		});

		label1 = JOE_L_ParamsForm_Reference.Control(new Label(group, SWT.NONE));

		createCParamsReference();

		bParamsNotes = JOE_B_ParamsForm_ParamsNote.Control(new Button(group, SWT.NONE));
		bParamsNotes.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				// String tip = Messages.getTooltip("doc.note.text.params");
				String tip = "";
				DocumentationForm.openNoteDialog(dom, listener.getParamsElement(), "note", tip, true, JOE_B_ParamsForm_ParamsNote.label());
			}
		});

		createGroup1();
	}

	/**
	 * This method initializes cParamsReference
	 */
	private void createCParamsReference() {
		GridData gridData1 = new GridData(GridData.FILL, GridData.CENTER, true, false);

		cParamsReference = JOE_Cbo_ParamsForm_Reference.Control(new Combo(group, SWT.NONE));
		cParamsReference.setLayoutData(gridData1); // Generated
		cParamsReference.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				listener.setReference(cParamsReference.getText());
			}
		});
	}

	/**
	 * This method initializes group1
	 */
	private void createGroup1() {
		GridData gridData14 = new GridData(GridData.FILL, GridData.CENTER, false, false, 2, 1);

		GridData gridData13 = new GridData();

		GridData gridData12 = new GridData(GridData.FILL, GridData.FILL, true, true, 4, 3);

		GridData gridData11 = new GridData(GridData.FILL, GridData.BEGINNING, false, false);

		GridData gridData10 = new GridData(GridData.FILL, GridData.CENTER, false, false);

		GridData gridData9 = new GridData(GridData.FILL, GridData.BEGINNING, false, false);

		GridData gridData8 = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 4);

		GridData gridData7 = new GridData(GridData.FILL, GridData.CENTER, false, false, 5, 1);

		GridData gridData5 = new GridData(GridData.FILL, GridData.CENTER, true, false);

		GridData gridData4 = new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1);

		GridData gridData3 = new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1);

		GridData gridData2 = new GridData(GridData.FILL, GridData.FILL, true, true, 5, 1);

		GridLayout gridLayout1 = new GridLayout(5, false);

		group1 = JOE_G_ParamsForm_ParamValues.Control(new Group(group, SWT.NONE));
		group1.setLayout(gridLayout1); // Generated
		group1.setLayoutData(gridData2); // Generated

		label2 = JOE_L_Name.Control(new Label(group1, SWT.NONE));

		tName = JOE_T_ParamsForm_Name.Control(new Text(group1, SWT.BORDER));
		tName.setLayoutData(gridData3); // Generated
		tName.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				setApplyStatus();
			}
		});

		bApply = JOE_B_ParamsForm_Apply.Control(new Button(group1, SWT.NONE));
		bApply.setLayoutData(gridData8); // Generated
		bApply.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				applyParam();
				newParam();
			}
		});

		label3 = JOE_L_ParamsForm_DefaultValue.Control(new Label(group1, SWT.NONE));

		tDefault = JOE_T_ParamsForm_DefaultValue.Control(new Text(group1, SWT.BORDER));
		tDefault.setLayoutData(gridData4); // Generated
		tDefault.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				setApplyStatus();
			}
		});

		label6 = JOE_L_ParamsForm_Reference.Control(new Label(group1, SWT.NONE));

		createCReference();

		label4 = JOE_L_ParamsForm_Required.Control(new Label(group1, SWT.NONE));

		cRequired = JOE_B_ParamsForm_Required.Control(new Button(group1, SWT.CHECK));
		cRequired.setLayoutData(gridData13); // Generated
		cRequired.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				setApplyStatus();
			}
		});

		label5 = JOE_L_ParamsForm_ID.Control(new Label(group1, SWT.NONE));

		tID = JOE_T_ParamsForm_ID2.Control(new Text(group1, SWT.BORDER));
		tID.setLayoutData(gridData5); // Generated
		tID.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				setApplyStatus();
			}
		});

		bNotes = JOE_B_ParamsForm_Notes.Control(new Button(group1, SWT.NONE));
		bNotes.setEnabled(false);
		bNotes.setLayoutData(gridData14); // Generated
		bNotes.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				// String tip = Messages.getTooltip("doc.note.text.param");
				String tip = "";
				if ((listener.getParamElement() == null) || ((listener.getParamElement() != null) && listener.getParamElement().getParentElement() == null)) {
					applyParam();
				}
				DocumentationForm.openNoteDialog(dom, listener.getParamElement(), "note", tip, true, !listener.isNewParam(),
						JOE_B_ParamsForm_ParamsNote.label());
			}
		});

		label7 = new Label(group1, SWT.SEPARATOR | SWT.HORIZONTAL);
		label7.setText("Label"); // Generated
		label7.setLayoutData(gridData7); // Generated

		tParams = JOE_Tbl_ParamsForm_Params.Control(new Table(group1, SWT.BORDER));
		tParams.setHeaderVisible(true); // Generated
		tParams.setLayoutData(gridData12); // Generated
		tParams.setLinesVisible(true); // Generated
		tParams.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (tParams.getSelectionCount() > 0) {
					if (listener.selectParam(tParams.getSelectionIndex())) {
						bRemove.setEnabled(true);
						setParamStatus(true);
					}
				}
				bApply.setEnabled(false);
			}
		});

		TableColumn tableColumn = JOE_TCl_ParamsForm_Name.Control(new TableColumn(tParams, SWT.NONE));
		tableColumn.setWidth(200); // Generated

		TableColumn tableColumn21 = JOE_TCl_ParamsForm_Default.Control(new TableColumn(tParams, SWT.NONE));
		tableColumn21.setWidth(150); // Generated

		TableColumn tableColumn3 = JOE_TCl_ParamsForm_Required.Control(new TableColumn(tParams, SWT.NONE));
		tableColumn3.setWidth(70); // Generated

		TableColumn tableColumn2 = JOE_TCl_ParamsForm_Reference.Control(new TableColumn(tParams, SWT.NONE));
		tableColumn2.setWidth(120); // Generated

		TableColumn tableColumn1 = JOE_TCl_ParamsForm_ID.Control(new TableColumn(tParams, SWT.NONE));
		tableColumn1.setWidth(120); // Generated

		bNew = JOE_B_ParamsForm_NewParam.Control(new Button(group1, SWT.NONE));
		bNew.setLayoutData(gridData11); // Generated
		bNew.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				listener.setNewParam();
				setParamStatus(true);
				tParams.deselectAll();
			}
		});

		label8 = new Label(group1, SWT.SEPARATOR | SWT.HORIZONTAL);
		label8.setText("Label"); // Generated
		label8.setLayoutData(gridData10); // Generated

		bRemove = JOE_B_ParamsForm_RemoveParam.Control(new Button(group1, SWT.NONE));
		bRemove.setLayoutData(gridData9); // Generated
		bRemove.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if (tParams.getSelectionCount() > 0) {
					listener.removeParam(tParams.getSelectionIndex());
					setParamStatus(false);
					bApply.setEnabled(false);
					bRemove.setEnabled(false);
					tParams.deselectAll();
					listener.fillParams(tParams);

					DocumentationListener.setCheckbox(cParamsReference, listener.getReferences(listener.getID()), listener.getReference());
				}
			}
		});
	}

	/**
	 * This method initializes cReference
	 */
	private void createCReference() {
		GridData gridData6 = new GridData(GridData.FILL, GridData.CENTER, true, false);

		cReference = JOE_Cbo_ParamsForm_Reference2.Control(new Combo(group1, SWT.NONE));
		cReference.setLayoutData(gridData6); // Generated
		cReference.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setApplyStatus();
			}
		});
	}

	private void setParamStatus(boolean enabled) {
		tName.setEnabled(enabled);
		tDefault.setEnabled(enabled);
		tID.setEnabled(enabled);
		cRequired.setEnabled(enabled);
		cReference.setEnabled(enabled);
		bNotes.setEnabled(enabled);

		if (enabled) {
			tName.setText(listener.getParamName());
			tDefault.setText(listener.getParamDefaultValue());
			tID.setText(listener.getParamID());
			cRequired.setSelection(listener.getParamRequired());
			DocumentationListener.setCheckbox(cReference, listener.getReferences(listener.getParamID()), listener.getParamReference());
			tName.setFocus();
		}
		bApply.setEnabled(false);
	}

	private void setApplyStatus() {
		bApply.setEnabled(tName.getText().length() > 0);
		bNotes.setEnabled(tName.getText().length() > 0);
		Utils.setBackground(tName, true);
		getShell().setDefaultButton(bApply);
	}

	private void newParam() {
		listener.setNewParam();
		setParamStatus(true);
		tParams.deselectAll();
	}

	private void applyParam() {
		listener.applyParam(tName.getText(), tDefault.getText(), tID.getText(), cReference.getText(), cRequired.getSelection());

		DocumentationListener.setCheckbox(cParamsReference, listener.getReferences(listener.getID()), listener.getReference());
		bApply.setEnabled(false);
		listener.fillParams(tParams);
		bRemove.setEnabled(tParams.getSelectionCount() > 0);
	}

	public void setToolTipText() {
		//
	}

	public void apply() {
		if (isUnsaved())
			applyParam();
	}

	public boolean isUnsaved() {
		listener.checkParams();
		return bApply.isEnabled();
	}

	public void checkParams() {
		listener.checkParams();
	}

} // @jve:decl-index=0:visual-constraint="10,10"
