package com.jayyyR.gifjam;


import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ProfileFragment extends Fragment{
	
	String profileId;
	String user;
	TextView userName;
	TextView bio;
	WebView profGif;
	ListView profFeed;
	Button follow;
	ArrayList<FeedItem> feedData;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		profileId = getArguments().getString("profileId");   
		Log.v("mainActivity", "profile id in frag: " + profileId);
		
		profileId = getArguments().getString("profileId");   
		user = getArguments().getString("user");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//inflate correct layout
		View v = inflater.inflate(R.layout.fragement_profile, container, false);
		
		userName = (TextView) v.findViewById(R.id.userName);
		userName.setText(user);
		
		bio = (TextView) v.findViewById(R.id.bio);
		profGif = (WebView) v.findViewById(R.id.profileGif);
		profFeed = (ListView) v.findViewById(R.id.feed);
		follow = (Button) v.findViewById(R.id.follow);
		
		JSONReader dataGrabber = new JSONReader("http://128.239.163.254:5000/profile_feed?user=" + profileId, getActivity());
		dataGrabber.execute();
		return v;
	}
	
	
	/*private class to grab json array from url*/
	private class JSONReader extends AsyncTask<String, Void, String>{

		String url;
		Activity activity;
		java.lang.reflect.Type arrayListType = new TypeToken<ArrayList<FeedItem>>(){}.getType();
		Gson gson = new Gson();
		ProgressDialog progressDialog;

		public JSONReader(String url, Activity activity){
			this.url = url;
			this.activity = activity;
		};

		@Override
		protected void onPreExecute(){
			//progress dialog displays while connecting to the internet
			progressDialog= ProgressDialog.show(activity, "Getting Feed","Please Wait", true);
		}

		@Override
		protected String doInBackground(String... params) {

			HttpClient httpClient = new DefaultHttpClient();
			try {
				//grab json data
				HttpResponse response = httpClient.execute(new HttpGet(url));
				HttpEntity entity = response.getEntity();
				Reader reader = new InputStreamReader(entity.getContent());
				//set our arraylist with fuzzitem objects using gson
				feedData = gson.fromJson(reader, arrayListType);
			} catch (Exception e) {
				Log.e("error", "error grabbing json");
			}
			return null;
		}


		@Override
		protected void onPostExecute(String result){
			super.onPostExecute(result);

			progressDialog.dismiss();
			ListAdapter customAdapter;
			
			Log.v("data3", feedData+ "");
			customAdapter = new ListAdapter(getActivity(), R.layout.feed_view_item, feedData, getActivity());

			profFeed.setAdapter(customAdapter);
		}

	}

}
