import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 好みの順番を比較するクラス(メインクラス)
 */
public class FavoriteOrderComparator {
	/** 引数の索引(順番ファイル1) */
	private static final int ARGS_INDEX_FILE1 = 0;

	/** 引数の索引(順番ファイル2) */
	private static final int ARGS_INDEX_FILE2 = 1;

	/**
	 * メインメソッド
	 * @param args 引数の配列
	 */
	public static void main(String[] args) {
		// 引数から各ファイルのパスを取得
		String filePath1 = null;
		String filePath2 = null;

		if (args != null) {
			if (args.length > ARGS_INDEX_FILE1) {
				filePath1 = args[ARGS_INDEX_FILE1];

				if (args.length > ARGS_INDEX_FILE2) {
					filePath2 = args[ARGS_INDEX_FILE2];
				}
			}
		}

		// ファイルパスが取得できなかった場合は終了
		if (filePath1 == null) {
			System.out.println("エラー[ファイル1未指定]");
			return;
		} else if (filePath2 == null) {
			System.out.println("エラー[ファイル2未指定]");
			return;
		}

		// 好みの順番オブジェクトを生成し、基本となる好みの順番データと、比較用の好みデータを取得
		FavoriteOrder favoriteOrder1 = new FavoriteOrder(filePath1);
		FavoriteOrder favoriteOrder2 = new FavoriteOrder(filePath2);
		List<List<String>> favoriteOrderList = favoriteOrder1.createFavoriteOrderList();
		Map<String, List<String>> favoriteMap = favoriteOrder2.getFavoriteMap();
		if (favoriteOrderList == null || favoriteMap == null) {
			return;
		}

		// 最善の好みの順番データを求める
		List<String> bestFavoriteOrder = null;
		List<String> leastCompromiseOrderList = null;
		for (List<String> favoriteOrder : favoriteOrderList) {
			List<String> compromiseOrderPairList = new ArrayList<String>();
			for (Entry<String, List<String>> entry : favoriteMap.entrySet()) {
				// 基準となる食べ物と、その食べ物の次に好きな食べ物の集合を取得
				String food1 = entry.getKey();
				List<String> foodList = entry.getValue();
				if (food1 == null || foodList == null) {
					continue;
				}

				// 両者の食べ物について、現在参照している好みの順番データに設定されている位置を取得して比較し、
				// 後者の食べ物の位置の方が前である場合(後者の方が好み)は、両者をつなげた文字列を組として集合に追加
				int index1 = favoriteOrder.indexOf(food1);
				if (index1 == -1) {
					continue;
				}
				for (String food2 : foodList) {
					int index2 = favoriteOrder.indexOf(food2);
					if (index2 == -1) {
						continue;
					}
					if (index2 < index1) {
						String pair = food1 + food2;
						compromiseOrderPairList.add(pair);
					}
				}
			}

			// 妥協した順番の組がこれまでよりも少ない場合は、その組データと好みの順番データを格納
			if (leastCompromiseOrderList == null || compromiseOrderPairList.size() < leastCompromiseOrderList.size()) {
				leastCompromiseOrderList = compromiseOrderPairList;
				bestFavoriteOrder = favoriteOrder;
			}
		}

		// 処理結果を出力
		if (bestFavoriteOrder != null) {
			// 好みの順番
			StringBuilder builder = new StringBuilder();
			for (String food : bestFavoriteOrder) {
				builder.append(food);
			}
			System.out.println(builder.toString());

			// 妥協した順番
			if (leastCompromiseOrderList != null) {
				builder.setLength(0);
				for (String pair : leastCompromiseOrderList) {
					if (builder.length() > 0) {
						builder.append(",");
					}
					builder.append(pair);
				}
				System.out.println(builder.toString());
			}
		}
	}
}