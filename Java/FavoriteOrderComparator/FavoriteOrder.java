import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 好みの順番データ
 */
public class FavoriteOrder {
	/** 文字コード(MS932) */
	private static final String CHAR_CODE_MS932 = "MS932";

	/** ファイル読み込みエラーの書式 */
	private static final String FORMAT_FILE_READ_ERROR = "エラー[ファイルの読み込みに失敗[パス:%s]]";

	/** 食べ物情報の各文字列 */
	private static final String[] FOODS = {"I", "R", "Y", "C", "S", "J", "F", "P", "U", "G", "H", "T"};

	/** 食べ物情報の文字列の桁数 */
	private static final int FOOD_STRING_LENGTH = FOODS[0].length();

	/** 食べ物情報の組の桁数 */
	private static final int FOOD_PAIR_LENGTH = FOOD_STRING_LENGTH * 2;

	/** 好みデータ */
	private Map<String, List<String>> favoriteMap;

	/** 制約データ */
	private Map<String, List<String>> restrictionMap;

	/**
	 * 好みデータを返す
	 * @return 好みデータ(キー:食べ物 値:キーの食べ物の次に好きな食べ物(集合))
	 */
	public Map<String, List<String>> getFavoriteMap() {
		return favoriteMap;
	}

	/**
	 * コンストラクタ
	 * @param filePath ファイルパス
	 */
	public FavoriteOrder(String filePath) {
		if (filePath == null || filePath.isEmpty()) {
			return;
		}

		// ファイルの読み込み
		File file = new File(filePath);
		List<String> list = readFile(file, CHAR_CODE_MS932);
		if (list == null || list.isEmpty()) {
			System.out.println(String.format(FORMAT_FILE_READ_ERROR, file.getAbsolutePath()));
			return;
		}

		// 好みデータと制約データを生成
		favoriteMap = new HashMap<String, List<String>>();
		restrictionMap = new HashMap<String, List<String>>();
		for (String favoriteInfo : list) {
			// 食べ物情報を取得(後者より前者の食べ物の方が好き)
			String food1 = favoriteInfo.substring(0, FOOD_STRING_LENGTH);
			String food2 = favoriteInfo.substring(FOOD_STRING_LENGTH, FOOD_PAIR_LENGTH);

			// 食べ物が同じ場合は無効
			if (food1.equals(food2)) {
				continue;
			}

			// 現在の好みの食べ物に続く食べ物データを取得し、データを更新
			List<String> favoriteFoodList = favoriteMap.get(food1);
			if (favoriteFoodList == null) {
				favoriteFoodList = new ArrayList<String>();
			}
			if (!favoriteFoodList.contains(food2)) {
				favoriteFoodList.add(food2);
				favoriteMap.put(food1, favoriteFoodList);
			}

			// 後者の食べ物を選ぶために必要な食べ物のデータを取得し、データを更新
			List<String> necessaryFoodList = restrictionMap.get(food2);
			if (necessaryFoodList == null) {
				necessaryFoodList = new ArrayList<String>();
			}
			if (!necessaryFoodList.contains(food1)) {
				necessaryFoodList.add(food1);
				restrictionMap.put(food2, necessaryFoodList);
			}
		}
	}

	/**
	 * 自身の持つ好みデータに従って論理的に可能な、好みの順番データの組み合わせを生成する
	 * @return 好みの順番データの組み合わせ<br />(List&lt;String&gt;:1組の好みの順番データ(好みの順に設定された食べ物の集合))
	 */
	public List<List<String>> createFavoriteOrderList() {
		// 初期化に失敗している場合は処理を中断
		if (restrictionMap == null) {
			return null;
		}

		// 追加条件がない食べ物を初期値として集合に追加
		List<List<String>> favoriteOrderList = new ArrayList<List<String>>();
		for (String food : FOODS) {
			List<String> necessaryFoodList = restrictionMap.get(food);
			if (necessaryFoodList == null) {
				List<String> favoriteOrder = new ArrayList<String>();
				favoriteOrder.add(food);
				favoriteOrderList.add(favoriteOrder);
			}
		}
		if (favoriteOrderList.isEmpty()) {
			return null;
		}

		// 何も追加されなくなるまで追加処理を繰り返す
		boolean isAdded;
		do {
			isAdded = false;
			Map<List<String>, List<List<String>>> newFavoriteOrderListMap = new LinkedHashMap<List<String>, List<List<String>>>();
			for (List<String> favoriteOrder : favoriteOrderList) {
				// 現在参照している好みの順番データに対し、追加可能な食べ物を取得
				List<String> addableFoodList = getAddableFoodList(favoriteOrder);
				if (addableFoodList == null || addableFoodList.isEmpty()) {
					continue;
				}

				// 食べ物が2つ以上ある場合は、好みの順番データのコピーを生成し、それに追加
				int foodCount = addableFoodList.size();
				if (foodCount > 1) {
					List<List<String>> list = new ArrayList<List<String>>();
					for (int i = 1; i < foodCount; i++) {
						List<String> copy = new ArrayList<String>(favoriteOrder);
						copy.add(addableFoodList.get(i));
						list.add(copy);
					}
					newFavoriteOrderListMap.put(favoriteOrder, list);
				}

				// 1つ目の食べ物はそのまま追加
				favoriteOrder.add(addableFoodList.get(0));
				isAdded = true;
			}

			// 好みの順番データが生成された場合は、元の順番データの次の位置に追加
			for (Entry<List<String>, List<List<String>>> entry : newFavoriteOrderListMap.entrySet()) {
				List<String> original = entry.getKey();
				List<List<String>> newFavoriteOrderList = entry.getValue();
				int index = favoriteOrderList.indexOf(original);
				favoriteOrderList.addAll(index + 1, newFavoriteOrderList);
			}
		} while (isAdded);

		return favoriteOrderList;
	}

	/**
	 * 指定されたファイルを文字列として読み込み、その内容を返す
	 * @param file ファイル
	 * @param characterCode 文字コード
	 * @return ファイルの内容(行単位)
	 */
	private static List<String> readFile(File file, String characterCode) {
		// 引数の確認
		if ((file == null || !file.exists() || !file.isFile()) || (characterCode == null || characterCode.isEmpty())) {
			return null;
		}

		// ファイルの内容を取得
		List<String> stringList = new ArrayList<String>();

		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader ism = new InputStreamReader(fis, characterCode);
			BufferedReader br = new BufferedReader(ism);
			String str;
			while ((str = br.readLine()) != null) {
				stringList.add(str.trim());
			}
			br.close();
			ism.close();
			fis.close();
		} catch(IOException e) {
			e.printStackTrace();
		}

		return stringList;
	}

	/**
	 * 指定された好みの順番データに追加可能な食べ物を返す
	 * @param favoriteOrder 好みの順番データ
	 */
	private List<String> getAddableFoodList(List<String> favoriteOrder) {
		if (favoriteOrder == null) {
			return null;
		}

		List<String> addableFoodList = new ArrayList<String>();
		for (String food : FOODS) {
			// 設定済みの食べ物は対象外
			boolean isSet = false;
			for (String setFood : favoriteOrder) {
				if (setFood.equals(food)) {
					isSet = true;
					break;
				}
			}
			if (isSet) {
				continue;
			}

			// 追加するために必要な食べ物がない場合は追加
			List<String> necessaryFoodList = restrictionMap.get(food);
			if (necessaryFoodList == null) {
				addableFoodList.add(food);
				continue;
			}

			// 必要な食べ物がある場合は、好みの順番データの中に、必要な食べ物が全て設定されている場合に追加
			boolean isAllSet = true;
			for (String necessaryFood : necessaryFoodList) {
				isSet = false;
				for (String setFood : favoriteOrder) {
					if (setFood.equals(necessaryFood)) {
						isSet = true;
						break;
					}
				}
				if (!isSet) {
					isAllSet = false;
					break;
				}
			}
			if (isAllSet) {
				addableFoodList.add(food);
			}
		}

		return addableFoodList;
	}
}