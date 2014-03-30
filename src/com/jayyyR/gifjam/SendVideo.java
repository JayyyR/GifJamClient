package com.jayyyR.gifjam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Build;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;



public class SendVideo extends Activity {

	String fileLoc;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_video);

		Bundle extras = getIntent().getExtras();
		if (extras != null) 
			fileLoc = extras.getString("file");

		Log.v("filelocation", fileLoc);
		
		Button upload = (Button) findViewById(R.id.sendVideo);
		
		upload.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendVideo task = new sendVideo();
				task.execute();
			}
			
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.send_video, menu);
		return true;
	}

	private class sendVideo extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {

			System.out.println("Hi");

			HttpClient client = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost post = new HttpPost("http://128.239.163.254:5000/upload");
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("video", fileLoc));
				MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

				for(int index=0; index < nameValuePairs.size(); index++) {
					if(nameValuePairs.get(index).getName().equalsIgnoreCase("video")) {
						// If the key equals to "image", we use FileBody to transfer the data
						Log.v("datasend", "index is: " + index);
						Log.v("datasend", "get name is: " + nameValuePairs.get(index).getName());
						Log.v("datasend", "get value is: " + nameValuePairs.get(index).getValue());
						entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue())));
					} else {
						// Normal string data
						entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
					}
				}

				post.setEntity(entity);

				HttpResponse response = client.execute(post, localContext);


			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}



}
