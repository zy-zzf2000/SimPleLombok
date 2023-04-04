package org.example.processor;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import org.example.anno.GenGetter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * @author zy
 * @version 1.0
 * @description TODO
 * @date 2023/4/4 13:30
 */
@SupportedAnnotationTypes("org.example.anno.GenGetter")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class GenGetterProcessor extends AbstractProcessor {
    private Messager messager;  // 用来打印日志
    private JavacTrees trees;   //抽象语法树
    private TreeMaker treeMaker; // 用来创建语法树节点
    private Names names;  // 用来创建标识符

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //首先拿到所有被@GenGetter注解的类
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(GenGetter.class);
        //遍历所有的类
        elements.forEach(element -> {
            //拿到对应类的抽象语法树
            JCTree jcTree = trees.getTree(element);
            //采用visitor模式遍历语法树
            jcTree.accept(new TreeTranslator(){
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    //遍历类中的所有成员变量,并为每个成员变量生成getter方法
                    jcClassDecl.defs.forEach(tree->{
                        if(tree instanceof JCTree.JCVariableDecl){
                            JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) tree;
                            messager.printMessage(Diagnostic.Kind.NOTE,"正在为"+jcVariableDecl.name+"生成getter方法");
                            //生成getter方法
                            //定义方法体
                            ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
                            statements.append(treeMaker.Return(treeMaker.Select(treeMaker.Ident(names.fromString("this")),jcVariableDecl.name)));
                            JCTree.JCBlock body = treeMaker.Block(0,statements.toList());
                            //定义方法
                            JCTree.JCMethodDecl methodDecl = treeMaker.MethodDef(
                                    treeMaker.Modifiers(Flags.PUBLIC),
                                    names.fromString("get"+jcVariableDecl.name.toString().substring(0,1).toUpperCase()+jcVariableDecl.name.toString().substring(1)),
                                    jcVariableDecl.vartype,
                                    List.nil(),
                                    List.nil(),
                                    List.nil(),
                                    body,
                                    null
                            );
                            //将生成的getter方法添加到类中
                            jcClassDecl.defs = jcClassDecl.defs.append(methodDecl);
                        }
                    });

                }
            });
        });

        return true;
    }
}
