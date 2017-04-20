package harps.blackboardscraper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * @file ModuleScraper.java
 * @author Ed Harper
 * @date 23/03/2017
 *
 * Simulates an AJAX request to Blackboard ajax function.
 * XML data is returned from Blackboard and parsed to extract Module data.
 */

public class ModuleScraper extends Activity{

    // Final url variables
    final String MODULE_AJAX_URL = "https://blackboard.swan.ac.uk/webapps/portal/execute/tabs/tabAction";
    final String BASE_URL = "https://blackboard.swan.ac.uk/webapps/login/";
    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";

    // Final AJAX data variables
    private final String ACTION = "refreshAjaxModule";
    private final String MOD_ID = "_1334_1";
    private final String TAB_ID = "_52_1";
    private final String TAB_GROUP = "_23_1";

    // Final scraping selector variables
    private final String LINK_SELECTOR = "a";
    private final String HREF_SELECTOR = "href";

    private Context context = ModuleScraper.this;
    private ProgressDialog pd;

    private ListView moduleView;
    private ModuleAdapter moduleAdapter;

    // Cookie map
    private Map<String, String> cookies;
    // Init cookie store
    private CookieStorage cookieStorage = new CookieStorage();

    private ArrayList<Module> modules = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_scraper);

        moduleView = (ListView)findViewById(R.id.module_list_view);
    }

    public void onResume() {
        super.onResume();
        new scrapeTimetable().execute();
    }


    /**
     * Gets cookies from storage and sends AJAX function to blackboard
     */
    private class scrapeTimetable extends AsyncTask<String, Void, Void> {
        private Exception exception;

        /**
         * Display progress dialog
         */
        @Override
        protected void onPreExecute(){
            pd = new ProgressDialog(context);
            pd.setMessage("Fetching Modules...");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected Void doInBackground(String...params){
            try{

                // Get cookies from cookie store
                cookies = cookieStorage.getCookiesMap(MODULE_AJAX_URL);

                // Simulate ajax call with JSoup and pass variables.
                Document xmlDoc = Jsoup
                        .connect(MODULE_AJAX_URL)
                        .userAgent(USER_AGENT)
                        .referrer(BASE_URL)
                        .cookies(cookies)
                        .data("action", ACTION)
                        .data("modId", MOD_ID)
                        .data("tabId", TAB_ID)
                        .data("tab_tab_group_id", TAB_GROUP)
                        .get();

                // Parse XML and extract link
                parseXML(xmlDoc);

            }catch(IOException e){
                this.exception = e;
                e.printStackTrace();
            }
            return null;
        }


        /**
         * Update adapter data and UI
         * @param result
         */
        protected void onPostExecute(Void result){
            if(exception instanceof IOException) {
                Toast.makeText(context, "Could not retrieve module info.", Toast.LENGTH_LONG);
            }else {
                if (moduleAdapter == null) {
                    moduleAdapter = new ModuleAdapter(context, modules);
                    moduleView.setAdapter(moduleAdapter);
                } else {
                    moduleAdapter.notifyDataSetChanged();
                }
            }

            //If progress is still showing dismiss.
            if(pd.isShowing()){
                pd.dismiss();
            }

        }
    }

    /**
     * Takse xml data and extracts module data
     * @param xmlDoc the xml document
     */
    public void parseXML(Document xmlDoc){

        Document doc = Jsoup.parse(Parser.unescapeEntities(xmlDoc.toString(), false), "", Parser.xmlParser());

        Elements links = doc.select(LINK_SELECTOR);
        for(Element e : links){
            String moduleName = e.text();
            String moduleUrl = e.attr(HREF_SELECTOR);
            Module module = new Module(moduleName, moduleUrl);
            modules.add(module);
        }
    }


}