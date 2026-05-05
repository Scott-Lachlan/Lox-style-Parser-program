package river_lox;

import java.util.List;

public abstract class Stmt {
    public interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);
        R visitPrintStmt(Print stmt);
        R visitVarStmt(Var stmt);
        R visitRivStmt(Riv stmt);
        R visitJoinStmt(Join stmt);
        R visitOutStmt(Out stmt);
        R visitListStmt(ListDecl stmt);
        R visitRainStmt(Rain stmt);
    }

    public static class Expression extends Stmt {
        public final Expr expression;
        public Expression(Expr expression) { this.expression = expression; }
        @Override public <R> R accept(Visitor<R> visitor) { return visitor.visitExpressionStmt(this); }
    }

    public static class Print extends Stmt {
        public final Expr expression;
        public Print(Expr expression) { this.expression = expression; }
        @Override public <R> R accept(Visitor<R> visitor) { return visitor.visitPrintStmt(this); }
    }

    public static class Var extends Stmt {
        public final Token name;
        public final Expr initializer;
        public Var(Token name, Expr initializer) { this.name = name; this.initializer = initializer; }
        @Override public <R> R accept(Visitor<R> visitor) { return visitor.visitVarStmt(this); }
    }

    public static class Riv extends Stmt {
        public final Token name;
        public final Expr initializer;
        public Riv(Token name, Expr initializer) { this.name = name; this.initializer = initializer; }
        @Override public <R> R accept(Visitor<R> visitor) { return visitor.visitRivStmt(this); }
    }

    public static class Join extends Stmt {
        public final Token name;
        public final List<Token> feeders;
        public Join(Token name, List<Token> feeders) { this.name = name; this.feeders = feeders; }
        @Override public <R> R accept(Visitor<R> visitor) { return visitor.visitJoinStmt(this); }
    }

    public static class Out extends Stmt {
        public final Token name;
        public Out(Token name) { this.name = name; }
        @Override public <R> R accept(Visitor<R> visitor) { return visitor.visitOutStmt(this); }
    }

    public static class ListDecl extends Stmt {
        public final Token name;
        public final Expr value;
        public ListDecl(Token name, Expr value) { this.name = name; this.value = value; }
        @Override public <R> R accept(Visitor<R> visitor) { return visitor.visitListStmt(this); }
    }

    public static class Rain extends Stmt {
        public final Expr listExpr;
        public Rain(Expr listExpr) { this.listExpr = listExpr; }
        @Override public <R> R accept(Visitor<R> visitor) { return visitor.visitRainStmt(this); }
    }

    public abstract <R> R accept(Visitor<R> visitor);
}