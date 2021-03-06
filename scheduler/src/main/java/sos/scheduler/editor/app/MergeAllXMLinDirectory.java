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
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import sos.scheduler.editor.conf.SchedulerDom;
import sos.util.SOSFile;

public class MergeAllXMLinDirectory {

	public final static String MASK_JOB = "^.*\\.job\\.xml$";

	public final static String MASK_LOCK = "^.*\\.lock\\.xml$";

	public final static String MASK_PROCESS_CLASS = "^.*\\.process_class\\.xml$";

	public final static String MASK_JOB_CHAIN = "^.*\\.job_chain\\.xml$";

	public final static String MASK_ORDER = "^.*\\.order\\.xml$";

	public final static String MASK_SCHEDULE = "^.*\\.schedule\\.xml$";

	private String path = "";

	private Element config = null;

	private static String encoding = "ISO-8859-1";

	private HashMap listOfChanges = null;

	/* Liste der SChreibgesch�tzen Dateien */
	private ArrayList listOfReadOnly = null;

	/*
	 * Wenn dateiname ungleich der Element Attribute Name ist, dann wird der
	 * Dateiname als name-Attribut gesetzt und * f�r save
	 */
	private ArrayList listOfChangeElementNames = null;

	public MergeAllXMLinDirectory(String path_) {
		path = path_;
	}

	public MergeAllXMLinDirectory() {

	}

	public String parseDocuments() {

		Element root = null;
		Document parentDoc = null;
		Element jobs = null;
		Element locks = null;
		Element processClass = null;
		Element jobChains = null;
		Element orders = null;

		listOfReadOnly = new ArrayList();
		listOfChangeElementNames = new ArrayList();

		try {
			SAXBuilder builder = new SAXBuilder();

			String xml = createConfigurationFile();

			parentDoc = builder.build(new StringReader(xml));

			root = parentDoc.getRootElement();
			if (root != null) {
				config = root.getChild("config");
			}

			/* Alle <name>.process_classes.xml parsieren */
			addContains(processClass, "process_classes", MASK_PROCESS_CLASS);

			/* Alle <name>.schedule.xml parsieren */
			addContains(locks, "schedules", MASK_SCHEDULE);

			/* Alle <name>.lock.xml parsieren */
			addContains(locks, "locks", MASK_LOCK);

			/* Alle <name>.job.xml parsieren */
			addContains(jobs, "jobs", MASK_JOB);

			/* Alle <name>.job_chain.xml parsieren */
			addContains(jobChains, "job_chains", MASK_JOB_CHAIN);

			/* Alle <name>.order.xml parsieren */
			addContainsForOrder(orders, "commands", MASK_ORDER);

			// Debug Document als String ausgeben
			// printXML(parentDoc);

			// Document speichern
			// System.out.println("test: xmlFilename: " + xmlFilename);
			// System.out.println("parentDoc: \n" +
			// Utils.getElementAsString(parentDoc.getRootElement()));
			return Utils.getElementAsString(parentDoc.getRootElement());

			// IOUtils.saveXML(parentDoc, xmlFilename);

			// System.out.println("OK: ");
		} catch (Exception e) {
			System.err.println("..error : " + e.getMessage());
			return null;
		}
	}

	public void printXML(Document doc) {

		try {
			/*
			 * //DebugSystem.out.println(
			 * "********************************************************************"
			 * ); JDOMSource in = new JDOMSource(doc); Format format =
			 * Format.getPrettyFormat(); //format.setEncoding(encoding);
			 * XMLOutputter outp = new XMLOutputter(format); String output =
			 * outp.outputString(doc);
			 * 
			 * System.out.println(output);System.out.println(
			 * "********************************************************************"
			 * );
			 */
		} catch (Exception e) {
			System.out
					.println("..error in MergeAllXMLinDirectory.printXML. Could not save file "
							+ e.getMessage());
		}

	}

	protected File getNormalizedFile(String url) throws Exception {
		try {
			if (url.startsWith("file")) {
				return new java.io.File(java.net.URI.create(url));
			} else {
				return new java.io.File(url);
			}
		} catch (Exception e) {
			throw new Exception("-> ..error : " + e);
		}
	}

	private void addContains(Element parent, String name, String mask) {

		// SAXBuilder builder = new SAXBuilder(true);
		SAXBuilder builder = null;
		Document currDocument = null;
		String jobXMLFilename = "";
		try {
			builder = getBuilder(false);
			Vector filelist = SOSFile.getFilelist(getNormalizedFile(path)
					.getAbsolutePath(), mask,
					java.util.regex.Pattern.CASE_INSENSITIVE);
			Iterator orderIterator = filelist.iterator();
			while (orderIterator.hasNext()) {
				try {
					jobXMLFilename = orderIterator.next().toString();
					File jobXMLFile = new File(jobXMLFilename);
					currDocument = builder.build(jobXMLFile);

					Element xmlRoot = currDocument.getRootElement();
					if (xmlRoot != null) {
						if (parent == null) {
							// config hat keinen Kindknoten {name}, also
							// erzeugen
							parent = new Element(name);
							config.addContent(parent);
						}
						String jobXMLNameWithoutExtension = jobXMLFile
								.getName().substring(
										0,
										jobXMLFile.getName().indexOf(
												"." + xmlRoot.getName()
														+ ".xml"));
						if (Utils.getAttributeValue("name", xmlRoot).length() > 0
								&& !jobXMLNameWithoutExtension
										.equalsIgnoreCase(Utils
												.getAttributeValue("name",
														xmlRoot))) {
							// life Dateiname und Element-Attribute-name m�ssen
							// gleich sein. Wenn dieser ungleich sind,
							// dann umbennen wie der Dateiname
							listOfChangeElementNames.add(xmlRoot.getName()
									+ "_" + jobXMLNameWithoutExtension);
							xmlRoot.setAttribute("name",
									jobXMLNameWithoutExtension);
						}
						if (Utils.getAttributeValue("name", xmlRoot).length() == 0) {
							// In der Formular sieht man den Namen. Beim
							// speichern soll es nicht zur�ckgeschrieben werden
							xmlRoot.setAttribute("name",
									jobXMLNameWithoutExtension);
						}

						parent.addContent((Element) xmlRoot.clone());

						if (!new File(jobXMLFilename).canWrite()) {
							listOfReadOnly.add(getChildElementName(name) + "_"
									+ Utils.getAttributeValue("name", xmlRoot));
						}
					}
				} catch (Exception e) {
					MainWindow.message(MainWindow.getSShell(), jobXMLFilename
							+ " has error:" + e.toString(), SWT.ICON_WARNING
							| SWT.OK | SWT.CANCEL);
					continue;
				}

			}

		} catch (Exception e) {
			System.out.println("error: " + e.getMessage());
		}
	}

	private void addContainsForOrder(Element parent, String name, String mask) {
		SAXBuilder builder = new SAXBuilder();
		Document currDocument = null;
		try {
			Vector filelist = SOSFile.getFilelist(getNormalizedFile(path)
					.getAbsolutePath(), mask,
					java.util.regex.Pattern.CASE_INSENSITIVE);
			Iterator orderIterator = filelist.iterator();
			while (orderIterator.hasNext()) {
				String xmlFilename = orderIterator.next().toString();
				File xmlFile = new File(xmlFilename);
				currDocument = builder.build(xmlFile);
				Element xmlRoot = currDocument.getRootElement();
				if (xmlRoot != null) {
					if (parent == null) {
						// config hat keinen Kindknoten {name}, also erzeugen
						parent = new Element(name);
						config.addContent(parent);
					}
					// jobchainname,orderid.order.xml
					String xmlNameWithoutExtension = xmlFile
							.getName()
							.substring(
									0,
									xmlFile
											.getName()
											.indexOf(
													"."
															+ (xmlRoot
																	.getName()
																	.equalsIgnoreCase(
																			"add_order") ? "order"
																	: xmlRoot
																			.getName()
																			+ ".xml")));
					String[] splitNames = xmlNameWithoutExtension.split(",");
					String jobChainname = "";
					String orderId = "";
					if (splitNames.length == 2) {
						jobChainname = splitNames[0];
						orderId = splitNames[1];
					}

					if (Utils.getAttributeValue("job_chain", xmlRoot).length() > 0
							&& !jobChainname.equalsIgnoreCase(Utils
									.getAttributeValue("job_chain", xmlRoot))) {
						// life Dateiname und Element-Attribute-name m�ssen
						// gleich sein. Wenn dieser ungleich sind,
						// dann umbennen wie der Dateiname
						listOfChangeElementNames.add(xmlRoot.getName() + "_"
								+ xmlNameWithoutExtension);
						xmlRoot.setAttribute("job_chain", jobChainname);
					}

					if (Utils.getAttributeValue("id", xmlRoot).length() > 0
							&& !orderId.equalsIgnoreCase(Utils
									.getAttributeValue("id", xmlRoot))) {
						// life Dateiname und Element-Attribute-name m�ssen
						// gleich sein. Wenn dieser ungleich sind,
						// dann umbennen wie der Dateiname
						listOfChangeElementNames.add(xmlRoot.getName() + "_"
								+ xmlNameWithoutExtension);
						xmlRoot.setAttribute("id", orderId);
					}

					if (Utils.getAttributeValue("job_chain", xmlRoot).length() == 0) {
						xmlRoot.setAttribute("job_chain", jobChainname);
					}

					if (Utils.getAttributeValue("id", xmlRoot).length() == 0) {
						xmlRoot.setAttribute("id", orderId);
					}

					parent.addContent((Element) xmlRoot.clone());
					if (!new File(xmlFilename).canWrite()) {
						listOfReadOnly.add(getChildElementName(name) + "_"
								+ Utils.getAttributeValue("job_chain", xmlRoot)
								+ "," + Utils.getAttributeValue("id", xmlRoot));
					}
				}

			}

		} catch (Exception e) {
			System.out.println("error: " + e.getMessage());
		}
	}

	private String getChildElementName(String pName) {
		if (pName.equals("jobs")) {
			return "job";
		} else if (pName.equals("process_classes")) {
			return "process_class";
		} else if (pName.equals("locks")) {
			return "lock";
		} else if (pName.equals("job_chains")) {
			return "job_chain";
		} else {
			return pName;
		}

	}

	private String createConfigurationFile() {
		String xml = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?> ";

		try {

			xml = xml + "<spooler>  " + "      <config> " + "      </config> "
					+ "    </spooler>";

		} catch (Exception e) {
			System.out
					.println("..error in MergeAllXMLinDirectory.createConfigurationFile(). Could not create a new configuration file: "
							+ e.getMessage());
		}
		return xml;
	}

	/**
	 * Speichert das Dokument in die einzelnen Dateien wieder zur�ck
	 */
	public void saveXMLDirectory(Document doc, HashMap listOfChanges_) {
		Element jobs = null;
		Element locks = null;
		Element processClass = null;
		Element jobChains = null;
		Element orders = null;
		Element schedules = null;

		listOfChanges = listOfChanges_;
		try {
			// system.out.println("********************************************************************");
			Element root = doc.getRootElement();

			if (root != null) {
				config = root.getChild("config");
			}
			if (config != null) {
				jobs = config.getChild("jobs");
				processClass = config.getChild("process_classes");
				locks = config.getChild("locks");
				jobChains = config.getChild("job_chains");
				orders = config.getChild("commands");
				schedules = config.getChild("schedules");
			}

			save("job", jobs);

			save("process_class", processClass);

			save("schedule", schedules);

			save("lock", locks);

			save("job_chain", jobChains);

			save("order", orders);

			save("add_order", orders);

			deleteFiles();

			listOfChanges.clear();

			// system.out.println("********************************************************************");
		} catch (Exception e) {
			System.out
					.println("..error in MergeAllXMLinDirectory.save. Could not save file "
							+ e.getMessage());
		}

	}

	private void save(String name, Element elem) {
		// String filename = " ";
		List list = null;

		if (elem != null) {
			list = elem.getChildren(name);
		}

		if (list == null || list.size() == 0) {
			return;
		}

		for (int i = 0; i < list.size(); i++) {
			Element e = (Element) list.get(i);

			saveLifeElement(name, e);

		}
	}

	public String saveLifeElement(String name, Element e,
			HashMap listOfChanges_, ArrayList listOfChangeElementNames_) {
		listOfChangeElementNames = listOfChangeElementNames_;
		listOfChanges = listOfChanges_;
		return saveLifeElement(name, e);
	}

	public String saveAsLifeElement(String name, Element e, String filename) {
		String attrName = "";
		if (name.equals("add_order"))
			name = "order";

		if (name.equals("order")) {
			if (!filename.endsWith(".order.xml")) {
				if (new File(filename).renameTo(new File(filename
						+ ".order.xml"))) {
					new File(filename).deleteOnExit();
				}
				filename = filename + ".order.xml";
			}

			String[] file = new File(filename).getName().split(",");
			if (file.length == 2) {
				attrName = (file.length >= 2 ? file[0] : "") + ","
						+ (file.length >= 2 ? file[1] : "");
				attrName = attrName
						.substring(0, attrName.indexOf(".order.xml"));
			} else {

				attrName = Utils.getAttributeValue("job_chain", e) + ","
						+ file[0];
				filename = filename.replaceAll(new File(filename).getName(),
						attrName);
				attrName = attrName
						.substring(0, attrName.indexOf(".order.xml"));

			}

		} else {
			if (!filename.endsWith("." + e.getName() + ".xml")) {
				if (!new File(filename).renameTo(new File(filename + "."
						+ e.getName() + ".xml"))) {
					new File(filename).deleteOnExit();
				}
				filename = filename + "." + e.getName() + ".xml";
			}
			String fname = new File(filename).getName();
			attrName = fname.substring(0, fname.indexOf("." + e.getName()));
		}

		Element _elem = e;
		if (name.equals("order")) {
			_elem.removeAttribute("job_chain");
			_elem.removeAttribute("id");
			_elem.removeAttribute("replace");
		} else {
			_elem.removeAttribute("name");
		}
		String xml = Utils.getElementAsString(_elem);
		saveXML(xml, filename);

		if (name.equals("order")) {
			Utils.setAttribute("job_chain", attrName.substring(0, attrName
					.indexOf(",")), e);
			Utils.setAttribute("id", attrName
					.substring(attrName.indexOf(",") + 1), e);
		} else {
			Utils.setAttribute("name", attrName, e);
		}
		return filename;

	}

	public String saveLifeElement(String name, Element e) {
		String filename = " ";
		String attrName = "";
		if (name.equals("add_order"))
			name = "order";

		if (name.equals("order")) {
			attrName = Utils.getAttributeValue("job_chain", e) + ","
					+ Utils.getAttributeValue("id", e);
		} else {
			attrName = Utils.getAttributeValue("name", e);
		}

		if (attrName != null && attrName.length() == 0)
			return "";

		filename = (path.endsWith("/") || path.endsWith("\\") ? path : path
				.concat("/"))
				+ attrName
				+ "."
				+ (name.equalsIgnoreCase("add_order") ? "order" : name)
				+ ".xml";
		// filename = (path.endsWith("/") || path.endsWith("\\")? path :
		// path.concat("/")) + new File(attrName).getName() + "." +
		// (name.equalsIgnoreCase("add_order")? "order": name) + ".xml";

		if (listOfChanges.containsKey(name + "_" + attrName)) {
			if (listOfChanges.get(name + "_" + attrName).equals(
					SchedulerDom.DELETE)) {
				if (!new File(filename).delete()) {
					MainWindow.message(MainWindow.getSShell(), filename
							+ " could not delete.", SWT.ICON_WARNING | SWT.OK
							| SWT.CANCEL);
				}
			} else {

				Element _elem = e;
				if (name.equals("order")) {
					_elem.removeAttribute("job_chain");
					_elem.removeAttribute("id");
					_elem.removeAttribute("replace");
				} else {
					_elem.removeAttribute("name");
				}

				if (name.equals("job")) {
					e.removeAttribute("spooler_id");
				}
				String xml = Utils.getElementAsString(_elem);
				saveXML(xml, filename);

				// attribute wieder zur�ckschreiben zum weiterverarbeiten
				if (name.equals("order")) {
					Utils.setAttribute("job_chain", attrName.substring(0,
							attrName.indexOf(",")), e);
					Utils.setAttribute("id", attrName.substring(attrName
							.indexOf(",") + 1), e);
				} else {
					Utils.setAttribute("name", attrName, e);
				}

			}
		}
		// Element ist neu angelegt, also muss dieser auch gespeichert werden.
		if (!new File(filename).exists()) {
			String xml = Utils.getElementAsString(e);
			saveXML(xml, filename);
		}

		deleteFiles();

		return filename;
	}

	private void saveXML(String xml, String filename) {

		String originalFilename = filename;
		filename = filename + "~";

		try {
			// system.out.println("********************************************************************");
			SAXBuilder builder2 = getBuilder(false);
			Document doc = builder2.build(new StringReader(xml));
			// test
			SchedulerDom dom = new SchedulerDom(SchedulerDom.DIRECTORY);
			// dom.setInit(true);

			new File(originalFilename).delete();

			dom.writeElement(filename, doc);

			if (!new File(filename).renameTo(new File(originalFilename))) {
				MainWindow.message(MainWindow.getSShell(),
						"could not rename file in " + filename,
						SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
			}
			// dom.setInit(false);
		} catch (Exception e) {
			// System.out.println("..error in MergeAllXMLinDirectory.saveXML. Could not save file "
			// + e.getMessage());
			MainWindow.message(MainWindow.getSShell(), "could not save file "
					+ filename + ". cause:" + e.getMessage(), SWT.ICON_WARNING
					| SWT.OK | SWT.CANCEL);
		}

	}

	private void deleteFiles() {

		String filename = "";
		String prefix = "";
		Iterator keys1 = listOfChanges.keySet().iterator();
		Iterator values1 = listOfChanges.values().iterator();
		while (keys1.hasNext()) {
			String key = keys1.next().toString();
			Object oVal = values1.next();
			String val = (oVal != null ? oVal.toString() : "");
			if (val.equals(SchedulerDom.DELETE)) {
				if (key.startsWith("job_chain_")) {
					prefix = "job_chain_";
				} else if (key.startsWith("job_")) {
					prefix = "job_";
				} else if (key.startsWith("process_class_")) {
					prefix = "process_class_";
				} else if (key.startsWith("lock_")) {
					prefix = "lock_";
				} else if (key.startsWith("order_")) {
					prefix = "order_";
				} else if (key.startsWith("schedule_")) {
					prefix = "schedule_";
				}
				filename = (path.endsWith("/") || path.endsWith("\\") ? path
						: path.concat("/"))
						+ key.substring(prefix.length())
						+ "."
						+ prefix.substring(0, prefix.length() - 1) + ".xml";
				File f = new File(filename);
				if (f.exists()) {
					if (!f.delete()) {
						// job5.job.xml
						MainWindow.message(MainWindow.getSShell(), filename
								+ " could not delete.", SWT.ICON_WARNING
								| SWT.OK | SWT.CANCEL);
					} else {
						// System.out.println(filename + " wurde gel�scht.");
					}
				} else {

					// System.out.println(filename +
					// " zum l�schen gibts nicht.");
				}
			}

		}
	}

	public String getJobname(String filename) {
		String jobname = "";
		try {
			SAXBuilder builder = new SAXBuilder();

			// String xml = createConfigurationFile();

			Document doc = builder.build(new File(filename));

			Element root = doc.getRootElement();

			jobname = Utils.getAttributeValue("name", root);

		} catch (Exception e) {
			MainWindow.message(".. could not get jobname from " + filename
					+ " cause: " + e.getMessage(), SWT.ICON_ERROR);
		}
		return jobname;
	}

	public static Element readElementFromHotHolderFile(File file) {
		Element elem = null;
		try {
			SAXBuilder builder = new SAXBuilder();

			// String xml = createConfigurationFile();

			Document doc = builder.build(file);

			elem = doc.getRootElement();
			String name = file.getName().substring(0,
					file.getName().indexOf("."));

			if (elem.getName().equals("order")
					|| elem.getName().equals("add_order")) {
				String[] split = name.split(",");
				String jobChain = split[0];
				String id = split.length > 1 ? split[1] : "";
				Utils.setAttribute("job_chain", jobChain, elem);
				Utils.setAttribute("id", id, elem);
			} else

				Utils.setAttribute("name", name, elem);

		} catch (Exception e) {
			MainWindow.message(".. could not read Element from " + file
					+ " cause: " + e.getMessage(), SWT.ICON_ERROR);
		}
		return elem;
	}

	public static void main(String[] args) throws Exception {
		// MergeAllXMLinDirectory allJob = new
		// MergeAllXMLinDirectory("C:/scheduler/config/temp",
		// "C:/scheduler/config/temp/config.xml");
		// allJob.parseDocuments();

		/*
		 * SAXBuilder builder = new SAXBuilder();
		 * 
		 * builder.setFeature("http://apache.org/xml/features/xinclude", true);
		 * Document doc = builder.build("C:/temp/a.xml");
		 * 
		 * Element root = doc.getRootElement();
		 * System.out.println(Utils.getElementAsString(root));
		 */

		/*
		 * javax.xml.parsers.SAXParserFactory spf =
		 * javax.xml.parsers.SAXParserFactory.newInstance();
		 * spf.setNamespaceAware(true); spf.setXIncludeAware(true);
		 */

		/*
		 * javax.xml.parsers.DocumentBuilderFactory dbf =
		 * javax.xml.parsers.DocumentBuilderFactory.newInstance();
		 * dbf.setNamespaceAware(true); dbf.setXIncludeAware(true);
		 * 
		 * javax.xml.parsers.DocumentBuilder dom = dbf.newDocumentBuilder();
		 * org.w3c.dom.Document document_ = dom.parse("C:/temp/a.xml");
		 * org.w3c.dom.Element root_ = document_.getDocumentElement();
		 * System.out.println(root_);
		 */

		/*
		 * System.setProperty("javax.xml.parsers.DocumentBuilderFactory","org.apache.xerces.jaxp.DocumentBuilderFactoryImpl"
		 * ); javax.xml.parsers.DocumentBuilderFactory dbfactory =
		 * javax.xml.parsers.DocumentBuilderFactory.newInstance();
		 * 
		 * dbfactory.setFeature("http://apache.org/xml/features/xinclude",
		 * true);dbfactory.setFeature(
		 * "http://apache.org/xml/features/xinclude/fixup-base-uris",false);
		 * 
		 * 
		 * dbfactory.setNamespaceAware(true); dbfactory.setXIncludeAware(true);
		 * 
		 * 
		 * javax.xml.parsers.DocumentBuilder parser =
		 * dbfactory.newDocumentBuilder();
		 * 
		 * System.out.println("Namespace aware:" + parser.isNamespaceAware());
		 * System.out.println("XInclude aware:" + parser.isXIncludeAware());
		 * 
		 * org.w3c.dom.Document document = parser.parse(new
		 * File("sourceXMLFilePath"));
		 * 
		 * //Source xmlsource = new DOMSource(document);
		 */

	}

	public ArrayList getListOfReadOnly() {
		return listOfReadOnly;
	}

	public ArrayList getListOfChangeElementNames() {
		return listOfChangeElementNames;
	}

	public void setListOfChangeElementNames(ArrayList listOfChangeElementNames) {
		this.listOfChangeElementNames = listOfChangeElementNames;
	}

	protected SAXBuilder getBuilder(boolean validate) throws IOException {

		SAXBuilder builder = new SAXBuilder(validate);
		if (validate) {

			builder.setProperty(
					"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
					"http://www.w3.org/2001/XMLSchema");
			builder.setProperty(
					"http://java.sun.com/xml/jaxp/properties/schemaSource",
					writeSchemaFile());

		}

		return builder;

	}

	protected String[] writeSchemaFile() throws IOException {
		try {
			String[] s = new String[1];
			s[0] = getClass().getResource(Options.getSchema()).toString();
			return s;
		} catch (Exception e) {
			try {
				new ErrorLog("error in "
						+ sos.util.SOSClassUtil.getMethodName()
						+ " ; could get schema ", e);
			} catch (Exception ee) {
				// tu nichts
			}
			throw new IOException(
					"error in writeSchemaFile(). could get schema "
							+ e.toString());
		}
	}

	/*
	 * protected String[] writeSchemaFile_old() throws IOException { ArrayList
	 * urls = new ArrayList();
	 * 
	 * String[] _schemaTmpFile = new String[] {"scheduler_editor_schema" };
	 * 
	 * String[] _schemaResource = new String[] { Options.getSchema() };
	 * 
	 * for (int i = 0; i < _schemaTmpFile.length; i++) { if (_schemaTmpFile[i]
	 * != null && !_schemaTmpFile[i].equals("") && _schemaResource[i] != null &&
	 * !_schemaResource[i].equals("")) {
	 * 
	 * File tmp = File.createTempFile(_schemaTmpFile[i], ".xsd");
	 * tmp.deleteOnExit(); InputStream in =
	 * getClass().getResourceAsStream(_schemaResource[i]);
	 * 
	 * FileOutputStream out = new FileOutputStream(tmp, true);
	 * 
	 * int c; while ((c = in.read()) != -1) out.write(c);
	 * 
	 * in.close(); out.close();
	 * 
	 * urls.add(tmp.getAbsolutePath()); } }
	 * 
	 * return (String[]) urls.toArray(new String[urls.size()]); }
	 */

}
