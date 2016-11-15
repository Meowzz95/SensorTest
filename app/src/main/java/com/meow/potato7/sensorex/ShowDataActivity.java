package com.meow.potato7.sensorex;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class ShowDataActivity extends AppCompatActivity {




    private Realm rdb;
    private RealmResults<AccResult> results;

    private final String TAG="ShowDataActivity";

    private Spinner showDataSpinner;
    private TextView showDataTvData;
    private LineChart showLineChart;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-05-18 14:31:10 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        showDataSpinner = (Spinner)findViewById( R.id.show_data_spinner );
        showDataTvData = (TextView)findViewById( R.id.show_data_tv_data );
        showLineChart = (LineChart)findViewById( R.id.show_line_chart );
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        findViews();

        rdb=Realm.getDefaultInstance();
        results = rdb.where(AccResult.class).findAll();
        List<String> resultIdList=new ArrayList<>();
        for(AccResult current: results)
            resultIdList.add(current.getId());

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.spinner_view,resultIdList);

        showDataSpinner.setAdapter(adapter);

        showDataSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemSelected: listener triggered");
                String selectedId=showDataSpinner.getSelectedItem().toString();
                Log.i(TAG, "onItemSelected: selected id"+selectedId);
                RealmResults<AccResult> selectedResult=rdb.where(AccResult.class).equalTo("id",selectedId).findAll();
                Log.i(TAG, "onItemSelected: acc reslut list size="+selectedResult.size());
                RealmList<SensorReading> readingList=selectedResult.get(0).getReadingList();
                Log.i(TAG, "onItemSelected: readingList size="+readingList.size());

//                StringBuilder sb=new StringBuilder();
//                for(SensorReading current:readingList)
//                {
//                    sb.append(current.toString());
//                    Log.i(TAG, "onItemSelected: Current reading"+current.toString());
//                }
//
//                showDataTvData.setText(sb.toString());
                ArrayList<Entry> xList=new ArrayList<Entry>();
                ArrayList<Entry> yList=new ArrayList<Entry>();
                ArrayList<Entry> zList=new ArrayList<Entry>();

                ArrayList<String> xVals=new ArrayList<String>();
                int counter=0;
                for(SensorReading reading:readingList){

                    xList.add(new Entry(reading.getX(),counter));
                    yList.add(new Entry(reading.getY(),counter));
                    zList.add(new Entry(reading.getZ(),counter));

                    xVals.add(counter+"");
                    counter++;
                }

                LineDataSet xSet=new LineDataSet(xList,"X");
                LineDataSet ySet=new LineDataSet(yList,"Y");
                LineDataSet zSet=new LineDataSet(zList,"Z");

                xSet.setColor(Color.GREEN);
                ySet.setColor(Color.BLUE);
                zSet.setColor(Color.RED);

                xSet.setDrawCircles(false);
                ySet.setDrawCircles(false);
                zSet.setDrawCircles(false);

//                xSet.setDrawCircleHole(false);
//                ySet.setDrawCircleHole(false);
//                zSet.setDrawCircleHole(false);
//
//                xSet.setCircleColor(Color.GREEN);
//                ySet.setCircleColor(Color.BLUE);
//                zSet.setCircleColor(Color.RED);


                ArrayList<ILineDataSet> dataSets=new ArrayList<ILineDataSet>();
                dataSets.add(xSet);
                dataSets.add(ySet);
                dataSets.add(zSet);

                LineData lineData=new LineData(xVals,dataSets);

                showLineChart.setHardwareAccelerationEnabled(true);
                showLineChart.animateX(3000);
                showLineChart.setData(lineData);
                showLineChart.invalidate();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }
}
