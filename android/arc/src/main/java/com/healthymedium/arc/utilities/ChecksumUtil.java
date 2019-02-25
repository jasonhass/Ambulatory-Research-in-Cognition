//
// ChecksumUtil.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.utilities;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChecksumUtil {

    public static boolean checkMD5(String md5, File updateFile) {
        if (TextUtils.isEmpty(md5) || updateFile == null) {
            Log.e(ChecksumUtil.class.getSimpleName(), "ChecksumUtil string empty or updateFile null");
            return false;
        }

        String calculatedDigest = calculateMD5(updateFile);
        if (calculatedDigest == null) {
            Log.e(ChecksumUtil.class.getSimpleName(), "calculatedDigest null");
            return false;
        }

        Log.v(ChecksumUtil.class.getSimpleName(), "Calculated digest: " + calculatedDigest);
        Log.v(ChecksumUtil.class.getSimpleName(), "Provided digest: " + md5);

        return calculatedDigest.equalsIgnoreCase(md5);
    }

    public static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("ChecksumUtil");
        } catch (NoSuchAlgorithmException e) {
            Log.e(ChecksumUtil.class.getSimpleName(), "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e(ChecksumUtil.class.getSimpleName(), "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for ChecksumUtil", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(ChecksumUtil.class.getSimpleName(), "Exception on closing ChecksumUtil input stream", e);
            }
        }
    }
}
