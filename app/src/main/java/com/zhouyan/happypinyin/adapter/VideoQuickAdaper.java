package com.zhouyan.happypinyin.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;
import com.zhouyan.happypinyin.R;
import com.zhouyan.happypinyin.entities.VideoModel;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by mo on 2018/4/25.
 */

public class VideoQuickAdaper extends BaseQuickAdapter<VideoModel,BaseViewHolder> {

    public VideoQuickAdaper(@Nullable List<VideoModel> data) {
        super(R.layout.item_videoview, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoModel item) {
        JCVideoPlayerStandard jcVideoPlayer = helper.getView(R.id.videoplayer);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .priority(Priority.HIGH);
        Glide.with(jcVideoPlayer.getContext()).load(item.getImgUrl()).apply(options).into(jcVideoPlayer.thumbImageView);
        if (item.getIsMoney() == 1 && !item.getCharge()) {
            helper.setVisible(R.id.view_pay, true);
        }else{
            helper.setVisible(R.id.view_pay,false);
        }
        if (item.getIsMoney() == 1){
            helper.setVisible(R.id.tv_price,true);
            if (item.getCharge()){
                helper.setText(R.id.tv_price,"已购买");
            }else {
                helper.setText(R.id.tv_price,item.getMuch()+"元");
            }
        }else {
            helper.setVisible(R.id.tv_price,false);
        }
        jcVideoPlayer.setUp(item.getUrl(), JCVideoPlayer.SCREEN_LAYOUT_LIST, item.getName());

//        Picasso.with(jcVideoPlayer.getContext())
//                .load(item.getImgUrl())
//                .centerCrop()
//                .into(jcVideoPlayer.thumbImageView);
        helper.addOnClickListener(R.id.view_pay);
    }
}
