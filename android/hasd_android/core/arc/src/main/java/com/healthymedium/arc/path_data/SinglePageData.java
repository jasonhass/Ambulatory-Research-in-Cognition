//
// SinglePageData.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.path_data;

import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.study.PathSegmentData;

/*
TODO: We should consider just getting rid of this class. The only place that it was being used
was for the QuestionInterrupted Fragment, which handled setting its own data, and returned null from
onDataCollection(). So practically speaking, this class didn't do anything.

For Gson serialization purposes, we need onProcess to return a BaseData object, but PathSegmentData's
objects property can't be changed to List<BaseData> without having to propagate a LOT of changes to
method return values and classes (like BaseFragments' onDataCollection()).
 */

public class SinglePageData extends PathSegmentData {


    public SinglePageData(){
        super();

    }

    @Override
    protected BaseData onProcess() {
//        if(objects.size()>0){
//            return objects.get(0);
//        }
        return null;
    }
}
