package com.dez.cameraapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Created by Dez on 11/8/2017.
 */

public class Picture {

    //Global Variable
    String name;
    int _id;
    long date_taken;
    String tags;
    String fileName;

    //------------------------------------------
    // Constuctor:
    //-------------------------------------------
    public Picture(String name, int _id, long date_taken, String tags, String fileName) {
        //--------------------
        // Instantiation of name this particulture picture
        this.name = name;
        this._id = _id;
        this.date_taken = date_taken;
        this.tags = tags;
        this.fileName=fileName;
    }
    @Override
    public String toString() {
        return "Name: " + name + "TEXT" + _id + "INTEGER" + date_taken + "LONG" + tags +"TEXT" + "FILE NAME" + fileName;

    }
}