package com.jayyyR.gifjam;

import java.util.ArrayList;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

public class FeedItem {

	@SerializedName("username")
	public String username;
	
	@SerializedName("gif_url")
	public String gif_url;
	
	@SerializedName("timestamp")
	public long timestamp;
	
	@SerializedName("comments")
	public ArrayList<String> comments;
	
	@SerializedName("caption")
	public String caption;

	@SerializedName("likes")
	public ArrayList<String> likes;
	

}
