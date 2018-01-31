package rc.loveq.eye.ui.adapter.main;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.drakeet.multitype.ItemViewBinder;
import rc.loveq.eye.R;
import rc.loveq.eye.data.model.TextHeader;

/**
 * Author：Rc
 * 0n 2018/1/28 10:57
 */

public class TextHeaderViewBinder extends ItemViewBinder<TextHeader, TextHeaderViewBinder.ViewHolder> {
    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_text_header, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull TextHeader item) {
        holder.mTextHeader.setText(item.textHeader);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextHeader;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextHeader = itemView.findViewById(R.id.tv_text_header);
        }
    }
}
