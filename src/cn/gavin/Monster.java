package cn.gavin;

import java.util.Random;

/**
 * Created by gluo on 8/26/2015.
 */
public class Monster {
    private final static String[] firstNames = {"普通", "怪异", "神奇"};
    private final static String[] secondNames = {"小", "中", "大"};
    private final static String[] lastNames = {"猪", "老鼠", "龙"};
    private final static int[] firstAdditionHP = {5, 25, 100};
    private final static int[] firstAdditionAtk = {5, 25, 80};
    private final static int[] secondAdditionHP = {5, 25, 100};
    private final static int[] secondAdditionAtk = {5, 25, 100};
    private final static int[] baseHP = {5, 25, 100};
    private final static int[] baseAtk = {5, 25, 30};
    private String firstName;
    private String secondName;
    private String lastName;
    private int atk;
    private int hp;
    private int material;

    private Monster(String firstName, String secondName, String lastName, int hp, int atk) {
        this.atk = atk;
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
    }

    public Monster(Hero hero, Maze maze) {
        Random random = new Random();
        int first = random.nextInt(firstNames.length);
        int second = random.nextInt(secondNames.length);
        int last = random.nextInt(lastNames.length);
        hp = baseHP[last] + firstAdditionHP[first] + secondAdditionHP[second];
        atk = baseAtk[last] = firstAdditionAtk[first] = secondAdditionAtk[second];
        firstName = firstNames[first];
        secondName = secondNames[second];
        lastName = lastNames[last];
        if (hero.getAttackValue() != 0) hp += random.nextInt(hero.getAttackValue());
        if (hero.getPower() != 0) atk += random.nextInt(hero.getPower());
        hp += maze.getLev() * random.nextInt(hero.getHp()/2);
        material = random.nextInt(hp) / 2;
    }

    public int getAtk() {
        return atk;
    }

    public int getHp() {
        return hp;
    }

    public void addHp(int hp) {
        this.hp += hp;
    }

    public int getMaterial() {
        return material;
    }

    public String getName() {
        return firstName + "的" + secondName + "の" + lastName;
    }
}
