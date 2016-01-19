package com.jhson.imageload.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.jhson.imageload.imageloader.process.Blur;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class NewMonet {

	private static final String TAG = "NewMonet";

	private Context mContext;

	protected ConcurrentHashMap<String, LinkedList<Runnable>> mWaitingJobs = new ConcurrentHashMap<String, LinkedList<Runnable>>();
	protected ConcurrentHashMap<Long, List<Runnable>> mRegisteredJobs = new ConcurrentHashMap<Long, List<Runnable>>();

	protected static ThreadPoolExecutor mCacheExecutor;
	protected static ThreadPoolExecutor mNetworkExecutor;
	protected static Handler sImageHandler;

	private static NewMonet instance = null;
	private static Bitmap mBitmap = null;

	private NewMonet(Context context) {
		this.mContext = context;
		sImageHandler = new Handler(Looper.getMainLooper());
	}

	public static NewMonet with(Context context) {
		if (instance == null) {
			instance = new NewMonet(context);
			mCacheExecutor = new ThreadPoolExecutor(MonetOptions.CACHE_POOL_SIZE, MonetOptions.CACHE_POOL_SIZE, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new MonetThreadFactory());
			mNetworkExecutor = new ThreadPoolExecutor(MonetOptions.NETWORK_POOL_SIZE, MonetOptions.NETWORK_POOL_SIZE, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new MonetThreadFactory());
		}
		return instance;
	}

	static class MonetThreadFactory implements ThreadFactory {
		public Thread newThread(Runnable r) {
			return new MonetThread(r);
		}
	}

	private static class MonetThread extends Thread {
		public MonetThread(Runnable r) {
			super(r);
		}
		@Override
		public void run() {
			Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND);
			super.run();
		}
	}

	/** Load From String */
	public MonetRequest load(String uri) {
		if (TextUtils.isEmpty(uri))
			return new MonetRequest("", instance);
		return new MonetRequest(uri, instance);
	}

	/** Load From Uri */
	public MonetRequest load(Uri uri) {
		if (null == uri)
			return new MonetRequest("", instance);
		return new MonetRequest(uri.toString(), instance);
	}

	/** Load From File */
	public MonetRequest load(File file) {
		if (file == null)
			return new MonetRequest("", instance);
		return load(Uri.fromFile(file).toString());
	}

	/** 요청 처리 */
	protected void executeRequest(String uri, long viewHashCode, MonetRequest request) {

		mBitmap = getImageInMemory(uri);
		Runnable cacheRunnable;
		if (null != mBitmap) {
			Log.d(TAG, "request is comp by mem cache");
			cacheRunnable = new Runnable() {@Override public void run() {}};
			registRequest(viewHashCode, cacheRunnable);
			unRegistRequest(viewHashCode, cacheRunnable, true);
			sImageHandler.post(request.new ImageRunnable(mBitmap));
		} else {
			cacheRunnable = new CacheTask(mContext, uri, viewHashCode, request);
			registRequest(viewHashCode, cacheRunnable);
			mCacheExecutor.submit(cacheRunnable);
		}
	}

	/** 네트워크 요청 */
	protected void executeNetworkRequest(String uri, long viewHashCode, MonetRequest request, Runnable requestedRunnable) {
		Runnable networkRunnable = new NetworkTask(mContext, uri, viewHashCode, request);
		changeRequest(viewHashCode, requestedRunnable, networkRunnable);
		mNetworkExecutor.submit(networkRunnable);
	}

	private Bitmap getImageInMemory(String uri) {
		return NewBitmapManager.getInstrance().getBitmapFromMemCache(uri);
	}

	public static interface MonetListener {
		public void onLoaded(ImageView iv, Bitmap bm);
		public void onFailed();
	}

	/** 동일 URL에 대한 wait 상태의 thread notify */
	protected void unLock(String imageUrl) {

		if (mWaitingJobs.size() > 0) {
			LinkedList<Runnable> waitingThisThread = mWaitingJobs.get(imageUrl);
			if (waitingThisThread != null) {
				Runnable notifyThis;
				while ((notifyThis = waitingThisThread.poll()) != null) {
					notifyThis.notify();
				}
				mWaitingJobs.remove(imageUrl);
			}
		}
	}

	/** 이미지 요청에 대해 등록 */
	protected void registRequest(long hashCode, Runnable task) {
//		try {
//			if (!sStartMethodTrace) {
//				Debug.stopMethodTracing();
//				isStart = false;
//			}
//		} catch (Exception e) {
//
//		}
		if (hashCode <= 0) return;

		if (mRegisteredJobs.containsKey(hashCode)) {
			List<Runnable> runnableList = mRegisteredJobs.get(hashCode);

			if (null != runnableList) {
				for (Runnable runnable: runnableList) {
					if (runnable instanceof NetworkTask)
						((NetworkTask)runnable).imgDownloadStop();
				}
				// 동일 이미지뷰에 등록된 요청들을 모두 제거
				mRegisteredJobs.remove(hashCode);
			}
		}
		mRegisteredJobs.put(hashCode, new LinkedList<Runnable>(Arrays.asList(task)));
	}

	/** 등록된 이미지 요청 제거 */
	protected boolean unRegistRequest(long hashCode, Runnable task, boolean cancelJob) {
		if (isValidRequest(hashCode, task)) {
			if (cancelJob)
				mRegisteredJobs.remove(hashCode);
			return true;
		}
		return false;
	}

	/** 해당 요청이 유효한지 확인 */
	protected boolean isValidRequest(long hashCode, Runnable task) {
		// 이미지뷰가 없다면 요청 처리
		if (hashCode <= 0) return true;

		if (mRegisteredJobs.containsKey(hashCode)) {
			List<Runnable> runnableList = mRegisteredJobs.get(hashCode);
			for (Runnable runnable: runnableList) {
				if (runnable == task) {
					return true;
				}
			}
		}
		return false;
	}

	/** 등록된 요청을 변경 */
	protected boolean changeRequest(long hashCode, Runnable requestedTask, Runnable newTask) {
		if (mRegisteredJobs.containsKey(hashCode)) {
			List<Runnable> runnableList = mRegisteredJobs.get(hashCode);
			for (Runnable runnable: runnableList) {
				if (runnable == requestedTask) {
					runnableList.set(runnableList.indexOf(runnable), newTask);
					return true;
				}
			}
		}
		return false;
	}
}

