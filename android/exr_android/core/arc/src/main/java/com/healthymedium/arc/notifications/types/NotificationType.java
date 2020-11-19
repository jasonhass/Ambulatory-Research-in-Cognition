//
// NotificationType.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.notifications.types;

import android.content.Context;
import android.support.annotation.RawRes;

import com.healthymedium.arc.notifications.NotificationNode;

public abstract class NotificationType {

    protected int id;
    protected int importance;
    protected String channelId;
    protected String channelName;
    protected String channelDesc;
    protected String extra;
    protected boolean proctored;
    protected boolean showBadge = true;

    @RawRes
    protected int soundResource = -1;

    public NotificationType(){

    }

    public int getId(){
        return id;
    }

    public String getChannelId(){
        return channelId;
    }

    public String getChannelName(){
        return channelName;
    }

    public String getChannelDesc(){
        return channelDesc;
    }

    public boolean hasExtra(){
        return extra!=null;
    }

    public String getExtra(){
        return extra;
    }

    public int getImportance(){
        return importance;
    }

    public boolean isProctored(){
        return proctored;
    }

    public boolean shouldShowBadge(){
        return showBadge;
    }

    public int getSoundResource() {
        return soundResource;
    }

    // abstract methods ----------------------------------------------------------------------------

    public abstract String getContent(NotificationNode node);

    // the return value dictates whether or not the notification is shown to the user
    public abstract boolean onNotifyPending(NotificationNode node);

}
