package com.dev.base.mvp.model.bus.support;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @Description: RxBus事件管理
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2016-12-19 15:07
 */
public class RxBus extends EventBase {

    private ConcurrentMap<Object, EventComposite> mEventCompositeMap = new ConcurrentHashMap<>();

    /**
     * 注册事件监听
     *
     * @param object
     */
    public void register(Object object) {
        if (object == null) {
            throw new NullPointerException("Object to register must not be null.");
        }
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        EventComposite subscriberMethods = EventFind.findAnnotatedSubscriberMethods(object, compositeDisposable);
        mEventCompositeMap.put(object, subscriberMethods);

        if (!STICKY_EVENT_MAP.isEmpty()) {//如果有粘性事件，则发送粘性事件
            subscriberMethods.subscriberSticky(STICKY_EVENT_MAP);
        }
    }

    /**
     * 取消事件监听
     *
     * @param object
     */
    public void unregister(Object object) {
        if (object == null) {
            throw new NullPointerException("Object to register must not be null.");
        }
        EventComposite subscriberMethods = mEventCompositeMap.get(object);
        if (subscriberMethods != null) {
            subscriberMethods.getCompositeDisposable().dispose();
        }
        mEventCompositeMap.remove(object);
    }

    /**
     * 发送普通事件
     *
     * @param event
     */
    public void post(Object event) {
        SUBJECT.onNext(event);
    }

    /**
     * 发送粘性事件
     *
     * @param event
     */
    public void postSticky(Object event) {
        STICKY_EVENT_MAP.put(event.getClass(), event);
        post(event);
    }
}
