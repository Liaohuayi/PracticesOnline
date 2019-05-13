package net.lzzy.practicesonline.activities.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.activities.fragments.QuestionFragment;
import net.lzzy.practicesonline.activities.models.Question;
import net.lzzy.practicesonline.activities.models.QuestionFactory;
import net.lzzy.practicesonline.activities.models.UserCookies;
import net.lzzy.practicesonline.activities.models.view.PracticeResult;
import net.lzzy.practicesonline.activities.models.view.QuestionResult;
import net.lzzy.practicesonline.activities.network.PracticeService;
import net.lzzy.practicesonline.activities.utils.AbstractStaticHandler;
import net.lzzy.practicesonline.activities.utils.AppUtils;
import net.lzzy.practicesonline.activities.utils.ViewUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class QuestionActivity extends AppCompatActivity {
    public static final int WHAT=0;
    public static final int WHAT1=1;
    public static final String EXTRA_PRACTICE_ID="extraPracticeId";
    public static final int WHAT2=2;
    public static final String EXTRA_RESULT = "extraResult";
    public static final int REQUEST_CODE_RESULT = 0;
    private String practiceId;
    private  int apiId;
    private List<Question> questions;
    private TextView tvView;
    private TextView tvCommit;
    private ViewPager pager;
    private boolean isCommitted=false;
    private TextView tvHint;
    private FragmentStatePagerAdapter adapter;
    private int pos;
    private View[] dots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_question);
        AppUtils.addAvtivity(this);
        retrieveData();
        initViews();
        initDots();
        setListeners();
        pos=UserCookies.getInstance().getCurrentQuestion(practiceId);
        pager.setCurrentItem(pos);
        refreshDots(pos);
        UserCookies.getInstance().updateReadCount(questions.get(pos).getId().toString());
    }

    private void setListeners() {
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                refreshDots(position);
                UserCookies.getInstance().updateCurrentQuestion(practiceId,position);
                UserCookies.getInstance().updateReadCount(questions.get(position).getId().toString());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tvCommit.setOnClickListener(v->commitPractice());
        tvView.setOnClickListener(v->redirect());
    }

    private void redirect() {
        List<QuestionResult> results =UserCookies.getInstance().getResultFromCookies(questions);
        Intent intent=new Intent(this,ResultActivity.class);
        intent.putExtra(EXTRA_PRACTICE_ID,practiceId);
        intent.putParcelableArrayListExtra(EXTRA_RESULT, (ArrayList<? extends Parcelable>) results);
        startActivityForResult(intent, REQUEST_CODE_RESULT);

    }

    //region 提交成绩

    String info;
    private void commitPractice() {
        List<QuestionResult>results=UserCookies.getInstance().getResultFromCookies(questions);
        List<String>macs=AppUtils.getMacAddress();
        String[]items=new String[macs.size()];
        macs.toArray(items);
        info=items[0];
        new AlertDialog.Builder(this)
                .setTitle("选择Mac地址")
                .setSingleChoiceItems(items,0,(dialog, which) -> info=items[which])
                .setNegativeButton("取消",null)
                .setPositiveButton("提交",(dialog, which) -> {
                    PracticeResult result=new PracticeResult(results,apiId,"廖华毅"+info);
                    postResult(result);
                }).show();
    }
    private QuestionActivity.CountHandler handler = new QuestionActivity.CountHandler(this);
    public static class CountHandler extends AbstractStaticHandler<QuestionActivity> {
        CountHandler(QuestionActivity context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, QuestionActivity questionActivity) {
            switch (msg.what){
                case WHAT:
                    questionActivity.isCommitted=true;
                    Toast.makeText(questionActivity, "提交成功", Toast.LENGTH_SHORT).show();
                    break;
                case WHAT1:
                    Toast.makeText(questionActivity, "提交失败", Toast.LENGTH_SHORT).show();
                    break;
                case WHAT2:
                    Toast.makeText(questionActivity, "提交失败", Toast.LENGTH_SHORT).show();
                    break;
                    default:
                        break;
            }
        }
    }

    private void postResult(PracticeResult result) {
        AppUtils.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int r= PracticeService.postResult(result);
                    if (r>=200&&r<=220){
                        handler.sendEmptyMessage(WHAT);
                    }else {
                        handler.sendEmptyMessage(WHAT1);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(WHAT2);
                }
            }
        });

    }
    //endregion

    private void initDots() {
        int count=questions.size();
        dots=new View[count];
        LinearLayout container=findViewById(R.id.activity_question_dots);
        container.removeAllViews();
        int px=ViewUtils.dp2px(16,this);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(px,px);
        px=ViewUtils.dp2px(5,this);
        params.setMargins(px,px,px,px);
        for (int i= 0;i < count;i++){
            TextView tvDot=new TextView(this);
            tvDot.setLayoutParams(params);
            tvDot.setBackgroundResource(R.drawable.dot_style);
            tvDot.setTag(i);
            //todo:tvDot添加监听
            container.addView(tvDot);
            dots[i]=tvDot;
        }
    }

    private void refreshDots(int pos){
        for (int i=0;i<dots.length;i++){
            int drawable=i==pos?R.drawable.dot_fill_style:R.drawable.dot_style;
            dots[i].setBackgroundResource(drawable);
        }
    }

    private void initViews() {
        tvView=findViewById(R.id.activity_question_tv_view);
        tvCommit=findViewById(R.id.activity_question_tv_commit);
        tvHint=findViewById(R.id.activity_question_tv_hint);
        pager=findViewById(R.id.activity_question_pager);
        if (isCommitted){
            tvCommit.setVisibility(View.GONE);
            tvView.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.VISIBLE);
        }else {
            tvView.setVisibility(View.GONE);
            tvCommit.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.GONE);
        }
        adapter=new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Question question=questions.get(position);
                return QuestionFragment.newInstance(question.getId().toString(),position,isCommitted);
            }

            @Override
            public int getCount() {
                return questions.size();
            }
        };
        pager.setAdapter(adapter);
    }

    private void retrieveData(){
    practiceId=getIntent().getStringExtra(PracticesActivity.PRACTICES_ID);
    apiId=getIntent().getIntExtra(PracticesActivity.API_ID,-1);
    questions= QuestionFactory.getInstance().getByPractice(practiceId);
    if (apiId<0||questions==null||questions.size()==0){
        Toast.makeText(this,"no question",Toast.LENGTH_SHORT).show();
        finish();
    }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.setRunning(getLocalClassName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppUtils.setStopped(getLocalClassName());
    }
}
