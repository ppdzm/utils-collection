package io.github.ppdzm.utils.universal.cli;

/**
 * @author Created by Stuart Alex on 2017/3/29.
 */
public enum Alignment {
    /**
     * 左对齐
     */
    LEFT(-1, "left"),
    /**
     * 居中对齐
     */
    CENTER(0, "center"),
    /**
     * 右对齐
     */
    RIGHT(1, "right");

    public int id;
    public String name;

    Alignment(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Alignment construct(Object any) {
        try {
            int id = Integer.parseInt(any.toString());
            for (Alignment value : Alignment.values()) {
                if (value.id == id) {
                    return value;
                }
            }
            throw new IllegalArgumentException("ID " + id + " is illegal for Alignment");
        } catch (Exception e) {
            String name = any.toString();
            for (Alignment value : Alignment.values()) {
                if (value.name.equals(name)) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Name " + name + " is illegal for Alignment");
        }
    }

}
