package cn.lalaki.desk.view.enumeration;

public enum BorderStyle {

    rectangle(0), circle(1), rounded_rectangle(2);

    BorderStyle(int i) {
        this.id = i;
    }

    private final int id;

    public int getId() {
        return id;
    }

    public static BorderStyle fromId(int id) {
        for (BorderStyle borderStyle : values()) {
            if (borderStyle.id == id) return borderStyle;
        }
        throw new IllegalArgumentException();
    }
}
