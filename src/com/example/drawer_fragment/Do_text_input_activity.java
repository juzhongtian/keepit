package com.example.drawer_fragment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ShareActionProvider;
import android.widget.Toast;
import android.widget.VideoView;
import android.provider.MediaStore;
import cn.yunzhisheng.basic.USCRecognizerDialog; //没有放到 lib 中去！！！
import cn.yunzhisheng.basic.USCRecognizerDialogListener;
import cn.yunzhisheng.common.USCError;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.BDNotifyListener;//假如用到位置提醒功能，需要import该类
//如果使用地理围栏功能，需要import如下类
import com.baidu.location.BDGeofence;
import com.baidu.location.BDLocationStatusCodes;
import com.baidu.location.GeofenceClient;
import com.baidu.location.GeofenceClient.OnAddBDGeofencesResultListener;
import com.baidu.location.GeofenceClient.OnGeofenceTriggerListener;
import com.baidu.location.GeofenceClient.OnRemoveBDGeofencesResultListener;
import com.baidu.location.LocationClientOption.LocationMode;

public class Do_text_input_activity extends Activity {
	private Intent share_intent;
	private EditText input;
	private ImageView image;
	private VideoView video;
	private USCRecognizerDialog mRecognizer;
	private String file_path;
	private String mode;
	private String button_mode;
	static final String MODE = "MODE";
	static final String BUTTON_MODE = "button_mode";
	static final int REQUEST_GET_PHOTO = 3;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_VIDEO_CAPTURE = 2;
	private String mCurrentPhotoPath = null;
	private String mCurrentVideoPath = null;
	private ShareActionProvider mShareActionProvider;
	BMapManager mBMapMan = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init(null);
		// 注意：请在试用setContentView前初始化BMapManager对象，否则会报错
		setContentView(R.layout.activity_do_text_input);

		input = (EditText) findViewById(R.id.inputarea);
		final PlaceholderFragment temp = new PlaceholderFragment();
		getFragmentManager().beginTransaction()
				.replace(R.id.notice_frame, temp).commit();
		video = (VideoView) findViewById(R.id.videoView1);
		dispathbyButton();

	}

	@Override
	protected void onDestroy() {

		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {

		if (mBMapMan != null) {
			mBMapMan.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {

		if (mBMapMan != null) {
			mBMapMan.start();
		}
		super.onResume();
	}

	public void dispathbyButton() {
		button_mode = getIntent().getExtras().getString(BUTTON_MODE);
		switch (button_mode) {
		case "text_button":
			break;
		case "voice_button":
			Toast.makeText(getApplicationContext(), "voice_mode",
					Toast.LENGTH_SHORT).show();
			dealvoice();
			break;
		case "cap_button":
			dispath();
			break;
		default:
			Log.d("Button type:", "error");
		}
	}

	public void dealvoice() {

		// 创建识别对话框，appKey通过 http://dev.hivoice.cn/ 网站申请
		mRecognizer = new USCRecognizerDialog(this,
				"fj4ucslc7o7gunx2hycm5zhwkhllqp6nzkwkinqt");

		// 识别结果回调监听器.
		mRecognizer.setListener(new USCRecognizerDialogListener() {

			// 识别结果回调接口
			public void onResult(String result, boolean isLast) {
				// 通常onResult接口多次返回结果，保留识别结果组成完整的识别内容。
				input.append(result);
			}

			// 识别结束回调接口.
			public void onEnd(USCError error) {
				// error为null表示识别成功，识别框自动关闭，可在此处理text结果，
				// error不为null，表示发生错误，对话框停留在错误页面
				if (error == null) {
				}
			}
		});
		mRecognizer.show();
	}

	// 用于判断启动此活动的类型
	public void dispath() {

		mode = getIntent().getExtras().getString(MODE);
		if (mode.equals("cap")) // java 没有运算符重载
		{
			Log.d("mode", "cap");
			setCap();
		} else if (mode.equals("video")) {
			Log.d("mode", "video");
			setVideo();
		} else if (mode.equals("pic")) {
			Log.d("mode", "pic");
			setPic();
		}
	}

	public void setPic() {
		Uri uri = Uri.parse(getIntent().getExtras().getString("uripic"));
		if (uri == null) {
			Log.d("getpic", "null");
		}
		image = (ImageView) findViewById(R.id.image_place);
		image.setAdjustViewBounds(true);
		image.setMaxWidth(300);
		image.setMaxHeight(300);
		image.setScaleType(ImageView.ScaleType.CENTER_CROP);
		image.setImageURI(uri);
	}

	public void setCap() {
		file_path = getIntent().getExtras().getString("filepath_name");
		// Toast.makeText(getActivity().getApplicationContext(), file_path,
		// Toast.LENGTH_LONG).show();
		if (file_path == null)
			Log.d("file_path", "file_path is null!");
		image = (ImageView) findViewById(R.id.image_place);

		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file_path, opt);
		int pthotoW = opt.outWidth;
		int pthotoH = opt.outHeight;
		int sampleSize = Math.min(pthotoW / 300, pthotoH / 300);
		opt.inJustDecodeBounds = false;
		opt.inSampleSize = sampleSize;
		opt.inPurgeable = true;
		Bitmap bitmap = BitmapFactory.decodeFile(file_path, opt);
		if (bitmap == null)
			Toast.makeText(this, "bitmap is null", Toast.LENGTH_LONG).show();
		image.setAdjustViewBounds(true);
		image.setMaxWidth(300);
		image.setMaxHeight(300);
		image.setScaleType(ImageView.ScaleType.CENTER_CROP);
		image.setImageBitmap(bitmap);
		// video.setVisibility(0);
	}

	public void setVideo() {
		file_path = getIntent().getExtras().getString("filepath_name");
		if (file_path == null)
			Log.d("file_path", "wrong");
		MediaController mc = new MediaController(this);
		video.setMediaController(mc);
		video.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, 300));
		video.setVideoPath(file_path); // 这里不能播放！！？？？？
		// video.requestFocus();
		// video.start(); //这里可设置为自动播放
	}

	public String getInputContent() {
		CharSequence x = input.getText();
		if (x == null)
			Log.d("input", "is null");
		return x.toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.do_text_input, menu);
		MenuItem item = menu.findItem(R.id.menu_item_share);
		MenuItem item_tran = menu.findItem(R.id.menu_item_translate);
		mShareActionProvider = (ShareActionProvider) item.getActionProvider();
		share_intent = new Intent(Intent.ACTION_SEND);
		share_intent.putExtra(Intent.EXTRA_TEXT, getInputContent()); // 如何改为获取输入区内容。
		share_intent.setType("text/plain");
		if (mShareActionProvider != null)
			mShareActionProvider.setShareIntent(share_intent);
		else
			Toast.makeText(this, "shareActionprovider is null",
					Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.menu_item_share:
			break;
		case R.id.menu_item_translate:
			doTranslate();
			break;
		case R.id.popupmenu_pic:
			pic();
			break;
		case R.id.popupmenu_cap:
			cap();
			break;
		case R.id.poppmenu_video:
			video();
			break;
		case R.id.menu_item_voice:
			dealvoice();
			break;
		}

		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);

	}

	public void cap() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File photoFile = null;
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			try {
				photoFile = creatImageFile();
			} catch (IOException e) {
				Toast.makeText(this, "wirte wrong!", Toast.LENGTH_SHORT);
			}
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
			}

		}
	}

	public void pic() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQUEST_GET_PHOTO);
	}

	private File creatImageFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, ".jpg", storageDir);
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}

	public void video() {
		File videoFile = null;
		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
			try {
				videoFile = creatVideoFile();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(),
						"creat videoFile wrong", Toast.LENGTH_SHORT).show();
			}
			if (videoFile != null) {
				takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(videoFile));
				startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
			}
		}
	}

	private File creatVideoFile() throws IOException {
		String videoFileName = "video";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File videoFile = File.createTempFile(videoFileName, ".mp4", storageDir);
		mCurrentVideoPath = videoFile.getAbsolutePath();
		Log.d("mCurrentVideoPath", mCurrentVideoPath);
		return videoFile;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			image = (ImageView) findViewById(R.id.image_place);
			int imageWidth = image.getWidth();
			int imageHeight = image.getHeight();
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(mCurrentPhotoPath, opt);
			int pthotoW = opt.outWidth;
			int pthotoH = opt.outHeight;
			int sampleSize = Math.min(pthotoW / 300, pthotoH / 300);
			opt.inJustDecodeBounds = false;
			opt.inSampleSize = sampleSize;
			opt.inPurgeable = true;
			Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, opt);
			if (bitmap == null)
				Toast.makeText(this, "bitmap is null", Toast.LENGTH_LONG)
						.show();
			image.setAdjustViewBounds(true);
			image.setMaxWidth(300);
			image.setMaxHeight(300);
			image.setScaleType(ImageView.ScaleType.CENTER_CROP);
			image.setImageBitmap(bitmap);
			galleryAddPic();
		}
		if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
			// Log.d("video", "success!");
			MediaController mc = new MediaController(this);
			video.setMediaController(mc);
			video.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, 300));
			video.setVideoPath(mCurrentVideoPath); // 这里不能播放！！？？？？
		}

		if (requestCode == REQUEST_GET_PHOTO && resultCode == RESULT_OK) {
			Uri uri = data.getData();
			image = (ImageView) findViewById(R.id.image_place);
			image.setAdjustViewBounds(true);
			image.setMaxWidth(300);
			image.setMaxHeight(300);
			image.setScaleType(ImageView.ScaleType.CENTER_CROP);
			image.setImageURI(uri);
		}
	}

	public void doTranslate() {
		String urlstring = getResources().getString(R.string.tran_URL)
				+ "?client_id=" + getResources().getString(R.string.client_id)
				+ "&q=";
		CharSequence text_input = input.getText();// 要不要检测
		Toast.makeText(getApplicationContext(), text_input, Toast.LENGTH_SHORT)
				.show();
		urlstring = urlstring.concat(text_input.toString());
		urlstring = urlstring.concat("&from=auto&to=auto");
		try {
			TranTask task = new TranTask();
			task.execute(new URL(urlstring));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public class TranTask extends AsyncTask<URL, Integer, String> {

		@Override
		protected String doInBackground(URL... arg0) {
			// TODO Auto-generated method stub
			List messages = new ArrayList();
			String temp;
			try {
				HttpURLConnection conn = (HttpURLConnection) arg0[0]
						.openConnection();

				conn.setConnectTimeout(10000);
				conn.setDoInput(true);
				conn.setRequestMethod("GET");
				conn.connect();
				int response = conn.getResponseCode();
				Log.d("DEBUG_TAG", "The response is: " + response);
				InputStream is = conn.getInputStream();

				InputStreamReader read = new InputStreamReader(is, "UTF-8");
				ArrayList T = new ArrayList();
				char[] buffer = new char[1000];
				read.read(buffer);
				temp = new String(buffer);
				Log.d("MESSAGE", temp); // 显示收到服务器的消息.
				try {
					JSONObject obj = new JSONObject(temp);
					JSONArray arr = obj.getJSONArray("trans_result");
					JSONObject obj_in = arr.getJSONObject(0);
					messages.add(obj_in.getString("dst")); // 翻译结果第一行
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return (String) messages.get(0);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			input.setText(result);
			super.onPostExecute(result);
		}

	}

	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		// Toast.makeText(getActivity().getApplicationContext(),"galleryAddPic",
		// Toast.LENGTH_SHORT).show();
		this.sendBroadcast(mediaScanIntent);
	}

	public static class PlaceholderFragment extends Fragment {
		private static Button notice_btn;
		private EditText notice_edt;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			View rootView = inflater.inflate(R.layout.notice_before, container,
					false);
			notice_btn = (Button) rootView.findViewById(R.id.button_set_notice);
			notice_edt = (EditText) rootView.findViewById(R.id.inputarea);
			notice_btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Fragment fragment_noticeafter = new NoticeAfterFragment();
					getFragmentManager().beginTransaction()
							.replace(R.id.notice_frame, fragment_noticeafter)
							.commit(); // 为什么这里使用 R.id.notice_fram? 而不是
										// tem.getId();
				}
			});
			return rootView;
		}

	}

}
