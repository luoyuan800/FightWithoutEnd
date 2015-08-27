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
            result.add(hero.getName() + "������" + monster.getName());
            boolean atk = hero.getAgility() > monster.getHp()/2 || random.nextBoolean();
            while(monster.getHp()>0 && hero.getHp() >0){
                if(atk){
                    Skill skill = hero.useSkill();
                    if(skill!=null){
                        result.addAll(skill.release(hero, monster));
                    }else {
                        monster.addHp(-(hero.getAttackValue()));
                        result.add(hero.getName() + "������" + monster.getName() + "�������" + hero.getAttackValue() + "���˺���");
                    }
                }else{
                    hero.addHp(-(monster.getAtk()));
                    result.add(monster.getName() + "������" + hero.getName() + "�������" + monster.getAtk() + "���˺���");
                }
                atk = !atk;
            }
            if(monster.getHp() <=0){
                result.add(hero.getName() + "������" + monster.getName() + "�� �����" + monster.getMaterial() + "�ݶ�����ϡ�");
                hero.addMaterial(monster.getMaterial());
            }else{
                result.add(hero.getName() + "��" + monster.getName() + "����ˣ��ص��Թ���һ�㡣");
                this.level = 1;
                hero.restore();
            }
        }else{
            level ++;
            int point = level + random.nextInt(level * 2);
            result.add(hero.getName() + "������"+ level + "���Թ��� �����" + point + "��������");
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
