package io.github.ppdzm.utils.office.excel.chart;

import org.apache.poi.ss.usermodel.charts.ChartDataSource;

import java.util.Map;

/**
 * @author Created by Stuart Alex on 2023/5/16.
 */
public interface Series {
    /**
     *
     * @return
     */
    Map<String, ChartDataSource<Number>> getSeries();
}
