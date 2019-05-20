
import org.junit.Test;
import parser.Parser;
import parser.models.Expression;
import parser.models.Statement;
import parser.models.expressions.BinaryOperation;
import parser.models.expressions.FunctionCall;
import parser.models.expressions.IntConstant;
import parser.models.expressions.Variable;
import parser.models.statements.If;
import tokenizer.Token;
import tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by mahdihs76 on 4/3/19.
 */

public class MyTests {

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

    @Test
    public void test_parser_1() {
        Tokenizer tokenizer = new Tokenizer("x * 5 < y + 10");
        tokenizer.tokenize();
        Expression actual = Parser.parseExpr(tokenizer.getResult());
        Expression left = new BinaryOperation(new Variable("x"), "*", new IntConstant(5));
        Expression right = new BinaryOperation(new Variable("y"), "+", new IntConstant(10));
        Expression expected = new BinaryOperation(left, "<", right);
        assertEquals(expected, actual);
    }


    @Test
    public void test_parser_2() {
        Tokenizer tokenizer = new Tokenizer("if x == 0 then print(x);");
        tokenizer.tokenize();
        Statement statement = Parser.parseStatement(tokenizer.getResult());
        Expression condition = new BinaryOperation(new Variable("x"), "==", new IntConstant(0));
        Statement thenClause = new FunctionCall("print", new Variable("x"));
        Statement expected = new If(condition, thenClause);
        assertEquals(expected, statement);
    }

}
