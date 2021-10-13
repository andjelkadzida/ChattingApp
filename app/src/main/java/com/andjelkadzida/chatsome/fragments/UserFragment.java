package com.andjelkadzida.chatsome.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andjelkadzida.chatsome.R;
import com.andjelkadzida.chatsome.adapter.UserAdapter;
import com.andjelkadzida.chatsome.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class UserFragment extends Fragment
{
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<Users> users;
    private EditText searchUser;

    public UserFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        recyclerView = view.findViewById(R.id.userRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        users = new ArrayList<>();
        readUsers();

        searchUser = view.findViewById(R.id.searchUser);
        searchUser.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
        return view;
    }

    private void searchUsers(String s)
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("search").startAt(s).endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                users.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Users user = dataSnapshot.getValue(Users.class);
                    assert user!= null;
                    assert firebaseUser != null;

                    if(!user.getId().equals(firebaseUser.getUid()))
                    {
                        users.add(user);
                    }
                }

                userAdapter = new UserAdapter(getContext(), users, false);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error)
            {

            }
        });
    }

    private void readUsers()
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(searchUser.getText().toString().equals(""))
                {
                    users.clear();

                    for(DataSnapshot dataSnapshot: snapshot.getChildren())
                    {
                        Users user = dataSnapshot.getValue(Users.class);

                        if(user!=null && user.getId() != null && !user.getId().equals(firebaseUser.getUid()))
                        {
                            users.add(user);
                        }

                        userAdapter = new UserAdapter(getContext(), users, false);
                        recyclerView.setAdapter(userAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                System.out.println("The read failed: " + error);
            }
        });
    }
}