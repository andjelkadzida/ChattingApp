package com.andjelkadzida.chatsome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.andjelkadzida.chatsome.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity
{

    //Firebase
    FirebaseUser currentUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Iz firebase baze podataka uzimam trenutno ulogovanog korisnika i smestam ga u currentUser
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
            {
                User user = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error)
            {

            }
        });
    }


    //Funkcija Logout
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //U slucaju da korisnik u meniju izabere opciju logout
    //Iz firebase baze pomocu FirebaseAuth klase uzimam instancu i korisnim funkciju za odjavljivanje
    //Nakon sto se korisnik odjavi, pokrecem novu aktivnost tako sto kreiram instancu klase intent
    //Korisnika iz Main tj glavne aktivnosti preusmeravam na Login aktivnost.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
        }
        return false;
    }
}