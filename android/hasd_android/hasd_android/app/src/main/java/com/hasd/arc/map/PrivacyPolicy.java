//
// PrivacyPolicy.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.hasd.arc.map;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class PrivacyPolicy extends com.healthymedium.arc.study.PrivacyPolicy {

    public PrivacyPolicy() {
        super();
    }

    @Override
    public void show(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wustl.edu/about/compliance-policies/computers-internet-policies/internet-privacy-policy/"));
        context.startActivity(intent);
    }
}
