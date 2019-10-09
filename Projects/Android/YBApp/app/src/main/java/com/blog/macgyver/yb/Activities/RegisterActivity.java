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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    //views
    private EditText reg_email, reg_pass, reg_cpass;
    private Button reg_bttn;
    private TextView mLog_Reg;

    //progressDialog
    private ProgressDialog progressDialog;

    //firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //views
        reg_email = findViewById(R.id.email_register_id);
        reg_pass = findViewById(R.id.password_register_id);
        reg_cpass = findViewById(R.id.confirm_password_register_id);
        reg_bttn = findViewById(R.id.register_button_id);
        mLog_Reg = findViewById(R.id.login_register_id);

        //progressDialog
        progressDialog = new ProgressDialog(this);

        //firebase
        mAuth = FirebaseAuth.getInstance();

        reg_bttn.setOnClickListener(this);
        mLog_Reg.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_button_id:
                createUser();
                break;

            case R.id.login_register_id:
                updateUItoLogin();
                break;
        }
    }

    private void createUser() {

        String register_email = reg_email.getText().toString().trim();
        String register_pass = reg_pass.getText().toString().trim();

        if (!validate()){
            return;
        }


        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(register_email,register_pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            updateUItoSetup();
                        }

                        else{

                            String Error = task.getException().getMessage();

                            Toast.makeText(RegisterActivity.this, "Error : " + Error, Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null){

        }
    }

    public boolean validate(){

        boolean valid = true;

        String register_email = reg_email.getText().toString().trim();

        if (TextUtils.isEmpty(register_email)){

            reg_email.setError("Required Field");
            reg_email.requestFocus();

            valid = false;
        }

        String register_pass = reg_pass.getText().toString().trim();

        if (TextUtils.isEmpty(register_pass)){
            reg_pass.setError("Required Field");
            reg_pass.requestFocus();

            valid = false;
        }

        String register_cpass = reg_cpass.getText().toString().trim();

        if (TextUtils.isEmpty(register_cpass)){
            reg_cpass.setError("Required Field");
            reg_cpass.requestFocus();

            valid = false;
        }

        return valid;
    }

    public void updateUItoLogin(){
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    public void updateUItoMain(){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    public void updateUItoSetup(){
        startActivity(new Intent(getApplicationContext(),SetupActivity.class));
        finish();
    }

    public void showProgressDialog(){

        progressDialog.setMessage("Creating User");
        progressDialog.show();
    }

    public void hideProgressDialog(){

        progressDialog.dismiss();
    }
}
