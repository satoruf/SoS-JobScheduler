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
package com.sos.JSHelper.Options;


/**
* \class SOSOptionGracious 
* 
* \brief SOSOptionGracious - 
* 
* \details
*
*         <note language="en">
          <div xmlns="http://www.w3.org/1999/xhtml">
            Enables or disables error messages that are caused by a nonexistent file or directory
            being specified with the parameters <code>file</code> and respectively <code>file_spec</code>.
            <br/><br/>
            Valid values:
            <em>false, 0, off, no, n, nein, none</em>
            , 
            <em>true, 1, on, yes, y, ja, j</em>
            and
            <em>all</em>
            <br/><br/>
            The following rules apply when <code>file</code> and respectively <code>file_spec</code> contains an incorrect value:
            <table border="1" cellspacing="0">
					<tr>
						<th><code>GRACIOUS</code></th>
						<th><code>Standalone-Job</code></th>
						<th><code>Order-Job</code></th>
					</tr>
					<tr>
						<td>false, 0, off, no, n, nein, none</td>
						<td>error log,<br/>Task error</td>
						<td>error log,<br/>set_state error</td>
					</tr>
					<tr>
						<td>true, 1, on, yes, y, ja, j</td>
						<td>no error log,<br/>Task success</td>
						<td>no error log,<br/>set_state error</td>
					</tr>
					<tr>
						<td>all</td>
						<td>no error log,<br/>Task success</td>
						<td>no error log,<br/>set_state success</td>
					</tr>
				</table>
          </div>
        </note>

*         <note language="de">
          <div xmlns="http://www.w3.org/1999/xhtml">
            Schaltet Fehlermeldungen an oder aus, die aufgrund einer
            nicht existierenden Datei oder eines nicht existierenden Verzeichnisses ausgel�st werden,
            die/das mit den Parametern <code>file</code> bzw. <code>file_spec</code> spezifiziert wurde.
            <br/><br/>
            G�ltige Werte:
            <em>false, 0, off, no, n, nein, none</em>
            ,
            <em>true, 1, on, yes, y, ja, j</em>
            und
            <em>all</em>
            <br/><br/>
            Bei einer fehlerhaften Angabe in <code>file</code> bzw. <code>file_spec</code> ergibt sich folgendes Job-Verhalten:
            <table border="1" cellspacing="0">
					<tr>
						<th><code>GRACIOUS</code></th>
						<th><code>Standalone-Job</code></th>
						<th><code>Order-Job</code></th>
					</tr>
					<tr>
						<td>false, 0, off, no, n, nein, none</td>
						<td>error log,<br/>Task error</td>
						<td>error log,<br/>set_state error</td>
					</tr>
					<tr>
						<td>true, 1, on, yes, y, ja, j</td>
						<td>no error log,<br/>Task success</td>
						<td>no error log,<br/>set_state error</td>
					</tr>
					<tr>
						<td>all</td>
						<td>no error log,<br/>Task success</td>
						<td>no error log,<br/>set_state success</td>
					</tr>
				</table>
          </div>
        </note>

* \section SOSOptionGracious.java_intro_sec Introduction
*
* \section SOSOptionGracious.java_samples Some Samples
*
* \code
*   .... code goes here ...
* \endcode
*
* <p style="text-align:center">
* <br />---------------------------------------------------------------------------
* <br /> APL/Software GmbH - Berlin
* <br />##### generated by ClaviusXPress (http://www.sos-berlin.com) #########
* <br />---------------------------------------------------------------------------
* </p>
* \author KB
* @version $Id: SOSOptionGracious.java 14985 2011-08-21 08:51:34Z kb $28.08.2010
* \see reference
*
* Created on 28.08.2010 22:38:58
 */

public class SOSOptionGracious extends SOSOptionBoolean {
	private static final long	serialVersionUID	= -7171366137385744951L;
	protected static final String	conALL															= "all";
	protected static final String	conTRUE															= "true";

	/**
	 * Disables error messages caused by specified (source and/or target) but not existing files.
	 */
	public static final int						GRACIOUS		= 0x02;

	@SuppressWarnings("unused")
	private final String	conClassName	= "SOSOptionGracious";

	/**
	 * \brief SOSOptionGracious
	 *
	 * \details
	 *
	 * @param pPobjParent
	 * @param pPstrKey
	 * @param pPstrDescription
	 * @param pPstrValue
	 * @param pPstrDefaultValue
	 * @param pPflgIsMandatory
	 */
	public SOSOptionGracious(JSOptionsClass pPobjParent, String pPstrKey, String pPstrDescription, String pPstrValue, String pPstrDefaultValue,
			boolean pPflgIsMandatory) {
		super(pPobjParent, pPstrKey, pPstrDescription, pPstrValue, pPstrDefaultValue, pPflgIsMandatory);
		// TODO Auto-generated constructor stub
	}

	public boolean isGraciousAll() {
		return strValue != null && strValue.equalsIgnoreCase(conALL);
	}

	public boolean isGraciousTrue() {
		boolean flgResult = flgValue == true;
		return flgResult;
	}


}
