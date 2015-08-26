
package cn.gavin;

import java.util.ArrayList;
import java.util.List;

public class Skill {

    private int id;
    private String name;
    private int type;
    private int occurProbability; // ��������,�ٷֱ���ֵ���ƴ���
    private int level;
    private int skillEffect; // ���ܾ���Ч��
    private double harm; //�˺��ӳ�
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
     * ��ù����ӳ�ָ�����ٷֱȣ�
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
        Skill skill1 = new Skill(1, "����һ��", 200.0, 10);
        Skill skill2 = new Skill(2, "Ⱥ�幥��", 0.80, 7);
        skill2.isGroup = true;
        Skill skill3 = new Skill(3, "�����ָ�", 20.0, 8);
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
