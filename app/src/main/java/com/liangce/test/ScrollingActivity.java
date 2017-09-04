package com.liangce.test;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import qiu.niorgai.StatusBarCompat;

public class ScrollingActivity extends AppCompatActivity {

    private PtrFrameLayout mPtrFrameLayout;
    private AppBarLayout mBarLayout;
    private RecyclerView mRecyclerView;
    private CollapsingToolbarLayout toolbarLayout;
    private float height;
    private float h_b;
    private int lastOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        height = getResources().getDimension(R.dimen.app_bar_height);
        h_b = 255/height;

        StatusBarCompat.translucentStatusBar(ScrollingActivity.this, false);

        mBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);


        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }

        mPtrFrameLayout = (PtrFrameLayout) findViewById(R.id.store_house_ptr_frame);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPtrFrameLayout.refreshComplete();
                    }
                }, 1000);
            }
        });

        final PtrClassicDefaultHeader header = new PtrClassicDefaultHeader(this);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.setHeaderView(header);

        mBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                boolean isDown = (lastOff < verticalOffset);

                mPtrFrameLayout.setEnabled(false);
                if (verticalOffset == 0){
                    mPtrFrameLayout.setEnabled(true);
                    StatusBarCompat.translucentStatusBar(ScrollingActivity.this, false);
                }else{
                    int alpha = 255 - (int)(-verticalOffset*h_b);
                    if (alpha > 200&&isDown){
                        // TODO: 2017/9/2  慢慢滑动造成闪烁
                        StatusBarCompat.translucentStatusBar(ScrollingActivity.this, false);
                    }else{
                        StatusBarCompat.setStatusBarColor(ScrollingActivity.this,Color.RED, alpha);
                    }
                }

                lastOff = verticalOffset;
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new TestAdapter());
    }

}
