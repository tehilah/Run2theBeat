package com.example.run2thebeat;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.ekalips.fancybuttonproj.FancyButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private FancyButton loginBtn;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Button signupBtn = view.findViewById(R.id.choice_sign_up);
        loginBtn = view.findViewById(R.id.btn_sign_in);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new SignupFragment())
                        .addToBackStack(null) // <-- does not make any difference if left out
                        .commit();

            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginBtn.isExpanded()) {
                    loginBtn.collapse();
                    loginBtn.setBackgroundResource(0);
                    if (email.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "Invalid email", Toast.LENGTH_SHORT).show();
                        loginBtn.expand();
                    } else if (password.getText().toString().equals("")) {
                        Toast.makeText(getContext(), "Invalid password", Toast.LENGTH_SHORT).show();
                        loginBtn.expand();
                    } else {
                        login();
                    }
                } else {
                    loginBtn.expand();
                }
            }
        });

    }


    private void login() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getActivity(), NavigationBarActivity.class);
//                            intent.putExtra("user", user);
                            startActivity(intent);
                            getActivity().finish();

                        } else {
                            loginBtn.expand();
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "onComplete: " + email.getText().toString() + ", " + password.getText().toString());

                        }

                    }
                });
    }
}

