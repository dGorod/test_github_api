package example.com.testgithub.api;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Dmitriy Gorodnytskiy on 28-Oct-15.
 */
public interface ApiInterface {
    @GET("/users")
    void getUsers(@Query("since") int offset,
                  @Query("per_page") int limit,
                  Callback<List<UserModel>> callback);
}
