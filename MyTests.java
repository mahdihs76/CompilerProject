
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
    public void test_lexical_1(){
        Tokenizer.get_next_token("");
    }

    @Test
    public void test_lexical_2(){
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
    public void test_lexical_3(){
        Tokenizer tokenizer = new Tokenizer("\n\nvoid main(void){\n" +
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

        while(true) {
            Token newToken = tokenizer.get_next_token();
            if(newToken == null)
                break;
            else
                System.out.println(newToken);

        }

    }




    //@Test
    //public void test_parser_1() {
    //    Tokenizer tokenizer = new Tokenizer("x * 5 < y + 10");
    //    tokenizer.get_next_token();
    //    Expression actual = Parser.parseExpr(tokenizer.getResult());
    //    //Expression left = new BinaryOperation(new Variable("x"), "*", new IntConstant(5));
    //    //Expression right = new BinaryOperation(new Variable("y"), "+", new IntConstant(10));
    //    //Expression expected = new BinaryOperation(left, "<", right);
    //    //assertEquals(expected, actual);
    //}


    //@Test
    //public void test_parser_2() {
    //    Tokenizer tokenizer = new Tokenizer("if (x > 2) x = 2; else x = 1;");
    //    tokenizer.get_next_token();
    //    Statement statement = Parser.parseStatement(tokenizer.getResult());
    //    Expression condition = new BinaryOperation(new Variable("x"), "==", new IntConstant(0));
    //    Statement thenClause = new FunctionCall("print", new Variable("x"));
    //    Statement expected = new If(condition, thenClause);
    //    assertEquals(expected, statement);
    //}

    @Test
    public void test_parser_3() {
        Tokenizer tokenizer = new Tokenizer(
                "int b;\n" +
                        "int foo(int d, int e){\n" +
                        "int f;\n" +
                        "void foo2(int k[]){\n" +
                        "return k[0] + k[1];\n" +
                        "}\n" +
                        "int fff[2];\n" +
                        "fff[0] = d;\n" +
                        "fff[1] = d + 1;\n" +
                        "f = foo2(fff);\n" +
                        "b = e + f;\n" +
                        "while(d < 0){\n" +
                        "f = f + d;\n" +
                        "d = d - 1;\n" +
                        "if(d == 4){\n" +
                        "break;\n" +
                        "}else{\n" +
                        "e = g(n);\n" +
                        "}\n" +
                        "// comment1\n" +
                        "}\n" +
                        "}\n");





        while(true) {
            Token newToken = tokenizer.get_next_token();
            if(newToken == null)
                break;
        }

        Parser parser = new Parser(tokenizer.getResult());

        System.out.println(parser.parse_program());
    }













}





















