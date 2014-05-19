package com.bg.buzzer;

import com.bg.buzzer.MainActivity.PlaceholderFragment;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author keysar
 *
 */
public class GamesFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GamesFragment newInstance(int sectionNumber) {
    	GamesFragment fragment = new GamesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

	private LayoutInflater inflater;
	private ParseQueryAdapter<ParseObject> adapter;

    public GamesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.fragment_games, container, false);
        final ViewGroup mContainer = container;
        adapter = new ParseQueryAdapter<ParseObject>(rootView.getContext(), "Game");
        adapter.setTextKey("name");
        //adapter.setImageKey("photo");
       
        ListView listView = (ListView) rootView.findViewById(R.id.games_list_label);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				MainActivity main = (MainActivity) getActivity();
				main.updateCurrentGame(adapter.getItem(position));
				main.mViewPager.setCurrentItem(1);
				
//				Toast.makeText(view.getContext(), "chosen position "+position+", chosen text = "+adapter.getItem(position).getString("name"), Toast.LENGTH_SHORT).show();
//				GameFragment gameFragment = GameFragment.newInstance(adapter.getItem(position).getString("id"));
//				Log.d("bla","got here0");
//				FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//				Log.d("bla","got here1");
//				// Replace whatever is in the fragment_container view with this fragment,
//				// and add the transaction to the back stack so the user can navigate back
//				transaction.replace(mContainer.getId(), gameFragment);
//				Log.d("bla","got here2");
//				transaction.addToBackStack(null);
//				Log.d("bla","got here3");
//				// Commit the transaction
//				transaction.commit();
//				Log.d("bla","got here4");
			}

		});
        
        Button addGameButton = (Button) rootView.findViewById(R.id.add_button);
        addGameButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addGameDialog();
				//updateData();
			}

		});

        return rootView;
    }

	protected void addGameDialog() {

    	 final Context context = getActivity();
    	 AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	 // Get the layout inflater
         //LayoutInflater inflater = context.getLayoutInflater();
        
         // Inflate and set the layout for the dialog
    	 // Pass null as the parent view because its going in the dialog layout
         
         final View dialogView = inflater.inflate(R.layout.new_user_dialog, null);
         final EditText playerName = (EditText) dialogView.findViewById(R.id.new_user_text);
         playerName.setHint("Game name");
         
    	 builder.setView(dialogView)
    	 		.setTitle("New Game ...")
    	 		// Add action buttons
	            .setPositiveButton("Create", new DialogInterface.OnClickListener() {
	                @Override
	                public void onClick(DialogInterface dialog, int id) {
	                	//EditText playerName = (EditText) dialogView.findViewById(R.id.new_user_text);
	       
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
		                	addNewGame(name);
		                	Toast.makeText(context, "Game Created successfuly", Toast.LENGTH_SHORT).show();
	                	}
	                }
	            })
	            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                   
	                }
	            });      
	    builder.create().show();
	    
	}

	protected void addNewGame(String name) {
	      ParseObject parseObject = new ParseObject("Game");
	      parseObject.put("name", name);
	      parseObject.put("rounds", 0);
	      parseObject.put("turn", Application.user.getObjectId());
	      parseObject.getRelation("players").add(Application.user);	
	      try {
			parseObject.save();
			adapter.loadObjects();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
