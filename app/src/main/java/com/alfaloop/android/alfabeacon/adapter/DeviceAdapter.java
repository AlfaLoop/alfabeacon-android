/**
 * © Copyright AlfaLoop Technology Co., Ltd. 2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package com.alfaloop.android.alfabeacon.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfaloop.android.alfabeacon.R;
import com.alfaloop.android.alfabeacon.models.LeBeacon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.MyViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onConnectClick(int position);
    }

    private List<LeBeacon> mItems = new ArrayList<>();
    private LayoutInflater mInflater;
    private OnItemClickListener itemClickListener;

    public DeviceAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public void updateDatas(List<LeBeacon> items) {
        mItems.clear();
        mItems.addAll(items);
    }

    public void setItemClickListener(OnItemClickListener clickListener) {
        this.itemClickListener = clickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView view = (CardView) mInflater.inflate(R.layout.item_device, parent, false);
        final MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LeBeacon item = mItems.get(position);

        if (item.getType() == LeBeacon.LEBEACON_TYPE_AA) {
            holder.imvBeaconType.setImageResource(R.drawable.ic_beacon_alfa_aa);
        } else if (item.getType() == LeBeacon.LEBEACON_TYPE_USB) {
            holder.imvBeaconType.setImageResource(R.drawable.ic_beacon_alfa_usb);
        } else if (item.getType() == LeBeacon.LEBEACON_TYPE_2477) {
            holder.imvBeaconType.setImageResource(R.drawable.ic_beacon_alfa_2477);
        } else if (item.getType() == LeBeacon.LEBEACON_TYPE_2477S) {
            holder.imvBeaconType.setImageResource(R.drawable.ic_beacon_alfa_2477);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
        String date = formatter.format(item.getDatetime());
        holder.tvLastSeen.setText(date);

        // update device name textview
        if (item.getDeviceName() == null) {
            holder.tvDeviceName.setText("Unknown");
        } else if (item.getDeviceName().equals("")) {
            holder.tvDeviceName.setText("Unknown");
        } else {
            holder.tvDeviceName.setText(item.getDeviceName());
        }

        holder.imvConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onConnectClick(position);
                }
            }
        });

        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(position);
                }
            }
        });

        if (item.getiBeacon() != null) {
            holder.vBeaconContainer.setVisibility(View.VISIBLE);
            holder.vIBeacon.setVisibility(View.VISIBLE);
            holder.tvIBeaconUuid.setText(String.format("UUID • %s", item.getiBeacon().getUuid()));
            holder.tvIBeaconMajor.setText(String.format("Major • %d", item.getiBeacon().getMajor()));
            holder.tvIBeaconMinor.setText(String.format("Minor • %d", item.getiBeacon().getMinor()));
            holder.tvIBeaconTx.setText(String.format("Tx • %d dBm", item.getiBeacon().getTxInMeter()));
        } else {
            holder.vIBeacon.setVisibility(View.GONE);
            holder.vBeaconContainer.setVisibility(View.GONE);
        }

        holder.tvMacAddress.setText(item.getMacAddress());
        holder.tvRssi.setText(String.format("RSSI %d dBm", item.getRssi()));
        holder.tvBattery.setText(String.format("Battery %d%%", item.getBatteryLevel()));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView mainView;
        private ImageView imvBeaconType;
        private TextView tvDeviceName;
        private ImageView imvConnect;
        private TextView tvMacAddress;
        private TextView tvLastSeen;
        private View vBeaconContainer;
        private View vIBeacon;
        private TextView tvIBeaconUuid;
        private TextView tvIBeaconMajor;
        private TextView tvIBeaconMinor;
        private TextView tvIBeaconTx;
        private View vRSSIContainer;
        private TextView tvRssi;
        private View vBatteryContainer;
        private TextView tvBattery;

        public MyViewHolder(View itemView) {
            super(itemView);
            mainView = (CardView) itemView.findViewById(R.id.card);
            imvBeaconType = (ImageView) itemView.findViewById(R.id.beacon_type_icon);
            tvDeviceName = (TextView) itemView.findViewById(R.id.device_name);
            imvConnect = (ImageView) itemView.findViewById(R.id.connect);
            tvMacAddress = (TextView) itemView.findViewById(R.id.address);
            tvLastSeen = (TextView) itemView.findViewById(R.id.last_seen);

            vBeaconContainer = (View) itemView.findViewById(R.id.beacon_container);

            // iBeacon View
            vIBeacon = (View) vBeaconContainer.findViewById(R.id.ibeacon_item);
            tvIBeaconUuid = (TextView) vIBeacon.findViewById(R.id.proximity_uuid);
            tvIBeaconMajor = (TextView) vIBeacon.findViewById(R.id.major);
            tvIBeaconMinor = (TextView) vIBeacon.findViewById(R.id.minor);
            tvIBeaconTx = (TextView) vIBeacon.findViewById(R.id.tx);

            // Rssi View
            vRSSIContainer = (View) itemView.findViewById(R.id.rssi_container);
            tvRssi = (TextView) vRSSIContainer.findViewById(R.id.rssi);

            // Battery View
            vBatteryContainer = (View) itemView.findViewById(R.id.battery_container);
            tvBattery = (TextView) vBatteryContainer.findViewById(R.id.battery);
        }
    }
}
