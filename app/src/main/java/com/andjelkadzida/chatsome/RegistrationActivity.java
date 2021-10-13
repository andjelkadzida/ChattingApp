package com.andjelkadzida.chatsome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity
{

    EditText usernameText, passwordText, emailText;
    Button registerButton;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        usernameText = findViewById(R.id.username);
        passwordText = findViewById(R.id.password);
        emailText = findViewById(R.id.email);
        registerButton = findViewById(R.id.register);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void RegisterClick(View view)
    {
        String username = usernameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
        {
            Toast.makeText(RegistrationActivity.this, "All fields are mandatory!", Toast.LENGTH_SHORT).show();
        }
        else if(passwordText.getText().length()<8)
        {
            Toast.makeText(RegistrationActivity.this, "Password must contain at least 8 characters!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            register(username, email, password);
        }
    }

    private void register(final String username, String email, String password)
    {
        progressDialog = new ProgressDialog(this, R.style.CustomDialog);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            progressDialog.setTitle("Registration");
                            progressDialog.setIcon(R.drawable.ic_key);
                            progressDialog.setMessage("Registration in progress... Please wait...");
                            progressDialog.setCanceledOnTouchOutside(true);
                            progressDialog.show();

                            FirebaseUser fireUser = firebaseAuth.getCurrentUser();
                            String userId = fireUser.getUid();

                            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            HashMap<String, String> usersMap = new HashMap<>();
                            usersMap.put("id", userId);
                            usersMap.put("username", username);
                            usersMap.put("imageUrl", "default");
                            usersMap.put("status", "offline");
                            usersMap.put("search", username.toLowerCase());

                            databaseReference.setValue(usersMap).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                   if(task.isSuccessful())
                                   {
                                       Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                       startActivity(intent);
                                       finish();
                                   }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(RegistrationActivity.this, "Invalid e-mail or password!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if((progressDialog!=null && progressDialog.isShowing()))
        {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if((progressDialog!=null && progressDialog.isShowing()))
        {
            progressDialog.dismiss();
        }
    }
}