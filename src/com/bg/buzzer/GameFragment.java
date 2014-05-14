package com.bg.buzzer;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseException;
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
public class GameFragment extends Fragment{


    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_GAME_ID = "game_id";
	private ParseObject game;
	private LayoutInflater inflater;
    
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GameFragment newInstance(String gameId) {
    	GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GAME_ID, gameId);
        fragment.setArguments(args);
        return fragment;
    }

    public GameFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        Toast.makeText(rootView.getContext(), "loading", Toast.LENGTH_SHORT).show();
        
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        try {
			game = query.get(getArguments().getString(ARG_GAME_ID));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        final ParseQueryAdapter<ParseObject> playerAdapter =
        		  new ParseQueryAdapter<ParseObject>(rootView.getContext(), 
        				  new ParseQueryAdapter.QueryFactory<ParseObject>() {
			        		    public ParseQuery<ParseObject> create() {
			        		      // Here we can configure a ParseQuery to our heart's desire.
			        		    	  return game.getRelation("players").getQuery();
			        		    }
			        		  });
        playerAdapter.setTextKey("name");
        
        
        final ParseQueryAdapter<ParseObject> adapter = new ParseQueryAdapter<ParseObject>(rootView.getContext(), "Game");
        adapter.setTextKey("name");
        //adapter.setImageKey("photo");
       
        TextView textView = (TextView) rootView.findViewById(R.id.game_name_label);
        textView.setText("Game: "+game.getString("name"));
        
        ListView listView = (ListView) rootView.findViewById(R.id.player_list_label);
        listView.setAdapter(playerAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				
				ParseObject selectedUser = playerAdapter.getItem(position);
				vibrateDialog(selectedUser);
			}

		});
        
        
        Button joinButton = (Button) rootView.findViewById(R.id.join_button);
        joinButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				joinGame(game, Application.user);
				//updateData();
			}

		});
        
        
        return rootView;
    }
    
    protected void vibrateDialog(final ParseObject selectedUser) {

    	final Context context = getActivity();
    	final String selectedUserName = selectedUser.getString("name");
	   	AlertDialog.Builder builder = new AlertDialog.Builder(context);

	    final View dialogView = inflater.inflate(R.layout.new_user_dialog, null);
	    final EditText message = (EditText) dialogView.findViewById(R.id.new_user_text);
	    message.setHint("Any Message?");
	        
	   	 builder.setView(dialogView)
	   	 		.setTitle("Buzz "+selectedUserName)
	   	 		// Add action buttons
		            .setPositiveButton("Buzz!", new DialogInterface.OnClickListener() {
		                @Override
		                public void onClick(DialogInterface dialog, int id) {
		                	
		                	String messageString = message.getText().toString();
		                	if (messageString.equals("")) {
		                		messageString = "No message";
		                	} else {
		                		sendVibrateNotification(Application.user,selectedUser,messageString);
			                	Toast.makeText(context, "Buzz sent to "+selectedUserName+"!", Toast.LENGTH_SHORT).show();
		                	}
		                }
		            })
		            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int id) {
		                   
		                }
		            });      
		    builder.create().show();
	
			
	}

	protected void joinGame(ParseObject game, ParseObject appUser) {
    	game.getRelation("players").add(appUser);
    	try {
			game.save();
			Toast.makeText(getActivity(), "You joined "+game.getString("name"),Toast.LENGTH_SHORT).show();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	    }

	/**
	 * 
	 */
	protected void sendVibrateNotification(ParseObject userFrom, ParseObject userTo, String message) {
		
		String installationId = userTo.getString("installationId");
		// Create our Installation query
		ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
		pushQuery.whereEqualTo("installationId", installationId);
		
		// Send push notification to query
		ParsePush push = new ParsePush();
		push.setQuery(pushQuery); // Set our Installation query
		push.setMessage("Buzz! Buzz!: "+message);
		push.sendInBackground();
	}
	
	protected void sendNotificationAll(ParseObject userFrom, 
			ParseObject userTo, ParseObject game) {
		
		String message = game.getString("name:")+": "+userFrom.getString("name")+" Buzzed "+userTo.getString("name");
		// Create our Installation query
		ParseQuery<ParseObject> query = game.getRelation("players").getQuery().whereNotEqualTo("objectId", userTo.getObjectId());
		query.whereNotEqualTo("objectId", userFrom.getObjectId());
		//ParseQuery<ParseObject> pushQuery1 = ParseQuery.getQuery("AppUser");
		ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
		pushQuery.whereMatchesKeyInQuery("installationId", "installationId", query);
		//pushQuery.whereEqualTo("installationId", installationId);
		
		// Send push notification to query
		ParsePush push = new ParsePush();
		push.setQuery(pushQuery); // Set our Installation query
		
		push.setMessage(message);
		push.sendInBackground();
	}
	
}
