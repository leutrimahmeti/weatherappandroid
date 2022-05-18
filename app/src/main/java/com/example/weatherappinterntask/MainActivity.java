package com.example.weatherappinterntask;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.Locale;


public class MainActivity extends AppCompatActivity implements APIConnection.API {

    private Button button;
    private ImageView weatherImage;
    private TextView theTemp;
    private TextView minTemp;
    private TextView maxTemp;
    private TextView wind;
    private TextView humidity;
    private TextView cityTextView;
    private Spinner spinner;
    private String city="";
    private String imageURL = "https://www.metaweather.com/static/img/weather/png/%s.png";
    public static String api = "https://www.metaweather.com/api/location/search/?";
    private APIConnection APIConnect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        weatherImage = findViewById(R.id.weather_image);
        theTemp = findViewById(R.id.temperature);
        minTemp = findViewById(R.id.low_textview);
        maxTemp = findViewById(R.id.high_textview);
        wind = findViewById(R.id.wind);
        humidity = findViewById(R.id.humidity);
        cityTextView = findViewById(R.id.city_name);
        spinner = findViewById(R.id.spinner);

        ArrayAdapter<String> myAdapter= new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1 , getResources().getStringArray(R.array.cities));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                city = spinner.getSelectedItem().toString();
                processData(city);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void Connect(Weather weather, String city) {

        Picasso.get().load(String.format(imageURL, weather.getWeatherStateAbbreviation())).into(weatherImage);
        cityTextView.setText(city);
        theTemp.setText(String.format(Locale.ENGLISH, "%.1f%s", weather.getTheTemp(), getString(R.string.degree)));
        maxTemp.setText(String.format(Locale.ENGLISH, "%s: %.1f%s", getString(R.string.high_temp), weather.getMaxTemp(), getString(R.string.degree)));
        minTemp.setText(String.format(Locale.ENGLISH, "%s: %.1f%s", getString(R.string.low_temp), weather.getMinTemp(), getString(R.string.degree)));
        wind.setText(String.format(Locale.ENGLISH, "%s %s %.1f mph", getString(R.string.wind), weather.getWindDir(), weather.getWindSpeed()));
        humidity.setText(String.format(Locale.ENGLISH, "%s %.1f%s", getString(R.string.water), weather.getHumidity(), "%"));


    }

    private void processData(String link){
        String temp = "";
        if (link.trim().length() != 0){
            try {
                String[] parts = link.trim().split(",");
                double lat = Double.parseDouble(parts[0]);
                double lon = Double.parseDouble(parts[1]);
                temp = "lattlong=" + lat + "," + lon;
            }catch (NumberFormatException e){
                temp = "query=" + link;
            }
        }
        APIConnect = new APIConnection();
        APIConnect.setCallBack(this);
        APIConnect.execute(api + temp);

    }
}