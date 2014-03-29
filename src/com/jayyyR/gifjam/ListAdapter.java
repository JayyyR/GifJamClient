package com.jayyyR.gifjam;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.VideoView;


public class ListAdapter extends ArrayAdapter<FeedItem> {

	Activity activity;
	public ListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public ListAdapter(Context context, int resource, List<FeedItem> items, Activity activity) {
		super(context, resource, items);
		
		this.activity= activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;

		//inflate our view if need be from correct layout
		if (v == null) {

			LayoutInflater vi;
			vi = LayoutInflater.from(getContext());
			v = vi.inflate(R.layout.feed_view_item, null);

		}

		//get correct fuzz item
		FeedItem item = getItem(position);

		if (item != null) {

			TextView userName = (TextView) v.findViewById(R.id.userName);
			TextView likes = (TextView) v.findViewById(R.id.likes);
			final VideoView gifContent = (VideoView) v.findViewById(R.id.gifContent);
			
			  //get display size to set the camera preview
	        Display display = activity.getWindowManager().getDefaultDisplay();
	        Point size = new Point();
	        display.getSize(size);
	        int width = size.x;
	        int height = size.y;
	        gifContent.setLayoutParams(new TableRow.LayoutParams(width,width));
			final String mUrl = "http://128.239.163.254:5000/file/8c4639f0-cae7-4bfc-be94-4d6d072e0740.mp4";
			//String srcPath = "rtsp://v4.cache7.c.youtube.com/CjYLENy73wIaLQky7ThXrRjPYRMYDSANFEIJbXYtZ29vZ2xlSARSBXdhdGNoYKjR78WV1ZH5Tgw=/0/0/0/video.3gp"; //Declare your url here.
			gifContent.setVideoPath(mUrl);
			gifContent.requestFocus();
			//gifContent.start();
			
			gifContent.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
		        @Override
		        public void onPrepared(MediaPlayer arg0) {
		        	gifContent.start();

		        }
		    });
			
			gifContent.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
		        public void onCompletion(MediaPlayer mp) {
		                //I have a log statment here, so I can see that it is making it this far.
		                mp.reset(); // <--- I added this recently to try to fix the problem
		                gifContent.setVideoPath(mUrl);
		        }
		    });
			Button likeButton = (Button) v.findViewById(R.id.likeButton);


		}

		return v;

	}
}
