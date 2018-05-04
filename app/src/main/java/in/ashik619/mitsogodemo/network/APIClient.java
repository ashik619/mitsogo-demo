package in.ashik619.mitsogodemo.network;

/*
  Created by DilipAti on 23/11/16.
 */

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class APIClient {

    private static Retrofit retrofit2 = null;
    private static final String BASE_URL_OPEN_WEATHER = "http://api.openweathermap.org";



    private static HttpLoggingInterceptor getLoggingIntercepeter(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }
    public static Retrofit getApiClient() {
        if (retrofit2==null) {
            retrofit2 = new Retrofit.Builder()
                    .baseUrl(BASE_URL_OPEN_WEATHER)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit2;
    }
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(getLoggingIntercepeter())
            .build();
}