package com.andjelkadzida.chatsome.model;

public class Chat
{
    private String sender;
    private String receiver;
    private String message;
    private String dateTimeSent;
    private String dateTimeSeen;
    private boolean statusSeen;

    public Chat(String sender, String receiver, String message, boolean statusSeen, String dateTimeSent, String dateTimeSeen)
    {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.statusSeen = statusSeen;
        this.dateTimeSent = dateTimeSent;
        this.dateTimeSeen = dateTimeSeen;
    }

    public Chat()
    {
    }

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

    public boolean isStatusSeen()
    {
        return statusSeen;
    }

    public void setStatusSeen(boolean statusSeen)
    {
        this.statusSeen = statusSeen;
    }

    public String getDateTimeSent()
    {
        return dateTimeSent;
    }

    public void setDateTimeSent(String dateTimeSent)
    {
        this.dateTimeSent = dateTimeSent;
    }

    public String getDateTimeSeen()
    {
        return dateTimeSeen;
    }

    public void setDateTimeSeen(String dateTimeSeen)
    {
        this.dateTimeSeen = dateTimeSeen;
    }
}
