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
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alfaloop.android.alfabeacon.MainActivity;
import com.alfaloop.android.alfabeacon.R;
import com.alfaloop.android.alfabeacon.base.BaseBackFragment;

import com.alfaloop.android.alfabeacon.models.LeBeacon;
import com.alfaloop.android.alfabeacon.utility.ParserUtils;
import com.alfaloop.android.alfabeacon.utility.UuidUtils;
import com.dd.morphingbutton.MorphingButton;
import com.dd.morphingbutton.impl.IndeterminateProgressButton;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleConnection;
import com.polidea.rxandroidble2.RxBleDevice;
import com.victor.loading.book.BookLoading;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;


public class ConnectedFragment extends BaseBackFragment implements View.OnClickListener, View.OnFocusChangeListener{
    public static final String TAG = ConnectedFragment.class.getSimpleName();
    private static final String MACADDR = "macaddress";
    private static final String DEVICENAME = "devicename";
    private static final String DEVICETYPE = "devicetype";
    private static final String BATTERY = "battery";

    /** AlfaBeacon iBeacon Service UUID */
    public final static UUID UUID_ALFA_IBEACON_SERVICE = UUID.fromString("a78e0001-f6c2-09a3-e9f9-128abca31297");
    public static final UUID UUID_ALFA_IBEACON_CHARACTER_UUID = UUID.fromString("a78e0002-f6c2-09a3-e9f9-128abca31297");
    public static final UUID UUID_ALFA_IBEACON_CHARACTER_MAJOR = UUID.fromString("a78e0003-f6c2-09a3-e9f9-128abca31297");
    public static final UUID UUID_ALFA_IBEACON_CHARACTER_MINOR = UUID.fromString("a78e0004-f6c2-09a3-e9f9-128abca31297");
    public static final UUID UUID_ALFA_IBEACON_CHARACTER_TXM = UUID.fromString("a78e0005-f6c2-09a3-e9f9-128abca31297");
    private BluetoothGattService mAlfaAppleBeaconService;
    private BluetoothGattCharacteristic mAlfaAppleBeaconUuidCharacteristic;
    private BluetoothGattCharacteristic mAlfaAppleBeaconMajorCharacteristic;
    private BluetoothGattCharacteristic mAlfaAppleBeaconMinorCharacteristic;
    private BluetoothGattCharacteristic mAlfaAppleBeaconTxmCharacteristic;

    /** AlfaBeacon Radio Service UUID */
    public final static UUID UUID_ALFA_RADIO_SERVICE = UUID.fromString("19FA0001-F6C2-09A3-E9F9-128ABCA31297");
    public static final UUID UUID_ALFA_RADIO_CHARACTER_INTERVAL = UUID.fromString("19FA0002-F6C2-09A3-E9F9-128ABCA31297");
    public static final UUID UUID_ALFA_RADIO_CHARACTER_TXPOWER = UUID.fromString("19FA0003-F6C2-09A3-E9F9-128ABCA31297");
    public static final UUID UUID_ALFA_RADIO_CHARACTER_CONN = UUID.fromString("19FA0004-F6C2-09A3-E9F9-128ABCA31297");
    private BluetoothGattService mAlfaRadioService;
    private BluetoothGattCharacteristic mAlfaRadioIntervalCharacteristic;
    private BluetoothGattCharacteristic mAlfaRadioTxPowerCharacteristic;
    private BluetoothGattCharacteristic mAlfaRadioConnCharacteristic;

    /** AlfaBeacon Alfa2477s Service UUID */
    public final static UUID UUID_ALFA_2477S_SERVICE = UUID.fromString("903E0001-F6C2-09A3-E9F9-128ABCA31297");
    public static final UUID UUID_ALFA_2477S_CHARACTER_RF_ATTE = UUID.fromString("903E0002-F6C2-09A3-E9F9-128ABCA31297");
    public static final UUID UUID_ALFA_2477S_CHARACTER_BUTTON = UUID.fromString("903E0003-F6C2-09A3-E9F9-128ABCA31297");
    public static final UUID UUID_ALFA_2477S_CHARACTER_BUZZER = UUID.fromString("903E0004-F6C2-09A3-E9F9-128ABCA31297");
    public static final UUID UUID_ALFA_2477S_CHARACTER_LED = UUID.fromString("903E0005-F6C2-09A3-E9F9-128ABCA31297");
    private BluetoothGattService mAlfa2477SService;
    private BluetoothGattCharacteristic mAlfa2477SRfAtteCharacteristic;
    private BluetoothGattCharacteristic mAlfa2477SButtonCharacteristic;
    private BluetoothGattCharacteristic mAlfa2477SBuzzerCharacteristic;
    private BluetoothGattCharacteristic mAlfa2477SLedCharacteristic;

    /** AlfaBeacon LINE Simple  Service UUID */
    public final static UUID UUID_ALFA_LINESIMPLEBEACON_SERVICE = UUID.fromString("A78D0001-F6C2-09A3-E9F9-128ABCA31297");
    public static final UUID UUID_ALFA_LINESIMPLEBEACON_CHARACTER_HWID = UUID.fromString("A78D0002-F6C2-09A3-E9F9-128ABCA31297");
    public static final UUID UUID_ALFA_LINESIMPLEBEACON_CHARACTER_DM = UUID.fromString("A78D0003-F6C2-09A3-E9F9-128ABCA31297");

    // RX Android
    private RxBleClient rxBleClient;
    private RxBleConnection rxBleConnection;
    private Disposable connectionDisposable;
    private int mergeProcessCounter = 0;
    private boolean disconnecClicked = false;

    // GUI components
    private Toolbar mToolbar;
    private BookLoading mBookLoading;
    // GUI components - Header
    private View mHeaderView;
    private ImageView mHeaderBeaconImage;
    private TextView mHeaderDeviceName;
    private View mHeaderRssiContainer;
    private TextView mHeaderRssi;
    private View mHeaderBatteryContainer;
    private TextView mHeaderBattery;
    // GUI components - iBeacon
    private View     iBeaconView;
    private TextView iBeaconHeaderText;
    private EditText iBeaconUuidEdit;
    private EditText iBeaconMajorEdit;
    private EditText iBeaconMinorEdit;
    private EditText iBeaconTxmEdit;
    private IndeterminateProgressButton iBeaconMorphingButton;
    // GUI components - Radio
    private View     radioView;
    private TextView radioIntervalTextView;
    private SeekBar radioIntervalSeekBar;
    private TextView radioTxpowerlTextView;
    private SeekBar radioTxpowerSeekBar;
    private IndeterminateProgressButton radioMorphingButton;
    // GUI components - Alfa2477s
    private View     alfa2477sView;
    private TextView alfa2477sRfatteTextView;
    private SeekBar alfa2477sRfatteSeekBar;
    private IndeterminateProgressButton alfa2477sMorphingButton;

    // Data
    private String macAddress;
    private String deviceName;
    private int deviceType;
    private int batteryLevel;
    private int radioInterval;
    private int radioTxPower;
    private int alfa2477sRfatte;

    public static ConnectedFragment newInstance(LeBeacon beacon) {
        ConnectedFragment fragment = new ConnectedFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MACADDR, beacon.getMacAddress());
        bundle.putString(DEVICENAME, beacon.getDeviceName());
        bundle.putInt(BATTERY, beacon.getBatteryLevel());
        bundle.putInt(DEVICETYPE, beacon.getType());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
        }
        macAddress = getArguments().getString(MACADDR, "");
        deviceName = getArguments().getString(DEVICENAME, "");
        deviceType = getArguments().getInt(DEVICETYPE, 0);
        batteryLevel = getArguments().getInt(BATTERY, 0);
        rxBleClient = ((MainActivity)_mActivity).getRxBluetooth();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connected, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        // init toolbar
        disconnecClicked = false;
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle(String.format("%s - %s", getResources().getString(R.string.establish_connection), macAddress));
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (disconnecClicked) {
                    return;
                }
                disconnecClicked = true;
                byte[] disconnect_command = new byte[1];
                disconnect_command[0] = 0x01;
                if (mAlfaRadioConnCharacteristic != null && rxBleConnection != null) {
                    rxBleConnection.writeCharacteristic(mAlfaRadioConnCharacteristic, disconnect_command)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(bytes -> {
                        Log.d(TAG, "disconnected");
                    });
                } else {
                    if (connectionDisposable != null) {
                       connectionDisposable.dispose();
                   }
                }
            }
        });

        // loading icon
        mBookLoading = (BookLoading) view.findViewById(R.id.loading);
        mBookLoading.setVisibility(View.GONE);

        initHeaderView(view);
        initAppleBeaconView(view);
        initRadioView(view);
        initAlfa2477sView(view);
    }

    private void initHeaderView(View view) {
        mHeaderView = (View) view.findViewById(R.id.header_view);
        mHeaderBeaconImage = (ImageView) mHeaderView.findViewById(R.id.beacon_type_icon);
        mHeaderDeviceName = (TextView) mHeaderView.findViewById(R.id.device_name);
        mHeaderRssiContainer = (View) mHeaderView.findViewById(R.id.rssi_container);
        mHeaderRssi = (TextView) mHeaderRssiContainer.findViewById(R.id.rssi);
        mHeaderBatteryContainer = (View) mHeaderView.findViewById(R.id.battery_container);
        mHeaderBattery = (TextView) mHeaderBatteryContainer.findViewById(R.id.battery);

        if (deviceType == LeBeacon.LEBEACON_TYPE_AA) {
            mHeaderBeaconImage.setImageResource(R.drawable.ic_beacon_alfa_aa);
            mHeaderDeviceName.setText(String.format("%s - AlfaAA", deviceName));
        } else if (deviceType == LeBeacon.LEBEACON_TYPE_USB) {
            mHeaderBeaconImage.setImageResource(R.drawable.ic_beacon_alfa_usb);
            mHeaderDeviceName.setText(String.format("%s - AlfaUSB", deviceName));
        } else if (deviceType == LeBeacon.LEBEACON_TYPE_2477) {
            mHeaderBeaconImage.setImageResource(R.drawable.ic_beacon_alfa_2477);
            mHeaderDeviceName.setText(String.format("%s - Alfa2477", deviceName));
        } else if (deviceType == LeBeacon.LEBEACON_TYPE_2477S) {
            mHeaderBeaconImage.setImageResource(R.drawable.ic_beacon_alfa_2477);
            mHeaderDeviceName.setText(String.format("%s - Alfa2477s", deviceName));
        }
        mHeaderBattery.setText(String.format("Battery %d%%", batteryLevel));
    }

    private void updateHeaderRssi(int rssi) {
        mHeaderRssi.setText(String.format("Rssi: %d dBm", rssi));
    }

    private void initAppleBeaconView(View view) {
        iBeaconView = (View) view.findViewById(R.id.ibeacon_edit);

        iBeaconMorphingButton =  (IndeterminateProgressButton) iBeaconView.findViewById(R.id.ibeacon_save_button);
        iBeaconMorphingButton.setOnClickListener(this);
        morphToSquare(iBeaconMorphingButton, 0);

        iBeaconHeaderText = (TextView) iBeaconView.findViewById(R.id.ibeacon_header_text);
        iBeaconUuidEdit = (EditText) iBeaconView.findViewById(R.id.input_ibeacon_uuid);
        iBeaconMajorEdit = (EditText) iBeaconView.findViewById(R.id.input_ibeacon_major);
        iBeaconMinorEdit = (EditText) iBeaconView.findViewById(R.id.input_ibeacon_minor);
        iBeaconTxmEdit = (EditText) iBeaconView.findViewById(R.id.input_ibeacon_txm);

        iBeaconUuidEdit.setText("82F10001-37E9-10CB-9E9F-42303BE58266");
        iBeaconUuidEdit.setOnFocusChangeListener(this);
        iBeaconUuidEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
               /* if (inputValidation(editable)) {
                    input_validation.setVisibility(View.VISIBLE);
                    input_validation.setText("You must enter your Age");
                } else {
                    input_validation.setVisibility(View.GONE);
                    final_input_value = Integer.parseInt(editable.toString());
                }*/
            }
        });
        iBeaconMajorEdit.setText(String.format("1234"));
        iBeaconMajorEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
               /* if (iBeaconMajorEdit.getText().length() > 0) {
                    int major = Integer.parseInt(iBeaconMajorEdit.getText().toString());
                    if (major > 65535) {
                        iBeaconMajorEdit.setText("65535");
                    }
                } else {
                    iBeaconMajorEdit.setText("0");
                }*/
            }
        });
        iBeaconMajorEdit.setOnFocusChangeListener(this);
        iBeaconMinorEdit.setText(String.format("8"));
        iBeaconMinorEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
               /* if (iBeaconMinorEdit.getText().length() > 0) {
                    int major = Integer.parseInt(iBeaconMinorEdit.getText().toString());
                    if (major > 65535) {
                        iBeaconMinorEdit.setText("65535");
                    }
                } else {
                    iBeaconMinorEdit.setText("0");
                }*/
            }
        });
        iBeaconMinorEdit.setOnFocusChangeListener(this);
        iBeaconTxmEdit.setText(String.format("-58"));
        iBeaconTxmEdit.setOnFocusChangeListener(this);
    }

    private void initRadioView(View view) {
        radioView= (View) view.findViewById(R.id.radio_edit);
        radioIntervalTextView = radioView.findViewById(R.id.radio_interval_text);
        radioIntervalSeekBar = radioView.findViewById(R.id.radio_interval_progress);
        radioIntervalSeekBar.setMax(8);
        radioIntervalSeekBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)   {
                    Log.v(TAG, String.format("radio interval progress %d", progress));
                    if (progress == 0) {
                        radioIntervalTextView.setText("152.5 ms");
                        radioInterval = 152;
                    } else if (progress == 1) {
                        radioIntervalTextView.setText("318.75 ms");
                        radioInterval = 318;
                    } else if (progress == 2) {
                        radioIntervalTextView.setText("546.25 ms");
                        radioInterval = 546;
                    } else if (progress == 3) {
                        radioIntervalTextView.setText("852.5 ms");
                        radioInterval = 852;
                    } else if (progress == 4) {
                        radioIntervalTextView.setText("1022.5 ms");
                        radioInterval = 1022;
                    } else if (progress == 5) {
                        radioIntervalTextView.setText("2045.0 ms");
                        radioInterval = 2045;
                    } else if (progress == 6) {
                        radioIntervalTextView.setText("4082.5 ms");
                        radioInterval = 4082;
                    } else if (progress == 7) {
                        radioIntervalTextView.setText("5120.0 ms");
                        radioInterval = 5120;
                    } else if (progress == 8) {
                        radioIntervalTextView.setText("10040.0 ms");
                        radioInterval = 10040;
                    }
                }
            }
        );
        radioIntervalSeekBar.setProgress(0);
        radioIntervalTextView.setText("152.5 ms");
        radioTxpowerlTextView = radioView.findViewById(R.id.radio_txpower_text);
        radioTxpowerSeekBar = radioView.findViewById(R.id.radio_txpower_progress);
        radioTxpowerSeekBar.setMax(7);
        radioTxpowerSeekBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {}
             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {}
             @Override
             public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)   {
                 Log.v(TAG, String.format("radio txpower progress %d", progress));
                 if (progress == 0) {
                     radioTxpowerlTextView.setText("+4 dBm");
                     radioTxPower = 4;
                 } else if (progress == 1) {
                     radioTxpowerlTextView.setText("+0 dBm");
                     radioTxPower = 0;
                 } else if (progress == 2) {
                     radioTxpowerlTextView.setText("-4 dBm");
                     radioTxPower = -4;
                 } else if (progress == 3) {
                     radioTxpowerlTextView.setText("-8 dBm");
                     radioTxPower = -8;
                 } else if (progress == 4) {
                     radioTxpowerlTextView.setText("-12 dBm");
                     radioTxPower = -12;
                 } else if (progress == 5) {
                     radioTxpowerlTextView.setText("-16 dBm");
                     radioTxPower = -16;
                 } else if (progress == 6) {
                     radioTxpowerlTextView.setText("-20 dBm");
                     radioTxPower = -20;
                 } else if (progress == 7) {
                     radioTxpowerlTextView.setText("-40 dBm");
                     radioTxPower = -40;
                 }
             }
        });
        radioTxpowerSeekBar.setProgress(0);
        radioTxpowerlTextView.setText("+4 dBm");
        radioMorphingButton =  (IndeterminateProgressButton) radioView.findViewById(R.id.radio_save_button);
        radioMorphingButton.setOnClickListener(this);
        morphToSquare(radioMorphingButton, 0);
    }

    private void initAlfa2477sView(View view) {
        alfa2477sView = (View) view.findViewById(R.id.alfa2477s_edit);
        alfa2477sRfatteTextView = alfa2477sView.findViewById(R.id.alfa2477s_rfatte_text);
        alfa2477sRfatteSeekBar = alfa2477sView.findViewById(R.id.alfa2477s_rfatte_progress);
        alfa2477sRfatteSeekBar.setMax(6);
        alfa2477sRfatteSeekBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)   {
                Log.v(TAG, String.format("2477s rfatte progress %d", progress));
                if (progress == 0) {
                    alfa2477sRfatteTextView.setText("-0 dBm");
                } else if (progress == 1) {
                    alfa2477sRfatteTextView.setText("-1 dBm");
                } else if (progress == 2) {
                    alfa2477sRfatteTextView.setText("-2 dBm");
                } else if (progress == 3) {
                    alfa2477sRfatteTextView.setText("-4 dBm");
                } else if (progress == 4) {
                    alfa2477sRfatteTextView.setText("-8 dBm");
                } else if (progress == 5) {
                    alfa2477sRfatteTextView.setText("-16 dBm");
                } else if (progress == 6) {
                    alfa2477sRfatteTextView.setText("-31 dBm");
                }
                alfa2477sRfatte = progress;
            }
        });
        alfa2477sRfatteSeekBar.setProgress(0);
        alfa2477sRfatteTextView.setText("-0 dBm");
        alfa2477sMorphingButton =  (IndeterminateProgressButton) alfa2477sView.findViewById(R.id.alfa2477s_save_button);
        alfa2477sMorphingButton.setOnClickListener(this);
        morphToSquare(alfa2477sMorphingButton, 0);
    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        initDelayView();
    }
    @Override
    public boolean onBackPressedSupport() {
        return true;
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        hideSoftInput();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            hideSoftInput();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibeacon_save_button) {
            morphingBeaconButtonClicked();
        } else if(v.getId() == R.id.radio_save_button) {
            morphingRadioButtonClicked();
        } else if(v.getId() == R.id.alfa2477s_save_button) {
            morphingAlfa2477sButtonClicked();
        }
    }

    private void initDelayView() {
        // establish connection
        mBookLoading.start();
        mBookLoading.setVisibility(View.VISIBLE);
        RxBleDevice device = rxBleClient.getBleDevice(macAddress);

        connectionDisposable = device.establishConnection(false) // <-- autoConnect flag
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(this::onConnectionDispose)
                .subscribe(this::onConnectionReceived, this::onConnectionFailure);
    }

    private void morphingBeaconButtonClicked( ) {
        morphToProcess(iBeaconMorphingButton);

        UUID ibeaconUuid = UUID.fromString(iBeaconUuidEdit.getText().toString());
        byte[] uuidArray = UuidUtils.asBytesFromUuid(ibeaconUuid);
        String major = iBeaconMajorEdit.getText().toString();
        byte[] majorArray = new byte[2];
        majorArray[0] = (byte)((Integer.parseInt(major)  >> 8) & 0xff);
        majorArray[1] = (byte)(Integer.parseInt(major)  & 0xff);
        String minor = iBeaconMinorEdit.getText().toString();
        byte[] minorArray = new byte[2];
        minorArray[0] = (byte)((Integer.parseInt(minor)  >> 8) & 0xff);
        minorArray[1] = (byte)(Integer.parseInt(minor)  & 0xff);
        String txm = iBeaconTxmEdit.getText().toString();
        byte[] txmArray = new byte[1];
        txmArray[0] = (byte)(Integer.parseInt(txm)  & 0xff);

        List<Single<byte[]>> singles = new ArrayList<>();
        singles.add(rxBleConnection.writeCharacteristic(mAlfaAppleBeaconUuidCharacteristic, uuidArray));
        singles.add(rxBleConnection.writeCharacteristic(mAlfaAppleBeaconMajorCharacteristic, majorArray));
        singles.add(rxBleConnection.writeCharacteristic(mAlfaAppleBeaconMinorCharacteristic, minorArray));
        singles.add(rxBleConnection.writeCharacteristic(mAlfaAppleBeaconTxmCharacteristic, txmArray));
        mergeProcessCounter = 0;
        Single.merge(singles)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(bytes -> {
                Log.d(TAG, String.format("update beacon profile Success %s", ParserUtils.bytesToHex(bytes)));
                mergeProcessCounter++;
                if (mergeProcessCounter == singles.size()) {
                    mergeProcessCounter = 0;
                    morphToCompleted(iBeaconMorphingButton);
                }
            }, this::onConnectionFailure);
    }

    private void morphingRadioButtonClicked() {

        morphToProcess(radioMorphingButton);
        byte[] radioIntervalArray = new byte[2];
        byte[] radioTxPowerArray = new byte[1];
        radioIntervalArray[0] = (byte)(radioInterval & 0xff);
        radioIntervalArray[1] = (byte)((radioInterval >> 8) & 0xff);
        radioTxPowerArray[0] = (byte)radioTxPower;

        List<Single<byte[]>> singles = new ArrayList<>();
        singles.add(rxBleConnection.writeCharacteristic(mAlfaRadioIntervalCharacteristic, radioIntervalArray));
        singles.add(rxBleConnection.writeCharacteristic(mAlfaRadioTxPowerCharacteristic, radioTxPowerArray));
        mergeProcessCounter = 0;
        Single.merge(singles)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(bytes -> {
                Log.d(TAG, String.format("update radio profile Success"));
                mergeProcessCounter++;
                if (mergeProcessCounter == singles.size()) {
                    mergeProcessCounter = 0;
                    morphToCompleted(radioMorphingButton);
                }
            }, this::onConnectionFailure);
    }

    private void morphingAlfa2477sButtonClicked() {
        morphToProcess(alfa2477sMorphingButton);
        byte[] alfa2477sRfAtteArray = new byte[1];
        alfa2477sRfAtteArray[0] = (byte)alfa2477sRfatte;

        List<Single<byte[]>> singles = new ArrayList<>();
        singles.add(rxBleConnection.writeCharacteristic(mAlfa2477SRfAtteCharacteristic, alfa2477sRfAtteArray));

        Single.merge(singles)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(bytes -> {
                Log.d(TAG, String.format("update alfa2477s profile Success"));
                morphToCompleted(alfa2477sMorphingButton);
            }, this::onConnectionFailure);
    }

    private void updateRssi(Long along) {
        if (rxBleConnection != null && connectionDisposable != null) {
            if (!connectionDisposable.isDisposed()) {
                rxBleConnection.readRssi()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( rssi -> {
                            updateHeaderRssi(rssi);
                        });
            }
        }
    }

    private void onConnectionReceived(RxBleConnection connection) {
        // start discovery
        rxBleConnection = connection;

        rxBleConnection.discoverServices()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(rxBleDeviceServices -> {
            List<BluetoothGattService> bleServices = rxBleDeviceServices.getBluetoothGattServices();
            for (BluetoothGattService service : bleServices) {
                Log.i(TAG, String.format("services: %s", service.getUuid().toString()));
                if (service.getUuid().equals(UUID_ALFA_IBEACON_SERVICE)) {
                    Log.i(TAG, String.format("found iBeacon service"));
                    mAlfaAppleBeaconService = service;
                    for (BluetoothGattCharacteristic character: service.getCharacteristics()) {
                        Log.i(TAG, String.format("character: %s", character.getUuid().toString()));
                        if (character.getUuid().equals(UUID_ALFA_IBEACON_CHARACTER_UUID)) {
                            Log.i(TAG, String.format("found iBeacon uuid characteristic"));
                            mAlfaAppleBeaconUuidCharacteristic = character;
                            rxBleConnection.readCharacteristic(mAlfaAppleBeaconUuidCharacteristic)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe( value -> {
                                    iBeaconUuidEdit.setText(UuidUtils.asUuidFromByteArray(value).toString());
                                }, this::onConnectionFailure);
                        } else if  (character.getUuid().equals(UUID_ALFA_IBEACON_CHARACTER_MAJOR)) {
                            mAlfaAppleBeaconMajorCharacteristic = character;
                            rxBleConnection.readCharacteristic(mAlfaAppleBeaconMajorCharacteristic)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe( value -> {
                                        iBeaconMajorEdit.setText(String.format("%d", ParserUtils.byteArrayToInteger(value)));
                                    }, this::onConnectionFailure);
                          } else if  (character.getUuid().equals(UUID_ALFA_IBEACON_CHARACTER_MINOR)) {
                            mAlfaAppleBeaconMinorCharacteristic = character;
                            rxBleConnection.readCharacteristic(mAlfaAppleBeaconMinorCharacteristic)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe( value -> {
                                        iBeaconMinorEdit.setText(String.format("%d", ParserUtils.byteArrayToInteger(value)));
                                    }, this::onConnectionFailure);
                        } else if  (character.getUuid().equals(UUID_ALFA_IBEACON_CHARACTER_TXM)) {
                            mAlfaAppleBeaconTxmCharacteristic = character;
                            rxBleConnection.readCharacteristic(mAlfaAppleBeaconTxmCharacteristic)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe( value -> {
                                    iBeaconTxmEdit.setText(String.format("%d", (int)value[0]));
                                }, this::onConnectionFailure);
                        }
                    }
                    iBeaconView.setVisibility(View.VISIBLE);
                } else if (service.getUuid().equals(UUID_ALFA_RADIO_SERVICE)) {
                    mAlfaRadioService = service;
                    for (BluetoothGattCharacteristic character: service.getCharacteristics()) {
                        Log.i(TAG, String.format("character: %s", character.getUuid().toString()));
                        if (character.getUuid().equals(UUID_ALFA_RADIO_CHARACTER_INTERVAL)) {
                            mAlfaRadioIntervalCharacteristic = character;
                            rxBleConnection.readCharacteristic(mAlfaRadioIntervalCharacteristic)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe( value -> {
                                        radioInterval = (value[1] & 0xff) * 0x100 + (value[0] & 0xff);
                                        Log.i(TAG, String.format("found Radio interval characteristic %d", radioInterval));
                                        updateRadioIntervalSeekbar(radioInterval);
                                    }, this::onConnectionFailure);
                        } else if  (character.getUuid().equals(UUID_ALFA_RADIO_CHARACTER_TXPOWER)) {
                            Log.i(TAG, String.format("found Radio txpower characteristic"));
                            mAlfaRadioTxPowerCharacteristic = character;
                            rxBleConnection.readCharacteristic(mAlfaRadioTxPowerCharacteristic)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe( value -> {
                                        int txpower = (int)value[0] ;
                                        radioTxPower = txpower;
                                        Log.i(TAG, String.format("found Radio txpower characteristic %d", txpower));
                                        updateRadioTxPowerSeekbar(txpower);
                                    }, this::onConnectionFailure);
                        } else if (character.getUuid().equals(UUID_ALFA_RADIO_CHARACTER_CONN)) {
                            Log.i(TAG, String.format("found Radio connection characteristic"));
                            mAlfaRadioConnCharacteristic = character;
                        }
                    }
                    radioView.setVisibility(View.VISIBLE);
                } else if (service.getUuid().equals(UUID_ALFA_2477S_SERVICE)) {
                    mAlfa2477SService = service;
                    for (BluetoothGattCharacteristic character: service.getCharacteristics()) {
                        Log.i(TAG, String.format("character: %s", character.getUuid().toString()));
                        if (character.getUuid().equals(UUID_ALFA_2477S_CHARACTER_RF_ATTE)) {
                            Log.i(TAG, String.format("found 2477s rf_atte characteristic"));
                            mAlfa2477SRfAtteCharacteristic = character;
                            rxBleConnection.readCharacteristic(mAlfa2477SRfAtteCharacteristic)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe( value -> {
                                        int rfatte = (int)value[0] ;
                                        alfa2477sRfatte = rfatte;
                                        alfa2477sRfatteSeekBar.setProgress(rfatte);
                                        Log.i(TAG, String.format("found Alfa2477s rfatte characteristic %d", rfatte));
                                    }, this::onConnectionFailure);
                        } else if  (character.getUuid().equals(UUID_ALFA_2477S_CHARACTER_BUTTON)) {
                            Log.i(TAG, String.format("found 2477s button characteristic"));
                            mAlfa2477SButtonCharacteristic = character;
                        } else if  (character.getUuid().equals(UUID_ALFA_2477S_CHARACTER_BUZZER)) {
                            Log.i(TAG, String.format("found 2477s button characteristic"));
                            mAlfa2477SBuzzerCharacteristic = character;
                        } else if  (character.getUuid().equals(UUID_ALFA_2477S_CHARACTER_LED)) {
                            Log.i(TAG, String.format("found 2477s button characteristic"));
                            mAlfa2477SLedCharacteristic = character;
                        }
                    }
                    alfa2477sView.setVisibility(View.VISIBLE);
                }
            }
            if (mAlfaRadioService == null) {
                new AlertDialog.Builder(_mActivity)
                        .setMessage(R.string.no_supported_device)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (connectionDisposable != null)
                                    connectionDisposable.dispose();
                            }
                        })
                        .show();
            } else {
                // disable the bookloading
                mBookLoading.stop();
                mBookLoading.setVisibility(View.GONE);
                mHeaderView.setVisibility(View.VISIBLE);
                mToolbar.setTitle(String.format("%s - %s", getResources().getString(R.string.connected), macAddress));
                Observable.interval(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::updateRssi, this::onConnectionFailure);
            }
        }, this::onConnectionFailure);
    }

    private void onConnectionFailure(Throwable throwable) {
        // show the dialog
        Log.i(TAG, "onConnectionFailure "+ throwable.toString());
        if (connectionDisposable != null) {
            if (!disconnecClicked) {
                new AlertDialog.Builder(_mActivity)
                .setMessage(R.string.connect_failure)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (connectionDisposable != null)
                            connectionDisposable.dispose();
                    }
                })
                .show();
            }
        }
    }

    private void onConnectionDispose() {
        Log.d(TAG, "onConnectionDispose");
        connectionDisposable = null;
        pop();
    }

    private void updateRadioIntervalSeekbar(int interval) {
        if (interval == 152) {
            radioIntervalSeekBar.setProgress(0);
        } else if (interval == 318) {
            radioIntervalSeekBar.setProgress(1);
        } else if (interval == 546) {
            radioIntervalSeekBar.setProgress(2);
        } else if (interval == 852) {
            radioIntervalSeekBar.setProgress(3);
        } else if (interval == 1022) {
            radioIntervalSeekBar.setProgress(4);
        } else if (interval == 2045) {
            radioIntervalSeekBar.setProgress(5);
        } else if (interval == 4082) {
            radioIntervalSeekBar.setProgress(6);
        } else if (interval == 5120) {
            radioIntervalSeekBar.setProgress(7);
        } else if (interval == 10040) {
            radioIntervalSeekBar.setProgress(8);
        }
    }

    private void updateRadioTxPowerSeekbar(int dbm) {
        if (dbm == +4) {
            radioTxpowerSeekBar.setProgress(0);
        } else if (dbm == 0) {
            radioTxpowerSeekBar.setProgress(1);
        } else if (dbm == -4) {
            radioTxpowerSeekBar.setProgress(2);
        } else if (dbm == -8) {
            radioTxpowerSeekBar.setProgress(3);
        } else if (dbm == -12) {
            radioTxpowerSeekBar.setProgress(4);
        } else if (dbm == -16) {
            radioTxpowerSeekBar.setProgress(5);
        } else if (dbm == -20) {
            radioTxpowerSeekBar.setProgress(6);
        } else if (dbm == -40) {
            radioTxpowerSeekBar.setProgress(7);
        }
    }

    private void morphToProcess(final IndeterminateProgressButton btnMorph) {
        // update button
        btnMorph.blockTouch(); // prevent user from clicking while button is in progress
        int progressColor = color(R.color.mb_blue);
        int color = color(R.color.mb_gray);
        int progressCornerRadius = dimen(R.dimen.mb_corner_radius_4);
        int width = dimen(R.dimen.mb_width_200);
        int height = dimen(R.dimen.mb_height_8);
        int duration = integer(R.integer.mb_animation);
        btnMorph.morphToProgress(color, progressCornerRadius, width, height, duration, progressColor);
    }

    private void morphToCompleted(final IndeterminateProgressButton btnMorph) {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                morphToSuccess(btnMorph);
                Handler handler2 = new Handler();
//                morphToSquare(btnMorph, 1500);
//                btnMorph.unblockTouch();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        morphToSquare(btnMorph, 500);
                        btnMorph.unblockTouch();

                    }
                }, 1500);
            }
        }, 1500);
    }

    private void morphToSquare(final MorphingButton btnMorph, int duration) {
        MorphingButton.Params square = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(dimen(R.dimen.mb_corner_radius_2))
                .width(dimen(R.dimen.mb_width_200))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.mb_blue))
                .colorPressed(color(R.color.mb_blue_dark))
                .text(getString(R.string.upload));
        btnMorph.morph(square);
    }

    private void morphToSuccess(final MorphingButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(integer(R.integer.mb_animation))
                .cornerRadius(dimen(R.dimen.mb_height_56))
                .width(dimen(R.dimen.mb_height_56))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.mb_green))
                .colorPressed(color(R.color.mb_green_dark))
                .icon(R.drawable.ic_done);
        btnMorph.morph(circle);
    }

    private void morphToFailure(final MorphingButton btnMorph, int duration) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(dimen(R.dimen.mb_height_56))
                .width(dimen(R.dimen.mb_height_56))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.mb_red))
                .colorPressed(color(R.color.mb_red_dark))
                .icon(R.drawable.ic_lock);
        btnMorph.morph(circle);
    }

    private int dimen(@DimenRes int resId) {
        return (int) getResources().getDimension(resId);
    }

    private int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }

    private int integer(@IntegerRes int resId) {
        return getResources().getInteger(resId);
    }

}
