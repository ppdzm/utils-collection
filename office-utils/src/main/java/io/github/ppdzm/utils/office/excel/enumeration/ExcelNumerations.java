package io.github.ppdzm.utils.office.excel.enumeration;

/**
 * @author Created by Stuart Alex on 2023/5/16.
 */
public class ExcelNumerations {
    /**
     * 柱状图分组
     */
    public static enum BarChartGrouping {
        /**
         * 百分比堆积图
         */
        percentStacked,
        /**
         * 堆积图
         */
        stacked,
        /**
         * 标准
         */
        standard

    }

    /**
     * 图类型
     */
    public static enum ChartType {
        /**
         * 柱状图
         */
        bar,
        /**
         * 曲线图
         */
        line,
        /**
         * 饼图
         */
        pie
    }

    /**
     * 方向
     */
    public static enum Direction {
        /**
         * 水平
         */
        horizontal,
        /**
         * 垂直
         */
        vertical
    }

    /**
     * 曲线图分组
     */
    public static enum LineChartGrouping {
        /**
         * 百分比堆积图
         */
        percentStacked,
        /**
         * 堆积图
         */
        stacked,
        /**
         * 标准
         */
        standard
    }

    /**
     * 排序
     */
    public static enum Order {
        /**
         * 升序
         */
        asc,
        /**
         * 降序
         */
        desc
    }

    /**
     * 序列类型
     */
    public static enum SeriesType {
        /**
         * 基于列
         */
        column,
        /**
         * 基于值
         */
        value
    }
}
