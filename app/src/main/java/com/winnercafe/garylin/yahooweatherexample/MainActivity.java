package com.winnercafe.garylin.yahooweatherexample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import zh.wang.android.apis.yweathergetter4a.WeatherInfo;
import zh.wang.android.apis.yweathergetter4a.YahooWeather;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherExceptionListener;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherInfoListener;

public class MainActivity extends AppCompatActivity implements YahooWeatherInfoListener, YahooWeatherExceptionListener{

    private final String TAG = "MainActivity";
    private ListView listView;
    private SimpleAdapter adapter;
    private Button button;
    private final String Url = "http://weather.yahooapis.com/forecastrss?w=2306181";
    private TextView weather_con;
    private TextView date;
    private TextView temphi;
    private TextView templow;
    List<Map<String, Object>> weather = new ArrayList<Map<String, Object>>();

    private YahooWeather mYahooWeather = YahooWeather.getInstance(5000, 5000, true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button)findViewById(R.id.button);
        listView = (ListView)findViewById(R.id.listView);
        weather_con = (TextView)findViewById(R.id.weather_con);
        date = (TextView)findViewById(R.id.date);
        temphi = (TextView)findViewById(R.id.temphi);
        templow = (TextView)findViewById(R.id.templow);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _location = "Taichung";
                searchByPlaceName(_location);
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemLongClick");
                Log.i(TAG, weather.get(position).toString());
                Map<String, Object> map = weather.remove(position);
                Log.i(TAG, map.get("Date").toString());

                updateView();

                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFailConnection(Exception e) {

    }

    @Override
    public void onFailParsing(Exception e) {

    }

    @Override
    public void onFailFindLocation(Exception e) {

    }

    @Override
    public void gotWeatherInfo(WeatherInfo weatherInfo) {

        if(weatherInfo != null) {

            Map<String, Object> map;
            for(int i = 0; i < YahooWeather.FORECAST_INFO_MAX_SIZE; i++) {

                final WeatherInfo.ForecastInfo forecastInfo = weatherInfo.getForecastInfoList().get(i);

                map = new HashMap<String, Object>();
                Log.i(TAG, "i = " + i
                        + ", Date = "+ forecastInfo.getForecastDate()
                        + ", Day = " + forecastInfo.getForecastDay()
                        + ", Temprature = " + forecastInfo.getForecastTempHigh()
                        + ", weather = " + forecastInfo.getForecastConditionIcon()
                        + ", text = " + forecastInfo.getForecastText());

                map.put("Date", forecastInfo.getForecastDay());
                map.put("TempratureHI", forecastInfo.getForecastTempHigh());
                map.put("TempratureLOW", forecastInfo.getForecastTempLow());
                map.put("weather", forecastInfo.getForecastText());
                weather.add(map);
            }
            Log.i(TAG, weather.toString());
            updateView();

        } else {
            Log.i(TAG, "weatherInfo == null");
        }

    }

    private void updateView () {
        adapter = new SimpleAdapter(this, weather, R.layout.sublistview, new String[] {"Date", "TempratureHI", "TempratureLOW", "weather"}, new int[] {R.id.date, R.id.temphi, R.id.templow, R.id.weather_con});
        listView.setAdapter(adapter);
    }

    private void searchByPlaceName(String location) {
        mYahooWeather.setNeedDownloadIcons(true);
        mYahooWeather.setUnit(YahooWeather.UNIT.CELSIUS);
        mYahooWeather.setSearchMode(YahooWeather.SEARCH_MODE.PLACE_NAME);
        mYahooWeather.queryYahooWeatherByPlaceName(getApplicationContext(), location, MainActivity.this);
    }
}
