package harps.blackboardscraper;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;

/**
 * @file PerformBBLogin.java
 * @author  Ed Harper
 * @date 23/03/2017
 * @see CookieStorage
 *
 * Performs login on Blackboard using JSoup and stores cookies in CookieStorage
 */

public class PerformBBLogin extends AsyncTask<Void, Void, String> {

    /**
     * Interface to pass back perform BB login result
     */
    public interface PerformBBLoginResponse{
        void loginResult(String result);
    }

    // Delegate variable
    public PerformBBLoginResponse delegate = null;

    final String BASE_URL = "https://blackboard.swan.ac.uk/webapps/portal/execute/tabs/tabAction?tab_tab_group_id=_49_1";
    final String LOGIN_URL = "https://blackboard.swan.ac.uk/webapps/login/";
    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";

    final private String CONNECTION_FAIL = "CONNECTION_FAIL";
    final private String LOGIN_FAIL = "LOGIN_FAIL";
    final private String SUCCESS = "SUCCESS";

    // Cookie map variable
    private Map<String, String> cookies;
    // Init Cookie Store
    private CookieStorage cookieStorage = new CookieStorage();

    private String username;
    private String password;

    private int statusCode;
    private Exception exception;
    private String successTitle = "";

    /**
     * Initialises a new instance of PerformBBLogin
     * @param delegate
     */
    public PerformBBLogin(PerformBBLoginResponse delegate){
        this.username = BlackboardUser.getUsername();
        this.password = BlackboardUser.getPassword();
        this.delegate = delegate;
    }


    /**
     * Perform login and store cookies in Cookie Storage
     *
     * @param params
     * @return
     */
    public String doInBackground(Void... params) {

        try {
            // HTTP Get request
            Connection.Response getReq = Jsoup
                    .connect(BASE_URL)
                    .method(Connection.Method.GET)
                    .execute();

            // Store cookies
            cookies = getReq.cookies();
            System.out.println("Init cookies: " + cookies);

            // Login Request
            Connection.Response loginReq = Jsoup
                    .connect(LOGIN_URL)
                    .header("Host", "blackboard.swan.ac.uk")
                    .referrer("https://blackboard.swan.ac.uk/webapps/login/")
                    .cookies(cookies)
                    .data("user_id", username)
                    .data("password", password)
                    .data("login", "Login")
                    .data("action", "login")
                    .data("new_loc", "")
                    .userAgent(USER_AGENT)
                    .referrer("https://blackboard.swan.ac.uk/webapps/login/")

                    .method(Connection.Method.POST)
                    .execute();

            // Get Status code
            statusCode = loginReq.statusCode();
            // Store cookies
            cookies = loginReq.cookies();
            cookieStorage.storeCookies(cookies,LOGIN_URL);
            // DEBUG CODE
            System.out.println("STATUS CODE: " + statusCode);
            System.out.println("Login Cookies " + cookies);

            // Request to check we are now logged in
            Document checkSuccess = Jsoup
                    .connect("https://blackboard.swan.ac.uk/webapps/portal/execute/tabs/tabAction?tab_tab_group_id=_23_1")
                    .userAgent(USER_AGENT)
                    .referrer(LOGIN_URL)
                    .cookies(cookies)
                    .get();
            // Login success check variable
            successTitle = checkSuccess.title();


        } catch (Exception e) {
            this.exception = e;
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Checks connection and log in was success and passes result back to calling method
     * @param result
     */
    protected void onPostExecute(String result) {
        if(statusCode != 200){
            System.out.println("Connection Issue" + statusCode + " Exception " + exception);
            result = CONNECTION_FAIL;
        }
        else if(successTitle.equals("Welcome – Blackboard Learn")) {
            System.out.println("Login failed");
            result = LOGIN_FAIL;
        }else{
            Log.d("ERROR","IN HERE SOMEHOW");
            result = SUCCESS;
            System.out.println(result);
        }
        // Set result
        delegate.loginResult(result);
    }
}
