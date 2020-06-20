package com.example.resource;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class otpActivity extends AppCompatActivity {

    Button verify;
    EditText phonenoentered;
    ProgressBar progressBar;
    String phoneno;
    TextView setname;
    String nameentered;
    DatabaseReference reference;
    String verificationcodesentbysystem;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor meditor;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Calendar calendar = Calendar.getInstance();
        String Month = DateFormat.getDateInstance(DateFormat.MONTH_FIELD).format(calendar.getTime());

        TextView textMonth = findViewById(R.id.date);
        textMonth.setText(Month);

        sharedPreferences=getSharedPreferences("resources",Context.MODE_PRIVATE);
        meditor=sharedPreferences.edit();

        verify=(Button) findViewById(R.id.verify);
        phonenoentered=(EditText)findViewById(R.id.otp);
        progressBar=(ProgressBar)findViewById(R.id.progress_Bar);
        progressBar.setVisibility(View.VISIBLE);
        setname=(TextView)findViewById(R.id.settingname);
        nameentered=getIntent().getStringExtra("name");

        setname.setText("Hi, "+nameentered);

        reference = FirebaseDatabase.getInstance().getReference("Users");


        phoneno=getIntent().getStringExtra("phoneno");

        sendcode(phoneno);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code=phonenoentered.getText().toString();
                if(code.isEmpty() || code.length()<6){
                    phonenoentered.setError("wrong OTP");
                    phonenoentered.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.INVISIBLE);
                verifycode(code);
            }
        });

    }

    private void sendcode(String phoneno) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneno,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationcodesentbysystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code=phoneAuthCredential.getSmsCode();
            if(code!=null){
                progressBar.setVisibility(View.VISIBLE);
                verifycode(code);
            }
        }


        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(otpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifycode(String codeByUser) {
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationcodesentbysystem,codeByUser);
        SignInUserBycredentials(credential);

    }

    private void SignInUserBycredentials(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(otpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent=new Intent(otpActivity.this,resourceActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    Userhelperclass userhelperclass=new Userhelperclass(nameentered,phoneno);
                    reference.child(phoneno).setValue(userhelperclass);

                    meditor.putBoolean("isloggedin",true).apply();

                }
            }
        });
    }
}