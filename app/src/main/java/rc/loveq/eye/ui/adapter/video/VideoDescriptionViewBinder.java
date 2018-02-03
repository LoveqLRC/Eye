package rc.loveq.eye.ui.adapter.video;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.drakeet.multitype.ItemViewBinder;
import rc.loveq.eye.data.model.Data;

/**
 * Created by rc on 2018/2/1.
 * Description:
 */

public class VideoDescriptionViewBinder extends ItemViewBinder<Data, VideoDescriptionViewBinder.ViewHolder> {
    private View mDescription;

    public VideoDescriptionViewBinder(View description) {
        mDescription = description;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
//        View view = inflater.inflate(R.layout.layout_video_description, parent, false);
        return new ViewHolder(mDescription);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Data item) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
