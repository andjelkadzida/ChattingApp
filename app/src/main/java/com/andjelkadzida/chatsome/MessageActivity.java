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
import com.andjelkadzida.chatsome.notifications.APIService;
import com.andjelkadzida.chatsome.model.Chat;
import com.andjelkadzida.chatsome.model.Users;
import com.andjelkadzida.chatsome.notifications.Client;
import com.andjelkadzida.chatsome.notifications.Data;
import com.andjelkadzida.chatsome.notifications.Response;
import com.andjelkadzida.chatsome.notifications.Sender;
import com.andjelkadzida.chatsome.notifications.Token;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import retrofit2.Call;
import retrofit2.Callback;
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

    String userid;

    ValueEventListener seenListener;

    APIService apiService;

    boolean notify = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

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
        userid = intent.getStringExtra("userid");

        //Uzimanje treuntog korisnika
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Users user = snapshot.getValue(Users.class);
                username.setText(user.getUsername());

                if(user.getImageUrl().equals("default"))
                {
                    userImage.setImageResource(R.mipmap.user_ico);
                }
                else
                {
                    Glide.with(MessageActivity.this).load(user.getImageUrl()).into(userImage);
                }

                readMessage(firebaseUser.getUid(), userid, user.getImageUrl());
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
                    sendMessage(firebaseUser.getUid(), userid, message);
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
            messageSeen(userid);
    }

    //Metoda koja proverava da li je korisnik procitao poruku
    private void  messageSeen(final String userid)
    {
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        seenListener = reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid))
                    {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("statusSeen", true);
                        dataSnapshot.getRef().updateChildren(map);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error)
            {

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
        map.put("statusSeen", false);

        ref.child("Chats").push().setValue(map);

        //Dodavanje korisnika u Chat fragment: Poslednji chat sa  kontaktima

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(!snapshot.exists())
                {
                    chatRef.child("id").setValue(userid);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    private void readMessage(final String myId, final String userId, String imageUrl)
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
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if(chat.getReceiver().equals(myId) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(myId))
                    {
                        chats.add(chat);
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


    private void sendNotification(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Token token = snapshot.getValue(Token.class);

                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username+": "+message, "New Message", userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>()
                            {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response)
                                {
                                    if (response.code() == 200){
                                        if (response.body().success != 1)
                                        {
                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t)
                                {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}