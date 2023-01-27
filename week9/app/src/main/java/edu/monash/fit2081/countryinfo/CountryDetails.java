package edu.monash.fit2081.countryinfo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.os.Looper;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;

public class CountryDetails extends AppCompatActivity {

    private TextView name;
    private TextView capital;
    private TextView code;
    private TextView population;
    private TextView area;
    private TextView currency;
    private TextView language;
    ImageView flag;
    private String alpha2Code;
    private String countryName;
    Button wikiB;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_details);

        getSupportActionBar().setTitle(R.string.title_activity_country_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String selectedCountry = getIntent().getStringExtra("country");
        countryName = selectedCountry;
        name = findViewById(R.id.country_name);
        capital = findViewById(R.id.capital);
        code = findViewById(R.id.country_code);
        population = findViewById(R.id.population);
        area = findViewById(R.id.area);
        currency = findViewById(R.id.currencies);
        language = findViewById(R.id.languages);
        flag = findViewById(R.id.imageView2);
        wikiB =findViewById(R.id.button);
        wikiB.setText("WIKI "+selectedCountry);


        ExecutorService executor = Executors.newSingleThreadExecutor();
        //Executor handler = ContextCompat.getMainExecutor(this);
        Handler uiHandler=new Handler(Looper.getMainLooper());




        executor.execute(() -> {
            //Background work here
            CountryInfo countryInfo = new CountryInfo();

            try {
                // Create URL
                URL webServiceEndPoint = new URL("https://restcountries.com/v2/name/" + selectedCountry); //

                // Create connection
                HttpsURLConnection myConnection = (HttpsURLConnection) webServiceEndPoint.openConnection();

                if (myConnection.getResponseCode() == 200) {
                    //JSON data has arrived successfully, now we need to open a stream to it and get a reader
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

                    //now use a JSON parser to decode data
                    JsonReader jsonReader = new JsonReader(responseBodyReader);
                    jsonReader.beginArray(); //consume arrays's opening JSON brace
                    String keyName;
                    // countryInfo = new CountryInfo(); //nested class (see below) to carry Country Data around in
                    boolean countryFound = false;
                    while (jsonReader.hasNext() && !countryFound) { //process array of objects
                        jsonReader.beginObject(); //consume object's opening JSON brace
                        while (jsonReader.hasNext()) {// process key/value pairs inside the current object
                            keyName = jsonReader.nextName();
                            if (keyName.equals("name")) {
                                countryInfo.setName(jsonReader.nextString());
                                if (countryInfo.getName().equalsIgnoreCase(selectedCountry)) {
                                    countryFound = true;
                                }
                                Log.i("CN_CHECK", countryInfo.getName() +","+countryFound );
                            }else if(keyName.equals("alpha2Code")){
                                alpha2Code= jsonReader.nextString();
                                Log.i("CN_CHECK", alpha2Code);
                            } else if (keyName.equals("alpha3Code")) {
                                countryInfo.setAlpha3Code(jsonReader.nextString());
                                Log.i("CN_CHECK", countryInfo.getAlpha3Code() );
                            } else if (keyName.equals("capital")) {
                                countryInfo.setCapital(jsonReader.nextString());
                                Log.i("CN_CHECK", countryInfo.getCapital() );
                            } else if (keyName.equals("population")) {
                                countryInfo.setPopulation(jsonReader.nextInt());
                            } else if (keyName.equals("area")) {
                                countryInfo.setArea(jsonReader.nextDouble());
                            } else if (keyName.equals("currencies")) {
                                String currencies_lst ="";
                                jsonReader.beginArray();
                                while (jsonReader.hasNext()){
                                    jsonReader.beginObject();
                                    while(jsonReader.hasNext()){
                                        if(jsonReader.nextName().equals("name")){
                                            currencies_lst += jsonReader.nextString();
                                        }
                                        else{
                                            jsonReader.skipValue();
                                        }
                                    }
                                    currencies_lst += ",";
                                    jsonReader.endObject();
                                }
                                jsonReader.endArray();
                                countryInfo.setCurrency(currencies_lst.substring(0,currencies_lst.length()-1));
                                Log.i("CN_CHECK", countryInfo.getCurrency() );
                            } else if (keyName.equals("languages")) {
                                String languages_lst = "";
                                jsonReader.beginArray();
                                while (jsonReader.hasNext()){
                                    jsonReader.beginObject();
                                    while(jsonReader.hasNext()){
                                        if(jsonReader.nextName().equals("name")){
                                            languages_lst += jsonReader.nextString();
                                        }
                                        else{
                                            jsonReader.skipValue();
                                        }
                                    }
                                    languages_lst += ",";
                                    jsonReader.endObject();
                                }
                                jsonReader.endArray();
                                countryInfo.setLanguage(languages_lst.substring(0,languages_lst.length()-1));
                                Log.i("CN_CHECK", countryInfo.getLanguage() );
                            } else {
                                jsonReader.skipValue();
                            }
                        }
                        //jsonReader.endObject();
                    }
                    //jsonReader.endArray();
                    uiHandler.post(()->{
                        name.setText(countryInfo.getName());
                        capital.setText(countryInfo.getCapital());
                        code.setText(countryInfo.getAlpha3Code());
                        population.setText(Integer.toString(countryInfo.getPopulation()));
                        area.setText(Double.toString(countryInfo.getArea()));
                        currency.setText(countryInfo.getCurrency());
                        language.setText(countryInfo.getLanguage());
                        Glide.with(this).load("https://countryflagsapi.com/png/"+alpha2Code).into(flag);
                    });

                } else {
                    Log.i("INFO200", "Error:  No response");
                }

                // All your networking logic should be here
            } catch (Exception e) {
                Log.i("INFO", "Error " + e.toString());
            }

        });

    }

    public void WikiClick(View view){
        WebWiki webWiki = new WebWiki();
        webWiki.setCountryDetails(this);
        Intent wiki = new Intent(this,webWiki.getClass());
        wiki.putExtra("COUNTRY",countryName);

        startActivity(wiki);
    }



    private class CountryInfo {
        private String name;
        private String alpha3Code;
        private String capital;
        private int population;
        private double area;
        private String currency;
        private String language;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAlpha3Code() {
            return alpha3Code;
        }

        public void setAlpha3Code(String alpha3Code) {
            this.alpha3Code = alpha3Code;
        }

        public String getCapital() {
            return capital;
        }

        public void setCapital(String capital) {
            this.capital = capital;
        }

        public int getPopulation() {
            return population;
        }

        public void setPopulation(int population) {
            this.population = population;
        }

        public double getArea() {
            return area;
        }

        public void setArea(double area) {
            this.area = area;
        }

        public String getCurrency() {
            return currency;
        }

        public String getLanguage() {
            return language;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

    }
}
