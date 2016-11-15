package com.meow.potato7.sensorex;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 1B15182 on 5/18/2016.
 */
public class AccResult extends RealmObject {
    @PrimaryKey
    private String id;



    private Date date;
    private RealmList<SensorReading> readingList;

    public AccResult() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public RealmList<SensorReading> getReadingList() {
        return readingList;
    }

    public void setReadingList(RealmList<SensorReading> readingList) {
        this.readingList = readingList;
    }
}
