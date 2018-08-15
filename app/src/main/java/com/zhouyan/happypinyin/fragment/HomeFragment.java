package com.zhouyan.happypinyin.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zhouwei.library.CustomPopWindow;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhouyan.happypinyin.BuildConfig;
import com.zhouyan.happypinyin.R;
import com.zhouyan.happypinyin.adapter.VideoQuickAdaper;
import com.zhouyan.happypinyin.base.BaseFragment;
import com.zhouyan.happypinyin.busmsg.LoginMessage;
import com.zhouyan.happypinyin.busmsg.PayMessage;
import com.zhouyan.happypinyin.entities.BaseEntity;
import com.zhouyan.happypinyin.entities.PrePayModel;
import com.zhouyan.happypinyin.entities.UserInfo;
import com.zhouyan.happypinyin.entities.VideoModel;
import com.zhouyan.happypinyin.network.BaseObserver;
import com.zhouyan.happypinyin.network.RetrofitFactory;
import com.zhouyan.happypinyin.network.RxSchedulers;
import com.zhouyan.happypinyin.utils.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import io.reactivex.Observable;

/**
 * Created by Mohaifeng on 18/7/3.
 */
public class HomeFragment extends BaseFragment {

    @BindView(R.id.bar_iv_back)
    ImageView barIvBack;
    @BindView(R.id.bar_title)
    TextView barTitle;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    List<VideoModel> mDataList = new ArrayList<>();
    private VideoQuickAdaper mVideoAdapter;
    private UserInfo mUserInfo;
    private CustomPopWindow mCustomPopWindow;
    private IWXAPI mWxApi;

    private String isTotalPay;
    private double totalPrice;

    @Override
    public int getLayoutId() {
        return R.layout.fg_home;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserInfo = (UserInfo) mACache.getAsObject(Constant.USERINFO);
        EventBus.getDefault().register(this);
        initView();
        loadData();
    }

    private void initView() {
        barIvBack.setVisibility(View.GONE);
        barTitle.setText("快乐拼音");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mVideoAdapter = new VideoQuickAdaper(mDataList);
        mVideoAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.view_pay) {
                    View contentView = LayoutInflater.from(mContext).inflate(R.layout
                            .pop_pay, null);
                    //处理popWindow 显示内容
                    handleLogic(contentView, mVideoAdapter.getData().get(position));
                    //创建并显示popWindow
                    mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
                            .setView(contentView)
                            .size(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                                    .WRAP_CONTENT)//显示大小
                            .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                            .setBgDarkAlpha(0.7f) // 控制亮度
                            .create()
                            .showAtLocation(mRecyclerView, Gravity.BOTTOM, 0, 0);
                }
            }
        });
        mRecyclerView.setAdapter(mVideoAdapter);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
    }

    private void loadData() {
        Observable<BaseEntity<List<VideoModel>>> observable = RetrofitFactory.getInstance()
                .appVideoList("");
        observable.compose(RxSchedulers.<BaseEntity<List<VideoModel>>>compose(mContext)).subscribe
                (new BaseObserver<List<VideoModel>>() {
                    @Override
                    protected void onHandleSuccess(List<VideoModel> dataList, String msg) {
                        if (mRefreshLayout.isRefreshing()){
                            mRefreshLayout.setRefreshing(false);
                        }
                        mDataList.clear();
                        mDataList.addAll(dataList);
                        mVideoAdapter.setNewData(mDataList);
                    }
                });

        Observable<BaseEntity<Double>> totalPriceObservable = RetrofitFactory.getInstance().appVideoTotalPrice(null);
        totalPriceObservable.compose(RxSchedulers.<BaseEntity<Double>>compose(mContext)).subscribe(new BaseObserver<Double>() {
            @Override
            protected void onHandleSuccess(Double price, String msg) {
                totalPrice = price;
            }
        });
    }

    /**
     * 处理弹出显示内容、点击事件等逻辑
     *
     * @param contentView
     * @param videoModel
     */
    private void handleLogic(View contentView, final VideoModel videoModel) {
        RadioGroup radioGroup = contentView.findViewById(R.id.radio_group);
        final TextView tvPrice = contentView.findViewById(R.id.tv_price);
        tvPrice.setText(videoModel.getMuch() + "元");
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_single:
                        isTotalPay = null;
                        tvPrice.setText(videoModel.getMuch() + "元");
                        break;
                    case R.id.rb_all:
                        isTotalPay = "yes";
                        tvPrice.setText(totalPrice + "元");
                        break;
                }
            }
        });
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ll_wxpay:
                        prePay(videoModel);
                        break;
                    case R.id.ll_alipay:
                        // TODO: 2018/5/11 支付宝支付
                        break;
                }
                if (mCustomPopWindow != null) {
                    mCustomPopWindow.dissmiss();
                }
            }
        };
        contentView.findViewById(R.id.ll_wxpay).setOnClickListener(listener);
        contentView.findViewById(R.id.ll_alipay).setOnClickListener(listener);
        contentView.findViewById(R.id.tv_cancel).setOnClickListener(listener);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(PayMessage messageEvent) {
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(LoginMessage messageEvent) {
        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void prePay(VideoModel videoModel) {
        String videoId = null;
        double amount;
        if (TextUtils.isEmpty(isTotalPay)) {
            videoId = videoModel.getVideoId();
            amount = videoModel.getMuch();
//            amount = 0.1;
        } else {
            amount = totalPrice;
//            amount = 0.1;


        }
        Observable<BaseEntity<PrePayModel>> observable = RetrofitFactory
                .getInstance().prepay(amount, videoId, isTotalPay, mUserInfo.getUserId());
        observable.compose(RxSchedulers.<BaseEntity<PrePayModel>>compose
                (mContext)).subscribe(new BaseObserver<PrePayModel>() {
            @Override
            protected void onHandleSuccess(PrePayModel payModel, String msg) {
                weixinPay(payModel);
            }
        });
    }

    /**
     * 调用微信支付
     */
    private void weixinPay(PrePayModel payModel) {
        mWxApi = WXAPIFactory.createWXAPI(mContext, null);
        mWxApi.registerApp(Constant.WX_APP_ID);
        //调用微信支付
        PayReq payReq = new PayReq();
        payReq.appId = Constant.WX_APP_ID;
        payReq.partnerId = payModel.getPartnerid();
        payReq.prepayId = payModel.getPrepayid();
        payReq.nonceStr = payModel.getNoncestr();
        payReq.timeStamp = payModel.getTimestamp();
        payReq.packageValue = payModel.getPackageX();
        payReq.sign = payModel.getSign();
        mWxApi.sendReq(payReq);
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();

    }

}
