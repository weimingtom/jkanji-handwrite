package com.iteye.weimingtom.jkanji;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XMLModelHandler extends DefaultHandler {
	private boolean inUTF8 = false;
	private boolean inStroke = false;
	private boolean inPoint = false;
	public HashMap<String, ArrayList<Point>> points = 
			new HashMap<String, ArrayList<Point>>();
	public HashMap<String, ArrayList<Integer>> seps = 
			new HashMap<String, ArrayList<Integer>>();
	
	private String curStr;
	private int posSep;
	private ArrayList<Point> curPoints;
	private ArrayList<Integer> curSeps;
	
	public XMLModelHandler() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();
        xr.setContentHandler(this);
        xr.parse(new InputSource(new FileInputStream(
        	//"e:/重要保存2/tomoe/handwriting-ja.xml"
        	//"e:/重要保存2/tomoe/tegaki/handwriting-ja.xml"
        	"handwriting-ja.xml"
        )));
        /*
        xr.parse(new InputSource(new FileInputStream(
            "e:/重要保存2/tomoe/tegaki/hiragana.charcol"
        )));
        xr.parse(new InputSource(new FileInputStream(
            "e:/重要保存2/tomoe/tegaki/katakana.charcol"
        )));
        */
	}
	
	public void test() {
        System.out.println("size = " + points.size());
        System.out.println("size = " + seps.size());
        ArrayList<Point> ps = points.get("ゆ");
        ArrayList<Integer> se = seps.get("ゆ");
        System.out.println(ps);
        System.out.println(se);
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		// TODO Auto-generated method stub
		new XMLModelHandler().test();
	}
	
	@Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("utf8")) {
            inUTF8 = true;
            curStr = null;
        } else if (qName.equals("strokes")) {
        	curPoints = new ArrayList<Point>();
        	curSeps = new ArrayList<Integer>();
        	posSep = 0;
        } else if (qName.equals("stroke")) {
        	inStroke = true;
        	curSeps.add(posSep);
        } else if (qName.equals("point")) {
        	inPoint = true;
        	int x = Integer.parseInt(attributes.getValue("x"));
        	int y = Integer.parseInt(attributes.getValue("y"));
        	if (false) {
	        	System.out.println(
	        			"x = " + x + ", " + 
	        			"y = " + y);
        	}
        	curPoints.add(new Point(x, y));
        	posSep++;
        }
    }
	
    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("utf8")) {
            inUTF8 = false;
        } else if (qName.equals("strokes")) {
        	if (false) {
        		System.out.println(curStr);
        	}
        	points.put(curStr, curPoints);
        	curPoints = null;    
        	seps.put(curStr, curSeps);
        	curSeps = null;
    	} else if (qName.equals("stroke")) {
        	inStroke = false;
        } else if (qName.equals("point")) {
        	inPoint = false;
        }
    }
	
    @Override
    public void characters(char[] ch, int offset, int length) {
    	if (inUTF8) {
        	String text = new String(ch, offset, length);
        	if (false) {
        		System.out.println("element ：" + text);
        	}
        	curStr = text;
    	}
    }
}
