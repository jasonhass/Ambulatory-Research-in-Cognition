//
// Grid2ChoiceDialog.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.ui;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.DrawableRes;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.healthymedium.arc.core.Application;
import com.healthymedium.arc.library.R;
import com.healthymedium.arc.ui.base.PointerDialog;
import com.healthymedium.arc.utilities.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class Grid2ChoiceDialog extends PointerDialog {

    Grid2ChoiceView phone;
    Grid2ChoiceView key;
    Grid2ChoiceView pen;

    TextView textViewGridDialog;
    Listener listener;

    TextView textViewRemoveItem;
    View divider;

    public Grid2ChoiceDialog(Activity activity, View target, int pointerConfig) {
        super(activity, target, null, pointerConfig);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_grid2_choice,null);

        OnTouchListener touchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() != MotionEvent.ACTION_DOWN) {
                    return false;
                }
                if(listener==null){
                    return false;
                }
                int id = ((Grid2ChoiceView)v).getDrawableImageId();
                listener.onSelected(id);
                dismiss();
                return false;
            }
        };

        phone = view.findViewById(R.id.phone);
        phone.setImage(R.drawable.phone);
        phone.setOnTouchListener(touchListener);

        key = view.findViewById(R.id.key);
        key.setImage(R.drawable.key);
        key.setOnTouchListener(touchListener);

        pen = view.findViewById(R.id.pen);
        pen.setImage(R.drawable.pen);
        pen.setOnTouchListener(touchListener);

        textViewGridDialog = view.findViewById(R.id.textViewGridDialog);
        textViewGridDialog.setText("Select the item that was here");

        divider = view.findViewById(R.id.divider);
        divider.setVisibility(View.GONE);
        textViewRemoveItem = view.findViewById(R.id.textViewRemoveItem);
        textViewRemoveItem.setVisibility(View.GONE);

        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        setRadius(16);
        setView(view);
    }

    public void disableChoice(@DrawableRes int image) {
        Grid2ChoiceView choice = null;

        if(image==R.drawable.phone) {
            choice = phone;
        }
        if(image==R.drawable.key) {
            choice = key;
        }
        if(image==R.drawable.pen) {
            choice = pen;
        }

        if(choice==null){
            return;
        }

        choice.setOnTouchListener(null);
        choice.setAlpha(0.4f);

        divider.setVisibility(View.VISIBLE);
        ViewUtil.underlineTextView(textViewRemoveItem);
        textViewRemoveItem.setVisibility(View.VISIBLE);

        OnTouchListener removeImageTouchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() != MotionEvent.ACTION_DOWN) {
                    return false;
                }
                if(listener==null){
                    return false;
                }
                listener.onRemove();
                dismiss();
                return false;
            }
        };
        textViewRemoveItem.setOnTouchListener(removeImageTouchListener);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onSelected(@DrawableRes int image);
        void onRemove();
    }

}
