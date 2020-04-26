package com.aina.dummydatabase;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class StartActivity extends AppCompatActivity {

    TextView userid,passwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        userid = (TextView) findViewById(R.id.useridView);
        passwd = (TextView) findViewById(R.id.passwdView);
        userid.setText("abc");
        passwd.setText("123");
    }

    public void onClickLogin(View v){
        if (userid.getText().toString().equals("abc") && passwd.getText().toString().equals("123")){
            Intent myInt = new Intent(getBaseContext(), Functionality.class);
            startActivity(myInt);
        }
        else{
            showMessage("Wrong Login Credententials","");
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

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
