package com.aina.dummydatabase;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

//public class AddPerson extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
public class AddPerson extends AppCompatActivity {
    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "SelectImageActivity";
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 101;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PHONE_STATE_REQUEST_CODE = 202;
    SQLiteDatabase db;

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    ImageView profilepicView;

    String mode_operation = "insert";

    String custIDUpdate = "";

    Spinner spinner1,spinner2;

    EditText nameField,phoneField,dobField,joinField,addField,fatherField,heightField,weightField,feeField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        profilepicView = (ImageView) findViewById(R.id.imageView3);
        profilepicView.setImageResource(R.drawable.ic_launcher_background);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        nameField = (EditText) findViewById(R.id.nameText);
        phoneField = (EditText) findViewById(R.id.mobileText);
        fatherField = (EditText) findViewById(R.id.fatherText);
        heightField = (EditText) findViewById(R.id.heightText);
        weightField = (EditText) findViewById(R.id.weightText);
        addField = (EditText) findViewById(R.id.addText);
        feeField = (EditText) findViewById(R.id.feeText);




        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_STATE_REQUEST_CODE);
            } else {
                //TODO
            }
        }

        spinner1 = (Spinner) findViewById(R.id.spinner);
        spinner2 = (Spinner) findViewById(R.id.spinner2);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.gender,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
//        spinner1.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.program,android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
//      Taking permission for Image
        handlePermission();


        final Calendar myCalendar = Calendar.getInstance();
        dobField= (EditText) findViewById(R.id.dobText);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                dobField.setText(sdf.format(myCalendar.getTime()));
            }

        };

        dobField.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddPerson.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        final Calendar myCalendar2 = Calendar.getInstance();
        joinField= (EditText) findViewById(R.id.joindateText);

        final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar2.set(Calendar.YEAR, year);
                myCalendar2.set(Calendar.MONTH, monthOfYear);
                myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                joinField.setText(sdf.format(myCalendar2.getTime()));
            }

        };

        joinField.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AddPerson.this, date2, myCalendar2
                        .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        db=openOrCreateDatabase("CustomerDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS customer(customerID VARCHAR,name VARCHAR,gender VARCHAR,fatherName VARCHAR,dob VARCHAR, height VARCHAR, weight VARCHAR, joindate VARCHAR, program VARCHAR, mobile VARCHAR, address VARCHAR, fee VARCHAR, img VARCHAR, activestat VARCHAR);");

        mode_operation = getIntent().getStringExtra("mode");

        if (mode_operation.equals("update")) {
            custIDUpdate = getIntent().getStringExtra("CUSTOMER_ID_UPDATE");
            if (custIDUpdate.toString().trim().length() == 0) {
                showMessage("Error", "Error: Customer ID not Valid");
                return;
            }
            String query = "SELECT * FROM customer WHERE customerID="+custIDUpdate.toString();
            Cursor c=db.rawQuery(query, null);
            if(c.moveToFirst())
            {
                nameField.setText(c.getString(1));
                spinner1.setSelection(getIndex(spinner1, c.getString(2)));
                fatherField.setText(c.getString(3));
                dobField.setText(c.getString(4));
                heightField.setText(c.getString(5));
                weightField.setText(c.getString(6));
                joinField.setText(c.getString(7));
                spinner2.setSelection(getIndex(spinner2, c.getString(8)));
                phoneField.setText(c.getString(9));
                addField.setText(c.getString(10));
                feeField.setText(c.getString(11));
                // Convert blob data to byte array

                try{
                    String infilename = c.getString(12);
                    File imageFile = new File(infilename);
                    FileInputStream fis = new FileInputStream(imageFile);
                    byte[] blob = new byte[(int) imageFile.length()];
                    fis.read(blob);
                    fis.close();
                    // Convert the byte array to Bitmap
                    Bitmap bitmap= BitmapFactory.decodeByteArray(blob, 0, blob.length);
                    profilepicView.setImageBitmap(bitmap);
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Image not retrieved", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                showMessage("Error", "Invalid CustomerID");
                clearText();
            }
        }
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }



    private void handlePermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //ask for permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    SELECT_PICTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case SELECT_PICTURE:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
                        if (showRationale) {
                            //  Show your own message here
                        } else {
                            showSettingsAlert();
                        }
                    }
                }
                break;
            case MY_CAMERA_PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                else
                {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            case PHONE_STATE_REQUEST_CODE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                }
                break;
            default:
                break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /* Choose an image from Gallery */
    void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (resultCode == RESULT_OK) {
                    if (requestCode == SELECT_PICTURE) {
                        // Get the url from data
                        final Uri selectedImageUri = data.getData();
                        if (null != selectedImageUri) {
                            // Get the path from the Uri
                            String path = getPathFromURI(selectedImageUri);
                            Log.i(TAG, "Image Path : " + path);
                            // Set the image in ImageView
                            findViewById(R.id.imageView3).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ImageView) findViewById(R.id.imageView3)).setImageURI(selectedImageUri);
                                }
                            });

                        }
                    }
                }

                if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK)
                {
//                    Toast.makeText(this, "I am here", Toast.LENGTH_LONG).show();
                    final Bitmap photo = (Bitmap) data.getExtras().get("data");
                    findViewById(R.id.imageView3).post(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageView) findViewById(R.id.imageView3)).setImageBitmap(photo);
                        }
                    });
//                    ((ImageView) findViewById(R.id.imageView3)).setImageBitmap(photo);
                }
            }
        }).start();

    }

    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public void onClickGetImage2(View v) {
        openImageChooser();
    }

    private void showSettingsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        openAppSettings(AddPerson.this);
                    }
                });
        alertDialog.show();
    }

    public static void openAppSettings(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    //Camera function
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onCameraClick(View v){
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }
        else
        {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }


    public void onClickSubmit(View v) {

        String custID = "0";


        if (nameField.getText().toString().trim().length() == 0 ||
                phoneField.getText().toString().trim().length() == 0 ||
                fatherField.getText().toString().trim().length() == 0 ||
                heightField.getText().toString().trim().length() == 0 ||
                weightField.getText().toString().trim().length() == 0 ||
                addField.getText().toString().trim().length() == 0 ||
                dobField.getText().toString().trim().length() == 0 ||
                joinField.getText().toString().trim().length() == 0 ||
                feeField.getText().toString().trim().length() == 0) {
            showMessage("Error", "Please enter all values");
            return;
        }

        if(profilepicView.getDrawable() == null){
            showMessage("Error", "Please Select an Image");
            return;
        }


        Bitmap image = ((BitmapDrawable) profilepicView.getDrawable()).getBitmap();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, out);
        byte[] buffer = out.toByteArray();

        db.beginTransaction();
        ContentValues values;
        try {
            values = new ContentValues();
            custID = sharedpreferences.getString("customerID", "1000");
            values.put("name", nameField.getText().toString());
            values.put("gender", spinner1.getSelectedItem().toString());
            values.put("fatherName", fatherField.getText().toString());
            values.put("dob", dobField.getText().toString());
            values.put("height", heightField.getText().toString());
            values.put("weight", weightField.getText().toString());
            values.put("joindate", joinField.getText().toString());
            values.put("program", spinner2.getSelectedItem().toString());
            values.put("mobile", phoneField.getText().toString());
            values.put("address", addField.getText().toString());
            values.put("fee", feeField.getText().toString());

            // Insert Row
            if (mode_operation.equals("insert")) {
                try {
                    File folder = new File(Environment.getExternalStorageDirectory() + "/BodyZoneBackUp/image_data");
                    boolean success = true;
                    if (!folder.exists()) {
                        success = folder.mkdirs();
                    }
                    String outFileName = Environment.getExternalStorageDirectory() + "/BodyZoneBackUp/image_data/" + custID;
                    values.put("img", outFileName);
                    OutputStream output = new FileOutputStream(outFileName);
                    output.write(buffer, 0, buffer.length);
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Image not Saved", Toast.LENGTH_SHORT)
                            .show();
                }
                values.put("customerID", custID);
                values.put("activestat","1");
                long i = db.insert("customer", null, values);
                Log.i("Insert", i + "");
                // Insert into database successfully.
            }
            else{

                try {
                    File folder = new File(Environment.getExternalStorageDirectory() + "/BodyZoneBackUp/image_data");
                    boolean success = true;
                    if (!folder.exists()) {
                        success = folder.mkdirs();
                    }
                    String outFileName = Environment.getExternalStorageDirectory() + "/BodyZoneBackUp/image_data/" + custIDUpdate;
                    values.put("img", outFileName);
                    OutputStream output = new FileOutputStream(outFileName);
                    output.write(buffer, 0, buffer.length);
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Image not Saved", Toast.LENGTH_SHORT)
                            .show();
                }

                long i = db.update("customer",values,"customerID="+custIDUpdate,null);
                Log.i("Update", i + "");
                // Updated into database successfully.
            }
            db.setTransactionSuccessful();

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            if (mode_operation.equals("insert")) {
                SharedPreferences.Editor mEditor = sharedpreferences.edit();
                String newcustID = String.valueOf(Integer.parseInt(custID) + 1);
                mEditor.putString("customerID", newcustID).commit();
                showMessage("Success", "Record added " + custID);
            }
            else{
                showMessage("Success","Record Updated Successfully");
            }
        }

        //Send Message To User

        if (mode_operation.equals("insert")) {
            String phoneNo = phoneField.getText().toString();
            String nameCustomer = nameField.getText().toString();
            String message = "Dear " + nameCustomer + ", Thank you for joining BodyZone Gym! Have a Happy and Healthy Life!";
            if (phoneNo.length() > 0 && message.length() > 0) {
                sendSms(phoneNo, message);
            } else
                Toast.makeText(getBaseContext(),
                        "Please enter both phone number and message.",
                        Toast.LENGTH_SHORT).show();

            clearText();
        }

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


    public void showMessage(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    public void clearText()
    {
        nameField.setText("");
        fatherField.setText("");
        dobField.setText("");
        heightField.setText("");
        weightField.setText("");
        joinField.setText("");
        phoneField.setText("");
        addField.setText("");
        feeField.setText("");
        nameField.requestFocus();
        profilepicView.setImageResource(R.drawable.ic_launcher_background);
    }

}


