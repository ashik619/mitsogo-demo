package in.ashik619.mitsogodemo.network;


import com.google.gson.JsonObject;

import in.ashik619.mitsogodemo.data.WeatherResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by dilip on 13/2/18.
 */

public interface APIInterface {

    @GET("/data/2.5/weather")
    Call<WeatherResponse> getWeatherData(@Query("lat")String lat, @Query("lon")String lng, @Query("APPID")String APPID);

}
