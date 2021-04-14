package io.github.ppdzm.utils.universal.cli

trait Bricks {
    val headerLeftTopAngle: String
    val headerHorizontal: String
    val headerRightTopAngle: String
    val headerUpT: String
    val headerVertical: String
    val headerLeftBottomAngle: String
    val headerDownT: String
    val headerRightBottomAngle: String
    val headerLeftT: String
    val headerRightT: String
    val headerCross: String
    val headerRowLeftT: String
    val headerRowRightT: String
    val headerRowUpT: String
    val headerRowDownT: String
    val headerRowCross: String
    val rowLeftTopAngle: String
    val rowRightTopAngle: String
    val rowLeftBottomAngle: String
    val rowRightBottomAngle: String
    val rowHorizontal: String
    val rowVertical: String
    val rowRightT: String
    val rowLeftT: String
    val rowDownT: String
    val rowUpT: String
    val rowCross: String

    /**
     * 列标题上边界交叉字符
     *
     * @param vertical 是否添加垂直边框
     * @return
     */
    def headerUpBorderCross(vertical: Boolean): String = if (vertical) headerUpT else headerHorizontal

    /**
     * 列标题下边界交叉字符
     *
     * @param vertical 是否添加垂直边框
     * @param empty    行数是否为0
     * @return
     */
    def headerDownBorderCross(vertical: Boolean, empty: Boolean): String = {
        if (vertical) {
            if (empty) headerDownT else headerRowCross
        } else {
            headerHorizontal
        }
    }

    /**
     * 列标题上下界线条字符
     */
    def headerLineBorder: String = headerHorizontal

    /**
     * 列标题两侧线条字符
     *
     * @param flank 是否添加侧边框
     * @return
     */
    def headerFlankBorder(flank: Boolean): String = if (flank) headerVertical else ""

    /**
     * 列标题分隔线条字符
     *
     * @param vertical 是否添加垂直边框
     * @return
     */
    def headerVerticalBorder(vertical: Boolean): String = if (vertical) headerVertical else " "

    /**
     * 行分隔线条字符
     *
     * @return
     */
    def rowLineBorder: String = rowHorizontal

    /**
     * 行分隔线条交叉字符
     *
     * @param vertical 是否添加垂直边框
     * @return
     */
    def rowCrossBorder(vertical: Boolean): String = if (vertical) rowCross else rowHorizontal

    /**
     * 数据行两侧线条字符
     *
     * @param flank 是否添加侧边框
     * @return
     */
    def rowFlankBorder(flank: Boolean): String = if (flank) rowVertical else ""

    /**
     * 数据行分隔线条字符
     *
     * @param vertical 是否添加垂直边框
     * @return
     */
    def rowVerticalBorder(vertical: Boolean): String = if (vertical) rowVertical else " "

    /**
     * 尾交叉字符
     *
     * @param vertical 是否添加垂直边框
     * @return
     */
    def rowTailCrossBorder(vertical: Boolean): String = if (vertical) rowDownT else rowHorizontal
}
