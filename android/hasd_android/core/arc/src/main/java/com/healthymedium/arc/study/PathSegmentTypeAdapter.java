//
// PathSegmentTypeAdapter.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.healthymedium.arc.utilities.PreferencesManager;

import java.lang.reflect.Type;

public class PathSegmentTypeAdapter implements JsonSerializer<PathSegment>, JsonDeserializer<PathSegment> {

    @Override
    public PathSegment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        PathSegment newSegment = new PathSegment();

        JsonObject obj = json.getAsJsonObject();


        if(obj.has("currentIndex"))
        {
            JsonElement currentIndex = obj.get("currentIndex");
            newSegment.currentIndex = currentIndex.getAsInt();
        }

        if(obj.has("dataObject"))
        {
            try {
                JsonElement dataObject = obj.get("dataObject");

                // Previous versions did not store the dataObject in this way, so we need to check and
                // make sure we're not trying to deserialize an old version (because this won't work!)
                if(dataObject.isJsonObject() && ((JsonObject)dataObject).has("data")) {
                    JsonObject d = dataObject.getAsJsonObject();
                    Class actualClass = null;
                    actualClass = Class.forName(d.get("actual_class").getAsString());
                    Object data = PreferencesManager.getInstance().getGson().fromJson(d.get("data"), actualClass);

                    if (PathSegmentData.class.isAssignableFrom(data.getClass())) {
                        newSegment.dataObject = (PathSegmentData) data;
                    }
                }
                else
                {
                    newSegment.dataObject = context.deserialize(dataObject, PathSegmentData.class);
                }
            }
            catch (ClassNotFoundException e) {

                e.printStackTrace();
            }
        }

        return newSegment;
    }

    @Override
    public JsonElement serialize(PathSegment pathSegment, Type srcType, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("currentIndex", new JsonPrimitive(pathSegment.currentIndex));
        if (pathSegment.dataObject != null) {


            JsonObject jsonDataObject = new JsonObject();
            jsonDataObject.add("data", PreferencesManager.getInstance().getGson().toJsonTree(pathSegment.dataObject));
            jsonDataObject.addProperty("actual_class",  pathSegment.dataObject.getClass().getName());

            result.add("dataObject", jsonDataObject);
        }
        return result;
    }

}
