package io.github.ppdzm.utils.universal.cli.table;

/**
 * @author StuartAlex on 2019-11-01 11:51
 * ┏━━━━━┳━━━━━┓
 * ┃     ┃     ┃
 * ┗━━━━━┻━━━━━┛
 * ┏━━━━━┳━━━━━┓
 * ┃     ┃     ┃
 * ┣━━━━━╋━━━━━┫
 * ┃     ┃     ┃
 * ┡━━━━━╇━━━━━┩
 * │     │     │
 * ├─────┼─────┤
 * │     │     │
 * └─────┴─────┘
 */
public class PrettyBorder extends AbstractBorder {
    public final String headerLeftTopAngle = "┏";
    public final String headerHorizontal = "━";
    public final String headerRightTopAngle = "┓";
    public final String headerUpT = "┳";
    public final String headerVertical = "┃";
    public final String headerLeftBottomAngle = "┗";
    public final String headerDownT = "┻";
    public final String headerRightBottomAngle = "┛";
    public final String headerLeftT = "┣";
    public final String headerRightT = "┫";
    public final String headerCross = "╋";
    public final String headerRowLeftT = "┡";
    public final String headerRowRightT = "┩";
    public final String headerRowUpT = "┯";
    public final String headerRowDownT = "┷";
    public final String headerRowCross = "╇";
    public final String rowLeftTopAngle = "┌";
    public final String rowRightTopAngle = "┐";
    public final String rowLeftBottomAngle = "└";
    public final String rowRightBottomAngle = "┘";
    public final String rowHorizontal = "─";
    public final String rowVertical = "│";
    public final String rowRightT = "┤";
    public final String rowLeftT = "├";
    public final String rowDownT = "┴";
    public final String rowUpT = "┬";
    public final String rowCross = "┼";
}
