package com.jayyyR.gifjam;

import java.util.ArrayList;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

public class FeedItem {

	@SerializedName("username")
	public String username;
	
	@SerializedName("caption")
	public String caption;
	
	@SerializedName("gif_url")
	public String gif_url;
	
	@SerializedName("video_url")
	public String video_url;
	
	@SerializedName("likes")
	public ArrayList likes;
	

}
