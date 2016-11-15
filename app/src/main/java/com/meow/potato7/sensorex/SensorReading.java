package com.meow.potato7.sensorex;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by 1B15182 on 5/17/2016.
 */
public class SensorReading extends RealmObject{

    private float x;
    private float y;
    private float z;



    public SensorReading() {
    }

    public SensorReading(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "["+x+","+y+","+z+"]";
    }
}
