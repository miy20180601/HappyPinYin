package com.zhouyan.happypinyin.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.RadioGroup;
import com.zhouyan.happypinyin.R;
import com.zhouyan.happypinyin.base.BaseActivity;
import com.zhouyan.happypinyin.fragment.HomeFragment;
import com.zhouyan.happypinyin.fragment.ProfileFragment;
import com.zhouyan.happypinyin.widget.ViewPagerSlide;

import butterknife.BindView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;


public class MainActivity extends BaseActivity {

    @BindView(R.id.view_pager)
    ViewPagerSlide viewPager;
    @BindView(R.id.radiogroup)
    RadioGroup radiogroup;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0){
                    return new HomeFragment();
                }else {
                    return new ProfileFragment();
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.controller_tab1:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.controller_tab3:
                        viewPager.setCurrentItem(1);
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
}
