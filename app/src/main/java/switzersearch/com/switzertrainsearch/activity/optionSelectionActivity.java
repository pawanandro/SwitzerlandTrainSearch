package switzersearch.com.switzertrainsearch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import switzersearch.com.switzertrainsearch.R;

public class optionSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_selection);

        Button searchButton = findViewById(R.id.search_button);
        Button favButton = findViewById(R.id.favourite);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(optionSelectionActivity.this,SearchActivity.class);
                startActivity(i);
            }
        });

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(optionSelectionActivity.this,FavouriteActivity.class);
                startActivity(i);
            }
        });

    }
}
