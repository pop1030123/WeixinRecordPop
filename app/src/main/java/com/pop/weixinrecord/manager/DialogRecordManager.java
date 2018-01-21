package com.pop.weixinrecord.manager;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pop.weixinrecord.R;


public class DialogRecordManager implements View.OnClickListener {

    /**
     * 以下为dialog的初始化控件，包括其中的布局文件
     */

    private Dialog mDialog;

    private RelativeLayout imgBg;

    private TextView tipTxt;
    private TextView cancelBtn;
    private TextView finishBtn;
    private Context mContext;

    private Callback mCallback ;

    public DialogRecordManager(Context context ,Callback callback) {
        mContext = context;
        this.mCallback = callback ;
    }

    public void showRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.Theme_audioDialog);
        // 用layoutinflater来引用布局
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_record_manager, null);
        mDialog.setContentView(view);
        imgBg = (RelativeLayout) view.findViewById(R.id.dm_rl_bg);
        tipTxt = (TextView) view.findViewById(R.id.tip_txt);
        cancelBtn = (TextView) view.findViewById(R.id.cancel_tv_txt);
        finishBtn = (TextView) view.findViewById(R.id.finish_tv_txt);

        cancelBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);

        mDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel_tv_txt:
                if(mCallback != null){
                    mCallback.onCancel();
                }
                break ;
            case R.id.finish_tv_txt:
                if(mCallback != null){
                    mCallback.onFinish();
                }
                break ;
        }
    }

    public interface Callback{
        void onCancel() ;
        void onFinish() ;
    }

    /**
     * 设置正在录音时的dialog界面
     */
    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            imgBg.setVisibility(View.VISIBLE);
            imgBg.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.yuyin_voice_1));
        }
    }


    // 时间过短
    public void tooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            imgBg.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.yuyin_gantanhao));
            Toast.makeText(mContext ,R.string.time_too_short ,Toast.LENGTH_SHORT).show();
        }

    }

    // 隐藏dialog
    public void dimissDialog() {

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }

    }

    public void updateVoiceLevel(int level) {
        if (level > 0 && level < 6) {

        } else {
            level = 5;
        }
        if (mDialog != null && mDialog.isShowing()) {

            //通过level来找到图片的id，也可以用switch来寻址，但是代码可能会比较长
            int resId = mContext.getResources().getIdentifier("yuyin_voice_" + level,
                    "drawable", mContext.getPackageName());
            imgBg.setBackgroundResource(resId);
        }

    }

    public TextView getTipsTxt() {
        return tipTxt;
    }

    public void setTipsTxt(TextView tipsTxt) {
        this.tipTxt = tipsTxt;
    }
}
