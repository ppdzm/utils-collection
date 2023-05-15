package io.github.ppdzm.utils.universal.cli.table;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author StuartAlex on 2019-11-01 11:51
 * +=====+=====+
 * |     |     |
 * +=====+=====+
 * |     |     |
 * +-----+-----+
 * |     |     |
 * +-----+-----+
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DefaultBorder extends AbstractBorder {
    private final String headerLeftTopAngle = "+";
    private final String headerHorizontal = "=";
    private final String headerRightTopAngle = "+";
    private final String headerUpT = "+";
    private final String headerVertical = "|";
    private final String headerLeftBottomAngle = "+";
    private final String headerDownT = "+";
    private final String headerRightBottomAngle = "+";
    private final String headerLeftT = "+";
    private final String headerRightT = "+";
    private final String headerCross = "+";
    private final String headerRowLeftT = "+";
    private final String headerRowRightT = "+";
    private final String headerRowUpT = "+";
    private final String headerRowDownT = "+";
    private final String headerRowCross = "+";
    private final String rowLeftTopAngle = "+";
    private final String rowRightTopAngle = "+";
    private final String rowLeftBottomAngle = "+";
    private final String rowRightBottomAngle = "+";
    private final String rowHorizontal = "-";
    private final String rowVertical = "|";
    private final String rowRightT = "+";
    private final String rowLeftT = "+";
    private final String rowDownT = "+";
    private final String rowUpT = "+";
    private final String rowCross = "+";
}
