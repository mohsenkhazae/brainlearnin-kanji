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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.WindowManager;


public class FrontDoor extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_door);
                
        Button aboutButton = (Button)findViewById(R.id.button_front_door_about);
        aboutButton.setOnClickListener(aboutButtonClick);
        
        Button newSessionButton = (Button)findViewById(R.id.button_front_door_new_session);
        newSessionButton.setOnClickListener(newSessionButtonClick);
        
        Button openSessionButton = (Button)findViewById(R.id.button_front_door_open_session);
        openSessionButton.setOnClickListener(openSessionButtonClick);
        
        Button openLastSessionButton = (Button)findViewById(R.id.button_front_door_last_session);
        openLastSessionButton.setOnClickListener(openLastSessionButtonClick);
        
        SharedPreferences settings = getSharedPreferences("BrainLearninKanji", MODE_PRIVATE);
        boolean firstGo = settings.getBoolean("firstGo", true);
        
        if (firstGo == true) {
        	AlertHelpers.BasicAlert(getString(R.string.first_run_title), getString(R.string.first_run_info), getString(R.string.OK), this);
        	
        	SharedPreferences.Editor editor = settings.edit();
        	editor.putBoolean("firstGo", false);
        	editor.commit();
        }//if
        
        Display d = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = d.getWidth();
        int height = d.getHeight();
        
        if ((width < 800) && (height < 600)) {
        	boolean warnOnSmallDisplay = settings.getBoolean("warnOnSmallDisplay", false);
            
            if (warnOnSmallDisplay == false) {
            	AlertHelpers.BasicAlert(getString(R.string.warn_on_small_display_title), getString(R.string.warn_on_small_display_info), getString(R.string.OK), this);
            	
            	SharedPreferences.Editor editor = settings.edit();
            	editor.putBoolean("warnOnSmallDisplay", true);
            	editor.commit();
            }//if
        }//if
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.front_door_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent aboutIntent = new Intent(getBaseContext(), About.class);
    	//aboutIntent.setClassName("com.brainlearnin.kanji", "com.brainlearnin.kanji.About");
    	
        // Handle item selection
        switch (item.getItemId()) {
	        case R.id.menu_about:
	        	startActivity(aboutIntent);
	            break;
	           
	        case R.id.menu_help:
	        	aboutIntent.putExtra("titleRes", R.string.app_name);
	        	aboutIntent.putExtra("stringVal", R.string.front_door_help_text);
	        	
	        	startActivity(aboutIntent);
	        	break;
	           
	        case R.id.menu_global_preferences:
	        	doGlobalPreferences();
	        	break;
	        default:
	            return super.onOptionsItemSelected(item);
        }
        
        return true;
    }
    
    void doGlobalPreferences()
    {    	
    	Intent preferencesIntent = new Intent();
    	//preferencesIntent.setClassName("com.brainlearnin", "com.brainlearnin.GlobalPreferencesDialog");
    	preferencesIntent = new Intent(getBaseContext(), GlobalPreferencesDialog.class);
    	startActivity(preferencesIntent);
    }//doGlobalPreferences
    
    private View.OnClickListener aboutButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	Intent aboutIntent = new Intent(getBaseContext(), About.class);
        	//aboutIntent.setClassName("com.brainlearnin.kanji", "com.brainlearnin.kanji.About");
        	//android.content.ComponentName component = new android.content.ComponentName("com.brainlearnin", "KanjiFrontDoor");
        	//kanjiFrontDoorIntent.setComponent(component);
        	startActivity(aboutIntent);
        }
    };
    
    private View.OnClickListener newSessionButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	Intent newSessionIntent = new Intent(getBaseContext(), NewSession.class);
        	//newSessionIntent.setClassName("com.brainlearnin.kanji", "com.brainlearnin.kanji.NewSession");
        	//android.content.ComponentName component = new android.content.ComponentName("com.brainlearnin", "KanjiFrontDoor");
        	//kanjiFrontDoorIntent.setComponent(component);
        	startActivity(newSessionIntent);
        }
    };
    
    private View.OnClickListener openSessionButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	//Intent openSessionIntent = new Intent();
        	//openSessionIntent.setClassName("com.brainlearnin", "com.brainlearnin.OpenSession");
        	
        	Intent openSessionIntent = new Intent(getBaseContext(), OpenSession.class);
        	startActivity(openSessionIntent);
        }
    };
    
    private View.OnClickListener openLastSessionButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	SharedPreferences settings = getSharedPreferences("BrainLearninKanji", MODE_PRIVATE);            
            
            String lastSessionName = settings.getString("lastSessionName", "");            
            String lastSessionFile = settings.getString("lastSessionFile", "");
            
            boolean isValidFile = true;
            if (lastSessionName.equals("") || lastSessionFile .equals("")) {
            	isValidFile = false;
            }//if
            
            FileInputStream inputStream = null;
    		try {    		
    			inputStream = openFileInput(lastSessionFile);
    			try { inputStream.close(); } catch (IOException e) { }
    		} catch (FileNotFoundException e) {
    			isValidFile = false;
    		}//try/catch
            
            if (isValidFile == true) {        	
            	Intent openSessionIntent = new Intent(getBaseContext(), SessionOpened.class);
            	startActivity(openSessionIntent);
            } else {
            	AlertHelpers.BasicAlert(getString(R.string.front_door_no_last_title), getString(R.string.front_door_no_last_message), getString(R.string.OK), FrontDoor.this);
            }//if
        }
    };
    
    
    
    
}

