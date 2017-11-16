package com.dez.cameraapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Created by Dez on 11/8/2017.
 */

public class Picture {

    String name;
    int _id;
    long date_taken;
    String tags;

    public Picture(String name, int _id, long date_taken, String tags) {
        this.name = name;
        this._id = _id;
        this.date_taken = date_taken;
        this.tags = tags;

    }
    @Override
    public String toString() {
        return "Name: " + name + "TEXT" + _id + "INTEGER" + date_taken + "LONG" + tags +"TEXT";

    }
}