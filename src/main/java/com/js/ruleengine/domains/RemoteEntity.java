/**
 *
 */
package com.js.ruleengine.domains;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

/**
 * @author goutam.mandal
 */
@Data
public abstract class RemoteEntity {

	@SuppressWarnings("rawtypes")
	protected Map	attributes;

	public Object getField(String... keys) {
		Object obj = attributes;
		for (String key : keys) {
			if (obj == null) {
				return null;
			}
			if (obj instanceof Map) {
				obj = ((Map) obj).get(key);
			}
		}
		return obj;
	}

	public Object getFieldWithDefaultValue(Object defaultValue , String... keys) {
		Object obj = attributes;
		for (String key : keys) {
			if (obj == null) {
				return defaultValue;
			}
			if (obj instanceof Map) {
				obj = ((Map) obj).get(key);
			}
		}
		if(obj == null) {
			return defaultValue;
		}
		return obj;
	}



	/**
	 * @param keys
	 * @return
	 * @return
	 */
	public void delField(String... keys) {
		Object obj = attributes;

		for (int i = 0; i < keys.length-1; i++) {
			if(obj == null) {
				return;
			}

			if (obj instanceof Map) {
				obj = ((Map) obj).get(keys[i]);
			}
		}

		if (obj != null) {
			((Map) obj).remove(keys[keys.length-1]);
		}
	}

	public void nullifyField(String... keys) {
		Object obj = attributes;

		for (int i = 0; i < keys.length-1; i++) {
			if(obj == null) {
				return;
			}

			if (obj instanceof Map) {
				obj = ((Map) obj).get(keys[i]);
			}
		}

		if (obj != null) {
			((Map) obj).put(keys[keys.length-1], null);
		}
	}

	public Map<String, Object> getMapField(String... keys) {
		return (Map<String, Object>) getField(keys);
	}

	public void addOrUpdateField(Object value, String... keys) {
		Object obj = attributes;
		for (int idx = 0; idx < keys.length - 1; idx++) {
			if (obj instanceof Map) {
				obj = ((Map) obj).get(keys[idx]);
			}
		}
		if (obj == null) {
			obj = new LinkedHashMap<>();
			attributes.put(keys[0], obj);
		}
		((Map) obj).put(keys[keys.length - 1], value);
	}
}