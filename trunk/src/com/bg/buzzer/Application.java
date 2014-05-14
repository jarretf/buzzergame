package com.bg.buzzer;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

public class Application extends android.app.Application {

  public static ParseObject user = null;

public Application() {
  }

  @Override
  public void onCreate() {
    super.onCreate();

	// Initialize the Parse SDK.
    Parse.initialize(this, "dygXVIIqFqWVnj1sBNuL1toJ4EbyhX37GNXTwfAH", "XLmSoXnyk2hEPOjiEG335Z7uJfElDhgiUm9cpA3o");

	// Specify an Activity to handle all pushes by default.
	PushService.setDefaultPushCallback(this, BuzzedActivity.class);
	//PushService.setDefaultPushCallback(this, MainActivity.class);
  }
}
