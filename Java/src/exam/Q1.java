package exam;

/**
 * 文字列の配列を入力として受け取り、カンマ区切りのひとつの文字列を出力するプログラム
 */
public class Q1 {
	public static void main(String[] args) {
		String[] array = new String[] {"abc", "def", "ghi"};
		String result = createCsv(array);
		System.out.println(result);
	}

	/**
	 * 指定された文字列配列からCSVを生成する
	 * @param strArray 文字列配列
	 * @return CSV(strArrayがnullの場合はnull)
	 */
	private static String createCsv(String[] strArray) {
		if (strArray == null) {
			return null;
		}

		StringBuilder csv = new StringBuilder();
		for (int i = 0; i < strArray.length; i++) {
			if (i > 0) {
				csv.append(",");
			}
			csv.append(strArray[i]);
		}

		return csv.toString();
	}
}

