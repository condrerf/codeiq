package question;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 特定のキーワードにリンクを付加するクラス
 */
public class KeywordLink {
	/** 引数の索引(文章ファイル) */
	private static final int ARGS_INDEX_SENTENCE_FILE = 0;

	/** 引数の索引(キーワードファイル) */
	private static final int ARGS_INDEX_KEYWORD_FILE = 1;

	/** リンクの書式 */
	private static final String LINK_FORMAT = "<a href=\"%s\">%s</a>";

	/** リンクの正規表現 */
	private static final String LINK_REGEX = "<a href=\".*?</a>";

	/** エスケープ対象の文字 */
	private static final String[] ESCAPE_CHARACTERS = {"\\", "*", "+", ".", "?", "{", "}", "(", ")", "[", "]", "^", "$", "-", "|"};

	/** 文字コード(MS932) */
	private static final String CHAR_CODE_MS932 = "MS932";

	/**
	 * 指定された文章から、指定されたキーワードを検索し、該当する部分にリンクタグを付けた文章を返す
	 * @param sentence 文章
	 * @param KeyWords キーワード配列
	 * @return キーワードにリンクタグが付いた文章
	 */
	public static String addATag(String sentence, String[] KeyWords) {
		// 引数の確認文章がnullの場合はnullを返す
		if (sentence == null) {
			return null;
		}

		// 文章が空か、キーワードが指定されていない場合は文章をそのまま返す
		if (sentence.isEmpty() || (KeyWords == null || KeyWords.length == 0)) {
			return sentence;
		}

		// キーワード配列から置換用の集合を生成
		List<String> keywordList = getSortedKeywordList(Arrays.asList(KeyWords));

		// 各キーワードを参照
		String resStr = sentence;
		Pattern pattern;
		Matcher matcher;

		for (String keyword : keywordList) {
			// 現在参照しているキーワードが空の場合は次へ
			if (keyword == null || keyword.isEmpty()) {
				continue;
			}

			// キーワード内にエスケープ対象の文字が含まれる場合はエスケープ文字を追加
			for (String escapeCharacter : ESCAPE_CHARACTERS) {
				keyword = keyword.replace(escapeCharacter, "\\" + escapeCharacter);
			}

			// 現時点の文章に含まれる各リンクの範囲を取得
			Map<Integer, Integer> linkIndexMap = new HashMap<Integer, Integer>();
			pattern = Pattern.compile(LINK_REGEX, Pattern.DOTALL);
			matcher = pattern.matcher(resStr);

			while (matcher.find()) {
				linkIndexMap.put(matcher.start(), matcher.end());
			}

			// 現在参照しているキーワードと一致する、文章内の要素を取得
			pattern = Pattern.compile(keyword, Pattern.DOTALL);
			matcher = pattern.matcher(resStr);

			// 一致した各要素を参照
			StringBuffer buffer = new StringBuffer();
			String linkedKeyword = String.format(LINK_FORMAT, keyword, keyword);

			while (matcher.find()) {
				// 現在参照している位置の要素が、既に他のキーワードによってリンクされているかどうかを判定
				int startIndex = matcher.start();
				int endIndex = matcher.end();
				boolean isLinked = false;

				for (Entry<Integer, Integer> entry : linkIndexMap.entrySet()) {
					int linkStartIndex = entry.getKey();
					int linkEndIndex = entry.getValue();

					if ((startIndex >= linkStartIndex && startIndex <= linkEndIndex) || (endIndex >= linkStartIndex && endIndex <= linkEndIndex)) {
						isLinked = true;
						break;
					}
				}

				// リンクされていない場合は置換
				if (!isLinked) {
					matcher.appendReplacement(buffer, linkedKeyword);
				}
			}

			// 置換前の文章の内、置換対象の文字が含まれていない残りの部分を置換後の文章に追加し、置換後の文章として格納
			matcher.appendTail(buffer);
			resStr = buffer.toString();
		}

		// 置換後の文章を返す
		return resStr;
	}

	/**
	 * 指定されたキーワードの集合を置換用に整列した集合を返す
	 * (※別のキーワードの一部の文字列で構成されているキーワードを先に置換すると、正常に置換できないキーワードが発生するため、
     *    該当するキーワードを集合の末尾に移動させることで対応する)
	 * @param keywordList キーワードの集合
	 * @return 整列したキーワードの集合
	 */
	private static List<String> getSortedKeywordList(List<String> keywordList) {
		// 集合が空の場合はnullを返す
		if (keywordList == null) {
			return null;
		}

		// 各キーワードを参照
		int keywordCount = keywordList.size();
		List<String> sortedKeywordList = new ArrayList<String>();
		List<String> partKeywordList = new ArrayList<String>();

		for (int i = 0; i < keywordCount; i++) {
			// 現在参照しているキーワードが空の場合は次へ
			String keyword = keywordList.get(i);

			if (keyword == null || keyword.isEmpty()) {
				continue;
			}

			// 現在参照しているキーワードの内容が他のキーワードの一部に当たるかどうかを判定
			boolean isPartOfOtherKeyword = false;

			for (int j = i+1; j < keywordCount; j++) {
				String keyword2 = keywordList.get(j);

				if (keyword2 != null && keyword2.indexOf(keyword) != -1) {
					isPartOfOtherKeyword = true;
					break;
				}
			}

			// 他のキーワードの一部である場合は専用の集合へ、そうでない場合は整列済みの集合に追加
			if (isPartOfOtherKeyword) {
				partKeywordList.add(keyword);
			} else {
				sortedKeywordList.add(keyword);
			}
		}

		// 他のキーワードの一部であるキーワードがある場合は、それらを整列させ、集合に追加
		if (!partKeywordList.isEmpty()) {
			sortedKeywordList.addAll(getSortedKeywordList(partKeywordList));
		}

		// 整列済みの集合を返す
		return sortedKeywordList;
	}

	/**
	 * メインメソッド
	 * @param args 引数の配列
	 *              (1つ目:テキストファイルパス、
	 *               2つ目:キーワードファイルパス)
	 */
	public static void main(String[] args) {
		// 引数から各ファイルのパスを取得
		String sentenceFilePath = null;
		String keywordFilePath = null;

		if (args != null) {
			if (args.length > ARGS_INDEX_SENTENCE_FILE) {
				sentenceFilePath = args[ARGS_INDEX_SENTENCE_FILE];

				if (args.length > ARGS_INDEX_KEYWORD_FILE) {
					keywordFilePath = args[ARGS_INDEX_KEYWORD_FILE];
				}
			}
		}

		// ファイルパスが取得できなかった場合は終了
		if (sentenceFilePath == null) {
			System.out.println("エラー[文章ファイル未指定]");
			return;
		} else if (keywordFilePath == null) {
			System.out.println("エラー[キーワードファイル未指定]");
			return;
		}

		// 文章ファイルを開く
		File sentenceFile = new File(sentenceFilePath);

		if (!sentenceFile.exists() || !sentenceFile.isFile()) {
			System.out.println(String.format("エラー[文章ファイルが存在しない[パス:%s]]", sentenceFile.getAbsolutePath()));
			return;
		}

		// 文章ファイルの内容を取得
		StringBuilder sentence = new StringBuilder();

		try {
			FileInputStream fis = new FileInputStream(sentenceFile);
			InputStreamReader ism = new InputStreamReader(fis, CHAR_CODE_MS932);
			BufferedReader br = new BufferedReader(ism);
			String str;

			while ((str = br.readLine()) != null) {
				sentence.append(str);
			}

			br.close();
			ism.close();
			fis.close();
		} catch(IOException e) {
			e.printStackTrace();
		}

		// キーワードファイルを開く
		File keywordFile = new File(keywordFilePath);

		if (!keywordFile.exists() || !keywordFile.isFile()) {
			System.out.println(String.format("エラー[キーワードファイルが存在しない[パス:%s]]", keywordFile.getAbsolutePath()));
			return;
		}

		// キーワードファイルの内容を取得
		List<String> keywordList = new ArrayList<String>();

		try {
			FileInputStream fis = new FileInputStream(keywordFile);
			InputStreamReader ism = new InputStreamReader(fis, CHAR_CODE_MS932);
			BufferedReader br = new BufferedReader(ism);
			String str;

			while ((str = br.readLine()) != null) {
				keywordList.add(str.trim());
			}

			br.close();
			ism.close();
			fis.close();
		} catch(IOException e) {
			e.printStackTrace();
		}

		String[] keywords = keywordList.toArray(new String[keywordList.size()]);

		// リンク付きの文章を取得
		String linkedSentence = addATag(sentence.toString(), keywords);

		// 以下未定
	}
}
