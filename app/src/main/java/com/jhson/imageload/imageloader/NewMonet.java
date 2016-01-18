package com.jhson.imageload.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.jhson.imageload.imageloader.process.Blur;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class NewMonet {

	private static final String TAG = "NewMonet";
	private static Handler sImageHandler;
	
	/* SingleTon Pattern */
	private NewMonet(Context context) {
		this.mContext = context;
		sImageHandler = new Handler(Looper.getMainLooper());
	}

	private static NewMonet singleton = null;

	public synchronized static NewMonet with(Context context) {
		if (singleton == null) {
			singleton = new NewMonet(context);
			singleton.mDownloadSemaphore = new Semaphore(MonetOptions.Download_Max);
			singleton.mBitmapSemaphore = new Semaphore(MonetOptions.Bitmap_Max);
		}
		return singleton;
	}

	/* Variables */
	private Context mContext;
	public Semaphore mDownloadSemaphore = null;
	public Semaphore mBitmapSemaphore = null;

	private HashMap<String, Thread> mDownloadingUrl = new HashMap<String, Thread>();
	private ConcurrentHashMap<String, LinkedList<Thread>> mWaiting = new ConcurrentHashMap<String, LinkedList<Thread>>();
	private ConcurrentLinkedQueue<Thread> mThreadQueue = new ConcurrentLinkedQueue<Thread>();
	private HashMap<Integer, Long> mImageJobs = new HashMap<Integer, Long>();

	// Load From Uri
	public synchronized MonetRequest load(String uri) {

		if (TextUtils.isEmpty(uri)) {
			return new MonetRequest(this, null, 0, null);
		}

		Thread thread = null;

		// 네트워크 상태 체크
		thread = new DLThread(uri, mDownloadSemaphore);

		return new MonetRequest(this, uri, 0, thread);
	}

	// Load From url path
	public MonetRequest load(Uri uri) {
		if (uri == null) {
			return new MonetRequest(this, null, 0, null);
		}

		return load(uri.toString());
	}

	// Load From File
	public MonetRequest load(File file) {
		if (file == null) {
			return new MonetRequest(this, null, 0, null);
		}
		return load(Uri.fromFile(file));
	}

	// Load From drawable ResourceId
	public MonetRequest load(int resourceId) {
		if (resourceId == 0) {
			throw new IllegalArgumentException("Resource Id Must not be zero. :(");
		}
		return new MonetRequest(this, null, resourceId, null);
	}

	public static interface MonetListener {
		public void onLoaded(ImageView iv, Bitmap bm);

		public void onFailed();
	}

	private synchronized boolean isThereThreadDownloadingSameUrl(String inUrl) {
		for (int i = 0; i < mDownloadingUrl.size(); i++) {
			if (mDownloadingUrl.get(inUrl) != null) {
				return true;
			}
		}

		return false;
	}

	private synchronized void unLock(String imageUrl) {

		mDownloadingUrl.remove(imageUrl);
		if (mWaiting.size() > 0) {
			LinkedList<Thread> waitingThisThread = mWaiting.get(imageUrl);
			if (waitingThisThread != null) {

				Thread notifyThis;
				while ((notifyThis = waitingThisThread.poll()) != null) {
					synchronized (notifyThis) {
						notifyThis.notify();
					}
				}
				mWaiting.remove(imageUrl);
			}
		}
	}
	

	class DLThread extends Thread {
		private Semaphore semaphore;
		private String url;
		private ImageView mImageView;
		private MonetRequest mRequest;
		private MonetDownloader mDownloader;

		private long mCreateTime;

		public DLThread(final String url, Semaphore semaphore) {
			this.setPriority(Thread.MIN_PRIORITY);
			
			this.url = url;
			this.semaphore = semaphore;
		}

		public void setImageView(final ImageView iv) {
			mImageView = iv;
		}

		public void setRequest(final MonetRequest request) {
			mRequest = request;
		}

		@Override
		public void run() {
			super.run();
			try {
				mCreateTime = System.currentTimeMillis();

				BMThread bmThread = null;
				File file = MonetDownloader.getCachedImageFile(url, mContext);
				if (file != null && file.exists() && file.length() > 1)
					bmThread = new BMThread(url, file, mBitmapSemaphore);
				else if (url.startsWith("content://"))
					bmThread = new BMThread(url, mBitmapSemaphore);

				if (bmThread != null) {
					if (null != mImageView)
						registRequest(mImageView.hashCode(), mCreateTime);
					bmThread.setImageView(mImageView);
					bmThread.setRequest(mRequest);
					bmThread.setmDLThreadId(mCreateTime);
					bmThread.start();
					return;
				}

				if (!IOUtils.checkNetwork(mContext)) {
					sImageHandler.post(mRequest.new ImageRunnable(null, mImageView));
					return;
				}

				if (isThereThreadDownloadingSameUrl(url)) { // "다운로드 중인 동일한 URL 이 있다면."
					try {
						LinkedList<Thread> waitingSameUrl = mWaiting.get(url);
						if (waitingSameUrl != null) {
							waitingSameUrl.add(this);
//							Log.i(TAG, "동일한 URL 스레드를 보관한한다. "+waitingSameUrl.size());
						} else {
							LinkedList<Thread> waitingUrl = new LinkedList<Thread>();
							waitingUrl.add(this);
							mWaiting.put(url, waitingUrl);
//							Log.i(TAG, "신규 스레드를 보관한한다. "+waitingUrl.size());
						}
						synchronized (this) {	// wait() 를 함부로 얻으면 안되는 거였어..
							wait();				// 실제로 upLock에서 notify()를 해도 newMonet 싱글턴 전체에서 딱 한개만 풀림. 나머지는 그제서야 wait()를 획득함
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
//				Log.d(TAG, "다운로드를 시도한다.");
				mDownloadingUrl.put(url, this); // "다운로드 중인 URL 을 등록."

				// TODO 작업 중간에 이미 다른놈이 해당 해쉬키로 작업 중이라면 그놈을 취소 시켜버려야함..
				// 이때 취소되는 작업은 MonetDownloader의 stop()을 호출해서 작업을 정지시켜야
				if (null != mImageView) {
					registRequest(mImageView.hashCode(), mCreateTime);
				}
				
				semaphore.acquire();
				
				if (null != mImageView) {
					if (!unRegistRequest(mImageView.hashCode(), mCreateTime, false)) {
						//  해당 이미지뷰에 해당 스레드가 더이상 유효하지 않다면, 다른 스레드가 이미지뷰를 점유했으므로 자원을 뱉고 스레드 종
						Log.d(TAG, "request is invalidate");
						unLock(url);
						semaphore.release();
						mThreadQueue.remove(this);
						return;
					}
				}
				
//				while (!isInterrupted()) {
					File cachedFile = MonetDownloader.getCachedImageFile(url, mContext);
					boolean isDownloadFinished = false;
					if (cachedFile.exists() && cachedFile.length() > 1) {
						isDownloadFinished = true;
					} else {
//						MonetDownloader downloader = new MonetDownloader(mContext);
						mDownloader = new MonetDownloader(mContext);
						isDownloadFinished = mDownloader.get(mContext, url, cachedFile); // "error 이면."
					}
					
					unLock(url); // "다운로드 실패가 발생해도 모두 잡혀있는 쓰레드 UNLOCK 해야한다."

					// 다운로드 실패 시에도 이미지에 대한 후처리를 하고 종료해야함 
					// 다운로드가 외부 간섭 등으로 인해서 실패 했다면 자원 반환. 단 요청에 대해서 등록해제 할 필요는 없음, 이미 해지되었으므
					// -> 요청과는 상관없이 해지될 경우 있음. 네트워크 되지 않는 경우
//					if (!isDownloadFinished) {
//						semaphore.release();
//						mThreadQueue.remove(this);
//						Log.d("test05", "download is canceled. job is cancel.");
//						return;
//					}
					
					Bitmap raw = null;
					int width = mRequest.getBuilder().width;
					int height = mRequest.getBuilder().height;
					int blur = mRequest.getBuilder().blur;
					
					raw = NewBitmapManager.getInstrance().getBitmapImage(url, cachedFile, width, height, mRequest.getBuilder().clipType, mRequest.getBuilder().blankColor);
					
					if (blur > 0) {
						raw = Blur.fastblur(mContext, raw, blur);
					}

					if (mRequest.getCustom() != null) {
						mRequest.getCustom().editBitmap(raw);
					}
					
					if (null != mImageView) {
						if (!unRegistRequest(mImageView.hashCode(), mCreateTime, true)) {
							//  해당 이미지뷰에 해당 스레드가 더이상 유효하지 않다면, 다른 스레드가 이미지뷰를 점유했으므로 자원을 뱉고 스레드 종
							Log.d(TAG, "request is invalidate");
							semaphore.release();
							mThreadQueue.remove(this);
							return;
						}
					}
					sImageHandler.post(mRequest.new ImageRunnable(raw, mImageView));
					semaphore.release();
					mThreadQueue.remove(this);
					
//					BMThread bmThread = new BMThread(url, cachedFile, mBitmapSemaphore);
//					bmThread.setImageView(mImageView);
//					bmThread.setRequest(mRequest);
//					bmThread.start();
//					semaphore.release();
//					mThreadQueue.remove(this);
//					break;
//				}
			} catch (Exception e) {
				Log.e(TAG, "DLThread error " + e.getMessage());

				semaphore.release();
				mThreadQueue.remove(this);

				sImageHandler.post(mRequest.new ImageRunnable(null, mImageView));
			}
		}
	}

	class BMThread extends Thread {
		private Semaphore semaphore;
		private File file;
		private String url;
		private String path;
		private ImageView mImageView;
		private MonetRequest mRequest;

		private long mDLThreadId;

		public BMThread(String url, final File file, Semaphore semaphore) {
			this.setPriority(Thread.MIN_PRIORITY);
			
			this.file = file;
			this.url = url;
			this.semaphore = semaphore;
		}

		public BMThread(final String path, Semaphore semaphore) {
			this.setPriority(Thread.MIN_PRIORITY);
			
			this.path = path;
			this.semaphore = semaphore;
		}

		public void setImageView(final ImageView iv) {
			mImageView = iv;
		}

		public void setRequest(final MonetRequest request) {
			mRequest = request;
		}

		public void setmDLThreadId(long threadId) {
			mDLThreadId = threadId;
		}

		@Override
		public void run() {
			super.run();
			try {
				semaphore.acquire();
				
				if (null != mImageView) {
					if (!unRegistRequest(mImageView.hashCode(), mDLThreadId, false)) {
						//  해당 이미지뷰에 해당 스레드가 더이상 유효하지 않다면, 다른 스레드가 이미지뷰를 점유했으므로 자원을 뱉고 스레드 종
						Log.d(TAG, "request is invalidate");
						semaphore.release();
						mThreadQueue.remove(this);
						return;
					}
				}
				
				while (!isInterrupted()) {
					Bitmap raw = null;
					int width = mRequest.getBuilder().width;
					int height = mRequest.getBuilder().height;
					int blur = mRequest.getBuilder().blur;
					
					int simpleSize = mRequest.getBuilder().simpleSize;
					
					if (TextUtils.isEmpty(path)) {
						raw = NewBitmapManager.getInstrance().getBitmapImage(url, file, width, height, mRequest.getBuilder().clipType, mRequest.getBuilder().blankColor);
					} else {
						raw = NewBitmapManager.getInstrance().getBitmapImage(mContext, path, width, height, simpleSize, mRequest.getBuilder().clipType, mRequest.getBuilder().blankColor);
					}
					if (blur > 0) {
						raw = Blur.fastblur(mContext, raw, blur);
					}

					if (mRequest.getCustom() != null) {
						mRequest.getCustom().editBitmap(raw);
					}
					
					if (null != mImageView) {
						if (!unRegistRequest(mImageView.hashCode(), mDLThreadId, true)) {
							//  해당 이미지뷰에 해당 스레드가 더이상 유효하지 않다면, 다른 스레드가 이미지뷰를 점유했으므로 자원을 뱉고 스레드 종
							Log.d(TAG, "request is invalidate");
							semaphore.release();
							mThreadQueue.remove(this);
							return;
						}
					}
					sImageHandler.post(mRequest.new ImageRunnable(raw, mImageView));
					semaphore.release();
					mThreadQueue.remove(this);
					break;
				}
			} catch (Exception e) {
				Log.e(TAG, "BMThread error " + e.getMessage());
				semaphore.release();
				mThreadQueue.remove(this);
			} catch (OutOfMemoryError e) {
				Log.e(TAG, "BMThread error " + e.getMessage());
				semaphore.release();
				mThreadQueue.remove(this);
			}
		}
	}
	
	/** 이미지 요청에 대해 등록 */
	private synchronized void registRequest(int hashCode, long threadId) {
		
//		try {
//			if (!MonetRequest.sStartMethodTrace) {
//				Debug.stopMethodTracing();
//				MonetRequest.isStart = false;
//			}
//		} catch(Exception e) {
//		}
		
		if (mImageJobs.containsKey(hashCode)) {

//			// 해당 뷰에 대해서 이미지 요청을 한 스레드들이 작업을 취소하도록 요청
//			Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
//			Thread[] threads = threadSet.toArray(new Thread[threadSet.size()]);
//			Log.d(TAG, "thread id = " + mImageJobs.get(hashCode));
//
//			// 동일한 해쉬키를 가지고 있는 스레드만 골라
//			ArrayList<Thread> arrThread = new ArrayList<Thread>();
//
//			// 요청들의 리스트에서 해쉬코드(키)가 동일한 엔트리들만 뽑아서 별도의 셋을 구성
//			for (Entry<Integer, Long> entry: mImageJobs.entrySet()) {
//				if (entry.getKey() == hashCode) {
//
//					// 같은 이미지뷰 해쉬코드를 사용하는 스레드 아이디를 가지고 스레드 셋을 구성
//					for (Thread thread: threads) {
//						if (entry.getValue() == thread.getId()) {
//							arrThread.add(thread);
//							break;
//						}
//					}
//				}
//			}
//
//			Log.d(TAG, hashCode + " has threads what is num = " + arrThread.size());
//
//			// TODO 스레드 가운데에 요청으로 등록되어 있으면서 해쉬키가 같은 놈을 반복해서 찾
//			for (Thread thread: arrThread) {
//				if (thread instanceof DLThread && null != ((DLThread) thread).mDownloader) {
//					((DLThread) thread).mDownloader.stop();
//				}
//			}

			mImageJobs.remove(hashCode);

			// 해쉬맵에서 요청을 하나씩 빼와서 그놈을 취소
			// 아이디를 이용해서 스레드를 가져오
		}

		mImageJobs.put(hashCode, threadId);
		Log.d(TAG, "mImageJobs size = " + mImageJobs.size());
	}
	
	/** 등록된 이미지 요청 제거 */
	private synchronized boolean unRegistRequest(int hashCode, long threadId, boolean cancelJob) {
		//  동일한 이미지뷰에 대한 요청이 있었고 해당 스레드가 아직 유효한 작업이라면, 해당 이미지뷰에 대한 이미지뷰 요청내역 제거 (스레드 작업이 모두 끝났으므로)
		if (null != mImageJobs.get(hashCode)) {
			Log.d(TAG, "jobs is valid");
			if (mImageJobs.get(hashCode) == threadId) {
				if (cancelJob)
					mImageJobs.remove(hashCode);
				return true;
			}
		}
		return false;
	}

}
