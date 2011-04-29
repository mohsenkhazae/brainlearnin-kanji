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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;



public class KanjiQuiz 
{
	public static class GlyphStat
	{
		String glyph;
		int index;
		int onGrade;
		int kunGrade;
		int meaningsGrade;
		
		java.util.Date lastOnAnswer;
		java.util.Date lastKunAnswer;
		java.util.Date lastMeaningAnswer;		
		
		java.util.ArrayList<String> onReadings;
		java.util.ArrayList<String> kunReadings;
		java.util.ArrayList<String> meanings;
		
		//For active quiz
		int attempted;
		int correctAnswer;
		
		public GlyphStat()
		{
			onGrade = 0;
			kunGrade = 0;
			meaningsGrade = 0;
			
			lastOnAnswer = Calendar.getInstance().getTime();
			lastKunAnswer = Calendar.getInstance().getTime();
			lastMeaningAnswer = Calendar.getInstance().getTime();
			
			lastOnAnswer.setHours(0);
			lastOnAnswer.setMinutes(0);
			lastOnAnswer.setSeconds(0);
			
			lastKunAnswer.setHours(0);
			lastKunAnswer.setMinutes(0);
			lastKunAnswer.setSeconds(0);
			
			lastMeaningAnswer.setHours(0);
			lastMeaningAnswer.setMinutes(0);
			lastMeaningAnswer.setSeconds(0);
			
			onReadings = new java.util.ArrayList<String>();
			kunReadings = new java.util.ArrayList<String>();
			meanings = new java.util.ArrayList<String>();
		}//constructor
	}//GlyphStat
	
	public static class QuizGlyphStat
	{
		GlyphStat glyphStat;
		int questionMode;
        int attempted;
        int correctAnswer;
        int originalGrade;
        boolean firstAnswerCorrect;

        QuizGlyphStat()
        {
            attempted = 0;
            correctAnswer = 0;
            firstAnswerCorrect = false;
        }//constructor
	}//QuizGlyphStat
		
	Preferences quizPreferences;
	java.util.HashMap<Integer, GlyphStat> glyphs;
	
	java.util.List<QuizGlyphStat> currentQuizGlyphsOrig;
    java.util.List<QuizGlyphStat> currentQuizGlyphs;
    String quizFile;
	
	static KanjiQuiz currentQuiz = null;
	
	private void importGlyphStat(Element glyphStatNode)
	{
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		GlyphStat newGlyphStat = new GlyphStat();
				
		NodeList glyphChildNodeList = glyphStatNode.getChildNodes();
		for (int index = 0; index < glyphChildNodeList.getLength(); ++index) {
			Node childNodeBase = glyphChildNodeList.item(index);
			if (childNodeBase instanceof Element) {
				Element childNode = (Element)glyphChildNodeList.item(index);
				
				if (childNode.getNodeName().equals("glyph")) {
					newGlyphStat.glyph = childNode.getFirstChild().getNodeValue();
				}//if
				
				if (childNode.getNodeName().equals("index")) {
					newGlyphStat.index = Integer.parseInt(childNode.getFirstChild().getNodeValue());					
				}//if
				
				if (childNode.getNodeName().equals("onGrade")) {
					newGlyphStat.onGrade = Integer.parseInt(childNode.getFirstChild().getNodeValue());					
				}//if
				
				if (childNode.getNodeName().equals("kunGrade")) {
					newGlyphStat.kunGrade = Integer.parseInt(childNode.getFirstChild().getNodeValue());					
				}//if
				
				if (childNode.getNodeName().equals("meaningsGrade")) {
					newGlyphStat.meaningsGrade = Integer.parseInt(childNode.getFirstChild().getNodeValue());					
				}//if
				
				if (childNode.getNodeName().equals("onReading")) {
					newGlyphStat.onReadings.add(childNode.getFirstChild().getNodeValue());					
				}//if
				
				if (childNode.getNodeName().equals("kunReading")) {
					newGlyphStat.kunReadings.add(childNode.getFirstChild().getNodeValue());					
				}//if
				
				if (childNode.getNodeName().equals("meaning")) {
					newGlyphStat.meanings.add(childNode.getFirstChild().getNodeValue());					
				}//if
				
				if (childNode.getNodeName().equals("lastOnAnswer")) {
					try {
						newGlyphStat.lastOnAnswer = dateFormat.parse(childNode.getFirstChild().getNodeValue());
					} catch (DOMException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}//if
				
				if (childNode.getNodeName().equals("lastKunAnswer")) {
					try {
						newGlyphStat.lastKunAnswer = dateFormat.parse(childNode.getFirstChild().getNodeValue());
					} catch (DOMException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}//if
				
				if (childNode.getNodeName().equals("lastMeaningAnswer")) {
					try {
						newGlyphStat.lastMeaningAnswer = dateFormat.parse(childNode.getFirstChild().getNodeValue());
					} catch (DOMException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}//if
				
			}//if
		}//for
		
		glyphs.put(newGlyphStat.index, newGlyphStat);
	}//importGlyphStat
	
	public void importGlyphs(Element glyphsNode)
	{
		glyphs = new java.util.HashMap<Integer, GlyphStat>(); 
				
		NodeList glyphChildNodeList = glyphsNode.getChildNodes();
		for (int index = 0; index < glyphChildNodeList.getLength(); ++index) {
			Node childNodeBase = glyphChildNodeList.item(index);
			if (childNodeBase instanceof Element) {
				Element glyphStatNode = (Element)glyphChildNodeList.item(index);
				if (glyphStatNode.getNodeName().equals("glyphStat")) {
					importGlyphStat(glyphStatNode);
				}//if
			}//if
		}//for
	}//importGlyphs

	private boolean exportGlyphStat(GlyphStat glyphStat, java.io.FileOutputStream fos)
	{
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			fos.write("<glyphStat>\n".getBytes());
			
			fos.write(("<glyph>" + glyphStat.glyph + "</glyph>").getBytes());
			fos.write(("<index>" + Integer.toString(glyphStat.index) + "</index>").getBytes());
			fos.write(("<onGrade>" + Integer.toString(glyphStat.onGrade) + "</onGrade>").getBytes());
			fos.write(("<kunGrade>" + Integer.toString(glyphStat.kunGrade) + "</kunGrade>").getBytes());
			fos.write(("<meaningsGrade>" + Integer.toString(glyphStat.meaningsGrade) + "</meaningsGrade>").getBytes());
			
			fos.write(("<lastOnAnswer>" + dateFormat.format(glyphStat.lastOnAnswer) + "</lastOnAnswer>").getBytes());
			fos.write(("<lastKunAnswer>" + dateFormat.format(glyphStat.lastKunAnswer) + "</lastKunAnswer>").getBytes());
			fos.write(("<lastMeaningAnswer>" + dateFormat.format(glyphStat.lastMeaningAnswer) + "</lastMeaningAnswer>").getBytes());
			
			for (String reading : glyphStat.onReadings) {
				fos.write(("<onReading>" + reading + "</onReading>").getBytes());
			}//for
			
			for (String reading : glyphStat.kunReadings) {
				fos.write(("<kunReading>" + reading + "</kunReading>").getBytes());
			}//for
			
			for (String reading : glyphStat.meanings) {
				fos.write(("<meaning>" + reading + "</meaning>").getBytes());
			}//for
			
			fos.write("</glyphStat>\n".getBytes());
		} catch (IOException e) {
			return false;			
		}
		
		return true;
	}//exportGlyphStat
	
	public boolean importQuiz(String quizFile_, Context context)
	{
		this.quizFile = quizFile_;
		
		FileInputStream inputStream = null;
		try {    		
			inputStream = context.openFileInput(quizFile_);
			
		} catch (FileNotFoundException e) {
			AlertHelpers.BasicAlert(context.getString(R.string.import_quiz_err_title), context.getString(R.string.import_quiz_err_message), context.getString(R.string.OK), context);			
			return false;
		}
		
		Document doc = null;
        try {
        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        	DocumentBuilder db = dbf.newDocumentBuilder();
        	doc = db.parse(inputStream);
        } catch (IOException ex) {
        	AlertHelpers.BasicAlert(context.getString(R.string.import_quiz_err_title), context.getString(R.string.import_quiz_err_message), context.getString(R.string.OK), context);
        	try { inputStream.close(); } catch (IOException e) { }
        	return false;
        } catch (SAXException e) {
        	AlertHelpers.BasicAlert(context.getString(R.string.import_quiz_err_title), context.getString(R.string.import_quiz_err_message), context.getString(R.string.OK), context);
        	try { inputStream.close(); } catch (IOException e2) { }
        	return false;
		} catch (ParserConfigurationException e) {
			AlertHelpers.BasicAlert(context.getString(R.string.import_quiz_err_title), context.getString(R.string.import_quiz_err_message), context.getString(R.string.OK), context);
			try { inputStream.close(); } catch (IOException e3) { }
			return false;
		}
		
		NodeList topLevelNodeList = doc.getChildNodes();
		Element templatesNode = null;
		for (int index = 0; index < topLevelNodeList.getLength(); ++index) {
			Node childNodeBase = topLevelNodeList.item(index);
			if (childNodeBase instanceof Element) {
				templatesNode = (Element)childNodeBase;
				break;
			}//if
		}//for
		
		//Element templatesNode = (Element)doc.getFirstChild();
		NodeList templateNodeList = templatesNode.getChildNodes();
		for (int index = 0; index < templateNodeList.getLength(); ++index) {
			Node childNodeBase = templateNodeList.item(index);
			if (childNodeBase instanceof Element) {
				Element templateNode = (Element)templateNodeList.item(index);
							
				if (templateNode.getNodeName().equals("preferences") == true) {
					quizPreferences = Preferences.importPreferences(templateNode);
				}//if
				
				if (templateNode.getNodeName().equals("GlyphStats") == true) {
					importGlyphs(templateNode);
				}//if
			}//if
		}//for
		
		try { inputStream.close(); } catch (IOException e) { }
				
		return true;
	}//importQuiz
	
	public boolean exportQuiz(String outFile, Context context)
	{
		this.quizFile = outFile;
		
		//output document preamble
    	boolean superBadError = false;
		
		java.io.FileOutputStream fos;
		try {
			fos = context.openFileOutput(outFile, android.content.Context.MODE_PRIVATE);
				
			fos.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n".getBytes());
			fos.write("<KanjiQuiz>\n".getBytes());
			
			//output global preferences as document prefs
			quizPreferences.exportPreferences(fos);
						
	    	//output template kanji stuff
			fos.write("<GlyphStats>\n".getBytes());
			for (GlyphStat glyphStat : glyphs.values()) {
				boolean ret = exportGlyphStat(glyphStat, fos);
				if (ret == false) {
					superBadError = true;
				}//if
			}//for
			fos.write("</GlyphStats>\n".getBytes());
			
			fos.write("</KanjiQuiz>\n".getBytes());
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
			return false;
		} 
		
		return true;
	}//exportQuiz

}//KanjiQuiz
