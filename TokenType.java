/**
 * Created by mahdihs76 on 4/3/19.
 */
public enum TokenType {
    SEMICOLON(";", "SYMBOL"),
    COLON(":", "SYMBOL"),
    COMMA(",", "SYMBOL"),
    OPEN_BRACKETS("[", "SYMBOL"),
    CLOSE_BRACKETS("]", "SYMBOL"),
    OPEN_PARENTHESES("(", "SYMBOL"),
    CLOSE_PARENTHESES(")", "SYMBOL"),
    OPEN_BRACES("{", "SYMBOL"),
    CLOSE_BRACES("}", "SYMBOL"),
    PLUS("+", "SYMBOL"),
    MINUS("-", "SYMBOL"),
    TIMES("*", "SYMBOL"),
    EQ("==", "SYMBOL"),
    ASSIGN("=", "SYMBOL"),
    LESS_THAN("<", "SYMBOL"),

    IF("if", "KEYWORD"),
    ELSE("else", "KEYWORD"),
    VOID("void", "KEYWORD"),
    INT("int", "KEYWORD"),
    WHILE("while", "KEYWORD"),
    BREAK("break", "KEYWORD"),
    CONTINUE("continue", "KEYWORD"),
    SWITCH("switch", "KEYWORD"),
    DEFAULT("default", "KEYWORD"),
    CASE("case", "KEYWORD"),
    RETURN("return", "KEYWORD"),

    OPEN_COMMENT("/*", "COMMENT"),
    CLOSE_COMMENT("*/", "COMMENT"),
    SINGLE_LINE_COMMENT("//", "COMMENT"),

    BLANK(" ", "WHITESPACE"),
    NEWLINE("\n", "WHITESPACE"),
    CARRIAGE_RETURN("\r", "WHITESPACE"),
    TAB("\t", "WHITESPACE"),
    VERTICAL_TAB("" + ((char)(11)), "WHITESPACE"),
    FORM_FEED("\f", "WHITESPACE"),

    EOF("eof", "EOF"),

    INT_CONST("int", "NUM"),
    BOOL_CONST("bool", "BOOL"),
    ID("id", "ID"),

    INVALID_INPUT("", "invalid input");




    private String group;

    private String text;
    TokenType(String text, String group) {
        this.text = text;
        this.group = group;
    }

    public String getText() {
        return text;
    }
    public String getGroup() {
        return group;
    }
}
