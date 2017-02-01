package ch.swisssmp.webcore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;

import ch.swisssmp.utils.YamlConfiguration;

public class DataSource {
	
	protected static String rootURL;
	protected static String pluginToken;
	private static Random random = new Random();
	
	public static String getResponse(String relativeURL){
		return getResponse(relativeURL, null);
	}
	
	public static String getResponse(String relativeURL, String[] params){
		String resultString = "";
		try{
			String urlString = rootURL+relativeURL+"?token="+pluginToken+"&random="+random.nextInt(1000)+"&server="+URLEncoder.encode(WebCore.server_name,"utf-8");
			if(params!=null && params.length>0){
				urlString+="&"+String.join("&", params);
			}
			WebCore.info("Connecting to: "+urlString);
			URL url = new URL(urlString);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String tempString = "";
			while(null!=(tempString = br.readLine())){
				resultString+= tempString;
			}
			if(resultString.isEmpty()){
				WebCore.info("Returning empty result");
				return "";
			}
			return resultString;
		}
		catch(Exception e){
			e.printStackTrace();
			WebCore.info("Causing the error: "+resultString);
			return "";
		}
	}
	
	public static YamlConfiguration getYamlResponse(String relativeURL){
		return getYamlResponse(relativeURL, null);
	}
	
	public static YamlConfiguration getYamlResponse(String relativeURL, String[] params){
		String resultString = convertWebYamlString(getResponse(relativeURL, params));
		if(resultString.isEmpty()){
			WebCore.info("Returning empty result");
			return new YamlConfiguration();
		}
		try{
			WebCore.info("Result: "+resultString);
			return new YamlConfiguration(resultString);
		}
		catch(Exception e){
			e.printStackTrace();
			WebCore.info("Causing the error: "+resultString);
			return new YamlConfiguration();
		}
	}
    private static String convertWebYamlString(String webYamlString){
    	webYamlString = webYamlString.replace("<br>", "\r\n");
    	webYamlString = webYamlString.replace("&nbsp;", " ");
    	return webYamlString;
    }
}
