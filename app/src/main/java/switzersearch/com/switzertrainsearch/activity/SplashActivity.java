package switzersearch.com.switzertrainsearch.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import switzersearch.com.switzertrainsearch.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/zag_bold.otf");
        TextView tv1 = (TextView) findViewById(R.id.tv_sts);
        tv1.setTypeface(face);

        goToNextActivity();
    }

    private void goToNextActivity() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                {
                    Log.d("isFirstTimeUser", "true:");

                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();

                }
            }
        }, 3000);
    }

}
