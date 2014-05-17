package com.bg.buzzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author keysar
 *
 */
public class NotificationsFragment extends Fragment{


    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

	private LayoutInflater inflater;
    
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NotificationsFragment newInstance() {
    	NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    public NotificationsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Notifications");

        final ParseQueryAdapter<ParseObject> notificationsAdapter =
        		  new NotificationsAdapter(rootView.getContext(), 
        				  new ParseQueryAdapter.QueryFactory<ParseObject>() {
			        		    public ParseQuery<ParseObject> create() {
			        		      // Here we can configure a ParseQuery to our heart's desire.
			        		    	ArrayList<ParseQuery<ParseObject>> orQueryList = new ArrayList<ParseQuery<ParseObject>>();
			        		    	orQueryList.add(new ParseQuery<ParseObject>("Notifications").whereEqualTo("from", Application.user));
			        		    	orQueryList.add(new ParseQuery<ParseObject>("Notifications").whereEqualTo("to", Application.user));
			        		        return ParseQuery.or(orQueryList);
			        		    	//return new ParseQuery<ParseObject>("Notifications");
			        		    }
			        		  });
        //notificationsAdapter.setTextKey("game");
        //notificationsAdapter.
        
        
        //final ParseQueryAdapter<ParseObject> adapter = new ParseQueryAdapter<ParseObject>(rootView.getContext(), "Game");
        //adapter.setTextKey("name");
        //adapter.setImageKey("photo");
       
        
        ListView listView = (ListView) rootView.findViewById(R.id.notification_list);
        listView.setAdapter(notificationsAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				
				try {
					ParseObject game = notificationsAdapter.getItem(position).getParseObject("game").fetchIfNeeded();
					MainActivity main = (MainActivity) getActivity();
					main.updateCurrentGame(game);
					main.mViewPager.setCurrentItem(1);
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
        
        
 
        return rootView;
    }

	
    public class NotificationsAdapter extends ParseQueryAdapter<ParseObject> {

		public NotificationsAdapter(
				Context context,
				com.parse.ParseQueryAdapter.QueryFactory<ParseObject> queryFactory) {
			super(context, queryFactory);
			
		}
		
		@Override
		public View getItemView(ParseObject object, View v, ViewGroup parent) {
		  if (v == null) {
		    v = View.inflate(getContext(), R.layout.notification_list_item, null);
		  }
		 
		  // Take advantage of ParseQueryAdapter's getItemView logic for
		  // populating the main TextView/ImageView.
		  // The IDs in your custom layout must match what ParseQueryAdapter expects
		  // if it will be populating a TextView or ImageView for you.
		  super.getItemView(object, v, parent);
		  
		  ParseImageView image = (ParseImageView) v.findViewById(R.id.icon);
		  ParseFile photoFile = null;
		  if (photoFile != null) {
		      image.setParseFile(photoFile);
		      image.loadInBackground(new GetDataCallback() {
			      @Override
	              public void done(byte[] data, ParseException e) {
			                // nothing to do
			            }
			      });
		  }
		 
		  // Do additional configuration before returning the View.
		  TextView descriptionView = (TextView) v.findViewById(R.id.text1);
		  try {
			String text = "";
			String gameName = object.getParseObject("game").fetch().getString("name");
			ParseObject playerFromObj = object.getParseObject("from").fetch();
			String playerFrom = playerFromObj.getString("name");
			ParseObject playerToObj = object.getParseObject("to").fetch(); 
			String playerTo = playerToObj.getString("name");
			if (playerToObj.getObjectId().equals(Application.user.getObjectId())) {
				text = gameName+": "+"Someone buzzed you!";
			} else {
				text = gameName+": "+playerFrom+" buzzed "+playerTo;
			}
			Log.e("bla","text = "+text);
			descriptionView.setText(text);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		  return v;
		}
    	
    }
}
