package com.andjelkadzida.chatsome.notifications;

public class Token
{
    String token;

    //Konstruktori
    public Token(String token)
    {
        this.token = token;
    }

    public Token()
    {

    }

    //Getteri i setteri
    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    @Override
    public String toString()
    {
        return "Token{" +
                "token='" + token + '\'' +
                '}';
    }
}
