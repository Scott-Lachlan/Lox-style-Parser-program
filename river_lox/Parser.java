package river_lox;

import java.util.ArrayList;
import java.util.List;

import static river_lox.TokenType.*;

public class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) { this.tokens = tokens; }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            Stmt decl = declaration();
            if (decl != null) statements.add(decl);
        }
        return statements;
    }

    private Stmt declaration() {
        try {
            if (match(VAR)) return varDeclaration();
            if (match(RIV)) return rivDeclaration();
            if (match(JOIN)) return joinDeclaration();
            if (match(OUT)) return outDeclaration();
            if (match(LIST)) return listDeclaration();
            return statement();
        } catch (ParseError err) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name.");
        Expr initializer = null;
        if (match(EQUAL)) initializer = expression();
        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt rivDeclaration() {
        Token name = consume(IDENTIFIER, "Expect river name.");
        Expr initializer = null;
        if (match(EQUAL)) initializer = expression();
        consume(SEMICOLON, "Expect ';' after riv declaration.");
        return new Stmt.Riv(name, initializer);
    }

    private Stmt joinDeclaration() {
        Token name = consume(IDENTIFIER, "Expect name for joined river.");
        consume(LEFT_PAREN, "Expect '(' after join name.");
        List<Token> feeders = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                feeders.add(consume(IDENTIFIER, "Expect feeder river name."));
            } while (match(COMMA));
        }
        consume(RIGHT_PAREN, "Expect ')' after feeders.");
        consume(SEMICOLON, "Expect ';' after join declaration.");
        return new Stmt.Join(name, feeders);
    }

    private Stmt outDeclaration() {
        Token name = consume(IDENTIFIER, "Expect river name for out.");
        consume(SEMICOLON, "Expect ';' after out declaration.");
        return new Stmt.Out(name);
    }

    private Stmt listDeclaration() {
        Token name = consume(IDENTIFIER, "Expect list name.");
        consume(EQUAL, "Expect '=' after list name.");
        Expr value = listLiteral();
        consume(SEMICOLON, "Expect ';' after list declaration.");
        return new Stmt.ListDecl(name, value);
    }

    private Expr listLiteral() {
    List<Expr> elements = new ArrayList<>();
    if (!check(RIGHT_BRACKET)) {
        do {
            elements.add(expression());
        } while (match(COMMA));
    }
    consume(RIGHT_BRACKET, "Expect ']' after list literal.");
    return new Expr.Literal(elements);
    }

    private Stmt statement() {
        if (match(PRINT)) return printStatement();
        if (match(RAIN_KW)) return rainStatement();
        return expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt rainStatement() {
        consume(EQUAL, "Expect '=' after RAIN.");
        Expr listExpr = listLiteral();
        consume(SEMICOLON, "Expect ';' after RAIN assignment.");
        return new Stmt.Rain(listExpr);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }


    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = equality();

        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Binary(new Expr.Variable(name), equals, value);
            } else if (expr instanceof Expr.Index) {
                return new Expr.Binary(expr, equals, value);
            }

            error(previous(), "Invalid assignment target.");
        }

        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr comparison() {
        Expr expr = term();
        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr term() {
        Expr expr = factor();
        while (match(PLUS, MINUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr factor() {
        Expr expr = unary();
        while (match(STAR, SLASH, ARROW)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return call();
    }

    private Expr call() {
        Expr expr = primary();
        while (true) {
            if (match(LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(LEFT_BRACKET)) {
                Expr indexExpr = expression();
                consume(RIGHT_BRACKET, "Expect ']' after index.");
                expr = new Expr.Index(expr, indexExpr);
            } else {
                break;
            }
        }
        return expr;
    }

    private Expr finishCall(Expr callee) {
        List<Expr> args = new ArrayList<>();
        if (!check(RIGHT_PAREN)) {
            do {
                args.add(expression());
            } while (match(COMMA));
        }
        Token paren = consume(RIGHT_PAREN, "Expect ')' after arguments.");
        return new Expr.Call(callee, paren, args);
    }

    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);

        if (match(NUMBER)) return new Expr.Literal(previous().literal);
        if (match(STRING)) return new Expr.Literal(previous().literal);

        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        if (match(LEFT_BRACKET)) {
            List<Expr> elements = new ArrayList<>();
            if (!check(RIGHT_BRACKET)) {
                do {
                    elements.add(expression());
                } while (match(COMMA));
            }
            consume(RIGHT_BRACKET, "Expect ']' after list literal.");
            return new Expr.Literal(elements);
        }

        throw error(peek(), "Expect expression.");
    }


    private boolean match(TokenType... types) {
        for (TokenType t : types) {
            if (check(t)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        if (token.type == EOF) Lox.error(token.line, " at end: " + message);
        else Lox.error(token.line, " at '" + token.lexeme + "': " + message);
        return new ParseError();
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;
            switch (peek().type) {
                case VAR:
                case RIV:
                case JOIN:
                case OUT:
                case LIST:
                case PRINT:
                case RAIN_KW:
                    return;
            }
            advance();
        }
    }
    
}
