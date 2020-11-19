//
// GridTest.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api.tests.data;

import java.util.List;

public class GridTest extends BaseData {

    public Double date;
    public List<GridTestSection> sections;

    public int getProgress(){

        int displayCounts = 0;
        int expectedDisplayCounts = 2;

        for(GridTestSection section : sections){
            if(section.displayTestGrid==null){
                continue;
            }
            if(section.displayTestGrid>0){
                displayCounts++;
            }
        }

        return (int) (100*((float)displayCounts/expectedDisplayCounts));
    }

}
