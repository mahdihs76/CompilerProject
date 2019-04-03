/**
 * Created by mahdihs76 on 4/3/19.
 */
public class Token {
    private TokenType type;
    private String text;

    public Token(TokenType type, String text) {
        this.type = type;
        this.text = text;
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
        return "(" + type.toString() + ", " + text +")";
    }
}
