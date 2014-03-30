package com.jayyyR.gifjam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsFragment extends Fragment{
	
	String userID;
	String user;
	Button updateBio;
	Button changePic;
	EditText bioText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		userID = getArguments().getString("userID");
		user = getArguments().getString("user");
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//inflate correct layout
		View v = inflater.inflate(R.layout.fragment_settings, container, false);
		
		bioText = (EditText) v.findViewById(R.id.biotext);
		updateBio = (Button) v.findViewById(R.id.updatebutton);
		changePic = (Button) v.findViewById(R.id.profpicbutton);
		
		updateBio.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String bioData = bioText.getText().toString();
				updateBio task = new updateBio(bioData);
				task.execute();
			}
		});
		
		changePic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), CameraActivity.class);
				intent.putExtra("user", user);
				intent.putExtra("profileId", userID);
				intent.putExtra("forProf", true);
			    startActivity(intent);
			}
		});

		
		return v;
	}
	
	private class updateBio extends AsyncTask<String, Void, String>{

		String url;
		Activity activity;
		Gson gson = new Gson();
		String bioText;
		String responseString;
		public updateBio(String bioText){
			this.bioText = bioText;
		}

		@Override
		protected void onPreExecute(){
			//progress dialog displays while connecting to the internet
	
		}

		@Override
		protected String doInBackground(String... params) {

			HttpClient client = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost post = new HttpPost("http://128.239.163.254:5000/update_profile/" + userID);
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("bio", bioText));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = client.execute(post, localContext);

			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}


		@Override
		protected void onPostExecute(String result){
			super.onPostExecute(result);

			Toast.makeText(getActivity(), "Updated Bio!", Toast.LENGTH_SHORT).show();

		}

	}

}
