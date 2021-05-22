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
import com.andjelkadzida.chatsome.model.Chat;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>
{
    private Context context;
    private List<Chat> chats;
    private String imageUrl;

    //Firebase
    FirebaseUser firebaseUser;


    public static final int MESSAGE_TYPE_LEFT = 0;
    public static final int MESSAGE_TYPE_RIGHT = 1;



    //Konstruktor
    public MessageAdapter(Context context, List<Chat> chats, String imageUrl)
    {
        this.context = context;
        this.chats = chats;
        this.imageUrl = imageUrl;
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(viewType == MESSAGE_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position)
    {
        Chat chat = chats.get(position);

        holder.showMessage.setText(chat.getMessage());

        if(imageUrl.equals("default"))
        {
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }
        else
        {
            Glide.with(context).load(imageUrl).into(holder.imageView);
        }

    }

    @Override
    public int getItemCount()
    {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView showMessage;
        public ImageView imageView;

        public ViewHolder(View itemView)
        {
            super(itemView);

            showMessage = itemView.findViewById(R.id.showMessage);
            imageView = itemView.findViewById(R.id.profilePicture);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chats.get(position).getSender().equals(firebaseUser.getUid()))
        {
            return MESSAGE_TYPE_RIGHT;
        }
        else
        {
            return MESSAGE_TYPE_LEFT;
        }
    }
}
