・処理の内容
　(0)mainメソッドにて、引数に指定された文字列を画像ファイルのパスとして取得し、ファイルを開く。(動作テスト用)

　(1)classifyメソッドにて、引数に指定されたファイルオブジェクトから画像を取得する。
　　 画像が取得できなかった場合はCOLOR_TYPE_BLOKENを返す。

　(2)画像の各画素を参照し、RGB値からその画素の色を判定する。
　　 RGBの各値に対し、プログラム内で定義された閾値以上の場合、その色の傾向があると判定する。
　　 判定条件は以下の通り。
　　 赤　　　→COLOR_TYPE_RED
　　 赤・緑　→COLOR_TYPE_YELLOW(黄)
　　 赤・青　→COLOR_TYPE_PURPLE(紫)
　　 緑　　　→COLOR_TYPE_GREEN
　　 緑・青　→COLOR_TYPE_AQUA(水)
　　 青　　　→COLOR_TYPE_BLUE
　　 該当なし→COLOR_TYPE_UNKNOWN

　(3)各画素の色判定結果を種類ごとに集計し、判定回数が最も多い色をその画像の色とみなしてその値を返す。
　
・参考にした文献やURL
　プログラムdeタマゴ(http://d.hatena.ne.jp/nodamushi/20111012/1318436587)