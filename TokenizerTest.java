import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by mahdihs76 on 4/3/19.
 */

public class TokenizerTest {

    @Test
    public void test1(){
        Tokenizer.get_next_token("");
    }

    @Test
    public void test2(){
        Map map = Tokenizer.get_next_token("\n\nvoid main(void){\n" +
                "\tint a = 0;\n" +
                "\t// comment2\n" +
                "\ta = 2 + +2;\n" +
                "\ta = a + -3;\n" +
                "\tcde = a;\n" +
                "\tif (b /* comment1 */ == 3) {\n" +
                "\ta = 3;\n" +
                "\tcd!e = -7;\n" +
                "\t}\n" +
                "\telse\n" +
                "\t{\n" +
                "\tb = a < cde;\n" +
                "\t{cde = @2;\n" +
                "\t}}\n" +
                "\treturn;\n" +
                "}\n");

        ArrayList<Token> result = (ArrayList<Token>)(map.get("result"));
        ArrayList<Token> lexical_errors = (ArrayList<Token>)(map.get("lexical_errors"));

        int line = 0;
        for (Token token : result) {
            if (token.getType().getGroup().equals("WHITESPACE"))
                continue;
            if (token.getLine() > line) {
                line = token.getLine();
                System.out.print("\n" + token.getLine() + ". ");
            }
            System.out.print(token + " ");
        }
        System.out.println();
        line = 0;
        for (Token token : lexical_errors) {
            if (token.getLine() > line) {
                line = token.getLine();
                System.out.print("\n" + token.getLine() + ". ");
            }
            System.out.print(token + " ");
        }
    }

}
