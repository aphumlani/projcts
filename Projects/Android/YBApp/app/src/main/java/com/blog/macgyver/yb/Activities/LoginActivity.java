package com.blog.macgyver.yb.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blog.macgyver.yb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //views
    private EditText login_email, login_password;
    private Button login_button;
    private TextView mLog_Reg;

    //prgressDialog
    private ProgressDialog mDialog;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //view
        login_email = findViewById(R.id.email_login_id);
        login_password = findViewById(R.id.password_login_id);
        login_button = findViewById(R.id.login_button_id);
        mLog_Reg = findViewById(R.id.register_login_id);

        //progressDialog
        mDialog = new ProgressDialog(this);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //clickListeners
        login_button.setOnClickListener(this);
        mLog_Reg.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser != null){
            updateUItoMain();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.login_button_id:
                login();
                break;

            case R.id.register_login_id:
                updateUItoRegister();
                break;

        }
    }

    public void login(){

        String loginemail = login_email.getText().toString().trim();
        String loginpassword = login_password.getText().toString().trim();

        if (!validate()){
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(loginemail,loginpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    updateUItoMain();
                    Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                }

                else{

                    String Error = task.getException().getMessage();
                    Toast.makeText(LoginActivity.this, "ERROR " + Error, Toast.LENGTH_SHORT).show();
                }

                hideProgressDialog();

            }
        });

    }

    public boolean validate(){

        boolean valid = true;

        String loginemail = login_email.getText().toString().trim();

        if (TextUtils.isEmpty(loginemail)){

            login_email.setError("Required Field");
            login_email.requestFocus();

            valid = false;
        }

        String loginpassword = login_password.getText().toString().trim();

        if (TextUtils.isEmpty(loginpassword)){
            login_password.setError("Required Field");
            login_password.requestFocus();

            valid = false;
        }

        return valid;
    }

    public void updateUItoMain(){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    public void updateUItoRegister(){
        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
        finish();
    }

    public void showProgressDialog(){

        mDialog.setMessage("Login...");
        mDialog.show();
    }

    public void hideProgressDialog(){

        mDialog.dismiss();
    }
}
