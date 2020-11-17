//
// ChipDrawable.java
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

public class ChipDrawable extends SimpleDrawable {

    public ChipDrawable(){
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

        int radius = height;

        path.moveTo(rect.left + radius, rect.top);

        // top line
        path.lineTo(rect.right - radius, rect.top);

        // right arc
        RectF rightRect = new RectF(rect.right - radius, rect.top, rect.right, rect.bottom);
        path.arcTo(rightRect, 270F, 180F, false);

        // bottom line
        path.lineTo(rect.left + radius, rect.bottom);

        // left arc
        RectF leftRect = new RectF(rect.left, rect.top, rect.left + radius, rect.bottom);
        path.arcTo(leftRect, 90F, 180F, false);

        path.close();

        return path;
    }


}
