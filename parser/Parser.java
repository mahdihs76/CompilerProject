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
    public static Statement parseStatement(ArrayList<Token> input) {
        Parser parser = new Parser(input);
        Statement stmt = parser.parseStatement();
        parser.consume(Token.Type.EOF);
        return stmt;
    }
    
     public static Expression parseExpr(ArrayList<Token> input) {
        Parser parser = new Parser(input);
        Expression stmt = parser.parseExpr();
        parser.consume(Token.Type.EOF);
        return stmt;
    }
    
    private ArrayList<Token> input;
    private int inputIndex;
    private Token eof;

    private Parser(ArrayList<Token> input) {
        this.input = input;
        this.inputIndex = 0;
        if (input.isEmpty()) {
            this.eof = new Token(Token.Type.EOF, "<EOF>", 0, 0);
        } else {
            Token last = input.get(input.size() - 1);
            this.eof = new Token(Token.Type.EOF, "<EOF>", last.getLine(), last.getCol());
        }
    }
    
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
        String varName = consume(Token.Type.ID).getText();
        consume(Token.Type.COLON);
        Type type = parseType();
        consume(Token.Type.ASSIGN);
        Expression expression = parseExpr();
        Statement decl = new Declaration(varName, type, expression);
        consume(Token.Type.SEMICOLON);
        return decl;
    }

    private Statement parseAssignment() {
        String varName = consume(Token.Type.ID).getText();
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
    
    private Token consume(Token.Type expected) {
        Token actual = peek();
        if (actual.getType() == expected) {
            inputIndex++;
            return actual;
        } else {
            return fail(expected + " expected");
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
