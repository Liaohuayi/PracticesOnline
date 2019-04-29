package net.lzzy.practicesonline.activities.activity;

import android.os.Bundle;
import android.view.Window;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import net.lzzy.practicesonline.activities.utils.AppUtils;

/**
 *
 * @author lzzy_gxy
 * @date 2019/4/11
 * Description:
 */
public abstract class BaseActivity extends AppCompatActivity {

    private Fragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutRes());
        AppUtils.addAvtivity(this);
        FragmentManager manager=getSupportFragmentManager();
        fragment = manager.findFragmentById(getContainerId());
        if (fragment ==null){
            fragment =createFragment();
            manager.beginTransaction().add(getContainerId(), fragment).commit();
        }
    }

    protected Fragment getFragment(){
        return fragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.removeActivity(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        AppUtils.setRunning(getLocalClassName());
    }

    @Override
    protected void onStop(){
        super.onStop();
        AppUtils.setStopped(getLocalClassName());
    }

/**
 * Activity 的资源布局文件id
 * @return 布局资源id
 */
    protected abstract int getLayoutRes();

    protected abstract int getContainerId();

    /**
     * 生产托管的Fragment对象
     * @return fragment
     */
    protected abstract Fragment createFragment();
}
