package com.example.run2thebeat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import static com.example.run2thebeat.ProgressFragment.CONFIRM_DELETE;

public class SettingsFragment extends Fragment {
    private String m_Email = "";
    private String m_Password ="";
    private String m_NewEmail = "";
    private String m_NewPassword = "";
    private MutableLiveData<String> liveDataEmail = new MutableLiveData<String>();
    private MutableLiveData<String> liveDataPassword = new MutableLiveData<String>();
    private FirebaseUser currentUser;
    private FirebaseAuth firebase = FirebaseAuth.getInstance();
    private String TAG = "SettingsFaragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button signoutBtn = view.findViewById(R.id.signout);
        currentUser = ShowPlaylistsFragment.currentUser;

        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sureWantLogout();
//                FirebaseAuth.getInstance().signOut();
//                if(getActivity() != null){
//                    getActivity().finish();
//                }
//                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        TextView changeEmail = view.findViewById(R.id.edit_email);
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEmailDialog();

                liveDataEmail.observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        if(!(m_Email.equals(""))&& !(m_Password.equals("")) && !(m_NewEmail.equals(""))) {
                            changeMail();
                        }
                    }
                });
            }
        });

        TextView changePassword = view.findViewById(R.id.edit_password);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordDialog();

                liveDataPassword.observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        if(!(m_Email.equals(""))&& !(m_Password.equals("")) && !(m_NewPassword.equals(""))){
                            changePass();
                        }
                    }
                });
            }
        });
    }


    public void sureWantLogout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        alertDialogBuilder
                .setMessage("Are you sure you want to logaout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, delete message
                        FirebaseAuth.getInstance().signOut();
                        if(getActivity() != null){
                            getActivity().finish();
                        }
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close the dialog box and do nothing
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public void changeEmailDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change email address");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText oldMail = new EditText(getContext());
        oldMail.setHint("Enter current Email");
        layout.addView(oldMail);

        final EditText oldPass = new EditText(getContext());
        oldPass.setHint("Enter current password");
        layout.addView(oldPass);

        final EditText newMail = new EditText(getContext());
        newMail.setHint("Enter new Email");
        newMail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        layout.addView(newMail);

        builder.setView(layout);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Email = oldMail.getText().toString();
                m_Password = oldPass.getText().toString();
                m_NewEmail = newMail.getText().toString();
                liveDataEmail.postValue(m_Email);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }


    public void changePasswordDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change password");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText oldMail = new EditText(getContext());
        oldMail.setHint("Enter current Email");
        layout.addView(oldMail);

        final EditText oldPass = new EditText(getContext());
        oldPass.setHint("Enter current password");
        layout.addView(oldPass);

        final EditText newPass = new EditText(getContext());
        newPass.setHint("Enter new password");
        layout.addView(newPass);

        builder.setView(layout); // Again this is a set method, not add

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Email = oldMail.getText().toString();
                m_Password = oldPass.getText().toString();
                m_NewPassword = newPass.getText().toString();
                liveDataPassword.postValue(m_Email);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    public void changeMail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(m_Email, m_Password);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updateEmail(m_NewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email updated");
                                Toast.makeText(getContext(),"Email updated successfully",Toast.LENGTH_LONG).show();
                            } else {
                                Log.d(TAG, "Error email not updated");
                                Toast.makeText(getContext(),"Could not update email, try again",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "Error auth failed");
                    Toast.makeText(getContext(),"Could not update email, try again",Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    public void changePass(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(m_Email, m_Password);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(m_NewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Password updated");
                                Toast.makeText(getContext(),"Password updated successfully",Toast.LENGTH_LONG).show();
                            } else {
                                Log.d(TAG, "Error password not updated");
                                Toast.makeText(getContext(),"Could not update password, try again",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "Error auth failed");
                    Toast.makeText(getContext(),"Could not update password, try again",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}
