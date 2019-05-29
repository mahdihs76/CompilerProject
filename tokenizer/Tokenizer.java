package tokenizer;

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


    public ArrayList<Token> getResult() {
        return result;
    }

    public Tokenizer(String input) {
        this.result = new ArrayList<>();
        this.lexical_errors = new ArrayList<>();
        this.input = input;
        this.line = 1;
        this.col = 1;
    }



    public String getResultString() {
        StringBuilder result_sb = new StringBuilder();
        StringBuilder lexical_errors_sb = new StringBuilder();

        int line = 0;
        boolean isFirstTime = true;

        for (Token token : this.result) {
            if (token.getType().getGroup().equals("WHITESPACE"))
                continue;
            if (token.getLine() > line) {
                line = token.getLine();
                if(isFirstTime)
                    result_sb.append(token.getLine() + ". ");
                else
                    result_sb.append("\n" + token.getLine() + ". ");
            }
            result_sb.append(token + " ");
            isFirstTime = false;
        }

        return result_sb.toString();

    }

    public String getLexicalErrorsString(){
        StringBuilder result_sb = new StringBuilder();
        StringBuilder lexical_errors_sb = new StringBuilder();

        int line = 0;
        boolean isFirstTime = true;

        for (Token token : this.lexical_errors) {
            if (token.getLine() > line) {
                line = token.getLine();
                if(isFirstTime)
                    lexical_errors_sb.append(token.getLine() + ". ");
                else
                    lexical_errors_sb.append("\n" + token.getLine() + ". ");
            }
            lexical_errors_sb.append(token + " ");
            isFirstTime = false;
        }

        return lexical_errors_sb.toString();

    }



    public Token get_next_token() {
        skipWhitespace();
        int initialResultSize = result.size();

        while(true) {
            if (input.isEmpty())
                return null;

            boolean ok = tryRegex(identifierPattern, Token.Type.ID, true) ||
                    tryManyTokens(true, 0) ||
                    tryRegex(intPattern, Token.Type.INT_CONST, true);
            handleComment();


            if (!ok && isInvalidCharacter(0)) {
                checkInvalidity("");
            }
            //boolean thereIsError = checkInvalidity("");
            //if (!thereIsError) continue;
            skipWhitespace();

            int newResultSize = result.size();
            if (newResultSize > initialResultSize) {
                //System.err.println(newResultSize - initialResultSize);
                return result.get(result.size() - 1);
            }


        }
    }






    private boolean tryToken(Token.Type type, boolean addToken, int start) {
        String text = type.getText();
        if (input.substring(start).startsWith(text)) {
            if (!addToken) return true;
            else {
//                boolean thereIsError = checkInvalidity(text);
//                if (!thereIsError) {
                    result.add(new Token(type, text, line, col));
                    consumeInput(text.length());
//                }
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean tryRegex(Pattern p, Token.Type type, boolean addToken) {
        Matcher m = p.matcher(input);
        if (m.lookingAt()) {

            //checking if it is not a keyword:
            for (Token.Type tokenType: Token.Type.values()) {
                if(tokenType.getGroup().equals("KEYWORD") &&
                        m.group().equals(tokenType.getText())) {
                    return false;
                }
            }

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
        for (Token.Type type : Token.Type.values()) {
            String group = type.getGroup();
            if (group.equals("KEYWORD") || group.equals("SYMBOL")) {
                output = output || tryToken(type, addToken, start);
            }
        }
        return output;
    }

    private boolean handleComment() {
        boolean output = false;
        int i = 0;
        if (tryToken(Token.Type.OPEN_COMMENT, false, i)) {
            i += Token.Type.OPEN_COMMENT.getText().length();
            while (i <= input.length() - 1) {
                if (tryToken(Token.Type.CLOSE_COMMENT, false, i++))
                    break;
            }
            consumeInput(i - 1 + Token.Type.CLOSE_COMMENT.getText().length());
            output = true;
        }
        i = 0;
        if (tryToken(Token.Type.SINGLE_LINE_COMMENT, false, i)) {
            i += Token.Type.SINGLE_LINE_COMMENT.getText().length();
            while (i <= input.length() - 1) {
                if (tryToken(Token.Type.NEWLINE, false, i++))
                    break;
            }
            consumeInput(i - 1 + Token.Type.NEWLINE.getText().length());
            output = true;
        }

        return output;
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
            lexical_errors.add(new Token(Token.Type.INVALID_INPUT, invalid_token.toString(), line, col));
            consumeInput(invalid_token.length());
        }

        return thereIsError;
    }


}
