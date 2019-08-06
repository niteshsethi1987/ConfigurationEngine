/**
 *
 */
package com.js.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author goutam.mandal
 *
 */
public class ViewType {

	private ViewType() {
		throw new UnsupportedOperationException("Constants class");
	}

	public static final String			BASIC			= "basic";
	public static final String			VCARD			= "vcard";
	public static final String			SHORT			= "shortview";
	public static final String			SUPER			= "super";
	public static final String			PD				= "pd";
	public static final String			SYS				= "sys";

	public static final List<String>	ALLOWED_VIEWS	= Collections.unmodifiableList(Arrays.asList(ViewType.VCARD, ViewType.BASIC,
																ViewType.SHORT, ViewType.SUPER, ViewType.PD));
}
