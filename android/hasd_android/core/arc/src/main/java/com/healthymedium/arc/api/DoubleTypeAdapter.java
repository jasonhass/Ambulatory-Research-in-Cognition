//
// DoubleTypeAdapter.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.MathContext;

public class DoubleTypeAdapter implements JsonSerializer<Double> {

    @Override
    public JsonElement serialize(Double src, Type srcType, JsonSerializationContext context) {
        MathContext mc = new MathContext(15);
        BigDecimal value = BigDecimal.valueOf(src);
        return new JsonPrimitive(new BigDecimal(value.doubleValue(), mc));
    }

}
