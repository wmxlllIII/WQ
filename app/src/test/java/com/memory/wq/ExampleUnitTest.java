package com.memory.wq;

import org.junit.Test;

import static org.junit.Assert.*;

import com.memory.wq.utils.TimeUtils;

import java.util.Random;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void convertTime_moreThanOneDay() {
        long now = System.currentTimeMillis() / 1000;
        Random random = new Random();
        long randomOffset = random.nextInt(3 * 24 * 60 * 60);
        long timestamp = now - randomOffset;
        String result = TimeUtils.convertTime(timestamp);
        assertTrue(result.matches("\\d+天前|\\d+小时前"));
    }
    @Test
    public void convertTime_moreThanOneHour() {
        long now = System.currentTimeMillis() / 1000;
        long timestamp = now - 3 * 60 * 60 - 5;

        String result = TimeUtils.convertTime(timestamp);

        assertEquals("3小时前", result);
    }

    @Test
    public void convertTime_moreThanOneMinute() {
        long now = System.currentTimeMillis() / 1000;
        long timestamp = now - 10 * 60 - 3;

        String result = TimeUtils.convertTime(timestamp);

        assertEquals("10分钟前", result);
    }

    @Test
    public void convertTime_justNow() {
        long now = System.currentTimeMillis() / 1000;
        long timestamp = now - 20;

        String result = TimeUtils.convertTime(timestamp);

        assertEquals("刚刚", result);
    }
}