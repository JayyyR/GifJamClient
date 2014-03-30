package com.jayyyR.gifjam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

public class Login extends Activity {

	String userName;
	String hashedPass;
	Activity activity;
	String profileId = null;
	boolean shouldLogin = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		activity = this;
		final EditText userField = (EditText) findViewById(R.id.userField);
		final EditText passField = (EditText) findViewById(R.id.passField);

		Button login = (Button) findViewById(R.id.login);
		Button register = (Button) findViewById(R.id.register);

		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				userName = userField.getText().toString();
				hashedPass = passField.getText().toString();

				login task = new login();
				task.execute();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	private class login extends AsyncTask<String, Void, String> {

		ProgressDialog pDialog;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = pDialog.show(activity, "Logging in","Please Wait", true);
		}
		@Override
		protected String doInBackground(String... urls) {


			HttpClient client = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost post = new HttpPost("http://128.239.163.254:5000/login");
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("username", userName));
				nameValuePairs.add(new BasicNameValuePair("password", hashedPass));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = client.execute(post, localContext);
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");

				Log.v("respon", "response: "+ responseString);

				if (responseString.equals("null")){
					shouldLogin=false;
				}
				else{
					shouldLogin=true;
					profileId = responseString;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}


		@Override
		protected void onPostExecute(String result) {
			

			if (shouldLogin){
				pDialog.dismiss();
				Intent intent = new Intent(activity, MainActivity.class);
				intent.putExtra("user", userName);
				intent.putExtra("profileId", profileId);
				startActivity(intent);
			}
			else{
				pDialog.dismiss();
				Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
			}

		}
	}
	
	@Override
	public void onBackPressed() {
	}



}
