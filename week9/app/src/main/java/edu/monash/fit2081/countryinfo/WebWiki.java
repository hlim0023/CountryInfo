package edu.monash.fit2081.countryinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toolbar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebWiki extends AppCompatActivity {
    WebView wiki;
    CountryDetails countryDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_wiki);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        wiki = findViewById(R.id.wiki);
        wiki.setWebViewClient(new WebViewClient());
        wiki.loadUrl("https://en.wikipedia.org/wiki/"+
                this.getIntent().getStringExtra("COUNTRY"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(this, getCountryDetails().getClass() ));
        return true;
    }

    public void setCountryDetails(CountryDetails countryDetails) {
        this.countryDetails = countryDetails;
    }

    public CountryDetails getCountryDetails() {
        return countryDetails;
    }
}
