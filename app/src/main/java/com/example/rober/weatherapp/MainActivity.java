package com.example.rober.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private TextView title;
    private EditText cityName;
    private TextView main;
    private TextView description;

    private TextView temp;
    private TextView humidity;
    private TextView pressure;
    private TextView min_temp;
    private TextView max_temp;

    public class GetWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params){

            URL url = null;
            HttpURLConnection connection  = null;

            try {
                url = new URL(params[0]);
                connection = (HttpURLConnection)url.openConnection();

                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(reader);

                StringBuilder builder = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null){
                    builder.append(line);
                }

                return builder.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONObject jsonObj = new JSONObject(s);

                String city_name = jsonObj.getString("name");

                JSONObject obj_sys  = jsonObj.getJSONObject("sys");
                String country = obj_sys.getString("country");

                JSONArray array_weather  = jsonObj.getJSONArray("weather");
                JSONObject obj_weather = new JSONObject(array_weather.getString(0));

                JSONObject obj_main  = jsonObj.getJSONObject("main");

                title.setText(city_name + ", " + country);
                main.setText(obj_weather.getString("main"));
                description.setText(obj_weather.getString("description").toUpperCase());

                temp.setText(String.format("%.2f°C", (obj_main.getDouble("temp") - 273.15)));
                humidity.setText(obj_main.getString("humidity") + "%");
                pressure.setText(obj_main.getString("pressure") + " hPa");
                min_temp.setText(String.format("%.2f°C", (obj_main.getDouble("temp_min") - 273.15)));
                max_temp.setText(String.format("%.2f°C", (obj_main.getDouble("temp_max") - 273.15)));


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Cannot get the JSON.", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI Elements
        title  = (TextView)findViewById(R.id.titleTxt);
        cityName  = (EditText)findViewById(R.id.cityEditTxt);
        main  = (TextView)findViewById(R.id.mainTxt);
        description  = (TextView)findViewById(R.id.desTxt);
        temp = (TextView)findViewById(R.id.tempTxt);
        humidity = (TextView)findViewById(R.id.humiTxt);
        pressure = (TextView)findViewById(R.id.pressTxt);
        min_temp = (TextView)findViewById(R.id.minTempTxt);
        max_temp = (TextView)findViewById(R.id.maxTempTxt);

    }

    public void onSearchBtn_Pressed(View view){

        //Check if the input is not null
        if (TextUtils.isEmpty(cityName.getText().toString().trim())){
            cityName.setError("This input cannot be empty.");
        } else {

            try {

                //Crate the URL
                String url = "http://api.openweathermap.org/data/2.5/weather?appid=44db6a862fba0b067b1930da0d769e98&q=";
                url += URLEncoder.encode(cityName.getText().toString());

                //Execute the post AsyncTask
                GetWeatherTask task = new GetWeatherTask();
                task.execute(url);

                //Hide the Keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }catch (Exception e){
                //
                Toast.makeText(getApplicationContext(), "Cannot found result.", Toast.LENGTH_LONG).show();
            }

        }

    }

}
