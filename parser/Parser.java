package parser;

import parser.models.Expression;
import parser.models.Statement;
import parser.models.Type;
import parser.models.expressions.*;
import parser.models.statements.*;
import parser.models.types.BoolType;
import parser.models.types.IntType;
import tokenizer.Token;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    /*
    public static Statement parseStatement(ArrayList<Token> input) {
        Parser parser = new Parser(input);
        Statement stmt = parser.parseStatement();
        parser.consume(Token.Type.EOF);
        return stmt;
    }
    
    public static Expression parseExpr(ArrayList<Token> input) {
        Parser parser = new Parser(input);
        Expression expr = parser.parseExpr();
        parser.consume(Token.Type.EOF);
        return expr;
    }
    */

    private ArrayList<Token> input;
    private int inputIndex;
    private Token eof;

    public Parser(ArrayList<Token> input) {
        this.input = input;
        this.inputIndex = 0;
        if (input.isEmpty()) {
            this.eof = new Token(Token.Type.EOF, "<EOF>", 0, 0);
        } else {
            Token last = input.get(input.size() - 1);
            this.eof = new Token(Token.Type.EOF, "<EOF>", last.getLine(), last.getCol());
        }
        input.add(this.eof);
    }



    private boolean isAllowedToEnterNT(String NT){
        boolean r1 = input.get(inputIndex).isFirst(NT);
        boolean r2 = hasEPSILONInFirst(NT) && input.get(inputIndex).isFollow(NT);
        return r1 || r2;
    }

    private boolean isAllowedToPassEPSILON(String NT){
        return input.get(inputIndex).isFollow(NT);
    }

    private boolean isAllowedToConsumeT(Token.Type terminalType){
        Token actual = peek();
        return (actual.getType() == terminalType);
    }

    private boolean hasEPSILONInFirst(String NT){
        Token auxToken = new Token(Token.Type.EPSILON, "EPSILON");
        return auxToken.isFirst(NT);
    }

    public void addInput(Token newToken){
        this.input.add(newToken);
    }


////////////////////////////////////////////////////////////////////////////

    public boolean parse_program(){
        boolean ret1 = isAllowedToEnterNT("DL") && parse_declarationList();
        if(!ret1) return false;

        boolean ret2 = isAllowedToConsumeT(Token.Type.EOF) && consume(Token.Type.EOF);
        if(!ret2) return false;

        return true;
    }

    private boolean parse_declarationList(){
        boolean ret1 = isAllowedToEnterNT("DL1") && parse_declarationList_1();

        return ret1;
    }

    private boolean parse_declarationList_1(){
        boolean enter1 = isAllowedToEnterNT("D");
        boolean enter2 = isAllowedToPassEPSILON("DL1");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToEnterNT("D") && parse_declaration() &&
                    isAllowedToEnterNT("DL1") && parse_declarationList_1();
        else if(enter2)
            ret = isAllowedToPassEPSILON("DL1");

        return ret;
    }

    private boolean parse_declaration(){
        boolean ret1 = isAllowedToEnterNT("TS") && parse_typeSpecifier();
        if(!ret1) return false;

        boolean ret2 = isAllowedToConsumeT(Token.Type.ID) && consume(Token.Type.ID);
        if(!ret2) return false;

        boolean ret3 = isAllowedToEnterNT("VDFD") && parse_varfunDeclaration();
        if(!ret3) return false;

        return true;
    }

    private boolean parse_varfunDeclaration(){
        boolean enter1 = isAllowedToEnterNT("VD");
        boolean enter2 = isAllowedToEnterNT("FD");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToEnterNT("VD") && parse_varDeclaration();
        else if(enter2)
            ret = isAllowedToEnterNT("FD") && parse_funDeclaration();

        return ret;
    }

    private boolean parse_varDeclaration(){
        boolean ret1 = isAllowedToEnterNT("VD1") && parse_varDeclaration_1();

        return ret1;
    }

    private boolean parse_varDeclaration_1(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.SEMICOLON);
        boolean enter2 = isAllowedToConsumeT(Token.Type.OPEN_BRACKETS);

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.SEMICOLON) && consume(Token.Type.SEMICOLON);
        else if(enter2)
            ret = isAllowedToConsumeT(Token.Type.OPEN_BRACKETS) && consume(Token.Type.OPEN_BRACKETS) &&
                    isAllowedToConsumeT(Token.Type.INT_CONST) && consume(Token.Type.INT_CONST) &&
                    isAllowedToConsumeT(Token.Type.CLOSE_BRACKETS) &&consume(Token.Type.CLOSE_BRACKETS) &&
                    isAllowedToConsumeT(Token.Type.SEMICOLON) &&consume(Token.Type.SEMICOLON);

        return ret;
    }

    private boolean parse_typeSpecifier(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.INT);
        boolean enter2 = isAllowedToConsumeT(Token.Type.VOID);

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.INT) && consume(Token.Type.INT);
        else if(enter2)
            ret = isAllowedToConsumeT(Token.Type.VOID) && consume(Token.Type.VOID);

        return ret;
    }

    private boolean parse_funDeclaration(){
        boolean ret1 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES) && consume(Token.Type.OPEN_PARENTHESES);
        if(!ret1) return false;

        boolean ret2 = isAllowedToEnterNT("PAS") && parse_params();
        if(!ret2) return false;

        boolean ret3 = isAllowedToConsumeT(Token.Type.CLOSE_PARENTHESES) && consume(Token.Type.CLOSE_PARENTHESES);
        if(!ret3) return false;

        boolean ret4 = isAllowedToEnterNT("CS") && parse_compoundStmt();
        if(!ret4) return false;

        return true;
    }

    private boolean parse_params(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.INT);
        boolean enter2 = isAllowedToConsumeT(Token.Type.VOID);

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.INT) && consume(Token.Type.INT) &&
                  isAllowedToConsumeT(Token.Type.ID) && consume(Token.Type.ID) &&
                  isAllowedToEnterNT("PA1") && parse_param_1() &&
                  isAllowedToEnterNT("PL1") && parse_paramList_1();
        else if(enter2)
            ret = isAllowedToConsumeT(Token.Type.VOID) && consume(Token.Type.VOID) &&
                    isAllowedToEnterNT("PAS1") && parse_params_1();

        return ret;
    }

    private boolean parse_params_1(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.ID);
        boolean enter2 = isAllowedToPassEPSILON("PAS1");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.ID) && consume(Token.Type.ID) &&
                    isAllowedToEnterNT("PA1") && parse_param_1() &&
                    isAllowedToEnterNT("PL1") && parse_paramList_1();
        else if(enter2)
            ret = isAllowedToPassEPSILON("PAS1");

        return ret;
    }

    private boolean parse_paramList(){
        boolean ret1 = isAllowedToEnterNT("PA") && parse_param();
        if(!ret1) return false;

        boolean ret2 = isAllowedToEnterNT("PL1") && parse_paramList_1();
        if(!ret2) return false;

        return true;
    }

    private boolean parse_paramList_1(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.COMMA);
        boolean enter2 = isAllowedToPassEPSILON("PL1");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.COMMA) && consume(Token.Type.COMMA) &&
                    isAllowedToEnterNT("PA") && parse_param() &&
                    isAllowedToEnterNT("PL1") && parse_paramList_1();
        else if(enter2)
            ret = isAllowedToPassEPSILON("PL1");

        return ret;
    }

    private boolean parse_param(){
        boolean ret1 = isAllowedToEnterNT("TS") && parse_typeSpecifier();
        if(!ret1) return false;

        boolean ret2 = isAllowedToConsumeT(Token.Type.ID) && consume(Token.Type.ID);
        if(!ret2) return false;

        boolean ret3 = isAllowedToEnterNT("PA1") && parse_param_1();
        if(!ret3) return false;

        return true;
    }

    private boolean parse_param_1(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.OPEN_BRACKETS);
        boolean enter2 = isAllowedToPassEPSILON("PA1");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.OPEN_BRACKETS) && consume(Token.Type.OPEN_BRACKETS) &&
                    isAllowedToConsumeT(Token.Type.CLOSE_BRACKETS) && consume(Token.Type.CLOSE_BRACKETS);
        else if(enter2)
            ret = isAllowedToPassEPSILON("PA1");

        return ret;
    }

    private boolean parse_compoundStmt(){
        boolean ret1 = isAllowedToConsumeT(Token.Type.OPEN_BRACES) && consume(Token.Type.OPEN_BRACES);
        if(!ret1) return false;

        boolean ret2 = isAllowedToEnterNT("DL") && parse_declarationList();
        if(!ret2) return false;

        boolean ret3 = isAllowedToEnterNT("SL") && parse_statementList();
        if(!ret3) return false;

        boolean ret4 = isAllowedToConsumeT(Token.Type.CLOSE_BRACES) && consume(Token.Type.CLOSE_BRACES);
        if(!ret4) return false;

        return true;
    }

    private boolean parse_statementList(){
        boolean ret1 = isAllowedToEnterNT("SL1") && parse_statementList_1();

        return ret1;
    }

    private boolean parse_statementList_1(){
        boolean enter1 = isAllowedToEnterNT("S");
        boolean enter2 = isAllowedToPassEPSILON("SL1");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToEnterNT("S") && parse_statement() &&
                    isAllowedToEnterNT("SL1") && parse_statementList_1();
        else if(enter2)
            ret = isAllowedToPassEPSILON("SL1");

        return ret;
    }

    private boolean parse_statement(){
        boolean enter1 = isAllowedToEnterNT("ES");
        boolean enter2 = isAllowedToEnterNT("CS");
        boolean enter3 = isAllowedToEnterNT("SS");
        boolean enter4 = isAllowedToEnterNT("IS");
        boolean enter5 = isAllowedToEnterNT("RS");
        boolean enter6 = isAllowedToEnterNT("SWS");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToEnterNT("ES") && parse_expressionStmt();
        else if(enter2)
            ret = isAllowedToEnterNT("CS") && parse_compoundStmt();
        else if(enter3)
            ret = isAllowedToEnterNT("SS") && parse_selectionStmt();
        else if(enter4)
            ret = isAllowedToEnterNT("IS") && parse_iterationStmt();
        else if(enter5)
            ret = isAllowedToEnterNT("RS") && parse_returnStmt();
        else if(enter6)
            ret = isAllowedToEnterNT("SWS") && parse_switchStmt();

        return ret;
    }

    private boolean parse_expressionStmt(){
        boolean enter1 = isAllowedToEnterNT("E");
        boolean enter2 = isAllowedToConsumeT(Token.Type.CONTINUE);
        boolean enter3 = isAllowedToConsumeT(Token.Type.BREAK);
        boolean enter4 = isAllowedToConsumeT(Token.Type.SEMICOLON);

        boolean ret = false;
        if(enter1)
            ret = isAllowedToEnterNT("E") && parse_expression() &&
                    isAllowedToConsumeT(Token.Type.SEMICOLON) && consume(Token.Type.SEMICOLON);
        else if(enter2)
            ret = isAllowedToConsumeT(Token.Type.CONTINUE) && consume(Token.Type.CONTINUE) &&
                    isAllowedToConsumeT(Token.Type.SEMICOLON) && consume(Token.Type.SEMICOLON);
        else if(enter3)
            ret = isAllowedToConsumeT(Token.Type.BREAK) && consume(Token.Type.BREAK) &&
                    isAllowedToConsumeT(Token.Type.SEMICOLON) && consume(Token.Type.SEMICOLON);
        else if(enter4)
            ret = isAllowedToConsumeT(Token.Type.SEMICOLON) && consume(Token.Type.SEMICOLON);

        return ret;
    }

    private boolean parse_selectionStmt(){
        boolean ret1 = isAllowedToConsumeT(Token.Type.IF) && consume(Token.Type.IF);
        if(!ret1) return false;

        boolean ret2 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES) && consume(Token.Type.OPEN_PARENTHESES);
        if(!ret2) return false;

        boolean ret3 = isAllowedToEnterNT("E") && parse_expression();
        if(!ret3) return false;

        boolean ret4 = isAllowedToConsumeT(Token.Type.CLOSE_PARENTHESES) && consume(Token.Type.CLOSE_PARENTHESES);
        if(!ret4) return false;

        boolean ret5 = isAllowedToEnterNT("S") && parse_statement();
        if(!ret5) return false;

        boolean ret6 = isAllowedToConsumeT(Token.Type.ELSE) && consume(Token.Type.ELSE);
        if(!ret6) return false;

        boolean ret7 = isAllowedToEnterNT("S") && parse_statement();
        if(!ret7) return false;

        return true;
    }

    private boolean parse_iterationStmt(){
        boolean ret1 = isAllowedToConsumeT(Token.Type.WHILE) && consume(Token.Type.WHILE);
        if(!ret1) return false;

        boolean ret2 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES) && consume(Token.Type.OPEN_PARENTHESES);
        if(!ret2) return false;

        boolean ret3 = isAllowedToEnterNT("E") && parse_expression();
        if(!ret3) return false;

        boolean ret4 = isAllowedToConsumeT(Token.Type.CLOSE_PARENTHESES) && consume(Token.Type.CLOSE_PARENTHESES);
        if(!ret4) return false;

        boolean ret5 = isAllowedToEnterNT("S") && parse_statement();
        if(!ret5) return false;

        return true;
    }

    private boolean parse_returnStmt(){
        boolean ret1 = isAllowedToConsumeT(Token.Type.RETURN) && consume(Token.Type.RETURN);
        if(!ret1) return false;

        boolean ret2 = isAllowedToEnterNT("RS1") && parse_returnStmt_1();
        if(!ret2) return false;

        return true;
    }

    private boolean parse_returnStmt_1(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.SEMICOLON);
        boolean enter2 = isAllowedToEnterNT("E");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.SEMICOLON) && consume(Token.Type.SEMICOLON);
        else if(enter2)
            ret = isAllowedToEnterNT("E") && parse_expression() &&
                    isAllowedToConsumeT(Token.Type.SEMICOLON) && consume(Token.Type.SEMICOLON);

        return ret;
    }

    private boolean parse_switchStmt(){
        boolean ret1 = isAllowedToConsumeT(Token.Type.SWITCH) && consume(Token.Type.SWITCH);
        if(!ret1) return false;

        boolean ret2 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES) && consume(Token.Type.OPEN_PARENTHESES);
        if(!ret2) return false;

        boolean ret3 = isAllowedToEnterNT("E") && parse_expression();
        if(!ret3) return false;

        boolean ret4 = isAllowedToConsumeT(Token.Type.CLOSE_PARENTHESES) && consume(Token.Type.CLOSE_PARENTHESES);
        if(!ret4) return false;

        boolean ret5 = isAllowedToConsumeT(Token.Type.OPEN_BRACES) && consume(Token.Type.OPEN_BRACES);
        if(!ret5) return false;

        boolean ret6 = isAllowedToEnterNT("CASS") && parse_caseStmts();
        if(!ret6) return false;

        boolean ret7 = isAllowedToEnterNT("DS") && parse_defaultStmt();
        if(!ret7) return false;

        boolean ret8 = isAllowedToConsumeT(Token.Type.CLOSE_BRACES) && consume(Token.Type.CLOSE_BRACES);
        if(!ret8) return false;

        return true;
    }

    private boolean parse_caseStmts(){
        boolean ret1 = isAllowedToEnterNT("CASS1") && parse_caseStmts_1();

        return ret1;
    }

    private boolean parse_caseStmts_1(){
        boolean enter1 = isAllowedToEnterNT("CAS");
        boolean enter2 = isAllowedToPassEPSILON("CASS1");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToEnterNT("CAS") && parse_caseStmt() &&
                isAllowedToEnterNT("CASS1") && parse_caseStmts_1();
        else if(enter2)
            ret = isAllowedToPassEPSILON("CASS1");

        return ret;
    }

    private boolean parse_caseStmt(){
        boolean ret1 = isAllowedToConsumeT(Token.Type.CASE) && consume(Token.Type.CASE);
        if(!ret1) return false;

        boolean ret2 = isAllowedToConsumeT(Token.Type.INT_CONST) && consume(Token.Type.INT_CONST);
        if(!ret2) return false;

        boolean ret3 = isAllowedToConsumeT(Token.Type.COLON) && consume(Token.Type.COLON);
        if(!ret3) return false;

        boolean ret4 = isAllowedToEnterNT("SL") && parse_statementList();
        if(!ret4) return false;

        return true;
    }

    private boolean parse_defaultStmt(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.DEFAULT);
        boolean enter2 = isAllowedToPassEPSILON("DS");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.DEFAULT) && consume(Token.Type.DEFAULT) &&
                isAllowedToConsumeT(Token.Type.COLON) && consume(Token.Type.COLON) &&
                isAllowedToEnterNT("SL") && parse_statementList();
        else if(enter2)
            ret = isAllowedToPassEPSILON("DS");

        return ret;
    }

    private boolean parse_expression(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.ID);
        boolean enter2 = isAllowedToEnterNT("SE2");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.ID) && consume(Token.Type.ID) &&
                    isAllowedToEnterNT("E2") && parse_expression_2();
        else if(enter2)
            ret = isAllowedToEnterNT("SE2") && parse_simpleExpression_2();

        return ret;
    }

    private boolean parse_expression_1(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.ASSIGN);
        boolean enter2 = isAllowedToEnterNT("T1");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.ASSIGN) && consume(Token.Type.ASSIGN) &&
                    isAllowedToEnterNT("E") && parse_expression();
        else if(enter2)
            ret = isAllowedToEnterNT("T1") && parse_term_1() &&
                    isAllowedToEnterNT("AE1") && parse_additiveExpression_1() &&
                    isAllowedToEnterNT("SE1") && parse_simpleExpression_1();

        return ret;
    }

    private boolean parse_expression_2(){
        boolean enter1 = isAllowedToEnterNT("V1");
        boolean enter2 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES);

        boolean ret = false;
        if(enter1)
            ret = isAllowedToEnterNT("V1") && parse_var_1() &&
                isAllowedToEnterNT("E1") && parse_expression_1();
        else if(enter2)
            ret = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES) && consume(Token.Type.OPEN_PARENTHESES) &&
                isAllowedToEnterNT("AR") && parse_args() &&
                isAllowedToConsumeT(Token.Type.CLOSE_PARENTHESES) && consume(Token.Type.CLOSE_PARENTHESES) &&
                isAllowedToEnterNT("T1") && parse_term_1() &&
                isAllowedToEnterNT("AE1") && parse_additiveExpression_1() &&
                isAllowedToEnterNT("SE1") && parse_simpleExpression_1();

        return ret;
    }

    private boolean parse_var(){
        boolean ret1 = isAllowedToConsumeT(Token.Type.ID) && consume(Token.Type.ID);
        if(!ret1) return false;

        boolean ret2 = isAllowedToEnterNT("V1") && parse_var_1();
        if(!ret2) return false;

        return true;
    }

    private boolean parse_var_1(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.OPEN_BRACKETS);
        boolean enter2 = isAllowedToPassEPSILON("V1");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.OPEN_BRACKETS) && consume(Token.Type.OPEN_BRACKETS) &&
                    isAllowedToEnterNT("E") && parse_expression() &&
                    isAllowedToConsumeT(Token.Type.CLOSE_BRACKETS) && consume(Token.Type.CLOSE_BRACKETS);
        else if(enter2)
            ret = isAllowedToPassEPSILON("V1");

        return ret;
    }

    private boolean parse_simpleExpression(){
        boolean enter1 = isAllowedToEnterNT("SE2");
        boolean enter2 = isAllowedToConsumeT(Token.Type.ID);

        boolean ret = false;
        if(enter1)
            ret = isAllowedToEnterNT("SE2") && parse_simpleExpression_2();
        else if(enter2)
            ret = isAllowedToConsumeT(Token.Type.ID) && consume(Token.Type.ID) &&
                isAllowedToEnterNT("VC") && parse_varcall() &&
                isAllowedToEnterNT("T1") && parse_term_1() &&
                isAllowedToEnterNT("AE1") && parse_additiveExpression_1() &&
                isAllowedToEnterNT("SE1") && parse_simpleExpression_1();

        return ret;
    }

    private boolean parse_simpleExpression_2(){
        boolean ret1 = isAllowedToEnterNT("AE2") && parse_additiveExpression_2();
        if(!ret1) return false;

        boolean ret2 = isAllowedToEnterNT("SE1") && parse_simpleExpression_1();
        if(!ret2) return false;

        return true;
    }

    private boolean parse_simpleExpression_1(){
        boolean enter1 = isAllowedToEnterNT("R");
        boolean enter2 = isAllowedToPassEPSILON("SE1");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToEnterNT("R") && parse_relop() &&
                    isAllowedToEnterNT("AE") && parse_additiveExpression();
        else if(enter2)
            ret = isAllowedToPassEPSILON("SE1");

        return ret;
    }

    private boolean parse_relop(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.LESS_THAN);
        boolean enter2 = isAllowedToConsumeT(Token.Type.EQ);

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.LESS_THAN) && consume(Token.Type.LESS_THAN);
        else if(enter2)
            ret = isAllowedToConsumeT(Token.Type.EQ) && consume(Token.Type.EQ);

        return ret;
    }

    private boolean parse_additiveExpression(){
        boolean enter1 = isAllowedToEnterNT("AE2");
        boolean enter2 = isAllowedToConsumeT(Token.Type.ID);

        boolean ret = false;
        if(enter1)
            ret = isAllowedToEnterNT("AE2") && parse_additiveExpression_2();
        else if(enter2)
            ret = isAllowedToConsumeT(Token.Type.ID) && consume(Token.Type.ID) &&
                isAllowedToEnterNT("VC") && parse_varcall() &&
                isAllowedToEnterNT("T1") && parse_term_1() &&
                isAllowedToEnterNT("AE1") && parse_additiveExpression_1();

        return ret;
    }

    private boolean parse_additiveExpression_2(){
        boolean ret1 = isAllowedToEnterNT("T2") && parse_term_2();
        if(!ret1) return false;

        boolean ret2 = isAllowedToEnterNT("AE1") && parse_additiveExpression_1();
        if(!ret2) return false;

        return true;
    }

    private boolean parse_additiveExpression_1(){
        boolean enter1 = isAllowedToEnterNT("A");
        boolean enter2 = isAllowedToPassEPSILON("AE1");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToEnterNT("A") && parse_addop() &&
                isAllowedToEnterNT("T") && parse_term() &&
                isAllowedToEnterNT("AE1") && parse_additiveExpression_1();
        else if(enter2)
            ret = isAllowedToPassEPSILON("AE1");

        return ret;
    }

    private boolean parse_addop(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.PLUS);
        boolean enter2 = isAllowedToConsumeT(Token.Type.MINUS);

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.PLUS) && consume(Token.Type.PLUS);
        else if(enter2)
            ret = isAllowedToConsumeT(Token.Type.MINUS) && consume(Token.Type.MINUS);

        return ret;
    }

    private boolean parse_term(){
        boolean enter1 = isAllowedToEnterNT("T2");
        boolean enter2 = isAllowedToConsumeT(Token.Type.ID);

        boolean ret = false;
        if(enter1)
            ret = isAllowedToEnterNT("T2") && parse_term_2();
        else if(enter2)
            ret = isAllowedToConsumeT(Token.Type.ID) && consume(Token.Type.ID) &&
                isAllowedToEnterNT("VC") && parse_varcall() &&
                isAllowedToEnterNT("T1") && parse_term_1();

        return ret;
    }

    private boolean parse_term_2(){
        boolean ret1 = isAllowedToEnterNT("SF1") && parse_signedFactor_1();
        if(!ret1) return false;

        boolean ret2 = isAllowedToEnterNT("T1") && parse_term_1();
        if(!ret2) return false;

        return true;
    }

    private boolean parse_term_1(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.TIMES);
        boolean enter2 = isAllowedToPassEPSILON("T1");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.TIMES) && consume(Token.Type.TIMES) &&
                isAllowedToEnterNT("SF") && parse_signedFactor() &&
                isAllowedToEnterNT("T1") && parse_term_1();
        else if(enter2)
            ret = isAllowedToPassEPSILON("T1");

        return ret;
    }

    private boolean parse_signedFactor(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.ID);
        boolean enter2 = isAllowedToEnterNT("SF1");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.ID) && consume(Token.Type.ID) &&
                isAllowedToEnterNT("VC") && parse_varcall();
        else if(enter2)
            ret = isAllowedToEnterNT("SF1") && parse_signedFactor_1();

        return ret;
    }

    private boolean parse_signedFactor_1(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.PLUS);
        boolean enter2 = isAllowedToConsumeT(Token.Type.MINUS);
        boolean enter3 = isAllowedToEnterNT("F1");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.PLUS) && consume(Token.Type.PLUS) &&
                    isAllowedToEnterNT("F") && parse_factor();
        else if(enter2)
            ret = isAllowedToConsumeT(Token.Type.MINUS) && consume(Token.Type.MINUS) &&
                    isAllowedToEnterNT("F") && parse_factor();
        else if(enter3)
            ret = isAllowedToEnterNT("F1") && parse_factor_1();

        return ret;
    }

    private boolean parse_factor(){
        boolean enter1 = isAllowedToEnterNT("F1");
        boolean enter2 = isAllowedToConsumeT(Token.Type.ID);

        boolean ret = false;
        if(enter1)
            ret = isAllowedToEnterNT("F1") && parse_factor_1();
        else if(enter2)
            ret = isAllowedToConsumeT(Token.Type.ID) && consume(Token.Type.ID) &&
                isAllowedToEnterNT("VC") && parse_varcall();

        return ret;
    }

    private boolean parse_factor_1(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES);
        boolean enter2 = isAllowedToConsumeT(Token.Type.INT_CONST);

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES) && consume(Token.Type.OPEN_PARENTHESES) &&
                    isAllowedToEnterNT("E") && parse_expression() &&
                    isAllowedToConsumeT(Token.Type.CLOSE_PARENTHESES) && consume(Token.Type.CLOSE_PARENTHESES);
        else if(enter2)
            ret = isAllowedToConsumeT(Token.Type.INT_CONST) && consume(Token.Type.INT_CONST);

        return ret;
    }

    private boolean parse_varcall(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES);
        boolean enter2 = isAllowedToEnterNT("V1") && parse_var_1();

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES) && consume(Token.Type.OPEN_PARENTHESES) &&
                isAllowedToEnterNT("AR") && parse_args() &&
                    isAllowedToConsumeT(Token.Type.CLOSE_PARENTHESES) && consume(Token.Type.CLOSE_PARENTHESES);
        else if(enter2)
            ret = isAllowedToEnterNT("V1") && parse_var_1();

        return ret;
    }

    private boolean parse_call(){
        boolean ret1 = isAllowedToConsumeT(Token.Type.ID) && consume(Token.Type.ID);
        if(!ret1) return false;

        boolean ret2 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES) && consume(Token.Type.OPEN_PARENTHESES);
        if(!ret2) return false;

        boolean ret3 = isAllowedToEnterNT("AR") && parse_args();
        if(!ret3) return false;

        boolean ret4 = isAllowedToConsumeT(Token.Type.CLOSE_PARENTHESES) && consume(Token.Type.CLOSE_PARENTHESES);
        if(!ret4) return false;

        return true;
    }

    private boolean parse_args(){
        boolean enter1 = isAllowedToEnterNT("ARL");
        boolean enter2 = isAllowedToPassEPSILON("AR");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToEnterNT("ARL") && parse_argList();
        else if(enter2)
            ret = isAllowedToPassEPSILON("AR");

        return ret;
    }

    private boolean parse_argList(){
        boolean ret1 = isAllowedToEnterNT("E") && parse_expression();
        if(!ret1) return false;

        boolean ret2 = isAllowedToEnterNT("ARL1") && parse_argList_1();
        if(!ret2) return false;

        return true;
    }

    private boolean parse_argList_1(){
        boolean enter1 = isAllowedToConsumeT(Token.Type.COMMA);
        boolean enter2 = isAllowedToPassEPSILON("ARL1");

        boolean ret = false;
        if(enter1)
            ret = isAllowedToConsumeT(Token.Type.COMMA) && consume(Token.Type.COMMA) &&
                isAllowedToEnterNT("E") && parse_expression() &&
                isAllowedToEnterNT("ARL1") && parse_argList_1();
        else if(enter2)
            ret = isAllowedToPassEPSILON("ARL1");

        return ret;
    }





/////////////////////////////////////////////////////////////////////////////

    /*
    private Statement parseStatement() {
        Token first = peek();
        Token second = peekSecond();
        if (first.getType() == Token.Type.OPEN_BRACES) {
            return parseBlock();
        } else if (first.getType() == Token.Type.WHILE) {
            return parseWhile();
        } else if (first.getType() == Token.Type.IF) {
            return parseIf();
        } else if (first.getType() == Token.Type.ID && second.getType() == Token.Type.COLON) {
            return parseDeclaration();
        } else if (first.getType() == Token.Type.ID && second.getType() == Token.Type.ASSIGN) {
            return parseAssignment();
        } else {
            Expression expression = parseExpr();
            consume(Token.Type.SEMICOLON);
            return expression;
        }
    }

    private Block parseBlock() {
        consume(Token.Type.OPEN_BRACES);
        ArrayList<Statement> statements = new ArrayList<Statement>();
        while (true) {
            Token t = peek();
            if (t.getType() == Token.Type.CLOSE_BRACES) {
                break;
            } else {
                statements.add(parseStatement());
            }
        }
        consume(Token.Type.CLOSE_BRACES);
        return new Block(statements);
    }

    private While parseWhile() {
        consume(Token.Type.WHILE);
        Expression head = parseExpr();
        consume(Token.Type.DO);
        Statement body = parseStatement();
        return new While(head, body);
    }

    private If parseIf() {
        consume(Token.Type.IF);
        Expression condition = parseExpr();
        consume(Token.Type.THEN);
        Statement thenClause = parseStatement();
        if (peek().getType() == Token.Type.ELSE) {
            consume(Token.Type.ELSE);
            Statement elseClause = parseStatement();
            return new If(condition, thenClause, elseClause);
        } else {
            return new If(condition, thenClause);
        }
    }

    private Statement parseDeclaration() {
        String varName = Token.Type.ID.getText();
        consume(Token.Type.COLON);
        Type type = parseType();
        consume(Token.Type.ASSIGN);
        Expression expression = parseExpr();
        Statement decl = new Declaration(varName, type, expression);
        consume(Token.Type.SEMICOLON);
        return decl;
    }

    private Statement parseAssignment() {
        String varName = Token.Type.ID.getText();
        consume(Token.Type.ASSIGN);
        Expression expression = parseExpr();
        Statement assignment = new Assignment(varName, expression);
        consume(Token.Type.SEMICOLON);
        return assignment;
    }

    private Expression parseExpr() {
        Expression left = parseMathexpr();
        Token op = peek();
        switch (op.getType()) {
            case EQ:
            case LESS_THAN:
            case GREATER_THAN:
                consume();
                Expression right = parseMathexpr();
                return new BinaryOperation(left, op.getText(), right);
            default:
                return left;
        }
    }

    private Expression parseMathexpr() {
        Expression left = parseTerm();
        while (true) {
            Token op = peek();
            switch (op.getType()) {
                case PLUS:
                case MINUS:
                    consume();
                    Expression right = parseTerm();
                    left = new BinaryOperation(left, op.getText(), right);
                    break;
                default:
                    return left;
            }
        }
    }

    private Expression parseTerm() {
        Expression left = parseFactor();
        while (true) {
            Token op = peek();
            switch (op.getType()) {
                case TIMES:
                    consume();
                    Expression right = parseFactor();
                    left = new BinaryOperation(left, op.getText(), right);
                    break;
                default:
                    return left;
            }
        }
    }

    private Expression parseFactor() {
        Token t = consume();
        if (t.getType() == Token.Type.OPEN_PARENTHESES) {
            Expression e = parseExpr();
            consume(Token.Type.CLOSE_PARENTHESES);
            return e;
        } else {
            switch (t.getType()) {
                case MINUS: return new UnaryOperation("-", parseFactor());
                case INT: return new IntConstant(Integer.parseInt(t.getText()));
                case BOOL_CONST: return new BoolConstant(Boolean.parseBoolean(t.getText()));
                case ID:
                    if (peek().getType()== Token.Type.OPEN_PARENTHESES) {
                        String functionName = t.getText();
                        consume(Token.Type.OPEN_PARENTHESES);
                        List<Expression> args = parseArguments();
                        consume(Token.Type.CLOSE_PARENTHESES);
                        return new FunctionCall(functionName, args);
                    } else {
                        return new Variable(t.getText());
                    }
                default: return fail("integer or boolean or variable expected instead of '" + t.getText()+ "'");

            }
        }
    }

    private List<Expression> parseArguments() {
        ArrayList<Expression> result = new ArrayList<Expression>();
        while (peek().getType() != Token.Type.CLOSE_PARENTHESES) {
            result.add(parseExpr());
            if (peek().getType() == Token.Type.COMMA) {
                consume(Token.Type.COMMA);
            }
        }
        return result;
    }

    private Type parseType() {
        Token t = consume(Token.Type.ID);
        switch (t.getText()) {
            case "int":
                return IntType.instance;
            case "bool":
                return BoolType.instance;
            default:
                return fail(t.getText() + " is not a known type");
        }
    }
    */

///////////////////////////////////////////////////////////////////////////////






    private Token peek() {
        return peekAtOffset(0);
    }

    private Token peekSecond() {
        return peekAtOffset(1);
    }

    private Token peekAtOffset(int offset) {
        if (inputIndex + offset < input.size()) {
            return input.get(inputIndex + offset);
        } else {
            return eof;
        }
    }



    private boolean consume(Token.Type expected) {
        Token actual = peek();
        if (actual.getType() == expected) {
            inputIndex++;
            return true;
        } else {
            return false;
        }
    }

    private Token consume() {
        Token tok = peek();
        inputIndex++;
        return tok;
    }



    private <T> T fail(String error) {
        Token t = peek();
        throw new ParseException("Parse error near line " + t.getLine() + " col " + t.getCol()+ ": " + error);
    }



}
