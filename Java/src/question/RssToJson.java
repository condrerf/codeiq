package question;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * RSSをJSONに変換するクラス
 */
public class RssToJson {
    /** RSSの各要素を構成するタグ名 */
    private static final String TAG_NAME_ITEM = "item";

    /** キー(基本情報) */
    private static final String KEY_BASE_INFO = "baseInfo";

    /** キー(RSS項目の数) */
    private static final String KEY_SIZE = "size";

    /** エスケープ処理する文字の配列 */
    private static final String[][] ESCAPE_STRING_PAIRS = {{"&", "＆"}};

    /** 索引(エスケープ処理する文字の配列・処理前) */
    private static final int INDEX_ESCAPE_STRING_PAIR_FROM = 0;

    /** 索引(エスケープ処理する文字の配列・処理後) */
    private static final int INDEX_ESCAPE_STRING_PAIR_TO = 1;

    /** 文字コード(UTF-8) */
    private static final String CHAR_CODE_UTF8 = "UTF-8";

    /** 文字コード(MS932) */
    private static final String CHAR_CODE_MS932 = "MS932";

    /** 使用する文字コード */
    private static final String CHAR_CODE = CHAR_CODE_UTF8;

    /**
     * 指定されたRSSの文字列を変換し、JSON形式の文字列を返す
     * @param rssString RSSの文字列
     * @return JSON形式の文字列
     * @exception ParseException
     */
    public static String convert(String rssString) throws ParseException {
        // 引数の確認
        if (rssString == null || rssString.isEmpty()) {
            return null;
        }

        // 特定の文字をエスケープ
        for (String[] convertStringPair : ESCAPE_STRING_PAIRS) {
            rssString = rssString.replace(convertStringPair[INDEX_ESCAPE_STRING_PAIR_FROM], convertStringPair[INDEX_ESCAPE_STRING_PAIR_TO]);
        }

        // XMLを解析
        Document doc = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(rssString.getBytes(CHAR_CODE)));
        // 解析に失敗した場合はnullを返す
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        // 各RSS項目の集合を生成
        LinkedList<LinkedHashMap<String, String>> itemList = new LinkedList<LinkedHashMap<String,String>>();
        NodeList itemNodeList = doc.getElementsByTagName(TAG_NAME_ITEM);
        int itemCount = itemNodeList.getLength();

        for (int i = 0; i < itemCount; i++) {
            // 現在参照しているRSS項目の要素の連想配列を生成
            LinkedHashMap<String, String> elementMap = new LinkedHashMap<String, String>();
            Node itemNode = itemNodeList.item(i);
            NodeList elementNodeList = itemNode.getChildNodes();

            for (int j = 0; j < elementNodeList.getLength(); j++) {
                Node elementNode = elementNodeList.item(j);
                elementMap.put(elementNode.getNodeName(), elementNode.getTextContent());
            }

            // 連想配列を集合に追加
            if (!elementMap.isEmpty()) {
                itemList.add(elementMap);
            }
        }

        // JSONオブジェクトを生成
        JSONObject json = new JSONObject();

        // JSONオブジェクトにRSSの基本情報を設定
        LinkedHashMap<String, Integer> itemSizeMap = new LinkedHashMap<String, Integer>();
        itemSizeMap.put(KEY_SIZE, itemCount);
        json.put(KEY_BASE_INFO, itemSizeMap);

        // RSS項目の集合が空でない場合は、JSONオブジェクトに項目情報を設定
        if (!itemList.isEmpty()) {
            json.put(TAG_NAME_ITEM, itemList);
        }

        // JSONの文字列を取得
        String jsonString = null;
        StringWriter writer = null;

        try {
            writer = new StringWriter();
            json.writeJSONString(writer);
            jsonString = writer.toString();

            if (jsonString != null) {
                // エスケープ文字を除去
                jsonString = jsonString.replace("\\", "");

                // エスケープ処理した文字を元に戻す
                for (String[] convertStringPair : ESCAPE_STRING_PAIRS) {
                    jsonString = jsonString.replace(convertStringPair[INDEX_ESCAPE_STRING_PAIR_TO], convertStringPair[INDEX_ESCAPE_STRING_PAIR_FROM]);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        return jsonString;
    }

    /**
     * メインメソッド
     * @param args 引数の配列
     */
    public static void main(String[] args) {
        // 引数からファイルパスを取得
        String filePath = null;

        if (args != null && args.length > 0) {
            filePath = args[0];
        }

        // ファイルパスが取得できなかった場合は終了
        if (filePath == null || filePath.isEmpty()) {
            return;
        }

        // ファイルを取得
        File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            return;
        }

        // ファイル内の文字列を取得
        StringBuilder stringBuilder = new StringBuilder();

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader ism = new InputStreamReader(fis, CHAR_CODE);
            BufferedReader br = new BufferedReader(ism);
            String str;

            while ((str = br.readLine()) != null) {
                stringBuilder.append(str);
            }

            br.close();
            ism.close();
            fis.close();
        } catch(IOException e) {
            e.printStackTrace();
        }

        // JSONの文字列の取得
        String jsonString = null;

        try {
            jsonString = convert(stringBuilder.toString());
        } catch(ParseException e) {
            e.printStackTrace();
        }

        // 以下未定
    }
}
