package io.github.ppdzm.utils.universal.cli;

public enum Alignment {
    LEFT(-1, "left"),
    CENTER(0, "center"),
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
                if (value.name == name) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Name " + name + " is illegal for Alignment");
        }
    }

}
