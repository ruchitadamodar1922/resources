package com.example.resource;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Date;
import java.text.DateFormat;
import java.util.Calendar;

public class signupActivity extends AppCompatActivity {

    Button login;
    View BottomSheetView;
    EditText regname = null;
    EditText regphone = null;
    DatabaseReference reference;
    RelativeLayout bottomsheetlayout;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        login = (Button) findViewById(R.id.login);

        regname = (EditText) findViewById(R.id.name);
        regphone = (EditText) findViewById(R.id.phone);

        Calendar calendar = Calendar.getInstance();
        String Month = DateFormat.getDateInstance(DateFormat.MONTH_FIELD).format(calendar.getTime());

        TextView textMonth = findViewById(R.id.month);
        textMonth.setText(Month);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validatename() && !validatephone()) {
                    return;
                }
                    String name = regname.getText().toString();
                    String phone = regphone.getText().toString();

                    Intent intent = new Intent(signupActivity.this, otpActivity.class);
                    intent.putExtra("phoneno", phone);
                    intent.putExtra("name",name);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

            }
        });

    }

    private Boolean validatename() {
        String name = regname.getText().toString();
        if(name.isEmpty()){
            Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }
    private Boolean validatephone() {
        String phone = regphone.getText().toString();
        if(phone.isEmpty()){
            Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

}
