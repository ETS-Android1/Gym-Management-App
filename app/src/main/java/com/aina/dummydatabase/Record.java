package com.aina.dummydatabase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.startActivity;

public class Record extends AppCompatActivity {

    SQLiteDatabase mydb;
    String find_active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        find_active=getIntent().getStringExtra("FIND_ACTIVE");
        mydb=openOrCreateDatabase("CustomerDB", Context.MODE_PRIVATE, null);
        prepareCustData();

    }

    @Override
    public void onResume(){
        super.onResume();
        prepareCustData();;
    }


    public void prepareCustData(){
        Customer customer = null;
        ArrayList<Customer> customerList = new ArrayList<Customer>();
        String query_active = "SELECT customerID,name FROM customer WHERE activestat="+find_active.toString();
        Cursor c=mydb.rawQuery(query_active, null);
        while(c.moveToNext()) {
            customer = new Customer(c.getString(0),c.getString(1));
            customerList.add(customer);
        }
        ListView listView = (ListView)findViewById(R.id.ListView);
        listadapter listadapter = new listadapter(Record.this,R.layout.list_row,customerList);
        listView.setAdapter(listadapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                String customerId = ((TextView) view.findViewById(R.id.custIdView)).getText().toString();
                Intent appInfo = new Intent(Record.this, Detail.class);
                appInfo.putExtra("CUSTOMER_ID_Name",customerId);
                startActivity(appInfo);
            }
        });
    }

    public void showMessage(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
