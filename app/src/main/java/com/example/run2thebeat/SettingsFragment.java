package com.example.run2thebeat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import static android.app.Activity.RESULT_OK;


public class SettingsFragment extends Fragment {
    private static final int PICK_IMAGE = 100;
    private String m_Email = "";
    private String m_Password = "";
    private String m_NewEmail = "";
    private String m_NewPassword = "";
    private MutableLiveData<String> liveDataEmail = new MutableLiveData<String>();
    private MutableLiveData<String> liveDataPassword = new MutableLiveData<String>();
    private FirebaseUser currentUser;
    private FirebaseAuth firebase = FirebaseAuth.getInstance();
    private String TAG = "SettingsFaragment";
    private Uri imageUri;
    private ImageButton profilePic;
    private SharedPreferences myPrefs;
    private TextView runningGoal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        profilePic = view.findViewById(R.id.profile_pic);
//        loadSavedProfilePic();
//        profilePic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openGallery();
//            }
//        });

        Button signoutBtn = view.findViewById(R.id.signout);
        currentUser = ShowPlaylistsFragment.currentUser;

        String email = currentUser.getEmail();
        TextView currentEmail = view.findViewById(R.id.current_email);
        currentEmail.setText(email);

        myPrefs = getActivity().getSharedPreferences("GOAL_PREF", Context.MODE_PRIVATE);
        String current_goal = myPrefs.getString(currentUser.getEmail(), "Goal not set");
        runningGoal = view.findViewById(R.id.current_goal);
        runningGoal.setText(current_goal);

        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sureWantLogout();
            }
        });

        LinearLayout changeEmail = view.findViewById(R.id.edit_email);
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEmailDialog();

                liveDataEmail.observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        if (!(m_Email.equals("")) && !(m_Password.equals("")) && !(m_NewEmail.equals(""))) {
                            changeMail();
                            m_Email = "";
                        }
                    }
                });
            }
        });

        LinearLayout changePassword = view.findViewById(R.id.edit_password);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordDialog();

                liveDataPassword.observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        if (!(m_Email.equals("")) && !(m_Password.equals("")) && !(m_NewPassword.equals(""))) {
                            changePass();
                            m_Email = "";
                        }
                    }
                });
            }
        });

        LinearLayout runningGoal = view.findViewById(R.id.running_goal);
        runningGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRunningGoalDialog();
            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    public void sureWantLogout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        alertDialogBuilder
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, delete message
                        FirebaseAuth.getInstance().signOut();
                        if (getActivity() != null) {
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


    public void changeEmailDialog() {
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


    public void changePasswordDialog() {
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


    public void changeMail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast toast = Toast.makeText(getContext(), "Couldn't perform action. Please reassign and try again", Toast.LENGTH_LONG);
            ViewGroup toastLayout = (ViewGroup) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();

            FirebaseAuth.getInstance().signOut();
            if (getActivity() != null) {
                getActivity().finish();
            }
            startActivity(new Intent(getActivity(), MainActivity.class));
        } else {
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
                                    Toast toast = Toast.makeText(getContext(), "Email updated successfully", Toast.LENGTH_LONG);
                                    ViewGroup toastLayout = (ViewGroup) toast.getView();
                                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                                    toastTV.setTextSize(20);
                                    toast.show();
                                } else {
                                    Log.d(TAG, "Error email not updated");
                                    Toast toast = Toast.makeText(getContext(), "Could not update email, try again", Toast.LENGTH_LONG);
                                    ViewGroup toastLayout = (ViewGroup) toast.getView();
                                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                                    toastTV.setTextSize(20);
                                    toast.show();
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, "Error auth failed");
                        Toast toast = Toast.makeText(getContext(), "Could not update email, try again", Toast.LENGTH_LONG);
                        ViewGroup toastLayout = (ViewGroup) toast.getView();
                        TextView toastTV = (TextView) toastLayout.getChildAt(0);
                        toastTV.setTextSize(20);
                        toast.show();
                    }
                }
            });
        }
    }


    public void changePass() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast toast = Toast.makeText(getContext(), "Couldn't perform action. Please reassign and try again", Toast.LENGTH_LONG);
            ViewGroup toastLayout = (ViewGroup) toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize(20);
            toast.show();

            FirebaseAuth.getInstance().signOut();
            if (getActivity() != null) {
                getActivity().finish();
            }
            startActivity(new Intent(getActivity(), MainActivity.class));
        } else {
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
                                    Toast toast = Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_LONG);
                                    ViewGroup toastLayout = (ViewGroup) toast.getView();
                                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                                    toastTV.setTextSize(20);
                                    toast.show();
                                } else {
                                    Log.d(TAG, "Error password not updated");
                                    Toast toast = Toast.makeText(getContext(), "Could not update password, try again", Toast.LENGTH_LONG);
                                    ViewGroup toastLayout = (ViewGroup) toast.getView();
                                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                                    toastTV.setTextSize(20);
                                    toast.show();
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, "Error auth failed");
                        Toast toast = Toast.makeText(getContext(), "Could not update password, try again", Toast.LENGTH_LONG);
                        ViewGroup toastLayout = (ViewGroup) toast.getView();
                        TextView toastTV = (TextView) toastLayout.getChildAt(0);
                        toastTV.setTextSize(20);
                        toast.show();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        myPrefs = getActivity().getSharedPreferences("PREF", Context.MODE_PRIVATE);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            SharedPreferences.Editor editor = myPrefs.edit();
            String uri = imageUri.toString();
            editor.putString("PROFILE_PIC", imageUri.toString());
            editor.apply();
        }
    }

//    private void loadSavedProfilePic() {
//        myPrefs = getActivity().getSharedPreferences("PREF", Context.MODE_PRIVATE);
//        Uri defaultImageUri = Uri.parse("android.resource://Run2theBeat/" + R.drawable.ic_person_white_24dp);
//        String imageURI = myPrefs.getString("PROFILE_PIC", null);
//        if(imageURI == null){
//            profilePic.setImageResource(R.drawable.ic_person_white_24dp);
//        }else{
//            Uri imgUri = Uri.parse(imageURI);
//            Glide.with(getContext()).load(imgUri).into(profilePic);
//        }
//
////        Bitmap bitmap = BitmapFactory.decodeFile(imageURI);
////        profilePic.setImageURI(null);
////        profilePic.setImageBitmap(bitmap);
//    }

    private void getRunningGoalDialog() {
        myPrefs = getActivity().getSharedPreferences("GOAL_PREF", Context.MODE_PRIVATE);
        androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        final EditText edittext = new EditText(getContext());
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        alert.setMessage("Enter running goal:");
        alert.setView(edittext);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String goal = edittext.getText().toString();
                if(goal.equals("")){
                    Toast.makeText(getContext(), "Invalid distance", Toast.LENGTH_SHORT).show();
                }else{
                    SharedPreferences.Editor editor = myPrefs.edit();
                    editor.putString(currentUser.getEmail(), goal);
                    editor.apply();
                    runningGoal.setText(goal + " km");
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();
    }


}
