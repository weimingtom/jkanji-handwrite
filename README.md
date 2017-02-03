jkanji-handwrite
================

Porting libspark Tomoe (in AS2) to Java AWT.  
This is only a simple demo for porting to android.  

## references

1. 文字認識エンジン「巴」 (see tomoe-0.6.0.tar.gz/data/dict.dtd and *.xml)    

	> http://tomoe.sourceforge.jp/ (**dead link**)  
	> https://sourceforge.net/projects/tomoe/  
	> https://osdn.jp/projects/tomoe/  

2. libspark (see src/spark/tomoe/Tomoe.java)  

	> http://www.libspark.org/  
	> http://lab.libspark.org/trac/browser/as2/tomoe (**dead link**)    
	> http://www.libspark.org/svn/as2/Tomoe/  
	> http://www.libspark.org/wiki/beinteractive   
	> http://iphone.be-interactive.org/  

3. tegaki (for training data) (see src\spark\tomoe\HiraganaExtraDictionary.java)

	> https://github.com/tegaki/tegaki/tree/master/tegaki-models/data/train/japanese

## Files from other project  
* spark.tomoe.Tomoe (and other class under spark.tomoe package except HiraganaExtraDictionary)     
Classes Ported from http://www.libspark.org/svn/as2/Tomoe/  
Tomoe is the core class to analyze input stroke data.  
Usage:  
	> private Tomoe tomoe = new Tomoe();  
	> tomoe.addDictionary(NumberDictionary.getDictionary());  
	> tomoe.addDictionary(HiraganaDictionary.getDictionary());  
	> tomoe.addDictionary(HiraganaExtraDictionary.getDictionary());  
	> ArrayList<ResultCandidate> res = tomoe.getMatched(inputStrokes, 30);  
	> tomoe.study(letter.charAt(0), inputStrokes)  

* spark.tomoe.HiraganaExtraDictionary  
This class is not ported from original as2 project.  
It's the code generated from katakana.charcol and hiragana.charcol, using CharColXMLModelHandler.main()  

* com.iteye.weimingtom.jkanji.CharColXMLModelHandler  
See HiraganaExtraDictionary  

* com.iteye.weimingtom.jkanji.XMLModelHandler  
Try to analyze handwriting-ja.xml (large amount, so it's not embeded in java code like HiraganaExtraDictionary)    

* com.iteye.weimingtom.jkanji.AWTIMEMain  
The code depend on Java AWT GUI, for strokes inputed by mouse on Windows.  
It's used to show strokes of kanji in left text box, see screenshot.  
It's also used to analyze strokes drawed in right space and output recognized kanji to console.   
(Press space key to clean input stroke area)    
**NOTE: This class is not written well, input stroke area will be cleaned if the window go to background**   
And,   
This class is ported to Android, see here:  
https://github.com/weimingtom/jkanji-android_4_2/blob/master/jkanji-android_v4.2/src/com/iteye/weimingtom/jkanji/HandInputView.java  
(not use xml data, only from data embeded in Java code)     

* handwriting-ja.xml and handwriting-ja_zinnia.xml  
See https://sourceforge.net/projects/tomoe/files/tomoe/tomoe-0.6.0/  
This file is found in tomoe-0.6.0.tar.gz/tomoe-0.6.0/data/handwriting-ja.xml      
and https://github.com/tegaki/tegaki/tree/master/tegaki-models/data/train/japanese  
Used in XMLModelHandler, for Japanese kanji's strokes    

* katakana.charcol and hiragana.charcol  
See https://github.com/tegaki/tegaki/tree/master/tegaki-models/data/train/japanese  
Used in CharColXMLModelHandler, for recognizing katakana and hiragana strokes      

* dict.dtd  
See https://sourceforge.net/projects/tomoe/files/tomoe/tomoe-0.6.0/  
This file is found in tomoe-0.6.0.tar.gz/tomoe-0.6.0/data/dict.dtd     

## Screenshot  
* Press show stroke button in AWTIMEMain  
![Snapshot001](/screenshot/screenshot_001.png)  

* Recognize result after handwriting in AWTIMEMain    
![Snapshot002](/screenshot/screenshot_002.png)  
