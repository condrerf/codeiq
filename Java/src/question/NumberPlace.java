package question;

import java.util.*;

/**
 * ナンプレ
 */
class NumberPlace{
    /** 配列の大きさ */
    private static final int ARRAY_SIZE = 4;

    /** 配列の索引の最大値 */
    private static final int MAX_ARRAY_INDEX = ARRAY_SIZE - 1;

    private static int[][] numbers = new int[4][4];

    public static void main(String[] args) {
        NumberPlace nm = new NumberPlace();
        nm.init();
        nm.show();

        nm.calc();
        nm.show();
    }

    public void init(){

        NumberPlace nm = new NumberPlace();

        do {
            // 全てのボックスで数字が打たれるまで繰り返す
            nm.setInitNumber();

        } while (
            (nm.sum(nm.getBox(0, 0)) == 0) ||
            (nm.sum(nm.getBox(0, 2)) == 0) ||
            (nm.sum(nm.getBox(2, 0)) == 0) ||
            (nm.sum(nm.getBox(2, 2)) == 0) );

    }

    // 行の情報を取得
    public int[] getRow(int i){
        int[] n = numbers[i];
        int[] result = { n[0], n[1], n[2], n[3] };
        return result;
    }

    // 縦列の情報を取得
    public int[] getColumn(int i){
        int[][] n = numbers;
        int[] result = { n[0][i], n[1][i], n[2][i], n[3][i] };
        return result;
    }

    // 2*2のボックスごとの情報を取得
    public int[] getBox(int y, int x){

        int[][] n = numbers;
        int[] base = {(int)Math.floor(y / 2) * 2, (int)Math.floor(x / 2) * 2};

        int[] result = { n[base[0]][base[1]],
                         n[base[0]][base[1] + 1],
                         n[base[0] + 1][base[1]],
                         n[base[0] + 1][base[1] + 1] };

        return result;
    }

    // 配列内の計算
    public int sum(int[] array){
        int result = 0;
        for(int i = 0; i < array.length; i++){
            result += array[i];
        }
        return result;
    }

    // 表示用
    public void show(){

        for(int i = 0; i< numbers.length; i++){
            for(int j = 0; j< numbers[i].length; j++){
                System.out.print(numbers[i][j] + ",");
            }
            System.out.println();
        }

    }

    // ランダムに4点打つ
    public boolean setInitNumber(){

        numbers = new int[4][4];

        ArrayList<Integer> dots1 = new ArrayList<Integer>();
        ArrayList<Integer> dots2 = new ArrayList<Integer>();
        for(int i = 0; i < 4; i++){
            dots1.add(i);
            dots2.add(i);
        }

        // ランダムな順序にする
        Collections.shuffle(dots1);
        Collections.shuffle(dots2);

        for(int i = 0; i < 4; i++){
            numbers[dots1.get(i)][dots2.get(i)] = i + 1;
        }

        return true;
    }

    // 作成メソッド
    public void calc() {
        // 最初の要素から順に最後の要素まで数字設定の試行処理を繰り返す
        if (!setNumber(0, 0)) {
            System.out.println("設定失敗");
        }
    }

    /**
     * 指定された行・列に数字を設定する
     * @param rowIndex 行
     * @param columnIndex 列
     * @return true:最後の要素まで設定成功 false:設定失敗
     */
    private boolean setNumber(int rowIndex, int columnIndex) {
        // 行列を格納
        int[][] n = numbers;

        // 指定された行・列に既に数字が設定されている場合(初期化処理時に数字が設定された要素)
        if (n[rowIndex][columnIndex] != 0) {
            // 最後の要素である場合はtrue
            if (isLast(rowIndex, columnIndex)) {
                return true;
            }

            // 次に設定すべき行・列の位置を取得
            int nextRowIndex;
            int nextColumnIndex;

            if (columnIndex == MAX_ARRAY_INDEX) {
                nextRowIndex = rowIndex + 1;
                nextColumnIndex = 0;
            } else {
                nextRowIndex = rowIndex;
                nextColumnIndex = columnIndex + 1;
            }

            // 次の行・列への数字の設定を試行
            // (初期化処理時に数字が設定された要素は、次の要素の数字の設定に失敗しても数字の初期化は行わない)
            return setNumber(nextRowIndex, nextColumnIndex);
        }

        // 各数字を参照
        for (int i = 1; i <= ARRAY_SIZE; i++) {
            // 現在参照している行・列に対し、現在参照している数字が設定できない場合は次へ
            if (!canSet(rowIndex, columnIndex, i)) {
                continue;
            }

            // 数字を設定
            n[rowIndex][columnIndex] = i;

            // 最後の要素である場合はtrue
            if (isLast(rowIndex, columnIndex)) {
                return true;
            }

            // 次に設定すべき行・列の位置を取得
            int nextRowIndex;
            int nextColumnIndex;

            if (columnIndex == MAX_ARRAY_INDEX) {
                nextRowIndex = rowIndex + 1;
                nextColumnIndex = 0;
            } else {
                nextRowIndex = rowIndex;
                nextColumnIndex = columnIndex + 1;
            }

            // 次の行・列への数字の設定を試行し、最後の要素まで設定できた場合はtrue
            if (setNumber(nextRowIndex, nextColumnIndex)) {
                return true;
            }

            // 最後の要素まで設定できなかった場合は、現在の行・列の数字を初期化し、新たな数字で再試行
            n[rowIndex][columnIndex] = 0;
        }

        // ここまで到達した(指定された行・列に設定できる数字がない)場合はfalse
        return false;
    }

    /**
     * 指定された行・列が最後の要素であるかどうかを返す
     * @param rowIndex 行
     * @param columnIndex 列
     * @return true:最後の要素 false:最後の要素でない
     */
    private boolean isLast(int rowIndex, int columnIndex) {
        return (rowIndex == MAX_ARRAY_INDEX && columnIndex == MAX_ARRAY_INDEX);
    }

    /**
     * 指定された行・列の位置に、指定された数字が設定可能かどうかを返す
     * @param rowIndex 行
     * @param columnIndex 列
     * @param number 数字
     * @return true:設定可能 false:設定不可
     */
    private boolean canSet(int rowIndex, int columnIndex, int number) {
        // 行単位で確認
        int[] rowNumbers = getRow(rowIndex);

        for (int n : rowNumbers) {
            if (n == number) {
                return false;
            }
        }

        // 列単位で確認
        int[] columnNumbers = getColumn(columnIndex);

        for (int n : columnNumbers) {
            if (n == number) {
                return false;
            }
        }

        // ボックス単位で確認
        int[] boxNumbers = getBox(rowIndex, columnIndex);

        for (int n : boxNumbers) {
            if (n == number) {
                return false;
            }
        }

        return true;
    }
}