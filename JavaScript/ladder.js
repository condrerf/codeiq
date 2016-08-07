function Ladder(success) {
    this.points = {
        start: ['A', 'B', 'C', 'D'],
        end: ['a', 'b', 'c', 'd']
    };
    this.success = this.points.end.indexOf(success);
    this.lines;
    this.ini(4);
}

// あみだの横棒を4本ひく
Ladder.prototype.ini = function(count) {
	var areaCount = count - 1;

	while (true) {
		// ランダムに線を生成
		var lines = new Array(count);

		for (var i = 0; i < lines.length; i++) {
			var index = Math.floor(Math.random() * areaCount);
			lines[i] = [index, index + 1];
		}

		// 各領域に線が引かれているかどうかを判定
		var isLined = new Array(areaCount);

		for (var i = 0; i < lines.length; i++) {
			var line = lines[i];
			var start = line[0];
			isLined[start] = true;
		}

		// 線が引かれていない位置がある場合は再設定
		var existsNotLinedArea = false;

		for (var i = 0; i < isLined.length; i++) {
			if (!isLined[i]) {
				existsNotLinedArea = true;
				break;
			}
		}

		if (existsNotLinedArea) {
			continue;
		}

		// 線を格納して処理を終了
		this.lines = lines;
		return;
	}
}
// あみだくじを実行し当たったかどうかをtrue/falseで返す
Ladder.prototype.check = function(point) {
	// 指定された要素の位置を開始位置とする
	var position = this.points.start.indexOf(point);

	// 各線を参照し、線の始点にいる場合は終点、終点にいる場合は始点に移動
	for (var i = 0; i < this.lines.length; i++) {
		var line = this.lines[i];
		var start = line[0];
		var end = line[1];

		if (position === start) {
			position = end;
		} else if (position === end) {
			position = start;
		}
	}

	return (position === this.success);
}