//
// SymbolTestSection.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api.tests.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SymbolTestSection {

    @SerializedName("appearance_time")
    public Double appearanceTime;
    @SerializedName("selection_time")
    public Double selectionTime;
    public Integer selected;
    public Integer correct;
    public List<List<String>> options;
    public List<List<String>> choices;

}
