/*
===================
Copyright (c) 2013 Riverbed Technology, Inc.

MAITI is licensed under the terms and conditions of the MIT License as set forth in LICENSE.TXT, which accompanies the software.  MAITI is distributed “AS IS” as set forth in the MIT License.
===================
*/
package com.riverbed.mobile.android.apmlib;

import java.util.Random;

final class Utility {

	private static final String RANDOM_LETTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private static StringBuilder appPerf_SB;
	private static Random appPerf_RandomGenerator = null;
	
	/**
	 * this method will generate a random string of given length
	 * @param maxNumber number of digits
	 * @return random String 
	 */
	static String generate_Random(int maxNumber)
	{
		if (appPerf_RandomGenerator == null) 
			appPerf_RandomGenerator = new Random();
		
		synchronized (appPerf_RandomGenerator)
		{
			appPerf_SB = new StringBuilder();
			for (int n=0; n<maxNumber; n++)
				appPerf_SB.append(RANDOM_LETTERS.charAt(appPerf_RandomGenerator.nextInt(
					  RANDOM_LETTERS.length())));
			return appPerf_SB.toString();
		}
	}
}
