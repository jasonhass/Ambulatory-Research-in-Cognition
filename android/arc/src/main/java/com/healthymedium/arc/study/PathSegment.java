//
// PathSegment.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;

import android.util.Log;

import com.healthymedium.arc.api.tests.data.BaseData;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.utilities.NavigationManager;

import java.util.ArrayList;
import java.util.List;

public class PathSegment {

    public List<BaseFragment> fragments = new ArrayList<>();
    public int currentIndex;
    public PathSegmentData dataObject;

    public PathSegment(){

    }

    public PathSegment(List<BaseFragment> fragments, Class dataClass){
        this.fragments = fragments;
        this.currentIndex = -1;
        if(PathSegmentData.class.isAssignableFrom(dataClass)){
            try {
                dataObject = (PathSegmentData) dataClass.newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("PathSegment", "created using invalid class ("+dataClass.getName()+")");
        }

    }

    public PathSegment(List<BaseFragment> fragments){
        this.fragments = fragments;
        this.currentIndex = -1;
        dataObject = new PathSegmentData();
    }

    public boolean openNext(){
        currentIndex++;
        if(fragments.size() > currentIndex) {
            fragments.get(currentIndex);
            NavigationManager.getInstance().open(fragments.get(currentIndex));
            return true;
        } else {
            NavigationManager.getInstance().open(new BaseFragment());
        }
        return false;
    }

    public boolean openNext(int skips){
        if(skips==0){
            return openNext();
        }
        if(fragments.size() > currentIndex+skips) {
            for(int i=0;i<skips+1;i++){
                currentIndex++;
                NavigationManager.getInstance().open(fragments.get(currentIndex));
            }
            return true;
        } else {
            BaseFragment fragment = new BaseFragment();
            NavigationManager.getInstance().open(fragment);
        }
        return false;
    }

    public boolean openPrevious(int skips){
        if(currentIndex-skips < 0){
            return false;
        }
        for(int i=0;i<skips+1;i++){
            if(!fragments.get(currentIndex).isBackAllowed()){
                return false;
            }
            currentIndex--;
            NavigationManager.getInstance().popBackStack();
        }

        return true;
    }

    public BaseData collectData(){
        int size = fragments.size();
        for(int i=0;i<size;i++){
            Object obj = fragments.get(i).onDataCollection();
            if(obj!=null){
                dataObject.add(obj);
            }
        }
        return dataObject.process();
    }

}
