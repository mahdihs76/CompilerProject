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




    public ArrayList<Token.Type> firstSet(String NT /*short form of non terminal*/) {
        ArrayList<Token.Type> ret = new ArrayList<Token.Type>();
        switch (NT){
            case "P":
                ret.add(Type.EOF);
                ret.add(Type.INT);
                ret.add(Type.VOID);
                break;
            case "DL1":
                ret.add(Type.EPSILON);
                ret.add(Type.INT);
                ret.add(Type.VOID);
                break;
            case "VD1":
                ret.add(Type.SEMICOLON);
                ret.add(Type.OPEN_BRACKETS);
                break;
            case "TS":
                ret.add(Type.INT);
                ret.add(Type.VOID);
                break;
            case "FD":
                ret.add(Type.OPEN_PARENTHESES);
                break;
            case "PAS":
                ret.add(Type.INT);
                ret.add(Type.VOID);
                break;
            case "PAS1":
                ret.add(Type.ID);
                ret.add(Type.EPSILON);
                break;
            case "PL1":
                ret.add(Type.COMMA);
                ret.add(Type.EPSILON);
                break;
            case "PA":
                ret.add(Type.INT);
                ret.add(Type.VOID);
                break;
            case "PA1":
                ret.add(Type.EPSILON);
                ret.add(Type.OPEN_BRACKETS);
                break;
            case "CS":
                ret.add(Type.OPEN_BRACES);
                break;
            case "SL1":
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
                ret.add(Type.INT_CONST);
                break;
            case "ES":
                ret.add(Type.CONTINUE);
                ret.add(Type.BREAK);
                ret.add(Type.SEMICOLON);
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.INT_CONST);
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
            case "RS1":
                ret.add(Type.SEMICOLON);
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.INT_CONST);
                break;
            case "SWS":
                ret.add(Type.SWITCH);
                break;
            case "CASS1":
                ret.add(Type.EPSILON);
                ret.add(Type.CASE);
                break;
            case "CAS":
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
                ret.add(Type.INT_CONST);
                break;
            case "E1":
                ret.add(Type.ASSIGN);
                ret.add(Type.EPSILON);
                ret.add(Type.TIMES);
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                break;
            case "E2":
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.OPEN_BRACKETS);
                ret.add(Type.ASSIGN);
                ret.add(Type.EPSILON);
                ret.add(Type.TIMES);
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                break;
            case "V":
                ret.add(Type.ID);
                break;
            case "V1":
                ret.add(Type.EPSILON);
                ret.add(Type.OPEN_BRACKETS);
                break;
            case "SE":
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.INT_CONST);
                break;
            case "SE1":
                ret.add(Type.EPSILON);
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                break;
            case "R":
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                break;
            case "AE":
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.INT_CONST);
                break;
            case "AE1":
                ret.add(Type.EPSILON);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
            case "A":
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                break;
            case "T":
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.INT_CONST);
                break;
            case "T1":
                ret.add(Type.EPSILON);
                ret.add(Type.TIMES);
                break;
            case "SF":
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.INT_CONST);
                break;
            case "SF1":
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.INT_CONST);
                break;
            case "F":
                ret.add(Type.ID);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.INT_CONST);
                break;
            case "F1":
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.INT_CONST);
                break;
            case "VC":
                ret.add(Type.EPSILON);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.OPEN_BRACKETS);
                break;
            case "C":
                ret.add(Type.ID);
                break;
            case "AR":
                ret.add(Type.EPSILON);
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.INT_CONST);
                break;
            case "ARL1":
                ret.add(Type.COMMA);
                ret.add(Type.EPSILON);
                break;
            case "D":
                ret.add(Type.INT);
                ret.add(Type.VOID);
                break;
            case "VD":
                ret.add(Type.SEMICOLON);
                ret.add(Type.OPEN_BRACKETS);
                break;
            case "VDFD":
                ret.add(Type.SEMICOLON);
                ret.add(Type.OPEN_BRACKETS);
                ret.add(Type.OPEN_PARENTHESES);
                break;
            case "PL":
                ret.add(Type.INT);
                ret.add(Type.VOID);
                break;
            case "CASS":
                ret.add(Type.EPSILON);
                ret.add(Type.CASE);
                break;
            case "T2":
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.INT_CONST);
                break;
            case "DL":
                ret.add(Type.EPSILON);
                ret.add(Type.INT);
                ret.add(Type.VOID);
                break;
            case "AE2":
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.INT_CONST);
                break;
            case "SE2":
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.INT_CONST);
                break;
            case "ARL":
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.INT_CONST);
                break;
            case "S":
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
                ret.add(Type.INT_CONST);
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
                ret.add(Type.INT_CONST);
                break;
        }
        return ret;
    }



    public ArrayList<Token.Type> followSet(String NT /*short form of non terminal*/) {
        ArrayList<Token.Type> ret = new ArrayList<Token.Type>();
        switch (NT){
            case "P":
                //ret.add(Type.EOF);
                break;
            case "DL":
                ret.add(Type.EOF);
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
                ret.add(Type.INT_CONST);
                ret.add(Type.CLOSE_BRACES);
                break;
            case "DL1":
                ret.add(Type.EOF);
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
                ret.add(Type.INT_CONST);
                ret.add(Type.CLOSE_BRACES);
                break;
            case "D":
                ret.add(Type.INT);
                ret.add(Type.VOID);
                ret.add(Type.EOF);
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
                ret.add(Type.INT_CONST);
                ret.add(Type.CLOSE_BRACES);
                break;
            case "VDFD":
                ret.add(Type.INT);
                ret.add(Type.VOID);
                ret.add(Type.EOF);
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
                ret.add(Type.INT_CONST);
                ret.add(Type.CLOSE_BRACES);
                break;
            case "VD":
                ret.add(Type.INT);
                ret.add(Type.VOID);
                ret.add(Type.EOF);
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
                ret.add(Type.INT_CONST);
                ret.add(Type.CLOSE_BRACES);
                break;
            case "VD1":
                ret.add(Type.INT);
                ret.add(Type.VOID);
                ret.add(Type.EOF);
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
                ret.add(Type.INT_CONST);
                ret.add(Type.CLOSE_BRACES);
                break;
            case "TS":
                ret.add(Type.ID);
                break;
            case "FD":
                ret.add(Type.INT);
                ret.add(Type.VOID);
                ret.add(Type.EOF);
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
                ret.add(Type.INT_CONST);
                ret.add(Type.CLOSE_BRACES);
                break;
            case "PAS":
                ret.add(Type.CLOSE_PARENTHESES);
                break;
            case "PAS1":
                ret.add(Type.CLOSE_PARENTHESES);
                break;
            case "PL":
                break;
            case "PL1":
                ret.add(Type.CLOSE_PARENTHESES);
                break;
            case "PA":
                ret.add(Type.COMMA);
                ret.add(Type.CLOSE_PARENTHESES);
                break;
            case "PA1":
                ret.add(Type.COMMA);
                ret.add(Type.CLOSE_PARENTHESES);
                break;
            case "CS":
                ret.add(Type.INT);
                ret.add(Type.VOID);
                ret.add(Type.EOF);
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
                ret.add(Type.INT_CONST);
                ret.add(Type.CLOSE_BRACES);
                ret.add(Type.ELSE);
                ret.add(Type.CASE);
                ret.add(Type.DEFAULT);
                break;
            case "SL":
                ret.add(Type.CLOSE_BRACES);
                ret.add(Type.CASE);
                ret.add(Type.DEFAULT);
                break;
            case "SL1":
                ret.add(Type.CLOSE_BRACES);
                ret.add(Type.CASE);
                ret.add(Type.DEFAULT);
                break;
            case "S":
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
                ret.add(Type.INT_CONST);
                ret.add(Type.CLOSE_BRACES);
                ret.add(Type.ELSE);
                ret.add(Type.CASE);
                ret.add(Type.DEFAULT);
                break;
            case "ES":
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
                ret.add(Type.INT_CONST);
                ret.add(Type.CLOSE_BRACES);
                ret.add(Type.ELSE);
                ret.add(Type.CASE);
                ret.add(Type.DEFAULT);
                break;
            case "SS":
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
                ret.add(Type.INT_CONST);
                ret.add(Type.CLOSE_BRACES);
                ret.add(Type.ELSE);
                ret.add(Type.CASE);
                ret.add(Type.DEFAULT);
                break;
            case "IS":
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
                ret.add(Type.INT_CONST);
                ret.add(Type.CLOSE_BRACES);
                ret.add(Type.ELSE);
                ret.add(Type.CASE);
                ret.add(Type.DEFAULT);
                break;
            case "RS":
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
                ret.add(Type.INT_CONST);
                ret.add(Type.CLOSE_BRACES);
                ret.add(Type.ELSE);
                ret.add(Type.CASE);
                ret.add(Type.DEFAULT);
                break;
            case "RS1":
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
                ret.add(Type.INT_CONST);
                ret.add(Type.CLOSE_BRACES);
                ret.add(Type.ELSE);
                ret.add(Type.CASE);
                ret.add(Type.DEFAULT);
                break;
            case "SWS":
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
                ret.add(Type.INT_CONST);
                ret.add(Type.CLOSE_BRACES);
                ret.add(Type.ELSE);
                ret.add(Type.CASE);
                ret.add(Type.DEFAULT);
                break;
            case "CASS":
                ret.add(Type.DEFAULT);
                ret.add(Type.CLOSE_BRACES);
                break;
            case "CASS1":
                ret.add(Type.DEFAULT);
                ret.add(Type.CLOSE_BRACES);
                break;
            case "CAS":
                ret.add(Type.CASE);
                ret.add(Type.DEFAULT);
                ret.add(Type.CLOSE_BRACES);
                break;
            case "DS":
                ret.add(Type.CLOSE_BRACES);
                break;
            case "E":
                ret.add(Type.SEMICOLON);
                ret.add(Type.CLOSE_PARENTHESES);
                ret.add(Type.CLOSE_BRACKETS);
                ret.add(Type.COMMA);
                break;
            case "E1":
                ret.add(Type.SEMICOLON);
                ret.add(Type.CLOSE_PARENTHESES);
                ret.add(Type.CLOSE_BRACKETS);
                ret.add(Type.COMMA);
                break;
            case "E2":
                ret.add(Type.SEMICOLON);
                ret.add(Type.CLOSE_PARENTHESES);
                ret.add(Type.CLOSE_BRACKETS);
                ret.add(Type.COMMA);
                break;
            case "V":
                break;
            case "V1":
                ret.add(Type.ASSIGN);
                ret.add(Type.TIMES);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                ret.add(Type.SEMICOLON);
                ret.add(Type.CLOSE_PARENTHESES);
                ret.add(Type.CLOSE_BRACKETS);
                ret.add(Type.COMMA);
                break;
            case "SE":
                break;
            case "SE1":
                ret.add(Type.SEMICOLON);
                ret.add(Type.CLOSE_PARENTHESES);
                ret.add(Type.CLOSE_BRACKETS);
                ret.add(Type.COMMA);
                break;
            case "SE2":
                ret.add(Type.SEMICOLON);
                ret.add(Type.CLOSE_PARENTHESES);
                ret.add(Type.CLOSE_BRACKETS);
                ret.add(Type.COMMA);
                break;
            case "R":
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.INT_CONST);
                break;
            case "AE":
                ret.add(Type.SEMICOLON);
                ret.add(Type.CLOSE_PARENTHESES);
                ret.add(Type.CLOSE_BRACKETS);
                ret.add(Type.COMMA);
                break;
            case "AE1":
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                ret.add(Type.SEMICOLON);
                ret.add(Type.CLOSE_PARENTHESES);
                ret.add(Type.CLOSE_BRACKETS);
                ret.add(Type.COMMA);
                break;
            case "AE2":
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                ret.add(Type.SEMICOLON);
                ret.add(Type.CLOSE_PARENTHESES);
                ret.add(Type.CLOSE_BRACKETS);
                ret.add(Type.COMMA);
                break;
            case "A":
                ret.add(Type.ID);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.OPEN_PARENTHESES);
                ret.add(Type.INT_CONST);
                break;
            case "T":
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                ret.add(Type.SEMICOLON);
                ret.add(Type.CLOSE_PARENTHESES);
                ret.add(Type.CLOSE_BRACKETS);
                ret.add(Type.COMMA);
                break;
            case "T2":
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                ret.add(Type.SEMICOLON);
                ret.add(Type.CLOSE_PARENTHESES);
                ret.add(Type.CLOSE_BRACKETS);
                ret.add(Type.COMMA);
                break;
            case "T1":
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                ret.add(Type.SEMICOLON);
                ret.add(Type.CLOSE_PARENTHESES);
                ret.add(Type.CLOSE_BRACKETS);
                ret.add(Type.COMMA);
                break;
            case "SF":
                ret.add(Type.TIMES);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                ret.add(Type.SEMICOLON);
                ret.add(Type.CLOSE_PARENTHESES);
                ret.add(Type.CLOSE_BRACKETS);
                ret.add(Type.COMMA);
                break;
            case "SF1":
                ret.add(Type.TIMES);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                ret.add(Type.SEMICOLON);
                ret.add(Type.CLOSE_PARENTHESES);
                ret.add(Type.CLOSE_BRACKETS);
                ret.add(Type.COMMA);
                break;
            case "F":
                ret.add(Type.TIMES);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                ret.add(Type.SEMICOLON);
                ret.add(Type.CLOSE_PARENTHESES);
                ret.add(Type.CLOSE_BRACKETS);
                ret.add(Type.COMMA);
                break;
            case "F1":
                ret.add(Type.TIMES);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                ret.add(Type.SEMICOLON);
                ret.add(Type.CLOSE_PARENTHESES);
                ret.add(Type.CLOSE_BRACKETS);
                ret.add(Type.COMMA);
                break;
            case "VC":
                ret.add(Type.TIMES);
                ret.add(Type.PLUS);
                ret.add(Type.MINUS);
                ret.add(Type.LESS_THAN);
                ret.add(Type.EQ);
                ret.add(Type.SEMICOLON);
                ret.add(Type.CLOSE_PARENTHESES);
                ret.add(Type.CLOSE_BRACKETS);
                ret.add(Type.COMMA);
                break;
            case "C":
                break;
            case "AR":
                ret.add(Type.CLOSE_PARENTHESES);
                break;
            case "ARL":
                ret.add(Type.CLOSE_PARENTHESES);
                break;
            case "ARL1":
                ret.add(Type.CLOSE_PARENTHESES);
                break;

        }
        return ret;
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
















