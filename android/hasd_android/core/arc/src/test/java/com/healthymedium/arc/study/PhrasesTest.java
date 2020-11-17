//
// PhrasesTest.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;

import com.healthymedium.arc.utilities.Phrase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PhrasesTest {

    private static final String testSpacer = "\n- - - - - - - - - -\n";

    @Test
    public void testFormatPhoneNumber() {
        String mockReceived = "+10987654321"; //number format of phone number on server
        String formatted = Phrase.formatPhoneNumber(mockReceived);

        String logMsg = String.format(
                "TEST_formatPhoneNumber\n" +
                        "\tunformatted:\t\"%s\"\n" +
                        "\tformatted:\t\t\"%s\"%s",
                mockReceived,
                formatted,
                testSpacer
        );

        System.out.print(logMsg);
    }

}
