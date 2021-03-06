package com.jhson.imageload.task;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

/**
 * 반환값을 리스너를 통해 전달하는 클래스
 * Created by INT-jhson5183 on 2016. 1. 14..
 */
public abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	private final String TAG = "BaseAsyncTask";
	protected Context mContext = null;

	public BaseAsyncTask(Context context){
		mContext = context;
	}

	private OnParsingLisnter<Result> mParsingLisnter = null;
	private long mStartTime = 0;

	public static interface OnParsingLisnter<Result>{
		void onParsingSuccess(Result result);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mStartTime = System.currentTimeMillis();
	}

	@Override
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);

		long endTime = System.currentTimeMillis() - mStartTime;
		Log.e(TAG, "time : " + DateFormat.format("ss", endTime) + " ms : " + endTime);
		Toast.makeText(mContext, "time : " + DateFormat.format("ss", endTime) + " ms : " + endTime, Toast.LENGTH_SHORT).show();
		if (mParsingLisnter != null) {
			mParsingLisnter.onParsingSuccess(result);
		}
	}

	@Override
	protected void onProgressUpdate(Progress... values) {
		super.onProgressUpdate(values);
	}

	public void setOnParsingLisnter(OnParsingLisnter<Result> parsingLisnter) {
		this.mParsingLisnter = parsingLisnter;
	}
}
