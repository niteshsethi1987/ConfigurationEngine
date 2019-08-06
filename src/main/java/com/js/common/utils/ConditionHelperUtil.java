package com.js.common.utils;

import java.util.ArrayList;
import java.util.Collection;

public class ConditionHelperUtil {
	public static boolean isIntersecting(Collection<?> c1,Collection<?> c2)
	{
		
		if ( c1 == null || c2 == null )
			return false;
		
		Collection<?> copyC1 = new ArrayList<>(c1);
		copyC1.retainAll(c2);
		return copyC1.size() != 0;
	}
}
