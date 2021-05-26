package io.github.ppdzm.utils.spark.hive.udf;

import io.github.ppdzm.utils.universal.base.StringUtils;
import io.github.ppdzm.utils.universal.base.functions;
import org.apache.hadoop.hive.ql.exec.UDF;


/**
 * @author Created by Stuart Alex on 2017/11/29
 */
public class SubstringIndex extends UDF {

    public String evaluate(String source, String delimiter, int count) {
        return StringUtils.substringIndex(source, delimiter, count);
    }

}