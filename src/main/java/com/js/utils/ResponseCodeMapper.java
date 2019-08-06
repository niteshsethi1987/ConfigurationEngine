/**
 *
 */
package com.js.utils;

import org.springframework.http.HttpStatus;

/**
 * @author goutam.mandal
 */
public class ResponseCodeMapper {

	private ResponseCodeMapper() {
		throw new UnsupportedOperationException("Utility Class !");
	}

	public static HttpStatus getHttpStatus(int internalResponseCode) {
		HttpStatus externalHttpStatus;
		switch (internalResponseCode) {
			case 4000:
			case 4001:
			case 4002:
			case 4003:
			case 4004:
			case 4005:
			case 4006:
			case 4007:
			case 4008:
			case 4009:
				externalHttpStatus = HttpStatus.BAD_REQUEST;
				break;
			case 5000:
			case 5001:
			case 5002:
			case 5003:
			case 5004:
			case 5005:
			case 5006:
			case 5007:
			case 5008:
			case 5009:
				externalHttpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
				break;
			default:
				externalHttpStatus = HttpStatus.OK;
		}
		return externalHttpStatus;
	}
}
