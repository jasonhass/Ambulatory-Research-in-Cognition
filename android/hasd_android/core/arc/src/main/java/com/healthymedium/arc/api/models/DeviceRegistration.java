//
// DeviceRegistration.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api.models;

public class DeviceRegistration {

    public String device_id;           // the unique id for this device
    public String participant_id;      // the user's participant id
    public String device_info;         // format "OS name|device model|OS version", ie "iOS|iPhone8,4|10.1.1"
    public String app_version;         // version of the app
    public String authorization_code;  // the authorization code entered by the user
    public Boolean override;           // (optional) set to true if re-registering an existing user

}
