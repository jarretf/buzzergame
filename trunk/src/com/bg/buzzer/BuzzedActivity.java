package com.bg.buzzer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BuzzedActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	Intent intent  = new Intent(this, MainActivity.class);
    	intent.putExtra("TAB", 2);
    	startActivity(intent);
    	finish();
    	
    	
    }

}
