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
package com.sos.scheduler.generics;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

import org.apache.log4j.Logger;

import sos.scheduler.job.JobSchedulerJobAdapter;
import sos.spooler.Job_impl;

import com.sos.JSHelper.Exceptions.JobSchedulerException;

/**
 * \class 		GenericAPIJobJSAdapterClass - JobScheduler Adapter for "A generic internal API job"
 *
 * \brief AdapterClass of GenericAPIJob for the SOSJobScheduler
 *
 * This Class GenericAPIJobJSAdapterClass works as an adapter-class between the SOS
 * JobScheduler and the worker-class GenericAPIJob.
 *
<job title="Copies one or more files" order="yes">
<description>
<include file="jobs/GenericAPIJobJSAdapterClass.xml"/>
</description>
<params>
<param name="file_spec" value=".*"/>
<param name="create_file" value="true"/>
<param name="overwrite" value="true"/>
<param name="source_file" value="R:/backup/sos/java/doxygen-docs"/>
<param name="target_file" value="W:/doc/doxygen-docs"/>
<param name="recursive" value="true"/>
<param name="javaClassName" value="sos.scheduler.file.JobSchedulerCopyFile"/>
</params>
<script language="java" java_class="com.sos.scheduler.generics.GenericAPIJobJSAdapterClass"/>
<run_time/>
</job>
 *
 * see \see C:\Users\KB\AppData\Local\Temp\scheduler_editor-2864692299059909179.html for more details.
 *
 * \verbatim ;
 * mechanicaly created by C:\Users\KB\sos-berlin.com\jobscheduler\scheduler\config\JOETemplates\java\xsl\JSJobDoc2JSAdapterClass.xsl from http://www.sos-berlin.com at 20120611173607
 * \endverbatim
 */
public class GenericAPIJobJSAdapterClass extends JobSchedulerJobAdapter {
	private final String	conClassName	= "GenericAPIJobJSAdapterClass";						//$NON-NLS-1$
	private static Logger	logger			= Logger.getLogger(GenericAPIJobJSAdapterClass.class);

	public void init() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::init"; //$NON-NLS-1$
		doInitialize();
	}

	private void doInitialize() {
	} // doInitialize

	@Override
	public boolean spooler_init() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::spooler_init";
		return super.spooler_init();
	}

	@Override
	public boolean spooler_process() throws Exception {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::spooler_process";

		try {
			super.spooler_process();
			doProcessing();
		}
		catch (Exception e) {
			throw e;
		}
		finally {
		} // finally
			// return value for classic and order driven processing
			// TODO create method in base-class for this functionality
		return signalSuccess();

	} // spooler_process

	@Override
	public void spooler_exit() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::spooler_exit";
		super.spooler_exit();

	}

	private GenericAPIJobOptions		objO				= null;
	private ClassLoader					classLoader			= null;
	private HashMap<String, Job_impl>	objLoadedClasses	= null;

	private void doProcessing() throws Exception {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::doProcessing"; //$NON-NLS-1$

		GenericAPIJob objR = new GenericAPIJob();
		objO = objR.Options();
		objO.CurrentNodeName(getCurrentNodeName());
		objO.setAllOptions(getSchedulerParameterAsProperties(getJobOrOrderParameters()));
		objO.CheckMandatory();

		if (objLoadedClasses == null) {
			objLoadedClasses = new HashMap<String, Job_impl>();
		}

		String strNameOfClass2Load = objO.javaClassName.Value();
		Job_impl objClass2Call = objLoadedClasses.get(strNameOfClass2Load);
		if (objClass2Call == null) {
			objClass2Call = getDynamicClassInstance(strNameOfClass2Load);

			if (objClass2Call == null && objO.javaClassPath.IsNotEmpty()) {
				String[] strJars = objO.javaClassPath.Value().split(";");
				for (String strJarFileName : strJars) {
					File objF = new File(strJarFileName);
					if (objF.isFile() && objF.canExecute()) {
						addJarsToClassPath(Thread.currentThread().getContextClassLoader(), new File[] { objF });
					}
					else {
						throw new JobSchedulerException(String.format("ClasspathElement '%1$s' not found", strJarFileName));
					}
				}
				objClass2Call = getDynamicClassInstance(strNameOfClass2Load);
			}

			if (objClass2Call == null) {
				throw new JobSchedulerException(strNameOfClass2Load);
			}

			objLoadedClasses.put(strNameOfClass2Load, objClass2Call);
			objClass2Call.spooler = spooler;
			objClass2Call.spooler_log = spooler_log;
			objClass2Call.spooler_task = spooler_task;
			objClass2Call.spooler_job = spooler_job;

			objClass2Call.spooler_init();
			objClass2Call.spooler_open();
		}

		objClass2Call.spooler_process();

	} // doProcessing

	@Override
	public void spooler_close() {

	}

	@Override
	public void spooler_on_error() {

	}

	@Override
	public void spooler_on_success() {

	}

	private void addJarsToClassPath(final ClassLoader classLoader1, final File[] jars) {
		if (classLoader1 instanceof URLClassLoader) {
			try {
				Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
				if (null != addUrlMethod) {
					addUrlMethod.setAccessible(true);
					for (File jar : jars) {
						try {
							addUrlMethod.invoke(classLoader1, jar.toURI().toURL());
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Job_impl getDynamicClassInstance(final String pstrLoadClassNameDefault) {

		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::getDynamicClassInstance";
		String strLoadClassName = pstrLoadClassNameDefault;

		Job_impl objC = null;
		try {
			classLoader = Thread.currentThread().getContextClassLoader();
			Class objA = classLoader.loadClass(strLoadClassName);

			objC = (Job_impl) objA.newInstance();

			if (objC instanceof Job_impl) {
				logger.debug("Job_impl is part of class   ...  " + objA.toString());
				//				objC = objC;
			}
			else {
				logger.error("Job_impl not part of class" + objA.toString());
			}
		}
		catch (ClassNotFoundException e) {
			System.out.println(e.getMessage() + " not found...");
			throw new JobSchedulerException(e);
		}

		catch (Exception e) {
			e.printStackTrace();
			throw new JobSchedulerException(e.getLocalizedMessage());
		}

		return objC;

	} // private Job_impl getDynamicClassInstance

}
