package io.github.ppdzm.utils.spark.file

import io.github.ppdzm.utils.spark.SparkUtils
import org.apache.spark.sql.DataFrame

/**
 * Created by Stuart Alex on 2016/5/26.
 */
object SparkFile {

    /**
     * 把字符分隔文件加载成DataFrame
     *
     * @param path      文件路径（本地文件系统或分布式文件系统）
     * @param delimiter 分隔符
     * @return
     */
    def df(path: String, useHeader: Boolean = true, delimiter: Char = ','): DataFrame = {
        SparkUtils.getSparkSession().read
            .option("useHeader", useHeader)
            .option("delimiter", delimiter)
            .csv(path: String)
    }

}
