import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mahdihs76 on 4/3/19.
 */
public class Tokenizer {
    private static final Pattern intPattern = Pattern.compile("[0-9]+");
    private static final Pattern boolPattern = Pattern.compile("true|false");
    private static final Pattern identifierPattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9]*");
    private ArrayList<Token> result;
    private ArrayList<Token> lexical_errors;
    private String input;
    private int line;
    private int col;

    public Tokenizer(String input) {
        this.result = new ArrayList<>();
        this.lexical_errors = new ArrayList<>();
        this.input = input;
        this.line = 1;
        this.col = 1;
    }

    public static Map<String, ArrayList<Token>> get_next_token(String input) {
        // reading from the file "input.txt":
        try {
            BufferedReader br = new BufferedReader(new FileReader("input.txt"));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while ( line != null ) {
                    sb.append( line );
                    sb.append( '\n' );
                    line = br.readLine();
                }

                input = sb.toString();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {br.close();} catch (Exception ex) {/*ignore*/}
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //tokenizing:
        Tokenizer tokenizer = new Tokenizer(input);
        tokenizer.tokenize();

        //making output strings ready to be written in files:
        StringBuilder result_sb = new StringBuilder();
        StringBuilder lexical_errors_sb = new StringBuilder();
        int line = 0;
        for (Token token : tokenizer.result) {
            if (token.getType().getGroup().equals("WHITESPACE"))
                continue;
            if (token.getLine() > line) {
                line = token.getLine();
                result_sb.append("\n" + token.getLine() + ". ");
            }
            result_sb.append(token + " ");
        }
        line = 0;
        for (Token token : tokenizer.lexical_errors) {
            if (token.getLine() > line) {
                line = token.getLine();
                lexical_errors_sb.append("\n" + token.getLine() + ". ");
            }
            lexical_errors_sb.append(token + " ");
        }

        //writing in files "scanner.txt" and "lexical_errors.txt":
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("scanner.txt")));
            bw.write("Something");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {bw.close();} catch (Exception ex) {/*ignore*/}
        }

        //returing result and lexical_errors:
        Map<String, ArrayList<Token>> map = new HashMap<>();
        map.put("result", tokenizer.result);
        map.put("lexical_errors", tokenizer.lexical_errors);
        return map;
    }

    private void tokenize() {
        skipWhitespace();
        while (!input.isEmpty()) {
            boolean ok = tryManyTokens(true, 0) ||
                    //tryRegex(boolPattern, TokenType.BOOL_CONST, true) ||
                    tryRegex(intPattern, TokenType.INT_CONST, true) ||
                    tryRegex(identifierPattern, TokenType.ID, true);
            handleComment();

            if (!ok && isInvalidCharacter(0)) {
                boolean thereIsError = checkInvalidity("");
                if (!thereIsError) continue;
            }

            skipWhitespace();
        }
    }

    private boolean tryToken(TokenType type, boolean addToken, int start) {
        String text = type.getText();
        if (input.substring(start).startsWith(text)) {
            if (!addToken) return true;
            else {
                boolean thereIsError = checkInvalidity(text);
                if (!thereIsError) {
                    result.add(new Token(type, text, line, col));
                    consumeInput(text.length());
                }
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean tryRegex(Pattern p, TokenType type, boolean addToken) {
        Matcher m = p.matcher(input);
        if (m.lookingAt()) {
            boolean thereIsError = checkInvalidity(m.group());
            if (!thereIsError && addToken) {
                result.add(new Token(type, m.group(), line, col));
                consumeInput(m.group().length());
            }

            return true;
        } else {
            return false;
        }
    }

//    private boolean tryKeywordOrIdentifier() {
//        if (tryRegex(identifierPattern, TokenType.ID, true)) {
//            /*Token tok = result.get(result.size() - 1);
//            TokenType kwType = Utils.getKeywords().get(tok.getText());
//            if (kwType != null) {
//                tok = new Token(kwType, tok.getText(), line, col);
//                result.set(result.size() - 1, tok);
//            }*/
//            return true;
//        } else {
//            return false;
//        }
//    }

    private void skipWhitespace() {
        int i = 0;
        while (i < input.length() && Character.isWhitespace(input.charAt(i))) {
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
            } /*else if (c == '\r') {

            } */else {
                col++;
            }
        }
        input = input.substring(amount);
    }

    private boolean tryManyTokens(boolean addToken, int start) {
        boolean output = false;
        for (TokenType tokenType: TokenType.values()) {
            String group = tokenType.getGroup();
            if (group.equals("KEYWORD") || group.equals("SYMBOL")) {
                output = output || tryToken(tokenType, addToken, start);
            }
        }
        return output;
    }

    private void handleComment() {
        boolean output = false;
        int i = 0;
        if (tryToken(TokenType.OPEN_COMMENT, false, i)) {
            i += TokenType.OPEN_COMMENT.getText().length();
            while (i <= input.length() - 1) {
                if (tryToken(TokenType.CLOSE_COMMENT, false, i++))
                    break;
            }
            consumeInput(i + TokenType.CLOSE_COMMENT.getText().length());
        }
        if (tryToken(TokenType.SINGLE_LINE_COMMENT, false, i)) {
            i += TokenType.SINGLE_LINE_COMMENT.getText().length();
            while (i <= input.length() - 1) {
                if (tryToken(TokenType.NEWLINE, false, i++))
                    break;
            }
            consumeInput(i + TokenType.NEWLINE.getText().length());
        }
    }

    private boolean isInvalidCharacter(int i) {
        return !Character.isWhitespace(input.charAt(i))
                && !tryManyTokens(false, i)
                && !identifierPattern.matcher(input.substring(i)).lookingAt()
                && !intPattern.matcher(input.substring(i)).lookingAt();
    }

    private boolean checkInvalidity(String text) {
        int i = text.length();
        StringBuilder invalid_token = new StringBuilder(text);
        boolean thereIsError = false;

        while (i <= input.length() - 1 && isInvalidCharacter(i)) {

            thereIsError = true;
            invalid_token.append(input.charAt(i++));
        }

        if (thereIsError) {
            lexical_errors.add(new Token(TokenType.INVALID_INPUT, invalid_token.toString(), line, col));
            consumeInput(invalid_token.length());
        }

        return thereIsError;
    }


}
