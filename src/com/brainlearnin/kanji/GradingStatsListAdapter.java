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

import com.brainlearnin.kanji.KanjiQuiz.GlyphStat;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GradingStatsListAdapter extends ArrayAdapter<GlyphStat> implements OnClickListener, OnCreateContextMenuListener
{
	private java.util.List<GlyphStat> kanjis;	
	private Context context;

	public GradingStatsListAdapter(Context context, int textViewResourceId, java.util.List<GlyphStat> kanjis_) 
	{		
		super(context, textViewResourceId, kanjis_);
		this.context = context;
		this.kanjis = kanjis_;		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.grading_stats_item, null);
		}//if
		
		TextView kanjiTextView = (TextView) v.findViewById(R.id.select_kanji_item_kanji);		
		TextView indexTextView = (TextView) v.findViewById(R.id.select_kanji_item_index);		

		KanjiQuiz.GlyphStat glyph = this.kanjis.get(position);					

		String glyphGradeText = "";
		glyphGradeText = Integer.toString(glyph.onGrade) + " / " + Integer.toString(glyph.kunGrade) + " / " + Integer.toString(glyph.meaningsGrade);
		
		kanjiTextView.setText(glyph.glyph);
		indexTextView.setText(glyphGradeText);
		
		v.setOnCreateContextMenuListener(this);
		
		return (v);
	}
		
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}//GradingStatsListAdapter
