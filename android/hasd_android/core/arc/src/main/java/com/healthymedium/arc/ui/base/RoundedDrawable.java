//
// RoundedDrawable.java
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

public class RoundedDrawable extends SimpleDrawable {

    private int radiusTopLeft;
    private int radiusTopRight;
    private int radiusBottomLeft;
    private int radiusBottomRight;

    public RoundedDrawable(){
        super();
    }

    @Override
    protected void updateOffsets() {

        // create a rect that's small enough that the stroke isn't cut off
        int offset = (int) (strokeWidth/2);
        rect.set(offset,offset,width-offset,height-offset);

        if(drawStroke) {
            int maxRadius = Math.min(rect.width() / 2, rect.height() / 2);

            if (radiusTopLeft > maxRadius) {
                radiusTopLeft = maxRadius;
            }
            if (radiusTopRight > maxRadius) {
                radiusTopRight = maxRadius;
            }
            if (radiusBottomLeft > maxRadius) {
                radiusBottomLeft = maxRadius;
            }
            if (radiusBottomRight > maxRadius) {
                radiusBottomRight = maxRadius;
            }
        }
    }

    public void setRadius(int radius) {
        radiusTopLeft = radius;
        radiusTopRight = radius;
        radiusBottomLeft = radius;
        radiusBottomRight = radius;
    }

    public void setRadius(int topLeft, int topRight, int bottomLeft, int bottomRight) {
        radiusTopLeft = topLeft;
        radiusTopRight = topRight;
        radiusBottomLeft = bottomLeft;
        radiusBottomRight = bottomRight;
    }


    protected Path getPath(Rect rect) {

        Path path = new Path();

        path.moveTo(rect.left + radiusTopLeft, rect.top);

        // top line
        path.lineTo(rect.right - radiusTopRight, rect.top);

        // top right corner
        RectF topRightRect = new RectF(rect.right - radiusTopRight,rect.top,rect.right,rect.top+radiusTopRight);
        path.arcTo(topRightRect, 270F, 90F, false);

        // right line
        path.lineTo(rect.right, rect.bottom - radiusBottomRight);

        // bottom right corner
        RectF bottomRightRect = new RectF(rect.right - radiusBottomRight,rect.bottom - radiusBottomRight,rect.right,rect.bottom);
        path.arcTo(bottomRightRect, 0F, 90F, false);

        // bottom line
        path.lineTo(rect.left + radiusBottomLeft, rect.bottom);

        // bottom left corner
        RectF bottomLeftRect = new RectF(rect.left,rect.bottom - radiusBottomLeft,rect.left+radiusBottomLeft,rect.bottom);
        path.arcTo(bottomLeftRect, 90F, 90F, false);

        // left side
        path.lineTo(rect.left, rect.top + radiusTopLeft);

        // top left corner
        RectF topLeftRect = new RectF(rect.left,rect.top,rect.left+radiusTopLeft,rect.top+radiusTopLeft);
        path.arcTo(topLeftRect, 180F, 90F, false);

        path.close();

        return path;
    }


}
