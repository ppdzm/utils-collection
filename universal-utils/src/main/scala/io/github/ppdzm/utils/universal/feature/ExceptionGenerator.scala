package io.github.ppdzm.utils.universal.feature

object ExceptionGenerator {

    def newException(exceptionName: String, exceptionMessage: String): Exception = {
        val code = s"""case class ${exceptionName}Exception(message: String) extends Exception(message)"""
        val clazz = Compiler.compileClass(code, s"${exceptionName}Exception")
        val constructor = clazz.getConstructor(classOf[String])
        constructor.newInstance(exceptionMessage).asInstanceOf[Exception]
    }

}