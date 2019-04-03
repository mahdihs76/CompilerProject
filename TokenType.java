/**
 * Created by mahdihs76 on 4/3/19.
 */
public enum TokenType {
    LEFT_PARENTHESES("("),
    RIGHT_PARENTHESES(")"),
    LEFT_BRACE("{"),
    RIGHT_BRACE("}"),
    SEMICOLON(";"),
    COMMA(","),
    PLUS("+"),
    MINUS("-"),
    TIMES("*"),
    DIV("/"),
    MOD("%"),
    NOT("!"),
    ASSIGN(":="),
    COLON(":"),
    EQ("=="),
    NEQ("<>"),
    LOWER_THAN("<"),
    GREATER_THAN(">"),
    LOWER_AND_EQUAL_TO("<="),
    GREATER_THAN_EQUAL_TO(">="),
    IF("if"),
    THEN("then"),
    ELSE("else"),
    WHILE("while"),
    DO("do"),
    INT_CONST("int"),
    BOOL_CONST("bool"),
    IDENTIFIER("id"),
    EOF("eof");

    private String text;
    TokenType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
