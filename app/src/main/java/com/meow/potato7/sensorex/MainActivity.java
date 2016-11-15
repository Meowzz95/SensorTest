package com.meow.potato7.sensorex;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private final String TAG="MainActivity";

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private TextView tvReading;
    private Button btnShow;
    private Button btnStartStop;
    private Button btnDelete;
    private LineChart lineChart;

    private AccResult accResult;
    private RealmList<SensorReading> readingList;

    private Realm rdb;

    private boolean isStarted;
    private int counter = 0;//sensor listener counter



    private ArrayList<Entry> xEntries;
    private ArrayList<Entry> yEntries;
    private ArrayList<Entry> zEntries;

    private ArrayList<String> xVals;

    private LineDataSet xDS;
    private LineDataSet yDS;
    private LineDataSet zDS;

    private ArrayList<ILineDataSet> dataSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvReading=(TextView)findViewById(R.id.tvReading);
        btnShow=(Button)findViewById(R.id.btn_show);
        btnStartStop=(Button)findViewById(R.id.btn_start_stop);
        btnDelete=(Button)findViewById(R.id.btn_delete_data);
        lineChart=(LineChart)findViewById(R.id.main_dynamic_chart);

        rdb=Realm.getDefaultInstance();

        mSensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        readingList=new RealmList<>();

        btnStartStop.setText("Start");
        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isStarted){
                   startSensor();
                    rdb.beginTransaction();
                    accResult=rdb.createObject(AccResult.class,UUID.randomUUID().toString());

                    accResult.setDate(new Date());
                    rdb.commitTransaction();
                }
                else{
                    stopSensor();
                    rdb.beginTransaction();
                    accResult.getReadingList().addAll(readingList);
                    Log.i(TAG, "onClick: readingList added into accresult size="+readingList.size());
                    rdb.commitTransaction();
                }
            }
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                StringBuilder sb=new StringBuilder();
//                for(SensorReading currentReading:readingList)
//                    sb.append(currentReading.toString());



//                StringBuilder sb=new StringBuilder();
//                RealmResults<SensorReading> results=rdb.where(SensorReading.class).findAll();
//                for(SensorReading current:results)
//                    sb.append(current.toString());
//                tvReading.setText(sb.toString());
                Intent goShow=new Intent(MainActivity.this,ShowDataActivity.class);
                startActivity(goShow);

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RealmResults<AccResult> results=rdb.where(AccResult.class).findAll();
                rdb.beginTransaction();
                results.deleteAllFromRealm();
                rdb.commitTransaction();
            }
        });



        xEntries=new ArrayList<>();
        yEntries=new ArrayList<>();
        zEntries=new ArrayList<>();

        xVals=new ArrayList<>();

        xDS=new LineDataSet(xEntries,"X");
        yDS=new LineDataSet(yEntries,"Y");
        zDS=new LineDataSet(zEntries,"Z");

        xDS.setColor(Color.GREEN);
        yDS.setColor(Color.BLUE);
        yDS.setColor(Color.RED);

        xDS.setDrawCircles(false);
        yDS.setDrawCircles(false);
        zDS.setDrawCircles(false);

        xDS.setAxisDependency(YAxis.AxisDependency.LEFT);
        yDS.setAxisDependency(YAxis.AxisDependency.LEFT);
        zDS.setAxisDependency(YAxis.AxisDependency.LEFT);

        xDS.setDrawValues(false);
        yDS.setDrawValues(false);
        zDS.setDrawValues(false);

        dataSets=new ArrayList<>();
        dataSets.add(xDS);
        dataSets.add(yDS);
        dataSets.add(zDS);

        LineData chartData=new LineData(xVals,dataSets);

        lineChart.getAxis(YAxis.AxisDependency.LEFT).setAxisMaxValue(20f);
        lineChart.getAxis(YAxis.AxisDependency.LEFT).setAxisMinValue(-20f);

        lineChart.setData(chartData);




    }

    private void startSensor(){
        mSensorManager.registerListener(MainActivity.this,accelerometer,200000);
        btnStartStop.setText("Stop");
        btnShow.setEnabled(false);
        isStarted=true;
        counter=0;
        readingList.clear();
    }

    private void stopSensor(){
        mSensorManager.unregisterListener(this);
        btnStartStop.setText("Start");
        btnShow.setEnabled(true);
        isStarted=false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //readingList.add(new SensorReading(event.values[0],event.values[1],event.values[2]));
        tvReading.setText(""+ counter);
        counter++;
        SensorReading reading=new SensorReading();
        reading.setX(event.values[0]);
        reading.setY(event.values[1]);
        reading.setZ(event.values[2]);
        readingList.add(reading);

        xEntries.add(new Entry(event.values[0],counter));
        yEntries.add(new Entry(event.values[1],counter));
        zEntries.add(new Entry(event.values[2],counter));

        xVals.add(counter+"");


        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
//        rdb.beginTransaction();
//        rdb.copyToRealm(reading);
//        rdb.commitTransaction();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
       stopSensor();
    }
}
