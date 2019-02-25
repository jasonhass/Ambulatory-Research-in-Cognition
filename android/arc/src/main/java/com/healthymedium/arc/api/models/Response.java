//
// Response.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api.models;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Response {

    public int code;
    public boolean successful;
    public JsonObject optional = new JsonObject();
    public JsonObject errors = new JsonObject();

}
