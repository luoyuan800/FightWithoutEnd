package cn.gavin;

import java.util.List;
import java.util.Random;

public class Hero {
    private static final String TAG = "Hero";

    public static int MAX_GOODS_COUNT = 50;

    // 血上限成长(每点生命点数增加）
    public static final int MAX_HP_RISE = 5;
    // 攻击成长（每点力量点数增加）
    public static final int ATR_RISE = 2;
    // 防御成长 （每点敏捷点数增加）
    public static final int DEF_RISE = 1;
    private int click;
    private String name;
    private int hp;//当前
    private int upperHp;//上限值
    private int attackValue;
    private int defenseValue;
    public int level;
    private List<Skill> existSkill; // 已有的技能
    private Sword sword;
    private Armor armor;
    private int swordLev;
    private int armorLev;
    private int material;
    private int point;
    private int strength;//力量，影响攻击数值上限
    private int power;//体力，影响HP上限，生命恢复技能效果
    private int agility;//敏捷，影响技能施放概率，防御数值上限
    private int maxMazeLev = 0;
    private Random random;
    private String swordName;
    private String armorName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHp() {
        return hp;
    }

    public void addHp(int hp) {
        this.hp += hp;
    }

    public int getAttackValue() {
        return attackValue + swordLev * 10;
    }

    public void addAttackValue(int attackValue) {
        this.attackValue += attackValue;
    }

    public int getDefenseValue() {
        return defenseValue + armorLev * 8;
    }

    public void addDefenseValue(int defenseValue) {
        this.defenseValue += defenseValue;
    }

    public List<Skill> getExistSkill() {
        return existSkill;
    }

    private Hero(String name, int hp, int attackValue, int defenseValue, int level) {
        super();
        this.name = name;
        this.hp = hp;
        this.attackValue = attackValue;
        this.defenseValue = defenseValue;
        this.level = level;
        this.upperHp = hp;
        existSkill = cn.gavin.Skill.getAllSkills();
        sword = Sword.木剑;
        armor = Armor.破布;
    }

    public Hero(String name) {
        this(name, 20, 10, 10, 1);
        random = new Random();
        strength = random.nextInt(5);
        agility = random.nextInt(5);
        power = random.nextInt(5);
    }

    @Override
    public String toString() {
        return "Hero [name=" + name + ", hp=" + hp + ", attackValue=" + attackValue
                + ", defenseValue=" + defenseValue + ", level=" + level + "]";
    }

    public boolean upgradeSword() {
        if (swordLev * 10 + attackValue >= Integer.MAX_VALUE - 100) {
            return false;
        } else {
            if (material >= 10 + swordLev) {
                swordLev++;
                if (sword != sword.levelUp(swordLev)) {
                    sword = sword.levelUp(swordLev);
                    swordLev = 0;
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean upgradeArmor() {
        if (armorLev * 10 + defenseValue >= Integer.MAX_VALUE - 100) {
            return false;
        } else {
            if (material >= 10 + armorLev) {
                material -= 10 + armorLev;
                armorLev++;
                if (armor != armor.levelUp(armorLev)) {
                    armor = armor.levelUp(armorLev);
                    armorLev = 0;
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public int getMaterial() {
        return material;
    }

    public void addMaterial(int material) {
        this.material += material;
    }

    public int getPoint() {
        return point;
    }

    public void addPoint(int point) {
        this.point += point;
    }

    public int getStrength() {
        return strength;
    }

    public void addStrength() {
        if (point != 0) {
            point--;
            strength++;
            attackValue += ATR_RISE;
        }
    }

    public int getPower() {
        return power;
    }

    public void addLife() {
        if (point != 0) {
            point--;
            power++;
            hp += MAX_HP_RISE;
            upperHp += MAX_HP_RISE;
        }
    }

    public int getAgility() {
        return agility;
    }

    public void addAgility() {
        if (point != 0) {
            point--;
            agility++;
            defenseValue += DEF_RISE;
        }
    }

    public void restore() {
        this.hp = upperHp;
    }

    public int getMaxMazeLev() {
        return maxMazeLev;
    }

    public void addMaxMazeLev() {
        this.maxMazeLev++;
    }

    public int getSwordLev() {
        return swordLev;
    }

    public int getArmorLev() {
        return armorLev;
    }

    public int getClick() {
        return click;
    }

    public void click() {
        if (this.click % 1000 == 0) {
            point += random.nextInt(15);
        }
        this.material++;
        this.click++;
    }

    public Skill useSkill() {
        for (Skill skill : existSkill) {
            if (skill.use(this)) {
                return skill;
            }
        }
        return null;
    }

    public int getUpperHp() {
        return upperHp;
    }

    public String getSword() {
        return sword.name();
    }

    public String getArmor() {
        return armor.name();
    }
}
