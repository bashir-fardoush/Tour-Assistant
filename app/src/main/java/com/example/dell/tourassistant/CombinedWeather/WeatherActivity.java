package com.example.dell.tourassistant.CombinedWeather;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.dell.tourassistant.CombinedWeather.CurrentWeatherPackage.CurrentWeather;
import com.example.dell.tourassistant.CombinedWeather.CurrentWeatherPackage.CurrentWeatherClient;
import com.example.dell.tourassistant.CombinedWeather.CurrentWeatherPackage.Weather;
import com.example.dell.tourassistant.CombinedWeather.DailyWeather.CustomDailyWeather;
import com.example.dell.tourassistant.CombinedWeather.DailyWeather.DailyForecastAdapter;
import com.example.dell.tourassistant.CombinedWeather.DailyWeather.DailyWeather;
import com.example.dell.tourassistant.CombinedWeather.DailyWeather.DailyWeatherClient;
import com.example.dell.tourassistant.CombinedWeather.HourlyWeather.CustomHourlyWeather;
import com.example.dell.tourassistant.CombinedWeather.HourlyWeather.HourlyForecastAdapter;
import com.example.dell.tourassistant.CombinedWeather.HourlyWeather.HourlyWeather;
import com.example.dell.tourassistant.CombinedWeather.HourlyWeather.HourlyWeatherClient;
import com.example.dell.tourassistant.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity implements WeatherHomeFragment.OnPlacePickListener{

   // private FragmentManager fm = getSupportFragmentManager();
   // private FragmentTransaction ft = fm.beginTransaction();
    private BottomNavigationView navigationView;
    private String cityName,dateTime;
    private double temp;
    private SharedPreferences preferences;
    private CurrentWeather currentWeather;

    private ArrayList<CustomHourlyWeather> hourlyDataList;
    private ArrayList<CustomDailyWeather> dailyDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        navigationView = (BottomNavigationView) findViewById(R.id.weather_bottom_nav);

        double lat = 90.4786;
        double lon = 23.81435;

        try{
            lat = getIntent().getDoubleExtra("event_lattitude",0.0);
            lon = getIntent().getDoubleExtra("event_longitude",0.0);
        }catch (Exception e){
            Log.e("weatherActivity","No intent data found");
            Log.d("weatherActivity","not lat lon recveived");
        }



    final double iLat = lat;
    final double iLon = lon;


        collectCurrentWeather(iLat, iLon);
        collectHourlyWeather(iLat, iLon);
        collectDailyWeather(iLat, iLon);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fmanager= getSupportFragmentManager();
                FragmentTransaction fTranjection = fmanager.beginTransaction();
                Fragment fragment = null;
                Bundle innerBundle = new Bundle();
                switch (item.getItemId()){
                    case R.id.nav_current:

                       innerBundle.putString("dateTime",currentWeather.getData().get(0).getDatetime());
                       innerBundle.putString("city_name",currentWeather.getData().get(0).getCityName());
                       innerBundle.putDouble("current_temp",currentWeather.getData().get(0).getTemp());
                       innerBundle.putString("sunrise",currentWeather.getData().get(0).getSunrise());
                       innerBundle.putString("sunset",currentWeather.getData().get(0).getSunset());
                       innerBundle.putDouble("windspeed",currentWeather.getData().get(0).getWindSpd());
                       innerBundle.putDouble("visibility",currentWeather.getData().get(0).getVis());
                       innerBundle.putString("icon_code",currentWeather.getData().get(0).getWeather().getIcon());
                       innerBundle.putString("description",currentWeather.getData().get(0).getWeather().getDescription());

                        fragment = new WeatherHomeFragment();
                        fragment.setArguments(innerBundle);
                        break;
                    case R.id.nav_details:
                        fragment = new DetailsFragment();
                        innerBundle.putString("cityname",currentWeather.getData().get(0).getCityName());
                        innerBundle.putDouble("temperature",currentWeather.getData().get(0).getTemp());
                        innerBundle.putString("datetime",currentWeather.getData().get(0).getDatetime());
                        fragment.setArguments(innerBundle);
                        break;
                    case R.id.nav_forecast:
                        innerBundle.putParcelableArrayList("dailydatalist",dailyDataList);
                        innerBundle.putParcelableArrayList("hourlydatalist",hourlyDataList);

                        fragment = new ForecastFragment();
                        fragment.setArguments(innerBundle);
                        break;
                }
                String backStateName = fragment.getClass().getName();
              //  fmanager.
             //   fragment fdd
                fTranjection.replace(R.id.weather_fragment_coontainer,fragment);
                fTranjection.addToBackStack(backStateName);
                fTranjection.commit();

                return true;
            }
        });



    }

    private void collectCurrentWeather(double lat, double lon) {

       final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();

        String subUrl = "currently?key=21580262673342e28e1c87639965a4e8&lat="+lat+"&lon="+lon;
        CurrentWeatherClient client = CurrentWeatherClient.currentRetrofitClient.create(CurrentWeatherClient.class);
        Call<CurrentWeather> cwCall = client.getCurrentWeather(subUrl);
        cwCall.enqueue(new Callback<CurrentWeather>() {

            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if(response.code()==200){
                    currentWeather = new CurrentWeather();
                      Toast.makeText(WeatherActivity.this, "200 OK", Toast.LENGTH_SHORT).show();
                    currentWeather = response.body();
                    final Bundle bundle= new Bundle();
                    bundle.putString("dateTime",currentWeather.getData().get(0).getDatetime());
                    bundle.putString("city_name",currentWeather.getData().get(0).getCityName());
                    bundle.putDouble("current_temp",currentWeather.getData().get(0).getTemp());
                    bundle.putString("sunrise",currentWeather.getData().get(0).getSunrise());
                    bundle.putString("sunset",currentWeather.getData().get(0).getSunset());
                    bundle.putDouble("windspeed",currentWeather.getData().get(0).getWindSpd());
                    bundle.putDouble("visibility",currentWeather.getData().get(0).getVis());
                    bundle.putString("icon_code",currentWeather.getData().get(0).getWeather().getIcon());
                    bundle.putString("description",currentWeather.getData().get(0).getWeather().getDescription());



                   // fm.popBackStack();
                    FragmentManager fragManager = getSupportFragmentManager();
                    FragmentTransaction fragTransaction = fragManager.beginTransaction();
                    try{

                        int backStackId = fragManager.getBackStackEntryAt(0).getId();
                        fragManager.popBackStack(backStackId,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    catch (Exception e){
                        Log.d("clearBackStack",e.getMessage());
                    }



                    WeatherHomeFragment weatherHomeFragment = new WeatherHomeFragment();
                    weatherHomeFragment.setArguments(bundle);
                    fragTransaction.add(R.id.weather_fragment_coontainer,weatherHomeFragment);
                    fragTransaction.commit();
                    navigationView.setSelectedItemId(R.id.nav_current);



                }
                else if(response.code()==304){
                    Toast.makeText(WeatherActivity.this, "304 Not Modified", Toast.LENGTH_SHORT).show();

                }else if(response.code()==400){
                    Toast.makeText(WeatherActivity.this, "400 Bed Request", Toast.LENGTH_SHORT).show();
                }
                else if(response.code()==401){
                    Toast.makeText(WeatherActivity.this, "401 Unauthorised", Toast.LENGTH_SHORT).show();

                }else if(response.code()==403){
                    Toast.makeText(WeatherActivity.this, "403 Forbidden", Toast.LENGTH_SHORT).show();

                }else if(response.code()==404){
                    Toast.makeText(WeatherActivity.this, "404 Not Found", Toast.LENGTH_SHORT).show();

                }else if(response.code()==409){
                    Toast.makeText(WeatherActivity.this, "409 Conflict", Toast.LENGTH_SHORT).show();

                }else if(response.code()==500){
                    Toast.makeText(WeatherActivity.this, "500 Internal Servar Error", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(WeatherActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }


                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
                Toast.makeText(WeatherActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("CurrentWeather",t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }
        });
    }
    private void collectHourlyWeather(double lat, double lon) {

        String subUrl = "hourly?key=21580262673342e28e1c87639965a4e8&lat="+lat+"&lon="+lon;
        HourlyWeatherClient hourlyWeatherClient= HourlyWeatherClient.hourlyRetrofitClient.create(HourlyWeatherClient.class);
        Call<HourlyWeather> hwCall = hourlyWeatherClient.getHourlyWeather(subUrl);
        hwCall.enqueue(new Callback<HourlyWeather>() {
            @Override
            public void onResponse(Call<HourlyWeather> call, Response<HourlyWeather> response) {

                if(response.code()==200){
                    Toast.makeText(WeatherActivity.this, "200 OK", Toast.LENGTH_SHORT).show();
                   // hourlyDataList = new ArrayList<HourlyWeather>();
                 HourlyWeather  hourlyWeather = response.body();
                    hourlyDataList = new ArrayList<CustomHourlyWeather>();

                    for (int i =0; i<24; i++){
                        CustomHourlyWeather weather = new CustomHourlyWeather(
                                hourlyWeather.getData().get(i).getDatetime(),
                                hourlyWeather.getData().get(i).getTemp(),
                                hourlyWeather.getData().get(i).getWeather().getIcon()

                        );
                        hourlyDataList.add(weather);
                    }


                    //  Toast.makeText(getActivity(), ""+response.body().getCityName(), Toast.LENGTH_SHORT).show();

                   // HourlyForecastAdapter hourlyForecastAdapter = new HourlyForecastAdapter(getActivity(),hourlyDataList);

                  //  LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                   // llm.setOrientation(LinearLayoutManager.HORIZONTAL);
                  //  hourlyRV.setLayoutManager(llm);
                  //  hourlyRV.setAdapter(hourlyForecastAdapter);
                }
                else if(response.code()==304){
                    Toast.makeText(WeatherActivity.this, "304 Not Modified", Toast.LENGTH_SHORT).show();

                }else if(response.code()==400){
                    Toast.makeText(WeatherActivity.this, "400 Bed Request", Toast.LENGTH_SHORT).show();
                }
                else if(response.code()==401){
                    Toast.makeText(WeatherActivity.this, "401 Unauthorised", Toast.LENGTH_SHORT).show();

                }else if(response.code()==403){
                    Toast.makeText(WeatherActivity.this, "403 Forbidden", Toast.LENGTH_SHORT).show();

                }else if(response.code()==404){
                    Toast.makeText(WeatherActivity.this, "404 Not Found", Toast.LENGTH_SHORT).show();

                }else if(response.code()==409){
                    Toast.makeText(WeatherActivity.this, "409 Conflict", Toast.LENGTH_SHORT).show();

                }else if(response.code()==500){
                    Toast.makeText(WeatherActivity.this, "500 Internal Servar Error", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(WeatherActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<HourlyWeather> call, Throwable t) {

                Log.d("forecast","HourlyCallFailed");
                Log.d("forecast H",""+t.getMessage());
            }
        });
    }
    private void collectDailyWeather(double lat, double lon) {

        /*https://api.weatherbit.io/v2.0/forecast/daily?key=21580262673342e28e1c87639965a4e8&lat=22.5726&lon=88.3639*/
        String subUrl = "daily?key=21580262673342e28e1c87639965a4e8&lat="+lat+"&lon="+lon;
        DailyWeatherClient dailyWeatherClient = DailyWeatherClient.dailyRetrofitClient.create(DailyWeatherClient.class);
        Call<DailyWeather> dwCall = dailyWeatherClient.getDailyWeather(subUrl);
        dwCall.enqueue(new Callback<DailyWeather>() {
            @Override
            public void onResponse(Call<DailyWeather> call, Response<DailyWeather> response) {
                if(response.code()==200){
                    Toast.makeText(WeatherActivity.this, "200 OK", Toast.LENGTH_SHORT).show();
                    DailyWeather dailyWeather =  response.body();

                    dailyDataList = new ArrayList<CustomDailyWeather>();
                    for (int i =0; i< 10; i++){

                        CustomDailyWeather weather = new CustomDailyWeather(
                                dailyWeather.getData().get(i).getMaxTemp(),
                                dailyWeather.getData().get(i).getMinTemp(),
                                dailyWeather.getData().get(i).getDatetime(),
                                dailyWeather.getData().get(i).getWeather().getIcon()

                        );
                        dailyDataList.add(weather);
                    }

                }
                else if(response.code()==304){
                    Toast.makeText(WeatherActivity.this, "304 Not Modified", Toast.LENGTH_SHORT).show();

                }else if(response.code()==400){
                    Toast.makeText(WeatherActivity.this, "400 Bed Request", Toast.LENGTH_SHORT).show();
                }
                else if(response.code()==401){
                    Toast.makeText(WeatherActivity.this, "401 Unauthorised", Toast.LENGTH_SHORT).show();

                }else if(response.code()==403){
                    Toast.makeText(WeatherActivity.this, "403 Forbidden", Toast.LENGTH_SHORT).show();

                }else if(response.code()==404){
                    Toast.makeText(WeatherActivity.this, "404 Not Found", Toast.LENGTH_SHORT).show();

                }else if(response.code()==409){
                    Toast.makeText(WeatherActivity.this, "409 Conflict", Toast.LENGTH_SHORT).show();

                }else if(response.code()==500){
                    Toast.makeText(WeatherActivity.this, "500 Internal Servar Error", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(WeatherActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DailyWeather> call, Throwable t) {

                Log.d("forecast","DailyCallFailed");
                Log.d("forecast D",""+t.getMessage());
            }
        });
    }

    @Override
    public void onPlacePick(double lattitude, double longitude) {

        currentWeather = new CurrentWeather();
        hourlyDataList = new ArrayList<CustomHourlyWeather>();
        dailyDataList = new ArrayList<CustomDailyWeather>();
        collectCurrentWeather(lattitude,longitude);
        collectHourlyWeather(lattitude,longitude);
        collectDailyWeather(lattitude, longitude);
    }

    @Override
    public void onBackPressed(){
        if (getSupportFragmentManager().getBackStackEntryCount() == 1){
            finish();
        }
        else {
            super.onBackPressed();
        }
    }
}
 /*preferences = getSharedPreferences("latlonSP",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("req_lat",(float) lat);
        editor.putFloat("req_lon",(float) lon);
        editor.apply();
        editor.commit();*/

