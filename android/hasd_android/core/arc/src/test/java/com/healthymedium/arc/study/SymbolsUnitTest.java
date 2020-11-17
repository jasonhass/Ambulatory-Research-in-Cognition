//
// SymbolsUnitTest.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;


import com.healthymedium.arc.paths.tests.SymbolTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RunWith(JUnit4.class)
public class SymbolsUnitTest {

    int MAX = 1000;
    Random random;
    List<Integer> symbols;
    int[] set;

    @Test
    public void test(){
        for(int i = 0; i < MAX; i++){
            setup(i);
            testNoDuplicateSymbols();
            testNoDuplicateCards();
            testNoInverseMatch();
        }
    }

    public void setup(int i){
        random = new Random(System.currentTimeMillis() + i);
        symbols =new ArrayList<Integer>();
        symbols.addAll(SymbolTest.symbolset);
        set = SymbolTest.generateNextRandomSet(random, symbols);
    }


    public void testNoDuplicateSymbols()
    {
        for(int i =0; i < 8; i = i + 2){
            Assert.assertNotEquals(set[i], set[i+1]);
        }
    }



    public void testNoDuplicateCards(){
        //testing cards 1 vs 2,3,4
        //testing cards 2 vs 1,3,4
        //testing cards 3 vs 1,2,4
        //testing cards 4 vs 1,2,3
        for(int x =0; x < 8; x = x + 2){
            for(int y = 0; y < 8; y = y +2){
                if(x != y){
                    boolean topSymbol = set[x] == set[y];
                    boolean botSymbol = set[x+1] == set[y+1];
                    Assert.assertFalse( topSymbol && botSymbol);
                }

            }

        }
    }


    public void testNoInverseMatch(){
        //testing cards 1 vs 2,3,4
        //testing cards 2 vs 1,3,4
        //testing cards 3 vs 1,2,4
        //testing cards 4 vs 1,2,3
        for(int x =0; x < 8; x = x + 2){
            for(int y = 0; y < 8; y = y +2){
                if(x != y){
                    boolean topSymbol = set[x] == set[y+1];
                    boolean botSymbol = set[x+1] == set[y];
                    Assert.assertFalse( topSymbol && botSymbol);
                }

            }

        }
    }
}
