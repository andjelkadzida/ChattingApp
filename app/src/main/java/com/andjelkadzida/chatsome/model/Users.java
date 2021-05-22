package com.andjelkadzida.chatsome.model;

public class Users
{
    private String id;
    private String username;
    private String imageUrl;
    private String status;

    //Konstruktori
    public Users(String id, String username, String imageUrl, String status)
    {
        this.id = id;
        this.username = username;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public Users()
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

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

}
