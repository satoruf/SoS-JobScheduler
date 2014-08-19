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
package com.sos.scheduler.history.db;


 
import static org.junit.Assert.assertEquals;

import java.io.File;

import java.text.ParseException;
import java.util.List;
import org.hibernate.Query;
import org.junit.Test;

import com.sos.hibernate.layer.SOSHibernateIntervalDBLayer;
import com.sos.scheduler.history.SchedulerTaskHistoryFilter;
 
/**
*  
* \class SchedulerHistoryDBLayer
* \brief SchedulerHistoryDBLayer - 
* 
* \details
*
* \section SchedulerHistoryDBLayer.java_intro_sec Introduction
*
* \section SchedulerHistoryDBLayer.java_samples Some Samples
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
* \author Uwe Risse
* \version 27.09.2011
* \see reference
*
* Created on 27.09.2011 14:40:18
 */

public class SchedulerTaskHistoryDBLayer extends SOSHibernateIntervalDBLayer{

	@SuppressWarnings("unused")
	private final String	conClassName			= "SchedulerTaskHistoryDBLayer";
    protected SchedulerTaskHistoryFilter  filter    = null;

	 
	public SchedulerTaskHistoryDBLayer(File configurationFile_) {
		
		super();


		this.setConfigurationFile(configurationFile_);
		this.resetFilter();
		
	}

 
	
	public SchedulerTaskHistoryDBItem get(Long id) {
		initSession();
		SchedulerTaskHistoryDBItem schedulerHistoryDBItem = null;
		try {
			schedulerHistoryDBItem = (SchedulerTaskHistoryDBItem)this.getSession().get(SchedulerTaskHistoryDBItem.class,id);
			return schedulerHistoryDBItem;
    	}catch (Exception e) {
	  	  return null;
	    }
	}
	
	public void resetFilter() {
	    this.filter = new SchedulerTaskHistoryFilter();
        this.filter.setDateFormat("yyyy-MM-dd HH:mm:ss");
        this.filter.setOrderCriteria("startTime");
        this.filter.setSortMode("desc");

	}
	
	public SchedulerTaskHistoryFilter getFilter() {
		return filter;
	}

	public void setFilter(SchedulerTaskHistoryFilter  filter_) {
		filter = filter_;
	}

	protected String getWhere() {
		 

		String where = "";
		String and = "";
		if (filter.getSchedulerId() != null && !filter.getSchedulerId().equals("")) {
			where += and + " spoolerId=:schedulerId";
			and = " and ";
		}
		if (filter.getJobname() != null && !filter.getJobname().equals("")) {
			where += and + " jobName=:jobName";
			and = " and ";
		}
		 

		if (filter.getSeverity() != null && filter.getSeverity().hasValue()) {
			where += and + " error=:severity";
			and = " and ";
		}

		if (filter.getStartTime() != null ) {
			where += and + " startTime>= :startTime";
			and = " and ";
		}

		if (filter.getEndTime() != null ) {
			where += and + " endTime <= :endTime ";
			and = " and ";
		}
		if (where.trim().equals("")) {

		}
		else {
			where = "where " + where;
		}
		return where;

	}
    
    protected String getWhereFromTo() {
        return getWhereFromToStart();
    }

    protected String getWhereFromToStart() {
        return getWhereFromTo("startTime");
    }
    
    protected String getWhereFromToEnd() {
        return getWhereFromTo("endTime");
    }
    
	protected String getWhereFromTo(String fieldname_date_field) {
        String where = "";
        String and = "";

        if (filter.getSchedulerId() != null && !filter.getSchedulerId().equals("")) {
            where += and + " spoolerId=:schedulerId";
            and = " and ";
        }
        
        if (filter.getJobname() != null && !filter.getJobname().equals("")) {
            if (filter.getJobname().contains("%")) {
                where += and + " jobName like :jobName";
            }else {
                where += and + " jobName=:jobName";
            }
            and = " and ";
        }
        
        
        if (filter.getExecutedUtcFrom() != null ) {
            where += and + fieldname_date_field + " >= :startTimeFrom";
            and = " and ";
        }

        if (filter.getExecutedUtcTo() != null ) {
            where += and + fieldname_date_field +  " <= :startTimeTo ";
            and = " and ";
        }

        if (filter.isShowSuccessfull() ) {
            where += and  + " exitCode=0";
            and = " and ";
        }
        
 
        if (filter.isShowSuccessfull() ) {
            where += and  + " exitCode=0";
            and = " and ";
        }
        

        if (where.trim().equals("")) {

        }
        else {
            where = "where " + where;
        }
        return where;

    }
     
	@Override
	public int deleteInterval() {

		if (session == null) {
			beginTransaction();
		}

		String hql = "delete from SchedulerTaskHistoryDBItem " + getWhereFromTo();

		Query query = session.createQuery(hql);
		if (filter.getExecutedUtcFrom()!= null ) {
    	   query.setTimestamp("startTimeFrom", filter.getExecutedUtcFrom());
		}
		if (filter.getExecutedUtcTo()!= null ) {
		   query.setTimestamp("startTimeTo", filter.getExecutedUtcTo());
		}

		int row = query.executeUpdate();

		return row;
	}
 
	 
	
	public int delete() {

		if (session == null) {
			beginTransaction();
		}

		String hql = "delete from SchedulerTaskHistoryDBItem " + getWhereFromTo();

		Query query = session.createQuery(hql);

		if (filter.getSchedulerId() != null && filter.getSchedulerId() != "") {
			query.setText("schedulerId", filter.getSchedulerId());
		}
		
		if (filter.getSeverity() != null) {
			query.setInteger("severity", filter.getSeverity().getIntValue());
		}
		
		if (filter.getJobname() != null && filter.getJobname() != "") {
			query.setText("jobName", filter.getJobname());
		}
		
		if (filter.getExecutedUtcFrom() != null ) {
			query.setTimestamp("startTimeFrom", filter.getExecutedUtcFrom());
		}
				
		if (filter.getExecutedUtcTo() != null ) {
			query.setTimestamp("startTimeTo", filter.getExecutedUtcTo());
		}

		int row = query.executeUpdate();

		return row;
	}
	
 
	private List<SchedulerTaskHistoryDBItem>  executeQuery(Query query, int limit) {
	    if (filter.getSchedulerId() != null && !filter.getSchedulerId().equals("")) {
            query.setText("schedulerId", filter.getSchedulerId());
        }
        
        if (filter.getSeverity() != null) {
            query.setInteger("severity", filter.getSeverity().getIntValue());
        }
        
        if (filter.getJobname() != null && !filter.getJobname().equals("")) {
            query.setText("jobName", filter.getJobname());
        }
        
        if (filter.getExecutedUtcFrom() != null) {
            query.setTimestamp("startTimeFrom", filter.getExecutedUtcFrom());
        }
        if (filter.getExecutedUtcTo() != null ) {
            query.setTimestamp("startTimeTo", filter.getExecutedUtcTo());
        }

        if (limit > 0) {
            query.setMaxResults(limit);
        }

        List<SchedulerTaskHistoryDBItem> schedulerHistoryList = query.list();
        return schedulerHistoryList;
	}

    public List<SchedulerTaskHistoryDBItem> getSchedulerHistoryListFromTo() {
        int limit = this.getFilter().getLimit();
        initSession();
        

        Query query = session.createQuery("from SchedulerTaskHistoryDBItem " + getWhereFromTo() + filter.getOrderCriteria() + filter.getSortMode());
        return executeQuery(query,limit);

    }

    public List<SchedulerTaskHistoryDBItem> getSchedulerHistoryListFromToStart() {
        int limit = this.getFilter().getLimit();
        initSession();
        

        Query query = session.createQuery("from SchedulerTaskHistoryDBItem " + getWhereFromToStart() + filter.getOrderCriteria() + filter.getSortMode());
        return executeQuery(query,limit);

    }

    public List<SchedulerTaskHistoryDBItem> getSchedulerHistoryListFromToEnd() {
        int limit = this.getFilter().getLimit();
        initSession();
        

        Query query = session.createQuery("from SchedulerTaskHistoryDBItem " + getWhereFromToStart() + filter.getOrderCriteria() + filter.getSortMode());
        return executeQuery(query,limit);

    }
	
	   public List<SchedulerTaskHistoryDBItem> getSchedulerHistoryListSchedulersFromTo() {
	        int limit = this.getFilter().getLimit();
	        initSession();
	        
            String q = "from SchedulerTaskHistoryDBItem e where e.spoolerId IN (select distinct e.spoolerId from SchedulerTaskHistoryDBItem " +  getWhereFromTo() +")";         
//            String q = "from SchedulerTaskHistoryDBItem  "  + getWhereFromTo() + " group by spoolerId";         
	        Query query = session.createQuery(q);
	        return executeQuery(query,limit);
	    }
	   
	
	   
	   
	      
  
	public List<SchedulerTaskHistoryDBItem> getHistoryItems()   {
		int limit = this.getFilter().getLimit();
		initSession();

 		Query query = session.createQuery("from SchedulerTaskHistoryDBItem " + getWhere() +  this.filter.getOrderCriteria() + this.filter.getSortMode()) ;

		if (filter.getSchedulerId() != null && filter.getSchedulerId() != "") {
			query.setText("schedulerId", filter.getSchedulerId());
		}
		
		if (filter.getSeverity() != null) {
			query.setInteger("severity", filter.getSeverity().getIntValue());
		}
		
		if (filter.getJobname() != null && filter.getJobname() != "") {
			query.setText("jobName", filter.getJobname());
		}
		
		if (filter.getStartTime() != null ) {
			query.setTimestamp("startTime", filter.getStartTime());
		}
		if (filter.getEndTime() != null && !filter.getEndTime().equals("")) {
			query.setTimestamp("endTime", filter.getEndTime());
		}

		if (limit > 0) {
			query.setMaxResults(limit);
		}

		List<SchedulerTaskHistoryDBItem> historyList = query.list();
 		return historyList;

	}

	public SchedulerTaskHistoryDBItem getHistoryItem()   {
		this.filter.setLimit(1);
		initSession();

 		Query query = session.createQuery("from SchedulerTaskHistoryDBItem " + getWhere() +  this.filter.getOrderCriteria() + this.filter.getSortMode()) ;

		if (filter.getSchedulerId() != null && filter.getSchedulerId() != "") {
			query.setText("schedulerId", filter.getSchedulerId());
		}
		
		if (filter.getSeverity() != null) {
			query.setInteger("severity", filter.getSeverity().getIntValue());
		}
		
		if (filter.getJobname() != null && filter.getJobname() != "") {
			query.setText("jobName", filter.getJobname());
		}
		
		if (filter.getStartTime() != null ) {
			query.setTimestamp("startTime", filter.getStartTime());
		}
		if (filter.getEndTime() != null && !filter.getEndTime().equals("")) {
			query.setTimestamp("endTime", filter.getEndTime());
		}

		if (this.filter.getLimit() > 0) {
			query.setMaxResults(this.filter.getLimit());
		}

		List<SchedulerTaskHistoryDBItem> historyList = query.list();
 		if (historyList.size() > 0) {
			return historyList.get(0);
		}else {
			return null;
		}
 
	}





}