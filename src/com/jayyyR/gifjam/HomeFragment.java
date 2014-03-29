package com.jayyyR.gifjam;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class HomeFragment extends ListFragment {
	int mNum; //mnum for home or prof?
	
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
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//inflate correct layout
		View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//create new custom adapter
		ListAdapter customAdapter;
		
		//test feed item
		FeedItem testItem = new FeedItem();
		testItem.username = "JayyyR";
		testItem.caption = "This is a test gif";
		testItem.gif_url = "";
		testItem.video_url = "";
		testItem.likes = new ArrayList<String>();
		
		FeedItem testItem2 = new FeedItem();
		testItem.username = "JayyyR";
		testItem.caption = "This is a test gif";
		testItem.gif_url = "";
		testItem.video_url = "";
		testItem.likes = new ArrayList<String>();
		
		FeedItem testItem3 = new FeedItem();
		testItem.username = "JayyyR";
		testItem.caption = "This is a test gif";
		testItem.gif_url = "";
		testItem.video_url = "";
		testItem.likes = new ArrayList<String>();
		
		
		ArrayList<FeedItem> testfeed = new ArrayList<FeedItem>();
		testfeed.add(testItem);
		testfeed.add(testItem2);
		testfeed.add(testItem3);
		
		customAdapter = new ListAdapter(getActivity(), R.layout.feed_view_item, testfeed, getActivity());

		setListAdapter(customAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("FragmentList", "Item clicked: " + id);

	}
}