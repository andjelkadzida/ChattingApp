package com.andjelkadzida.chatsome.model;

public class ChatList
{
    private String id;

    //Konstruktori
    public ChatList(String id)
    {
        this.id = id;
    }

    public ChatList()
    {

    }

    //Getteri i setteri
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}
