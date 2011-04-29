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

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SessionPreferences extends PreferenceActivity
{
	private String quizFile;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.session_preferences);
            
            Bundle bundle = this.getIntent().getExtras(); 
            quizFile = bundle.getString("quizFile");
            
            //EditTextPreference pref_onKunTextSize = (EditTextPreference)findPreference("pref_onKunTextSize");
            //EditTextPreference pref_meaningsTextSize = (EditTextPreference)findPreference("pref_meaningsTextSize");
            EditTextPreference pref_maxEntriesPerSession = (EditTextPreference)findPreference("pref_maxEntriesPerSession");
            EditTextPreference pref_minQuestionLocality = (EditTextPreference)findPreference("pref_minQuestionLocality");
            EditTextPreference pref_maxQuestionLocality = (EditTextPreference)findPreference("pref_maxQuestionLocality");
            
            CheckBoxPreference pref_doRealLeitner = (CheckBoxPreference)findPreference("pref_doRealLeitner");
            CheckBoxPreference pref_showWordCycle = (CheckBoxPreference)findPreference("pref_showWordCycle");
            CheckBoxPreference pref_resetCorrectCountOnWrongAnswer = (CheckBoxPreference)findPreference("pref_resetCorrectCountOnWrongAnswer");
            CheckBoxPreference pref_doRandomFlashcardMode = (CheckBoxPreference)findPreference("pref_doRandomFlashcardMode");
                                    
            Preference pref_testKanji = findPreference("pref_testKanji");
            Preference pref_flashcardKanji = findPreference("pref_flashcardKanji");
            Preference pref_editKanji = findPreference("pref_editKanji");
            Preference pref_levels = findPreference("pref_levels");
            
            pref_testKanji.setOnPreferenceClickListener(pref_testKanji_onPreferenceClickListener);
            pref_flashcardKanji.setOnPreferenceClickListener(pref_flashcardKanji_onPreferenceClickListener);
            pref_editKanji.setOnPreferenceClickListener(pref_editKanji_onPreferenceClickListener);
            pref_levels.setOnPreferenceClickListener(pref_levels_onPreferenceClickListener);
            
            //pref_onKunTextSize.setText(Integer.toString(KanjiQuiz.currentQuiz.quizPreferences.onKunTextSize));
            //pref_meaningsTextSize.setText(Integer.toString(KanjiQuiz.currentQuiz.quizPreferences.meaningsSize));
            pref_maxEntriesPerSession.setText(Integer.toString(KanjiQuiz.currentQuiz.quizPreferences.maxEntriesPerSession));
            pref_minQuestionLocality.setText(Integer.toString(KanjiQuiz.currentQuiz.quizPreferences.minQuestionLocality));
            pref_maxQuestionLocality.setText(Integer.toString(KanjiQuiz.currentQuiz.quizPreferences.maxQuestionLocality));
            
            pref_doRealLeitner.setChecked(KanjiQuiz.currentQuiz.quizPreferences.doRealLeitner);
            pref_showWordCycle.setChecked(KanjiQuiz.currentQuiz.quizPreferences.showWordCycle);
            pref_resetCorrectCountOnWrongAnswer.setChecked(KanjiQuiz.currentQuiz.quizPreferences.resetCorrectCountOnWrongAnswer);
            pref_doRandomFlashcardMode.setChecked(KanjiQuiz.currentQuiz.quizPreferences.doRandomFlashcardMode);
            
	}//onCreate
	
	private OnPreferenceClickListener pref_testKanji_onPreferenceClickListener = new OnPreferenceClickListener() {		 
        public boolean onPreferenceClick(Preference preference) {
                //Toast.makeText(getBaseContext(), "pref_testKanji_onPreferenceClickListener", Toast.LENGTH_LONG).show();
	        	Intent selectKanjiIntent = new Intent();
	   	    	
	        	selectKanjiIntent = new Intent(getBaseContext(), SelectKanji.class);
	        	selectKanjiIntent.putExtra("selectionMode", SelectKanji.SelectionMode_Test);
	        	startActivity(selectKanjiIntent);
                return true;
        }
	};//pref_testKanji_onPreferenceClickListener
	
	private OnPreferenceClickListener pref_flashcardKanji_onPreferenceClickListener = new OnPreferenceClickListener() {		 
        public boolean onPreferenceClick(Preference preference) {
                //Toast.makeText(getBaseContext(), "pref_flashcardKanji_onPreferenceClickListener", Toast.LENGTH_LONG).show();
	        	Intent selectKanjiIntent = new Intent();
	   	    	
	        	selectKanjiIntent = new Intent(getBaseContext(), SelectKanji.class);
	        	selectKanjiIntent.putExtra("selectionMode", SelectKanji.SelectionMode_Flashcard);
	        	startActivity(selectKanjiIntent);
                return true;
        }
	};//pref_flashcardKanji_onPreferenceClickListener
		
	private OnPreferenceClickListener pref_editKanji_onPreferenceClickListener = new OnPreferenceClickListener() {		 
        public boolean onPreferenceClick(Preference preference) {
	        	Intent selectKanjiIntent = new Intent();
	   	    	
	        	selectKanjiIntent = new Intent(getBaseContext(), SelectEditKanji.class);	        	
	        	selectKanjiIntent.putExtra("showCorrectAnswerList", false);
	        	startActivity(selectKanjiIntent);                
                return true;
        }
	};//pref_editKanji_onPreferenceClickListener	
	
	private OnPreferenceClickListener pref_levels_onPreferenceClickListener = new OnPreferenceClickListener() {		 
        public boolean onPreferenceClick(Preference preference) {
	        	Intent leitnerEditIntent = new Intent();
	   	    	
	        	leitnerEditIntent = new Intent(getBaseContext(), EditLeitner.class);	        	
	        	startActivity(leitnerEditIntent);                 
                return true;
        }
	};//pref_levels_onPreferenceClickListener
	
	private void commitPreferences()
	{
		//EditTextPreference pref_onKunTextSize = (EditTextPreference)findPreference("pref_onKunTextSize");
        //EditTextPreference pref_meaningsTextSize = (EditTextPreference)findPreference("pref_meaningsTextSize");
        EditTextPreference pref_maxEntriesPerSession = (EditTextPreference)findPreference("pref_maxEntriesPerSession");
        EditTextPreference pref_minQuestionLocality = (EditTextPreference)findPreference("pref_minQuestionLocality");
        EditTextPreference pref_maxQuestionLocality = (EditTextPreference)findPreference("pref_maxQuestionLocality");
        
        CheckBoxPreference pref_doRealLeitner = (CheckBoxPreference)findPreference("pref_doRealLeitner");
        CheckBoxPreference pref_showWordCycle = (CheckBoxPreference)findPreference("pref_showWordCycle");
        CheckBoxPreference pref_resetCorrectCountOnWrongAnswer = (CheckBoxPreference)findPreference("pref_resetCorrectCountOnWrongAnswer");
        CheckBoxPreference pref_doRandomFlashcardMode = (CheckBoxPreference)findPreference("pref_doRandomFlashcardMode");
                
		//try { KanjiQuiz.currentQuiz.quizPreferences.onKunTextSize = Integer.parseInt(pref_onKunTextSize.getText()); } catch (NumberFormatException e) {}
		//try { KanjiQuiz.currentQuiz.quizPreferences.meaningsSize = Integer.parseInt(pref_meaningsTextSize.getText()); } catch (NumberFormatException e) {}
		try { KanjiQuiz.currentQuiz.quizPreferences.maxEntriesPerSession = Integer.parseInt(pref_maxEntriesPerSession.getText()); } catch (NumberFormatException e) {}
		try { KanjiQuiz.currentQuiz.quizPreferences.minQuestionLocality = Integer.parseInt(pref_minQuestionLocality.getText()); } catch (NumberFormatException e) {}
		try { KanjiQuiz.currentQuiz.quizPreferences.maxQuestionLocality = Integer.parseInt(pref_maxQuestionLocality.getText()); } catch (NumberFormatException e) {}
		
		KanjiQuiz.currentQuiz.quizPreferences.doRealLeitner = pref_doRealLeitner.isChecked();
		KanjiQuiz.currentQuiz.quizPreferences.showWordCycle = pref_showWordCycle.isChecked();
		KanjiQuiz.currentQuiz.quizPreferences.resetCorrectCountOnWrongAnswer = pref_resetCorrectCountOnWrongAnswer.isChecked();
		KanjiQuiz.currentQuiz.quizPreferences.doRandomFlashcardMode = pref_doRandomFlashcardMode.isChecked();
		
		KanjiQuiz.currentQuiz.exportQuiz(quizFile, this);
	}//commitPreferences
	
	@Override
    public boolean onKeyUp(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) < 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {            
            onBackPressed();
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {    
        commitPreferences(); 

        super.onBackPressed();
    }
            
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
	        	aboutIntent.putExtra("titleRes", R.string.session_preferences_title);
	        	aboutIntent.putExtra("stringVal", R.string.session_preferences_help_text);
	        	
	        	startActivity(aboutIntent);
	        	break;
	        }
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    
	    return true;
	}//onOptionsItemSelected  
    
}//SessionPreferences
