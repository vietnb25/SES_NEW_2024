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
import vn.ses.s3m.plus.batch.dto.DataCombiner1;
import vn.ses.s3m.plus.batch.dto.DataCombiner1View;
import vn.ses.s3m.plus.batch.dto.DataCombiner1ViewCache;
import vn.ses.s3m.plus.batch.mapper.DataCombiner1Mapper;
import vn.ses.s3m.plus.batch.mapper.DataCombiner1ViewCacheMapper;
import vn.ses.s3m.plus.batch.mapper.DataCombiner1ViewMapper;

@Service
public class DataCombiner1ViewService {

    private static final Logger log = LoggerFactory.getLogger(DataCombiner1ViewService.class);

    private Map<MultiKey, DataCombiner1View> map = new HashMap<MultiKey, DataCombiner1View>();

    private Map<Long, DataCombiner1ViewCache> mapCache = new HashMap<Long, DataCombiner1ViewCache>();

    @Autowired
    private DataCombiner1Mapper dataCombiner1Mapper;

    @Autowired
    private DataCombiner1ViewMapper dataCombiner1ViewMapper;

    @Autowired
    private DataCombiner1ViewCacheMapper dataCombiner1ViewCacheMapper;

    public boolean doProcess() {
        // ----- FLAG CHECK ALL CUSTOMER HAVE DATA OR NOT
        boolean isNoData = true;
        // get list customer in system
        List<String> schemas = dataCombiner1ViewMapper.getCustomerList("s3m_plus", "s3m_customer");
        for (String schema : schemas) {
            schema = "s3m_plus_customer_" + schema;

            log.info("!!!!!CUSTOMER-COMBINER: [" + schema + "]........");
            DataCombiner1View lastData = null;
            try {
                lastData = dataCombiner1ViewMapper.selectLastestTime(schema);
                if (lastData == null) {
                    log.error("!!!First record in s3m_data_combiner_1_view is not exist");
                    log.error(
                        "!!!Please insert the first record to [s3m_data_combiner_1_view] with [view_time] = 0 and [sent_date] = 2022");
                    continue;
                }
            } catch (Exception e) {
                log.error("!!!SCHEMA [" + schema + "] IS NOT EXIST");
                continue;
            }
            String lastTime = lastData.getViewTime();
            Integer tableYear = Integer.valueOf(lastData.getSentDate());
            String tableName = "s3m_data_combiner_1_" + tableYear;
            // get data table s3m_data_combiner_1 in year
            List<DataCombiner1> datas = dataCombiner1Mapper.selectNewRecord(schema, tableName,
                lastTime != null ? lastTime : "0");
            if (datas == null || datas.size() == 0) {
                tableYear++;
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                while (year >= tableYear && (datas == null || datas.size() == 0)) {
                    tableName = "s3m_data_combiner_1_" + tableYear;
                    datas = dataCombiner1Mapper.selectNewRecord(schema, tableName, "0");
                    if (datas == null || datas.size() == 0) {
                        tableYear++;
                    }
                }
            }
            // insert data to tabble view
            if (datas != null && datas.size() > 0) {
                // ----- UPDATE FLAG CUSTOMER HAVE DATA
                isNoData = false;
                List<DataCombiner1View> dataCombiner1Views = dataCombiner1ViewMapper.selectByTypeLastTime(schema);

                if (dataCombiner1Views.size() > 0) {
                    map = convertListToMap(dataCombiner1Views);
                }
                List<DataCombiner1ViewCache> cache = dataCombiner1ViewCacheMapper.selectAll(schema);
                mapCache = cache.stream()
                    .collect(Collectors.toMap(DataCombiner1ViewCache::getDeviceId, Function.identity()));

                lastTime = datas.get(datas.size() - 1)
                    .getId()
                    .toString();

                // -----ADD NEW DATA TO OLD DATA OR CREATE NEW RECORD-----
                log.info("!!!PROCESSING.........");
                for (DataCombiner1 data : datas) {
                    String sentDate = data.getSentDate();
                    String year = sentDate.substring(0, 4);
                    String month = sentDate.substring(0, 7);
                    String day = sentDate.substring(0, 10);
                    String hour = sentDate.substring(0, 13) + ":00:00";
                    String minuate15 = sentDate.substring(14, 16);
                    int minuate15Int = Integer.valueOf(minuate15);
                    if (minuate15Int < 15) {
                        minuate15 = sentDate.substring(0, 14) + "00:00";
                    } else if (minuate15Int < 30) {
                        minuate15 = sentDate.substring(0, 14) + "15:00";

                    } else if (minuate15Int < 45) {
                        minuate15 = sentDate.substring(0, 14) + "30:00";

                    } else if (minuate15Int <= 59) {
                        minuate15 = sentDate.substring(0, 14) + "45:00";
                    }

                    DataCombiner1ViewCache powerCache = mapCache.get(data.getDeviceId());

                    if (powerCache == null) {
                        powerCache = new DataCombiner1ViewCache();
                        powerCache.setDeviceId(data.getDeviceId());
                        powerCache.setEpCombiner(data.getEpCombiner());
                    }
                    // add data view minuate
                    MultiKey multikey = new MultiKey(data.getDeviceId(), minuate15, Constants.MINUTE_15);

                    DataCombiner1View minuteData = map.get(multikey);
                    if (minuteData == null) {
                        minuteData = new DataCombiner1View();
                        minuteData.setDeviceId(data.getDeviceId());
                        minuteData.setViewTime(minuate15);
                        minuteData.setViewType(Constants.MINUTE_15);
                    }
                    minuteData = addData(minuteData, data, powerCache.getEpCombiner());
                    map.put(multikey, minuteData);

                    // add data view hour
                    multikey = new MultiKey(data.getDeviceId(), hour, Constants.HOUR);

                    DataCombiner1View hourData = map.get(multikey);
                    if (hourData == null) {
                        hourData = new DataCombiner1View();
                        hourData.setDeviceId(data.getDeviceId());
                        hourData.setViewTime(hour);
                        hourData.setViewType(Constants.HOUR);

                    }
                    hourData = addData(hourData, data, powerCache.getEpCombiner());
                    map.put(multikey, hourData);

                    // add data view DAY
                    multikey = new MultiKey(data.getDeviceId(), day, Constants.DAY);

                    DataCombiner1View dayData = map.get(multikey);
                    if (dayData == null) {
                        dayData = new DataCombiner1View();
                        dayData.setDeviceId(data.getDeviceId());
                        dayData.setViewTime(day);
                        dayData.setViewType(Constants.DAY);

                    }
                    dayData = addData(minuteData, data, powerCache.getEpCombiner());
                    map.put(multikey, dayData);

                    // add data view month
                    multikey = new MultiKey(data.getDeviceId(), month, Constants.MONTH);

                    DataCombiner1View monthData = map.get(multikey);
                    if (monthData == null) {
                        monthData = new DataCombiner1View();
                        monthData.setDeviceId(data.getDeviceId());
                        monthData.setViewTime(month);
                        monthData.setViewType(Constants.MONTH);

                    }
                    monthData = addData(monthData, data, powerCache.getEpCombiner());
                    map.put(multikey, monthData);

                    // add data view year
                    multikey = new MultiKey(data.getDeviceId(), year, Constants.YEAR);

                    DataCombiner1View yearData = map.get(multikey);
                    if (yearData == null) {
                        yearData = new DataCombiner1View();
                        yearData.setDeviceId(data.getDeviceId());
                        yearData.setViewTime(year);
                        yearData.setViewType(Constants.YEAR);

                    }
                    yearData = addData(yearData, data, powerCache.getEpCombiner());
                    map.put(multikey, yearData);

                    powerCache.setEpCombiner(data.getEpCombiner());
                    mapCache.put(data.getDeviceId(), powerCache);

                }
                // -----SAVE DATA-----
                log.info("!!!WRITING.........");
                List<DataCombiner1View> itemData = map.values()
                    .stream()
                    .collect(Collectors.toList());
                List<DataCombiner1View> saveList = new ArrayList<>();
                for (DataCombiner1View data : itemData) {
                    if (data.getId() != null) {
                        dataCombiner1ViewMapper.update(schema, data);
                    } else {
                        saveList.add(data);
                    }
                }
                if (saveList.size() > 0) {
                    dataCombiner1ViewMapper.saveAll(schema, saveList);
                }
                List<DataCombiner1ViewCache> itemDataCache = mapCache.values()
                    .stream()
                    .collect(Collectors.toList());
                List<DataCombiner1ViewCache> saveListCache = new ArrayList<>();
                for (DataCombiner1ViewCache data : itemDataCache) {
                    if (data.getId() != null) {
                        dataCombiner1ViewCacheMapper.update(schema, data);
                    } else {
                        saveListCache.add(data);
                    }
                }
                if (saveListCache.size() > 0) {
                    dataCombiner1ViewCacheMapper.saveAll(schema, saveListCache);
                }
                log.info("[SAVE] UPDATE LASTTIME");

                dataCombiner1ViewMapper.updateLastTime(schema, lastTime, tableYear.toString());
            }
            log.info("!!![DONE]");

        }
        return isNoData;
    }

    public Map<MultiKey, DataCombiner1View> convertListToMap(List<DataCombiner1View> list) {

        Map<MultiKey, DataCombiner1View> mapData = new HashMap<MultiKey, DataCombiner1View>();

        for (DataCombiner1View data : list) {
            MultiKey multikey = new MultiKey(data.getDeviceId(), data.getViewTime(), data.getViewType());
            mapData.put(multikey, data);
        }

        return mapData;
    }

    public DataCombiner1View addData(DataCombiner1View dataCombiner1View, DataCombiner1 dataCombiner1,
        Float wattHoursCache) {

        Float EpView = dataCombiner1View.getEpCombiner() != null && dataCombiner1View.getEpCombiner() > 0
            ? dataCombiner1View.getEpCombiner()
            : 0;
        Float Ep = dataCombiner1.getEpCombiner() != null && dataCombiner1.getEpCombiner() > 0
            ? dataCombiner1.getEpCombiner()
            : 0;
        Float EpCache = wattHoursCache != null && wattHoursCache > 0 ? wattHoursCache : 0;
        dataCombiner1View.setEpCombiner(EpView + (Ep - EpCache));
        dataCombiner1View.setPOWER(dataCombiner1.getPOWER());
        dataCombiner1View.setPdcCombiner(dataCombiner1.getPdcCombiner());
        return dataCombiner1View;

    }
}
