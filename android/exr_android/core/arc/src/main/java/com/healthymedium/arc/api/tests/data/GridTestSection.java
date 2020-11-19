//
// GridTestSection.java
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

public class GridTestSection {

    @SerializedName("display_symbols")
    public Double displaySymbols;
    @SerializedName("display_distraction")
    public Double displayDistraction;
    @SerializedName("display_test_grid")
    public Double displayTestGrid;
    @SerializedName("e_count")
    public int eCount;
    @SerializedName("f_count")
    public int fCount;
    public List<GridTestImage> images;
    public List<GridTestTap> choices;

}
