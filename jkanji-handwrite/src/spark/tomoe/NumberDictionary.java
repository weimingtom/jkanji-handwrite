package spark.tomoe;

/**
 * 文字認識エンジン「巴」をベースとした、数字の認識辞書。
 * 
 * 巴：http://tomoe.sourceforge.jp/
 * 
 * @author  Y.Shindo
 * @see     TomoeDictionary
 * @version $Rev: 2 $
 */
public class NumberDictionary {
	public static DictionaryItem[] getDictionary() {
		return new DictionaryItem[] {
			new DictionaryItem(
				'0',
				new int[][][]{
					{{161, 51}, {109, 79}, {85, 146}, {99, 262}, {160, 281}, {222, 238}, {229, 143}, {157, 52}}
				}
			),
			new DictionaryItem(
				'1',
				new int[][][]{
					{{161, 45}, {131, 264}}
				} 
			),
			new DictionaryItem(
				'2',
				new int[][][]{
					{{86, 90}, {110, 62}, {164, 70}, {182, 132}, {78, 250}, {207, 256}} 
				}
			),
			new DictionaryItem(
				'3',
				new int[][][]{
					{{100, 62}, {160, 55}, {181, 115}, {122, 141}, {159, 147}, {184, 188}, {133, 240}, {100, 213}}
				} 
			),
			new DictionaryItem(
				'4',
				new int[][][]{
					{{156, 44}, {53, 166}, {242, 182}},
					{{169, 60}, {139, 247}}
				} 
			),
			new DictionaryItem(
				'5',
				new int[][][]{
					{{106, 39}, {64, 164}, {138, 159}, {170, 225}, {125, 254}, {74, 240}},
					{{112, 53}, {198, 66}}
				} 
			),
			new DictionaryItem(
				'6',
				new int[][][]{
					{{217, 52}, {167, 69}, {102, 165}, {119, 247}, {191, 230}, {186, 157}, {116, 160}}
				} 
			),
			new DictionaryItem(
				'7',
				new int[][][]{
					{{80, 62}, {87, 116}},
					{{83, 64}, {213, 75}, {175, 117}, {133, 255}}
				} 
			),
			new DictionaryItem(
				'7', /* １画バージョン */
				new int[][][]{
					{{83, 64}, {213, 75}, {175, 117}, {133, 255}}
				} 
			),
			new DictionaryItem(
				'8',
				new int[][][]{
					{{203, 94}, {151, 54}, {106, 93}, {112, 132}, {182, 199}, {192, 250}, {126, 270}, {89, 218}, {202, 95}}
				}
			),
			new DictionaryItem(
				'9',
				new int[][][]{
					{{195, 71}, {128, 35}, {86, 86}, {91, 120}, {137, 132}, {195, 82}, {139, 258}}
				}
			),
		};
	}
}

