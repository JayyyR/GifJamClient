package com.jayyyR.gifjam;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


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
			v = vi.inflate(R.layout.list_item, null);

		}

		//get correct fuzz item
		FeedItem item = getItem(position);

		if (item != null) {

			TextView id = (TextView) v.findViewById(R.id.idTextView);
			TextView text = (TextView) v.findViewById(R.id.textTextView);
			final ImageView image = (ImageView) v.findViewById(R.id.imageImageView);

			//set id
			if (id != null) {
				id.setText("id: " + item.id);
			}
			
			//if the type is text
			if (text != null && !item.isImage()) {
				//show textview, hide imageview
				text.setVisibility(View.VISIBLE);
				image.setVisibility(View.GONE);
				text.setText(item.data);
			}
			//if the type is image
			else if (image != null) {
				//show imageview, hide textview
				image.setVisibility(View.VISIBLE);
				text.setVisibility(View.GONE);
				
				if(item.image != null)
					image.setImageBitmap(item.image);
				//a good amount of the urls for the images lead nowhere. I'm putting this image here to indicate those
				else
					image.setImageResource(R.drawable.error);

				
			}
		}

		return v;

	}
}
