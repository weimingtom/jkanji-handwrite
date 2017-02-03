//******************************************************************************
// Spark - ActionScript2.0 Game Framework
// Copyright(C) 2005 BeInteractive!, all rights reserved.
//
// $Id: Tomoe.as 2 2007-03-26 03:25:55Z yossy $
//******************************************************************************

/**
 * 文字認識エンジン「巴」をベースとした、手書き文字認識クラス。
 * 
 * 巴：http://tomoe.sourceforge.jp/
 * 
 * @author  Y.Shindo
 * @see     NumberDictionary
 * @see     HiraganaDictionary
 * @version $Rev: 2 $
 */
 
class spark.tomoe.Tomoe
{
	private var dictionary:Array;
	
	/**
	 * 新しい文字認識クラスのインスタンスを生成します。
	 */
	public function Tomoe ()
	{
		dictionary = new Array();
	}
	
	/**
	 * 新しい辞書を追加します。
	 * 
	 * 辞書は [文字名, 1画目, 2画目, ..., n画目] という配列を要素とした配列です。
	 * 一画は [始点ポイント, 中継ポイント1, 中継ポイント2, ..., 中継ポイントn, 終点ポイント] という配列です。
	 * ポイントは [x座標, y座標] という配列です。
	 * 
	 * @param   dictionary 辞書配列
	 * @see   NumberDictionary
	 * @see   HiraganaDictionary
	 */
	public function addDictionary (dictionary:Array) : Void
	{
		this.dictionary = this.dictionary.concat(dictionary);
	}
	
	/**
	 * 手書き文字を判定し、最も近いと思われる文字を辞書から探します。
	 * 
	 * 結果は最大maxOut個を配列で返します。但し、必ずしも要素数はmaxOut個にはなりません。
	 * 全く候補が無い場合は、nullを返します。
	 * 
	 * 返される配列の要素は {letter: 文字, score: スコア} となります。スコアは小さければ小さいほど、
	 * 手書き文字に近いと判定されています。
	 * 
	 * 手書き文字の入力は [１画目, ２画目, ..., n画目] という配列で行います。
	 * 一画は [始点ポイント, 中継ポイント1, 中継ポイント2, ..., 中継ポイントn, 終点ポイント] という配列です。
	 * ポイントは [x座標, y座標] という配列です。
	 * 
	 * 各ポイントを単純化したりする必要はありません。但し、デフォルト辞書は 300x300 の正方形内に
	 * 書かれた文字を基準とした座標で登録されているので、それ以外の大きさの場合は、スケーリングを
	 * するようにして下さい。
	 * 例えば、100x100の正方形内に入力をさせたのなら、ポイントの座標を3倍する必要があります。
	 * 
	 * また、座標は入力領域の左上を(0, 0)とした座標系に予め変換しておくようにしてください。
	 * 入力領域が(100, 100)の位置にあるのならば、各ポイントの座標を100ずつ引く必要があります。
	 * 
	 * @param   inputStrokes 手書き文字の配列
	 * @param   maxOut       最大いくつの候補を返すか
	 * @return   最も手書き文字に近いと思われる文字とそのスコア。最大maxOut個。
	 */
	public function getMatched (inputStrokes:Array, maxOut:Number) : Array
	{
		// 入力文字の補正（単純化）
		inputStrokes = fixInputStrokes(inputStrokes);
		
		var inputStrokesLen:Number = inputStrokes.length;
		
		if(inputStrokesLen<=0)
		{
			// 判定する画が無いやん
			return null;
		}
		
		// 辞書リファレンス
		var dic:Array = dictionary;
		var dicLen:Number = dic.length;
		
		// 候補
		// 辞書から候補を選び出し、どんどんテストにかけていく事で絞り込む
		var candidates:Array = new Array();
		
		// とりあえず、画数を見て同じものだけ候補に追加
		for(var i:Number=0; i<dicLen; ++i)
		{
			// 先頭に文字名があるのでlength-1
			if((dic[i].length-1) == inputStrokesLen)
			{
				candidates.push({c:dic[i], score:0});
			}
		}
		
		// 1画ずつチェックして候補を絞り込む
		for(var i:Number=0; i<inputStrokesLen; ++i)
		{
			if(candidates.length==0)
			{
				// 候補がなくなってしまった．．．
				return null;
			}
			
			// 絞り込み
			candidates = narrowCandidates(inputStrokes[i], candidates);
		}
		
		// 残った候補をスコアでソート
		candidates.sortOn("score", Array.NUMERIC);
		
		// 結果を抽出
		var resultCandidates:Array = new Array();
		for(var i:Number=0; i<maxOut; ++i)
		{
			// もう候補がない
			if(!candidates[i]) break;
			
			resultCandidates.push({letter:candidates[i].c[0], score:candidates[i].score});
		}
		
		return resultCandidates;
	}
	
	
	/*
	 * 入力文字を単純化して、辞書との比較処理をしやすくする
	 */
	private function fixInputStrokes (inputStrokes:Array) : Array
	{
		var resultInputStrokes:Array = new Array();	// 補正後の手書き文字
		
		var inputStrokesLen:Number = inputStrokes.length;
		
		for(var i:Number=0; i<inputStrokesLen; ++i)
		{
			var inputStroke:Array = inputStrokes[i];	// (i+1)画目
			var inputStrokeLen:Number = inputStroke.length-1;
			
			var resultInputStroke:Array = new Array();	// 補正後の手書き文字の1画
			
			if(inputStrokeLen > 0)
			{
				var p:Array = inputStroke[0];	// 頂点0
				var q:Array = inputStroke[1];	// 頂点1
				
				// とりあえず頂点1は積んじゃう
				resultInputStroke.push([p[0], p[1]]);
				
				// ベクトル頂点0→1の角度
				var lastAngle:Number = Math.atan2((q[1]-p[1]), (q[0]-p[0]));
				
				// 角度の差の累積
				var total:Number = 0;
				
				for(var j:Number=1; ; ++j)
				{
					p = inputStroke[j];	// 頂点j
					q = inputStroke[j+1];	// 頂点(j+1)
					
					if(j < inputStrokeLen)
					{
						// ベクトル頂点j→(j+1)の角度
						var angle:Number = Math.atan2((q[1]-p[1]), (q[0]-p[0]));
						
						// 前回の角度との差を加算
						total += Math.abs(lastAngle - angle);
						
						// 保存
						lastAngle = angle;
						
						// 角度の差が一定値を越えたら新しい頂点を追加
						if(total >= 0.8)
						{
							// 但しあんまりにも線の長さが無い時はスルー（マウス等によるブレの補正）
							{
								var resultInputStrokeLen:Number = resultInputStroke.length;
								var rp:Array = resultInputStroke[resultInputStrokeLen-1];
								var xd:Number = rp[0] - p[0];
								var yd:Number = rp[1] - p[1];
								// 最後に追加した補正後の点から今追加しようとしている点までの距離（の2乗）と比較
								if((xd*xd+yd*yd) <= 30*30)
								{
									continue;
								}
							}
							
							total = 0;
							// 点を追加
							resultInputStroke.push([p[0], p[1]]);
						}
					}
					else
					{
						// もう最後の点（j==inputStroke）なので問答無用で追加
						resultInputStroke.push([p[0], p[1]]);
						
						break;
					}
				}
				
				// 1画完成したので追加
				resultInputStrokes.push(resultInputStroke);
			}
		}
		
		return resultInputStrokes;
	}
	
	/*
	 * inputStrokeとcandidatesを比較して、候補を絞り込む
	 */
	private function narrowCandidates (inputStroke:Array, candidates:Array) : Array
	{
		// 残った候補を入れる配列
		var resultCandidates:Array = new Array();
		
		var inputStrokeLen:Number = inputStroke.length;
		var candidatesLen:Number = candidates.length;
		
		var inputStrokeFirstX:Number = inputStroke[0][0];
		var inputStrokeFirstY:Number = inputStroke[0][1];
		var inputStrokeLastX:Number = inputStroke[inputStrokeLen-1][0];
		var inputStrokeLastY:Number = inputStroke[inputStrokeLen-1][1];
		
		for(var i:Number=0; i<candidatesLen; ++i)
		{
			// 候補を取り出す
			var candidate:Object = candidates[i];
			var candidateStrokes:Array = candidate.c;
			var candidateStrokesLen:Number = candidateStrokes.length-1;	// -1は文字名の分
			
			for(var j:Number=0; j<candidateStrokesLen; ++j)
			{
				var candidateStroke:Array = candidateStrokes[j+1];	// 候補の(j+1)画目
				
				var candidateStrokeLen:Number = candidateStroke.length;
				
				// あまりにも特徴点の数が違えばスルー
				if(Math.abs(inputStrokeLen-candidateStrokeLen) > 3)
				{
					continue;
				}
				
				// 1画の始点同士の距離を算出
				var candidateStrokeFirst:Number = candidateStroke[0];
				var dx:Number = candidateStrokeFirst[0] - inputStrokeFirstX;
				var dy:Number = candidateStrokeFirst[1] - inputStrokeFirstY;
				var d1:Number = (dx*dx+dy*dy);
				if(d1 > 90*90) continue;
				
				// 1画の終点同士の距離を算出
				var candidateStrokeLast:Number = candidateStroke[candidateStrokeLen-1];
				dx = candidateStrokeLast[0] - inputStrokeLastX;
				dy = candidateStrokeLast[1] - inputStrokeLastY;
				var d2:Number = (dx*dx+dy*dy);
				if(d2 > 90*90) continue;				
				
				// 入力画と候補の(j+1)画目を比較してスコア算出
				var score1:Number = calculateScore(inputStroke, candidateStroke);
				if(score1<0) continue;
				
				// 候補の(j+1)画目と入力画を比較してスコア算出
				var score2:Number = calculateScore(candidateStroke, inputStroke);
				if(score2<0) continue;
				
				// スコア加算
				candidate.score += (d1+d2+score1+score2);
				
				// この候補は残しておこう
				resultCandidates.push(candidate);
				
				break;
			}
		}
		return resultCandidates;
	}
	
	/**
	 * inputStrokeとmatchStrokeを比較してスコアを算出する。スコアが低いほど似ている。
	 * あまりにも違う場合、-1を返す。
	 */
	private function calculateScore (inputStroke:Array, matchStroke:Array) : Number
	{
		var score:Number = 0;
		
		var inputStrokeLen:Number = inputStroke.length;
		var matchStrokeLen:Number = matchStroke.length;
		
		// inputStrokeの各線分の角度を算出
		var inputStrokeAngle:Array = new Array();
		for(var i:Number=0; i<(inputStrokeLen-1); ++i)
		{
			var p:Array = inputStroke[i];
			var q:Array = inputStroke[i+1];
			inputStrokeAngle[i] = Math.atan2((q[1]-p[1]), (q[0]-p[0]));
		}
		
		// matchStrokeの各線分の角度を算出
		var matchStrokeAngle:Array = new Array();
		for(var i:Number=0; i<(matchStrokeLen-1); ++i)
		{
			var p:Array = matchStroke[i];
			var q:Array = matchStroke[i+1];
			matchStrokeAngle[i] = Math.atan2((q[1]-p[1]), (q[0]-p[0]));
		}
		
		// 最後に比較を中断した点から始められるように
		var matchStrokeOffset:Number = 0;
		
		// inputStroke主体で比較
		for(var i:Number=0; i<inputStrokeLen; ++i)
		{
			var p:Array = inputStroke[i];
			
			// 最後に比較を中断した点から続ける
			for(var j:Number = matchStrokeOffset; j<matchStrokeLen; ++j)
			{
				var dp:Array = matchStroke[j];
				
				// inputStroke[i]とmatchStroke[j]の距離を算出
				var dx:Number = (p[0]-dp[0]);
				var dy:Number = (p[1]-dp[1]);
				var d:Number = (dx*dx+dy*dy);
				
				if(j < (matchStrokeLen-1))
				{
					// 始点と始点の距離と線分の向き（角度）が一定以内
					if(d < 90*90 && Math.abs(inputStrokeAngle[i]-matchStrokeAngle[j]) < (Math.PI/2))
					{
						// この点の比較は終わり
						matchStrokeOffset = j;
						
						// スコア加算
						score += d;
						
						break;
					}
					// inputStroke[i]とベクトルmatchStroke[j]→[j+1]との距離で比較する（点と線分の距離）
					else
					{
						// ベクトルmatchStroke[j]→matchStroke[j+1] : A
						var ax:Number = (matchStroke[j+1][0]-dp[0]);
						var ay:Number = (matchStroke[j+1][1]-dp[1]);
						// ベクトルinputStroke[i]→matchStroke[j] : B
						var bx:Number = dx;
						var by:Number = dy;
						// 媒介変数tを求め、inputStroke[i]からAへの垂線が存在するか調べる
						var t:Number = (ax*bx+ay*by)/(ax*ax+ay*ay);
						
						if(t >= 0 && t <= 1)
						{
							// ベクトルmatchStroke[j]→交点
							ax *= t;
							ay *= t;
							// ベクトルinputStroke[i]→交点
							ax -= bx;
							ay -= by;
							// そのスカラー値が距離
							d = (ax*ax+ay*ay);
							
							// 距離と角度が一定以内
							if(d < 120*120 && Math.abs(inputStrokeAngle[i]-matchStrokeAngle[j]) < (Math.PI/2))
							{
								// この点の比較は終わり
								matchStrokeOffset = j;
								
								// スコア加算
								score += d;
								
								break;
							}
						}
					}
				}
				else
				{
					// 最後の点なので単純に距離比較だけ
					if(d < 90*90)
					{
						// この点の比較は終わり
						matchStrokeOffset = j;
						
						// スコア加算
						score += d;
						
						break;
					}
				}
			}
			// 近い距離の点が無く最後まで来てしまった．．．
			if(j >= matchStrokeLen)
			{
				return -1;
			}
		}
		return score;
	}
	
	/**
	 * letter に対応する辞書の項目に strokes を学習させる、もしくは追加します。
	 * 
	 * 辞書に既に同じ画数の letter が存在すれば、その字形を strokes で指定されたものに近い形に
	 * 修正をします。
	 * 
	 * それ以外の場合、辞書に新しく項目を追加します。
	 * 
	 * 配列の内容については他のメソッドを参照。
	 * 
	 * @param   letter  学習/追加する文字名
	 * @param   strokes 学習/追加する字形
	 * @return   更新（学習）した場合はtrue, 追加した場合はfalse
	 */
	public function study (letter:String, strokes:Array) : Boolean
	{
		// 入力文字の補正（単純化）
		strokes = fixInputStrokes(strokes);
		
		var strokesLen:Number = strokes.length;
		
		var dic:Array = dictionary;
		var dicLen:Number = dic.length;
		
		var modifyIndex:Number;
		
		for(var i:Number=0; i<dicLen; ++i)
		{
			var dicStrokes:Array = dic[i];
			
			// 文字名と画数を比較
			if(dicStrokes[0]==letter && (dicStrokes.length-1)==strokesLen)
			{
				for(var j:Number=0; j<strokesLen; ++j)
				{
					var stroke:Array = strokes[j];
					var dicStroke:Array = dicStrokes[j+1];	// +1は文字名分
					
					// 特徴点の数が同じ
					if(stroke.length==dicStroke.length)
					{
						// そのまま平均を取る
						var dicStrokeLen:Number = dicStroke.length;
						for(var n:Number=0; n<dicStrokeLen; ++n)
						{
							var p:Array = dicStroke[n];
							var q:Array = stroke[n];
							p[0] = (p[0]+q[0])/2;
							p[1] = (p[1]+q[1])/2;
						}
					}
					// 違う
					else
					{
						// 辞書の数に合わせて平均を取る
						var dicStrokeLen:Number = dicStroke.length;
						var div:Number = stroke.length/dicStrokeLen;
						for(var n:Number=0; n<dicStrokeLen; ++n)
						{
							var p:Array = dicStroke[n];
							var q:Array;
							if(n<(dicStrokeLen-1))
							{
								q = stroke[Math.floor(div*n)];
							}
							else
							{
								q = stroke[stroke.length-1];
							}
							p[0] = (p[0]+q[0])/2;
							p[1] = (p[1]+q[1])/2;
						}
					}
				}
				modifyIndex = i;
				break;
			}
		}
		
		// 既存の辞書には見つからなかった
		if(i==dicLen)
		{
			// 辞書に新しく追加
			var dicStrokes:Array = strokes.slice();
			dicStrokes.unshift(letter);
			dic.push(dicStrokes);
			
			return false;
		}
		
		return true;
	}
}