package io.github.ppdzm.utils.universal.cli.table;

/**
 * @author Created by Stuart Alex on 2017/3/29.
 */
public abstract class AbstractBorder implements Border {
    protected String headerHorizontal;
    protected String headerUpT;
    protected String headerVertical;
    protected String headerDownT;
    protected String headerRowCross;
    protected String rowHorizontal;
    protected String rowVertical;
    protected String rowDownT;
    protected String rowCross;

    @Override
    public String headerUpBorderCross(boolean vertical) {
        return vertical ? headerUpT : headerHorizontal;
    }

    @Override
    public String headerDownBorderCross(boolean vertical, boolean empty) {
        if (vertical) {
            return empty ? headerDownT : headerRowCross;
        } else {
            return headerHorizontal;
        }
    }

    @Override
    public String headerLineBorder() {
        return headerHorizontal;
    }

    @Override
    public String headerFlankBorder(boolean flank) {
        return flank ? headerVertical : "";
    }

    @Override
    public String headerVerticalBorder(boolean vertical) {
        return vertical ? headerVertical : " ";
    }

    @Override
    public String rowLineBorder() {
        return rowHorizontal;
    }

    @Override
    public String rowCrossBorder(boolean vertical) {
        return vertical ? rowCross : rowHorizontal;
    }

    @Override
    public String rowFlankBorder(boolean flank) {
        return flank ? rowVertical : "";
    }

    @Override
    public String rowVerticalBorder(boolean vertical) {
        return vertical ? rowVertical : " ";
    }

    @Override
    public String rowTailCrossBorder(boolean vertical) {
        return vertical ? rowDownT : rowHorizontal;
    }

}
