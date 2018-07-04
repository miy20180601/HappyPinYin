package com.zhouyan.happypinyin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhouyan.happypinyin.R;
import com.zhouyan.happypinyin.entities.BaseEntity;
import com.zhouyan.happypinyin.entities.UserInfo;
import com.zhouyan.happypinyin.network.BaseObserver;
import com.zhouyan.happypinyin.network.RetrofitFactory;
import com.zhouyan.happypinyin.network.RxSchedulers;
import com.zhouyan.happypinyin.utils.ACache;
import com.zhouyan.happypinyin.utils.Constant;
import com.zhouyan.happypinyin.utils.DeviceUuidFactory;
import com.zhouyan.happypinyin.utils.LMUtils;
import com.zhouyan.happypinyin.utils.NToast;
import com.zhouyan.happypinyin.utils.SPUtil;
import com.zhouyan.happypinyin.widget.ClearWriteEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;


/**
 * Created by mo on 2018/4/9.
 */

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.iv_img_bg)
    ImageView mIvImgBg;
    @BindView(R.id.tv_login_forgot)
    TextView mTvLoginForgot;
    @BindView(R.id.tv_login_register)
    TextView mTvLoginRegister;
    @BindView(R.id.btn_login_sign)
    Button mBtnLoginSign;
    @BindView(R.id.et_login_phone)
    ClearWriteEditText mPhoneEdit;
    @BindView(R.id.et_login_password)
    ClearWriteEditText mPasswordEdit;
    @BindView(R.id.rl_body)
    RelativeLayout mRlBody;


    private boolean isLogin;
    private ACache mACache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mACache = ACache.get(this);
        initView();
        initListener();
    }


    private void initView() {
        isLogin = (boolean) SPUtil.get(LoginActivity.this, Constant.ISLOGIN, false);
        String loginPhone = (String) SPUtil.get(LoginActivity.this, Constant.LOGIN_PHONE, "");
        String loginPassword = (String) SPUtil.get(LoginActivity.this, Constant.LOGIN_PASSWORD, "");
        imgBackgroundAnimat();
        if (isLogin && !TextUtils.isEmpty(loginPhone) && !TextUtils.isEmpty(loginPassword)){
            doLogin(loginPhone,loginPassword);
        }else {
            mRlBody.setVisibility(View.VISIBLE);
        }

    }

    private void initListener() {
        mPhoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 11)
                    LMUtils.onInactive(LoginActivity.this, mPhoneEdit);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    //背景图动画
    private void imgBackgroundAnimat() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.translate_anim);
                mIvImgBg.startAnimation(animation);
            }
        }, 200);
    }

    @OnClick({R.id.btn_login_sign, R.id.tv_login_forgot, R.id.tv_login_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login_sign:
                String phoneString = mPhoneEdit.getText().toString().trim();
                String passwordString = mPasswordEdit.getText().toString().trim();
                if (TextUtils.isEmpty(phoneString)) {
                    NToast.shortToast(LoginActivity.this, R.string.phone_number_is_null);
                    mPhoneEdit.setShakeAnimation();
                    return;
                }
                if (TextUtils.isEmpty(passwordString)) {
                    NToast.shortToast(LoginActivity.this, R.string.password_is_null);
                    mPasswordEdit.setShakeAnimation();
                    return;
                }
                if (passwordString.contains(" ")) {
                    NToast.shortToast(LoginActivity.this, R.string.password_cannot_contain_spaces);
                    mPasswordEdit.setShakeAnimation();
                    return;
                }
                doLogin(phoneString, passwordString);
                break;
            case R.id.tv_login_forgot:
                // TODO: 2018/4/10  找回密码
                //                startActivityForResult(new Intent(this, ForgetPasswordActivity.class), 2);
                break;
            case R.id.tv_login_register:
                startActivityForResult(new Intent(this, RegisterActivity.class), 1);
                break;
        }
    }

    private void doLogin(final String phone, final String password) {
        Observable<BaseEntity<UserInfo>> observable = RetrofitFactory.getInstance().applogin
                (phone, password, DeviceUuidFactory.getUniquePsuedoID());
        observable.compose(RxSchedulers.<BaseEntity<UserInfo>>compose(LoginActivity.this)).subscribe(new BaseObserver<UserInfo>() {

            @Override
            protected void onHandleSuccess(UserInfo userInfo, String msg) {
                //登录成功
//                NToast.shortToast(LoginActivity.this, "登录成功");
                if (!isLogin){
                    SPUtil.put(LoginActivity.this, Constant.ISLOGIN, true);
                    SPUtil.put(LoginActivity.this, Constant.LOGIN_PHONE, phone);
                    SPUtil.put(LoginActivity.this, Constant.LOGIN_PASSWORD, password);
                }
                mACache.put(Constant.USERINFO,userInfo);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            protected void onHandleError(int statusCode, String msg) {
                NToast.shortToast(LoginActivity.this, msg);
            }
        });
    }
}
