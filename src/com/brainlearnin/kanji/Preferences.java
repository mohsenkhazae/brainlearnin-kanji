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
import java.util.ArrayList;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;

public class Preferences 
{	
	private static Preferences globalPreferences = null;
	
	int onKunTextSize;
	int meaningsSize;
	boolean doRealLeitner; //drop back to level 0 or drop one level
	int maxEntriesPerSession;
	int minQuestionLocality;
	int maxQuestionLocality;
	boolean showWordCycle;
	int numTimesToRepeatCorrectUntilAccepted; //3
	boolean resetCorrectCountOnWrongAnswer;
	ArrayList<String> acceptedSeparators;
	boolean doRandomFlashcardMode; //or sequential
	
	TreeSet<String> testSet;
	TreeSet<String> flashcardSet;
	
	ArrayList<Integer> leitnerTimeouts;
	
	Preferences(boolean loadGlobals, Context context)
	{
		onKunTextSize = 16;
		meaningsSize = 12;
		doRealLeitner = true;
		maxEntriesPerSession = 50;
		minQuestionLocality = 20;
		maxQuestionLocality = -1;
		showWordCycle = true;
		numTimesToRepeatCorrectUntilAccepted = 3;
		resetCorrectCountOnWrongAnswer = true;
		acceptedSeparators = new ArrayList<String>();
        acceptedSeparators.add(",");
        acceptedSeparators.add("、");
		doRandomFlashcardMode = true;
		
		testSet = new TreeSet<String>();
		flashcardSet = new TreeSet<String>();
		
		leitnerTimeouts = new ArrayList<Integer>();
		
		leitnerTimeouts.add(-1);
		leitnerTimeouts.add(1);
		leitnerTimeouts.add(2);
		leitnerTimeouts.add(4);
		leitnerTimeouts.add(7);
		leitnerTimeouts.add(14);
		leitnerTimeouts.add(30);
		leitnerTimeouts.add(60);
		
		if (loadGlobals == true) {
			importGlobalPreferences(context);
		}//if
	}//constructor
	
	public static Preferences getGlobalInstance(Context context)
	{		
		if (globalPreferences == null) {
			globalPreferences = new Preferences(true, context);
		}//if
		
		return globalPreferences;
	}//getGlobalInstance
	
	Preferences deepClone()
	{
		Preferences preferences = new Preferences(false, null);
		
		preferences.onKunTextSize = onKunTextSize;
		preferences.meaningsSize = meaningsSize;
		preferences.doRealLeitner = doRealLeitner;
		preferences.maxEntriesPerSession = maxEntriesPerSession;
		preferences.minQuestionLocality = minQuestionLocality;
		preferences.maxQuestionLocality = maxQuestionLocality;
		preferences.showWordCycle = showWordCycle;
		preferences.numTimesToRepeatCorrectUntilAccepted = numTimesToRepeatCorrectUntilAccepted;
		preferences.resetCorrectCountOnWrongAnswer = resetCorrectCountOnWrongAnswer;		
		preferences.doRandomFlashcardMode = doRandomFlashcardMode;
		
		preferences.acceptedSeparators.clear();
		for (String str : acceptedSeparators) {
			preferences.acceptedSeparators.add(str);
		}//for
		
		preferences.leitnerTimeouts.clear();
		for (Integer val : leitnerTimeouts) {
			preferences.leitnerTimeouts.add(val);
		}//for
		
		preferences.testSet.clear();
		for (String str : testSet) {
			preferences.testSet.add(str);
		}//for
		
		preferences.flashcardSet.clear();
		for (String str : flashcardSet) {
			preferences.flashcardSet.add(str);
		}//for
		
		return preferences;
	}//deepClone
	
	void exportPreferences(java.io.FileOutputStream fos) throws IOException
	{
		fos.write("<preferences>\n".getBytes());
		
		fos.write(("<onKunTextSize>" + Integer.toString(onKunTextSize) + "</onKunTextSize>\n").getBytes());
		fos.write(("<meaningsSize>" + Integer.toString(meaningsSize) + "</meaningsSize>\n").getBytes());
		fos.write(("<maxEntriesPerSession>" + Integer.toString(maxEntriesPerSession) + "</maxEntriesPerSession>\n").getBytes());
		fos.write(("<minQuestionLocality>" + Integer.toString(minQuestionLocality) + "</minQuestionLocality>\n").getBytes());
		fos.write(("<maxQuestionLocality>" + Integer.toString(maxQuestionLocality) + "</maxQuestionLocality>\n").getBytes());
		fos.write(("<numTimesToRepeatCorrectUntilAccepted>" + Integer.toString(numTimesToRepeatCorrectUntilAccepted) + "</numTimesToRepeatCorrectUntilAccepted>\n").getBytes());
		fos.write(("<doRealLeitner>" + Boolean.toString(doRealLeitner) + "</doRealLeitner>\n").getBytes());
		fos.write(("<showWordCycle>" + Boolean.toString(showWordCycle) + "</showWordCycle>\n").getBytes());
		fos.write(("<resetCorrectCountOnWrongAnswer>" + Boolean.toString(resetCorrectCountOnWrongAnswer) + "</resetCorrectCountOnWrongAnswer>\n").getBytes());
		fos.write(("<doRandomFlashcardMode>" + Boolean.toString(doRandomFlashcardMode) + "</doRandomFlashcardMode>\n").getBytes());

		for (String str : acceptedSeparators) {
			fos.write(("<acceptedSeparator>" + str + "</acceptedSeparator>\n").getBytes());
		}//for
		
		for (Integer val : leitnerTimeouts) {
			fos.write(("<leitnerTimeout>" + Integer.toString(val) + "</leitnerTimeout>\n").getBytes());
		}//for
		
		for (String str : testSet) {
			fos.write(("<testSet>" + str + "</testSet>\n").getBytes());
		}//for
		
		for (String str : flashcardSet) {
			fos.write(("<flashcardSet>" + str + "</flashcardSet>\n").getBytes());
		}//for
		
		fos.write("</preferences>\n".getBytes());
	}//exportPreferences
	
	void exportGlobalPreferences(Context context)
	{
		boolean superBadError = false;
		
		java.io.FileOutputStream fos;
		try {
			fos = context.openFileOutput("global_preferences", android.content.Context.MODE_PRIVATE);			
				
			fos.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".getBytes());
			exportPreferences(fos);
			
			fos.close();		
		} catch (FileNotFoundException e) {
			//What to do? We're boned...
			superBadError = true;
		} catch (IOException e) {
			//What to do? We're boned...
			superBadError = true;
		}		
		
		if (true == superBadError) {
			AlertHelpers.BasicAlert(context.getString(R.string.err_title), context.getString(R.string.import_sample_template_manifest_write_err), context.getString(R.string.OK), context);
			
			return;
		}//if
	}//exportGlobalPreferences
	
	static Preferences importPreferences(Element preferencesNode)
	{
		Preferences preferences = new Preferences(false, null);
		
		preferences.acceptedSeparators.clear();
		preferences.leitnerTimeouts.clear();
		preferences.testSet.clear();
		preferences.flashcardSet.clear();
		
		NodeList preferencesNodeList = preferencesNode.getChildNodes();
		for (int index = 0; index < preferencesNodeList.getLength(); ++index) {
			Node childNodeBase = preferencesNodeList.item(index);
			if (childNodeBase instanceof Element) {
				Element baseElement = (Element)childNodeBase;
				String nodeName = baseElement.getNodeName();
				
				if (nodeName.equals("onKunTextSize") == true) {
					preferences.onKunTextSize = Integer.parseInt(baseElement.getFirstChild().getNodeValue());
				}//if
				
				else if (nodeName.equals("meaningsSize") == true) {
					preferences.meaningsSize = Integer.parseInt(baseElement.getFirstChild().getNodeValue());
				}//if
				
				else if (nodeName.equals("maxEntriesPerSession") == true) {
					preferences.maxEntriesPerSession = Integer.parseInt(baseElement.getFirstChild().getNodeValue());
				}//if
				
				else if (nodeName.equals("minQuestionLocality") == true) {
					preferences.minQuestionLocality = Integer.parseInt(baseElement.getFirstChild().getNodeValue());
				}//if
				
				else if (nodeName.equals("maxQuestionLocality") == true) {
					preferences.maxQuestionLocality = Integer.parseInt(baseElement.getFirstChild().getNodeValue());
				}//if
				
				else if (nodeName.equals("numTimesToRepeatCorrectUntilAccepted") == true) {
					preferences.numTimesToRepeatCorrectUntilAccepted = Integer.parseInt(baseElement.getFirstChild().getNodeValue());
				}//if
				
				else if (nodeName.equals("doRealLeitner") == true) {
					preferences.doRealLeitner = Boolean.parseBoolean(baseElement.getFirstChild().getNodeValue());
				}//if
				
				else if (nodeName.equals("showWordCycle") == true) {
					preferences.showWordCycle = Boolean.parseBoolean(baseElement.getFirstChild().getNodeValue());
				}//if
				
				else if (nodeName.equals("resetCorrectCountOnWrongAnswer") == true) {
					preferences.resetCorrectCountOnWrongAnswer = Boolean.parseBoolean(baseElement.getFirstChild().getNodeValue());
				}//if
				
				else if (nodeName.equals("doRandomFlashcardMode") == true) {
					preferences.doRandomFlashcardMode = Boolean.parseBoolean(baseElement.getFirstChild().getNodeValue());
				}//if
				
				else if (nodeName.equals("leitnerTimeout") == true) {
					preferences.leitnerTimeouts.add(Integer.parseInt(baseElement.getFirstChild().getNodeValue()));
				}//if
				
				else if (nodeName.equals("acceptedSeparator") == true) {
					preferences.acceptedSeparators.add(baseElement.getFirstChild().getNodeValue());
				}//if
				
				else if (nodeName.equals("testSet") == true) {
					preferences.testSet.add(baseElement.getFirstChild().getNodeValue());
				}//if
				
				else if (nodeName.equals("flashcardSet") == true) {
					preferences.flashcardSet.add(baseElement.getFirstChild().getNodeValue());
				}//if
			}//if
		}//for
		
		return preferences;
	}//importPreferences
	
	Preferences importGlobalPreferences(Context context)
	{
		FileInputStream globalPreferencesInStream = null;
		try {    		
    		globalPreferencesInStream = context.openFileInput("global_preferences");
		} catch (Exception e) { //FileNotFoundException e) {
			exportGlobalPreferences(context);
		}//if
		
		try {    		
    		globalPreferencesInStream = context.openFileInput("global_preferences");
			
    		Document doc = null;
            try {
            	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            	DocumentBuilder db = dbf.newDocumentBuilder();
            	doc = db.parse(globalPreferencesInStream);
            } catch (IOException ex) {
            
            } catch (SAXException e) {
    			
    		} catch (ParserConfigurationException e) {
    			
    		}
    		
    		//Element preferencesNode = (Element)doc.getFirstChild();
    		NodeList topLevelNodeList = doc.getChildNodes();
    		Element preferencesNode = null;
    		for (int index = 0; index < topLevelNodeList.getLength(); ++index) {
    			Node childNodeBase = topLevelNodeList.item(index);
    			if (childNodeBase instanceof Element) {
    				preferencesNode = (Element)childNodeBase;
    				break;
    			}//if
    		}//for
    		
    		globalPreferences = importPreferences(preferencesNode);
    		return globalPreferences;
		} catch (FileNotFoundException e) {
			AlertHelpers.BasicAlert(context.getString(R.string.err_title), context.getString(R.string.import_sample_template_manifest_write_err), context.getString(R.string.OK), context);
						
			return globalPreferences;
		}
	}//importGlobalPreferences
	
}//Preferences
