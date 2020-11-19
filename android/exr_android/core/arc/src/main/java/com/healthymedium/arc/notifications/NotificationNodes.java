//
// NotificationNodes.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.notifications;

import com.healthymedium.arc.utilities.PreferencesManager;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NotificationNodes {

    static private final String NOTIFICATION_NODES = "notification_nodes";
    static private final String NOTIFICATION_REQUEST_INDEX = "notification_request_index";

    private List<NotificationNode> nodes;
    private int requestIndex;

    public void load(){
        if(PreferencesManager.getInstance().contains(NOTIFICATION_NODES)) {
            NotificationNode[] nodeArrays = PreferencesManager.getInstance().getObject(NOTIFICATION_NODES, NotificationNode[].class);
            nodes = Collections.synchronizedList(new ArrayList<>(Arrays.asList(nodeArrays)));
        } else {
            nodes = Collections.synchronizedList(new ArrayList<NotificationNode>());
        }
        requestIndex = PreferencesManager.getInstance().getInt(NOTIFICATION_REQUEST_INDEX,0);
    }

    public void save(){
        PreferencesManager.getInstance().putObject(NOTIFICATION_NODES,nodes.toArray());
        PreferencesManager.getInstance().putInt(NOTIFICATION_REQUEST_INDEX,requestIndex);
    }

    public boolean remove(List<NotificationNode> removeNodes){
        boolean result = nodes.remove(removeNodes);
        save();
        return result;
    }

    public boolean remove(NotificationNode node){
        boolean result = nodes.remove(node);
        save();
        return result;
    }

    public NotificationNode add(Integer type, Integer id, DateTime time){
        requestIndex++;
        NotificationNode node = new NotificationNode(id,type,requestIndex,time);
        nodes.add(node);
        save();
        return node;
    }

    public NotificationNode get(int type, int sessionId){
        int size = nodes.size();
        for(int i=0;i<size;i++){
            if(nodes.get(i).id==sessionId && nodes.get(i).type==type){
                return nodes.get(i);
            }
        }
        return null;
    }

    public List<NotificationNode> getAll(){
        return nodes;
    }

}
