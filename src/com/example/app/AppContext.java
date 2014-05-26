package com.example.app;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * 
 * @author 巨乐
 * @version 1.0
 * @created 2014-5-21
 */
public class AppContext extends Application {
	public static final int NETTYPE_WIFI = 1;
	public static final int NETTYPE_MOBILE = 2;
	private String saveImagePath;// 保存图片路径
	private File imageFile; // 保存的图片文件目录
	private String saveVideoPath;  //保存视频路径
	private File videoFile;  //保存视频文件目录

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		// 设置保存图片的路径以及图片文件目录
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File imageStorageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		try {
			imageFile = File.createTempFile(imageFileName, ".jpg", imageStorageDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("creatimagefile","false");
			e.printStackTrace();
		}
		saveImagePath = imageFile.getAbsolutePath();
		
		String videoFileName = "mp4_" + timeStamp +"_";
		File videoStorageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		try {
			videoFile = File.createTempFile(videoFileName, ".mp4", videoStorageDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("creatvideofile","false");
			e.printStackTrace();
		}
		saveVideoPath = videoFile.getAbsolutePath();

	}
	

	/**
	 * 获取当前网络类型,是网络连接情况下的网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：移动网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			// fetch data
			int nType = networkInfo.getType();
			if (nType == ConnectivityManager.TYPE_MOBILE)
				return NETTYPE_MOBILE;
			else if (nType == ConnectivityManager.TYPE_WIFI)
				return NETTYPE_WIFI;
		} else {
			// display error
			return 0;
		}
		return netType;
	}

}
