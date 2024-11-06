package do_an.tkll.an_iot_app;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface AdafruitApiService {
    @GET(secretKey.APIcambien1)
    Call<List<FeedData>> getTempData(
            @Path(secretKey.username) String username,
            @Path(secretKey.APIcambien1) String temp,
            @Header(secretKey.active_key) String apiKey
    );
    @GET(secretKey.APIcambien2)
    Call<List<FeedData>> getLightData(
            @Path(secretKey.username) String username,
            @Path(secretKey.APIcambien2) String light,
            @Header(secretKey.active_key) String apiKey
    );
    @GET(secretKey.APIcambien3)
    Call<List<FeedData>> getHumiData(
            @Path(secretKey.username) String username,
            @Path(secretKey.APIcambien3) String temp,
            @Header(secretKey.active_key) String apiKey
    );
}
