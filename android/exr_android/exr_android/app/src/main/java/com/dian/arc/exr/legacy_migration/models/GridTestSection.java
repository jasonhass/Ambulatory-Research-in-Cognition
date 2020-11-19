//
// GridTestSection.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr.legacy_migration.models;

import java.util.ArrayList;
import java.util.List;

public class GridTestSection {

    public Double displaySymbols = new Double(0);
    public Double displayDistraction = new Double(0);
    public Double displayTestGrid = new Double(0);
    public int eCount;
    public int fCount;
    public List<GridTestImage> images = new ArrayList<>();
    public List<GridTestTap> choices = new ArrayList<>();

}
