package com.jayyyR.gifjam;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

public class ProfileFragment extends Fragment{
	
	String lookingAtID;
	String userID;
	String user;
	TextView userName;
	TextView bio;
	WebView profGif;
	ListView profFeed;
	Button follow;
	ProfileItem profData;
	boolean canFollow = true;
	ArrayList<FeedItem> feedData;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		lookingAtID = getArguments().getString("lookingAt");   
		userID = getArguments().getString("userID");
		Log.v("mainActivity", "profile id in frag: " + lookingAtID);   
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
		
		follow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if (canFollow){
					follow.setText("Unfollow");
					
				}
				else{
					follow.setText("Follow");
					
				}
				follow task = new follow();
				task.execute();
			}
		});
		
		GrabProfileData test = new GrabProfileData("http://128.239.163.254:5000/get_profile/" + lookingAtID + "?viewer_id=" + userID, getActivity());
		test.execute();
		
		return v;
	}
	
	
	/*private class to grab json array from url*/
	private class GrabFeed extends AsyncTask<String, Void, String>{

		String url;
		Activity activity;
		java.lang.reflect.Type arrayListType = new TypeToken<ArrayList<FeedItem>>(){}.getType();
		Gson gson = new Gson();
		ProgressDialog progressDialog;

		public GrabFeed(String url, Activity activity){
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
	
	private class GrabProfileData extends AsyncTask<String, Void, String>{

		String url;
		Activity activity;
		Gson gson = new Gson();
		ProgressDialog progressDialog;

		public GrabProfileData(String url, Activity activity){
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
				profData = gson.fromJson(reader, ProfileItem.class);
			} catch (Exception e) {
				Log.e("error", "error grabbing json");
			}
			return null;
		}


		@Override
		protected void onPostExecute(String result){
			super.onPostExecute(result);
			progressDialog.dismiss();
			
			bio.setText(profData.bio);
			
			profGif.getSettings().setLoadWithOverviewMode(true); 
			profGif.getSettings().setUseWideViewPort(true);
			profGif.loadUrl(profData.profile_gif_url);
			if(profData.is_following){
				follow.setText("Unfollow");
				canFollow = false;
			}
			

			GrabFeed dataGrabber = new GrabFeed("http://128.239.163.254:5000/profile_feed?user=" + lookingAtID, getActivity());
			dataGrabber.execute();
			
		}

	}
	
	private class follow extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			HttpClient client = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			String url = null;
			if (canFollow){
				url = "http://128.239.163.254:5000/follow/" + userID;
			}
			else{
				url = "http://128.239.163.254:5000/unfollow/" + userID;
				}
			HttpPost post = new HttpPost(url);
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				if (canFollow)
					nameValuePairs.add(new BasicNameValuePair("id_to_follow", lookingAtID));
				else
					nameValuePairs.add(new BasicNameValuePair("id_to_unfollow", lookingAtID));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = client.execute(post, localContext);
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");

				Log.v("respon", "response: "+ responseString);


			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}


		@Override
		protected void onPostExecute(String result) {
			
			if (canFollow)
				canFollow = false;
			else
				canFollow = true;

		}
	}

}
