package cn.lalaki.desk.view.enumeration.analogical;

public enum ValueDisposition {

    regular(-1), alternate(0);

    ValueDisposition(int i) {
        this.id = i;
    }

    private final int id;

    public int getId() {
        return id;
    }

    public static ValueDisposition fromId(int id) {
        for (ValueDisposition valueDisposition : values()) {
            if (valueDisposition.id == id) {
                return valueDisposition;
            }
        }
        throw new IllegalArgumentException();
    }
}
