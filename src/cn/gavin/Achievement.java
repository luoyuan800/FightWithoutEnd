package cn.gavin;

import cn.gavin.activity.MainGameActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gluo on 8/28/2015.
 */
public enum Achievement {
    click10000("点击中手", "点击次数达到10000次", 0, 0, 0, 2),
    click50000("点击高手", "点击次数达到50000次", 0, 0, 0, 4),
    click100000("点击达人", "点击次数达到100000次", 0, 0, 0, 6),
    click100("点击新手", "点击次数达到100次", 0, 0, 0, 0),
    EMPTY("", "", 0, 0, 0, 0);
    private int addStrength;
    private int addPower;
    private int addAgility;
    private String name;
    private String desc;
    private boolean enable;
    private int click;

    private Achievement(String name, String desc, int addStrength, int addPower, int addAgility, int click) {
        this.name = name;
        this.desc = desc;
        this.addStrength = addStrength;
        this.addAgility = addAgility;
        this.addPower = addPower;
        this.enable = false;
        this.click = click;
    }

    public void enable(){
        this.enable = true;
    }

    public void enable(Hero hero) {
        this.enable = true;
        hero.addStrength(addStrength);
        hero.addLife(addPower);
        hero.addAgility(addAgility);
        hero.addClickAward(click);
    }

    public void disable(Hero hero) {
        this.enable = false;
        hero.addStrength(-addStrength);
        hero.addLife(-addPower);
        hero.addAgility(-addAgility);
        hero.addClickAward(-click);
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isEnable() {
        return enable;
    }

    public static List<MainGameActivity.AchievementList> getAchievementListAdp() {
        List<MainGameActivity.AchievementList> rs = new ArrayList<MainGameActivity.AchievementList>(values().length / 3);
        for (int i = 0; i < values().length; i += 3) {
            Achievement a0 = values()[i];
            Achievement a1 = i + 1 < values().length ? values()[i + 1] : EMPTY;
            Achievement a2 = i + 2 < values().length ? values()[i + 2] : EMPTY;
            MainGameActivity.AchievementList list = new MainGameActivity.AchievementList(a0, a1, a2);
            rs.add(list);
        }
        return rs;
    }
}
