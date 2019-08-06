package com.js.ruleengine.domains;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.text.WordUtils;

import com.js.ruleengine.constants.NameOfUserError;
import com.js.ruleengine.constants.PhotoShowStatus;
import com.js.ruleengine.constants.ProfileNonVisibilityReason;

@Slf4j
@ToString(callSuper = true)
@SuppressWarnings("unchecked")
public class JsProfile extends RemoteEntity {

	private static final Pattern REMOVE_WORDS_PAID;
	private static final Pattern REMOVE_WORDS_FREE;
	private static final Pattern REQUIRE_ALPHABET;

	static {
		String pattern = "(^(%s)(\\s).*?)(?=[a-zA-Z])";
		REMOVE_WORDS_PAID = Pattern.compile(String.format(pattern, "mr|ms|miss"), Pattern.CASE_INSENSITIVE);
		REMOVE_WORDS_FREE = Pattern.compile(String.format(pattern, "mr|ms|miss|dr"), Pattern.CASE_INSENSITIVE);
		REQUIRE_ALPHABET = Pattern.compile("[a-zA-Z]", Pattern.CASE_INSENSITIVE);
	}

	private void handleBlockerCase(Map<String, Object> reason) {
		Object idSection = getField("id");
		Object gender = getField("bi", "gender");
		attributes.clear();
		attributes.put("be", reason);
		addOrUpdateField(idSection, "id");
		addOrUpdateField(gender, "bi", "gender");
	}

	public void markLoginMust() {
		handleBlockerCase(ProfileNonVisibilityReason.LOGIN_MUST_REGISTERED.asMap());
	}

	public void markNotActivated() {
		handleBlockerCase(ProfileNonVisibilityReason.NOT_ACTIVATED.asMap());
	}

	public void markHidden() {
		handleBlockerCase(ProfileNonVisibilityReason.HIDDEN.asMap());
	}

	public void markUnderScreening() {
		handleBlockerCase(ProfileNonVisibilityReason.UNDERSCREENING.asMap());
	}

	public void markDeleted() {
		handleBlockerCase(ProfileNonVisibilityReason.DELETED.asMap());
	}

	public void markBackendDeleted() {
		handleBlockerCase(ProfileNonVisibilityReason.DELETED_BACKEND.asMap());
	}

	public void markDppFiltered() {
		handleBlockerCase(ProfileNonVisibilityReason.FILTERED.asMap());
	}

	public void markContactPrivacy() {
		handleBlockerCase(ProfileNonVisibilityReason.SHOW_CONTACTED.asMap());
	}

	public void markBlocked() {
		handleBlockerCase(ProfileNonVisibilityReason.BLOCKED.asMap());
	}

	public void markIncomplete() {
		handleBlockerCase(ProfileNonVisibilityReason.INCOMPLETE.asMap());
	}

	public void markNotPhoneVerfied() {
		handleBlockerCase(ProfileNonVisibilityReason.MARK_PHONE_NOT_VERFIIED.asMap());
	}

	public void markPgProfileIncomplete() {
		handleBlockerCase(ProfileNonVisibilityReason.PG_PROFILE_INCOMPLETE.asMap());
	}

	public void markPgProfileDeleted() {
		handleBlockerCase(ProfileNonVisibilityReason.PG_PROFILE_DELETED.asMap());
	}

	/** Photos section starts here **/
	public void hidePhoto(String reason) {
		PhotoShowStatus imageShowStatus = PhotoShowStatus.valueOf(reason);
		if (imageShowStatus == PhotoShowStatus.SHOW) {
			return;
		}
		if (attributes.get("aph") != null) {
			((Map) attributes.get("aph")).clear();
		}
		addOrUpdateField(imageShowStatus.asMap(), "aph", "be");
	}

	public void removeUnscreenedPhotos() {

		/**
		 * If all photos get null because of show photos cases xls.
		 */
		Map allPhotosMap = (Map) attributes.get("aph");
		if (allPhotosMap.containsKey("be")) {
			return;
		}

		Integer count = (Integer) ((Map) attributes.get("aph")).get("count");
		if (count == 0 || count == null) {
			return;
		}
		List<Map<String, Object>> allPhotos = (List<Map<String, Object>>) ((Map) attributes.get("aph")).get("items");
		Iterator<Map<String, Object>> it = allPhotos.iterator();
		while (it.hasNext()) {
			Map<String, Object> photo = it.next();
			if (!(Boolean) photo.get("screened")) {
				count--;
				it.remove();
			}
		}
		((Map) attributes.get("aph")).put("count", count);
	}

	/** Photos section ends here **/

	/** Name Section Starts **/
	public void markPgNameNotFilled() {
		attributes.put("nu", Collections.singletonMap("be", NameOfUserError.SELF_NAME_NOT_FILLED.asMap()));
	}

	public void markPgNameNotScreened() {
		attributes.put("nu", Collections.singletonMap("be", NameOfUserError.SELF_NAME_NOT_SCREENED.asMap()));
	}

	@SuppressWarnings("rawtypes")
	public void markValidName() {
		String name = (String) ((Map) attributes.get("nu")).get("name");
		if (StringUtils.isBlank(name) || !REQUIRE_ALPHABET.matcher(name).find()) {
			markPogNameNotFilled();
			return;
		}
		name = getName(name, true);
		((Map) attributes.get("nu")).put("name", name);
	}

	public void markPgSelfNameHidden() {
		attributes.put("nu", Collections.singletonMap("be", NameOfUserError.SELF_NAME_HIDDEN.asMap()));
	}

	public void markPogNameNotFilled() {
		attributes.put("nu", Collections.singletonMap("be", NameOfUserError.POG_NAME_NOT_FILLED.asMap()));
	}

	public void markPogNameNotScreened() {
		attributes.put("nu", Collections.singletonMap("be", NameOfUserError.POG_NAME_NOT_SCREENED.asMap()));
	}

	public void markPogNameHidden() {
		attributes.put("nu", Collections.singletonMap("be", NameOfUserError.POG_NAME_HIDDEN.asMap()));
	}

	public void markNotLoggedIn() {
		attributes.put("nu", Collections.singletonMap("be", NameOfUserError.NOT_LOGGED_IN.asMap()));
	}

	@SuppressWarnings("rawtypes")
	public void abbreviatePogName() {
		String name = (String) ((Map) attributes.get("nu")).get("name");
		if (StringUtils.isBlank(name) || !REQUIRE_ALPHABET.matcher(name).find()) {
			markPogNameNotFilled();
			return;
		}
		name = getName(name, false);
		((Map) attributes.get("nu")).put("name", name);
	}

	protected String getName(String name, boolean isPaid) {
		if (name == null) {
			return "";
		}
		String modName = name.replaceAll("\\.", " ");
		modName = modName.replaceAll("( )+", " ");
		modName = modName.trim();
		modName = WordUtils.capitalize(modName, ' ', ',', '\'');
		if (isPaid) {
			modName = REMOVE_WORDS_PAID.matcher(modName).replaceAll("");
		} else {
			modName = REMOVE_WORDS_FREE.matcher(modName).replaceAll("");
			String[] parts = modName.split(" ");
			StringBuilder nameBuilder = new StringBuilder();
			for (int idx = 0; idx < parts.length - 1; idx++) {
				nameBuilder.append(parts[idx].charAt(0));
				nameBuilder.append(" ");
			}
			nameBuilder.append(parts[parts.length - 1]);
			if (parts.length == 1) {
				modName = String.valueOf(nameBuilder.charAt(0));
			} else {
				modName = nameBuilder.toString();
			}
		}
		modName = WordUtils.capitalizeFully(modName);
		return modName;
	}

	private static final String NAME_HIDDEN_MASK = "[Name Hidden]";
	public void hideNameInYourInfo() {

		Map<String, Object> bi = (Map) attributes.get("bi");
		String yourInfo = (String) bi.get("yourinfo");

		if (attributes.get("nu") == null
				|| StringUtils.isBlank((String) ((Map) attributes.get("nu")).get("name"))
				|| StringUtils.isBlank(yourInfo)) {
			return;
		}

		String name = (String) ((Map) attributes.get("nu")).get("name");
		log.debug("Name :: {}", name);
		log.debug("Before :: {}", yourInfo);

		long startTime = System.currentTimeMillis();

		name = REMOVE_WORDS_FREE.matcher(name).replaceAll("").trim();
		String[] nameTokens = name.split(" ");
		if (nameTokens[0].length() < 3) {
			log.debug("POG FirstName less than 3 chars. Skipping hide name from your info.");
			return;
		}

		StringBuilder searchTerm = new StringBuilder(name);
		if (nameTokens.length > 2) {
			searchTerm.append("|").append(nameTokens[0]).append(" ").append(nameTokens[nameTokens.length - 1]);
		}
		if (nameTokens.length > 1) {
			searchTerm.append("|").append(nameTokens[0]).append(nameTokens[nameTokens.length - 1]);
			searchTerm.append("|").append(nameTokens[0]);
		}

		log.debug("To hide name search term :: {}", searchTerm);
		yourInfo = yourInfo.replaceAll("(?i)(" + searchTerm + ")", NAME_HIDDEN_MASK);

		bi.put("yourinfo", yourInfo);

		long execTime = System.currentTimeMillis() - startTime;
		if (execTime > 1) {
			log.debug("Method containing regex matching took {} ms", execTime);
		}

		log.debug("After :: {}", yourInfo);
	}

	/** Name Section Ends **/

	public void nullifyUnscreenedField(String key) {
		attributes.forEach((sectionName, sectionDetails) -> {
			if (sectionName != "sc" && sectionDetails != null && sectionDetails instanceof Map && ((Map) sectionDetails).get(key) != null) {
				((Map) sectionDetails).put(key, null);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public boolean checkOrderingPhoto(String type) {
		MutableBoolean ifExists = new MutableBoolean(false);
		Map response = (Map) attributes.get("aph");
		if (response == null || response.get("items") == null) {
			return ifExists.getValue();
		}
		ArrayList<Object> items = (ArrayList<Object>) response.get("items");

		if (items == null || items.isEmpty()) {
			return ifExists.getValue();
		}

		items.forEach(item -> {
			Map pictureObj = (Map) item;
			if ((Integer) pictureObj.get("ordering") == 0 && (type.equals("doesNotMatter") || (Boolean) pictureObj.get("screened"))) {
				ifExists.setValue(true);
				return;
			}
		});

		return ifExists.getValue();
	}

	@SuppressWarnings("unchecked")
	public void copyFieldAtRoot(String key1, String key2, String key3, Integer index) {

		Map response = (Map) attributes.get(key2);
		if (response == null) {
			return;
		}
		ArrayList<Object> items = (ArrayList<Object>) response.get(key3);
		if (items == null || items.isEmpty()) {
			return;
		}
		response.put(key1, items.get(0));
	}

	public Collection<?> getIntersection(Collection<?> c1, Collection<?> c2) {
		if (c1 == null || c2 == null) {
			return null;
		}

		Collection<?> copyC1 = new ArrayList<>(c1);
		copyC1.retainAll(c2);
		return copyC1;
	}
}