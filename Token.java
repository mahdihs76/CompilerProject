/**
 * Created by mahdihs76 on 4/3/19.
 */
public class Token {
    private TokenType type;
    private String text;

    private int line;
    private int col;

    public Token(TokenType type, String text) {
        this.type = type;
        this.text = text;
    }

    public Token(TokenType type, String text, int line, int col) {
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

    public TokenType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        if(type.equals(TokenType.INVALID_INPUT))
            return "(" + text + ", " + type.getGroup() + ")";
        else
            return "(" + type.getGroup() + ", " + text + ")";
    }

    public int getLine() { return line; }
    public int getCol() { return col; }

}
