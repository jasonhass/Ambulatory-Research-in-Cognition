//
// AsyncLoader.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.utilities;

import android.os.AsyncTask;
import com.healthymedium.arc.core.LoadingDialog;

public class AsyncLoader extends AsyncTask<AsyncLoader.Listener, Void, Void> {

    LoadingDialog dialog;

    @Override
    protected Void doInBackground(Listener... listeners) {
        for(int i=0;i<listeners.length;i++){
            listeners[i].onExecute();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new LoadingDialog();
        dialog.show(NavigationManager.getInstance().getFragmentManager(),"LoadingDialog");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
    }

    public interface Listener{
        void onExecute();
    }
}

