package harps.blackboardscraper;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Map;

/**
 * Created by eghar on 19/04/2017.
 */

public class PerformBBLogin extends AsyncTask<Void, Void, String> {

    public interface PerformBBLoginResponse{
        void loginResult(String result);
    }

    public PerformBBLoginResponse delegate = null;

    final String baseUrl = "https://blackboard.swan.ac.uk/webapps/portal/execute/tabs/tabAction?tab_tab_group_id=_49_1";
    final String loginUrl = "https://blackboard.swan.ac.uk/webapps/login/";
    final String nextUrl = "https://blackboard.swan.ac.uk/webapps/portal/execute/defaultTab";
    final String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";

    final private String CONNECTION_FAIL = "CONNECTION_FAIL";
    final private String LOGIN_FAIL = "LOGIN_FAIL";
    final private String SUCCESS = "SUCCESS";

    private Map<String, String> cookies;

    private String username = BlackboardUser.getUsername();
    private String password = BlackboardUser.getPassword();

    private int statusCode;
    private Exception exception;
    private String successTitle = "";

    private String result = null;

    public PerformBBLogin(PerformBBLoginResponse delegate){
        this.username = BlackboardUser.getUsername();
        this.password = BlackboardUser.getPassword();
        this.delegate = delegate;
    }


    /**
     * Perform login and store cookies
     *
     * @param params
     * @return
     */
    public String doInBackground(Void... params) {

        try {
            // HTTP Get request
            Connection.Response getReq = Jsoup
                    .connect(baseUrl)
                    .method(Connection.Method.GET)
                    .execute();

            // Store cookies
            cookies = getReq.cookies();
            System.out.println("Init cookies: " + cookies);

            // Login Request
            Connection.Response loginReq = Jsoup
                    .connect(loginUrl)
                    .header("Host", "blackboard.swan.ac.uk")
                    .referrer("https://blackboard.swan.ac.uk/webapps/login/")
                    .cookies(cookies)
                    .data("user_id", username)
                    .data("password", password)
                    .data("login", "Login")
                    .data("action", "login")
                    .data("new_loc", "")
                    .userAgent(userAgent)
                    .referrer("https://blackboard.swan.ac.uk/webapps/login/")

                    .method(Connection.Method.POST)
                    .execute();

            // Get Status code
            statusCode = loginReq.statusCode();
            //Store cookies
            cookies = loginReq.cookies();
            // DEBUG CODE
            System.out.println("STATUS CODE: " + statusCode);
            System.out.println("Login Cookies " + cookies);


            Document checkSuccess = Jsoup
                    .connect("https://blackboard.swan.ac.uk/webapps/portal/execute/tabs/tabAction?tab_tab_group_id=_23_1")
                    .userAgent(userAgent)
                    .referrer(loginUrl)
                    .cookies(cookies)
                    .get();

            System.out.println(checkSuccess);

            successTitle = checkSuccess.title();


        } catch (Exception e) {
            System.out.println("EXCEPTION");
            this.exception = e;

            exception.printStackTrace();
        }
        return null;
    }

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
        delegate.loginResult(result);
    }
}
