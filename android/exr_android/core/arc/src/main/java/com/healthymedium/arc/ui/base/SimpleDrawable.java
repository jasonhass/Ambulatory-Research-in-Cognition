//
// SimpleDrawable.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui.base;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewOutlineProvider;


public abstract class SimpleDrawable extends Drawable {

    protected boolean drawStroke = false;
    protected boolean drawFill = false;
    protected boolean alphaBlock = false;
    protected SimpleGradient fillGradient;
    protected SimpleGradient strokeGradient;

    protected Path path;
    protected Paint fillPaint;
    protected Paint strokePaint;
    protected float strokeWidth;

    // only used for drawing
    protected Rect rect;
    protected int height;
    protected int width;

    public SimpleDrawable(){

        // initialize member variables
        rect = new Rect(0,0,0,0);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        width = bounds.width();
        height = bounds.height();
        invalidate();
    }

    public void invalidate() {

        // create a rect that's small enough that the stroke isn't cut off
        updateOffsets();

        path = getPath(rect);

        if(fillGradient!=null){
            fillPaint.setShader(fillGradient.getShader(width,height));
        }
        if(strokeGradient!=null){
            strokePaint.setShader(strokeGradient.getShader(width,height));
        }
    }

    protected void updateOffsets() {
        int offset = (int) (strokeWidth/2);
        rect.set(offset,offset,width-offset,height-offset);
    }

    @Override
    public void draw(Canvas canvas) {
        if(path==null){
            return;
        }
        if(alphaBlock){
            return;
        }
        if(drawFill){
            canvas.drawPath(path,fillPaint);
        }
        if(drawStroke) {
            canvas.drawPath(path,strokePaint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        fillPaint.setAlpha(alpha);
        strokePaint.setAlpha(alpha);
        alphaBlock = (alpha==0);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        fillPaint.setColorFilter(colorFilter);
        strokePaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    public void setFillColor(int color) {
        drawFill = color!=0;
        fillPaint.setColor(color);
    }

    public void setFillShader(Shader shader) {
        drawFill = true;
        fillPaint.setShader(shader);
    }

    public void setStrokeColor(int color) {
        drawStroke = color!=0;
        strokePaint.setColor(color);
    }

    public void setStrokeWidth(float width) {
        strokeWidth = width;
        strokePaint.setStrokeWidth(width);
    }

    public void setStrokeDash(float length, float spacing) {
        strokePaint.setPathEffect(new DashPathEffect(new float[]{length,spacing}, 0));
    }

    public void removeStrokeDash() {
        strokePaint.setPathEffect(null);
    }

    //default to fill gradient
    public void setGradient(int gradientId, int colorFirst, int colorSecond) {
        setFillGradient(gradientId,colorFirst,colorSecond);
    }

    public void setFillGradient(int gradientId, int colorFirst, int colorSecond) {
        fillGradient = SimpleGradient.getGradient(gradientId,colorFirst,colorSecond);
        if(fillGradient!=null){
            drawFill = true;
        }
    }

    public void setStrokeGradient(int gradientId, int colorFirst, int colorSecond) {
        strokeGradient = SimpleGradient.getGradient(gradientId,colorFirst,colorSecond);
        if(strokeGradient!=null){
            drawStroke = true;
        }
    }


    public void setStrokeEnabled(boolean enabled){
        drawStroke = enabled;
    }

    public void setFillEnabled(boolean enabled){
        drawFill = enabled;
    }

    public Paint getFillPaint() {
        return fillPaint;
    }

    public Paint getStrokePaint() {
        return strokePaint;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public ViewOutlineProvider getOutlineProvider() {
        return new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                if (path != null) {
                    if(path.isConvex()) {
                        outline.setConvexPath(path);
                        return;
                    }
                }
                outline.setRect(0, 0, width, height);
            }
        };
    }

    protected abstract Path getPath(Rect rect);

}
