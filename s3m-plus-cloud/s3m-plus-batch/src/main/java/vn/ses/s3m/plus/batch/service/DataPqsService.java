package vn.ses.s3m.plus.batch.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import vn.ses.s3m.plus.batch.dto.DataCombiner1;
import vn.ses.s3m.plus.batch.dto.DataFlow;
import vn.ses.s3m.plus.batch.dto.DataInverter1;
import vn.ses.s3m.plus.batch.dto.DataLoadFrame1;
import vn.ses.s3m.plus.batch.dto.DataPqs;
import vn.ses.s3m.plus.batch.dto.DataRmuDrawer1;
import vn.ses.s3m.plus.batch.dto.Setting;
import vn.ses.s3m.plus.batch.mapper.DataCombiner1Mapper;
import vn.ses.s3m.plus.batch.mapper.DataFlowMapper;
import vn.ses.s3m.plus.batch.mapper.DataInverter1Mapper;
import vn.ses.s3m.plus.batch.mapper.DataLoadFrame1Mapper;
import vn.ses.s3m.plus.batch.mapper.DataPqsMapper;
import vn.ses.s3m.plus.batch.mapper.DataRmuDrawer1Mapper;
import vn.ses.s3m.plus.batch.mapper.SettingMapper;

@Service
public class DataPqsService {

    private static final Logger log = LoggerFactory.getLogger(DataPqsService.class);

    private Map<MultiKey, DataPqs> map = new HashMap<MultiKey, DataPqs>();

    private Map<Long, DataPqs> mapCache = new HashMap<Long, DataPqs>();

    @Autowired
    private DataLoadFrame1Mapper dataLoadFrame1Mapper;

    @Autowired
    private DataInverter1Mapper dataInverter1Mapper;

    @Autowired
    private DataFlowMapper dataFlowMapper;

    @Autowired
    private DataCombiner1Mapper dataCombiner1Mapper;

    @Autowired
    private DataRmuDrawer1Mapper dataRmuDrawer1Mapper;

    @Autowired
    private DataPqsMapper dataPqsMapper;

    @Autowired
    private SettingMapper settingMapper;

    public boolean doProcess() {
        // ----- FLAG CHECK ALL CUSTOMER HAVE DATA OR NOT
        boolean isNoData = true;

        List<String> schemas = dataPqsMapper.getCustomerList("s3m_plus", "s3m_customer");
        processMeterFrame1(schemas, isNoData);
        processInverter1(schemas, isNoData);
        processFlowSensor(schemas, isNoData);
        return isNoData;
    }

    public void processMeterFrame1(List<String> schemas, boolean isNoData) {
        for (String schema : schemas) {
            schema = "s3m_plus_customer_" + schema;
            // -----GET NEW DATA FROM DATABASE SINCE LASTTIME EXECUTOR-----
            log.info("!!!READING PQS.........");
            DataPqs lastData = null;
            try {
                lastData = dataPqsMapper.selectLastestTime(schema, Constants.Device_type.METER);

                if (lastData == null) {
                    log.warn("!!!First record in s3m_data_pqs is not exist");
                    Calendar instance = Calendar.getInstance();
                    int year = instance.get(Calendar.YEAR);
                    String tableName = "s3m_data_meter_1_" + year;
                    DataLoadFrame1 newData = dataLoadFrame1Mapper.selectNewestRecord(schema, tableName);
                    lastData = new DataPqs();
                    lastData.setViewTime(newData.getId()
                        .toString());
                    lastData.setSentDate(newData.getSentDate()
                        .substring(0, 4));
                    lastData.setDeviceType(Constants.Device_type.METER);
                    lastData.setViewType(Constants.System_type.NUMBER_0);
                    dataPqsMapper.insertFirstRecord(schema, lastData);
                    log.info("!!!First record in s3m_data_pqs is inserted..");
                    continue;
                }
                log.info("!!!SCHEMA [" + schema + "] IS EXIST");
            } catch (Exception e) {
                log.error("!!!SCHEMA [" + schema + "] IS NOT EXIST");
                continue;
            }
            String lastTime = lastData.getViewTime();
            Integer tableYear = Integer.valueOf(lastData.getSentDate());
            String tableName = "s3m_data_meter_1_" + tableYear;
            List<DataLoadFrame1> dataLoadFrame1 = dataLoadFrame1Mapper.selectNewRecord(schema, tableName,
                lastTime != null ? lastTime : "0");

            if (dataLoadFrame1 == null || dataLoadFrame1.size() == 0) {
                tableYear++;
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                while (year >= tableYear && (dataLoadFrame1 == null || dataLoadFrame1.size() == 0)) {
                    tableName = "s3m_data_meter_1_" + tableYear;
                    dataLoadFrame1 = dataLoadFrame1Mapper.selectNewRecord(schema, tableName, "0");
                    if (dataLoadFrame1 == null || dataLoadFrame1.size() == 0) {
                        tableYear++;
                    }
                }
            }

            if (dataLoadFrame1 != null && dataLoadFrame1.size() > 0) {
                // ----- UPDATE FLAG CUSTOMER HAVE DATA
                isNoData = false;
                List<DataPqs> dataPqss = dataPqsMapper.selectByTypeLastTime(schema);
                if (dataPqss.size() > 0) {
                    map = convertListToMap(dataPqss);
                }
                log.info("Size of selectByTypeLastTime: " + dataPqss.size());
                List<DataPqs> cache = dataPqsMapper.selectAllCache(schema, Constants.System_type.LOAD_TYPE.METER);
                mapCache = cache.stream()
                    .collect(Collectors.toMap(DataPqs::getDeviceId, Function.identity()));

                lastTime = dataLoadFrame1.get(dataLoadFrame1.size() - 1)
                    .getId()
                    .toString();

                // -----ADD NEW DATA TO OLD DATA OR CREATE NEW RECORD-----
                log.info("!!!PROCESSING.........");
                for (DataLoadFrame1 data : dataLoadFrame1) {
                    String sentDate = data.getSentDate();
                    String year = sentDate.substring(0, 4);
                    String month = sentDate.substring(0, 7);
                    String day = sentDate.substring(0, 10);
                    String hour = sentDate.substring(0, 13) + ":00:00";
                    String minute15 = sentDate.substring(14, 16);
                    String minute05 = sentDate.substring(14, 16);
                    int minute15Int = Integer.valueOf(minute15);
                    int minute05Int = Integer.valueOf(minute05);
                    if (minute15Int < 15) {
                        minute15 = sentDate.substring(0, 14) + "00:00";
                    } else if (minute15Int < 30) {
                        minute15 = sentDate.substring(0, 14) + "15:00";
                    } else if (minute15Int < 45) {
                        minute15 = sentDate.substring(0, 14) + "30:00";
                    } else if (minute15Int <= 59) {
                        minute15 = sentDate.substring(0, 14) + "45:00";
                    }

                    // if (minute05Int < 5) {
                    // minute05 = sentDate.substring(0, 14) + "00:00";
                    // } else if (minute05Int < 10) {
                    // minute05 = sentDate.substring(0, 14) + "05:00";
                    // } else if (minute05Int < 15) {
                    // minute05 = sentDate.substring(0, 14) + "10:00";
                    // } else if (minute05Int < 20) {
                    // minute05 = sentDate.substring(0, 14) + "15:00";
                    // } else if (minute05Int < 25) {
                    // minute05 = sentDate.substring(0, 14) + "20:00";
                    // } else if (minute05Int < 30) {
                    // minute05 = sentDate.substring(0, 14) + "25:00";
                    // } else if (minute05Int < 35) {
                    // minute05 = sentDate.substring(0, 14) + "30:00";
                    // } else if (minute05Int < 40) {
                    // minute05 = sentDate.substring(0, 14) + "35:00";
                    // } else if (minute05Int < 45) {
                    // minute05 = sentDate.substring(0, 14) + "40:00";
                    // } else if (minute05Int < 50) {
                    // minute05 = sentDate.substring(0, 14) + "45:00";
                    // } else if (minute05Int < 55) {
                    // minute05 = sentDate.substring(0, 14) + "50:00";
                    // } else if (minute05Int <= 59) {
                    // minute05 = sentDate.substring(0, 14) + "55:00";
                    // }

                    DataPqs epCache = mapCache.get(data.getDeviceId());
                    if (epCache == null) {
                        epCache = new DataPqs();
                        epCache.setDeviceId(data.getDeviceId());
                        epCache.setEpCache(Float.parseFloat(data.getEp() + ""));
                    }

                    MultiKey multikey = new MultiKey(data.getDeviceId(), minute05, Constants.MINUTE_05);

                    // DataPqs minuteData05 = map.get(multikey);
                    // if (minuteData05 == null) {
                    // minuteData05 = new DataPqs();
                    // minuteData05.setDeviceId(data.getDeviceId());
                    // minuteData05.setViewTime(minute05);
                    // minuteData05.setViewType(Constants.MINUTE_05);
                    // minuteData05.setLowEp(0f);
                    // minuteData05.setNormalEp(0f);
                    // minuteData05.setHighEp(0f);
                    // minuteData05.setEpAtATime(data.getEp() > 0 ? Float.parseFloat(data.getEp() + "") : null);
                    // }
                    // minuteData05 = addData(minuteData05, data, epCache.getEpCache());
                    // map.put(multikey, minuteData05);

                    multikey = new MultiKey(data.getDeviceId(), minute15, Constants.MINUTE_15);

                    DataPqs minuteData = map.get(multikey);
                    if (minuteData == null) {
                        minuteData = new DataPqs();
                        minuteData.setDeviceId(data.getDeviceId());
                        minuteData.setViewTime(minute15);
                        minuteData.setViewType(Constants.MINUTE_15);
                        minuteData.setLowEp(0f);
                        minuteData.setNormalEp(0f);
                        minuteData.setHighEp(0f);
                        minuteData.setEpAtATime(data.getEp() > 0 ? Float.parseFloat(data.getEp() + "") : null);
                    }
                    minuteData = addData(minuteData, data, epCache.getEpCache());
                    map.put(multikey, minuteData);

                    multikey = new MultiKey(data.getDeviceId(), hour, Constants.HOUR);

                    DataPqs hourData = map.get(multikey);
                    if (hourData == null) {
                        hourData = new DataPqs();
                        hourData.setDeviceId(data.getDeviceId());
                        hourData.setViewTime(hour);
                        hourData.setViewType(Constants.HOUR);
                        hourData.setLowEp(0f);
                        hourData.setNormalEp(0f);
                        hourData.setHighEp(0f);
                        hourData.setEpAtATime(data.getEp() > 0 ? Float.parseFloat(data.getEp() + "") : null);
                    }
                    hourData = addData(hourData, data, epCache.getEpCache());
                    map.put(multikey, hourData);

                    multikey = new MultiKey(data.getDeviceId(), day, Constants.DAY);

                    DataPqs dayData = map.get(multikey);
                    if (dayData == null) {
                        dayData = new DataPqs();
                        dayData.setDeviceId(data.getDeviceId());
                        dayData.setViewTime(day);
                        dayData.setViewType(Constants.DAY);
                        dayData.setLowEp(0f);
                        dayData.setNormalEp(0f);
                        dayData.setHighEp(0f);
                        dayData.setEpAtATime(data.getEp() > 0 ? Float.parseFloat(data.getEp() + "") : null);

                        // log.info("!!!DEVICE: " + data.getDeviceId() + " ~ DAY: " + day);
                    }
                    dayData = addData(dayData, data, epCache.getEpCache());
                    map.put(multikey, dayData);

                    multikey = new MultiKey(data.getDeviceId(), month, Constants.MONTH);

                    DataPqs monthData = map.get(multikey);
                    if (monthData == null) {
                        monthData = new DataPqs();
                        monthData.setDeviceId(data.getDeviceId());
                        monthData.setViewTime(month);
                        monthData.setViewType(Constants.MONTH);
                        monthData.setLowEp(0f);
                        monthData.setNormalEp(0f);
                        monthData.setHighEp(0f);
                        monthData.setEpAtATime(data.getEp() > 0 ? Float.parseFloat(data.getEp() + "") : null);
                    }
                    monthData = addData(monthData, data, epCache.getEpCache());
                    map.put(multikey, monthData);

                    multikey = new MultiKey(data.getDeviceId(), year, Constants.YEAR);

                    DataPqs yearData = map.get(multikey);
                    if (yearData == null) {
                        yearData = new DataPqs();
                        yearData.setDeviceId(data.getDeviceId());
                        yearData.setViewTime(year);
                        yearData.setViewType(Constants.YEAR);
                        yearData.setLowEp(0f);
                        yearData.setNormalEp(0f);
                        yearData.setHighEp(0f);
                        yearData.setEpAtATime(data.getEp() > 0 ? Float.parseFloat(data.getEp() + "") : null);
                    }
                    yearData = addData(yearData, data, epCache.getEpCache());
                    map.put(multikey, yearData);
                    epCache.setEp(Float.parseFloat(data.getEp() > 0 ? (data.getEp() + "") : "0.0"));
                    epCache.setEpCache(Float.parseFloat(data.getEp() + ""));
                    mapCache.put(data.getDeviceId(), epCache);
                }

                // -----SAVE DATA-----
                log.info("!!!WRITING.........");
                List<DataPqs> itemData = map.values()
                    .stream()
                    .collect(Collectors.toList());
                List<DataPqs> saveList = new ArrayList<>();
                for (DataPqs data : itemData) {

                    if (data.getId() != null) {
                        dataPqsMapper.update(schema, data);
                    } else {
                        data.setDeviceType(Constants.Device_type.METER);
                        saveList.add(data);
                    }
                }
                if (saveList.size() > 0) {
                    dataPqsMapper.saveAll(schema, saveList);
                }

                List<DataPqs> itemDataCache = mapCache.values()
                    .stream()
                    .collect(Collectors.toList());
                List<DataPqs> saveListCache = new ArrayList<>();
                for (DataPqs data : itemDataCache) {
                    if (data.getId() != null) {
                        if (data.getEp() != null) dataPqsMapper.updateCache(schema, data);
                    } else {
                        data.setDeviceType(Constants.Device_type.METER);
                        saveListCache.add(data);
                    }
                }
                if (saveListCache.size() > 0) {
                    dataPqsMapper.saveAllCache(schema, saveListCache);
                }
                dataPqsMapper.updateLastTime(schema, lastTime, tableYear.toString(), Constants.Device_type.METER);
                log.info("[SAVE] UPDATE LASTTIME SCHEMA [" + schema + "]");
            }
            log.info("!!![DONE]");
        }
    }

    public void processInverter1(List<String> schemas, boolean isNoData) {
        for (String schema : schemas) {
            schema = "s3m_plus_customer_" + schema;
            // -----GET NEW DATA FROM DATABASE SINCE LASTTIME EXECUTOR-----
            log.info("!!!READING PQS Inverter.........");
            DataPqs lastData = null;
            try {
                lastData = dataPqsMapper.selectLastestTime(schema, Constants.Device_type.INVERTER);

                if (lastData == null) {
                    log.warn("!!!First record in s3m_data_pqs is not exist");
                    Calendar instance = Calendar.getInstance();
                    int year = instance.get(Calendar.YEAR);
                    String tableName = "s3m_data_inverter_1_" + year;
                    DataInverter1 newData = dataInverter1Mapper.selectNewestRecordInverter(schema, tableName);
                    lastData = new DataPqs();
                    lastData.setViewTime(newData.getId()
                        .toString());
                    lastData.setSentDate(newData.getSentDate()
                        .substring(0, 4));
                    lastData.setDeviceType(Constants.Device_type.INVERTER);
                    lastData.setViewType(Constants.System_type.NUMBER_0);
                    dataPqsMapper.insertFirstRecord(schema, lastData);
                    log.info("!!!First record in s3m_data_pqs is inserted..");
                    continue;
                }
            } catch (Exception e) {
                log.error("!!!SCHEMA [" + schema + "] IS NOT EXIST");
                continue;
            }
            String lastTime = lastData.getViewTime();
            Integer tableYear = Integer.valueOf(lastData.getSentDate());
            String tableName = "s3m_data_inverter_1_" + tableYear;
            List<DataInverter1> dataInverter1 = dataInverter1Mapper.selectNewRecordInverter(schema, tableName,
                lastTime != null ? lastTime : "0");

            if (dataInverter1 == null || dataInverter1.size() == 0) {
                tableYear++;
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                while (year >= tableYear && (dataInverter1 == null || dataInverter1.size() == 0)) {
                    tableName = "s3m_data_inverter_1_" + tableYear;
                    dataInverter1 = dataInverter1Mapper.selectNewRecordInverter(schema, tableName, "0");
                    if (dataInverter1 == null || dataInverter1.size() == 0) {
                        tableYear++;
                    }
                }
            }

            if (dataInverter1 != null && dataInverter1.size() > 0) {
                // ----- UPDATE FLAG CUSTOMER HAVE DATA
                isNoData = false;
                List<DataPqs> dataPqss = dataPqsMapper.selectByTypeLastTime(schema);
                if (dataPqss.size() > 0) {
                    map = convertListToMap(dataPqss);
                }
                List<DataPqs> cache = dataPqsMapper.selectAllCache(schema, Constants.Device_type.INVERTER);
                mapCache = cache.stream()
                    .collect(Collectors.toMap(DataPqs::getDeviceId, Function.identity()));
                lastTime = dataInverter1.get(dataInverter1.size() - 1)
                    .getId()
                    .toString();

                // -----ADD NEW DATA TO OLD DATA OR CREATE NEW RECORD-----
                log.info("!!!PROCESSING.........");
                for (DataInverter1 data : dataInverter1) {
                    String sentDate = data.getSentDate();
                    String year = sentDate.substring(0, 4);
                    String month = sentDate.substring(0, 7);
                    String day = sentDate.substring(0, 10);
                    String hour = sentDate.substring(0, 13) + ":00:00";
                    String minute15 = sentDate.substring(14, 16);
                    String minute05 = sentDate.substring(14, 16);
                    int minute15Int = Integer.valueOf(minute15);
                    int minute05Int = Integer.valueOf(minute05);
                    if (minute15Int < 15) {
                        minute15 = sentDate.substring(0, 14) + "00:00";
                    } else if (minute15Int < 30) {
                        minute15 = sentDate.substring(0, 14) + "15:00";
                    } else if (minute15Int < 45) {
                        minute15 = sentDate.substring(0, 14) + "30:00";
                    } else if (minute15Int <= 59) {
                        minute15 = sentDate.substring(0, 14) + "45:00";
                    }

                    // if (minute05Int < 5) {
                    // minute05 = sentDate.substring(0, 14) + "00:00";
                    // } else if (minute05Int < 10) {
                    // minute05 = sentDate.substring(0, 14) + "05:00";
                    // } else if (minute05Int < 15) {
                    // minute05 = sentDate.substring(0, 14) + "10:00";
                    // } else if (minute05Int < 20) {
                    // minute05 = sentDate.substring(0, 14) + "15:00";
                    // } else if (minute05Int < 25) {
                    // minute05 = sentDate.substring(0, 14) + "20:00";
                    // } else if (minute05Int < 30) {
                    // minute05 = sentDate.substring(0, 14) + "25:00";
                    // } else if (minute05Int < 35) {
                    // minute05 = sentDate.substring(0, 14) + "30:00";
                    // } else if (minute05Int < 40) {
                    // minute05 = sentDate.substring(0, 14) + "35:00";
                    // } else if (minute05Int < 45) {
                    // minute05 = sentDate.substring(0, 14) + "40:00";
                    // } else if (minute05Int < 50) {
                    // minute05 = sentDate.substring(0, 14) + "45:00";
                    // } else if (minute05Int < 55) {
                    // minute05 = sentDate.substring(0, 14) + "50:00";
                    // } else if (minute05Int <= 59) {
                    // minute05 = sentDate.substring(0, 14) + "55:00";
                    // }

                    DataPqs epCache = mapCache.get(data.getDeviceId());
                    if (epCache == null) {
                        epCache = new DataPqs();
                        epCache.setDeviceId(data.getDeviceId());
                        epCache.setEpCache(Float.parseFloat(data.getEp() != null ? data.getEp() + "" : "0.0"));
                    }

                    MultiKey multikey = new MultiKey(data.getDeviceId(), minute05, Constants.MINUTE_05);

                    // DataPqs minuteData05 = map.get(multikey);
                    // if (minuteData05 == null) {
                    // minuteData05 = new DataPqs();
                    // minuteData05.setDeviceId(data.getDeviceId());
                    // minuteData05.setViewTime(minute05);
                    // minuteData05.setViewType(Constants.MINUTE_05);
                    // minuteData05.setLowEp(0f);
                    // minuteData05.setNormalEp(0f);
                    // minuteData05.setHighEp(0f);
                    // minuteData05.setEpAtATime(data.getEp() > 0 ? Float.parseFloat(data.getEp() + "") : null);
                    // }
                    // minuteData05 = addDataInverter(minuteData05, data, epCache.getEpCache());
                    // map.put(multikey, minuteData05);

                    multikey = new MultiKey(data.getDeviceId(), minute15, Constants.MINUTE_15);

                    DataPqs minuteData = map.get(multikey);
                    if (minuteData == null) {
                        minuteData = new DataPqs();
                        minuteData.setDeviceId(data.getDeviceId());
                        minuteData.setViewTime(minute15);
                        minuteData.setViewType(Constants.MINUTE_15);
                        minuteData.setLowEp(0f);
                        minuteData.setNormalEp(0f);
                        minuteData.setHighEp(0f);
                        minuteData.setEpAtATime(data.getEp() > 0 ? Float.parseFloat(data.getEp() + "") : null);
                    }
                    minuteData = addDataInverter(minuteData, data, epCache.getEpCache());
                    map.put(multikey, minuteData);

                    multikey = new MultiKey(data.getDeviceId(), hour, Constants.HOUR);

                    DataPqs hourData = map.get(multikey);
                    if (hourData == null) {
                        hourData = new DataPqs();
                        hourData.setDeviceId(data.getDeviceId());
                        hourData.setViewTime(hour);
                        hourData.setViewType(Constants.HOUR);
                        hourData.setLowEp(0f);
                        hourData.setNormalEp(0f);
                        hourData.setHighEp(0f);
                        hourData.setEpAtATime(data.getEp() > 0 ? Float.parseFloat(data.getEp() + "") : null);
                    }
                    hourData = addDataInverter(hourData, data, epCache.getEpCache());
                    map.put(multikey, hourData);

                    multikey = new MultiKey(data.getDeviceId(), day, Constants.DAY);

                    DataPqs dayData = map.get(multikey);
                    if (dayData == null) {
                        dayData = new DataPqs();
                        dayData.setDeviceId(data.getDeviceId());
                        dayData.setViewTime(day);
                        dayData.setViewType(Constants.DAY);
                        dayData.setLowEp(0f);
                        dayData.setNormalEp(0f);
                        dayData.setHighEp(0f);
                        dayData.setEpAtATime(data.getEp() > 0 ? Float.parseFloat(data.getEp() + "") : null);

                        // log.info("!!!DEVICE: " + data.getDeviceId() + " ~ DAY: " + day);
                    }
                    dayData = addDataInverter(dayData, data, epCache.getEpCache());
                    map.put(multikey, dayData);

                    multikey = new MultiKey(data.getDeviceId(), month, Constants.MONTH);

                    DataPqs monthData = map.get(multikey);
                    if (monthData == null) {
                        monthData = new DataPqs();
                        monthData.setDeviceId(data.getDeviceId());
                        monthData.setViewTime(month);
                        monthData.setViewType(Constants.MONTH);
                        monthData.setLowEp(0f);
                        monthData.setNormalEp(0f);
                        monthData.setHighEp(0f);
                        monthData.setEpAtATime(data.getEp() > 0 ? Float.parseFloat(data.getEp() + "") : null);
                    }
                    monthData = addDataInverter(monthData, data, epCache.getEpCache());
                    map.put(multikey, monthData);

                    multikey = new MultiKey(data.getDeviceId(), year, Constants.YEAR);

                    DataPqs yearData = map.get(multikey);
                    if (yearData == null) {
                        yearData = new DataPqs();
                        yearData.setDeviceId(data.getDeviceId());
                        yearData.setViewTime(year);
                        yearData.setViewType(Constants.YEAR);
                        yearData.setLowEp(0f);
                        yearData.setNormalEp(0f);
                        yearData.setHighEp(0f);
                        yearData.setEpAtATime(data.getEp() > 0 ? Float.parseFloat(data.getEp() + "") : null);
                    }
                    yearData = addDataInverter(yearData, data, epCache.getEpCache());
                    map.put(multikey, yearData);

                    epCache.setEp(Float.parseFloat(data.getEp() != null ? (data.getEp() + "") : "0.0"));
                    epCache.setEpCache(Float.parseFloat(data.getEp() + ""));
                    mapCache.put(data.getDeviceId(), epCache);
                }

                // -----SAVE DATA-----
                log.info("!!!WRITING.........");
                List<DataPqs> itemData = map.values()
                    .stream()
                    .collect(Collectors.toList());
                List<DataPqs> saveList = new ArrayList<>();
                for (DataPqs data : itemData) {
                    if (data.getId() != null) {
                        dataPqsMapper.update(schema, data);
                    } else {
                        data.setDeviceType(Constants.Device_type.INVERTER);
                        saveList.add(data);
                    }
                }
                if (saveList.size() > 0) {
                    dataPqsMapper.saveAll(schema, saveList);
                }

                List<DataPqs> itemDataCache = mapCache.values()
                    .stream()
                    .collect(Collectors.toList());
                List<DataPqs> saveListCache = new ArrayList<>();
                for (DataPqs data : itemDataCache) {
                    if (data.getId() != null) {
                        if (data.getEp() != null) dataPqsMapper.updateCache(schema, data);
                    } else {
                        data.setDeviceType(Constants.Device_type.INVERTER);
                        saveListCache.add(data);
                    }
                }
                if (saveListCache.size() > 0) {
                    dataPqsMapper.saveAllCache(schema, saveListCache);
                }

                log.info("[SAVE] UPDATE LASTTIME");
                dataPqsMapper.updateLastTime(schema, lastTime, tableYear.toString(), Constants.Device_type.INVERTER);
            }
            log.info("!!![DONE]");
        }
    }

    public void processFlowSensor(List<String> schemas, boolean isNoData) {
        for (String schema : schemas) {
            schema = "s3m_plus_customer_" + schema;
            // -----GET NEW DATA FROM DATABASE SINCE LASTTIME EXECUTOR-----
            log.info("!!!READING PQS Flow sensor.........");
            DataPqs lastData = null;
            try {
                lastData = dataPqsMapper.selectLastestTime(schema, Constants.Device_type.CAM_BIEN_LUU_LUONG);

                if (lastData == null) {
                    log.warn("!!!First record in s3m_data_pqs is not exist");
                    Calendar instance = Calendar.getInstance();
                    int year = instance.get(Calendar.YEAR);
                    String tableName = "s3m_data_flow_1_" + year;
                    DataFlow newData = dataFlowMapper.selectNewestRecordFlow(schema, tableName);
                    lastData = new DataPqs();
                    lastData.setViewTime(newData.getId()
                        .toString());
                    lastData.setSentDate(newData.getSentDate()
                        .substring(0, 4));
                    lastData.setDeviceType(Constants.Device_type.CAM_BIEN_LUU_LUONG);
                    lastData.setViewType(Constants.System_type.NUMBER_0);
                    dataPqsMapper.insertFirstRecord(schema, lastData);
                    log.info("!!!First record flow in s3m_data_pqs is inserted..");
                    continue;
                }
            } catch (Exception e) {
                log.error("!!!SCHEMA [" + schema + "] IS NOT EXIST");
                continue;
            }
            String lastTime = lastData.getViewTime();
            Integer tableYear = Integer.valueOf(lastData.getSentDate());
            String tableName = "s3m_data_flow_1_" + tableYear;
            List<DataFlow> dataFlow = dataFlowMapper.selectNewRecordFlow(schema, tableName,
                lastTime != null ? lastTime : "0");

            if (dataFlow == null || dataFlow.size() == 0) {
                tableYear++;
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                while (year >= tableYear && (dataFlow == null || dataFlow.size() == 0)) {
                    tableName = "s3m_data_flow_1_" + tableYear;
                    dataFlow = dataFlowMapper.selectNewRecordFlow(schema, tableName, "0");
                    if (dataFlow == null || dataFlow.size() == 0) {
                        tableYear++;
                    }
                }
            }

            if (dataFlow != null && dataFlow.size() > 0) {
                // ----- UPDATE FLAG CUSTOMER HAVE DATA
                isNoData = false;
                List<DataPqs> dataPqss = dataPqsMapper.selectByTypeLastTime(schema);
                if (dataPqss.size() > 0) {
                    map = convertListToMap(dataPqss);
                }
                List<DataPqs> cache = dataPqsMapper.selectAllCache(schema, Constants.Device_type.CAM_BIEN_LUU_LUONG);
                mapCache = cache.stream()
                    .collect(Collectors.toMap(DataPqs::getDeviceId, Function.identity()));
                lastTime = dataFlow.get(dataFlow.size() - 1)
                    .getId()
                    .toString();

                // -----ADD NEW DATA TO OLD DATA OR CREATE NEW RECORD-----
                log.info("!!!PROCESSING.........");
                for (DataFlow data : dataFlow) {
                    String sentDate = data.getSentDate();
                    String year = sentDate.substring(0, 4);
                    String month = sentDate.substring(0, 7);
                    String day = sentDate.substring(0, 10);
                    String hour = sentDate.substring(0, 13) + ":00:00";
                    String minute15 = sentDate.substring(14, 16);
                    String minute05 = sentDate.substring(14, 16);
                    int minute15Int = Integer.valueOf(minute15);
                    int minute05Int = Integer.valueOf(minute05);
                    if (minute15Int < 15) {
                        minute15 = sentDate.substring(0, 14) + "00:00";
                    } else if (minute15Int < 30) {
                        minute15 = sentDate.substring(0, 14) + "15:00";
                    } else if (minute15Int < 45) {
                        minute15 = sentDate.substring(0, 14) + "30:00";
                    } else if (minute15Int <= 59) {
                        minute15 = sentDate.substring(0, 14) + "45:00";
                    }

                    DataPqs tCache = mapCache.get(data.getDeviceId());
                    if (tCache == null) {
                        tCache = new DataPqs();
                        tCache.setDeviceId(data.getDeviceId());
                        tCache.setTCache(Double.parseDouble(data.getT() != null ? data.getT() + "" : "0.0"));
                    }

                    MultiKey multikey = new MultiKey(data.getDeviceId(), minute05, Constants.MINUTE_05);

                    multikey = new MultiKey(data.getDeviceId(), minute15, Constants.MINUTE_15);

                    DataPqs minuteData = map.get(multikey);
                    if (minuteData == null) {
                        minuteData = new DataPqs();
                        minuteData.setDeviceId(data.getDeviceId());
                        minuteData.setViewTime(minute15);
                        minuteData.setViewType(Constants.MINUTE_15);
                        minuteData.setLowEp(0f);
                        minuteData.setNormalEp(0f);
                        minuteData.setHighEp(0f);
                        minuteData.setTAtATime(data.getT() > 0 ? Double.parseDouble(data.getT() + "") : null);
                    }
                    minuteData = addDataFlow(minuteData, data, tCache.getTCache());
                    map.put(multikey, minuteData);

                    multikey = new MultiKey(data.getDeviceId(), hour, Constants.HOUR);

                    DataPqs hourData = map.get(multikey);
                    if (hourData == null) {
                        hourData = new DataPqs();
                        hourData.setDeviceId(data.getDeviceId());
                        hourData.setViewTime(hour);
                        hourData.setViewType(Constants.HOUR);
                        hourData.setLowEp(0f);
                        hourData.setNormalEp(0f);
                        hourData.setHighEp(0f);
                        hourData.setTAtATime(data.getT() > 0 ? Double.parseDouble(data.getT() + "") : null);
                    }
                    hourData = addDataFlow(hourData, data, tCache.getTCache());
                    map.put(multikey, hourData);

                    multikey = new MultiKey(data.getDeviceId(), day, Constants.DAY);

                    DataPqs dayData = map.get(multikey);
                    if (dayData == null) {
                        dayData = new DataPqs();
                        dayData.setDeviceId(data.getDeviceId());
                        dayData.setViewTime(day);
                        dayData.setViewType(Constants.DAY);
                        dayData.setLowEp(0f);
                        dayData.setNormalEp(0f);
                        dayData.setHighEp(0f);
                        dayData.setTAtATime(data.getT() > 0 ? Double.parseDouble(data.getT() + "") : null);

                        // log.info("!!!DEVICE: " + data.getDeviceId() + " ~ DAY: " + day);
                    }
                    dayData = addDataFlow(dayData, data, tCache.getTCache());
                    map.put(multikey, dayData);

                    multikey = new MultiKey(data.getDeviceId(), month, Constants.MONTH);

                    DataPqs monthData = map.get(multikey);
                    if (monthData == null) {
                        monthData = new DataPqs();
                        monthData.setDeviceId(data.getDeviceId());
                        monthData.setViewTime(month);
                        monthData.setViewType(Constants.MONTH);
                        monthData.setLowEp(0f);
                        monthData.setNormalEp(0f);
                        monthData.setHighEp(0f);
                        monthData.setTAtATime(data.getT() > 0 ? Double.parseDouble(data.getT() + "") : null);
                    }
                    monthData = addDataFlow(monthData, data, tCache.getTCache());
                    map.put(multikey, monthData);

                    multikey = new MultiKey(data.getDeviceId(), year, Constants.YEAR);

                    DataPqs yearData = map.get(multikey);
                    if (yearData == null) {
                        yearData = new DataPqs();
                        yearData.setDeviceId(data.getDeviceId());
                        yearData.setViewTime(year);
                        yearData.setViewType(Constants.YEAR);
                        yearData.setLowEp(0f);
                        yearData.setNormalEp(0f);
                        yearData.setHighEp(0f);
                        yearData.setTAtATime(data.getT() > 0 ? Double.parseDouble(data.getT() + "") : null);
                    }
                    yearData = addDataFlow(yearData, data, tCache.getTCache());
                    map.put(multikey, yearData);

                    tCache.setT(Double.parseDouble(data.getT() != null ? (data.getT() + "") : "0.0"));
                    tCache.setTCache(Double.parseDouble(data.getT() + ""));
                    mapCache.put(data.getDeviceId(), tCache);
                }

                // -----SAVE DATA-----
                log.info("!!!WRITING.........");
                List<DataPqs> itemData = map.values()
                    .stream()
                    .collect(Collectors.toList());
                List<DataPqs> saveList = new ArrayList<>();
                for (DataPqs data : itemData) {
                    if (data.getId() != null) {
                        dataPqsMapper.update(schema, data);
                    } else {
                        data.setDeviceType(Constants.Device_type.CAM_BIEN_LUU_LUONG);
                        saveList.add(data);
                    }
                }
                if (saveList.size() > 0) {
                    dataPqsMapper.saveAll(schema, saveList);
                }

                List<DataPqs> itemDataCache = mapCache.values()
                    .stream()
                    .collect(Collectors.toList());
                List<DataPqs> saveListCache = new ArrayList<>();
                for (DataPqs data : itemDataCache) {
                    if (data.getId() != null) {
                        if (data.getT() != null) dataPqsMapper.updateCache(schema, data);
                    } else {
                        data.setDeviceType(Constants.Device_type.CAM_BIEN_LUU_LUONG);
                        saveListCache.add(data);
                    }
                }
                if (saveListCache.size() > 0) {
                    dataPqsMapper.saveAllCache(schema, saveListCache);
                }

                log.info("[SAVE] UPDATE LASTTIME");
                dataPqsMapper.updateLastTime(schema, lastTime, tableYear.toString(),
                    Constants.Device_type.CAM_BIEN_LUU_LUONG);
            }
            log.info("!!![DONE]");
        }
    }

    // public void processCombiner1(List<String> schemas, boolean isNoData) {
    // for (String schema : schemas) {
    // schema = "s3m_plus_customer_" + schema;
    // // -----GET NEW DATA FROM DATABASE SINCE LASTTIME EXECUTOR-----
    // log.info("!!!READING PQS Combiner.........");
    // DataPqs lastData = null;
    // try {
    // lastData = dataPqsMapper.selectLastestTime(schema, Constants.System_type.SOLAR,
    // Constants.System_type.SOLAR_TYPE.COMBINER);
    //
    // if (lastData == null) {
    // log.error("!!!First record in s3m_data_pqs is not exist");
    // log.error(
    // "!!!Please insert the first record to [s3m_data_pqs] with [view_type] = 0 [view_time] = 0 and [sent_date] =
    // 2023");
    // continue;
    // }
    // } catch (Exception e) {
    // log.error("!!!SCHEMA [" + schema + "] IS NOT EXIST");
    // continue;
    // }
    // String lastTime = lastData.getViewTime();
    // Integer tableYear = Integer.valueOf(lastData.getSentDate());
    // String tableName = "s3m_data_combiner_1_" + tableYear;
    // List<DataCombiner1> dataCombiner1 = dataCombiner1Mapper.selectNewRecord(schema, tableName,
    // lastTime != null ? lastTime : "0");
    // if (dataCombiner1 == null || dataCombiner1.size() == 0) {
    // tableYear++;
    // Calendar cal = Calendar.getInstance();
    // int year = cal.get(Calendar.YEAR);
    // while (year >= tableYear && (dataCombiner1 == null || dataCombiner1.size() == 0)) {
    // tableName = "s3m_data_combiner_1_" + tableYear;
    // dataCombiner1 = dataCombiner1Mapper.selectNewRecord(schema, tableName, "0");
    // if (dataCombiner1 == null || dataCombiner1.size() == 0) {
    // tableYear++;
    // }
    // }
    // }
    //
    // if (dataCombiner1 != null && dataCombiner1.size() > 0) {
    // // ----- UPDATE FLAG CUSTOMER HAVE DATA
    // isNoData = false;
    // List<DataPqs> dataPqss = dataPqsMapper.selectByTypeLastTime(schema);
    // if (dataPqss.size() > 0) {
    // map = convertListToMap(dataPqss);
    // }
    // List<DataPqs> cache = dataPqsMapper.selectAllCache(schema, Constants.System_type.SOLAR,
    // Constants.System_type.SOLAR_TYPE.COMBINER);
    // mapCache = cache.stream()
    // .collect(Collectors.toMap(DataPqs::getDeviceId, Function.identity()));
    // lastTime = dataCombiner1.get(dataCombiner1.size() - 1)
    // .getId()
    // .toString();
    //
    // // -----ADD NEW DATA TO OLD DATA OR CREATE NEW RECORD-----
    // log.info("!!!PROCESSING.........");
    // for (DataCombiner1 data : dataCombiner1) {
    // String sentDate = data.getSentDate();
    // String year = sentDate.substring(0, 4);
    // String month = sentDate.substring(0, 7);
    // String day = sentDate.substring(0, 10);
    // String hour = sentDate.substring(0, 13) + ":00:00";
    // String minute15 = sentDate.substring(14, 16);
    // int minute15Int = Integer.valueOf(minute15);
    // if (minute15Int < 15) {
    // minute15 = sentDate.substring(0, 14) + "00:00";
    // } else if (minute15Int < 30) {
    // minute15 = sentDate.substring(0, 14) + "15:00";
    // } else if (minute15Int < 45) {
    // minute15 = sentDate.substring(0, 14) + "30:00";
    // } else if (minute15Int <= 59) {
    // minute15 = sentDate.substring(0, 14) + "45:00";
    // }
    //
    // DataPqs epCache = mapCache.get(data.getDeviceId());
    // if (epCache == null) {
    // epCache = new DataPqs();
    // epCache.setDeviceId(data.getDeviceId());
    // epCache.setEpCache(
    // Float.parseFloat(data.getEpCombiner() != null ? data.getEpCombiner() + "" : "0.0"));
    // }
    //
    // MultiKey multikey = new MultiKey(data.getDeviceId(), minute15, Constants.MINUTE_15);
    //
    // DataPqs minuteData = map.get(multikey);
    // if (minuteData == null) {
    // minuteData = new DataPqs();
    // minuteData.setDeviceId(data.getDeviceId());
    // minuteData.setViewTime(minute15);
    // minuteData.setViewType(Constants.MINUTE_15);
    // minuteData.setLowEp(0f);
    // minuteData.setNormalEp(0f);
    // minuteData.setHighEp(0f);
    // minuteData.setEpAtATime(Float.parseFloat(data.getEpCombiner() + ""));
    // }
    // minuteData = addDataCombiner(minuteData, data, epCache.getEpCache());
    // map.put(multikey, minuteData);
    //
    // multikey = new MultiKey(data.getDeviceId(), hour, Constants.HOUR);
    //
    // DataPqs hourData = map.get(multikey);
    // if (hourData == null) {
    // hourData = new DataPqs();
    // hourData.setDeviceId(data.getDeviceId());
    // hourData.setViewTime(hour);
    // hourData.setViewType(Constants.HOUR);
    // hourData.setLowEp(0f);
    // hourData.setNormalEp(0f);
    // hourData.setHighEp(0f);
    // hourData.setEpAtATime(Float.parseFloat(data.getEpCombiner() + ""));
    // }
    // hourData = addDataCombiner(hourData, data, epCache.getEpCache());
    // map.put(multikey, hourData);
    //
    // multikey = new MultiKey(data.getDeviceId(), day, Constants.DAY);
    //
    // DataPqs dayData = map.get(multikey);
    // if (dayData == null) {
    // dayData = new DataPqs();
    // dayData.setDeviceId(data.getDeviceId());
    // dayData.setViewTime(day);
    // dayData.setViewType(Constants.DAY);
    // dayData.setLowEp(0f);
    // dayData.setNormalEp(0f);
    // dayData.setHighEp(0f);
    // dayData.setEpAtATime(Float.parseFloat(data.getEpCombiner() + ""));
    //
    // // log.info("!!!DEVICE: " + data.getDeviceId() + " ~ DAY: " + day);
    // }
    // dayData = addDataCombiner(dayData, data, epCache.getEpCache());
    // map.put(multikey, dayData);
    //
    // multikey = new MultiKey(data.getDeviceId(), month, Constants.MONTH);
    //
    // DataPqs monthData = map.get(multikey);
    // if (monthData == null) {
    // monthData = new DataPqs();
    // monthData.setDeviceId(data.getDeviceId());
    // monthData.setViewTime(month);
    // monthData.setViewType(Constants.MONTH);
    // monthData.setLowEp(0f);
    // monthData.setNormalEp(0f);
    // monthData.setHighEp(0f);
    // monthData.setEpAtATime(Float.parseFloat(data.getEpCombiner() + ""));
    // }
    // monthData = addDataCombiner(monthData, data, epCache.getEpCache());
    // map.put(multikey, monthData);
    //
    // multikey = new MultiKey(data.getDeviceId(), year, Constants.YEAR);
    //
    // DataPqs yearData = map.get(multikey);
    // if (yearData == null) {
    // yearData = new DataPqs();
    // yearData.setDeviceId(data.getDeviceId());
    // yearData.setViewTime(year);
    // yearData.setViewType(Constants.YEAR);
    // yearData.setLowEp(0f);
    // yearData.setNormalEp(0f);
    // yearData.setHighEp(0f);
    // yearData.setEpAtATime(Float.parseFloat(data.getEpCombiner() + ""));
    // }
    // yearData = addDataCombiner(yearData, data, epCache.getEpCache());
    // map.put(multikey, yearData);
    //
    // epCache.setEp(Float.parseFloat(data.getEpCombiner() != null ? (data.getEpCombiner() + "") : "0.0"));
    // epCache.setEpCache(Float.parseFloat(data.getEpCombiner() + ""));
    // mapCache.put(data.getDeviceId(), epCache);
    // }
    //
    // // -----SAVE DATA-----
    // log.info("!!!WRITING.........");
    // List<DataPqs> itemData = map.values()
    // .stream()
    // .collect(Collectors.toList());
    // List<DataPqs> saveList = new ArrayList<>();
    // for (DataPqs data : itemData) {
    // if (data.getId() != null) {
    // dataPqsMapper.update(schema, data);
    // } else {
    // data.setSystemTypeId(Constants.System_type.SOLAR);
    // data.setDeviceType(Constants.System_type.SOLAR_TYPE.COMBINER);
    // saveList.add(data);
    // }
    // }
    // if (saveList.size() > 0) {
    // dataPqsMapper.saveAll(schema, saveList);
    // }
    //
    // List<DataPqs> itemDataCache = mapCache.values()
    // .stream()
    // .collect(Collectors.toList());
    // List<DataPqs> saveListCache = new ArrayList<>();
    // for (DataPqs data : itemDataCache) {
    // if (data.getId() != null) {
    // if (data.getEp() != null) dataPqsMapper.updateCache(schema, data);
    // } else {
    // data.setSystemTypeId(Constants.System_type.SOLAR);
    // data.setDeviceType(Constants.System_type.SOLAR_TYPE.COMBINER);
    // saveListCache.add(data);
    // }
    // }
    // if (saveListCache.size() > 0) {
    // dataPqsMapper.saveAllCache(schema, saveListCache);
    // }
    //
    // log.info("[SAVE] UPDATE LASTTIME");
    // dataPqsMapper.updateLastTime(schema, lastTime, tableYear.toString(), Constants.System_type.SOLAR,
    // Constants.System_type.SOLAR_TYPE.COMBINER);
    // }
    // log.info("!!![DONE]");
    // }
    // }
    //
    // public void processRmuDrawer1(List<String> schemas, boolean isNoData) {
    // for (String schema : schemas) {
    // schema = "s3m_plus_customer_" + schema;
    // // -----GET NEW DATA FROM DATABASE SINCE LASTTIME EXECUTOR-----
    // log.info("!!!READING PQS RMU.........");
    // DataPqs lastData = null;
    // try {
    // lastData = dataPqsMapper.selectLastestTime(schema, Constants.System_type.GRID,
    // Constants.System_type.GRID_TYPE.RMU);
    //
    // if (lastData == null) {
    // log.error("!!!First record in s3m_data_pqs is not exist");
    // log.error(
    // "!!!Please insert the first record to [s3m_data_pqs] with [view_type] = 0 [view_time] = 0 and [sent_date] =
    // 2023");
    // continue;
    // }
    // } catch (Exception e) {
    // log.error("!!!SCHEMA [" + schema + "] IS NOT EXIST");
    // continue;
    // }
    // String lastTime = lastData.getViewTime();
    // Integer tableYear = Integer.valueOf(lastData.getSentDate());
    // String tableName = "s3m_data_rmu_drawer_1_" + tableYear;
    // List<DataRmuDrawer1> dataRmuDrawer1 = dataRmuDrawer1Mapper.selectNewRecord(schema, tableName,
    // lastTime != null ? lastTime : "0");
    //
    // if (dataRmuDrawer1 == null || dataRmuDrawer1.size() == 0) {
    // tableYear++;
    // Calendar cal = Calendar.getInstance();
    // int year = cal.get(Calendar.YEAR);
    // while (year >= tableYear && (dataRmuDrawer1 == null || dataRmuDrawer1.size() == 0)) {
    // tableName = "s3m_data_rmu_drawer_1_" + tableYear;
    // dataRmuDrawer1 = dataRmuDrawer1Mapper.selectNewRecord(schema, tableName, "0");
    // if (dataRmuDrawer1 == null || dataRmuDrawer1.size() == 0) {
    // tableYear++;
    // }
    // }
    // }
    //
    // if (dataRmuDrawer1 != null && dataRmuDrawer1.size() > 0) {
    // // ----- UPDATE FLAG CUSTOMER HAVE DATA
    // isNoData = false;
    // List<DataPqs> dataPqss = dataPqsMapper.selectByTypeLastTime(schema);
    // if (dataPqss.size() > 0) {
    // map = convertListToMap(dataPqss);
    // }
    // List<DataPqs> cache = dataPqsMapper.selectAllCache(schema, Constants.System_type.GRID,
    // Constants.System_type.GRID_TYPE.RMU);
    // mapCache = cache.stream()
    // .collect(Collectors.toMap(DataPqs::getDeviceId, Function.identity()));
    // lastTime = dataRmuDrawer1.get(dataRmuDrawer1.size() - 1)
    // .getId()
    // .toString();
    //
    // // -----ADD NEW DATA TO OLD DATA OR CREATE NEW RECORD-----
    // log.info("!!!PROCESSING.........");
    // for (DataRmuDrawer1 data : dataRmuDrawer1) {
    // String sentDate = data.getSentDate();
    // String year = sentDate.substring(0, 4);
    // String month = sentDate.substring(0, 7);
    // String day = sentDate.substring(0, 10);
    // String hour = sentDate.substring(0, 13) + ":00:00";
    // String minute15 = sentDate.substring(14, 16);
    // int minute15Int = Integer.valueOf(minute15);
    // if (minute15Int < 15) {
    // minute15 = sentDate.substring(0, 14) + "00:00";
    // } else if (minute15Int < 30) {
    // minute15 = sentDate.substring(0, 14) + "15:00";
    // } else if (minute15Int < 45) {
    // minute15 = sentDate.substring(0, 14) + "30:00";
    // } else if (minute15Int <= 59) {
    // minute15 = sentDate.substring(0, 14) + "45:00";
    // }
    //
    // DataPqs epCache = mapCache.get(data.getDeviceId());
    // if (epCache == null) {
    // epCache = new DataPqs();
    // epCache.setDeviceId(data.getDeviceId());
    // epCache.setEpCache(Float.parseFloat(data.getEp() != null ? data.getEp() + "" : "0.0"));
    // }
    //
    // MultiKey multikey = new MultiKey(data.getDeviceId(), minute15, Constants.MINUTE_15);
    //
    // DataPqs minuteData = map.get(multikey);
    // if (minuteData == null) {
    // minuteData = new DataPqs();
    // minuteData.setDeviceId(data.getDeviceId());
    // minuteData.setViewTime(minute15);
    // minuteData.setViewType(Constants.MINUTE_15);
    // minuteData.setLowEp(0f);
    // minuteData.setNormalEp(0f);
    // minuteData.setHighEp(0f);
    // minuteData.setEpAtATime(data.getEp() > 0 ? Float.parseFloat(data.getEp() + "") : null);
    // }
    // minuteData = addDataRmuDrawer(minuteData, data, epCache.getEpCache());
    // map.put(multikey, minuteData);
    //
    // multikey = new MultiKey(data.getDeviceId(), hour, Constants.HOUR);
    //
    // DataPqs hourData = map.get(multikey);
    // if (hourData == null) {
    // hourData = new DataPqs();
    // hourData.setDeviceId(data.getDeviceId());
    // hourData.setViewTime(hour);
    // hourData.setViewType(Constants.HOUR);
    // hourData.setLowEp(0f);
    // hourData.setNormalEp(0f);
    // hourData.setHighEp(0f);
    // hourData.setEpAtATime(data.getEp() > 0 ? Float.parseFloat(data.getEp() + "") : null);
    // }
    // hourData = addDataRmuDrawer(hourData, data, epCache.getEpCache());
    // map.put(multikey, hourData);
    //
    // multikey = new MultiKey(data.getDeviceId(), day, Constants.DAY);
    //
    // DataPqs dayData = map.get(multikey);
    // if (dayData == null) {
    // dayData = new DataPqs();
    // dayData.setDeviceId(data.getDeviceId());
    // dayData.setViewTime(day);
    // dayData.setViewType(Constants.DAY);
    // dayData.setLowEp(0f);
    // dayData.setNormalEp(0f);
    // dayData.setHighEp(0f);
    // dayData.setEpAtATime(data.getEp() > 0 ? Float.parseFloat(data.getEp() + "") : null);
    //
    // // log.info("!!!DEVICE: " + data.getDeviceId() + " ~ DAY: " + day);
    // }
    // dayData = addDataRmuDrawer(dayData, data, epCache.getEpCache());
    // map.put(multikey, dayData);
    //
    // multikey = new MultiKey(data.getDeviceId(), month, Constants.MONTH);
    //
    // DataPqs monthData = map.get(multikey);
    // if (monthData == null) {
    // monthData = new DataPqs();
    // monthData.setDeviceId(data.getDeviceId());
    // monthData.setViewTime(month);
    // monthData.setViewType(Constants.MONTH);
    // monthData.setLowEp(0f);
    // monthData.setNormalEp(0f);
    // monthData.setHighEp(0f);
    // monthData.setEpAtATime(data.getEp() > 0 ? Float.parseFloat(data.getEp() + "") : null);
    // }
    // monthData = addDataRmuDrawer(monthData, data, epCache.getEpCache());
    // map.put(multikey, monthData);
    //
    // multikey = new MultiKey(data.getDeviceId(), year, Constants.YEAR);
    //
    // DataPqs yearData = map.get(multikey);
    // if (yearData == null) {
    // yearData = new DataPqs();
    // yearData.setDeviceId(data.getDeviceId());
    // yearData.setViewTime(year);
    // yearData.setViewType(Constants.YEAR);
    // yearData.setLowEp(0f);
    // yearData.setNormalEp(0f);
    // yearData.setHighEp(0f);
    // yearData.setEpAtATime(data.getEp() > 0 ? Float.parseFloat(data.getEp() + "") : null);
    // }
    // yearData = addDataRmuDrawer(yearData, data, epCache.getEpCache());
    // map.put(multikey, yearData);
    //
    // epCache.setEp(Float.parseFloat(data.getEp() != null ? (data.getEp() + "") : "0.0"));
    // epCache.setEpCache(Float.parseFloat(data.getEp() + ""));
    // mapCache.put(data.getDeviceId(), epCache);
    // }
    //
    // // -----SAVE DATA-----
    // log.info("!!!WRITING.........");
    // List<DataPqs> itemData = map.values()
    // .stream()
    // .collect(Collectors.toList());
    // List<DataPqs> saveList = new ArrayList<>();
    // for (DataPqs data : itemData) {
    // if (data.getId() != null) {
    // dataPqsMapper.update(schema, data);
    // } else {
    // data.setSystemTypeId(Constants.System_type.GRID);
    // data.setDeviceType(Constants.System_type.GRID_TYPE.RMU);
    // saveList.add(data);
    // }
    // }
    // if (saveList.size() > 0) {
    // dataPqsMapper.saveAll(schema, saveList);
    // }
    //
    // List<DataPqs> itemDataCache = mapCache.values()
    // .stream()
    // .collect(Collectors.toList());
    // List<DataPqs> saveListCache = new ArrayList<>();
    // for (DataPqs data : itemDataCache) {
    // if (data.getId() != null) {
    // if (data.getEp() != null) dataPqsMapper.updateCache(schema, data);
    // } else {
    // data.setSystemTypeId(Constants.System_type.GRID);
    // data.setDeviceType(Constants.System_type.GRID_TYPE.RMU);
    // saveListCache.add(data);
    // }
    // }
    // if (saveListCache.size() > 0) {
    // dataPqsMapper.saveAllCache(schema, saveListCache);
    // }
    //
    // log.info("[SAVE] UPDATE LASTTIME");
    // dataPqsMapper.updateLastTime(schema, lastTime, tableYear.toString(), Constants.System_type.GRID,
    // Constants.System_type.GRID_TYPE.RMU);
    // }
    // log.info("!!![DONE]");
    // }
    // }

    public Map<MultiKey, DataPqs> convertListToMap(List<DataPqs> list) {

        Map<MultiKey, DataPqs> mapData = new HashMap<MultiKey, DataPqs>();

        for (DataPqs data : list) {
            MultiKey multikey = new MultiKey(data.getDeviceId(), data.getViewTime(), data.getViewType());
            mapData.put(multikey, data);
        }

        return mapData;
    }

    public DataPqs addData(DataPqs dataPqs, DataLoadFrame1 dataLoadFrame1, Float epCache) {
        dataPqs.setPTotal(dataLoadFrame1.getPTotal());
        Integer ep = dataLoadFrame1.getEp() > 0 ? dataLoadFrame1.getEp() : 0;
        Float epPds = dataPqs.getEp() != null ? dataPqs.getEp() : 0;
        Float epCa = epCache != null ? epCache : 0;
        Float epDiff = 0f;

        if (dataPqs.getViewType() == 3) {
            System.out.println("VT:" + dataPqs.getViewType());
            Map<String, Object> condition = new HashMap<>();
            condition.put("deviceId", dataLoadFrame1.getDeviceId());
            Setting setting = new Setting();
            setting = settingMapper.getSettingProject(condition);
            dataPqs.setAmountOfPeople(setting.getAmountOfPeople());
            dataPqs.setAreaOfFloor(setting.getAreaOfFloor());
            dataPqs.setEmissionFactorCo2Charcoal(setting.getEmissionFactorCo2Charcoal());
            dataPqs.setEmissionFactorCo2Electric(setting.getEmissionFactorCo2Electric());
            dataPqs.setEmissionFactorCo2Gasoline(setting.getEmissionFactorCo2Gasoline());
            log.info("!!!IMPORT SETTING PROJECT SUCCESS..");
        }

        if (ep > 0 && epCa != 0) {
            dataPqs.setEp(epPds + (dataLoadFrame1.getEp() - epCa));
            epDiff = (dataLoadFrame1.getEp() - epCa);
        } else {
            dataPqs.setEp(epPds);
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("device_id", dataLoadFrame1.getDeviceId());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = formatter.parse(dataLoadFrame1.getSentDate());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        float hour = Float.parseFloat(dataLoadFrame1.getSentDate()
            .substring(11, 13) + "."
            + dataLoadFrame1.getSentDate()
                .substring(14, 16));
        if (dayOfWeek == 1) {
            // Ngày chủ nhật không có giờ cao điểm
            if (hour >= 22.00 || hour < 04.00) {
                condition.put("setting_mst_id", Constants.Setting.GIO_THAP_DIEM);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setLowCostIn(0f);
                } else {
                    dataPqs.setLowCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getLowCostIn()
                        : 0);
                }
                dataPqs.setLowEp(epDiff + dataPqs.getLowEp());

            } else {
                condition.put("setting_mst_id", Constants.Setting.GIO_BINH_THUONG);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setNormalCostIn(0f);
                } else {
                    dataPqs.setNormalCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getNormalCostIn()
                        : 0);
                }
                dataPqs.setNormalEp(epDiff + dataPqs.getNormalEp());
            }

        } else {
            if (hour >= 22.00 || hour < 04.00) {
                condition.put("setting_mst_id", Constants.Setting.GIO_THAP_DIEM);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setLowCostIn(0f);
                } else {
                    dataPqs.setLowCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getLowCostIn()
                        : 0);
                }
                dataPqs.setLowEp(epDiff + dataPqs.getLowEp());
            } else if ( (hour >= 09.30 && hour < 11.30) || (hour >= 17.00 && hour < 20.00)) {
                condition.put("setting_mst_id", Constants.Setting.GIO_CAO_DIEM);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setHighCostIn(0f);
                } else {
                    dataPqs.setHighCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getHighCostIn()
                        : 0);
                }
                dataPqs.setHighEp(epDiff + dataPqs.getHighEp());
            } else {
                condition.put("setting_mst_id", Constants.Setting.GIO_BINH_THUONG);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setNormalCostIn(0f);
                } else {
                    dataPqs.setNormalCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getNormalCostIn()
                        : 0);
                }
                dataPqs.setNormalEp(epDiff + dataPqs.getNormalEp());
            }
        }

        return dataPqs;
    }

    public DataPqs addDataInverter(DataPqs dataPqs, DataInverter1 data, Float epCache) {
        dataPqs.setPTotal(data.getPtotal());
        Float ep = 0.f;
        Float epDiff = 0f;
        if (data != null) {
            ep = data.getEp() != null ? data.getEp() : 0.f;
        }

        Float epPds = dataPqs.getEp() != null ? dataPqs.getEp() : 0.f;
        Float epCa = epCache != null ? epCache : 0.f;

        if (ep > 0 && epCa != 0) {
            dataPqs.setEp(epPds + (data.getEp() - epCa));
            epDiff = (data.getEp() - epCa);
        } else {
            dataPqs.setEp(epPds);
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("device_id", data.getDeviceId());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = formatter.parse(data.getSentDate());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        float hour = Float.parseFloat(data.getSentDate()
            .substring(11, 13) + "."
            + data.getSentDate()
                .substring(14, 16));
        if (dayOfWeek == 1) {
            // Ngày chủ nhật không có giờ cao điểm
            if (hour >= 22.00 || hour < 04.00) {
                condition.put("setting_mst_id", Constants.Setting.GIO_THAP_DIEM);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setLowCostIn(0f);
                } else {
                    dataPqs.setLowCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getLowCostIn()
                        : 0);
                }
                dataPqs.setLowEp(epDiff + dataPqs.getLowEp());
            } else {
                condition.put("setting_mst_id", Constants.Setting.GIO_BINH_THUONG);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setNormalCostIn(0f);
                } else {
                    dataPqs.setNormalCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getNormalCostIn()
                        : 0);
                }
                dataPqs.setNormalEp(epDiff + dataPqs.getNormalEp());
            }

        } else {
            if (hour >= 22.00 || hour < 04.00) {
                condition.put("setting_mst_id", Constants.Setting.GIO_THAP_DIEM);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setLowCostIn(0f);
                } else {
                    dataPqs.setLowCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getLowCostIn()
                        : 0);
                }
                dataPqs.setLowEp(epDiff + dataPqs.getLowEp());
            } else if ( (hour >= 09.30 && hour < 11.30) || (hour >= 17.00 && hour < 20.00)) {
                condition.put("setting_mst_id", Constants.Setting.GIO_CAO_DIEM);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setHighCostIn(0f);
                } else {
                    dataPqs.setHighCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getHighCostIn()
                        : 0);
                }
                dataPqs.setHighEp(epDiff + dataPqs.getHighEp());
            } else {
                condition.put("setting_mst_id", Constants.Setting.GIO_BINH_THUONG);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setNormalCostIn(0f);
                } else {
                    dataPqs.setNormalCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * dataPqs.getEp()
                        : 0);
                }
                dataPqs.setNormalEp(epDiff + dataPqs.getNormalEp());
            }
        }
        return dataPqs;
    }

    public DataPqs addDataCombiner(DataPqs dataPqs, DataCombiner1 data, Float epCache) {
        dataPqs.setPTotal(data.getPOWER() != null ? Float.parseFloat(data.getPOWER() + "") : null);
        Float ep = 0.f;
        Float epDiff = 0f;
        if (data != null) {
            ep = data.getEpCombiner() != null ? data.getEpCombiner() : 0.f;
        }

        Float epPds = dataPqs.getEp() != null ? dataPqs.getEp() : 0.f;
        Float epCa = epCache != null ? epCache : 0.f;

        if (ep > 0 && epCa != 0) {
            dataPqs.setEp(epPds + (data.getEpCombiner() - epCa));
            epDiff = (data.getEpCombiner() - epCa);
        } else {
            dataPqs.setEp(epPds);
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("device_id", data.getDeviceId());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = formatter.parse(data.getSentDate());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        float hour = Float.parseFloat(data.getSentDate()
            .substring(11, 13) + "."
            + data.getSentDate()
                .substring(14, 16));
        if (dayOfWeek == 1) {
            // Ngày chủ nhật không có giờ cao điểm
            if (hour >= 22.00 || hour < 04.00) {
                condition.put("setting_mst_id", Constants.Setting.GIO_THAP_DIEM);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setLowCostIn(0f);
                } else {
                    dataPqs.setLowCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getLowCostIn()
                        : 0);
                }
                dataPqs.setLowEp(epDiff + dataPqs.getLowEp());
            } else {
                condition.put("setting_mst_id", Constants.Setting.GIO_BINH_THUONG);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setNormalCostIn(0f);
                } else {
                    dataPqs.setNormalCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getNormalCostIn()
                        : 0);
                }
                dataPqs.setNormalEp(epDiff + dataPqs.getNormalEp());
            }

        } else {
            if (hour >= 22.00 || hour < 04.00) {
                condition.put("setting_mst_id", Constants.Setting.GIO_THAP_DIEM);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setLowCostIn(0f);
                } else {
                    dataPqs.setLowCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getLowCostIn()
                        : 0);
                }
                dataPqs.setLowEp(epDiff + dataPqs.getLowEp());
            } else if ( (hour >= 09.30 && hour < 11.30) || (hour >= 17.00 && hour < 20.00)) {
                condition.put("setting_mst_id", Constants.Setting.GIO_CAO_DIEM);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setHighCostIn(0f);
                } else {
                    dataPqs.setHighCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getHighCostIn()
                        : 0);
                }
                dataPqs.setHighEp(epDiff + dataPqs.getHighEp());
            } else {
                condition.put("setting_mst_id", Constants.Setting.GIO_BINH_THUONG);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setNormalCostIn(0f);
                } else {
                    dataPqs.setNormalCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getNormalCostIn()
                        : 0);
                }
                dataPqs.setNormalEp(epDiff + dataPqs.getNormalEp());
            }
        }
        return dataPqs;
    }

    public DataPqs addDataRmuDrawer(DataPqs dataPqs, DataRmuDrawer1 data, Float epCache) {
        dataPqs.setPTotal(data.getPTotal() != null ? Float.parseFloat(data.getPTotal() + "") : null);
        Float ep = 0.f;
        Float epDiff = 0f;
        if (data != null) {
            ep = data.getEp() != null ? data.getEp() : 0.f;
        }

        Float epPds = dataPqs.getEp() != null ? dataPqs.getEp() : 0.f;
        Float epCa = epCache != null ? epCache : 0.f;

        if (ep > 0 && epCa != 0) {
            dataPqs.setEp(epPds + (data.getEp() - epCa));
            epDiff = (data.getEp() - epCa);
        } else {
            dataPqs.setEp(epPds);
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("device_id", data.getDeviceId());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = formatter.parse(data.getSentDate());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        float hour = Float.parseFloat(data.getSentDate()
            .substring(11, 13) + "."
            + data.getSentDate()
                .substring(14, 16));
        if (dayOfWeek == 1) {
            // Ngày chủ nhật không có giờ cao điểm
            if (hour >= 22.00 || hour < 04.00) {
                condition.put("setting_mst_id", Constants.Setting.GIO_THAP_DIEM);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setLowCostIn(0f);
                } else {
                    dataPqs.setLowCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getLowCostIn()
                        : 0);
                }
                dataPqs.setLowEp(epDiff + dataPqs.getLowEp());
            } else {
                condition.put("setting_mst_id", Constants.Setting.GIO_BINH_THUONG);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setNormalCostIn(0f);
                } else {
                    dataPqs.setNormalCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getNormalCostIn()
                        : 0);
                }
            }
            dataPqs.setNormalEp(epDiff + dataPqs.getNormalEp());
        } else {
            if (hour >= 22.00 || hour < 04.00) {
                condition.put("setting_mst_id", Constants.Setting.GIO_THAP_DIEM);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setLowCostIn(0f);
                } else {
                    dataPqs.setLowCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getLowCostIn()
                        : 0);
                }
                dataPqs.setLowEp(epDiff + dataPqs.getLowEp());
            } else if ( (hour >= 09.30 && hour < 11.30) || (hour >= 17.00 && hour < 20.00)) {
                condition.put("setting_mst_id", Constants.Setting.GIO_CAO_DIEM);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setHighCostIn(0f);
                } else {
                    dataPqs.setHighCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getHighCostIn()
                        : 0);
                }
                dataPqs.setHighEp(epDiff + dataPqs.getHighEp());
            } else {
                condition.put("setting_mst_id", Constants.Setting.GIO_BINH_THUONG);
                Setting setting = settingMapper.getSetting(condition);
                if (setting == null) {
                    dataPqs.setNormalCostIn(0f);
                } else {
                    dataPqs.setNormalCostIn(setting.getSettingValue() != null
                        ? Float.parseFloat(setting.getSettingValue()) * epDiff + dataPqs.getNormalCostIn()
                        : 0);
                }
                dataPqs.setNormalEp(epDiff + dataPqs.getNormalEp());
            }
        }
        return dataPqs;
    }

    public DataPqs addDataFlow(DataPqs dataPqs, DataFlow data, Double tCache) {
        Float ep = data.getT() > 0 ? data.getT() : 0;
        Double epPds = dataPqs.getT() != null ? dataPqs.getT() : 0;
        Double epCa = tCache != null ? tCache : 0;
        if (ep > 0 && epCa != 0) {
            dataPqs.setT(epPds + (data.getT() - epCa));
        } else {
            dataPqs.setT(epPds);
        }

        return dataPqs;
    }

}
