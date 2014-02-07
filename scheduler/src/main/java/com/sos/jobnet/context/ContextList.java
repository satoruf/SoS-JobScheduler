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
package com.sos.jobnet.context;

import com.sos.jobnet.classes.NodeStatus;
import com.sos.jobnet.db.JobNetPlanDBItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @uthor ss
 * at 09.09.13 16:41
 */
public class ContextList {

    private final List<JobNetPlanDBItem> nodesInContext;
    private final Map<NodeStatus,Integer> statusCounter = new HashMap<NodeStatus,Integer>();

    public ContextList(List<JobNetPlanDBItem> nodesInContext) {
        this.nodesInContext = nodesInContext;
        setStatusCounter();
    }

    private void setStatusCounter() {
        for(JobNetPlanDBItem item : nodesInContext) {
            NodeStatus status = NodeStatus.valueOf(item.getStatusNode());
            Integer count = (statusCounter.containsKey(status)) ? statusCounter.get(status) : 0;
            count++;
            statusCounter.put(status,count);
        }
    }

    private Integer countNodesWithStatus(NodeStatus status) {
        return (statusCounter.containsKey(status)) ? statusCounter.get(status) : 0;
    }

    public boolean hasRunningNodes() {
        return (countNodesWithStatus(NodeStatus.RUNNING) > 0);
    }

    public void changeStatus() {

    }
}