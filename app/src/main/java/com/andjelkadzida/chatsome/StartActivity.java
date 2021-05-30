package com.andjelkadzida.chatsome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        if(firebaseUser != null)
        {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}