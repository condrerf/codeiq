package exam;


/**
 * prependした各文字列を標準出力に表示するプログラム
 */
public class Q4 {
	public static void main(String[] args) {
		SimpleList list = new SimpleListImpl();
		list = list.prepend("a");
		list = list.prepend("b");
		list = list.prepend("c");

		while (list != null) {
			System.out.println(list.getFirst());
			list = list.getRest();
		}
//		for (String str = list.getFirst(); str != null; str = list.getFirst()) {
//			System.out.println(str);
//			list.getRest();
//		}
	}
}

interface SimpleList {
    /**
     * @current A-B-C-D
     * @return A
     */
    public String getFirst();

    /**
     * @current A-B-C-D
     * @return B-C-D
     */
    public SimpleList getRest();

    /**
     * @current B-C-D
     * @param A
     * @return A-B-C-D
     */
    public SimpleList prepend(String value);
}

/**
 * SimpleListインタフェースの実装クラス
 */
class SimpleListImpl implements SimpleList {
	private String str;
	private SimpleList next;

	/**
	 * 集合の先頭の要素を返す
	 * @return 先頭の要素(要素が空の場合はnull)
	 */
	public String getFirst() {
		return str;
	}

	/**
	 * 集合の先頭の要素を削除する
	 * @return SimpleListインスタンス
	 */
	public SimpleList getRest() {
		return next;
	}

	/**
	 * 指定された文字列を集合の先頭に追加する
	 * @param value 文字列
	 * @return SimpleListインスタンス
	 */
	public SimpleList prepend(String value) {
		SimpleListImpl newList = new SimpleListImpl();
		newList.setStr(value);
		newList.setNext((str == null) ? null : this);
		return newList;
	}
	
	private void setStr(String str) {
		this.str = str;
	}
	private void setNext(SimpleList list) {
		next = list;
	}
//	// 先頭要素の操作しか行わないため、ListでなくLinkedListオブジェクトとして宣言
//	private LinkedList<String> strList = new LinkedList<String>();
//
//	/**
//	 * 集合の先頭の要素を返す
//	 * @return 先頭の要素(集合が空の場合はnull)
//	 */
//	public String getFirst() {
//		return strList.isEmpty() ? null : strList.getFirst();
//	}
//
//	/**
//	 * 集合の先頭の要素を削除する
//	 * @return SimpleListインスタンス
//	 */
//	public SimpleList getRest() {
//		strList.removeFirst();
//		return this;
//	}
//
//	/**
//	 * 指定された文字列を集合の先頭に追加する
//	 * @param value 文字列
//	 * @return SimpleListインスタンス
//	 */
//	public SimpleList prepend(String value) {
//		strList.addFirst(value);
//		return this;
//	}
}
