package rc.loveq.eye.data;

import io.reactivex.Flowable;
import rc.loveq.eye.data.model.Daily;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Author：Rc
 * 0n 2018/1/27 21:34
 */

public interface EyeService {
    String EYE_END_POINT = " http://baobab.kaiyanapp.com/api/";

    //main
    @GET("v2/feed?num=2")
    Flowable<Daily> getDaily(@Query("date") Long date);

    @GET("v2/feed?num=2")
    Flowable<Daily> getDaily();


}
