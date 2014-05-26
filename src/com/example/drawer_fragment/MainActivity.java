package com.example.drawer_fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.app.AppContext;
import com.example.app.service.LocateService;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {
	final AppContext ac = (AppContext) getApplication(); // 全局context;
	private DrawerLayout mDrawerLayout;
	private static TextView content_text;
	private ListView list;
	private ListView list_top;
	private LinearLayout lay;
	static final String OPEARTEMODE = "operatemode";
	private int image[] = { android.R.drawable.ic_menu_edit,
			android.R.drawable.ic_menu_myplaces,
			android.R.drawable.ic_menu_slideshow,
			android.R.drawable.ic_menu_search };
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LocateService service = new LocateService();
		Intent intent = new Intent(this,LocateService.class);
		startService(intent);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		lay = (LinearLayout) findViewById(R.id.left_drawer_all);
		list = (ListView) findViewById(R.id.left_drawer);
		list_top = (ListView) findViewById(R.id.left_drawer_top);
		setActionBar();
		setList();
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Bundle args = new Bundle();
				args.putInt(OPEARTEMODE, position);
				Fragment fragment = new MainFragment();
				fragment.setArguments(args);
				getFragmentManager().beginTransaction()
						.replace(R.id.content_frame, fragment).commit();
				list.setItemChecked(position, true);
				mDrawerLayout.closeDrawer(lay);
			}

		});
		
	}

	private void setList() {
		// TODO Auto-generated method stub
		List<Map<String, Object>> name_list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> name_list_top = new ArrayList<Map<String, Object>>();
		Map<String, Object> map_top = new HashMap<String, Object>();
		map_top.put("touxiang", R.drawable.ic_launcher);
		map_top.put("name", getResources().getString(R.string.account_name));
		map_top.put("email", getResources().getString(R.string.email));
		name_list_top.add(map_top);
		for (int i = 0; i < 4; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("touxiang", image[i]);
			map.put("name", getResources().getStringArray(R.array.name_list)[i]);
			name_list.add(map);
		}
		list.setAdapter(new SimpleAdapter(this, name_list, R.layout.list_item,
				new String[] { "touxiang", "name" }, new int[] { R.id.pic,
						R.id.text }));
		list_top.setAdapter(new SimpleAdapter(this, name_list_top,
				R.layout.list_item_account, new String[] { "touxiang", "name",
						"email" }, new int[] { R.id.imageView1, R.id.textView1,
						R.id.textView2 }));

	}

	private void setActionBar() {
		// TODO Auto-generated method stub
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer,// android.R.drawable.ic_delete,
				R.string.drawer_open, R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				// getActionBar().setTitle("开启");
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				// getActionBar().setTitle("关闭");
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

}
