package tokenizer;

/**
 * Created by mahdihs76 on 4/3/19.
 */
public class Token {
    private Type type;
    private String text;

    private int line;
    private int col;

    public Token(Type type, String text) {
        this.type = type;
        this.text = text;
    }

    public Token(Type type, String text, int line, int col) {
        this.type = type;
        this.text = text;
        this.line = line;
        this.col = col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Token) {
            Token token = (Token) obj;
            return getType().equals(token.type) && getText().equals(token.text);
        } else {
            return false;
        }
    }

    public Type getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        if(type.equals(Type.INVALID_INPUT))
            return "(" + text + ", " + type.getGroup() + ")";
        else
            return "(" + type.getGroup() + ", " + text + ")";
    }

    public int getLine() { return line; }
    public int getCol() { return col; }

    public enum Type {
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
        GREATER_THAN(">", "SYMBOL"),

        IF("if", "KEYWORD"),
        THEN("then", "KEYWORD"),
        ELSE("else", "KEYWORD"),
        VOID("void", "KEYWORD"),
        INT("int", "KEYWORD"),
        WHILE("while", "KEYWORD"),
        DO("do", "KEYWORD"),
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
        Type(String text, String group) {
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

}
