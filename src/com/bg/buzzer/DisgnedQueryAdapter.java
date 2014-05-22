package com.bg.buzzer;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

public class DisgnedQueryAdapter extends ParseQueryAdapter<ParseObject> {

	public DisgnedQueryAdapter(
			Context context,
			com.parse.ParseQueryAdapter.QueryFactory<ParseObject> queryFactory) {
		super(context, queryFactory);
	}
	
	public DisgnedQueryAdapter(Context context, String string) {
		super(context, string);
	}

	@Override
	public View getItemView(ParseObject object, View v, ViewGroup parent) {
	  v = super.getItemView(object, v, parent);
	  TextView text = (TextView) v.findViewById(android.R.id.text1);
	  text.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
	  text.setTextColor(Color.parseColor("#ff33b5e5"));
	  text.setPadding(30, 20, 0, 0);
	  
	  ImageView img = (ImageView) v.findViewById(android.R.id.icon);
	  LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
	  img.setLayoutParams(layoutParams);
	  img.setPadding(3, 3, 3, 3);
	  return v;
	}
	

}
