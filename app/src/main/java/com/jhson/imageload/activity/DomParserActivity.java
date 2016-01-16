package com.jhson.imageload.activity;

import com.jhson.imageload.adpater.ImageAdapter;
import com.jhson.imageload.connection.HttpConnection;
import com.jhson.imageload.model.ImageModel;
import com.jhson.imageload.parser.dom.ImageDomParser;
import com.jhson.imageload.task.BaseAsyncTask;
import com.jhson.imageload.task.ImageParsingTask;

import java.util.List;

/**
 * Created by jhson on 2016-01-16.
 */
public class DomParserActivity extends BaseActivity{

    private ImageParsingTask mImagePasingTask = null;

    @Override
    protected void loadData(boolean isCacheRefresh){
        if(mImagePasingTask != null){
            mImagePasingTask.cancel(true);
        }
        mImagePasingTask = new ImageParsingTask(this, new ImageDomParser(), new HttpConnection());
        mImagePasingTask.setOnParsingLisnter(new BaseAsyncTask.OnParsingLisnter<List<ImageModel>>() {
            @Override
            public void onParsingSuccess(List<ImageModel> imageModels) {

                if (getApplicationContext() == null) {
                    return;
                }

                hideProgressView();
                mSwipeRefreshLayout.setRefreshing(false);
                if (imageModels == null || imageModels.size() == 0) {
                    showEmptyView();
                    return;
                }
                hideEmptyView();

                if (mRecyclerView != null) {
                    if (mAdapter == null) {
                        mAdapter = new ImageAdapter(DomParserActivity.this, imageModels);
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        mAdapter.setList(imageModels);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
//        mImagePasingTask.execute("https://dl-ssl.google.com/android/repository/repository.xml");
        mImagePasingTask.execute(HTTP_URL);
    }

}
