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
package sos.scheduler.editor.doc.listeners;

import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

import sos.scheduler.editor.app.Editor;
import sos.scheduler.editor.app.Utils;
import sos.scheduler.editor.doc.DocumentationDom;

import com.sos.scheduler.model.LanguageDescriptorList;

public class ScriptListener {
    private final DocumentationDom     _dom;

    private final Element              _parent;

    private Element              _script;

    private int                  _type       = Editor.DOC_SCRIPT;

    public final static int      NONE        = 0;
    public final static int      JAVA        = 1;
    public final static int      JAVA_SCRIPT = 2;
    public final static int      PERL        = 3;
    public final static int      VB_SCRIPT   = 4;
    public final static int      SHELL       = 5;
    public final static int      COM         = 99;


//    public final static String[] _languages  = { "", "java", "javascript", "perlscript", "vbscript", "shell",  "" };
    public final static String[] _languages  = LanguageDescriptorList.getLanguages4APIJobs();


    public ScriptListener(final DocumentationDom dom, final Element parent, final int type) {
        _dom = dom;
        _parent = parent;
        _type = type;
        setScript();
    }


    private Element setScript() {
        if (_type == Editor.DOC_MONITOR) {
            Element monitor = _parent.getChild("monitor", _dom.getNamespace());
            if (monitor == null) {
                monitor = new Element("monitor", _dom.getNamespace());
                _parent.addContent(monitor);
            }
            _script = monitor.getChild("script", _dom.getNamespace());
            if (_script == null) {
                _script = new Element("script", _dom.getNamespace());
                monitor.addContent(_script);
            }
        } else {
            _script = _parent.getChild("script", _dom.getNamespace());
            if (_script == null) {
                _script = new Element("script", _dom.getNamespace());
                _parent.addContent(_script);
            }
        }

        return _script;
    }


    public Element getScript() {
        return _script;
    }


    public void checkScript() {
        if (_script != null && _type == Editor.DOC_MONITOR) {
            Attribute language = _script.getAttribute("language");
            Attribute javaClass = _script.getAttribute("java_class");
            Attribute comClass = _script.getAttribute("com_class");
            Attribute resource = _script.getAttribute("resource");
            List includes = _script.getChildren("include");

            if (language == null && javaClass == null && comClass == null && resource == null && includes.size() == 0) {
                _parent.removeChild("monitor", _dom.getNamespace());
                // _dom.setChanged(true);
            }
        }
    }


    public int getLanguage() {
        if (_script != null)
            return languageAsInt(_script.getAttributeValue("language"));
        else
            return NONE;
    }


    private int languageAsInt(final String language) {
        for (int i = 0; i < _languages.length; i++) {
            if (_languages[i].equalsIgnoreCase(language))
                return i;
        }

        if (_script != null && _script.getAttributeValue("com_class") != null)
            return COM;

        return NONE;
    }


    private String languageAsString(final int language) {
        return _languages[language];
    }


    public void setLanguage(final int language) {
        setScript();

        if (_script != null) {
            switch (language) {
                case NONE:
                    _script.removeAttribute("com_class");
                    _script.removeAttribute("java_class");
                    break;
                case PERL:
                case JAVA_SCRIPT:
                case VB_SCRIPT:
                   _script.removeAttribute("com_class");
                   _script.removeAttribute("java_class");
                   break;
                case SHELL:
                   _script.removeAttribute("com_class");
                   _script.removeAttribute("java_class");
                   break;
                case JAVA:
                    _script.removeAttribute("com_class");
                    break;
                case COM:
                    if (_script.getAttributeValue("com_class") == null)
                        _script.setAttribute("com_class", "");
                    _script.removeAttribute("java_class");
                    break;
            }

            // if (language != NONE)
            Utils.setAttribute("language", languageAsString(language), _script, _dom);

            _dom.setChanged(true);
        }
    }


    public String getComClass() {
        return Utils.getAttributeValue("com_class", _script);
    }


    public void setComClass(final String com) {
        setScript();
        Utils.setAttribute("com_class", com, "", _script, _dom);
    }


    public String getJavaClass() {
        return Utils.getAttributeValue("java_class", _script);
    }


    public void setJavaClass(final String java) {
        setScript();
        Utils.setAttribute("java_class", java, "", _script, _dom);
    }


    public String[] getResources(final String ownID) {
        return DocumentationListener.getIDs(_dom, ownID);
    }


    public String getResource() {
        return Utils.getAttributeValue("resource", _script);
    }


    public void setResource(final String resource) {
        setScript();
        Utils.setAttribute("resource", DocumentationListener.getID(resource), _script, _dom);
    }

}
