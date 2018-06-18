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

import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.github.aakira.expandablelayout.Utils;
import com.pacific.timer.Rx2Timer;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.scan.ScanRecord;
import com.polidea.rxandroidble2.scan.ScanResult;
import com.polidea.rxandroidble2.scan.ScanSettings;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;

public class ScannerFragment extends BaseMainFragment implements View.OnFocusChangeListener {
    public static final String TAG = ScannerFragment.class.getSimpleName();

    private static final String NO_FILTERS = "No filter";

    private RxBleClient rxBleClient;
    private HashMap<String, LeBeacon> mLeBeaconHashMap = new HashMap<String, LeBeacon>();
    private UUID ALFABEACON_UUID = UUID.fromString("0000a55a-0000-1000-8000-00805f9b34fb");
    private Rx2Timer mCountTimer = null;

    private Disposable scanSubscription;
    private DeviceAdapter mAdapter;

    private String mFilterString = "";
    private String mFilterEditString = "";
    private boolean mFilterStringEnable = false;
    private int mRssiFilter = -100;
    private boolean mRssiFilterEnable = false;

    // GUI component
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private ExpandableLinearLayout mExpandLayout;
    private TextView mExpandTitle;
    private RelativeLayout mExpandButton;
    private EditText mFilterStringEditText;
    private ImageButton mFilterStringCancelButton;
    private SeekBar mRssiFilterSeekBar;
    private TextView mRssiFilterTextView;
    private ImageButton mFilterRssiCancelButton;
    private FloatingActionButton mFloatingActionButton;
    private RecyclerViewEmptySupport mRecycleView;
    private View mEmptyView;

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

        // Expandable
        View expandableView = view.findViewById(R.id.expandablebar);
        mExpandTitle = (TextView) expandableView.findViewById(R.id.text_expandable_title);
        mExpandButton = (RelativeLayout) expandableView.findViewById(R.id.relative_layout_expandable_button);
        mExpandLayout = (ExpandableLinearLayout) expandableView.findViewById(R.id.expandable_layout);
        mFilterStringEditText = (EditText) expandableView.findViewById(R.id.input_filter_string);
        mFilterStringEditText.setOnFocusChangeListener(this);
        mFilterStringEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                mFilterEditString = mFilterStringEditText.getText().toString();
                if (mFilterEditString.length() > 0) {
                    mFilterStringEnable = true;
                } else {
                    mFilterStringEnable = false;
                }
                updateFilterString();
            }
        });

        mFilterStringCancelButton = (ImageButton) expandableView.findViewById(R.id.button_filter_string_cancel);
        mFilterStringCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFilterStringEnable = false;
                mFilterStringEditText.setText("");
                updateFilterString();
            }
        });
        mRssiFilterTextView = (TextView)expandableView.findViewById(R.id.text_rssi_filter);
        mFilterRssiCancelButton = (ImageButton) expandableView.findViewById(R.id.button_rssi_filter_cancel);
        mFilterRssiCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRssiFilterEnable = false;
                updateFilterString();
            }
        });
        mRssiFilterSeekBar = (SeekBar) expandableView.findViewById(R.id.seekbar_rssi_filter);
        mRssiFilterSeekBar.setMax(100);
        mRssiFilterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {}
             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {}
             @Override
             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)   {
                  Log.v(TAG, String.format("rssi filter %d", -progress));
                  mRssiFilterTextView.setText(String.format("%d dBm", -progress));
                  mRssiFilter = -progress;
                  mRssiFilterEnable = true;
                  updateFilterString();
             }
        });
        mRssiFilterSeekBar.setProgress(100);
        mExpandTitle.setText(NO_FILTERS);
        mExpandButton.setRotation(0f);
        mExpandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mExpandLayout.toggle();
            }
        });

        expandableView.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.myPrimaryWhite));
        mExpandLayout.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.myPrimaryWhite));
        mExpandLayout.setInterpolator(Utils.createInterpolator(Utils.BOUNCE_INTERPOLATOR));
        mExpandLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                createRotateAnimator(mExpandButton, 0f, 180f).start();
            }
            @Override
            public void onPreClose() {
                createRotateAnimator(mExpandButton, 180f, 0f).start();
            }
        });
        expandableView.setVisibility(View.VISIBLE);

        mFloatingActionButton = (FloatingActionButton)view.findViewById(R.id.scan_fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleScan();
            }
        });

        mRecycleView = (RecyclerViewEmptySupport) view.findViewById(R.id.recycle_view);
        mRecycleView.setLayoutManager(new LinearLayoutManager(_mActivity));

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
        hideSoftInput();
        mCountTimer = Rx2Timer.builder()
            .initialDelay(0)
            .period(1)
            .take(250)
            .unit(TimeUnit.MILLISECONDS)
            .onCount(new Rx2Timer.OnCount() {
                @Override
                public void onCount(Long count) {
                }
            })
            .onError(new Rx2Timer.OnError() {
                @Override
                public void onError(Throwable t) {
                }
            })
            .onComplete(new Rx2Timer.OnComplete() {
                @Override
                public void onComplete() {
                    updateBeaconList();
                    mCountTimer.restart();
                }
            })
            .build();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            hideSoftInput();
        }
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
             mCountTimer.start();

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
            mCountTimer.stop();
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
                previous.setDatetime(new Date());
                mLeBeaconHashMap.put(previous.getMacAddress(), previous);
            }
//            updateBeaconList();
        }
    }

    private void updateFilterString() {
        String filterRssi = mRssiFilterEnable ? String.format("%ddBm;", mRssiFilter) : "" ;
        String filterString = mFilterStringEnable ? mFilterEditString + ";" : "";
        mFilterString = String.format("%s%s", filterString, filterRssi);
        if (mFilterString.length() == 0) {
            mExpandTitle.setText(NO_FILTERS);
            mAdapter.getFilter().filter("");
        } else {
            mExpandTitle.setText(mFilterString);
            mAdapter.getFilter().filter(mFilterString);
        }
    }

    private void updateBeaconList() {
        List<LeBeacon> valueList = new ArrayList<LeBeacon>(mLeBeaconHashMap.values());
        mAdapter.updateDatas(valueList);
//        mAdapter.notifyDataSetChanged();
        mAdapter.getFilter().filter(mFilterString);
    }

    private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }
}
