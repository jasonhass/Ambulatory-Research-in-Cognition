//
// PricesTestSection.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr.legacy_migration.models;

public class PricesTestSection {

    public int goodPrice;
    public Double stimulusDisplayTime = new Double(0);;
    public Double questionDisplayTime = new Double(0);;
    public String item;
    public String price;
    public String altPrice;
    public int correctIndex;
    public int selectedIndex;
    public Double selectionTime = new Double(0);;

    public PricesTestSection(){
        goodPrice = 99;
        item = "";
    }

}
