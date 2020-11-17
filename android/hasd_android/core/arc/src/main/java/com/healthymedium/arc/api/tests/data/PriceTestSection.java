//
// PriceTestSection.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api.tests.data;

import com.google.gson.annotations.SerializedName;

public class PriceTestSection {

    @SerializedName("good_price")
    public Integer goodPrice;
    @SerializedName("stimulus_display_time")
    public Double stimulusDisplayTime;
    @SerializedName("question_display_time")
    public Double questionDisplayTime;
    public String item;
    public String price;
    @SerializedName("alt_price")
    public String altPrice;
    @SerializedName("correct_index")
    public Integer correctIndex;
    @SerializedName("selected_index")
    public Integer selectedIndex;
    @SerializedName("selection_time")
    public Double selectionTime;

}
