package com.dian.arc.libs.rest.models;

import com.dian.arc.libs.utilities.ArcManager;
import com.dian.arc.libs.utilities.Config;
import com.dian.arc.libs.utilities.JodaUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class TestSession {

    private int version = Config.REST_API_VERSION;

    private String arcId;
    private String deviceInfo;
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    private String deviceId;
    private int visitId;
    private int sessionId;

    private Double sessionDate = new Double(0);
    private Double startTime = new Double(0);
    private Double completeTime = new Double(0);
    private Double expirationDate = new Double(0);

    private ChronotypeSurvey chronotypeSurvey;
    private WakeSurvey wakeSurvey;
    private ContextSurvey contextSurvey;
    private GridTest gridTest;
    private PricesTest pricesTest;
    private SymbolsTest symbolsTest;

    private int finishedSession;
    private int missedSession;
    private int interrupted;
    private int willUpgradePhone;




    public TestSession(int visitId, int sessionId){
        deviceInfo = ArcManager.getInstance().getDeviceInfo();
        arcId = ArcManager.getInstance().getArcId();
        this.visitId = visitId;
        this.sessionId = sessionId;
    }

    public void setCompleted(boolean finished){
        completeTime = JodaUtil.toUtcDouble(DateTime.now());
        finishedSession = (finished)? 1:0;
    }

    public boolean isSessionFinished(){
        return finishedSession==1;
    }

    public void setMissedSession(){
        missedSession = 1;
    }

    public DateTime getExpirationDate() {
        return new DateTime(expirationDate.longValue()*1000);
    }

    public String getArcId(){
        return arcId;
    }

    public Integer getVisitId(){
        return visitId;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public DateTime getSessionDate() {
        return new DateTime(sessionDate.longValue()*1000);
    }

    public void setSessionDate(DateTime sessionDate) {
        this.sessionDate = JodaUtil.toUtcDouble(sessionDate);
        this.expirationDate = JodaUtil.toUtcDouble(sessionDate.plusHours(2));
    }

    public DateTime getStartTime() {
        return new DateTime(startTime.longValue()*1000);
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = JodaUtil.toUtcDouble(startTime);
    }

    public double getCompleteTime() {
        return completeTime;
    }

    public ChronotypeSurvey getChronotypeSurvey() {
        return chronotypeSurvey;
    }

    public void setChronotypeSurvey(ChronotypeSurvey chronotypeSurvey) {
        this.chronotypeSurvey = chronotypeSurvey;
    }

    public WakeSurvey getWakeSurvey() {
        return wakeSurvey;
    }

    public void setWakeSurvey(WakeSurvey wakeSurvey) {
        this.wakeSurvey = wakeSurvey;
    }

    public ContextSurvey getContextSurvey() {
        return contextSurvey;
    }

    public void setContextSurvey(ContextSurvey contextSurvey) {
        this.contextSurvey = contextSurvey;
    }

    public GridTest getGridTest() {
        if(gridTest==null){
            gridTest = new GridTest();
            gridTest.setDate(JodaUtil.toUtcDouble(DateTime.now()));
        }
        return gridTest;
    }

    public void setGridTest(GridTest gridTest) {
        this.gridTest = gridTest;
    }

    public PricesTest getPricesTest() {
        if(pricesTest==null){
            List<PricesTestSection> sections = new ArrayList<>();
            for(int i=0;i<10;i++){
                sections.add(new PricesTestSection());
            }
            pricesTest = new PricesTest();
            pricesTest.setDate(JodaUtil.toUtcDouble(DateTime.now()));
            pricesTest.setSections(sections);
        }
        return pricesTest;
    }

    public void setPricesTest(PricesTest pricesTest) {
        this.pricesTest = pricesTest;
    }

    public SymbolsTest getSymbolsTest() {
        if(symbolsTest==null){
            List<SymbolsTestSection> sections = new ArrayList<>();
            for(int i=0;i<12;i++){
                sections.add(new SymbolsTestSection());
            }
            symbolsTest = new SymbolsTest();
            symbolsTest.setDate(JodaUtil.toUtcDouble(DateTime.now()));
            symbolsTest.setSections(sections);
        }
        return symbolsTest;
    }

    public void setSymbolsTest(SymbolsTest symbolsTest) {
        this.symbolsTest = symbolsTest;
    }

    public Boolean getInterrupted() {
        return interrupted==1;
    }

    public void setInterrupted(Boolean interrupted) {
        this.interrupted = (interrupted) ? 1:0;
    }

    public void setWillUpgradePhone(Boolean willUpgradePhone) {
        this.willUpgradePhone = (willUpgradePhone) ? 1:0;
    }

    public boolean isCurrentlyInTestSession(){
        if(startTime > 0 && completeTime == 0){
            return true;
        }
        return false;
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
