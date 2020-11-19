//
// TestSession.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.dian.arc.exr.legacy_migration.models;

import org.joda.time.DateTime;

public class TestSession {

    public int version = 2;

    public String arcId;
    public String deviceInfo;

    public String deviceId;
    public int visitId;
    public int sessionId;

    public Double sessionDate = new Double(0);
    public Double startTime = new Double(0);
    public Double completeTime = new Double(0);
    public Double expirationDate = new Double(0);

    public ChronotypeSurvey chronotypeSurvey;
    public WakeSurvey wakeSurvey;
    public ContextSurvey contextSurvey;
    public GridTest gridTest;
    public PricesTest pricesTest;
    public SymbolsTest symbolsTest;

    public int finishedSession;
    public int missedSession;
    public int interrupted;
    public int willUpgradePhone;

    public DateTime getSessionDate() {
        return new DateTime(sessionDate.longValue()*1000);
    }


    public void purgeData(){
        arcId = null;
        deviceInfo = null;

        startTime = null;
        completeTime = null;
        expirationDate = null;

        chronotypeSurvey = null;
        wakeSurvey = null;
        contextSurvey = null;
        gridTest = null;
        pricesTest = null;
        symbolsTest = null;

        finishedSession = 0;
        missedSession = 0;
        interrupted = 0;
        willUpgradePhone = 0;
    }

    public void ensureInit(){
        if(chronotypeSurvey==null){
            chronotypeSurvey = new ChronotypeSurvey();
        }
        if(wakeSurvey==null){
            wakeSurvey = new WakeSurvey();
        }
        if(contextSurvey==null){
            contextSurvey = new ContextSurvey();
        }
        if(gridTest==null){
            gridTest = new GridTest();
        }
        if(pricesTest==null){
            pricesTest = new PricesTest();
        }
        if(symbolsTest==null){
            symbolsTest = new SymbolsTest();
        }
    }

}
