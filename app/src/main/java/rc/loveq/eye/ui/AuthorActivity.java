package rc.loveq.eye.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import rc.loveq.eye.R;
import rc.loveq.eye.data.model.ItemList;
import rc.loveq.eye.ui.base.BaseActivity;
import rc.loveq.eye.utils.glide.GlideApp;
import rc.loveq.eye.widget.ElasticDragDismissFrameLayout;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class AuthorActivity extends BaseActivity {
    public static final String EXTRA_DATA = "EXTRA_DATA";
    @BindView(R.id.draggable_frame)
    ElasticDragDismissFrameLayout mDraggableFrame;
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.player_name)
    TextView mPlayerName;
    private ItemList itemData;
    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        ButterKnife.bind(this);
        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_DATA)) {
            itemData = intent.getParcelableExtra(EXTRA_DATA);
            initAvatar();
        } else {
            throw new IllegalArgumentException("you should pass ItemList");
        }

    }

    private void initAvatar() {
        GlideApp.with(this)
                .load(itemData.data.author.icon)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .priority(Priority.IMMEDIATE)
                .circleCrop()
                .override(400, 300)
                .transition(withCrossFade())
                .into(mAvatar);
        postponeEnterTransition();
        mAvatar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mAvatar.getViewTreeObserver().removeOnPreDrawListener(this);
                startPostponedEnterTransition();
                return true;
            }
        });
        mPlayerName.setText(itemData.data.author.name);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDraggableFrame.addListener(chromeFader);
    }

    @Override
    protected void onPause() {
        mDraggableFrame.removeListener(chromeFader);
        super.onPause();
    }
}
