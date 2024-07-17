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
import vn.ses.s3m.plus.batch.dto.DataInverter1;
import vn.ses.s3m.plus.batch.dto.DataInverter1View;
import vn.ses.s3m.plus.batch.dto.DataInverter1ViewCache;
import vn.ses.s3m.plus.batch.mapper.DataInverter1Mapper;
import vn.ses.s3m.plus.batch.mapper.DataInverter1ViewCacheMapper;
import vn.ses.s3m.plus.batch.mapper.DataInverter1ViewMapper;

@Service
public class DataInverter1ViewService {

    private static final Logger log = LoggerFactory.getLogger(DataInverter1ViewService.class);

    private Map<MultiKey, DataInverter1View> map = new HashMap<MultiKey, DataInverter1View>();

    private Map<Long, DataInverter1ViewCache> mapCache = new HashMap<Long, DataInverter1ViewCache>();

    @Autowired
    private DataInverter1Mapper dataInverter1Mapper;

    @Autowired
    private DataInverter1ViewMapper dataInverter1ViewMapper;

    @Autowired
    private DataInverter1ViewCacheMapper dataInverter1ViewCacheMapper;

    public boolean doProcess() {
        // ----- FLAG CHECK ALL CUSTOMER HAVE DATA OR NOT
        boolean isNoData = true;

        List<String> schemas = dataInverter1ViewMapper.getCustomerList("s3m_plus", "s3m_customer");
        for (String schema : schemas) {
            schema = "s3m_plus_customer_" + schema;

            log.info("!!!!![INVERTER]-CUSTOMER: [" + schema + "].........");

            // -----GET NEW DATA FROM DATABASE SINCE LASTTIME EXECUTOR-----
            log.info("!!!READING.........");
            DataInverter1View lastData = null;
            try {
                lastData = dataInverter1ViewMapper.selectInverterLastestTime(schema);
                if (lastData == null) {
                    log.error("!!!First record in s3m_data_inverter_1_view is not exist");
                    log.error(
                        "!!!Please insert the first record to [s3m_inverter_1_view] with [view_time] = 0 and [sent_date] = 2022");
                    continue;
                }
            } catch (Exception e) {
                log.error("!!!SCHEMA [" + schema + "] IS NOT EXIST");
                continue;
            }
            String lastTime = lastData.getViewTime();
            Integer tableYear = Integer.valueOf(lastData.getSentDate());
            String tableName = "s3m_data_inverter_1_" + tableYear;
            List<DataInverter1> datas = dataInverter1Mapper.selectNewRecordInverter(schema, tableName,
                lastTime != null ? lastTime : "0");
            if (datas == null || datas.size() == 0) {
                tableYear++;
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                while (year >= tableYear && (datas == null || datas.size() == 0)) {
                    tableName = "s3m_data_inverter_1_" + tableYear;
                    datas = dataInverter1Mapper.selectNewRecordInverter(schema, tableName, "0");
                    if (datas == null || datas.size() == 0) {
                        tableYear++;
                    }
                }
            }

            if (datas != null && datas.size() > 0) {
                // ----- UPDATE FLAG CUSTOMER HAVE DATA
                isNoData = false;
                List<DataInverter1View> dataInverter1Views = dataInverter1ViewMapper
                    .selectInverterByTypeLastTime(schema);

                if (dataInverter1Views.size() > 0) {
                    map = convertListToMap(dataInverter1Views);
                }
                List<DataInverter1ViewCache> cache = dataInverter1ViewCacheMapper.selectAllInverter(schema);
                mapCache = cache.stream()
                    .collect(Collectors.toMap(DataInverter1ViewCache::getDeviceId, Function.identity()));

                lastTime = datas.get(datas.size() - 1)
                    .getId()
                    .toString();

                // -----ADD NEW DATA TO OLD DATA OR CREATE NEW RECORD-----
                log.info("!!!PROCESSING.........");
                for (DataInverter1 data : datas) {
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

                    DataInverter1ViewCache epCache = mapCache.get(data.getDeviceId());
                    if (epCache == null) {
                        epCache = new DataInverter1ViewCache();
                        epCache.setDeviceId(data.getDeviceId());
                        epCache.setEp(data.getEp());
                    }

                    MultiKey multikey = new MultiKey(data.getDeviceId(), minute15, Constants.MINUTE_15);

                    DataInverter1View minuteData = map.get(multikey);
                    if (minuteData == null) {
                        minuteData = new DataInverter1View();
                        minuteData.setDeviceId(data.getDeviceId());
                        minuteData.setViewTime(minute15);
                        minuteData.setViewType(Constants.MINUTE_15);
                    }
                    minuteData = addData(minuteData, data, epCache.getEp());
                    map.put(multikey, minuteData);

                    multikey = new MultiKey(data.getDeviceId(), hour, Constants.HOUR);

                    DataInverter1View hourData = map.get(multikey);
                    if (hourData == null) {
                        hourData = new DataInverter1View();
                        hourData.setDeviceId(data.getDeviceId());
                        hourData.setViewTime(hour);
                        hourData.setViewType(Constants.HOUR);
                    }
                    hourData = addData(hourData, data, epCache.getEp());
                    map.put(multikey, hourData);

                    multikey = new MultiKey(data.getDeviceId(), day, Constants.DAY);

                    DataInverter1View dayData = map.get(multikey);
                    if (dayData == null) {
                        dayData = new DataInverter1View();
                        dayData.setDeviceId(data.getDeviceId());
                        dayData.setViewTime(day);
                        dayData.setViewType(Constants.DAY);

                        // log.info("!!!DEVICE: " + data.getDeviceId() + " ~ DAY: " + day);
                    }
                    dayData = addData(dayData, data, epCache.getEp());
                    map.put(multikey, dayData);

                    multikey = new MultiKey(data.getDeviceId(), month, Constants.MONTH);

                    DataInverter1View monthData = map.get(multikey);
                    if (monthData == null) {
                        monthData = new DataInverter1View();
                        monthData.setDeviceId(data.getDeviceId());
                        monthData.setViewTime(month);
                        monthData.setViewType(Constants.MONTH);
                    }
                    monthData = addData(monthData, data, epCache.getEp());
                    map.put(multikey, monthData);

                    multikey = new MultiKey(data.getDeviceId(), year, Constants.YEAR);

                    DataInverter1View yearData = map.get(multikey);
                    if (yearData == null) {
                        yearData = new DataInverter1View();
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
                List<DataInverter1View> itemData = map.values()
                    .stream()
                    .collect(Collectors.toList());
                List<DataInverter1View> saveList = new ArrayList<>();
                for (DataInverter1View data : itemData) {
                    if (data.getId() != null) {
                        dataInverter1ViewMapper.updateInverter(schema, data);
                    } else {
                        saveList.add(data);
                    }
                }
                if (saveList.size() > 0) {
                    dataInverter1ViewMapper.saveAllInverter(schema, saveList);
                }
                List<DataInverter1ViewCache> itemDataCache = mapCache.values()
                    .stream()
                    .collect(Collectors.toList());
                List<DataInverter1ViewCache> saveListCache = new ArrayList<>();
                for (DataInverter1ViewCache data : itemDataCache) {
                    if (data.getId() != null) {
                        dataInverter1ViewCacheMapper.updateInverter(schema, data);
                    } else {
                        saveListCache.add(data);
                    }
                }
                if (saveListCache.size() > 0) {
                    dataInverter1ViewCacheMapper.saveAllInverter(schema, saveListCache);
                }
                log.info("[SAVE] UPDATE LASTTIME");
                dataInverter1ViewMapper.updateLastTimeInverter(schema, lastTime, tableYear.toString());
            }
            log.info("!!![DONE]");
        }
        return isNoData;
    }

    public Map<MultiKey, DataInverter1View> convertListToMap(List<DataInverter1View> list) {

        Map<MultiKey, DataInverter1View> mapData = new HashMap<MultiKey, DataInverter1View>();

        for (DataInverter1View data : list) {
            MultiKey multikey = new MultiKey(data.getDeviceId(), data.getViewTime(), data.getViewType());
            mapData.put(multikey, data);
        }

        return mapData;
    }

    public DataInverter1View addData(DataInverter1View dataInverter1View, DataInverter1 dataInverter1, Float epCache) {
        dataInverter1View.setPtotal(dataInverter1.getPtotal());
        dataInverter1View.setPdc(dataInverter1.getPdc());

        Float EpView = dataInverter1View.getEp() != null && dataInverter1View.getEp() > 0
            ? dataInverter1View.getEp()
            : 0;
        Float Ep = dataInverter1.getEp() != null && dataInverter1.getEp() > 0 ? dataInverter1.getEp() : 0;
        Float EpCache = epCache != null && epCache > 0 ? epCache : 0;

        dataInverter1View.setEp(EpView + (Ep - EpCache));
        return dataInverter1View;
    }
}
