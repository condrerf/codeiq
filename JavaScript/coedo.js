// 性別定数
var MALE = 1;
var FEMALE = 2;

// 最低料金
var MINIMUM_FEE = 500;

// 1日料金
var DAILY_FEE = 1000;

// 最低料金と1日料金の境界となる時間(分)
var BORDER_MINUTE = 120;

// 女性割引料金が適用される人数
var FEMALE_DISCOUNT_FEE_COUNT = 2;

// 割引料金が適用される入店時間を分換算した値
var DISCOUNT_FEE_START_TIME_MINUTE = 10 * 60;

function CoEdo() {
	// 利用者数
	this.counts = 0;

	// 売上高
	this.sales = 0;

	/**
	 * チェックアウト処理
	 */
	this.checkout = function(sexType, count, startTime, endTime) {
		// 引数の確認
		if (!sexType || (sexType != MALE && sexType != FEMALE)) {
			return;
		}

		if (!count || isNaN(parseInt(count, 10))) {
			return;
		}

		if (!startTime || startTime.length != 5) {
			return;
		}

		if (!endTime || endTime.length != 5) {
			return;
		}

		// 開始時間・終了時間を数値に変換
		var startHour = parseInt(startTime.substring(0, 2), 10);
		var startMinute = parseInt(startTime.substring(3), 10);
		var endHour = parseInt(endTime.substring(0, 2), 10);
		var endMinute = parseInt(endTime.substring(3), 10);

		if (isNaN(startHour) || isNaN(startMinute) || isNaN(endHour) || isNaN(endMinute)) {
			return;
		}

		// 利用時間(分)を計算
		var startTimeMinute = startHour * 60 + startMinute;
		var endTimeMinute = endHour * 60 + endMinute;
		var utilityTimeMinute = endTimeMinute - startTimeMinute;

		var fee;

		// 既定の時間以内の利用の場合は最低料金を適用
		if (utilityTimeMinute <= BORDER_MINUTE) {
			fee = MINIMUM_FEE;
		// 女性で規定人数以上、または規定時間までの入店の場合は1日料金の半額を適用
		} else if ((sexType == FEMALE && count >= FEMALE_DISCOUNT_FEE_COUNT) || startTimeMinute <= DISCOUNT_FEE_START_TIME_MINUTE) {
			fee = DAILY_FEE / 2;
		// 上記以外は1日料金を適用
		} else {
			fee = DAILY_FEE;
		}

		// 売上高を計算し、利用者数と売上高の合計を加算
		var sales = fee * count;
		this.counts += count;
		this.sales += sales;
	}

	/**
	 * 利用者数を返す
	 */
	this.getCounts = function() {
		return this.counts;
	}

	/**
	 * 売上高を返す
	 */
	this.getSales = function() {
		return this.sales;
	}
}