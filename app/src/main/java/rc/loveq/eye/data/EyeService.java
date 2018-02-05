package rc.loveq.eye.data;

import io.reactivex.Flowable;
import rc.loveq.eye.data.model.Daily;
import rc.loveq.eye.data.model.Replies;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Author：Rc
 * 0n 2018/1/27 21:34
 */

public interface EyeService {
    String EYE_END_POINT = " http://baobab.kaiyanapp.com/api/";

    //首页
    @GET("v2/feed?num=2")
    Flowable<Daily> getDaily(@Query("date") Long date);

    //such as http://baobab.kaiyanapp.com/api/v2/feed?num=2
    @GET("v2/feed?num=2")
    Flowable<Daily> getDaily();

    //评论 such as  http://baobab.kaiyanapp.com/api/v1/replies/video?id=19301
    @GET("v1/replies/video")
    Flowable<Replies> getReplies(@Query("id") int id);

    @GET("v1/replies/video?num=10")
    Flowable<Replies> getReplies(@Query("id") int id, @Query("lastId") int lastId);

}
