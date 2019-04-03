import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mahdihs76 on 4/3/19.
 */
public class Tokenizer {
    private static final Pattern intPattern = Pattern.compile("[0-9]+");
    private static final Pattern boolPattern = Pattern.compile("true|false");
    private static final Pattern identifierPattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
    private ArrayList<Token> result;
    private String input;
    private int line;
    private int col;

    public Tokenizer(String input) {
        this.result = new ArrayList<>();
        this.input = input;
        this.line = 1;
        this.col = 1;
    }

    public static ArrayList<Token> process(String input) {
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();
        return tokenizer.result;
    }

    private void tokenize() {
        skipWhitespace();
        while (!input.isEmpty()) {
            boolean ok = tryToken(TokenType.LEFT_PARENTHESES) ||
                    tryToken(TokenType.RIGHT_PARENTHESES) ||
                    tryToken(TokenType.LEFT_BRACE) ||
                    tryToken(TokenType.RIGHT_BRACE) ||
                    tryToken(TokenType.SEMICOLON) ||
                    tryToken(TokenType.COMMA) ||
                    tryToken(TokenType.PLUS) ||
                    tryToken(TokenType.MINUS) ||
                    tryToken(TokenType.TIMES) ||
                    tryToken(TokenType.DIV) ||
                    tryToken(TokenType.MOD) ||
                    tryToken(TokenType.NOT) ||
                    tryToken(TokenType.ASSIGN) ||
                    tryToken(TokenType.COLON) ||
                    tryToken(TokenType.EQ) ||
                    tryToken(TokenType.NEQ) ||
                    tryToken(TokenType.LOWER_AND_EQUAL_TO) ||
                    tryToken(TokenType.GREATER_THAN_EQUAL_TO) ||
                    tryToken(TokenType.LOWER_THAN) ||
                    tryToken(TokenType.GREATER_THAN) ||
                    tryRegex(intPattern, TokenType.INT_CONST) ||
                    tryRegex(boolPattern, TokenType.BOOL_CONST) ||
                    tryKeywordOrIdentifier();
            if (!ok) {
                //TODO cause error
            }

            skipWhitespace();
        }
    }

    private boolean tryToken(TokenType type) {
        String text = type.getText();
        if (input.startsWith(text)) {
            result.add(new Token(type, text));
            consumeInput(text.length());
            return true;
        } else {
            return false;
        }
    }

    private boolean tryRegex(Pattern p, TokenType type) {
        Matcher m = p.matcher(input);
        if (m.lookingAt()) {
            result.add(new Token(type, m.group()));
            consumeInput(m.end());
            return true;
        } else {
            return false;
        }
    }

    private boolean tryKeywordOrIdentifier() {
        if (tryRegex(identifierPattern, TokenType.IDENTIFIER)) {
            Token tok = result.get(result.size() - 1);
            TokenType kwType = Utils.getKeywords().get(tok.getText());
            if (kwType != null) {
                tok = new Token(kwType, tok.getText());
                result.set(result.size() - 1, tok);
            }
            return true;
        } else {
            return false;
        }
    }

    private void skipWhitespace() {
        int i = 0;
        while (i < input.length() &&
                Character.isWhitespace(input.charAt(i))) {
            i++;
        }
        consumeInput(i);
    }

    private void consumeInput(int amount) {
        for (int i = 0; i < amount; ++i) {
            char c = input.charAt(i);
            if (c == '\n') {
                line++;
                col = 1;
            } else if (c == '\r') {
            } else {
                col++;
            }
        }
        input = input.substring(amount);
    }
}
