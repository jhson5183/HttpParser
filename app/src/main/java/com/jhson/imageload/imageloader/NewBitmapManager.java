package com.jhson.imageload.imageloader;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class NewBitmapManager {

	private static final String TAG = "BitmapManager";
	private LruCache<String, Bitmap> mMemoryCache;
	private final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

	private static NewBitmapManager sNewBitmapManager;

	public synchronized static NewBitmapManager getInstrance() {
		if (sNewBitmapManager == null)
			sNewBitmapManager = new NewBitmapManager();
		return sNewBitmapManager;
	}

	private NewBitmapManager() {
		initMemCache();
	}
	
	/**
	 * 이미지를 정사각형으로 만듦
	 * @param bm
	 * @param clipType CLIP_TYPE_NONE: 너비와 높이 중 긴 길이로 정사각형 생성(여백생성). CLIP_TYPE_CENTER: 중앙 기준으로 잘라냄. CLIP_TYPE_LONGSIDE: 긴면의 뒤쪽(세로면 아래쪽, 가로면 우측)을 잘라냄.
	 * @param blankColor	clip == false 인 경우, 빈공간을 채울 색을 받음.
	 * @return
	 */
	public synchronized static Bitmap getSquareBitmap(Bitmap bm, int clipType, int blankColor) {
		if (bm == null) {
			return null;
		}

		if (clipType == MonetRequest.CLIP_TYPE_NONE && blankColor == MonetRequest.TRANSPARENT) {
			return bm;
		}

		int w = bm.getWidth();
		int h = bm.getHeight();

		int l = 0;
		if (clipType > MonetRequest.CLIP_TYPE_NONE)
			l = (w > h) ? h : w;
		else
			l = (w > h) ? w : h;

		float left = 0;
		float top = 0;

		if (clipType == MonetRequest.CLIP_TYPE_LONGSIDE) {
			if (w > h) {
				left = 0;
				top = (l - h) / 2;
			} else {
				left = (l - w) / 2;
				top = 0;
			}
		} else {
			left = (l - w) / 2;
			top = (l - h) / 2;
		}

		Bitmap squareBitmap = null;

		try {
			squareBitmap = Bitmap.createBitmap(l, l, Config.RGB_565);
		} catch (OutOfMemoryError e) {
			if (squareBitmap != null) {
//				squareBitmap.recycle();
				squareBitmap = null;
			}
			return null;
		}
		Canvas canvas = null;

		if (squareBitmap != null && !squareBitmap.isRecycled()) {
			canvas = new Canvas(squareBitmap);
		} else {
			return null;
		}

		Paint paint = new Paint();
		if (clipType == MonetRequest.CLIP_TYPE_NONE)
			paint.setColor(blankColor);
		paint.setFilterBitmap(false);
		canvas.drawRect(new Rect(0, 0, l, l), paint);

		canvas.drawBitmap(bm, left, top, paint);

		return squareBitmap;
	}

	public synchronized static Bitmap getClipAlignBottomBitmap(Bitmap bm, int imageViewWidth, int imageViewHeight) {

		float ratio = (float)imageViewWidth / bm.getWidth();
		int h = (int)(bm.getHeight() * ratio);

		// 비트맵이 그려질 y값
		int top = 0;
		if (h > imageViewHeight) {
			top = imageViewHeight - h;
			top = (int)(top / ratio);
		}

		Bitmap squareBitmap = null;

		try {
			squareBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight() + top, Config.RGB_565);
		} catch (OutOfMemoryError e) {
			if (squareBitmap != null) {
//				squareBitmap.recycle();
				squareBitmap = null;
			}
			return null;
		}
		Canvas canvas = null;

		if (squareBitmap != null && !squareBitmap.isRecycled()) {
			canvas = new Canvas(squareBitmap);
		} else {
			return null;
		}

		Paint paint = new Paint();
		paint.setFilterBitmap(false);
		canvas.drawRect(new Rect(0, 0, bm.getWidth(), bm.getHeight() + top), paint);

		canvas.drawBitmap(bm, 0, top, paint);

		return squareBitmap;
	}

	public synchronized static Bitmap getAlignBottomBitmap(Bitmap bm, int imageViewWidth, int imageViewHeight) {

		float ratio = (float)imageViewWidth / bm.getWidth();
		int h = (int)(bm.getHeight() * ratio);

		// 비트맵이 그려질 y값
		int top = 0;
		top = imageViewHeight - h;
		top = (int)(top / ratio);

		Bitmap squareBitmap = null;

		try {
			squareBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight() + top, Config.RGB_565);
		} catch (OutOfMemoryError e) {
			if (squareBitmap != null) {
//				squareBitmap.recycle();
			}
			return null;
		}
		Canvas canvas = null;

		if (squareBitmap != null && !squareBitmap.isRecycled()) {
			canvas = new Canvas(squareBitmap);
		} else {
			return null;
		}

		Paint paint = new Paint();
		if (imageViewHeight > h) {
			paint.setColor(Color.BLACK);
		}
		paint.setFilterBitmap(false);
		canvas.drawRect(new Rect(0, 0, bm.getWidth(), bm.getHeight() + top), paint);

		canvas.drawBitmap(bm, 0, top, paint);

		return squareBitmap;
	}

//	public synchronized static Bitmap getAlignBottomBitmap(Context context, Bitmap bm) {
//
//		int w = bm.getWidth();
//		int h = bm.getHeight();
//
//		int l = 0;
//		if (clipType > MonetRequest.CLIP_TYPE_NONE)
//			l = (w > h) ? h : w;
//		else
//			l = (w > h) ? w : h;
//
//		float left = 0;
//		float top = 0;
//
//		if (clipType == MonetRequest.CLIP_TYPE_LONGSIDE) {
//			if (w > h) {
//				left = 0;
//				top = (l - h) / 2;
//			} else {
//				left = (l - w) / 2;
//				top = 0;
//			}
//		} else {
//			left = (l - w) / 2;
//			top = (l - h) / 2;
//		}
//
//		Bitmap squareBitmap = null;
//
//		try {
//			squareBitmap = Bitmap.createBitmap(l, l, Config.ARGB_8888);
//		} catch (OutOfMemoryError e) {
//			if (squareBitmap != null) {
//				squareBitmap.recycle();
//			}
//			return null;
//		}
//		Canvas canvas = null;
//
//		if (squareBitmap != null && !squareBitmap.isRecycled()) {
//			canvas = new Canvas(squareBitmap);
//		} else {
//			return null;
//		}
//
//		Paint paint = new Paint();
//		if (clipType == MonetRequest.CLIP_TYPE_NONE)
//			paint.setColor(blankColor);
//		paint.setFilterBitmap(false);
//		canvas.drawRect(new Rect(0, 0, l, l), paint);
//
//		canvas.drawBitmap(bm, left, top, paint);
//
//		return squareBitmap;
//	}

	private void initMemCache() {
		if (mMemoryCache == null) {
			final int maxMemory = (int) (Runtime.getRuntime().maxMemory());
			int cacheSize = maxMemory / 16;
			if(cacheSize <= 16 * 1024 * 1024){
				cacheSize = 16 * 1024 * 1024;
			}

			mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					return ((bitmap.getRowBytes() * bitmap.getHeight()));
				}
			};
		}
	}

	public void clearMemoryCache() {
		mMemoryCache.evictAll();
	}

	public synchronized Bitmap getBitmapImage(Context context, String contentUrl, int width, int height, int sampleSize, int clipType, int blankColor) {
//		int album_id = Integer.valueOf(Uri.parse(contentUrl).getLastPathSegment());
		
		Bitmap bm = getBitmapFromMemCache(contentUrl + "?pixelSize=" + sampleSize);
		
		if (bm != null) {
			return getSquareBitmap(bm, clipType, blankColor);
		}
		
		return getArtwork(context, Integer.valueOf(Uri.parse(contentUrl).getLastPathSegment()), sampleSize, clipType, blankColor);
	}

	public synchronized Bitmap getBitmapImage(String url, final File file, int width, int height, int clipType, int blankColor) {
		Bitmap bm = getBitmapFromMemCache(TextUtils.isEmpty(url)? file.getName() : url);
		if (bm != null) {
//			Log.d("jsbyeon", "bm.getHeight() = " + bm.getHeight());
//			android.util.Log.e("", "mMemoryCache maxsize : " + mMemoryCache.maxSize());
//			android.util.Log.e("", "mMemoryCache size : " + mMemoryCache.size());
//			android.util.Log.e("", "mMemoryCache putcount : " + mMemoryCache.putCount());
//			android.util.Log.e("", "2bm size : " + bm.getByteCount());
			return getSquareBitmap(bm, clipType, blankColor);
		}
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPreferredConfig = Config.RGB_565;
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
		int outWidth = opts.outWidth;
		opts.inJustDecodeBounds = false;

		if (outWidth <= 0)
			return null;

		if (width < 1) {
			width = outWidth;
		}

		int sampleSize = outWidth / width;
		opts.inSampleSize = sampleSize;

		try {
			bm = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
			if (bm == null) {
				file.delete();
			}
		} catch (OutOfMemoryError e) {
			return null;
		} catch (Exception e) {
			if (bm != null) {
//				bm.recycle();
				bm = null;
			}
			Log.e(TAG, "BitmapFactory.decodeFile " + file.length());
		}
		if (bm != null) {
			addBitmapToMemoryCache(TextUtils.isEmpty(url)? file.getName() : url, bm);
		}

		return getSquareBitmap(bm, clipType, blankColor);
	}
	
	private synchronized Bitmap getArtwork(Context context, int album_id, int sampleSize, int clipType, int blankColor) {

		if (album_id < 0 || context == null) {
			return null;
		}

		ContentResolver res = context.getContentResolver();
		if (res == null)
			return null;

		Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

		if (uri != null) {
			InputStream in = null;
			Bitmap bm = null;
			int pixelSize = sampleSize;
			try {
				in = res.openInputStream(uri);
				BitmapFactory.Options options = new BitmapFactory.Options();
				if (in != null) {
					options.inSampleSize = 1;
					options.inJustDecodeBounds = true;
					options.inPreferredConfig = Config.RGB_565;
					BitmapFactory.decodeStream(in, null, options);

					int maxSize = Math.max(options.outWidth, options.outHeight);
					if (sampleSize > 1) { // " 이미지 가로/세로 최소 크기가 700pixel 을 넘을 경우 "
						sampleSize = maxSize / sampleSize;
					} else {
						sampleSize = 1;
					}

					in.close();
				}

				in = res.openInputStream(uri);
				if (in != null) {
					options.inSampleSize = sampleSize;
					options.inJustDecodeBounds = false;
					options.inPreferredConfig = Config.RGB_565;
					bm = BitmapFactory.decodeStream(in, null, options);
				}

				if (bm != null)
					addBitmapToMemoryCache(uri.toString() + "?pixelSize=" + pixelSize, bm);
				return getSquareBitmap(bm, clipType, blankColor);

			} catch (FileNotFoundException ex) {
				// 앨범 아트가 없을 경우???
				Log.e(TAG, "FileNotFoundException " + ex.toString());
			} catch (OutOfMemoryError e) {
				// 로컬 트랙 앨범 아트 불러오기 실패
				Log.e(TAG, "OutOfMemoryError " + e.toString());
//				if (bm != null)
//					bm.recycle();
			} catch (IOException e) {
				Log.e(TAG, "IOException " + e.toString());
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
					Log.e(TAG, "IOException " + ex.toString());
				}
			}
		}

		return null;
	}
	
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (!TextUtils.isEmpty(key) && bitmap != null) {
			if (getBitmapFromMemCache(key) == null) {
				mMemoryCache.put(key, bitmap);
			}
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		if (!TextUtils.isEmpty(key)) {
			return mMemoryCache.get(key);
		} else {
			return null;
		}
	}
}
