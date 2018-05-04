package in.ashik619.mitsogodemo.data;

import in.ashik619.mitsogodemo.network.APIClient;
import in.ashik619.mitsogodemo.network.APIInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dilip on 3/5/18.
 */

public class WeatherRepository {
    private static final String ApiKey = "d6a4ee3791f1a28ab53a9d771ce648ec";
    public void getWeatherData(String lat, String lng, final DataListner listner){
        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
        Call<WeatherResponse> call = apiInterface.getWeatherData(lat,lng,ApiKey);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if(response.isSuccessful()){
                   listner.onData(response.body());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {

            }
        });

    }
    public interface DataListner{
        void onData(WeatherResponse response);
    }
}
