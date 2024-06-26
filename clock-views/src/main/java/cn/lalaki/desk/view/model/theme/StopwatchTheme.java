package cn.lalaki.desk.view.model.theme;

import cn.lalaki.desk.view.enumeration.BorderStyle;
import cn.lalaki.desk.view.enumeration.ClockType;
@SuppressWarnings("unused")
public class StopwatchTheme {

    private final ClockType clockType;

    private final int clockBackground;

    private final int valuesFont;
    private final int valuesColor;

    private final boolean showBorder;
    private final int borderColor;
    private final BorderStyle borderStyle;
    private final int borderRadiusRx;
    private final int borderRadiusRy;

    public ClockType getClockType() {
        return clockType;
    }

    public int getClockBackground() {
        return clockBackground;
    }

    public int getValuesFont() {
        return valuesFont;
    }

    public int getValuesColor() {
        return valuesColor;
    }

    public boolean isShowBorder() {
        return showBorder;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public BorderStyle getBorderStyle() {
        return borderStyle;
    }

    public int getBorderRadiusRx() {
        return borderRadiusRx;
    }

    public int getBorderRadiusRy() {
        return borderRadiusRy;
    }

    private StopwatchTheme(StopwatchThemeBuilder stopwatchThemeBuilder) {

        this.clockType = stopwatchThemeBuilder.clockType;

        this.clockBackground = stopwatchThemeBuilder.clockBackground;

        this.valuesFont = stopwatchThemeBuilder.valuesFont;
        this.valuesColor = stopwatchThemeBuilder.valuesColor;

        this.showBorder = stopwatchThemeBuilder.showBorder;
        this.borderColor = stopwatchThemeBuilder.borderColor;
        this.borderStyle = stopwatchThemeBuilder.borderStyle;
        this.borderRadiusRx = stopwatchThemeBuilder.borderRadiusRx;
        this.borderRadiusRy = stopwatchThemeBuilder.borderRadiusRy;
    }

    public static class StopwatchThemeBuilder {

        private ClockType clockType;

        private int clockBackground;

        private int valuesFont;
        private int valuesColor;

        private boolean showBorder;
        private int borderColor;
        private BorderStyle borderStyle;
        private int borderRadiusRx;
        private int borderRadiusRy;

        public StopwatchThemeBuilder setClockType(ClockType clockType) {
            this.clockType = clockType;
            return this;
        }

        public StopwatchThemeBuilder setClockBackground(int clockBackground) {
            this.clockBackground = clockBackground;
            return this;
        }

        public StopwatchThemeBuilder setValuesFont(int valuesFont) {
            this.valuesFont = valuesFont;
            return this;
        }

        public StopwatchThemeBuilder setValuesColor(int valuesColor) {
            this.valuesColor = valuesColor;
            return this;
        }

        public StopwatchThemeBuilder setShowBorder(boolean showBorder) {
            this.showBorder = showBorder;
            return this;
        }

        public StopwatchThemeBuilder setBorderColor(int borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        public StopwatchThemeBuilder setBorderStyle(BorderStyle borderStyle) {
            this.borderStyle = borderStyle;
            return this;
        }

        public StopwatchThemeBuilder setBorderRadiusRx(int borderRadiusRx) {
            this.borderRadiusRx = borderRadiusRx;
            return this;
        }

        public StopwatchThemeBuilder setBorderRadiusRy(int borderRadiusRy) {
            this.borderRadiusRy = borderRadiusRy;
            return this;
        }

        public StopwatchTheme build() {
            return new StopwatchTheme(this);
        }
    }
}
