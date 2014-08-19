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
package com.sos.i18n.annotation;


/**
 * \package com.sos.Localization.annotation
 * 
 * \brief JobScheduler Localization and i18n Classes: Annotation classes
 * 
 * 
 * <h3>I18N Annotations and Resource Bundle Auto-Generation</h3>
 * <p>
 * So far, we've assumed that when ever you need to specify a particular message, you would provide the message's bundle key by hardcoding its value in the API calls; e.g.: <code>Msg.createMsg("my-bundle-key")</code>.  Because this does not provide a way for nice compile-time checks (to ensure that key actually refers to an actual bundle message), the developer must ensure that the key does not have a typo and the developer must remember to add the actual message associated with that key to the resource bundle properties file.  This is not an easy thing to do and is ripe for problems that will not manifest itself until you run your app and notice that messages are missing.  This isn't even easy to unit test.  What we need is a way to get the compiler to check these things for us.  It would also be nice to have a tool that automatically generates our resource bundle properties files so the developer isn't responsible for manually adding new messages to them and manually cleaning up old, obsolete messages that are no longer used.
 * </p>
 * <p>
 * Fortunately, <i>i18nlog</i> provides mechanisms to do those things.  
 * First, there are several I18N annotations that allow you to annotate your Java classes to facilitate 
 * the automatic generation of resource bundles via a custom Ant task so you don't have to manually create and 
 * edit your resource bundle .properties files.  
 * The message annotations are placed on constants that you use in place of your resource bundle key strings, 
 * so this inherently forces compile time checks.  
 * This means that typos introduced in the code (i.e. misspelling a constant name) and usage of obsolete/deleted messages are detected at compile time.
 * </p>
 * 
 * <p>
 * The annotations that are available are:
 * </p>
 * 
 * <ul>
 * <li>\@I18NResourceBundle</li>
 * <li>\@I18NMessage</li>
 * <li>\@I18NMessages</li>
 * </ul>
 * 
 * <p>
 * The <code>\@I18NResourceBundle</code> annotation defines what resource bundle properties file you want to put your localized messages in.  
 * This can annotate an entire class or interface, or it can annotate a specific field.  
 * If you annotate a class or interface, all <code>\@I18NMessage</code> annotations found in that class or instance will be stored, by default,
 *  in that resource bundle.  
 *  If the annotation is on a particular constant field, it will be the default bundle for that constant only.
 * </p>
 * 
 * <p>
 * The <code>\@I18NMessage</code> can only annotate a field, and more specifically, should only annotate a <code>static final</code> field 
 * that is of type <code>java.lang.String</code> 
 * (in fact, an error will be generated by the Ant task if this is not the case - more on the Ant task below).  
 * This annotation defines the actual localized message translation for a particular locale.  
 * You can define multiple message translations for a single field using the <code>\@I18NMessages</code> annotation 
 * (which simply wraps multiple <code>\@I18NMessage</code> annotations) 
 * The constant String value of the field that you are annotating is the resource bundle key string.  
 * It is these constants that you pass into the <code>Msg</code>, <code>Logger</code> and <code>LocalizedException</code> methods/constructors 
 * when you want to refer to a specific resource bundle key.  
 * This avoids having to hardcode string literals into your method calls and thus avoids the possibility of introducing 
 * a typo in the string literal and avoids the possibility that you are using a message key that no longer exists 
 * (since deleting a constant would cause all uses of that old constant to be flagged as an error by the compiler).
 * </p>
 * 
 * <p>
 * This is probably confusing, so an example is in order here.  
 * Let's look at the 
 * <a href="http://i18nlog.cvs.sourceforge.net/i18nlog/i18nlog/test/mazz/i18n/annotation/ExampleAnnotatedClass.java?view=markup">ExampleAnnotatedClass</a> 
 * that is in the <i>i18nlog</i> project as an illustrative example on how to annotate your classes 
 * (this class also has a couple of incorrectly used annotations; 
 * those incorrect usages are documented in the comments within the class so as not to confuse the reader).
 * </p>
 * 
 * <p>
 * First, notice that the class declaration has a <code>\@I18NResourceBundle</code> annotation.
 * </p>
 * 
 * <pre>
 *    \@I18NResourceBundle( baseName = "example-messages" )
 *    public class ExampleAnnotatedClass
 * </pre>
 * 
 * <p>
 * This tells us the default file where this class's I18N messages will be written. 
 * In this example, all I18N messages found within the scope of this class definition will be stored, by default, 
 * in the resource bundle properties file named "example-messages_en_US.properties" assuming my VM's default locale is "en_US". 
 * The <code>\@I18NResourceBundle</code> annotation defines what the base bundle name is via its 
 * ::baseName() attribute (if you do not specify the <code>baseName</code> attribute, the default will be "messages").  
 * It also indicates which locale the messages are written in via the 
 * ::defaultLocale attribute (if this attribute is not specified, the default will be that of the <i>defaultlocale</i> 
 * attribute of the i18n ANT task or, if that is not defined, the VM's default locale).  
 * Two additional things to note here: first, both <code>class</code> and <code>interface</code> declarations can be 
 * annotated with <code>\@I18NResourceBundle</code> - since both can contain static final String constants 
 * (this is useful if you want to put all of your I18N message fields in a single interface - thus keeping all I18N 
 * information in a single location within your code base). Secondly, this top-level annotation can be overridden by placing a <code>\@I18NResourceBundle</code> annotation on a particular field that wants to put its messages in a resource bundle that is different than that specified by the top-level annotation.  In the <code>ExampleAnnotatedClass</code> example, you can see this on the field <code>MESSAGE_THREE_KEY</code>:
 * </p>
 * 
 * <pre>
 *    \@I18NMessage( "This is my en_CA version of the third message that should go in a en_CA bundle" )
 *    \@I18NResourceBundle( baseName = "example-messages-for-third",
 *                         defaultLocale = "en_CA" )
 *    public static final String MESSAGE_THREE_KEY = "Example.message3-key";
 * </pre>
 * 
 * <p>
 * Now that you have defined where you want to store your I18N messages via the <code>\@I18NResourceBundle</code> annotation, you then begin to define the messages themselves.  That's where the <code>\@I18NMessage</code> and <code>\@I18NMessages</code> annotations come in.  You need to define a static final String constant whose value is the bundle key of the localized message.  This bundle key is the same across all locales - it identifies your message regardless of what language the message is displayed as.  Once you define the constant, you need to annotate it to denote it as an I18N message.  If you are multi-lingual, you can use <code>I18NMessages</code> to provide multiple translations for your message; the typically use-case, however, is that you provide a single translation in a <code>\@I18NMessage</code> annotation.  So, back to our example class, you can see these annotations at work here:
 * </p>
 * 
 * <pre>
 *    \@I18NMessages( {
 *                     \@I18NMessage( "This is my English message: {0}" ),
 *                     \@I18NMessage( value = "This is my UK-English message: {0}",
 *                                   locale = "en_UK"),
 *                     \@I18NMessage( value = "Dieses ist meine deutsche Anzeige: {0}",
 *                                   locale = "de")
 *                  })
 *    public static final String MESSAGE_KEY = "Example.message-key";
 * 
 *    \@I18NMessage( "This is my English version of the second message" )
 *    public static final String MESSAGE_TWO_KEY = "Example.message2-key";
 * </pre>
 * 
 * <p>
 * You can see that for <code>MESSAGE_KEY</code>, I decided to provide three translations - one for the default locale (which is defined by my <code>\@I18NResourceBundle</code> annotation), one for the "en_UK" locale and one for the "de" (aka German) locale. For the <code>MESSAGE_TWO_KEY</code> bundle key constant, I only defined a single translation in my default locale.
 * </p>
 * 
 * <p>
 * All that said, in your typical use-case, you will have a single <code>\@I18NResourceBundle</code> annotation on a top-level <code>interface</code> and each I18N constant field will have a single <code>\@I18NMessage</code> that uses the default locale as defined by the <code>\@I18NResourceBundle</code> annotation:
 * </p>
 * 
 * <pre>
 *    \@I18NResourceBundle( baseName="my-messages" defaultLocale="en" )
 *    public interface MyMessageKeys
 *    {
 *       \@I18NMessage( "This is an English message" )
 *       public static final String I18N_FIRST_MESSAGE = "Example.first-message";
 * 
 *       \@I18NMessage( "Hello {0}.  You visited this website {1} times" )
 *       public static final String I18N_WELCOME = "Example.welcome";
 * 
 *       // ... and any more you want to define
 *    }
 * </pre>
 * 
 * <p>
 * Now you use these in your code rather than hardcoding the string literals:
 * </p>
 * 
 * <pre>
 *    LOG.debug( MyMessageKeys.I18N_FIRST_MESSAGE );
 *    System.out.println( Msg.createMsg( MyMessageKeys.I18N_WELCOME, "John", counter ) );
 * </pre>
 * 
 * 
 */
