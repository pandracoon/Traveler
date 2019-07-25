package com.example.tripscheduler.Server;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface IAppService {
    @POST("json_test")
    @FormUrlEncoded
    Observable<String> json_test(@Field("test") String email);

    @POST("UserInfo/insert_one")
    @FormUrlEncoded
    Observable<String> user_insert_one(@Field("email") String email,
                                       @Field("password") String password,
                                       @Field("name") String name);

    @POST("UserInfo/get_one")
    @FormUrlEncoded
    Observable<String> user_get_one(@Field("email") String email,
                                    @Field("password") String password);

    @POST("UserInfo/get_recent_one")
    @FormUrlEncoded
    Observable<String> user_get_recent_one(@Field("email") String email);

    @POST("UserInfo/set_recent_one")
    @FormUrlEncoded
    Observable<String> user_set_recent_one(@Field("email") String email,
                                           @Field("title") String title);

    @POST("Travel/insert_one")
    @FormUrlEncoded
    Observable<String> travel_insert_one(@Field("email") String email,
                                         @Field("title") String title,
                                         @Field("area") String area,
                                         @Field("start_date") String start_date,
                                         @Field("end_date") String end_date);

    @POST("Travel/delete_one")
    @FormUrlEncoded
    Observable<String> travel_delete_one(@Field("email") String email,
                                         @Field("title") String title);

    @POST("Travel/get_one")
    @FormUrlEncoded
    Observable<String> travel_get_one(@Field("email") String email,
                                      @Field("title") String title);

    @POST("Travel/get_all")
    @FormUrlEncoded
    Observable<String> travel_get_all(@Field("email") String email);

//    @POST("Places/insert_one")
//    @FormUrlEncoded
//    Observable<String> place_insert_one(@Field("email") String email,
//                                        @Field("title") String title,
//                                        @Field("name") String name,
//                                        @Field("location") String location,
//                                        @Field("label") String label);

    @Multipart
    @POST("Places/insert_one")
    Call<ResponseBody> place_insert_one(@Part MultipartBody.Part image,
                                        @Part("upload") RequestBody imageName,
                                        @Part("email") RequestBody email,
                                        @Part("title") RequestBody title,
                                        @Part("name") RequestBody name,
                                        @Part("location") RequestBody location,
                                        @Part("label") RequestBody label);

    @POST("Places/delete_one")
    @FormUrlEncoded
    Observable<String> place_delete_one(@Field("email") String email,
                                        @Field("title") String title,
                                        @Field("name") String name,
                                        @Field("location") String location,
                                        @Field("label") String label);

    @POST("Places/update_one")
    @FormUrlEncoded
    Observable<String> place_update_one(@Field("email") String email,
                                        @Field("title") String title,
                                        @Field("src_name") String src_name,
                                        @Field("src_location") String src_location,
                                        @Field("src_label") String src_label,
                                        @Field("dst_name") String dst_name,
                                        @Field("dst_location") String dst_location,
                                        @Field("dst_label") String dst_label);

    @POST("Places/get_one")
    @FormUrlEncoded
    Observable<String> places_get_one(@Field("email") String email,
                                      @Field("title") String title);

    @POST("Schedule/insert_one")
    @FormUrlEncoded
    Observable<String> schedule_insert_one(@Field("email") String email,
                                           @Field("title") String title,
                                           @Field("day") String day,
                                           @Field("name") String name,
                                           @Field("location") String location,
                                           @Field("label") String label,
                                           @Field("memo") String memo,
                                           @Field("start") String start,
                                           @Field("duration") String duration);

    @POST("Schedule/delete_one")
    @FormUrlEncoded
    Observable<String> schedule_delete_one(@Field("email") String email,
                                           @Field("title") String title,
                                           @Field("day") String day,
                                           @Field("name") String name,
                                           @Field("location") String location,
                                           @Field("label") String label,
                                           @Field("memo") String memo,
                                           @Field("start") String start,
                                           @Field("duration") String duration);

    @POST("Schedule/update_one")
    @FormUrlEncoded
    Observable<String> schedule_update_one(@Field("email") String email,
                                           @Field("title") String title,
                                           @Field("day") String day,
                                           @Field("src_name") String src_name,
                                           @Field("src_location") String src_location,
                                           @Field("src_label") String src_label,
                                           @Field("src_memo") String src_memo,
                                           @Field("src_start") String src_start,
                                           @Field("src_duration") String src_duration,
                                           @Field("dst_name") String dst_name,
                                           @Field("dst_location") String dst_location,
                                           @Field("dst_label") String dst_label,
                                           @Field("dst_memo") String dst_memo,
                                           @Field("dst_start") String dst_start,
                                           @Field("dst_duration") String dst_duration);

    @POST("Schedule/get_one")
    @FormUrlEncoded
    Observable<String> schedule_get_one(@Field("email") String email,
                                        @Field("title") String title);

    @POST("Schedule/optimize")
    @FormUrlEncoded
    Observable<String> schedule_optimize(@Field("size") String size,
                                         @Field("graph") String graph,
                                         @Field("limits") String limits,
                                         @Field("keyPoints") String keyPoints);

    @Multipart
    @POST("upload_image_request")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image,
                                 @Part("upload") RequestBody name,
                                 @Part("owner_email") RequestBody owner_email);

    @GET("maps/api/directions/json")
    Call<Model.DirectionResults> getJson(@Query("origin") String origin,
                                         @Query("destination") String destination,
                                         @Query("key") String key,
                                         @Query("mode") String mode);
}
