package com.flaquir4.simplechat.friendsManagement;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.flaquir4.simplechat.MainActivity;
import com.flaquir4.simplechat.R;
import com.flaquir4.simplechat.DB.DatabaseFunctions;
import com.flaquir4.simplechat.login.UserFunctions;

public class SearchFriends extends Activity {
	private findFriends  friendsBackground= null;
	
	private Button searchFriends;
	private EditText searchBar;
	private String friendName;
	private ListView listview;
	private ArrayList<HashMap<String,String>> setNames;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_friends);
		searchFriends=(Button)findViewById(R.id.findFriends);
		searchBar=(EditText)findViewById(R.id.searchFriendsBD);
		setNames = new ArrayList<HashMap<String, String>>();
		listview = (ListView)findViewById(R.id.listViewSearchFriends);
		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				creaDialogoAceptarAmigo( arg2 );
				
			}
			
		});
		searchFriends.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				friendName=searchBar.getText().toString();
				if(TextUtils.isEmpty(friendName)){
					searchBar.setError(getString(R.string.SearchFriendsBDBar));
					searchBar.requestFocus();
				}
				else{
					//TODO Borrar setnames;
					friendsBackground = new findFriends();
					friendsBackground.execute((Void) null);
				}
				
			}
			
		});
	}

	private void creaDialogoAceptarAmigo(final int pos) {
		AlertDialog.Builder dialogoAceptarAmigo = new AlertDialog.Builder(this);
		String name = setNames.get(pos).get("name");
		String Message = String.format(getResources().getString(R.string.DialogAddFriend), name);
		dialogoAceptarAmigo.setMessage(Message);
		dialogoAceptarAmigo.setPositiveButton(getString(R.string.DialogPositiveButton),new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DatabaseFunctions db = new DatabaseFunctions(getApplicationContext());
				
				db.addFriend(setNames.get(pos).get("name"), setNames.get(pos).get("gcm_regid"));
				Intent i = new Intent(getApplicationContext(),MainActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});
		dialogoAceptarAmigo.setNegativeButton(R.string.DialogNegativeButton, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		dialogoAceptarAmigo.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search_friends, menu);
		return true;
	}
	
	public class findFriends extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			UserFunctions userFunctions = new UserFunctions();			
			JSONObject json = userFunctions.findFriends(friendName);
			try {
				JSONArray names = json.getJSONArray("friends");
				for(int i = 0; i< names.length(); i++){
					JSONObject aux = names.getJSONObject(i);
					HashMap<String,String> map = new HashMap<String,String>();				
					map.put("name",aux.getString("name"));
					map.put("gcm_regid", aux.getString("gcm_regid"));
					setNames.add(map);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(final Boolean success){
			runOnUiThread(new Runnable(){

				@Override
				public void run() {
					ListAdapter adapter = new SimpleAdapter(SearchFriends.this, setNames, R.layout.item_listview_search_friends, new String[]{"name"}, new int[]{R.id.itemText});
					listview.setAdapter(adapter);
				}
				
			});
		}
		
	}

}

