package com.aina.dummydatabase;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
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

import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.startActivity;

public class listadapter extends ArrayAdapter<Customer> {
    private Context mcontext;
    int mresource;
    private static class ViewHolder{
        TextView custid;
        TextView custName;
    }
    public listadapter(Context context, int resource, ArrayList<Customer> objects){
        super(context,resource,objects);
        mcontext = context;
        mresource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       String custid = getItem(position).getCustid();
       String custname = getItem(position).getName();
       Customer customer = new Customer(custid,custname);
       View result;
       final ViewHolder holder;
       if(convertView==null){
           LayoutInflater inflator = LayoutInflater.from(mcontext);
           convertView = inflator.inflate(mresource,parent,false);
           holder = new ViewHolder();
           holder.custid = (TextView)convertView.findViewById(R.id.custIdView);
           holder.custName = (TextView)convertView.findViewById(R.id.custNameView);
           result = convertView;
           convertView.setTag(holder);
       }
       else{
           holder = (ViewHolder)convertView.getTag();
           result = convertView;
       }
        holder.custName.setText(customer.getName());
        holder.custid.setText(customer.getCustid());
       return convertView;

    }

}
