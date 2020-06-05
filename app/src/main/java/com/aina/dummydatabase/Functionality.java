package com.aina.dummydatabase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class Functionality extends AppCompatActivity {

    ArrayList<String> items = new ArrayList<>();
    SpinnerDialog spinnerDialog;
    SQLiteDatabase mydatabase, feeDB;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    TextView activeMale, activeFemale, total_inactive, feecollected, feepending;
    private static final int PERMISSION_REQUEST_CODE = 1;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functionality);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        mydatabase=openOrCreateDatabase("CustomerDB", Context.MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS customer(customerID VARCHAR,name VARCHAR,gender VARCHAR,fatherName VARCHAR,dob VARCHAR, height VARCHAR, weight VARCHAR, joindate VARCHAR, program VARCHAR, mobile VARCHAR, address VARCHAR, fee VARCHAR, img VARCHAR, activestat VARCHAR);");


        feeDB=openOrCreateDatabase("feesDB", Context.MODE_PRIVATE, null);
        feeDB.execSQL("CREATE TABLE IF NOT EXISTS feerecord(customerID VARCHAR,datetime VARCHAR,amount VARCHAR);");

        activeMale = (TextView)findViewById(R.id.activeMalesView);
        activeFemale = (TextView)findViewById(R.id.activeFemalesView);
        total_inactive = (TextView)findViewById(R.id.inactiveView);
        feecollected = (TextView)findViewById(R.id.collectedView);
        feepending = (TextView)findViewById(R.id.pendingView);

        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
//                Toast.makeText(getApplicationContext(), "Already has permissions", Toast.LENGTH_SHORT).show();

            } else {
                requestPermission(); // Code for permission
            }
        }

        prepareSpinner();
        fillDetails();


    }

    @Override
    public void onResume(){
        super.onResume();
        prepareSpinner();
        fillDetails();
    }

    @Override
    public void onBackPressed() {
        //No codes at all
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void fillDetails(){
        String query = "SELECT activestat,fee,gender,customerID FROM customer";
        Cursor c=mydatabase.rawQuery(query, null);
        int active_males = 0,active_females=0,inactive_persons=0,total_amount=0,total_collected=0,total_pending=0;
        String sdft = new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
        int today_month = Integer.parseInt(sdft.split("/")[0]);
        int today_year = Integer.parseInt(sdft.split("/")[2]);
        while(c.moveToNext()) {
            if (c.getString(0).equals("1")){
                total_amount = total_amount + Integer.parseInt(c.getString(1));
                if (c.getString(2).equals("S/O")){
                    active_males = active_males + 1;
                }
                else{
                    active_females = active_females + 1;
                }
                String query2 = "SELECT * FROM feerecord WHERE customerID="+c.getString(3);
                Cursor cr=feeDB.rawQuery(query2, null);
                while(cr.moveToNext()) {
                    int tranc_month = Integer.parseInt(cr.getString(1).split(" ")[0].split("/")[1]);
                    int tranc_year = Integer.parseInt(cr.getString(1).split(" ")[0].split("/")[2]);
                    if (tranc_month==today_month && tranc_year==today_year){
                        total_collected = total_collected + Integer.parseInt(cr.getString(2));
                    }
                }
            }
            else{
                inactive_persons = inactive_persons + 1;
            }

        }

        total_pending = total_amount - total_collected;

        activeMale.setText("Active Males = "+Integer.toString(active_males));
        activeFemale.setText("Active Females = "+Integer.toString(active_females));
        total_inactive.setText("Inactive Persons = "+Integer.toString(inactive_persons));
        feecollected.setText("Collected = Rs. "+Integer.toString(total_collected));
        feepending.setText("Pending = Rs. "+Integer.toString(total_pending));
    }

    public void logoutClick(View v){
        Intent myInt = new Intent(getBaseContext(), StartActivity.class);
        startActivity(myInt);
    }

    public void prepareSpinner(){
        initItems();
        spinnerDialog = new SpinnerDialog(Functionality.this,items,"Select Customer");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                Toast.makeText(Functionality.this,"Selected: "+item,Toast.LENGTH_SHORT).show();
                Intent myInt = new Intent(getBaseContext(), Detail.class);
                String parts[] = item.split(" ", 2);
                String custid = parts[0].substring(0, parts[0].length() - 1);
                myInt.putExtra("CUSTOMER_ID_Name",custid);
                startActivity(myInt);

            }
        });
    }

    public void addPersonClick(View v){
        Intent myInt = new Intent(getBaseContext(), AddPerson.class);
        myInt.putExtra("mode","insert");
        startActivity(myInt);
    }
    public void viewPersonClick(View v){
        spinnerDialog.showSpinerDialog();
    }

    private void initItems() {
        items.clear();
        Cursor c = mydatabase.rawQuery("SELECT customerID,name FROM customer", null);
        while(c.moveToNext()) {
            items.add(c.getString(0) + ": " +c.getString(1));
        }
    }

    public void viewActiveClick(View v){
        Intent myInt = new Intent(getBaseContext(), Record.class);
        myInt.putExtra("FIND_ACTIVE","1");
        startActivity(myInt);
    }

    public void viewInactiveClick(View v){
        Intent myInt = new Intent(getBaseContext(), Record.class);
        myInt.putExtra("FIND_ACTIVE","0");
        startActivity(myInt);
    }

    public void dayReminderClick(View v){
        Intent myInt = new Intent(getBaseContext(), DayRecord.class);
        startActivity(myInt);
    }

    public void sendCommonMessageClick(View v){
        Intent myInt = new Intent(getBaseContext(), SendCommonMessage.class);
        startActivity(myInt);
    }

    public void onBackupClick(View v){
        try {

            File folder = new File(Environment.getExternalStorageDirectory() + "/BodyZoneBackUp");
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }

            final String inFileName = "/data/data/com.aina.dummydatabase/databases/CustomerDB";
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = Environment.getExternalStorageDirectory() + "/BodyZoneBackUp" + "/customer_backup.db";
//            String outFileName = Environment.getExternalStorageDirectory() + "/customer_backup.db";

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer))>0){
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();


            final String inFileName2 = "/data/data/com.aina.dummydatabase/databases/feesDB";
            File dbFile2 = new File(inFileName2);
            FileInputStream fis2 = new FileInputStream(dbFile2);

            String outFileName2 = Environment.getExternalStorageDirectory() + "/BodyZoneBackUp" + "/fee_backup.db";
//            String outFileName2 = Environment.getExternalStorageDirectory() + "/fee_backup.db";

            // Open the empty db as the output stream
            OutputStream output2 = new FileOutputStream(outFileName2);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer2 = new byte[1024];
            int length2;
            while ((length2 = fis2.read(buffer2))>0){
                output2.write(buffer2, 0, length2);
            }

            // Close the streams
            output2.flush();
            output2.close();
            fis2.close();


            Toast.makeText(getApplicationContext(), "Backup Successful!",
                        Toast.LENGTH_SHORT).show();


        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "Backup Failed!", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    public void onRestoreClick(View v){
        try {

            final String inFileName = Environment.getExternalStorageDirectory() + "/BodyZoneBackUp" + "/customer_backup.db";
//            final String inFileName = Environment.getExternalStorageDirectory() + "/customer_backup.db";

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName =  "/data/data/com.aina.dummydatabase/databases/CustomerDB";

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer))>0){
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            final String inFileName2 = Environment.getExternalStorageDirectory() + "/BodyZoneBackUp" + "/fee_backup.db";
//            final String inFileName2 = Environment.getExternalStorageDirectory() + "/fee_backup.db";

            File dbFile2 = new File(inFileName2);
            FileInputStream fis2 = new FileInputStream(dbFile2);

            String outFileName2 =  "/data/data/com.aina.dummydatabase/databases/feesDB";

            // Open the empty db as the output stream
            OutputStream output2 = new FileOutputStream(outFileName2);

            // Transfer bytes from the inputfile to the outputfile
            byte[] buffer2 = new byte[1024];
            int length2;
            while ((length2 = fis2.read(buffer2))>0){
                output2.write(buffer2, 0, length2);
            }

            // Close the streams
            output2.flush();
            output2.close();
            fis2.close();

            prepareSpinner();
            fillDetails();


            String query = "SELECT customerID FROM customer";
            int maxCustID = 0;
            Cursor c=mydatabase.rawQuery(query, null);
            while(c.moveToNext()) {
                if(Integer.parseInt(c.getString(0))>maxCustID){
                    maxCustID = Integer.parseInt(c.getString(0));
                }
            }


            SharedPreferences.Editor mEditor = sharedpreferences.edit();
            String newcustID = String.valueOf(maxCustID + 1);
            mEditor.putString("customerID", newcustID).commit();



            Toast.makeText(getApplicationContext(), "Restore Successful!",
                    Toast.LENGTH_SHORT).show();


        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "Restore Failed!", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Functionality.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(Functionality.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(Functionality.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Functionality.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
}
