//---------------------------------------------------------------------
//
// Copyright (c) 2016 CWB Tech Limited All rights reserved
//
//
//---------------------------------------------------------------------
// File: glanceMainActivity.java
// Author: Kevin Kwok (kevinkwok@cwb-tech.com)
//         William Chan (williamchan@cwb-tech.com)
// Project: Glance
//---------------------------------------------------------------------
package com.cwb.glancesampleapp;

import com.cwb.bleframework.GlanceMotionStreamRawData;
import com.cwb.bleframework.GlanceProtocolService;
import com.cwb.bleframework.GlanceProtocolService.RSCData2;
import com.cwb.bleframework.GlanceStatus;
import com.cwb.bleframework.InnobandProtocolService;
import com.cwb.bleframework.LanternProtocolService;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class glanceMainActivity extends Activity {
    private static final String TAG = "glanceMainActivity";
    private GlanceProtocolService mService;

    // Activity Result Code
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    public final static int REQUEST_LIGHT_AUTO_OFF = 3;

    private static final int HANDLER_PROCESS_BAS_DATA = 1000;
    private static final int HANDLER_PROCESS_UART_DATA = 1001;

    private boolean mIsFoundDevice = false;

    private BluetoothAdapter mBluetoothAdapter;

    private Button mScanButton;
    private boolean mIsScanStarted;
    private JumpHeight mJumpHeight;
    private String mLatestBLEDeviceAddress;
    private TextView mLatestBLEDeviceAddressTextView;
    private TextView mLatestBLEDeviceRSSITextView;
    private Spinner mGlanceDeviceAddressSpinnerList;

    private Button mConnectButton;
    private boolean mIsConnected;
    private TextView mConnectStateTextView;
    private String mConnectedDeviceAddress;

    private Button mReadRSCButton;
    private boolean mIsStartRSC;
    private Button mReadMotionStreamButton;
    private boolean mIsStartMotionStream;
    private TextView mRSCResultTextView;
    private TextView mBatteryLevelTextView;

    private Button mGetBatteryButton;

    //private Handler mGetBatteryHandler;

    private String mUARTResult;
    private TextView mUARTResultTextView;

    private boolean mIsUpSideDown;

    //private boolean mIsScrollRight;

    private boolean mIsTimeFormat24h;

    private Button mPrevButton;
    private Button mNextButton;
    private Button mGetSetButton;
    private TextView mButtonLabelTextView;


    private int     m_intNUSMsgCount = 0;

    private int mCurrentCmdIndex = 0;

    private GraphFragment mGraphFragment = null;

    private boolean mIsSetConnectionInterval = false;

//    Set mScannedGlaneDeviceList = new LinkedHashSet();
    List<String> mScannedGlaneDeviceArrayList = new ArrayList<String>();
    ArrayAdapter<String> mScannedGlaneDeviceListAdapter = null;

    enum COMMAND_LIST {
        SET_USER_DATA_ALARM,
        GET_USER_DATA_ALARM,
        SET_USER_DATA_PROFILE,
        GET_USER_DATA_PROFILE,
        SET_USER_DATA_GOAL,
        GET_USER_DATA_GOAL,
        SET_MPU_MODE,
        GET_MPU_MODE,
        GET_SPI_WHOAMI,
        GET_DEBUG_LOG,
        CLEAR_DEBUG_LOG,
//        SET_ACTIVITY_GOAL,
//        GET_ACTIVITY_GOAL,
//        SET_MAIN_GOAL,
//        GET_MAIN_GOAL,
//        SET_PRORUN_AUTO_DETECT_ON,
//        GET_PRORUN_AUTO_DETECT_ON,
        SET_APP_CONNECT_FINISH_ON,
        SET_APP_CONNECT_FINISH_OFF,
        GET_PRORUN_LOG_DATA,
        ACK_PRORUN_LOG,
        GET_FIRMWARE_ID,
//        SET_AGE,
//        GET_AGE,
//        SET_GENDER,
//        GET_GENDER,
//        SET_HAND_ARISE,
//        GET_HAND_ARISE,
//        SET_STEP_GOAL,
//        GET_STEP_GOAL,
        SEND_DUMMY_CMD,
        REQ_PROTOCOL_VER,
        GET_PROTOCOL_VER,
        SET_ISCALED,
        RESET_COLD_BOOT_COUNT,
        GET_COLD_BOOT_COUNT,
        SET_SECONDARY_DISPLAY_ASCII_STRING,
        SET_SECONDARY_DISPLAY_CLUB_HEAD_SPEED,
        SET_SECONDARY_DISPLAY_BITMAP,
//        SET_ALARM,
//        GET_ALARM,
//        SET_CLUB_HEAD_SPEED,
        SET_BOOTUP_MODE,
//        GET_VOLTAGE_LOG,
        SET_CONNECTION_INTERVAL,
        GET_CONNECTION__INTERVAL,
        GET_FINISH_CALIBRATION,
        GET_CALIBRATE_FACE_N,
        GET_GET_CALIBRATION,
        GET_PSTOR_VALUE,
        SET_AUTOWALK_INTERVAL,
        GET_AUTOWALK_INTERVAL,
        SET_BROWNOUT_INTERVAL,
        GET_BROWNOUT_INTERVAL,
        GET_FW_VERSION,
        FACTORY_RESET,
        DFU_MODE,
        GET_LOG_DATA,
        ACK_LOG_WITHOUT_TIMESTAMP,
        GET_SLEEP_LOG_DATA,
        ACK_SLEEP_LOG,
//        GET_BLE_ID,
        SHIPMENT_MODE,
//        SET_12H_24H_FORMAT,
//        GET_12H_24H_FORMAT,
//        SET_UNIT_OF_MEASURE,
//        GET_UNIT_OF_MEASURE,
//        SET_OLED_INTENSITY,
//        GET_OLED_INTENSITY,
//        SET_LOG_INTERVAL,
//        GET_LOG_INTERVAL,
        SET_POWER_MODE,
        GET_POWER_MODE,
//        SET_GOAL,
//        GET_GOAL,
//        SET_WEIGHT,
//        GET_WEIGHT,
//        SET_HEIGHT,
//        GET_HEIGHT,
        SET_DATETIME,
        GET_DATETIME,
//        SET_DEVICE_NAME,
//        GET_DEVICE_NAME,
        ACK_LOG,
        GET_STEP_COUNT,
        GET_TXPOWER,
        GET_LIBRARY_VERSION,
        GET_PSTORAGE_LOG_INFO,
        MAX_CMD_INDEX
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glance_main);

        Log.d(TAG, "onCreate()");

        mIsScanStarted = false;

        // Exit if BLE is not supported on the system
        if (isBLESupported() == false) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Exit if cannot get BluetoothAdapter
        mBluetoothAdapter = getBluetoothAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Cannot get Bluetooth Adapter");
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        mIsFoundDevice = false;
        mIsConnected = false;
        mIsSetConnectionInterval = false;
        mIsStartRSC = false;
        mIsStartMotionStream = false;
        mIsUpSideDown = true;
        mIsTimeFormat24h = true;

        mService = null;

        mLatestBLEDeviceAddress = null;
        mConnectedDeviceAddress = null;

        //mGetBatteryHandler = new Handler();

        initUI();
        service_init();

        initGraphFragment();
    }
    
    /* Activity Life Cycle */

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        //if ( mGetBatteryHandler != null )
        //    mGetBatteryHandler.removeCallbacks(getBatteryLevelRunnable);
        service_destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        //mService.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        enableBlutooth();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.glance_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initUI() {
        mLatestBLEDeviceAddressTextView = (TextView)findViewById(R.id.latestDeviceAddress);
        mLatestBLEDeviceAddressTextView.setText("");
        mLatestBLEDeviceRSSITextView = (TextView)findViewById(R.id.rssiText);
        mLatestBLEDeviceRSSITextView.setText("");
        mGlanceDeviceAddressSpinnerList = (Spinner)findViewById(R.id.glanceDeviceAddressList);
        mConnectStateTextView = (TextView)findViewById(R.id.textConnectState);
        mConnectStateTextView.setText("");
        mConnectButton = (Button)findViewById(R.id.butConnect);
        mConnectButton.setOnClickListener(mConnectionButtonListener);
        mConnectButton.setEnabled(false);
        mConnectButton.setText(getResources().getString(R.string.connect_string));
        mScanButton = (Button)findViewById(R.id.butScan);
        mScanButton.setOnClickListener(mScanListener);
        mReadRSCButton = (Button)findViewById(R.id.butGetRSC);
        mReadRSCButton.setText(getResources().getString(R.string.read_rsc));
        mReadRSCButton.setEnabled(false);
        mReadRSCButton.setOnClickListener(mRSCButtonListener);
        mReadMotionStreamButton = (Button)findViewById(R.id.butGetMotionStream);
//        mReadMotionStreamButton.setText(getResources().getString(R.string.str_start_motion_stream));
        mReadMotionStreamButton.setEnabled(false);
        mReadMotionStreamButton.setOnClickListener(mMotionStreamListener);
        mGetBatteryButton = (Button)findViewById(R.id.butGetBatteryLvl);
        mGetBatteryButton.setOnClickListener(mBatteryButtonListener);
        mGetBatteryButton.setEnabled(false);
        mRSCResultTextView = (TextView)findViewById(R.id.textRSC);
        mRSCResultTextView.setText("");
        mBatteryLevelTextView = (TextView)findViewById(R.id.textBatteryLevel);
        mBatteryLevelTextView.setText("");
        mUARTResultTextView = (TextView)findViewById(R.id.textUART);
        mUARTResultTextView.setMovementMethod(new ScrollingMovementMethod());
        mUARTResultTextView.setText("");
        mPrevButton = (Button)findViewById(R.id.butPrev);
        mPrevButton.setOnClickListener(mPrevButtonListener);
        mPrevButton.setEnabled(true);
        mNextButton = (Button)findViewById(R.id.butNext);
        mNextButton.setOnClickListener(mNextButtonListener);
        mNextButton.setEnabled(true);
        mGetSetButton = (Button)findViewById(R.id.butGetSet);
        mGetSetButton.setEnabled(false);
        mGetSetButton.setOnClickListener(getOnClickListenerByIndex(mCurrentCmdIndex));
        mButtonLabelTextView = (TextView) findViewById(R.id.textCommandButton);
        mButtonLabelTextView.setText(getButtonString(mCurrentCmdIndex));
//        mScannedGlaneDeviceArrayList.addAll(mScannedGlaneDeviceList);
        mScannedGlaneDeviceListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mScannedGlaneDeviceArrayList);
        mGlanceDeviceAddressSpinnerList.setAdapter(mScannedGlaneDeviceListAdapter);
    }
    
    /* Bluetooth */

    public void enableBlutooth() {
        if (!isBluetoothEnabled()) {
            /* mIsFoundDevice is set to false to indicate no scan happen after first bluetooth on */
            mIsFoundDevice = false;
            //updateUIbyStatus();

            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    public boolean isBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public BluetoothAdapter getBluetoothAdapter() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        return bluetoothManager.getAdapter();
    }

    /* BLE */

    public boolean isBLESupported() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        } else {
            return true;
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(GlanceProtocolService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(GlanceProtocolService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(GlanceProtocolService.ACTION_RSSI);
        intentFilter.addAction(GlanceProtocolService.ACTION_FOUND_DEVICE);
        intentFilter.addAction(GlanceProtocolService.ACTION_DEVICE_DOES_NOT_SUPPORT_UART);
        intentFilter.addAction(GlanceProtocolService.ACTION_SCANNING_DEVICE);
        intentFilter.addAction(GlanceProtocolService.ACTION_SCANNING_STOPPED);
        intentFilter.addAction(GlanceProtocolService.ACTION_GLANCE_CONNECT_COMPLETE);
        intentFilter.addAction(GlanceProtocolService.ACTION_GLANCE_RSC_DATA2);
        intentFilter.addAction(GlanceProtocolService.ACTION_GATT_CONNECTION_STATUS_133);

        // intent filter of getting the response of API
        intentFilter.addAction(GlanceProtocolService.GLANCE_FW_VER_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_BLE_ID_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_FACTORY_RESET_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SHIPMENT_MODE_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_TIME_FORMAT_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_TIME_FORMAT_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_UNIT_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_UNIT_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_SCREEN_BRIGHTNESS_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_SCREEN_BRIGHTNESS_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_LOG_INTERVAL_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_LOG_INTERVAL_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_POWER_MODE_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_POWER_MODE_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_CALORIES_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_CALORIES_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_WEIGHT_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_WEIGHT_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_HEIGHT_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_HEIGHT_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_DATETIME_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_DATETIME_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_LOGDATA_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_LOGDATA_PROGRESS_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_DEVICE_NAME_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_DEVICE_NAME_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_ACK_GET_LOG_DATA_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_COMMAND_SEND_TIMEOUT);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_STEP_COUNT_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_TXPOWER_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_PSTORAGE_LOG_INFO_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_SLEEP_LOGDATA_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_SLEEP_LOGDATA_PROGRESS_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_ACK_GET_SLEEP_LOG_DATA_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_AUTOWALK_RESPONSE);//This broadcast is for Internal Development use only
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_AUTOWALK_RESPONSE);//This broadcast is for Internal Development use only
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_BROWNOUT_RESPONSE);//This broadcast is for Internal Development use only
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_BROWNOUT_RESPONSE);//This broadcast is for Internal Development use only
//        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_VOLTAGE_LOG_RESPONSE);//This broadcast is for Internal Development use only
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_CONNECTION_INTERVAL_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_CONNECTION_INTERVAL_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_FINISH_CALIBRATION_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_CALIBRATE_FACE_N_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_CALIBRATION_DATA_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_PSTORAGE_DATA_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_BOOTUP_MODE_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_BATTERY_LEVEL_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_CLUB_HEAD_SPEED_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_SECONDARY_DISPLAY_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_ALARM_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_ALARM_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_COLD_BOOT_COUNT_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_RESET_COLD_BOOT_COUNT_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_ISCALED_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_PROTOCOL_VER_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SEND_DUMMY_CMD_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_DFU_MODE_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_PRORUN_LOGDATA_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_PRORUN_LOGDATA_PROGRESS_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_ACK_GET_PRORUN_LOG_DATA_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_APP_CONNECTION_FINISH_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_AGE_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_AGE_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_GENDER_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_GENDER_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_HAND_ARISE_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_HAND_ARISE_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_STEP_GOAL_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_STEP_GOAL_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_FIRMWARE_ID_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_PRORUN_AUTO_DETECT_ON_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_PRORUN_AUTO_DETECT_ON_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_ACTIVITY_GOAL_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_ACTIVITY_GOAL_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_MAIN_GOAL_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_MAIN_GOAL_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_DEBUG_LOGDATA_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_DEBUG_LOGDATA_PROGRESS_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_CLEAR_DEBUG_LOG_DATA_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_MPU_MODE_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_MPU_MODE_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_SPI_WHOAMI_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_USER_DATA_1_ALARM_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_USER_DATA_1_ALARM_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_USER_DATA_2_PROFILE_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_USER_DATA_2_PROFILE_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_GET_USER_DATA_3_GOAL_RESPONSE);
        intentFilter.addAction(GlanceProtocolService.GLANCE_SET_USER_DATA_3_GOAL_RESPONSE);
        return intentFilter;
    }

    /* Bluetooth service init & destroy */

    private void service_init() {
        Log.d(TAG, "service init");
        Intent bindIntent = new Intent(this, GlanceProtocolService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(mBluetoothServiceBroadcastReceiver, makeGattUpdateIntentFilter());
    }

    private void service_destroy() {
        Log.d(TAG, "service destroy");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBluetoothServiceBroadcastReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }

        unbindService(mServiceConnection);
        mService.stopSelf();
        mService = null;
    }

    /* Bluetooth Service */

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((GlanceProtocolService.LocalBinder) rawBinder).getService();
            //updateUIbyStatus();

            Log.d(TAG, "onServiceConnected mService= " + mService);

            mService.setOnUartReceivedListener(new GlanceProtocolService.OnUartReceivedListener() {
                                                   @Override
                                                   public void uartDataReceived(BluetoothDevice device, Object data, GlanceStatus.StreamType streamType) {
                                                       Message sendMsg = mHandler.obtainMessage(HANDLER_PROCESS_UART_DATA);
                                                       if (streamType == GlanceStatus.StreamType.RAW) {
                                                           GlanceMotionStreamRawData rawData = (GlanceMotionStreamRawData) data;
                                                           Bundle bundle = new Bundle();
                                                           bundle.putSerializable("UartRawData", rawData);
                                                           bundle.putSerializable("UartStreamType", streamType);
                                                           sendMsg.setData(bundle);
                                                           mHandler.sendMessage(sendMsg);
                                                       }
                                                   }
                                               }
            );
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };

    // Scan button 
    private View.OnClickListener mScanListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.d(TAG, "mScanListener::onClick()");

            if (mService != null) {
                if (!mIsScanStarted) {
                    Log.d(TAG, "mScanListener::onClick() 1");
                    mScanButton.setText(getResources().getString(R.string.stop_string));
                    mLatestBLEDeviceAddress = null;
                    mLatestBLEDeviceAddressTextView.setText("");
                    mLatestBLEDeviceRSSITextView.setText("");
                    mService.startScanningPeripheral(true);
                    mIsScanStarted = true;

                    mConnectButton.setEnabled(false);
                    mConnectStateTextView.setText("");
                } else {
                    Log.d(TAG, "mScanListener::onClick() 2");
                    mScanButton.setText(getResources().getString(R.string.scan_string));
                    mService.stopScanningPeripheral();
                    mIsScanStarted = false;
                }
            } else {
                Log.d(TAG, "mScanListener::onClick() 3");
            }
        }
    };

    // Connect button
    private View.OnClickListener mConnectionButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            String [] splitAddress = mGlanceDeviceAddressSpinnerList.getSelectedItem().toString().split(",");
            if ((!mIsConnected) && (splitAddress != null) && (splitAddress.length == 3)) {
                if (mService != null) {
                    mLatestBLEDeviceAddress = splitAddress[1];
                    if (mService.connect(mLatestBLEDeviceAddress)) {
                        mConnectButton.setEnabled(false);
                        mConnectStateTextView.setText(getResources().getString(R.string.connect_state_connecting));

                        mScanButton.setEnabled(false);

                        // TODO: Create a thread to lookup connection timeout
                    }
                }
            } else if (mIsConnected) {
                if (mService != null) {
                    mService.disconnect();
                    mConnectButton.setEnabled(false);
                    mConnectStateTextView.setText(getResources().getString(R.string.connect_state_disconnecting));
                }
            }
        }
    };

    // Get RSC data
    private View.OnClickListener mRSCButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                int ret = mService.gettingRSCData(mConnectedDeviceAddress, !mIsStartRSC);
                printStatus(ret, !mIsStartRSC ? R.string.read_rsc : R.string.stop_rsc);
                if (ret == com.cwb.bleframework.GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_NONE) {
                    mIsStartRSC = !mIsStartRSC;
                    if (mIsStartRSC) {
                        mReadRSCButton.setText(getResources().getString(R.string.stop_rsc));
                        // Disable the buttons of getting battery level and other settings
                        mGetBatteryButton.setEnabled(false);
                        mGetSetButton.setEnabled(false);
                        mReadMotionStreamButton.setEnabled(false);
                    } else {
                        mReadRSCButton.setText(getResources().getString(R.string.read_rsc));
                        // Enable the buttons of getting battery level and other settings
                        mGetBatteryButton.setEnabled(true);
                        mGetSetButton.setEnabled(true);
                        mReadMotionStreamButton.setEnabled(true);
                    }
                }
            }
        }
    };

    // Get Battery Level
    private View.OnClickListener mBatteryButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                int ret = mService.readBatteryLevel(mConnectedDeviceAddress);
                printStatus(ret, R.string.get_battery_lvl);
            }
        }
    };

    // Send Set time command
    private View.OnClickListener mSendUARTButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";

                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set Time (hh:mm:ss)");

                final LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.setdatetime_layout, null);
                editDialog.setView(layout);
                final EditText editText1 = (EditText)layout.findViewById(R.id.editInput1);
                final EditText editText2 = (EditText)layout.findViewById(R.id.editInput2);
                editText1.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText2.setInputType(InputType.TYPE_CLASS_NUMBER);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult1="", textResult2="";
                        if (editText1 != null) {
                            textResult1 = editText1.getText().toString();
                            Log.d(TAG, "mSendUARTButtonListener::onClick1, " + textResult1);
                        }

                        if (editText2 != null) {
                            textResult2 = editText2.getText().toString();
                            Log.d(TAG, "mSendUARTButtonListener::onClick2, " + textResult2);
                        }

                        if ((textResult1.length() > 0) && (textResult2.length() > 0)) {
                            ///////////mService.setTime( mConnectedDeviceAddress, Integer.parseInt(textResult1), Integer.parseInt(textResult2) );
                        }
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Change time format
    private View.OnClickListener mTimeFormatListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                mIsTimeFormat24h = !mIsTimeFormat24h;
                int ret = mService.setTimeFormat(mConnectedDeviceAddress, mIsTimeFormat24h);
                printStatus(ret, mIsTimeFormat24h ? R.string.str_24h : R.string.str_12h);

//                if (mIsTimeFormat24h) {
//                    mChangeTimeFormatButton.setText(getResources().getString(R.string.str_24h));
//                } else {
//                    mChangeTimeFormatButton.setText(getResources().getString(R.string.str_12h));
//                }
            }
        }
    };

    // Get time format
    private View.OnClickListener mGetTimeFormatListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.getTimeFormat(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get24H_Format);
            }
        }
    };

    // Get firmware versions
    private View.OnClickListener mGetVersionListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.getFirmwareVersions(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_fw_version);
            }
        }
    };

    // Get BLE MAC layer address
    private View.OnClickListener mGetBLEIDListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.getBLEID(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_ble_id);
            }
        }
    };

    // Factory Reset
    private View.OnClickListener mFactoryResetListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.executeFactoryReset(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_factory_reset);
            }
        }
    };

    // Shipment mode
    private View.OnClickListener mShipmentModeListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.executeShipmentMode(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_shipment_mode);
            }
        }
    };

    // DFU Mode
    private View.OnClickListener mDFUModeListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.executeDFUMode(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_dfu_mode);
            }
        }
    };

    // Enable/Disable motion streaming
    private View.OnClickListener mMotionStreamListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                addGraphFragment();
            }
        }
    };

    // Get data button
    private View.OnClickListener mGetLOGDataListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                Log.e(TAG, "onButtonGetData");
                int ret = mService.getLOGData(mConnectedDeviceAddress, 0);
                printStatus(ret, R.string.str_get_log_data);
            }
        }
    };

    // Set date button listener
    private View.OnClickListener mSetDateTimeListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";

                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set Date dd/mm/yy hh:mm:ss");

                final LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.setdatetime_layout, null);
                editDialog.setView(layout);
                final EditText editText1 = (EditText)layout.findViewById(R.id.editInput1);
                final EditText editText2 = (EditText)layout.findViewById(R.id.editInput2);
                final EditText editText3 = (EditText)layout.findViewById(R.id.editInput3);
                final EditText editText4 = (EditText)layout.findViewById(R.id.editInput4);
                final EditText editText5 = (EditText)layout.findViewById(R.id.editInput5);
                final EditText editText6 = (EditText)layout.findViewById(R.id.editInput6);
                editText1.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText2.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText3.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText4.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText5.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText6.setInputType(InputType.TYPE_CLASS_NUMBER);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String day="", month="", year="", hour="", minute="", seconds="";
                        if (editText1 != null) {
                            day = editText1.getText().toString();
                            Log.d(TAG, "mSendUARTButtonListener::onClick1, "+ day);
                        }

                        if (editText2 != null) {
                            month = editText2.getText().toString();
                            Log.d(TAG, "mSendUARTButtonListener::onClick2, "+ month);
                        }

                        if (editText3 != null) {
                            year = editText3.getText().toString();
                            Log.d(TAG, "mSendUARTButtonListener::onClick3, "+ year);
                        }
                        if (editText4 != null) {
                            hour = editText4.getText().toString();
                            Log.d(TAG, "mSendUARTButtonListener::onClick4, "+ hour);
                        }
                        if (editText5 != null) {
                            minute = editText5.getText().toString();
                            Log.d(TAG, "mSendUARTButtonListener::onClick5, "+ minute);
                        }
                        if (editText6 != null) {
                            seconds = editText6.getText().toString();
                            Log.d(TAG, "mSendUARTButtonListener::onClick6, "+ seconds);
                        }

                        if ((day.length() > 0) && (month.length() > 0) && (year.length() > 0)  && (hour.length() > 0)  && (minute.length() > 0)  && (seconds.length() > 0)) {
                            int ret = mService.setDateTime(mConnectedDeviceAddress, Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(minute) ,Integer.parseInt(seconds));
                            printStatus(ret, R.string.str_set_datetime);
                        }
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get DateTime
    private View.OnClickListener mGetDateTimeListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.getDateTime(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_datetime);
            }
        }
    };

    // Set Unit Of Measure
    private View.OnClickListener mSetUnitOfMeasureListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";

                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set Imperial (0-1)");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("0");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        int ret = mService.setUnitOfMeasure(mConnectedDeviceAddress, Integer.parseInt(textResult));
                        printStatus(ret, R.string.str_set_unit_of_measure);
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get Unit Of Measure
    private View.OnClickListener mGetUnitOfMeasureListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.getUnitOfMeasure(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_unit_of_measure);
            }
        }
    };

    // Set OLED Intensity
    private View.OnClickListener mSetOLEDIntensityListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";

                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set OLED intensity (1-3)");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("1");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mChangeScrollSpeedListener::onClick, " + textResult);
                        int ret = mService.setOLEDIntensity(mConnectedDeviceAddress, Integer.parseInt(textResult));
                        printStatus(ret, R.string.str_set_oled_intensity);
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get OLED Intensity
    private View.OnClickListener mGetOLEDIntensityListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.getOLEDIntensity(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_oled_intensity);
            }
        }
    };

//    // Set LOG Interval
//    private View.OnClickListener mSetLogIntervalListener = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            // TODO Auto-generated method stub
//            if (mService != null) {
//                // Must Stop Getting RSC data first. Otherwise, device will slow response
//                mUARTResult = "";
//                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
//                editDialog.setTitle("Set LOG Interval (0-15) mapping");
//
//                final EditText editText = new EditText(glanceMainActivity.this);
//                editText.setText("2");
//                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
//                editDialog.setView(editText);
//
//                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
//                    // do something when the button is clicked
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        String textResult = editText.getText().toString();
//                        Log.d(TAG, "mSetLogIntervalListener::onClick, " + textResult);
//                        int ret = mService.setLogInterval(mConnectedDeviceAddress, Integer.parseInt(textResult));
//                        printStatus(ret, R.string.str_set_log_interval);
//                    }
//                });
//
//                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    // do something when the button is clicked
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        //...
//                    }
//                });
//                editDialog.show();
//            }
//        }
//    };
//
//    // Get LOG Interval
//    private View.OnClickListener mGetLogIntervalListener = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            // TODO Auto-generated method stub
//            if (mService != null) {
//                // Must Stop Getting RSC data first. Otherwise, device will slow response
//                mUARTResult = "";
//                int ret = mService.getLogInterval(mConnectedDeviceAddress);
//                printStatus(ret, R.string.str_get_log_data);
//            }
//        }
//    };

    // Set LOG Interval
    private View.OnClickListener mSetPowerModeListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set Power Mode (0-255) mapping");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("2");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mSetPowerModeListener::onClick, " + textResult);
                        int ret = mService.setPowerMode(mConnectedDeviceAddress, Integer.parseInt(textResult));
                        printStatus(ret, R.string.str_set_power_mode);
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get LOG Interval
    private View.OnClickListener  mGetPowerModeListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.getPowerMode(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_power_mode);
            }
        }
    };

    // Set Goal By Calories
    private View.OnClickListener mSetGoalListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set Goal By Calories (1-65535)");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("1000");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mChangeScrollSpeedListener::onClick, " + textResult);
                        int ret = mService.setGoalByCalories(mConnectedDeviceAddress, Integer.parseInt(textResult));
                        printStatus(ret, R.string.str_set_goal);
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get Goal By Calories
    private View.OnClickListener mGetGoalListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.getGoalByCalories(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_goal);
            }
        }
    };

    // Set Height
    private View.OnClickListener mSetHeightListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set Height (1-65536)");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("1");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mChangeScrollSpeedListener::onClick, " + textResult);
                        int ret = mService.setHeight(mConnectedDeviceAddress, Integer.parseInt(textResult));
                        printStatus(ret, R.string.str_set_height);
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get Height
    private View.OnClickListener mGetHeightListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.getHeight(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_height);
            }
        }
    };

    // Set Weight
    private View.OnClickListener mSetWeightListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set Weight (1-65565)");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("1");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mChangeScrollSpeedListener::onClick, " + textResult);
                        int ret = mService.setWeight(mConnectedDeviceAddress, Integer.parseInt(textResult));
                        printStatus(ret, R.string.str_set_weight);
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get Weight
    private View.OnClickListener mGetWeightListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.getWeight(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_weight);
            }
        }
    };

    // Set nickname
    private View.OnClickListener mSetDeviceNameListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";

                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set DeviceName");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("");
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "showSingleEditTextDialog::onClick, " + textResult);
                        int ret = mService.setDeviceName(mConnectedDeviceAddress, textResult);
                        printStatus(ret, R.string.str_set_device_name);
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get Device Name
    private View.OnClickListener mGetDeviceNameListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.getDeviceName(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_device_name);
            }
        }
    };
    // Ack Get Log Data button listener
    private View.OnClickListener mAckGetLogDataListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";

                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set Date dd/mm/yy hh:mm");

                final LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.setdatetime_layout, null);
                editDialog.setView(layout);
                final EditText editText1 = (EditText)layout.findViewById(R.id.editInput1);
                final EditText editText2 = (EditText)layout.findViewById(R.id.editInput2);
                final EditText editText3 = (EditText)layout.findViewById(R.id.editInput3);
                final EditText editText4 = (EditText)layout.findViewById(R.id.editInput4);
                final EditText editText5 = (EditText)layout.findViewById(R.id.editInput5);
                final EditText editText6 = (EditText)layout.findViewById(R.id.editInput6);
                editText6.setVisibility(View.GONE);
                editText1.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText2.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText3.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText4.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText5.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText6.setInputType(InputType.TYPE_CLASS_NUMBER);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String day = "", month = "", year = "", hour = "", minute = "", seconds = "";
                        if (editText1 != null) {
                            day = editText1.getText().toString();
                            Log.d(TAG, "mSendUARTButtonListener::onClick1, " + day);
                        }

                        if (editText2 != null) {
                            month = editText2.getText().toString();
                            Log.d(TAG, "mSendUARTButtonListener::onClick2, " + month);
                        }

                        if (editText3 != null) {
                            year = editText3.getText().toString();
                            Log.d(TAG, "mSendUARTButtonListener::onClick3, " + year);
                        }
                        if (editText4 != null) {
                            hour = editText4.getText().toString();
                            Log.d(TAG, "mSendUARTButtonListener::onClick4, " + hour);
                        }
                        if (editText5 != null) {
                            minute = editText5.getText().toString();
                            Log.d(TAG, "mSendUARTButtonListener::onClick5, " + minute);
                        }

                        if ((day.length() > 0) && (month.length() > 0) && (year.length() > 0) && (hour.length() > 0) && (minute.length() > 0)) {
                            int ret = mService.ackGetLogData(mConnectedDeviceAddress, Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(minute));
                            printStatus(ret, R.string.str_ack_log);
                        }
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get Step Count
    private View.OnClickListener mGetStepCountListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.getStepCount(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_step_count);
            }
        }
    };

    // Get Tx Power
    private View.OnClickListener  mGetTxPowerListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.getTxPower(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_txpower);
            }
        }
    };

    // Get Library Version
    private View.OnClickListener  mGetLibraryVersionListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                int ver = mService.getProtocolLibraryVersion();
                updateUartText("Library Version: " + Integer.toString(ver));
            }
        }
    };

    // Ack GET LOG without timestamp
    private View.OnClickListener  mAckGetLogDataWithoutTimestampListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                //Function of ackGetLogData
                //Called after GlanceProtocolService.GLANCE_GET_LOGDATA_RESPONSE success
                //If Call Get Log Data after, Device will response Empty Log Data
                //If already call, call second time will retur error: GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_NO_ACK_TIMESTAMP
                int ret = mService.ackGetLogData(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_ack_log_without_timestamp);
            }
        }
    };

    // Get Pstorage Log Info
    private View.OnClickListener  mAckGetPstorageLogInfoListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                //For Firmware Developer Debug only
                int ret = mService.getPstorageLogInfo(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_pstorage_log_info);
            }
        }
    };

    // Get data button
    private View.OnClickListener mGetSleepLogDataListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.getSleepLogData(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_sleep_log_data);
            }
        }
    };

    // Ack GET LOG without timestamp
    private View.OnClickListener  mAckGetSleepLogDataListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                //Function of ackGetLogData
                //Called after GlanceProtocolService.GLANCE_GET_SLEEP_LOGDATA_RESPONSE success
                //If Call Get Sleep Log Data after call ackGetSleepLogData success,, Device will response Empty Log Data
                //If already call, call second time will return error: GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_NO_ACK_TIMESTAMP
                int ret = mService.ackGetSleepLogData(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_ack_sleep_log);
            }
        }
    };

    // Set AutoWalk
    private View.OnClickListener  mSetAutoWalkListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set AutoWalk: 0 is Disable, 1 is Enable");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("1");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mSetAutoWalkListener::onClick, " + textResult);
                        int ret = mService.setAutoWalk(mConnectedDeviceAddress, Integer.parseInt(textResult));
                        printStatus(ret, R.string.str_set_autowalk_interval);
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get AutoWalk
    private View.OnClickListener  mGetAutoWalkListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                int ret = mService.getAutoWalk(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_autowalk_interval);
            }
        }
    };

    // Set BrownOut
    private View.OnClickListener  mSetBrownOutListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set BrownOut: 0 is Disable, 1 is Enable");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("1");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mSetBrownOutListener::onClick, " + textResult);
                        int ret = mService.setBrownOut(mConnectedDeviceAddress, Integer.parseInt(textResult));
                        printStatus(ret, R.string.str_set_brownout_interval);
                    }
                });
                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get BrownOut
    private View.OnClickListener  mGetBrownOutListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                int ret = mService.getBrownOut(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_brownout_interval);
            }
        }
    };

//    // Get Voltage Log
//    private View.OnClickListener mGetVoltageLogListener = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            // TODO Auto-generated method stub
//            if (mService != null) {
//                // Must Stop Getting RSC data first. Otherwise, device will slow response
//                mUARTResult = "";
//                int ret = mService.getVoltageLog(mConnectedDeviceAddress);
//                printStatus(ret, R.string.str_get_voltage_log);
//            }
//        }
//    };

    // Set Connection Interval
    private View.OnClickListener  mSetConnectionIntervalListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set AutoWalk: 1 is Low , 2 is High");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("2");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mSetAutoWalkListener::onClick, " + textResult);
                        int ret = mService.setConnectionInterval(mConnectedDeviceAddress, Integer.parseInt(textResult));
                        printStatus(ret, R.string.str_set_connection_interval);
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get Connection Interval
    private View.OnClickListener  mGetConnectionIntervalListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                int ret = mService.getConnectionInterval(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_connection_interval);
            }
        }
    };

    private View.OnClickListener  mFinishCalibrationListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                int ret = mService.executeFinishCalibration(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_finish_calibration);
            }
        }
    };

    private View.OnClickListener  mCalibrateFaceNListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Operate Calibration for Face Number(1-255)");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("1");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mCalibrateFaceNListener::onClick, " + textResult);
                        int ret = mService.executeCalibrateFaceN(mConnectedDeviceAddress, Integer.parseInt(textResult));
                        printStatus(ret, R.string.str_calibrate_face_n);
                    }
                });
                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    private View.OnClickListener  mGetCalibrationListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                int ret = mService.getCalibrationData(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_calibration);
            }
        }
    };

    private View.OnClickListener  mGetPstorValueListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Get Pstor Value: Page No(0-31), Offset(0-63)");

                final LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.setdatetime_layout, null);
                editDialog.setView(layout);
                final EditText editText1 = (EditText)layout.findViewById(R.id.editInput1);
                final EditText editText2 = (EditText)layout.findViewById(R.id.editInput2);
                final EditText editText3 = (EditText)layout.findViewById(R.id.editInput3);
                final EditText editText4 = (EditText)layout.findViewById(R.id.editInput4);
                final EditText editText5 = (EditText)layout.findViewById(R.id.editInput5);
                final EditText editText6 = (EditText)layout.findViewById(R.id.editInput6);
                editText3.setVisibility(View.GONE);
                editText4.setVisibility(View.GONE);
                editText5.setVisibility(View.GONE);
                editText6.setVisibility(View.GONE);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String pageNo="", offset="";
                        if (editText1 != null) {
                            pageNo = editText1.getText().toString();
                            Log.d(TAG, "mSendUARTButtonListener::onClick1, "+ pageNo);
                        }

                        if (editText2 != null) {
                            offset = editText2.getText().toString();
                            Log.d(TAG, "mSendUARTButtonListener::onClick2, "+ offset);
                        }
                        if ((pageNo.length() > 0) && (offset.length() > 0)) {
                            int ret = mService.GetPstorValue(mConnectedDeviceAddress, Integer.parseInt(pageNo), Integer.parseInt(offset));
                            printStatus(ret, R.string.str_get_pstor_value);
                        }
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Set Bootup Mode
    private View.OnClickListener  mSetBootupModeListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set Bootup Mode: -1 is Test Mode , 0 is Radio Mode, 1 is Normal Mode");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("1");
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mSetBootupModeListener::onClick, " + textResult);
                        int ret = mService.setBootupMode(mConnectedDeviceAddress, Integer.parseInt(textResult));
                        printStatus(ret, R.string.str_set_connection_interval);
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Set Club Head Speed
    private View.OnClickListener  mSetClubHeadSpeedListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set Speed 0-999\r\nUnit: 0=mph, 1=km/h, 2=m/s");

                final EditText editText1 = new EditText(glanceMainActivity.this);
                editText1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                final EditText editText2 = new EditText(glanceMainActivity.this);
                editText2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                editText1.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText1.setText("100");
                editText2.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText2.setText("0");

                LinearLayout layout = new LinearLayout(glanceMainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(editText1);
                layout.addView(editText2);
                editDialog.setView(layout);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult1="", textResult2="";
                        if (editText1 != null) {
                            textResult1 = editText1.getText().toString();
                            Log.d(TAG, "mSetClubHeadSpeedListener::onClick1, " + textResult1);
                        }

                        if (editText2 != null) {
                            textResult2 = editText2.getText().toString();
                            Log.d(TAG, "mSetClubHeadSpeedListener::onClick2, " + textResult2);
                        }

                        if ((textResult1.length() > 0) && (textResult2.length() > 0)) {
                            int ret = mService.setClubHeadSpeed(mConnectedDeviceAddress, Integer.parseInt(textResult1), Integer.parseInt(textResult2));
                            printStatus(ret, R.string.str_set_club_head_speed);
                        }
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Set Secondary Display Club Head Speed
    private View.OnClickListener  mSetSecondaryDisplayClubHeadSpeedListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Unit: 1=mph, 2=km/h, 3=m/s\r\nSet Speed 0.0-999.9");

                final EditText editText1 = new EditText(glanceMainActivity.this);
                editText1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                final EditText editText2 = new EditText(glanceMainActivity.this);
                editText2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                editText1.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText1.setText("1");
                editText2.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText2.setText("99.9");

                LinearLayout layout = new LinearLayout(glanceMainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(editText1);
                layout.addView(editText2);
                editDialog.setView(layout);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult1="", textResult2="";
                        if (editText1 != null) {
                            textResult1 = editText1.getText().toString();
                            Log.d(TAG, "mSetClubHeadSpeedListener::onClick1, " + textResult1);
                        }

                        if (editText2 != null) {
                            textResult2 = editText2.getText().toString();
                            Log.d(TAG, "mSetClubHeadSpeedListener::onClick2, " + textResult2);
                        }

                        if ((textResult1.length() > 0) && (textResult2.length() > 0)) {
                            int ret = mService.setSecondoryDisplay(mConnectedDeviceAddress, Integer.parseInt(textResult1), Float.parseFloat(textResult2));
                            printStatus(ret, R.string.str_set_club_head_speed);
                        }
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Set Secondary Display Club Head Speed
    private View.OnClickListener  mSetSecondaryDisplayBitmapListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.setSecondoryDisplay(mConnectedDeviceAddress, 0x05, null);
                printStatus(ret, R.string.str_set_secondary_display_bitmap);
            }
        }
    };

    // Set Secondary Display Ascii String
    private View.OnClickListener  mSetSecondaryDisplayAsciiStringListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Unit: 0=6x8 font, 4=8x16 font\r\nAscii String");

                final EditText editText1 = new EditText(glanceMainActivity.this);
                editText1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                final EditText editText2 = new EditText(glanceMainActivity.this);
                editText2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                editText1.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText1.setText("0");
                editText2.setText("Hello");

                LinearLayout layout = new LinearLayout(glanceMainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(editText1);
                layout.addView(editText2);
                editDialog.setView(layout);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult1="", textResult2="";
                        byte [] textByte = null;
                        if (editText1 != null) {
                            textResult1 = editText1.getText().toString();
                            Log.d(TAG, "mSetSecondaryDisplayAsciiStringListener::onClick1, " + textResult1);
                        }

                        if (editText2 != null) {
                            textResult2 = editText2.getText().toString();
                            Log.d(TAG, "mSetSecondaryDisplayAsciiStringListener::onClick2, " + textResult2);
                            try {
                                if (textResult2.length() > 17)
                                {
                                    textResult2 = textResult2.substring(0, 16);
                                }
                                byte[] stringBytes = textResult2.getBytes("ASCII");
                                textByte=new byte[stringBytes.length+1];
                                Arrays.fill(textByte, (byte) 0);
                                System.arraycopy(stringBytes, 0, textByte, 0, stringBytes.length);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                        if ((textResult1.length() > 0) && (textResult2.length() > 0)) {
                            int ret = mService.setSecondoryDisplay(mConnectedDeviceAddress, Integer.parseInt(textResult1), textByte);
                            printStatus(ret, R.string.str_set_club_head_speed);
                        }
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get Alarm
    private View.OnClickListener  mGetAlarmListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set alarm index (0-3)");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("0");
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mGetAlarmListener::onClick, " + textResult);
                        int index = Integer.parseInt(textResult);
                        if ( index >= 0 && index <= 3 ) {
                            int ret = mService.getAlarm(mConnectedDeviceAddress, index);
                            printStatus(ret, R.string.str_get_alarm);
                        }
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Set Alarm
    private View.OnClickListener  mSetAlarmListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("HH:MM:SS, 0=Disable 1=Enable, index=0");

                final EditText editText1 = new EditText(glanceMainActivity.this);
                editText1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                final EditText editText2 = new EditText(glanceMainActivity.this);
                editText2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                final EditText editText3 = new EditText(glanceMainActivity.this);
                editText3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                final EditText editText4 = new EditText(glanceMainActivity.this);
                editText4.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                final EditText editText5 = new EditText(glanceMainActivity.this);
                editText5.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                editText1.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText1.setText("23");
                editText2.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText2.setText("59");
                editText3.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText3.setText("59");
                editText4.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText4.setText("1");
                editText5.setInputType(InputType.TYPE_CLASS_NUMBER);
                //editText5.setEnabled(false);
                editText5.setText("0");

                LinearLayout layout = new LinearLayout(glanceMainActivity.this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layout.addView(editText1);
                layout.addView(editText2);
                layout.addView(editText3);
                layout.addView(editText4);
                layout.addView(editText5);
                editDialog.setView(layout);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult1="", textResult2="", textResult3="", textResult4="", textResult5="";
                        if (editText1 != null) {
                            textResult1 = editText1.getText().toString();
                            Log.d(TAG, "mSetAlarmListener::onClick1, " + textResult1);
                        }

                        if (editText2 != null) {
                            textResult2 = editText2.getText().toString();
                            Log.d(TAG, "mSetAlarmListener::onClick2, " + textResult2);
                        }

                        if (editText3 != null) {
                            textResult3 = editText3.getText().toString();
                            Log.d(TAG, "mSetAlarmListener::onClick3, " + textResult3);
                        }

                        if (editText4 != null) {
                            textResult4 = editText4.getText().toString();
                            Log.d(TAG, "mSetAlarmListener::onClick4, " + textResult4);
                        }

                        if (editText5 != null) {
                            textResult5 = editText5.getText().toString();
                            Log.d(TAG, "mSetAlarmListener::onClick5, " + textResult5);
                        }

                        if ((textResult1.length() > 0) && (textResult2.length() > 0)) {
                            int ret = mService.setAlarm(mConnectedDeviceAddress, Integer.parseInt(textResult1), Integer.parseInt(textResult2), Integer.parseInt(textResult3), Integer.parseInt(textResult4), Integer.parseInt(textResult5));
                            printStatus(ret, R.string.str_set_club_head_speed);
                        }
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get Cold Boot Count
    private View.OnClickListener  mGetColdBootCountListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                int ret = mService.getColdBootCount(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_cold_boot_count);
            }
        }
    };

    // Reset Cold Boot Count
    private View.OnClickListener  mResetColdBootCountListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                int ret = mService.resetColdBootCount(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_reset_cold_boot_count);
            }
        }
    };

    // Set IsCaled
    private View.OnClickListener  mSetIsCaledListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set isCaled: 1 set true, 0 or other values set to false");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("1");
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mSetIsCaledListener::onClick, " + textResult);
                        int setting = Integer.parseInt(textResult);
                        boolean isCaled = false;
                        if ( setting == 1 )
                            isCaled = true;
                        int ret = mService.setIsCaled(mConnectedDeviceAddress, isCaled);
                        printStatus(ret, R.string.str_set_iscaled);
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get Protocol Version
    private View.OnClickListener  mGetProtocolVersionListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                int ret = mService.getCurrentWorkingProtocolVersion(mConnectedDeviceAddress);
                updateUartText("Send \"" + getResources().getString(R.string.str_get_protocol_version) + "\" Protocol Version " + ret);
            }
        }
    };

    // Request Protocol Version
    private View.OnClickListener  mRequestProtocolVersionListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                int ret = mService.requestProtocolVersion(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_protocol_version);
            }
        }
    };

    // Send dummy command
    private View.OnClickListener  mSendDummyCommandListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                int ret = mService.sendDummyCommand(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_send_dummy_command);
            }
        }
    };


    // Get Pro Run data button
    private View.OnClickListener mGetProRunLogDataListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                mUARTResult = "";
                int ret = mService.getProRunLogData(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_prorun_log_data);
            }
        }
    };

    // Ack GET Pro Run LOG
    private View.OnClickListener  mAckGetProRunLogDataListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                //Function of ackGetLogData
                //Called after GlanceProtocolService.GLANCE_GET_PRORUN_LOGDATA_RESPONSE success
                //If Call Get Sleep Log Data after call ackGetSleepLogData success,, Device will response Empty Log Data
                //If already call, call second time will return error: GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_NO_ACK_TIMESTAMP
                int ret = mService.ackGetProRunLogData(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_ack_prorun_log);
            }
        }
    };

    //App Connection Finish
    private View.OnClickListener mSetAppConnectFinsihOnListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                mUARTResult = "";
                int ret = mService.setAppConnectFinsih(mConnectedDeviceAddress, 1);
                printStatus(ret, R.string.str_app_connect_finish_on);
            }
        }
    };

    //App Connection Finish
    private View.OnClickListener mSetAppConnectFinsihOffListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                mUARTResult = "";
                int ret = mService.setAppConnectFinsih(mConnectedDeviceAddress, 0);
                printStatus(ret, R.string.str_app_connect_finish_off);
            }
        }
    };

    // Set Age
    private View.OnClickListener mSetAgeListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set Age (5-99)");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("25");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mSetAgeListener::onClick, " + textResult);
                        int ret = mService.setAge(mConnectedDeviceAddress, Integer.parseInt(textResult));
                        printStatus(ret, R.string.str_set_age);
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get Age
    private View.OnClickListener mGetAgeListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                int ret = mService.getAge(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_age);
            }
        }
    };

    // Set Gender
    private View.OnClickListener mSetGenderListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set Gender: 0 is Female, 1 is Male");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("1");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mSetGenderListener::onClick, " + textResult);
                        int ret = mService.setGender(mConnectedDeviceAddress, Integer.parseInt(textResult));
                        printStatus(ret, R.string.str_set_gender);
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get AutoWalk
    private View.OnClickListener  mGetGenderListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                int ret = mService.getGender(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_gender);
            }
        }
    };

    // Set Hand Arise
    private View.OnClickListener  mSetHandAriseListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set Hand Arise: 0 is Disable, 1 is Enable");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("1");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mSetHandAriseListener::onClick, " + textResult);
                        int ret = mService.setHandArise(mConnectedDeviceAddress, Integer.parseInt(textResult));
                        printStatus(ret, R.string.str_set_hand_arise);
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get AutoWalk
    private View.OnClickListener  mGetHandAriseListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                int ret = mService.getHandArise(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_hand_arise);
            }
        }
    };

    // Set Step Goal
    private View.OnClickListener mSetStepGoalListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set Step Goal (1000-65535)");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("8000");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mSetStepGoalListener::onClick, " + textResult);
                        int ret = mService.setStepGoal(mConnectedDeviceAddress, Integer.parseInt(textResult));
                        printStatus(ret, R.string.str_set_step_goal);
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get Step Goal
    private View.OnClickListener mGetStepGoalListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                mUARTResult = "";
                int ret = mService.getStepGoal(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_step_goal);
            }
        }
    };

    // Get Firmware Id
    private View.OnClickListener mGetFirmwareIdListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                mUARTResult = "";
                int ret = mService.getFirmwareId(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_firmware_id);
            }
        }
    };

    // Set Age
    private View.OnClickListener mSetProRunAutoDetectOnListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set Pro Run Auto Detect: 0 is Disable, 1 is Enable");

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("1");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mSetProRunAutoDetectOnListener::onClick, " + textResult);
                        int ret = mService.setProRunAutoDetectOn(mConnectedDeviceAddress, Integer.parseInt(textResult));
                        printStatus(ret, R.string.str_get_prorun_auto_detect_on);
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get Pro Run Auto Detect On Listener
    private View.OnClickListener mGetProRunAutoDetectOnListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                mUARTResult = "";
                int ret = mService.getProRunAutoDetectOn(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_prorun_auto_detect_on);
            }
        }
    };

    // Get Activity Goal
    private View.OnClickListener  mGetActivityGoalListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                TextView textView = new TextView(glanceMainActivity.this);
                textView.setText("Goal Type:\n" +
                        "0 - Calories Goal\n" +
                        "1 - Step Goal\n" +
                        "2 - Walk Distance Goal\n" +
                        "3 - Walk Duration Goal\n" +
                        "4 - Run Distance Goal\n" +
                        "5 - Run Duration Goal");
                editDialog.setCustomTitle(textView);

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("0");
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mGetActivityGoalListener::onClick, " + textResult);
                        int index = Integer.parseInt(textResult);
                        if ( index >= 0 && index <= 5 ) {
                            int ret = mService.getActivityGoal(mConnectedDeviceAddress, index);
                            printStatus(ret, R.string.str_get_activity_goal);
                        }
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Set Activity Goal
    private View.OnClickListener  mSetActivityGoalListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                TextView textView = new TextView(glanceMainActivity.this);
                textView.setText("Goal Type:\t\t, value\n" +
                        "0 - Calories Goal\n" +
                        "1 - Step Goal\n" +
                        "2 - Walk Distance Goal\n" +
                        "3 - Walk Duration Goal\n" +
                        "4 - Run Distance Goal\n" +
                        "5 - Run Duration Goale");
                editDialog.setCustomTitle(textView);
                editDialog.setTitle("HH:MM:SS, 0=Disable 1=Enable, index=0");

                final EditText editText1 = new EditText(glanceMainActivity.this);
                editText1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                final EditText editText2 = new EditText(glanceMainActivity.this);
                editText2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                editText1.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText1.setText("0");
                editText2.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText2.setText("3500");

                LinearLayout layout = new LinearLayout(glanceMainActivity.this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layout.addView(editText1);
                layout.addView(editText2);
                editDialog.setView(layout);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult1="", textResult2="", textResult3="", textResult4="", textResult5="";
                        if (editText1 != null) {
                            textResult1 = editText1.getText().toString();
                            Log.d(TAG, "mSetAlarmListener::onClick1, " + textResult1);
                        }

                        if (editText2 != null) {
                            textResult2 = editText2.getText().toString();
                            Log.d(TAG, "mSetAlarmListener::onClick2, " + textResult2);
                        }

                        if ((textResult1.length() > 0) && (textResult2.length() > 0)) {
                            int ret = mService.setActivityGoal(mConnectedDeviceAddress, Integer.parseInt(textResult1), Integer.parseInt(textResult2));
                            printStatus(ret, R.string.str_set_activity_goal);
                        }
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get Main Goal
    private View.OnClickListener  mGetMainGoalListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                mUARTResult = "";
                int ret = mService.getMainGoal(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_main_goal);
            }
        }
    };

    // Set Main Goal
    private View.OnClickListener  mSetMainGoalListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                TextView textView = new TextView(glanceMainActivity.this);
                textView.setText("Goal Type:\n" +
                        "0 - Calories Goal\n" +
                        "1 - Step Goal\n" +
                        "2 - Walk Distance Goal\n" +
                        "3 - Walk Duration Goal\n" +
                        "4 - Run Distance Goal\n" +
                        "5 - Run Duration Goal");
                editDialog.setCustomTitle(textView);

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("0");
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mGetActivityGoalListener::onClick, " + textResult);
                        int index = Integer.parseInt(textResult);
                        if (index >= 0 && index <= 5) {
                            int ret = mService.setMainGoal(mConnectedDeviceAddress, index);
                            printStatus(ret, R.string.str_get_activity_goal);
                        }
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get Debug Log
    private View.OnClickListener  mGetDebugLogListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                mUARTResult = "";
                int ret = mService.getDebugLogData(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_debug_log);
            }
        }
    };

    // Clear Debug Log
    private View.OnClickListener  mClearDebugLogListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                mUARTResult = "";
                int ret = mService.clearDebugLOGData(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_clear_debug_log);
            }
        }
    };

    // Set MPU Mode
    private View.OnClickListener  mSetMPUModeListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                TextView textView = new TextView(glanceMainActivity.this);
                textView.setText("MPU Mode:\n" +
                        "0 = Pedo mode,\n" +
                        "1 = Sport mode,\n" +
                        "2 = Magnet 9x mode,\n" +
                        "3 = Sleep mode,\n" +
                        "4 = PreProrun mode,\n" +
                        "5 = Prorun mode,\n" +
                        "6 = Stream 32Hz,\n" +
                        "7 = Stream 250Hz,\n" +
                        "8 = Stream 125Hz,\n" +
                        "255 = Re-init mpu with current mode");
                editDialog.setCustomTitle(textView);

                final EditText editText = new EditText(glanceMainActivity.this);
                editText.setText("0");
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editDialog.setView(editText);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        String textResult = editText.getText().toString();
                        Log.d(TAG, "mSetMPUModeListener::onClick, " + textResult);
                        int index = Integer.parseInt(textResult);
                        if ( (index >= 0 && index <= 8) || (index == 255) ) {
                            int ret = mService.setMPUMode(mConnectedDeviceAddress, index);
                            printStatus(ret, R.string.str_set_mpu_mode);
                        }
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get MPU Mode
    private View.OnClickListener  mGetMPUModeListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                mUARTResult = "";
                int ret = mService.getMPUMode(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_mpu_mode);
            }
        }
    };

    // Get SPI Whoami
    private View.OnClickListener  mGetSPIWhoamiListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                mUARTResult = "";
                int ret = mService.getSPIWhoami(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_spi_whoami);
            }
        }
    };

    private View.OnClickListener mSetUserDataAlarmListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";

                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set User Data Alarm");

                final LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.setalarm_layout, null);
                editDialog.setView(layout);
                final EditText editTextAlarm1Hour = (EditText)layout.findViewById(R.id.editInputALarm1Hour);
                final EditText editTextAlarm1Min = (EditText)layout.findViewById(R.id.editInputALarm1Min);
                final EditText editTextAlarm1Sec = (EditText)layout.findViewById(R.id.editInputALarm1Sec);
                final EditText editTextAlarm1Enable = (EditText)layout.findViewById(R.id.editInputALarm1Enable);
                editTextAlarm1Hour.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextAlarm1Min.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextAlarm1Sec.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextAlarm1Enable.setInputType(InputType.TYPE_CLASS_NUMBER);

                final EditText editTextAlarm2Hour = (EditText)layout.findViewById(R.id.editInputALarm2Hour);
                final EditText editTextAlarm2Min = (EditText)layout.findViewById(R.id.editInputALarm2Min);
                final EditText editTextAlarm2Sec = (EditText)layout.findViewById(R.id.editInputALarm2Sec);
                final EditText editTextAlarm2Enable = (EditText)layout.findViewById(R.id.editInputALarm2Enable);
                editTextAlarm2Hour.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextAlarm2Min.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextAlarm2Sec.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextAlarm2Enable.setInputType(InputType.TYPE_CLASS_NUMBER);

                final EditText editTextAlarm3Hour = (EditText)layout.findViewById(R.id.editInputALarm3Hour);
                final EditText editTextAlarm3Min = (EditText)layout.findViewById(R.id.editInputALarm3Min);
                final EditText editTextAlarm3Sec = (EditText)layout.findViewById(R.id.editInputALarm3Sec);
                final EditText editTextAlarm3Enable = (EditText)layout.findViewById(R.id.editInputALarm3Enable);
                editTextAlarm3Hour.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextAlarm3Min.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextAlarm3Sec.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextAlarm3Enable.setInputType(InputType.TYPE_CLASS_NUMBER);

                final EditText editTextAlarm4Hour = (EditText)layout.findViewById(R.id.editInputALarm4Hour);
                final EditText editTextAlarm4Min = (EditText)layout.findViewById(R.id.editInputALarm4Min);
                final EditText editTextAlarm4Sec = (EditText)layout.findViewById(R.id.editInputALarm4Sec);
                final EditText editTextAlarm4Enable = (EditText)layout.findViewById(R.id.editInputALarm4Enable);
                editTextAlarm4Hour.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextAlarm4Min.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextAlarm4Sec.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextAlarm4Enable.setInputType(InputType.TYPE_CLASS_NUMBER);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        GlanceStatus.AlarmResult alarm = (new GlanceStatus()).new AlarmResult();
                        try {
                            alarm.getAlarm1().setHour(Integer.parseInt(editTextAlarm1Hour.getText().toString()));
                            alarm.getAlarm1().setMin(Integer.parseInt(editTextAlarm1Min.getText().toString()));
                            alarm.getAlarm1().setSecond(Integer.parseInt(editTextAlarm1Sec.getText().toString()));
                            alarm.getAlarm1().setEnable(Integer.parseInt(editTextAlarm1Enable.getText().toString()) == 1);

                            alarm.getAlarm2().setHour(Integer.parseInt(editTextAlarm2Hour.getText().toString()));
                            alarm.getAlarm2().setMin(Integer.parseInt(editTextAlarm2Min.getText().toString()));
                            alarm.getAlarm2().setSecond(Integer.parseInt(editTextAlarm2Sec.getText().toString()));
                            alarm.getAlarm2().setEnable(Integer.parseInt(editTextAlarm2Enable.getText().toString()) == 1);

                            alarm.getAlarm3().setHour(Integer.parseInt(editTextAlarm3Hour.getText().toString()));
                            alarm.getAlarm3().setMin(Integer.parseInt(editTextAlarm3Min.getText().toString()));
                            alarm.getAlarm3().setSecond(Integer.parseInt(editTextAlarm3Sec.getText().toString()));
                            alarm.getAlarm3().setEnable(Integer.parseInt(editTextAlarm3Enable.getText().toString()) == 1);

                            alarm.getAlarm4().setHour(Integer.parseInt(editTextAlarm4Hour.getText().toString()));
                            alarm.getAlarm4().setMin(Integer.parseInt(editTextAlarm4Min.getText().toString()));
                            alarm.getAlarm4().setSecond(Integer.parseInt(editTextAlarm4Sec.getText().toString()));
                            alarm.getAlarm4().setEnable(Integer.parseInt(editTextAlarm4Enable.getText().toString()) == 1);
                            mService.setUserDataAlarm(mConnectedDeviceAddress, alarm);
                        } catch (Exception ex) {}
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get User Data Amarm
    private View.OnClickListener  mGetUserDataAlarmListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                mUARTResult = "";
                int ret = mService.getUserDataAlarm(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_user_data_alarm);
            }
        }
    };

    private View.OnClickListener mSetUserDataProfileListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";

                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set User Data Profile");

                final LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.setprofile_layout, null);
                editDialog.setView(layout);
                final EditText editTextWeight = (EditText)layout.findViewById(R.id.editInputWeight);
                final EditText editTextHeight = (EditText)layout.findViewById(R.id.editInputHeight);
                final EditText editTextAge = (EditText)layout.findViewById(R.id.editInputAge);
                final EditText editTextGender = (EditText)layout.findViewById(R.id.editInputGender);
                final EditText editTextUnit = (EditText)layout.findViewById(R.id.editInputUnit);
                final EditText editTextTimeFormat = (EditText)layout.findViewById(R.id.editInputTimeFormat);
                final EditText editTextViewWatch = (EditText)layout.findViewById(R.id.editInputViewWatch);
                final EditText editTextOledBrightness = (EditText)layout.findViewById(R.id.editInputOledBrightness);
                final EditText editTextAutoDetectProrun = (EditText)layout.findViewById(R.id.editInputAutoDetectProrun);
                final EditText editTextMainGoal = (EditText)layout.findViewById(R.id.editInputMainGoal);
                final EditText editTextCaloriesGoal = (EditText)layout.findViewById(R.id.editInputCaloriesGoal);
                final EditText editTextStepGpal = (EditText)layout.findViewById(R.id.editInputStepGoal);
                editTextWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextHeight.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextAge.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextGender.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextUnit.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextTimeFormat.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextOledBrightness.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextAutoDetectProrun.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextMainGoal.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextCaloriesGoal.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextStepGpal.setInputType(InputType.TYPE_CLASS_NUMBER);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        GlanceStatus.UserData2Profile profile = (new GlanceStatus()).new UserData2Profile();
                        try {
                            profile.setWeight(Integer.parseInt(editTextWeight.getText().toString()));
                            profile.setHeight(Integer.parseInt(editTextHeight.getText().toString()));
                            profile.setAge(Integer.parseInt(editTextAge.getText().toString()));
                            profile.setGender(Integer.parseInt(editTextGender.getText().toString()));
                            profile.setUnit(Integer.parseInt(editTextUnit.getText().toString()));
                            profile.setTimeFormat(Integer.parseInt(editTextTimeFormat.getText().toString()));
                            profile.setViewWatch(Integer.parseInt(editTextViewWatch.getText().toString()));
                            profile.setOledBrightness(Integer.parseInt(editTextOledBrightness.getText().toString()));
                            profile.setAutoDetectProrun(Integer.parseInt(editTextAutoDetectProrun.getText().toString()));
                            profile.setMainGoal(Integer.parseInt(editTextMainGoal.getText().toString()));
                            profile.setCaloriesGoal(Integer.parseInt(editTextCaloriesGoal.getText().toString()));
                            profile.setStepGoal(Integer.parseInt(editTextStepGpal.getText().toString()));
                            mService.setUserDataProfile(mConnectedDeviceAddress, profile);
                        } catch (Exception ex) {}
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };

    // Get User Data Profile
    private View.OnClickListener  mGetUserDataProfileListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                mUARTResult = "";
                int ret = mService.getUserDataProfile(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_user_data_profile);
            }
        }
    };

    private View.OnClickListener mSetUserDataGoalListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (mService != null) {
                // Must Stop Getting RSC data first. Otherwise, device will slow response
                mUARTResult = "";

                AlertDialog.Builder editDialog = new AlertDialog.Builder(glanceMainActivity.this);
                editDialog.setTitle("Set User Data Goal");

                final LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.setgoal_layout, null);
                editDialog.setView(layout);
                final EditText editTextWalkDistanceGoal = (EditText)layout.findViewById(R.id.editInputWalkDistanceGoal);
                final EditText editTextWalkDurationGoal = (EditText)layout.findViewById(R.id.editInputWalkDurationGoal);
                final EditText editTextRunDistanceGoal = (EditText)layout.findViewById(R.id.editInputRunDistanceGoal);
                final EditText editTextRunDurationGoal = (EditText)layout.findViewById(R.id.editInputRunDurationGoal);
                editTextWalkDistanceGoal.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextWalkDurationGoal.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextRunDistanceGoal.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTextRunDurationGoal.setInputType(InputType.TYPE_CLASS_NUMBER);

                editDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        GlanceStatus.UserData3Goal goal = (new GlanceStatus()).new UserData3Goal();
                        try {
                            goal.setWalkDistanceGoal(Integer.parseInt(editTextWalkDistanceGoal.getText().toString()));
                            goal.setWalkDurationGoal(Integer.parseInt(editTextWalkDurationGoal.getText().toString()));
                            goal.setRunDistanceGoal(Integer.parseInt(editTextRunDistanceGoal.getText().toString()));
                            goal.setRunDurationGoal(Integer.parseInt(editTextRunDurationGoal.getText().toString()));
                            mService.setUserDataGoal(mConnectedDeviceAddress, goal);
                        } catch (Exception ex) {}
                    }
                });

                editDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        //...
                    }
                });
                editDialog.show();
            }
        }
    };
    // Get User Data Goal
    private View.OnClickListener  mGetUserDataGoalListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mService != null) {
                mUARTResult = "";
                int ret = mService.getUserDataGoal(mConnectedDeviceAddress);
                printStatus(ret, R.string.str_get_user_data_goal);
            }
        }
    };



    // Get DUMMY LOG Data test
//    private View.OnClickListener mGetDummyLOGDataTestListener = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            // TODO Auto-generated method stub
//            if (mService != null) {
//                // Must Stop Getting RSC data first. Otherwise, device will slow response
//                mUARTResult = "";
//                int ret = mService.getDummyLOGDataTest(mConnectedDeviceAddress);
//                printStatus(ret, R.string.str_get_dummy_log_test);
//            }
//        }
//    };

    // (-) Prev Button
    private View.OnClickListener mPrevButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mCurrentCmdIndex > 0)
            {
                mCurrentCmdIndex--;
            }
            mButtonLabelTextView.setText(getButtonString(mCurrentCmdIndex));
            mGetSetButton.setOnClickListener(getOnClickListenerByIndex(mCurrentCmdIndex));
        }
    };

    // (+) Prev Button
    private View.OnClickListener mNextButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mCurrentCmdIndex < COMMAND_LIST.MAX_CMD_INDEX.ordinal() - 1)
            {
                mCurrentCmdIndex++;
            }
            mButtonLabelTextView.setText(getButtonString(mCurrentCmdIndex));
            mGetSetButton.setOnClickListener(getOnClickListenerByIndex(mCurrentCmdIndex));
        }
    };

    private Runnable getBatteryLevelRunnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (mService != null) {
                mService.readBatteryLevel(mConnectedDeviceAddress);
                // Siukoon: I donno what to do, just comment out this below line and review in July 2016.
                //mGetBatteryHandler.postDelayed(getBatteryLevelRunnable, 5000);
            }
        }
    };

    // Broadcast receiver to receive message from BLE service
    private final BroadcastReceiver mBluetoothServiceBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();

            String deviceName = "";
            String deviceAddress = "";

            if (!action.equals(GlanceProtocolService.ACTION_SCANNING_DEVICE) && !action.equals(GlanceProtocolService.ACTION_SCANNING_STOPPED)) {
                deviceName = intent.getStringExtra(LanternProtocolService.EXTRA_DEVICE_NAME);
                deviceAddress = intent.getStringExtra(LanternProtocolService.EXTRA_DEVICE_ADDRESS);
            }

            Log.i(TAG, "Received Action - " + action + " from " + deviceName);

            /* Device Connected */

            if (action.equals(GlanceProtocolService.ACTION_GATT_CONNECTED)) {
                Log.d(TAG, "GATT connected");
            } else if (action.equals(GlanceProtocolService.ACTION_GATT_DISCONNECTED)) {
                Log.d(TAG, "Device disconnected");

                mConnectButton.setText(getResources().getString(R.string.connect_string));
                mConnectButton.setEnabled(true);
                mConnectStateTextView.setText(getResources().getString(R.string.connect_state_disconnected));
                mGlanceDeviceAddressSpinnerList.setEnabled(true);
                mIsConnected = false;
                mIsSetConnectionInterval = false;
                mConnectedDeviceAddress = null;

                mScanButton.setEnabled(true);
                mReadRSCButton.setEnabled(false);
                mGetBatteryButton.setEnabled(false);
                mReadMotionStreamButton.setEnabled(false);
                mRSCResultTextView.setText("");
                mBatteryLevelTextView.setText("");

                //if (mGetBatteryHandler != null) {
                //mGetBatteryHandler.removeCallbacks(getBatteryLevelRunnable);
                //}

                mGetSetButton.setEnabled(false);

                if (mService != null) {
                    mService.startGettingUARTData(mConnectedDeviceAddress, false);
                    mService.close();
                }

                updateUartText("");
            } else if (action.equals(GlanceProtocolService.ACTION_FOUND_DEVICE)) {
            Log.d(TAG, "Target Device found");
                //if ( deviceAddress.contains("AE:76") )
                {
                    mLatestBLEDeviceAddress = deviceAddress;
                    String deviceInfo = deviceName + "," + deviceAddress;
                    mLatestBLEDeviceAddressTextView.setText(deviceInfo);
                    boolean isDfuMode = intent.getBooleanExtra(GlanceProtocolService.EXTRA_DFU_MODE, false);
                    int bootMode = intent.getIntExtra(GlanceProtocolService.EXTRA_GET_BOOT_MODE_RESULT, GlanceProtocolService.BOOT_MODE_UNKNOWN);
                    int buttonState = intent.getIntExtra(GlanceProtocolService.EXTRA_GET_BUTTON_STATE, GlanceProtocolService.BUTTON_UNKNOWN);
                    byte[] macAddr = intent.getByteArrayExtra(GlanceProtocolService.EXTRA_GET_MAC_ADDR);
                    int protocolVer = intent.getIntExtra(GlanceProtocolService.EXTRA_GET_PROTOCOL_VERSION, GlanceProtocolService.PROTOCOL_UNKNOWN);
                    if (isDfuMode) {
                        Log.d(TAG, "Target Device in DFU Mode");
                        deviceInfo += ", D";
                    } else
                        deviceInfo += ", N";
                    if (bootMode == GlanceProtocolService.BOOT_MODE_NORMAL)
                        deviceInfo += "N";
                    else if (bootMode == GlanceProtocolService.BOOT_MODE_TEST)
                        deviceInfo += "T";
                    else
                        deviceInfo += "U";
                    if (buttonState == GlanceProtocolService.BUTTON_NORMAL)
                        deviceInfo += "B";
                    else if (buttonState == GlanceProtocolService.BUTTON_PRESSED)
                        deviceInfo += "P";
                    else
                        deviceInfo += "U";
                    deviceInfo += protocolVer;
                    mLatestBLEDeviceAddressTextView.setText(deviceInfo);
                    updateGlanceDeviceList(deviceInfo);
                }
            } else if (action.equals(GlanceProtocolService.ACTION_RSSI)) {
                int rssi = intent.getIntExtra(GlanceProtocolService.EXTRA_RSSI, -999);
                Log.d(TAG, "Device RSSI: " + rssi);

//                mLatestBLEDeviceRSSITextView.setText(String.valueOf(rssi));
            } else if ( action.equals(GlanceProtocolService.ACTION_SCANNING_DEVICE)) {
                Log.d(TAG, "Scanning device");
            } else if (action.equals(GlanceProtocolService.ACTION_SCANNING_STOPPED)) {
                Log.d(TAG, "Scanning stopped");
                mIsScanStarted = false;
                mScanButton.setText(getResources().getString(R.string.scan_string));

//                if ((mLatestBLEDeviceAddress != null) && (mLatestBLEDeviceAddress.length() > 0)) {
//                    mConnectButton.setEnabled(true);
//                }
                if ((mGlanceDeviceAddressSpinnerList.getSelectedItem() != null) && (mGlanceDeviceAddressSpinnerList.getSelectedItem().toString().length() > 0)) {
                    mConnectButton.setEnabled(true);
                }
            } else if (action.equals(GlanceProtocolService.ACTION_DEVICE_DOES_NOT_SUPPORT_UART)) {
            } else if (action.equals(GlanceProtocolService.ACTION_GLANCE_CONNECT_COMPLETE)) {
                Log.d(TAG, "Completely connected");

                mConnectButton.setText(getResources().getString(R.string.disconnect_string));
                mConnectButton.setEnabled(true);
                mConnectStateTextView.setText(getResources().getString(R.string.connect_state_connected));
                mIsConnected = true;
                mGlanceDeviceAddressSpinnerList.setEnabled(false);

                mReadRSCButton.setEnabled(true);
                mGetBatteryButton.setEnabled(true);
                mReadMotionStreamButton.setEnabled(true);
                mConnectedDeviceAddress = mLatestBLEDeviceAddress;


                if (mService != null) {
                    mService.startGettingUARTData(mConnectedDeviceAddress, true);
                }

                mGetSetButton.setEnabled(true);
            } else if (action.equals(GlanceProtocolService.ACTION_GLANCE_RSC_DATA2)) {
                Log.d(TAG, "RSC Data");
                GlanceProtocolService.RSCData2 data = (GlanceProtocolService.RSCData2)intent.getSerializableExtra(GlanceProtocolService.EXTRA_RSC_DATA2);
                if (data != null) {
                    String result;
                    result = "Walking Calories: " + data.mWalkingCals +" kcal\n";
                    result += String.format("Walking Total Distance: %1.1f m\n", data.mWalkingDistance);
                    result += ("Walking Steps: " + data.mWalkingCount + "\n");
                    result += ("Walking Duration: " + data.mWalkingDuration + "mins\n");
                    result += "Running Calories: " + data.mRunningCals +" kcal\n";
                    result += String.format("Running Total Distance: %1.1f m\n", data.mRunningDistance);
                    result += ("Running Steps: " + data.mRunningCount + "\n");
                    result += ("Running Duration: " + data.mRunningDuration + "mins\n");
                    result += ("Total Calories: " + data.mTotalCals + " kcal\n");
                    result += ("Activity: " + data.mMotionValue + "\n");
                    mUARTResultTextView.setText(result);
                    mUARTResultTextView.scrollTo(0, 0);

                    // Siukoon 20150624: To appTask
                    //     Daily accumulated Calories = data.mCalories                                    [unit is Cals]
                    //     Daily accumulated Steps    = (data.mTotalDistance * 10) / data.mStrideLength;  [unit is Steps]
                    //     Daily accumulated Distance = data.mTotalDistance                               [unit is m]
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_FW_VER_RESPONSE)) {
                GlanceStatus.FirmwareVersion firmwareVersion  = (GlanceStatus.FirmwareVersion)intent.getSerializableExtra(GlanceProtocolService.EXTRA_FW_VER);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Get Firmware Version:" + firmwareVersion.toString());
                    updateUartText("Device Response: Get Firmware Version:\r\n" + "FW: " + firmwareVersion.getFirmwareMajorVersion() + "." + firmwareVersion.getFirmwareMinorVersion() +
                            "\r\nBuild: " + firmwareVersion.getBuildNumber() +
                            "\r\nAlg: " + firmwareVersion.getAlgorithmMajorNumber() + "." + firmwareVersion.getAlgorithmMinorNumber() +
                            "\r\nBootloader: " + firmwareVersion.getBootloaderMajorNumber() + "." + firmwareVersion.getBootLoaderMinorNumber());
                }
                else
                {
                    Log.d(TAG, "Device Response: Get Firmware Version: Fail");
                    updateUartText("Device Response: Get Firmware Version: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_BLE_ID_RESPONSE)) {
                GlanceStatus.BLEID BleId  = (GlanceStatus.BLEID)intent.getSerializableExtra(GlanceProtocolService.EXTRA_BLE_ID);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    String str = "Data hex values: ";
                    for (int ii = 0; ii < BleId.getByte().length; ii++)
                    {
                        str += String.format("0x%02x ", BleId.getByte()[ii]);
                    }
                    Log.d(TAG, "Device Response: Get BLIE ID/Mac address: " + str);
                    updateUartText("Device Response: Get BLIE ID/Mac address:\r\n" + str);
                }
                else
                {
                    Log.d(TAG, "Device Response: Get BLIE ID/Mac address: Fail");
                    updateUartText("Device Response: Get BLIE ID/Mac address: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_STEP_COUNT_RESPONSE)) {
                int stepCount  = intent.getIntExtra(GlanceProtocolService.EXTRA_STEP_COUNT, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get Step Count: " + stepCount);
                    updateUartText("Device Response: Get Step Count: " + stepCount);
                }
                else
                {
                    Log.d(TAG, "Device Response: Get Step Count: Fail");
                    updateUartText("Device Response: Get Step Count: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_FACTORY_RESET_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_FACTORY_RESET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Factory Reset: Success");
                    updateUartText("Device Response: Factory Reset: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Factory Reset: Fail");
                    updateUartText("Device Response: Factory Reset: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SHIPMENT_MODE_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SHIPMENT_MODE_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Shipment Mode: Success");
                    updateUartText("Device Response: Shipment Mode: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Shipment Mode: Fail");
                    updateUartText("Device Response: Shipment Mode: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_TIME_FORMAT_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_TIME_FORMAT_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Set time format: Success");
                    updateUartText("Device Response: Set time format: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set time format: Fail");
                    updateUartText("Device Response: Set time format: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_TIME_FORMAT_RESPONSE)) {
                GlanceStatus.TimeFormat timeFormat  = (GlanceStatus.TimeFormat)intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_TIME_FORMAT);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get Time format: " + timeFormat.toString());
                    updateUartText("Device Response: Get Time format:\r\n" + timeFormat.toString());
                }
                else
                {
                    Log.d(TAG, "Device Response: Get Time format: Fail");
                    updateUartText("Device Response: Get Time format: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_UNIT_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_UNIT_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Set unit: Success");
                    updateUartText("Device Response: Set unit: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set unit: Fail");
                    updateUartText("Device Response: Set unit: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_UNIT_RESPONSE)) {
                GlanceStatus.UnitOfMeasure unitOfMeasure  = (GlanceStatus.UnitOfMeasure)intent.getSerializableExtra(GlanceProtocolService.EXTRA_UNIT);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get unit: " + unitOfMeasure.toString());
                    updateUartText("Device Response: Get unit:\r\n" + unitOfMeasure.toString());
                }
                else
                {
                    Log.d(TAG, "Device Response: Get unit: Fail");
                    updateUartText("Device Response: Get unit: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_SCREEN_BRIGHTNESS_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_SCREEN_BRIGHTNESS_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Set screen brightness: Success");
                    updateUartText("Device Response: Set screen brightness: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set screen brightness: Fail");
                    updateUartText("Device Response: Set screen brightness: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_SCREEN_BRIGHTNESS_RESPONSE)) {
                int logInterval  = intent.getIntExtra(GlanceProtocolService.EXTRA_GET_SCREEN_BRIGHTNESS, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get screen brightness: " + logInterval);
                    updateUartText("Device Response: Get screen brightness: " + logInterval);
                }
                else
                {
                    Log.d(TAG, "Device Response: Get screen brightness: Fail");
                    updateUartText("Device Response: Get screen brightness: Fail, Error Code: " + success);
                }
            }
//            else if (action.equals(GlanceProtocolService.GLANCE_SET_LOG_INTERVAL_RESPONSE)) {
//                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_LOG_INTERVAL_RESULT);
//                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
//                {
//                    Log.d(TAG, "Device Response: Set log interval: Success");
//                    updateUartText("Device Response: Set log interval: Success");
//                }
//                else
//                {
//                    Log.d(TAG, "Device Response: Set log interval: Fail");
//                    updateUartText("Device Response: Set log interval: Fail, Error Code: " + success);
//                }
//            }
//            else if (action.equals(GlanceProtocolService.GLANCE_GET_LOG_INTERVAL_RESPONSE)) {
//                int logInterval  = intent.getIntExtra(GlanceProtocolService.EXTRA_LOG_INTERVAL, -1);
//                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
//                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
//                {
//                    Log.d(TAG, "Device Response: Get log interval: " + logInterval);
//                    updateUartText("Device Response: Get log interval: " + logInterval);
//                }
//                else
//                {
//                    Log.d(TAG, "Device Response: Get log interval: Fail");
//                    updateUartText("Device Response: Get log interval: Fail, Error Code: " + success);
//                }
//            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_POWER_MODE_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_POWER_MODE_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    SetStreamingModeSuccess();
                    Log.d(TAG, "Device Response: Set power mode: Success");
                    updateUartText("Device Response: Set power mode: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set power mode: Fail");
                    updateUartText("Device Response: Set power mode: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_POWER_MODE_RESPONSE)) {
                int powerMode  = intent.getIntExtra(GlanceProtocolService.EXTRA_POWER_MODE, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get power mode: " + powerMode);
                    updateUartText("Device Response: Get power mode: " + powerMode);
                }
                else
                {
                    Log.d(TAG, "Device Response: Get power mode: Fail");
                    updateUartText("Device Response: Get power mode: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_TXPOWER_RESPONSE)) {
                int txPower  = intent.getIntExtra(GlanceProtocolService.EXTRA_TXPOWER, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get TX Power: " + txPower + " dBm");
                    updateUartText("Device Response: Get TX Power: " + txPower + " dBm");
                }
                else
                {
                    Log.d(TAG, "Device Response: Get TX Power: Fail");
                    updateUartText("Device Response: Get TX Power: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_CALORIES_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_CALORIES_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Set Goal: Success");
                    updateUartText("Device Response: Set Goal: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set Goal: Fail");
                    updateUartText("Device Response: Set Goal: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_CALORIES_RESPONSE)) {
                int calories  = intent.getIntExtra(GlanceProtocolService.EXTRA_CALORIES, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get Goal: " + calories);
                    updateUartText("Device Response: Get Goal: " + calories);
                }
                else
                {
                    Log.d(TAG, "Device Response: Get Goal: Fail");
                    updateUartText("Device Response: Get Goal: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_WEIGHT_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_WEIGHT_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Set weight: Success");
                    updateUartText("Device Response: Set weight: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set weight: Fail");
                    updateUartText("Device Response: Set weight: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_WEIGHT_RESPONSE)) {
                int weight  = intent.getIntExtra(GlanceProtocolService.EXTRA_WEIGHT, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get weight: " + weight);
                    updateUartText("Device Response: Get weight: " + weight);
                }
                else
                {
                    Log.d(TAG, "Device Response: Get weight: Fail");
                    updateUartText("Device Response: Get weight: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_HEIGHT_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_HEIGHT_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Set height: Success");
                    updateUartText("Device Response: Set height: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set height: Fail");
                    updateUartText("Device Response: Set height: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_HEIGHT_RESPONSE)) {
                int height  = intent.getIntExtra(GlanceProtocolService.EXTRA_HEIGHT, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get height: " + height);
                    updateUartText("Device Response: Get height: " + height);
                }
                else
                {
                    Log.d(TAG, "Device Response: Get height: Fail");
                    updateUartText("Device Response: Get height: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_DATETIME_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_DATETIME_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Set date and time: Success");
                    updateUartText("Device Response: Set date and time: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set date and time: Fail");
                    updateUartText("Device Response: Set date and time: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_DATETIME_RESPONSE)) {
                GlanceStatus.DateTime dateTime  = (GlanceStatus.DateTime)intent.getSerializableExtra(GlanceProtocolService.EXTRA_DATETIME);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get DateTime: " + dateTime.getYear() + "-" + dateTime.getMonth() + "-" + dateTime.getDay() + " " + dateTime.getHour() + ":" + dateTime.getMin() + ":" + dateTime.getSecond());
                    updateUartText(String.format("Device Response: Get DateTime: %d-%02d-%02d %02d:%02d:%02d", dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(), dateTime.getHour(), dateTime.getMin(), dateTime.getSecond()));
                }
                else
                {
                    Log.d(TAG, "Device Response: Get DateTime: Fail");
                    updateUartText("Device Response: Get DateTime: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_LOGDATA_PROGRESS_RESPONSE)) {
                int progress = intent.getIntExtra(GlanceProtocolService.EXTRA_LOG_PROGRESS_DATA, 0);
                Log.d(TAG, "Device Response: Get  Log Data  progress=" + progress);
                updateUartText("progress:" + progress, false);
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_LOGDATA_RESPONSE)) {
                GlanceStatus.LogData logData  = (GlanceStatus.LogData)intent.getSerializableExtra(GlanceProtocolService.EXTRA_LOG_DATA);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (logData != null && success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get Log Data: Success");
                    //Example
                    //If user walk 150 step in  between 2015-07-13 13:00:00 to 2015-07-13 14:00:00
                    //App will receivce log data as follow
                    //2015-07-13 13:00:00 to 2015-07-13 13:05:00   10 steps
                    //getLogStartUTCTime() = 1436792400000
                    //getLogEndUTCTime() = 1436792700000
                    //getCount() = 10
                    //2015-07-13 13:05:00 to 2015-07-13 13:10:00   20 steps
                    //getLogStartUTCTime() = 1436792700000
                    //getLogEndUTCTime() = 1436793000000
                    //getCount() = 20
                    //2015-07-13 13:10:00 to 2015-07-13 13:15:00   10 steps
                    //getLogStartUTCTime() = 1436793000000
                    //getLogEndUTCTime() = 1436793300000
                    //getCount() = 10
                    //2015-07-13 13:15:00 to 2015-07-13 13:20:00    5 steps
                    //getLogStartUTCTime() = 1436793300000
                    //getLogEndUTCTime() = 1436793600000
                    //getCount() = 5
                    //2015-07-13 13:20:00 to 2015-07-13 13:25:00   10 steps
                    //getLogStartUTCTime() = 1436793600000
                    //getLogEndUTCTime() = 1436793900000
                    //getCount() = 10
                    //2015-07-13 13:25:00 to 2015-07-13 13:30:00   10 steps
                    //getLogStartUTCTime() = 1436793900000
                    //getLogEndUTCTime() = 1436794200000
                    //getCount() = 10
                    //2015-07-13 13:30:00 to 2015-07-13 13:35:00   10 steps
                    //getLogStartUTCTime() = 1436794200000
                    //getLogEndUTCTime() = 1436794500000
                    //getCount() = 10
                    //2015-07-13 13:35:00 to 2015-07-13 13:40:00   15 steps
                    //getLogStartUTCTime() = 1436794500000
                    //getLogEndUTCTime() = 1436794800000
                    //getCount() = 15
                    //2015-07-13 13:40:00 to 2015-07-13 13:45:00   10 steps
                    //getLogStartUTCTime() = 1436794800000
                    //getLogEndUTCTime() = 1436795100000
                    //getCount() = 10
                    //2015-07-13 13:45:00 to 2015-07-13 13:50:00   10 steps
                    //getLogStartUTCTime() = 1436795100000
                    //getLogEndUTCTime() = 1436795400000
                    //getCount() = 10
                    //2015-07-13 13:50:00 to 2015-07-13 13:55:00   20 steps
                    //getLogStartUTCTime() = 1436795400000
                    //getLogEndUTCTime() = 1436795700000
                    //getCount() = 20
                    //2015-07-13 13:55:00 to 2015-07-13 14:00:00   20 steps
                    //getLogStartUTCTime() = 1436795700000
                    //getLogEndUTCTime() = 1436796000000
                    //getCount() = 20
                    //Total 150 steps
                    //Remark, you can convert the Time value to readable time from website http://www.epochconverter.com

                    //Get Walk LOG data
                    ArrayList<GlanceStatus.SportData> walkDataArrayList = logData.getWalkData();
                    String str = "";
                    str += "\r\nWalk Data\r\n";
                    str += "Index, Start Period, End Period, Duration, Count in Period, Calories\r\n";
                    if (walkDataArrayList.size() == 0)
                    {
                        str += "No Walk LOG Data!\r\n";
                    }
                    for (int ii = 0; ii < walkDataArrayList.size(); ii++)
                    {
                        GlanceStatus.SportData sportData = walkDataArrayList.get(ii);
                        //sportData.getLogStartUTCTime() is the start period of a Walk LOG Data
                        //sportData.getLogEndUTCTime() is the end period of a Walk LOG Data
                        //sportData.getCount() is number of count in between start and end period
                        str += String.format("%04d", ii) + ": " + printUTCTime(sportData.getLogStartUTCTime()) + ", " + printUTCTime(sportData.getLogEndUTCTime()) + ", " + sportData.getDuration() + ", " + String.format("%04d", sportData.getCount()) + ", " + sportData.getCalories() + "\r\n";
                    }

                    //Get Run LOG data
                    ArrayList<GlanceStatus.SportData> runDataArrayList = logData.getRunData();
                    str += "\r\nRun Data\r\n";
                    str += "Index, Start Period, End Period, Duration, Count in Period, Calories\r\n";
                    if (runDataArrayList.size() == 0)
                    {
                        str += "No Run LOG Data!\r\n";
                    }
                    for (int ii = 0; ii < runDataArrayList.size(); ii++)
                    {
                        GlanceStatus.SportData sportData = runDataArrayList.get(ii);
                        //sportData.getLogStartUTCTime() is the start period of a Run LOG Data
                        //sportData.getLogEndUTCTime() is the end period of a Run LOG Data
                        //sportData.getCount() is number of count in between start and end period
                        str += String.format("%04d", ii) + ": " + printUTCTime(sportData.getLogStartUTCTime()) + ", " + printUTCTime(sportData.getLogEndUTCTime()) + ", " + sportData.getDuration() + ", " + String.format("%04d", sportData.getCount()) + ", " + sportData.getCalories() + "\r\n";
                    }

                    //Get Summary LOG data
                    ArrayList<GlanceStatus.SportData> summaryArrayList = logData.getSummaryCaloriesData();
                    str += "\r\nSummary Data\r\n";
                    str += "Index, Start Period, End Period, Distance, Calories\r\n";
                    if (summaryArrayList.size() == 0)
                    {
                        str += "No Summary LOG Data!\r\n";
                    }
                    for (int ii = 0; ii < summaryArrayList.size(); ii++)
                    {
                        GlanceStatus.SportData sportData = summaryArrayList.get(ii);
                        str += String.format("%04d", ii) + ": " + printUTCTime(sportData.getLogStartUTCTime()) + ", " + printUTCTime(sportData.getLogEndUTCTime()) + ", " + sportData.getDistance()  + ", " + sportData.getCalories() + "\r\n";
                    }

                    //Kevin 2015-08-03, Sleep data is move to a standalone API getSleepLogData()
                    //Get Sleep LOG data

                    //Get Cycle  LOG data
//                    ArrayList<GlanceStatus.SportData> cycleDataArrayList = logData.getCycleData();
//                    str += "\r\nCycle Data\r\n";
//                    str += "Index, Start Period, End Period, Count in Period\r\n";
//                    if (runDataArrayList.size() == 0)
//                    {
//                        str += "No Cycle LOG Data!\r\n";
//                    }
//                    for (int ii = 0; ii < cycleDataArrayList.size(); ii++)
//                    {
//                        GlanceStatus.SportData sportData = cycleDataArrayList.get(ii);
//                        //sportData.getLogStartUTCTime() is the start period of a Cycle LOG Data
//                        //sportData.getLogEndUTCTime() is the end period of a Cycle LOG Data
//                        //sportData.getCount() is number of count in between start and end period
//                        str += String.format("%04d", ii) + ": " + printUTCTime(sportData.getLogStartUTCTime()) + ", " + printUTCTime(sportData.getLogEndUTCTime()) + ", " + String.format("%04d", sportData.getCount()) + "\r\n";
//                    }
                    updateUartText("Device Response: Get Log Data: Success\r\n" + str);
                }
                else
                {
                    Log.d(TAG, "Device Response: Get Log Data: Fail");
                    updateUartText("Device Response: Get Log Data: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_DEVICE_NAME_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_DEVICE_NAME_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Set Device Name: Success");
                    updateUartText("Device Response: Set Device Name: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set Device Name: Fail");
                    updateUartText("Device Response: Set Device Name: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_DEVICE_NAME_RESPONSE)) {
                GlanceStatus.DeviceName name  = (GlanceStatus.DeviceName)intent.getSerializableExtra(GlanceProtocolService.EXTRA_GLANCE_NAME);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get Device Name: " + name.getString());
                    updateUartText("Device Response: Get Device Name:\r\n" + name.getString());
                }
                else
                {
                    Log.d(TAG, "Device Response: Get Device Name: Fail");
                    updateUartText("Device Response: Get Device Name: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_ACK_GET_LOG_DATA_RESPONSE)) {
                Log.d(TAG, "Device Response: Set Ack Get Log Data");
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_ACK_LOG_DATA_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Set Ack Get Log Data: Success");
                    updateUartText("Device Response: Set Ack Get Log Data: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set Ack Get Log Data: Fail");
                    updateUartText("Device Response: Set Ack Get Log Data: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_COMMAND_SEND_TIMEOUT)) {
                Log.d(TAG, "Device Response: Send Command Timeout");
                updateUartText("Device Response: Send Command Timeout");
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_PSTORAGE_LOG_INFO_RESPONSE)) {
                int [] integerArray = (int []) intent.getSerializableExtra(GlanceProtocolService.EXTRA_PSTORAGE_LOG_INFO);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    if (integerArray.length == 4) {
                        Log.d(TAG, String.format("Device Response:  Get Pstorage Log Info: Start Pointer of Sent Log:0x%04x,  End Pointer of Sent Log:0x%04x ", integerArray[0], integerArray[1]));
                        updateUartText(String.format("Device Response:  Get Pstorage Log Info: Start Pointer of Sent Log:0x%04x,  End Pointer of Sent Log:0x%04x ", integerArray[0], integerArray[1]));
                        Log.d(TAG, String.format("Device Response:  Get Pstorage Log Info: Start Pointer of Current Log:0x%04x,  End Pointer of Current Log:0x%04x ", integerArray[2], integerArray[3]));
                        updateUartText(String.format("Device Response:  Get Pstorage Log Info: Start Pointer of Current Log:0x%04x,  End Pointer of Current Log:0x%04x ", integerArray[2], integerArray[3]));
                    }
                    else {
                        Log.d(TAG, "Device Response:  Get Pstorage Log Info: Fail, Reason: Integer Array size not equal to four");
                        updateUartText("Device Response:  Get Pstorage Log Info: Fail, Reason: Integer Array size not equal to four");
                    }
                }
                else
                {
                    Log.d(TAG, "Device Response:  Get Pstorage Log Info: Fail");
                    updateUartText("Device Response:  Get Pstorage Log Info: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_SLEEP_LOGDATA_PROGRESS_RESPONSE)) {
                int progress = intent.getIntExtra(GlanceProtocolService.EXTRA_SLEEP_LOG_PROGRESS_DATA, 0);
                Log.d(TAG, "Device Response: Get Sleep Log Data  progress=" + progress);
                updateUartText("progress:" + progress, false);
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_SLEEP_LOGDATA_RESPONSE)) {
                GlanceStatus.SleepLogData sleeplogData = (GlanceStatus.SleepLogData) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SLEEP_LOG_DATA);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (sleeplogData != null && success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Get Sleep Log Data: Success");
                    //Get Sleep LOG data
                    ArrayList<GlanceStatus.SleepData> sleepDataArrayList = sleeplogData.getSleepData();
                    String str = "";
                    String logfilename = "";
                    str += "\r\nSleep Data\r\n";
                    str += "Index, Start Period, End Period, Status, Avg, Max\r\n";
                    if (sleepDataArrayList.size() == 0)
                    {
                        str += "No Sleep LOG Data!\r\n";
                    }
                    else if (sleepDataArrayList.size() < 100) {
                        for (int ii = 0; ii < sleepDataArrayList.size(); ii++) {
                            GlanceStatus.SleepData sleepData = sleepDataArrayList.get(ii);
                            //sportData.getLogStartUTCTime() is the start period of a Sleep LOG Data
                            //sportData.getLogEndUTCTime() is the end period of a Sleep LOG Data
                            //sportData.getSleepStatus() is sleep Status
                            //sportData.getDebugAvgRms() is sleep average RMS value for Debug purpose
                            //sportData.getDebugMaxRms() is sleep maximum RMS value for Debug purpose
                            String strSleepStatus = "";
                            if (sleepData.getSleepStatus() == GlanceStatus.SleepStatus.RESTFUL) {
                                strSleepStatus = "Resful";
                            } else if (sleepData.getSleepStatus() == GlanceStatus.SleepStatus.LIGHT) {
                                strSleepStatus = "Light";
                            } else {
                                strSleepStatus = "Awake";
                            }
                            str += String.format("%04d", ii) + ": " + printUTCTime(sleepData.getLogStartUTCTime()) + ", " + printUTCTime(sleepData.getLogEndUTCTime()) + ", " + strSleepStatus + ", " + Long.toString(sleepData.getDebugAvgRms()) + ", " + Long.toString(sleepData.getDebugMaxRms()) + "\r\n";
                        }
                        //Write Sleep data into internal storage
                        logfilename = createSleepCsv(sleepDataArrayList);
                    }
                    else
                    {
                        logfilename = createSleepCsv(sleepDataArrayList);
                    }
                    updateUartText("Device Response: Get Sleep Log Data: Success\r\n" + str);
                    if (logfilename.length() > 0) {
                        updateUartText("Saved Sleep CSV file saved into " + logfilename);
                    }
                    else if (sleepDataArrayList.size() > 0)
                    {
                        updateUartText("Fail to saved Sleep CSV file");
                    }
                    str = "";
                    ArrayList<ArrayList<GlanceStatus.SleepData>> sleepData2dArrayList = sleeplogData.getSleep2dData();
                    Log.e("KEVIN", "sleepData2dArrayList size=" + sleepData2dArrayList.size());
                    for (int ii = 0; ii < sleepData2dArrayList.size(); ii++) {
                        ArrayList<GlanceStatus.SleepData> sleepDatas = sleepData2dArrayList.get(ii);
                        for (int jj = 0; jj < sleepDatas.size(); jj++)
                        {
                            GlanceStatus.SleepData sleepData = sleepDatas.get(jj);
                            String strSleepStatus = "";
                            if (sleepData.getSleepStatus() == GlanceStatus.SleepStatus.RESTFUL) {
                                strSleepStatus = "Resful";
                            } else if (sleepData.getSleepStatus() == GlanceStatus.SleepStatus.LIGHT) {
                                strSleepStatus = "Light";
                            } else {
                                strSleepStatus = "Awake";
                            }
                            str += String.format("%04d", jj) + ": " + printUTCTime(sleepData.getLogStartUTCTime()) + ", " + printUTCTime(sleepData.getLogEndUTCTime()) + ", " + strSleepStatus  + "\r\n";
                        }
                    }
                    updateUartText("Device Response: Get Sleep Log Data: Success\r\n" + str);
                }
                else
                {
                    Log.d(TAG, "Device Response: Get Sleep Log Data: Fail");
                    updateUartText("Device Response: Get Sleep Log Data: Fail");
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_ACK_GET_SLEEP_LOG_DATA_RESPONSE)) {
                Log.d(TAG, "Device Response: Set Ack Get Sleep Log Data");
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_ACK_SLEEP_LOG_DATA_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Set Ack Get Sleep Log Data: Success");
                    updateUartText("Device Response: Set Ack Get Sleep Log Data: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set Ack Get Sleep Log Data: Fail");
                    updateUartText("Device Response: Set Ack Get Sleep Log Data: Fail, Error Code: " + success);
                }
            }
            //This broadcast is for Internal Development use only
            else if (action.equals(GlanceProtocolService.GLANCE_SET_AUTOWALK_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_AUTOWALK_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Set AutoWalk: Success");
                    updateUartText("Device Response: Set AutoWalk: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set AutoWalk: Fail");
                    updateUartText("Device Response: Set AutoWalk: Fail, Error Code: " + success);
                }
            }
            //This broadcast is for Internal Development use only
            else if (action.equals(GlanceProtocolService.GLANCE_GET_AUTOWALK_RESPONSE)) {
                int logInterval  = intent.getIntExtra(GlanceProtocolService.EXTRA_AUTOWALK, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get AutoWalk: " + logInterval);
                    updateUartText("Device Response: Get AutoWalk: " + logInterval);
                }
                else
                {
                    Log.d(TAG, "Device Response: Get AutoWalk: Fail");
                    updateUartText("Device Response: Get AutoWalk: Fail, Error Code: " + success);
                }
            }
            //This broadcast is for Internal Development use only
            else if (action.equals(GlanceProtocolService.GLANCE_SET_BROWNOUT_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_BROWNOUT_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Set BrownOut: Success");
                    updateUartText("Device Response: Set BrownOut: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set BrownOut: Fail");
                    updateUartText("Device Response: Set BrownOut: Fail, Error Code: " + success);
                }
            }
            //This broadcast is for Internal Development use only
            else if (action.equals(GlanceProtocolService.GLANCE_GET_BROWNOUT_RESPONSE)) {
                int brownout  = intent.getIntExtra(GlanceProtocolService.EXTRA_BROUNOUT, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get BrownOut: " + brownout);
                    updateUartText("Device Response: Get BrownOut: " + brownout);
                }
                else
                {
                    Log.d(TAG, "Device Response: Get BrownOut: Fail");
                    updateUartText("Device Response: Get BrownOut: Fail, Error Code: " + success);
                }
            }
            //This broadcast is for Internal Development use only
//            else if (action.equals(GlanceProtocolService.GLANCE_GET_VOLTAGE_LOG_RESPONSE)) {
//                GlanceStatus.VoltageLogData voltagelogData = (GlanceStatus.VoltageLogData) intent.getSerializableExtra(GlanceProtocolService.EXTRA_VOLTAGE_LOG_DATA);
//                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
//                if (voltagelogData != null && success == GlanceStatus.ErrorResponse.ERROR_NONE) {
//                    Log.d(TAG, "Device Response: Get Voltage Log Data: Success");
//                    //Get Sleep LOG data
//                    ArrayList<GlanceStatus.VoltageData> voltageDataArrayList = voltagelogData.getVoltageData();
//                    String str = "";
//                    String logfilename = "";
//                    str += "\r\nVoltage Log Data\r\n";
//                    str += "Index, Log DateTime, ADC, Voltage(mV)\r\n";
//                    if (voltageDataArrayList.size() == 0)
//                    {
//                        str += "No Voltage Log Data!\r\n";
//                    }
//                    else {
//                        for (int ii = 0; ii < voltageDataArrayList.size(); ii++) {
//                            GlanceStatus.VoltageData voltageData = voltageDataArrayList.get(ii);
//                            str += String.format("%03d", ii) + ": " +  String.format("%02d-%02d %02d:00, %d, %d",
//                                    voltageData.getMonth(),
//                                    voltageData.getDay(),
//                                    voltageData.getHour(),
//                                    voltageData.getADC(),
//                                    voltageData.getMilliVoltage()) + "\r\n";
//                        }
//                        //Write Voltage data into internal storage
//                        logfilename = createVoltageCsv(voltageDataArrayList);
//                    }
//                    updateUartText("Device Response: Get Voltage Log Data: Success\r\n" + str);
//                    if (logfilename.length() > 0) {
//                        updateUartText("Voltage CSV file saved into " + logfilename);
//                    }
//                    else if (voltageDataArrayList.size() > 0)
//                    {
//                        updateUartText("Fail to save Voltage CSV file");
//                    }
//                }
//                else
//                {
//                    Log.d(TAG, "Device Response: Get Voltage Log Data: Fail");
//                    updateUartText("Device Response: Get Voltage Log Data: Fail");
//                }
//            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_CONNECTION_INTERVAL_RESPONSE)) {
                int result  = intent.getIntExtra(GlanceProtocolService.EXTRA_SET_CONNECTION_INTERVAL_RESULT, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    mIsSetConnectionInterval = true;
                    SetConnectionIntervalSuccess();
                    Log.d(TAG, "Device Response: Set Connection Interval, Result: " + result);
                    updateUartText("Device Response: Set Connection Interval, Result: " + result);
                }
                else
                {
                    Log.d(TAG, "Device Response: Set Connection Interval: Fail");
                    updateUartText("Device Response: Set Connection Interval: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_CONNECTION_INTERVAL_RESPONSE)) {
                int connectInterval  = intent.getIntExtra(GlanceProtocolService.EXTRA_CONNECTION_INTERVAL, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get Connection Interval: " + connectInterval);
                    updateUartText("Device Response: Get Connection Interval: " + connectInterval);
                }
                else
                {
                    Log.d(TAG, "Device Response: Get Connection Interval: Fail");
                    updateUartText("Device Response: Get Connection Interval: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_FINISH_CALIBRATION_RESPONSE)) {
                int calibrationResult  = intent.getIntExtra(GlanceProtocolService.EXTRA_FINISH_CALIBRATION, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Finish Calibration Result: " + calibrationResult);
                    updateUartText("Device Response: Finish Calibration Result: " + calibrationResult);
                }
                else
                {
                    Log.d(TAG, "Device Response: Finish Calibration Result: Fail");
                    updateUartText("Device Response: Finish Calibration Result: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_CALIBRATE_FACE_N_RESPONSE)) {
                GlanceStatus.CalibrationFaceResult calibrationFaceResult  = (GlanceStatus.CalibrationFaceResult)intent.getSerializableExtra(GlanceProtocolService.EXTRA_CALIBRATE_FACE_N);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Operate Calibration for Face: " + calibrationFaceResult.toString());
                    updateUartText("Device Response: Operate Calibration for Face: " + calibrationFaceResult.toString());
                }
                else
                {
                    Log.d(TAG, "Device Response: Operate Calibration for Face: Fail");
                    updateUartText("Device Response: Operate Calibration for Face: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_CALIBRATION_DATA_RESPONSE)) {
                GlanceStatus.CalibrationData calibrationData  = (GlanceStatus.CalibrationData)intent.getSerializableExtra(GlanceProtocolService.EXTRA_CALIBRATION_DATA);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get Calibration: " + calibrationData.toString());
                    updateUartText("Device Response: Get Calibration: " + calibrationData.toString());
                }
                else
                {
                    Log.d(TAG, "Device Response: Get Calibration: Fail");
                    updateUartText("Device Response: Get Calibration: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_PSTORAGE_DATA_RESPONSE)) {
                int [] integerArray = (int []) intent.getSerializableExtra(GlanceProtocolService.EXTRA_PSTORAGE_DATA);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    String hexValue = "";
                    for (int ii = 0; ii < integerArray.length; ii++)
                    {
                        hexValue += String.format("0x%02x, ", integerArray[ii]);
                    }
                    Log.d(TAG, "Device Response: Get Pstorage Data: " + hexValue);
                    updateUartText("Device Response: Get Pstorage Data: " + hexValue);
                }
                else
                {
                    Log.d(TAG, "Device Response: Get Pstorage Data: Fail");
                    updateUartText("Device Response: Get Pstorage Data: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.ACTION_GATT_CONNECTION_STATUS_133)) {
                Log.d(TAG, "Connection Status 133 received");
                updateUartText("Connection Status 133 received");
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_BOOTUP_MODE_RESPONSE)) {
                Log.d(TAG, "Device Response: GLANCE_SET_BOOTUP_MODE_RESPONSE");
                GlanceStatus.ErrorResponse result = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_BOOTUP_MODE_RESULT);
                if (result == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Set Bootup Mode: Success");
                    updateUartText("Device Response: Set Bootup Mode, Result: " + result);
                } else {
                    Log.d(TAG, "Device Response: Set Bootup Mode: Fail");
                    updateUartText("Device Response: Set Bootup Mode: Fail, Error Code: " + result);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_BATTERY_LEVEL_RESPONSE)) {
                Log.d(TAG, "Device Response: GLANCE_GET_BATTERY_LEVEL_RESPONSE");
                GlanceStatus.BatteryResult batteryResult  = (GlanceStatus.BatteryResult)intent.getSerializableExtra(GlanceProtocolService.EXTRA_BATTERY_LEVEL);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get Battery Level: " + batteryResult.getBatteryLevel() + ",Status=" + batteryResult.getBatteryStatus());
                    updateUartText(getResources().getString(R.string.battery_level_title) +  " " + batteryResult.getBatteryLevel() + ",Status=" + batteryResult.getBatteryStatus());
                }
                else
                {
                    Log.d(TAG, "Device Response: Get Battery Level: Fail");
                    updateUartText("Device Response: Get Battery Level: Fail, Error Code: " + batteryResult.getBatteryLevel() + ",Status=" + batteryResult.getBatteryStatus());
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_CLUB_HEAD_SPEED_RESPONSE)) {
                Log.d(TAG, "Device Response: GLANCE_SET_CLUB_HEAD_SPEED_RESPONSE");
                GlanceStatus.ErrorResponse result = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_CLUB_HEAD_SPEED_RESULT);
                if (result == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Set Club Head Speed: Success");
                    updateUartText("Device Response: Set Club Head Speed, Result: " + result);
                } else {
                    Log.d(TAG, "Device Response: Set Club Head Speed: Fail");
                    updateUartText("Device Response: Set Club Head Speed: Fail, Error Code: " + result);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_SECONDARY_DISPLAY_RESPONSE)) {
                Log.d(TAG, "Device Response: GLANCE_SET_SECONDARY_DISPLAY_RESPONSE");
                GlanceStatus.ErrorResponse result = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_SECONDARY_DISPLAY_RESULT);
                if (result == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Set Secondary Display: Success");
                    updateUartText("Device Response: Set Secondary Display, Result: " + result);
                } else {
                    Log.d(TAG, "Device Response: Set Secondary Display: Fail");
                    updateUartText("Device Response: Set Secondary Display: Fail, Error Code: " + result);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_ALARM_RESPONSE)) {
                Log.d(TAG, "Device Response: GLANCE_SET_ALARM_RESPONSE");
                GlanceStatus.ErrorResponse result = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_ALARM_RESULT);
                if (result == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Set Alarm: Success");
                    updateUartText("Device Response: Set Alarm, Result: " + result);
                } else {
                    Log.d(TAG, "Device Response: Set Alarm: Fail");
                    updateUartText("Device Response: Set Alarm: Fail, Error Code: " + result);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_ALARM_RESPONSE)) {
                Log.d(TAG, "Device Response: GLANCE_GET_ALARM_RESPONSE");
                GlanceStatus.AlarmResult alarm = (GlanceStatus.AlarmResult) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_ALARM_RESULT);
                GlanceStatus.ErrorResponse result = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (result == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Get Alarm: Success");
                    updateUartText("Device Response: Get Alarm, Result: " + alarm);
                } else {
                    Log.d(TAG, "Device Response: Get Alarm: Fail");
                    updateUartText("Device Response: Get Alarm: Fail, Error Code: " + result);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_COLD_BOOT_COUNT_RESPONSE)) {
                Log.d(TAG, "Devcie Response: GLANCE_GET_COLD_BOOT_COUNT_RESPONSE");
                int bootCount  = intent.getIntExtra(GlanceProtocolService.EXTRA_GET_COLD_BOOT_COUNT_RESULT, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get Cold Boot Count: " + bootCount);
                    updateUartText(getResources().getString(R.string.str_get_cold_boot_count) + " " + bootCount);
                }
                else
                {
                    Log.d(TAG, "Device Response: Get Cold Boot Count: Fail");
                    updateUartText("Device Response: Get Cold Boot Count: Fail, Error Code: " + bootCount);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_RESET_COLD_BOOT_COUNT_RESPONSE)) {
                Log.d(TAG, "Device Response: GLANCE_RESET_COLD_BOOT_COUNT_RESPONSE");
                GlanceStatus.ErrorResponse result = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_RESET_COLD_BOOT_COUNT_RESULT);
                if (result == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Reset Cold Boot Count: Success");
                    updateUartText("Device Response: Reset Cold Boot Count, Result: " + result);
                } else {
                    Log.d(TAG, "Device Response: Reset Cold Boot Count: Fail");
                    updateUartText("Device Response: Reset Cold Boot Count: Fail, Error Code: " + result);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_ISCALED_RESPONSE)) {
                Log.d(TAG, "Device Response: GLANCE_SET_ISCALED_RESPONSE");
                GlanceStatus.ErrorResponse result = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_ISCALED_RESULT);
                if (result == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Set IsCaled: Success");
                    updateUartText("Device Response: Set IsCaled, Result: " + result);
                } else {
                    Log.d(TAG, "Device Response: Set IsCaled: Fail");
                    updateUartText("Device Response: Set IsCaled: Fail, Error Code: " + result);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_PROTOCOL_VER_RESPONSE)) {
                Log.d(TAG, "Device Response: GLANCE_PROTOCOL_VER_RESPONSE");
                int protocolVer = (int) intent.getIntExtra(GlanceProtocolService.EXTRA_PROTOCOL_VER, GlanceProtocolService.PROTOCOL_UNKNOWN);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);

                if ( success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    if (protocolVer == GlanceProtocolService.PROTOCOL_UNKNOWN)
                        updateUartText("Device Response: Request Protocol Version:Unknown");
                    else
                        updateUartText("Device Response: Request Protocol Version:" + protocolVer);
                }
                else {
                    updateUartText("Device Response: Request Protocol Version Fail, Error Code: " + success);
                }

            }
            else if (action.equals(GlanceProtocolService.GLANCE_DFU_MODE_RESPONSE)) {
                Log.d(TAG, "Device Response: GLANCE_DFU_MODE_RESPONSE");
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_DFU_MODE_RESULT);
                if ( success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    updateUartText("Device Response: DFU API sent successfully. Disconnect Device to enter DFU mode");
                }
                else {
                    updateUartText("Device Response: DFU API sent Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SEND_DUMMY_CMD_RESPONSE)) {
                Log.d(TAG, "Device Response: GLANCE_SEND_DUMMY_CMD_RESPONSE");
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);

                if ( success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    updateUartText("Device Response: Send Dummy Cmd no error");
                }
                else {
                    updateUartText("Device Response: Send Dummy Cmd Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_PRORUN_LOGDATA_RESPONSE)) {
                GlanceStatus.ProRunLogData proRunlogData = (GlanceStatus.ProRunLogData) intent.getSerializableExtra(GlanceProtocolService.EXTRA_PRORUN_LOG_DATA);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (proRunlogData != null && success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Get Pro Run Log Data: Success");

                    //Get ProRun Walk LOG data
                    ArrayList<GlanceStatus.ProRunData> walkDataArrayList = proRunlogData.getWalkProRunData();
                    String str = "";
                    String logfilename = "";
                    str += "\r\nWalk Data\r\n";
                    str += "Index, Start Period, End Period, Cadence, Step, Sum, AvgPeak, AvgValley\r\n";
                    if (walkDataArrayList.size() == 0)
                    {
                        str += "No Walk LOG Data!\r\n";
                    }
                    else if (walkDataArrayList.size() < 100) {
                        for (int ii = 0; ii < walkDataArrayList.size(); ii++) {
                            GlanceStatus.ProRunData proRunData = walkDataArrayList.get(ii);
                            str += String.format("%04d", ii) + ": " + printUTCTime(proRunData.getLogStartUTCTime())
                                    + ", " + printUTCTime(proRunData.getLogEndUTCTime())
                                    + ", " + Long.toString(proRunData.getCadence())
                                    + ", " + Long.toString(proRunData.getStep())
                                    + ", " + Long.toString(proRunData.getSum())
                                    + ", " + Long.toString(proRunData.getAvgPeak())
                                    + ", " + Long.toString(proRunData.getAvgValley())
                                    + "\r\n";

                        }
                        //Write  data into internal storage
                        logfilename = createProRunCsv(walkDataArrayList, GlanceStatus.ProRunSportType.WALK);
                    }
                    else
                    {
                        logfilename = createProRunCsv(walkDataArrayList, GlanceStatus.ProRunSportType.WALK);
                    }
                    if (logfilename.length() > 0) {
                        updateUartText("Saved Sleep CSV file saved into " + logfilename);
                    }
                    else if (walkDataArrayList.size() > 0)
                    {
                        updateUartText("Fail to saved Pro Run Walk CSV file");
                    }

                    //Get ProRun Run LOG data
                    ArrayList<GlanceStatus.ProRunData> runDataArrayList = proRunlogData.getRunProRunData();
                    logfilename = "";
                    str += "\r\nRun Data\r\n";
                    str += "Index, Start Period, End Period, Cadence, Step, Sum, AvgPeak, AvgValley\r\n";
                    if (runDataArrayList.size() == 0)
                    {
                        str += "No Run LOG Data!\r\n";
                    }
                    else if (runDataArrayList.size() < 100) {
                        for (int ii = 0; ii < runDataArrayList.size(); ii++) {
                            GlanceStatus.ProRunData proRunData = runDataArrayList.get(ii);
                            str += String.format("%04d", ii) + ": " + printUTCTime(proRunData.getLogStartUTCTime())
                                    + ", " + printUTCTime(proRunData.getLogEndUTCTime())
                                    + ", " + Long.toString(proRunData.getCadence())
                                    + ", " + Long.toString(proRunData.getStep())
                                    + ", " + Long.toString(proRunData.getSum())
                                    + ", " + Long.toString(proRunData.getAvgPeak())
                                    + ", " + Long.toString(proRunData.getAvgValley())
                                    + "\r\n";

                        }
                        //Write data into internal storage
                        logfilename = createProRunCsv(runDataArrayList, GlanceStatus.ProRunSportType.RUN);
                    }
                    else
                    {
                        logfilename = createProRunCsv(runDataArrayList, GlanceStatus.ProRunSportType.RUN);
                    }
                    updateUartText("Device Response: Get Pro Run Log Data: Success\r\n" + str);
                    if (logfilename.length() > 0) {
                        updateUartText("Saved Pro Run Run CSV file saved into " + logfilename);
                    }
                    else if (runDataArrayList.size() > 0)
                    {
                        updateUartText("Fail to saved Pro Run Run CSV file");
                    }

                    ArrayList<ArrayList<GlanceStatus.ProRunData>> mProRunData2dArray = proRunlogData.getProRun2dData();
                    for (int ii = 0; ii < mProRunData2dArray.size(); ii++)
                    {
                        ArrayList<GlanceStatus.ProRunData> proRunDatas = mProRunData2dArray.get(ii);
                        for (int jj = 0; jj < proRunDatas.size(); jj++)
                        {
                            GlanceStatus.ProRunData proRunData = proRunDatas.get(jj);
                            Log.e(TAG, "ProRunList," + ii + "," + jj + "," + proRunData.getSportType() + "," + proRunData.getLogStartUTCTime() + "," + proRunData.getStep());
                        }
                    }
                }
                else
                {
                    Log.d(TAG, "Device Response: Get Pro Run Log Data: Fail");
                    updateUartText("Device Response: Get Pro Run Log Data: Fail");
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_SET_ACK_GET_PRORUN_LOG_DATA_RESPONSE)) {
                Log.d(TAG, "Device Response: Set Ack Get Pro Run Log Data");
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_ACK_PRORUN_LOG_DATA_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Set Ack Get Pro Run Log Data: Success");
                    updateUartText("Device Response: Set Ack Get Pro Run Log Data: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set Ack Get Pro Run Log Data: Fail");
                    updateUartText("Device Response: Set Ack Get Pro Run Log Data: Fail, Error Code: " + success);
                }
            }
            else if (action.equals(GlanceProtocolService.GLANCE_GET_PRORUN_LOGDATA_PROGRESS_RESPONSE)) {
                int progress = intent.getIntExtra(GlanceProtocolService.EXTRA_PRORUN_LOG_PROGRESS_DATA, 0);
                Log.d(TAG, "Device Response: Get Pro Run Log Data  progress=" + progress);
                updateUartText("progress:" + progress, false);
            } else if (action.equals(GlanceProtocolService.GLANCE_SET_APP_CONNECTION_FINISH_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_APP_CONNECTION_FINISH_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Set App Connection Finish: Success");
                    updateUartText("Device Response: Set App Connection Finish: Success");
                } else {
                    Log.d(TAG, "Device Response: Set App Connection Finish: Fail");
                    updateUartText("Device Response: Set App Connection Finish: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_SET_AGE_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_AGE_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Set Goal: Success");
                    updateUartText("Device Response: Set Goal: Success");
                } else {
                    Log.d(TAG, "Device Response: Set Goal: Fail");
                    updateUartText("Device Response: Set Goal: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_GET_AGE_RESPONSE)) {
                int value = intent.getIntExtra(GlanceProtocolService.EXTRA_AGE, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Get Age: " + value);
                    updateUartText("Device Response: Get Age: " + value);
                } else {
                    Log.d(TAG, "Device Response: Get Age: Fail");
                    updateUartText("Device Response: Get Age: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_SET_GENDER_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_GENDER_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Set Gender: Success");
                    updateUartText("Device Response: Set Gender: Success");
                } else {
                    Log.d(TAG, "Device Response: Set Gender: Fail");
                    updateUartText("Device Response: Set Gender: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_GET_GENDER_RESPONSE)) {
                int value = intent.getIntExtra(GlanceProtocolService.EXTRA_GENDER, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Get Gender: " + (value == 1 ? "Male" : "Female"));
                    updateUartText("Device Response: Get Gender: " + (value == 1 ? "Male" : "Female"));
                } else {
                    Log.d(TAG, "Device Response: Get Gender: Fail");
                    updateUartText("Device Response: Get Gender: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_SET_HAND_ARISE_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_HAND_ARISE_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Set Hand Arise: Success");
                    updateUartText("Device Response: Set Hand Arise: Success");
                } else {
                    Log.d(TAG, "Device Response: Set Hand Arise: Fail");
                    updateUartText("Device Response: Set Hand Arise: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_GET_HAND_ARISE_RESPONSE)) {
                int value = intent.getIntExtra(GlanceProtocolService.EXTRA_HAND_ARISE, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Get Hand Arise: " + value);
                    updateUartText("Device Response: Get Hand Arise: " + value);
                } else {
                    Log.d(TAG, "Device Response: Get Hand Arise: Fail");
                    updateUartText("Device Response: Get Hand Arise: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_SET_STEP_GOAL_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_STEP_GOAL_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Set Step Goal: Success");
                    updateUartText("Device Response: Set Step Goal: Success");
                } else {
                    Log.d(TAG, "Device Response: Set Step Goal: Fail");
                    updateUartText("Device Response: Set Step Goal: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_GET_STEP_GOAL_RESPONSE)) {
                int value = intent.getIntExtra(GlanceProtocolService.EXTRA_STEP_GOAL, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Get Step Goal: " + value);
                    updateUartText("Device Response: Get Step Goal: " + value);
                } else {
                    Log.d(TAG, "Device Response: Get Step Goal: Fail");
                    updateUartText("Device Response: Get Step Goal: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_GET_FIRMWARE_ID_RESPONSE)) {
                GlanceStatus.ByteArrayResult value  = (GlanceStatus.ByteArrayResult)intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_FIRMWARE_ID_RESULT);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get Firmware ID: " + value.getString());
                    updateUartText("Device Response: Get Firmware ID:\r\n" + value.getString());
                }
                else
                {
                    Log.d(TAG, "Device Response: Get Firmware ID: Fail");
                    updateUartText("Device Response: Get Firmware ID: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_GET_PRORUN_AUTO_DETECT_ON_RESPONSE)) {
                int value = intent.getIntExtra(GlanceProtocolService.EXTRA_GET_PRORUN_AUTO_DETECT_ON_RESULT, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Get Pro Run Auto Detect: " + value);
                    updateUartText("Device Response: Get Pro Run Auto Detect: " + value);
                } else {
                    Log.d(TAG, "Device Response: Get Pro Run Auto Detect: Fail");
                    updateUartText("Device Response: Get Pro Run Auto Detect: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_SET_PRORUN_AUTO_DETECT_ON_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_PRORUN_AUTO_DETECT_ON_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Set Pro Run Auto Detect: Success");
                    updateUartText("Device Response: Set Pro Run Auto Detect: Success");
                } else {
                    Log.d(TAG, "Device Response: Set Pro Run Auto Detect: Fail");
                    updateUartText("Device Response: Set Pro Run Auto Detect: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_SET_ACTIVITY_GOAL_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_ACTIVITY_GOAL_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Set Activity Goal: Success");
                    updateUartText("Device Response: Set Activity Goal: Success");
                } else {
                    Log.d(TAG, "Device Response: Set Activity Goal: Fail");
                    updateUartText("Device Response: Set Activity Goal: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_GET_ACTIVITY_GOAL_RESPONSE)) {
                int [] integerArray = (int []) intent.getSerializableExtra(GlanceProtocolService.EXTRA_ACTIVITY_GOAL);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Get Activity Goal: " + integerArray[0] + "," + integerArray[1]);
                    updateUartText("Device Response: Get Activity Goal: " + integerArray[0] + "," + integerArray[1]);
                } else {
                    Log.d(TAG, "Device Response: Get Activity Goal: Fail");
                    updateUartText("Device Response: Get Activity Goal: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_SET_MAIN_GOAL_RESPONSE)) {
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_MAIN_GOAL_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Set Main Goal: Success");
                    updateUartText("Device Response: Set Main Goal: Success");
                } else {
                    Log.d(TAG, "Device Response: Set Main Goal: Fail");
                    updateUartText("Device Response: Set Main Goal: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_GET_MAIN_GOAL_RESPONSE)) {
                int value = intent.getIntExtra(GlanceProtocolService.EXTRA_MAIN_GOAL, -1);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Get Main Goal: " + value);
                    updateUartText("Device Response: Get Main Goal: " + value);
                } else {
                    Log.d(TAG, "Device Response: Get Main Goal: Fail");
                    updateUartText("Device Response: Get Main Goal: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_GET_DEBUG_LOGDATA_RESPONSE)) {
                GlanceStatus.DebugLogData debuglogData = (GlanceStatus.DebugLogData) intent.getSerializableExtra(GlanceProtocolService.EXTRA_DEBUG_LOG_DATA);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (debuglogData != null && success == GlanceStatus.ErrorResponse.ERROR_NONE) {
                    Log.d(TAG, "Device Response: Get Debug Log Data: Success");

                    //Get ProRun Walk LOG data
                    ArrayList<GlanceStatus.DebugData> debugDataArrayList = debuglogData.getDebugData();
                    String str = "";
                    String logfilename = "";
                    str += "\r\nDebug Data\r\n";
                    str += "Build Number, Error Code, Line Number, Filename\r\n";
                    if (debugDataArrayList.size() == 0)
                    {
                        updateUartText("No Debug Data!");
                    }
                    else if (debugDataArrayList.size() < 100) {
                        for (int ii = 0; ii < debugDataArrayList.size(); ii++) {
                            GlanceStatus.DebugData debugData = debugDataArrayList.get(ii);
                            str += String.format("%04d", ii) + ": " + debugData.getBuildNumber()
                                    + ", " + debugData.getErrorCode()
                                    + ", " + debugData.getLineNumber()
                                    + ", " + debugData.getFileName()
                                    + ", " + debugData.getBootupTime()
                                    + "\r\n";

                        }
                        //Write  data into internal storage
                        logfilename = createDebugCsv(debugDataArrayList);
                    }
                    else
                    {
                        logfilename = createDebugCsv(debugDataArrayList);
                    }
                    if (logfilename.length() > 0) {
                        updateUartText("Saved Sleep CSV file saved into " + logfilename);
                    }
                    else if (debugDataArrayList.size() > 0)
                    {
                        updateUartText("Fail to saved Debug Log CSV file");
                    }
                }
                else
                {
                    Log.d(TAG, "Device Response: Get Debug Log Data: Fail");
                    updateUartText("Device Response: Get Debug Log Data: Fail");
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_GET_DEBUG_LOGDATA_PROGRESS_RESPONSE)) {
                int progress = intent.getIntExtra(GlanceProtocolService.EXTRA_DEBUG_LOG_PROGRESS_DATA, 0);
                Log.d(TAG, "Device Response: Get Debug Log Data  progress=" + progress);
                updateUartText("progress:" + progress, false);
            } else if (action.equals(GlanceProtocolService.GLANCE_CLEAR_DEBUG_LOG_DATA_RESPONSE)) {
                Log.d(TAG, "Device Response: Clear Debug Log Data");
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_CLEAR_DEBUG_LOG_DATA_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Clear Debug Log Data: Success");
                    updateUartText("Device Response: Clear Debug Log Data: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Clear Debug Log Data: Fail");
                    updateUartText("Device Response: Clear Debug Log Data: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_GET_MPU_MODE_RESPONSE)) {
                int result = intent.getIntExtra(GlanceProtocolService.EXTRA_GET_MPU_MODE_RESULT, 0);
                String [] mpu_mode = new String [] {
                        "0 = Pedo mode",
                        "1 = Sport mode",
                        "2 = Magnet 9x mode",
                        "3 = Sleep mode",
                        "4 = PreProrun mode",
                        "5 = Prorun mode",
                        "6 = Stream 32Hz",
                        "7 = Stream 250Hz",
                        "8 = Stream 125Hz"
                };
                Log.d(TAG, "Device Response: Get MPU Mode:" + ((result >= 0 && result <= 8) ? mpu_mode[result] : result));
                updateUartText("Device Response: Get MPU Mode:" + ((result >= 0 && result <= 8) ? mpu_mode[result] : result));
            } else if (action.equals(GlanceProtocolService.GLANCE_SET_MPU_MODE_RESPONSE)) {
                Log.d(TAG, "Device Response: Set MPU Mode");
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_MPU_MODE_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Set MPU Mode: Success");
                    updateUartText("Device Response: Set MPU Mode: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set MPU Mode: Fail");
                    updateUartText("Device Response: Set MPU Mode: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_GET_SPI_WHOAMI_RESPONSE)) {
                int result = intent.getIntExtra(GlanceProtocolService.EXTRA_GET_SPI_WHOAMI_RESULT, 0);
                Log.d(TAG, "Device Response: Get SPI Whoami:" + result);
                updateUartText("Device Response: Get SPI Whoami:" + result);
            } else if (action.equals(GlanceProtocolService.GLANCE_GET_USER_DATA_1_ALARM_RESPONSE)) {
                GlanceStatus.AlarmResult alarmResult = (GlanceStatus.AlarmResult) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_USER_DATA_1_ALARM_RESULT);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get User Data Alarm: Success");
                    updateUartText("Device Response: Get User Data Alarm: Success");
                    if (alarmResult != null) {
                        String log = "1: " + alarmResult.getAlarm1().getHour() + ":" + alarmResult.getAlarm1().getMin() +":" + alarmResult.getAlarm1().getSecond() + " " + (alarmResult.getAlarm1().getEnable() ? "Enable" : "Disable");
                        log += "\r\n2: " + alarmResult.getAlarm2().getHour() + ":" + alarmResult.getAlarm2().getMin() +":" + alarmResult.getAlarm2().getSecond() + " " + (alarmResult.getAlarm2().getEnable() ? "Enable" : "Disable");
                        log += "\r\n3: " + alarmResult.getAlarm3().getHour() + ":" + alarmResult.getAlarm3().getMin() +":" + alarmResult.getAlarm3().getSecond() + " " + (alarmResult.getAlarm3().getEnable() ? "Enable" : "Disable");
                        log += "\r\n4: " + alarmResult.getAlarm4().getHour() + ":" + alarmResult.getAlarm4().getMin() +":" + alarmResult.getAlarm4().getSecond() + " " + (alarmResult.getAlarm4().getEnable() ? "Enable" : "Disable");
                        updateUartText(log);
                    }
                }
                else
                {
                    Log.d(TAG, "Device Response: Get User Data Alarm: Fail");
                    updateUartText("Device Response: Get User Data Alarm: Fail, Error Code: " + success);
                }

            } else if (action.equals(GlanceProtocolService.GLANCE_SET_USER_DATA_1_ALARM_RESPONSE)) {
                Log.d(TAG, "Device Response: Set User Data Alarm");
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_USER_DATA_1_ALARM_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Set User Data Alarm: Success");
                    updateUartText("Device Response: Set User Data Alarm: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set User Data Alarm: Fail");
                    updateUartText("Device Response: Set User Data Alarm: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_GET_USER_DATA_2_PROFILE_RESPONSE)) {
                GlanceStatus.UserData2Profile userData2Profile = (GlanceStatus.UserData2Profile) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_USER_DATA_2_PROFILE_RESULT);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get User Data Profile: Success");
                    updateUartText("Device Response: Get User Data Profile: Success");
                    if (userData2Profile != null) {
                        String log = "Weight:" + userData2Profile.getWeight();
                        log += "\r\nHeight:" + userData2Profile.getHeight();
                        log += "\r\nAge:" + userData2Profile.getAge();
                        log += "\r\nGender:" + userData2Profile.getGender();
                        log += "\r\nUnit:" + userData2Profile.getUnit();
                        log += "\r\nTime Format:" + userData2Profile.getTimeFormat();
                        log += "\r\nView Watch:" + userData2Profile.getViewWatch();
                        log += "\r\nOled Brightness:" + userData2Profile.getOledBrightness();
                        log += "\r\nAuto Detect ProRun:" + userData2Profile.getAutoDetectProrun();
                        log += "\r\nMain Goal:" + userData2Profile.getMainGoal();
                        log += "\r\nCalories Goal:" + userData2Profile.getCaloriesGoal();
                        log += "\r\nStep Goal:" + userData2Profile.getStepGoal();
                        updateUartText(log);
                    }
                }
                else
                {
                    Log.d(TAG, "Device Response: Get User Data Profile: Fail");
                    updateUartText("Device Response: Get User Data Profile: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_SET_USER_DATA_2_PROFILE_RESPONSE)) {
                Log.d(TAG, "Device Response: Set User Data Profile");
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_USER_DATA_2_PROFILE_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Set User Data Profile: Success");
                    updateUartText("Device Response: Set User Data Profile: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set User Data Profile: Fail");
                    updateUartText("Device Response: Set User Data Profile: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_GET_USER_DATA_3_GOAL_RESPONSE)) {
                GlanceStatus.UserData3Goal userData3Goal = (GlanceStatus.UserData3Goal) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_USER_DATA_3_GOAL_RESULT);
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_GET_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Get User Data Goal: Success");
                    updateUartText("Device Response: Get User Data Goal: Success");
                    if (userData3Goal != null) {
                        String log = "Walk Distance Goal:" + userData3Goal.getWalkDistanceGoal();
                        log += "\r\nWalk Duration Goal:" + userData3Goal.getWalkDurationGoal();
                        log += "\r\nRun Distance Goal:" + userData3Goal.getRunDistanceGoal();
                        log += "\r\nRun Duration Goal:" + userData3Goal.getRunDurationGoal();
                        updateUartText(log);
                    }
                }
                else
                {
                    Log.d(TAG, "Device Response: Get User Data Goal: Fail");
                    updateUartText("Device Response: Get User Data Goal: Fail, Error Code: " + success);
                }
            } else if (action.equals(GlanceProtocolService.GLANCE_SET_USER_DATA_3_GOAL_RESPONSE)) {
                Log.d(TAG, "Device Response: Set User Data Goal");
                GlanceStatus.ErrorResponse success = (GlanceStatus.ErrorResponse) intent.getSerializableExtra(GlanceProtocolService.EXTRA_SET_USER_DATA_3_GOAL_RESULT);
                if (success == GlanceStatus.ErrorResponse.ERROR_NONE)
                {
                    Log.d(TAG, "Device Response: Set User Data Goal: Success");
                    updateUartText("Device Response: Set User Data Goal: Success");
                }
                else
                {
                    Log.d(TAG, "Device Response: Set User Data Goal: Fail");
                    updateUartText("Device Response: Set User Data Goal: Fail, Error Code: " + success);
                }
            }
        }
    };

    private int getButtonString(int index)
    {
        if (index == COMMAND_LIST.SET_CONNECTION_INTERVAL.ordinal()) {
            return R.string.str_set_connection_interval;
        } else if (index == COMMAND_LIST.GET_CONNECTION__INTERVAL.ordinal()) {
            return R.string.str_get_connection_interval;
        } else if (index == COMMAND_LIST.SET_AUTOWALK_INTERVAL.ordinal()) {
                return R.string.str_set_autowalk_interval;
        } else if (index == COMMAND_LIST.GET_AUTOWALK_INTERVAL.ordinal()) {
            return R.string.str_get_autowalk_interval;
        } else if (index == COMMAND_LIST.SET_BROWNOUT_INTERVAL.ordinal()) {
            return R.string.str_set_brownout_interval;
        } else if (index == COMMAND_LIST.GET_BROWNOUT_INTERVAL.ordinal()) {
            return R.string.str_get_brownout_interval;
        } else if (index == COMMAND_LIST.GET_FW_VERSION.ordinal()) {
            return R.string.str_get_fw_version;
        } else if (index == COMMAND_LIST.FACTORY_RESET.ordinal()) {
            return R.string.str_factory_reset;
        } else if (index == COMMAND_LIST.DFU_MODE.ordinal()) {
            return R.string.str_dfu_mode;
        } else if (index == COMMAND_LIST.GET_LOG_DATA.ordinal()) {
            return R.string.str_get_log_data;
        } else if (index == COMMAND_LIST.ACK_LOG_WITHOUT_TIMESTAMP.ordinal()) {
            return R.string.str_ack_log_without_timestamp;
        } else if (index == COMMAND_LIST.GET_SLEEP_LOG_DATA.ordinal()) {
            return R.string.str_get_sleep_log_data;
        } else if (index == COMMAND_LIST.ACK_SLEEP_LOG.ordinal()) {
            return R.string.str_ack_sleep_log;
//        } else if (index == COMMAND_LIST.GET_BLE_ID.ordinal()) {
//            return R.string.str_get_ble_id;
        } else if (index == COMMAND_LIST.SHIPMENT_MODE.ordinal()) {
            return R.string.str_shipment_mode;
//        } else if (index == COMMAND_LIST.SET_12H_24H_FORMAT.ordinal()) {
//            return mIsTimeFormat24h ? R.string.str_12h : R.string.str_24h;
//        } else if (index == COMMAND_LIST.GET_12H_24H_FORMAT.ordinal()) {
//            return R.string.str_get24H_Format;
//        } else if (index == COMMAND_LIST.SET_UNIT_OF_MEASURE.ordinal()) {
//            return R.string.str_set_unit_of_measure;
//        } else if (index == COMMAND_LIST.GET_UNIT_OF_MEASURE.ordinal()) {
//            return R.string.str_get_unit_of_measure;
//        } else if (index == COMMAND_LIST.SET_OLED_INTENSITY.ordinal()) {
//            return R.string.str_set_oled_intensity;
//        } else if (index == COMMAND_LIST.GET_OLED_INTENSITY.ordinal()) {
//            return R.string.str_get_oled_intensity;
//        } else if (index == COMMAND_LIST.SET_LOG_INTERVAL.ordinal()) {
//            return R.string.str_set_log_interval;
//        } else if (index == COMMAND_LIST.GET_LOG_INTERVAL.ordinal()) {
//            return R.string.str_get_log_interval;
        } else if (index == COMMAND_LIST.SET_POWER_MODE.ordinal()) {
            return R.string.str_set_power_mode;
        } else if (index == COMMAND_LIST.GET_POWER_MODE.ordinal()) {
            return R.string.str_get_power_mode;
//        } else if (index == COMMAND_LIST.SET_GOAL.ordinal()) {
//            return R.string.str_set_goal;
//        } else if (index == COMMAND_LIST.GET_GOAL.ordinal()) {
//            return R.string.str_get_goal;
//        } else if (index == COMMAND_LIST.SET_WEIGHT.ordinal()) {
//            return R.string.str_set_weight;
//        } else if (index == COMMAND_LIST.GET_WEIGHT.ordinal()) {
//            return R.string.str_get_weight;
//        } else if (index == COMMAND_LIST.SET_HEIGHT.ordinal()) {
//            return R.string.str_set_height;
//        } else if (index == COMMAND_LIST.GET_HEIGHT.ordinal()) {
//            return R.string.str_get_height;
        } else if (index == COMMAND_LIST.SET_DATETIME.ordinal()) {
            return R.string.str_set_datetime;
        } else if (index == COMMAND_LIST.GET_DATETIME.ordinal()) {
            return R.string.str_get_datetime;
//        } else if (index == COMMAND_LIST.SET_DEVICE_NAME.ordinal()) {
//            return R.string.str_set_device_name;
//        } else if (index == COMMAND_LIST.GET_DEVICE_NAME.ordinal()) {
//            return R.string.str_get_device_name;
        } else if (index == COMMAND_LIST.ACK_LOG.ordinal()) {
            return R.string.str_ack_log;
        } else if (index == COMMAND_LIST.GET_STEP_COUNT.ordinal()) {
            return R.string.str_get_step_count;
        } else if (index == COMMAND_LIST.GET_TXPOWER.ordinal()) {
            return R.string.str_get_txpower;
        } else if (index == COMMAND_LIST.GET_LIBRARY_VERSION.ordinal()) {
            return R.string.str_get_library_version;
        } else if (index == COMMAND_LIST.GET_PSTORAGE_LOG_INFO.ordinal()) {
            return R.string.str_get_pstorage_log_info;
//        } else if (index == COMMAND_LIST.GET_VOLTAGE_LOG.ordinal()) {
//            return R.string.str_get_voltage_log;
        } else if (index == COMMAND_LIST.GET_FINISH_CALIBRATION.ordinal()) {
            return R.string.str_finish_calibration;
        } else if (index == COMMAND_LIST.GET_CALIBRATE_FACE_N.ordinal()) {
            return R.string.str_calibrate_face_n;
        } else if (index == COMMAND_LIST.GET_GET_CALIBRATION.ordinal()) {
            return R.string.str_get_calibration;
        } else if (index == COMMAND_LIST.GET_PSTOR_VALUE.ordinal()) {
            return R.string.str_get_pstor_value;
        } else if (index == COMMAND_LIST.SET_BOOTUP_MODE.ordinal()) {
            return R.string.str_set_bootup_mode;
//        } else if (index == COMMAND_LIST.SET_CLUB_HEAD_SPEED.ordinal()) {
//            return R.string.str_set_club_head_speed;
        } else if (index == COMMAND_LIST.SET_SECONDARY_DISPLAY_ASCII_STRING.ordinal()) {
            return R.string.str_set_secondary_display_ascii_string;
        } else if (index == COMMAND_LIST.SET_SECONDARY_DISPLAY_CLUB_HEAD_SPEED.ordinal()) {
            return R.string.str_set_secondary_display_club_head_speed;
        } else if (index == COMMAND_LIST.SET_SECONDARY_DISPLAY_BITMAP.ordinal()) {
            return R.string.str_set_secondary_display_bitmap;
//        } else if (index == COMMAND_LIST.SET_ALARM.ordinal()) {
//            return R.string.str_set_alarm;
//        } else if (index == COMMAND_LIST.GET_ALARM.ordinal()) {
//            return R.string.str_get_alarm;
        } else if (index == COMMAND_LIST.SET_ISCALED.ordinal()) {
            return R.string.str_set_iscaled;
        } else if (index == COMMAND_LIST.RESET_COLD_BOOT_COUNT.ordinal()) {
            return R.string.str_reset_cold_boot_count;
        } else if (index == COMMAND_LIST.GET_COLD_BOOT_COUNT.ordinal()) {
            return R.string.str_get_cold_boot_count;
        } else if (index == COMMAND_LIST.GET_PROTOCOL_VER.ordinal()) {
            return R.string.str_get_protocol_version;
        } else if (index == COMMAND_LIST.REQ_PROTOCOL_VER.ordinal()) {
            return R.string.str_req_protocol_version;
        } else if (index == COMMAND_LIST.SEND_DUMMY_CMD.ordinal()) {
            return R.string.str_send_dummy_command;
        } else if (index == COMMAND_LIST.GET_PRORUN_LOG_DATA.ordinal()) {
            return R.string.str_get_prorun_log_data;
        } else if (index == COMMAND_LIST.ACK_PRORUN_LOG.ordinal()) {
            return R.string.str_ack_prorun_log;
        } else if (index == COMMAND_LIST.SET_APP_CONNECT_FINISH_ON.ordinal()) {
            return R.string.str_app_connect_finish_on;
        } else if (index == COMMAND_LIST.SET_APP_CONNECT_FINISH_OFF.ordinal()) {
            return R.string.str_app_connect_finish_off;
//        } else if (index == COMMAND_LIST.SET_AGE.ordinal()) {
//            return R.string.str_set_age;
//        } else if (index == COMMAND_LIST.GET_AGE.ordinal()) {
//            return R.string.str_get_age;
//        } else if (index == COMMAND_LIST.SET_GENDER.ordinal()) {
//            return R.string.str_set_gender;
//        } else if (index == COMMAND_LIST.GET_GENDER.ordinal()) {
//            return R.string.str_get_gender;
//        } else if (index == COMMAND_LIST.SET_HAND_ARISE.ordinal()) {
//            return R.string.str_set_hand_arise;
//        } else if (index == COMMAND_LIST.GET_HAND_ARISE.ordinal()) {
//            return R.string.str_get_hand_arise;
//        } else if (index == COMMAND_LIST.SET_STEP_GOAL.ordinal()) {
//            return R.string.str_set_step_goal;
//        } else if (index == COMMAND_LIST.GET_STEP_GOAL.ordinal()) {
//            return R.string.str_get_step_goal;
        } else if (index == COMMAND_LIST.GET_FIRMWARE_ID.ordinal()) {
            return R.string.str_get_firmware_id;
//        } else if (index == COMMAND_LIST.SET_PRORUN_AUTO_DETECT_ON.ordinal()) {
//            return R.string.str_set_prorun_auto_detect_on;
//        } else if (index == COMMAND_LIST.GET_PRORUN_AUTO_DETECT_ON.ordinal()) {
//            return R.string.str_get_prorun_auto_detect_on;
//        } else if (index == COMMAND_LIST.SET_ACTIVITY_GOAL.ordinal()) {
//            return R.string.str_set_activity_goal;
//        } else if (index == COMMAND_LIST.GET_ACTIVITY_GOAL.ordinal()) {
//            return R.string.str_get_activity_goal;
//        } else if (index == COMMAND_LIST.SET_MAIN_GOAL.ordinal()) {
//            return R.string.str_set_main_goal;
//        } else if (index == COMMAND_LIST.GET_MAIN_GOAL.ordinal()) {
//            return R.string.str_get_main_goal;
        } else if (index == COMMAND_LIST.GET_DEBUG_LOG.ordinal()) {
            return R.string.str_get_debug_log;
        } else if (index == COMMAND_LIST.CLEAR_DEBUG_LOG.ordinal()) {
            return R.string.str_clear_debug_log;
        } else if (index == COMMAND_LIST.SET_MPU_MODE.ordinal()) {
            return R.string.str_set_mpu_mode;
        } else if (index == COMMAND_LIST.GET_MPU_MODE.ordinal()) {
            return R.string.str_get_mpu_mode;
        } else if (index == COMMAND_LIST.GET_SPI_WHOAMI.ordinal()) {
            return R.string.str_get_spi_whoami;
        } else if (index == COMMAND_LIST.SET_USER_DATA_ALARM.ordinal()) {
            return R.string.str_set_user_data_alarm;
        } else if (index == COMMAND_LIST.GET_USER_DATA_ALARM.ordinal()) {
            return R.string.str_get_user_data_alarm;
        } else if (index == COMMAND_LIST.SET_USER_DATA_PROFILE.ordinal()) {
            return R.string.str_set_user_data_profile;
        } else if (index == COMMAND_LIST.GET_USER_DATA_PROFILE.ordinal()) {
            return R.string.str_get_user_data_profile;
        } else if (index == COMMAND_LIST.SET_USER_DATA_GOAL.ordinal()) {
            return R.string.str_set_user_data_goal;
        } else if (index == COMMAND_LIST.GET_USER_DATA_GOAL.ordinal()) {
            return R.string.str_get_user_data_goal;
        }
        return R.string.str_get_fw_version;
    }

    private View.OnClickListener getOnClickListenerByIndex(int index)
    {
        if (index == COMMAND_LIST.SET_CONNECTION_INTERVAL.ordinal()) {
            return mSetConnectionIntervalListener;
        } else if (index == COMMAND_LIST.GET_CONNECTION__INTERVAL.ordinal()) {
            return mGetConnectionIntervalListener;
        } else if (index == COMMAND_LIST.SET_AUTOWALK_INTERVAL.ordinal()) {
            return mSetAutoWalkListener;
        } else if (index == COMMAND_LIST.GET_AUTOWALK_INTERVAL.ordinal()) {
            return mGetAutoWalkListener;
        } else if (index == COMMAND_LIST.SET_BROWNOUT_INTERVAL.ordinal()) {
            return mSetBrownOutListener;
        } else if (index == COMMAND_LIST.GET_BROWNOUT_INTERVAL.ordinal()) {
            return mGetBrownOutListener;
        } else if (index == COMMAND_LIST.GET_FW_VERSION.ordinal()) {
            return mGetVersionListener;
        } else if (index == COMMAND_LIST.FACTORY_RESET.ordinal()) {
            return mFactoryResetListener;
        } else if (index == COMMAND_LIST.DFU_MODE.ordinal()) {
            return mDFUModeListener;
        } else if (index == COMMAND_LIST.GET_LOG_DATA.ordinal()) {
            return mGetLOGDataListener;
        } else if (index == COMMAND_LIST.ACK_LOG_WITHOUT_TIMESTAMP.ordinal()) {
            return mAckGetLogDataWithoutTimestampListener;
        } else if (index == COMMAND_LIST.GET_SLEEP_LOG_DATA.ordinal()) {
            return mGetSleepLogDataListener;
        } else if (index == COMMAND_LIST.ACK_SLEEP_LOG.ordinal()) {
            return mAckGetSleepLogDataListener;
//        } else if (index == COMMAND_LIST.GET_BLE_ID.ordinal()) {
//            return mGetBLEIDListener;
        } else if (index == COMMAND_LIST.SHIPMENT_MODE.ordinal()) {
            return mShipmentModeListener;
//        } else if (index == COMMAND_LIST.SET_12H_24H_FORMAT.ordinal()) {
//            return mTimeFormatListener;
//        } else if (index == COMMAND_LIST.GET_12H_24H_FORMAT.ordinal()) {
//            return mGetTimeFormatListener;
//        } else if (index == COMMAND_LIST.SET_UNIT_OF_MEASURE.ordinal()) {
//            return mSetUnitOfMeasureListener;
//        } else if (index == COMMAND_LIST.GET_UNIT_OF_MEASURE.ordinal()) {
//            return mGetUnitOfMeasureListener;
//        } else if (index == COMMAND_LIST.SET_OLED_INTENSITY.ordinal()) {
//            return mSetOLEDIntensityListener;
//        } else if (index == COMMAND_LIST.GET_OLED_INTENSITY.ordinal()) {
//            return mGetOLEDIntensityListener;
//        } else if (index == COMMAND_LIST.SET_LOG_INTERVAL.ordinal()) {
//            return mSetLogIntervalListener;
//        } else if (index == COMMAND_LIST.GET_LOG_INTERVAL.ordinal()) {
//            return mGetLogIntervalListener;
        } else if (index == COMMAND_LIST.SET_POWER_MODE.ordinal()) {
            return mSetPowerModeListener;
        } else if (index == COMMAND_LIST.GET_POWER_MODE.ordinal()) {
            return mGetPowerModeListener;
//        } else if (index == COMMAND_LIST.SET_GOAL.ordinal()) {
//            return mSetGoalListener;
//        } else if (index == COMMAND_LIST.GET_GOAL.ordinal()) {
//            return mGetGoalListener;
//        } else if (index == COMMAND_LIST.SET_WEIGHT.ordinal()) {
//            return mSetWeightListener;
//        } else if (index == COMMAND_LIST.GET_WEIGHT.ordinal()) {
//            return mGetWeightListener;
//        } else if (index == COMMAND_LIST.SET_HEIGHT.ordinal()) {
//            return mSetHeightListener;
//        } else if (index == COMMAND_LIST.GET_HEIGHT.ordinal()) {
//            return mGetHeightListener;
        } else if (index == COMMAND_LIST.SET_DATETIME.ordinal()) {
            return mSetDateTimeListener;
        } else if (index == COMMAND_LIST.GET_DATETIME.ordinal()) {
            return mGetDateTimeListener;
//        } else if (index == COMMAND_LIST.SET_DEVICE_NAME.ordinal()) {
//            return mSetDeviceNameListener;
//        } else if (index == COMMAND_LIST.GET_DEVICE_NAME.ordinal()) {
//            return mGetDeviceNameListener;
        } else if (index == COMMAND_LIST.ACK_LOG.ordinal()) {
            return mAckGetLogDataListener;
        } else if (index == COMMAND_LIST.GET_STEP_COUNT.ordinal()) {
            return mGetStepCountListener;
        } else if (index == COMMAND_LIST.GET_TXPOWER.ordinal()) {
            return mGetTxPowerListener;
        } else if (index == COMMAND_LIST.GET_LIBRARY_VERSION.ordinal()) {
            return mGetLibraryVersionListener;
        } else if (index == COMMAND_LIST.GET_PSTORAGE_LOG_INFO.ordinal()) {
            return mAckGetPstorageLogInfoListener;
//        } else if (index == COMMAND_LIST.GET_VOLTAGE_LOG.ordinal()) {
//            return mGetVoltageLogListener;
        } else if (index == COMMAND_LIST.GET_FINISH_CALIBRATION.ordinal()) {
            return mFinishCalibrationListener;
        } else if (index == COMMAND_LIST.GET_CALIBRATE_FACE_N.ordinal()) {
            return mCalibrateFaceNListener;
        } else if (index == COMMAND_LIST.GET_GET_CALIBRATION.ordinal()) {
            return mGetCalibrationListener;
        } else if (index == COMMAND_LIST.GET_PSTOR_VALUE.ordinal()) {
            return mGetPstorValueListener;
        } else if (index == COMMAND_LIST.SET_BOOTUP_MODE.ordinal()) {
            return mSetBootupModeListener;
//        } else if (index == COMMAND_LIST.SET_CLUB_HEAD_SPEED.ordinal()) {
//            return mSetClubHeadSpeedListener;
        } else if (index == COMMAND_LIST.SET_SECONDARY_DISPLAY_ASCII_STRING.ordinal()) {
            return mSetSecondaryDisplayAsciiStringListener;
        } else if (index == COMMAND_LIST.SET_SECONDARY_DISPLAY_CLUB_HEAD_SPEED.ordinal()) {
            return mSetSecondaryDisplayClubHeadSpeedListener;
        } else if (index == COMMAND_LIST.SET_SECONDARY_DISPLAY_BITMAP.ordinal()) {
            return mSetSecondaryDisplayBitmapListener;
//        } else if (index == COMMAND_LIST.SET_ALARM.ordinal()) {
//            return mSetAlarmListener;
//        } else if (index == COMMAND_LIST.GET_ALARM.ordinal()) {
//            return mGetAlarmListener;
        } else if (index == COMMAND_LIST.RESET_COLD_BOOT_COUNT.ordinal()) {
            return mResetColdBootCountListener;
        } else if (index == COMMAND_LIST.GET_COLD_BOOT_COUNT.ordinal()) {
            return mGetColdBootCountListener;
        } else if (index == COMMAND_LIST.SET_ISCALED.ordinal()) {
            return mSetIsCaledListener;
        } else if (index == COMMAND_LIST.GET_PROTOCOL_VER.ordinal()) {
            return mGetProtocolVersionListener;
        } else if (index == COMMAND_LIST.REQ_PROTOCOL_VER.ordinal()) {
            return mRequestProtocolVersionListener;
        } else if (index == COMMAND_LIST.SEND_DUMMY_CMD.ordinal()) {
            return mSendDummyCommandListener;
        } else if (index == COMMAND_LIST.GET_PRORUN_LOG_DATA.ordinal()) {
            return mGetProRunLogDataListener;
        } else if (index == COMMAND_LIST.ACK_PRORUN_LOG.ordinal()) {
            return mAckGetProRunLogDataListener;
        } else if (index == COMMAND_LIST.SET_APP_CONNECT_FINISH_ON.ordinal()) {
            return mSetAppConnectFinsihOnListener;
        } else if (index == COMMAND_LIST.SET_APP_CONNECT_FINISH_OFF.ordinal()) {
            return mSetAppConnectFinsihOffListener;
//        } else if (index == COMMAND_LIST.SET_AGE.ordinal()) {
//            return mSetAgeListener;
//        } else if (index == COMMAND_LIST.GET_AGE.ordinal()) {
//            return mGetAgeListener;
//        } else if (index == COMMAND_LIST.SET_GENDER.ordinal()) {
//            return mSetGenderListener;
//        } else if (index == COMMAND_LIST.GET_GENDER.ordinal()) {
//            return mGetGenderListener;
//        } else if (index == COMMAND_LIST.SET_HAND_ARISE.ordinal()) {
//            return mSetHandAriseListener;
//        } else if (index == COMMAND_LIST.GET_HAND_ARISE.ordinal()) {
//            return mGetHandAriseListener;
//        } else if (index == COMMAND_LIST.SET_STEP_GOAL.ordinal()) {
//            return mSetStepGoalListener;
//        } else if (index == COMMAND_LIST.GET_STEP_GOAL.ordinal()) {
//            return mGetStepGoalListener;
        } else if (index == COMMAND_LIST.GET_FIRMWARE_ID.ordinal()) {
            return mGetFirmwareIdListener;
//        } else if (index == COMMAND_LIST.SET_PRORUN_AUTO_DETECT_ON.ordinal()) {
//            return mSetProRunAutoDetectOnListener;
//        } else if (index == COMMAND_LIST.GET_PRORUN_AUTO_DETECT_ON.ordinal()) {
//            return mGetProRunAutoDetectOnListener;
//        } else if (index == COMMAND_LIST.SET_ACTIVITY_GOAL.ordinal()) {
//            return mSetActivityGoalListener;
//        } else if (index == COMMAND_LIST.GET_ACTIVITY_GOAL.ordinal()) {
//            return mGetActivityGoalListener;
//        } else if (index == COMMAND_LIST.SET_MAIN_GOAL.ordinal()) {
//            return mSetMainGoalListener;
//        } else if (index == COMMAND_LIST.GET_MAIN_GOAL.ordinal()) {
//            return mGetMainGoalListener;
        } else if (index == COMMAND_LIST.GET_DEBUG_LOG.ordinal()) {
            return mGetDebugLogListener;
        } else if (index == COMMAND_LIST.CLEAR_DEBUG_LOG.ordinal()) {
            return mClearDebugLogListener;
        } else if (index == COMMAND_LIST.SET_MPU_MODE.ordinal()) {
            return mSetMPUModeListener;
        } else if (index == COMMAND_LIST.GET_MPU_MODE.ordinal()) {
            return mGetMPUModeListener;
        } else if (index == COMMAND_LIST.GET_SPI_WHOAMI.ordinal()) {
            return mGetSPIWhoamiListener;
        } else if (index == COMMAND_LIST.SET_USER_DATA_ALARM.ordinal()) {
            return mSetUserDataAlarmListener;
        } else if (index == COMMAND_LIST.GET_USER_DATA_ALARM.ordinal()) {
            return mGetUserDataAlarmListener;
        } else if (index == COMMAND_LIST.SET_USER_DATA_PROFILE.ordinal()) {
            return mSetUserDataProfileListener;
        } else if (index == COMMAND_LIST.GET_USER_DATA_PROFILE.ordinal()) {
            return mGetUserDataProfileListener;
        } else if (index == COMMAND_LIST.SET_USER_DATA_GOAL.ordinal()) {
            return mSetUserDataGoalListener;
        } else if (index == COMMAND_LIST.GET_USER_DATA_GOAL.ordinal()) {
            return mGetUserDataGoalListener;
        }
        return mGetVersionListener;
    }

    private void updateUartText(String str)
    {
        updateUartText(str, true);
    }

    private void updateUartText(String str, Boolean isAppend)
    {
        mUARTResultTextView.setText((isAppend ? (mUARTResultTextView.getText() + "\r\n") : "") + str);
        final int scrollAmount = mUARTResultTextView.getLayout().getLineTop(mUARTResultTextView.getLineCount()) - mUARTResultTextView.getHeight();
        // if there is no need to scroll, scrollAmount will be <=0
        if (scrollAmount > 0)
            mUARTResultTextView.scrollTo(0, scrollAmount);
        else
            mUARTResultTextView.scrollTo(0, 0);
    }

    private String getResultDisplayName(int ret)
    {
        switch (ret)
        {
            case GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_NONE:
                return "Success";
            case GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_GENERAL:
                return "Fail, Error Code:" + Integer.toString(GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_GENERAL) + ", Error Description: GLANCE_APP_BLE_PROTOCOL_ERROR_GENERAL";
            case GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_DATA_RANGE:
                return "Fail, Error Code:" + Integer.toString(GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_DATA_RANGE) + ", Error Description: GLANCE_APP_BLE_PROTOCOL_ERROR_DATA_RANGE";
            case GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_ANOTHER_CMD_IN_PROGRESS:
                return "Fail, Error Code:" + Integer.toString(GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_ANOTHER_CMD_IN_PROGRESS) + ", Error Description: GLANCE_APP_BLE_PROTOCOL_ERROR_ANOTHER_CMD_IN_PROGRESS";
            case GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_TIMEOUT:
                return "Fail, Error Code:" + Integer.toString(GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_TIMEOUT) + ", Error Description: GLANCE_APP_BLE_PROTOCOL_ERROR_TIMEOUT";
            case GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_NO_SUCH_DEVICE_ADDRESS:
                return "Fail, Error Code:" + Integer.toString(GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_NO_SUCH_DEVICE_ADDRESS) + ", Error Description: GLANCE_APP_BLE_PROTOCOL_ERROR_NO_SUCH_DEVICE_ADDRESS";
            case GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_NULL_POINTER_EXCEPTION:
                return "Fail, Error Code:" + Integer.toString(GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_NULL_POINTER_EXCEPTION) + ", Error Description: GLANCE_APP_BLE_PROTOCOL_ERROR_NULL_POINTER_EXCEPTION";
            case GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_SEND_CMD_IS_NULL:
                return "Fail, Error Code:" + Integer.toString(GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_SEND_CMD_IS_NULL) + ", Error Description: GLANCE_APP_BLE_PROTOCOL_ERROR_SEND_CMD_IS_NULL";
            case GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_CMD_NOT_IMPLEMENTED_YET:
                return "Fail, Error Code:" + Integer.toString(GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_CMD_NOT_IMPLEMENTED_YET) + ", Error Description: GLANCE_APP_BLE_PROTOCOL_ERROR_CMD_NOT_IMPLEMENTED_YET";
            case GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_CMD_SEND_FAIL:
                return "Fail, Error Code:" + Integer.toString(GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_CMD_SEND_FAIL) + ", Error Description: GLANCE_APP_BLE_PROTOCOL_ERROR_CMD_SEND_FAIL";
            case GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_NO_ACK_TIMESTAMP:
                return "Fail, Error Code:" + Integer.toString(GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_NO_ACK_TIMESTAMP) + ", Error Description: GLANCE_APP_BLE_PROTOCOL_ERROR_NO_ACK_TIMESTAMP";
            case GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_DEVICE_IS_DISCONNECTING:
                return "Fail, Error Code:" + Integer.toString(GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_DEVICE_IS_DISCONNECTING) + ", Error Description: GLANCE_APP_BLE_PROTOCOL_ERROR_DEVICE_IS_DISCONNECTING";
            case GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_DEVICE_IS_DISCONNECTED:
                return "Fail, Error Code:" + Integer.toString(GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_DEVICE_IS_DISCONNECTED) + ", Error Description: GLANCE_APP_BLE_PROTOCOL_ERROR_DEVICE_IS_DISCONNECTED";
            case GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_PROTOCOL_NOT_UPDATED:
                return "Fail, Error Code:" + Integer.toString(GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_PROTOCOL_NOT_UPDATED) + ", Error Description: GLANCE_APP_BLE_PROTOCOL_ERROR_PROTOCOL_NOT_UPDATED";
            default:
                return "Fail, Error Code:" + Integer.toString(ret) + ", Error Unknown";
        }
    }

    private void printStatus(int ret, int cmd_string_id)
    {
        updateUartText("Send \"" + getResources().getString(cmd_string_id) + "\" API " + getResultDisplayName(ret));
    }

    Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case HANDLER_PROCESS_UART_DATA:
                    GlanceStatus.StreamType streamType = (GlanceStatus.StreamType) msg.getData().getSerializable("UartStreamType");
                    if (streamType == GlanceStatus.StreamType.RAW) {
                        GlanceMotionStreamRawData data = (GlanceMotionStreamRawData) msg.getData().getSerializable("UartRawData");
                        byte [] value = data.getRawData();
//                        Log.e(TAG, "HANDLER_PROCESS_UART_DATA size:" + value.length);
                        //mJumpHeight.processMotionData(mService.parserMotionData(value));
                        if (mGraphFragment != null && mService != null) {
                            mGraphFragment.processMotionData(mService.parserMotionData(value));
                        }
                    }
                default:
                    break;
            }
        }
    };


    private String printUTCTime(long time)
    {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //format.setTimeZone(TimeZone.getTimeZone("UTC"));
        format.setTimeZone(TimeZone.getDefault());
        return format.format(date);
    }

    public String createVoltageCsv(ArrayList<GlanceStatus.VoltageData> voltageDataArrayList)
    {
        String datapath = getCsvDirectory();
        if (datapath.length() == 0)
        {
            Log.e(TAG, "Fail to get CSV directory");
            return "";
        }
        Date currentDateTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String session = dateFormat.format(currentDateTime);
        String logfilename = String.format("%s/voltageLog_%s.csv", datapath, session);
        Log.d(TAG, "" + logfilename);
        File file = new File(logfilename);
        try {
            FileWriter writer = new FileWriter(file);
            writer.append("Log DateTime(month-day hour:min), ADC, Voltage(mV)\r\n");
            for (int ii = 0; ii < voltageDataArrayList.size(); ii++) {
                GlanceStatus.VoltageData voltageData = voltageDataArrayList.get(ii);
                writer.append(String.format("%02d-%02d %02d:00, %d, %d",
                        voltageData.getMonth(),
                        voltageData.getDay(),
                        voltageData.getHour(),
                        voltageData.getADC(),
                        voltageData.getMilliVoltage()) + "\r\n");
            }
            writer.close();
            return logfilename;
        }
        catch (Exception ex)
        {
            Log.e(TAG, "" + ex.toString());
        }
        return "";
    }

    public String createSleepCsv(ArrayList<GlanceStatus.SleepData> sleepDataArrayList) {
        String datapath = getCsvDirectory();
        if (datapath.length() == 0)
        {
            Log.e(TAG, "Fail to get CSV directory");
            return "";
        }
        Date currentDateTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String session = dateFormat.format(currentDateTime);
        String logfilename = String.format("%s/sleepLog_%s.csv", datapath, session);
        Log.d(TAG, "" + logfilename);
        File file = new File(logfilename);
        try {
            FileWriter writer = new FileWriter(file);
            writer.append("Start Time,End Time,Sleep Type,Motion Count,Score\r\n");
            for (int ii = 0; ii < sleepDataArrayList.size(); ii++) {
                GlanceStatus.SleepData sleepData = sleepDataArrayList.get(ii);
                String strSleepStatus = "";
                if (sleepData.getSleepStatus() == GlanceStatus.SleepStatus.RESTFUL) {
                    strSleepStatus = "Resful";
                } else if (sleepData.getSleepStatus() == GlanceStatus.SleepStatus.LIGHT) {
                    strSleepStatus = "Light";
                } else {
                    strSleepStatus = "Awake";
                }
                writer.append(printUTCTime(sleepData.getLogStartUTCTime()) + "," + printUTCTime(sleepData.getLogEndUTCTime()) + "," + strSleepStatus + "," + Long.toString(sleepData.getDebugAvgRms()) + "," + Long.toString(sleepData.getDebugMaxRms()) + "\r\n");
            }
            writer.close();
            return logfilename;
        }
        catch (Exception ex)
        {
            Log.e(TAG, "" + ex.toString());
        }
        return "";
    }

    public String createProRunCsv(ArrayList<GlanceStatus.ProRunData> proRunDataArrayList, GlanceStatus.ProRunSportType sportType) {
        String datapath = getCsvDirectory();
        if (datapath.length() == 0)
        {
            Log.e(TAG, "Fail to get CSV directory");
            return "";
        }
        Date currentDateTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String session = dateFormat.format(currentDateTime);
        String logfilename = String.format("%s/proRun%sLog_%s.csv", datapath, sportType.toString(), session);
        Log.d(TAG, "" + logfilename);
        File file = new File(logfilename);
        try {
            FileWriter writer = new FileWriter(file);
            writer.append("Start Time,End Time,Cadence,Step,Sum,AvgPeak,AvgValley\r\n");
            for (int ii = 0; ii < proRunDataArrayList.size(); ii++) {
                GlanceStatus.ProRunData proRunData = proRunDataArrayList.get(ii);
                writer.append(printUTCTime(proRunData.getLogStartUTCTime()) + "," + printUTCTime(proRunData.getLogEndUTCTime()) + "," + Long.toString(proRunData.getCadence()) + "," + Long.toString(proRunData.getStep()) + "," + Long.toString(proRunData.getSum()) + "," + Long.toString(proRunData.getAvgPeak()) + "," + Long.toString(proRunData.getAvgValley()) + "\r\n");
            }
            writer.close();
            return logfilename;
        }
        catch (Exception ex)
        {
            Log.e(TAG, "" + ex.toString());
        }
        return "";
    }

    public String createDebugCsv(ArrayList<GlanceStatus.DebugData> debugDataArrayList) {
        String datapath = getCsvDirectory();
        if (datapath.length() == 0)
        {
            Log.e(TAG, "Fail to get CSV directory");
            return "";
        }
        Date currentDateTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String session = dateFormat.format(currentDateTime);
        String logfilename = String.format("%s/debugLog_%s.csv", datapath, session);
        Log.d(TAG, "" + logfilename);
        File file = new File(logfilename);
        try {
            FileWriter writer = new FileWriter(file);
            writer.append("RcvTime,BuildNumber,ErrorCode,LineNumber,Filename,BootupTime\r\n");
            for (int ii = 0; ii < debugDataArrayList.size(); ii++) {
                GlanceStatus.DebugData debugData = debugDataArrayList.get(ii);
                String output = String.format("%s,%d,%x,%d,%s,%d\r\n", printUTCTime(debugData.getRcvTimeStamp()), debugData.getBuildNumber(), debugData.getErrorCode(), debugData.getLineNumber(), debugData.getFileName(), debugData.getBootupTime());
                writer.append(output);
            }
            writer.close();
            return logfilename;
        }
        catch (Exception ex)
        {
            Log.e(TAG, "" + ex.toString());
        }
        return "";
    }

    public static String [] getExternalSDCardDirectory() {
        Pattern DIR_SEPORATOR = Pattern.compile("/");
        // Final set of paths
        final Set<String> rv = new HashSet<String>();
        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            // Device has physical external storage; use plain paths.
            if (TextUtils.isEmpty(rawExternalStorage)) {
                // EXTERNAL_STORAGE undefined; falling back to default.
                rv.add("/storage/sdcard0");
            } else {
                rv.add(rawExternalStorage);
            }
        } else {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rawUserId = "";
            } else {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = DIR_SEPORATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                } catch(NumberFormatException ignored) {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if (TextUtils.isEmpty(rawUserId)) {
                rv.add(rawEmulatedStorageTarget);
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }

        // Add all secondary storages
        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }
        return rv.toArray(new String[rv.size()]);
    }

    public static String [] getDirectorys()
    {
        ArrayList<String> dirArray = new ArrayList<String>();
        dirArray.add(Environment.getExternalStorageDirectory().toString());
        String [] pathArray = getExternalSDCardDirectory();
        for (int ii = 0; ii < pathArray.length; ii++)
        {
            dirArray.add(pathArray[ii]);
        }
        return dirArray.toArray(new String[dirArray.size()]);
    }

    public static String getCsvDirectory()
    {
        String [] dirArray = getDirectorys();

        int ii = 0;
        String path = "";
        for (ii = dirArray.length - 1; ii >= 0; ii--)
        {
            boolean success = false;
            File file = new File(dirArray[ii]+"/glance/");
            if (!file.exists()) {
                success = file.mkdir();
            }
            file = new File(dirArray[ii]+"/glance/csv/");
            if (!file.exists()) {
                success = file.mkdir();
            } else {
                success = true;
            }
            if (success) {
                path = file.getAbsolutePath();
                break;
            }
        }
        return path;
    }

    private int containAddress(List<String> list, String address)
    {
        int index = -1;
        for (int ii = 0; ii < list.size(); ii++)
        {
            if (list.get(ii).toString().contains(address))
            {
                return ii;
            }
        }
        return index;
    }

    private void updateGlanceDeviceList(String address){
        String [] splitAddress = address.split(",");
        if (splitAddress != null && splitAddress.length == 3) {
            int index = containAddress(mScannedGlaneDeviceArrayList, splitAddress[1]);
            if (index != -1) {
                mScannedGlaneDeviceArrayList.remove(index);
                mScannedGlaneDeviceArrayList.add(index, address);
            }
            else
            {
                mScannedGlaneDeviceArrayList.add(address);
            }
            ((BaseAdapter) mGlanceDeviceAddressSpinnerList.getAdapter()).notifyDataSetChanged();
            mGlanceDeviceAddressSpinnerList.invalidate();
        }
    }

    private void initGraphFragment()
    {
        mGraphFragment = new GraphFragment();
        mGraphFragment.setOnGraphListener(new GraphFragment.onGraphListener() {
            @Override
            public void onEnableStreaming(boolean enable) {
                enableStreaming(enable);
            }

            @Override
            public void onSetConnectionIntervalHigh() {
                setConnectionIntervalHigh();
            }

            @Override
            public void onSetStreamingMode() {
                setStreamingMode();
            }

            @Override
            public void onResetStreamingMode() {
                resetStreamingMode();
            }

            @Override
            public void onKeyBack() {
                removeGraphFragment();
            }
        });
    }

    private void addGraphFragment()
    {
        if (mGraphFragment != null && !mGraphFragment.isAdded()) {
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(android.R.id.content, mGraphFragment);
            ft.commit();
            mGraphFragment.start();
        }
    }

    private void removeGraphFragment()
    {
        if (mGraphFragment != null && mGraphFragment.isAdded()) {
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(mGraphFragment);
            ft.commit();
        }
    }

    private void SetStreamingModeSuccess()
    {
        if (mGraphFragment != null && mGraphFragment.isAdded()) {
            mGraphFragment.SetStreamingModeSuccess();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mGraphFragment != null && mGraphFragment.isAdded() && keyCode == KeyEvent.KEYCODE_BACK)
        {
            mGraphFragment.keyBack();
            return true;
        }
        else
        {
            finish();
        }
        return false;
    }

    private void enableStreaming(boolean enable)
    {
        if (mService == null)
        {
            return;
        }
        int ret = mService.enableGenericMotionStreaming(mConnectedDeviceAddress, enable);
        printStatus(ret, enable ? R.string.str_start_motion_stream : R.string.str_stop_motion_stream);
        if (ret == com.cwb.bleframework.GlanceProtocolService.GLANCE_APP_BLE_PROTOCOL_ERROR_NONE) {
            if (enable) {
                // Disable the buttons of getting battery level and other settings
                mGetBatteryButton.setEnabled(false);
                mGetSetButton.setEnabled(false);
                mReadRSCButton.setEnabled(false);
            } else {
                // Enable the buttons of getting battery level and other settings
                mGetBatteryButton.setEnabled(true);
                mGetSetButton.setEnabled(true);
                mReadRSCButton.setEnabled(true);
            }
        }
    }

    private void setConnectionIntervalHigh()
    {
        if (mService == null)
        {
            return;
        }
        if (mIsSetConnectionInterval)
        {
            SetConnectionIntervalSuccess();
        }
        else {
            mService.setConnectionInterval(mConnectedDeviceAddress, 2);
        }
    }

    private void setStreamingMode()
    {
        if (mService == null)
        {
            return;
        }
        else {
            mService.setPowerMode(mConnectedDeviceAddress, 1);
        }
    }

    private void resetStreamingMode()
    {
        if (mService == null)
        {
            return;
        }
        else {
            mService.setPowerMode(mConnectedDeviceAddress, 0);
        }
    }

    private void SetConnectionIntervalSuccess()
    {
        if (mGraphFragment != null && mGraphFragment.isAdded()) {
            mGraphFragment.SetConnectionIntervalSuccess();
        }
    }
}