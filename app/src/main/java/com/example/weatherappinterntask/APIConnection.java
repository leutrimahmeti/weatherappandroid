package com.example.weatherappinterntask;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class APIConnection extends AsyncTask<String, Void, String[]> {

    public static final String WEATHER_STATE_NAME = "weather_state_name";
    public static final String WEATHER_STATE_ABBR = "weather_state_abbr";
    public static final String WIND_DIR_COMPASS = "wind_direction_compass";
    public static final String DATE = "applicable_date";
    public static final String MIN_TEMP = "min_temp";
    public static final String MAX_TEMP = "max_temp";
    public static final String THE_TEMP = "the_temp";
    public static final String WIND_SPEED = "wind_speed";
    public static final String AIR_PRESSURE = "air_pressure";
    public static final String HUMIDITY = "humidity";

    interface API {
        void Connect(Weather weather, String cityName);
    }

    private API callBack;


    @Override
    protected String[] doInBackground(String... strings) {
        String data = getWoeidData(strings[0]);
        String city;
        int woeid;
        if (data != null) {
            String[] parts = data.split("\\|");
            try{city = parts[1];}catch (ArrayIndexOutOfBoundsException e){city = "";}
            woeid = Integer.parseInt(parts[0]);
            String weatherURL = "https://www.metaweather.com/api/location/" + woeid + "/";
            return new String[]{getWeather(weatherURL), city};
        }else {
            return null;
        }
    }
    private String getWoeidData(String link){
        BufferedReader bufferedReader = null;
        StringBuilder jsonData = null;
        URL url = null;
        HttpURLConnection connection = null;
        String readLine;
        try {
            url = new URL(link);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            if (responseCode == HttpURLConnection.HTTP_OK){
                jsonData = new StringBuilder();
                while ((readLine = bufferedReader.readLine()) != null){
                    jsonData.append(readLine);
                }
            }
            bufferedReader.close();
        }catch (MalformedURLException e){
            e.getMessage();
        }catch (IOException e){
            e.getMessage();
        }
        int id = 0;
        String cityName = "";
        if (jsonData != null) {
//            Log.d(TAG, "getWoeid: JSON: " + jsonData.toString());
            try {
                JSONArray arr = new JSONArray(jsonData.toString());
                id = arr.getJSONObject(0).getInt("woeid");
                cityName = arr.getJSONObject(0).getString("title");
            }catch (JSONException e) {
                e.printStackTrace();
            }
            return id + "|" + cityName;
        } else {
            return null;
        }
    }

    private String getWeather(String link){
        URL url;
        BufferedReader br;
        StringBuilder jsonData = new StringBuilder();
        String readLine;
        HttpURLConnection conn;

        try {
            url = new URL(link);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            if (responseCode == HttpURLConnection.HTTP_OK){
                while ((readLine = br.readLine()) != null){
                    jsonData.append(readLine);
                }
            }
            br.close();
        }catch (MalformedURLException e){
            e.getMessage();
        }catch (IOException e){
            e.getMessage();
        }

        return jsonData.toString();
    }

    public void setCallBack(API callBack) {
        this.callBack = callBack;
    }

    @Override
    protected void onPostExecute(String[] s) {
        String cityName = s[1];
        try {
            JSONObject obj = new JSONObject(s[0]);
            JSONObject todayWeather = obj.getJSONArray("consolidated_weather").getJSONObject(0);
            Weather weather = new Weather(todayWeather.getString(WEATHER_STATE_NAME), todayWeather.getString(WEATHER_STATE_ABBR),
                    todayWeather.getString(DATE), todayWeather.getDouble(MIN_TEMP),
                    todayWeather.getDouble(MAX_TEMP), todayWeather.getDouble(THE_TEMP), todayWeather.getDouble(WIND_SPEED),
                    todayWeather.getString(WIND_DIR_COMPASS), todayWeather.getDouble(AIR_PRESSURE), todayWeather.getDouble(HUMIDITY));
            callBack.Connect(weather, cityName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
