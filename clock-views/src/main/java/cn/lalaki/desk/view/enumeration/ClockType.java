package cn.lalaki.desk.view.enumeration;

public enum ClockType {

    analogical(0), numeric(1), stopwatch(2), time_counter(3);

    ClockType(int i) {
        this.id = i;
    }

    private final int id;

    public int getId() {
        return id;
    }

    public static ClockType fromId(int id) {
        for (ClockType clockType : values()) {
            if (clockType.id == id) {
                return clockType;
            }
        }
        throw new IllegalArgumentException();
    }
}
