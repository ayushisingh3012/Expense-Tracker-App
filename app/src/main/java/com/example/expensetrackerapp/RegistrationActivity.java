package com.example.expensetrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {
    private EditText mEmail;
    private  EditText mPass;
    private Button btnReg;
    private TextView mSignin;
    private static final String TAG="EmailPassword";

    private ProgressDialog mDialog;

    //Firebase

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth=FirebaseAuth.getInstance();

        mDialog=new ProgressDialog(this);
        registration();
    }
    private void  registration()
    {
        mEmail=findViewById(R.id.email_reg);
        mPass=findViewById(R.id.password_reg);
        btnReg=findViewById(R.id.btn_reg);
        mSignin=findViewById(R.id.signin_here);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mEmail.getText().toString().trim();
                String pass=mPass.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    mEmail.setError("Email Required ......");
                    return;
                }
                if (TextUtils.isEmpty(pass))
                {
                    mPass.setError("Password Required......");
                    return;
                }

                mDialog.setMessage("Processing...");


                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registration Complete",Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder builder
                                    = new AlertDialog
                                    .Builder(RegistrationActivity.this);

                            // Set the message show for the Alert time
                            builder.setMessage("Welcome");
                            builder.setTitle("Registration Successful");

                            AlertDialog alertDialog = builder.create();

                            // Show the Alert Dialog box
                            alertDialog.show();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        }
                        else{
                            mDialog.dismiss();
                            Log.w(TAG,"signUpWithEmail.failure",task.getException());
                            AlertDialog.Builder builder
                                    = new AlertDialog
                                    .Builder(RegistrationActivity.this);

                            // Set the message show for the Alert time
                            builder.setMessage("There is some error");
                            builder.setTitle("Registration Failed");

                            AlertDialog alertDialog = builder.create();

                            // Show the Alert Dialog box
                            alertDialog.show();
                            Toast.makeText(getApplicationContext(),"Registration Failed...",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        mSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }
}