package com.example.run2thebeat;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignupFragment extends Fragment {
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button signupBtn;

    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        Button loginBtn = view.findViewById(R.id.btnLogin);
        signupBtn = view.findViewById(R.id.button3);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment())
                        .addToBackStack(null) // <-- does not make any difference if left out
                        .commit();

            }
        });

        email = view.findViewById(R.id.editText);
        password = view.findViewById(R.id.editText2);
        confirmPassword = view.findViewById(R.id.editText3);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!password.getText().toString().equals(confirmPassword.getText().toString())){
                    Toast.makeText(getActivity(), "passwords don't match", Toast.LENGTH_SHORT).show();
                }
                else if(password.getText().toString().length()<6){
                    Toast.makeText(getActivity(), "The given password is invalid.\n Password should be at least 6 characters", Toast.LENGTH_LONG).show();
                }
                else{
                    createUser();
                }


            }
        });
    }


    private void createUser() {
        mAuth = FirebaseAuth.getInstance();
        Log.d("TAG", "createUser: here");
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
//                            updateUI(null);
                        }
                    }
                });
    }


}
