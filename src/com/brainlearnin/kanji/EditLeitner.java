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

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class EditLeitner extends ListActivity 
{	
	EditLeitnerDialog editLeitnerDialog = null;
	android.widget.ListView tableLayout = null;
	int curOffset;
	int selectedItemIndex;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_leitner_levels);
        
        curOffset = 0;
        
        tableLayout = this.getListView();        
        
        editLeitnerDialog = new EditLeitnerDialog(this);
               
        doFillGrid();        
        
        Button appendButton = (Button)findViewById(R.id.edit_leitner_append_button);
        appendButton.setOnClickListener(appendButtonClick);
        
        Button editButton = (Button)findViewById(R.id.edit_leitner_edit_button);
        editButton.setOnClickListener(editButtonClick);
        
        Button deleteButton = (Button)findViewById(R.id.edit_leitner_delete_button);
        deleteButton.setOnClickListener(deleteButtonClick);
        
        selectedItemIndex = 0;
        tableLayout.setOnItemClickListener(listViewClickListener);
        
        EditLeitnerListAdapter.lastSelectionKanji = -1;
    }//onCreate
    
    private ListView.OnItemClickListener listViewClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                selectedItemIndex = myItemInt;            
          }
    };//listViewClickListener
    
    private View.OnClickListener appendButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	int lastTime = (int)(KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.get(KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.size()-1) * 1.5);
        	KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.add(lastTime);
        	doFillGrid();
        }
    };//prevButtonClick
    
    private View.OnClickListener editButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	if (EditLeitnerListAdapter.lastSelectionKanji < 0) {
        		return;
        	}//if
        	
        	editLeitnerDialog.setOldLevel(EditLeitnerListAdapter.lastSelectionKanji, KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.get(EditLeitnerListAdapter.lastSelectionKanji));
        	editLeitnerDialog.show();        	
        }
    };//nextButtonClick
    
    private View.OnClickListener deleteButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
        	if ((EditLeitnerListAdapter.lastSelectionKanji < 0) || (KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.size() < 2)) {
        		return;
        	}//if
        	
        	int itemIndex = EditLeitnerListAdapter.lastSelectionKanji;
        	
        	KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.remove(itemIndex);
        	
        	EditLeitnerListAdapter.lastSelectionKanji = -1;
        	doFillGrid();
        }
    };//clearButtonClick
    
    public void doChangeLevel(int level, int newTimeOut)
    {
    	KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts.set(level, newTimeOut);
    	
    	doFillGrid();
    }//doChangeLevel
    
    private void doFillGrid()
    {
    	tableLayout.setCacheColorHint(0);

    	EditLeitnerListAdapter adapter = new EditLeitnerListAdapter(this, R.layout.select_kanji_item, KanjiQuiz.currentQuiz.quizPreferences.leitnerTimeouts);
    	tableLayout.setAdapter(adapter);
    	tableLayout.setItemsCanFocus(false);
    	tableLayout.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    	registerForContextMenu(getListView());
    }//doFillGrid
    
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
	            aboutIntent.putExtra("titleRes", R.string.editLeitnerTitle);
	            aboutIntent.putExtra("stringVal", R.string.editLeitner_help_text);
	    
	            startActivity(aboutIntent);
	            return true;
	        }
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	    
	    return true;
	}//onOptionsItemSelected


}//EditLeitner
