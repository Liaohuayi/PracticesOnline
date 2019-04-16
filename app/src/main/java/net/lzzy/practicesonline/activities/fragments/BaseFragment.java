package net.lzzy.practicesonline.activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * Created by lzzy_gxy on 2019/3/27.
 * Description:
 */
public abstract class BaseFragment extends Fragment {
    public BaseFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutRes(),container,false);
    }
    @Override
    public void onViewCreated(@Nullable View view,@Nullable Bundle savedInstanceState){
        populate();
    }

    /**
     * 执行onCreateView中初始化视图组件、填充数据的任务
     */


    protected abstract void populate();

    /**
     * Fragment的布局文件id
     * @retutn 布局资源id
     */
    public abstract int getLayoutRes();
    <T extends View> T find(@IdRes int id){
        return  getView().findViewById(id);
    }

    public abstract void search(String kw);
}
