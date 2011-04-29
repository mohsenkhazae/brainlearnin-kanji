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
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class SessionOpened extends Activity
{
	private Handler handler = new Handler();
	private static ProgressDialog dialog;
	private String file;

	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_opened);
        
        SharedPreferences settings = getSharedPreferences("BrainLearninKanji", MODE_PRIVATE);
        String name = settings.getString("lastSessionName", "ERROR!!!");
        file = settings.getString("lastSessionFile", "ERROR!!!");
        
        this.setTitle(name);
        
        dialog = ProgressDialog.show(this, "", getString(R.string.session_opened_wait_message), true);
        
        Thread t = new Thread() {
            public void run() {
            	KanjiQuiz.currentQuiz = new KanjiQuiz();
            	KanjiQuiz.currentQuiz.importQuiz(file, SessionOpened.this);
            	
                handler.post(new Runnable() {
                    public void run() {
                        dialog.dismiss();                        
                    };
                });
            }
        };

        t.start();
        
        Button flashcardButton = (Button)findViewById(R.id.session_opened_flashcard_button);
        flashcardButton.setOnClickListener(flashcardButtonClick);
        
        Button onButton = (Button)findViewById(R.id.session_opened_on_button);
        onButton.setOnClickListener(onButtonClick);
        
        Button kunButton = (Button)findViewById(R.id.session_opened_kun_button);
        kunButton.setOnClickListener(kunButtonClick);
        
        Button onKunButton = (Button)findViewById(R.id.session_opened_onkun_button);
        onKunButton.setOnClickListener(onKunButtonClick);
        
        Button meaningsButton = (Button)findViewById(R.id.session_opened_meanings_button);
        meaningsButton.setOnClickListener(meaningsButtonClick);
        
        CheckBox enableGradingCheckbox = (CheckBox)findViewById(R.id.session_opened_grading_check);
        enableGradingCheckbox.setChecked(true);
    }
	
	private View.OnClickListener flashcardButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	Intent flashcardSessionIntent = new Intent(getBaseContext(), FlashCard.class);
        	startActivity(flashcardSessionIntent);
        }
    };//flashcardButtonClick
    
    private View.OnClickListener onButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	CheckBox enableGradingCheckbox = (CheckBox)findViewById(R.id.session_opened_grading_check);
        	CheckBox quickQuizCheckbox = (CheckBox)findViewById(R.id.session_opened_quick_quiz_check);
        	
        	Intent onSessionIntent = new Intent(getBaseContext(), MainQuiz.class);
        	onSessionIntent.putExtra("enableGrading", enableGradingCheckbox.isChecked());
        	onSessionIntent.putExtra("quickQuiz", quickQuizCheckbox.isChecked());
        	onSessionIntent.putExtra("quizMode", MainQuiz.QuizMode_On);
        	startActivity(onSessionIntent);
        }
    };//onButtonClick
    
    private View.OnClickListener kunButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	CheckBox enableGradingCheckbox = (CheckBox)findViewById(R.id.session_opened_grading_check);
        	CheckBox quickQuizCheckbox = (CheckBox)findViewById(R.id.session_opened_quick_quiz_check);
        	
        	Intent kunSessionIntent = new Intent(getBaseContext(), MainQuiz.class);
        	kunSessionIntent.putExtra("enableGrading", enableGradingCheckbox.isChecked());
        	kunSessionIntent.putExtra("quickQuiz", quickQuizCheckbox.isChecked());
        	kunSessionIntent.putExtra("quizMode", MainQuiz.QuizMode_Kun);
        	startActivity(kunSessionIntent);
        }
    };//kunButtonClick
    
    private View.OnClickListener onKunButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	CheckBox enableGradingCheckbox = (CheckBox)findViewById(R.id.session_opened_grading_check);
        	CheckBox quickQuizCheckbox = (CheckBox)findViewById(R.id.session_opened_quick_quiz_check);
        	
        	Intent onKunSessionIntent = new Intent(getBaseContext(), MainQuiz.class);
        	onKunSessionIntent.putExtra("enableGrading", enableGradingCheckbox.isChecked());
        	onKunSessionIntent.putExtra("quickQuiz", quickQuizCheckbox.isChecked());
        	onKunSessionIntent.putExtra("quizMode", MainQuiz.QuizMode_OnKun);
        	startActivity(onKunSessionIntent);
        }
    };//onKunButtonClick
	
    private View.OnClickListener meaningsButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	CheckBox enableGradingCheckbox = (CheckBox)findViewById(R.id.session_opened_grading_check);
        	CheckBox quickQuizCheckbox = (CheckBox)findViewById(R.id.session_opened_quick_quiz_check);
        	
        	Intent meaningsSessionIntent = new Intent(getBaseContext(), MainQuiz.class);
        	meaningsSessionIntent.putExtra("quizMode", MainQuiz.QuizMode_Meanings);
        	meaningsSessionIntent.putExtra("enableGrading", enableGradingCheckbox.isChecked());
        	meaningsSessionIntent.putExtra("quickQuiz", quickQuizCheckbox.isChecked());
        	startActivity(meaningsSessionIntent);
        }
    };//onButtonClick
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.session_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent aboutIntent = new Intent(getBaseContext(), About.class);
    	//aboutIntent.setClassName("com.brainlearnin", "com.brainlearnin.About");
    	
        // Handle item selection
        switch (item.getItemId()) {
	        case R.id.menu_about:
	        	startActivity(aboutIntent);
	            return true;
	        case R.id.menu_session_preferences:
	        	doSessionPreferences();
	        	break;
	        	
	        case R.id.menu_grading_stats:
	        	doGradingStats();
	        	break;
	        	
	        case R.id.menu_num_leitners:
	        	doNumLeitnerList();
	        	break;
	        	
	        case R.id.menu_help:
            {                
                aboutIntent.putExtra("titleRes", R.string.session_opened_title);
                aboutIntent.putExtra("stringVal", R.string.session_opened_help_text);
        
                startActivity(aboutIntent);
                break;
            }
	        	
	        default:
	            return super.onOptionsItemSelected(item);
        }
        
        return true;
    }
    
    void doNumLeitnerList()
    {
    	Intent numLeitnerListIntent = new Intent();
    	
    	numLeitnerListIntent = new Intent(getBaseContext(), NumLeitnerList.class);
    	startActivity(numLeitnerListIntent);
    }//doNumLeitnerList
    
    void doGradingStats()
    {
    	Intent showGradingStatsIntent = new Intent();
	    	
    	showGradingStatsIntent = new Intent(getBaseContext(), GradingStats.class);
    	showGradingStatsIntent.putExtra("showFullKanjiList", true);
    	startActivity(showGradingStatsIntent);
    }//doGradingStats
    
    void doSessionPreferences()
    {    	
    	Intent preferencesIntent = new Intent();
    	   	    	
    	preferencesIntent = new Intent(getBaseContext(), SessionPreferences.class);
    	preferencesIntent.putExtra("quizFile", file);
    	startActivity(preferencesIntent);
    }//doGlobalPreferences
	
}
