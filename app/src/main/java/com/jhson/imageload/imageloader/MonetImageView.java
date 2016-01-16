package com.jhson.imageload.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MonetImageView extends ImageView {

	public static final String TAG = "MonetImageView";
	private String mCurrentImageUrl;

	public MonetImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MonetImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MonetImageView(Context context) {
		super(context);
	}

	public String getCurrentImageUrl() {
		return mCurrentImageUrl;
	}

	public void setCurrentImageUrl(String imageUrl) {
		this.mCurrentImageUrl = imageUrl;
	}

	public static Handler imageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};

	private ImageListener mListener;

	final ImageListener defaultListener = new ImageListener() {
		public void onImageLoaded(MonetImageView imageView, Bitmap bitmap) {
			//Log.i(TAG, "Default Listener: ImageLoaded");
			imageView.setImageBitmap(bitmap);
		}

		@Override
		public void onImageLoadFailed() {
			//Log.i(TAG, "Default Listener: ImageLoadFailed");
		}
	};

	public static interface ImageListener {
		public void onImageLoaded(MonetImageView imageView, Bitmap bitmap);

		public void onImageLoadFailed();
	}

	public class ImageRunnable implements Runnable {

		Bitmap bm;

		public ImageRunnable(Bitmap bm) {
			this.bm = bm;
		}

		@Override
		public void run() {
			if (MonetImageView.this.mListener != null) {
				if (bm != null)
					MonetImageView.this.mListener.onImageLoaded(MonetImageView.this, bm);
				else
					MonetImageView.this.mListener.onImageLoadFailed();
			} else {
				if (bm != null)
					MonetImageView.this.defaultListener.onImageLoaded(MonetImageView.this, bm);
				else
					MonetImageView.this.defaultListener.onImageLoadFailed();
			}
		}
	}
	
	public void setImageListener(ImageListener listener) {
		this.mListener = listener;
	}

}
