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

import java.util.TreeSet;

import com.brainlearnin.kanji.KanjiQuiz.QuizGlyphStat;

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

public class IncorrectAnswerListAdapter extends ArrayAdapter<QuizGlyphStat> implements OnClickListener, OnCreateContextMenuListener
{
	private java.util.List<QuizGlyphStat> kanjis;
	private Context context;
	TreeSet<String> selectionSet;
	boolean singleSelect;
	static CheckBox lastChecked = null;
	static int lastSelectionKanji = 0; 

	public IncorrectAnswerListAdapter(Context context, int textViewResourceId, java.util.List<QuizGlyphStat> kanjis_, TreeSet<String> selectionSet_, boolean singleSelect_) 
	{		
		super(context, textViewResourceId, kanjis_);
		this.context = context;
		this.kanjis = kanjis_;		
		selectionSet = selectionSet_;
		singleSelect = singleSelect_;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.select_kanji_item, null);
		}//if
		
		QuizGlyphStat itemIndex = this.kanjis.get(position);

		TextView kanjiTextView = (TextView) v.findViewById(R.id.select_kanji_item_kanji);
		
		TextView indexTextView = (TextView) v.findViewById(R.id.select_kanji_item_index);
		CheckBox bCheck = (CheckBox) v.findViewById(R.id.select_kanji_item_check);

		KanjiQuiz.GlyphStat glyph = itemIndex.glyphStat;			
		
		bCheck.setTag(itemIndex);

		if (selectionSet.contains(glyph.glyph)) {
			bCheck.setChecked(true);
		} else {
			bCheck.setChecked(false);
		}//if

		bCheck.setOnClickListener(this);		

		String glyphText = "";
		switch (itemIndex.questionMode) {
			 case MainQuiz.QuizMode_On:
	             glyphText = glyph.glyph + " (O)";
	             break;
	             
		     case MainQuiz.QuizMode_Kun:
		    	 glyphText = glyph.glyph + " (K)";
	             break;
		             
		     case MainQuiz.QuizMode_Meanings:
		    	 glyphText = glyph.glyph + " (M)";
	             break;
		}//switch
		
		kanjiTextView.setText(glyphText);
		indexTextView.setText(Integer.toString(glyph.index));
		
		v.setOnCreateContextMenuListener(this);
		
		return (v);
	}
	
	@Override
	public void onClick(View v) 
	{
		CheckBox cBox = (CheckBox) v;
		QuizGlyphStat glyph = (QuizGlyphStat)cBox.getTag();
		
		if (singleSelect == true) {
			if ((lastChecked != null) && (lastChecked != v)) {
				lastChecked.setChecked(false);
			}//if
			
			cBox.setChecked(true);
		}//if
		
		lastChecked = cBox;
		lastSelectionKanji = glyph.glyphStat.index;
		
		if (cBox.isChecked()) {
			selectionSet.add(glyph.glyphStat.glyph);
		} else {
			if (selectionSet.contains(glyph.glyphStat.glyph)) {
				selectionSet.remove(glyph.glyphStat.glyph);
			}//if
		}//if
	}//onClick

	public void ClearSelections() 
	{
		selectionSet.clear();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		
	}
}//IncorrectAnswerListAdapter
