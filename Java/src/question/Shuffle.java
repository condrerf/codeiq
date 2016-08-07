/**
 * 
 */
package question;

import java.util.LinkedList;

/**
 * シャッフルプログラム
 */
public class Shuffle {
	/** N(配列の要素数:N×抜き出し単位) */
	private static final int N = 3;
	
	/** 抜き出し単位 */
	private static final int UNIT = 2;
	
	/** 抜き出し回数の最小値 */
	private static int minCount = Integer.MAX_VALUE;
	
	/** 順序の値を持つ配列の集合 */
	private static LinkedList<int[]> orderArrayList;

	/**
	 * メインメソッド
	 * @param args 引数
	 */
	public static void main(String[] args) {
		// 配列を生成し、昇順で順序を設定
		int[] array = new int[N * UNIT];
		for (int i = 0; i < array.length; i++) {
			array[i] = i;
		}
		
		// 初期値の配列を順序配列の集合の1つ目に設定
		orderArrayList = new LinkedList<int[]>();
		orderArrayList.add(array);
		
		// 整列を実行
		sort();
		
		// 抜き出し回数の最小値が設定されている場合は出力
		if (minCount < Integer.MAX_VALUE) {
			System.out.println(String.format("minCount: %d", minCount));
		}
	}
	
	/**
	 * 順序の整列(昇順→逆順)を行う
	 * @return true:整列完了 false:整列失敗
	 */
	private static boolean sort() {
		// 順序配列の集合を取得
		LinkedList<int[]> list = orderArrayList;
		
		// 集合が空の場合はfalse(通常は空にならない)
		int arrayCount = (list == null) ? 0 : list.size();
		if (arrayCount == 0) {
			return false;
		}

		// 集合の終端に設定されている配列を現在の配列として取得
		int[] array = list.getLast();
		
		// 配列から抜き出しを行えない場合はfalse(通常は行えない状況にならない)
		int indexCount = (array == null) ? 0 : array.length;
		if (indexCount <= UNIT) {
			return false;
		}
		
		// 整列済みかどうかを確認
		boolean isSorted = true;
		outerLoop: for (int i = 0; i < indexCount - 1; i++) {
			int index1 = array[i];
			for (int j = i + 1; j < indexCount; j++) {
				int index2 = array[j];
				if (index2 >= index1) {
					isSorted = false;
					break outerLoop;
				}
			}
		}
		
		// 整列済みの場合
		if (isSorted) {
			// 抜き出し回数がこれまでで最小の場合は更新
			int extractedCount = arrayCount - 1;
			if (extractedCount < minCount) {
				minCount = extractedCount;
			}
			return true;
		}

		// 現在の配列に対して抜き出しを行う
		int lastExtractableIndex = indexCount - UNIT;
		outerLoop : for (int i = 1; i <= lastExtractableIndex; i++) {
			// 抜き出し後の配列を生成
			int[] newArray = new int[indexCount];
			int endExtractIndex = i + UNIT;
			int index = 0;
			for (int j = i; j < endExtractIndex; j++) {
				newArray[index++] = array[j];
			}
			for (int j = 0; j < i; j++) {
				newArray[index++] = array[j];
			}
			for (int j = endExtractIndex; j < indexCount; j++) {
				newArray[index++] = array[j];
			}
			
			// 生成した配列と同一の順序構成の配列が既に存在する場合は次へ
			for (int[] historyArray : list) {
				boolean isDuplicated = true;
				for (int j = 0; j < indexCount; j++) {
					if (historyArray[j] != newArray[j]) {
						isDuplicated = false;
						break;
					}
				}

				if (isDuplicated) {
					continue outerLoop;
				}
			}
			
			// 新たな配列を集合に追加
			list.add(newArray);
			sort();
			list.removeLast();

//			// 新たな配列によって整列が完了した場合はtrue
//			if (sort()) {
//				return true;
//			// 完了しなかった場合は、追加した配列を除去
//			} else {
//				list.removeLast();
//			}
		}
		
		return false;
	}
}
