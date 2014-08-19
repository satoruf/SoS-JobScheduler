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
package com.sos.JSHelper.Basics;



/**
* \class JSJobUtilities
*
* \brief JSJobUtilities -
*
* \details
*
* \section JSJobUtilities.java_intro_sec Introduction
*
* \section JSJobUtilities.java_samples Some Samples
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
* @version $Id: JSJobUtilities.java 20145 2013-05-16 13:10:30Z kb $13.06.2010
* \see reference
*
* Created on 13.06.2010 10:44:14
 */

/**
 * @author KB
 *
 */
public interface JSJobUtilities {

	/**
	 *
	 * \brief myReplaceAll
	 *
	 * \details
	 *
	 * \return String
	 *
	 * @param source
	 * @param what
	 * @param replacement
	 * @return
	 */
	public String myReplaceAll(String source, String what, String replacement) ;
	/**
	 *
	 * \brief replaceSchedulerVars
	 *
	 * \details
	 *
	 * \return String - the modified String
	 *
	 * @param isWindows
	 * @param pstrString2Modify
	 */
	public String replaceSchedulerVars(boolean isWindows, final String pstrString2Modify);

	/**
	 *
	 * \brief setJSParam
	 *
	 * \details
	 *
	 *
	 * @param pstrKey
	 * @param pstrValue
	 * @return
	 */
	public void setJSParam(final String pstrKey, final String pstrValue);
	public void setJSParam(final String pstrKey, final StringBuffer pstrValue);
 
	/**
	 *
	 * \brief getCurrentNodeName
	 *
	 * \details
	 * Returns the the Name of the actual processed Node in a JobChain, if
	 * a Jobchain is to be executed.
	 *
	 * \return String
	 *
	 */
    public String getCurrentNodeName ();

	public void setJSJobUtilites (JSJobUtilities pobjJSJobUtilities);
//	/**
//	 *
//	 * \brief setParameters
//	 *
//	 * \details
//	 *
//	 * \return void
//	 *
//	 * @param pVariableSet
//	 */
//	public void setParameters(Variable_set pVariableSet);

	public void setStateText(final String pstrStateText);

	public void setCC (final int pintCC);
}