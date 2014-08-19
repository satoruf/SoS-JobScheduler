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
package sos.ftphistory.db;

 
import java.util.Date;
import javax.persistence.*;

  
/**
* \class JadeFilesDBItem 
* 
* \brief JadeFilesDBItem - 
* 
* \details
*
* \section JadeFilesDBItem.java_intro_sec Introduction
*
* \section JadeFilesDBItem.java_samples Some Samples
*
* \code
*   .... code goes here ...
* \endcode
*
* <p style="`text-align:center`">
* <br />---------------------------------------------------------------------------
* <br /> APL/Software GmbH - Berlin
* <br />##### generated by ClaviusXPress (http://www.sos-berlin.com) #########
* <br />---------------------------------------------------------------------------
* </p>
* \author Uwe Risse
* \version 13.05.2011
* \see reference
*
* Created on 13.05.2011 16:42:58
 */

@Entity
@Table(name="SOSFTP_FILES")
public class JadeFilesDBItem {
	
	 private Long id;
	 private String mandator;
     private String sourceHost;
     private String sourceHostIp;
     private String sourceUser;
     private String sourceDir;
     private String sourceFilename;
     private String md5;
     private Integer fileSize;
     private Date   created;
     private String createdBy;
     private Date   modified;
     private String modifiedBy;
    // private List <JadeFilesHistoryDBItem> jadeFilesHistoryDBItems = new ArrayList <JadeFilesHistoryDBItem>();
  //   private Session session;



	/*@OneToMany(mappedBy="`sosftpId`")
	public List<JadeFilesHistoryDBItem> getJadeFilesHistoryDBItems() {
		return jadeFilesHistoryDBItems;
	}


	public void setJadeFilesHistoryDBItems(List<JadeFilesHistoryDBItem> jadeFilesHistoryDBItems) {
		this.jadeFilesHistoryDBItems = jadeFilesHistoryDBItems;
	}
*/

	public JadeFilesDBItem() {
		 
	}
      
 
     
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="`ID`",nullable=false)
    public Long getId() {
		return id;
	}

    @Column(name="`ID`",nullable=false)
	public void setId(Long id) {
		this.id = id;
	}

    @Column(name="`MANDATOR`",nullable=false)
	public void setMandator(String mandator) {
		this.mandator = mandator;
	}
 
    @Column(name="`MANDATOR`",nullable=false)
	public String getMandator() {
		return mandator;
	}

    
    @Column(name="`FILE_SIZE`",nullable=false)
    public Integer getFileSize() {
		return fileSize;
	}


    @Column(name="`FILE_SIZE`",nullable=false)
	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}
 


   @Column(name="`SOURCE_HOST`", nullable=true)
	public String getSourceHost() {
 		return sourceHost;
	}

   @Column(name="`SOURCE_HOST`", nullable=true)
	public void setSourceHost(String sourceHost) {
		this.sourceHost = sourceHost;
	} 

   @Column(name="`SOURCE_HOST_IP`",nullable=false)
	public void setSourceHostIp(String sourceHostIp) { 
	this.sourceHostIp = sourceHostIp;
   }

   @Column(name="`SOURCE_HOST_IP`",nullable=false)
  	public String getSourceHostIp() {
		return sourceHostIp;
   }

   
   @Column(name="`SOURCE_USER`",nullable=false)
	public void setSourceUser(String sourceUser) {
		this.sourceUser = sourceUser;
	}
   
   @Column(name="`SOURCE_USER`",nullable=false)
	public String getSourceUser() {
		return sourceUser;
	}

   @Column(name="`SOURCE_DIR`",nullable=false)
	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}
   
   @Column(name="`SOURCE_DIR`",nullable=false)
	public String getSourceDir() {
		return sourceDir;
	}

   @Column(name="`SOURCE_FILENAME`",nullable=false)
	public void setSourceFilename(String sourceFilename) {
		this.sourceFilename = sourceFilename;
	}
   
   @Column(name="`SOURCE_FILENAME`",nullable=false)
	public String getSourceFilename() {
		return sourceFilename;
	}
 
   @Column(name="`MD5`",nullable=false)
	public void setMd5(String md5) {
		this.md5 = md5;
	}
   
   @Column(name="`MD5`",nullable=false)
	public String getMd5() {
		return md5;
	}

  
    

   @Column(name="`MODIFIED_BY`",nullable=false)
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
   
   @Column(name="`MODIFIED_BY`",nullable=false)
	public String getModifiedBy() {
		return modifiedBy;
	}


   @Column(name="`CREATED_BY`",nullable=false)
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
   
   @Column(name="`CREATED_BY`",nullable=false)
	public String getCreatedBy() {
		return createdBy;
	}


   @Column(name="`CREATED`", nullable=false)
   @Temporal(TemporalType.TIMESTAMP)
   public Date getCreated() {
		return created;
	}
  
   @Column(name="`CREATED`", nullable=false)
   @Temporal(TemporalType.TIMESTAMP)
	public void setCreated(Date created) {
		this.created = created;
	}

   @Column(name="`MODIFIED`", nullable=true)
   @Temporal(TemporalType.TIMESTAMP)
	public Date getModified() {
		return modified;
	}

   @Column(name="`MODIFIED`", nullable=true)
   @Temporal(TemporalType.TIMESTAMP)
	public void setModified(Date modified) {;
		this.modified = modified;
	}

   
/*   public void addHistory(JadeFilesHistoryDBItem history) {
	   this.getJadeFilesHistoryDBItems().add(history);
	   history.setJadeFilesDBItem(this);
   }

   //save the 1 and all n HistoryItems for 1
   public void save() {
		session.save(this);
		for (int i = 0;i<jadeFilesHistoryDBItems.size();i++) {
			JadeFilesHistoryDBItem h = (JadeFilesHistoryDBItem)jadeFilesHistoryDBItems.get(i);
			session.save(h);
		}
   }
*/
}