/*
Brain Learnin' Kanji: It ain't learnin' if it ain't Brain Learnin'!
Written by Chris Mennie (chris at chrismennie.ca or cmennie at rogers.com)

License:

Brain Learnin' Kanji -- A kanji learning tool for the Android platform
Copyright (C) 2011 Chris A. Mennie								

Brain Learnin' Kanji is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Brain Learnin' Kanji is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Brain Learnin' Kanji.  If not, see http://www.gnu.org/licenses/.
*/

package com.brainlearnin.kanji;

import java.util.ArrayList;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class EditLeitnerListAdapter extends ArrayAdapter<Integer> implements OnClickListener, OnCreateContextMenuListener
{
	private ArrayList<Integer> levels;
	private Context context;		
	static CheckBox lastChecked = null;
	static int lastSelectionKanji = 0; 

	public EditLeitnerListAdapter(Context context, int textViewResourceId, ArrayList<Integer> levels_) 
	{		
		super(context, textViewResourceId, levels_);
		this.context = context;
		this.levels = levels_;				
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.select_kanji_item, null);
		}//if
		
		int itemIndex = this.levels.get(position);

		TextView kanjiTextView = (TextView) v.findViewById(R.id.select_kanji_item_kanji);
		
		TextView indexTextView = (TextView) v.findViewById(R.id.select_kanji_item_index);
		CheckBox bCheck = (CheckBox) v.findViewById(R.id.select_kanji_item_check);
		bCheck.setTag(position);

		bCheck.setChecked(false);		

		bCheck.setOnClickListener(this);		

		kanjiTextView.setText(Integer.toString(itemIndex));
		indexTextView.setText(Integer.toString(position));
		
		v.setOnCreateContextMenuListener(this);
		
		return (v);
	}
	
	@Override
	public void onClick(View v) 
	{
		CheckBox cBox = (CheckBox) v;
		int position = (Integer) cBox.getTag();
		
		if ((lastChecked != null) && (lastChecked != v)) {
			lastChecked.setChecked(false);
		}//if
			
		cBox.setChecked(true);
		
		lastChecked = cBox;
		lastSelectionKanji = position;		
	}//onClick


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		
	}
}//EditLeitnerListAdapter
