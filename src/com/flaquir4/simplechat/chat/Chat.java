package com.flaquir4.simplechat.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.flaquir4.simplechat.R;
import com.flaquir4.simplechat.GCM.ServerUtilities;

public class Chat extends Activity {

	private String name;
	private Button sendButton;
	private ListView listView;
	private EditText editText;
	FileOutputStream fos =null;
	BufferedWriter writer = null;
	ArrayList<HashMap<String,String>> conversation = null;
	ListAdapter adapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		final String regId = getIntent().getExtras().getString("regid");
		name = getIntent().getExtras().getString("name");
		registerReceiver(mHandleMessageReceiver, new IntentFilter("com.flaquir4.simplechat.DISPLAY_MESSAGE"));

		//ui elements
		sendButton = (Button)findViewById(R.id.chatButton);
		listView = (ListView)findViewById(R.id.chatListView);
		editText = (EditText)findViewById(R.id.chateditText);

		//Chat load

		conversation = new ArrayList<HashMap<String,String>>();

		//read from text
		try {
			FileInputStream fis = openFileInput(getString(R.string.app_name)+name);
			BufferedReader read = new BufferedReader(new InputStreamReader(fis));
			String line = "";
			while(line != null){
				line = read.readLine();
				if(line != null){
					HashMap<String,String> aux = new HashMap<String,String>();
					aux.put("sentence", line);
					conversation.add(aux);
				}
			}
			read.close();
			fis.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		adapter = new SimpleAdapter(this, conversation, R.layout.item_listview_chat, new String[] {"sentence"},new int[]{R.id.ChatitemText});
		listView.setAdapter(adapter);
		listView.setSelection(listView.getAdapter().getCount()-1);


		sendButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(TextUtils.isEmpty(editText.getText().toString())){
					editText.setError(getResources().getString(R.string.SearchFriendsBDBar));
				}
				else{
					String message = editText.getText().toString();
					ServerUtilities utilities = new ServerUtilities();
					SharedPreferences preferences= getSharedPreferences("com.flaquir4.simplechat", Context.MODE_PRIVATE);
					String nm = preferences.getString("name", "");
					utilities.sendMessage(message, regId, nm);
					HashMap<String,String> map = new HashMap<String,String>();
					map.put("sentence", message);
					conversation.add(map);
					listView.setAdapter(adapter);
					listView.setSelection(listView.getAdapter().getCount()-1);

					try {
						writer.write(message+"\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					editText.setText("");
				}
			}

		});

		try {

			fos = openFileOutput(getString(R.string.app_name)+name, Context.MODE_APPEND|Context.MODE_PRIVATE);
			writer = new BufferedWriter(new OutputStreamWriter(fos));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_chat, menu);
		return true;
	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			String message = arg1.getExtras().getString("message");
			String nameI = arg1.getExtras().getString("name");
			if(nameI.equals(name)){
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("sentence", message);
				conversation.add(map);
				try {
					writer.write(message+"\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				listView.setAdapter(adapter);
				listView.setSelection(listView.getAdapter().getCount()-1);
			}
			else{
			      FileOutputStream fos;
			  	try {
			  		fos = openFileOutput(getString(R.string.app_name)+nameI, Context.MODE_APPEND|Context.MODE_PRIVATE);
			  	    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
			  	    
			  	    writer.write(message+"\n");
			  	    writer.flush();
			  	    writer.close();

			  	} catch (FileNotFoundException e) {
			  		// TODO Auto-generated catch block
			  		e.printStackTrace();
			  	} catch (IOException e) {
			  		// TODO Auto-generated catch block
			  		e.printStackTrace();
			  	}
			}
		}

	};
	@Override
	public void onDestroy(){
		try {
			writer.flush();
			writer.close();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.onDestroy();
	}

}
