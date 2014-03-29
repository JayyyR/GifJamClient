package com.jayyyR.gifjam;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;


public class ListAdapter extends ArrayAdapter<FeedItem> {


	public ListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public ListAdapter(Context context, int resource, List<FeedItem> items) {
		super(context, resource, items);
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
			VideoView gifContent = (VideoView) v.findViewById(R.id.gifContent);
			Button likeButton = (Button) v.findViewById(R.id.likeButton);


		}

		return v;

	}
}
