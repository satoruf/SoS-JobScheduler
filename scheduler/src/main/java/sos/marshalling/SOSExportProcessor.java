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
package sos.marshalling;

import java.io.File;
import java.util.StringTokenizer;

import sos.connection.SOSConnection;
import sos.util.SOSArguments;
import sos.marshalling.SOSExport;
import sos.util.SOSStandardLogger;

/**
 * Title: SOSExportProcessor<br>
 * Description: Kommandozeilentool zum Exportieren von Daten einer Applikation im XML-Format<br>
 * 
 * Copyright: Copyright (c) 2005<br>
 * Company: SOS Berlin GmbH<br>
 * @author <a href="mailto:robert.ehrlich@sos-berlin.com">Robert Ehrlich</a>
 * Resource sos.connection.jar sos.util.jar
 * @version $Id$
 */
public class SOSExportProcessor {

    private SOSConnection _sosConnection = null;

    private SOSStandardLogger _sosLogger = null;

    private File _configFile = null;

    private File _logFile = null;

    private int _logLevel = 0;

    private File _outputFile = null;

    private String _tableNames = null;

    private String _executeQuery = null;

    private String _keys = null;

    private boolean enableTableParametr = true;

    private boolean enableExecuteParametr = true;

    /**
     * Konstruktor
     * 
     * @param configFile
     *            Datei, in der die Zugangsdaten zur Datenbank enthalten sind
     * @param logFile
     *            Name der Protokolldatei
     * @param logLevel
     *            Log Level f�r die Protokolldatei
     * @param outputFile
     *            Name der XML-Datei f�r Export
     * @param tableNames
     *            Tabellennamen f�r den Export. <br>
     *            Es werden alle Daten der jeweiligen Tabelle exportiert <br>
     *            Mehrere Tabellen durch + Zeichen getrennt.
     * @param executeQuery
     *            eigene SQL-Statement f�r eine Tabelle angeben. <br>
     *            Mu� in doppelten Hochkommas angegeben werden
     * @param keys
     *            Schl�sselfelder f�r eine bzw mehreren Tabellen. <br>
     *            Wird im Zusammenhang mit dem Parameter tableNames
     *            ber�cksichtigt <br>
     *            Schl�sselfelder sind wichtig, wenn eine Tabelle CLOB bzw BLOB
     *            enth�lt. <br>
     *            mehrere Schl�ssel f�r eine Tabellen durch Komma getrennt <br>
     *            f�r mehreren Tabellen durch + Zeichen getrennt <br>
     *            f�r mehreren Tabellen : die Reihenfolge wie bei tableNames
     * @throws Exception
     */
    public SOSExportProcessor(File configFile, File logFile, int logLevel,
            File outputFile, String tableNames, String executeQuery, String keys)
            throws Exception {

        if (configFile == null)
                throw new NullPointerException(
                        "Export: Parameter config == null!");
        if (outputFile == null)
                throw new NullPointerException(
                        "Export: Parameter output == null!");

        try {
            _configFile = configFile;
            _logFile = logFile;
            _logLevel = logLevel;
            _outputFile = outputFile;
            _tableNames = tableNames;
            _executeQuery = executeQuery;
            _keys = keys;

            if (_configFile != null && _configFile.getName().length() > 0
                    && !_configFile.exists()) { throw new Exception(
                    "configuration file not found: " + _configFile); }

            if ((_tableNames != null && !tableNames.equals(""))
                    && (_executeQuery != null && !_executeQuery.equals(""))) { throw new Exception(
                    "-tables and -execute may not be indicated together"); }

            if (_logLevel != 0 && _logFile.toString().equals("")) { throw new Exception(
                    "log file is not defined"); }

        } catch (Exception e) {
            throw new Exception("error in SOSExportProcessor: "
                    + e.getMessage());
        }
    }

    /**
     * Konstruktor
     * 
     * wird aufgerufen, um die Programm USAGE anzuzeigen
     *  
     */
    public SOSExportProcessor() {
        System.out.println("Syntax");
        System.out.println("Optionen :");
        System.out.println("        -config     Namen der Konfigurationsdatei f�r die DB Verbindung angeben.");
        System.out.println("                    Default : sos_settings.ini");
        System.out.println("        -output     Namen der Export XML-Datei angeben.");
        System.out.println("                    Default : sos_export.xml ");
        System.out.println("        -tables     Tabellennamen f�r den Export.");
        System.out.println("                    Es werden alle Daten der jeweiligen Tabelle exportiert.");
        System.out.println("                    Mehrere Tabellen durch + Zeichen getrennt");
        System.out.println("        -keys    	Schl�sselfelder f�r eine bzw mehreren Tabellen angeben.");
        System.out.println("           			Wird im Zusammenhang mit der Option -tables ber�cksichtigt.");
        System.out.println("                    Schl�sselfelder sind wichtig, wenn eine Tabelle CLOB bzw BLOB enth�lt.");
        System.out.println("                    mehrere Schl�ssel f�r eine Tabelle - durch Komma getrennt.");
        System.out.println("                    f�r mehreren Tabellen durch + Zeichen getrennt.");
        System.out.println("                    f�r mehreren Tabellen : die Reihenfolge wie bei -tables.");
        System.out.println("        -execute    eigene SQL-Statement f�r eine Tabelle angeben.");
        System.out.println("                    SQL-Statement in doppelten Hochkommas.");
        System.out.println("        -log        Namen der Log-Datei angeben.");
        System.out.println("                    Default : sos_export.log");
        System.out.println("        -log-level  Loglevel angeben.");
        System.out.println("                    Default : 0  keine Log-Datei schreiben");
        System.out.println("");
        System.out.println("");
        System.out.println("Notiz : -execute und -tables d�rfen nicht zusammen angegeben werden.");
        System.out.println("        eine von beiden Optionen muss angegeben sein");
        System.out.println("");
        System.out.println("");
        System.out.println("Beispiel 1 : alle Daten der Tabelle t1 exportieren und in die default Log-Datei loggen");
        System.out.println("         -config=config/sos_settings.ini -tables=t1 -log-level=9");
        System.out.println("");
        System.out.println("Beispiel 2 : wie Beispiel 1 + Schl�sselfeld ID f�r die Tabelle t1 definieren");
        System.out.println("         -config=config/sos_settings.ini -tables=t1 -keys=ID -log-level=9");
        System.out.println("");
        System.out.println("Beispiel 3 : alle Daten der Tabellen t1 und t2 ohne zu loggen exportieren");
        System.out.println("         -config=config/sos_settings.ini -tables=t1+t2");
        System.out.println("");
        System.out.println("Beispiel 4 : wie Beispiel 3 + Schl�sselfelder T1_ID und T1_NAME f�r die Tabelle t1 und Schl�sselfeld T2_ID f�r die Tabelle t2 definieren");
        System.out.println("         -config=config/sos_settings.ini -tables=t1+t2 -keys=T1_ID,T1_NAME+T2_ID");
        System.out.println("");
        System.out.println("Beispiel 5 : eigene SQL-Statement f�r die Tabelle t1 definieren");
        System.out.println("         -config=config/sos_settings.ini -execute=\"select * from t1 where ID=1\"");

    }

    /**
     * Export anhand der registrierten Abfragen ausf&uuml;hren
     * 
     * @throws Exception
     */
    public void doExport() throws Exception {

        try {

            if (this.isEnableTableParametr() && this.isEnableExecuteParametr()) {
                if ((_tableNames == null || _tableNames.equals(""))
                        && (_executeQuery == null || _executeQuery.equals(""))) { throw new Exception(
                        "undefined operation for export. Check please input for your -tables or -execute arguments"); }
            }

            if (_logLevel == 0) {
                _sosLogger = new SOSStandardLogger(SOSStandardLogger.DEBUG);
            } else {
                _sosLogger = new SOSStandardLogger(_logFile.toString(),
                        _logLevel);
            }

            _sosConnection = SOSConnection.createInstance(_configFile
                    .toString(), _sosLogger);
            _sosConnection.connect();

            SOSExport export = new SOSExport(_sosConnection, _outputFile
                    .toString(), "EXPORT", _sosLogger);

            prepareExport(export);

            export.doExport();
            System.out.println("");
            System.out.println("Export erfolgreich beendet.");

        } catch (Exception e) {
            throw new Exception("error in SOSExportProcessor: "
                    + e.getMessage());
        } finally {
            try {
                if (_sosConnection != null) {
                    _sosConnection.disconnect();
                }
            } catch (Exception e) {
            }
        }

    }

    /**
     * Export vorbereiten.<br>
     * Die Methode setzt Tabellennamen, Schl�sselfelder usw f�r den Export. 
     * 
     * @param export
     *            SOSExport Objekt
     * @throws Exception
     */
    public void prepareExport(SOSExport export) throws Exception {

        String keys = "";
        String[] tablesKeys = {};

        if (_keys != null && !_keys.trim().equals("")) {
            keys = _keys.toUpperCase();
            tablesKeys = keys.split("\\+");
        }

        if (!_tableNames.equals("")) {
            StringTokenizer tables = new StringTokenizer(_tableNames, "+");

            int i = 0;

            while (tables.hasMoreTokens()) {
                String table = tables.nextToken().toUpperCase();
                String key = "";

                if (!table.equals("")) {
                    if (tablesKeys != null && tablesKeys.length != 0) {
                        try {
                            key = tablesKeys[i];
                        } catch (Exception e) {
                        }
                    }
                    export.add(table, key, "select * from " + table, null, i);
                    i++;
                }
            }
        } else if (!_executeQuery.equals("")) {
            StringTokenizer st = new StringTokenizer(_executeQuery, " ");

            String table = "";

            while (st.hasMoreTokens()) {
                String token = st.nextToken().toUpperCase();
                if (token.equals("FROM")) {
                    table = st.nextToken().toUpperCase();
                    break;
                }
            }
            export.add(table, keys, _executeQuery, null, 0);
        }
        
    }

    /**
     * Programm ausf�hren<br>
     * 
     * @param args		Programmargumente<br><br>
     * 
     * Mit dem Argument "?" bzw "help"  kann mann sich Programm Usage anzeigen lassen.<br><br> 
     * 
     * Weitere m�gliche Argumente<br>
     *  	-config		Datei, in der die Zugangsdaten zur Datenbank enthalten sind<br>
     * 					Default	: sos_settings.ini<br>
     *  	-log        Name der Protokolldatei<br>
     * 					Default : sos_export.log<br>
     *  	-log-level  Log Level f�r die Protokolldatei<br>
     * 					Default : 0   keine Protokollierung<br>
     *  	-output     Name der XML-Datei f�r Export<br>
     * 					Default	: sos_export.xml<br>
     * 		-tables		Tabellennamen f�r den Export. <br>
     *            		Es werden alle Daten der jeweiligen Tabelle exportiert <br>
     *            		Mehrere Tabellen durch + Zeichen getrennt.<br>
     * 					Default : Leerstring<br>
     *  	-execute    eigene SQL-Statement f�r eine Tabelle angeben. <br>
     *            		Mu� in doppelten Hochkommas angegeben werden<br>
     * 					Default : Leerstring<br>
     * 		-keys       Schl�sselfelder f�r eine bzw mehreren Tabellen. <br>
     *            		Wird im Zusammenhang mit dem Argument -tables ber�cksichtigt <br>
     *            		Schl�sselfelder sind wichtig, wenn eine Tabelle CLOB bzw BLOB enth�lt. <br>
     *            		mehrere Schl�ssel f�r eine Tabelle -  durch Komma getrennt <br>
     *            		f�r mehreren Tabellen durch + Zeichen getrennt <br>
     *            		f�r mehreren Tabellen : die Reihenfolge wie bei -tables<br>
     * 					Default : Leerstring<br><br>
     * -execute und -tables d�rfen nicht zusammen angegeben werden.<br>
     * eine von beiden Optionen muss angegeben sein<br>
     * 
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        boolean isExport = true;

        if (args.length == 1) {
            String argument = args[0].toLowerCase().trim();
            if (argument.equals("?") || argument.equals("help")) {
                SOSExportProcessor processor = new SOSExportProcessor();
                isExport = false;
            }
        }

        if (isExport) {
            SOSArguments arguments = new SOSArguments(args);

            SOSExportProcessor processor = new SOSExportProcessor(new File(
                    arguments.as_string("-config=", "sos_settings.ini")),
                    new File(arguments.as_string("-log=", "sos_export.log")),
                    arguments.as_int("-log-level=", 0), 
                    new File(arguments.as_string("-output=", "sos_export.xml")),
                    new String(arguments.as_string("-tables=", "")),
                    new String(arguments.as_string("-execute=", "")),
                    new String(arguments.as_string("-keys=", "")));

            arguments.check_all_used();
            processor.doExport();
        }
    }

    
    /**
     * Liefert den Zustand : Pr�fen nach -execute Option
     * 
     * @return Liefert den Zustand : Pr�fen nach -execute Option oder nicht.
     */
    public boolean isEnableExecuteParametr() {
        return enableExecuteParametr;
    }

    /**
     * Setzt Zustand : Pr�fen nach -execute Option
     * 
     * @param enableExecuteParametr  Pr�fen nach -execute Option  oder nicht
     *            
     */
    public void setEnableExecuteParametr(boolean enableExecuteParametr) {
        this.enableExecuteParametr = enableExecuteParametr;
    }

    /**
     * Liefert den Zustand : Pr�fen nach -tables Option
     * 
     * @return Liefert den Zustand : Pr�fen nach -tables Option oder nicht.
     */
    public boolean isEnableTableParametr() {
        return enableTableParametr;
    }

    /**
     * Setzt Zustand : Pr�fen nach -tables Option
     * 
     * @param enableTableParametr
     *            Pr�fen nach -tables Option  oder nicht.
     */
    public void setEnableTableParametr(boolean enableTableParametr) {
        this.enableTableParametr = enableTableParametr;
    }
}
