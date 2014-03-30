package com.jayyyR.gifjam;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class ProfileItem {
	
	@SerializedName("username")
	public String username;
	
	@SerializedName("bio")
	public String bio;
	
	@SerializedName("is_following")
	public boolean is_following;
	
	@SerializedName("profile_gif_url")
	public String profile_gif_url;


}
