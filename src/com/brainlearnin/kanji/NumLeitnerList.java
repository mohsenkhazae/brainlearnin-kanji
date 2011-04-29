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

import java.util.Iterator;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class NumLeitnerList extends Activity
{	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.num_leitner_list);
        
        java.util.HashMap<Integer, KanjiQuiz.GlyphStat> glyphs = new java.util.HashMap<Integer, KanjiQuiz.GlyphStat>();
        java.util.HashMap<String, KanjiQuiz.GlyphStat> glyphsMap = new java.util.HashMap<String, KanjiQuiz.GlyphStat>();
        Iterator<Entry<Integer, KanjiQuiz.GlyphStat>> iter = KanjiQuiz.currentQuiz.glyphs.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Integer, KanjiQuiz.GlyphStat> entry = iter.next();

            glyphsMap.put(entry.getValue().glyph, entry.getValue());
        }//while

        for (String glyphStr : KanjiQuiz.currentQuiz.quizPreferences.testSet) {
            if (glyphsMap.containsKey(glyphStr) == true) {
            	KanjiQuiz.GlyphStat curGlyph = glyphsMap.get(glyphStr);
            	glyphs.put(curGlyph.index, curGlyph);
            }//if
        }//for
        
        TextView label = (TextView)findViewById(R.id.val_num_due_on_questions_test_list);
        setNumPending(glyphs, MainQuiz.QuizMode_On, label, 0);
        
        label = (TextView)findViewById(R.id.val_num_due_kun_questions_test_list);
        setNumPending(glyphs, MainQuiz.QuizMode_Kun, label, 0);
        
        label = (TextView)findViewById(R.id.val_num_due_meanings_questions_test_list);
        setNumPending(glyphs, MainQuiz.QuizMode_Meanings, label, 0);
        
        label = (TextView)findViewById(R.id.val_num_due_on_questions_test_list_tomorrow);
        setNumPending(glyphs, MainQuiz.QuizMode_On, label, 1);
        
        label = (TextView)findViewById(R.id.val_num_due_kun_questions_test_list_tomorrow);
        setNumPending(glyphs, MainQuiz.QuizMode_Kun, label, 1);
        
        label = (TextView)findViewById(R.id.val_num_due_meanings_questions_test_list_tomorrow);
        setNumPending(glyphs, MainQuiz.QuizMode_Meanings, label, 1);
        
        glyphs = KanjiQuiz.currentQuiz.glyphs;
        
        label = (TextView)findViewById(R.id.val_num_due_on_questions_all_list);
        setNumPending(glyphs, MainQuiz.QuizMode_On, label, 0);
        
        label = (TextView)findViewById(R.id.val_num_due_kun_questions_all_list);
        setNumPending(glyphs, MainQuiz.QuizMode_Kun, label, 0);
        
        label = (TextView)findViewById(R.id.val_num_due_meanings_questions_all_list);
        setNumPending(glyphs, MainQuiz.QuizMode_Meanings, label, 0);
    }//onCreate

	private void setNumPending(java.util.HashMap<Integer, KanjiQuiz.GlyphStat> glyphs, int quizMode, TextView label, int offset)
	{
		int count = 0;
		
        Iterator<Entry<Integer, KanjiQuiz.GlyphStat>> iter = glyphs.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Integer, KanjiQuiz.GlyphStat> entry = iter.next();

            switch (quizMode) {
            	case MainQuiz.QuizMode_On:
            		if (MainQuiz.isExpired(entry.getValue().onGrade, entry.getValue().lastOnAnswer, offset) == true) {
            			count++;
            		}//if
            		break;
            		
            	case MainQuiz.QuizMode_Kun:
            		if (MainQuiz.isExpired(entry.getValue().kunGrade, entry.getValue().lastKunAnswer, offset) == true) {
            			count++;
            		}//if
            		break;
            		
            	case MainQuiz.QuizMode_Meanings:
            		if (MainQuiz.isExpired(entry.getValue().meaningsGrade, entry.getValue().lastMeaningAnswer, offset) == true) {
            			count++;
            		}//if
            		break;
            }//switch
            
            label.setText(Integer.toString(count));
        }//while

	}//setNumPending
	
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
                aboutIntent.putExtra("titleRes", R.string.num_leitner_list_title);
                aboutIntent.putExtra("stringVal", R.string.num_leitner_list_help_text);
        
                startActivity(aboutIntent);
                break;
            }
            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    
	    return true;
	}//onOptionsItemSelected
}//NumLeitnerList
