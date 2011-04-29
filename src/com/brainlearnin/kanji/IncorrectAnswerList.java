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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

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

public class IncorrectAnswerList extends ListActivity 
{	
	android.widget.ListView tableLayout = null;
	int curOffset;
	int selectedItemIndex;
	java.util.List<QuizGlyphStat> glyphs;	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incorrect_answer_list);
        
        curOffset = 0;
        
        tableLayout = this.getListView();
        
        fillGlyphList();
        doFillGrid();        
        
        Button markCorrectButton = (Button)findViewById(R.id.incorrect_answer_mark_correct_button);
        markCorrectButton.setOnClickListener(markCorrectButtonClick);
        
        Button editButton = (Button)findViewById(R.id.incorrect_answer_edit_button);
        editButton.setOnClickListener(editButtonClick);
        
        Button prevButton = (Button)findViewById(R.id.incorrect_answer_prev_button);
        prevButton.setOnClickListener(prevButtonClick);
        
        Button nextButton = (Button)findViewById(R.id.incorrect_answer_next_button);
        nextButton.setOnClickListener(nextButtonClick);
        
        selectedItemIndex = 0;
        tableLayout.setOnItemClickListener(listViewClickListener);
    }//onCreate
    
    private ListView.OnItemClickListener listViewClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                selectedItemIndex = myItemInt;            
          }
    };//listViewClickListener
    
    private void fillGlyphList()
    {
    	glyphs = new java.util.ArrayList<QuizGlyphStat>();
    	
    	for (QuizGlyphStat glyphStat : KanjiQuiz.currentQuiz.currentQuizGlyphsOrig) {
    		if (glyphStat.attempted != glyphStat.correctAnswer) {
    			glyphs.add(glyphStat);
    		}//if
    	}//for
    	
    	Comparator<? super QuizGlyphStat> comparator = new GlyphListComparator();
		Collections.sort(glyphs, comparator);
    }//fillGlyphList
    
    private static class GlyphListComparator implements Comparator<QuizGlyphStat>
    {
		@Override
		public int compare(QuizGlyphStat object1, QuizGlyphStat object2) {
			if (object1.glyphStat.glyph != object2.glyphStat.glyph) {
				return object1.glyphStat.glyph.compareTo(object2.glyphStat.glyph);
			}//if
			
			if (object1.questionMode < object2.questionMode) {
				return -1;
			}//if
			
			if (object1.questionMode > object2.questionMode) {
				return 1;
			}//if
			
			if (object1.questionMode == object2.questionMode) {
				return 0;
			}//if
			
			return 0;
		}//compare
    }//GlyphListComparator
    
    private void cheatOnGrade(QuizGlyphStat glyphStat)
    {
    	glyphStat.attempted = glyphStat.correctAnswer;
    	
    	switch (glyphStat.questionMode) {
			case MainQuiz.QuizMode_On:
				glyphStat.glyphStat.onGrade = glyphStat.originalGrade + 1;
				if (KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.size() <= glyphStat.glyphStat.onGrade) {
					glyphStat.glyphStat.onGrade = KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.size() - 1;
				}//if							
				
				glyphStat.glyphStat.lastOnAnswer = Calendar.getInstance().getTime();
				glyphStat.glyphStat.lastOnAnswer.setHours(0);
				glyphStat.glyphStat.lastOnAnswer.setHours(0);
				glyphStat.glyphStat.lastOnAnswer.setHours(0);							
				break;
				
			case MainQuiz.QuizMode_Kun:
				glyphStat.glyphStat.kunGrade = glyphStat.originalGrade + 1;
				if (KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.size() <= glyphStat.glyphStat.kunGrade) {
					glyphStat.glyphStat.kunGrade = KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.size() - 1;
				}//if							
				
				glyphStat.glyphStat.lastKunAnswer = Calendar.getInstance().getTime();
				glyphStat.glyphStat.lastKunAnswer.setHours(0);
				glyphStat.glyphStat.lastKunAnswer.setHours(0);
				glyphStat.glyphStat.lastKunAnswer.setHours(0);
				break;
				
			case MainQuiz.QuizMode_Meanings:
				glyphStat.glyphStat.meaningsGrade = glyphStat.originalGrade + 1;
				if (KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.size() <= glyphStat.glyphStat.meaningsGrade) {
					glyphStat.glyphStat.meaningsGrade = KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.size() - 1;
				}//if							
				
				glyphStat.glyphStat.lastMeaningAnswer = Calendar.getInstance().getTime();
				glyphStat.glyphStat.lastMeaningAnswer.setHours(0);
				glyphStat.glyphStat.lastMeaningAnswer.setHours(0);
				glyphStat.glyphStat.lastMeaningAnswer.setHours(0);
				break;
		}//switch
    }//cheatOnGrade
    
    private View.OnClickListener markCorrectButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	int itemIndex = IncorrectAnswerListAdapter.lastSelectionKanji;
        	for (QuizGlyphStat glyphStat : glyphs) {
        		if (glyphStat.glyphStat.index == itemIndex) {
        			cheatOnGrade(glyphStat);        			
        			
        			glyphs.remove(glyphStat);
        			        			
        	        doFillGrid();  
        			break;
        		}//if
        	}//for
        }
    };//markCorrectButtonClick
    
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
    
    private View.OnClickListener editButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	int itemIndex = IncorrectAnswerListAdapter.lastSelectionKanji;
        	
        	Intent editKanjiIntent = new Intent(getBaseContext(), EditKanji.class);	        	
        	editKanjiIntent.putExtra("kanji", itemIndex);
        	editKanjiIntent.putExtra("doingEdit", true);
        	startActivityForResult(editKanjiIntent, 0);
        }
    };//clearButtonClick
    
    
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
    	  	    	
    	ArrayList<QuizGlyphStat> kanjiList = new ArrayList<QuizGlyphStat>();
    	
    	for (int kanji = offset; kanji < endKanji; ++kanji) {
    		QuizGlyphStat curGlyph = glyphs.get(kanji);
    		kanjiList.add(curGlyph);
    	}//for
    	
    	tableLayout.setCacheColorHint(0);

  	    IncorrectAnswerListAdapter adapter = new IncorrectAnswerListAdapter(this, R.layout.select_kanji_item, kanjiList, selectedKanjiSet, true);
    	tableLayout.setAdapter(adapter);
    	tableLayout.setItemsCanFocus(false);
    	tableLayout.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }//fillGrid
    
    @Override
    public void onBackPressed() 
    {
        KanjiQuiz.currentQuiz.exportQuiz(KanjiQuiz.currentQuiz.quizFile, this);

        super.onBackPressed();
    }//onBackPressed

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
                aboutIntent.putExtra("titleRes", R.string.incorrect_answer_list_title);
                aboutIntent.putExtra("stringVal", R.string.incorrect_answer_list_help_text);
        
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


}//IncorrectAnswerList
