package question;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * データ分析クラス
 */
public class SimpleDataAnalysis {
	/** 県の数 */
	public static final int PREFECTURE_SIZE = 47;

	/** 文字コード(MS932) */
	private static final String CHAR_CODE_MS932 = "MS932";

	/** 改行コード */
	private static final String BR = System.getProperty("line.separator");

	/** 統計の開始年 */
	private static final String START_YEAR = "1935";

	/** 全国(=統計データの開始位置) */
	private static final String PREFS = "全　　　国";

	/** 都道府県名に付いている番号の桁数 */
	private static final int PREF_NAME_NUMBER_LENGTH = 2;

	/** 全角空白 */
	private static final String TWO_BYTE_SPACE = "　";

	/**
	 * データを格納するクラス
	 */
	public static class Data {
		/** 県の名前 */
		public String name = null;

		/** 上昇傾向ならtrue */
		public boolean upward = false;
	}

	/**
	 * 指定されたCSVの文字列を分析し、Dataオブジェクトの配列を返す
	 * @param csvStr CSVの文字列
	 * @return Dataオブジェクトの配列(CSVの分析が失敗した場合はnull)
	 */
	public static Data[] analys(String csvStr) {
		// 引数の確認
		if (csvStr == null || csvStr.isEmpty()) {
			return null;
		}

		// 改行コードをコンマに変換
		csvStr = csvStr.replace(BR, ",");

		// 文字列をコンマで分割
		String[] csvStrArray = csvStr.split(",");

		// 統計の開始位置と列数(統計の開始年～終了年(=「全国」の前の列))を取得
		int startYearIndex = Integer.MIN_VALUE;
		int statsStartIndex = Integer.MIN_VALUE;
		int statsColumnCount = Integer.MIN_VALUE;

		for (int i = 0; i < csvStrArray.length; i++) {
			String str = csvStrArray[i];

			if (str.equals(START_YEAR)) {
				startYearIndex = i;
			} else if (str.equals(PREFS)) {
				if (startYearIndex != Integer.MIN_VALUE) {
					statsStartIndex = i;
					statsColumnCount = statsStartIndex - startYearIndex;
				}

				break;
			}
		}

		if (statsStartIndex == Integer.MIN_VALUE || statsColumnCount == Integer.MIN_VALUE) {
			return null;
		}

		// 全国の人口上昇率を計算
		double countryRate = Double.NEGATIVE_INFINITY;

		if ((statsStartIndex+statsColumnCount) < csvStrArray.length) {
			try {
				String oldestPopulationString = csvStrArray[statsStartIndex+1];
				String newestPopulationString = csvStrArray[statsStartIndex+statsColumnCount];
				int oldestPopulation = Integer.parseInt(oldestPopulationString);
				int newestPopulation = Integer.parseInt(newestPopulationString);
				countryRate = (double) newestPopulation / oldestPopulation;
			} catch(NumberFormatException e) {
			}
		}

		if (countryRate == Double.NEGATIVE_INFINITY) {
			return null;
		}

		// 都道府県名と上昇傾向判定の集合を生成
		List<String> prefNameList = new ArrayList<String>();
		List<Boolean> upwardList = new ArrayList<Boolean>();
		int startIndex = statsStartIndex + statsColumnCount+1;

		for (int i = startIndex; i < csvStrArray.length; i += statsColumnCount+1) {
			// 全角空白と番号を除去した都道府県名を取得
			String prefName = csvStrArray[i];

			if (!prefName.isEmpty() && prefName.length() > PREF_NAME_NUMBER_LENGTH) {
				prefName = prefName.substring(PREF_NAME_NUMBER_LENGTH).replace(TWO_BYTE_SPACE, "");
			}

			// 人口上昇率を計算
			double rate = Double.NEGATIVE_INFINITY;

			try {
				String oldestPopulationString = csvStrArray[i+1];
				String newestPopulationString = csvStrArray[i+statsColumnCount];
				int oldestPopulation = Integer.parseInt(oldestPopulationString);
				int newestPopulation = Integer.parseInt(newestPopulationString);
				rate = (double) newestPopulation / oldestPopulation;
			} catch(NumberFormatException e) {
			}

			// 人口上昇率が計算できた場合は、都道府県名と上昇傾向判定を集合に追加
			if (rate != Double.NEGATIVE_INFINITY) {
				boolean upward = rate > countryRate;
				prefNameList.add(prefName);
				upwardList.add(upward);
			}
		}

		// Dataオブジェクトの配列を生成
		Data[] resData = new Data[PREFECTURE_SIZE];

		for (int i = 0; i < PREFECTURE_SIZE; i++) {
			Data data = new Data();

			if (i < prefNameList.size() && i < upwardList.size()) {
				data.name = prefNameList.get(i);
				data.upward = upwardList.get(i);
			}

			resData[i] = data;
		}

		return resData;
	}


	/**
	 * メインメソッド
	 * @param args 引数の配列
	 */
	public static void main(String[] args) {
		// 引数からファイルパスを取得
		String filePath = null;

		if (args != null && args.length > 0) {
			filePath = args[0];
		}

		// ファイルパスが取得できなかった場合は終了
		if (filePath == null || filePath.isEmpty()) {
			return;
		}

		// CSVファイルを取得
		File csvFile = new File(filePath);

		if (!csvFile.exists() || !csvFile.isFile()) {
			return;
		}

		// CSVファイルの内容を取得
		StringBuilder csv = new StringBuilder();

		try {
			FileInputStream fis = new FileInputStream(csvFile);
			InputStreamReader ism = new InputStreamReader(fis, CHAR_CODE_MS932);
			BufferedReader br = new BufferedReader(ism);
			String str;

			while ((str = br.readLine()) != null) {
				csv.append(str + BR);
			}

			br.close();
			ism.close();
			fis.close();
		} catch(IOException e) {
			e.printStackTrace();
		}

		// CSVからDataオブジェクトの配列を取得
		Data[] dataArray = analys(csv.toString());

		// 以下未定
	}
}
