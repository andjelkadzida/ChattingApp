package com.andjelkadzida.chatsome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class StartActivity extends AppCompatActivity
{

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button btnRedirectToLogin = findViewById(R.id.btnRedirectToLogin);
        Button btnRedirectToRegister = findViewById(R.id.btnRedirectToRegister);
        Button btnRedirectToPhoneLogin = findViewById(R.id.btnRegirectToPhoneLogin);

        //Redirekcija korisnika na login stranicu
        btnRedirectToLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //Redirekcija korisnika na stranicu za login koriscenjem mobilnog telefona
        btnRedirectToPhoneLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(StartActivity.this, PhoneLoginActivity.class);
                startActivity(intent);
            }
        });

        //Redirekcija korisnika na stranicu za registraciju
        btnRedirectToRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(StartActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Ako je korisnik vec ulogovan aplikacija ce ga prebaciti na glavnu stranicu
        //Za svaki slucaj proveravam da li korisnik i njegovi podaci postoje u bazi ili je samo na listi autentifikovanih korisnika
        //U slucaju da ne postoji u bazi brisem ga iz autentifikovanih korisnika
        if(firebaseUser!=null)
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

            reference.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                    {
                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        FirebaseAuth.getInstance().getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void unused)
                            {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error)
                {

                }
            });
        }
    }
}