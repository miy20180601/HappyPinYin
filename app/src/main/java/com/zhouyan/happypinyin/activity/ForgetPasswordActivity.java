package com.zhouyan.happypinyin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhouyan.happypinyin.R;
import com.zhouyan.happypinyin.base.BaseActivity;
import com.zhouyan.happypinyin.entities.BaseEntity;
import com.zhouyan.happypinyin.network.BaseObserver;
import com.zhouyan.happypinyin.network.RetrofitFactory;
import com.zhouyan.happypinyin.network.RxSchedulers;
import com.zhouyan.happypinyin.utils.LMUtils;
import com.zhouyan.happypinyin.utils.NToast;
import com.zhouyan.happypinyin.widget.ClearWriteEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;

public class ForgetPasswordActivity extends BaseActivity {
    @BindView(R.id.iv_img_bg)
    ImageView mIvImgBg;
    @BindView(R.id.reg_phone)
    ClearWriteEditText mPhoneEdit;
    @BindView(R.id.reg_code)
    ClearWriteEditText mCodeEdit;
    @BindView(R.id.reg_getcode)
    Button mBtnGetCode;
    @BindView(R.id.reg_password)
    ClearWriteEditText mPasswordEdit;
    @BindView(R.id.reg_button)
    Button mBtnConfirm;
    @BindView(R.id.reg_login)
    TextView mRegLogin;
    @BindView(R.id.reg_confirm_password)
    ClearWriteEditText mConfirmPasswordEdit;


    //正在获取请求码
    boolean isBright = true;

    private String  mPhone, mCode, mPassword,mConfirmPwd;
    //信息是否填写
    private boolean isPwdFill, isCodeFill, isPhoneFill,isConfirmPwdFill;

    /**
     * 倒计时60秒，一次1秒
     */
    CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            isBright = false;
            mBtnGetCode.setText(millisUntilFinished / 1000 + "s");
            mBtnGetCode.setEnabled(false);
        }

        @Override
        public void onFinish() {
            isBright = true;
            mBtnGetCode.setText("获取验证码");
            mBtnGetCode.setEnabled(true);
        }
    };

    @BindView(R.id.main)
    LinearLayout mMain;



    @Override
    public int getLayoutId() {
        return R.layout.activity_forget_pwd;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
    }


    private void initView() {
        imgBackgroundAnimat();
        addEditTextListener();
    }

    //背景图动画
    private void imgBackgroundAnimat() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.translate_anim);
                mIvImgBg.startAnimation(animation);
            }
        }, 200);
    }

    private void addEditTextListener() {

        mPhoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 11 && isBright) {
                    if (LMUtils.isMobile(s.toString().trim())) {
                        mPhone = s.toString();
                        mBtnGetCode.setEnabled(true);
                        isPhoneFill = true;
                        LMUtils.onInactive(mContext, mPhoneEdit);
                    } else {
                        Toast.makeText(mContext, R.string.Illegal_phone_number, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mBtnGetCode.setEnabled(false);
                    isPhoneFill = false;
                }
                checkConfirmEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mCodeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    isCodeFill = true;
                    mCode = s.toString();
                    LMUtils.onInactive(mContext, mCodeEdit);
                } else {
                    isCodeFill = false;
                }
                checkConfirmEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 5) {
                    isPwdFill = true;
                    mPassword = s.toString();
                } else {
                    isPwdFill = false;
                }
                checkConfirmEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mConfirmPasswordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 5) {
                    isConfirmPwdFill = true;
                    mConfirmPwd = s.toString();
                } else {
                    isConfirmPwdFill = false;
                }
                checkConfirmEnable();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void checkConfirmEnable() {
        if (isPhoneFill && isCodeFill && isPwdFill && isConfirmPwdFill)
            mBtnConfirm.setEnabled(true);
        else mBtnConfirm.setEnabled(false);
    }


    @OnClick({R.id.reg_getcode, R.id.reg_button, R.id.tv_reg, R.id.reg_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.reg_getcode:
                if (TextUtils.isEmpty(mPhoneEdit.getText().toString().trim())) {
                    NToast.longToast(mContext, R.string.phone_number_is_null);
                } else {
                    timer.start();
                    doSendCode();
                }
                break;
            case R.id.reg_button:
                if (mPassword.equals(mConfirmPwd)){
                    doRegister();
                }else {
                    NToast.shortToast(mContext,"两次输入的密码不一致，请重新输入");
                }
                break;
            case R.id.tv_reg:
                startActivity(new Intent(mContext,RegisterActivity.class));
                break;
            case R.id.reg_login:
                startActivity(new Intent(mContext, LoginActivity.class));
                break;
        }
    }

    //注册
    private void doRegister() {
        Observable<BaseEntity<String>> observable = RetrofitFactory.getInstance().appModifyPassword(mPhone, mCode, mPassword);
        observable.compose(RxSchedulers.<BaseEntity<String>>compose(mContext)).subscribe(new BaseObserver<String>() {
            @Override
            protected void onHandleSuccess(String s, String msg) {
                NToast.shortToast(mContext, s);
                startActivity(new Intent(mContext, LoginActivity.class));
            }

            @Override
            protected void onHandleError(int statusCode, String msg) {
                NToast.shortToast(mContext, msg);
            }
        });
    }

    //    发送验证码
    private void doSendCode() {
        Observable<BaseEntity<String>> observable = RetrofitFactory.getInstance().appSendCodeForPassword(mPhone);
        observable.compose(RxSchedulers.<BaseEntity<String>>compose(mContext)).subscribe(new BaseObserver<String>() {
            @Override
            protected void onHandleSuccess(String s, String msg) {
                NToast.shortToast(mContext, R.string.messge_send);
        }

            @Override
            protected void onHandleError(int statusCode, String msg) {
                NToast.shortToast(mContext, msg);
            }
        });
    }


}
