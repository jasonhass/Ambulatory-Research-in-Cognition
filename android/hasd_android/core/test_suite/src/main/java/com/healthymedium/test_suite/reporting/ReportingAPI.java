//
// ReportingAPI.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.test_suite.reporting;

import com.healthymedium.test_suite.core.TestReport;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ReportingAPI {

    @POST("send-report")
    Call<ResponseBody> submitReport(@Body TestReport report);

    @POST("send-screenshot")
    Call<ResponseBody> submitScreenshot(@Body RequestBody screenshot);

}
