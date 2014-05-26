package com.example.drawer_fragment;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
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
import com.example.drawer_fragment.NoticeAfterFragment.MySearchListener;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;
import android.os.Build;

public class SearchNearbyActivity extends Activity {
	MapView mMapView = null;
	BMapManager mBMapMan = null;
	MKSearch mMKSearch = null;
	private LocateService mservice;
	BDLocation location;
    GeoPoint geopoint;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBMapMan = new BMapManager(getApplicationContext());
		mBMapMan.init(null);
		setContentView(R.layout.activity_search_nearby);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		mMapView = (MapView) findViewById(R.id.bmapsView);
		// mMapView.setBuiltInZoomControls(true);
		// 设置启用内置的缩放控件
		final MapController mMapController = mMapView.getController();
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
	
		mMapController.setZoom(12);// 设置地图zoom级别
		Intent intent = new Intent(this, LocateService.class);
		mMKSearch = new MKSearch();
		mMKSearch.init(mBMapMan, new MySearchListener());// 注意，MKSearchListener只支持一个，以最后一次设置为准
		bindService(intent, new ServiceConnection() {

			@Override
			public void onServiceConnected(ComponentName arg0, IBinder arg1) {
				// TODO Auto-generated method stub
				Log.d("go in", "yes");
				LocalBinder binder = (LocalBinder) arg1;
				mservice = binder.getService();
				if (mservice == null)
					Toast.makeText(getApplicationContext(), "null",
							Toast.LENGTH_SHORT).show();
				location = mservice.getCurrentLocation();
				geopoint = new GeoPoint(
						(int) (location.getLatitude() * 1E6), (int) (location
								.getLongitude() * 1E6));
				mMapController.setCenter(geopoint);// 设置地图中心点
			}

			@Override
			public void onServiceDisconnected(ComponentName arg0) {
				// TODO Auto-generated method stub

			}
		}, Context.BIND_AUTO_CREATE);

	}

	public class MySearchListener implements MKSearchListener {

		@Override
		public void onGetPoiResult(MKPoiResult res, int type, int error) {
			// TODO Auto-generated method stub
			if (error == MKEvent.ERROR_RESULT_NOT_FOUND) {
				Toast.makeText(SearchNearbyActivity.this, "抱歉，未找到结果",
						Toast.LENGTH_LONG).show();
				return;
			} else if (error != 0 || res == null) {
				Toast.makeText(SearchNearbyActivity.this, "搜索出错啦..",
						Toast.LENGTH_LONG).show();
				return;
			}
			// 将poi结果显示到地图上
			PoiOverlay poiOverlay = new PoiOverlay(SearchNearbyActivity.this,
					mMapView);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_nearby, menu);
		// 所以通过getActionView()便可以显式转换为SearchView
        SearchView searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        // 注册搜索输入栏的事件监听器，来自于实现接口：android.widget.SearchView.OnQueryTextListener
        // 事件回调方法为：onQueryTextSubmit()开始搜索事件；onQueryTextChange()文本改变事件
        searchView.setOnQueryTextListener(new OnQueryTextListener(){

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				showToast("get in");
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				mMKSearch.poiSearchNearBy(query, geopoint, 5000);
				return false;
			}});
		return true;
	}
	
	public void showToast(String x){
		Toast.makeText(this, x, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
