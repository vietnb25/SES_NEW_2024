package vn.ses.s3m.plus.batch.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.batch.common.Constants;
import vn.ses.s3m.plus.batch.dto.DataLoadFrame1;
import vn.ses.s3m.plus.batch.dto.DataLoadFrame1View;
import vn.ses.s3m.plus.batch.dto.DataLoadFrame1ViewCache;
import vn.ses.s3m.plus.batch.mapper.DataLoadFrame1Mapper;
import vn.ses.s3m.plus.batch.mapper.DataLoadFrame1ViewCacheMapper;
import vn.ses.s3m.plus.batch.mapper.DataLoadFrame1ViewMapper;

@Service
public class DataLoadFrame1ViewService {

    private static final Logger log = LoggerFactory.getLogger(DataLoadFrame1ViewService.class);

    private Map<MultiKey, DataLoadFrame1View> map = new HashMap<MultiKey, DataLoadFrame1View>();

    private Map<Long, DataLoadFrame1ViewCache> mapCache = new HashMap<Long, DataLoadFrame1ViewCache>();

    @Autowired
    private DataLoadFrame1Mapper dataLoadFrame1Mapper;

    @Autowired
    private DataLoadFrame1ViewMapper dataLoadFrame1ViewMapper;

    @Autowired
    private DataLoadFrame1ViewCacheMapper dataLoadFrame1ViewCacheMapper;

    public boolean doProcess() {
        // ----- FLAG CHECK ALL CUSTOMER HAVE DATA OR NOT
        boolean isNoData = true;

        List<String> schemas = dataLoadFrame1ViewMapper.getCustomerList("s3m_plus", "s3m_customer");
        for (String schema : schemas) {
            schema = "s3m_plus_customer_" + schema;

            log.info("!!!!![LOAD-FRAM1]-CUSTOMER: [" + schema + "].........");

            // -----GET NEW DATA FROM DATABASE SINCE LASTTIME EXECUTOR-----
            log.info("!!!READING.........");
            DataLoadFrame1View lastData = null;
            try {
                lastData = dataLoadFrame1ViewMapper.selectLastestTime(schema);
                if (lastData == null) {
                    log.error("!!!First record in s3m_data_load_frame_1_view is not exist");
                    log.error(
                        "!!!Please insert the first record to [s3m_data_load_frame_1_view] with [view_time] = 0 and [sent_date] = 2022");
                    continue;
                }
            } catch (Exception e) {
                log.error("!!!SCHEMA [" + schema + "] IS NOT EXIST");
                continue;
            }
            String lastTime = lastData.getViewTime();
            Integer tableYear = Integer.valueOf(lastData.getSentDate());
            String tableName = "s3m_data_meter_1_" + tableYear;
            List<DataLoadFrame1> datas = dataLoadFrame1Mapper.selectNewRecord(schema, tableName,
                lastTime != null ? lastTime : "0");
            if (datas == null || datas.size() == 0) {
                tableYear++;
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                while (year >= tableYear && (datas == null || datas.size() == 0)) {
                    tableName = "s3m_data_meter_1_" + tableYear;
                    datas = dataLoadFrame1Mapper.selectNewRecord(schema, tableName, "0");
                    if (datas == null || datas.size() == 0) {
                        tableYear++;
                    }
                }
            }

            if (datas != null && datas.size() > 0) {
                // ----- UPDATE FLAG CUSTOMER HAVE DATA
                isNoData = false;
                List<DataLoadFrame1View> dataLoadFrame1Views = dataLoadFrame1ViewMapper.selectByTypeLastTime(schema);

                if (dataLoadFrame1Views.size() > 0) {
                    map = convertListToMap(dataLoadFrame1Views);
                }
                List<DataLoadFrame1ViewCache> cache = dataLoadFrame1ViewCacheMapper.selectAll(schema);
                mapCache = cache.stream()
                    .collect(Collectors.toMap(DataLoadFrame1ViewCache::getDeviceId, Function.identity()));

                lastTime = datas.get(datas.size() - 1)
                    .getId()
                    .toString();

                // -----ADD NEW DATA TO OLD DATA OR CREATE NEW RECORD-----
                log.info("!!!PROCESSING.........");
                for (DataLoadFrame1 data : datas) {
                    String sentDate = data.getSentDate();
                    String year = sentDate.substring(0, 4);
                    String month = sentDate.substring(0, 7);
                    String day = sentDate.substring(0, 10);
                    String hour = sentDate.substring(0, 13) + ":00:00";
                    String minute15 = sentDate.substring(14, 16);
                    int minute15Int = Integer.valueOf(minute15);
                    if (minute15Int < 15) {
                        minute15 = sentDate.substring(0, 14) + "00:00";
                    } else if (minute15Int < 30) {
                        minute15 = sentDate.substring(0, 14) + "15:00";
                    } else if (minute15Int < 45) {
                        minute15 = sentDate.substring(0, 14) + "30:00";
                    } else if (minute15Int <= 59) {
                        minute15 = sentDate.substring(0, 14) + "45:00";
                    }

                    DataLoadFrame1ViewCache epCache = mapCache.get(data.getDeviceId());
                    if (epCache == null) {
                        epCache = new DataLoadFrame1ViewCache();
                        epCache.setDeviceId(data.getDeviceId());
                        epCache.setEp(data.getEp());
                    }

                    MultiKey multikey = new MultiKey(data.getDeviceId(), minute15, Constants.MINUTE_15);

                    DataLoadFrame1View minuteData = map.get(multikey);
                    if (minuteData == null) {
                        minuteData = new DataLoadFrame1View();
                        minuteData.setDeviceId(data.getDeviceId());
                        minuteData.setViewTime(minute15);
                        minuteData.setViewType(Constants.MINUTE_15);
                    }
                    minuteData = addData(minuteData, data, epCache.getEp());
                    map.put(multikey, minuteData);

                    multikey = new MultiKey(data.getDeviceId(), hour, Constants.HOUR);

                    DataLoadFrame1View hourData = map.get(multikey);
                    if (hourData == null) {
                        hourData = new DataLoadFrame1View();
                        hourData.setDeviceId(data.getDeviceId());
                        hourData.setViewTime(hour);
                        hourData.setViewType(Constants.HOUR);
                    }
                    hourData = addData(hourData, data, epCache.getEp());
                    map.put(multikey, hourData);

                    multikey = new MultiKey(data.getDeviceId(), day, Constants.DAY);

                    DataLoadFrame1View dayData = map.get(multikey);
                    if (dayData == null) {
                        dayData = new DataLoadFrame1View();
                        dayData.setDeviceId(data.getDeviceId());
                        dayData.setViewTime(day);
                        dayData.setViewType(Constants.DAY);

                        // log.info("!!!DEVICE: " + data.getDeviceId() + " ~ DAY: " + day);
                    }
                    dayData = addData(dayData, data, epCache.getEp());
                    map.put(multikey, dayData);

                    multikey = new MultiKey(data.getDeviceId(), month, Constants.MONTH);

                    DataLoadFrame1View monthData = map.get(multikey);
                    if (monthData == null) {
                        monthData = new DataLoadFrame1View();
                        monthData.setDeviceId(data.getDeviceId());
                        monthData.setViewTime(month);
                        monthData.setViewType(Constants.MONTH);
                    }
                    monthData = addData(monthData, data, epCache.getEp());
                    map.put(multikey, monthData);

                    multikey = new MultiKey(data.getDeviceId(), year, Constants.YEAR);

                    DataLoadFrame1View yearData = map.get(multikey);
                    if (yearData == null) {
                        yearData = new DataLoadFrame1View();
                        yearData.setDeviceId(data.getDeviceId());
                        yearData.setViewTime(year);
                        yearData.setViewType(Constants.YEAR);
                    }
                    yearData = addData(yearData, data, epCache.getEp());
                    map.put(multikey, yearData);

                    epCache.setEp(data.getEp());
                    mapCache.put(data.getDeviceId(), epCache);
                }

                // -----SAVE DATA-----
                log.info("!!!WRITING.........");
                List<DataLoadFrame1View> itemData = map.values()
                    .stream()
                    .collect(Collectors.toList());
                List<DataLoadFrame1View> saveList = new ArrayList<>();
                for (DataLoadFrame1View data : itemData) {
                    if (data.getId() != null) {
                        dataLoadFrame1ViewMapper.update(schema, data);
                    } else {
                        saveList.add(data);
                    }
                }
                if (saveList.size() > 0) {
                    dataLoadFrame1ViewMapper.saveAll(schema, saveList);
                }
                List<DataLoadFrame1ViewCache> itemDataCache = mapCache.values()
                    .stream()
                    .collect(Collectors.toList());
                List<DataLoadFrame1ViewCache> saveListCache = new ArrayList<>();
                for (DataLoadFrame1ViewCache data : itemDataCache) {
                    if (data.getId() != null) {
                        dataLoadFrame1ViewCacheMapper.update(schema, data);
                    } else {
                        saveListCache.add(data);
                    }
                }
                if (saveListCache.size() > 0) {
                    dataLoadFrame1ViewCacheMapper.saveAll(schema, saveListCache);
                }

                log.info("[SAVE] UPDATE LASTTIME");
                dataLoadFrame1ViewMapper.updateLastTime(schema, lastTime, tableYear.toString());
            }
            log.info("!!![DONE]");
        }
        return isNoData;
    }

    public Map<MultiKey, DataLoadFrame1View> convertListToMap(List<DataLoadFrame1View> list) {

        Map<MultiKey, DataLoadFrame1View> mapData = new HashMap<MultiKey, DataLoadFrame1View>();

        for (DataLoadFrame1View data : list) {
            MultiKey multikey = new MultiKey(data.getDeviceId(), data.getViewTime(), data.getViewType());
            mapData.put(multikey, data);
        }

        return mapData;
    }

    public DataLoadFrame1View addData(DataLoadFrame1View dataLoadFrame1View, DataLoadFrame1 dataLoadFrame1,
        Integer epCache) {
        dataLoadFrame1View.setPTotal(dataLoadFrame1.getPTotal());
        dataLoadFrame1View.setEp(dataLoadFrame1View.getEp() + (dataLoadFrame1.getEp() - epCache));
        return dataLoadFrame1View;
    }
}
