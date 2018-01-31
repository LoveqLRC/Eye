package rc.loveq.eye.ui.adapter.video;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.transition.AutoTransition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import me.drakeet.multitype.ItemViewBinder;
import rc.loveq.eye.R;
import rc.loveq.eye.data.model.ReplyList;
import rc.loveq.eye.utils.AnimUtils;
import rc.loveq.eye.utils.glide.GlideApp;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by rc on 2018/1/31.
 * Description:
 */

public class VideoCommentsViewBinder extends ItemViewBinder<ReplyList, VideoCommentsViewBinder.ViewHolder> {

    public VideoCommentsViewBinder(long expandDuration, Activity activity) {
        AutoTransition expandCollapse = new AutoTransition();
        expandCollapse.setDuration(expandDuration);
        expandCollapse.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(activity));

    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_video_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull ReplyList item) {
        GlideApp.with(holder.itemView.getContext())
                .load(item.user.avatar)
                .circleCrop()
                .placeholder(R.drawable.shape_avatar_placeholder)
                .override(800, 600)
                .transition(withCrossFade())
                .into(holder.mIvAvatar);
        holder.mTvTimeAgo.setText(DateUtils.getRelativeTimeSpanString(item.createTime,
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS).toString().toLowerCase());
        holder.mTvAuthor.setText(item.user.nickname);

        holder.mTvCommentContent.setText(item.message);

        holder.mTvCommentLikeCount.setText(String.valueOf(item.likeCount));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mIvAvatar;
        private final TextView mTvTimeAgo;
        private final TextView mTvAuthor;
        private final TextView mTvCommentContent;
        private final TextView mTvCommentLikeCount;

        public ViewHolder(View itemView) {
            super(itemView);
            mIvAvatar = itemView.findViewById(R.id.iv_comment_avatar);
            mTvTimeAgo = itemView.findViewById(R.id.tv_comment_time_ago);
            mTvAuthor = itemView.findViewById(R.id.tv_comment_author);
            mTvCommentContent = itemView.findViewById(R.id.tv_comment_text);
            mTvCommentLikeCount = itemView.findViewById(R.id.tv_comment_likes_count);
        }
    }
}
