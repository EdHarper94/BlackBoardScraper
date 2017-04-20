package harps.blackboardscraper;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A lightweight cookie store for use with JSoup requests
 * Created by eghar on 19/03/2017.
 */

public class CookieStorage {

    // Map <Host, < Key, Value> >
    private static Map<URI, Map<String, String>> hostCookieMap = new HashMap<>();
    Map<String, String> cookies;

    public CookieStorage(){
    }

    /**
     * Stores Hashmaps for URI and cookie maps (Uri, (key, value))
     * @param cookies Map of cookies (key, value)
     * @param stringUrl url of cookie
     */
    public void storeCookies(Map<String,String> cookies, String stringUrl){
        try {
            URI uri = getUri(stringUrl);
            // If host is not there
            if(hostCookieMap.get(uri)== null) {
                for(Map.Entry e : cookies.entrySet()){
                    hostCookieMap.put(uri, cookies);
                }
            }else{
            }
            System.out.println(hostCookieMap);
        }catch (Exception e){

        }
    }

    /**
     * Gets cookies for provided host
     * @param stringUrl string url of host
     * @return
     */
    public Map<String, String> getCookiesMap(String stringUrl){
        try{
            URI uri = getUri(stringUrl);
            if (hostCookieMap.containsKey(uri)) {
                cookies = hostCookieMap.get(uri);
                System.out.println("FROM COOKIE STORE "+ cookies);
            }
        }catch(Exception e){

        }
        return cookies;
    }

    /**
     * Removes all cookies from storage
     */
    public void removeCookies(){
        hostCookieMap.clear();
    }

    /**
     * Converts String url to URI for cookie storage
     * @param stringUrl the url in string format
     * @return URI host
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public URI getUri(String stringUrl) throws URISyntaxException, MalformedURLException {

        URL url = new URL(stringUrl);
        URI uri = new URI(url.getProtocol(), url.getHost(), null);

        return uri;
    }
}