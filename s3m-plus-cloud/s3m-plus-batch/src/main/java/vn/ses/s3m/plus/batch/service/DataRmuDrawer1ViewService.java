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
import vn.ses.s3m.plus.batch.dto.DataRmuDarwer1ViewCache;
import vn.ses.s3m.plus.batch.dto.DataRmuDrawer1;
import vn.ses.s3m.plus.batch.dto.DataRmuDrawer1View;
import vn.ses.s3m.plus.batch.mapper.DataRmuDrawer1Mapper;
import vn.ses.s3m.plus.batch.mapper.DataRmuDrawer1ViewCacheMapper;
import vn.ses.s3m.plus.batch.mapper.DataRmuDrawer1ViewMapper;

@Service
public class DataRmuDrawer1ViewService {

    private static final Logger log = LoggerFactory.getLogger(DataRmuDrawer1ViewService.class);

    private Map<MultiKey, DataRmuDrawer1View> map = new HashMap<MultiKey, DataRmuDrawer1View>();

    private Map<Long, DataRmuDarwer1ViewCache> mapCache = new HashMap<Long, DataRmuDarwer1ViewCache>();

    @Autowired
    private DataRmuDrawer1Mapper dataRmuDrawer1Mapper;

    @Autowired
    private DataRmuDrawer1ViewMapper dataRmuDrawer1ViewMapper;

    @Autowired
    private DataRmuDrawer1ViewCacheMapper dataRmuDrawer1ViewCacheMapper;

    public boolean doProcess() {
        // ----- FLAG CHECK ALL CUSTOMER HAVE DATA OR NOT
        boolean isNoData = true;

        List<String> schemas = dataRmuDrawer1ViewMapper.getCustomerList("s3m_plus", "s3m_customer");
        for (String schema : schemas) {
            schema = "s3m_plus_customer_" + schema;

            log.info("!!!!![RMU-DRAWER1]-CUSTOMER: [" + schema + "].........");

            // -----GET NEW DATA FROM DATABASE SINCE LASTTIME EXECUTOR-----
            log.info("!!!READING.........");
            DataRmuDrawer1View lastData = null;
            try {
                lastData = dataRmuDrawer1ViewMapper.selectLastestTime(schema);
                if (lastData == null) {
                    log.error("!!!First record in s3m_data_rmu_drawer_1_view is not exist");
                    log.error(
                        "!!!Please insert the first record to [s3m_data_rmu_drawer_1_view] with [view_time] = 0 and [sent_date] = 2023");
                    continue;
                }
            } catch (Exception e) {
                log.error("!!!SCHEMA [" + schema + "] IS NOT EXIST");
                continue;
            }
            String lastTime = lastData.getViewTime();
            Integer tableYear = Integer.valueOf(lastData.getSentDate());
            String tableName = "s3m_data_rmu_drawer_1_" + tableYear;
            List<DataRmuDrawer1> datas = dataRmuDrawer1Mapper.selectNewRecord(schema, tableName, lastTime != null
                ? lastTime
                : "0");
            if (datas == null || datas.size() == 0) {
                tableYear++;
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                while (year >= tableYear && (datas == null || datas.size() == 0)) {
                    tableName = "s3m_data_rmu_drawer_1_" + tableYear;
                    datas = dataRmuDrawer1Mapper.selectNewRecord(schema, tableName, "0");
                    if (datas == null || datas.size() == 0) {
                        tableYear++;
                    }
                }
            }

            if (datas != null && datas.size() > 0) {
                // ----- UPDATE FLAG CUSTOMER HAVE DATA
                isNoData = false;
                List<DataRmuDrawer1View> dataRmuDrawer1Views = dataRmuDrawer1ViewMapper.selectByTypeLastTime(schema);

                if (dataRmuDrawer1Views.size() > 0) {
                    map = convertListToMap(dataRmuDrawer1Views);
                }
                List<DataRmuDarwer1ViewCache> cache = dataRmuDrawer1ViewCacheMapper.selectAll(schema);
                mapCache = cache.stream()
                    .collect(Collectors.toMap(DataRmuDarwer1ViewCache::getDeviceId, Function.identity()));

                lastTime = datas.get(datas.size() - 1)
                    .getId()
                    .toString();

                // -----ADD NEW DATA TO OLD DATA OR CREATE NEW RECORD-----
                log.info("!!!PROCESSING.........");
                for (DataRmuDrawer1 data : datas) {
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

                    DataRmuDarwer1ViewCache epCache = mapCache.get(data.getDeviceId());
                    if (epCache == null) {
                        epCache = new DataRmuDarwer1ViewCache();
                        epCache.setDeviceId(data.getDeviceId());
                        epCache.setEp(data.getEp());
                    }

                    MultiKey multikey = new MultiKey(data.getDeviceId(), minute15, Constants.MINUTE_15);

                    DataRmuDrawer1View minuteData = map.get(multikey);
                    if (minuteData == null) {
                        minuteData = new DataRmuDrawer1View();
                        minuteData.setDeviceId(data.getDeviceId());
                        minuteData.setViewTime(minute15);
                        minuteData.setViewType(Constants.MINUTE_15);
                    }
                    minuteData = addData(minuteData, data, epCache.getEp());
                    map.put(multikey, minuteData);

                    multikey = new MultiKey(data.getDeviceId(), hour, Constants.HOUR);

                    DataRmuDrawer1View hourData = map.get(multikey);
                    if (hourData == null) {
                        hourData = new DataRmuDrawer1View();
                        hourData.setDeviceId(data.getDeviceId());
                        hourData.setViewTime(hour);
                        hourData.setViewType(Constants.HOUR);
                    }
                    hourData = addData(hourData, data, epCache.getEp());
                    map.put(multikey, hourData);

                    multikey = new MultiKey(data.getDeviceId(), day, Constants.DAY);

                    DataRmuDrawer1View dayData = map.get(multikey);
                    if (dayData == null) {
                        dayData = new DataRmuDrawer1View();
                        dayData.setDeviceId(data.getDeviceId());
                        dayData.setViewTime(day);
                        dayData.setViewType(Constants.DAY);

                        // log.info("!!!DEVICE: " + data.getDeviceId() + " ~ DAY: " + day);
                    }
                    dayData = addData(dayData, data, epCache.getEp());
                    map.put(multikey, dayData);

                    multikey = new MultiKey(data.getDeviceId(), month, Constants.MONTH);

                    DataRmuDrawer1View monthData = map.get(multikey);
                    if (monthData == null) {
                        monthData = new DataRmuDrawer1View();
                        monthData.setDeviceId(data.getDeviceId());
                        monthData.setViewTime(month);
                        monthData.setViewType(Constants.MONTH);
                    }
                    monthData = addData(monthData, data, epCache.getEp());
                    map.put(multikey, monthData);

                    multikey = new MultiKey(data.getDeviceId(), year, Constants.YEAR);

                    DataRmuDrawer1View yearData = map.get(multikey);
                    if (yearData == null) {
                        yearData = new DataRmuDrawer1View();
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
                List<DataRmuDrawer1View> itemData = map.values()
                    .stream()
                    .collect(Collectors.toList());
                List<DataRmuDrawer1View> saveList = new ArrayList<>();
                for (DataRmuDrawer1View data : itemData) {
                    if (data.getId() != null) {
                        dataRmuDrawer1ViewMapper.update(schema, data);
                    } else {
                        saveList.add(data);
                    }
                }
                if (saveList.size() > 0) {
                    dataRmuDrawer1ViewMapper.saveAll(schema, saveList);
                }
                List<DataRmuDarwer1ViewCache> itemDataCache = mapCache.values()
                    .stream()
                    .collect(Collectors.toList());
                List<DataRmuDarwer1ViewCache> saveListCache = new ArrayList<>();
                for (DataRmuDarwer1ViewCache data : itemDataCache) {
                    if (data.getId() != null) {
                        dataRmuDrawer1ViewCacheMapper.update(schema, data);
                    } else {
                        saveListCache.add(data);
                    }
                }
                if (saveListCache.size() > 0) {
                    dataRmuDrawer1ViewCacheMapper.saveAll(schema, saveListCache);
                }

                log.info("[SAVE] UPDATE LASTTIME");
                dataRmuDrawer1ViewMapper.updateLastTime(schema, lastTime, tableYear.toString());
            }
            log.info("!!![DONE]");
        }
        return isNoData;
    }

    public Map<MultiKey, DataRmuDrawer1View> convertListToMap(List<DataRmuDrawer1View> list) {

        Map<MultiKey, DataRmuDrawer1View> mapData = new HashMap<MultiKey, DataRmuDrawer1View>();

        for (DataRmuDrawer1View data : list) {
            MultiKey multikey = new MultiKey(data.getDeviceId(), data.getViewTime(), data.getViewType());
            mapData.put(multikey, data);
        }

        return mapData;
    }

    public DataRmuDrawer1View addData(DataRmuDrawer1View dataRmuDrawere1View, DataRmuDrawer1 dataRmuDrawer1,
        Long epCache) {
        dataRmuDrawere1View.setPTotal(dataRmuDrawer1.getPTotal());
        dataRmuDrawere1View.setEp(dataRmuDrawere1View.getEp() != null
            ? dataRmuDrawere1View.getEp() + (dataRmuDrawer1.getEp() - epCache)
            : 0 + (dataRmuDrawer1.getEp() - epCache));
        return dataRmuDrawere1View;
    }
}
