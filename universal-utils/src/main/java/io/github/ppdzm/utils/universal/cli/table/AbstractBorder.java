package io.github.ppdzm.utils.universal.cli.table;

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

    public String headerUpBorderCross(boolean vertical) {
        return vertical ? headerUpT : headerHorizontal;
    }

    public String headerDownBorderCross(boolean vertical, boolean empty) {
        if (vertical) {
            return empty ? headerDownT : headerRowCross;
        } else {
            return headerHorizontal;
        }
    }

    public String headerLineBorder() {
        return headerHorizontal;
    }

    public String headerFlankBorder(boolean flank) {
        return flank ? headerVertical : "";
    }

    public String headerVerticalBorder(boolean vertical) {
        return vertical ? headerVertical : " ";
    }

    public String rowLineBorder() {
        return rowHorizontal;
    }

    public String rowCrossBorder(boolean vertical) {
        return vertical ? rowCross : rowHorizontal;
    }

    public String rowFlankBorder(boolean flank) {
        return flank ? rowVertical : "";
    }

    public String rowVerticalBorder(boolean vertical) {
        return vertical ? rowVertical : " ";
    }

    public String rowTailCrossBorder(boolean vertical) {
        return vertical ? rowDownT : rowHorizontal;
    }

}
