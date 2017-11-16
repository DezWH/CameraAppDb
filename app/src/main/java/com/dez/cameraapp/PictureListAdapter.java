package com.dez.cameraapp;

/**
 * Created by Dez on 11/15/2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class PictureListAdapter extends CursorAdapter{

    Context context;

    private static int NAME_COL = 1;
    private static int QUANTITY_COL = 2;

    public PictureListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.list_item, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Put data from this cursor (represents one row of the database) into this
        // view (the corresponding row in the list)

        final TextView pictureListName = (TextView) view.findViewById(R.id.picture_list_name);
        TextView pictureListQuantity = (TextView) view.findViewById(R.id.picture_list_quantity);

        pictureListName.setText(cursor.getString(NAME_COL));

        pictureListQuantity.setText(cursor.getString(QUANTITY_COL));




    }

}
