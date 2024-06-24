package com.example.alcoaware_00;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("send-sensor-data")
    Call<Void> sendSensorData(
            @Field("x") float x,
            @Field("y") float y,
            @Field("z") float z
    );

}
