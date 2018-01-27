package rc.loveq.eye.ui.base;

import android.support.v7.widget.Toolbar;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import rc.loveq.eye.R;

/**
 * Author：Rc
 * 0n 2018/1/27 21:11
 */

public class BaseActivity extends RxAppCompatActivity {

    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            throw new NullPointerException();
        }
        setSupportActionBar(toolbar);
    }
}
