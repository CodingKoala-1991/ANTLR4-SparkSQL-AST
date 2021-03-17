package org.codingkoala.antlr4_spark_sql_ast;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;


public class ParserDriver {
    public static void main(String[] args) {
        // 关于 language level 相关的问题
        // File -> Project Structure -> Modules -> Language Level
        // 如果设置成5， 在 gen 下一大堆 @Override 的 代码会飘红，虽然能运行，设置成 6 就不会飘红
        // 同时还要在 Preferences -> Build, Execution, Deployment -> Compiler -> Java Compiler 里
        // 把 target bytecode version 改成 6，不然会说 Java Compiler 有问题之类的，运行不起来

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

        SqlBaseParser parser = new SqlBaseParser(new CommonTokenStream(lexer));
        MyVisitor visitor = new MyVisitor();

        // 在 g4 文件中，假定存在以下语法或者词法规则
        // 小写 aaa 是一个语法规则，包含两个语法分支 #aaa1 和 #aaa2
        // 小写 bbb 是一个语法规则，不包含语法分支
        // 大写 AAA 是一个词法规则



        // ******************************************** 关于 Context 和 Parser文件 ********************************************
        // 在 由ANTLR4生成的语法解释器Parser 中
        // aaa规则 生成 AaaContext 这个 class
        // aaa语法规则的分支 #aaa1 和 #aaa2 也会生成 context，对应 Aaa1Context 和 Aaa2Context，继承自 AaaContext

        // bbb语法规则 只有一个 BbbContext，没有分支

        // AAA词法规则 不会生成对应的 context

        // 在 语法解释器parser 中的每一个 Context 里
        // 如果 语法规则 不包含分支#，那么会在 context 里重构 enterRule exitRule accept 和 getRuleIndex 4个方法
        // 如果 语法规则 包含分支#，那么在 context 里重构 getRuleIndex 方法
        // 在 分支里 重构 enterRule exitRule accept 方法
        // enterRule exitRule 是用于 listener 模式，所以在这里不用管
        // accept 用于 visitor 模式

        // copyFrom(ctx)，子规则的context 的构造函数才有



        // ******************************************** 关于 Context自身的 字段 和 方法 ********************************************
        // g4 文件中的 = 操作符，在 Context 中生成的字段的类型 有 token 和 Context 两种，取决于 = 后面的是词法规则还是语法规则，参考 #use 和 #createDatabase

        // g4 文件中的 += 操作符，在 Context 中生成 字段 和 方法，可以参考 queryOrganization 这个语法规则对应的 QueryOrganizationContext 类内部

        // = 和 += 操作符，都会在 Context 中对应生成字段，就是用来记住一些信息的
        // = 操作符 为规则中的元素添加标签,这样会在规则的上下文对象中添加元素的字段.
        // += 操作符 可以很方便的记录大量的token或者规则的引用

        // 一个规则里，如果同一个 子语法规则 或者 词法规则有多个，那么会生成一个 list，例如 #setOperation
        // + 和 * 在 Context 中也会返回一个list，例如 #addTablePartition， #dropTablePartitions
        // 但是两个同名规则，就没有生成list，因为规则在某一个时刻只有一个，例如#setTableSerDe
        // querySpecification 规则中某一个分支，尽管有多个 kind 规则，但是并列，生成的 context 里也只是一个 kind，没有list
        // #setConfiguration 这种 * 不是作用在规则上，就没有list

        // Context 中 通过 getRuleContexts() 来获得一个 List<Context>
        // Context 中 通过 getRuleContext() 来获得一个 语法规则的 context 节点
        // Context 中 通过 getToken() 来获得一个 词法规则的 token



        // ******************************************** 关于 aaa()方法 和 Parser文件 ********************************************
        // 首先，在 Parser 中，会生成 public final aaa() 和 public final bbb() 两个方法
        // 上面的样例方法隶属于 Parser，用于 构建 AST。
        // 分支不会产生这样的方法

        // 然后在 一些 Context 内部，也可能有 public aaa() 和 public bbb() 方法
        // 但是隶属于 Context 内部，用于访问器visitor进行调用的时候，返回对应的节点
        // 分支也不会产生这样的方法

        // 例如 statement 这个语法规则
        // 首先，Parser 中会生成隶属于 Parser 的 public final StatementContext statement() 这样的一个方法。
        // 这个方法用于，把输入的 SparkSQL Query 递归转换成一棵 AST，AST 的每一个节点就是一个 Context。

        // 然而，在 一些 Context 内部存在 public StatementContext statement() 方法
        // 例如 singleStatement 这个语法规则，statement语法规则 是他的一部分，因此 SingleStatementContext 内部存在 public StatementContext statement() 方法
        // statement语法规则 的分支 #explain，也包含 statement语法规则，因此 ExplainContext 内部也有 public StatementContext statement() 方法
        // 以上的方法是用于 访问器visitor 返回对应的Context内容的



        // ******************************************** 关于 visit方法 和 Visitor文件 ********************************************
        // 在 由ANTLR4生成的访问器Visitor 中
        // aaa规则 生成 visitAaa(AaaContext ctx) 方法
        // aaa规则 分支 #aaa1 和 #aaa2 生成 visitAaa1(Aaa1Context ctx) 和 visitAaa2(Aaa2Context ctx)

        // bbb规则 生成 visitBbb(BbbContext ctx) 方法

        // AAA词法规则 不会在 visitor 中生成任何方法



        // ******************************************** 构建 AST 的大致过程 ********************************************
        // 在 SparkSQL 的语法中， singleStatement 是入口规则
        // 因此，直接调用 parser.singleStatement() 就可以构建 对应query 的AST
        // AST 的每一个节点都是一个 Context 或者 Token
        // Context 自身有两个比较重要的变量 children 和 parent
        // Context 肯定对应的是非叶子节点，是一个 语法规则
        // Token 对应的是叶子节点，是一个 词法规则
        // 其中 children 是一个 List<Context> 类型，parent 是单个 Context 类型，通过这两个变量，就可以从 AST 的 root Context 遍历整棵 AST
        // 构建的具体代码没有细看，根据推测，比如从 parser.singleStatement() 开始，首先构造 一个 SingleStatementContext，然后递归的构建 孩子 Context，然后赋值给他的 children变量
        // 子孩子Context 也是这样依次向下递归构建，最终把整个 AST 构建起来。



        // ******************************************** 访问 AST 的大致过程 ********************************************
        // 首先获得 AST 的 root Context
        // 例如 parser.singleStatement() 返回的就是 SparkSQL Query 的 AST 的 root Context，这是一个 SingleStatementContext 类型的 object，假设叫做 rootContext
        // visitor.visitSingleStatement(rootContext)，在 visitor 中传入 rootContext 这个根。
        // visitSingleStatement 方法的含义就是 访问 SingleStatementContext类型的 节点。
        // 然后 visitSingleStatement 方法内部，访问不同的 子孩子Context 的时候，可以自定义顺序，这就是 visitXxx 方法的自由。
        // visitor 用的是访问者模式



        SqlBaseParser.SingleStatementContext singleStatementContext = parser.singleStatement();

        System.out.println("****************************");
        visitor.visitSingleStatement(singleStatementContext);
    }
}
