package com.andjelkadzida.chatsome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity
{

    //Widgeti
    EditText emailText, passText;
    Button loginBtn;

    //Firebase
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicijalizacija widgeta
        emailText = findViewById(R.id.emailLogin);
        passText = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginButton);

        //Firebase autentifikacija
        firebaseAuth = FirebaseAuth.getInstance();

        //Logovanje sam odradila tako sto sam na dugme stavila onClickListener
        loginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Inicijalizacija promenljivih tipa string u koje smestam vrednosti "preuzete" iz editText polja.
                String email = emailText.getText().toString();
                String pass = passText.getText().toString();

                //Provera li su oba polja popunjena
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass))
                {
                    //Ako nisu prikazujem korisniku obavestenje da su oba polja obevezna
                    Toast.makeText(LoginActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //Ako je korisnik popunio oba polja, pozvacu Firebaseovu metodu za login koriscenjem emaila i sifre.
                    firebaseAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<AuthResult> task)
                                {
                                    //Proveravam da li je korisnik uspesno ulogovan.
                                    //Ako jeste, vracam ga na glavnu stranicu
                                    if(task.isSuccessful())
                                    {
                                        //Pokretanje glavne aktivnosti koriscenjem Intenta
                                        //Kreiramo instancu klase intent
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        //Metodi startActivity prosledjujemo instancu klase Intent koju smo kreirali
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                    //Ako logovanje nije bilo uspesno, obavestavam korisnika da postoji problem.
                                    else
                                    {
                                        Toast.makeText(LoginActivity.this, "Login failed! Please try again!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}