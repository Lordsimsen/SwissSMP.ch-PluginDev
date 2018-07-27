package ch.swisssmp.utils;

import java.io.UnsupportedEncodingException;

public final class URLEncoder {
	public static String encode(String string){
		try {
			return java.net.URLEncoder.encode(string, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return string;
		}
	}
}
