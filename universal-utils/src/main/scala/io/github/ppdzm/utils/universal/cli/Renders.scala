package io.github.ppdzm.utils.universal.cli

/**
 * @author StuartAlex on 2019-07-26 18:16
 */
object Renders {
    /**
     * 重置所有设置
     */
    val RESET: Render = new Render(0)
    /**
     * 粗体（高亮度）
     */
    val BOLD: Render = new Render(1)
    /**
     * 亮度减半
     */
    val HALF_LIGHT: Render = new Render(2)
    /**
     * 斜体
     */
    val ITALIC: Render = new Render(3)
    /**
     * 下划线
     */
    val UNDERLINED: Render = new Render(4)
    /**
     * 闪烁
     */
    val BLINK: Render = new Render(5)
    /**
     * 文本和背景颜色反转
     */
    val REVERSED: Render = new Render(7)
    /**
     * 你看不见我
     */
    val INVISIBLE: Render = new Render(8)
    /**
     * 黑色文本
     */
    val BLACK: Render = new Render(30)
    /**
     * 红色文本
     */
    val RED: Render = new Render(31)
    /**
     * 绿色文本
     */
    val GREEN: Render = new Render(32)
    /**
     * 黄色文本
     */
    val YELLOW: Render = new Render(33)
    /**
     * 蓝色文本
     */
    val BLUE: Render = new Render(34)
    /**
     * 洋红色文本
     */
    val MAGENTA: Render = new Render(35)
    /**
     * 青色文本
     */
    val CYAN: Render = new Render(36)
    /**
     * 白色文本
     */
    val WHITE: Render = new Render(37)
    /**
     * 黑色背景
     */
    val BLACK_B: Render = new Render(40)
    /**
     * 红色背景
     */
    val RED_B: Render = new Render(41)
    /**
     * 绿色背景
     */
    val GREEN_B: Render = new Render(42)
    /**
     * 黄色背景
     */
    val YELLOW_B: Render = new Render(43)
    /**
     * 蓝色背景
     */
    val BLUE_B: Render = new Render(44)
    /**
     * 洋红色背景
     */
    val MAGENTA_B: Render = new Render(45)
    /**
     * 青色背景
     */
    val CYAN_B: Render = new Render(46)
    /**
     * 白色背景
     */
    val WHITE_B: Render = new Render(47)

    def valueOf(value: Int): Render = {
        this.getClass.getDeclaredFields
            .filter(_.getType.getSimpleName == classOf[Render].getSimpleName)
            .find(f => {
                f.setAccessible(true)
                f.get(this).asInstanceOf[Render].value == value
            }).getOrElse(throw new IllegalArgumentException(s"Illegal value $value"))
            .get(this)
            .asInstanceOf[Render]
    }

    class Render private[Renders](val value: Int) {
        override def toString: String = value.toString
    }

}
