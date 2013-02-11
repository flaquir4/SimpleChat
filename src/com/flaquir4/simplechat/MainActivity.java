package com.flaquir4.simplechat;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.flaquir4.simplechat.DB.DatabaseFunctions;
import com.flaquir4.simplechat.chat.Chat;
import com.flaquir4.simplechat.friendsManagement.SearchFriends;
import com.flaquir4.simplechat.login.LoginActivity;
import com.google.android.gcm.GCMRegistrar;

public class MainActivity extends Activity {
	private Button addFriends;
	private EditText searchFriends;
	private ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//while developing check maifest and phone
		
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		//Check if user is logged in
		SharedPreferences preferences = getSharedPreferences("com.flaquir4.simplechat",Context.MODE_PRIVATE);
		boolean loggedin = preferences.getBoolean("loggedin", false);
		if(!loggedin){	
			Intent i = new Intent(this, LoginActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivity(i);
		}
		setContentView(R.layout.activity_main);
		
		///UI Elements
		addFriends = (Button)findViewById(R.id.addFriends);
		searchFriends = (EditText)findViewById(R.id.searchFriends);
		listView =(ListView)findViewById(R.id.listViewMain);
		addFriends.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(),SearchFriends.class);
				startActivity(i);
			}
		
		});
		
		DatabaseFunctions db = new DatabaseFunctions(this);
		final ArrayList<HashMap<String,String>> listContent = db.getChats();
		SimpleAdapter adapter = new SimpleAdapter(this,listContent,R.layout.item_listview_main_activity,new String [] {"name"}, new int[] {R.id.textItemMainActivity});
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent(getApplicationContext(), Chat.class);
				i.putExtra("name", listContent.get(arg2).get("name"));
				i.putExtra("regid", listContent.get(arg2).get("regid"));
				startActivity(i);
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
