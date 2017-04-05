package com.cwb.glancesampleapp;

import android.util.Log;

import com.cwb.bleframework.GlanceStatus;

import java.util.HashMap;

/**
 * Created by jasonchang on 2016/11/11.
 */
public class JumpHeight {
    private long timeStarted;
    private long timeEnded;
    private boolean timeFlag = true;
    private double height;
    private int Z;
    private int timer;
    public void processMotionData(HashMap<String, GlanceStatus.SensorData> kvs)
    {
        if (kvs.containsKey("accel") && kvs.containsKey("gyro")) {
            receiveRawDataGraph(kvs.get("accel"), kvs.get("gyro"));
        }
    }
    public void receiveRawDataGraph(GlanceStatus.SensorData accel, GlanceStatus.SensorData gyro){
        Z = accel.getZ()-4060;
         if(Math.abs(Z)>250&&timeFlag){
             timeFlag = false;
             timeStarted=System.currentTimeMillis();
             Log.i("Taking off", "Taking off ");

        }
        if(!timeFlag&&Math.abs(Z)<250){
            timeFlag = true;
            timeEnded = System.currentTimeMillis();
            height=1.225*1e-4*(timeEnded-timeStarted);
            Log.i("Jump Height", "JP"+height);
        }
    }
}
