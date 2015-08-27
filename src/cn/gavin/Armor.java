package cn.gavin;

/**
 * Created by luoyuan on 8/27/15.
 */
public enum Armor {
    破布(100), 肚兜(200), 布衣(500), 布甲(1000), 铁甲(2000), 铜甲(4000), 银甲(8000), 烈焰甲(16000), 水波甲(32000), 天使甲(64000), 能量甲(100000);
    private int lev;

    private Armor(int lev) {
        this.lev = lev;
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
