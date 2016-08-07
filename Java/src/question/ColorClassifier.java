package question;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 画像の色判定を行うクラス
 */
public class ColorClassifier {
	// 色を表す定数
	public static final int COLOR_TYPE_BLOKEN  = -1;	// 画像は壊れている
	public static final int COLOR_TYPE_UNKNOWN = 0;		// 画像は分類不能である
	public static final int COLOR_TYPE_RED     = 1;		// 画像は赤色中心である
	public static final int COLOR_TYPE_YELLOW  = 2;		// 画像は黄色中心である
	public static final int COLOR_TYPE_GREEN   = 3;		// 画像は緑色中心である
	public static final int COLOR_TYPE_AQUA    = 4;		// 画像は水色中心である
	public static final int COLOR_TYPE_BLUE    = 5;		// 画像は青色中心である
	public static final int COLOR_TYPE_PURPLE  = 6;		// 画像は紫色中心である

	/** 色の種類数(破損除く) */
	private static final int COLOR_TYPE_COUNT  = 7;

	/** 引数の索引(画像ファイル) */
	private static final int ARGS_INDEX_IMAGE_FILE = 0;

	/** 色の閾値(RGBの各要素に対し、この値以上の場合にその色の傾向があると判定する) */
	private static final int COLOR_THRESHOLD = 128;

	/**
	 * 指定されたファイルを画像として各画素の色を判定し、判定結果を返す
	 * @param f ファイル
	 * @return 色の判定結果
	 */
	public static int classify(File f) {
		// 指定されたファイルを画像として取得
		BufferedImage image = null;

		try {
			image = ImageIO.read(f);
		} catch(IOException e) {
			e.printStackTrace();
		}

		// 取得に失敗した場合はCOLOR_TYPE_BLOKENを返す
		if (image == null) {
			return COLOR_TYPE_BLOKEN;
		}

		// 画像の各画素を参照し、色の種類別に判定回数を集計
		int[] colorTypeCountArray = new int[COLOR_TYPE_COUNT];
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
            	// 画素の色をRGBに分解
                int rgb = image.getRGB(x, y);
                int r = rgb >> 16 & 0xff;
	            int g = rgb >> 8 & 0xff;
	            int b = rgb & 0xff;

	            // RGBの各値から、色の傾向を判定
	            boolean isRed = (r >= COLOR_THRESHOLD);
	            boolean isGreen = (g >= COLOR_THRESHOLD);
	            boolean isBlue = (b >= COLOR_THRESHOLD);
	    		int colorType = COLOR_TYPE_UNKNOWN;

	    		// 赤
	            if (isRed) {
	            	// 赤・緑→黄(※青は閾値未満である必要がある)
	            	if (isGreen) {
	            		if (!isBlue) {
		            		colorType = COLOR_TYPE_YELLOW;
	            		}
		            // 赤・青→紫
	            	} else if (isBlue) {
	            		colorType = COLOR_TYPE_PURPLE;
	            	// 赤
	            	} else {
	            		colorType = COLOR_TYPE_RED;
	            	}
	            // 緑
	            } else if (isGreen) {
		            // 緑・青→水
	            	if (isBlue) {
	            		colorType = COLOR_TYPE_AQUA;
	            	// 緑
	            	} else {
	            		colorType = COLOR_TYPE_GREEN;
	            	}
	            // 青
	            } else if (isBlue) {
	            	colorType = COLOR_TYPE_BLUE;
	            }

	            // 判定回数を加算
	            colorTypeCountArray[colorType]++;
            }
        }

        // 各色の判定回数を比較し、最大の要素を画像の色とみなす
        int maxColorTypeCount = Integer.MIN_VALUE;
		int colorType = COLOR_TYPE_UNKNOWN;

        for (int i = 0; i < COLOR_TYPE_COUNT; i++) {
        	int colorTypeCount = colorTypeCountArray[i];

        	if (colorTypeCount > maxColorTypeCount) {
        		colorType = i;
        		maxColorTypeCount = colorTypeCount;
        	}
        }

		return colorType;
	}

	/**
	 * メインメソッド
	 * @param args 引数の配列(1つ目:画像ファイルパス)
	 */
	public static void main(String[] args) {
		// 引数から画像ファイルのパスを取得
		String imageFilePath = null;

		if (args != null) {
			if (args.length > ARGS_INDEX_IMAGE_FILE) {
				imageFilePath = args[ARGS_INDEX_IMAGE_FILE];
			}
		}

		// ファイルパスが取得できなかった場合は終了
		if (imageFilePath == null) {
			System.out.println("エラー[画像ファイル未指定]");
			return;
		}

		// 画像ファイルを開く
		File imageFile = new File(imageFilePath);

		// 指定されたファイルオブジェクトが存在しないか、ファイルでない場合は終了
		if (!imageFile.exists()) {
			System.out.println(String.format("エラー[ファイルが存在しない[パス:%s]]", imageFile.getAbsolutePath()));
			return;
		} else if (!imageFile.isFile()) {
			System.out.println(String.format("エラー[指定されたパスはファイルでない[パス:%s]]", imageFile.getAbsolutePath()));
			return;
		}

		// 画像の色の判定結果を取得
		int classifyCode = classify(imageFile);

		// 以下未定
	}
}
