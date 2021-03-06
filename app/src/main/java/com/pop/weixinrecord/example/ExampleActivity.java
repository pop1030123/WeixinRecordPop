package com.pop.weixinrecord.example;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.pop.weixinrecord.R;
import com.pop.weixinrecord.history.DBManager;
import com.pop.weixinrecord.manager.AudioRecordButton;
import com.pop.weixinrecord.manager.AudioRecordButton2;
import com.pop.weixinrecord.manager.MediaManager;
import com.pop.weixinrecord.utils.PermissionHelper;

import java.util.ArrayList;
import java.util.List;

public class ExampleActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView mEmLvRecodeList;
    private AudioRecordButton mEmTvBtn;
    private AudioRecordButton2 mRecordBtn;
    List<Record> mRecords;
    ExampleAdapter mExampleAdapter;
    PermissionHelper mHelper;
    //db
    private DBManager mgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        initView();
        initData();
        initAdapter();
        initListener();
    }

    private void initView() {
        mEmLvRecodeList = (ListView) findViewById(R.id.em_lv_recodeList);
        mEmTvBtn = (AudioRecordButton) findViewById(R.id.em_tv_btn);
        mRecordBtn = (AudioRecordButton2) findViewById(R.id.voice_record_btn);
        mRecordBtn.setOnClickListener(this);
        //设置不想要可见或者不想被点击
        // mEmTvBtn.setVisibility(View.GONE);//隐藏
       // mEmTvBtn.setCanRecord(false);//重写该方法，设置为不可点击
    }

    private void initData() {
        mRecords = new ArrayList<>();
        //初始化DBManager
        mgr = new DBManager(this);
    }

    private void initAdapter() {
        mExampleAdapter = new ExampleAdapter(this, mRecords);
        mEmLvRecodeList.setAdapter(mExampleAdapter);

        //开始获取数据库数据
        List<Record> records = mgr.query();
        if(records==null||records.isEmpty())return;
        for (Record record : records) {
            Log.e("wgy", "initAdapter: "+record.toString() );
        }
        mRecords.addAll(records);
        mExampleAdapter.notifyDataSetChanged();
    }

    private void initListener() {
        mEmTvBtn.setHasRecordPromission(false);
        mRecordBtn.setHasRecordPromission(false);
//        授权处理
        mHelper = new PermissionHelper(this);

        mHelper.requestPermissions("请授予[录音]，[读写]权限，否则无法录音",
                new PermissionHelper.PermissionListener() {
                    @Override
                    public void doAfterGrand(String... permission) {
                        mEmTvBtn.setHasRecordPromission(true);
                        mEmTvBtn.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
                            @Override
                            public void onFinished(float seconds, String filePath) {
                                Record recordModel = new Record();
                                recordModel.setSecond((int) seconds <= 0 ? 1 : (int) seconds);
                                recordModel.setPath(filePath);
                                recordModel.setPlayed(false);
                                mRecords.add(recordModel);
                                mExampleAdapter.notifyDataSetChanged();

                                //添加到数据库
                                mgr.add(recordModel);
                            }
                        });
                        mRecordBtn.setHasRecordPromission(true);
                        mRecordBtn.setAudioFinishRecorderListener(new AudioRecordButton2.AudioFinishRecorderListener() {
                            @Override
                            public void onFinished(float seconds, String filePath) {
                                Record recordModel = new Record();
                                recordModel.setSecond((int) seconds <= 0 ? 1 : (int) seconds);
                                recordModel.setPath(filePath);
                                recordModel.setPlayed(false);
                                mRecords.add(recordModel);
                                mExampleAdapter.notifyDataSetChanged();

                                //添加到数据库
                                mgr.add(recordModel);
                            }
                        });
                    }

                    @Override
                    public void doAfterDenied(String... permission) {
                        mEmTvBtn.setHasRecordPromission(false);
                        mRecordBtn.setHasRecordPromission(false);
                        Toast.makeText(ExampleActivity.this, "请授权,否则无法录音", Toast.LENGTH_SHORT).show();
                    }
                }, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }

    //直接把参数交给mHelper就行了
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mHelper.handleRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        MediaManager.release();//保证在退出该页面时，终止语音播放
        super.onPause();
    }

    public DBManager getMgr() {
        return mgr;
    }

    public void setMgr(DBManager mgr) {
        this.mgr = mgr;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.voice_record_btn:
                // 开始录音
                mRecordBtn.startRecord();
                break ;
        }
    }
}
