package com.example.drawer_fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.baidu.mapapi.BMapManager;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainFragment extends Fragment {
	private Button text_button;
	private Button cap_button;
	private Button voice_button;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_VIDEO_CAPTURE = 2;
	static final int REQUEST_GET_PHOTO = 3;
	private String mCurrentPhotoPath;
	private String mCurrentVideoPath;
	private File photoFile;
	private AlertDialog ad;
	static final String BUTTON_MODE="button_mode";
	static final String MODE = "MODE";
	static MediaPlayer play;

	public MainFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.edit_part, container, false);
		text_button = (Button) rootView.findViewById(R.id.text_mode);
		cap_button = (Button) rootView.findViewById(R.id.cap_mode);
		voice_button = (Button)rootView.findViewById(R.id.voice_mode);
		//playmusic();
		prepareChoice();

		text_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity()
						.getApplicationContext(), Do_text_input_activity.class);
				intent.putExtra(BUTTON_MODE, "text_button");
				startActivity(intent);
			}
		});
		voice_button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity().getApplicationContext(),Do_text_input_activity.class);
				intent.putExtra(BUTTON_MODE, "voice_button");
				startActivity(intent);
			}
		});
		cap_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ad.show();
			}
		});
		return rootView;
	}

	public void playmusic() {
		String url = "http://yinyueshiting.baidu.com/data2/music/119386935/14945107241200128.mp3?xcode=cf669a6d59fe55189419a416b84605e1012310c402a3862b";
		play = new MediaPlayer();
		play.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			play.setDataSource(url);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			play.prepareAsync();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		play.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				play.start();
			}

		});
	}

	public void prepareChoice() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("添加图片!").setItems(R.array.pic_choice,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
							cap();
							break;
						case 1:
							pic();
							break;
						case 2:
							video();
							break;
						}

					}
				});
		ad = builder.create();
	}

	public void cap() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent
				.resolveActivity(getActivity().getPackageManager()) != null) {
			try {
				photoFile = creatImageFile();
			} catch (IOException e) {
				Toast.makeText(getActivity().getApplicationContext(),
						"wirte wrong!", Toast.LENGTH_SHORT);
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

	public void video() {
		File videoFile = null;
		Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
			try {
				videoFile = creatVideoFile();
			} catch (IOException e) {
				Toast.makeText(getActivity().getApplicationContext(),
						"creat videoFile wrong", Toast.LENGTH_SHORT).show();
			}
			if (videoFile != null) {
				takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(videoFile));
				startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_IMAGE_CAPTURE
				&& resultCode == getActivity().RESULT_OK) {
			Intent intent = new Intent(getActivity().getApplicationContext(),
					Do_text_input_activity.class);
			intent.putExtra(BUTTON_MODE, "cap_button");
			intent.putExtra(MODE, "cap");
			intent.putExtra("filepath_name", mCurrentPhotoPath);
			startActivity(intent);
			galleryAddPic();
		}
		if (requestCode == REQUEST_VIDEO_CAPTURE
				&& resultCode == getActivity().RESULT_OK) {
			// Log.d("video", "success!");
			Intent intent = new Intent(getActivity().getApplicationContext(),
					Do_text_input_activity.class);
			intent.putExtra(BUTTON_MODE, "cap_button");
			intent.putExtra(MODE, "video");
			intent.putExtra("filepath_name", mCurrentVideoPath);
			startActivity(intent);
		}
		if (requestCode == REQUEST_GET_PHOTO
				&& resultCode == getActivity().RESULT_OK) {
            Intent intent = new Intent(getActivity().getApplicationContext(),Do_text_input_activity.class);
            Uri uri = data.getData();
            intent.putExtra(BUTTON_MODE, "cap_button");
            intent.putExtra(MODE, "pic");
            intent.putExtra("uripic", uri.toString());
            startActivity(intent);
            
		}
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

	private File creatVideoFile() throws IOException {
		String videoFileName = "video";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File videoFile = File.createTempFile(videoFileName, ".mp4", storageDir);
		mCurrentVideoPath = videoFile.getAbsolutePath();
		Log.d("mCurrentVideoPath", mCurrentVideoPath);
		return videoFile;
	}

	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		// Toast.makeText(getActivity().getApplicationContext(),"galleryAddPic",
		// Toast.LENGTH_SHORT).show();
		this.getActivity().sendBroadcast(mediaScanIntent);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		playmusic();
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		play.release();
		super.onPause();
	}

}
