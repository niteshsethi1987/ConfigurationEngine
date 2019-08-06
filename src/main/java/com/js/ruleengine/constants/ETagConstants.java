package com.js.ruleengine.constants;

public class ETagConstants {

	private ETagConstants() {
		throw new UnsupportedOperationException("Constants File");
	}

	public static final String ETAG_PROFILE_KEY = "profileETagTmp:";
	public static final String ETAG_PROFILE_HEADER_SET = "eTagProfileHeaderSet";
	public static final String ETAG_VALID_PROFILE_ETAG_MAP = "eTagValidProfileETagList";
	public static final String ETAG_INVALID_PROFILE_ETAG_MAP = "eTagInValidProfileEtagList";
	public static final String ORIGINAL_PROFILEID_LIST = "originalProfileIdList";
	public static final String TYPE = "eTagType";
	public static final String VALUE = "eTagValue";
	public static final String PROFILE_ID = "pid";
	public static final int TYPE_STALE = 0;
	public static final int TYPE_FRESH = 1;
	public static final String ETAG = "eTag";
	public static final String RESPONSE_CODE = "status";
	public static final String RESPONSE_CODE_200 = "200";
	public static final String RESPONSE_CODE_304 = "304";
	public static final String FINAL_RESPONSE = "finalResponse";
	public static final long MICRO_DIFFERENCE_ETAG = 10;
	public static final String ETAG_SECTION = "eTs";
}
