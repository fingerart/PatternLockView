package me.fingerart.patternlock.view;

/**
 * 点
 * Created by FingerArt.me on 2016/7/25.
 */
class Point {
    /**
     * x,y坐标
     */
    private float x, y;

    /**
     * 当前状态
     */
    private PointState state = PointState.POINT_STATE_NORMAL;

    /**
     * 单个点的密码
     */
    private String value;

    public Point() {
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point(float x, float y, PointState state) {
        this.x = x;
        this.y = y;
        this.state = state;
    }

    public Point(float x, float y, String value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public PointState getState() {
        return state;
    }

    public void setState(PointState state) {
        this.state = state;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", state=" + state +
                ", value='" + value + '\'' +
                '}';
    }
}
