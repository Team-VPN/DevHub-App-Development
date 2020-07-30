package com.teamvpn.devhub.mfragment;

import com.teamvpn.devhub.Notifications.MyResponse;
import com.teamvpn.devhub.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService
{
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAANks7FIc:APA91bHY96c7cScxJTTitKk5GA_AdUmmeGRryekCwAjUq4JeHtmRhjMcsqLCl9NW6O3Sas0sT7yJB7ElrJBQwvFHHCjyEjRNEgSVh7ZOWQjqyb7qEJ3rpFI-eIetAxgfMtd4Ln0tC2Y0"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
