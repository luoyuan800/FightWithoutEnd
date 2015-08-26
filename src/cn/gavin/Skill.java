
package cn.gavin;

import java.util.ArrayList;
import java.util.List;

public class Skill {

    private int id;
    private String name;
    private int type;
    private int occurProbability; // 触发几率,百分比数值控制触发
    private int level;
    private int skillEffect; // 技能具体效果
    private double harm; //伤害加成
    private boolean isGroup = false;
    private boolean isRestore = false;
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public int getOccurProbability() {
        return occurProbability;
    }

    public void setOccurProbability(int occurProbability) {
        this.occurProbability = occurProbability;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * 获得攻击加成指数（百分比）
     * 
     * @return
     */
    public double getHarmAdditionValue() {
        return harm;
    }

    public Skill(int id, String name, double harm, int occurProbability) {
        super();
        this.id = id;
        this.name = name;
        this.occurProbability = occurProbability;
        this.level = 1;
        this.harm = harm;
    }

    public static List<Skill> getAllSkills() {
        List<Skill> allSkills = new ArrayList<Skill>();
        Skill skill1 = new Skill(1, "重重一击", 200.0, 10);
        Skill skill2 = new Skill(2, "群体攻击", 0.80, 7);
        skill2.isGroup = true;
        Skill skill3 = new Skill(3, "生命恢复", 20.0, 8);
        skill3.isRestore = true;
        allSkills.add(skill1);
        allSkills.add(skill2);
        allSkills.add(skill3);
        return allSkills;
    }

    public boolean isRestore() {
        return isRestore;
    }

    public boolean isGroup() {
        return isGroup;
    }
}
