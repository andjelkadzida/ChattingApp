package com.andjelkadzida.chatsome;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import com.andjelkadzida.chatsome.adapter.MessageAdapter;
import com.andjelkadzida.chatsome.model.Chat;
import com.andjelkadzida.chatsome.model.Users;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    MessageAdapter messageAdapter;
    List<Chat> chats;

    RecyclerView viewRecycle;
    String userId;

    ValueEventListener seenListener;


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

        //RecycleViewer
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        intent = getIntent();
        userId = intent.getStringExtra("userid");

        //Uzimanje treuntog korisnika
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Users user = snapshot.getValue(Users.class);
                assert user != null;
                username.setText(user.getUsername());

                if(user.getImageUrl().equals("default"))
                {
                    userImage.setImageResource(R.mipmap.ic_launcher);
                }
                else
                {
                    Glide.with(MessageActivity.this).load(user.getImageUrl()).into(userImage);
                }

                readMessage(firebaseUser.getUid(), userId, user.getImageUrl());
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
            messageSeen(userId);
    }

    private void sendMessage(String sender, String receiver, String message)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> map = new HashMap<>();
        map.put("sender", sender);
        map.put("receiver", receiver);
        map.put("message", message);
        map.put("isseen", false);

        ref.child("Chats").push().setValue(map);

        //Dodavanje korisnika u Chat fragment: Poslednji chat sa  kontaktima

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userId);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(!snapshot.exists())
                {
                    chatRef.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    private void readMessage(String myId, String userId, String imageUrl)
    {
        chats = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                chats.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Chat chat = snapshot.getValue(Chat.class);

                    assert chat != null;
                    if(chat.getReceiver().equals(myId) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(myId))
                    {
                        chats.add(chat);
                        messageAdapter.notifyDataSetChanged();
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, chats, imageUrl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    //Metoda koja proverava da li je korisnik procitao poruku
    private void  messageSeen(final String userId)
    {
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        seenListener = reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Chat chat = snapshot.getValue(Chat.class);

                    assert chat != null;
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId))
                    {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("isseen", true);
                        snapshot.getRef().updateChildren(map);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error)
            {

            }
        });

    }

    //Provera statusa poruke
    private void statusCheck(String status)
    {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        statusCheck("Online");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        reference.removeEventListener(seenListener);
        statusCheck("Offline");
    }

}