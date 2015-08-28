package cn.gavin;

/**
 * Created by luoyuan on 8/27/15.
 */
public enum Armor {
    破布(100,3), 肚兜(200, 103), 布衣(500, 303), 布甲(1000, 800), 铁甲(2000, 1800), 铜甲(4000, 3000), 银甲(8000, 7000),
    烈焰甲(16000, 15000), 水波甲(32000, 30000), 天使甲(64000, 64000), 能量甲(100000, 128000);
    private int lev, base;
public int getBase(){
    return base;
}
    private Armor(int lev, int base) {
        this.lev = lev;
        this.base = base;
    }

    public Armor levelUp(int lev) {
        if (lev >= this.lev) {
            int index = ordinal();
            if (index < values().length - 1) {
                return values()[index + 1];
            }
        }
        return this;
    }
}
