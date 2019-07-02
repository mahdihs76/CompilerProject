package parser;

import models.TreeNode;
import semantic_analysis.SemanticAnalyser;
import tokenizer.Token;
import tokenizer.Tokenizer;
import utility.TreeHandler;

import java.util.ArrayList;
import semantic_analysis.SemanticAnalyser.RoutineType;


public class Parser {

    public class IntIndex{
        public int value;
        public IntIndex(int val) { this.value = val; }
        public void inc() { this.value++; }
        public void dec() { this.value--; }
    }


    private ArrayList<Token> input;
    private IntIndex inputIndex;
    private Token eof;
    private StringBuilder parsingErrors;
    private Tokenizer tokenizer;
    private TreeNode parseTreeRoot;

    private SemanticAnalyser sa;



    public Parser(Tokenizer tokenizer) {
        this.parsingErrors = new StringBuilder();
        this.tokenizer = tokenizer;

        this.input = new ArrayList<Token>();
        this.input.add(tokenizer.get_next_token());

        this.inputIndex = new IntIndex(0);

        this.parseTreeRoot = new TreeNode("P");

        this.sa = new SemanticAnalyser(input, inputIndex);
    }


    public String getParsingErrorsString(){
        return  this.parsingErrors.toString();
    }

    public String getParseTreeString(){
        return TreeHandler.iterativePreOrder(this.parseTreeRoot);
    }



////////////////////////////////////////////////////////////////////////////////////////////////

    private void addMissingTerminalError(Token.Type terminalType){
        parsingErrors.append("line " + input.get(inputIndex.value - 1).getLine() + " : Syntax Error! Missing " +
                            terminalType.getText() + "\n");
    }

    private void addUnexpectedTerminalError(Token terminalToken) {
        parsingErrors.append("line " + terminalToken.getLine() + " : Syntax Error! Unexpected " +
                            terminalToken.getType().getText() + "\n");
    }


    private String NTDescription(String NT) {
        switch(NT){
            case "P":
                return "program";
            case "DL":
                return "declaration list";
            case "D":
                return "declaration";
            case "TS":
                return "type specifier";
            case "VDFD":
                return "variable or function declaration";
            case "VD":
                return "variable declaration";
            case "FD":
                return "function declaration";
            case "PARS":
                return "function parameters";
            case "VPAR":
                return "function's void parameter";
            case "PL":
                return "function parameters";
            case "BRCK":
                return "function's array parameter";
            case "CS":
                return "compound statement";
            case "SL":
                return "statement list";
            case "S":
                return "statement";
            case "ES":
                return "expression statement";
            case "SS":
                return "selection statement";
            case "IS":
                return "iteration statement";
            case "RS":
                return "return statement";
            case "RVAL":
                return "return value statement";
            case "SWS":
                return "switch statement";
            case "CASS":
                return "case statements";
            case "DS":
                return "default statement";
            case "E":
                return "expression";
            case "EID":
                return "identifier in expression";
            case "EID1":
                return "array identifier in expression";
            case "SE1":
                return "simple expression - first part";
            case "AE":
                return "additional expression";
            case "AE1":
                return "part of additional expression";
            case "A":
                return "additive operation";
            case "R":
                return "relative operation";
            case "T":
                return "term";
            case "T1":
                return "part of term";
            case "SF":
                return "signed factor";
            case "SF1":
                return "part of signed factor";
            case "F":
                return "factor";
            case "F1":
                return "part of factor";
            case "VC":
                return "variable or function call";
            case "VC1":
                return "part of variable or function call";
            case "VC2":
                return "part of variable or function call";
            case "ARGS":
                return "function's arguments";
            case "ARL":
                return "function's argument list";
            case "ARL1":
                return "part of function's arguments";
            default:
                return "non terminal";
        }
    }


    private void addWrongNonTerminalError(int line, String NT) {
        parsingErrors.append("line " + line + " : Syntax Error! Missing " + NTDescription(NT) + "\n");
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
            if(inFirst || inFollow){
                inputIndex.dec();
                break;
            }

            if(currToken.getType() == Token.Type.EOF) {
                this.inputIndex.dec();
                addUnexpectedEOFError();
                fail("parsing stopped!");
            }
            else{
                addUnexpectedTerminalError(currToken);
            }
        }

        if(inFollow && !haveEpsilon){
            addWrongNonTerminalError(input.get(inputIndex.value - 1).getLine(), nonTerminal);
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
        boolean r1 = input.get(inputIndex.value).isFirst(NT);
        boolean r2 = hasEPSILONInFirst(NT) && input.get(inputIndex.value).isFollow(NT);
        return r1 || r2;
    }

    private boolean isAllowedToPassEPSILON(String NT){
        return input.get(inputIndex.value).isFollow(NT);
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
        return input.get(inputIndex.value).getLine();
    }


////////////////////////////////////////////////////////////////////////////

    private boolean callParseFunction(String nonTerminal, TreeNode thisNode) throws Exception {
        switch (nonTerminal) {
            case "P": return this.parse_program();
            case "DL": return this.parse_DL(thisNode);
            case "D": return this.parse_D(thisNode);
            case "TS": return this.parse_TS(thisNode);
            case "VDFD": return this.parse_VDFD(thisNode);
            case "VD": return this.parse_VD(thisNode);
            case "FD": return this.parse_FD(thisNode);
            case "PARS": return this.parse_PARS(thisNode);
            case "VPAR": return this.parse_VPAR(thisNode);
            case "PL": return this.parse_PL(thisNode);
            case "BRCK": return this.parse_BRCK(thisNode);
            case "CS": return this.parse_CS(thisNode);
            case "SL": return this.parse_SL(thisNode);
            case "S": return this.parse_S(thisNode);
            case "ES": return this.parse_ES(thisNode);
            case "SS": return this.parse_SS(thisNode);
            case "IS": return this.parse_IS(thisNode);
            case "RS": return this.parse_RS(thisNode);
            case "RVAL": return this.parse_RVAL(thisNode);
            case "SWS": return this.parse_SWS(thisNode);
            case "CASS": return this.parse_CASS(thisNode);
            case "DS": return this.parse_DS(thisNode);
            case "E": return this.parse_E(thisNode);
            case "EID": return this.parse_EID(thisNode);
            case "EID1": return this.parse_EID1(thisNode);
            case "SE1": return this.parse_SE1(thisNode);
            case "AE": return this.parse_AE(thisNode);
            case "AE1": return this.parse_AE1(thisNode);
            case "A": return this.parse_A(thisNode);
            case "R": return this.parse_R(thisNode);
            case "T": return this.parse_T(thisNode);
            case "T1": return this.parse_T1(thisNode);
            case "SF": return this.parse_SF(thisNode);
            case "SF1": return this.parse_SF1(thisNode);
            case "F": return this.parse_F(thisNode);
            case "F1": return this.parse_F1(thisNode);
            case "VC": return this.parse_VC(thisNode);
            case "VC1": return this.parse_VC1(thisNode);
            case "VC2": return this.parse_VC2(thisNode);
            case "ARGS": return this.parse_ARGS(thisNode);
            case "ARL": return this.parse_ARL(thisNode);
            case "ARL1": return this.parse_ARL1(thisNode);
            default:
                return true;

        }
    }

    private void nnn(String nextNT, TreeNode thisNode) throws Exception {
        if(isAllowedToEnterNT(nextNT) || handleWrongNonTerminalError(nextNT))
            callParseFunction(nextNT, thisNode);
        else {
            TreeNode child = new TreeNode(nextNT);
            child.addChild(new TreeNode("ε"));
            thisNode.addChild(child);
        }
    }

    private void ttt(Token.Type terminalType, TreeNode thisNode) throws Exception {
        handleMissingTerminalError(terminalType);
        thisNode.addChild(new TreeNode(terminalType.getText()));
    }




    public boolean parse_program() throws Exception {
        nnn("DL", this.parseTreeRoot);
        ttt(Token.Type.EOF, this.parseTreeRoot);

        return true;
    }

    private boolean parse_DL(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("DL");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("D");
        boolean enter2 = isAllowedToPassEPSILON("DL");

        if(enter1) {
            nnn("D", thisNode);
            nnn("DL", thisNode);
        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_D(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("D");
        parNode.addChild(thisNode);

        nnn("TS", thisNode);
        sa.executeSemanticRoutine(RoutineType.PID);
        ttt(Token.Type.ID, thisNode);
        nnn("VDFD", thisNode);

        return true;
    }

    private boolean parse_TS(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("TS");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.INT);
        boolean enter2 = isAllowedToConsumeT(Token.Type.VOID);

        if(enter1) {
            sa.executeSemanticRoutine(RoutineType.TSINT);
            ttt(Token.Type.INT, thisNode);
        }else if(enter2) {
            sa.executeSemanticRoutine(RoutineType.TSVOID);
            ttt(Token.Type.VOID, thisNode);
        }

        return true;
    }

    private boolean parse_VDFD(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("VDFD");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("VD");
        boolean enter2 = isAllowedToEnterNT("FD");

        if(enter1) {
            nnn("VD", thisNode);
        }else if(enter2) {
            nnn("FD", thisNode);
        }
        return true;
    }

    private boolean parse_VD(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("VD");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.SEMICOLON);
        boolean enter2 = isAllowedToConsumeT(Token.Type.OPEN_BRACKETS);

        if(enter1) {
            sa.executeSemanticRoutine(RoutineType.VARDEC);
            ttt(Token.Type.SEMICOLON, thisNode);
        }else if(enter2) {
            ttt(Token.Type.OPEN_BRACKETS, thisNode);
            sa.executeSemanticRoutine(RoutineType.ARRDEC);
            ttt(Token.Type.NUM, thisNode);
            ttt(Token.Type.CLOSE_BRACKETS, thisNode);
            ttt(Token.Type.SEMICOLON, thisNode);
        }

        return true;
    }

    private boolean parse_FD(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("FD");
        parNode.addChild(thisNode);

        sa.executeSemanticRoutine(RoutineType.FUNDEC);
        ttt(Token.Type.OPEN_PARENTHESES, thisNode);
        nnn("PARS", thisNode);
        ttt(Token.Type.CLOSE_PARENTHESES, thisNode);
        sa.executeSemanticRoutine(RoutineType.FUNENDPARS);
        nnn("CS", thisNode);
        sa.executeSemanticRoutine(RoutineType.FUNJPCALLER);

        return true;
    }

    private boolean parse_PARS(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("PARS");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.INT);
        boolean enter2 = isAllowedToConsumeT(Token.Type.VOID);

        if(enter1) {
            ttt(Token.Type.INT, thisNode);
            sa.executeSemanticRoutine(RoutineType.TSINT);
            sa.executeSemanticRoutine(RoutineType.PARID);
            ttt(Token.Type.ID, thisNode);
            nnn("BRCK", thisNode);
            nnn("PL", thisNode);

        }else if(enter2) {
            ttt(Token.Type.VOID, thisNode);
            sa.executeSemanticRoutine(RoutineType.TSVOID);
            nnn("VPAR", thisNode);
        }

        return true;
    }

    private boolean parse_VPAR(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("VPAR");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.ID);
        boolean enter2 = isAllowedToPassEPSILON("VPAR");

        if(enter1) {
            ttt(Token.Type.ID, thisNode);
            nnn("BRCK", thisNode);
            sa.executeSemanticRoutine(RoutineType.VOIDPARERR);
            nnn("PL", thisNode);
        }else if(enter2) {
            sa.executeSemanticRoutine(RoutineType.SINGLEVOIDPAR);
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_PL(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("PL");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.COMMA);
        boolean enter2 = isAllowedToPassEPSILON("PL");

        if(enter1) {
            sa.executeSemanticRoutine(RoutineType.SETPAR);
            ttt(Token.Type.COMMA, thisNode);
            nnn("TS", thisNode);
            sa.executeSemanticRoutine(RoutineType.PARID);
            ttt(Token.Type.ID, thisNode);
            nnn("BRCK", thisNode);
            nnn("PL", thisNode);
        }else if(enter2) {
            sa.executeSemanticRoutine(RoutineType.SETPAR);
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_BRCK(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("BRCK");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.OPEN_BRACKETS);
        boolean enter2 = isAllowedToPassEPSILON("BRCK");

        if(enter1) {
            sa.executeSemanticRoutine(RoutineType.WITHBRCK);
            ttt(Token.Type.OPEN_BRACKETS, thisNode);
            ttt(Token.Type.CLOSE_BRACKETS, thisNode);
        }else if(enter2) {
            sa.executeSemanticRoutine(RoutineType.WITHOUTBRCK);
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_CS(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("CS");
        parNode.addChild(thisNode);

        ttt(Token.Type.OPEN_BRACES, thisNode);
        nnn("DL", thisNode);
        nnn("SL", thisNode);
        ttt(Token.Type.CLOSE_BRACES, thisNode);

        return true;
    }

    private boolean parse_SL(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("SL");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("S");
        boolean enter2 = isAllowedToPassEPSILON("SL");

        if(enter1) {
            nnn("S", thisNode);
            nnn("SL", thisNode);
        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_S(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("S");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("ES");
        boolean enter2 = isAllowedToEnterNT("CS");
        boolean enter3 = isAllowedToEnterNT("SS");
        boolean enter4 = isAllowedToEnterNT("IS");
        boolean enter5 = isAllowedToEnterNT("RS");
        boolean enter6 = isAllowedToEnterNT("SWS");

        if(enter1) {
            nnn("ES", thisNode);
        }else if(enter2) {
            nnn("CS", thisNode);
        }else if(enter3) {
            nnn("SS", thisNode);
        }else if(enter4) {
            nnn("IS", thisNode);
        }else if(enter5) {
            nnn("RS", thisNode);
        }else if(enter6) {
            nnn("SWS", thisNode);
        }

        return true;
    }

    private boolean parse_ES(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("ES");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("E");
        boolean enter2 = isAllowedToConsumeT(Token.Type.CONTINUE);
        boolean enter3 = isAllowedToConsumeT(Token.Type.BREAK);
        boolean enter4 = isAllowedToConsumeT(Token.Type.SEMICOLON);

        if(enter1) {
            nnn("E", thisNode);
            ttt(Token.Type.SEMICOLON, thisNode);
        }else if(enter2) {
            ttt(Token.Type.CONTINUE, thisNode);
            ttt(Token.Type.SEMICOLON, thisNode);
        }else if(enter3) {
            ttt(Token.Type.BREAK, thisNode);
            ttt(Token.Type.SEMICOLON, thisNode);
        }else if(enter4) {
            ttt(Token.Type.SEMICOLON, thisNode);
        }

        return true;
    }

    private boolean parse_SS(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("SS");
        parNode.addChild(thisNode);

        ttt(Token.Type.IF, thisNode);
        ttt(Token.Type.OPEN_PARENTHESES, thisNode);
        nnn("E", thisNode);
        ttt(Token.Type.CLOSE_PARENTHESES, thisNode);
        sa.executeSemanticRoutine(RoutineType.IF_SAVE);
        nnn("S", thisNode);
        ttt(Token.Type.ELSE, thisNode);
        sa.executeSemanticRoutine(RoutineType.IF_JP_SAVE);
        nnn("S", thisNode);
        sa.executeSemanticRoutine(RoutineType.END_IF);

        return true;
    }

    private boolean parse_IS(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("IS");
        parNode.addChild(thisNode);

        ttt(Token.Type.WHILE, thisNode);
        ttt(Token.Type.OPEN_PARENTHESES, thisNode);
        sa.executeSemanticRoutine(RoutineType.WH_LABEL);
        nnn("E", thisNode);
        ttt(Token.Type.CLOSE_PARENTHESES, thisNode);
        sa.executeSemanticRoutine(RoutineType.WH_SAVE);
        nnn("S", thisNode);
        sa.executeSemanticRoutine(RoutineType.ENDWH);
        return true;
    }

    private boolean parse_RS(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("RS");
        parNode.addChild(thisNode);

        ttt(Token.Type.RETURN, thisNode);
        nnn("RVAL", thisNode);

        return true;
    }

    private boolean parse_RVAL(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("RVAL");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.SEMICOLON);
        boolean enter2 = isAllowedToEnterNT("E");

        if(enter1) {
            sa.executeSemanticRoutine(RoutineType.VOIDRETURN);
            ttt(Token.Type.SEMICOLON, thisNode);
        }else if(enter2) {
            nnn("E", thisNode);
            sa.executeSemanticRoutine(RoutineType.RETURN);
            ttt(Token.Type.SEMICOLON, thisNode);
        }

        return true;
    }

    private boolean parse_SWS(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("SWS");
        parNode.addChild(thisNode);

        ttt(Token.Type.SWITCH, thisNode);
        ttt(Token.Type.OPEN_PARENTHESES, thisNode);
        nnn("E", thisNode);
        ttt(Token.Type.CLOSE_PARENTHESES, thisNode);
        ttt(Token.Type.OPEN_BRACES, thisNode);
        nnn("CASS", thisNode);
        nnn("DS", thisNode);
        ttt(Token.Type.CLOSE_BRACES, thisNode);

        return true;
    }

    private boolean parse_CASS(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("CASS");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.CASE);
        boolean enter2 = isAllowedToPassEPSILON("CASS");

        if(enter1) {
            ttt(Token.Type.CASE, thisNode);
            ttt(Token.Type.NUM, thisNode);
            ttt(Token.Type.COLON, thisNode);
            nnn("SL", thisNode);
            nnn("CASS", thisNode);
        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_DS(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("DS");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.DEFAULT);
        boolean enter2 = isAllowedToPassEPSILON("DS");

        if(enter1) {
            ttt(Token.Type.DEFAULT, thisNode);
            ttt(Token.Type.COLON, thisNode);
            nnn("SL", thisNode);
        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_E(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("E");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.ID);
        boolean enter2 = isAllowedToEnterNT("SF1");

        if(enter1) {
            sa.executeSemanticRoutine(RoutineType.SYMBOL);
            ttt(Token.Type.ID, thisNode);
            nnn("EID", thisNode);
        }else if(enter2) {
            nnn("SF1", thisNode);
            nnn("T1", thisNode);
            nnn("AE1", thisNode);
            nnn("SE1", thisNode);
        }

        return true;
    }

    private boolean parse_EID(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("EID");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.ASSIGN);
        boolean enter2 = isAllowedToConsumeT(Token.Type.OPEN_BRACKETS);
        boolean enter3 = isAllowedToEnterNT("VC2");

        if(enter1) {
            ttt(Token.Type.ASSIGN, thisNode);
            nnn("E", thisNode);
            sa.executeSemanticRoutine(RoutineType.ASSIGN);
        }else if(enter2) {
            ttt(Token.Type.OPEN_BRACKETS, thisNode);
            nnn("E", thisNode);
            ttt(Token.Type.CLOSE_BRACKETS, thisNode);
            sa.executeSemanticRoutine(RoutineType.GETARR);
            nnn("EID1", thisNode);
        }else if(enter3) {
            nnn("VC2", thisNode);
            nnn("T1", thisNode);
            nnn("AE1", thisNode);
            nnn("SE1", thisNode);
        }

        return true;
    }

    private boolean parse_EID1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("EID1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.ASSIGN);
        boolean enter2 = isAllowedToEnterNT("T1");

        if(enter1) {
            ttt(Token.Type.ASSIGN, thisNode);
            nnn("E", thisNode);
            sa.executeSemanticRoutine(RoutineType.ASSIGN);
        }else if(enter2) {
            nnn("T1", thisNode);
            nnn("AE1", thisNode);
            nnn("SE1", thisNode);
        }

        return true;
    }

    private boolean parse_SE1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("SE1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("R");
        boolean enter2 = isAllowedToPassEPSILON("SE1");

        if(enter1) {
            nnn("R", thisNode);
            nnn("AE", thisNode);
            sa.executeSemanticRoutine(RoutineType.LTOREQ);
        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_AE(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("AE");
        parNode.addChild(thisNode);

        nnn("T", thisNode);
        nnn("AE1", thisNode);

        return true;
    }

    private boolean parse_AE1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("AE1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("A");
        boolean enter2 = isAllowedToPassEPSILON("AE1");

        if(enter1) {
            nnn("A", thisNode);
            nnn("T", thisNode);
            sa.executeSemanticRoutine(RoutineType.PLORMI);
        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_A(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("A");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.PLUS);
        boolean enter2 = isAllowedToConsumeT(Token.Type.MINUS);

        if(enter1) {
            ttt(Token.Type.PLUS, thisNode);
            sa.executeSemanticRoutine(RoutineType.PLUS);
        }else if(enter2) {
            ttt(Token.Type.MINUS, thisNode);
            sa.executeSemanticRoutine(RoutineType.MINUS);
        }

        return true;
    }

    private boolean parse_R(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("R");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.LESS_THAN);
        boolean enter2 = isAllowedToConsumeT(Token.Type.EQ);

        if(enter1) {
            ttt(Token.Type.LESS_THAN, thisNode);
            sa.executeSemanticRoutine(RoutineType.LT);
        }else if(enter2) {
            ttt(Token.Type.EQ, thisNode);
            sa.executeSemanticRoutine(RoutineType.EQ);
        }

        return true;
    }

    private boolean parse_T(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("T");
        parNode.addChild(thisNode);

        nnn("SF", thisNode);
        nnn("T1", thisNode);

        return true;
    }

    private boolean parse_T1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("T1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.TIMES);
        boolean enter2 = isAllowedToPassEPSILON("T1");

        if(enter1) {
            ttt(Token.Type.TIMES, thisNode);
            nnn("SF", thisNode);
            sa.executeSemanticRoutine(RoutineType.FMULT);
        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_SF(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("SF");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("VC");
        boolean enter2 = isAllowedToEnterNT("SF1");

        if(enter1) {
            nnn("VC", thisNode);
        }else if(enter2) {
            nnn("SF1", thisNode);
        }

        return true;
    }

    private boolean parse_SF1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("SF1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.PLUS);
        boolean enter2 = isAllowedToConsumeT(Token.Type.MINUS);
        boolean enter3 = isAllowedToEnterNT("F1");

        if(enter1) {
            ttt(Token.Type.PLUS, thisNode);
            nnn("F", thisNode);
        }else if(enter2) {
            ttt(Token.Type.MINUS, thisNode);
            nnn("F", thisNode);
            sa.executeSemanticRoutine(RoutineType.FMINUS);
        }else if(enter3) {
            nnn("F1", thisNode);
        }

        return true;
    }

    private boolean parse_F(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("F");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("VC");
        boolean enter2 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES);
        boolean enter3 = isAllowedToConsumeT(Token.Type.NUM);

        if(enter1) {
            nnn("VC", thisNode);
        } else if(enter2) {
            ttt(Token.Type.OPEN_PARENTHESES, thisNode);
            nnn("E", thisNode);
            ttt(Token.Type.CLOSE_PARENTHESES, thisNode);
        } else if(enter3) {
            sa.executeSemanticRoutine(RoutineType.NUM);
            ttt(Token.Type.NUM, thisNode);
        }

        return true;
    }

    private boolean parse_F1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("F1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES);
        boolean enter2 = isAllowedToConsumeT(Token.Type.NUM);

        if(enter1) {
            ttt(Token.Type.OPEN_PARENTHESES, thisNode);
            nnn("E", thisNode);
            ttt(Token.Type.CLOSE_PARENTHESES, thisNode);
        }else if(enter2) {
            sa.executeSemanticRoutine(RoutineType.NUM);
            ttt(Token.Type.NUM, thisNode);
        }

        return true;
    }

    private boolean parse_VC(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("VC");
        parNode.addChild(thisNode);

        sa.executeSemanticRoutine(RoutineType.SYMBOL);
        ttt(Token.Type.ID, thisNode);
        nnn("VC1", thisNode);

        return true;
    }

    private boolean parse_VC1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("VC1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.OPEN_BRACKETS);
        boolean enter2 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES);
        boolean enter3 = isAllowedToPassEPSILON("VC1");

        if(enter1) {
            ttt(Token.Type.OPEN_BRACKETS, thisNode);
            nnn("E", thisNode);
            ttt(Token.Type.CLOSE_BRACKETS, thisNode);
            sa.executeSemanticRoutine(RoutineType.GETARR);
        } else if(enter2) {
            sa.executeSemanticRoutine(RoutineType.FUNCALL);
            ttt(Token.Type.OPEN_PARENTHESES, thisNode);
            nnn("ARGS", thisNode);
            ttt(Token.Type.CLOSE_PARENTHESES, thisNode);
            sa.executeSemanticRoutine(RoutineType.FUNCALLJP);
        } else if (enter3) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_VC2(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("VC2");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.OPEN_PARENTHESES);
        boolean enter2 = isAllowedToPassEPSILON("VC2");

        if(enter1) {
            sa.executeSemanticRoutine(RoutineType.FUNCALL);
            ttt(Token.Type.OPEN_PARENTHESES, thisNode);
            nnn("ARGS", thisNode);
            ttt(Token.Type.CLOSE_PARENTHESES, thisNode);
            sa.executeSemanticRoutine(RoutineType.FUNCALLJP);
        } else if (enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_ARGS(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("ARGS");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToEnterNT("ARL");
        boolean enter2 = isAllowedToPassEPSILON("ARGS");

        if(enter1) {
            sa.executeSemanticRoutine(RoutineType.ZEROARGNUM);
            nnn("ARL", thisNode);
        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }

    private boolean parse_ARL(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("ARL");
        parNode.addChild(thisNode);

        nnn("E", thisNode);
        sa.executeSemanticRoutine(RoutineType.COUNTARG);
        nnn("ARL1", thisNode);

        return true;
    }

    private boolean parse_ARL1(TreeNode parNode) throws Exception {
        TreeNode thisNode = new TreeNode("ARL1");
        parNode.addChild(thisNode);

        boolean enter1 = isAllowedToConsumeT(Token.Type.COMMA);
        boolean enter2 = isAllowedToPassEPSILON("ARL1");

        if(enter1) {
            ttt(Token.Type.COMMA, thisNode);
            nnn("E", thisNode);
            sa.executeSemanticRoutine(RoutineType.COUNTARG);
            nnn("ARL1", thisNode);
        }else if(enter2) {
            thisNode.addChild(new TreeNode("ε"));
            return true;
        }

        return true;
    }





///////////////////////////////////////////////////////////////////////////////






    private Token peek() {
        if (inputIndex.value < input.size()) {
            return input.get(inputIndex.value);
        } else {
            return eof;
        }
    }

    private boolean consume(Token.Type expected) {
        Token actual = peek();
        if (actual.getType() == expected) {
            input.add(tokenizer.get_next_token());
            inputIndex.inc();
            return true;
        } else {
            return false;
        }
    }

    private Token consume() {
        Token tok = peek();
        input.add(tokenizer.get_next_token());
        inputIndex.inc();
        return tok;
    }

    private void fail(String error) throws Exception {
        throw new Exception(error);
    }


    public SemanticAnalyser getSemanticAnalyser() {
        return sa;
    }
}


































