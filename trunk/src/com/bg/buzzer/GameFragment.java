package com.bg.buzzer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.SaveCallback;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
	private ParseQueryAdapter<ParseObject> playerAdapter;
	private TextView textView;
	private ParseObject lastNotification;
	private TextView turnText;
    
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
        
        
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        try {
			game = query.get(getArguments().getString(ARG_GAME_ID));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        playerAdapter = new DisgnedQueryAdapter(rootView.getContext(), 
				  new ParseQueryAdapter.QueryFactory<ParseObject>() {
		    		    public ParseQuery<ParseObject> create() {
		    		      // Here we can configure a ParseQuery to our heart's desire.
		    		    	  return game.getRelation("players").getQuery();
		    		    }
		    		    
		    		  });
        playerAdapter.setTextKey("name");
        playerAdapter.setImageKey("photo");
        playerAdapter.setPlaceholder(getResources().getDrawable(android.R.drawable.ic_menu_myplaces));
        
        
        //final ParseQueryAdapter<ParseObject> adapter = new ParseQueryAdapter<ParseObject>(rootView.getContext(), "Game");
        //adapter.setTextKey("name");
        //adapter.setImageKey("photo");
       
        textView = (TextView) rootView.findViewById(R.id.game_name_label);
        String gameStats = "Game: "+game.getString("name")+"          Round: "+game.getInt("rounds");
//        String turn= game.getString("turn");
//        if (turn!=null && Application.user != null && game.getString("turn").equals(Application.user.getObjectId())) {
//        	gameStats += "\nIt's your turn, Buzz someone\n";
//        } else {
//        	gameStats += "\nSomeone else's turn to Buzz\n";
//        }
        
        textView.setText(gameStats);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
        
        turnText = (TextView) rootView.findViewById(R.id.game_turn_label);
        setTurnText();
     
        ListView listView = (ListView) rootView.findViewById(R.id.player_list_label);
        listView.setAdapter(playerAdapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				try{
					if (game.getString("turn").equals(Application.user.getObjectId())) {
						ParseObject selectedUser = playerAdapter.getItem(position);
						if (selectedUser.getObjectId().equals(Application.user.getObjectId())) {
							wrongPlayerDialog();
						} else {
							vibrateDialog(selectedUser);
						}
					} else if (lastNotification != null && 
							lastNotification.getParseObject("to").fetch().getObjectId().equals(Application.user.getObjectId()) &&
							lastNotification.getBoolean("responded") == false) {
						ParseObject selectedUser = playerAdapter.getItem(position);
						checkGuessDialog(selectedUser);
						lastNotification.put("responded",true);
						lastNotification.saveEventually();
						changeTurn();
					} else {
						wrongTurnDialog();
					}
				} catch(ParseException e) {
					
				}
				
				
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
    
    private void setTurnText() {
    	final String text = "Turn: someone else...";
        String turn= game.getString("turn");
        if (turn!=null && Application.user != null && game.getString("turn").equals(Application.user.getObjectId())) {
        	turnText.setText("Turn: It's your turn, Buzz someone");
        	return;
        } else if (lastNotification != null && Application.user != null) {
        	lastNotification.getParseObject("to").fetchInBackground(new GetCallback<ParseObject>() {

				@Override
				public void done(ParseObject to, ParseException e) {
					
					if (to.getObjectId().equals(Application.user.getObjectId()) 
							&& lastNotification.getBoolean("responded") == false ) {
						turnText.setText("Turn: Guess who buzzed you!");
					}
					
				}
			});
        }
        turnText.setText(text);
        	
	}

	protected void wrongPlayerDialog() {
		new AlertDialog.Builder(getActivity())
		.setMessage("Can't buzz yourself...")
		.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) { 
		        // do nothing
		    }
		 })
		.setIcon(android.R.drawable.ic_dialog_alert)
		.show();
	}

	protected void changeTurn() {
    	
		int nextPlayerIndex = new Random().nextInt(playerAdapter.getCount());
		final ParseObject nextPlayer = playerAdapter.getItem(nextPlayerIndex);
		game.put("turn", nextPlayer.getObjectId());
		game.increment("rounds");
		game.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				String message = game.getString("name") + ": yoohoo - you are up next, Buzz the person you like the most";
				sendVibrateNotification(nextPlayer, message);
				setTurnText();
			}
		});
		

		
	}

	protected void checkGuessDialog(ParseObject selectedUser) {
		
    	String message = "Error";
    	ParseObject buzzer;
		try {
			buzzer = lastNotification.getParseObject("from").fetch();
	    	boolean check = buzzer.getObjectId().equals(selectedUser.getObjectId());
	    	message = buzzer.getString("name")+" buzzed you... \n"; 
	    	if (check) {
	    		message += "You guessed right!";
	    	} else {
	    		message += "You guessed wrong!";
	    	}
		} catch (ParseException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	
    	new AlertDialog.Builder(getActivity())
		.setMessage(message)
		.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) { 
		        // do nothing
		    }
		 })
		.setIcon(android.R.drawable.ic_dialog_alert)
		.show();
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
		                	} 
	                		handleNotification(Application.user, selectedUser, game, messageString);
	                		//sendVibrateNotification(Application.user,selectedUser,messageString);
		                	Toast.makeText(context, "Buzz sent to "+selectedUserName+"!", Toast.LENGTH_SHORT).show();
		                	
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
			playerAdapter.loadObjects();
			Toast.makeText(getActivity(), "You joined "+game.getString("name"),Toast.LENGTH_SHORT).show();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	    }

	/**
	 * 
	 */
	protected void sendVibrateNotification(ParseObject userTo, String message) {
		
		String installationId = userTo.getString("installationId");
		// Create our Installation query
		ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
		pushQuery.whereEqualTo("installationId", installationId);
		
		// Send push notification to query
		ParsePush push = new ParsePush();
		push.setQuery(pushQuery); // Set our Installation query
		push.setMessage(message);
		push.sendInBackground();
	}
	
	protected void sendNotificationAll(ParseObject userFrom, 
			ParseObject userTo, ParseObject game) {

		String message = game.getString("name")+": "+userFrom.getString("name")+" Buzzed "+userTo.getString("name");
		// Create our Installation query
		//ParseQuery<ParseObject> query = game.getRelation("players").getQuery().whereNotEqualTo("objectId", userTo.getObjectId());
		//query = query.whereNotEqualTo("objectId", userFrom.getObjectId());
		LinkedList<String> objects = new LinkedList<String>();
		objects.add(userTo.getObjectId()); objects.add(userFrom.getObjectId());
		//query = query.whereNotContainedIn("objectId", objects);
		ParseQuery<ParseObject> query = game.getRelation("players").getQuery().whereNotContainedIn("objectId", objects);
		//ParseQuery<ParseObject> pushQuery1 = ParseQuery.getQuery("AppUser");
		ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
		pushQuery.whereMatchesKeyInQuery("installationId", "installationId", query);
		//pushQuery.whereEqualTo("installationId", installationId);
		List<ParseObject> res;
		try {
			res = query.find();
			Log.e("bla","names: list size"+res.size());
			for (ParseObject parseObject : res) {
				Log.e("bla","names: "+parseObject.getString("name"));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Send push notification to query
		ParsePush push = new ParsePush();
		push.setQuery(pushQuery); // Set our Installation query
		
		push.setMessage(message);
		push.sendInBackground();
	}
	
	protected ParseObject addNotification(ParseObject userFrom, 
			ParseObject userTo, final ParseObject game) {
		
		
		final ParseObject notification = new ParseObject("Notifications");
		notification.put("from", userFrom);
		notification.put("to", userTo);
		notification.put("game", game);
		notification.put("type", "buzz");
		notification.put("responded", false);
		notification.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				game.put("lastNotification", notification);
				game.saveInBackground();
				
			}
		});
		
		
		Log.e("bla","notification sent");
		return notification;
	}
	
	protected void handleNotification(ParseObject userFrom, 
			ParseObject userTo, ParseObject game, String message){
		
		addNotification(userFrom, userTo, game);
		sendVibrateNotification(userTo,"Buzz! Buzz!: "+ message);
		sendNotificationAll(userFrom, userTo, game);
	}
	
	protected void updateData(final ParseObject game) {
		this.game = game;
		textView.setText("Game: "+game.getString("name")+"          Round: "+game.getInt("rounds"));
		playerAdapter.loadObjects();

		if (game.getParseObject("lastNotification") != null) {
			game.getParseObject("lastNotification").fetchInBackground(new GetCallback<ParseObject>() {
	
				@Override
				public void done(ParseObject notification, ParseException arg1) {
					lastNotification = notification;
//					notification.getParseObject("to").fetchInBackground(new GetCallback<ParseObject>() {
//
//						@Override
//						public void done(ParseObject object, ParseException e) {
//							String gameStats = "Game: "+game.getString("name")+"     Rounds: "+game.getInt("rounds");
//							if (object.getObjectId().equals(Application.user)) {
//								gameStats += "\nGuess who buzzed you\n";
//							}
//							textView.setText(gameStats);
//							
//						}
//					});
					setTurnText();
				}
			});
		}
	}

	private void wrongTurnDialog() {
		new AlertDialog.Builder(getActivity())
		.setMessage("Not your turn yet..")
		.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) { 
		        // do nothing
		    }
		 })
		.setIcon(android.R.drawable.ic_dialog_alert)
		.show();
	}
	
	

	
}
