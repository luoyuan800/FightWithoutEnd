package cn.gavin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Skill {

    private int id;
    private String name;
    private int type;
    private int occurProbability; // 触发几率,百分比数值控制触发
    private int level;
    private int skillEffect; // 技能具体效果
    private float harm; //伤害加成
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
    public float getHarmAdditionValue() {
        return harm;
    }

    public Skill(int id, String name, float harm, int occurProbability) {
        super();
        this.id = id;
        this.name = name;
        this.occurProbability = occurProbability;
        this.level = 1;
        this.harm = harm;
    }

    public static List<Skill> getAllSkills() {
        List<Skill> allSkills = new ArrayList<Skill>();
        Skill skill1 = new Skill(1, "重重一击", 200.0f, 10);
        Skill skill2 = new Skill(2, "群体攻击", 0.80f, 7);
        skill2.isGroup = true;
        Skill skill3 = new Skill(3, "生命恢复", 0.20f, 8);
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

    public boolean use(Hero hero) {
        Random random = new Random();
        int occur = 0;
        switch (id) {
            case 1:
                occur = getOccurProbability() + random.nextInt(hero.getStrength() + hero.getAgility());
                break;
            case 2:
                occur = getOccurProbability() + random.nextInt(hero.getAgility() + hero.getAgility());
                break;
            case 3:
                occur = getOccurProbability() + random.nextInt(hero.getPower() + hero.getAgility());
                break;
            default:
                break;
        }
        if (occur > 100) {
            occur = random.nextInt(100);
        }
        return random.nextInt(100) + 1 <= occur;
    }

    public Collection<? extends String> release(Hero hero, Monster ... monsters) {
        List<String> msg = new ArrayList<String>();
        msg.add(hero.getName() + "使用了" + getName());
        int atk = 0;
        switch (id){
            case 1:
                atk = hero.getAttackValue() * Math.round(getHarmAdditionValue());
                monsters[0].addHp(-atk);
                msg.add(hero.getName() + "攻击了" + monsters[0].getName() + "，造成了" + atk + "点伤害。");
                break;
            case 2:
                atk = hero.getAttackValue() * Math.round(getHarmAdditionValue());
                for(Monster monster : monsters) {
                    monster.addHp(-atk);
                    msg.add(hero.getName() + "攻击了" + monster.getName() + "，造成了" + atk + "点伤害。");
                }
                break;
            case 3:
                int v =Math.round(getHarmAdditionValue() * hero.getUpperHp() + new Random().nextInt(hero.getPower()));
                if(v + hero.getHp() > hero.getUpperHp()){
                    hero.restore();
                }else {
                    hero.addHp(v);
                }
        }
        return msg;
    }
}
