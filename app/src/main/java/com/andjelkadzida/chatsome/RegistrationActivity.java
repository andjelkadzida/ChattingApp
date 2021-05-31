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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity
{

    //Widgeti iz xml fajla
    EditText usernameText, passwordText, emailText;
    Button registerButton;

    //Firebase baza konfiguracija
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Inicijalizacija widgeta
        usernameText = findViewById(R.id.username);
        passwordText = findViewById(R.id.password);
        emailText = findViewById(R.id.email);
        registerButton = findViewById(R.id.register);

        //Firebase autentifikacija
        firebaseAuth = FirebaseAuth.getInstance();
    }

    //Povezujemo metodu za registraciju sa dugmetom za registraciju
    public void RegisterClick(View view)
    {
        //Za svaki editText kreiramo string u koji ubacujem vrednosti koje je korisnik uneo u polja
        String username = usernameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        //Proveravam da li su sva polja popunjena
        //Ako je neko polje prazno, obavestavam korisnika da su sva polja obavezna
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
        {
            Toast.makeText(RegistrationActivity.this, "All fields are mandatory!", Toast.LENGTH_SHORT).show();
        }
        //Ako su sva polja popunjena pozivam metodu za registraciju i prosledjujem joj parametre koje mi je dao korisnik
        else
        {
            register(username, email, password);
        }
    }

    //Metoda za registraciju
    //username tj korisnicko ce biti final  i nece moci da se menja, dok se email i password mogu regularno menjati
    private void register(final String username, String email, String password)
    {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            FirebaseUser fireUser = firebaseAuth.getCurrentUser();
                            String userId = fireUser.getUid();

                            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            //HashMap
                            HashMap<String, String> usersMap = new HashMap<>();
                            usersMap.put("id", userId);
                            usersMap.put("username", username);
                            usersMap.put("imageUrl", "default");
                            usersMap.put("status", "offline");
                            usersMap.put("search", username.toLowerCase());

                            //Pokretanje glavne aktivnosti nakon uspesne registracije
                            databaseReference.setValue(usersMap).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                   if(task.isSuccessful())
                                   {
                                       //Pokretanje glavne aktivnosti koriscenjem Intenta
                                       //Kreiramo instancu klase intent
                                       Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                       //Metodi startActivity prosledjujemo instancu klase Intent koju smo kreirali
                                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                       startActivity(intent);
                                       finish();
                                   }
                                }
                            });
                        }
                        //Ako registracija ne uspe, prikazujemo korisniku obavestenje da ista nije uspela.
                        else {
                            Toast.makeText(RegistrationActivity.this, "Invalid e-mail or password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}