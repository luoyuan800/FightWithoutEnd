package com.boredream.fightwithoutend.random;

import java.util.Random;

/**
 * Created by gluo on 8/25/2015.
 */
public class RandomEvent {
    private static String[] events = {"���ʼ�", "��ˮ", "�ϲ���", "����", "����"};
    public static Event getEvent(){
        int index = new Random().nextInt(events.length);
        Event event = new Event();
        event.setDesc(events[index]);
        return event;
    }
}
