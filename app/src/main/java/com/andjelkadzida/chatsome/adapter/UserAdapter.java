package com.andjelkadzida.chatsome.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.andjelkadzida.chatsome.MessageActivity;
import com.andjelkadzida.chatsome.R;
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
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>
{
    private final Context context;
    private final List<Users> users;
    private final boolean isChat;

    String latestMsg;

    //Konstruktor
    public UserAdapter(Context context, List<Users> users, boolean isChat)
    {
        this.context = context;
        this.users = users;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        final Users user = users.get(position);
        holder.usersNameView.setText(user.getUsername());

        if (user.getImageUrl().equals("default"))
        {
            holder.ProfileimageView.setImageResource(R.drawable.user_ico);
        }
        else
        {
            Glide.with(context)
                    .load(user.getImageUrl())
                    .into(holder.ProfileimageView);
        }

        if(isChat)
        {
            lastMessage(user.getId(), holder.lastMessage);
        }
        else
        {
            holder.lastMessage.setVisibility(View.GONE);
        }

        //Check status
        if(isChat)
        {
            if(user.getStatus().equals("Online"))
            {
                holder.onlineView.setVisibility(View.VISIBLE);
                holder.offlineView.setVisibility(View.GONE);
            }
            else
            {
                holder.onlineView.setVisibility(View.GONE);
                holder.offlineView.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            holder.onlineView.setVisibility(View.GONE);
            holder.offlineView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v ->
        {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("userid", user.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount()
    {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView usersNameView, lastMessage;
        public CircleImageView ProfileimageView, onlineView, offlineView;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            usersNameView = itemView.findViewById(R.id.allUsersNameView);
            ProfileimageView = itemView.findViewById(R.id.profileImg);
            onlineView = itemView.findViewById(R.id.onlineView);
            offlineView = itemView.findViewById(R.id.offlineView);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }
    private void lastMessage(final String userid, final TextView lastMsg)
    {
        latestMsg = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
            {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if(firebaseUser != null && chat != null)
                    {
                        if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid()))
                        {
                            latestMsg = chat.getMessage();
                        }
                    }
                }
                switch (latestMsg)
                {
                    case "default":
                        lastMsg.setText("No message");
                        break;
                    default:
                        lastMsg.setText(latestMsg);
                        break;
                }
                latestMsg = "default";
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error)
            {

            }
        });
    }
}