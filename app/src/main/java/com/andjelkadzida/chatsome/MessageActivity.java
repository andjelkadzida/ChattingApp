package com.andjelkadzida.chatsome;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.andjelkadzida.chatsome.model.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class MessageActivity extends AppCompatActivity
{

    //Widgeti
    TextView username;
    ImageView userImage;

    RecyclerView recyclerView;
    EditText messageText;
    ImageButton btnSend;

    //Firebase, databseReference i intent
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //Inicijalizacija widgeta
        userImage = findViewById(R.id.userProfileImage);
        username = findViewById(R.id.usernameV);
        messageText = findViewById(R.id.textSend);
        btnSend = findViewById(R.id.btnSend);


        //Toolbar
      /*  Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });*/


        intent = getIntent();
        String userId = intent.getStringExtra("userid");

        //Uzimanje treuntog korisnika
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
            {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());

                if(user.getImageUrl().equals("default"))
                {
                    userImage.setImageResource(R.mipmap.ic_launcher);
                }
                else
                {
                    Glide.with(MessageActivity.this).load(user.getImageUrl()).into(userImage);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error)
            {

            }
        });


        //Implementiranje dugmeta za slanje poruke
        btnSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Iz editText widgeta se uzima uneti tekst, konvertuje u string i pakuje u promenljivu istog tipa
                String message = messageText.getText().toString();
                //Ako TextEdit nije prazan, tj ako je korisnik uneo poruku, poziva se funkcija za slanje poruke
                if(!message.equals(""))
                {
                    sendMessage(firebaseUser.getUid(), userId, message);
                }
                //Ako korisnik nije uneo poruku, a klikne na dugme za slanje poruke, dobija obavestenje da je potrebno da unese poruku
                else
                {
                    Toast.makeText(MessageActivity.this, "Enter your message...", Toast.LENGTH_SHORT).show();
                }
                //Nakon sto je poruka poslata, EditText se prazni i spreman je za unos nove poruke
                messageText.setText("");
            }
        });


    }

    private void sendMessage(String sender, String receiver, String message)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> map = new HashMap<>();
        map.put("sender", sender);
        map.put("receiver", receiver);
        map.put("message", message);

        ref.child("Chats").push().setValue(map);
    }


    private void setSupportActionBar(Toolbar toolbar)
    {
    }
}