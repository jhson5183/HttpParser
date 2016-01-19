package com.jhson.imageload.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.jhson.imageload.imageloader.Gogh.MonetListener;


public class GoghRequest {

	private final String TAG = "GoghRequest";

	private String mUrl;
	private Builder mBuilder;
	private ImageView mImageView;
	private Gogh mMonetInstance;

	public class Builder {
		public int width = -1;
		public int height = -1;
		public int errorId = 0;
		public MonetListener listener = null;
		public MonetListener defaultListener = null;
	}

	public class ImageRunnable implements Runnable {

		private Bitmap bm;

		public ImageRunnable(Bitmap bm) {
			this.bm = bm;
		}

		@Override
		public void run() {
			if (GoghRequest.this.mBuilder.listener != null) {
				if (bm != null) {
					GoghRequest.this.mBuilder.listener.onLoaded(mImageView, bm);
				} else {
					GoghRequest.this.mBuilder.listener.onFailed();
				}

			} else {
				if (bm != null) {
					GoghRequest.this.mBuilder.defaultListener.onLoaded(mImageView, bm);
				} else {
					GoghRequest.this.mBuilder.defaultListener.onFailed();
				}
			}
		}
	}

	public GoghRequest(String url, Gogh monet) {
		this.mUrl = url;
		this.mMonetInstance = monet;
		this.mBuilder = new Builder();

		mBuilder.defaultListener = new MonetListener() {

			@Override
			public void onLoaded(ImageView iv, Bitmap bm) {
				if (null != mImageView && null != bm)
					mImageView.setImageBitmap(bm);
			}

			@Override
			public void onFailed() {
				if (null != mImageView && mBuilder.errorId != 0) {
					mImageView.setImageResource(mBuilder.errorId);
				}
			}
		};
	}

	public void into(ImageView imageView) {
		mImageView = imageView;
		mImageView.setImageBitmap(null);

		mMonetInstance.executeRequest(mUrl, imageView.hashCode(), this);
	}

	public void into() {
		mMonetInstance.executeRequest(mUrl, -1, this);
	}

	public Builder getBuilder() {
		return mBuilder;
	}

	public GoghRequest listener(MonetListener listener) {
		mBuilder.listener = listener;
		return this;
	}

	public GoghRequest error(int resourceId) {
		mBuilder.errorId = resourceId;
		return this;
	}

	public GoghRequest resize(int width, int height) {
		if (width < 1 || height < 1) {
			throw new IllegalArgumentException("width < 1 || height < 1");
		}
		mBuilder.width = width;
		mBuilder.height = height;
		return this;
	}

}