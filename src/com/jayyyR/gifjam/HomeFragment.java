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
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class HomeFragment extends Fragment {
	int mNum; //mnum for home or prof?
	ArrayList<FeedItem> feedData;
	String profileId;
	View theView;
	int currentPage = 0;
	TextView username;
	TextView caption;
	Button likeButton;
	TextView likes;
	WebView gifContent;
	Button nextButton;
	Button prevButton;
	String loggedUser;


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
		loggedUser = getArguments().getString("userName");
		Log.v("mainActivity", "profile id in frag: " + profileId);

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//inflate correct layout
		View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
		theView = v;

		username = (TextView) v.findViewById(R.id.userName);
		likes = (TextView) v.findViewById(R.id.likes);
		gifContent = (WebView) v.findViewById(R.id.webContent);
		caption = (TextView) v.findViewById(R.id.captionContent);
		likeButton = (Button) v.findViewById(R.id.likeButton);
		nextButton = (Button) v.findViewById(R.id.nextButton);
		prevButton = (Button) v.findViewById(R.id.prevButton);


		likeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String likesText = likes.getText().toString();
				StringBuilder likesBuilder = new StringBuilder(likesText);
				likesBuilder.append(", " + loggedUser);
				likes.setText(likesBuilder.toString());
				like task = new like();
				task.execute();

			}
		});
		nextButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentPage < feedData.size()-1){
					currentPage++;
					afterData();
				}

			}
		});

		prevButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentPage >0){
					currentPage--;
					afterData();
				}

			}
		});

		Log.v("mainActivity", "returning view");
		JSONReader dataGrabber = new JSONReader("http://128.239.163.254:5000/news_feed?loggedInUser=" + profileId, getActivity());
		dataGrabber.execute();
		return v;
	}

	public void afterData(){
		if (feedData.size() >0){
			
			username.setVisibility(View.VISIBLE);
			likes.setVisibility(View.VISIBLE);
			gifContent.setVisibility(View.VISIBLE);
			caption.setVisibility(View.VISIBLE);
			likeButton.setVisibility(View.VISIBLE);
			if (feedData.get(currentPage) != null){
				username.setText("@" +feedData.get(currentPage).username);
				caption.setText(feedData.get(currentPage).caption);
				gifContent.clearHistory();
				gifContent.clearCache(true);
				gifContent.freeMemory();  //new code 
				Log.v("gifurl", feedData.get(currentPage).gif_url);
				gifContent.loadUrl(feedData.get(currentPage).gif_url);

				StringBuilder likeees = new StringBuilder();
				for (int i = 0; i < feedData.get(currentPage).likes.size(); i++){
					if (i < feedData.get(currentPage).likes.size()-1 )
						likeees.append(feedData.get(currentPage).likes.get(i)+", ");
					else
						likeees.append(feedData.get(currentPage).likes.get(i));
				}
				likes.setText("<3: " + likeees.toString());
			}
		}
		else{
			username.setVisibility(View.GONE);
			likes.setVisibility(View.GONE);
			gifContent.setVisibility(View.GONE);
			caption.setVisibility(View.GONE);
			likeButton.setVisibility(View.GONE);
		}

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
			afterData();
		}

	}

	private class like extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			HttpClient client = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			String gifURL = feedData.get(currentPage).gif_url;
			String[] split = gifURL.split("file/");
			Log.v("gifurl", "split sub 1" + split[1]);
			String[] split2 = split[1].split("\\.");
			Log.v("gifurl", "split2" + split2[0]);
			String gifCode = split2[0];
			Log.v("gifurl", gifCode + "");
			String url = "http://128.239.163.254:5000/like/" + profileId;

			HttpPost post = new HttpPost(url);
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("gif_name", gifCode));
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


	}
}