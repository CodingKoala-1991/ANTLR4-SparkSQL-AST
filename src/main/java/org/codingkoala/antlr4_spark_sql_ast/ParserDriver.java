package org.codingkoala.antlr4_spark_sql_ast;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.Arrays;

public class ParserDriver {
    public static void main(String[] args) {
        // 关于 language level 相关的问题
        // File -> Project Structure -> Modules -> Language Level
        // 如果设置成5， 在 gen 下一大堆 @Override 的 代码会飘红，虽然能运行，设置成 6 就不会飘红
        // 同时还要在 Preferences -> Build, Execution, Deployment -> Compiler -> Java Compiler 里
        // 把 target bytecode version 改成 6，不然会说 Java Compiler 有问题之类的，运行不起来


        // String query = "(select a, b, c from tables";
        // String query = "select * from xxx where yy = 'z'";
        String defaultQuery = "select a, b, c from x where a > 100";
        String query;
        if (args.length > 0) {
            query = args[0];
        } else {
            query = defaultQuery;
        }
        System.out.println(String.format("The query is %s", query));
        System.out.println("********************************************");
        SqlBaseLexer lexer = new SqlBaseLexer(new ANTLRInputStream(query.toUpperCase()));

        // String text = lexer.getToken();
        // System.out.println(text);
        SqlBaseParser parser = new SqlBaseParser(new CommonTokenStream(lexer));
        MyVisitor visitor = new MyVisitor();

        // 在 g4 文件中
        // 小写 xyz 是一个规则
        // 如果 xyz 规则 不带有 分支#，就会在 visitor 中生成 visitXyz() 方法
        // 如果 xyz 规则 带有 分支#，每一个分支 都会在 visitor 中生成 visitXxx() 方法

        // 每个 xyz 规则 会在 语法解释器parser 中生成一个 xyz() 方法
        // xyz 规则 和 他的分支（不管有没有），都会在 语法解释器parser 中生成 XxxContext
        // 在这里，语法解释器parser 中的 SingleStatementContext 就是传入的 query 生成的 AST 的 root 节点
        // parser.singleStatement() 返回的就是 SingleStatementContext 的对象
        visitor.visitSingleStatement(parser.singleStatement());
    }
}
