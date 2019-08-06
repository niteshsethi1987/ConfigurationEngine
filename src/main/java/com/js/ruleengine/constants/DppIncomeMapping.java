/**
 *
 */
package com.js.ruleengine.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @author goutam.mandal
 */
public class DppIncomeMapping {

	private static Map<Integer, List<Integer>> allowedDppIncomeMap;
	private static List<Integer> incomeRs;
	private static List<Integer> incomeDol;

	private DppIncomeMapping() {
		throw new UnsupportedOperationException("Static class !");
	}

	static {
		loadAllowedDppIncomeMap();
	}

	private static void loadAllowedDppIncomeMap() {

		Map<Integer, List<Integer>> map = new HashMap<>();

		map.put(15, Arrays.asList(8, 9, 10, 11, 12, 13, 21, 14, 2, 3, 4, 5, 6, 16, 17, 18, 20, 22, 23, 24, 25, 26, 27, 15));
		map.put(2, Arrays.asList(8, 9, 10, 11, 12, 13, 21, 14, 2, 3, 4, 5, 6, 16, 17, 18, 20, 22, 23, 24, 25, 26, 27, 15));
		map.put(3, Arrays.asList(8, 9, 10, 11, 12, 13, 21, 14, 3, 4, 5, 6, 16, 17, 18, 20, 22, 23, 24, 25, 26, 27));
		map.put(4, Arrays.asList(8, 9, 10, 11, 12, 13, 21, 14, 4, 5, 6, 16, 17, 18, 20, 22, 23, 24, 25, 26, 27));
		map.put(5, Arrays.asList(8, 9, 10, 11, 12, 13, 21, 14, 5, 6, 16, 17, 18, 20, 22, 23, 24, 25, 26, 27));
		map.put(6, Arrays.asList(9, 10, 11, 12, 13, 21, 14, 6, 16, 17, 18, 20, 22, 23, 24, 25, 26, 27));
		map.put(16, Arrays.asList(9, 10, 11, 12, 13, 21, 14, 16, 17, 18, 20, 22, 23, 24, 25, 26, 27));
		map.put(17, Arrays.asList(10, 11, 12, 13, 21, 14, 17, 18, 20, 22, 23, 24, 25, 26, 27));
		map.put(18, Arrays.asList(11, 12, 13, 21, 14, 18, 20, 22, 23, 24, 25, 26, 27));
		map.put(20, Arrays.asList(12, 13, 21, 14, 20, 22, 23, 24, 25, 26, 27));
		map.put(22, Arrays.asList(13, 21, 14, 22, 23, 24, 25, 26, 27));
		map.put(23, Arrays.asList(21, 14, 23, 24, 25, 26, 27));
		map.put(24, Arrays.asList(21, 14, 24, 25, 26, 27));
		map.put(25, Arrays.asList(14, 25, 26, 27));
		map.put(26, Arrays.asList(14, 26, 27));
		map.put(27, Arrays.asList(14, 27));
		map.put(8, Arrays.asList(8, 9, 10, 11, 12, 13, 21, 14, 2, 3, 4, 5, 6, 16, 17, 18, 20, 22, 23, 24, 25, 26, 27, 15));
		map.put(9, Arrays.asList(9, 10, 11, 12, 13, 21, 14, 6, 16, 17, 18, 20, 22, 23, 24, 25, 26, 27));
		map.put(10, Arrays.asList(10, 11, 12, 13, 21, 14, 16, 17, 18, 20, 22, 23, 24, 25, 26, 27));
		map.put(11, Arrays.asList(11, 12, 13, 21, 14, 18, 20, 22, 23, 24, 25, 26, 27));
		map.put(12, Arrays.asList(12, 13, 21, 14, 20, 22, 23, 24, 25, 26, 27));
		map.put(13, Arrays.asList(13, 21, 14, 22, 23, 24, 25, 26, 27));
		map.put(21, Arrays.asList(21, 14, 23, 24, 25, 26, 27));
		map.put(14, Arrays.asList(14, 24, 25, 26, 27));

		allowedDppIncomeMap = Collections.unmodifiableMap(map);

		incomeRs = Collections.unmodifiableList(Arrays.asList(2, 3, 4, 5, 6, 16, 17, 18, 20, 22, 23, 24, 25, 26, 27));
		incomeDol = Collections.unmodifiableList(Arrays.asList(8, 9, 10, 11, 12, 13, 21, 14));
	}

	public static boolean isAllowedDppIncome(Integer pgIncome, List<Integer> pogDppIncome, String lIncomeDol, String lIncomeRs) {
		if (StringUtils.isBlank(lIncomeDol) || StringUtils.isBlank(lIncomeRs)) {
			for (Integer ele : pogDppIncome) {
				if (allowedDppIncomeMap.get(ele) != null && allowedDppIncomeMap.get(ele).contains(pgIncome)) {
					return true;
				}
			}
			return false;
		}
		for (Integer ele : pogDppIncome) {
			if (allowedDppIncomeMap.get(ele) != null) {
				List<Integer> allowedIncome = new ArrayList<>(allowedDppIncomeMap.get(ele));
				if (incomeRs.contains(ele)) {
					allowedIncome.removeAll(incomeDol);
				} else {
					allowedIncome.removeAll(incomeRs);
				}
				if(allowedIncome.contains(pgIncome)) {
					return true;
				}
			}
		}
		return false;
	}
}
