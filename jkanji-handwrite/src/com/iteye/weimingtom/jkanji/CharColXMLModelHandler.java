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

import spark.tomoe.HiraganaDictionary;
import spark.tomoe.NumberDictionary;
import spark.tomoe.ResultCandidate;
import spark.tomoe.Tomoe;

public class CharColXMLModelHandler extends DefaultHandler {
	private final static String JAP_CHARS = 
			"ぁあぃいぅうぇえぉおかがきぎくぐけげこごさざしじすずせぜそぞ " +
			"ただちぢっつづてでとどなにぬねのはばぱひびぴふぶぷへべぺほぼぽ" +  
			"まみむめもゃやゅゆょよらりるれろゎわゐゑをん" + 
			"ァアィイゥウェエォオカガキギクグケゲコゴサザシジスズセゼソゾ " +
			"タダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポ" + 
			"マミムメモャヤュユョヨラリルレロヮワヰヱヲンヴヵヶ"; 
	
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
	
	public CharColXMLModelHandler() throws ParserConfigurationException, SAXException, IOException {
		tomoe.addDictionary(NumberDictionary.getDictionary());
		tomoe.addDictionary(HiraganaDictionary.getDictionary());
		
		SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();
        xr.setContentHandler(this);
        /**
         * @see https://github.com/tegaki/tegaki/tree/master/tegaki-models/data/train/japanese
         */
        xr.parse(new InputSource(new FileInputStream(
            //"e:/重要保存2/tomoe/tegaki/hiragana.charcol"
        	"hiragana.charcol"
        )));
        xr.parse(new InputSource(new FileInputStream(
            //"e:/重要保存2/tomoe/tegaki/katakana.charcol"
        	"katakana.charcol"
        )));
	}
	
	public void checkin(char ch) {
        ArrayList<Point> ps = points.get(Character.toString(ch));
        ArrayList<Integer> se = seps.get(Character.toString(ch));
        if (false) {
	        System.out.println("size = " + points.size());
	        System.out.println("size = " + seps.size());
	        System.out.println(ps);
	        System.out.println(se);
        }
        if (ps != null && se != null) {
        	clearStrokes();
        	for (int i = 0; i < ps.size(); i++) {
        		if ((i + 1) == ps.size()) {
        			endStroke(
	        				ch,
	        				(int)(ps.get(i).x / 1000.f * 300), 
	        				(int)(ps.get(i).y / 1000.f * 300), true);         			
        		} else if (se.contains(i + 1)) {
        			endStroke(
            				ch,
            				(int)(ps.get(i).x / 1000.f * 300), 
            				(int)(ps.get(i).y / 1000.f * 300), false);        			
        		} else if (se.contains(i)) {
        			startStroke(
        				(int)(ps.get(i).x / 1000.f * 300), 
        				(int)(ps.get(i).y / 1000.f * 300));
        		} else {
        			addPoint(
        				(int)(ps.get(i).x / 1000.f * 300), 
        				(int)(ps.get(i).y / 1000.f * 300));
        		}
        	}
        }
	}
	
	private Tomoe tomoe = new Tomoe();
	private ArrayList<ArrayList<Point>> inputStrokes;
	private ArrayList<Point> inputStroke;
	
	private void clearStrokes() {
		inputStrokes = new ArrayList<ArrayList<Point>>();
	}
	
	private void startStroke(int x, int y) {
		inputStroke = new ArrayList<Point>();
		inputStroke.add(new Point(x, y));
	}
	
	private void addPoint(int x, int y) {
		inputStroke.add(new Point(x, y));
	}
	
	private void endStroke(char letter, int x, int y, boolean showAppend) {
		inputStrokes.add(inputStroke);
		if (showAppend) {
			boolean ret = tomoe.study(letter, inputStrokes);
			//System.out.println("letter == " + letter + ", ret == " + ret);
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		// TODO Auto-generated method stub
		CharColXMLModelHandler handler = new CharColXMLModelHandler();
		for (int i = 0; i < JAP_CHARS.length(); i++) {
			handler.checkin(JAP_CHARS.charAt(i));
		}
		handler.test();
	}
	
	private void test() {
		//'ス'
		int[][][] javaInputStrokes = 			new int[][][] {
				{{69, 80},{99, 79},{173, 65},{203, 60},{203, 92},{186, 118},{167, 144},{147, 171},{127, 197},{104, 217},{79, 234},{47, 242},},
				{{151, 163},{180, 183},{212, 204},{238, 222},{253, 231},},
				};
		
		inputStrokes = new ArrayList<ArrayList<Point>>();
		for (int k = 0; k < javaInputStrokes.length; k++) {
			ArrayList<Point> temparray = new ArrayList<Point>();
			for (int k2 = 0; k2 < javaInputStrokes[k].length; k2++) {
				temparray.add(new Point(javaInputStrokes[k][k2][0], javaInputStrokes[k][k2][1]));
			}
			inputStrokes.add(temparray);
		}
		
		long startTime = System.currentTimeMillis();
		//AWTIMEMain.traceJavaArray(inputStrokes);
		ArrayList<ResultCandidate> res = tomoe.getMatched(inputStrokes, 100);
		long progressTime = System.currentTimeMillis() - startTime;
		for(int i = 0; i < res.size(); ++i) {
			System.out.println("letter:" + res.get(i).letter + ", sccroe:" + res.get(i).score);
		}
		int len = (res!=null) ? res.size() : 0;
		System.out.println(len + "個候補を見つけました (処理時間 " + progressTime + "ms)");
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
