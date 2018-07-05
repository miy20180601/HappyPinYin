package com.zhouyan.happypinyin.fragment;

import android.content.DialogInterface;
import android.content.Intent;
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

    @OnClick({R.id.edit_profile, R.id.tv_logout})
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
        }
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
