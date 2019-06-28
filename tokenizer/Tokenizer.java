package tokenizer;


import java.util.*;

/**
 * Created by mahdihs76 on 4/3/19.
 */

public class Tokenizer {
    private ArrayList<Token> result;
    private ArrayList<Token> lexical_errors;
    private String input;
    private int line;
    private int inputIndex;
    private int inputLength;
    private boolean ended;

    public ArrayList<Token> get_result() {
        return result;
    }
    public ArrayList<Token> get_lexical_errors() {
        return lexical_errors;
    }

    public Tokenizer(String input) {
        this.result = new ArrayList<>();
        this.lexical_errors = new ArrayList<>();
        this.input = input;
        this.inputLength = input.length();
        this.inputIndex = 0;
        this.line = 1;
        this.ended = false;
    }



    public String getResultString() {
        StringBuilder result_sb = new StringBuilder();
        StringBuilder lexical_errors_sb = new StringBuilder();

        int line = 0;
        boolean isFirstTime = true;

        for (Token token : this.result) {
            if(token.getType() == Token.Type.EOF)
                continue;

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





    private int countNewLines(String str) {
        return str.length() - str.replaceAll("\n", "").length();
    }

    private char consumeInput(){
        char output = peek();
        if(output == '\n')
            line++;
        inputIndex++;
        if(inputIndex >= inputLength)
            ended = true;
        return output;
    }
    private void consumeInput(int length){
        inputIndex += length;
        if(inputIndex >= inputLength)
            ended = true;
        line += countNewLines(input.substring(inputIndex - length, inputIndex));
    }

    private char peek(){
        return input.charAt(inputIndex);
    }
    private char peek(int start){
        return input.charAt(inputIndex + start);
    }







    private boolean isWhitespace() {
        if(ended)
            return false;
        return Character.isWhitespace(peek());
    }
    private void skipWhitespace() {
        if(ended)
            return;
        char currChar = peek();
        while (isWhitespace()) {
            currChar = consumeInput();
        }
    }

    private boolean isExcatChar(char expectedChar) {
        if(ended)
            return false;
        return (peek() == expectedChar);
    }
    private boolean isExcatChar(char expectedChar, int start) {
        if(ended)
            return false;
        return (peek(start) == expectedChar);
    }



    private boolean isExactKeyword(Token.Type expectedType) {
        int start = 0;
        for(char c: expectedType.getText().toCharArray()) {
            if(!isExcatChar(c, start++))
                return false;
        }

        if(isValidNumberChar(start) || isValidLetterChar(start))
            return false;
        return true;
    }

    private boolean isExactSymbol(Token.Type expectedType) {
        int start = 0;
        for(char c: expectedType.getText().toCharArray()) {
            if(!isExcatChar(c, start++))
                return false;
        }
        return true;
    }



    private boolean isValidLetterChar(int start) {
        if(ended)
            return false;
        char c = peek(start);
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isValidSymbolChar() {
        if(ended)
            return false;
        String c = Character.toString(peek());
        String validSymbols = ";,:[](){}+-*=</";
        return validSymbols.contains(c);
    }

    private boolean isValidNumberChar(int start) {
        if(ended)
            return false;
        String c = Character.toString(peek(start));
        String validNumbers = "0123456789";
        return validNumbers.contains(c);
    }

    private boolean isInvalidChar() {
        if(ended) return false;
        if(isValidLetterChar(0)) return false;
        if(isValidSymbolChar()) return false;
        if(isValidNumberChar(0)) return false;
        if(isWhitespace()) return false;
        return true;
    }




    private void consumeSymbolOrKeywordToken(Token.Type expectedType) {
        int start = inputIndex;
        consumeInput(expectedType.getText().length());
        int end = inputIndex;

        result.add(new Token(expectedType, input.substring(start, end), line));
    }

    private void consumeInvalidToken(boolean doAddError) {
        int start = inputIndex;

        char currChar = peek();
        while (!ended && isInvalidChar()) {
            currChar = consumeInput();
        }
        int end = inputIndex;
        if(end >= input.length())
            ended = true;

        if(doAddError)
            lexical_errors.add(new Token(Token.Type.INVALID_INPUT, input.substring(start, end), line));
    }

    private void consumeNumberToken() {
        int start = inputIndex;

        char currChar = peek();
        while (!ended && isValidNumberChar(0)) {
            currChar = consumeInput();
        }
        int end = inputIndex;
        if(end >= input.length())
            ended = true;

        result.add(new Token(Token.Type.NUM, input.substring(start, end), line));
    }

    private void consumeIdentifierToken() {
        int start = inputIndex;

        char currChar = peek();
        while (!ended && (isValidLetterChar(0) || isValidNumberChar(0))) {
            currChar = consumeInput();
        }
        int end = inputIndex;
        if(end >= input.length())
            ended = true;

        if(isInvalidChar()) {
            consumeInvalidToken(false);
            end = inputIndex;
            if(end >= input.length())
                ended = true;
            lexical_errors.add(new Token(Token.Type.INVALID_INPUT, input.substring(start, end), line));
            return;
        }
        result.add(new Token(Token.Type.ID, input.substring(start, end), line));
    }

    private void consumeSingleLineComment() {
        while (!ended && !isExcatChar('\n')) {
            consumeInput();
        }
        if(!ended)
            consumeInput();
    }

    private void consumeMultilineComment() {
        while (!ended && !isExactSymbol(Token.Type.CLOSE_COMMENT)) {
            consumeInput();
        }
        if(!ended) {
            consumeInput();
            consumeInput();
        }

    }



    public Token get_next_token() {
        while(true) {
            Token newToken = try_next_token();
            if(newToken == null)
                continue;
            else return newToken;
        }
    }

    public Token try_next_token() {
        int resultInitialSize = result.size();

        if(ended) {
            Token eofToken = new Token(Token.Type.EOF, "eof", line);
            result.add(eofToken);
            return eofToken;
        }

        if(isWhitespace()) {
            skipWhitespace();
        } else if(isInvalidChar()) {
            consumeInvalidToken(true);
        } else if(isValidNumberChar(0)) {
            consumeNumberToken();
        } else if(isValidSymbolChar()) {

            if(isExactSymbol(Token.Type.SEMICOLON)) consumeSymbolOrKeywordToken(Token.Type.SEMICOLON);
            else if(isExactSymbol(Token.Type.COLON)) consumeSymbolOrKeywordToken(Token.Type.COLON);
            else if(isExactSymbol(Token.Type.COMMA)) consumeSymbolOrKeywordToken(Token.Type.COMMA);
            else if(isExactSymbol(Token.Type.OPEN_BRACKETS)) consumeSymbolOrKeywordToken(Token.Type.OPEN_BRACKETS);
            else if(isExactSymbol(Token.Type.CLOSE_BRACKETS)) consumeSymbolOrKeywordToken(Token.Type.CLOSE_BRACKETS);
            else if(isExactSymbol(Token.Type.OPEN_PARENTHESES)) consumeSymbolOrKeywordToken(Token.Type.OPEN_PARENTHESES);
            else if(isExactSymbol(Token.Type.CLOSE_PARENTHESES)) consumeSymbolOrKeywordToken(Token.Type.CLOSE_PARENTHESES);
            else if(isExactSymbol(Token.Type.OPEN_BRACES)) consumeSymbolOrKeywordToken(Token.Type.OPEN_BRACES);
            else if(isExactSymbol(Token.Type.CLOSE_BRACES)) consumeSymbolOrKeywordToken(Token.Type.CLOSE_BRACES);
            else if(isExactSymbol(Token.Type.PLUS)) consumeSymbolOrKeywordToken(Token.Type.PLUS);
            else if(isExactSymbol(Token.Type.MINUS)) consumeSymbolOrKeywordToken(Token.Type.MINUS);
            else if(isExactSymbol(Token.Type.TIMES)) consumeSymbolOrKeywordToken(Token.Type.TIMES);
            else if(isExactSymbol(Token.Type.EQ)) consumeSymbolOrKeywordToken(Token.Type.EQ);
            else if(isExactSymbol(Token.Type.ASSIGN)) consumeSymbolOrKeywordToken(Token.Type.ASSIGN);
            else if(isExactSymbol(Token.Type.LESS_THAN)) consumeSymbolOrKeywordToken(Token.Type.LESS_THAN);
            else if(isExactSymbol(Token.Type.SINGLE_LINE_COMMENT)) consumeSingleLineComment();
            else if(isExactSymbol(Token.Type.OPEN_COMMENT)) consumeMultilineComment();

        } else if(isValidLetterChar(0)) {

            if(isExactKeyword(Token.Type.IF)) consumeSymbolOrKeywordToken(Token.Type.IF);
            else if(isExactKeyword(Token.Type.ELSE)) consumeSymbolOrKeywordToken(Token.Type.ELSE);
            else if(isExactKeyword(Token.Type.VOID)) consumeSymbolOrKeywordToken(Token.Type.VOID);
            else if(isExactKeyword(Token.Type.INT)) consumeSymbolOrKeywordToken(Token.Type.INT);
            else if(isExactKeyword(Token.Type.WHILE)) consumeSymbolOrKeywordToken(Token.Type.WHILE);
            else if(isExactKeyword(Token.Type.BREAK)) consumeSymbolOrKeywordToken(Token.Type.BREAK);
            else if(isExactKeyword(Token.Type.CONTINUE)) consumeSymbolOrKeywordToken(Token.Type.CONTINUE);
            else if(isExactKeyword(Token.Type.SWITCH)) consumeSymbolOrKeywordToken(Token.Type.SWITCH);
            else if(isExactKeyword(Token.Type.DEFAULT)) consumeSymbolOrKeywordToken(Token.Type.DEFAULT);
            else if(isExactKeyword(Token.Type.CASE)) consumeSymbolOrKeywordToken(Token.Type.CASE);
            else if(isExactKeyword(Token.Type.RETURN)) consumeSymbolOrKeywordToken(Token.Type.RETURN);
            else consumeIdentifierToken();

        }

        Token newToken = null;
        if(result.size() > resultInitialSize)
            newToken = result.get(result.size() - 1);

        return newToken;
    }



}
