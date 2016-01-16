package com.jhson.imageload.imageloader;

public class MonetOptions {

	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	public static final int Download_Max = CPU_COUNT * 2 + 1;
	public static final int Bitmap_Max = CPU_COUNT * 2 + 1;
	
}
