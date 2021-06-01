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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity
{

    //Phone provajder
    PhoneAuthProvider.OnVerificationStateChangedCallbacks onVerificationStateChangedCallbacks;
    //Firebase
    FirebaseAuth firebaseAuth;
    //Db reference
    DatabaseReference databaseReference;

    String verificationId;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        EditText phoneNumberEdit = findViewById(R.id.phoneNumber);
        EditText verificationCodeEdit = findViewById(R.id.verificationCode);
        Button btnVerify = findViewById(R.id.btnVerify);
        Button btnSendCode = findViewById(R.id.btnSendCode);

        ProgressDialog progressDialog = new ProgressDialog(this, R.style.CustomDialog);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        //Dogadjaj koji se okida kada korisnik zatrazi kod za verifikaciju broja telefona
        btnSendCode.setOnClickListener(v -> {

            String phoneNumber = phoneNumberEdit.getText().toString();

            if(TextUtils.isEmpty(phoneNumber))
            {
                Toast.makeText(PhoneLoginActivity.this,"Please enter phone number including country number", Toast.LENGTH_SHORT).show();
            }
            else
            {
                progressDialog.setTitle("Phone verification");
                progressDialog.setIcon(R.drawable.ic_login);
                progressDialog.setMessage("Please wait, phone verification in progress...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(onVerificationStateChangedCallbacks).build();

                PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
            }
        });

        //Dogadjaj koji se okida kada korisnik unese verifikacioni kod i klikne na udgme Verify
        btnVerify.setOnClickListener(v -> {

            btnSendCode.setVisibility(View.INVISIBLE);
            phoneNumberEdit.setVisibility(View.INVISIBLE);

            String verificationCode = verificationCodeEdit.getText().toString();

            if(TextUtils.isEmpty(verificationCode))
            {
                Toast.makeText(PhoneLoginActivity.this, "Please insert verification code you received", Toast.LENGTH_SHORT).show();
            }
            else
            {
                progressDialog.setTitle("Code verification");
                progressDialog.setMessage("Please wait, code verification in progress...");
                progressDialog.setCanceledOnTouchOutside(false);
               // progressDialog.show();

                PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, verificationCode);
                signInWithPhoneCredentials(phoneAuthCredential);
            }
        });

        onVerificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
            {
                   signInWithPhoneCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e)
            {
                Toast.makeText(PhoneLoginActivity.this, "Phone number " + phoneNumberEdit.getText().toString() + " is not valid!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                phoneNumberEdit.setText("");
                btnSendCode.setVisibility(View.VISIBLE);
                phoneNumberEdit.setVisibility(View.VISIBLE);
                btnVerify.setVisibility(View.INVISIBLE);
                verificationCodeEdit.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String verifiyId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken)
            {
                verificationId = verifiyId;
                progressDialog.dismiss();

                Toast.makeText(PhoneLoginActivity.this, "Verification code sent on phone number: " + phoneNumberEdit.getText().toString(), Toast.LENGTH_SHORT).show();

                btnSendCode.setVisibility(View.INVISIBLE);
                phoneNumberEdit.setVisibility(View.INVISIBLE);
                btnVerify.setVisibility(View.VISIBLE);
                verificationCodeEdit.setVisibility(View.VISIBLE);

            }
        };
    }

    private void signInWithPhoneCredentials(PhoneAuthCredential authCredential)
    {
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, task -> {
            if(task.isSuccessful())
            {
                AuthCredential credential = PhoneAuthProvider.getCredential(authCredential.getProvider(), Objects.requireNonNull(authCredential.getSmsCode()));
                final String currentId = firebaseAuth.getCurrentUser().getUid();

                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task1 ->
                {
                   if(!task1.isSuccessful())
                   {
                       String msg = task1.getException().toString();
                       Toast.makeText(PhoneLoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                       return;
                   }
                   if(task1.getResult()!=null)
                   {
                       boolean isNewPhone = task.getResult().getAdditionalUserInfo().isNewUser();
                       if(isNewPhone)
                       {
                           FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                           String userId = firebaseUser.getUid();
                           String username = task.getResult().getUser().getPhoneNumber();
                           databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                           //HashMap
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
                                       //Pokretanje glavne aktivnosti koriscenjem Intenta
                                       //Kreiramo instancu klase intent
                                       Intent intent = new Intent(PhoneLoginActivity.this, MainActivity.class);
                                       //Metodi startActivity prosledjujemo instancu klase Intent koju smo kreirali
                                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                       startActivity(intent);
                                       finish();
                                   }
                                   else
                                   {
                                       Toast.makeText(PhoneLoginActivity.this, "User " + username + " login failed!", Toast.LENGTH_LONG).show();
                                   }
                                   progressDialog.dismiss();
                               }
                           });
                       }
                       else
                       {
                           Toast.makeText(PhoneLoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                           Intent intent = new Intent(PhoneLoginActivity.this, MainActivity.class);
                           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           startActivity(intent);
                           finish();
                       }
                   }
                });
            }
            else
            {
                Toast.makeText(PhoneLoginActivity.this, "Phone number verification failed!", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStart()
    {
        super.onStart();

        //Provera da li je korisnik ulogovan i ako jeste prosledjuje se na glavnu aktivnost
        FirebaseUser loggedUser = firebaseAuth.getCurrentUser();

        if(loggedUser != null)
        {
            startActivity(new Intent(PhoneLoginActivity.this, MainActivity.class));
        }
        else
        {
            startActivity(new Intent(PhoneLoginActivity.this, StartActivity.class));
        }
    }
}