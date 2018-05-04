package in.ashik619.mitsogodemo.data;

/**
 * Created by dilip on 3/5/18.
 */

public class UpdateData {
    private String weatherResponse = null;
    private String battery = null;
    private String deviceName = null;
    private String networkType = null;
    private String deviceStorage = null;

    public UpdateData(){
        this.weatherResponse = null;
        this.battery = null;
        this.deviceName = null;
        this.networkType = null;
        this.deviceStorage = null;
    }

    public String getWeatherResponse() {
        return weatherResponse;
    }

    public void setWeatherResponse(String weatherResponse) {
        this.weatherResponse = weatherResponse;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getDeviceStorage() {
        return deviceStorage;
    }

    public void setDeviceStorage(String deviceStorage) {
        this.deviceStorage = deviceStorage;
    }
}
