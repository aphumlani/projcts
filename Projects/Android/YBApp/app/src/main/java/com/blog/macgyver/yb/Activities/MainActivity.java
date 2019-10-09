package com.blog.macgyver.yb.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.blog.macgyver.yb.Fragments.AccountFragment;
import com.blog.macgyver.yb.Fragments.HomeFragment;
import com.blog.macgyver.yb.Fragments.NotificationFragment;
import com.blog.macgyver.yb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    //view
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private BottomNavigationView navigationView;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore databaseCloud;
    private FirebaseUser currentUser;

    //string
    private String current_user_id;

    //fragments
    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment userAccountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //views
        toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Younglings Blog");

        fab = findViewById(R.id.floatingActionButton);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        databaseCloud = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        //string
        current_user_id = mAuth.getCurrentUser().getUid();
        if (mAuth.getCurrentUser() != null) {

            navigationView = findViewById(R.id.mainBottomNav);
            //fragment
            homeFragment = new HomeFragment();
            notificationFragment = new NotificationFragment();
            userAccountFragment = new AccountFragment();

            replaceFragment(homeFragment);

            navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.bottom_home_menu:
                            replaceFragment(homeFragment);
                            return true;

                        case R.id.bottom_notification_menu:
                            replaceFragment(notificationFragment);
                            return true;

                        case R.id.bottom_user_menu:
                            replaceFragment(userAccountFragment);
                            return true;
                    }

                    return false;
                }
            });
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUItoNewPost();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null){
            updateUItoLogin();
        }

        else{

            databaseCloud.collection("Users").document(current_user_id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    try {

                        if (task.isSuccessful()){
                            if (!task.getResult().exists()){

                                updateUItoSetup();

                            }

                            else {
                                assert false;
                                String Error = task.getException().getMessage();
                                Toast.makeText(MainActivity.this, "ERROR" + Error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_logout_btn:
                logout();
                return true;

            case R.id.action_Settings_btn:
                updateUItoSetup();
                return true;

                default:
                    return false;
        }

    }

    public void logout(){
        mAuth.signOut();
        updateUItoLogin();

    }

    public void updateUItoLogin(){

        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }

    public void updateUItoSetup(){

        startActivity(new Intent(getApplicationContext(),SetupActivity.class));
    }

    public void updateUItoNewPost(){

        startActivity(new Intent(getApplicationContext(),NewPostActivity.class));
    }

    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();

    }
}


