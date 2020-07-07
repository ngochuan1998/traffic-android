package ngochuan.damh.thongtingiaothong.Retrofit;

import io.reactivex.Observable;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface IMyService {
    @POST("signup/CTV")
    @FormUrlEncoded
    Observable<String> registerUser(@Field("id") String id,
                                    @Field("password") String password,
                                    @Field("name") String name);
    @POST("login")
    @FormUrlEncoded
    Observable<String> loginUser(@Field("id") String id,
                                 @Field("password") String password);
    @PUT("userGPS")
    @FormUrlEncoded
    Observable<String> updateGPS(@Field("id") String id,
                                 @Field("latitude") double latitude,
                                 @Field("longitude") double longitude);

//    @PUT("userGPS/{id}")
//    @FormUrlEncoded
//    Observable<String> updateGPS(@Path("id") String id,
//                                 @Field("latitude") double latitude,
//                                 @Field("longitude") double longitude);

}



