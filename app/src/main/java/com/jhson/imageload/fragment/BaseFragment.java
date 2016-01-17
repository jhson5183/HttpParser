package com.jhson.imageload.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ProgressBar;

import com.jhson.imageload.R;
import com.jhson.imageload.adpater.ImageAdapter;
import com.jhson.imageload.view.ImageLoadRecyclerView;

/**
 * Created by jhson on 2016-01-17.
 */
public class BaseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    protected String HTTP_URL = "http://www.gettyimagesgallery.com/collections/archive/slim-aarons.aspx";

    protected SwipeRefreshLayout mSwipeRefreshLayout = null;
    protected ImageLoadRecyclerView mRecyclerView = null;
    protected ImageAdapter mAdapter = null;

    private ProgressBar mProgressBar = null;
    private ViewStub mEmptyView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_grid, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mRecyclerView = (ImageLoadRecyclerView) view.findViewById(R.id.recycler_view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
        mEmptyView = (ViewStub) view.findViewById(R.id.vs_empty);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setOnScrollListener(mOnScrollListener);

        loadData(false);

        return view;
    }

    /*
    데이터를 로드하는 메서드
    boolean isCacheRefresh 을 통해서 캐시된 데이터를 리플래시 할 것인지 정한다.
     */
    protected void loadData(boolean isCacheRefresh){

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

    protected void showEmptyView(){
        mEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    protected void hideEmptyView(){
        mEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    protected void showProgressView(){
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void hideProgressView(){
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }
}