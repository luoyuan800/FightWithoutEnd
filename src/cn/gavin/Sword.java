package cn.gavin;

/**
 * Created by luoyuan on 8/27/15.
 */
public enum Sword {

    木剑(100), 铁剑(200), 铜剑(500), 银剑(1000), 金剑(2000), 合金剑(4000), 鱼肠剑(8000), 水波剑(16000), 烈焰剑(32000), 水焰剑(64000), 风烈剑(100000);
    private int lev;

    private Sword(int lev) {
        this.lev = lev;
    }

    public Sword levelUp(int lev) {
        if (lev >= this.lev) {
            int index = ordinal();
            if (index < values().length - 1) {
                return values()[index + 1];
            }
        }
        return this;
    }
}
