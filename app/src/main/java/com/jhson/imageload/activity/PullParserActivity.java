package com.jhson.imageload.activity;

import com.jhson.imageload.adpater.ImageAdapter;
import com.jhson.imageload.connection.CacheHttpConnection;
import com.jhson.imageload.model.ImageModel;
import com.jhson.imageload.parser.pull.ImageXmlPullParser;
import com.jhson.imageload.task.BaseAsyncTask;
import com.jhson.imageload.task.ImageParsingTask;

import java.util.List;

/**
 * Created by jhson on 2016-01-16.
 */
public class PullParserActivity extends BaseActivity{

    private ImageParsingTask mImagePasingTask = null;

    @Override
    protected void loadData(boolean isCacheRefresh){
        if(mImagePasingTask != null){
            mImagePasingTask.cancel(true);
        }
        mImagePasingTask = new ImageParsingTask(this, new ImageXmlPullParser(), new CacheHttpConnection(this, isCacheRefresh));
        mImagePasingTask.setOnParsingLisnter(new BaseAsyncTask.OnParsingLisnter<List<ImageModel>>() {
            @Override
            public void onParsingSuccess(List<ImageModel> imageModels) {

                if(getApplicationContext() == null){
                    return ;
                }

                hideProgressView();
                mSwipeRefreshLayout.setRefreshing(false);
                if (imageModels == null || imageModels.size() == 0) {
                    showEmptyView();
                    return ;
                }
                hideEmptyView();

                if (mRecyclerView != null) {
                    if(mAdapter == null){
                        mAdapter = new ImageAdapter(PullParserActivity.this, imageModels);
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
}
