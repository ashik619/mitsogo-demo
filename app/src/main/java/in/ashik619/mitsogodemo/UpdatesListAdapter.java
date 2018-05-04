package in.ashik619.mitsogodemo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import in.ashik619.mitsogodemo.data.UpdateData;

/**
 * Created by dilip on 4/5/18.
 */

public class UpdatesListAdapter extends RecyclerView.Adapter<UpdatesListAdapter.ViewHolder> {

    private final List<UpdateData> updateDataList;

    public UpdatesListAdapter(List<UpdateData> updateDataList) {
            this.updateDataList = updateDataList;
    }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.update_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            UpdateData data = updateDataList.get(position);
            if(data.getWeatherResponse()!=null){
                holder.weatherLayout.setVisibility(View.VISIBLE);
                holder.weatherText.setText((Double.parseDouble(data.getWeatherResponse())-273.15)+ " ℃");
            }else {
                holder.weatherLayout.setVisibility(View.GONE);
            }
            if(data.getBattery()!=null){
                holder.batteryLayout.setVisibility(View.VISIBLE);
                holder.batteryText.setText(data.getBattery());
            }else {
                holder.batteryLayout.setVisibility(View.GONE);
            }
            if(data.getDeviceName()!=null){
                holder.deviceLayout.setVisibility(View.VISIBLE);
                holder.deviceNameText.setText(data.getDeviceName());
            }else {
                holder.deviceLayout.setVisibility(View.GONE);
            }
            if(data.getDeviceStorage()!=null){
                holder.storageLayout.setVisibility(View.VISIBLE);
                holder.storageText.setText(data.getDeviceStorage());
            }else {
                holder.storageLayout.setVisibility(View.GONE);
            }
            if(data.getNetworkType()!=null){
                holder.networkTypeLayout.setVisibility(View.VISIBLE);
                holder.networkTypeText.setText(data.getNetworkType());
            }else {
                holder.networkTypeLayout.setVisibility(View.GONE);
            }
        }

    @Override
    public int getItemCount() {
        return updateDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView weatherText;
        public final TextView batteryText;
        public final TextView deviceNameText;
        public final TextView storageText;
        public final TextView networkTypeText;
        public final LinearLayout weatherLayout;
        public final LinearLayout batteryLayout;
        public final LinearLayout deviceLayout;
        public final LinearLayout storageLayout;
        public final LinearLayout networkTypeLayout;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            weatherText = (TextView) view.findViewById(R.id.weatherText);
            batteryText = (TextView) view.findViewById(R.id.batteryText);
            deviceNameText = (TextView) view.findViewById(R.id.deviceNameText);
            storageText = (TextView) view.findViewById(R.id.storageText);
            networkTypeText = (TextView) view.findViewById(R.id.networkTypeText);
            weatherLayout = (LinearLayout) view.findViewById(R.id.weatherLayout);
            batteryLayout = (LinearLayout) view.findViewById(R.id.batteryLayout);
            deviceLayout = (LinearLayout) view.findViewById(R.id.deviceLayout);
            storageLayout = (LinearLayout) view.findViewById(R.id.storageLayout);
            networkTypeLayout = (LinearLayout) view.findViewById(R.id.networkTypeLayout);
        }
    }
}
