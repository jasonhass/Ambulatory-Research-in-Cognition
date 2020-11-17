//
// SymbolTestBehavior.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.behaviors.tests;



import com.healthymedium.test_suite.behaviors.Behavior;
import com.healthymedium.test_suite.utilities.Capture;
import com.healthymedium.test_suite.utilities.SymbolTestImageMatcher;
import com.healthymedium.test_suite.utilities.UI;
import com.healthymedium.arc.core.BaseFragment;
import com.healthymedium.arc.library.R;

import org.junit.Assert;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class SymbolTestBehavior extends Behavior {

    @Override
    public void onOpened(BaseFragment fragment){
        super.onOpened(fragment);
        UI.sleep(2000);

        int[] symbolId = new int[]{
                R.id.symbolbutton_top1,
                R.id.symbolbutton_top2,
                R.id.symbolbutton_top3,
                R.id.symbolbutton_bottom1,
                R.id.symbolbutton_bottom2,
        };
        for(int i = 0; i < 12;i++){

            //Even index are the top symbol, Odd index are the bottom symbol
            ArrayList<Integer> symbols = new ArrayList<>();

            for (int value : symbolId) {
                onView(withId(value)).check(matches(new SymbolTestImageMatcher()));
                symbols.add(SymbolTestImageMatcher.top);
                symbols.add(SymbolTestImageMatcher.bottom);
            }


            //symbols prettyprint for debugging
            StringBuilder symbolsStringBuilder = new StringBuilder("\n");
            for(int x = 0; x < 10; x=x+2){
                String tile = "";
                switch (x){
                    case 0:
                        tile = "Top 1: ";
                        break;
                    case 2:
                        tile = "Top 2: ";
                        break;
                    case 4:
                        tile = "Top 3: ";
                        break;
                    case 6:
                        tile = "Bottom 1: ";
                        break;
                    case 8:
                        tile = "Bottom 2: ";
                        break;
               }
                symbolsStringBuilder.append(tile).append("[ ").append(symbols.get(x)).append(", ").append(symbols.get(x + 1)).append(" ]\n");
            }
            String symbolsFormatted = symbolsStringBuilder.toString();

            //checking for exact match from the top tile against the bottom tile
            boolean exactMatch = false;
            for(int x = 0; x < 6; x=x+2){
                for(int y=6; y < 10; y=y+2){
                    if(symbols.get(y).equals(symbols.get(x)) &&
                        symbols.get(y+1).equals(symbols.get(x+1))){

                        Assert.assertFalse("There should only be one exact match." + symbolsFormatted, exactMatch);
                        exactMatch = true;
                    }
                }
            }
            
            Assert.assertTrue(
                    "There is no exact match with the top 3 tiles against the bottom 2 tiles." + symbolsFormatted,
                    exactMatch);




            Capture.takeScreenshot(fragment, getClass().getSimpleName(), "test_"+i);
            UI.click(R.id.symbolbutton_bottom1);
            UI.sleep(1000);
        }

    }

}
