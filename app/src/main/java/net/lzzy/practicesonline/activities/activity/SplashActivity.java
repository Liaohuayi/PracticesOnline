package net.lzzy.practicesonline.activities.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.constants.ApiConstants;
import net.lzzy.practicesonline.activities.fragments.FragmentSplash;
import net.lzzy.practicesonline.activities.utils.AbstractStaticHandler;
import net.lzzy.practicesonline.activities.utils.AppUtils;
import net.lzzy.practicesonline.activities.utils.ViewUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Administrator
 */
public  class SplashActivity extends BaseActivity implements FragmentSplash.OnSplashFinishedListener {
    public static final int WHAT_COUNTING = 0;
    public static final int WHAT_EXCEPTION = 1;
    public static final int WHAT_COUNT_DONE = 2;
    public static final int WHAT_SERVER_OFF = 3;
    private boolean isServerOn=true;
    int seconds=10;
    private SplashHandler handler=new SplashHandler(this);
    private TextView tvCount;

    private static class SplashHandler extends AbstractStaticHandler<SplashActivity> {

        public SplashHandler(SplashActivity context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, SplashActivity activity) {
            switch (msg.what) {
                case WHAT_COUNTING:
                    String display = msg.obj.toString()+ "秒";
                    activity.tvCount.setText(display);
                    break;
                case WHAT_COUNT_DONE:
                    if (activity.isServerOn) {
                        activity.gotoMain();
                    }
                    break;
                case WHAT_EXCEPTION:
                    new AlertDialog.Builder(activity)
                            .setMessage(msg.obj.toString())
                            .setPositiveButton("继续", (dialog, which) -> activity.gotoMain())
                            .setNegativeButton("退出", (dialog, which) -> AppUtils.exit())
                            .show();
                    break;
                case WHAT_SERVER_OFF:
                    //region 处理消息
                    Activity context = AppUtils.getRunningActivity();
                    new AlertDialog.Builder(Objects.requireNonNull(context))
                            .setMessage("服务器没有响应，是否继续？\n" + msg.obj)
                            .setPositiveButton("确定", (dialog, which) -> {
                                if (context instanceof SplashActivity) {
                                    ((SplashActivity) context).gotoMain();
                                }
                            })
                            .setNegativeButton("退出", (dialog, which) -> AppUtils.exit())
                            .setNeutralButton("设置", (dialog, which) -> ViewUtils.gotoSetting(context))
                            .show();
                default:
                    break;
            }
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!AppUtils.isNetworkAvailable()) {
            new AlertDialog.Builder(this)
                    .setMessage("网络不可用，是否继续")
                    .setPositiveButton("退出", (dialog, which) -> AppUtils.exit())
                    .setPositiveButton("确定", (dialog, which) -> gotoMain()).show();
        }else {
                ThreadPoolExecutor executor=AppUtils.getExecutor();
                executor.execute(this::countDown);
            executor.execute(this::detectServerStantus);
        }
        tvCount=findViewById(R.id.activity_splash_tv_count_down);
    }

    private void countDown() {
        while (seconds >= 0) {
            Message message = handler.obtainMessage(WHAT_COUNTING);
            message.obj = seconds;
            handler.sendMessage(message);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                handler.sendMessage(handler.obtainMessage(WHAT_EXCEPTION, e.getMessage()));
            }
            seconds--;
        }
        handler.sendEmptyMessage(WHAT_COUNT_DONE);
    }

    private void detectServerStantus(){
        try {
            AppUtils.tryConnectServer(ApiConstants.URL_API);
        }catch (IOException e){
            isServerOn=false;
            handler.sendMessage(handler.obtainMessage(WHAT_SERVER_OFF,e.getMessage()));
        }
    }

    public void gotoMain(){
        startActivity(new Intent(this,PracticesActivity.class));
        finish();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_splash;
    }

    @Override
    protected int getContainerId() {
        return R.id.fragment_splash_container;
    }

    @Override
    protected Fragment createFragment() {
        return new FragmentSplash();
    }

    @Override
    public void cancelCount() {
       seconds=0;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        new AlertDialog.Builder(this)
                .setMessage("要退出吗?")
                .setPositiveButton("确定",(dialog,which) ->AppUtils.exit())
                .show();
    }



}
