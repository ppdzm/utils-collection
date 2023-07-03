package io.github.ppdzm.utils.universal.feature

import io.github.ppdzm.utils.universal.base.Logging

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import scala.collection.mutable
import scala.reflect.internal.util.{AbstractFileClassLoader, BatchSourceFile}
import scala.reflect.io.{AbstractFile, VirtualDirectory}
import scala.tools.nsc.{Global, Settings}

/**
 * Created by Stuart Alex on 2017/3/20.
 */
object Compiler extends Compiler(null) {

    /**
     * 给表达式添加类定义
     *
     * @param className  类名
     * @param expression 表达式
     * @return
     */
    def wrapExpressionInClass(className: String, expression: String): String = {
        s"""class $className extends (() => Any) {
           |      def apply() = {
           |        $expression
           |      }
           |    }""".stripMargin
    }

    /**
     * 给方法添加类定义
     *
     * @param className        类名
     * @param methodDefinition 方法定义
     * @return
     */
    def wrapMethodInClass(className: String, methodDefinition: String): String = {
        s"""object $className {
           |      $methodDefinition
           |    }""".stripMargin
    }

}

case class Compiler(targetDirectory: String) {
    private val logging = new Logging(getClass)
    private val classCache = mutable.Map[String, Class[_]]()
    private val target =
        if (targetDirectory != null) {
            AbstractFile.getDirectory(new File(targetDirectory))
        } else {
            new VirtualDirectory("(memory)", None)
        }
    private val classLoader = new AbstractFileClassLoader(this.target, this.getClass.getClassLoader)
    private val settings = new Settings()
    this.settings.deprecation.value = true
    this.settings.unchecked.value = true
    this.settings.outputDirs.setSingleOutput(this.target)
    this.settings.usejavacp.value = true
    private val global = new Global(this.settings)

    /**
     * 动态编译方法
     *
     * @param methodDefinition 方法定义
     * @return
     */
    def compileMethod(methodDefinition: String): Class[_] = {
        val className = getClassName(methodDefinition)
        val code = Compiler.wrapMethodInClass(className, methodDefinition)
        compileClass(code, className)
    }

    /**
     * 动态编译类
     *
     * @param classDefinition 类定义
     * @return
     */
    def compileClass(classDefinition: String, givenClassName: String = null): Class[_] = {
        val className = if (givenClassName == null)
            getClassName(classDefinition)
        else
            givenClassName
        val clazzOption = this.findClass(className)
        if (clazzOption.isDefined) {
            logging.logInfo(s"Class $className found")
            clazzOption.get
        } else {
            compile(classDefinition)
            this.findClass(className).get
        }
    }

    /**
     * 编译代码
     *
     * @param code code
     */
    private def compile(code: String): Unit = {
        val sourceFiles = List(new BatchSourceFile("(inline)", code))
        logging.logInfo(s"Compile code: \n" + sourceFiles.head.content.mkString)
        new this.global.Run().compileSources(sourceFiles)
    }

    /**
     * 通过类名从缓存中找类，未找到则编译
     *
     * @param className 类名
     * @return
     */
    private def findClass(className: String): Option[Class[_]] = {
        synchronized {
            this.classCache.get(className).orElse {
                try {
                    val clazz = this.classLoader.loadClass(className)
                    this.classCache(className) = clazz
                    Some(clazz)
                }
                catch {
                    case _: ClassNotFoundException => None
                }
            }
        }
    }

    /**
     * 获取（生成）类名
     *
     * @param code code
     * @return
     */
    private def getClassName(code: String): String = {
        val splits = code.split("( {".toCharArray)
        val classIndex = splits.indexOf("class")
        if (classIndex > -1) {
            splits(classIndex + 1)
        } else {
            "sha" + new BigInteger(1, MessageDigest.getInstance("SHA-1").digest(code.getBytes)).toString(16)
        }
    }

    /**
     * 表达式求值
     *
     * @param expression 可执行代码字符串
     * @tparam T 期望的返回值类型
     * @return
     */
    def evaluate[T](expression: String): T = {
        synchronized[T] {
            this.compileExpression(expression).getConstructor().newInstance().asInstanceOf[() => Any].apply().asInstanceOf[T]
        }
    }

    /**
     * 动态编译字符串形式的可执行代码，例如: 1 + 2
     *
     * @param expression 可执行代码字符串
     * @return
     */
    def compileExpression(expression: String): Class[_] = {
        val className = getClassName(expression)
        val code = Compiler.wrapExpressionInClass(className, expression)
        compileClass(code, className)
    }

}