package io.github.ppdzm.utils.universal.cli;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
public enum Render {
    /**
     * 重置所有设置
     */
    RESET(0),
    /**
     * 粗体（高亮度）
     */
    BOLD(1),
    /**
     * 亮度减半
     */
    HALF_LIGHT(2),
    /**
     * 斜体
     */
    ITALIC(3),
    /**
     * 下划线
     */
    UNDERLINED(4),
    /**
     * 闪烁
     */
    BLINK(5),
    /**
     * 文本和背景颜色反转
     */
    REVERSED(7),
    /**
     * 你看不见我
     */
    INVISIBLE(8),
    /**
     * 黑色文本
     */
    BLACK(30),
    /**
     * 红色文本
     */
    RED(31),
    /**
     * 绿色文本
     */
    GREEN(32),
    /**
     * 黄色文本
     */
    YELLOW(33),
    /**
     * 蓝色文本
     */
    BLUE(34),
    /**
     * 洋红色文本
     */
    MAGENTA(35),
    /**
     * 青色文本
     */
    CYAN(36),
    /**
     * 白色文本
     */
    WHITE(37),
    /**
     * 黑色背景
     */
    BLACK_B(40),
    /**
     * 红色背景
     */
    RED_B(41),
    /**
     * 绿色背景
     */
    GREEN_B(42),
    /**
     * 黄色背景
     */
    YELLOW_B(43),
    /**
     * 蓝色背景
     */
    BLUE_B(44),
    /**
     * 洋红色背景
     */
    MAGENTA_B(45),
    /**
     * 青色背景
     */
    CYAN_B(46),
    /**
     * 白色背景
     */
    WHITE_B(47);

    private int value;

    Render(int value) {
        this.value = value;
    }

    public static Render valueOf(int value) {
        for (Render render : Render.values()) {
            if (render.value == value) {
                return render;
            }
        }
        throw new IllegalArgumentException("Illegal value " + value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
