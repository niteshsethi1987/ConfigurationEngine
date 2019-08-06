/**
 *
 */
package com.js.common.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import orchestrator.exception.ArgumentException;

/**
 * @author goutam.mandal
 */
public class ProfileCheckSumUtil {

	private ProfileCheckSumUtil() {
		throw new IllegalStateException("Utility Class");
	}

	public static Integer profileCheckSumToProfileID(String pChecksum) {
		String profileid = null;
		if (pChecksum.length() >= 34) {
			profileid = pChecksum.substring(33);
			try {
				if (!pChecksum.equals(profileIDToProfileCheckSum(Integer.valueOf(profileid)))) {
					throw new ArgumentException("Invalid profile checksum " + pChecksum);
				}
			}
			catch (NumberFormatException e) {
				throw new ArgumentException("Invalid profile profileId " + profileid, e);
			}
		} else {
			profileid = pChecksum;
		}
		try {
			return Integer.valueOf(profileid);
		}
		catch (NumberFormatException e) {
			throw new ArgumentException("Invalid profile profileId " + profileid, e);
		}
	}

	public static Map<String, Integer> profileCheckSumToProfileID(Collection<String> pChecksums) {
		Map<String, Integer> profiles = new LinkedHashMap<>();
		if (pChecksums != null) {
			for (String pChecksum : pChecksums) {
				profiles.put(pChecksum, profileCheckSumToProfileID(pChecksum));
			}
		}
		return profiles;
	}

	public static String profileIDToProfileCheckSum(Integer profileId) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(StandardCharsets.UTF_8.encode(String.valueOf(profileId)));
			return String.format("%032x", new BigInteger(1, md5.digest())) + "i" + profileId;
		}
		catch (Exception e) {
			return "";
		}
	}

	public static Map<Integer, String> profileIDToProfileCheckSum(Collection<Integer> profileIds) {
		Map<Integer, String> profiles = new LinkedHashMap<>();
		if (profileIds != null) {
			for (Integer profileId : profileIds) {
				profiles.put(profileId, profileIDToProfileCheckSum(profileId));
			}
		}
		return profiles;
	}
}
