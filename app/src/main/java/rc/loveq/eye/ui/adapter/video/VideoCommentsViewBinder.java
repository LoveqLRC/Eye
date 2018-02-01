package rc.loveq.eye.ui.adapter.video;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.drakeet.multitype.ItemViewBinder;
import rc.loveq.eye.R;
import rc.loveq.eye.data.model.ReplyList;
import rc.loveq.eye.utils.glide.GlideApp;
import rc.loveq.eye.utils.recyclerview.CommentAnimator;
import rc.loveq.eye.widget.CheckableImageButton;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by rc on 2018/1/31.
 * Description:
 */

public class VideoCommentsViewBinder extends ItemViewBinder<ReplyList, VideoCommentsViewBinder.ViewHolder> {
    private Transition expandCollapse;
    private RecyclerView mRecyclerView;
    private CommentAnimator mAnimator;
    private int expandedCommentPosition = RecyclerView.NO_POSITION;
    private static final int EXPAND = 0x1;
    private static final int COLLAPSE = 0x2;
    private static final int COMMENT_LIKE = 0x3;
    private static final int REPLY = 0x4;

    public VideoCommentsViewBinder(AutoTransition collapse, RecyclerView recyclerView, CommentAnimator animator) {
        expandCollapse = collapse;
        mRecyclerView = recyclerView;
        mAnimator = animator;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_video_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull ReplyList item) {
        boolean isExpanded = getPosition(holder) == expandedCommentPosition;
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


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getPosition(holder);
                if (position == RecyclerView.NO_POSITION) return;
                TransitionManager.beginDelayedTransition(mRecyclerView, expandCollapse);
                mAnimator.setAnimateMoves(false);

                if (RecyclerView.NO_POSITION != expandedCommentPosition) {
                    getAdapter().notifyItemChanged(expandedCommentPosition, COLLAPSE);
                }

                if (expandedCommentPosition != position) {
                    expandedCommentPosition = position;
                    getAdapter().notifyItemChanged(position, EXPAND);
                    holder.itemView.requestFocus();
                } else {
                    expandedCommentPosition = RecyclerView.NO_POSITION;
                }

            }
        });
        setExpanded(holder, isExpanded);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull ReplyList item, @NonNull List<Object> partialChangePayloads) {
        if ((partialChangePayloads.contains(EXPAND)
                || partialChangePayloads.contains(COLLAPSE))
                || partialChangePayloads.contains(REPLY)) {
            setExpanded(holder, getPosition(holder) == expandedCommentPosition);
        } else if (partialChangePayloads.contains(COMMENT_LIKE)) {
            return; // nothing to do
        } else {
            onBindViewHolder(holder, item);
        }

    }

    private void setExpanded(ViewHolder holder, boolean isExpanded) {
        holder.itemView.setActivated(isExpanded);
        holder.mIbReply.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.mCibLikeHeart.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.mTvCommentLikeCount.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mIvAvatar;
        private final TextView mTvTimeAgo;
        private final TextView mTvAuthor;
        private final TextView mTvCommentContent;
        private final TextView mTvCommentLikeCount;
        private final CheckableImageButton mCibLikeHeart;
        private final ImageButton mIbReply;

        public ViewHolder(View itemView) {
            super(itemView);
            mIvAvatar = itemView.findViewById(R.id.iv_comment_avatar);
            mTvTimeAgo = itemView.findViewById(R.id.tv_comment_time_ago);
            mTvAuthor = itemView.findViewById(R.id.tv_comment_author);
            mTvCommentContent = itemView.findViewById(R.id.tv_comment_text);
            mTvCommentLikeCount = itemView.findViewById(R.id.tv_comment_likes_count);
            mCibLikeHeart = itemView.findViewById(R.id.cib_comment_like);
            mIbReply = itemView.findViewById(R.id.ib_comment_reply);

        }
    }
}
