//---------------------------------------------------------------------
//
// Copyright (c) 2016 CWB Tech Limited All rights reserved
//
//
//---------------------------------------------------------------------
// File: GraphDisplayXYZ.java
// Author: Kevin Kwok (kevinkwok@cwb-tech.com)
//         William Chan (williamchan@cwb-tech.com)
// Project: Glance
//---------------------------------------------------------------------

package com.cwb.glancesampleapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;


import com.cwb.bleframework.GlanceStatus;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GraphDisplayXYZ{
    private static final String TAG = "GraphDisplayXYZ";

    private final static int MAX_SERIES_SIZE = 1000;
    private Context mContext;
    private TimeSeries mXSeries;
    private TimeSeries mYSeries;
    private TimeSeries mZSeries;
    private XYSeriesRenderer mXRenderer = new XYSeriesRenderer();
    private XYSeriesRenderer mYRenderer = new XYSeriesRenderer();
    private XYSeriesRenderer mZRenderer = new XYSeriesRenderer();
    private XYMultipleSeriesDataset mDataSet = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mMultiRenderer = new XYMultipleSeriesRenderer();
    private GraphicalView mGraphicalView;
    private int minX = 0;
    private double minY, maxY;
    private double squareSum[] = new double[3];
    private int rmsSize;
    private boolean mRealTimeUpdate;
    //private RemoveFirstDataTask mRealTimeRemove;
    private int mRemoveCount;
    private TimeSeries bufX, bufY, bufZ;
    private Queue<int[]> mNewPoint;
//    private boolean
// = false;
    private double mMinUpperBound = 0;
    private double mMinLowerBound = 0;
    private ArrayList<Double> rmaArrayList = new ArrayList<Double>();


    private class mySeries extends TimeSeries{
        private int maxPoints;

        List<Integer> mX;
        List<Integer> mY;


        public mySeries(String title) {
            super(title);
            maxPoints = -1;
        }

        public void setMax(int maxPoints){
            this.maxPoints = maxPoints;
        }

        @Override
        public synchronized void add(double x, double y) {
        }

    }

    private void createChart(Context context){
        Log.d(TAG, "new graph");
        mContext = context;
        squareSum[0] = squareSum[1] = squareSum[2] = rmsSize = 0;
        minY = maxY = 0;
        mXSeries = new TimeSeries("X");
        mYSeries = new TimeSeries("Y");
        mZSeries = new TimeSeries("Z");


        mDataSet.addSeries(mXSeries);
        mXRenderer.setColor(mContext.getResources().getColor(R.color.pace_text));
        mXRenderer.setLineWidth((float) 3.0);

        mDataSet.addSeries(mYSeries);
        mYRenderer.setColor(mContext.getResources().getColor(R.color.distance_text));
        mYRenderer.setLineWidth((float) 3.0);

        mDataSet.addSeries(mZSeries);
        mZRenderer.setColor(mContext.getResources().getColor(R.color.step_text));
        mZRenderer.setLineWidth((float) 3.0);

        final XYMultipleSeriesRenderer renderer = mMultiRenderer;
        renderer.setBackgroundColor(Color.WHITE);
        renderer.setApplyBackgroundColor(true);
        renderer.setMargins(new int[]{20, 60, 20, 20});
        renderer.setMarginsColor(Color.WHITE);
        renderer.setAxesColor(Color.BLACK);
        renderer.setAxisTitleTextSize(30);
        renderer.setShowGrid(true);
        renderer.setGridColor(Color.DKGRAY);
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setYLabelsPadding(4.0f);
        renderer.setLabelsTextSize(20);
        renderer.setLegendTextSize(20);
        renderer.setAntialiasing(false);
        renderer.setZoomButtonsVisible(false);

        renderer.setPanEnabled(false, false);
        renderer.setZoomEnabled(false, false);

        renderer.setYLabelsColor(0, Color.BLACK);
        renderer.setXLabelsColor(Color.BLACK);
        renderer.addSeriesRenderer(mXRenderer);
        renderer.addSeriesRenderer(mYRenderer);
        renderer.addSeriesRenderer(mZRenderer);

        mGraphicalView = ChartFactory.getLineChartView(mContext, mDataSet, mMultiRenderer);
    }

    public void setRealTimeUpdate(boolean isRealTimeUpdate){
        mRealTimeUpdate = isRealTimeUpdate;
    }

    public boolean isRealTimeUpdate(){
        return mRealTimeUpdate;
    }

    /**
     * No initial data, wait for real time update
     * @param context
     */
    public GraphDisplayXYZ(Context context, double minLowerBound, double minUpperBound){
        mMinLowerBound = minLowerBound;
        mMinUpperBound = minUpperBound;
        createChart(context);
        mNewPoint = new LinkedList<int[]>();
        setRealTimeUpdate(true);
    }

    /**
     * Has initial data, will not wait for real time update
     * @param context
     * @param xSeries
     * @param ySeries
     * @param zSeries
     */
    public GraphDisplayXYZ(Context context, List<Integer> xSeries, List<Integer> ySeries, List<Integer> zSeries, boolean isShowRMS){
        createChart(context);
        for (int i = 0; i != xSeries.size(); i++ ){
            mXSeries.add(i, xSeries.get(i));
            mYSeries.add(i, ySeries.get(i));
            mZSeries.add(i, zSeries.get(i));
        }
        repaint();
        setRealTimeUpdate(false);
    }


    public GraphicalView getView(){
        return mGraphicalView;
    }

    public int getSize(){
        return mXSeries.getItemCount();
    }

    public void addPoint(int x, GlanceStatus.SensorData sensorData){
        int[] element = {x, sensorData.getX(), sensorData.getY(), sensorData.getZ()};
        rmsSize++;


        mXSeries.add(element[0], element[1]);
        mYSeries.add(element[0], element[2]);
        mZSeries.add(element[0], element[3]);
        repaint();
        squareSum[0] += sensorData.getX() * sensorData.getX();
        squareSum[1] += sensorData.getY() * sensorData.getY();
        squareSum[2] += sensorData.getZ() * sensorData.getZ();
    }

    public void setDataAndRepaint(List<Integer> xSeries, List<Integer> ySeries, List<Integer> zSeries){
        mXSeries.clear();
        mYSeries.clear();
        mZSeries.clear();
        for (int i = 0; i != xSeries.size(); i++ ){
            mXSeries.add(i, xSeries.get(i));
            mYSeries.add(i, ySeries.get(i));
            mZSeries.add(i, zSeries.get(i));
        }
        repaint();
    }

    public double[] getRMS(){
        double rms[] = new double[3];
        rms[0] = Math.sqrt(squareSum[0] / rmsSize);
        rms[1] = Math.sqrt(squareSum[1] / rmsSize);
        rms[2] = Math.sqrt(squareSum[2] / rmsSize);
        return rms;
    }

    public void graphClear(){
        mXSeries.clear();
        mYSeries.clear();
        mZSeries.clear();
        minX = 0;
        squareSum[0] = squareSum[1] = squareSum[2] = rmsSize = 0;
        repaint();
    }

    public void repaint(){
        mGraphicalView.repaint();
    }

    public void removeFirst() {
        double value = mXSeries.getY(minX);
        squareSum[0] -= value * value ;
        value = mYSeries.getY(minX);
        squareSum[1] -= value * value;
        value = mZSeries.getY(minX);
        squareSum[2] -= value * value;
        mMultiRenderer.setXAxisMin(++minX);
        rmsSize--;
    }

    public double rms(double[] nums){
        double ms = 0;
        for (int i = 0; i < nums.length; i++)
            ms += nums[i] * nums[i];
        return Math.sqrt(ms);
    }


}
