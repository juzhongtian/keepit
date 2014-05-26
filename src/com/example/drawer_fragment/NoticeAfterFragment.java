package com.example.drawer_fragment;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.app.service.LocateService;
import com.example.app.service.LocateService.LocalBinder;
import android.R.color;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class NoticeAfterFragment extends Fragment {

	private Spinner spinner_day;
	private Spinner spinner_timeorlocation;
	private Spinner spinner_morning;
	private AlertDialog ad;
	private AlertDialog dispathlog;
	private Button cancel;
	BMapManager mBMapMan = null;
	MapView mMapView = null;
	MKSearch mMKSearch = null;
	private ListView mlist;
	View rootView;
	View view;
	private LocateService mservice;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mBMapMan = new BMapManager(getActivity().getApplicationContext());
		mBMapMan.init(null);

		rootView = inflater.inflate(R.layout.notice_after, container, false);
		cancel = (Button) rootView.findViewById(R.id.button_noticeafter);
		spinner_day = (Spinner) rootView.findViewById(R.id.spinner_day);
		spinner_timeorlocation = (Spinner) rootView
				.findViewById(R.id.spinner_timeorlocation);
		spinner_morning = (Spinner) rootView.findViewById(R.id.spinner_morning);
		spinner_morning.setPopupBackgroundResource(R.color.spinnerback);
		spinner_timeorlocation.setPopupBackgroundResource(R.color.spinnerback);
		spinner_day.setPopupBackgroundResource(R.color.spinnerback);

		init_spinner_timeorlocation();
		init_spinner_day();
		init_spinner_morning();

		Intent intent = new Intent(getActivity(), LocateService.class);
		getActivity().bindService(intent, new MyServiceConnection(),
				Context.BIND_AUTO_CREATE);

		return rootView;
	}

	public void init_spinner_timeorlocation() {

		ArrayAdapter adaptertimeOrLocation = null;
		adaptertimeOrLocation = adaptertimeOrLocation.createFromResource(
				getActivity(), R.array.notice_timeorlocation,
				android.R.layout.simple_spinner_item);
		adaptertimeOrLocation
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_timeorlocation.setAdapter(adaptertimeOrLocation);
		spinner_timeorlocation
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						// TODO Auto-generated method stub
						if (position == 1)
							dispathlocation(); // 处理地图事物
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}

				});
	}

	public void init_spinner_day() {
		ArrayAdapter adapterDay = null;
		adapterDay = adapterDay.createFromResource(getActivity(),
				R.array.notice_day_array, android.R.layout.simple_spinner_item);
		adapterDay
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_day.setAdapter(adapterDay);
		spinner_day.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				if (pos == 3) {
					Toast.makeText(getActivity(), "day pic", Toast.LENGTH_SHORT)
							.show();
					DatePickerDialog datepic = new DatePickerDialog(
							getActivity(), null, 2014, 2, 26);
					datepic.show();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
	}

	public void init_spinner_morning() {
		ArrayAdapter adapterMorning = null;
		adapterMorning = adapterMorning.createFromResource(getActivity(),
				R.array.notice_morning_array,
				android.R.layout.simple_spinner_item);
		adapterMorning
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner_morning.setAdapter(adapterMorning);
		spinner_morning.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				if (position == 4) {
					Toast.makeText(getActivity(), "time pic",
							Toast.LENGTH_SHORT).show();

					TimePickerDialog timepic = new TimePickerDialog(
							getActivity(), null, 0, 0, true);
					timepic.show();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

	}

	public class MySearchListener implements MKSearchListener {

		@Override
		public void onGetPoiResult(MKPoiResult res, int type, int error) {
			// TODO Auto-generated method stub
			if (error == MKEvent.ERROR_RESULT_NOT_FOUND) {
				Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_LONG)
						.show();
				return;
			} else if (error != 0 || res == null) {
				Toast.makeText(getActivity(), "搜索出错啦..", Toast.LENGTH_LONG)
						.show();
				return;
			}
			// 将poi结果显示到地图上
			PoiOverlay poiOverlay = new PoiOverlay(getActivity(), mMapView);
			poiOverlay.setData(res.getAllPoi());
			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(poiOverlay);
			mMapView.refresh();
			// 当ePoiType为2（公交线路）或4（地铁线路）时， poi坐标为空
			for (MKPoiInfo info : res.getAllPoi()) {
				if (info.pt != null) {
					mMapView.getController().animateTo(info.pt);
					break;
				}
			}
		}
		@Override
		public void onGetSuggestionResult(MKSuggestionResult res, int iError) {
			// TODO Auto-generated method stub
			if (iError != 0 || res == null) {
				Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_LONG)
						.show();
				return;
			}
			int nSize = res.getSuggestionNum();
			String[] mStrSuggestions = new String[nSize];
			for (int i = 0; i < nSize; i++) {
				mStrSuggestions[i] = res.getSuggestion(i).city
						+ res.getSuggestion(i).key;
			}

			mlist = (ListView) view.findViewById(R.id.sugguestList);
			EditText text = (EditText) view.findViewById(R.id.location_input);
			text.setHint("输入地址");
			ArrayAdapter<String> suggestionString = new ArrayAdapter<String>(
					getActivity(), android.R.layout.simple_list_item_1,
					mStrSuggestions);
			mlist.setAdapter(suggestionString);
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}
		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
				int arg2) {
			// TODO Auto-generated method stub
			
		}

	}

	public void dispathlocation() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("位置方式").setItems(R.array.location_list,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int position) {
						// TODO Auto-generated method stub
						switch (position) {
						case 0:
							dealmap();
							break;
						case 1:
							BDLocation location = mservice.getCurrentLocation();
							Toast.makeText(getActivity(),
									location.getAddrStr(), Toast.LENGTH_SHORT)
									.show();
							break;
						case 2:
							Intent intent1 = new Intent(getActivity(),
									SearchNearbyActivity.class);
							getActivity().startActivityForResult(intent1,1);   //得到的结果该是 string 吗？？
							break;
						}
					}
				});

		dispathlog = builder.create();
		dispathlog.show();

	}

	public class MyServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// TODO Auto-generated method stub
			// We've bound to LocalService, cast the IBinder and get
			// LocalService instance
			Log.d("go in", "yes");
			LocalBinder binder = (LocalBinder) service;
			mservice = binder.getService();
			if (mservice == null)
				Log.d("what the hell", "null");
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub

		}

	}

	public void dealmap() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.loction_input, null);
		mMKSearch = new MKSearch();
		mMKSearch.init(mBMapMan, new MySearchListener());// 注意，MKSearchListener只支持一个，以最后一次设置为准
		Button buttonsugguest = (Button) view
				.findViewById(R.id.buttontosugguest);
		buttonsugguest.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				EditText text = (EditText) view
						.findViewById(R.id.location_input);
				CharSequence content = text.getText();
				if (content != null)
					mMKSearch.suggestionSearch(content.toString(),
							"南京");
			}
		});
		builder.setView(view);
		ad = builder.create();
		ad.show();

		// mMKSearch.poiSearchNearBy("KFC", new GeoPoint((int) (39.915 * 1E6),
		// (int) (116.404 * 1E6)), 5000);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(mMKSearch!=null)
		mMKSearch.destory();
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		if(mMKSearch!=null)
		mMKSearch.destory();
		super.onPause();
	}

}
