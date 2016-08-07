package question;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 青空文庫のHTMLから本文を抽出するクラス
 */
public class WebScraping {
	/** 抽出する文字数 */
	private static final int SCRAP_LENGTH = 100;

	/** 本文が記述されているタグのクラス名 */
	private static final String TEXT_CLASS_NAME = "main_text";

	/** 本文の正規表現 */
	private static final String TEXT_REGEX = String.format("<div class=\"%s\">(.*?)</div>", TEXT_CLASS_NAME);

	/** ふりがなの正規表現 */
	private static final String RUBY_REGEX = "<ruby><rb>(.*?)</rb>.*?</ruby>";

	/** 全角空白 */
	private static final String TWO_BYTE_SPACE = "　";

	/** 文字コード(MS932) */
	private static final String CHAR_CODE_MS932 = "MS932";

	/**
	 * 指定されたHTMLの文字列から、本文を抽出し、その内容を返す
	 * @param srcHtml HTML
	 * @return 抽出した本文
	 */
	public static String scraping(String srcHtml) {
		Pattern pattern = null;
		Matcher matcher = null;

		// テキストの抽出
		pattern = Pattern.compile(TEXT_REGEX, Pattern.DOTALL);
		matcher = pattern.matcher(srcHtml);

		if (!matcher.find()) {
			return null;
		}

		String text = matcher.group(1);

		// ふりがなの置換
		pattern = Pattern.compile(RUBY_REGEX, Pattern.DOTALL);
		matcher = pattern.matcher(text);
		text = matcher.replaceAll("$1");

		// タグと空白の除去
		text = text.replaceAll("<.*?>", "");
		text = text.replace(TWO_BYTE_SPACE, "");

		// 既定の文字数を超える場合はその文字数分を抽出
		String resTxt = (text == null || text.length() <= SCRAP_LENGTH) ? text : text.substring(0, SCRAP_LENGTH);

		return resTxt;
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

		// HTMLファイルを取得
		File htmlFile = new File(filePath);

		if (!htmlFile.exists() || !htmlFile.isFile()) {
			return;
		}

		// HTMLファイルの内容を取得
		StringBuilder htmlText = new StringBuilder();

		try {
			FileInputStream fis = new FileInputStream(htmlFile);
			InputStreamReader ism = new InputStreamReader(fis, CHAR_CODE_MS932);
			BufferedReader br = new BufferedReader(ism);
			String str;

			while ((str = br.readLine()) != null) {
				htmlText.append(str);
			}

			br.close();
			ism.close();
			fis.close();
		} catch(IOException e) {
			e.printStackTrace();
		}

		// 本文の抽出
		String text = scraping(htmlText.toString());

		// 以下未定
	}
}
