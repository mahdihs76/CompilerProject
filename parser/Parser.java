package parser;

import com.sun.source.tree.Tree;
import parser.models.Expression;
import parser.models.Statement;
import parser.models.Type;
import parser.models.expressions.*;
import parser.models.statements.*;
import parser.models.types.BoolType;
import parser.models.types.IntType;
import tokenizer.Token;
import models.TreeNode;
import utility.TreeHandler;


import java.util.ArrayList;
import java.util.List;



public class Parser {

    private ArrayList<Token> input;
    private int inputIndex;
    private Token eof;
    private StringBuilder parsingErrors;

    private TreeNode parseTreeRoot;


    public Parser(ArrayList<Token> input) {
        this.parsingErrors = new StringBuilder();
        this.input = input;
        this.inputIndex = 0;

        this.parseTreeRoot = new TreeNode("P");


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

    public String getParseTreeString(){
        return TreeHandler.iterativePreOrder(this.parseTreeRoot);
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
            parse_declarationList(this.parseTreeRoot);
        else {
            TreeNode child = new TreeNode("DL");
            child.addChild(new TreeNode("ε"));
            this.parseTreeRoot.addChild(child);
        }

        handleMissingTerminalError(Token.Type.EOF);
        this.parseTreeRoot.addChild(new TreeNode("eof"));

        return true;
    }

    private boolean parse_declarationList(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("DL");
        parNode.addChild(thisNode);

        if(isAllowedToEnterNT("DL1") || handleWrongNonTerminalError("DL1"))
            parse_declarationList_1(thisNode);
        else {
            TreeNode child = new TreeNode("DL1");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        return true;
    }

    private boolean parse_declarationList_1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("DL1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("D");
        boolean enter2 = isAllowedToPassEPSILON("DL1");

        if(enter1) {
            if(isAllowedToEnterNT("D") || handleWrongNonTerminalError("D"))
                parse_declaration(thisNode);
            else {
                TreeNode child = new TreeNode("D");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("DL1") || handleWrongNonTerminalError("DL1"))
                parse_declarationList_1(thisNode);
            else {
                TreeNode child = new TreeNode("DL1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }
        else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_declaration(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("D");
        parNode.addChild(thisNode);

        if(isAllowedToEnterNT("TS") || handleWrongNonTerminalError("TS"))
            parse_typeSpecifier(thisNode);
        else {
            TreeNode child = new TreeNode("TS");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        handleMissingTerminalError(Token.Type.ID);
        thisNode.addChild(new TreeNode("id"));

        if(isAllowedToEnterNT("VDFD") || handleWrongNonTerminalError("VDFD"))
            parse_varfunDeclaration(thisNode);
        else {
            TreeNode child = new TreeNode("VDFD");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        return true;
    }

    private boolean parse_varfunDeclaration(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("VDFD");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("VD");
        boolean enter2 = isAllowedToEnterNT("FD");

        if(enter1) {
            if(isAllowedToEnterNT("VD") || handleWrongNonTerminalError("VD"))
                parse_varDeclaration(thisNode);
            else {
                TreeNode child = new TreeNode("VD");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }else if(enter2) {
            if(isAllowedToEnterNT("FD") || handleWrongNonTerminalError("FD"))
                parse_funDeclaration(thisNode);
            else {
                TreeNode child = new TreeNode("FD");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }

        return true;
    }

    private boolean parse_varDeclaration(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("VD");
        parNode.addChild(thisNode);

        if(isAllowedToEnterNT("VD1") || handleWrongNonTerminalError("VD1"))
            parse_varDeclaration_1(thisNode);
        else {
            TreeNode child = new TreeNode("VD1");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        return true;
    }

    private boolean parse_varDeclaration_1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("VD1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.SEMICOLON);
        boolean enter2 = isAllowedToConsumeT(Token.Type.OPEN_BRACKETS);

        if(enter1) {
            handleMissingTerminalError(Token.Type.SEMICOLON);
            thisNode.addChild(new TreeNode(";"));
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.OPEN_BRACKETS);
            thisNode.addChild(new TreeNode("["));
            handleMissingTerminalError(Token.Type.INT_CONST);
            thisNode.addChild(new TreeNode("num"));
            handleMissingTerminalError(Token.Type.CLOSE_BRACKETS);
            thisNode.addChild(new TreeNode("]"));
            handleMissingTerminalError(Token.Type.SEMICOLON);
            thisNode.addChild(new TreeNode(";"));
        }

        return true;
    }

    private boolean parse_typeSpecifier(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("TS");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.INT);
        boolean enter2 = isAllowedToConsumeT(Token.Type.VOID);

        if(enter1) {
            handleMissingTerminalError(Token.Type.INT);
            thisNode.addChild(new TreeNode("int"));
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.VOID);
            thisNode.addChild(new TreeNode("void"));
        }

        return true;
    }

    private boolean parse_funDeclaration(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("FD");
        parNode.addChild(thisNode);

        handleMissingTerminalError(Token.Type.OPEN_PARENTHESES);
        thisNode.addChild(new TreeNode("("));

        if(isAllowedToEnterNT("PAS") || handleWrongNonTerminalError("PAS"))
            parse_params(thisNode);
        else {
            TreeNode child = new TreeNode("PAS");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        handleMissingTerminalError(Token.Type.CLOSE_PARENTHESES);
        thisNode.addChild(new TreeNode(")"));

        if(isAllowedToEnterNT("CS") || handleWrongNonTerminalError("CS"))
            parse_compoundStmt(thisNode);
        else {
            TreeNode child = new TreeNode("CS");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        return true;
    }

    private boolean parse_params(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("PAS");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.INT);
        boolean enter2 = isAllowedToConsumeT(Token.Type.VOID);

        if(enter1) {
            handleMissingTerminalError(Token.Type.INT);
            thisNode.addChild(new TreeNode("int"));
            handleMissingTerminalError(Token.Type.ID);
            thisNode.addChild(new TreeNode("id"));

            if(isAllowedToEnterNT("PA1") || handleWrongNonTerminalError("PA1"))
                parse_param_1(thisNode);
            else {
                TreeNode child = new TreeNode("PA1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("PL1") || handleWrongNonTerminalError("PL1"))
                parse_paramList_1(thisNode);
            else {
                TreeNode child = new TreeNode("PL1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

        }else if(enter2) {
            handleMissingTerminalError(Token.Type.VOID);
            thisNode.addChild(new TreeNode("void"));

            if(isAllowedToEnterNT("PAS1") || handleWrongNonTerminalError("PAS1"))
                parse_params_1(thisNode);
            else {
                TreeNode child = new TreeNode("PAS1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }

        return true;
    }

    private boolean parse_params_1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("PAS1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.ID);
        boolean enter2 = isAllowedToPassEPSILON("PAS1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.ID);
            thisNode.addChild(new TreeNode("id"));

            if(isAllowedToEnterNT("PA1") || handleWrongNonTerminalError("PA1"))
                parse_param_1(thisNode);
            else {
                TreeNode child = new TreeNode("PA1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("PL1") || handleWrongNonTerminalError("PL1"))
                parse_paramList_1(thisNode);
            else {
                TreeNode child = new TreeNode("PL1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_paramList_1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("PL1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.COMMA);
        boolean enter2 = isAllowedToPassEPSILON("PL1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.COMMA);
            thisNode.addChild(new TreeNode(","));

            if(isAllowedToEnterNT("PA") || handleWrongNonTerminalError("PA"))
                parse_param(thisNode);
            else {
                TreeNode child = new TreeNode("PA");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("PL1") || handleWrongNonTerminalError("PL1"))
                parse_paramList_1(thisNode);
            else {
                TreeNode child = new TreeNode("PL1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_param(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("PA");
        parNode.addChild(thisNode);

        if(isAllowedToEnterNT("TS") || handleWrongNonTerminalError("TS"))
            parse_typeSpecifier(thisNode);
        else {
            TreeNode child = new TreeNode("TS");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        handleMissingTerminalError(Token.Type.ID);
        thisNode.addChild(new TreeNode("id"));

        if(isAllowedToEnterNT("PA1") || handleWrongNonTerminalError("PA1"))
            parse_param_1(thisNode);
        else {
            TreeNode child = new TreeNode("PA1");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        return true;
    }

    private boolean parse_param_1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("PA1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.OPEN_BRACKETS);
        boolean enter2 = isAllowedToPassEPSILON("PA1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.OPEN_BRACKETS);
            thisNode.addChild(new TreeNode("["));
            handleMissingTerminalError(Token.Type.CLOSE_BRACKETS);
            thisNode.addChild(new TreeNode("]"));
        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_compoundStmt(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("CS");
        parNode.addChild(thisNode);

        handleMissingTerminalError(Token.Type.OPEN_BRACES);
        thisNode.addChild(new TreeNode("{"));

        if(isAllowedToEnterNT("DL") || handleWrongNonTerminalError("DL"))
            parse_declarationList(thisNode);
        else {
            TreeNode child = new TreeNode("DL");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        if(isAllowedToEnterNT("SL") || handleWrongNonTerminalError("SL"))
            parse_statementList(thisNode);
        else {
            TreeNode child = new TreeNode("SL");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        handleMissingTerminalError(Token.Type.CLOSE_BRACES);
        thisNode.addChild(new TreeNode("}"));

        return true;
    }

    private boolean parse_statementList(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("SL");
        parNode.addChild(thisNode);

        if(isAllowedToEnterNT("SL1") || handleWrongNonTerminalError("SL1"))
            parse_statementList_1(thisNode);
        else {
            TreeNode child = new TreeNode("SL1");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        return true;
    }

    private boolean parse_statementList_1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("SL1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("S");
        boolean enter2 = isAllowedToPassEPSILON("SL1");

        if(enter1) {
            if(isAllowedToEnterNT("S") || handleWrongNonTerminalError("S"))
                parse_statement(thisNode);
            else {
                TreeNode child = new TreeNode("S");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("SL1") || handleWrongNonTerminalError("SL1"))
                parse_statementList_1(thisNode);
            else {
                TreeNode child = new TreeNode("SL1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_statement(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("S");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("ES");
        boolean enter2 = isAllowedToEnterNT("CS");
        boolean enter3 = isAllowedToEnterNT("SS");
        boolean enter4 = isAllowedToEnterNT("IS");
        boolean enter5 = isAllowedToEnterNT("RS");
        boolean enter6 = isAllowedToEnterNT("SWS");

        if(enter1) {
            if (isAllowedToEnterNT("ES") || handleWrongNonTerminalError("ES"))
                parse_expressionStmt(thisNode);
            else {
                TreeNode child = new TreeNode("ES");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }else if(enter2) {
            if (isAllowedToEnterNT("CS") || handleWrongNonTerminalError("CS"))
                parse_compoundStmt(thisNode);
            else {
                TreeNode child = new TreeNode("CS");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }else if(enter3) {
            if (isAllowedToEnterNT("SS") || handleWrongNonTerminalError("SS"))
                parse_selectionStmt(thisNode);
            else {
                TreeNode child = new TreeNode("SS");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }else if(enter4) {
            if (isAllowedToEnterNT("IS") || handleWrongNonTerminalError("IS"))
                parse_iterationStmt(thisNode);
            else {
                TreeNode child = new TreeNode("IS");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }else if(enter5) {
            if (isAllowedToEnterNT("RS") || handleWrongNonTerminalError("RS"))
                parse_returnStmt(thisNode);
            else {
                TreeNode child = new TreeNode("RS");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }else if(enter6) {
            if (isAllowedToEnterNT("SWS") || handleWrongNonTerminalError("SWS"))
                parse_switchStmt(thisNode);
            else {
                TreeNode child = new TreeNode("SWS");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }

        return true;
    }

    private boolean parse_expressionStmt(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("ES");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("E");
        boolean enter2 = isAllowedToConsumeT(Token.Type.CONTINUE);
        boolean enter3 = isAllowedToConsumeT(Token.Type.BREAK);
        boolean enter4 = isAllowedToConsumeT(Token.Type.SEMICOLON);

        if(enter1) {
            if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
                parse_expression(thisNode);
            else {
                TreeNode child = new TreeNode("E");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
            handleMissingTerminalError(Token.Type.SEMICOLON);
            thisNode.addChild(new TreeNode(";"));
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.CONTINUE);
            thisNode.addChild(new TreeNode("continue"));
            handleMissingTerminalError(Token.Type.SEMICOLON);
            thisNode.addChild(new TreeNode(";"));
        }else if(enter3) {
            handleMissingTerminalError(Token.Type.BREAK);
            thisNode.addChild(new TreeNode("break"));
            handleMissingTerminalError(Token.Type.SEMICOLON);
            thisNode.addChild(new TreeNode(";"));
        }else if(enter4) {
            handleMissingTerminalError(Token.Type.SEMICOLON);
            thisNode.addChild(new TreeNode(";"));
        }

        return true;
    }

    private boolean parse_selectionStmt(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("SS");
        parNode.addChild(thisNode);

        handleMissingTerminalError(Token.Type.IF);
        thisNode.addChild(new TreeNode("if"));
        handleMissingTerminalError(Token.Type.OPEN_PARENTHESES);
        thisNode.addChild(new TreeNode("("));

        if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
            parse_expression(thisNode);
        else {
            TreeNode child = new TreeNode("E");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        handleMissingTerminalError(Token.Type.CLOSE_PARENTHESES);
        thisNode.addChild(new TreeNode(")"));

        if(isAllowedToEnterNT("S") || handleWrongNonTerminalError("S"))
            parse_statement(thisNode);
        else {
            TreeNode child = new TreeNode("S");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        handleMissingTerminalError(Token.Type.ELSE);
        thisNode.addChild(new TreeNode("else"));

        if(isAllowedToEnterNT("S") || handleWrongNonTerminalError("S"))
            parse_statement(thisNode);
        else {
            TreeNode child = new TreeNode("S");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        return true;
    }

    private boolean parse_iterationStmt(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("IS");
        parNode.addChild(thisNode);

        handleMissingTerminalError(Token.Type.WHILE);
        thisNode.addChild(new TreeNode("while"));

        handleMissingTerminalError(Token.Type.OPEN_PARENTHESES);
        thisNode.addChild(new TreeNode("("));

        if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
            parse_expression(thisNode);
        else {
            TreeNode child = new TreeNode("E");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        handleMissingTerminalError(Token.Type.CLOSE_PARENTHESES);
        thisNode.addChild(new TreeNode(")"));

        if(isAllowedToEnterNT("S") || handleWrongNonTerminalError("S"))
            parse_statement(thisNode);
        else {
            TreeNode child = new TreeNode("S");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        return true;
    }

    private boolean parse_returnStmt(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("RS");
        parNode.addChild(thisNode);

        handleMissingTerminalError(Token.Type.RETURN);
        thisNode.addChild(new TreeNode("return"));

        if(isAllowedToEnterNT("RS1") | handleWrongNonTerminalError("RS1"))
            parse_returnStmt_1(thisNode);
        else {
            TreeNode child = new TreeNode("RS1");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        return true;
    }

    private boolean parse_returnStmt_1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("RS1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.SEMICOLON);
        boolean enter2 = isAllowedToEnterNT("E");

        if(enter1) {
            handleMissingTerminalError(Token.Type.SEMICOLON);
            thisNode.addChild(new TreeNode(";"));
        }else if(enter2) {
            if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
                parse_expression(thisNode);
            else {
                TreeNode child = new TreeNode("E");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            handleMissingTerminalError(Token.Type.SEMICOLON);
            thisNode.addChild(new TreeNode(";"));
        }

        return true;
    }

    private boolean parse_switchStmt(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("SWS");
        parNode.addChild(thisNode);

        handleMissingTerminalError(Token.Type.SWITCH);
        thisNode.addChild(new TreeNode("switch"));

        handleMissingTerminalError(Token.Type.OPEN_PARENTHESES);
        thisNode.addChild(new TreeNode("("));

        if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
            parse_expression(thisNode);
        else {
            TreeNode child = new TreeNode("E");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        handleMissingTerminalError(Token.Type.CLOSE_PARENTHESES);
        thisNode.addChild(new TreeNode(")"));

        handleMissingTerminalError(Token.Type.OPEN_BRACES);
        thisNode.addChild(new TreeNode("{"));

        if(isAllowedToEnterNT("CASS") || handleWrongNonTerminalError("CASS"))
            parse_caseStmts(thisNode);
        else {
            TreeNode child = new TreeNode("CASS");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        if(isAllowedToEnterNT("DS") || handleWrongNonTerminalError("DS"))
            parse_defaultStmt(thisNode);
        else {
            TreeNode child = new TreeNode("DS");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        handleMissingTerminalError(Token.Type.CLOSE_BRACES);
        thisNode.addChild(new TreeNode("}"));

        return true;
    }

    private boolean parse_caseStmts(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("CASS");
        parNode.addChild(thisNode);

        if(isAllowedToEnterNT("CASS1") || handleWrongNonTerminalError("CASS1"))
            parse_caseStmts_1(thisNode);
        else {
            TreeNode child = new TreeNode("CASS1");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        return true;
    }

    private boolean parse_caseStmts_1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("CASS1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("CAS");
        boolean enter2 = isAllowedToPassEPSILON("CASS1");

        if(enter1) {
            if(isAllowedToEnterNT("CAS") || handleWrongNonTerminalError("CAS"))
                parse_caseStmt(thisNode);
            else {
                TreeNode child = new TreeNode("CAS");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("CASS1") || handleWrongNonTerminalError("CASS1"))
                parse_caseStmts_1(thisNode);
            else {
                TreeNode child = new TreeNode("CASS1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_caseStmt(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("CAS");
        parNode.addChild(thisNode);

        handleMissingTerminalError(Token.Type.CASE);
        thisNode.addChild(new TreeNode("case"));

        handleMissingTerminalError(Token.Type.INT_CONST);
        thisNode.addChild(new TreeNode("num"));

        handleMissingTerminalError(Token.Type.COLON);
        thisNode.addChild(new TreeNode(":"));


        if(isAllowedToEnterNT("SL") || handleWrongNonTerminalError("SL"))
            parse_statementList(thisNode);
        else {
            TreeNode child = new TreeNode("SL");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        return true;
    }

    private boolean parse_defaultStmt(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("DS");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.DEFAULT);
        boolean enter2 = isAllowedToPassEPSILON("DS");

        if(enter1) {
            handleMissingTerminalError(Token.Type.DEFAULT);
            thisNode.addChild(new TreeNode("default"));

            handleMissingTerminalError(Token.Type.COLON);
            thisNode.addChild(new TreeNode(":"));


            if(isAllowedToEnterNT("SL") || handleWrongNonTerminalError("SL"))
                parse_statementList(thisNode);
            else {
                TreeNode child = new TreeNode("SL");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_expression(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("E");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.ID);
        boolean enter2 = isAllowedToEnterNT("SE2");

        if(enter1) {
            handleMissingTerminalError(Token.Type.ID);
            thisNode.addChild(new TreeNode("id"));

            if(isAllowedToEnterNT("E2") || handleWrongNonTerminalError("E2"))
                parse_expression_2(thisNode);
            else {
                TreeNode child = new TreeNode("E2");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

        }else if(enter2) {
            if(isAllowedToEnterNT("SE2") || handleWrongNonTerminalError("SE2"))
                parse_simpleExpression_2(thisNode);
            else {
                TreeNode child = new TreeNode("SE2");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }

        return true;
    }

    private boolean parse_expression_1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("E1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.ASSIGN);
        boolean enter2 = isAllowedToEnterNT("T1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.ASSIGN);
            thisNode.addChild(new TreeNode("="));

            if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
                parse_expression(thisNode);
            else {
                TreeNode child = new TreeNode("E");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

        }else if(enter2) {
            if(isAllowedToEnterNT("T1") || handleWrongNonTerminalError("T1"))
                parse_term_1(thisNode);
            else {
                TreeNode child = new TreeNode("T1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("AE1") || handleWrongNonTerminalError("AE1"))
                parse_additiveExpression_1(thisNode);
            else {
                TreeNode child = new TreeNode("AE1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("SE1") || handleWrongNonTerminalError("SE1"))
                parse_simpleExpression_1(thisNode);
            else {
                TreeNode child = new TreeNode("SE1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }

        return true;
    }

    private boolean parse_expression_2(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("E2");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("V1");
        boolean enter2 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES);

        if(enter1) {
            if(isAllowedToEnterNT("V1") || handleWrongNonTerminalError("V1"))
                parse_var_1(thisNode);
            else {
                TreeNode child = new TreeNode("V1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("E1") || handleWrongNonTerminalError("E1"))
                parse_expression_1(thisNode);
            else {
                TreeNode child = new TreeNode("E1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

        }else if(enter2) {
            handleMissingTerminalError(Token.Type.OPEN_PARENTHESES);
            thisNode.addChild(new TreeNode("("));

            if(isAllowedToEnterNT("AR") || handleWrongNonTerminalError("AR"))
                parse_args(thisNode);
            else {
                TreeNode child = new TreeNode("AR");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            handleMissingTerminalError(Token.Type.CLOSE_PARENTHESES);
            thisNode.addChild(new TreeNode(")"));


            if(isAllowedToEnterNT("T1") || handleWrongNonTerminalError("T1"))
                parse_term_1(thisNode);
            else {
                TreeNode child = new TreeNode("T1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("AE1") || handleWrongNonTerminalError("AE1"))
                parse_additiveExpression_1(thisNode);
            else {
                TreeNode child = new TreeNode("AE1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("SE1") || handleWrongNonTerminalError("SE1"))
                parse_simpleExpression_1(thisNode);
            else {
                TreeNode child = new TreeNode("SE1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }

        return true;
    }

    private boolean parse_var_1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("V1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.OPEN_BRACKETS);
        boolean enter2 = isAllowedToPassEPSILON("V1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.OPEN_BRACKETS);
            thisNode.addChild(new TreeNode("["));

            if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
                parse_expression(thisNode);
            else {
                TreeNode child = new TreeNode("E");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            handleMissingTerminalError(Token.Type.CLOSE_BRACKETS);
            thisNode.addChild(new TreeNode("]"));

        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_simpleExpression_2(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("SE2");
        parNode.addChild(thisNode);

        if(isAllowedToEnterNT("AE2") || handleWrongNonTerminalError("AE2"))
            parse_additiveExpression_2(thisNode);
        else {
            TreeNode child = new TreeNode("AE2");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        if(isAllowedToEnterNT("SE1") || handleWrongNonTerminalError("SE1"))
            parse_simpleExpression_1(thisNode);
        else {
            TreeNode child = new TreeNode("SE1");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        return true;
    }

    private boolean parse_simpleExpression_1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("SE1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("R");
        boolean enter2 = isAllowedToPassEPSILON("SE1");

        if(enter1) {
            if(isAllowedToEnterNT("R") || handleWrongNonTerminalError("R"))
                parse_relop(thisNode);
            else {
                TreeNode child = new TreeNode("R");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("AE") || handleWrongNonTerminalError("AE"))
                parse_additiveExpression(thisNode);
            else {
                TreeNode child = new TreeNode("AE");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_relop(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("R");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.LESS_THAN);
        boolean enter2 = isAllowedToConsumeT(Token.Type.EQ);

        if(enter1) {
            handleMissingTerminalError(Token.Type.LESS_THAN);
            thisNode.addChild(new TreeNode("<"));
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.EQ);
            thisNode.addChild(new TreeNode("=="));
        }

        return true;
    }

    private boolean parse_additiveExpression(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("AE");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("AE2");
        boolean enter2 = isAllowedToConsumeT(Token.Type.ID);

        if(enter1) {
            if(isAllowedToEnterNT("AE2") || handleWrongNonTerminalError("AE2"))
                parse_additiveExpression_2(thisNode);
            else {
                TreeNode child = new TreeNode("AE2");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.ID);
            thisNode.addChild(new TreeNode("id"));

            if(isAllowedToEnterNT("VC") || handleWrongNonTerminalError("VC"))
                parse_varcall(thisNode);
            else {
                TreeNode child = new TreeNode("VC");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("T1") || handleWrongNonTerminalError("T1"))
                parse_term_1(thisNode);
            else {
                TreeNode child = new TreeNode("T1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("AE1") || handleWrongNonTerminalError("AE1"))
                parse_additiveExpression_1(thisNode);
            else {
                TreeNode child = new TreeNode("AE1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }

        return true;
    }

    private boolean parse_additiveExpression_2(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("AE2");
        parNode.addChild(thisNode);

        if(isAllowedToEnterNT("T2") || handleWrongNonTerminalError("T2"))
            parse_term_2(thisNode);
        else {
            TreeNode child = new TreeNode("T2");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        if(isAllowedToEnterNT("AE1") || handleWrongNonTerminalError("AE1"))
            parse_additiveExpression_1(thisNode);
        else {
            TreeNode child = new TreeNode("AE1");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        return true;
    }

    private boolean parse_additiveExpression_1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("AE1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("A");
        boolean enter2 = isAllowedToPassEPSILON("AE1");

        if(enter1) {
            if(isAllowedToEnterNT("A") || handleWrongNonTerminalError("A"))
                parse_addop(thisNode);
            else {
                TreeNode child = new TreeNode("A");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("T") || handleWrongNonTerminalError("T"))
                parse_term(thisNode);
            else {
                TreeNode child = new TreeNode("T");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("AE1") || handleWrongNonTerminalError("AE1"))
                parse_additiveExpression_1(thisNode);
            else {
                TreeNode child = new TreeNode("AE1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_addop(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("A");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.PLUS);
        boolean enter2 = isAllowedToConsumeT(Token.Type.MINUS);

        if(enter1) {
            handleMissingTerminalError(Token.Type.PLUS);
            thisNode.addChild(new TreeNode("+"));
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.MINUS);
            thisNode.addChild(new TreeNode("-"));
        }

        return true;
    }

    private boolean parse_term(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("T");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("T2");
        boolean enter2 = isAllowedToConsumeT(Token.Type.ID);

        if(enter1) {
            if(isAllowedToEnterNT("T2") || handleWrongNonTerminalError("T2"))
                parse_term_2(thisNode);
            else {
                TreeNode child = new TreeNode("T2");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.ID);
            thisNode.addChild(new TreeNode("id"));

            if(isAllowedToEnterNT("VC") || handleWrongNonTerminalError("VC"))
                parse_varcall(thisNode);
            else {
                TreeNode child = new TreeNode("VC");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("T1") || handleWrongNonTerminalError("T1"))
                parse_term_1(thisNode);
            else {
                TreeNode child = new TreeNode("T1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }

        return true;
    }

    private boolean parse_term_2(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("T2");
        parNode.addChild(thisNode);

        if(isAllowedToEnterNT("SF1") || handleWrongNonTerminalError("SF1"))
            parse_signedFactor_1(thisNode);
        else {
            TreeNode child = new TreeNode("SF1");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        if(isAllowedToEnterNT("T1") || handleWrongNonTerminalError("T1"))
            parse_term_1(thisNode);
        else {
            TreeNode child = new TreeNode("T1");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        return true;
    }

    private boolean parse_term_1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("T1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.TIMES);
        boolean enter2 = isAllowedToPassEPSILON("T1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.TIMES);
            thisNode.addChild(new TreeNode("*"));

            if(isAllowedToEnterNT("SF") || handleWrongNonTerminalError("SF"))
                parse_signedFactor(thisNode);
            else {
                TreeNode child = new TreeNode("SF");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("T1") || handleWrongNonTerminalError("SF"))
                parse_term_1(thisNode);
            else {
                TreeNode child = new TreeNode("T1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_signedFactor(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("SF");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.ID);
        boolean enter2 = isAllowedToEnterNT("SF1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.ID);
            thisNode.addChild(new TreeNode("id"));

            if(isAllowedToEnterNT("VC") | handleWrongNonTerminalError("VC"))
                parse_varcall(thisNode);
            else {
                TreeNode child = new TreeNode("VC");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

        }else if(enter2) {
            if(isAllowedToEnterNT("SF1") || handleWrongNonTerminalError("SF1"))
                parse_signedFactor_1(thisNode);
            else {
                TreeNode child = new TreeNode("SF1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }

        return true;
    }

    private boolean parse_signedFactor_1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("SF1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.PLUS);
        boolean enter2 = isAllowedToConsumeT(Token.Type.MINUS);
        boolean enter3 = isAllowedToEnterNT("F1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.PLUS);
            thisNode.addChild(new TreeNode("+"));

            if(isAllowedToEnterNT("F") || handleWrongNonTerminalError("F"))
                parse_factor(thisNode);
            else {
                TreeNode child = new TreeNode("F");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.MINUS);
            thisNode.addChild(new TreeNode("-"));

            if(isAllowedToEnterNT("F") || handleWrongNonTerminalError("F"))
                parse_factor(thisNode);
            else {
                TreeNode child = new TreeNode("F");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }else if(enter3) {
            if(isAllowedToEnterNT("F1") || handleWrongNonTerminalError("F1"))
                parse_factor_1(thisNode);
            else {
                TreeNode child = new TreeNode("F1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }

        return true;
    }

    private boolean parse_factor(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("F");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("F1");
        boolean enter2 = isAllowedToConsumeT(Token.Type.ID);

        if(enter1) {
            if(isAllowedToEnterNT("F1") || handleWrongNonTerminalError("F1"))
                parse_factor_1(thisNode);
            else {
                TreeNode child = new TreeNode("F1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }else if(enter2) {
            handleMissingTerminalError(Token.Type.ID);
            thisNode.addChild(new TreeNode("id"));

            if(isAllowedToEnterNT("VC") || handleWrongNonTerminalError("VC"))
                parse_varcall(
                        thisNode);
            else {
                TreeNode child = new TreeNode("VC");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }

        return true;
    }

    private boolean parse_factor_1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("F1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES);
        boolean enter2 = isAllowedToConsumeT(Token.Type.INT_CONST);

        if(enter1) {
            handleMissingTerminalError(Token.Type.OPEN_PARENTHESES);
            thisNode.addChild(new TreeNode("("));

            if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
                parse_expression(thisNode);
            else {
                TreeNode child = new TreeNode("E");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            handleMissingTerminalError(Token.Type.CLOSE_PARENTHESES);
            thisNode.addChild(new TreeNode(")"));

        }else if(enter2) {
            handleMissingTerminalError(Token.Type.INT_CONST);
            thisNode.addChild(new TreeNode("num"));
        }

        return true;
    }

    private boolean parse_varcall(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("VC");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES);
        boolean enter2 = isAllowedToEnterNT("V1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.OPEN_PARENTHESES);
            thisNode.addChild(new TreeNode("("));

            if(isAllowedToEnterNT("AR") || handleWrongNonTerminalError("AR"))
                parse_args(thisNode);
            else {
                TreeNode child = new TreeNode("AR");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            handleMissingTerminalError(Token.Type.CLOSE_PARENTHESES);
            thisNode.addChild(new TreeNode(")"));

        }else if(enter2) {
            if(isAllowedToEnterNT("V1") || handleWrongNonTerminalError("V1"))
                parse_var_1(thisNode);
            else {
                TreeNode child = new TreeNode("V1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }

        return true;
    }

    private boolean parse_args(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("AR");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("ARL");
        boolean enter2 = isAllowedToPassEPSILON("AR");

        if(enter1) {
            if(isAllowedToEnterNT("ARL") || handleWrongNonTerminalError("ARL"))
                parse_argList(thisNode);
            else {
                TreeNode child = new TreeNode("ARL");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }
        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_argList(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("ARL");
        parNode.addChild(thisNode);

        if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
            parse_expression(thisNode);
        else {
            TreeNode child = new TreeNode("E");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        if(isAllowedToEnterNT("ARL1") || handleWrongNonTerminalError("ARL1"))
            parse_argList_1(thisNode);
        else {
            TreeNode child = new TreeNode("ARL1");
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }

        return true;
    }

    private boolean parse_argList_1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("ARL1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.COMMA);
        boolean enter2 = isAllowedToPassEPSILON("ARL1");

        if(enter1) {
            handleMissingTerminalError(Token.Type.COMMA);
            thisNode.addChild(new TreeNode(","));

            if(isAllowedToEnterNT("E") || handleWrongNonTerminalError("E"))
                parse_expression(thisNode);
            else {
                TreeNode child = new TreeNode("E");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

            if(isAllowedToEnterNT("ARL1") || handleWrongNonTerminalError("ARL1"))
                parse_argList_1(thisNode);
            else {
                TreeNode child = new TreeNode("ARL1");
                child.addChild(new TreeNode("ε"));
                thisNode.addChild(child);
            }

        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
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
