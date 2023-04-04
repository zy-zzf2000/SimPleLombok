package org.example.processor;

import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
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
        return false;
    }
}
