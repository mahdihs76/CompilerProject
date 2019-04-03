import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by mahdihs76 on 4/3/19.
 */

public class TokenizerTest {

    @Test
    public void test1(){
        ArrayList<Token> tokens = Tokenizer.process("a = 2;" +
                "b <= 3;" +
                "if (a == b) {" +
                "}");

        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    @Test
    public void test2(){
        ArrayList<Token> tokens = Tokenizer.process("int a = 0;" +
                "// comment2" +
                "a = 2 + +2;" +
                "a = a + -3;" +
                "cde = a;" +
                "if (b /* comment1 */ == 3) {" +
                "a = 3;" +
                "cd!e = -7;" +
                "}" +
                "else" +
                "{" +
                "b = a < cde;" +
                "{cde = @2;" +
                "}}" +
                "return;" +
                "}");

        for (Token token : tokens) {
            System.out.println(token);
        }
    }

}
