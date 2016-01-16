package com.jhson.imageload.imageloader;

import android.graphics.Bitmap;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import com.jhson.imageload.imageloader.NewMonet.MonetListener;
import com.jhson.imageload.imageloader.NewMonet.DLThread;


public class MonetRequest {

	private final String TAG = "MonetRequest";

//	public static boolean sStartMethodTrace = false;
//	public static boolean isStart = false;

	private static final int DURATION_ALPHA_ANIM = 100;

	public static final int TRANSPARENT = 0;

	public static final int CLIP_TYPE_NONE = 0;
	public static final int CLIP_TYPE_CENTER = 1;
	public static final int CLIP_TYPE_LONGSIDE = 2;

	private final NewMonet monet;
	private final String uri;
	private final Thread mThread;
	private Builder mBuilder;
	private MonetCustom mCustom = null;
	private ImageView mImageView;

	public class Builder {
		public int width = -1;
		public int height = -1;
		public int simpleSize = 1;
		public int blur = 0;
		public float alpha = 1;
		public int placeholderId = 0;
		public int clipType = CLIP_TYPE_NONE;
		public int blankColor = TRANSPARENT;
		public int errorId = 0;
		public boolean fadeInOn = true;
		public MonetListener listener = null;
		public MonetListener defaultListener = null;
	}

	public class ImageRunnable implements Runnable {

		private Bitmap bm;
		private ImageView iv;

		public ImageRunnable(Bitmap bm, ImageView iv) {
			this.bm = bm;
			this.iv = iv;
		}

		@Override
		public void run() {
			if (MonetRequest.this.mBuilder.listener != null) {
				if (bm != null) {
					if (iv != null) {
						if (mBuilder.fadeInOn) {
							AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
							anim.setInterpolator(new AccelerateInterpolator());
							anim.setDuration(DURATION_ALPHA_ANIM);
							iv.startAnimation(anim);
						}
					}
					MonetRequest.this.mBuilder.listener.onLoaded(iv, bm);
				} else {
					if (iv != null) {
						if (mBuilder.placeholderId != 0)
							iv.setImageResource(mBuilder.placeholderId);
					}
					MonetRequest.this.mBuilder.listener.onFailed();
				}

			} else {
				if (bm != null) {
					if (iv != null) {
						if (mBuilder.fadeInOn) {
							AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
							anim.setInterpolator(new AccelerateInterpolator());
							anim.setDuration(DURATION_ALPHA_ANIM);
							iv.startAnimation(anim);
						}
					}
					MonetRequest.this.mBuilder.defaultListener.onLoaded(iv, bm);
				} else {
					MonetRequest.this.mBuilder.defaultListener.onFailed();
					if (iv != null) {
						if (mBuilder.placeholderId != 0) {
							iv.setImageResource(mBuilder.placeholderId);
						}
					}
				}
			}
		}
	}

	// "DEBUG 용"
	public MonetRequest() {
		this.monet = null;
		this.uri = null;
		this.mThread = null;
		this.mBuilder = new Builder();
	}

	public MonetRequest(NewMonet monet, String uri, int resourceId, Thread thread) {
		this.monet = monet;
		this.uri = uri;
		this.mBuilder = new Builder();
		this.mThread = thread;

		mBuilder.defaultListener = new MonetListener() {

			@Override
			public void onLoaded(ImageView iv, Bitmap bm) {
				if (iv != null && bm != null) {
					iv.setImageBitmap(bm);

//					if (mBuilder.fadeInOn) {
//						AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
//						anim.setDuration(1000);
//						iv.startAnimation(anim);
//					}
				}
			}

			@Override
			public void onFailed() {
				if (mBuilder.errorId != 0) {
					mImageView.setImageResource(mBuilder.errorId);
				}
			}
		};
	}

	public Bitmap getBitmap() {
		Bitmap bm = null;
		return bm;
	}

	public Builder getBuilder() {
		return mBuilder;
	}

	public MonetCustom getCustom() {
		return mCustom;
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

	public interface MonetCustom {
		public Bitmap editBitmap(Bitmap bitmap);
	}

	public MonetRequest custom(MonetCustom monetCustom) {
		mCustom = monetCustom;
		return this;
	}

	public void into(ImageView imageView) {
		mImageView = imageView;

		if (mBuilder.fadeInOn && mImageView != null) {
			mImageView.setImageBitmap(null);
//			mBuilder.backgroundDrawable = mImageView.getBackground();
//			mImageView.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		}

		if (mThread == null){
			onFail();
			return;
		}

//		if (mBuilder.placeholderId != 0 && mImageView != null) {
//			imageView.setImageResource(mBuilder.placeholderId);
//		}
//		if (mThread.getClass().getSimpleName().contains("DLThread")) {
			((DLThread) mThread).setImageView(imageView);
			((DLThread) mThread).setRequest(this);
//		} else {
//			((BMThread) mThread).setImageView(imageView);
//			((BMThread) mThread).setRequest(this);
//		}

//		try {
//			if (!isStart && sStartMethodTrace) {
//				Debug.startMethodTracing("" + System.currentTimeMillis());
//				isStart = true;
//			}
//		} catch (Exception e) {
//		}

//		Log.d(TAG, "native heap = " + Debug.getNativeHeapSize() + ", native allocated = " + Debug.getNativeHeapAllocatedSize());

		mThread.start();
	}

	public void into() {
		if (mThread == null){
			onFail();
			return;
		}


//		if (mThread.getClass().getSimpleName().contains("DLThread")) {
			((DLThread) mThread).setRequest(this);
//		} else {
//			((BMThread) mThread).setRequest(this);
//		}
		mThread.start();
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

	public MonetRequest setFadeIn(boolean fadeInOn) {
		mBuilder.fadeInOn = fadeInOn;
		return this;
	}

	/**
	 * 이미지를 정사각형으로 생성하기 위한 옵션 정의
	 * @param clipType CLIP_TYPE_NONE: 너비와 높이 중 긴 길이로 정사각형 생성(여백생성). CLIP_TYPE_CENTER: 중앙 기준으로 잘라냄. CLIP_TYPE_LONGSIDE: 긴면의 뒤쪽(세로면 아래쪽, 가로면 우측)을 잘라냄.
	 * @param blankColor	clip == true 인 경우, 여백을 채울 색. clip == false 인 경우, 의미 없음.
	 * @return
	 */
	public MonetRequest setSquareOption(int clipType, int blankColor) {
		mBuilder.clipType = clipType;
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

	private void onLoaded(ImageView iv, Bitmap bm){
		if(mBuilder != null){
			if(mBuilder.listener != null){
				mBuilder.listener.onLoaded(iv, bm);
			} else if(mBuilder.defaultListener != null){
				mBuilder.defaultListener.onLoaded(iv, bm);
			}
		}
	}

	private void onFail(){
		if(mBuilder != null){
			if(mBuilder.listener != null){
				mBuilder.listener.onFailed();
			} else if(mBuilder.defaultListener != null){
				mBuilder.defaultListener.onFailed();
			}
		}
	}
}
