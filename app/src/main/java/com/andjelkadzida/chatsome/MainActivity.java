package com.andjelkadzida.chatsome;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.andjelkadzida.chatsome.fragments.ChatsFragment;
import com.andjelkadzida.chatsome.fragments.ProfileFragment;
import com.andjelkadzida.chatsome.fragments.UserFragment;
import com.andjelkadzida.chatsome.model.Chat;
import com.andjelkadzida.chatsome.model.Users;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
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

import de.hdodenhof.circleimageview.CircleImageView;

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

       CircleImageView profileImage = findViewById(R.id.profileImage);
       TextView username = findViewById(R.id.username);

        //Iz firebase baze podataka uzimam trenutno ulogovanog korisnika i smestam ga u currentUser
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                snapshot.notify();
                Users user = snapshot.getValue(Users.class);
                username.setText(user.getUsername());
                if (user!=null && user.getImageUrl() != null && user.getImageUrl().equals("default"))
                {
                    profileImage.setImageResource(R.drawable.user_ico);
                }
                else
                {
                    Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profileImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

        //TabLayout i ViewPager
       final TabLayout tabLayout = findViewById(R.id.tabLayout);
       final ViewPager viewPager = findViewById(R.id.viewPager);

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
            {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                int unread = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(currentUser.getUid()) && !chat.isStatusSeen())
                    {
                        unread++;
                    }
                }

                if(unread == 0)
                {
                    viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
                }
                else
                {
                    viewPagerAdapter.addFragment(new ChatsFragment(), "("+unread+") Chats");
                }

                //ViewPagerAdapter
                viewPagerAdapter.addFragment(new UserFragment(), "Users");
                viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");
                viewPager.setAdapter(viewPagerAdapter);

                tabLayout.setupWithViewPager(viewPager);
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
                startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
        }
        return false;
    }

    //Klasa ViewPagerAdapter
    class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private final ArrayList<Fragment> fragments;
        private final ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position)
        {
            return fragments.get(position);
        }

        @Override
        public int getCount()
        {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title)
        {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position)
        {
            return titles.get(position);
        }
    }

    //Provera da li je korisnik online
    //Pre nego sto proverim da li je korisnik online i updateujem mu status, proveravam da li korisnik postoji u bazi
    private void checkOnlineStatus(String status)
    {
            reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

            reference.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                    {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("status", status);

                        reference.updateChildren(hashMap);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error)
                {

                }
            });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        checkOnlineStatus("Online");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        checkOnlineStatus("Offline");
    }
}