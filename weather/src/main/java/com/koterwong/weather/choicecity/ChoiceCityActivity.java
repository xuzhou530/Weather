package com.koterwong.weather.choicecity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.koterwong.weather.beans.City;
import com.koterwong.weather.beans.Province;
import com.koterwong.weather.R;
import com.koterwong.weather.utils.ToolsUtil;
import com.koterwong.weather.choicecity.View.CityView;
import com.koterwong.weather.choicecity.presenter.CityPresenter;
import com.koterwong.weather.choicecity.presenter.CityPresenterImp;
import com.koterwong.weather.commons.SavedCityDBManager;

import java.util.List;

public class ChoiceCityActivity extends AppCompatActivity implements CityView {


    //ui
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    private CityPresenter mCityPresenter;
    private CityListAdapter mAdapter;
    private CollapsingToolbarLayout mCollapsing;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCityPresenter = new CityPresenterImp(this);
        initView();
        initEvent();
    }

    private void initView() {
        setContentView(R.layout.activity_choice_city);

//        mSwipeBackLayout = getSwipeBackLayout();
//        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        //ScrollToolbar
        mCollapsing = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mCollapsing.setTitle("选择城市");

        //back
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_city);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CityListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        loadDatas();
    }

    private void initEvent() {
        mAdapter.setOnItemClickListener(new CityListAdapter.OnItemClickListener() {
            @Override
            public void onClick(View itemView, int position, String msg ,String id, int level) {
                switch (level) {
                    case CityListAdapter.LEVEL_PROVINCE:
                        mCityPresenter.queryCity(id);
                        mCollapsing.setTitle(msg);
                        break;
                    case CityListAdapter.LEVEL_CITY:
                        /**
                         * 判断保存城市的数据中是否存在该城市
                         */
                        boolean containTheCity = SavedCityDBManager.getInstance(ChoiceCityActivity.this).isExistCity(msg);
                        if (containTheCity){
                            Snackbar.make(mRecyclerView,"已经包含该城市",Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        if (!ToolsUtil.isNetworkAvailable(ChoiceCityActivity.this)){
                            Snackbar.make(mRecyclerView,"网络未连接，请连接网络重试",Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        Intent mIntent = new Intent();
                        mIntent.putExtra("city",msg);
                        setResult(RESULT_OK,mIntent);
                        finish();
                        break;
                }
            }
        });
    }

    private void loadDatas() {
        mCityPresenter.loadDataList();
    }

    @Override
    public void setProDatas(List<Province> mDatas) {
        mAdapter.setmProDatas(mDatas);
    }

    @Override
    public void setCityDatas(List<City> mDatas) {
        mAdapter.setmCityDatass(mDatas);
    }

    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setTitle(String title) {
        mCollapsing.setTitle(title);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (mAdapter.currentLevel == CityListAdapter.LEVEL_CITY){
            //回到省份
            mCityPresenter.loadDataList();
        }else{
            super.onBackPressed();
        }
    }
}