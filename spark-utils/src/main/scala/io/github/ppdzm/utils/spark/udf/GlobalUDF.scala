package io.github.ppdzm.utils.spark.udf

import io.github.ppdzm.utils.universal.base.functions
import io.github.ppdzm.utils.universal.implicits.BasicConversions.AnyImplicits
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._

/**
 * Created by Stuart Alex on 2016/5/26.
 */
object GlobalUDF {

    /**
     * 将字符串null替换为空字符串
     *
     * @return org.apache.spark.sql.Column
     */
    def null2Empty: UserDefinedFunction = udf { (value: String) => if (value.isNull || value.toString.toLowerCase == "null") "" else value }

    /**
     * 返回一个值为空字符串的Column
     *
     * @return org.apache.spark.sql.Column
     */
    def Empty: UserDefinedFunction = udf { () => "" }

    /**
     * 返回一个值为“-”的Column
     *
     * @return org.apache.spark.sql.Column
     */
    def Dash: UserDefinedFunction = udf { () => "-" }

    /**
     * 返回一个值为0的Column
     *
     * @return org.apache.spark.sql.Column
     */
    def Zero: UserDefinedFunction = udf { () => 0 }

    /**
     * 返回一个值为false的Column
     *
     * @return
     */
    def False: UserDefinedFunction = udf { () => false }

    /**
     * 将一个Column的值转换为大写
     *
     * @return org.apache.spark.sql.Column
     */
    def toUpper: UserDefinedFunction = udf { letter: String => letter.toUpperCase }

    /**
     * 将一个Column的值转换为小写
     *
     * @return org.apache.spark.sql.Column
     */
    def toLower: UserDefinedFunction = udf { letter: String => letter.toLowerCase }

    /**
     * 将汉语转换为不带声调的拼音，使用v代表ü
     *
     * @return org.apache.spark.sql.Column
     */
    //def mandarin2Phoneticization = udf { (hanyu: String) => RegisterUDF.mandarin2Phoneticization(hanyu) }

    /**
     * 字节数组转换为Long
     *
     * @return
     */
    def bytes2Long: UserDefinedFunction = udf { (bytes: Array[Byte]) => functions.bytes2Long(bytes) }

}