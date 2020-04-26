package com.aina.dummydatabase;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

public class DayRecord extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<Customer>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_record);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();


    }

    @Override
    public void onResume(){
        super.onResume();
        prepareListData();
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Customer>>();
        ArrayList<ArrayList<Customer>> outerCustomer = new ArrayList<ArrayList<Customer>>();
        int countinday[] = new int[28];
        int feeinday[] = new int[28];

        // Adding child data
        int i=1;
        for (i=1;i<29;i++){
            ArrayList<Customer> customerList = new ArrayList<Customer>();
            outerCustomer.add(customerList);
            countinday[i-1] = 0;
            feeinday[i-1] = 0;
        }

        SQLiteDatabase mydb=openOrCreateDatabase("CustomerDB", Context.MODE_PRIVATE, null);
        Customer customer = null;
        String query_active = "SELECT  customerID,name,fee,joindate FROM customer WHERE activestat=1";
        Cursor c=mydb.rawQuery(query_active, null);
        while(c.moveToNext()) {
            customer = new Customer(c.getString(0),c.getString(1));
            int day_of_joining = Integer.parseInt(c.getString(3).split("/")[0]) - 1;
            if (day_of_joining>27){
                day_of_joining = 27;
            }
            outerCustomer.get(day_of_joining).add(customer);
            countinday[day_of_joining] = countinday[day_of_joining] + 1;
            feeinday[day_of_joining] = feeinday[day_of_joining] + Integer.parseInt(c.getString(2));
        }

        for (i=0;i<28;i++){
            String daystr = "Day=" + Integer.toString(i+1) + " Number="+Integer.toString(countinday[i])+" Amount="+Integer.toString(feeinday[i]);
            listDataHeader.add(daystr);
            listDataChild.put(listDataHeader.get(i),outerCustomer.get(i));
        }


        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
                expListView.setSelection(groupPosition);
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition).getName(), Toast.LENGTH_SHORT)
                        .show();
                Intent myInt = new Intent(getBaseContext(), Detail.class);
                String custid = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getCustid();
                myInt.putExtra("CUSTOMER_ID_Name",custid);
                startActivity(myInt);
                return false;
            }
        });
        String sdft = new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
        int today_day = Integer.parseInt(sdft.split("/")[1]);
        if (today_day>28){
            today_day = 28;
        }
        today_day = today_day -1;
        expListView.expandGroup(today_day);
    }
}
