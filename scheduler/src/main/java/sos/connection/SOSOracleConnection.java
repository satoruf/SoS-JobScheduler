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
package sos.connection;

import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.sql.Driver;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.Types;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.io.StringReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;

import java.lang.NullPointerException;

import oracle.sql.BLOB;
import oracle.sql.CLOB;

import sos.util.SOSClassUtil;
import sos.util.SOSLogger;
import sos.util.SOSString;

/**
 * <p>Title: </p> 
 * <p>Description: Implementation of SOSConnection for Oracle</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: SOS GmbH</p>
 * @author <a href="mailto:ghassan.beydoun@sos-berlin.com">Ghassan Beydoun</a>
 * @resource ojdbc14.jar sos.util.jar
 * @version $Id: SOSOracleConnection.java 7254 2010-10-26 08:15:07Z al $
 */

public class SOSOracleConnection extends sos.connection.SOSConnection implements SequenceReader {

    /** Replacements for %lcase, %ucase, %now, %updlock */
    private static final String replacement[] = { "LOWER", "UPPER", "SYSDATE",
            "FOR UPDATE"};

    private static final SOSConnectionVersionLimiter versionLimiter;
    
    // initialize versionLimiter
    static {
    	versionLimiter = new SOSConnectionVersionLimiter();
    	versionLimiter.addSupportedVersion(8,1);
    	versionLimiter.addSupportedVersion(9,2);
    	versionLimiter.setMinSupportedVersion(10,0);
    	versionLimiter.setMaxSupportedVersion(10,2); 
    }
    
    public SOSOracleConnection(Connection connection, SOSLogger logger)
            throws Exception {
        super(connection, logger);
    }

    public SOSOracleConnection(Connection connection) throws Exception {
        super(connection);
    }

    public SOSOracleConnection(String configFileName, SOSLogger logger)
            throws Exception {

        super(configFileName, logger);
    }

    public SOSOracleConnection(String configFileName) throws Exception {
        super(configFileName);
    }

    public SOSOracleConnection(String driver, String url, String dbuser,
            String dbpassword, SOSLogger logger) throws Exception {
        super(driver, url, dbuser, dbpassword, logger);
    }

    public SOSOracleConnection(String driver, String url, String dbuser,
            String dbpassword) throws Exception {
        super(driver, url, dbuser, dbpassword);
    }

    public void connect() throws Exception {

        Properties properties = new Properties();
        logger.debug6("calling " + SOSClassUtil.getMethodName());

        if (SOSString.isEmpty(url))
                throw new Exception(SOSClassUtil.getMethodName()
                        + ": missing database url.");
        if (SOSString.isEmpty(driver))
                throw new Exception(SOSClassUtil.getMethodName()
                        + ": missing database driver.");
        if (SOSString.isEmpty(dbuser))
                throw new Exception(SOSClassUtil.getMethodName()
                        + ": missing database user.");
        if (SOSString.isEmpty(dbpassword))
                throw new Exception(SOSClassUtil.getMethodName()
                        + ": missing database password.");

        properties.setProperty("user", dbuser);
        properties.setProperty("password", dbpassword);

        Driver driver = (Driver) Class.forName(this.driver).newInstance();
        connection = driver.connect(url, properties);
        if (connection == null)
                throw new Exception("can't connect to database");
        logger.debug6(".. successfully connected to " + url);
        
        versionLimiter.check(this, logger);
        prepare(connection);

    }

    public void prepare(Connection connection) throws Exception {

        logger.debug6("calling " + SOSClassUtil.getMethodName());
        Statement stmt = null;

        try {
            if (connection == null)
                    throw new Exception("can't connect to database");

            connection.setAutoCommit(false);
            connection.rollback();
            stmt = connection.createStatement();
            String NLS_NUMERIC_CHARACTERS = "ALTER SESSION SET NLS_NUMERIC_CHARACTERS='.,'";
            String NLS_DATE_FORMAT = "ALTER SESSION SET NLS_DATE_FORMAT='YYYY-MM-DD HH24:MI:SS'";
            String NLS_SORT = "ALTER SESSION SET NLS_SORT='BINARY'";
            stmt.addBatch(NLS_NUMERIC_CHARACTERS);
            stmt.addBatch(NLS_DATE_FORMAT);
            stmt.addBatch(NLS_SORT);
            stmt.executeBatch();
            logger.debug9(".. " + NLS_NUMERIC_CHARACTERS + " successfully set.");
            logger.debug9(".. " + NLS_DATE_FORMAT + " successfully set.");
            logger.debug9(".. " + NLS_SORT + " successfully set.");
            CallableStatement enable_stmt = null;
            enable_stmt = this.getConnection().prepareCall(
                    "begin dbms_output.enable(10000); end;");
            enable_stmt.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * updates a blob
     * 
     * @param tableName
     *            taget table
     * @param columnName
     *            target column
     * @param data
     *            blob data byte-array
     * @param condition
     *            condition(s) (excluding WHERE) of the SQL statement
     * @return number bytes written
     * @throws java.lang.Exception
     * @see #getBlob( String )
     * @see #getBlob( String, String )
     */
    public long updateBlob(String tableName, String columnName, byte[] data,
            String condition) throws Exception {
        long totalBytesRead = 0;
        Statement stmt = null;
        StringBuffer query = null;
        String theQuery = null;
        int chunkSize = 0;
        ByteArrayInputStream in = null;
        ResultSet rs = null;
        OutputStream out = null;

        try {

            logger.debug6("calling " + SOSClassUtil.getMethodName());

            if (connection == null)
                    throw new Exception(
                            SOSClassUtil.getMethodName()
                                    + ": sorry, there is no successful connection established."
                                    + " may be the connect method was not called");

            if (SOSString.isEmpty(tableName))
                    throw new Exception("tableName is null.");
            if (SOSString.isEmpty(columnName))
                    throw new Exception("columnName is null.");
            if (data.length <= 0) throw new Exception("data has no value.");

            in = new ByteArrayInputStream(data);

            connection.setAutoCommit(false);

            query = new StringBuffer("UPDATE ");

            if (tableNameUpperCase)
                query.append(tableName.toUpperCase());
            else
                query.append(tableName);

            if (fieldNameUpperCase) {
                query.append(" SET \"");
                query.append(columnName.toUpperCase());
                query.append("\" = empty_blob() ");
            } else {
                query.append(" SET ");
                query.append(columnName);
                query.append(" = empty_blob() ");
            }

            if (!SOSString.isEmpty(condition)) {
                condition = " WHERE " + condition;
            } else {
                condition = "";
            }
            query.append(condition);
            theQuery = normalizeStatement(query.toString(), replacement);
            logger.debug9(".. " + theQuery);

            stmt = connection.createStatement();
            stmt.execute(theQuery);

            stmt.close();
            stmt = null;

            query = new StringBuffer("SELECT ");

            if (fieldNameUpperCase) {
                query.append("\"");
                query.append(columnName.toUpperCase());
                query.append("\"");
            } else
                query.append(columnName);

            query.append(" FROM ");

            if (tableNameUpperCase)
                query.append(tableName.toUpperCase());
            else
                query.append(tableName);

            query.append(condition);

            query.append(" FOR UPDATE");

            theQuery = normalizeStatement(query.toString(), replacement);
            logger.debug9(".. " + theQuery);

            stmt = connection.createStatement();
            rs = stmt.executeQuery(theQuery);
            if (rs.next()) {

                BLOB blob = (oracle.sql.BLOB) rs.getBlob(1);

                out = blob.getBinaryOutputStream();
                chunkSize = blob.getChunkSize();
                logger.debug9(".. current chunk size: " + chunkSize);
                byte[] buffer = new byte[chunkSize];

                int bytesRead = 0;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }
            } else {
                throw new Exception("failed: no blob found: " + theQuery);
            }

            logger.debug6(".. blob successfully updated.");
        } catch (Exception e) {
            if (connection != null) connection.rollback();
            throw e;
        } finally {

            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
            }

            try {
                if (rs != null) rs.close();
            } catch (Exception e) {
            }

            try {
                if (out != null) out.close();
            } catch (Exception e) {
            }
            try {
                if (in != null) in.close();
            } catch (Exception e) {
            }

        }
        return totalBytesRead;

    }

    /**
     * updates a blob from a file
     * 
     * @param tableName
     *            taget table
     * @param columnName
     *            target column
     * @param file
     *            source of the blob data
     * @param condition
     *            condition(s) (excluding WHERE) of the SQL statement
     * @return number of bytes written
     * @throws java.lang.Exception
     */
    public long updateBlob(String tableName, String columnName,
            String fileName, String condition) throws Exception {

        long totalBytesRead = 0;
        OutputStream out = null;
        InputStream in = null;
        StringBuffer query = null;
        Statement stmt = null;
        ResultSet rs = null;
        String theQuery = null;
        int chunkSize = 0;

        try {

            logger.debug6("calling " + SOSClassUtil.getMethodName());

            if (connection == null)
                    throw new Exception(
                            SOSClassUtil.getMethodName()
                                    + ": sorry, there is no successful connection established."
                                    + " may be the connect method was not called");

            if (SOSString.isEmpty(tableName))
                    throw new Exception("tableName is null.");
            if (SOSString.isEmpty(columnName))
                    throw new Exception("columnName is null.");
            if (SOSString.isEmpty(fileName))
                    throw new Exception("fileName is null.");

            File file = new File(fileName);
            if (!file.exists()) throw new Exception("file doesn't exist.");

            in = new FileInputStream(file);

            connection.setAutoCommit(false);

            query = new StringBuffer("UPDATE ");

            if (tableNameUpperCase)
                query.append(tableName.toUpperCase());
            else
                query.append(tableName);

            if (fieldNameUpperCase) {
                query.append(" SET \"");
                query.append(columnName.toUpperCase());
                query.append("\" = empty_blob() ");
            } else {
                query.append(" SET ");
                query.append(columnName);
                query.append(" = empty_blob() ");
            }

            if (!SOSString.isEmpty(condition)) {
                condition = " WHERE " + condition;
            } else {
                condition = "";
            }
            query.append(condition);
            theQuery = normalizeStatement(query.toString(), replacement);
            logger.debug9(".. " + theQuery);

            stmt = connection.createStatement();
            stmt.execute(theQuery);

            query = new StringBuffer("SELECT ");

            if (fieldNameUpperCase) {
                query.append("\"");
                query.append(columnName.toUpperCase());
                query.append("\"");
            } else
                query.append(columnName);

            query.append(" FROM ");

            if (tableNameUpperCase)
                query.append(tableName.toUpperCase());
            else
                query.append(tableName);
            query.append(" ");
            query.append(condition);
            query.append(" FOR UPDATE");

            theQuery = normalizeStatement(query.toString(), replacement);
            logger.debug9(".. select the BLOB: " + theQuery);

            rs = stmt.executeQuery(theQuery);
            if (rs.next()) {
                BLOB blob = (oracle.sql.BLOB) rs.getBlob(1);
                out = blob.getBinaryOutputStream();
                chunkSize = blob.getChunkSize();
                logger.debug9(".. current chunk size: " + chunkSize);
                byte[] buffer = new byte[chunkSize];
                int bytesRead = 0;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }
            } else {
                throw new Exception("failed: no blob found: " + theQuery);
            }

            logger.debug6(".. blob successfully updated.");
        } catch (Exception e) {
            if (connection != null) connection.rollback();
            throw new Exception(SOSClassUtil.getMethodName() + ":"
                    + e.toString(), e);
        } finally {

            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
            }

            try {
                if (rs != null) rs.close();
            } catch (Exception e) {
            }

            try {
                if (out != null) out.close();
            } catch (Exception e) {
            }
            try {
                if (in != null) in.close();
            } catch (Exception e) {
            }

        }
        return totalBytesRead;
    }

    /**
     * writes the contents of a clob to a file
     * 
     * @param query
     *            SQL query
     * @param filename the file to write
     * @return number of bytes written
     * @throws java.lang.Exception
     * @see #updateBlob( String, String, byte[], String)
     * @see #updateBlob( String, String, String, String)
     * @see #getBlob( String )
     */
    public long getBlob(String query, String fileName) throws Exception {

        InputStream in = null;
        FileOutputStream out = null;
        ResultSet rs = null;
        Statement stmt = null;
        long readBytes = 0;
        int len = 0;

        try {

            logger.debug9("calling " + SOSClassUtil.getMethodName());

            if (connection == null)
                    throw new Exception(
                            SOSClassUtil.getMethodName()
                                    + ": sorry, there is no successful connection established."
                                    + " may be the connect method was not called");

            query = normalizeStatement(query, replacement);
            if (profiler != null) try {
                profiler.start(query);
            } catch (Exception e) {
            }

            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            logger.debug6(SOSClassUtil.getMethodName() + ": " + query);

            if (rs.next()) {
                in = rs.getBinaryStream(1);
                if (in == null) { return readBytes; }
                byte[] buff = new byte[1024];

                if ((len = in.read(buff)) > 0) { // hat BLOB-Feld 0 byte??
                    out = new FileOutputStream(fileName);
                    out.write(buff, 0, len);
                    readBytes += len;
                } else {
                    logger.debug9(".. blob column has 0 bytes.");
                    return readBytes;
                }

                while (0 < (len = in.read(buff))) {
                    out.write(buff, 0, len);
                    readBytes += len;
                }

            }
            logger.debug9(SOSClassUtil.getMethodName()
                    + " successfully executed.");
        } catch (Exception e) {
            if (profiler != null) try {
                profiler.stop("ERROR", e.toString());
            } catch (Exception ex) {
            }
            throw e;
        } finally {
            if (out != null) try {
                out.close();
            } catch (Exception e) {
            }
            if (in != null) try {
                in.close();
            } catch (Exception e) {
            }
            if (stmt != null) try {
                stmt.close();
            } catch (Exception e) {
            }
            if (profiler != null) try {
                profiler.stop("", "");
            } catch (Exception e) {
            }
        }

        return readBytes;
    }

    /**
     * returns the content of a blob as byte array
     * 
     * @param query
     *            SQL query
     * @throws java.lang.Exception
     * @see #getBlob( String, String )
     * @see #updateBlob( String, String, byte[], String )
     * @see #updateBlob( String, String, String, String )
     */
    public byte[] getBlob(String query) throws Exception {

        ByteArrayOutputStream out = null;
        BufferedInputStream in = null;
        ResultSet rs = null;
        Statement stmt = null;
        byte[] result = {};
        Blob blob;
        int bytesRead;

        try {
            logger.debug9("calling " + SOSClassUtil.getMethodName());

            if (connection == null)
                    throw new Exception(
                            SOSClassUtil.getMethodName()
                                    + ": sorry, there is no successful connection established."
                                    + " may be the connect method was not called");

            query = normalizeStatement(query, replacement);
            if (profiler != null) try {
                profiler.start(query);
            } catch (Exception e) {
            }

            stmt = connection.createStatement();
            logger.debug6(".. " + query);
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                blob = rs.getBlob(1);

                if (blob == null) {
                    logger.debug9(".. ResultSet returns NULL value.");
                    return result;
                }
                byte[] data = new byte[(int) blob.length()];
                out = new ByteArrayOutputStream((int) blob.length());
                in = new BufferedInputStream(blob.getBinaryStream());
                if (in == null) {
                    logger
                            .debug9(".. ResultSet InputStream returns NULL value.");
                    return result;
                }
                if ((bytesRead = in.read(data, 0, data.length)) != -1) {
                    out.write(data, 0, bytesRead);
                    result = out.toByteArray();
                }
            }
            logger.debug9(SOSClassUtil.getMethodName()
                    + " successfully executed.");
        } catch (Exception e) {
            if (profiler != null) try {
                profiler.stop("ERROR", e.toString());
            } catch (Exception ex) {
            }
            throw e;
        } finally {
            if (out != null) try {
                out.close();
            } catch (Exception e) {
            }
            if (in != null) try {
                in.close();
            } catch (Exception e) {
            }
            if (stmt != null) try {
                stmt.close();
            } catch (Exception e) {
            }
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
            }
            if (profiler != null) try {
                profiler.stop("", "");
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * writes the contents of a clob to a file
     * 
     * @param query
     *            SQL query
     * @return number of bytes written
     * @throws java.lang.Exception
     * @see #updateClob( String, String, String, String )
     * @see #updateClob( String, String, File, String )
     * @see #getClob( String )
     */
    public long getClob(String query, String fileName) throws Exception {

        FileWriter out = null;
        Reader in = null;
        File file = null;
        ResultSet rs = null;
        Statement stmt = null;
        int bytesRead = 0;
        long totalBytesRead = 0;
        try {
            logger.debug9("calling " + SOSClassUtil.getMethodName());

            if (connection == null)
                    throw new Exception(
                            SOSClassUtil.getMethodName()
                                    + ": sorry, there is no successful connection established."
                                    + " may be the connect method was not called");

            query = normalizeStatement(query, replacement);
            logger.debug6(SOSClassUtil.getMethodName() + ": " + query);

            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                in = rs.getCharacterStream(1);
                if (in == null) {
                    logger.debug9(".. ResultSet returns NULL value.");
                    return totalBytesRead;
                }
                file = new File(fileName);
                out = new FileWriter(file);
                char[] buffer = new char[32];
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }
            }
            logger.debug9(SOSClassUtil.getMethodName()
                    + " successfully executed.");
        } catch (Exception e) {
            throw e;
        } finally {
            if (out != null) try {
                out.flush();
            } catch (Exception e) {
            }
            if (out != null) try {
                out.close();
            } catch (Exception e) {
            }
            if (stmt != null) try {
                stmt.close();
            } catch (Exception e) {
            }
            if (in != null) try {
                in.close();
            } catch (Exception e) {
            }
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
            }
        }
        return totalBytesRead;
    }

    /**
     * returns content of a clob
     * 
     * @param query
     *            SQL query
     * @return content of the clob or empty string
     * @throws java.lang.Exception
     * @see #updateClob( String, String, String, String )
     * @see #updateClob( String, String, File, String )
     * @see #getClob( String, String )
     */
    public String getClob(String query) throws Exception {

        Reader in = null;
        ResultSet rs = null;
        Statement stmt = null;
        int bytesRead = 0;
        StringBuffer sb = new StringBuffer();
        try {
            logger.debug9("calling " + SOSClassUtil.getMethodName());

            if (connection == null)
                    throw new Exception(
                            SOSClassUtil.getMethodName()
                                    + ": sorry, there is no successful connection established."
                                    + " may be the connect method is not called");

            query = normalizeStatement(query, replacement);
            logger.debug6(SOSClassUtil.getMethodName() + ": " + query);

            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                in = rs.getCharacterStream(1);
                if (in == null) {
                    logger.debug9(".. ResultSet returns NULL value.");
                    return sb.toString();
                }
                while ((bytesRead = in.read()) != -1) {
                    sb.append((char) bytesRead);
                }
            }
            logger.debug9(SOSClassUtil.getMethodName()
                    + " successfully executed.");
        } catch (Exception e) {
            throw e;
        } finally {
            if (stmt != null) try {
                stmt.close();
            } catch (Exception e) {
            }
            if (in != null) try {
                in.close();
            } catch (Exception e) {
            }
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
            }
        }
        return sb.toString();
    }

    /**
     * updates a clob from a file
     * 
     * @param tableName
     *            taget table
     * @param columnName
     *            target column
     * @param file
     *            source of the clob data
     * @param condition
     *            condition(s) (excluding WHERE) of the SQL statement
     * @return number of bytes written
     * @throws java.lang.Exception
     * @see #updateClob( String, String, String, String )
     * @see #getClob( String, String )
     * @see #getClob( String )
     */
    public long updateClob(String tableName, String columnName, File file,
            String condition) throws Exception {

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer query = null;
        long totalBytesWritten = 0;
        int bytesRead = 0;
        String theQuery = null;
        FileReader in = null;
        Writer out = null;
        CLOB clob = null;

        try {
            logger.debug9("calling " + SOSClassUtil.getMethodName());
            if (connection == null)
                    throw new Exception(
                            SOSClassUtil.getMethodName()
                                    + ": sorry, there is no successful connection established."
                                    + " may be the connect method was not called");

            if (SOSString.isEmpty(tableName))
                    throw new NullPointerException("tableName is null.");
            if (SOSString.isEmpty(columnName))
                    throw new NullPointerException("columnName is null.");
            if (!file.exists()) throw new Exception("file doesn't exist.");

            query = new StringBuffer("UPDATE ");

            if (tableNameUpperCase)
                query.append(tableName.toUpperCase());
            else
                query.append(tableName);

            if (fieldNameUpperCase) {
                query.append(" SET \"");
                query.append(columnName.toUpperCase());
                query.append("\" = empty_clob() ");
            } else {
                query.append(" SET ");
                query.append(columnName);
                query.append(" = empty_clob() ");
            }

            if (!SOSString.isEmpty(condition))
                condition = " WHERE " + condition;
            else
                condition = "";

            query.append(condition);

            theQuery = this.normalizeStatement(query.toString(), replacement);
            logger.debug6(SOSClassUtil.getMethodName() + ": " + theQuery);

            stmt = connection.createStatement();
            stmt.executeUpdate(theQuery);
            try {
                stmt.close();
                stmt = null;
            } catch (Exception e) {
                throw new Exception("an error occurred closing the statement: "
                        + e);
            }
            stmt = connection.createStatement();
            if (fieldNameUpperCase) {
                query = new StringBuffer("SELECT \"");
                query.append(columnName.toUpperCase());
                query.append("\" FROM ");
            } else {
                query = new StringBuffer("SELECT ");
                query.append(columnName);
                query.append(" FROM ");
            }

            if (tableNameUpperCase)
                query.append(tableName.toUpperCase());
            else
                query.append(tableName);

            query.append(" ");
            query.append(condition);
            query.append(" for update nowait");
            theQuery = this.normalizeStatement(query.toString(), replacement);
            logger.debug6(SOSClassUtil.getMethodName() + ": " + theQuery);
            rs = stmt.executeQuery(theQuery);
            if (rs.next()) clob = (CLOB) rs.getClob(1);

            in = new FileReader(file);
            char[] buffer = new char[clob.getBufferSize()];
            out = clob.getCharacterOutputStream();

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalBytesWritten += bytesRead;
            }
            logger.debug9(SOSClassUtil.getMethodName()
                    + " successfully executed.");
        } catch (Exception e) {
            throw e;
        } finally {
            if (out != null) try {
                out.close();
            } catch (Exception e) {
            }
            if (stmt != null) try {
                stmt.close();
            } catch (Exception e) {
            }
            if (in != null) try {
                in.close();
            } catch (Exception e) {
            }
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
            }
        }
        return totalBytesWritten;
    }

    /**
     * updates a clob
     * 
     * @param tableName
     *            taget table
     * @param columnName
     *            target column
     * @param data
     *            clob String
     * @param condition
     *            condition(s) (excluding WHERE) of the SQL statement
     * @return number bytes written
     * @throws java.lang.Exception
     * @see #getClob( String )
     * @see #getClob( String, String )
     */
    public long updateClob(String tableName, String columnName, String data,
            String condition) throws Exception {

        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer query = null;
        long totalBytesWritten = 0;
        int bytesRead = 0;
        int bufferSize = 0;
        String theQuery = null;
        Writer out = null;
        StringReader in = null;
        CLOB clob = null;

        try {
            logger.debug9("calling " + SOSClassUtil.getMethodName());
            if (connection == null)
                    throw new Exception(
                            "sorry, there is no successful connection established."
                                    + " may be the connect method is not called");

            if (SOSString.isEmpty(tableName))
                    throw new NullPointerException("tableName is null.");
            if (SOSString.isEmpty(columnName))
                    throw new NullPointerException("columnName is null.");
            if (SOSString.isEmpty(data))
                    throw new Exception("data has null value.");

            query = new StringBuffer("UPDATE ");
            if (tableNameUpperCase)
                query.append(tableName.toUpperCase());
            else
                query.append(tableName);

            if (fieldNameUpperCase) {
                query.append(" SET \"");
                query.append(columnName.toUpperCase());
                query.append("\" = empty_clob() ");
            } else {
                query.append(" SET ");
                query.append(columnName);
                query.append(" = empty_clob() ");
            }

            if (!SOSString.isEmpty(condition))
                condition = " WHERE " + condition;
            else
                condition = "";

            query.append(condition);

            theQuery = this.normalizeStatement(query.toString(), replacement);
            logger.debug6(SOSClassUtil.getMethodName() + ": " + theQuery);

            stmt = connection.createStatement();
            stmt.executeUpdate(theQuery);
            try {
                stmt.close();
                stmt = null;
            } catch (Exception e) {
                throw new Exception(" an error occurred closing the statement: "
                        + e);
            }
            stmt = connection.createStatement();
            if (fieldNameUpperCase) {
                query = new StringBuffer("SELECT \"");
                query.append(columnName.toUpperCase());
                query.append("\" FROM ");
            } else {
                query = new StringBuffer("SELECT ");
                query.append(columnName);
                query.append(" FROM ");
            }

            if (tableNameUpperCase)
                query.append(tableName.toUpperCase());
            else
                query.append(tableName);

            query.append(" ");
            query.append(condition);
            query.append(" for update nowait");
            theQuery = this.normalizeStatement(query.toString(), replacement);
            logger.debug6(SOSClassUtil.getMethodName() + ": " + theQuery);
            rs = stmt.executeQuery(theQuery);
            if (rs.next()) clob = (CLOB) rs.getClob(1);
            bufferSize = clob.getBufferSize();
            char[] buffer = new char[bufferSize];
            out = clob.getCharacterOutputStream();
            in = new StringReader(data);
            while ((bytesRead = in.read(buffer, 0, bufferSize)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalBytesWritten += bytesRead;
            }
            logger.debug9(SOSClassUtil.getMethodName()
                    + " successfully executed.");
        } catch (Exception e) {
            throw e;
        } finally {
            if (out != null) try {
                out.close();
            } catch (Exception e) {
            }
            if (stmt != null) try {
                stmt.close();
            } catch (Exception e) {
            }
            if (in != null) try {
                in.close();
            } catch (Exception e) {
            }
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
            }
        }
        return totalBytesWritten;
    }

    /**
     * returns Oracle timestamp functions
     * 
     * @param dateString
     * @return oracle timestamp function
     * @throws java.lang.Exception
     */
    public String toDate(String dateString) throws Exception {
        if (SOSString.isEmpty(dateString))
                throw new Exception(SOSClassUtil.getMethodName()
                        + ": dateString has no value.");
        return "to_date('" + dateString + "','YYYY-MM-DD HH24:MI:SS')";
    }

    /**
    /**
     * returns output of the database sertver (not the ResultSet)
     * 
     * @return Vector of Strings for each line of the output
     */
    public Vector getOutput() throws Exception {

        Vector out = new Vector();
        CallableStatement stmt = null;

        try {
            // enable_stmt = this.getConnection().prepareCall( "begin
            // dbms_output.enable(10000); end;" );
            // enable_stmt.executeUpdate();

            String getLineSql = "begin dbms_output.get_line(?,?); end;";
            stmt = this.getConnection().prepareCall(getLineSql);
            boolean hasMore = true;
            stmt.registerOutParameter(1, Types.VARCHAR);
            stmt.registerOutParameter(2, Types.INTEGER);
            while (hasMore) {
                stmt.execute();
                hasMore = (stmt.getInt(2) == 0);
                if (hasMore) {
                    out.add(stmt.getString(1));
                }
            }
        } catch (Exception e) {
            throw new Exception("error occurred reading output: "
                    + e.getMessage());
        } finally {
            if (stmt != null) try {
                stmt.close();
                stmt = null;
            } catch (Exception ex) {
            } // ignore this error
        }
        return out;
    }

    
    protected GregorianCalendar getDateTime(String format) throws Exception {
        GregorianCalendar gc = new GregorianCalendar();

        String timestamp = this.getSingleValue("select SYSDATE from dual");

        if (timestamp.length() > 19) {
            timestamp = timestamp.substring(0, 19);
        }

        java.util.Date date = sos.util.SOSDate.getDate(timestamp, format);

        gc.setTime(date);

        return gc;
    }
    
    
    protected String replaceCasts( String inputString) throws Exception {
        
        logger.debug6("Calling " + SOSClassUtil.getMethodName());
        
        Pattern pattern = Pattern.compile(CAST_PATTERN);
        Matcher matcher = pattern.matcher(inputString);
        StringBuffer buffer = new StringBuffer();
        String replaceString;
        String token;
       
        while ((matcher.find())) {

            replaceString = matcher.group().toLowerCase();
            if ( matcher.group(1) != null &&  matcher.group(6) != null) {
                token = matcher.group(6).replaceFirst("\\)","").trim();

                if ( token.matches(".*varchar.*")) {
                  replaceString = replaceString.replaceAll("varchar",")");
                  replaceString = replaceString.replaceFirst("%cast","TRIM(TO_CHAR");
                } else if ( token.matches(".*character.*")) {
                      replaceString = replaceString.replaceAll("character","");
                      replaceString = replaceString.replaceFirst("%cast","TO_CHAR");
                } else if (token.matches(".*integer.*")) {
                  replaceString = replaceString.replaceAll("integer","");
                  replaceString = replaceString.replaceFirst("%cast","TO_NUMBER");
                }
                else if (token.matches(".*timestamp.*")) {
                    replaceString = replaceString.replaceAll("timestamp",",'yyyy-mm-dd HH24:MI:SS'");
                    replaceString = replaceString.replaceFirst("%cast","TO_DATE");
                }
                else if (token.matches(".*datetime.*")) {
                    replaceString = replaceString.replaceAll("datetime",",'yyyy-mm-dd HH24:MI:SS'");
                    replaceString = replaceString.replaceFirst("%cast","TO_DATE");
                }
            } // if
            if ( matcher.group(3) != null && matcher.group(4) != null) { // group 4 "VALUE <data_type>"
                token = matcher.group(4).replaceFirst("\\(","").trim();
                
                if ( token.matches(".*varchar.*")) {
                   replaceString = replaceString.replaceAll("varchar",")");
                   replaceString = replaceString.replaceAll("%cast","TRIM(TO_CHAR");
                } else if ( token.matches(".*character.*")) {
                       replaceString = replaceString.replaceAll("character","");
                       replaceString = replaceString.replaceAll("%cast","TO_CHAR");
                } else if (token.matches(".*integer.*")) {
                   replaceString = replaceString.replaceAll("integer","");
                   replaceString = replaceString.replaceAll("%cast","TO_NUMBER");
               }
                else if (token.matches(".*timestamp.*")) {
                    replaceString = replaceString.replaceAll("timestamp",",'yyyy-mm-dd HH24:MI:SS'");
                    replaceString = replaceString.replaceFirst("%cast","TO_DATE");
                }
                else if (token.matches(".*datetime.*")) {
                    replaceString = replaceString.replaceAll("datetime",",'yyyy-mm-dd HH24:MI:SS'");
                    replaceString = replaceString.replaceFirst("%cast","TO_DATE");
                }
            }
            replaceString = replaceString.toUpperCase();
            matcher.appendReplacement(buffer, replaceString);
            
        }
        
        
        
        matcher.appendTail(buffer);
        logger.debug6(".. result [" + buffer.toString() + "]");
        return buffer.toString();
    } // pseudoFunctions

	
	protected String getLastSequenceQuery(String sequence) {		
		return "SELECT "+sequence+".currval FROM DUAL";
	}
    
	public String getNextSequenceValue(String sequence) throws Exception {
		return getSingleValue("SELECT "+sequence+".nextval FROM DUAL");
	}

	/*
	 * Oracle returns as productVersion e.g.:
	 * Oracle9i Enterprise Edition Release 9.2.0.1.0 - Production
     * With the Partitioning, OLAP and Oracle Data Mining options
     * JServer Release 9.2.0.1.0 - Production
	 */
	public int parseMajorVersion(String productVersion) throws Exception {
		String[] oraSplit = productVersion.split("Release");
		if (oraSplit.length>1) productVersion=oraSplit[1];
		return super.parseMajorVersion(productVersion);
	}

	
	public int parseMinorVersion(String productVersion) throws Exception {
		String[] oraSplit = productVersion.split("Release");
		if (oraSplit.length>1) productVersion=oraSplit[1];
		return super.parseMinorVersion(productVersion);
	}
	
    protected boolean prepareGetStatements(StringBuffer contentSB,StringBuffer splitSB,StringBuffer endSB) throws Exception{
    	if(contentSB == null){ throw new Exception("contentSB is null");}
    	if(splitSB == null){ throw new Exception("splitSB is null");}
    	if(endSB == null){ throw new Exception("endSB is null");}
    	    	
        splitSB.append("\n/\n");
        endSB.append("");   
    return true;
    }
    
    public String[] getReplacement() {
		return replacement;
	}
    
}
