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
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FlashCard extends Activity
{
	boolean kanjiCard = true;
	RelativeLayout kanjiCardLayout = null;
	RelativeLayout readingsCardLayout = null;
	ArrayList<KanjiQuiz.GlyphStat> glyphs;
	int curItem = 0;
	private TextView kanjiEdit = null;
	private TextView onEdit = null;
	private TextView kunEdit = null;
	private TextView meaningsEdit = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashcard);
                
        kanjiCardLayout = (RelativeLayout)findViewById(R.id.kanji_flash_kanji_layout);                
        readingsCardLayout = (RelativeLayout)findViewById(R.id.kanji_flash_meanings_layout);        
        
        Button flipButton = (Button)findViewById(R.id.kanji_flash_flip_button);
        flipButton.setOnClickListener(flipButtonClick);
        
        Button nextButton = (Button)findViewById(R.id.kanji_flash_next_button);
        nextButton.setOnClickListener(nextButtonClick);
         
        kanjiCard = true;
        kanjiCardLayout.setVisibility(View.VISIBLE);
        readingsCardLayout.setVisibility(View.GONE);
        
        kanjiEdit = (TextView)findViewById(R.id.edit_kanji_kanji_edit);
        onEdit = (TextView)findViewById(R.id.edit_kanji_on_edit);
        kunEdit = (TextView)findViewById(R.id.edit_kanji_kun_edit);
        meaningsEdit = (TextView)findViewById(R.id.edit_kanji_meanings_edit);
        
        setUpCards();        
    }//onCreate
	
	private void setUpCards()
	{
		glyphs = new ArrayList<KanjiQuiz.GlyphStat>();
		curItem = 0;
		
		KanjiQuiz curQuiz = KanjiQuiz.currentQuiz;
		Preferences quizPreferences = curQuiz.quizPreferences;
		
		java.util.HashMap<String, KanjiQuiz.GlyphStat> glyphsMap = new java.util.HashMap<String, KanjiQuiz.GlyphStat>();
		Iterator<Entry<Integer, KanjiQuiz.GlyphStat>> iter = curQuiz.glyphs.entrySet().iterator();
        while (iter.hasNext()) {
                Entry<Integer, KanjiQuiz.GlyphStat> entry = iter.next();
                
                glyphsMap.put(entry.getValue().glyph, entry.getValue());
        }//while
        
        for (String glyphStr : quizPreferences.flashcardSet) {
        	if (glyphsMap.containsKey(glyphStr) == true) {
        		glyphs.add(glyphsMap.get(glyphStr));
        	}//if
        }//for

        if (quizPreferences.doRandomFlashcardMode == true) {
        	java.util.Collections.shuffle(glyphs);
        }//if
        
        setUpNextCard();        
	}//setUpCards
	
	private void setUpNextCard()
	{
		if (glyphs.size() == 0) {
			return;
		}//if
		
		KanjiQuiz.GlyphStat nextGlyph = glyphs.get(curItem);
		
		kanjiEdit.setText(nextGlyph.glyph);
        
        String onReadings = nextGlyph.onReadings.toString();
        String kunReadings = nextGlyph.kunReadings.toString();
        String meanings = nextGlyph.meanings.toString();
        
        onReadings = onReadings.substring(1, onReadings.length() - 1);
        kunReadings = kunReadings.substring(1, kunReadings.length() - 1);
        meanings = meanings.substring(1, meanings.length() - 1);
        
        onEdit.setText(onReadings);
        kunEdit.setText(kunReadings);
        meaningsEdit.setText(meanings);
        
        kanjiCard = true;
        kanjiCardLayout.setVisibility(View.VISIBLE);
        readingsCardLayout.setVisibility(View.GONE);
		
		curItem = (curItem + 1) % glyphs.size();
	}//setUpNextCard
	
	private View.OnClickListener flipButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	toggleVisibilities();
        }
    };//flipButtonClick    
    
    private View.OnClickListener nextButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	setUpNextCard();
        }
    };//nextButtonClick  
	
	private void toggleVisibilities()
	{
		if (kanjiCard == true) {
			kanjiCard = false;
			kanjiCardLayout.setVisibility(View.GONE);
	        readingsCardLayout.setVisibility(View.VISIBLE);
		} else {
			kanjiCard = true;
			kanjiCardLayout.setVisibility(View.VISIBLE);
	        readingsCardLayout.setVisibility(View.GONE);
		}//if
				
	}//toggleVisibilities
	
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
                aboutIntent.putExtra("titleRes", R.string.flashCardTitle);
                aboutIntent.putExtra("stringVal", R.string.flashCard_help_text);
        
                startActivity(aboutIntent);
                break;
            }
            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    
	    return true;
	}//onOptionsItemSelected   
	
}//FlashCard
