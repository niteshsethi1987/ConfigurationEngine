/**
 *
 */
package com.js.common.utils;

import com.js.ruleengine.constants.ETagConstants;


/**
 * @author goutam.mandal
 */
public class EtagUtil {

	private EtagUtil() {
		throw new UnsupportedOperationException("Utility Class !");
	}

	public static String getEtag() {
		return String.valueOf((System.currentTimeMillis() / ETagConstants.MICRO_DIFFERENCE_ETAG));
	}

	public static String generateEtagKey(Integer profileId,String keyType) {
		return keyType+ profileId;
	}

}
