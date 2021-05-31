package com.andjelkadzida.chatsome;

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
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity
{

    //Widgeti
    EditText mail;
    Button btnReset;

    //Firebase
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //Inicijalizacija widgeta
        mail = findViewById(R.id.mailReset);
        btnReset = findViewById(R.id.btnPasswordReset);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //Postavljam event lister kada korisnik klikne na dugme
        btnReset.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Definisem string email koji ce iz polja EditText pokupiti tekst i pretvoriti ga u string
                String email = mail.getText().toString();

                //Proveravam da li je korisnik uneo mail i ako nije prikazujem kratku poruku da je email obavezan
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(ResetPasswordActivity.this, "E-mail field is mandatory!", Toast.LENGTH_SHORT).show();
                }
                //Ako je korisnik uneo svoj email pozivam instancu klase FirebaseAuth i njenu metodu sendPasswordResetEmail.
                //Metodi prosledjujem mail koji je korisnik uneo i dodajem dogadjaj onCompleteListener tj sta treba da se izvrsi dalje
                else
                {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            //Ako je zadatak uspesno izvrsen, prikazujem korisniku poruku da proveri postansko sanduce svoje e-mail adrese
                            //Pozivam metodu startActivity i prosledjujem joj novu instancu klase intent kako bi pokrenula aktivnost za logovanje korisnika
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ResetPasswordActivity.this, "Please check your inbox", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                            }
                        }
                    });
                }

            }
        });
    }
}