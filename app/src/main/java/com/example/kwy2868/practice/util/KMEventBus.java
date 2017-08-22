package com.example.kwy2868.practice.util;

import android.util.Log;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.EventBusException;

/**
 * EventBus Wrapper 클래스
 */
public class KMEventBus {
    public static void register(Object subscriber) {
        register(subscriber, 0);
    }

    public static void register(Object subscriber, int priority) {
        try {
            if (!EventBus.getDefault().isRegistered(subscriber)) {
                EventBus.getDefault().register(subscriber, priority);
            }
        } catch (EventBusException e) {
            Log.e("KMEventBus", "EventBus registration error", e);
        }
    }

    public static void unregister(Object subscriber) {
        try {
            if (EventBus.getDefault().isRegistered(subscriber)) {
                EventBus.getDefault().unregister(subscriber);
            }
        } catch (EventBusException e) {
            Log.e("KMEventBus", "EventBus unregistration error", e);
        }
    }

    public static void post(Object event) {
        try {
            EventBus.getDefault().post(event);
        } catch (EventBusException e) {
            Log.e("KMEventBus", "EventBus posting error", e);
        }
    }
}
