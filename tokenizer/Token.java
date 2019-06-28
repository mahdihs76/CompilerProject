package tokenizer;

import java.util.ArrayList;

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

    public Token(Type type, String text, int line) {
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




    public boolean isFirst(String nonTerminalShortName) {
        for(Token.Type terminalType: firstSet(nonTerminalShortName)){
            if(terminalType == this.type)
                return true;
        }
        return false;
    }

    public boolean isFollow(String nonTerminalShortName) {
        for(Token.Type terminalType: followSet(nonTerminalShortName)){
            if(terminalType == this.type)
                return true;
        }
        return false;
    }






    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////
    //////                                                   //////
    //////    We got the "First" and "Follow" sets from :    //////
    //////    "https://mikedevice.github.io/first-follow"    //////
    //////                                                   //////
    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////




    public ArrayList<Token.Type> firstSet(String NT) {
        ArrayList<Token.Type> ret = new ArrayList<Token.Type>();
        switch (NT){
            case "P":
                ret.add(Type.EOF);
                ret.add(Type.INT);
                ret.add(Type.VOID);
                break;
            case "DL":
                ret.add(Type.EPSILON);
                ret.add(Type.INT);
                ret.add(Type.VOID);
                break;
            case "TS":
                ret.add(Type.INT);
                ret.add(Type.VOID);
                break;
            case "VD":
                ret.add(Type.SEMICOLON);
                ret.add(Type.OPEN_BRACKETS);
                break;
            case "FD":
                ret.add(Type.OPEN_PARENTHESES);
                break;
            case "PARS":
                ret.add(Type.INT);
                ret.add(Type.VOID);
                break;
            case "VPAR":
                ret.add(Type.ID);
                ret.add(Type.EPSILON);
                break;
            case "PL":
                ret.add(Type.COMMA);
                ret.add(Type.EPSILON);
                break;
            case "BRCK":
                ret.add(Type.EPSILON);
                ret.add(Type.OPEN_BRACKETS);
                break;
            case "CS":
                ret.add(Type.OPEN_BRACES);
                break;
            case "SL":
                ret.add(Type.EPSILON);
                ret.add(Type.OPEN_BRACES);
                ret.add(Type.CONTINUE);
                ret.add(Type.BREAK);
                ret.add(Type.SEMICOLON);
                ret.add(Type.IF);
                ret.add(Type.WHILE);
                ret.add(Type.RETURN);
                ret.add(Type.SWITCH);
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.NUM);
                break;
            case "ES":
                ret.add(Type.CONTINUE);
                ret.add(Type.BREAK);
                ret.add(Type.SEMICOLON);
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.NUM);
                break;
            case "SS":
                ret.add(Type.IF);
                break;
            case "IS":
                ret.add(Type.WHILE);
                break;
            case "RS":
                ret.add(Type.RETURN);
                break;
            case "RVAL":
                ret.add(Type.SEMICOLON);
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.NUM);
                break;
            case "SWS":
                ret.add(Type.SWITCH);
                break;
            case "CASS":
                ret.add(Type.EPSILON);
                ret.add(Type.CASE);
                break;
            case "DS":
                ret.add(Type.DEFAULT);
                ret.add(Type.EPSILON);
                break;
            case "E":
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.NUM);
                break;
            case "EID":
                ret.add(Type.ASSIGN);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.OPEN_BRACKETS);
                ret.add(Type.EPSILON);
                ret.add(Type.TIMES);
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                break;
            case "EID1":
                ret.add(Type.ASSIGN);
                ret.add(Type.EPSILON);
                ret.add(Type.TIMES);
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                break;
            case "SE1":
                ret.add(Type.EPSILON);
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                break;
            case "AE1":
                ret.add(Type.EPSILON);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                break;
            case "A":
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                break;
            case "R":
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                break;
            case "T1":
                ret.add(Type.EPSILON);
                ret.add(Type.TIMES);
                break;
            case "SF1":
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.NUM);
                break;
            case "F":
                ret.add(Type.ID);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.NUM);
                break;
            case "F1":
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.NUM);
                break;
            case "VC":
                ret.add(Type.ID);
                break;
            case "VC1":
                ret.add(Type.EPSILON);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.OPEN_BRACKETS);
                break;
            case "VC2":
                ret.add(Type.EPSILON);
                ret.add(Type.OPEN_PARENTHESES);
                break;
            case "ARGS":
                ret.add(Type.EPSILON);
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.NUM);
                break;
            case "ARL1":
                ret.add(Type.COMMA);
                ret.add(Type.EPSILON);
                break;
            case "D":
                ret.add(Type.INT);
                ret.add(Type.VOID);
                break;
            case "VDFD":
                ret.add(Type.SEMICOLON);
                ret.add(Type.OPEN_BRACKETS);
                ret.add(Type.OPEN_PARENTHESES);
                break;
            case "T":
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.NUM);
                break;
            case "SF":
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.NUM);
                break;
            case "ARL":
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.NUM);
                break;
            case "AE":
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.NUM);
                break;
            case "S":
                ret.add(Type.CONTINUE);
                ret.add(Type.BREAK);
                ret.add(Type.SEMICOLON);
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.NUM);
                ret.add(Type.OPEN_BRACES);
                ret.add(Type.IF);
                ret.add(Type.WHILE);
                ret.add(Type.RETURN);
                ret.add(Type.SWITCH);
                break;
        }
        return ret;
    }



    public ArrayList<Token.Type> followSet(String NT) {
        ArrayList<Token.Type> ret = new ArrayList<Token.Type>();
        switch (NT){
            case "P":
                //ret.add(Type.EOF);
                break;
            case "DL":
                ret.add(Token.Type.EOF);
                ret.add(Token.Type.OPEN_BRACES);
                ret.add(Token.Type.CONTINUE);
                ret.add(Token.Type.BREAK);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.IF);
                ret.add(Token.Type.WHILE);
                ret.add(Token.Type.RETURN);
                ret.add(Token.Type.SWITCH);
                ret.add(Token.Type.ID);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.OPEN_PARENTHESES);
                ret.add(Token.Type.NUM);
                ret.add(Token.Type.CLOSE_BRACES);
                break;
            case "D":
                ret.add(Token.Type.INT);
                ret.add(Token.Type.VOID);
                ret.add(Token.Type.EOF);
                ret.add(Token.Type.OPEN_BRACES);
                ret.add(Token.Type.CONTINUE);
                ret.add(Token.Type.BREAK);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.IF);
                ret.add(Token.Type.WHILE);
                ret.add(Token.Type.RETURN);
                ret.add(Token.Type.SWITCH);
                ret.add(Token.Type.ID);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.OPEN_PARENTHESES);
                ret.add(Token.Type.NUM);
                ret.add(Token.Type.CLOSE_BRACES);
                break;
            case "TS":
                ret.add(Token.Type.ID);
                break;
            case "VDFD":
                ret.add(Token.Type.INT);
                ret.add(Token.Type.VOID);
                ret.add(Token.Type.EOF);
                ret.add(Token.Type.OPEN_BRACES);
                ret.add(Token.Type.CONTINUE);
                ret.add(Token.Type.BREAK);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.IF);
                ret.add(Token.Type.WHILE);
                ret.add(Token.Type.RETURN);
                ret.add(Token.Type.SWITCH);
                ret.add(Token.Type.ID);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.OPEN_PARENTHESES);
                ret.add(Token.Type.NUM);
                ret.add(Token.Type.CLOSE_BRACES);
                break;
            case "VD":
                ret.add(Token.Type.INT);
                ret.add(Token.Type.VOID);
                ret.add(Token.Type.EOF);
                ret.add(Token.Type.OPEN_BRACES);
                ret.add(Token.Type.CONTINUE);
                ret.add(Token.Type.BREAK);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.IF);
                ret.add(Token.Type.WHILE);
                ret.add(Token.Type.RETURN);
                ret.add(Token.Type.SWITCH);
                ret.add(Token.Type.ID);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.OPEN_PARENTHESES);
                ret.add(Token.Type.NUM);
                ret.add(Token.Type.CLOSE_BRACES);
                break;
            case "FD":
                ret.add(Token.Type.INT);
                ret.add(Token.Type.VOID);
                ret.add(Token.Type.EOF);
                ret.add(Token.Type.OPEN_BRACES);
                ret.add(Token.Type.CONTINUE);
                ret.add(Token.Type.BREAK);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.IF);
                ret.add(Token.Type.WHILE);
                ret.add(Token.Type.RETURN);
                ret.add(Token.Type.SWITCH);
                ret.add(Token.Type.ID);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.OPEN_PARENTHESES);
                ret.add(Token.Type.NUM);
                ret.add(Token.Type.CLOSE_BRACES);
                break;
            case "PARS":
                ret.add(Token.Type.CLOSE_PARENTHESES);
                break;
            case "VPAR":
                ret.add(Token.Type.CLOSE_PARENTHESES);
                break;
            case "PL":
                ret.add(Token.Type.CLOSE_PARENTHESES);
                break;
            case "BRCK":
                ret.add(Token.Type.COMMA);
                ret.add(Token.Type.CLOSE_PARENTHESES);
                break;
            case "CS":
                ret.add(Token.Type.INT);
                ret.add(Token.Type.VOID);
                ret.add(Token.Type.EOF);
                ret.add(Token.Type.OPEN_BRACES);
                ret.add(Token.Type.CONTINUE);
                ret.add(Token.Type.BREAK);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.IF);
                ret.add(Token.Type.WHILE);
                ret.add(Token.Type.RETURN);
                ret.add(Token.Type.SWITCH);
                ret.add(Token.Type.ID);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.OPEN_PARENTHESES);
                ret.add(Token.Type.NUM);
                ret.add(Token.Type.CLOSE_BRACES);
                ret.add(Token.Type.ELSE);
                ret.add(Token.Type.CASE);
                ret.add(Token.Type.DEFAULT);
                break;
            case "SL":
                ret.add(Token.Type.CLOSE_BRACES);
                ret.add(Token.Type.CASE);
                ret.add(Token.Type.DEFAULT);
                break;
            case "S":
                ret.add(Token.Type.OPEN_BRACES);
                ret.add(Token.Type.CONTINUE);
                ret.add(Token.Type.BREAK);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.IF);
                ret.add(Token.Type.WHILE);
                ret.add(Token.Type.RETURN);
                ret.add(Token.Type.SWITCH);
                ret.add(Token.Type.ID);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.OPEN_PARENTHESES);
                ret.add(Token.Type.NUM);
                ret.add(Token.Type.CLOSE_BRACES);
                ret.add(Token.Type.ELSE);
                ret.add(Token.Type.CASE);
                ret.add(Token.Type.DEFAULT);
                break;
            case "ES":
                ret.add(Token.Type.OPEN_BRACES);
                ret.add(Token.Type.CONTINUE);
                ret.add(Token.Type.BREAK);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.IF);
                ret.add(Token.Type.WHILE);
                ret.add(Token.Type.RETURN);
                ret.add(Token.Type.SWITCH);
                ret.add(Token.Type.ID);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.OPEN_PARENTHESES);
                ret.add(Token.Type.NUM);
                ret.add(Token.Type.CLOSE_BRACES);
                ret.add(Token.Type.ELSE);
                ret.add(Token.Type.CASE);
                ret.add(Token.Type.DEFAULT);
                break;
            case "SS":
                ret.add(Token.Type.OPEN_BRACES);
                ret.add(Token.Type.CONTINUE);
                ret.add(Token.Type.BREAK);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.IF);
                ret.add(Token.Type.WHILE);
                ret.add(Token.Type.RETURN);
                ret.add(Token.Type.SWITCH);
                ret.add(Token.Type.ID);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.OPEN_PARENTHESES);
                ret.add(Token.Type.NUM);
                ret.add(Token.Type.CLOSE_BRACES);
                ret.add(Token.Type.ELSE);
                ret.add(Token.Type.CASE);
                ret.add(Token.Type.DEFAULT);
                break;
            case "IS":
                ret.add(Token.Type.OPEN_BRACES);
                ret.add(Token.Type.CONTINUE);
                ret.add(Token.Type.BREAK);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.IF);
                ret.add(Token.Type.WHILE);
                ret.add(Token.Type.RETURN);
                ret.add(Token.Type.SWITCH);
                ret.add(Token.Type.ID);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.OPEN_PARENTHESES);
                ret.add(Token.Type.NUM);
                ret.add(Token.Type.CLOSE_BRACES);
                ret.add(Token.Type.ELSE);
                ret.add(Token.Type.CASE);
                ret.add(Token.Type.DEFAULT);
                break;
            case "RS":
                ret.add(Token.Type.OPEN_BRACES);
                ret.add(Token.Type.CONTINUE);
                ret.add(Token.Type.BREAK);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.IF);
                ret.add(Token.Type.WHILE);
                ret.add(Token.Type.RETURN);
                ret.add(Token.Type.SWITCH);
                ret.add(Token.Type.ID);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.OPEN_PARENTHESES);
                ret.add(Token.Type.NUM);
                ret.add(Token.Type.CLOSE_BRACES);
                ret.add(Token.Type.ELSE);
                ret.add(Token.Type.CASE);
                ret.add(Token.Type.DEFAULT);
                break;
            case "RVAL":
                ret.add(Token.Type.OPEN_BRACES);
                ret.add(Token.Type.CONTINUE);
                ret.add(Token.Type.BREAK);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.IF);
                ret.add(Token.Type.WHILE);
                ret.add(Token.Type.RETURN);
                ret.add(Token.Type.SWITCH);
                ret.add(Token.Type.ID);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.OPEN_PARENTHESES);
                ret.add(Token.Type.NUM);
                ret.add(Token.Type.CLOSE_BRACES);
                ret.add(Token.Type.ELSE);
                ret.add(Token.Type.CASE);
                ret.add(Token.Type.DEFAULT);
                break;
            case "SWS":
                ret.add(Token.Type.OPEN_BRACES);
                ret.add(Token.Type.CONTINUE);
                ret.add(Token.Type.BREAK);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.IF);
                ret.add(Token.Type.WHILE);
                ret.add(Token.Type.RETURN);
                ret.add(Token.Type.SWITCH);
                ret.add(Token.Type.ID);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.OPEN_PARENTHESES);
                ret.add(Token.Type.NUM);
                ret.add(Token.Type.CLOSE_BRACES);
                ret.add(Token.Type.ELSE);
                ret.add(Token.Type.CASE);
                ret.add(Token.Type.DEFAULT);
                break;
            case "CASS":
                ret.add(Type.CLOSE_BRACES);
                ret.add(Token.Type.DEFAULT);
                break;
            case "DS":
                ret.add(Token.Type.CLOSE_BRACES);
                break;
            case "E":
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.CLOSE_PARENTHESES);
                ret.add(Token.Type.CLOSE_BRACKETS);
                ret.add(Token.Type.COMMA);
                break;
            case "EID":
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.CLOSE_PARENTHESES);
                ret.add(Token.Type.CLOSE_BRACKETS);
                ret.add(Token.Type.COMMA);
                break;
            case "EID1":
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.CLOSE_PARENTHESES);
                ret.add(Token.Type.CLOSE_BRACKETS);
                ret.add(Token.Type.COMMA);
                break;
            case "SE1":
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.CLOSE_PARENTHESES);
                ret.add(Token.Type.CLOSE_BRACKETS);
                ret.add(Token.Type.COMMA);
                break;
            case "AE":
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.CLOSE_PARENTHESES);
                ret.add(Token.Type.CLOSE_BRACKETS);
                ret.add(Token.Type.COMMA);
                break;
            case "AE1":
                ret.add(Token.Type.LESS_THAN);
                ret.add(Token.Type.EQ);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.CLOSE_PARENTHESES);
                ret.add(Token.Type.CLOSE_BRACKETS);
                ret.add(Token.Type.COMMA);
                break;
            case "A":
                ret.add(Token.Type.ID);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.OPEN_PARENTHESES);
                ret.add(Token.Type.NUM);
                break;
            case "R":
                ret.add(Token.Type.ID);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.OPEN_PARENTHESES);
                ret.add(Token.Type.NUM);
                break;
            case "T":
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.CLOSE_PARENTHESES);
                ret.add(Token.Type.LESS_THAN);
                ret.add(Token.Type.EQ);
                ret.add(Token.Type.CLOSE_BRACKETS);
                ret.add(Token.Type.COMMA);
                break;
            case "T1":
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.LESS_THAN);
                ret.add(Token.Type.EQ);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.CLOSE_PARENTHESES);
                ret.add(Token.Type.CLOSE_BRACKETS);
                ret.add(Token.Type.COMMA);
                break;
            case "SF":
                ret.add(Token.Type.TIMES);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.LESS_THAN);
                ret.add(Token.Type.EQ);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.CLOSE_PARENTHESES);
                ret.add(Token.Type.CLOSE_BRACKETS);
                ret.add(Token.Type.COMMA);
                break;
            case "SF1":
                ret.add(Token.Type.TIMES);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.LESS_THAN);
                ret.add(Token.Type.EQ);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.CLOSE_PARENTHESES);
                ret.add(Token.Type.CLOSE_BRACKETS);
                ret.add(Token.Type.COMMA);
                break;
            case "F":
                ret.add(Token.Type.TIMES);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.LESS_THAN);
                ret.add(Token.Type.EQ);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.CLOSE_PARENTHESES);
                ret.add(Token.Type.CLOSE_BRACKETS);
                ret.add(Token.Type.COMMA);
                break;
            case "F1":
                ret.add(Token.Type.TIMES);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.LESS_THAN);
                ret.add(Token.Type.EQ);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.CLOSE_PARENTHESES);
                ret.add(Token.Type.CLOSE_BRACKETS);
                ret.add(Token.Type.COMMA);
                break;
            case "VC":
                ret.add(Token.Type.TIMES);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.LESS_THAN);
                ret.add(Token.Type.EQ);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.CLOSE_PARENTHESES);
                ret.add(Token.Type.CLOSE_BRACKETS);
                ret.add(Token.Type.COMMA);
                break;
            case "VC1":
                ret.add(Token.Type.TIMES);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.LESS_THAN);
                ret.add(Token.Type.EQ);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.CLOSE_PARENTHESES);
                ret.add(Token.Type.CLOSE_BRACKETS);
                ret.add(Token.Type.COMMA);
                break;
            case "VC2":
                ret.add(Token.Type.TIMES);
                ret.add(Token.Type.PLUS);
                ret.add(Token.Type.MINUS);
                ret.add(Token.Type.LESS_THAN);
                ret.add(Token.Type.EQ);
                ret.add(Token.Type.SEMICOLON);
                ret.add(Token.Type.CLOSE_PARENTHESES);
                ret.add(Token.Type.CLOSE_BRACKETS);
                ret.add(Token.Type.COMMA);
                break;
            case "ARGS":
                ret.add(Token.Type.CLOSE_PARENTHESES);
                break;
            case "ARL":
                ret.add(Token.Type.CLOSE_PARENTHESES);
                break;
            case "ARL1":
                ret.add(Token.Type.CLOSE_PARENTHESES);
                break;
        }
        return ret;
    }





    public ArrayList<String> getTokensFromGroup(String group) {
        ArrayList<String> output = new ArrayList<String>();
        for(Type type: Type.values()) {
            if(type.getGroup().equals(group))
                output.add(type.getText());
        }

        return output;
    }

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

        NUM("num", "NUM"),
        ID("id", "ID"),

        INVALID_INPUT("", "invalid input"),

        EPSILON("EPSILON", "NOTHING");


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
















