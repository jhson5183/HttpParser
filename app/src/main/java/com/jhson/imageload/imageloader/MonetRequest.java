package com.jhson.imageload.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.jhson.imageload.imageloader.NewMonet.MonetListener;

import java.util.concurrent.ThreadPoolExecutor;


public class MonetRequest {

	private final String TAG = "MonetRequest";

//	public static boolean sStartMethodTrace = false;
//	public static boolean isStart = false;

	public static final int TRANSPARENT = 0;

	private String mUri;
	private Builder mBuilder;
	private ImageView mImageView;
	private NewMonet mMonetInstance;

	public class Builder {
		public int width = -1;
		public int height = -1;
		public int simpleSize = 1;
		public int blur = 0;
		public float alpha = 1;
		public int placeholderId = 0;
		public boolean clip = false;
		public int blankColor = TRANSPARENT;
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
			if (MonetRequest.this.mBuilder.listener != null) {
				if (bm != null) {
					MonetRequest.this.mBuilder.listener.onLoaded(mImageView, bm);
				} else {
					MonetRequest.this.mBuilder.listener.onFailed();
				}

			} else {
				if (bm != null) {
					MonetRequest.this.mBuilder.defaultListener.onLoaded(mImageView, bm);
				} else {
					MonetRequest.this.mBuilder.defaultListener.onFailed();
				}
			}
		}
	}

	public MonetRequest(String uri, NewMonet monet) {
		this.mUri = uri;
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

		if (mBuilder.placeholderId != 0) {
			imageView.setImageResource(mBuilder.placeholderId);
		} else {
			imageView.setImageDrawable(null);
		}

		mMonetInstance.executeRequest(mUri, imageView.hashCode(), this);
	}

	public void into() {
		mMonetInstance.executeRequest(mUri, -1, this);
	}

	public Builder getBuilder() {
		return mBuilder;
	}

	public MonetRequest listener(MonetListener listener) {
		mBuilder.listener = listener;
		return this;
	}

	public MonetRequest placeholder(int resourceId) {
		mBuilder.placeholderId = resourceId;
		return this;
	}

	public MonetRequest error(int resourceId) {
		mBuilder.errorId = resourceId;
		return this;
	}

	public MonetRequest resize(int width, int height) {
		if (width < 1 || height < 1) {
			throw new IllegalArgumentException("width < 1 || height < 1");
		}
		mBuilder.width = width;
		mBuilder.height = height;
		return this;
	}

	public MonetRequest blur(int value) {
		if (value > 25 || value < 0) {
			throw new IllegalArgumentException("blur value must be 0~25");
		}
		mBuilder.blur = value;
		return this;
	}

	/**
	 * 이미지를 정사각형으로 생성하기 위한 옵션 정의
	 * @param clip	true: 너비와 높이 중 짤은 길이로 정사각형 이미지 생성(잘라냄).	false: 너비와 높이 중 긴 길이로 정사각형 생성(여백생성).
	 * @param blankColor	clip == true 인 경우, 여백을 채울 색. clip == false 인 경우, 의미 없음.
	 * @return
	 */
	public MonetRequest setSquareOption(boolean clip, int blankColor) {
		mBuilder.clip = clip;
		mBuilder.blankColor = blankColor;
		return this;
	}

	/**
	 * contents uri만 적용
	 *
	 * @param simpleSize
	 * @return
	 */
	public MonetRequest simpleSize(int simpleSize) {
		mBuilder.simpleSize = simpleSize;
		return this;
	}
}