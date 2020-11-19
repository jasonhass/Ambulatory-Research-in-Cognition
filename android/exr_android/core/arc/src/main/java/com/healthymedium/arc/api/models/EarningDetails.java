//
// EarningDetails.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.api.models;

import java.util.ArrayList;
import java.util.List;

public class EarningDetails {

    public String total_earnings;
    public List<Cycle> cycles;

    public EarningDetails(){
        total_earnings = new String();
        cycles = new ArrayList<>();
    }

    public static class Cycle {
        public Integer cycle;
        public String total;
        public Long start_date;
        public Long end_date;
        public List<Goal> details;

        public Cycle() {
            cycle = new Integer(-1);
            total = new String();
            end_date = new Long(0);
            details = new ArrayList<>();
        }
    }

    public static class Goal {
        public String name;
        public String value;
        public Integer count_completed;
        public String amount_earned;
    }

}
