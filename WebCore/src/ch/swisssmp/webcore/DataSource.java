package ch.swisssmp.webcore;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;

import org.bukkit.Bukkit;

import ch.swisssmp.utils.YamlConfiguration;

public class DataSource {

	private static final String USER_AGENT = Bukkit.getVersion();
	
	protected static String rootURL;
	protected static String pluginToken;
	private static Random random = new Random();
	
	public static String getResponse(String relativeURL){
		return getResponse(relativeURL, null);
	}
	
	public static String getResponse(String relativeURL, String[] params){
		return getResponse(relativeURL, params, RequestMethod.GET);
	}
	
	public static String getResponse(String relativeURL, String[] params, RequestMethod method){
		String resultString = "";
		try{
			String paramString = "token="+pluginToken+"&random="+random.nextInt(1000)+"&server="+URLEncoder.encode(WebCore.server_name,"utf-8");
			if(params!=null && params.length>0){
				paramString+="&"+String.join("&", params);
			}
			if(method==RequestMethod.GET){
				resultString = sendGet(rootURL+relativeURL+"?"+paramString);
			}
			else{
				resultString = sendPost(rootURL+relativeURL,paramString);
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

	// HTTP GET request
	private static String sendGet(String url) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		WebCore.info("\nSending 'GET' request to URL : " + url);
		WebCore.info("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		WebCore.info(response.toString());
		//return result
		return response.toString();

	}

	// HTTP POST request
	private static String sendPost(String url, String paramString) throws Exception {

		URL obj = new URL(null, url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(paramString);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		WebCore.info("\nSending 'POST' request to URL : " + url);
		WebCore.info("Post parameters : " + paramString);
		WebCore.info("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		WebCore.info(response.toString());
		//return result
		return response.toString();

	}
    private static String convertWebYamlString(String webYamlString){
    	webYamlString = webYamlString.replace("<br>", "\r\n");
    	webYamlString = webYamlString.replace("&nbsp;", " ");
    	return webYamlString;
    }
}
