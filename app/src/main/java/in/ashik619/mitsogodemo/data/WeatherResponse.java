package in.ashik619.mitsogodemo.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ashik619 on 3/5/18.
 */

public class WeatherResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("dt")
    private String dt;

    @SerializedName("cod")
    private String cod;

    @SerializedName("name")
    private String name;

    @SerializedName("base")
    private String base;

    private List<WeatherData> weather;

    private MainData main;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getDt ()
    {
        return dt;
    }

    public void setDt (String dt)
    {
        this.dt = dt;
    }

    public String getCod ()
    {
        return cod;
    }

    public void setCod (String cod)
    {
        this.cod = cod;
    }


    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getBase ()
    {
        return base;
    }

    public void setBase (String base)
    {
        this.base = base;
    }

    public List<WeatherData> getWeather ()
    {
        return weather;
    }

    public void setWeather (List<WeatherData> weather)
    {
        this.weather = weather;
    }

    public MainData getMain ()
    {
        return main;
    }

    public void setMain (MainData main)
    {
        this.main = main;
    }
}