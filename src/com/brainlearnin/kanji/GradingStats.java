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
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Map.Entry;

import com.brainlearnin.kanji.KanjiQuiz.GlyphStat;
import com.brainlearnin.kanji.KanjiQuiz.QuizGlyphStat;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ListView;

public class GradingStats extends ListActivity 
{	
	android.widget.ListView tableLayout = null;
	int curOffset;
	int selectedItemIndex;
	boolean showFullKanjiList;
	java.util.ArrayList<GlyphStat> glyphs;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grading_stats);
        
        curOffset = 0;
        
        tableLayout = this.getListView();
        
        Bundle bundle = this.getIntent().getExtras();
       	showFullKanjiList = bundle.getBoolean("showFullKanjiList");        	
            
       	glyphs = new java.util.ArrayList<GlyphStat>();
       	if (showFullKanjiList == true) {
       		Iterator<Entry<Integer, KanjiQuiz.GlyphStat>> iter = KanjiQuiz.currentQuiz.glyphs.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<Integer, KanjiQuiz.GlyphStat> entry = iter.next();

                glyphs.add(entry.getValue());
            }//while
       	} else {
        	for (QuizGlyphStat glyphStat : KanjiQuiz.currentQuiz.currentQuizGlyphsOrig) {
       			glyphs.add(glyphStat.glyphStat);
        	}//for
       	}//if
       	
        doFillGrid();        
                
        Button prevButton = (Button)findViewById(R.id.select_kanji_prev_button);
        prevButton.setOnClickListener(prevButtonClick);
        
        Button nextButton = (Button)findViewById(R.id.select_kanji_next_button);
        nextButton.setOnClickListener(nextButtonClick);
        
        selectedItemIndex = 0;
        tableLayout.setOnItemClickListener(listViewClickListener);
    }//onCreate
    
    private ListView.OnItemClickListener listViewClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                selectedItemIndex = myItemInt;            
          }
    };//listViewClickListener
    
    private View.OnClickListener prevButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	curOffset -= 20;
        	if (curOffset < 0) {
        		curOffset = 0;
        	}//if
        	
        	doFillGrid();
        }
    };//prevButtonClick
    
    private View.OnClickListener nextButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	curOffset += 20;
        	if (curOffset >= glyphs.size()) {
        		curOffset = glyphs.size() - 20;
        	}//if
        	
        	if (curOffset < 0) {
        		curOffset = 0;
        	}//if
        	
        	doFillGrid();
        }
    };//nextButtonClick
        
    private void doFillGrid()
    {
    	TreeSet<String> selectedKanjiSet = new TreeSet<String>();
    	fillGrid(selectedKanjiSet, curOffset);
    	registerForContextMenu(getListView());
    }//doFillGrid
    
    private void fillGrid(TreeSet<String> selectedKanjiSet, int offset)
    {
    	int endKanji = offset + 20;
    	
    	if (glyphs.size() >= 20) {
	    	if (endKanji >= glyphs.size()) {
	    		offset = glyphs.size() - 20;
	    		curOffset = offset;
	    		endKanji = glyphs.size();
	    	}//if
    	} else {
    		endKanji = glyphs.size();
    	}//if
    	  	   	
    	ArrayList<GlyphStat> kanjiList = new ArrayList<GlyphStat>();
    	
    	for (int kanji = offset; kanji < endKanji; ++kanji) {
    		GlyphStat curGlyph = glyphs.get(kanji);
    		kanjiList.add(curGlyph);
    	}//for
    	
    	tableLayout.setCacheColorHint(0);

  	    GradingStatsListAdapter adapter = new GradingStatsListAdapter(this, R.layout.grading_stats_item, kanjiList);
    	tableLayout.setAdapter(adapter);
    	tableLayout.setItemsCanFocus(false);
    	tableLayout.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }//fillGrid
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.preferences_menu, menu);
	    return true;
	}//onCreateOptionsMenu
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_about:
		        {
		        	Intent aboutIntent = new Intent(getBaseContext(), About.class);
		        	//aboutIntent.setClassName("com.brainlearnin.kanji", "com.brainlearnin.kanji.About");
		        	startActivity(aboutIntent);
		        }
	            break;
	            
	        case R.id.menu_help:
            {
                    Intent aboutIntent = new Intent(getBaseContext(), About.class);
                    aboutIntent.putExtra("titleRes", R.string.grading_stats_title);
                    aboutIntent.putExtra("stringVal", R.string.grading_stats_help_text);
            
                    startActivity(aboutIntent);
                    break;
            }
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    
	    return true;
	}//onOptionsItemSelected
    
    @Override 
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {     
    	super.onActivityResult(requestCode, resultCode, data);
    	doFillGrid();
    }//onActivityResult


}//GradingStats
