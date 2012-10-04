package com.iteye.weimingtom.jkanji;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import spark.tomoe.HiraganaDictionary;
import spark.tomoe.HiraganaExtraDictionary;
import spark.tomoe.NumberDictionary;
import spark.tomoe.ResultCandidate;
import spark.tomoe.Tomoe;

public class AWTIMEMain implements MouseMotionListener, MouseListener, KeyListener{
	private final static boolean SHOW_TOMOE = true;
	
	private boolean isMouseDown = false;
	private Panel panel1;
	private TextField tf;
	private Button btn;
	private Button btn2;
	private ArrayList<ArrayList<Point>> inputStrokes;
	private ArrayList<Point> inputStroke;
	private int prevX;
	private int prevY;
	
	private Tomoe tomoe = new Tomoe();
	private XMLModelHandler model;
	
	public static void main(String[] args) {
		new AWTIMEMain().onCreate();
	}
	
	public void onCreate() {
		Frame f = new Frame();
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				test();
				try {
					model = new XMLModelHandler();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				clearCanvas();
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		f.setLayout(new FlowLayout());
		
		tf = new TextField();
		tf.setPreferredSize(new Dimension(180, 20));
		f.add(tf);
		
		btn = new Button("study");
		f.add(btn);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				study();
			}
		});
		
		btn2 = new Button("show stroke");
		f.add(btn2);
		btn2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showStroke();
			}
		});
		
		panel1 = new Panel();
		f.add(panel1);
		panel1.setLayout(new FlowLayout());
		
		panel1.setPreferredSize(new Dimension(300, 300));
		panel1.addMouseMotionListener(this);
		panel1.addMouseListener(this);
		panel1.addKeyListener(this);
		
		f.pack();
		f.setResizable(false);
		f.setLocationRelativeTo(null);
		f.setTitle("jkanji-java");
		f.pack();
		f.setVisible(true);
		
		tomoe.addDictionary(NumberDictionary.getDictionary());
		tomoe.addDictionary(HiraganaDictionary.getDictionary());
		tomoe.addDictionary(HiraganaExtraDictionary.getDictionary());
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (isMouseDown) {
			if (SHOW_TOMOE) {
				//System.out.println("mouseDragged " + isMouseDown);
			}
			addPoint(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		/*
		if (isMouseDown) {
			Graphics g = panel1.getGraphics();
			g.setColor(Color.blue);
			//g.drawLine(e.getX(), e.getY(), e.getX(), e.getY());	
			g.fillOval(e.getX(), e.getY(), 5, 5);
		}
		*/
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!isMouseDown) {
			if (e.getX() < 300 && e.getY() < 300) {
				if (SHOW_TOMOE) {
					System.out.println("mousePressed : " + e.getX() + ", " + e.getY());
				}
				isMouseDown = true;
				startStroke(e.getX(), e.getY());
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (isMouseDown) {
			isMouseDown = false;
			endStroke(e.getX(), e.getY());
		}
	}
	
	private void moveTo(int x, int y) {
		prevX = x;
		prevY = y;
	}
	
	private void lineTo(int x, int y) {
		Graphics g = panel1.getGraphics();
		g.drawLine(prevX, prevY, x, y);
		prevX = x;
		prevY = y;
	}
	
	private void startStroke(int x, int y) {
		if (SHOW_TOMOE) {
			System.out.println("startStroke");
		}
		inputStroke  = new ArrayList<Point>();
		inputStroke.add(new Point(x, y));
		
		//inputClip.moveTo(x, y);
		moveTo(x, y);
	}
	
	private void addPoint(int x, int y) {
		inputStroke.add(new Point(x, y));
		lineTo(x, y);
	}
	
	private void endStroke(int x, int y) {
		if (SHOW_TOMOE) {
			System.out.println("endStroke");
			System.out.println("inputStrokes " + inputStroke);
		}
		inputStrokes.add(inputStroke);
		long startTime = System.currentTimeMillis();
		ArrayList<ResultCandidate> res = tomoe.getMatched(inputStrokes, 30);
		long progressTime = System.currentTimeMillis() - startTime;
		//
		if (SHOW_TOMOE) {
			for(int i = 0; i < res.size(); ++i) {
				System.out.println("letter:" + res.get(i).letter + ", sccroe:" + res.get(i).score);
			}
			int len = (res!=null) ? res.size() : 0;
			System.out.println(len + "個候補を見つけました (処理時間 " + progressTime + "ms)");
		}
	}
	
	private void clearCanvas() {
		if (SHOW_TOMOE) {
			System.out.println("clearCanvas");
		}
		inputStrokes = new ArrayList<ArrayList<Point>>();
		
		Graphics g = panel1.getGraphics();
		g.clearRect(0, 0, panel1.getWidth(), panel1.getHeight());
		g.setColor(Color.blue);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (SHOW_TOMOE) {
			System.out.println("keyPressed : " + e.getKeyCode());
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			
			clearCanvas();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void study() {
		String letter = tf.getText();
		if (letter.length() >= 1) {
			if (tomoe.study(letter.charAt(0), inputStrokes)) {
				if (SHOW_TOMOE) {
					System.out.println("[" + letter + "] 学習しました");
				}
			} else {
				if (SHOW_TOMOE) {
					System.out.println("[" + letter + "] 追加しました");
				}
			}
		}
	}
	
	public static void  traceJavaArray(ArrayList<ArrayList<Point>> array) {
		String output = "int[][][] javaInputStrokes = {\n";
		for (int k = 0; k < array.size(); k++) {
			output += "{\n";
			for (int k2 = 0; k2 < array.get(k).size(); k2++) {
				//trace("inputStrokes[" + k + "][" + k2 + "]:" + 
				//	inputStrokes[k][k2][0] + ", " + inputStrokes[k][k2][1]);
				output += "{" + array.get(k).get(k2).getX() + ", " + array.get(k).get(k2).getY() + "}";
				if (k2 < array.get(k).size() - 1)
					output += ",";
			}
			output += "}";
			if (k < array.size() - 1)
				output += ",";
			output += "\n";
		}
		output += "};\n";
		if (SHOW_TOMOE) {
			System.out.println(output);
		}
	}
	
	private void test() {
		int[][][] javaInputStrokes = {
				{
				{105, 114},{105, 113},{107, 112},{115, 110},{121, 108},{130, 106},{151, 103},{164, 101},{176, 99},{184, 99},{190, 99},{195, 98},{199, 98},{202, 97},{203, 96}},
				{
				{189, 62},{185, 67},{180, 75},{172, 90},{161, 106},{152, 121},{146, 128},{142, 135},{136, 146},{134, 151},{132, 155},{132, 160},{132, 165},{132, 168},{132, 170},{133, 173},{136, 182},{141, 192},{146, 207},{149, 213},{151, 216},{157, 227},{163, 236},{165, 238},{166, 239},{167, 239},{168, 238},{173, 233}},
				{
				{185, 171},{178, 182},{173, 187},{168, 192},{162, 195},{155, 199},{147, 202},{141, 204},{135, 206},{129, 206},{123, 206},{120, 206},{115, 204},{113, 202},{112, 201},{108, 196},{107, 193},{104, 188},{103, 185},{100, 181},{98, 175},{95, 161},{95, 159},{95, 157},{99, 153},{103, 150},{107, 148},{116, 145},{121, 144},{128, 144},{147, 144},{158, 145},{171, 148},{190, 154},{202, 159},{210, 165},{213, 167},{215, 169},{216, 173},{217, 178},{217, 183},{217, 186},{217, 190},{217, 192},{216, 197},{215, 198},{215, 200},{214, 201},{213, 202}}
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
		traceJavaArray(inputStrokes);
		ArrayList<ResultCandidate> res = tomoe.getMatched(inputStrokes, 5);
		long progressTime = System.currentTimeMillis() - startTime;
		//
		if (SHOW_TOMOE) {
			for(int i = 0; i < res.size(); ++i) {
				System.out.println("letter:" + res.get(i).letter + ", sccroe:" + res.get(i).score);
			}
			int len = (res!=null) ? res.size() : 0;
			System.out.println(len + "個候補を見つけました (処理時間 " + progressTime + "ms)");
		}
	}
	
	private void showStroke() {
		if (false) {
			moveTo(51, 29);
			lineTo(117, 41);
			moveTo(99, 65);
			lineTo(219, 77);
			moveTo(27, 131);
			lineTo(261, 131);
			lineTo(129, 17);
			lineTo(57, 203);
			moveTo(111, 71);
			lineTo(219, 173);
			moveTo(81, 161);
			lineTo(93, 281);
			moveTo(99, 167);
			lineTo(207, 167);
			lineTo(189, 245);
			moveTo(99, 227);
			lineTo(189, 227);
			moveTo(111, 257);
			lineTo(189, 245);
		}
		
		clearCanvas();
		String letter = tf.getText();
        ArrayList<Point> ps = model.points.get(letter);
        ArrayList<Integer> se = model.seps.get(letter);
        if (ps != null && se != null) {
        	for (int i = 0; i < ps.size(); i++) {
        		if (se.contains(i)) {
        			moveTo(
        				(int)(ps.get(i).x / 1000.f * 300), 
        				(int)(ps.get(i).y / 1000.f * 300));
        		} else {
        			lineTo(
        				(int)(ps.get(i).x / 1000.f * 300), 
        				(int)(ps.get(i).y / 1000.f * 300));
        		}
        	}
        }
	}
}
