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
package sos.ftphistory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sos.hibernate.classes.DbItem;
import com.sos.hibernate.classes.SOSHibernateIntervalFilter;
import com.sos.hibernate.interfaces.ISOSHibernateFilter;

/**
 * \class SchedulerHistoryFilter
 *
 * \brief SchedulerHistoryFilter -
 *
 * \details
 *
 * \section SchedulerHistoryFilter.java_intro_sec Introduction
 *
 * \section SchedulerHistoryFilter.java_samples Some Samples
 *
 * \code .... code goes here ... \endcode
 *
 * <p style="text-align:center">
 * <br />
 * --------------------------------------------------------------------------- <br />
 * APL/Software GmbH - Berlin <br />
 * ##### generated by ClaviusXPress (http://www.sos-berlin.com) ######### <br />
 * ---------------------------------------------------------------------------
 * </p>
 * \author Uwe Risse \version 14.12.2011 \see reference
 *
 * Created on 14.12.2011 13:53:37
 */

public class JadeFilesFilter extends SOSHibernateIntervalFilter implements ISOSHibernateFilter {
	@SuppressWarnings("unused")
	private final String	conClassName	= "JadeFilesFilter";
	private String			dateFormat1		= "yyyy-MM-dd HH:mm:ss";
	public final String			conSVNVersion					= "$Id: SOSDataExchangeEngine.java 19091 2013-02-08 12:49:32Z kb $";

	private String			sourceHost;
	private String			sourceHostIp;
	private String			sourceUser;
	private String			sourceDir;
	private String			sourceFilename;

	private Date			createdFrom;
	private Date			createdTo;

	private String			createdFromIso;
	private String			createdToIso;

	public String getCreatedFromIso() {
		return createdFromIso;
	}

	public String getCreatedToIso() {
		return createdToIso;
	}

	public JadeFilesFilter() {
	}

	@Override
	public String getDateFormat() {
		return dateFormat1;
	}

	@Override
	public void setDateFormat(final String dateFormat) {
		dateFormat1 = dateFormat;
	}

	public String getSourceHost() {
		return sourceHost;
	}

	public void setSourceHost(final String sourceHost) {
		this.sourceHost = sourceHost;
	}

	public String getSourceHostIp() {
		return sourceHostIp;
	}

	public void setSourceHostIp(final String sourceHostIp) {
		this.sourceHostIp = sourceHostIp;
	}

	public String getSourceUser() {
		return sourceUser;
	}

	public void setSourceUser(final String sourceUser) {
		this.sourceUser = sourceUser;
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(final String sourceDir) {
		this.sourceDir = sourceDir;
	}

	public String getSourceFilename() {
		return sourceFilename;
	}

	public void setSourceFilename(final String sourceFilename) {
		this.sourceFilename = sourceFilename;
	}

	public Date getCreatedFrom() {
		return createdFrom;
	}

	public void setCreatedFrom(final String createdFrom) throws ParseException {
		if (createdFrom.equals("")) {
			this.createdFrom = null;
		}
		else {
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat1);
			Date d = formatter.parse(createdFrom);
			setCreatedFrom(d);
		}
	}

	public Date getCreatedTo() {
		return createdTo;
	}

	public void setCreatedTo(final String createdTo) throws ParseException {
		if (createdTo.equals("")) {
			this.createdTo = null;
		}
		else {
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat1);
			Date d = formatter.parse(createdTo);
			setCreatedTo(d);
		}
	}

	public void setCreatedFrom(final Date from) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String d = formatter.format(from);
		try {
			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			createdFrom = formatter.parse(d);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		createdFromIso = formatter.format(from);
	}

	public void setCreatedTo(final Date to) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		String d = formatter.format(to);
		try {
			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			createdTo = formatter.parse(d);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		createdToIso = formatter.format(to);
	}

	@Override
	public String getTitle() {
		String s = "";

		if (createdFrom != null) {
			s += String.format("From: %s ", date2Iso(createdFrom));
		}
		if (createdTo != null) {
			s += String.format("To: %s ", date2Iso(createdTo));
		}

		String title = String.format("%1s ", s);
		return title;
	}

	@Override
	public boolean isFiltered(final DbItem h) {
		return false;
	}

	@Override
	public void setIntervalFromDate(final Date d) {
		createdFrom = d;
	}

	@Override
	public void setIntervalToDate(final Date d) {
		createdTo = d;
	}

	@Override
	public void setIntervalFromDateIso(final String s) {
		createdFromIso = s;
	}

	@Override
	public void setIntervalToDateIso(final String s) {
		createdToIso = s;
	}

}
