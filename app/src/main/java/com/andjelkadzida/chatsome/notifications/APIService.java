package com.andjelkadzida.chatsome.notifications;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService
{
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AIzaSyAU2z9sgb6QiJE8iGSGLNWBdBlbWvFICno"
            }
    )
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
