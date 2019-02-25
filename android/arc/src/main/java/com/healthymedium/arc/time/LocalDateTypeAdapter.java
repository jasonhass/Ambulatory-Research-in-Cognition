//
// LocalDateTypeAdapter.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.time;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.IOException;

public final class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter out, LocalDate date) throws IOException {
        if (date == null) {
            out.nullValue();
        } else {
            out.value(new DateTime().withTimeAtStartOfDay().withDate(date).getMillis());
        }
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        switch (in.peek()) {
            case NULL:
                in.nextNull();
                return null;
            default:
                long millis = in.nextLong();
                DateTime dateTime = new DateTime(millis);
                return dateTime.toLocalDate();
        }
    }

}
