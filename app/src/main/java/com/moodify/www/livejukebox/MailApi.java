package com.moodify.www.livejukebox;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MailApi {
    @FormUrlEncoded
    @POST("messages")
    Call<ResponseBody> sendAlert(
            @Field("from") String from,
            @Field("to") String to,
            @Field("subject") String subject,
            @Field("text") String text
    );
}
