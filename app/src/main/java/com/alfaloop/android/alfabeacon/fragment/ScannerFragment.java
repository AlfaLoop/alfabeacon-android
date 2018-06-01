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
package com.alfaloop.android.alfabeacon.fragment;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfaloop.android.alfabeacon.MainActivity;
import com.alfaloop.android.alfabeacon.R;
import com.alfaloop.android.alfabeacon.adapter.DeviceAdapter;
import com.alfaloop.android.alfabeacon.base.BaseMainFragment;
import com.alfaloop.android.alfabeacon.base.RecyclerViewEmptySupport;
import com.alfaloop.android.alfabeacon.models.AppleBeacon;
import com.alfaloop.android.alfabeacon.models.LeBeacon;
import com.alfaloop.android.alfabeacon.utility.ParserUtils;
import com.alfaloop.android.alfabeacon.utility.ScanFilterUtils;
import com.alfaloop.android.alfabeacon.utility.UuidUtils;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.scan.ScanRecord;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.scan.ScanSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;

public class ScannerFragment extends BaseMainFragment {
    public static final String TAG = ScannerFragment.class.getSimpleName();

    private RxBleClient rxBleClient;
    private HashMap<String, LeBeacon> mLeBeaconHashMap = new HashMap<String, LeBeacon>();
    private UUID ALFABEACON_UUID = UUID.fromString("0000a55a-0000-1000-8000-00805f9b34fb");

    private Disposable scanSubscription;
    private DeviceAdapter mAdapter;

    // GUI component
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private FloatingActionButton mFloatingActionButton;
    private RecyclerViewEmptySupport mRecycleView;
    private View mEmptyView;
    private ExpandableLinearLayout mExpandableLinearLayout;
    private RelativeLayout mExpandableButtonLayout;

    public static ScannerFragment newInstance() {
        ScannerFragment fragment = new ScannerFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxBleClient = ((MainActivity)_mActivity).getRxBluetooth();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        initView(view);
        setFragmentAnimator(new DefaultHorizontalAnimator());
        return view;
    }

    private void initView(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.app_name);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        mFloatingActionButton = (FloatingActionButton)view.findViewById(R.id.scan_fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleScan();
            }
        });

        mExpandableLinearLayout = (ExpandableLinearLayout)view.findViewById(R.id.expandableLayout);
        mExpandableLinearLayout.setInRecyclerView(true);
        mExpandableLinearLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
              
            }

            @Override
            public void onPreClose() {
               
            }
        });

        mExpandableButtonLayout = (RelativeLayout)view.findViewById(R.id.expandableButton);
        mExpandableButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
            }
        });
        
        mRecycleView = (RecyclerViewEmptySupport) view.findViewById(R.id.recycle_view);
        mRecycleView.setLayoutManager(new LinearLayoutManager(_mActivity));
        mAdapter = new DeviceAdapter(_mActivity);
        mAdapter.setItemClickListener(new DeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                stopScan();
                List<LeBeacon> valueList = new ArrayList<LeBeacon>(mLeBeaconHashMap.values());
                LeBeacon beacon = valueList.get(position);
                start(ConnectedFragment.newInstance(beacon));
            }
            @Override
            public void onConnectClick(int position) {
                stopScan();
                List<LeBeacon> valueList = new ArrayList<LeBeacon>(mLeBeaconHashMap.values());
                LeBeacon beacon = valueList.get(position);
                start(ConnectedFragment.newInstance(beacon));
            }
        });
        mRecycleView.setAdapter(mAdapter);
        mEmptyView = (View) view.findViewById(R.id.empty_view);
        mRecycleView.setEmptyView(mEmptyView);
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        initDelayView();
    }

    private void initDelayView() {
        // Auto start discover nearby devices
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
        if (scanSubscription != null){
            if (!scanSubscription.isDisposed()) {
                scanSubscription.dispose();
            }
        }
    }

    private void toggleScan() {
        if (scanSubscription == null) {
            startScan();
        } else if (scanSubscription.isDisposed()) {
            startScan();
        } else {
            stopScan();
        }
    }

    private boolean startScan(){
         boolean flag = false;
         if (scanSubscription == null){
             flag = true;
         } else if (scanSubscription.isDisposed()) {
             flag = true;
         }
         if (flag) {
             mProgressBar.setVisibility(View.VISIBLE);
             mFloatingActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.pause_icon));
             mLeBeaconHashMap = new HashMap<String, LeBeacon>();
             updateBeaconList();

             scanSubscription = rxBleClient.scanBleDevices(
                     new ScanSettings.Builder()
                             .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // change if needed
                             .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES) // change if needed
                             .build()
                     // add filters if needed
             ).observeOn(AndroidSchedulers.mainThread()
             ).subscribe(this::scanResultParser,
                     throwable -> {
                         // Handle an error here.
                     }
             );
             return true;
         }
         return false;
    }

    private boolean stopScan(){
        if (scanSubscription == null)
            return false;

        if (!scanSubscription.isDisposed()) {
            scanSubscription.dispose();
            mProgressBar.setVisibility(View.GONE);
            mFloatingActionButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.play_icon));
            return true;
        }
        return false;
    }

    private void scanResultParser(ScanResult result) {
        boolean hasUuid = false;
        int type = 0;
        int battery = 0;
        ScanRecord record = result.getScanRecord();
        if (record == null)
            return;

//        if(result.getRssi() < -58) {
//            return;
//        }

        String deviceName = record.getDeviceName();
        List<ParcelUuid> advUuids = record.getServiceUuids();
        if (deviceName == null || advUuids == null)
            return;

        for (ParcelUuid u : advUuids) {
//            Log.d(TAG, "ADV UUID " + u.toString());
            if (u.getUuid().equals(ALFABEACON_UUID)) {
                hasUuid = true;
                byte[] sdata = record.getServiceData(u);
                type = (int)sdata[0];
                battery = (int)sdata[1];
//                Log.i(TAG, ParserUtils.bytesToHex(sdata));
                break;
            }
        }

        if (hasUuid) {
            LeBeacon previous = mLeBeaconHashMap.get(result.getBleDevice().getMacAddress());
            if (previous == null) {
                LeBeacon beacon = new LeBeacon(type,
                        deviceName, result.getBleDevice().getMacAddress(),
                        result.getRssi(), battery, new Date());

                final Pair<Boolean, AppleBeacon> isAppleBeacon = ScanFilterUtils.isBeaconPattern(result);
                if (isAppleBeacon.first) {
                    beacon.setiBeacon(isAppleBeacon.second);
                }
                mLeBeaconHashMap.put(result.getBleDevice().getMacAddress(), beacon);
            } else {
                if (previous.getiBeacon() == null) {
                    // parser for the iBeacon
                    final Pair<Boolean, AppleBeacon> isAppleBeacon = ScanFilterUtils.isBeaconPattern(result);
                    if (isAppleBeacon.first) {
                        previous.setiBeacon(isAppleBeacon.second);
                    }
                }
                previous.setRssi(result.getRssi());
                mLeBeaconHashMap.put(previous.getMacAddress(), previous);
            }
            updateBeaconList();
        }
    }

    private void updateBeaconList() {
        List<LeBeacon> valueList = new ArrayList<LeBeacon>(mLeBeaconHashMap.values());
        mAdapter.updateDatas(valueList);
        mAdapter.notifyDataSetChanged();
    }
}
