package com.furoom.ejson;
import java.util.IdentityHashMap;

import com.furoom.utils.ZStringUtils;


public class StringSerializer extends AbstractSerializer<String> {

	public String serialize(String t, IdentityHashMap<Object, Integer> refMap) {
		if (t == null){
				return EJsonConsts.EJ_NULL_VALUE;
		}
		return new StringBuilder("\"").append(ZStringUtils.escapeJavaScript(t, false, false)).append("\"").toString();
	}
}
