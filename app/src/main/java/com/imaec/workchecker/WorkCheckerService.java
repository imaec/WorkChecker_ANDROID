package com.imaec.workchecker;

import com.imaec.workchecker.model.JoinResult;
import com.imaec.workchecker.model.LoginResult;
import com.imaec.workchecker.model.UserResult;
import com.imaec.workchecker.model.Work;
import com.imaec.workchecker.model.WorkResult;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by imaec on 2018-06-23.
 */

public interface WorkCheckerService {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://52.79.144.254:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET("user")
    Call<UserResult> callUserList();

    @GET("user/login")
    Call<LoginResult> callUser(@Query("email") String email,
                               @Query("password") String password);

    @Multipart
    @POST("user")
    Call<JoinResult> callAddUser(@Part("name") RequestBody name,
                                 @Part("rank") RequestBody rank,
                                 @Part("department") RequestBody department,
                                 @Part("email") RequestBody email,
                                 @Part("password") RequestBody password,
                                 @Part("reg_date") RequestBody reg_date,
                                 @Part("tel") RequestBody tel,
                                 @Part MultipartBody.Part image);

    @GET("work")
    Call<WorkResult> callWorkList(@Query("user_id") String user_id);

    @GET("work")
    Call<WorkResult> callWorkToday(@Query("user_id") String user_id,
                                   @Query("date") String date);

    @FormUrlEncoded
    @POST("work")
    Call<WorkResult> callAddWork(@Field("user_id") String user_id,
                                 @Field("status") String status,
                                 @Field("date") String date,
                                 @Field("time_a") String time_a,
                                 @Field("time_b") String time_b);

    @FormUrlEncoded
    @PUT("work/{id}")
    Call<WorkResult> callEditWork(@Path("id") String _id,
                                  @Field("time_b") String time_b);
}
