package com.aina.dummydatabase;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Detail extends AppCompatActivity {

    TextView nameView, totalFeeView, dojView;
    ImageView imgView;
    SQLiteDatabase custDB, feeDB;
    String custid = "";
    String phoneNumber;
    AlertDialog dialogBuilder, dialogBuilder2, dialogBuilder3, dialogBuilder4;
    String fees;
    Switch activeStat;

    String dtm_consider;
    EditText editText2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        custid= getIntent().getStringExtra("CUSTOMER_ID_Name");

        custDB=openOrCreateDatabase("CustomerDB", Context.MODE_PRIVATE, null);
        feeDB = openOrCreateDatabase("feesDB", Context.MODE_PRIVATE, null);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            //TODO
        }


        nameView = (TextView)findViewById(R.id.textView3);
        imgView = (ImageView) findViewById(R.id.imageView4);
        activeStat = (Switch) findViewById(R.id.switch1);
        totalFeeView = (TextView) findViewById(R.id.totalFeePaid);
        dojView = (TextView) findViewById(R.id.dojView);

        fillBasicDetails();

        dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        final EditText editText = (EditText) dialogView.findViewById(R.id.edt_comment);
        Button button1 = (Button) dialogView.findViewById(R.id.buttonSubmit);
        Button button2 = (Button) dialogView.findViewById(R.id.buttonCancel);
        editText.setText(fees);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amountfees = editText.getText().toString();
                ContentValues values = new ContentValues();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                values.put("customerID",custid);
                values.put("datetime",formatter.format(date).toString());
                values.put("amount",amountfees);
                long i = feeDB.insert("feerecord", null, values);


                String nameCustomer = nameView.getText().toString();
                String message = "Dear " + nameCustomer + ",\nYou have successfully deposited "+ amountfees +"/-" +"\nThanks,\nBody Fitness Zone";
                if (phoneNumber.length() > 0 && message.length() > 0) {
                    sendSms(phoneNumber, message);
                } else
                    Toast.makeText(getBaseContext(),
                            "Please enter both phone number and message.",
                            Toast.LENGTH_SHORT).show();


                showMessage("Record","Inserted Successfully");
                populateFeeList();
                dialogBuilder.dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);


        dialogBuilder2 = new AlertDialog.Builder(this).create();
        LayoutInflater inflater2 = this.getLayoutInflater();
        View dialogView2 = inflater2.inflate(R.layout.custom_dialog_2, null);

        editText2 = (EditText) dialogView2.findViewById(R.id.edt_comment2);
        Button button3 = (Button) dialogView2.findViewById(R.id.buttonSubmit2);
        Button button4 = (Button) dialogView2.findViewById(R.id.buttonCancel2);
        Button button5 = (Button) dialogView2.findViewById(R.id.buttonUpdate);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Code for deletion of entry
//                Toast.makeText(getBaseContext(),dtm_consider+" " +custid, Toast.LENGTH_SHORT).show();
                String dtm_del = "'"+ dtm_consider +"'";
                feeDB.delete("feerecord", "customerID" + "=" + custid + " AND datetime = "+dtm_del, null);
                showMessage("Record","Deleted Successfully");
                populateFeeList();
                dialogBuilder2.dismiss();
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cancel Button
                dialogBuilder2.dismiss();
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Update the entry
                ContentValues values = new ContentValues();
                String amountfees = editText2.getText().toString();
                String dtm_del = "'" + dtm_consider + "'";
                values.put("amount",amountfees);
                feeDB.update("feerecord",values,"customerID" + "=" + custid + " AND datetime = "+dtm_del,null);
                populateFeeList();
                dialogBuilder2.dismiss();
            }
        });
        dialogBuilder2.setView(dialogView2);



        dialogBuilder3 = new AlertDialog.Builder(this).create();
        LayoutInflater inflater3 = this.getLayoutInflater();
        View dialogView3 = inflater3.inflate(R.layout.custom_dialog_3, null);

        Button button6 = (Button) dialogView3.findViewById(R.id.button11);
        Button button7 = (Button) dialogView3.findViewById(R.id.button12);

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cancel Button

                dialogBuilder3.dismiss();
            }
        });
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Delete the User
                custDB.delete("customer","customerID="+custid,null);
                feeDB.delete("feerecord","customerID="+custid,null);
                dialogBuilder3.dismiss();
                Intent myInt = new Intent(getBaseContext(), Functionality.class);
                startActivity(myInt);
            }
        });
        dialogBuilder3.setView(dialogView3);



        dialogBuilder4 = new AlertDialog.Builder(this).create();
        LayoutInflater inflater4 = this.getLayoutInflater();
        View dialogView4 = inflater4.inflate(R.layout.custom_dialog_4, null);

        Button button8 = (Button) dialogView4.findViewById(R.id.button51);
        Button button9 = (Button) dialogView4.findViewById(R.id.button52);

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cancel Button
                dialogBuilder4.dismiss();
            }
        });
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Delete the User
                feeDB.delete("feerecord","customerID="+custid,null);
                populateFeeList();
                dialogBuilder4.dismiss();
            }
        });
        dialogBuilder4.setView(dialogView4);

        populateFeeList();


        activeStat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    ContentValues values = new ContentValues();
                    values.put("activestat","1");
                    long i = custDB.update("customer",values,"customerID="+custid,null);
                    Log.i("Update", i + "");
                    showMessage("Active","Active");
                }
                else{
                    ContentValues values = new ContentValues();
                    values.put("activestat","0");
                    long i = custDB.update("customer",values,"customerID="+custid,null);
                    Log.i("Update", i + "");
                    showMessage("Inactive","Inactive");
                }
            }
        });

    }

    @Override
    public void onResume() {

        super.onResume();
//        this.onCreate(null);
        fillBasicDetails();
        populateFeeList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                }
                break;

            default:
                break;
        }
    }

    public void fillBasicDetails(){
        String query = "SELECT * FROM customer WHERE customerID="+custid.toString();
        Cursor cr=custDB.rawQuery(query, null);
        if (cr.moveToFirst()){
            nameView.setText(cr.getString(1));
            try {
                String infilename = cr.getString(12);
                File imageFile = new File(infilename);
                FileInputStream fis = new FileInputStream(imageFile);
                byte[] blob = new byte[(int) imageFile.length()];
                fis.read(blob);
                fis.close();
                // Convert the byte array to Bitmap
                Bitmap bitmap= BitmapFactory.decodeByteArray(blob, 0, blob.length);
                imgView.setImageBitmap(bitmap);
            }
            catch (Exception e){
//                Toast.makeText(getApplicationContext(), "Image not retrieved", Toast.LENGTH_SHORT).show();
            }

            phoneNumber = cr.getString(9);
            fees = cr.getString(11);
            if (cr.getString(13).equals("1")){
                activeStat.setChecked(true);
            }
            else{
                activeStat.setChecked(false);
            }
            dojView.setText(cr.getString(7));
        }
    }

    public void populateFeeList(){
        FeeEntry feeentry = null;
        ArrayList<FeeEntry> feeList = new ArrayList<FeeEntry>();
        int counter=1;
        int total_fees_paid = 0;
        Cursor c=feeDB.rawQuery("SELECT * FROM feerecord WHERE customerID='"+custid+"'", null);
        while(c.moveToNext()) {
            feeentry = new FeeEntry(c.getString(1),c.getString(2), Integer.toString(counter),custid);
            counter = counter + 1;
            total_fees_paid = total_fees_paid + Integer.parseInt(c.getString(2));
            feeList.add(feeentry);
        }

        ListView listView = (ListView)findViewById(R.id.ListView2);
        listadapter2 listadapter = new listadapter2(Detail.this,R.layout.list_row_fee,feeList);
        listView.setAdapter(listadapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                dtm_consider = ((TextView) view.findViewById(R.id.dtmTextView)).getText().toString();
                String fee_consider = ((TextView) view.findViewById(R.id.amtTextView)).getText().toString();
                editText2.setText(fee_consider);
                dialogBuilder2.show();
            }
        });
        totalFeeView.setText("Total Fees Paid = " + Integer.toString(total_fees_paid));
    }



    public void onClickUpdate(View v){
        Intent myInt = new Intent(getBaseContext(), AddPerson.class);
        myInt.putExtra("mode","update");
        myInt.putExtra("CUSTOMER_ID_UPDATE",custid);
        startActivity(myInt);
    }

    public void onClickCall(View v){
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:"+phoneNumber));
            startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
            Log.e("Calling a Phone Number", "Call failed", activityException);
        }
    }

    public void onSendReminder(View v){
        String nameCustomer = nameView.getText().toString();
        String message = "Dear " + nameCustomer + ",\nPlease accept this message as a soft reminder for the pending fees of this month."+ "\nThanks,\nBody Fitness Zone.";
        if (phoneNumber.length() > 0 && message.length() > 0) {
            sendSms(phoneNumber, message);
        } else
            Toast.makeText(getBaseContext(),
                    "Please enter both phone number and message.",
                    Toast.LENGTH_SHORT).show();
    }

    public void onClickRecord(View v){
        dialogBuilder.show();
    }

    public void onDeleteCall(View v){
        dialogBuilder3.show();
    }

    public void onDeleteFeeRecord(View v){
        dialogBuilder4.show();
    }

    public void showMessage(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    private void sendSms(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

}
