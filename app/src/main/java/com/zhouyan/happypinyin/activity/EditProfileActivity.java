package com.zhouyan.happypinyin.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhouyan.happypinyin.R;
import com.zhouyan.happypinyin.base.BaseActivity;
import com.zhouyan.happypinyin.entities.BaseEntity;
import com.zhouyan.happypinyin.entities.UserInfo;
import com.zhouyan.happypinyin.network.BaseObserver;
import com.zhouyan.happypinyin.network.RetrofitFactory;
import com.zhouyan.happypinyin.network.RxSchedulers;
import com.zhouyan.happypinyin.utils.Constant;
import com.zhouyan.happypinyin.utils.PhotoUtils;
import com.zhouyan.happypinyin.widget.ActionSheetDialog;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import top.zibin.luban.Luban;

/**
 * Created by Mohaifeng on 18/7/4.
 */
public class EditProfileActivity extends BaseActivity {

    @BindView(R.id.bar_title)
    TextView barTitle;
    @BindView(R.id.bar_tv_right)
    TextView barTvRight;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.iv_avatar)
    CircleImageView ivAvatar;

    private PhotoUtils photoUtils;
    private String mAge;
    private File mAvatarFile;

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        barTitle.setText("编辑个人资料");
        barTvRight.setText("修改");
        barTvRight.setVisibility(View.VISIBLE);
        setPortraitChangeListener();
    }

    /**
     * 初始化图片选择工具及注册回调监听
     */
    private void setPortraitChangeListener() {
        photoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                    compressWithRx(new File(uri.getPath()));
                }
            }

            @Override
            public void onPhotoCancel() {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PhotoUtils.INTENT_CROP:
            case PhotoUtils.INTENT_TAKE:
            case PhotoUtils.INTENT_SELECT:
                photoUtils.onActivityResult(EditProfileActivity.this, requestCode, resultCode,
                        data);
                break;

        }
    }

    /**
     * 压缩图片
     */
    @SuppressLint("CheckResult")
    private void compressWithRx(File file) {
        Flowable.just(file)
                .observeOn(Schedulers.io())
                .map(new Function<File, File>() {
                    @Override
                    public File apply(@NonNull File file) throws Exception {
                        return Luban.with(mContext).load(file).get();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(@NonNull File file) throws Exception {
                        mAvatarFile = file;
                        Glide.with(mContext).load(file).into(ivAvatar);
                    }
                });
    }

    /**
     * 上传头像
     */
    private void updateImage(final File file) {


    }

    @OnClick({R.id.bar_iv_back, R.id.rl_avatar, R.id.rl_age,R.id.bar_tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bar_iv_back:
                finish();
                break;
            case R.id.rl_avatar:
                showPhotoDialog();
                break;
            case R.id.rl_age:
                alertDialog();
                break;
            case R.id.bar_tv_right:
                updateInfo(mAvatarFile);
                break;
        }
    }

    private void updateInfo(File file) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (!TextUtils.isEmpty(mAge)){
            builder.addFormDataPart("age",mAge);
        }

        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        builder.addFormDataPart("type", file.getName(), imageBody);//"files" 后台接收图片流的参数名
        List<MultipartBody.Part> parts = builder.build().parts();
        Observable<BaseEntity<UserInfo>> observable = RetrofitFactory.getInstance()
                .appUpdateInfo(parts);
        observable.compose(RxSchedulers.<BaseEntity<UserInfo>>compose(mContext)).subscribe(new BaseObserver<UserInfo>() {

            @Override
            protected void onHandleSuccess(UserInfo userInfo, String msg) {
                mACache.put(Constant.USERINFO,userInfo);
                finish();
            }
        });
    }

    private void alertDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.dialog_edit_age);
        final EditText editAge = window.findViewById(R.id.edit_age);
        TextView tvCancel = window.findViewById(R.id.tv_cancel);
        TextView tvConfirm = window.findViewById(R.id.tv_confirm);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAge = editAge.getText().toString().trim();
                if (!TextUtils.isEmpty(mAge)){
                    tvAge.setText(mAge+"岁");
                }
                dialog.dismiss();
            }
        });

    }
    /**
     * 弹出图片选择框
     */
    @TargetApi(23)
    private void showPhotoDialog() {
        ActionSheetDialog dialog = new ActionSheetDialog(this);
        dialog.builder().setTitle("选择照片")
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addSheetItem("相机", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog
                        .OnSheetItemClickListener() {

                    @Override
                    public void onClick(int which) {
                        initPermission();
                        photoUtils.takePicture(EditProfileActivity.this);
                    }

                })
                .addSheetItem("相册", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog
                        .OnSheetItemClickListener() {

                            @Override
                            public void onClick(int which) {
                                initPermission();
                                photoUtils.selectPicture(EditProfileActivity.this);
                            }
                        }

                );
        dialog.show();

    }

    @SuppressLint("CheckResult")
    private void initPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA
                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean aBoolean) throws Exception {
                                   if (aBoolean) {
                                       // All requested permissions are granted
                                   } else {
                                       // At least one permission is denied
                                   }
                               }
                           }
                );
    }
}
