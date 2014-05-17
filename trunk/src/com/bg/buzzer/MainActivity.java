package com.bg.buzzer;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.PushService;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        
        //Parse.initialize(this, "dygXVIIqFqWVnj1sBNuL1toJ4EbyhX37GNXTwfAH", "XLmSoXnyk2hEPOjiEG335Z7uJfElDhgiUm9cpA3o");
        //PushService.setDefaultPushCallback(this, MainActivity.class);
//        ParseObject parseObject = new ParseObject("Game");
//        parseObject.put("name", "test1");
//        parseObject.saveInBackground();
//        ParseObject parseObject2 = new ParseObject("Game");
//        parseObject2.put("name", "test2");
//        parseObject2.saveInBackground();
        
        
        

        userLogin();
        mViewPager.setCurrentItem(getIntent().getIntExtra("TAB", 0),false);
//	      ParseObject parseObject = new ParseObject("AppUser");
//	      parseObject.put("name", "Guy");
//	      parseObject.put("installationId", ParseInstallation.getCurrentInstallation().getInstallationId());
//	      parseObject.saveInBackground();
    }	


    private void newUserDialog() {
    	
    	 final Context context = this;
    	 AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	 // Get the layout inflater
         LayoutInflater inflater = this.getLayoutInflater();
        
         // Inflate and set the layout for the dialog
    	 // Pass null as the parent view because its going in the dialog layout
         
         final View dialogView = inflater.inflate(R.layout.new_user_dialog, null);
    	 builder.setView(dialogView)
    	 		.setTitle("New Player ...")
    	 		.setCancelable(false)
    	 		// Add action buttons
	            .setPositiveButton("Start", new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int id) {
	                	EditText playerName = (EditText) dialogView.findViewById(R.id.new_user_text);
	                	String name = playerName.getText().toString();
	                	if (name.equals("")) {
	    					new AlertDialog.Builder(context)
	    					.setMessage("Invalid name. Please insert a valid name")
	    					.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	    				        public void onClick(DialogInterface dialog, int which) { 
	    				            // do nothing
	    				        }
	    				     })
	    				    .setIcon(android.R.drawable.ic_dialog_alert)
	    				    .show();
	                	} else {
		                	Application.user = addNewAppUser(name);
		                	Toast.makeText(context, "Player Created successfuly", Toast.LENGTH_SHORT).show();
	                	}
	                }
	            })
	            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                   
	                }
	            });      
	    builder.create().show();
    	    
    	// custom dialog
//		final Dialog dialog = new Dialog(this);
//		dialog.setContentView(R.layout.new_user_dialog);
//		dialog.setTitle("New Player ...");
//		dialog.
//		final EditText playerName = (EditText) dialog.findViewById(R.id.new_user_text);
//		Button startButton = (Button) dialog.findViewById(R.id.start_button);
//		startButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View view) {
//				String name = playerName.getText().toString();
//				if (name.equals("")) {
//					new AlertDialog.Builder(getBaseContext())
//					.setMessage("Invalid name. Please insert a valid name")
//					.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//				        public void onClick(DialogInterface dialog, int which) { 
//				            // do nothing
//				        }
//				     })
//				    .setIcon(android.R.drawable.ic_dialog_alert)
//				    .show();
//				} else {
//					addNewAppUser(name);
//				}
//				
//			}
//		});
//		
//		dialog.show();
		
	}


	protected ParseObject addNewAppUser(String name) {
		
	      ParseObject parseObject = new ParseObject("AppUser");
	      parseObject.put("name", name);
	      parseObject.put("installationId", ParseInstallation.getCurrentInstallation().getInstallationId());
	      try {
			parseObject.save();
		  } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
	      return parseObject;
	}


	private void userLogin() {
    	
		ParseQuery<ParseObject> userExistQuery = ParseQuery.getQuery("AppUser");

		userExistQuery.whereEqualTo("installationId",
				ParseInstallation.getCurrentInstallation().getInstallationId());
		List<ParseObject> res = null;
		Log.e("bla","installation id:"+ParseInstallation.getCurrentInstallation().getInstallationId());
		try {
			res = userExistQuery.find();
			if (res.isEmpty()) {
				Log.e("bla", "in empty");
	        	//open dialog for new user
	        	newUserDialog();
	        	//res = userExistQuery.find();
			} else {
				Application.user = res.get(0);
				Toast.makeText(this, "Welcome back "+res.get(0).getString("name"), Toast.LENGTH_SHORT).show();
			}
			Log.e("bla", "find "+res.size());
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
		//Application.user = res.get(0);
		
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	if (position == 0) {
        		return GamesFragment.newInstance(position+1);
        	}
        	if (position == 1) {
        		return GameFragment.newInstance("njlbGL0qb4");
        	}
        	if (position == 2) {
        		return NotificationsFragment.newInstance();
        	}
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

}
