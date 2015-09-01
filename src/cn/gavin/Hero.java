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
    private int clickAward = 1;

    public void addClickAward(int num) {
        clickAward += num;
    }

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
        if (hp < 0) {
            if (this.hp + hp < 0) {
                this.hp = 0;
            } else {
                this.hp += hp;
            }
        } else if (this.hp < (Integer.MAX_VALUE - hp - 100)) {
            this.hp += hp;
        }
    }

    public int getBaseAttackValue() {
        return attackValue;
    }

    public int getUpperAtk() {
        return attackValue + sword.getBase() + swordLev;
    }

    public int getUpperDef() {
        return defenseValue + armorLev + armor.getBase();
    }

    public int getAttackValue() {
        return attackValue + random.nextInt(sword.getBase()) + random.nextInt(swordLev + 1);
    }

    public void addAttackValue(int attackValue) {
        this.attackValue += attackValue;
    }

    public int getBaseDefense() {
        return defenseValue;
    }

    public int getDefenseValue() {
        int defend = defenseValue + random.nextInt(armor.getBase()) + random.nextInt(armorLev * 2 + 1);
        if (random.nextInt(100) > 96) {
            defend *= 1.5;
        }
        return defend;
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
        if (swordLev + sword.getBase() + attackValue >= Integer.MAX_VALUE - 100) {
            return false;
        } else {
            if (material >= 100 + swordLev) {
                material -= 100 + armorLev;
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
        if (armorLev + armor.getBase() + defenseValue >= Integer.MAX_VALUE - 100) {
            return false;
        } else {
            if (material >= 80 + armorLev) {
                material -= 80 + armorLev;
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
        if (this.material < 0 || this.material < (Integer.MAX_VALUE - material - 1000))
            this.material += material;
        if (this.material < 0) this.material = 0;
    }

    public int getPoint() {
        return point;
    }

    public void addPoint(int point) {
        if (point < 0 || this.point < (Integer.MAX_VALUE - point - 5000))
            this.point += point;
        if (this.point < 0) this.point = 0;
    }

    public int getStrength() {
        return strength;
    }

    public void addStrength() {
        if (point != 0 && strength < (Integer.MAX_VALUE - 500)) {
            point--;
            strength++;
            if (attackValue < (Integer.MAX_VALUE - ATR_RISE - 500))
                attackValue += ATR_RISE;
        }
    }

    public void addStrength(int str) {
        if (str < 0 || strength < (Integer.MAX_VALUE - strength - 100)) {
            strength += str;
            if (str < 0 || attackValue < (Integer.MAX_VALUE - ATR_RISE * str))
                attackValue += ATR_RISE * str;
        }
        if (strength < 0) strength = 0;
        if (attackValue < 0) attackValue = 0;
    }

    public int getPower() {
        return power;
    }

    public void addLife() {
        if (point != 0 && power < (Integer.MAX_VALUE - 500)) {
            point--;
            power++;
            if (upperHp < (Integer.MAX_VALUE - MAX_HP_RISE)) {
                hp += MAX_HP_RISE;
                upperHp += MAX_HP_RISE;
            }
        }
    }

    public void addLife(int life) {
        if (life < 0 || power < (Integer.MAX_VALUE - life - 100)) {
            power += life;
            if (life < 0 || upperHp < (Integer.MAX_VALUE - MAX_HP_RISE * life)) {
                hp += MAX_HP_RISE * life;
                upperHp += MAX_HP_RISE * life;
            }
        }
        if (power < 0) power = 0;
        if (hp < 0) hp = 0;
        if (upperHp < 0) upperHp = 0;
    }

    public int getAgility() {
        return agility;
    }

    public void addAgility() {
        if (point != 0 && (agility < Integer.MAX_VALUE - 500)) {
            point--;
            agility++;
            if (defenseValue < (Integer.MAX_VALUE - DEF_RISE))
                defenseValue += DEF_RISE;
        }
    }

    public void addAgility(int agi) {
        if (agi < 0 || agility < (Integer.MAX_VALUE - agi - 100)) {
            agility += agi;
            if (agi < 0 || defenseValue < (Integer.MAX_VALUE - DEF_RISE * agi))
                defenseValue += DEF_RISE * agi;
        }
        if (agility < 0) agility = 0;
        if (defenseValue < 0) defenseValue = 0;
    }

    public void restore() {
        this.hp = upperHp;
    }

    public int getMaxMazeLev() {
        return maxMazeLev;
    }

    public void addMaxMazeLev() {
        if (this.maxMazeLev < Integer.MAX_VALUE - 10) {
            this.maxMazeLev++;
        }
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
        if (click < Integer.MAX_VALUE - 10) {
            if (this.click % 1000 == 0) {
                point += random.nextInt(15);
            }
            this.material += clickAward;
            this.click++;
            switch (click) {
                case 100:
                    Achievement.click100.enable(this);
                    break;
                case 10000:
                    Achievement.click10000.enable(this);
                    break;
                case 50000:
                    Achievement.click50000.enable(this);
                    break;
                case 100000:
                    Achievement.click100000.enable(this);
                    break;
            }
        }
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

    public void setClick(int click) {
        this.click = click;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setUpperHp(int upperHp) {
        this.upperHp = upperHp;
    }

    public void setAttackValue(int attackValue) {
        this.attackValue = attackValue;
    }

    public void setDefenseValue(int defenseValue) {
        this.defenseValue = defenseValue;
    }

    public void setSword(Sword sword) {
        this.sword = sword;
    }

    public void setArmor(Armor armor) {
        this.armor = armor;
    }

    public void setSwordLev(int swordLev) {
        this.swordLev = swordLev;
    }

    public void setArmorLev(int armorLev) {
        this.armorLev = armorLev;
    }

    public void setMaterial(int material) {
        this.material = material;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public void setMaxMazeLev(int maxMazeLev) {
        this.maxMazeLev = maxMazeLev;
    }

    public void setClickAward(int clickAward) {
        this.clickAward = clickAward;
    }

    public int getClickAward() {
        return clickAward;
    }
}
