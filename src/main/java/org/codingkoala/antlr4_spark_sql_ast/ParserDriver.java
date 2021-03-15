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
        String defaultQuery = "select name from student where age > 18 order by id, name";
        String query;
        if (args.length > 0) {
            query = args[0];
        } else {
            query = defaultQuery;
        }
        System.out.println("The query is:");
        System.out.println(query);
        System.out.println("********************************************");
        SqlBaseLexer lexer = new SqlBaseLexer(new ANTLRInputStream(query.toUpperCase()));

        // String text = lexer.getToken();
        // System.out.println(text);
        SqlBaseParser parser = new SqlBaseParser(new CommonTokenStream(lexer));
        MyVisitor visitor = new MyVisitor();

        // 在 g4 文件中
        // 小写 xyz 是一个语法规则

        // *********** 关于 visitor ***********
        // 如果 xyz 语法规则 不带有 分支#，就会在 visitor 中生成 visitXyz() 方法
        // 如果 xyz 语法规则 带有 分支#，每一个分支 都会在 visitor 中生成 visitXxx() 方法

        // *********** 关于 context ***********
        // xyz 语法规则 和 他的分支（不管有没有），都会在 语法解释器parser 中生成 XxxContext
        // 分支的 context 会 extends 语法规则的 context
        // 例如，语法解释器parser 中的 SingleStatementContext 就是传入的 query 生成的 AST 的 root 节点
        // parser.singleStatement() 返回的就是 SingleStatementContext 的对象
        // context 内部通过

        // *********** 关于 context 对应的访问方法 ***********
        // 每个 xyz 语法规则 会在 语法解释器parser 中生成一个 xyz() 方法
        // 在构建 AST的过程中，context 内部调用这些不同的 xyz() 方法，可以生成不同子 context

        // 在 语法解释器parser 中的每一个 Context 里
        // 如果 xyz语法规则 不包含分支，那么会在 context 里重构 enterRule exitRule accept 和 getRuleIndex 4个方法
        // 如果 xyz语法规则 包含分支，那么在 context 里重构 getRuleIndex 方法
        // 在 分支里 重构 enterRule exitRule accept 方法
        // enterRule exitRule 是用于 listener 模式，所以在这里不用管
        // accept 用于 visitor 模式

        // 有两个 xyZ() 方法，分别位于 parser 里 和 context 里？？？？？？？
        // 构建 AST 的调用样例？？？？？？
        // visitor 访问的调用样例 ？？？？？
        // += 的列表 和 普通的 正则列表不一样，前者需要记住一些信息？？？？？？？？
        // 一个规则里，如果同一个 子语法规则 或者 词法规则有多个，那么会生成一个 list

        // parser.singleStatement() 构建了一棵 Context 树（AST），每一个节点都是一个 context，并最终返回 root 的 context
        // 这个 root context 最终交给 visitor，去访问这个 AST
        SqlBaseParser.SingleStatementContext singleStatementContext = parser.singleStatement();

        System.out.println("****************************");
        visitor.visitSingleStatement(singleStatementContext);
    }
}
