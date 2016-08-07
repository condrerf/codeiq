/**
 * タグ検証クラス
 */
function ValidTag() {
	// 検査対象のタグ
	var TARGET_TAGS = ["html", "head", "body", "div", "span", "ul", "form"];

	// 正規表現(タグ)
	var REGEX_TAG = /<(.+?)>/ig;

	// 正規表現(開始タグ)
	var REGEX_START_TAG = /<([^\/>]+?)>/ig;

	// 正規表現(タグ(単体で開始・終了))
	var REGEX_TAG_SELF_CLOSING = /<(.+?)\/>/ig;

	// 正規表現(タグ記号)
	var REGEX_TAG_SIGN = /<\/?|\/?>/g;

	// 正規表現(タグ間の文字列)
	var REGEX_BETWEEN_TAG_STRING = "<{0}>(.*)<\/{1}>";

	/**
	 * 指定された文字列内に含まれる特定のタグについて、開始・終了が適切に行われているかどうかを検査する
	 */
	this.check = function(string) {
		// 指定された文字列が空の場合はtrue
		if (!string) {
			return true;
		}

		// 指定された文字列から開始タグを検索
		var matchedStartTags = string.match(REGEX_START_TAG);

		// 開始タグが存在する場合
		if (matchedStartTags) {
			// 各開始タグを参照
			for (var i = 0; i < matchedStartTags.length; i++) {
				// 開始タグと、その終了タグの間の文字列を検索
				var insideTagString = matchedStartTags[i].replace(REGEX_TAG_SIGN, "");
				var tagName = insideTagString.split(" ")[0];
				var regexBetweenTagString = new RegExp(REGEX_BETWEEN_TAG_STRING.replace("{0}", insideTagString).replace("{1}", tagName), "i");
				var matchedStrings = string.match(regexBetweenTagString);

				// 文字列が存在する場合
				if (matchedStrings) {
					// その文字列を検査し、その中に無効なタグが含まれている場合はfalse
					if (!this.check(matchedStrings[1])) {
						return false;
					}

					// 元の文字列から、タグと、タグで囲まれた文字列を除去
					string = string.replace(matchedStrings[0], "");
				}
			}
		}

		// 指定された文字列からタグを検索
		// (※開始・終了の両方が存在するタグは上記の処理で除去されている)
		var existsInvalidTag = false;
		var matchedTags = string.match(REGEX_TAG);

		// タグが存在する場合
		if (matchedTags) {
			// 各タグを参照
			for (var i = 0; i < matchedTags.length; i++) {
				// 単体で開始・終了しているタグは検査対象から除外
				var matchedTag = matchedTags[i];

				if (matchedTag.match(REGEX_TAG_SELF_CLOSING)) {
					continue;
				}

				// 検査対象のタグである場合は、無効なタグが存在するものと判断
				var insideTagString = matchedTag.replace(REGEX_TAG_SIGN, "");
				var tagName = insideTagString.split(" ")[0];

				for (var j = 0; j < TARGET_TAGS.length; j++) {
					if (tagName == TARGET_TAGS[j]) {
						existsInvalidTag = true;
						break;
					}
				}

				if (existsInvalidTag) {
					break;
				}
			}
		}

		// 無効なタグが存在しない場合はtrue、存在する場合はfalseを返す
		return !existsInvalidTag;
	}
}