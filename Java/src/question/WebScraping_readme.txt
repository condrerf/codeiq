・RSSが正しく変換できない場合の対応
1.RSSの内容を確認し、タグの開始・終了など、XML文書として問題がないかどうか確認してください。
　問題がある場合はRSSの内容を修正し、正しく読み込めるようにしてください。

2.文書の構成に問題がない場合、RSSの一単位を構成するタグの名前が、
　RssToJson.javaの28行目

　private static final String TAG_NAME_ITEM

　で指定している文字列と一致しているかどうか確認してください。
　異なっている場合は、指定している文字列が一致するように修正してください。

3.上記の対応を行なっても変換できない場合は、
　特定の文字が原因で変換時にエラーが発生している可能性があります。
　プログラムを実行し、コンソール出力されるエラー情報を確認してください。
　その情報から、エラーの原因となっている文字を特定し、
　RssToJson.javaの38行目

　private static final String[][] ESCAPE_STRING_PAIRS = {{"&", "＆"}};

　を、

　private static final String[][] ESCAPE_STRING_PAIRS = {{"&", "＆"}, {"a", "ａ"}};
　(※a:エラー文字、ａ:エラー回避のために一時的に変換する文字)

　となるように、項目を追加してください。


・プログラム作成に際して、参考にした文献やURL
json-simple(http://code.google.com/p/json-simple/)