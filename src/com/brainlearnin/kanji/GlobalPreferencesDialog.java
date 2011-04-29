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
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class GlobalPreferencesDialog extends PreferenceActivity
{
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.global_preferences);
            
            //EditTextPreference pref_onKunTextSize = (EditTextPreference)findPreference("pref_onKunTextSize");
            //EditTextPreference pref_meaningsTextSize = (EditTextPreference)findPreference("pref_meaningsTextSize");
            EditTextPreference pref_maxEntriesPerSession = (EditTextPreference)findPreference("pref_maxEntriesPerSession");
            EditTextPreference pref_minQuestionLocality = (EditTextPreference)findPreference("pref_minQuestionLocality");
            EditTextPreference pref_maxQuestionLocality = (EditTextPreference)findPreference("pref_maxQuestionLocality");
            
            CheckBoxPreference pref_doRealLeitner = (CheckBoxPreference)findPreference("pref_doRealLeitner");
            CheckBoxPreference pref_showWordCycle = (CheckBoxPreference)findPreference("pref_showWordCycle");
            CheckBoxPreference pref_resetCorrectCountOnWrongAnswer = (CheckBoxPreference)findPreference("pref_resetCorrectCountOnWrongAnswer");
            CheckBoxPreference pref_doRandomFlashcardMode = (CheckBoxPreference)findPreference("pref_doRandomFlashcardMode");
            
            Preferences globalPreferences = Preferences.getGlobalInstance(this);
                        
            //pref_onKunTextSize.setText(Integer.toString(globalPreferences.onKunTextSize));
            //pref_meaningsTextSize.setText(Integer.toString(globalPreferences.meaningsSize));
            pref_maxEntriesPerSession.setText(Integer.toString(globalPreferences.maxEntriesPerSession));
            pref_minQuestionLocality.setText(Integer.toString(globalPreferences.minQuestionLocality));
            pref_maxQuestionLocality.setText(Integer.toString(globalPreferences.maxQuestionLocality));
            
            pref_doRealLeitner.setChecked(globalPreferences.doRealLeitner);
            pref_showWordCycle.setChecked(globalPreferences.showWordCycle);
            pref_resetCorrectCountOnWrongAnswer.setChecked(globalPreferences.resetCorrectCountOnWrongAnswer);
            pref_doRandomFlashcardMode.setChecked(globalPreferences.doRandomFlashcardMode);            
	}//onCreate
	
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
        
        Preferences globalPreferences = Preferences.getGlobalInstance(this);
        
		//try { globalPreferences.onKunTextSize = Integer.parseInt(pref_onKunTextSize.getText()); } catch (NumberFormatException e) {}
		//try { globalPreferences.meaningsSize = Integer.parseInt(pref_meaningsTextSize.getText()); } catch (NumberFormatException e) {}
		try { globalPreferences.maxEntriesPerSession = Integer.parseInt(pref_maxEntriesPerSession.getText()); } catch (NumberFormatException e) {}
		try { globalPreferences.minQuestionLocality = Integer.parseInt(pref_minQuestionLocality.getText()); } catch (NumberFormatException e) {}
		try { globalPreferences.maxQuestionLocality = Integer.parseInt(pref_maxQuestionLocality.getText()); } catch (NumberFormatException e) {}
		
		globalPreferences.doRealLeitner = pref_doRealLeitner.isChecked();
		globalPreferences.showWordCycle = pref_showWordCycle.isChecked();
		globalPreferences.resetCorrectCountOnWrongAnswer = pref_resetCorrectCountOnWrongAnswer.isChecked();
		globalPreferences.doRandomFlashcardMode = pref_doRandomFlashcardMode.isChecked();
		
		globalPreferences.exportGlobalPreferences(this);
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
		        	//aboutIntent.setClassName("com.brainlearnin.kanji", "com.brainlearnin.kanji.About");
		        	startActivity(aboutIntent);
		        }
	            break;
	            
	        case R.id.menu_help:
	        {
	        	Intent aboutIntent = new Intent(getBaseContext(), About.class);
	        	aboutIntent.putExtra("titleRes", R.string.global_preferences_title);
	        	aboutIntent.putExtra("stringVal", R.string.global_preferences_help_text);
	        	
	        	startActivity(aboutIntent);
	        	break;
	        }
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    
	    return true;
	}//onOptionsItemSelected   
            
}//PreferencesDialog
