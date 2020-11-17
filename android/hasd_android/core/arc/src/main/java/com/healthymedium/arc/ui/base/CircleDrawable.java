//
// CircleDrawable.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui.base;


import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

public class CircleDrawable extends SimpleDrawable {

    public CircleDrawable(){
        super();
    }

    @Override
    protected void updateOffsets() {
        // create a rect that's small enough that the stroke isn't cut off
        int offset = (int) (strokeWidth/2);
        rect.set(offset,offset,width-offset,height-offset);
    }

    protected Path getPath(Rect rect) {

        Path path = new Path();
        float radius = Math.min(rect.width() / 2, rect.height() / 2);
        path.addCircle(rect.centerX(),rect.centerY(),radius, Path.Direction.CCW);
        path.close();

        return path;
    }


}
