package com.jhson.imageload.fragment;

import com.jhson.imageload.adpater.ImageAdapter;
import com.jhson.imageload.connection.CacheHttpConnection;
import com.jhson.imageload.model.ImageModel;
import com.jhson.imageload.parser.pull.ImageXmlPullParser;
import com.jhson.imageload.task.BaseAsyncTask;
import com.jhson.imageload.task.ImageParsingTask;

import java.util.List;

/**
 * Created by jhson on 2016-01-17.
 */
public class PullParserFragment extends BaseFragment{

    public PullParserFragment(){}

    private ImageParsingTask mImagePasingTask = null;

    @Override
    protected void loadData(boolean isCacheRefresh){
        if(mImagePasingTask != null){
            mImagePasingTask.cancel(true);
        }
        mImagePasingTask = new ImageParsingTask(getActivity(), new ImageXmlPullParser(), new CacheHttpConnection(getActivity(), isCacheRefresh));
        mImagePasingTask.setOnParsingLisnter(new BaseAsyncTask.OnParsingLisnter<List<ImageModel>>() {
            @Override
            public void onParsingSuccess(List<ImageModel> imageModels) {

                if(getActivity() == null){
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
                        mAdapter = new ImageAdapter(getActivity(), imageModels);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mImagePasingTask != null){
            mImagePasingTask.cancel(true);
        }
    }
}
