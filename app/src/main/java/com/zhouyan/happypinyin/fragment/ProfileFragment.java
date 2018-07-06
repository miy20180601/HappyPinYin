package com.zhouyan.happypinyin.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhouyan.happypinyin.R;
import com.zhouyan.happypinyin.activity.EditProfileActivity;
import com.zhouyan.happypinyin.activity.LoginActivity;
import com.zhouyan.happypinyin.activity.MainActivity1;
import com.zhouyan.happypinyin.base.BaseFragment;
import com.zhouyan.happypinyin.entities.BaseEntity;
import com.zhouyan.happypinyin.entities.UserInfo;
import com.zhouyan.happypinyin.network.BaseObserver;
import com.zhouyan.happypinyin.network.RetrofitFactory;
import com.zhouyan.happypinyin.network.RxSchedulers;
import com.zhouyan.happypinyin.utils.Constant;
import com.zhouyan.happypinyin.utils.DeviceUuidFactory;
import com.zhouyan.happypinyin.utils.NToast;
import com.zhouyan.happypinyin.utils.SPUtil;
import com.zhouyan.happypinyin.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;

/**
 * Created by Mohaifeng on 18/7/4.
 */
public class ProfileFragment extends BaseFragment {

    @BindView(R.id.profile_avatar)
    CircleImageView profileAvatar;
    @BindView(R.id.profile_name)
    TextView profileName;
    @BindView(R.id.profile_id)
    TextView profileId;
    Unbinder unbinder;
    private UserInfo mUserInfo;

    @Override
    public int getLayoutId() {
        return R.layout.fg_profile;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();
        mUserInfo = (UserInfo) mACache.getAsObject(Constant.USERINFO);
        RequestOptions options = new RequestOptions();
        options.error(R.mipmap.data_button_avatar_n);
        Glide.with(mContext).load(mUserInfo.getPhoto()).apply(options).into(profileAvatar);
        profileName.setText(mUserInfo.getNickName());
        profileId.setText(mUserInfo.getAge()+"岁");

    }

    @OnClick({R.id.edit_profile, R.id.tv_logout,R.id.tv_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edit_profile:
                startActivity(EditProfileActivity.class);
                break;
            case R.id.tv_logout:
                new AlertDialog.Builder(mContext)
                        .setMessage("确定退出吗？")
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                applogout();
                            }
                        }).setPositiveButton("取消",null).show();

                break;
            case R.id.tv_share:
                shareToWx();
                break;
        }
    }

    private void shareToWx() {
        IWXAPI wxApi = WXAPIFactory.createWXAPI(mContext, null);
        wxApi.registerApp(Constant.WX_APP_ID);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "http://www.baidu.com";
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "快乐拼音";
        msg.description = "孩子学习的好帮手";
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        msg.thumbData = Util.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
//        req.scene = isTimelineCb.isChecked() ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        req.scene =  SendMessageToWX.Req.WXSceneSession;
        wxApi.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private void applogout() {
        Observable<BaseEntity<String>> observable = RetrofitFactory
                .getInstance().appLogout(DeviceUuidFactory.getUniquePsuedoID());
        observable.compose(RxSchedulers.<BaseEntity<String>>compose(mContext)).subscribe(new BaseObserver<String>() {
                    @Override
                    protected void onHandleSuccess(String s, String msg) {
                        NToast.shortToast(mContext,msg);
                        SPUtil.clear(mContext, Constant.LOGIN_PHONE);
                        SPUtil.clear(mContext,Constant.LOGIN_PASSWORD);
                        SPUtil.put(mContext,Constant.ISLOGIN,false);
                        startActivity(new Intent(mContext,LoginActivity.class));
                    }

            @Override
            protected void onHandleError(int statusCode, String msg) {
                super.onHandleError(statusCode, msg);
                NToast.shortToast(mContext,msg);
            }
        });
    }
}
