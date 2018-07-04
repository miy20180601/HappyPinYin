package com.zhouyan.happypinyin.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zhouwei.library.CustomPopWindow;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhouyan.happypinyin.R;
import com.zhouyan.happypinyin.adapter.VideoQuickAdaper;
import com.zhouyan.happypinyin.busmsg.PayMessage;
import com.zhouyan.happypinyin.entities.BaseEntity;
import com.zhouyan.happypinyin.entities.PrePayModel;
import com.zhouyan.happypinyin.entities.UserInfo;
import com.zhouyan.happypinyin.entities.VideoModel;
import com.zhouyan.happypinyin.network.BaseObserver;
import com.zhouyan.happypinyin.network.RetrofitFactory;
import com.zhouyan.happypinyin.network.RxSchedulers;
import com.zhouyan.happypinyin.utils.ACache;
import com.zhouyan.happypinyin.utils.Constant;
import com.zhouyan.happypinyin.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import io.reactivex.Observable;


public class MainActivity1 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    //    @BindView(R.id.fab)
    //    FloatingActionButton mFab;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;


    List<VideoModel> mDataList = new ArrayList<>();
    private VideoQuickAdaper mVideoAdapter;
    private UserInfo mUserInfo;
    private CustomPopWindow mCustomPopWindow;
    private IWXAPI mWxApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 设置一个exit transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mUserInfo = (UserInfo) ACache.get(this).getAsObject(Constant.USERINFO);
        init();
        /**初始化列表*/
        initView();
        loadData();
    }

    private void loadData() {
        Observable<BaseEntity<List<VideoModel>>> observable = RetrofitFactory.getInstance().appVideoList("");
        observable.compose(RxSchedulers.<BaseEntity<List<VideoModel>>>compose(MainActivity1.this)).subscribe(new BaseObserver<List<VideoModel>>() {
            @Override
            protected void onHandleSuccess(List<VideoModel> dataList, String msg) {
                mDataList.clear();
                mDataList.addAll(dataList);
                mVideoAdapter.setNewData(mDataList);
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(PayMessage messageEvent) {
        loadData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mVideoAdapter = new VideoQuickAdaper(mDataList);
        mVideoAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.view_pay){
                    View contentView = LayoutInflater.from(MainActivity1.this).inflate(R.layout.pop_pay,null);
                    //处理popWindow 显示内容
                    handleLogic(contentView,mVideoAdapter.getData().get(position));
                    //创建并显示popWindow
                    mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(MainActivity1.this)
                            .setView(contentView)
                            .size(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)//显示大小
                            .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                            .setBgDarkAlpha(0.7f) // 控制亮度
                            .create()
                            .showAtLocation(mDrawerLayout, Gravity.BOTTOM,0,0);
                }
            }
        });
        mRecyclerView.setAdapter(mVideoAdapter);
    }

    /**
     * 初始化标题栏及导航栏
     */
    private void init() {
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mNavView.setNavigationItemSelectedListener(this);
        View headerLayout = mNavView.inflateHeaderView(R.layout.nav_header_main);
        TextView tVuserName = (TextView) headerLayout.findViewById(R.id.tv_user_name);
        tVuserName.setText(mUserInfo == null ? "暂无昵称" : mUserInfo.getNickName());
    }

    /**
     * 处理弹出显示内容、点击事件等逻辑
     * @param contentView
     * @param videoModel
     */
    private void handleLogic(View contentView, final VideoModel videoModel){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.ll_wxpay:
                        Observable<BaseEntity<PrePayModel>> observable = RetrofitFactory
                                .getInstance().prepay(videoModel.getMuch(),videoModel.getVideoId
                                        (),"",mUserInfo.getUserId());
                        observable.compose(RxSchedulers.<BaseEntity<PrePayModel>>compose(MainActivity1.this)).subscribe(new BaseObserver<PrePayModel>() {
                            @Override
                            protected void onHandleSuccess(PrePayModel payModel, String msg) {
                                weixinPay(payModel);
                            }
                        });
                        break;
                    case R.id.ll_alipay:
                        // TODO: 2018/5/11 支付宝支付
                        break;
                }
                if(mCustomPopWindow!=null){
                    mCustomPopWindow.dissmiss();
                }
            }
        };
        contentView.findViewById(R.id.ll_wxpay).setOnClickListener(listener);
        contentView.findViewById(R.id.ll_alipay).setOnClickListener(listener);
        contentView.findViewById(R.id.tv_cancel).setOnClickListener(listener);
        TextView tvPrice = contentView.findViewById(R.id.tv_price);
        tvPrice.setText(videoModel.getMuch()+"元");
    }

    /**
     * 调用微信支付
     */
    private void weixinPay(PrePayModel payModel) {
        mWxApi = WXAPIFactory.createWXAPI(MainActivity1.this, null);
        mWxApi.registerApp(Constant.WX_APP_ID);
        //调用微信支付
        PayReq payReq = new PayReq();
        payReq.appId = Constant.WX_APP_ID;
        payReq.partnerId = payModel.getPartnerid();
        payReq.prepayId = payModel.getPrepayid();
        payReq.nonceStr =payModel.getNoncestr();
        payReq.timeStamp = payModel.getTimestamp();
        payReq.packageValue = payModel.getPackageX();
        payReq.sign = payModel.getSign();
        mWxApi.sendReq(payReq);
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.nav_sign_out:
                new AlertDialog.Builder(this)
                        .setMessage("确定退出吗？")
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SPUtil.clear(MainActivity1.this,Constant.LOGIN_PHONE);
                                SPUtil.clear(MainActivity1.this,Constant.LOGIN_PASSWORD);
                                SPUtil.put(MainActivity1.this,Constant.ISLOGIN,false);
                                startActivity(new Intent(MainActivity1.this,LoginActivity.class));
                            }
                        }).setPositiveButton("取消",null).show();

                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
