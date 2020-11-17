//
// UpdateHASD.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.translations;

import com.healthymedium.translations.common.CopyDoc;

public class UpdateHASD {

    public static void main(String[] args) {

        System.out.println("");
        System.out.println("HealthyMedium Translation Tool ---------");
        System.out.println("Updating HASD --------------------------\n");

        CopyDoc copyDoc = new CopyDoc();
        if(!copyDoc.init()) {
            return;
        }
        copyDoc.updateHASD();

        System.out.println("\nthat's all folks");
    }

}
