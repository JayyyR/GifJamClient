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
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class HomeFragment extends ListFragment {
	int mNum; //mnum for home or prof?
	ArrayList<FeedItem> feedData;
	String profileId;
	View theView;

	//new instance of our list fragment, pass num as the argument to indicate what page we're on
	static HomeFragment newInstance(int num) {
		HomeFragment f = new HomeFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//grab num which is our page number
		mNum = getArguments() != null ? getArguments().getInt("num") : 1;
		profileId = getArguments().getString("profileId");   
		Log.v("mainActivity", "profile id in frag: " + profileId);
		JSONReader dataGrabber = new JSONReader("http://128.239.163.254:5000/news_feed?loggedInUser=" + profileId, getActivity());
		dataGrabber.execute();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//inflate correct layout
		View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
		theView = v;
		return v;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//create new custom adapter

		/*
		//test feed item
		FeedItem testItem = new FeedItem();
		testItem.username = "JayyyR";
		testItem.caption = "This is a test gif";
		testItem.gif_url = "";
		testItem.likes = new ArrayList<String>();

		FeedItem testItem2 = new FeedItem();
		testItem.username = "JayyyR";
		testItem.caption = "This is a test gif";
		testItem.gif_url = "";
		testItem.likes = new ArrayList<String>();

		FeedItem testItem3 = new FeedItem();
		testItem.username = "JayyyR";
		testItem.caption = "This is a test gif";
		testItem.gif_url = "";
		testItem.likes = new ArrayList<String>();


		ArrayList<FeedItem> testfeed = new ArrayList<FeedItem>();
		testfeed.add(testItem);
		testfeed.add(testItem2);
		testfeed.add(testItem3);

		 */
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("FragmentList", "Item clicked: " + id);

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
			progressDialog = new ProgressDialog(activity);
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

			setListAdapter(customAdapter);
		}

	}
}