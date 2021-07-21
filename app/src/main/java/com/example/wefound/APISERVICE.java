package com.example.wefound;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APISERVICE {


    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA0UWcMiY:APA91bHwoxRpz1B9gua8aX6CkNg42z6IeC5fQeBAhlHYZhPfpfvoV7uo_fU0oqDtk0qnPhnAS_m6iA6s4WoiR4mjiGTdYOxGkKNXtdFl6BznI2sWPrlOADt7mSR_f2VqG0_EG0rhqRAd"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);


}