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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Scoring extends Activity
{
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoring);
        
        Bundle bundle = this.getIntent().getExtras();
        int originalQuizSize = bundle.getInt("originalQuizSize");
        
        int numDueQuestionsUnasked = originalQuizSize - KanjiQuiz.currentQuiz.currentQuizGlyphsOrig.size();
        TextView numDueQuestionsUnaskedTextView = (TextView)findViewById(R.id.val_num_due_questions_not_asked);
        numDueQuestionsUnaskedTextView.setText(Integer.toString(numDueQuestionsUnasked));
        
        int numAllCorrect = 0;
        int numFirstCorrect = 0;
        int numFirstIncorrect = 0;
        
        for (KanjiQuiz.QuizGlyphStat glyphStat : KanjiQuiz.currentQuiz.currentQuizGlyphsOrig) {
        	if (glyphStat.attempted == glyphStat.correctAnswer) {
        		numAllCorrect++;
        		numFirstCorrect++;
        	} else {
        		if (glyphStat.firstAnswerCorrect == true) {
        			numFirstCorrect++;
        		} else {
        			numFirstIncorrect++;
        		}//if
        	}//if
        }//for
        
        TextView numAllCorrectTextView = (TextView)findViewById(R.id.val_nul_all_correct);
        numAllCorrectTextView.setText(Integer.toString(numAllCorrect));
        
        TextView numFirstCorrectTextView = (TextView)findViewById(R.id.val_num_first_answer_correct);
        numFirstCorrectTextView.setText(Integer.toString(numFirstCorrect));
        
        TextView numFirstIncorrectTextView = (TextView)findViewById(R.id.val_num_incorrect_answer);
        numFirstIncorrectTextView.setText(Integer.toString(numFirstIncorrect));
        
        Button showGradingStatsButton = (Button)findViewById(R.id.button_show_grading_stats);
        showGradingStatsButton.setOnClickListener(showGradingStatsButtonClick);
        
        Button showCorrectKanjiAnsweredButton = (Button)findViewById(R.id.button_show_correct_kanji_answered);
        showCorrectKanjiAnsweredButton.setOnClickListener(showCorrectKanjiAnsweredButtonClick);
        
        Button showIncorrectKanjiAnsweredButton = (Button)findViewById(R.id.button_show_incorrect_kanji_answered);
        showIncorrectKanjiAnsweredButton.setOnClickListener(showIncorrectKanjiAnsweredButtonClick);        
    }//onCreate
	
	private View.OnClickListener showGradingStatsButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	Intent showGradingStatsIntent = new Intent();
   	    	
        	showGradingStatsIntent = new Intent(getBaseContext(), GradingStats.class);
        	showGradingStatsIntent.putExtra("showFullKanjiList", false);
        	startActivity(showGradingStatsIntent);
        }//onClick
	};//showGradingStatsButtonClick
	
	private View.OnClickListener showCorrectKanjiAnsweredButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	Intent selectKanjiIntent = new Intent();
   	    	
        	selectKanjiIntent = new Intent(getBaseContext(), SelectEditKanji.class);
        	selectKanjiIntent.putExtra("showCorrectAnswerList", true);
        	startActivity(selectKanjiIntent);
        }//onClick
	};//showGradingStatsButtonClick
	
	private View.OnClickListener showIncorrectKanjiAnsweredButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	Intent incorrectAnswerListIntent = new Intent();
   	    	
        	incorrectAnswerListIntent = new Intent(getBaseContext(), IncorrectAnswerList.class);        	
        	startActivity(incorrectAnswerListIntent);
        }//onClick
	};//showGradingStatsButtonClick
       
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
		        	//aboutIntent.setClassName("com.brainlearnin", "com.brainlearnin.About");
		        	startActivity(aboutIntent);
		        }
	            break;	
	            
	        case R.id.menu_help:
            {                
            	Intent aboutIntent = new Intent(getBaseContext(), About.class);
                aboutIntent.putExtra("titleRes", R.string.scoringTitle);
                aboutIntent.putExtra("stringVal", R.string.scoring_help_text);
        
                startActivity(aboutIntent);
                break;
            }
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    
	    return true;
	}//onOptionsItemSelected
}//Scoring
