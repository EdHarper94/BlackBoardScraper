package harps.blackboardscraper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Perform login
        new PerformBBLogin(new PerformBBLogin.PerformBBLoginResponse() {
            @Override
            public void loginResult(String result) {
                if(result.equals("SUCCESS")){
                    Toast.makeText(MainActivity.this, "Login Successful...", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(MainActivity.this, ModuleScraper.class);
                    startActivity(intent);

                }else if(result.equals("LOGIN_FAIL")){
                    Toast.makeText(MainActivity.this, "Login has failed, please check your username/password." , Toast.LENGTH_LONG).show();;
                }else {
                    Toast.makeText(MainActivity.this, "Connection issue, please try again later.", Toast.LENGTH_LONG).show();
                }
            }
        }).execute();
    }
}
