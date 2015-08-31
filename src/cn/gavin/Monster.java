package cn.gavin;

import java.util.Random;

/**
 * Created by gluo on 8/26/2015.
 */
public class Monster {
    private final static String[] firstNames = {"普通", "怪异", "稀有", "神奇"};
    private final static int[] firstAdditionHP = {15, 25, 1000, 2000};
    private final static int[] firstAdditionAtk = {3, 25, 400, 1800};
    private final static String[] secondNames = {"小", "中", "大"};
    private final static int[] secondAdditionHP = {15, 25, 100};
    private final static int[] secondAdditionAtk = {5, 25, 100};
    private final static String[] lastNames = {"蟑螂", "猪", "老鼠", "蛇", "野牛", "龟", "刺猬", "狼", "精灵", "僵尸", "骷髅", "龙", "作者"};
    private final static int[] baseHP = {3, 25, 75, 95, 115, 220, 280, 350, 380, 450, 530, 1000, 50000};
    private final static int[] baseAtk = {2, 15, 55, 75, 105, 200, 250, 310, 350, 400, 430, 1000, 50000};
    private String firstName;
    private String secondName;
    private String lastName;
    private int atk;
    private int hp;
    private int material;


    public static Monster getBoss(Maze maze, Hero hero) {
        Random random = new Random();
        Monster monster = new Monster("第" + maze.getLev() + "层", "守护", "者", hero.getUpperHp() * (random.nextInt(maze.getLev() + 1) + 1), random.nextInt(hero.getHp() + maze.getLev()));
        return monster;
    }

    private Monster(String firstName, String secondName, String lastName, int hp, int atk) {
        this.atk = atk;
        this.hp = hp;
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
    }

    public Monster(Hero hero, Maze maze) {
        Random random = new Random();
        int first = random.nextInt(maze.getLev() < firstNames.length ? maze.getLev() + 1 : firstNames.length);
        int second = random.nextInt(maze.getLev() < secondNames.length ? maze.getLev() + 1 : secondNames.length);
        int last = random.nextInt(maze.getLev() < lastNames.length ? maze.getLev() + 1 : lastNames.length);
        hp = baseHP[last] + firstAdditionHP[first] + secondAdditionHP[second];
        atk = baseAtk[last] + firstAdditionAtk[first] + secondAdditionAtk[second];
        firstName = firstNames[first];
        secondName = secondNames[second];
        lastName = lastNames[last];
        if (hero.getAttackValue() != 0) hp += random.nextInt(hero.getAttackValue() + 1);
        if (hero.getPower() != 0) atk += random.nextInt(hero.getPower() + +maze.getLev() + 1);
        hp += maze.getLev() * random.nextInt(hero.getUpperHp() / 2 + 1);
        material = random.nextInt(hp + 1) / 2 + 5;
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
        return firstName + "的" + secondName + lastName;
    }
}
