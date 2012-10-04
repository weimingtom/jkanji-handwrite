package spark.tomoe;

public class DictionaryItem {
	public char c;
	public int[][][] d;
	
	public DictionaryItem(char c, int[][][] d) {
		this.c = c;
		this.d = d;
	}
	
	public int getStrokesLen() {
		return d.length;
	}
	
	public String toJavaString() {
		StringBuffer sb = new StringBuffer();
		sb.append("new DictionaryItem(\n");
		sb.append("'" + c + "',\n");
		sb.append("new int[][][] {\n");
		for (int i = 0; i < d.length; i++) {
			sb.append("{");
			for (int j = 0; j < d[i].length; j++) {
				sb.append("{" + d[i][j][0] + ", " + d[i][j][1] + "},");
			}
			sb.append("},\n");
		}
		sb.append("}\n");
		sb.append("),\n");
		return sb.toString();
	}
	
	public String toAS3String() {
		StringBuffer sb = new StringBuffer();
		sb.append("[\n");
		sb.append("'" + c + "',\n");
		sb.append("[\n");
		for (int i = 0; i < d.length; i++) {
			sb.append("[");
			for (int j = 0; j < d[i].length; j++) {
				if (j == d[i].length - 1) {
					sb.append("[" + d[i][j][0] + ", " + d[i][j][1] + "]");
				} else {
					sb.append("[" + d[i][j][0] + ", " + d[i][j][1] + "],");
				}
			}
			if (i == d.length - 1) {
				sb.append("]\n");
			} else {
				sb.append("],\n");
			}
		}
		sb.append("]\n");
		sb.append("],\n");
		return sb.toString();
	}
}
