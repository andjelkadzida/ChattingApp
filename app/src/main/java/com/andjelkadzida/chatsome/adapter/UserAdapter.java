package com.andjelkadzida.chatsome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andjelkadzida.chatsome.R;
import com.andjelkadzida.chatsome.model.User;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>
{
    private Context context;
    private List<User> users;

    //Konstruktor


    public UserAdapter(Context context, List<User> users)
    {
        this.context = context;
        this.users = users;
    }


    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserAdapter.ViewHolder holder, int position)
    {
        User user = users.get(position);
        holder.username.setText(user.getUsername());

        if (user.getImageUrl().equals("default"))
        {
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Glide.with(context)
                    .load(user.getImageUrl())
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount()
    {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView username;
        public ImageView imageView;

        public ViewHolder(View itemView)
        {
            super(itemView);

            username = itemView.findViewById(R.id.usernameView);
            imageView = itemView.findViewById(R.id.userImageView);
        }
    }

}
