package rc.loveq.eye.ui.adapter.main;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Collections;
import java.util.List;

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

public class VideoViewBinder extends ItemViewBinder<ItemList, VideoViewBinder.ViewHolder>
        implements ListPreloader.PreloadModelProvider<ItemList> {
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
        GlideApp.with(mActivity)
                .load(item.data.cover.detail)
//                .placeholder(shotLoadingPlaceholders[getPosition(holder) % shotLoadingPlaceholders.length])
                .placeholder(shotLoadingPlaceholders[0])
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop()
                .override(800, 600)
                .transition(withCrossFade())
                .into(holder.mIvVideoCover);

        String iconUrl;
        String description;

        if (item.data.author != null) {
            iconUrl = item.data.author.icon;
            description = item.data.author.name + " / #" + item.data.category;
        } else {
            iconUrl = item.data.provider.icon;
            description = item.data.provider.name + " / #" + item.data.provider.alias;
        }
        GlideApp.with(mActivity)
                .load(iconUrl)
                .placeholder(R.drawable.shape_avatar_placeholder)
                .error(R.mipmap.ic_launcher)
                .circleCrop()
                .transition(withCrossFade())
                .into(holder.mIvAvatar);
        holder.mTvVideoDescription.setText(description);

        holder.mTvVideoTitle.setText(item.data.title);

        holder.mIvVideoCover.setOnClickListener(view -> IntentManager.startVideoPlayerActivity(view, mActivity, item));
        holder.mIvVideoCover.setBackground(
                shotLoadingPlaceholders[getPosition(holder) % shotLoadingPlaceholders.length]);

    }

    @NonNull
    @Override
    public List<ItemList> getPreloadItems(int position) {
        List<?> items = getAdapter().getItems();
        Object o = items.get(position);
        if (o instanceof ItemList) {
            return Collections.singletonList((ItemList) o);
        }
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull ItemList item) {
        return GlideApp.with(mActivity).load(item.data.cover.detail)
                .placeholder(shotLoadingPlaceholders[0])
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop()
                .override(800, 600)
                .transition(withCrossFade());
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
