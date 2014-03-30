package com.jayyyR.gifjam;

import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SearchFragment extends Fragment {
	
	EditText searchName;
	Button searchButton;
	String searchProfId;
	String userID;
	String user;
	
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
		View v = inflater.inflate(R.layout.fragment_search, container, false);
		
		searchName = (EditText) v.findViewById(R.id.usernamesearch);
		searchButton = (Button) v.findViewById(R.id.search);
		
		searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String searchUser = searchName.getText().toString();
				search task = new search(searchUser);
				task.execute();
			}
		});

		
		return v;
	}
	
	private class search extends AsyncTask<String, Void, String>{

		String url;
		Activity activity;
		Gson gson = new Gson();
		String userSearch;
		String responseString;
		public search(String userToSearch){
			userSearch = userToSearch;
		}

		@Override
		protected void onPreExecute(){
			//progress dialog displays while connecting to the internet
	
		}

		@Override
		protected String doInBackground(String... params) {

			HttpClient httpClient = new DefaultHttpClient();
			try {
				//grab json data
				HttpResponse response = httpClient.execute(new HttpGet("http://128.239.163.254:5000/api/user_exists?username=" + userSearch));
				HttpEntity entity = response.getEntity();
				responseString = EntityUtils.toString(entity, "UTF-8");
				
				//set our arraylist with fuzzitem objects using gson
				
			} catch (Exception e) {
				Log.e("error", "error grabbing json");
			}
			return null;
		}


		@Override
		protected void onPostExecute(String result){
			super.onPostExecute(result);
			
			if (responseString.equals("")){
				Toast.makeText(getActivity(), "User Doesn't Exist", Toast.LENGTH_SHORT).show();
			}
			else{
				Log.v("hello", "response string: " + responseString);
				searchProfId = responseString;
				
				Fragment fragment = new ProfileFragment();
				Bundle bundle = new Bundle();
				bundle.putString("lookingAt", searchProfId);
				bundle.putString("userID", userID);
				bundle.putString("user", userSearch);
				fragment.setArguments(bundle);
				
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
			}



		}

	}

}
