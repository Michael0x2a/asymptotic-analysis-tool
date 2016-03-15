package wolfram;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Scanner;

import com.wolfram.alpha.WAEngine;
import com.wolfram.alpha.WAException;
import com.wolfram.alpha.WAPlainText;
import com.wolfram.alpha.WAPod;
import com.wolfram.alpha.WAQuery;
import com.wolfram.alpha.WAQueryResult;
import com.wolfram.alpha.WASubpod;


/*
 * A simple example program demonstrating the WolframAlpha.jar library. The program
 * performs a query given on the command line and prints out the resulting pod titles
 * and plaintext content.
 * 
 * You will need to insert your appid into the code. To compile or run this program
 * you will need the following dependent libraries on your classpath (including 
 * WolframAlpha.jar, of course):
 * 
 *     commons-codec-1.3.jar
 *     httpclient-4.0.1.jar
 *     httpcore-4.0.1.jar
 *     commons-logging.jar
 *     
 * These libraries are widely available on the Internet. You can probably use other version
 * numbers than these, although these are the versions I used.
 * 
 * To launch from the command line, do the following (these classpath specifications assume
 * that the WolframAlpha.jar file and the four other dependent jars listed above are in the same
 * directory as AlphaAPISample.class):
 * 
 *     Windows:
 *     
 *       java -classpath .;* AlphaAPISample "sin x"
 *     
 *     Linux, Mac OSX:
 *     
 *       java -classpath .:* AlphaAPISample "sin x"
 */

public class WolframQuery {

    // PUT YOUR APPID HERE:
    private static String appid = "393YL5-7PAYX696YV";
    private WAEngine engine;
    
    public WolframQuery() {
    	engine = new WAEngine();
    	engine.setAppID(appid);
        engine.addFormat("plaintext");
        //engine.addFormat("mathml");
    }
    
    /**
     * Sends input to Wolfram and returns its plaintext response.
     * Returns null if an error occurred or if the query was not understood.
     */
    public String getWolframPlaintext(String input) {
    	// Create the query.
        WAQuery query = engine.createQuery();
        
        // Set properties of the query.
        query.setInput(input);
        
        try {
            
            // This sends the URL to the Wolfram|Alpha server, gets the XML result
            // and parses it into an object hierarchy held by the WAQueryResult object.
            WAQueryResult queryResult = engine.performQuery(query);
            
            if (queryResult.isError() || !queryResult.isSuccess()) {
                return null;
            } else {
            	//System.out.println(queryResult.getXML());
                // Got a result.
                for (WAPod pod : queryResult.getPods()) {
                    if (!pod.isError() && (pod.getTitle().equals("Result")
                    		|| pod.getTitle().equals("Recurrence equation solution"))) {
                        for (WASubpod subpod : pod.getSubpods()) {
                            for (Object element : subpod.getContents()) {
                                if (element instanceof WAPlainText) {
                                    String response = (((WAPlainText) element).getText());
                                    //return new String(response.getBytes("US-ASCII"));
                                    //response = new String(response.getBytes("UTF-16"), "UTF-8");
                                    String output = "";
                                    for (int i = 0; i < response.length(); i++) {
                                    		if (response.charAt(i) < 256)
                                    			output += response.charAt(i);
                                    		else
                                    			output += '=';
                                    }
                                    return output;
                                    
                                   }
                            }
                        }
                    }
                }
                // We ignored many other types of Wolfram|Alpha output, such as warnings, assumptions, etc.
                // These can be obtained by methods of WAQueryResult or objects deeper in the hierarchy.
            }
        } catch (WAException e) {
            return null;
        }
		return null;
    }
    
    public static void main(String[] args) {
    	System.out.println(getWolframResponse("2+3+4n+2n+10n+4n^2"));
    	//System.out.println(getWolframResponse("T(n) = T(n/2) + T(n/2) + 20, T(1) = 1"));
    }
    
    public static String getWolframResponse(String input) {
//    	String urlinput = "http://api.wolframalpha.com/v2/query?appid=393YL5-7PAYX696YV&input="
//    			+ input + "&format=plaintext";
    	try {
			URL url = new URI("http", "api.wolframalpha.com", "/v2/query", 
					"appid=393YL5-7PAYX696YV&input=" + input + "&format=plaintext",
					null).toURL();
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			Scanner s = new Scanner(conn.getInputStream(), "UTF-8");
			s.useDelimiter("\\A");
			String result = s.hasNext() ? s.next() : "";
			s.close();
			return result;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
    }

}
