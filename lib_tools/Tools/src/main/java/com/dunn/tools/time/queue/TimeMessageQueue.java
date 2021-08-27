package com.dunn.tools.time.queue;

import com.dunn.tools.log.LogUtil;
import com.dunn.tools.time.TimeAnalysis;
import com.dunn.tools.time.constant.TimeConstant;
import com.dunn.tools.time.bean.TimeBean;
import com.dunn.tools.time.temp.RemoteCommand;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Author:zhuyidian
 * Date:2021/8/27 14:25
 * Description:TimeMessageQueue
 */
public class TimeMessageQueue {
    private CopyOnWriteArrayList<ConcurrentHashMap<String,Object>> timeTaskList = new CopyOnWriteArrayList<>();

    public void TimeMessageQueue(){}

    public boolean addTimeTask(int cmdType, TimeBean bean, RemoteCommand command){
        if(bean==null) return false;
        if(command==null) return false;
        if(timeTaskList==null) timeTaskList = new CopyOnWriteArrayList<>();

        if(timeTaskList.isEmpty()) {
            ConcurrentHashMap<String,Object> map = new ConcurrentHashMap<>();
            map.put(TimeConstant.MAP_KEY_CMDTYPE,cmdType);
            map.put(TimeConstant.MAP_KEY_BEAN,bean);
            map.put(TimeConstant.MAP_KEY_COMMAND,command);
            switch (bean.getPlanType()){
                case TimeConstant.CMD_DELAY:
                    map.put(TimeConstant.MAP_KEY_TIME, TimeAnalysis.getDelayTime(bean.getDelay()));
                    break;
                case TimeConstant.CMD_DAY:
                    map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getDayTime(bean.getTime(),System.currentTimeMillis()));
                    break;
                case TimeConstant.CMD_WEEK:
                    map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getWeekTime(bean.getWeek(),System.currentTimeMillis()));
                    break;
                case TimeConstant.CMD_MONTH:
                    map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getMonthTime(bean.getMonth(),System.currentTimeMillis()));
                    break;
                default:
                    break;
            }
            timeTaskList.add(map);
            LogUtil.i("time", "add Time MessageQueue success cmdType="+cmdType);
            return true;
        }

        Iterator<ConcurrentHashMap<String,Object>> iter = timeTaskList.iterator();
        while (iter.hasNext()) {
            ConcurrentHashMap<String,Object> map = (ConcurrentHashMap<String,Object>) iter.next();
            if(map!=null && map.containsKey(TimeConstant.MAP_KEY_CMDTYPE)){
                int cmd = (Integer) map.get(TimeConstant.MAP_KEY_CMDTYPE);
                if(cmd == cmdType) {
                    LogUtil.i("time", "add Time MessageQueue is have !!!!!! cmdType="+cmdType);
                    return false;
                }
            }
        }

        ConcurrentHashMap<String,Object> map = new ConcurrentHashMap<>();
        map.put(TimeConstant.MAP_KEY_CMDTYPE,cmdType);
        map.put(TimeConstant.MAP_KEY_BEAN,bean);
        map.put(TimeConstant.MAP_KEY_COMMAND,command);
        switch (bean.getPlanType()){
            case TimeConstant.CMD_DELAY:
                map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getDelayTime(bean.getDelay()));
                break;
            case TimeConstant.CMD_DAY:
                map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getDayTime(bean.getTime(),System.currentTimeMillis()));
                break;
            case TimeConstant.CMD_WEEK:
                map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getWeekTime(bean.getWeek(),System.currentTimeMillis()));
                break;
            case TimeConstant.CMD_MONTH:
                map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getMonthTime(bean.getMonth(),System.currentTimeMillis()));
                break;
            default:
                break;
        }
        timeTaskList.add(map);
        LogUtil.i("time", "add Time MessageQueue success cmdType="+cmdType);
        return true;
    }

    public boolean removeTimeTask(int cmdType){
        if(timeTaskList==null) return false;
        if(timeTaskList.isEmpty()) return false;

        Iterator<ConcurrentHashMap<String,Object>> iter = timeTaskList.iterator();
        while (iter.hasNext()) {
            ConcurrentHashMap<String,Object> map = (ConcurrentHashMap<String,Object>) iter.next();
            if(map!=null && map.containsKey(TimeConstant.MAP_KEY_CMDTYPE)){
                int cmd = (Integer) map.get(TimeConstant.MAP_KEY_CMDTYPE);
                if(cmd == cmdType) {
                    //iter.remove();
                    timeTaskList.remove(map);
                    LogUtil.i("time", "Time MessageQueue is remove success cmdType="+cmdType);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean clearTimeTask(){
        if(timeTaskList==null) return false;
        if(timeTaskList.isEmpty()) return false;

        timeTaskList.clear();
        return true;
    }

    public void getNearestTimeTask(){
        if(timeTaskList==null) return;
        if(timeTaskList.isEmpty()) return;

        LogUtil.i("time", "#################adjust Time MessageQueue start##################queue size="+timeTaskList.size());
        Iterator<ConcurrentHashMap<String,Object>> iter = timeTaskList.iterator();
        while (iter.hasNext()) {
            ConcurrentHashMap<String,Object> map = (ConcurrentHashMap<String,Object>) iter.next();
            if(map==null) continue;
            if(!map.containsKey(TimeConstant.MAP_KEY_BEAN)) continue;
            TimeBean bean = (TimeBean) map.get(TimeConstant.MAP_KEY_BEAN);
            if(bean==null) continue;
            int cmdType = -100;
            if(map.containsKey(TimeConstant.MAP_KEY_COMMAND)) cmdType = (Integer) map.get(TimeConstant.MAP_KEY_CMDTYPE);

            switch (bean.getPlanType()){
                case TimeConstant.CMD_DELAY:
                    long delayTime = TimeAnalysis.getDelayTime(bean.getDelay());
                    if(delayTime<System.currentTimeMillis()){
                        //iter.remove();
                        timeTaskList.remove(map);
                        LogUtil.i("time", "Time MessageQueue is remove success cmdType="+cmdType);
                    }else{
                        map.put(TimeConstant.MAP_KEY_TIME,delayTime);
                    }
                    break;
                case TimeConstant.CMD_DAY:
                    map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getDayTime(bean.getTime(),System.currentTimeMillis()));
                    break;
                case TimeConstant.CMD_WEEK:
                    map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getWeekTime(bean.getWeek(),System.currentTimeMillis()));
                    break;
                case TimeConstant.CMD_MONTH:
                    map.put(TimeConstant.MAP_KEY_TIME,TimeAnalysis.getMonthTime(bean.getMonth(),System.currentTimeMillis()));
                    break;
                default:
                    break;
            }
        }

        Iterator<ConcurrentHashMap<String,Object>> iter1 = timeTaskList.iterator();
        while (iter1.hasNext()) {
            ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) iter1.next();
            if (map == null) continue;
            if(!map.containsKey(TimeConstant.MAP_KEY_TIME)) continue;
            long timeLong = (Long) map.get(TimeConstant.MAP_KEY_TIME);
            int cmdType = -100;
            if(map.containsKey(TimeConstant.MAP_KEY_COMMAND)) cmdType = (Integer) map.get(TimeConstant.MAP_KEY_CMDTYPE);
            LogUtil.i("time", "Time MessageQueue 1111 cmdType="+cmdType+", timeLong="+timeLong);
        }
        Collections.sort(timeTaskList, new Comparator<ConcurrentHashMap<String,Object>>() {
            @Override
            public int compare(ConcurrentHashMap<String,Object> t1, ConcurrentHashMap<String,Object> t2) {
                if(t1==null || t2==null) return 0;
                if(!t1.containsKey(TimeConstant.MAP_KEY_TIME) || !t2.containsKey(TimeConstant.MAP_KEY_TIME)) return 0;

                long t2Time = (long)t2.get(TimeConstant.MAP_KEY_TIME);
                long t1Time = (long)t1.get(TimeConstant.MAP_KEY_TIME);
                return t2Time > t1Time ? -1 : 1;
            }
        });
        Iterator<ConcurrentHashMap<String,Object>> iter2 = timeTaskList.iterator();
        while (iter2.hasNext()) {
            ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) iter2.next();
            if (map == null) continue;
            if(!map.containsKey(TimeConstant.MAP_KEY_TIME)) continue;
            long timeLong = (Long) map.get(TimeConstant.MAP_KEY_TIME);
            int cmdType = -100;
            if(map.containsKey(TimeConstant.MAP_KEY_COMMAND)) cmdType = (Integer) map.get(TimeConstant.MAP_KEY_CMDTYPE);
            LogUtil.i("time", "Time MessageQueue 2222 cmdType="+cmdType+", timeLong="+timeLong);
        }
        LogUtil.i("time", "#################adjust Time MessageQueue end##################queue size="+timeTaskList.size());
    }

    public void printfTimeTask(){
        if(timeTaskList==null) return;
        if(timeTaskList.isEmpty()) return;

        LogUtil.i("time", "-----------------Time MessageQueue printf start------------------");
        LogUtil.i("time", "size=" + timeTaskList.size());
        Iterator<ConcurrentHashMap<String,Object>> iter = timeTaskList.iterator();
        while (iter.hasNext()) {
            ConcurrentHashMap<String,Object> map = (ConcurrentHashMap<String,Object>) iter.next();
            if(map!=null){
                if(map.containsKey(TimeConstant.MAP_KEY_BEAN)){
                    TimeBean bean = (TimeBean) map.get(TimeConstant.MAP_KEY_BEAN);
                    if(bean!=null) {
                        LogUtil.i("time", "bean=" + bean);
                        LogUtil.i("time", "planType=" + bean.getPlanType());
                        LogUtil.i("time", "switch=" + bean.getSwitchX());
                        LogUtil.i("time", "volume=" + bean.getVolume());
                        LogUtil.i("time", "delay=" + bean.getDelay());
                        if (bean.getTime() != null) {
                            List<String> listTime = bean.getTime();
                            int size = listTime.size();
                            LogUtil.i("time", "time.size=" + size);
                            for (int i = 0; i < size; i++) {
                                LogUtil.i("time", "time.str=" + listTime.get(i));
                            }
                        } else {
                            LogUtil.i("time", "time=null");
                        }

                        if (bean.getWeek() != null) {
                            List<TimeBean.DayBean> listWeek = bean.getWeek();
                            int size = listWeek.size();
                            LogUtil.i("time", "week.size=" + size);
                            for (int i = 0; i < size; i++) {
                                LogUtil.i("time", "week.number=" + listWeek.get(i).getNumber() + ", week.time=" + listWeek.get(i).getTimeX());
                            }
                        } else {
                            LogUtil.i("time", "week=null");
                        }

                        if (bean.getMonth() != null) {
                            List<TimeBean.DayBean> listMonth = bean.getMonth();
                            int size = listMonth.size();
                            LogUtil.i("time", "month.size=" + size);
                            for (int i = 0; i < size; i++) {
                                LogUtil.i("time", "month.number=" + listMonth.get(i).getNumber() + ", month.time=" + listMonth.get(i).getTimeX());
                            }
                        } else {
                            LogUtil.i("time", "month=null");
                        }
                    }
                }
                if(map.containsKey(TimeConstant.MAP_KEY_CMDTYPE)){
                    int cmdType = (Integer)map.get(TimeConstant.MAP_KEY_CMDTYPE);
                    LogUtil.i("time", "cmdType=" + cmdType);
                }
                if(map.containsKey(TimeConstant.MAP_KEY_COMMAND)){
                    RemoteCommand command = (RemoteCommand) map.get(TimeConstant.MAP_KEY_COMMAND);
                    LogUtil.i("time", "command=" + command);
                }
            }
        }
        LogUtil.i("time", "-----------------Time MessageQueue printf end------------------");
    }
}
