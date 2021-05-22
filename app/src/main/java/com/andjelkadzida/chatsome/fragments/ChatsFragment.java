package com.andjelkadzida.chatsome.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andjelkadzida.chatsome.R;
import com.andjelkadzida.chatsome.adapter.UserAdapter;
import com.andjelkadzida.chatsome.model.ChatList;
import com.andjelkadzida.chatsome.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment
{

    private UserAdapter userAdapter;
    private List<Users> users;

    //Firebase
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    private List<ChatList> usersList;

    RecyclerView chatRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        chatRecycler = view.findViewById(R.id.chatRecycler);
        chatRecycler.setHasFixedSize(true);
        chatRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                usersList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    ChatList chatList = snapshot.getValue(ChatList.class);
                    usersList.add(chatList);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
        return view;
    }

    private void chatList()
    {
        //Getting all recent chats
        users = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                users.clear();

                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Users user = snapshot.getValue(Users.class);

                    for(ChatList chatList:usersList)
                    {
                        if(user.getId().equals(chatList.getId()))
                        {
                            users.add(user);
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(), users);
                chatRecycler.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });



    }

}