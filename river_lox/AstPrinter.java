package river_lox;

public class AstPrinter implements Expr.Visitor<String> {

    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name.lexeme;
    }

    @Override
    public String visitIndexExpr(Expr.Index expr) {
        return parenthesize("index", expr.object, expr.index);
    }

    @Override
    public String visitCallExpr(Expr.Call expr) {
        StringBuilder sb = new StringBuilder();
        sb.append("(call ").append(expr.callee.accept(this));
        for (Expr arg : expr.arguments) {
            sb.append(" ").append(arg.accept(this));
        }
        sb.append(")");
        return sb.toString();
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(name);
        for (Expr e : exprs) {
            sb.append(" ");
            sb.append(e.accept(this));
        }
        sb.append(")");
        return sb.toString();
    }

    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
            new Expr.Unary(new Token(TokenType.MINUS, "-", null, 1), new Expr.Literal(123.0)),
            new Token(TokenType.STAR, "*", null, 1),
            new Expr.Grouping(new Expr.Literal(45.67))
        );

        AstPrinter printer = new AstPrinter();
        System.out.println(printer.print(expression));
    }
}