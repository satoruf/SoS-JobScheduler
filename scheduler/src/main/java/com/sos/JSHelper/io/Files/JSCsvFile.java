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
package com.sos.JSHelper.io.Files;
/**
* \class JSCsvFile
*
* \brief JSCsvFile - Die Klasse repr�sentiert eine Csv-Datei
*
* \details
* Die Klasse repr�sentiert eine Csv-Datei und bietet die typischen Operationen f�r eine solche Datei.
*
* \section JSCsvFile.intro_sec Introduction
*
* \section JSCsvFile.samples Some Samples
*
* \code
	private void TestCSVFile() throws Exception {
		String strFileName = "data/sample.csv";
		JSCsvFile objCsvFile = new JSCsvFile(strFileName);
		String strField = null;
		objCsvFile.loadHeaders();
		while (true) {
			strField = objCsvFile.readCSVField();

			if (strField == null)
				break;

			if (strField.equalsIgnoreCase(JSCsvFile.END_OF_LINE))
				System.out.print("\n");
			else

				System.out.print(strField + "=");
		}
		System.out.print("\n");
		objCsvFile.close();

		objCsvFile = new JSCsvFile(strFileName);
		while ( (strField = objCsvFile.readCSVField("Text")) != null) {
			message(strField);
		}
		objCsvFile.close();

	}
* \endcode
*
* <p style="text-align:center">
* <br />---------------------------------------------------------------------------
* <br /> APL/Software GmbH - Berlin
* <br />##### generated by ClaviusXPress (http://www.sos-berlin.com) #########
* <br />Mittwoch, 22. Oktober 2008, sgx2343 (sgx2343)
* <br />---------------------------------------------------------------------------
* </p>
* \author sgx2343
* @version $Id: JSCsvFile.java 14731 2011-07-05 20:50:42Z sos $0.9
* \see JSCsvFile
*
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import com.sos.JSHelper.Listener.JSListener;

public class JSCsvFile extends JSTextFile {

	private static final long	serialVersionUID					= 1L;

	private final String		conClassName						= "JSCsvFile";

	/**
	 * strHeaders - a Header for each Column Headers must be defined by the
	 * caller
	 */
	private String[]			strHeaders							= null;						// a
	private String[]			strCurrentLine						= null;						// Array
	static public String		END_OF_LINE							= new String("END_OF_LINE");
	static public char			FIELD_DELIMITER						= ';';
	static public char			BLOCK_DELIMITER						= '\n';
	static public char			VALUE_DELIMITER						= '\"';
	private char				chrColumnDelimiter					= FIELD_DELIMITER;
	private char				chrRecordDelimiter					= BLOCK_DELIMITER;
	private final char				chrValueDelimiter					= VALUE_DELIMITER;
	private final char				chrEscapeCharacter					= '\\';
	// private Reader reader;
	private boolean				flgIsNewline;
	/** boolean IgnoreValueDelimiter: Ignore Value Delimiter */
	private boolean				flgIgnoreValueDelimiter				= false;
	private boolean				flgHeadersWritten					= false;

	/** boolean AlwaysSurroundFielJSithQuotes: AlwaysSurroundFielJSithQuotes */
	private boolean				flgAlwaysSurroundFielJSithQuotes	= true;
	private int					lngNoOfFieldsInBuffer				= 0;

	/**
	 * \change Donnerstag, 5. M�rz 2009 EQCPN IGNORECOLS
	 * Zeilen mit falscher Anzahl an Spalten �berlesen
	 */
	private int					intFieldCount						= 0;
	private int					intRecordCount						= 0;
	private boolean				flgFieldCount						= true;

	private boolean				flgCheckColumnCount					= true;

	// should bbb,,,ccc be considered to be two elements?
	// useful for log parsing.
	// private boolean consume;

	/*
	 * ---------------------------------------------------------------------------
	 * <method type="smcw" version="1.0"> <name></name> <title>Represents a
	 * CSV-File as an Object</title> <description> <para> Represents a CSV-File
	 * as an Object </para> </description> <params> </params> <keywords>
	 * <keyword>CSV-File</keyword> <keyword>Object:CSV</keyword> </keywords>
	 * <categories> <category>TextFile</category> <category>CSV</category>
	 * </categories> </method>
	 * ----------------------------------------------------------------------------
	 */
	public JSCsvFile ColumnDelimiter(final String pstrColumnDelimiter) {
		chrColumnDelimiter = pstrColumnDelimiter.toCharArray()[0];
		return this;
	}

	public char ColumnDelimiter() {
		return chrColumnDelimiter;
	}

	public JSCsvFile(final String pstrFileName) {
		super(pstrFileName);
	} // public JSCsvFile (String pstrFileName)

	public JSCsvFile(final String pstrFileName, final JSListener objListener) {
		super(pstrFileName);
		registerMessageListener(objListener);
	}

	public void loadHeaders() throws Exception {
		if (strHeaders == null) {
			strHeaders = readCSVLine();
		}
	}

	public boolean CheckColumnCount() {

		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::CheckColumnCount";

		return flgCheckColumnCount;
	} // public boolean CheckColumnCount}

	public void CheckColumnCount(final boolean pflgCheckColumnCount) {

		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::CheckColumnCount";

		flgCheckColumnCount = pflgCheckColumnCount;

	} // public void CheckColumnCount

	public JSCsvFile Headers(final String[] pstrHeaders) throws Exception {
		strHeaders = pstrHeaders;
		WriteHeaders();
		return this;
	}

	public JSCsvFile Headers(final ArrayList<String> fields) throws Exception {

		strHeaders = new String[fields.size()];
		for (int j = 0; j < fields.size(); j++) {
			strHeaders[j] = fields.get(j);
		}
		WriteHeaders();
		return this;
	}

	/**
	 *
	 * \brief WriteHeaders
	 *
	 * \details

	 * \return JSCsvFile
	 *
	 * @return
	 * @throws Exception
	 */
	public JSCsvFile WriteHeaders() throws Exception {
		if (!flgHeadersWritten && NoOfCharsInBuffer() <= 0) {
			this.append(strHeaders);
			// this.NewLine(); kommt automatisch mit, weil append immer eine
			// gesamte
			// Zeile behandelt.
			flgHeadersWritten = true;
		}
		return this;
	}

	/**
	 *
	 * \brief Headers
	 *
	 * \details

	 * \return String[]
	 *
	 * @return
	 */
	public String[] Headers() {
		return strHeaders;
	}

	/**
	 * \change Donnerstag, 5. M�rz 2009 EQCPN IGNORECOLS
	 * Zeilen mit falscher Anzahl an Spalten �berlesen
	 */
	public String[] readCSVLine() throws IOException {
		final ArrayList<String> list = new ArrayList<String>();
		String str = null;
		//- <remark who='EQCPN' when='Donnerstag, 5. M�rz 2009' id='IGNORECOLS' >
		//-   <para>
		//-   Zeilen mit falscher Anzahl an Spalten �berlesen
		//-   </para>
		//- <oldcode>
		//	while (true) {
		//		str = readCSVField();
		//		if (str == null) { // EOF
		//			break;
		//		}
		//		if (str == END_OF_LINE) { // EOL
		//			break;
		//		}
		//		list.add(str);
		//	}
		//
		//	if (list.isEmpty()) {
		//		return null;
		//	}
		//- </oldcode>
		//- <newcode>
		int intColumnCount = 0; // Anzahl Spalten des aktuellen Satzes
		intRecordCount++; // Satzz�hler
		while (true) {
			str = readCSVField();
			if (str == null) { // EOF
				break;
			}
			if (str == END_OF_LINE && intColumnCount == intFieldCount) { // EOL
				flgFieldCount = false;
				break;
			}
			if (flgFieldCount) {
				intFieldCount++; // Anzahl Spalten des ersten Satzes
			}
			intColumnCount++;
			if (str == END_OF_LINE) { // EOL
				list.add("");
				break;
			}
			list.add(str);
		}

		if (intColumnCount != intFieldCount) {
			if (flgCheckColumnCount == true) {
				message("WARN: problem in record " + intRecordCount + " - " + intFieldCount + " columns expected, but the record contains " + intColumnCount);
			}
		}

		if (list.isEmpty() || intColumnCount != intFieldCount && flgCheckColumnCount == true) {
			return null;
			//			if (intColumnCount > 0 && list.get(0).startsWith("#") == false) {
			//				return null;
			//			}
			//TODO: Besser eine Exception werfen. Der Empf�nger soll selbst entscheiden, was zu tun ist
		}
		//- </newcode>
		//- </remark>      <!-- id=<IGNORECOLS>  -->

		return list.toArray(new String[0]);
	}

	/**
	 *
	 * \brief readCSVField - Read (the next) field from the CSV-File
	 *
	 * \details
	 *
	 * \code
	 * 		while (true) {
			strField = objCsvFile.readCSVField();
			if (strField == null)
				break;
			if (strField.equalsIgnoreCase(JSCsvFile.END_OF_LINE))
				System.out.print("\n");
			else
				System.out.print(strField + "=");
		}
	 * \endcode
	 * \return String The content of the Field or END_OF_LINE or null as EOF
	 *
	 * @throws IOException
	 */
	public String readCSVField() throws IOException {

		if (flgIsNewline) { // End of line occured
			flgIsNewline = false;
			return END_OF_LINE;
		}

		StringBuffer stbBuffer = new StringBuffer();
		boolean flgFieldIsQuoted = false;
		int intLast = -1;
		int ch = Reader().read();

		if (ch == -1) {
			return null;
		}

		if (ch == chrValueDelimiter && !flgIgnoreValueDelimiter) {
			flgFieldIsQuoted = true;
		}
		else {
			if (ch == chrRecordDelimiter && flgFieldIsQuoted == false) {
				return END_OF_LINE;
			}
			else {
				if (ch == chrColumnDelimiter && !flgFieldIsQuoted) {
					return "";
				}
				else {
					stbBuffer.append((char) ch);
				}
			}
		}

		while ((ch = Reader().read()) != -1) {
			if (ch == chrRecordDelimiter && !flgFieldIsQuoted) {
				final int intL = stbBuffer.length();
				if (intL > 1) {
					if (stbBuffer.charAt(stbBuffer.length() - 1) == '\r') {
						final String strB = stbBuffer.substring(0, stbBuffer.length() - 1);
						stbBuffer = new StringBuffer(strB);
						lngNoOfLinesRead++;
					}
				}
				flgIsNewline = true;
				break;
			}
			if (flgFieldIsQuoted) {
				if (ch == chrValueDelimiter) {
					if (intLast == ch) { // double-quote
						intLast = -1;
						stbBuffer.append(chrValueDelimiter);
						continue;
					}
					intLast = ch;
					continue;
				}
			}
			if (ch == chrColumnDelimiter) {
				if (flgFieldIsQuoted) {
					if (intLast == chrValueDelimiter) {
						break;
					}
				}
				else {
					break;
				}
			}
			stbBuffer.append((char) ch);
		}

		final String strR = RemoveNewLineChar(stbBuffer);
		// message (strR);
		return strR;
	} // public String readCSVField()

	/**
	 *
	 * \brief RemoveNewLineChar
	 *
	 * \details

	 * \return String
	 *
	 * @param pstrB
	 * @return
	 */
	private String RemoveNewLineChar(final StringBuffer pstrB) {
		String strB = pstrB.toString();

		int intL = pstrB.length();
		if (intL > 1) {
			intL--;
			if (pstrB.charAt(intL) == '\r') {
				strB = pstrB.substring(1, pstrB.length());
				// pstrB = new StringBuffer (strB);
			}
		}
		return strB;

	}

	public String readCSVField(final String pstrColumnName) throws Exception {
		if (strHeaders == null) {
			loadHeaders();
		}

		if (strCurrentLine == null) {
			nextBlock();
			if (strCurrentLine == null) {
				return null;
			}
		}

		int idx = -1;
		for (int i = 0; i < strHeaders.length; i++) {
			if (pstrColumnName.equalsIgnoreCase(strHeaders[i])) {
				idx = i;
			}
		}

		String strFieldValue = null;

		if (idx != -1) {
			strFieldValue = strCurrentLine[idx];
			strCurrentLine = null;
		}
		else {
			message(String.format("ColumnName '%1$s' not found in Headers.", pstrColumnName));
		}

		return strFieldValue;
	}

	private boolean isQuoteChar(final char c) {
		// for (int i = 0; i < quotes.length; i++)
		// {
		// char quote = quotes[i];
		if (c == chrValueDelimiter) {
			return true;
		}
		// }
		return false;
	}

	public boolean nextBlock() throws IOException {
		strCurrentLine = readCSVLine();
		return strCurrentLine != null;
	}

	public JSCsvFile append(final Object[] fields) throws Exception {

		final StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < fields.length; i++) {
			buffer.append(MaskSpecialChars(String.valueOf(fields[i])));
			if (i < fields.length - 1) {
				buffer.append(chrColumnDelimiter);
			}
		}

		Write(buffer.toString());
		return this;
	}

	public JSCsvFile append(final Vector<String> fields) throws Exception {

		final StringBuffer buffer = new StringBuffer();
		final int intSize = fields.size();
		for (int i = 0; i < intSize; i++) {
			buffer.append(MaskSpecialChars(String.valueOf(fields.elementAt(i))));
			if (i < intSize - 1) {
				buffer.append(chrColumnDelimiter);
			}
		}
		Write(buffer.toString());
		return this;
	}

	public JSCsvFile append(final Iterator<String> fields) throws Exception {

		final StringBuffer buffer = new StringBuffer();
		while (fields.hasNext()) {
			final String elem = fields.next();
			buffer.append(MaskSpecialChars(elem));
			if (fields.hasNext()) {
				buffer.append(chrColumnDelimiter);
			}
		}
		Write(buffer.toString());
		return this;
	}

	/**
	 * AddCellValues Eine Liste mit Feldwerten in die Datei �bernehmen
	 *
	 * @param fields
	 * @return JSCsvFile
	 * @throws Exception
	 */
	public JSCsvFile AddCellValues(final Object[] fields) throws Exception {

		for (final Object field : fields) {
			AddCellValue(String.valueOf(field));
		}

		return this;
	}

	/**
	 * @brief AddCellValues Eine Liste mit Feldwerten in die Datei �bernehmen
	 *
	 * @param fields
	 * @return JSCsvFile
	 * @throws Exception
	 */
	public JSCsvFile AddCellValues(final Iterator<String> fields) throws Exception {

		this.append(fields);
		return this;
	}

	/**
	 * @brief NewLine Abschluss der Zeile
	 *
	 * Am Ende einer Zeile (wenn also alle Felder f�r die Zeile mit Werten
	 * gef�llt sind) ist diese Methode aufzurufen. Damit wird der bis dahin
	 * gepufferte Satz dann auf die Ausgabe geschrieben und es werden einige
	 * interne Felder zur�ckgesetzt.
	 *
	 * @return JSCsvFile
	 * @throws Exception
	 */
	@Override
	public JSCsvFile NewLine() throws Exception {

		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::NewLine";

		super.NewLine();
		lngNoOfFieldsInBuffer = 0;
		return this;
	}

	/**
	 * @brief AddCellValue fuegt dem aktuellen Puffer eine Textzelle hinzu
	 *
	 * Der als Parameter uebergebene String wird dem aktuellen Textpuffer
	 * hinzugef�gt. Zwischen den einzelnen Werten im Puffer wird ein Feldtrenner
	 * (ColumnDelimiter) eingef�gt.
	 *
	 * @param pstrS
	 *            Feldwert
	 * @return JSCsvFile
	 * @throws Exception
	 */
	public JSCsvFile AddCellValue(final String pstrS) throws Exception {

		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::AddCellValue";

		if (NoOfCharsInBuffer() > 0 || lngNoOfFieldsInBuffer > 0) {
			OutChar(chrColumnDelimiter);
		}
		OutString(MaskSpecialChars(pstrS));
		lngNoOfFieldsInBuffer++;
		return this;
	}

	/**
	 * @brief MaskSpecialChars - Textbegrenzer einbauen
	 *
	 * Hier werden die Textbegrenzer (Standard ist doppelte Anf�hrungszeichen
	 * '"'; siehe Option \c ColumnDelimiter) in den Text eingebaut, falls es
	 * gefordert (Option \c AlwaysSurroundFielJSithQuotes) oder durch den Text
	 * selbst erforderlich ist.
	 *
	 * Beginnt der Text bereits mit dem Textbegrenzer, so werden alle
	 * Textbegrenzer verdoppelt und der gesamte Text nocheinmal in einfache
	 * Begrenzer eingeschlossen. Ist der Text leer, werden Textbegrenzer
	 * eingesetzt, auch dann nicht, wenn �ber die Option
	 * AlwaysSurroundFielJSithQuotes welche angefordert worden sind.
	 *
	 * @param strT zu modifizierender Text
	 *
	 * @return modifizierter Text
	 */
	private String MaskSpecialChars(String strT) {

		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::MaskSpecialChars";

		boolean flgSurroundWithQuotes = false;
		final String strDelim = String.valueOf(chrValueDelimiter);

		final int iPos = strT.indexOf(chrValueDelimiter);
		if (iPos >= 0) { // -1 is an andicator for "nothing found"
			strT = strT.replace(strDelim, strDelim + strDelim);
			flgSurroundWithQuotes = true;
		}
		if (strT.length() > 0 && flgAlwaysSurroundFielJSithQuotes) {
			flgSurroundWithQuotes = true;
		}

		if (flgSurroundWithQuotes || strT.indexOf(chrColumnDelimiter) > 0 || strT.indexOf('\r') > 0 || strT.indexOf('\n') > 0) {
			strT = strDelim + strT + strDelim;
		}

		return strT;
	}

	@SuppressWarnings("unused")
	private CharSequence munge(final CharSequence field) {
		final StringBuffer buffer = new StringBuffer();
		char c = 0;
		for (int i = 0; i < field.length(); i++) {
			c = field.charAt(i);
			if (isQuoteChar(c) || c == chrColumnDelimiter) {
				buffer.append(chrEscapeCharacter);
				buffer.append(c);
			}
			else {
				buffer.append(c);
			}
		}
		return buffer.toString();
	} // private CharSequence munge(CharSequence field)

	/*
	 * ---------------------------------------------------------------------------
	 * <method type="smcw" version="1.0"> <name>AlwaysSurroundFielJSithQuotes</name>
	 * <title>AlwaysSurroundFielJSithQuotes</title> <description> <para>
	 * AlwaysSurroundFielJSithQuotes </para> <para> Initial-Wert (Default) ist
	 * "true" (ohne Anf�hrungszeichen). </para> <mandatory>true</mandatory>
	 * </description> <params> <param name="param1" type=" "
	 * ref="byref|byvalue|out" > <para> </para> </param> </params> <keywords>
	 * <keyword>CSV</keyword> <keyword>Options</keyword> </keywords>
	 * <categories> <category>Options</category> </categories> </method>
	 * ----------------------------------------------------------------------------
	 */
	/**
	 * @brief AlwaysSurroundFielJSithQuotes - AlwaysSurroundFielJSithQuotes
	 *
	 * Getter: AlwaysSurroundFielJSithQuotes
	 *
	 * Example:
	 *
	 * @return Returns the AlwaysSurroundFielJSithQuotes.
	 */
	public boolean AlwaysSurroundFielJSithQuotes() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::AlwaysSurroundFielJSithQuotes";
		return flgAlwaysSurroundFielJSithQuotes;
	} // boolean AlwaysSurroundFielJSithQuotes()

	/*
	 * ! AlwaysSurroundFielJSithQuotes - AlwaysSurroundFielJSithQuotes
	 *
	 * Setter: AlwaysSurroundFielJSithQuotes
	 *
	 * @param pflgAlwaysSurroundFielJSithQuotes: The boolean
	 * AlwaysSurroundFielJSithQuotes to set.
	 */
	public JSCsvFile AlwaysSurroundFielJSithQuotes(final boolean pflgAlwaysSurroundFielJSithQuotes) throws Exception {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::AlwaysSurroundFielJSithQuotes";
		flgAlwaysSurroundFielJSithQuotes = pflgAlwaysSurroundFielJSithQuotes;
		return this;
	} // public void AlwaysSurroundFielJSithQuotes(boolean

	// pflgAlwaysSurroundFielJSithQuotes)

	/* ---------------------------------------------------------------------------
	<method type="smcw" version="1.0">
	<name>IgnoreValueDelimiter</name>
	<title>Ignore Value Delimiter</title>
	<description>
	<para>
	Ignore Value Delimiter
	</para>
	<para>
	Initial-Wert (Default) ist "false" (ohne Anf�hrungszeichen).
	</para>
	<mandatory>true</mandatory>
	</description>
	<params>
		<param name="param1" type=" " ref="byref|byvalue|out" >
			<para>
			</para>
		</param>
	</params>
	<keywords>
		<keyword>WarehouseTransactions</keyword>
		<keyword>CSV</keyword>
		<keyword>Converter</keyword>
		<keyword>Import</keyword>
	</keywords>
	<categories>
	<category>Inbound</category>
	<category>CSV</category>
	<category>Mapics</category>
	</categories>
	</method>
	---------------------------------------------------------------------------- */
	/*!
	 * @brief Ignore Value Delimiter
	 *
	 * Ignore Value Delimiter
	 *
	 */
	/*!
	 * IgnoreValueDelimiter - Ignore Value Delimiter
	 *
	 * Getter: Ignore Value Delimiter
	 *
	 * Example:
	 *
	 * @return Returns the IgnoreValueDelimiter.
	 */
	public boolean IgnoreValueDelimiter() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::IgnoreValueDelimiter";
		return flgIgnoreValueDelimiter;
	} // boolean IgnoreValueDelimiter()

	/*!
	 * IgnoreValueDelimiter - Ignore Value Delimiter
	 *
	 * Setter: Ignore Value Delimiter
	 *
	 * @param pflgIgnoreValueDelimiter: The boolean IgnoreValueDelimiter to set.
	 */
	public JSCsvFile IgnoreValueDelimiter(final boolean pflgIgnoreValueDelimiter) throws Exception {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::IgnoreValueDelimiter";
		flgIgnoreValueDelimiter = pflgIgnoreValueDelimiter;
		return this;
	} // public void IgnoreValueDelimiter(boolean pflgIgnoreValueDelimiter)

	/**
	 * \brief setRecordDelimiter
	 *
	 * \details
	 *
* @version $Id: JSCsvFile.java 14731 2011-07-05 20:50:42Z sos $2009-03-05-EQCPN
	 * \return void
	 *
	 * @param pchrRecordDelimiter
	 */
	public void setRecordDelimiter(final char pchrRecordDelimiter) {
		chrRecordDelimiter = pchrRecordDelimiter;
	}

	public void write(final String[] pstrCells) throws Exception {

		@SuppressWarnings("unused")
		final String	conMethodName	= conClassName + "::write";

		String strT = "";
		for (int i =0; i < pstrCells.length; i++) {
			if (pstrCells[i] == null) {
				pstrCells[i] = "";
			}
			else {
				pstrCells[i] = AdjustCsvValue(pstrCells[i]);
			}
			strT += pstrCells[i] + FIELD_DELIMITER;
		}

		if (bufWriter == null) {
			Writer();
		}

		bufWriter.write(strT);

	} // public void write}
	public void WriteLine(final String[] pstrCells) throws Exception {

		@SuppressWarnings("unused")
		final String	conMethodName	= conClassName + "::WriteLine";

		write (pstrCells);
		this.WriteLine("");
	} // public void WriteLine

	public String AdjustCsvValue(final String pstrV) {

		@SuppressWarnings("unused")
		final String	conMethodName	= conClassName + "::AdjustCsvValue";

		String strT = pstrV;
		if (pstrV.indexOf(FIELD_DELIMITER) > 0) {
			strT = VALUE_DELIMITER + strT + VALUE_DELIMITER;
		}
		return strT;
	} // public String AdjustCsvValue

} // public class JSCsvFile
