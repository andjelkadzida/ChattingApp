package com.andjelkadzida.chatsome.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andjelkadzida.chatsome.MessageActivity;
import com.andjelkadzida.chatsome.R;
import com.andjelkadzida.chatsome.model.Users;
import com.bumptech.glide.Glide;


import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>
{
    private Context context;
    private List<Users> users;

    //Konstruktor


    public UserAdapter(Context context, List<Users> users)
    {
        this.context = context;
        this.users = users;
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
        Users user = users.get(position);
        holder.usersNameView.setText(user.getUsername());

        if (user.getImageUrl().equals("default"))
        {
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Glide.with(context)
                    .load(user.getImageUrl())
                    .into(holder.imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView usersNameView;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            usersNameView = itemView.findViewById(R.id.allUsersNameView);
            imageView = itemView.findViewById(R.id.userImageView);
        }
    }

}
