package com.example.run2thebeat;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();



        final LoginFragment loginFragment = new LoginFragment();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            //todo: update UI
//        updateUI(currentUser);
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, loginFragment)
                    .commit();
        }




    }



}
