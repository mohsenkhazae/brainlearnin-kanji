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
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;


public class About extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        TextView aboutText = (TextView) findViewById(R.id.about_text);
        
        Bundle bundle = this.getIntent().getExtras();
        
        String aboutTextStrBase = "";
        if (bundle == null) {
        	aboutTextStrBase = getString(R.string.about_text);            
        } else {
        	int titleRes = bundle.getInt("titleRes");
        	int textRes = bundle.getInt("stringVal");
        	
        	this.setTitle(getString(R.string.help_title_base) + " " + getString(titleRes));        	
        	aboutTextStrBase = getString(textRes);
        }//if
        
        Spanned aboutTextSpanned = Html.fromHtml(aboutTextStrBase);        
        aboutText.setText(aboutTextSpanned);
                
    }
	
}
