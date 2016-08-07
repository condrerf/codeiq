import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;


/**
 * base64形式画像の生成クラス
 */
public class Base64ImageGenerator {
	/** 画像ディレクトリのパス */
	private static final String IMAGE_DIRECTORY_PATH = "src/";

	/** 対応するファイルの拡張子 */
	private static final String[] SUPPORT_EXTENSIONS = {"jpg", "jpeg"};

	/** 画像の大きさの上限 */
	private static final int MAX_SIZE = 100;

	/** 画像情報の書式 */
	private static final String FORMAT_IMAGE_INFO = "file:%s (width:%d, height:%d -> width:%d, height:%d)";

	/** 出力ファイル情報の書式 */
	private static final String FORMAT_OUTPUT_FILE_INFO = "output file:%s";

	/** 画像の形式 */
	private static final String IMAGE_FORMAT = "jpeg";

	/** imgタグの書式 */
	private static final String FORMAT_IMG_TAG = "<img src=\"data:image/%s;base64,%s\" />";

	/** 出力ファイルパス */
	private static final String OUTPUT_FILE_PATH = "dst.html";

	/** 文字コード */
	private static final String CHARACTER_CODE = "MS932";

	/** エラーメッセージの書式 */
	private static final String FORMAT_ERROR_MESSAGE = "no image file at:%s";

	/**
	 * メインメソッド
	 * @param args 引数の配列
	 */
	public static void main(String[] args) {
		// 画像を取得
		List<BufferedImage> imageList = getImageList();

		if (imageList == null || imageList.isEmpty()) {
			System.out.println(String.format(FORMAT_ERROR_MESSAGE, new File(IMAGE_DIRECTORY_PATH).getAbsolutePath()));
			return;
		}

		// 画像をBASE64に変換し、imgタグを生成
		List<String> base64List = convertToBase64(imageList);
		List<String> imageTagList = createImageTagList(base64List);

		// imgタグをファイルに出力
		StringBuilder imageTagString = new StringBuilder();

		for (String imageTag : imageTagList) {
			imageTagString.append(imageTag);
		}

		outputToFile(imageTagString.toString(), OUTPUT_FILE_PATH, CHARACTER_CODE);
	}

	/**
	 * 処理対象の画像(集合)を返す
	 * @return 画像の集合
	 */
	private static List<BufferedImage> getImageList() {
		// 画像ファイルを開く
		File directory = new File(IMAGE_DIRECTORY_PATH);

		if (!directory.exists() || !directory.isDirectory()) {
			return null;
		}

		List<BufferedImage> imageList = new ArrayList<BufferedImage>();
		List<String> supportExtensionList = Arrays.asList(SUPPORT_EXTENSIONS);

		for (File file : directory.listFiles()) {
			// 拡張子の確認
			String name = file.getName();
			int index = name.lastIndexOf(".");
			String extension = null;

			if (index != -1) {
				extension = name.substring(index+1).toLowerCase();
			}

			if (extension == null || !supportExtensionList.contains(extension)) {
				continue;
			}

			// ファイルを画像として取得
			BufferedImage image = null;
			try {
				image = ImageIO.read(file);
			} catch(IOException e) {
				e.printStackTrace();
			}
			if (image == null) {
				continue;
			}

			// 画像の大きさが規定値以下の場合はそのままの大きさで集合に追加
			int width = image.getWidth();
			int height = image.getHeight();
			int largerSize = (width > height) ? width : height;

			if (largerSize <= MAX_SIZE) {
				imageList.add(image);
				continue;
			}

			// 縮尺を求め、規定値以下の大きさの画像を生成
			int scaledWidth;
			int scaledHeight;

			if (width > height) {
				double scale = (double) MAX_SIZE / width;
				scaledWidth = MAX_SIZE;
				scaledHeight = (int) (height * scale);
			} else {
				double scale = (double) MAX_SIZE / height;
				scaledWidth = (int) (width * scale);
				scaledHeight = MAX_SIZE;
			}

			Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
			BufferedImage bufferedImage = new BufferedImage(scaledWidth, scaledHeight, image.getType());
			Graphics graphics = bufferedImage.getGraphics();
			graphics.drawImage(scaledImage, 0, 0, null);
			graphics.dispose();
			imageList.add(bufferedImage);
			System.out.println(String.format(FORMAT_IMAGE_INFO, file.getAbsolutePath(), width, height, scaledWidth, scaledHeight));
		}

		return imageList;
	}

	/**
	 * 指定された画像の集合の各要素をBASE64形式の文字列に変換する
	 * @param imageList 画像の集合
	 * @return BASE64形式の文字列の集合
	 */
	private static List<String> convertToBase64(List<BufferedImage> imageList) {
		if (imageList == null || imageList.isEmpty()) {
			return null;
		}

		List<String> base64List = new ArrayList<String>();

		for (BufferedImage image : imageList) {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
		        BufferedOutputStream os = new BufferedOutputStream(bos);
		        ImageIO.write(image, IMAGE_FORMAT, os);
		        os.flush();
		        os.close();
				String base64 = Base64.encode(bos.toByteArray());
				base64List.add(base64);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		return base64List;
	}

	/**
	 * 指定されたBASE64形式の文字列の集合を使用して、imgタグの集合を生成する
	 * @param base64List
	 * @return
	 */
	private static List<String> createImageTagList(List<String> base64List) {
		if (base64List == null || base64List.isEmpty()) {
			return null;
		}

		List<String> imageTagList = new ArrayList<String>();

		for (String base64 : base64List) {
			String imageTag = String.format(FORMAT_IMG_TAG, IMAGE_FORMAT, base64);
			imageTagList.add(imageTag);
		}

		return imageTagList;
	}

	/**
	 * 指定された文字列をファイルに出力する
	 * @param 文字列
	 * @param filePath ファイルパス
	 * @param characterCode 文字コード
	 * @return true:出力成功 false:出力失敗
	 */
	private static boolean outputToFile(String string, String filePath, String characterCode) {
		if (string == null || filePath == null || characterCode == null) {
			return false;
		}

		try {
			// ファイルを開く
			File file = new File(filePath);
			FileOutputStream fOut = new FileOutputStream(file, false);
			OutputStreamWriter out = new OutputStreamWriter(fOut, characterCode);
			BufferedWriter writer = new BufferedWriter(out);

			// ファイルに出力
			writer.write(string);
			writer.newLine();

			// ファイルを閉じる
			writer.close();
			out.close();
			fOut.close();
			System.out.println(String.format(FORMAT_OUTPUT_FILE_INFO, file.getAbsolutePath()));

			return true;
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}