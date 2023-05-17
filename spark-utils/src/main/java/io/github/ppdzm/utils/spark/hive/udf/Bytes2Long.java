package io.github.ppdzm.utils.spark.hive.udf;

import io.github.ppdzm.utils.universal.base.Functions;
import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * @author Created by Stuart Alex on 2017/11/29
 */
public class Bytes2Long extends UDF {

    /**
     * 转字节数组为长整型
     * 应用场景：HBase计数列对应Hive外部表字段类型为binary，取实际值需要用此UDF
     *
     * @param bytes 字节数组
     * @return Long
     */
    public Long evaluate(byte[] bytes) {
        return Functions.bytes2Long(bytes);
    }

}