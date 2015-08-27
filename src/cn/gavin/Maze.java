package cn.gavin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by gluo on 8/26/2015.
 */
public class Maze {
    private Hero hero;
    private int level;
    private Random random = new Random();
    public Maze(Hero hero){
        this.hero = hero;
    }

    public List<String> move(){
        List<String> result = new ArrayList<String>();
        if(random.nextBoolean()){
            Monster monster = new Monster(hero, this);
            result.add(hero.getName() + "遇到了" + monster.getName());
            boolean atk = hero.getAgility() > monster.getHp()/2 || random.nextBoolean();
            while(monster.getHp()>0 && hero.getHp() >0){
                if(atk){
                    Skill skill = hero.useSkill();
                    if(skill!=null){
                        result.addAll(skill.release(hero, monster));
                    }else {
                        monster.addHp(-(hero.getAttackValue()));
                        result.add(hero.getName() + "攻击了" + monster.getName() + "，造成了" + hero.getAttackValue() + "点伤害。");
                    }
                }else{
                    hero.addHp(-(monster.getAtk()));
                    result.add(monster.getName() + "攻击了" + hero.getName() + "，造成了" + monster.getAtk() + "点伤害。");
                }
                atk = !atk;
            }
            if(monster.getHp() <=0){
                result.add(hero.getName() + "击败了" + monster.getName() + "， 获得了" + monster.getMaterial() + "份锻造材料。");
                hero.addMaterial(monster.getMaterial());
            }else{
                result.add(hero.getName() + "被" + monster.getName() + "打败了，回到迷宫第一层。");
                this.level = 1;
                hero.restore();
            }
        }else{
            level ++;
            int point = level + random.nextInt(level * 2);
            result.add(hero.getName() + "进入了"+ level + "层迷宫， 获得了" + point + "点数奖励");
            if(level>hero.getMaxMazeLev()){
                hero.addMaxMazeLev();
            }
            hero.addPoint(point);
        }
        return result;
    }

    public int getLev() {
        return level;
    }
}
