package rc.loveq.eye.ui.adapter;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import me.drakeet.multitype.ItemViewBinder;
import rc.loveq.eye.IntentManager;
import rc.loveq.eye.R;
import rc.loveq.eye.data.model.ItemList;
import rc.loveq.eye.utils.glide.GlideApp;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Author：Rc
 * 0n 2018/1/28 11:08
 */

public class VideoViewBinder extends ItemViewBinder<ItemList, VideoViewBinder.ViewHolder> {
    public Activity mActivity;
    private final ColorDrawable[] shotLoadingPlaceholders;

    public VideoViewBinder(Activity activity) {
        mActivity = activity;
        TypedArray array = mActivity.obtainStyledAttributes(R.styleable.MainVideo);
        int resourceId = array.getResourceId(R.styleable.MainVideo_shotLoadingPlaceholderColors, 0);
        if (resourceId != 0) {
            int[] placeholderColors = mActivity.getResources().getIntArray(resourceId);
            shotLoadingPlaceholders = new ColorDrawable[placeholderColors.length];
            for (int i = 0; i < placeholderColors.length; i++) {
                shotLoadingPlaceholders[i] = new ColorDrawable(placeholderColors[i]);
            }
        } else {
            shotLoadingPlaceholders = new ColorDrawable[]{new ColorDrawable(Color.DKGRAY)};
        }
        array.recycle();
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull ItemList item) {
        GlideApp.with(holder.itemView.getContext())
                .load(item.data.cover.detail)
                .placeholder(shotLoadingPlaceholders[getPosition(holder) % shotLoadingPlaceholders.length])
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCropAndRoundedCorners(16)
                .override(800, 600)
                .transition(withCrossFade())
                .into(holder.mIvVideoCover);

        GlideApp.with(holder.itemView.getContext())
                .load(item.data.author.icon)
                .placeholder(R.drawable.shape_avatar_placeholder)
                .circleCrop()
                .transition(withCrossFade())
                .into(holder.mIvAvatar);
        holder.mTvVideoTitle.setText(item.data.title);
        holder.mTvVideoDescription.setText(item.data.author.name + " / #" + item.data.category);

        holder.mIvVideoCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentManager.startVideoPlayerActivity(view, mActivity, item);
            }
        });
        holder.mIvVideoCover.setBackground(
                shotLoadingPlaceholders[getPosition(holder) % shotLoadingPlaceholders.length]);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIvVideoCover;
        public ImageView mIvAvatar;
        public TextView mTvVideoTitle;
        public TextView mTvVideoDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            mIvVideoCover = itemView.findViewById(R.id.iv_video_cover);
            mTvVideoTitle = itemView.findViewById(R.id.tv_video_title);
            mTvVideoDescription = itemView.findViewById(R.id.tv_video_description);
            mIvAvatar = itemView.findViewById(R.id.iv_avatar);
        }
    }
}
