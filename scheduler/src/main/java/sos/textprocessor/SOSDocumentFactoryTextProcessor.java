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
package sos.textprocessor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;

import sos.connection.SOSConnection;
import sos.hostware.Factory_processor;
import sos.settings.SOSConnectionSettings;
import sos.util.SOSLogger;
import sos.util.SOSStandardLogger;


public class SOSDocumentFactoryTextProcessor extends SOSTextProcessor {

    Factory_processor processor = null;

    
    public SOSDocumentFactoryTextProcessor() throws Exception {
        
        this.init();
    }

    
    public SOSDocumentFactoryTextProcessor(SOSLogger logger) throws Exception {
        
        this.setLogger(logger);
        this.init();
    }

    
    public SOSDocumentFactoryTextProcessor(SOSConnectionSettings settings) throws Exception {
        
        this.setSettings(settings);
        this.init();
    }
    

    public SOSDocumentFactoryTextProcessor(SOSConnectionSettings settings, SOSLogger logger) throws Exception {
        
        this.setLogger(logger);
        this.setSettings(settings);
        this.init();
    }
    

    public SOSDocumentFactoryTextProcessor(SOSConnectionSettings settings, String templateSectionName, String templateApplicationName) throws Exception {
        
        this.setSettings(settings);
        this.setTemplateSectionName(templateSectionName);
        this.setTemplateApplicationName(templateApplicationName);
        this.init();
    }
    
    
    public SOSDocumentFactoryTextProcessor(SOSConnectionSettings settings, SOSLogger logger, String templateSectionName, String templateApplicationName) throws Exception {
        
        this.setSettings(settings);
        this.setLogger(logger);
        this.setTemplateSectionName(templateSectionName);
        this.setTemplateApplicationName(templateApplicationName);
        this.init();
    }
    
    
    /**
     * initialization
     * - set date format, datetime format
     * 
     * @throws java.lang.Exception
     */
    public void init() throws Exception {

        super.init();
        
        this.processor = new Factory_processor();
    }
    
    
    /**
     * process text with replacements
     *
     * @throws java.lang.Exception
     */
    public void process() throws Exception {
        
        if (this.getTemplateContent() != null && this.getTemplateContent().length() > 0) {
            this.process(this.getTemplateContent(), this.getTemplateScriptLanguage(), this.getReplacements(), false);
        } else if (this.getTemplateFile() != null) {
            this.process(this.getTemplateFile(), this.getTemplateScriptLanguage(), this.getReplacements(), false);
        } else {
            throw new Exception("no template was given.");
        }
    }
    
    
    /**
     * process text with replacements
     *
     * @param templateFile File template file
     * @throws java.lang.Exception
     */
    public File process(File templateFile) throws Exception {
        
        this.setTemplateFile(templateFile);
        
        return this.process(this.getTemplateFile(), this.getTemplateScriptLanguage(), this.getReplacements());
    }
    
    
    /**
     * process text with replacements
     *
     * @param templateFile File template file
     * @param templatecScriptLanguage String script language
     * @throws java.lang.Exception
     */
    public File process(File templateFile, String templateScriptLanguage) throws Exception {
        
        this.setTemplateFile(templateFile);
        this.setTemplateScriptLanguage(templateScriptLanguage);
        
        return this.process(this.getTemplateFile(), this.getTemplateScriptLanguage(), this.getReplacements(), false);
    }
    
    
    /**
     * process text with replacements
     *
     * @param templateFile File template file
     * @param replacements HashMap name/value pairs to be replaced in template content
     * @throws java.lang.Exception
     */
    public File process(File templateFile, HashMap replacements) throws Exception {
        
        this.setTemplateFile(templateFile);
        this.setReplacements(replacements);
        
        return this.process(this.getTemplateFile(), this.getTemplateScriptLanguage(), this.getReplacements(), false);
    }
    
    
    /**
     * process text with replacements
     *
     * @param templateFile File template file
     * @param templateScriptLanguage String script language
     * @param replacements HashMap name/value pairs to be replaced in template content
     * @throws java.lang.Exception
     */
    public File process(File templateFile, String templateScriptLanguage, HashMap replacements) throws Exception {
        
        this.setTemplateFile(templateFile);
        this.setTemplateScriptLanguage(templateScriptLanguage);
        this.setReplacements(replacements);
        
        return this.process(this.getTemplateFile(), this.getTemplateScriptLanguage(), this.getReplacements(), false);
    }
    
    
    /**
     * process text with replacements
     *
     * @param templateFile File template file
     * @param replacements HashMap name/value pairs to be replaced in template content
     * @param nl2br boolean replace newlines by HTML breaks
     * @throws java.lang.Exception
     */
    public File process(File templateFile, HashMap replacements, boolean nl2br) throws Exception {

        this.setTemplateFile(templateFile);
        this.setReplacements(replacements);
        
        return this.process(templateFile, this.getTemplateScriptLanguage(), this.getReplacements(), nl2br);
    }
    
    
    /**
     * process text with replacements
     *
     * @param templateFile File template file
     * @param templateScriptLanguage String script language
     * @param replacements HashMap name/value pairs to be replaced in template content
     * @param nl2br boolean replace newlines by HTML breaks
     * @throws java.lang.Exception
     */
    public File process(File templateFile, String templateScriptLanguage, HashMap replacements, boolean nl2br) throws Exception {

        Object key     = null;
        Object value   = null;

        this.setTemplateFile(templateFile);
        this.setTemplateScriptLanguage(templateScriptLanguage);
        this.setReplacements(replacements);

        try {
            if (this.processor == null) this.processor = new Factory_processor(); 
            
            if (this.documentFile == null) this.documentFile = File.createTempFile("sos", ".tmp");
            this.processor.set_document_filename( this.documentFile.getAbsolutePath() );

            this.processor.set_language( this.getTemplateScriptLanguage() );
            if (this.getTemplateScriptLanguage().equalsIgnoreCase("javascript") ) {
               this.processor.add_obj( this, "document_factory");
            }

            this.processor.set_template_filename( templateFile.getAbsolutePath() );

            Iterator keys  = replacements.keySet().iterator();
            while( keys.hasNext() ) {
                key = keys.next();
                if ( key != null ) {
                    value = replacements.get(key.toString());
                    if (value != null ) {
                      this.processor.set_parameter(key.toString(), value.toString() );
                    }
                }
            }
            this.processor.add_parameters();
        
            if (this.scripts != null && !this.scripts.isEmpty()) {
                if (this.scripts.containsKey("start_script." + this.getTemplateScriptLanguage()))
                    processor.parse(this.scripts.getProperty("start_script." + this.getTemplateScriptLanguage()));
            }
            
            this.processor.process();
            this.processor.close_output_file();
            this.processor.close();

            return this.documentFile;
            
        } catch (Exception e) {
            throw new Exception("error occurred processing template: " + e.getMessage());
        } finally {
            if ( this.processor != null ) try { this.processor.close(); this.processor = null; } catch (Exception ex) {};
        }
    }
    
    
    /**
     * process text with replacements
     *
     * @param templateContent String template content
     * @throws java.lang.Exception
     */
    public String process(String templateContent) throws Exception {
        
        this.setTemplateContent(templateContent);
        
        return this.process(this.getTemplateContent(), this.getTemplateScriptLanguage(), this.getReplacements());
    }
    
    
    /**
     * process text with replacements
     *
     * @param templateContent String template content
     * @param templateScriptLanguage String script language
     * @throws java.lang.Exception
     */
    public String process(String templateContent, String templateScriptLanguage) throws Exception {
        
        this.setTemplateContent(templateContent);
        this.setTemplateScriptLanguage(templateScriptLanguage);
        
        return this.process(this.getTemplateContent(), this.getTemplateScriptLanguage(), this.getReplacements());
    }
    

    /**
     * process text with replacements
     *
     * @param templateContent String template content
     * @param replacements HashMap name/value pairs to be replaced in template content
     * @throws java.lang.Exception
     */
    public String process(String templateContent, HashMap replacements) throws Exception {
        
        this.setTemplateContent(templateContent);
        this.setReplacements(replacements);
        
        return this.process(this.getTemplateContent(), this.getTemplateScriptLanguage(), this.getReplacements(), false);
    }
    

    /**
     * process text with replacements
     *
     * @param templateContent String template content
     * @param templateScriptLanguage String script language
     * @param replacements HashMap name/value pairs to be replaced in template content
     * @throws java.lang.Exception
     */
    public String process(String templateContent, String templateScriptLanguage, HashMap replacements) throws Exception {
        
        this.setTemplateContent(templateContent);
        this.setTemplateScriptLanguage(templateScriptLanguage);
        this.setReplacements(replacements);
        
        return this.process(this.getTemplateContent(), this.getTemplateScriptLanguage(), this.getReplacements(), false);
    }
    

    /**
    * process text with replacements
    *
    * @param templateContent String template content
    * @param replacements HashMap name/value pairs to be replaced in template content
    * @param nl2br boolean replace newlines by HTML breaks
    * @throws java.lang.Exception
    */
    public String process(String templateContent, HashMap replacements, boolean nl2br) 
         throws Exception {

        this.setTemplateContent(templateContent);
        this.setReplacements(replacements);
        
        return this.process(this.getTemplateContent(), this.getTemplateScriptLanguage(), this.getReplacements(), nl2br);
    }
    
    
    /**
     * process text with replacements
     *
     * @param templateContent String template content
     * @param templateScriptLanguage String script language
     * @param replacements HashMap name/value pairs to be replaced in template content
     * @param nl2br boolean replace newlines by HTML breaks
     * @throws java.lang.Exception
     */
     public String process(String templateContent, String templateScriptLanguage, HashMap replacements, boolean nl2br) 
          throws Exception {

    
      this.setTemplateContent(templateContent);
      this.setTemplateScriptLanguage(templateScriptLanguage);
      this.setReplacements(replacements);
      
      this.templateFile = File.createTempFile("sos", ".tmp");
      this.templateFile.deleteOnExit();

      BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(this.templateFile.getAbsolutePath()));
      out.write(this.getTemplateContent().getBytes());
      out.flush();
      out.close();
      
      this.process(this.templateFile, this.getTemplateScriptLanguage(), this.getReplacements(), nl2br);
      return this.getDocumentContent();
   }

    
    /**
     * return the resulting document content
     * 
     * @return Returns the documentContent.
     */
    public String getDocumentContent() throws Exception {
        
        if (this.getDocumentFile() == null) throw new Exception("no document has been created.");
        
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(this.getDocumentFile().getAbsolutePath()));
        StringBuffer content = new StringBuffer("");
        byte buffer[] = new byte[1024];
        int bytesRead;
        while ( (bytesRead = in.read(buffer)) != -1) {
            content.append(new String(buffer, 0, bytesRead)); 
        }
        in.close();
        
        return content.toString();
    }

    
    /**
     * @return Returns the processor.
     */
    public Factory_processor getProcessor() throws Exception {

        if (this.processor == null) this.processor = new Factory_processor();
        return processor;
    }

    

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        try {
            SOSDocumentFactoryTextProcessor processor = new SOSDocumentFactoryTextProcessor();

            // plain text sample
            processor.process("Hello World &(datetime)");
            System.out.println(processor.getDocumentContent());
            
            
            // template sample with settings
            // .. create logger
            SOSStandardLogger logger = new SOSStandardLogger(9);
            // .. create connection
            SOSConnection connection = SOSConnection.createInstance("/scheduler/config/sos_settings.ini", logger);
            connection.connect();
            // .. create settings instance from connection
            SOSConnectionSettings settings = new
            SOSConnectionSettings( connection,          // connection instance
                                  "SETTINGS",           // settings table
                                  logger);

            processor = new SOSDocumentFactoryTextProcessor(settings, "email_templates", "email_templates");
            processor.setHasLocalizedTemplates(true);
            File outputFile = new File("/tmp/sample.txt");
            processor.setDocumentFile(outputFile);
            processor.process(processor.getTemplateFile("sample_body"));
            System.out.println(processor.getDocumentContent());
            
            
        } catch (Exception e) {
            System.out.println("error occurred: " + e.getMessage());
        }
    }

}
