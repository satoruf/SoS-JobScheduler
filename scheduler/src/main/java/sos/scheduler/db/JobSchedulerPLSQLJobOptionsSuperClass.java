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
package sos.scheduler.db;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.sos.JSHelper.Annotations.JSOptionClass;
import com.sos.JSHelper.Annotations.JSOptionDefinition;
import com.sos.JSHelper.Exceptions.JSExceptionMandatoryOptionMissing;
import com.sos.JSHelper.Listener.JSListener;
import com.sos.JSHelper.Options.JSOptionsClass;
import com.sos.JSHelper.Options.SOSOptionCommandString;
import com.sos.JSHelper.Options.SOSOptionRegExp;
import com.sos.JSHelper.Options.SOSOptionString;

/**
 * \class 		JobSchedulerPLSQLJobOptionsSuperClass - Launch Database Statement
 *
 * \brief 
 * An Options-Super-Class with all Options. This Class will be extended by the "real" Options-class (\see JobSchedulerPLSQLJobOptions.
 * The "real" Option class will hold all the things, which are normaly overwritten at a new generation
 * of the super-class.
 *
 *

 *
 * see \see R:\backup\sos\java\development\com.sos.scheduler\src\sos\scheduler\jobdoc\JobSchedulerPLSQLJob.xml for (more) details.
 * 
 * \verbatim ;
 * mechanicaly created by  from http://www.sos-berlin.com at 20120905153339 
 * \endverbatim
 * \section OptionsTable Tabelle der vorhandenen Optionen
 * 
 * Tabelle mit allen Optionen
 * 
 * MethodName
 * Title
 * Setting
 * Description
 * IsMandatory
 * DataType
 * InitialValue
 * TestValue
 * 
 * 
 *
 * \section TestData Eine Hilfe zum Erzeugen einer HashMap mit Testdaten
 *
 * Die folgenden Methode kann verwendet werden, um f�r einen Test eine HashMap
 * mit sinnvollen Werten f�r die einzelnen Optionen zu erzeugen.
 *
 * \verbatim
 private HashMap <String, String> SetJobSchedulerSSHJobOptions (HashMap <String, String> pobjHM) {
	pobjHM.put ("		JobSchedulerPLSQLJobOptionsSuperClass.auth_file", "test");  // This parameter specifies the path and name of a user's pr
		return pobjHM;
  }  //  private void SetJobSchedulerSSHJobOptions (HashMap <String, String> pobjHM)
 * \endverbatim
 */
@JSOptionClass(name = "JobSchedulerPLSQLJobOptionsSuperClass", description = "JobSchedulerPLSQLJobOptionsSuperClass")
public class JobSchedulerPLSQLJobOptionsSuperClass extends JSOptionsClass {
	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;
	private final String			conClassName		= "JobSchedulerPLSQLJobOptionsSuperClass";
	@SuppressWarnings("unused")
	private static Logger			logger				= Logger.getLogger(JobSchedulerPLSQLJobOptionsSuperClass.class);

	/**
	 * \var command : Database Commands for the Job. It is possible to define m
	 * Database Commands for the Job. It is possible to define more than one instruction in the COMMAND field. Such instructions are then carried out in the order in which they are written and must be separated by a semicolon and a subsequent new line. Parameters can be replaced in database commands. This is done by the addition of a variable in the form �{param} at any given point in a command. This variable is then given the value of the instruction parameter with the name [param] before execution.
	 *
	 */
	@JSOptionDefinition(name = "command", description = "Database Commands for the Job. It is possible to define m", key = "command", type = "SOSOptionString", mandatory = false)
	public SOSOptionCommandString	command				= new SOSOptionCommandString(this, conClassName + ".command", // HashMap-Key
																"Database Commands for the Job. It is possible to define m", // Titel
																"", // InitValue
																"", // DefaultValue
																false // isMandatory
														);

	/**
	 * \brief getcommand : Database Commands for the Job. It is possible to define m
	 * 
	 * \details
	 * Database Commands for the Job. It is possible to define more than one instruction in the COMMAND field. Such instructions are then carried out in the order in which they are written and must be separated by a semicolon and a subsequent new line. Parameters can be replaced in database commands. This is done by the addition of a variable in the form �{param} at any given point in a command. This variable is then given the value of the instruction parameter with the name [param] before execution.
	 *
	 * \return Database Commands for the Job. It is possible to define m
	 *
	 */
	public SOSOptionCommandString getcommand() {
		return command;
	}

	/**
	 * \brief setcommand : Database Commands for the Job. It is possible to define m
	 * 
	 * \details
	 * Database Commands for the Job. It is possible to define more than one instruction in the COMMAND field. Such instructions are then carried out in the order in which they are written and must be separated by a semicolon and a subsequent new line. Parameters can be replaced in database commands. This is done by the addition of a variable in the form �{param} at any given point in a command. This variable is then given the value of the instruction parameter with the name [param] before execution.
	 *
	 * @param command : Database Commands for the Job. It is possible to define m
	 */
	public void setcommand(final SOSOptionCommandString p_command) {
		command = p_command;
	}

	/**
	 * \var variable_parser_reg_expr : variable_parser_reg_expr
	 *
	 *
	 */
	@JSOptionDefinition(name = "variable_parser_reg_expr", description = "variable_parser_reg_expr", key = "variable_parser_reg_expr", type = "SOSOptionRegExp", mandatory = false)
	public SOSOptionRegExp	variable_parser_reg_expr	= new SOSOptionRegExp(this, conClassName + ".variable_parser_reg_expr", // HashMap-Key
																"variable_parser_reg_expr", // Titel
																"^SET\\s+([^\\s]+)\\s*IS\\s+(.*)$", // InitValue
																"^SET\\s+([^\\s]+)\\s*IS\\s+(.*)$", // DefaultValue
																false // isMandatory
														);

	/**
	 * \brief getvariable_parser_reg_expr : variable_parser_reg_expr
	 *
	 * \details
	 *
	 *
	 * \return variable_parser_reg_expr
	 *
	 */
	public SOSOptionRegExp getvariable_parser_reg_expr() {
		return variable_parser_reg_expr;
	}

	/**
	 * \brief setvariable_parser_reg_expr : variable_parser_reg_expr
	 *
	 * \details
	 *
	 *
	 * @param variable_parser_reg_expr : variable_parser_reg_expr
	 */
	public void setvariable_parser_reg_expr(final SOSOptionRegExp p_variable_parser_reg_expr) {
		variable_parser_reg_expr = p_variable_parser_reg_expr;
	}

	public SOSOptionRegExp	VariableParserRegExpr	= (SOSOptionRegExp) variable_parser_reg_expr.SetAlias(conClassName + ".VariableParserRegExpr");

	/**
	 * \var db_password : database password
	 * database password
	 *
	 */
	@JSOptionDefinition(name = "db_password", description = "database password", key = "db_password", type = "SOSOptionString", mandatory = false)
	public SOSOptionString	db_password				= new SOSOptionString(this, conClassName + ".db_password", // HashMap-Key
															"database password", // Titel
															" ", // InitValue
															" ", // DefaultValue
															false // isMandatory
													);

	/**
	 * \brief getdb_password : database password
	 * 
	 * \details
	 * database password
	 *
	 * \return database password
	 *
	 */
	public SOSOptionString getdb_password() {
		return db_password;
	}

	/**
	 * \brief setdb_password : database password
	 * 
	 * \details
	 * database password
	 *
	 * @param db_password : database password
	 */
	public void setdb_password(final SOSOptionString p_db_password) {
		db_password = p_db_password;
	}

	/**
	 * \var db_url : jdbc url (e.g. jdbc:oracle:thin:@localhost:1521:XE)
	 * jdbc url (e.g. jdbc:oracle:thin:@localhost:1521:XE)
	 *
	 */
	@JSOptionDefinition(name = "db_url", description = "jdbc url (e.g. jdbc:oracle:thin:@localhost:1521:XE)", key = "db_url", type = "SOSOptionString", mandatory = false)
	public SOSOptionString	db_url	= new SOSOptionString(this, conClassName + ".db_url", // HashMap-Key
											"jdbc url (e.g. jdbc:oracle:thin:@localhost:1521:XE)", // Titel
											" ", // InitValue
											" ", // DefaultValue
											false // isMandatory
									);

	/**
	 * \brief getdb_url : jdbc url (e.g. jdbc:oracle:thin:@localhost:1521:XE)
	 * 
	 * \details
	 * jdbc url (e.g. jdbc:oracle:thin:@localhost:1521:XE)
	 *
	 * \return jdbc url (e.g. jdbc:oracle:thin:@localhost:1521:XE)
	 *
	 */
	public SOSOptionString getdb_url() {
		return db_url;
	}

	/**
	 * \brief setdb_url : jdbc url (e.g. jdbc:oracle:thin:@localhost:1521:XE)
	 * 
	 * \details
	 * jdbc url (e.g. jdbc:oracle:thin:@localhost:1521:XE)
	 *
	 * @param db_url : jdbc url (e.g. jdbc:oracle:thin:@localhost:1521:XE)
	 */
	public void setdb_url(final SOSOptionString p_db_url) {
		db_url = p_db_url;
	}

	/**
	 * \var db_user : database user
	 * database user
	 *
	 */
	@JSOptionDefinition(name = "db_user", description = "database user", key = "db_user", type = "SOSOptionString", mandatory = false)
	public SOSOptionString	db_user	= new SOSOptionString(this, conClassName + ".db_user", // HashMap-Key
											"database user", // Titel
											" ", // InitValue
											" ", // DefaultValue
											false // isMandatory
									);

	/**
	 * \brief getdb_user : database user
	 * 
	 * \details
	 * database user
	 *
	 * \return database user
	 *
	 */
	public SOSOptionString getdb_user() {
		return db_user;
	}

	/**
	 * \brief setdb_user : database user
	 * 
	 * \details
	 * database user
	 *
	 * @param db_user : database user
	 */
	public void setdb_user(final SOSOptionString p_db_user) {
		db_user = p_db_user;
	}

	/**
	 * \var exec_returns_resultset : If stored procedures are called which return a result set
	 * If stored procedures are called which return a result set, this needs to be set to true in order to run the stored procedure as a query. This does not work with the SQL Server 2000 and 2005 jdbc drivers.
	 *
	 */
	@JSOptionDefinition(name = "exec_returns_resultset", description = "If stored procedures are called which return a result set", key = "exec_returns_resultset", type = "SOSOptionString", mandatory = false)
	public SOSOptionString	exec_returns_resultset	= new SOSOptionString(this, conClassName + ".exec_returns_resultset", // HashMap-Key
															"If stored procedures are called which return a result set", // Titel
															"false", // InitValue
															"false", // DefaultValue
															false // isMandatory
													);

	/**
	 * \brief getexec_returns_resultset : If stored procedures are called which return a result set
	 * 
	 * \details
	 * If stored procedures are called which return a result set, this needs to be set to true in order to run the stored procedure as a query. This does not work with the SQL Server 2000 and 2005 jdbc drivers.
	 *
	 * \return If stored procedures are called which return a result set
	 *
	 */
	public SOSOptionString getexec_returns_resultset() {
		return exec_returns_resultset;
	}

	/**
	 * \brief setexec_returns_resultset : If stored procedures are called which return a result set
	 * 
	 * \details
	 * If stored procedures are called which return a result set, this needs to be set to true in order to run the stored procedure as a query. This does not work with the SQL Server 2000 and 2005 jdbc drivers.
	 *
	 * @param exec_returns_resultset : If stored procedures are called which return a result set
	 */
	public void setexec_returns_resultset(final SOSOptionString p_exec_returns_resultset) {
		exec_returns_resultset = p_exec_returns_resultset;
	}

	/**
	 * \var resultset_as_parameters : false No output parameters are generated.
	 * false No output parameters are generated. true If set to true, the first row of the resultset will be set as order parameters (using the column names as parameter names). Example: The query "SELECT first_name, last_name, age FROM persons" produces the following result set: first_name last_name age John Doe 30 Hans Mustermann 33 Jean Dupont 56 The following name/value pairs will be generated as order parameters: first_name=John last_name=Doe age=30 name_value If set to name_value, the first two columns of the resultset will be turned into order parameters. The values of the first column will become parameter names, the values of the second column will become parameter values. Example: From the above resultset, the following name/value pairs will be generated as order parameters: John=Doe Hans=Mustermann Jean=Dupont
	 *
	 */
	@JSOptionDefinition(name = "resultset_as_parameters", description = "false No output parameters are generated.", key = "resultset_as_parameters", type = "SOSOptionString", mandatory = false)
	public SOSOptionString	resultset_as_parameters	= new SOSOptionString(this, conClassName + ".resultset_as_parameters", // HashMap-Key
															"false No output parameters are generated.", // Titel
															"false", // InitValue
															"false", // DefaultValue
															false // isMandatory
													);

	/**
	 * \brief getresultset_as_parameters : false No output parameters are generated.
	 * 
	 * \details
	 * false No output parameters are generated. true If set to true, the first row of the resultset will be set as order parameters (using the column names as parameter names). Example: The query "SELECT first_name, last_name, age FROM persons" produces the following result set: first_name last_name age John Doe 30 Hans Mustermann 33 Jean Dupont 56 The following name/value pairs will be generated as order parameters: first_name=John last_name=Doe age=30 name_value If set to name_value, the first two columns of the resultset will be turned into order parameters. The values of the first column will become parameter names, the values of the second column will become parameter values. Example: From the above resultset, the following name/value pairs will be generated as order parameters: John=Doe Hans=Mustermann Jean=Dupont
	 *
	 * \return false No output parameters are generated.
	 *
	 */
	public SOSOptionString getresultset_as_parameters() {
		return resultset_as_parameters;
	}

	/**
	 * \brief setresultset_as_parameters : false No output parameters are generated.
	 * 
	 * \details
	 * false No output parameters are generated. true If set to true, the first row of the resultset will be set as order parameters (using the column names as parameter names). Example: The query "SELECT first_name, last_name, age FROM persons" produces the following result set: first_name last_name age John Doe 30 Hans Mustermann 33 Jean Dupont 56 The following name/value pairs will be generated as order parameters: first_name=John last_name=Doe age=30 name_value If set to name_value, the first two columns of the resultset will be turned into order parameters. The values of the first column will become parameter names, the values of the second column will become parameter values. Example: From the above resultset, the following name/value pairs will be generated as order parameters: John=Doe Hans=Mustermann Jean=Dupont
	 *
	 * @param resultset_as_parameters : false No output parameters are generated.
	 */
	public void setresultset_as_parameters(final SOSOptionString p_resultset_as_parameters) {
		resultset_as_parameters = p_resultset_as_parameters;
	}

	/**
	 * \var resultset_as_warning : If set to true, a warning will be issued, if the statemen
	 * If set to true, a warning will be issued, if the statements produce a result
	 *
	 */
	@JSOptionDefinition(name = "resultset_as_warning", description = "If set to true, a warning will be issued, if the statemen", key = "resultset_as_warning", type = "SOSOptionString", mandatory = false)
	public SOSOptionString	resultset_as_warning	= new SOSOptionString(this, conClassName + ".resultset_as_warning", // HashMap-Key
															"If set to true, a warning will be issued, if the statemen", // Titel
															"false", // InitValue
															"false", // DefaultValue
															false // isMandatory
													);

	/**
	 * \brief getresultset_as_warning : If set to true, a warning will be issued, if the statemen
	 * 
	 * \details
	 * If set to true, a warning will be issued, if the statements produce a result
	 *
	 * \return If set to true, a warning will be issued, if the statemen
	 *
	 */
	public SOSOptionString getresultset_as_warning() {
		return resultset_as_warning;
	}

	/**
	 * \brief setresultset_as_warning : If set to true, a warning will be issued, if the statemen
	 * 
	 * \details
	 * If set to true, a warning will be issued, if the statements produce a result
	 *
	 * @param resultset_as_warning : If set to true, a warning will be issued, if the statemen
	 */
	public void setresultset_as_warning(final SOSOptionString p_resultset_as_warning) {
		resultset_as_warning = p_resultset_as_warning;
	}

	public JobSchedulerPLSQLJobOptionsSuperClass() {
		objParentClass = this.getClass();
	} // public JobSchedulerPLSQLJobOptionsSuperClass

	public JobSchedulerPLSQLJobOptionsSuperClass(final JSListener pobjListener) {
		this();
		this.registerMessageListener(pobjListener);
	} // public JobSchedulerPLSQLJobOptionsSuperClass

	//

	public JobSchedulerPLSQLJobOptionsSuperClass(final HashMap<String, String> JSSettings) throws Exception {
		this();
		this.setAllOptions(JSSettings);
	} // public JobSchedulerPLSQLJobOptionsSuperClass (HashMap JSSettings)

	/**
	 * \brief getAllOptionsAsString - liefert die Werte und Beschreibung aller
	 * Optionen als String
	 *
	 * \details
	 * 
	 * \see toString 
	 * \see toOut
	 */
	private String getAllOptionsAsString() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::getAllOptionsAsString";
		String strT = conClassName + "\n";
		final StringBuffer strBuffer = new StringBuffer();
		// strT += IterateAllDataElementsByAnnotation(objParentClass, this,
		// JSOptionsClass.IterationTypes.toString, strBuffer);
		// strT += IterateAllDataElementsByAnnotation(objParentClass, this, 13,
		// strBuffer);
		strT += this.toString(); // fix
		//
		return strT;
	} // private String getAllOptionsAsString ()

	/**
	 * \brief setAllOptions - �bernimmt die OptionenWerte aus der HashMap
	 *
	 * \details In der als Parameter anzugebenden HashMap sind Schl�ssel (Name)
	 * und Wert der jeweiligen Option als Paar angegeben. Ein Beispiel f�r den
	 * Aufbau einer solchen HashMap findet sich in der Beschreibung dieser
	 * Klasse (\ref TestData "setJobSchedulerSSHJobOptions"). In dieser Routine
	 * werden die Schl�ssel analysiert und, falls gefunden, werden die
	 * dazugeh�rigen Werte den Properties dieser Klasse zugewiesen.
	 *
	 * Nicht bekannte Schl�ssel werden ignoriert.
	 *
	 * \see JSOptionsClass::getItem
	 *
	 * @param pobjJSSettings
	 * @throws Exception
	 */
	@Override
	public void setAllOptions(final HashMap<String, String> pobjJSSettings) throws Exception {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::setAllOptions";
		flgSetAllOptions = true;
		objSettings = pobjJSSettings;
		super.Settings(objSettings);
		super.setAllOptions(pobjJSSettings);
		flgSetAllOptions = false;
	} // public void setAllOptions (HashMap <String, String> JSSettings)

	/**
	 * \brief CheckMandatory - pr�ft alle Muss-Optionen auf Werte
	 *
	 * \details
	 * @throws Exception
	 *
	 * @throws Exception
	 * - wird ausgel�st, wenn eine mandatory-Option keinen Wert hat
	 */
	@Override
	public void CheckMandatory() throws JSExceptionMandatoryOptionMissing //
			, Exception {
		try {
			super.CheckMandatory();
		}
		catch (Exception e) {
			throw new JSExceptionMandatoryOptionMissing(e.toString());
		}
	} // public void CheckMandatory ()

	/**
	 *
	 * \brief CommandLineArgs - �bernehmen der Options/Settings aus der
	 * Kommandozeile
	 *
	 * \details Die in der Kommandozeile beim Starten der Applikation
	 * angegebenen Parameter werden hier in die HashMap �bertragen und danach
	 * den Optionen als Wert zugewiesen.
	 *
	 * \return void
	 *
	 * @param pstrArgs
	 * @throws Exception
	 */
	@Override
	public void CommandLineArgs(final String[] pstrArgs) throws Exception {
		super.CommandLineArgs(pstrArgs);
		this.setAllOptions(super.objSettings);
	}
} // public class JobSchedulerPLSQLJobOptionsSuperClass
