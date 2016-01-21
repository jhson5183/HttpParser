package com.jhson.imageload.adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jhson.gogh.Gogh;
import com.jhson.imageload.R;
import com.jhson.imageload.model.ImageModel;

import java.util.List;

/**
 * Created by INT-jhson5183 on 2016. 1. 15..
 */
public class ImageAdapter extends RecyclerView.Adapter{

    private List<ImageModel> mList = null;
    private Context mContext = null;

    public ImageAdapter(Context context, List<ImageModel> list){
        mContext = context;
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(viewType, null);
        CoverViewHolder holder = new CoverViewHolder(view);

        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_image;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ImageModel data = getItem(position);
        CoverViewHolder coverViewholder = (CoverViewHolder)holder;

        coverViewholder.mTextView.setText(data.getmTitle());
        Gogh.getInstance(mContext).load(data.getmImageUrl()).into(coverViewholder.mImageView);

    }

    private ImageModel getItem(int position){
        return mList.get(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class CoverViewHolder extends RecyclerView.ViewHolder{

        ImageView mImageView = null;
        TextView mTextView = null;

        CoverViewHolder(View itemView){
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.adapter_cover_img);
            mTextView = (TextView) itemView.findViewById(R.id.adapter_cover_text);
        }
    }

    public void setList(List<ImageModel> list) {
        this.mList = list;
    }
}
