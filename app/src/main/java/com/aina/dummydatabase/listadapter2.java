package com.aina.dummydatabase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;
import static android.support.v4.content.ContextCompat.startActivity;

public class listadapter2 extends ArrayAdapter<FeeEntry> {
    private Context mcontext;
    int mresource;
    private static class ViewHolder{
        TextView serialno;
        TextView datetime;
        TextView amount;
    }
    public listadapter2(Context context, int resource, ArrayList<FeeEntry> objects){
        super(context,resource,objects);
        mcontext = context;
        mresource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String sno = getItem(position).getSerialno();
        String amt = getItem(position).getAmount();
        String dtm = getItem(position).getDatetime();
        String custid = getItem(position).getCustid();

        final FeeEntry feentry = new FeeEntry(dtm,amt,sno,custid);
        View result;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater inflator = LayoutInflater.from(mcontext);
            convertView = inflator.inflate(mresource,parent,false);
            holder = new ViewHolder();
            holder.serialno = (TextView)convertView.findViewById(R.id.snTextView);
            holder.datetime = (TextView)convertView.findViewById(R.id.dtmTextView);
            holder.amount = (TextView)convertView.findViewById(R.id.amtTextView);
            result = convertView;
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
            result = convertView;
        }
        holder.serialno.setText(feentry.getSerialno());
        holder.datetime.setText(feentry.getDatetime());
        holder.amount.setText(feentry.getAmount());
        return convertView;

    }

}
