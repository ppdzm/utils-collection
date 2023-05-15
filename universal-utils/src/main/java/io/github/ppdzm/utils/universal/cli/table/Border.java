package io.github.ppdzm.utils.universal.cli.table;

public interface Border {
    /**
     * 列标题上边界交叉字符
     *
     * @param vertical 是否添加垂直边框
     * @return String
     */
    String headerUpBorderCross(boolean vertical);

    /**
     * 列标题下边界交叉字符
     *
     * @param vertical 是否添加垂直边框
     * @param empty    行数是否为0
     * @return String
     */
    String headerDownBorderCross(boolean vertical, boolean empty);

    /**
     * 列标题上下界线条字符
     */
    String headerLineBorder();

    /**
     * 列标题两侧线条字符
     *
     * @param flank 是否添加侧边框
     * @return String
     */
    String headerFlankBorder(boolean flank);

    /**
     * 列标题分隔线条字符
     *
     * @param vertical 是否添加垂直边框
     * @return String
     */
    String headerVerticalBorder(boolean vertical);

    /**
     * 行分隔线条字符
     *
     * @return String
     */
    String rowLineBorder();

    /**
     * 行分隔线条交叉字符
     *
     * @param vertical 是否添加垂直边框
     * @return String
     */
    String rowCrossBorder(boolean vertical);

    /**
     * 数据行两侧线条字符
     *
     * @param flank 是否添加侧边框
     * @return String
     */
    String rowFlankBorder(boolean flank);

    /**
     * 数据行分隔线条字符
     *
     * @param vertical 是否添加垂直边框
     * @return String
     */
    String rowVerticalBorder(boolean vertical);

    /**
     * 尾交叉字符
     *
     * @param vertical 是否添加垂直边框
     * @return String
     */
    String rowTailCrossBorder(boolean vertical);
}
