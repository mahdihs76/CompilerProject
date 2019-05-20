package utility;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by mahdihs76 on 4/3/19.
 */
public class MyUtils {

//    public static HashMap<String, Token.Type> getKeywords(){
//        HashMap<String, TokenType> keywords = new HashMap<>();
//        keywords.put("if", parser.models.Type.IF);
//        keywords.put("then", parser.models.Type.THEN);
//        keywords.put("else", parser.models.Type.ELSE);
//        keywords.put("while", parser.models.Type.WHILE);
//        keywords.put("do", parser.models.Type.DO);
//        return keywords;
//
//    }

    public static <T> String join(Iterable<T> stuff, String sep) {
        StringBuilder sb = new StringBuilder();
        Iterator<T> i = stuff.iterator();
        while (i.hasNext()) {
            sb.append(i.next().toString());
            if (i.hasNext()) {
                sb.append(sep);
            }
        }
        return sb.toString();
    }
}
