package com.jhson.imageload;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ProgressBar;

import com.jhson.imageload.adpater.ImageAdapter;
import com.jhson.imageload.connection.CacheHttpConnection;
import com.jhson.imageload.model.ImageModel;
import com.jhson.imageload.parser.pull.ImageXmlPullParser;
import com.jhson.imageload.task.BaseAsyncTask;
import com.jhson.imageload.task.ImageParsingTask;
import com.jhson.imageload.view.ImageLoadRecyclerView;

import java.util.List;

public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener{
    private String HTTP_URL = "http://www.gettyimagesgallery.com/collections/archive/slim-aarons.aspx";

    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private ImageLoadRecyclerView mRecyclerView = null;
    private ImageParsingTask mImagePasingTask = null;
    private ImageAdapter mAdapter = null;
    private ProgressBar mProgressBar = null;
    private ViewStub mEmptyView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mRecyclerView = (ImageLoadRecyclerView) findViewById(R.id.recycler_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mEmptyView = (ViewStub) findViewById(R.id.vs_empty);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setOnScrollListener(mOnScrollListener);

        loadData(false);
    }

    private void loadData(boolean isCacheRefresh){
        if(mImagePasingTask != null){
            mImagePasingTask.cancel(true);
        }
        mImagePasingTask = new ImageParsingTask(MainActivity.this, new ImageXmlPullParser(), new CacheHttpConnection(isCacheRefresh));
        mImagePasingTask.setOnParsingLisnter(new BaseAsyncTask.OnParsingLisnter<List<ImageModel>>() {
            @Override
            public void onParsingSuccess(List<ImageModel> imageModels) {

                if(getApplicationContext() == null){
                    return ;
                }

                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }
                mSwipeRefreshLayout.setRefreshing(false);
                if (imageModels == null || imageModels.size() == 0) {
                    showEmptyView();
                    return ;
                }
                hideEmptyView();

                if (mRecyclerView != null) {
                    if(mAdapter == null){
                        mAdapter = new ImageAdapter(MainActivity.this, imageModels);
                        mRecyclerView.setAdapter(mAdapter);
                    }else {
                        mAdapter.setList(imageModels);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        mImagePasingTask.execute(HTTP_URL);
    }

    RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if(mSwipeRefreshLayout != null){
                mSwipeRefreshLayout.setEnabled(mRecyclerView.getVerticalScrollOffset() == 0);
            }
        }
    };

    @Override
    public void onRefresh() {
        loadData(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mImagePasingTask != null){
            mImagePasingTask.cancel(true);
        }
    }

    private void showEmptyView(){
        mEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void hideEmptyView(){
        mEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
