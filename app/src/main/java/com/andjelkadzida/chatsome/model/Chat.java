package com.andjelkadzida.chatsome.model;

public class Chat
{
    private String sender;
    private String receiver;
    private String message;

    //Konstruktori

    public Chat(String sender, String receiver, String message)
    {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public Chat()
    {
    }

    //Getteri i setteri
    public String getSender()
    {
        return sender;
    }

    public void setSender(String sender)
    {
        this.sender = sender;
    }

    public String getReceiver()
    {
        return receiver;
    }

    public void setReceiver(String receiver)
    {
        this.receiver = receiver;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
