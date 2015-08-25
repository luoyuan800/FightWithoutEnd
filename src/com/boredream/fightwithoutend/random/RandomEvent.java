package com.boredream.fightwithoutend.random;

import java.util.Random;

/**
 * Created by gluo on 8/25/2015.
 */
public class RandomEvent {
    private static String[] events = {"看邮件", "喝水", "上厕所", "发呆", "工作"};
    public static Event getEvent(){
        int index = new Random().nextInt(events.length);
        Event event = new Event();
        event.setDesc(events[index]);
        return event;
    }
}
