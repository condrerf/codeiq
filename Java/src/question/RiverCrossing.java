package question;

import java.util.ArrayList;
import java.util.List;


/**
 * 渡河プログラム
 */
public class RiverCrossing {
	/**
	 * メインメソッド
	 * @param args プログラム引数
	 */
	public static void main(String[] args) {
		// 処理を開始
		RiverCrossing me = new RiverCrossing();
		me.startCrossing();
	}

	/** 記号(兵士) */
	private static final char SYMBOL_SOLDIER = 'S';

	/** 記号(巨人) */
	private static final char SYMBOL_TITAN = 'T';

	/** 兵士の数 */
	private static final int SOLDIER_COUNT = 3;

	/** 巨人の数 */
	private static final int TITAN_COUNT = 3;

	/** 船の定員 */
	private static final int BOAT_CAPACITY = 2;

	/** 状況の書式 */
	private static final String FORMAT_STATUS = "%s/%s";

	/** 解答の書式 */
	private static final String FORMAT_ANSWER = "解答%d";

	/** 兵士の数(左岸) */
	private int leftSoldierCount;

	/** 巨人の数(左岸) */
	private int leftTitanCount;

	/** 兵士の数(右岸) */
	private int rightSoldierCount;

	/** 巨人の数(右岸) */
	private int rightTitanCount;

	/** 状態データの集合 */
	private List<Status> statusList = new ArrayList<Status>();

	/** 解答の番号 */
	private int answerNumber;

	/**
	 * コンストラクタ
	 */
	public RiverCrossing() {
		// 初期化
		leftSoldierCount = SOLDIER_COUNT;
		leftTitanCount = TITAN_COUNT;
		addStatus(true);
	}
	
	/**
	 * 状態データを追加する
	 * @param isBoatLeftSide 船が左岸にあるかどうか
	 */
	private void addStatus(boolean isBoatLeftSide) {
		Status status = new Status(isBoatLeftSide);
		status.setLeftSoldierCount(leftSoldierCount);
		status.setLeftTitanCount(leftTitanCount);
		status.setRightSoldierCount(rightSoldierCount);
		status.setRightTitanCount(rightTitanCount);
		statusList.add(status);
	}
	
	/**
	 * 渡河を開始する
	 */
	private void startCrossing() {
		// 船の定員内の組み合わせで渡河を試行
		for (int soldierCountToRight = 0; soldierCountToRight <= BOAT_CAPACITY; soldierCountToRight++) {
			for (int titanCountToRight = 0; (soldierCountToRight + titanCountToRight) <= BOAT_CAPACITY; titanCountToRight++) {
				// 右岸への渡河を行う前の状態の索引を格納
				int previousStatusIndexToRight = statusList.size() - 1;

				// 現在の条件では右岸に渡河できなかった場合は次へ
				if (!cross(soldierCountToRight, titanCountToRight, true)) {
					continue;
				}

				// 渡河が完了した場合は解答を出力して終了
				if (leftSoldierCount == 0 && leftTitanCount == 0) {
					printAnswer();
					return;
				}

				// 船の定員内の組み合わせで左岸への渡河を試行
				for (int soldierCountToLeft = 0; soldierCountToLeft <= BOAT_CAPACITY; soldierCountToLeft++) {
					for (int titanCountToLeft = 0; (soldierCountToLeft + titanCountToLeft) <= BOAT_CAPACITY; titanCountToLeft++) {
						// 左岸への渡河を行う前の状態の索引を格納
						int previousStatusIndexToLeft = statusList.size() - 1;

						// 現在の条件で左岸に渡河できた場合
						if (cross(soldierCountToLeft, titanCountToLeft, false)) {
							// 改めて渡河を開始し、処理終了後に左岸への渡河前の状態に戻す
							startCrossing();
							rollback(previousStatusIndexToLeft);
						}
					}
				}
				
				// 右岸への渡河前の状態に戻す
				rollback(previousStatusIndexToRight);
			}
		}
	}
	
	/**
	 * 渡河処理
	 * @param soldierCount 渡河する兵士の数
	 * @param titanCount 渡河する巨人の数
	 * @param isToRight 右岸への渡河であるかどうか
	 * @return true:渡河成功 false:渡河失敗
	 */
	private boolean cross(int soldierCount, int titanCount, boolean isToRight) {
		// 指定された条件で渡河が行えない場合はfalse
		if (!canCross(soldierCount, titanCount, isToRight)) {
			return false;
		}

		// 渡河を行い、移動元の数を減らして移動先の数を増やす
		if (isToRight) {
			leftSoldierCount -= soldierCount;
			leftTitanCount -= titanCount;
			rightSoldierCount += soldierCount;
			rightTitanCount += titanCount;
		} else {
			rightSoldierCount -= soldierCount;
			rightTitanCount -= titanCount;
			leftSoldierCount += soldierCount;
			leftTitanCount += titanCount;
		}
		
		// 状態データを追加
		addStatus(!isToRight);
		
		return true;
	}

	/**
	 * 指定された条件で渡河が可能かどうかを返す
	 * @param soldierCount 渡河する兵士の数
	 * @param titanCount 渡河する巨人の数
	 * @param isToRight 右岸への渡河であるかどうか
	 * @return true:可能 false:不可
	 */
	private boolean canCross(int soldierCount, int titanCount, boolean isToRight) {
		// 渡河する数が共に0の場合はfalse
		if (soldierCount == 0 && titanCount == 0) {
			return false;
		// 渡河する兵士がいて、巨人よりも数が少ない場合はfalse
		} else if (soldierCount > 0 && soldierCount < titanCount) {
			return false;
		}

		// 指定された数のいずれかが、移動元の場所にいる数を超えている場合はfalse
		int currentSoldierCount;
		int currentTitanCount;
		if (isToRight) {
			currentSoldierCount = leftSoldierCount;
			currentTitanCount = leftTitanCount;
		} else {
			currentSoldierCount = rightSoldierCount;
			currentTitanCount = rightTitanCount;
		}
		if (soldierCount > currentSoldierCount || titanCount > currentTitanCount) {
			return false;
		}

		// 渡河を行った場合の両岸の兵士と巨人の数、船の位置を取得
		int leftSoldierCount = this.leftSoldierCount;
		int leftTitanCount = this.leftTitanCount;
		int rightSoldierCount = this.rightSoldierCount;
		int rightTitanCount = this.rightTitanCount;
		if (isToRight) {
			leftSoldierCount -= soldierCount;
			leftTitanCount -= titanCount;
			rightSoldierCount += soldierCount;
			rightTitanCount += titanCount;
		} else {
			leftSoldierCount += soldierCount;
			leftTitanCount += titanCount;
			rightSoldierCount -= soldierCount;
			rightTitanCount -= titanCount;
		}
		boolean isBoatLeftSide = !isToRight;

		// 渡河を行った場合の状態がこれまでと重複する場合はfalse
		for (Status status : statusList) {
			if (leftSoldierCount == status.getLeftSoldierCount() && leftTitanCount == status.getLeftTitanCount() &&
				rightSoldierCount == status.getRightSoldierCount() && rightTitanCount == status.getRightTitanCount() &&
				isBoatLeftSide == status.isBoatLeftSide()) {
				return false;
			}
		}

		// 渡河を行った場合、両岸のいずれかに兵士がいて、かつ巨人よりも少ない場合はfalse
		if ((leftSoldierCount > 0 && leftSoldierCount < leftTitanCount) ||
			(rightSoldierCount > 0 && rightSoldierCount < rightTitanCount)) {
			return false;
		}

		return true;
	}

	/**
	 * 指定された状態に戻す
	 * @param statusIndex 状態の索引
	 */
	private void rollback(int statusIndex) {
		// 指定された索引の状態データを適用
		Status status = statusList.get(statusIndex);
		leftSoldierCount = status.getLeftSoldierCount();
		leftTitanCount = status.getLeftTitanCount();
		rightSoldierCount = status.getRightSoldierCount();
		rightTitanCount = status.getRightTitanCount();

		// 不要となった状態データを削除
		int nextIndex = statusIndex + 1;
		while (statusList.size() > nextIndex) {
			statusList.remove(nextIndex);
		}
	}

	/**
	 * 解答を出力する
	 */
	private void printAnswer() {
		System.out.println(String.format(FORMAT_ANSWER, ++answerNumber));
		for (Status status : statusList) {
			System.out.println(status);
		}
	}

	/**
	 * 状態クラス
	 */
	private class Status {
		/** 船が左岸にあるかどうか */
		private final boolean isBoatLeftSide;

		/** 兵士の数(左岸) */
		private int leftSoldierCount;

		/** 巨人の数(左岸) */
		private int leftTitanCount;

		/** 兵士の数(右岸) */
		private int rightSoldierCount;

		/** 巨人の数(右岸) */
		private int rightTitanCount;

		public Status(boolean isBoatLeftSide) {
			this.isBoatLeftSide = isBoatLeftSide;
		}

		public int getLeftSoldierCount() {
			return leftSoldierCount;
		}

		public void setLeftSoldierCount(int leftSoldierCount) {
			this.leftSoldierCount = leftSoldierCount;
		}

		public int getLeftTitanCount() {
			return leftTitanCount;
		}

		public void setLeftTitanCount(int leftTitanCount) {
			this.leftTitanCount = leftTitanCount;
		}

		public int getRightSoldierCount() {
			return rightSoldierCount;
		}

		public void setRightSoldierCount(int rightSoldierCount) {
			this.rightSoldierCount = rightSoldierCount;
		}

		public int getRightTitanCount() {
			return rightTitanCount;
		}

		public void setRightTitanCount(int rightTitanCount) {
			this.rightTitanCount = rightTitanCount;
		}

		public boolean isBoatLeftSide() {
			return isBoatLeftSide;
		}

		@Override
		public String toString() {
			// 左岸と右岸の状態を出力
			StringBuilder leftStatus = new StringBuilder();
			for (int i = 0; i < leftSoldierCount; i++) leftStatus.append(SYMBOL_SOLDIER);
			for (int i = 0; i < leftTitanCount; i++) leftStatus.append(SYMBOL_TITAN);
			StringBuilder rightStatus = new StringBuilder();
			for (int i = 0; i < rightSoldierCount; i++) rightStatus.append(SYMBOL_SOLDIER);
			for (int i = 0; i < rightTitanCount; i++) rightStatus.append(SYMBOL_TITAN);

			return String.format(FORMAT_STATUS, leftStatus, rightStatus);
		}
	}
}
