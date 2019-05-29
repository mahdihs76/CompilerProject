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

    private ArrayList<Token> input;
    private int inputIndex;
    private Token eof;
    private StringBuilder parsingErrors;



    public Parser(ArrayList<Token> input) {
        this.parsingErrors = new StringBuilder();
        this.input = input;
        this.inputIndex = 0;

        if (input.isEmpty()) {
            this.eof = new Token(Token.Type.EOF, "<EOF>", 1, 1);
        } else {
            Token last = input.get(input.size() - 1);
            this.eof = new Token(Token.Type.EOF, "<EOF>", last.getLine(), last.getCol());
        }
        input.add(this.eof);
    }


    public String getParsingErrorsString(){
        return  this.parsingErrors.toString();
    }




////////////////////////////////////////////////////////////////////////////////////////////////

    private void addMissingTerminalError(Token.Type terminalType){
        parsingErrors.append("line " + input.get(inputIndex - 1).getLine() + " : Syntax Error! Missing " +
                            terminalType.getText() + "\n");
    }

    private void addUnexpectedTerminalError(Token terminalToken) {
        parsingErrors.append("line " + terminalToken.getLine() + " : Syntax Error! Unexpected " +
                            terminalToken.getType().getText() + "\n");
    }

    private void addWrongNonTerminalError(int line, String description) {
        parsingErrors.append("line " + line + " : Syntax Error! Missing " + description + "\n");
    }

    private void addMissingEOFError(){
        parsingErrors.append("line " + this.eof.getLine() + " : Syntax Error! Malformed Input" + "\n");
    }

    private void addUnexpectedEOFError(){
        parsingErrors.append("line " + input.get(input.size() - 2).getLine() + " : Syntax Error! Unexpected EndOfFile" + "\n");
    }




////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean handleWrongNonTerminalError(String nonTerminal) throws Exception {
        boolean inFirst;
        boolean inFollow;
        boolean haveEpsilon;
        Token currToken;

        while(true) {
            currToken = consume();

            inFirst = currToken.isFirst(nonTerminal);
            inFollow = currToken.isFollow(nonTerminal);
            haveEpsilon = hasEPSILONInFirst(nonTerminal);
            if(inFirst || (inFollow && haveEpsilon)){
                inputIndex --;
                break;
            }

            if(currToken.getType() == Token.Type.EOF) {
                this.inputIndex --;
                addUnexpectedEOFError();
                fail("parsing stopped!");
            }
            else{
                addUnexpectedTerminalError(currToken);
            }
        }

        if(inFollow && !haveEpsilon){
            addWrongNonTerminalError(input.get(inputIndex - 1).getLine(), nonTerminal);
            return false;
        }
        return true;

    }

    private void handleMissingTerminalError(Token.Type terminalType) throws Exception {
        if(isAllowedToConsumeT(terminalType))
            consume(terminalType);
        else {
            if(terminalType == Token.Type.EOF){
                addMissingEOFError();
                fail("parsing stopped!");
            }
            if(peek().getType() == Token.Type.EOF){
                addUnexpectedEOFError();
                fail("parsing stopped!");
            }
            addMissingTerminalError(terminalType);
        }

    }



////////////////////////////////////////////////////////////////////////////////////////////////

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
        boolean allowed = false;
        if(actual.getType() == terminalType)
            allowed = true;

        return allowed;
    }


////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean hasEPSILONInFirst(String NT){
        Token auxToken = new Token(Token.Type.EPSILON, "EPSILON");
        return auxToken.isFirst(NT);
    }

    private int getCurrLine(){
        return input.get(inputIndex).getLine();
    }


////////////////////////////////////////////////////////////////////////////

    public boolean parse_program() throws Exception {

        if(isAllowedToEnterNT("DL") || handleWrongNonTerminalError("DL"))
            parse_declarationList();

        handleMissingTerminalError(Token.Type.EOF);

        return true;
    }

    private boolean parse_declarationList() throws Exception {
        if(isAllowedToEnterNT("DL1") || handleWrongNonTerminalError("DL1"))
            parse_declarationList_1();

        return true;
    }

    private boolean parse_declarationList_1() throws Exception {
        boolean enter1 = isAllowedToEnterNT("D");
        boolean enter2 = isAllowedToPassEPSILON("DL1");

        if(enter1) {
            if(isAllowedToEnterNT("D") || handleWrongNonTerminalError("D"))
                parse_declaration();
            if(isAllowedToEnterNT("DL1") || handleWrongNonTerminalError("DL1"))
                parse_declarationList_1();
        }
        else if(enter2) {
            return true;
        }

        return true;
    }

    private boolean parse_declaration() throws Exception {
        if(isAllowedToEnterNT("TS") || handleWrongNonTerminalError("TS"))
            parse_typeSpecifier();

        handleMissingTerminalError(Token.Type.ID);

        if(isAllowedToEnterNT("VDFD") || handleWrongNonTerminalError("VDFD"))
            parse_varfunDeclaration();

        return true;
    }

    private boolean parse_varfunDeclaration() throws Exception {
        boolean enter1 = isAllowedToEnterNT("VD");
        boolean enter2 = isAllowedToEnterNT("FD");

        if(enter1) {
            if(isAllowedToEnterNT("VD") || handleWrongNonTerminalError("VD"))
                parse_varDeclaration();
        }else if(enter2) {
            if(isAllowedToEnterNT("FD") || handleWrongNonTerminalError("FD"))
                parse_funDeclaration();
        }

        return true;
    }

    private boolean parse_varDeclaration() throws Exception {
        if(isAllowedToEnterNT("VD1") || handleWrongNonTerminalError("VD1"))
            parse_varDeclaration_1();

        return true;
    }

    private boolean parse_varDeclaration_1() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.SEMICOLON);
        boolean enter2 = isAllowedToConsumeT(Token.Type.OPEN_BRACKETS);

        if(enter1) {
            handleMissingTerminalError(Token.Type.SEMICOLON);
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.OPEN_BRACKETS);
            handleMissingTerminalError(Token.Type.INT_CONST);
            handleMissingTerminalError(Token.Type.CLOSE_BRACKETS);
            handleMissingTerminalError(Token.Type.SEMICOLON);
        }

        return true;
    }

    private boolean parse_typeSpecifier() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.INT);
        boolean enter2 = isAllowedToConsumeT(Token.Type.VOID);

        if(enter1) {
            handleMissingTerminalError(Token.Type.INT);
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.VOID);
        }

        return true;
    }

    private boolean parse_funDeclaration() throws Exception {
        handleMissingTerminalError(Token.Type.OPEN_PARENTHESES);

        if(isAllowedToEnterNT("PAS") || handleWrongNonTerminalError("PAS"))
            parse_params();

        handleMissingTerminalError(Token.Type.CLOSE_PARENTHESES);

        if(isAllowedToEnterNT("CS") || handleWrongNonTerminalError("CS"))
            parse_compoundStmt();

        return true;
    }

    private boolean parse_params() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.INT);
        boolean enter2 = isAllowedToConsumeT(Token.Type.VOID);

        if(enter1) {
            handleMissingTerminalError(Token.Type.INT);
            handleMissingTerminalError(Token.Type.ID);

            if(isAllowedToEnterNT("PA1") || handleWrongNonTerminalError("PA1"))
                parse_param_1();

            if(isAllowedToEnterNT("PL1") || handleWrongNonTerminalError("PL1"))
                parse_paramList_1();

        }else if(enter2) {
            handleMissingTerminalError(Token.Type.VOID);

            if(isAllowedToEnterNT("PAS1") || handleWrongNonTerminalError("PAS1"))
                parse_params_1();
        }

        return true;
    }

    private boolean parse_params_1() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.ID);
        boolean enter2 = isAllowedToPassEPSILON("PAS1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.ID);

            if(isAllowedToEnterNT("PA1") || handleWrongNonTerminalError("PA1"))
                parse_param_1();

            if(isAllowedToEnterNT("PL1") || handleWrongNonTerminalError("PL1"))
                parse_paramList_1();

        }else if(enter2) {
            return true;
        }

        return true;
    }

    private boolean parse_paramList_1() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.COMMA);
        boolean enter2 = isAllowedToPassEPSILON("PL1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.COMMA);

            if(isAllowedToEnterNT("PA") || handleWrongNonTerminalError("PA"))
                parse_param();

            if(isAllowedToEnterNT("PL1") || handleWrongNonTerminalError("PL1"))
                parse_paramList_1();

        }else if(enter2) {
            return true;
        }

        return true;
    }

    private boolean parse_param() throws Exception {
        if(isAllowedToEnterNT("TS") || handleWrongNonTerminalError("TS"))
            parse_typeSpecifier();

        handleMissingTerminalError(Token.Type.ID);

        if(isAllowedToEnterNT("PA1") || handleWrongNonTerminalError("PA1"))
            parse_param_1();

        return true;
    }

    private boolean parse_param_1() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.OPEN_BRACKETS);
        boolean enter2 = isAllowedToPassEPSILON("PA1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.OPEN_BRACKETS);
            handleMissingTerminalError(Token.Type.CLOSE_BRACKETS);
        }else if(enter2) {
            return true;
        }

        return true;
    }

    private boolean parse_compoundStmt() throws Exception {
        handleMissingTerminalError(Token.Type.OPEN_BRACES);

        if(isAllowedToEnterNT("DL") || handleWrongNonTerminalError("DL"))
            parse_declarationList();

        if(isAllowedToEnterNT("SL") || handleWrongNonTerminalError("SL"))
            parse_statementList();

        handleMissingTerminalError(Token.Type.CLOSE_BRACES);

        return true;
    }

    private boolean parse_statementList() throws Exception {
        if(isAllowedToEnterNT("SL1") || handleWrongNonTerminalError("SL1"))
            parse_statementList_1();

        return true;
    }

    private boolean parse_statementList_1() throws Exception {
        boolean enter1 = isAllowedToEnterNT("S");
        boolean enter2 = isAllowedToPassEPSILON("SL1");

        if(enter1) {
            if(isAllowedToEnterNT("S") || handleWrongNonTerminalError("S"))
                parse_statement();

            if(isAllowedToEnterNT("SL1") || handleWrongNonTerminalError("SL1"))
                parse_statementList_1();

        }else if(enter2) {
            return true;
        }

        return true;
    }

    private boolean parse_statement() throws Exception {
        boolean enter1 = isAllowedToEnterNT("ES");
        boolean enter2 = isAllowedToEnterNT("CS");
        boolean enter3 = isAllowedToEnterNT("SS");
        boolean enter4 = isAllowedToEnterNT("IS");
        boolean enter5 = isAllowedToEnterNT("RS");
        boolean enter6 = isAllowedToEnterNT("SWS");

        if(enter1) {
            if (isAllowedToEnterNT("ES") || handleWrongNonTerminalError("ES"))
                parse_expressionStmt();
        }else if(enter2) {
            if (isAllowedToEnterNT("CS") || handleWrongNonTerminalError("CS"))
                parse_compoundStmt();
        }else if(enter3) {
            if (isAllowedToEnterNT("SS") || handleWrongNonTerminalError("SS"))
                parse_selectionStmt();
        }else if(enter4) {
            if (isAllowedToEnterNT("IS") || handleWrongNonTerminalError("IS"))
                parse_iterationStmt();
        }else if(enter5) {
            if (isAllowedToEnterNT("RS") || handleWrongNonTerminalError("RS"))
                parse_returnStmt();
        }else if(enter6) {
            if (isAllowedToEnterNT("SWS") || handleWrongNonTerminalError("SWS"))
                parse_switchStmt();
        }

        return true;
    }

    private boolean parse_expressionStmt() throws Exception {
        boolean enter1 = isAllowedToEnterNT("E");
        boolean enter2 = isAllowedToConsumeT(Token.Type.CONTINUE);
        boolean enter3 = isAllowedToConsumeT(Token.Type.BREAK);
        boolean enter4 = isAllowedToConsumeT(Token.Type.SEMICOLON);

        if(enter1) {
            if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
                parse_expression();
            handleMissingTerminalError(Token.Type.SEMICOLON);
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.CONTINUE);
            handleMissingTerminalError(Token.Type.SEMICOLON);
        }else if(enter3) {
            handleMissingTerminalError(Token.Type.BREAK);
            handleMissingTerminalError(Token.Type.SEMICOLON);
        }else if(enter4) {
            handleMissingTerminalError(Token.Type.SEMICOLON);
        }

        return true;
    }

    private boolean parse_selectionStmt() throws Exception {
        handleMissingTerminalError(Token.Type.IF);
        handleMissingTerminalError(Token.Type.OPEN_PARENTHESES);

        if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
            parse_expression();

        handleMissingTerminalError(Token.Type.CLOSE_PARENTHESES);

        if(isAllowedToEnterNT("S") || handleWrongNonTerminalError("S"))
            parse_statement();

        handleMissingTerminalError(Token.Type.ELSE);

        if(isAllowedToEnterNT("S") || handleWrongNonTerminalError("S"))
            parse_statement();

        return true;
    }

    private boolean parse_iterationStmt() throws Exception {
        handleMissingTerminalError(Token.Type.WHILE);

        handleMissingTerminalError(Token.Type.OPEN_PARENTHESES);

        if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
            parse_expression();

        handleMissingTerminalError(Token.Type.CLOSE_PARENTHESES);

        if(isAllowedToEnterNT("S") || handleWrongNonTerminalError("S"))
            parse_statement();

        return true;
    }

    private boolean parse_returnStmt() throws Exception {
        handleMissingTerminalError(Token.Type.RETURN);

        if(isAllowedToEnterNT("RS1") | handleWrongNonTerminalError("RS1"))
            parse_returnStmt_1();

        return true;
    }

    private boolean parse_returnStmt_1() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.SEMICOLON);
        boolean enter2 = isAllowedToEnterNT("E");

        if(enter1) {
            handleMissingTerminalError(Token.Type.SEMICOLON);
        }else if(enter2) {
            if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
                parse_expression();
            handleMissingTerminalError(Token.Type.SEMICOLON);
        }

        return true;
    }

    private boolean parse_switchStmt() throws Exception {
        handleMissingTerminalError(Token.Type.SWITCH);

        handleMissingTerminalError(Token.Type.OPEN_PARENTHESES);

        if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
            parse_expression();

        handleMissingTerminalError(Token.Type.CLOSE_PARENTHESES);

        handleMissingTerminalError(Token.Type.OPEN_BRACES);

        if(isAllowedToEnterNT("CASS") || handleWrongNonTerminalError("CASS"))
            parse_caseStmts();

        if(isAllowedToEnterNT("DS") || handleWrongNonTerminalError("DS"))
            parse_defaultStmt();

        handleMissingTerminalError(Token.Type.CLOSE_BRACES);

        return true;
    }

    private boolean parse_caseStmts() throws Exception {
        if(isAllowedToEnterNT("CASS1") || handleWrongNonTerminalError("CASS1"))
            parse_caseStmts_1();

        return true;
    }

    private boolean parse_caseStmts_1() throws Exception {
        boolean enter1 = isAllowedToEnterNT("CAS");
        boolean enter2 = isAllowedToPassEPSILON("CASS1");

        if(enter1) {
            if(isAllowedToEnterNT("CAS") || handleWrongNonTerminalError("CAS"))
                parse_caseStmt();

            if(isAllowedToEnterNT("CASS1") || handleWrongNonTerminalError("CASS1"))
                parse_caseStmts_1();

        }else if(enter2) {
            return true;
        }

        return true;
    }

    private boolean parse_caseStmt() throws Exception {
        handleMissingTerminalError(Token.Type.CASE);

        handleMissingTerminalError(Token.Type.INT_CONST);

        handleMissingTerminalError(Token.Type.COLON);

        if(isAllowedToEnterNT("SL") || handleWrongNonTerminalError("SL"))
            parse_statementList();

        return true;
    }

    private boolean parse_defaultStmt() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.DEFAULT);
        boolean enter2 = isAllowedToPassEPSILON("DS");

        if(enter1) {
            handleMissingTerminalError(Token.Type.DEFAULT);
            handleMissingTerminalError(Token.Type.COLON);

            if(isAllowedToEnterNT("SL") || handleWrongNonTerminalError("SL"))
                parse_statementList();

        }else if(enter2) {
            return true;
        }

        return true;
    }

    private boolean parse_expression() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.ID);
        boolean enter2 = isAllowedToEnterNT("SE2");

        if(enter1) {
            handleMissingTerminalError(Token.Type.ID);

            if(isAllowedToEnterNT("E2") || handleWrongNonTerminalError("E2"))
                parse_expression_2();

        }else if(enter2) {
            if(isAllowedToEnterNT("SE2") || handleWrongNonTerminalError("SE2"))
                parse_simpleExpression_2();
        }

        return true;
    }

    private boolean parse_expression_1() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.ASSIGN);
        boolean enter2 = isAllowedToEnterNT("T1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.ASSIGN);

            if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
                parse_expression();

        }else if(enter2) {
            if(isAllowedToEnterNT("T1") || handleWrongNonTerminalError("T1"))
                parse_term_1();

            if(isAllowedToEnterNT("AE1") || handleWrongNonTerminalError("AE1"))
                parse_additiveExpression_1();

            if(isAllowedToEnterNT("SE1") || handleWrongNonTerminalError("SE1"))
                parse_simpleExpression_1();
        }

        return true;
    }

    private boolean parse_expression_2() throws Exception {
        boolean enter1 = isAllowedToEnterNT("V1");
        boolean enter2 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES);

        if(enter1) {
            if(isAllowedToEnterNT("V1") || handleWrongNonTerminalError("V1"))
                parse_var_1();

            if(isAllowedToEnterNT("E1") || handleWrongNonTerminalError("E1"))
                parse_expression_1();

        }else if(enter2) {
            handleMissingTerminalError(Token.Type.OPEN_PARENTHESES);
            if(isAllowedToEnterNT("AR") || handleWrongNonTerminalError("AR"))
                parse_args();

            handleMissingTerminalError(Token.Type.CLOSE_PARENTHESES);


            if(isAllowedToEnterNT("T1") || handleWrongNonTerminalError("T1"))
                parse_term_1();

            if(isAllowedToEnterNT("AE1") || handleWrongNonTerminalError("AE1"))
                parse_additiveExpression_1();

            if(isAllowedToEnterNT("SE1") || handleWrongNonTerminalError("SE1"))
                parse_simpleExpression_1();
        }

        return true;
    }

    private boolean parse_var_1() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.OPEN_BRACKETS);
        boolean enter2 = isAllowedToPassEPSILON("V1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.OPEN_BRACKETS);

            if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
                parse_expression();

            handleMissingTerminalError(Token.Type.CLOSE_BRACKETS);

        }else if(enter2) {
            return true;
        }

        return true;
    }

    private boolean parse_simpleExpression_2() throws Exception {
        if(isAllowedToEnterNT("AE2") || handleWrongNonTerminalError("AE2"))
            parse_additiveExpression_2();

        if(isAllowedToEnterNT("SE1") || handleWrongNonTerminalError("SE1"))
            parse_simpleExpression_1();

        return true;
    }

    private boolean parse_simpleExpression_1() throws Exception {
        boolean enter1 = isAllowedToEnterNT("R");
        boolean enter2 = isAllowedToPassEPSILON("SE1");

        if(enter1) {
            if(isAllowedToEnterNT("R") || handleWrongNonTerminalError("R"))
                parse_relop();

            if(isAllowedToEnterNT("AE") || handleWrongNonTerminalError("AE"))
                parse_additiveExpression();

        }else if(enter2) {
            return true;
        }

        return true;
    }

    private boolean parse_relop() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.LESS_THAN);
        boolean enter2 = isAllowedToConsumeT(Token.Type.EQ);

        if(enter1) {
            handleMissingTerminalError(Token.Type.LESS_THAN);
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.EQ);
        }

        return true;
    }

    private boolean parse_additiveExpression() throws Exception {
        boolean enter1 = isAllowedToEnterNT("AE2");
        boolean enter2 = isAllowedToConsumeT(Token.Type.ID);

        if(enter1) {
            if(isAllowedToEnterNT("AE2") || handleWrongNonTerminalError("AE2"))
                parse_additiveExpression_2();
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.ID);

            if(isAllowedToEnterNT("VC") || handleWrongNonTerminalError("VC"))
                parse_varcall();

            if(isAllowedToEnterNT("T1") || handleWrongNonTerminalError("T1"))
                parse_term_1();

            if(isAllowedToEnterNT("AE1") || handleWrongNonTerminalError("AE1"))
                parse_additiveExpression_1();
        }

        return true;
    }

    private boolean parse_additiveExpression_2() throws Exception {
        if(isAllowedToEnterNT("T2") || handleWrongNonTerminalError("T2"))
            parse_term_2();

        if(isAllowedToEnterNT("AE1") || handleWrongNonTerminalError("AE1"))
            parse_additiveExpression_1();

        return true;
    }

    private boolean parse_additiveExpression_1() throws Exception {
        boolean enter1 = isAllowedToEnterNT("A");
        boolean enter2 = isAllowedToPassEPSILON("AE1");

        if(enter1) {
            if(isAllowedToEnterNT("A") || handleWrongNonTerminalError("A"))
                parse_addop();

            if(isAllowedToEnterNT("T") || handleWrongNonTerminalError("T"))
                parse_term();

            if(isAllowedToEnterNT("AE1") || handleWrongNonTerminalError("AE1"))
                parse_additiveExpression_1();

        }else if(enter2) {
            return true;
        }

        return true;
    }

    private boolean parse_addop() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.PLUS);
        boolean enter2 = isAllowedToConsumeT(Token.Type.MINUS);

        if(enter1) {
            handleMissingTerminalError(Token.Type.PLUS);
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.MINUS);
        }

        return true;
    }

    private boolean parse_term() throws Exception {
        boolean enter1 = isAllowedToEnterNT("T2");
        boolean enter2 = isAllowedToConsumeT(Token.Type.ID);

        if(enter1) {
            if(isAllowedToEnterNT("T2") || handleWrongNonTerminalError("T2"))
                parse_term_2();
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.ID);

            if(isAllowedToEnterNT("VC") || handleWrongNonTerminalError("VC"))
                parse_varcall();

            if(isAllowedToEnterNT("T1") || handleWrongNonTerminalError("T1"))
                parse_term_1();
        }

        return true;
    }

    private boolean parse_term_2() throws Exception {
        if(isAllowedToEnterNT("SF1") || handleWrongNonTerminalError("SF1"))
            parse_signedFactor_1();

        if(isAllowedToEnterNT("T1") || handleWrongNonTerminalError("T1"))
            parse_term_1();

        return true;
    }

    private boolean parse_term_1() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.TIMES);
        boolean enter2 = isAllowedToPassEPSILON("T1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.TIMES);

            if(isAllowedToEnterNT("SF") || handleWrongNonTerminalError("SF"))
                parse_signedFactor();

            if(isAllowedToEnterNT("T1") || handleWrongNonTerminalError("SF"))
                parse_term_1();

        }else if(enter2) {
            return true;
        }

        return true;
    }

    private boolean parse_signedFactor() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.ID);
        boolean enter2 = isAllowedToEnterNT("SF1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.ID);

            if(isAllowedToEnterNT("VC") | handleWrongNonTerminalError("VC"))
                parse_varcall();

        }else if(enter2) {
            if(isAllowedToEnterNT("SF1") || handleWrongNonTerminalError("SF1"))
                parse_signedFactor_1();
        }

        return true;
    }

    private boolean parse_signedFactor_1() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.PLUS);
        boolean enter2 = isAllowedToConsumeT(Token.Type.MINUS);
        boolean enter3 = isAllowedToEnterNT("F1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.PLUS);
            if(isAllowedToEnterNT("F") || handleWrongNonTerminalError("F"))
                parse_factor();
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.MINUS);
            if(isAllowedToEnterNT("F") || handleWrongNonTerminalError("F"))
                parse_factor();
        }else if(enter3) {
            if(isAllowedToEnterNT("F1") || handleWrongNonTerminalError("F1"))
                parse_factor_1();
        }

        return true;
    }

    private boolean parse_factor() throws Exception {
        boolean enter1 = isAllowedToEnterNT("F1");
        boolean enter2 = isAllowedToConsumeT(Token.Type.ID);

        if(enter1) {
            if(isAllowedToEnterNT("F1") || handleWrongNonTerminalError("F1"))
                parse_factor_1();
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.ID);

            if(isAllowedToEnterNT("VC") || handleWrongNonTerminalError("VC"))
                parse_varcall();
        }

        return true;
    }

    private boolean parse_factor_1() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES);
        boolean enter2 = isAllowedToConsumeT(Token.Type.INT_CONST);

        if(enter1) {
            handleMissingTerminalError(Token.Type.OPEN_PARENTHESES);

            if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
                parse_expression();

            handleMissingTerminalError(Token.Type.CLOSE_PARENTHESES);

        }else if(enter2) {
            handleMissingTerminalError(Token.Type.INT_CONST);
        }

        return true;
    }

    private boolean parse_varcall() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES);
        boolean enter2 = isAllowedToEnterNT("V1") && parse_var_1();

        if(enter1) {
            handleMissingTerminalError(Token.Type.OPEN_PARENTHESES);

            if(isAllowedToEnterNT("AR") || handleWrongNonTerminalError("AR"))
                parse_args();

            handleMissingTerminalError(Token.Type.CLOSE_PARENTHESES);

        }else if(enter2) {
            if(isAllowedToEnterNT("V1") || handleWrongNonTerminalError("V1"))
                parse_var_1();
        }

        return true;
    }

    private boolean parse_args() throws Exception {
        boolean enter1 = isAllowedToEnterNT("ARL");
        boolean enter2 = isAllowedToPassEPSILON("AR");

        if(enter1) {
            if(isAllowedToEnterNT("ARL") || handleWrongNonTerminalError("ARL"))
                parse_argList();
        }else if(enter2) {
            return true;
        }

        return true;
    }

    private boolean parse_argList() throws Exception {
        if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
            parse_expression();

        if(isAllowedToEnterNT("ARL1") || handleWrongNonTerminalError("ARL1"))
            parse_argList_1();

        return true;
    }

    private boolean parse_argList_1() throws Exception {
        boolean enter1 = isAllowedToConsumeT(Token.Type.COMMA);
        boolean enter2 = isAllowedToPassEPSILON("ARL1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.COMMA);

            if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
                parse_expression();

            if(isAllowedToEnterNT("ARL1") || handleWrongNonTerminalError("ARL1"))
                parse_argList_1();

        }else if(enter2) {
            return  true;
        }

        return true;
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

    private void fail(String error) throws Exception {
        throw new Exception(error);
    }



}
