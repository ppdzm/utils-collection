package io.github.ppdzm.utils.office.excel.workbook;

import java.io.IOException;

/**
 * @author Created by Stuart Alex on 2019/3/29
 */
public interface WorkBook {
    /**
     * 关闭Workbook
     * @throws IOException IOException
     */
    void close() throws IOException;

    /**
     * 检查扩展名是否支持
     *
     * @param supportedExtensions 支持的扩展名
     * @throws Exception Exception
     */
    void checkExtension(String... supportedExtensions) throws Exception;
}
