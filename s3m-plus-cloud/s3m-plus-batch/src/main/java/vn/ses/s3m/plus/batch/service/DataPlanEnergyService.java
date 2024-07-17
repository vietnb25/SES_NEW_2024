package vn.ses.s3m.plus.batch.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.batch.common.Constants;
import vn.ses.s3m.plus.batch.common.DateUtils;
import vn.ses.s3m.plus.batch.dto.DataPlanEnergy;
import vn.ses.s3m.plus.batch.dto.Device;
import vn.ses.s3m.plus.batch.mapper.DataPlanEnergyMapper;
import vn.ses.s3m.plus.batch.mapper.DataPqsMapper;

@Service
public class DataPlanEnergyService {

    private static final Logger log = LoggerFactory.getLogger(DataPlanEnergy.class);

    @Autowired
    private DataPlanEnergyMapper dataPlanEnergyMapper;

    @Autowired
    private DataPqsMapper dataPqsMapper;

    public boolean doProcess() {
        // ----- FLAG CHECK ALL CUSTOMER HAVE DATA OR NOT
        boolean isNoData = true;

        List<String> customerIds = dataPqsMapper.getCustomerList("s3m_plus", "s3m_customer");
        processLoad(customerIds, isNoData);
        processSolar(customerIds, isNoData);
        processGrid(customerIds, isNoData);

        return isNoData;
    }

    public void processLoad(List<String> customerIds, boolean isNoData) {
        log.info("!!!READING PLAN ENERGY LOAD.........");

        String year = DateUtils.toString(new Date(), "yyyy");

        for (String customerId : customerIds) {
            String schema = "s3m_plus_customer_" + customerId;
            List<String> projectIds = dataPlanEnergyMapper.getListProjectId(customerId);
            for (String projectId : projectIds) {
                log.info("!!!READING PROJECT " + projectId);
                List<Device> devices = dataPlanEnergyMapper.getListDevice(projectId, Constants.System_type.LOAD);
                List<String> lastDeviceIds = null;

                lastDeviceIds = dataPlanEnergyMapper.getLastListDeviceIdData(schema, projectId,
                    Constants.System_type.LOAD);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (lastDeviceIds.size() == 0) {
                    log.info("!!!SIZE DEVICE IDS IS 0");
                    for (Device device : devices) {
                        DataPlanEnergy dataPlanEnergy = new DataPlanEnergy();
                        if (device.getDeviceType() == 1) {
                            String table = "s3m_data_meter_1_" + year;
                            DataPlanEnergy dataFirst = dataPlanEnergyMapper.getFirstDataLoad(schema, table, year,
                                device.getDeviceId());
                            DataPlanEnergy dataLast = dataPlanEnergyMapper.getLastDataLoad(schema, table, year,
                                device.getDeviceId());

                            Date dateStart = new Date();
                            Date dateLast = new Date();
                            Calendar calendar = Calendar.getInstance();
                            String dateAfter24Hours = "";
                            try {
                                dateStart = formatter.parse(dataFirst.getTimeStart());
                                calendar.setTime(dateStart);
                                calendar.roll(Calendar.DATE, true);
                                dateAfter24Hours = formatter.format(calendar.getTime());
                                dateLast = formatter.parse(dataLast.getTimeEnd());

                                long day = (dateLast.getTime() - dateStart.getTime()) / (24 * 3600 * 1000);

                                if (day >= 1) {
                                    String fromDate = dataFirst.getTimeStart();
                                    String toDate = dateAfter24Hours;
                                    dataPlanEnergy.setTimeStart(dataFirst.getTimeStart());
                                    dataPlanEnergy.setTimeEnd(dataLast.getTimeEnd());
                                    dataPlanEnergy.setDeviceId(device.getDeviceId());
                                    dataPlanEnergy.setProjectId(Integer.valueOf(projectId));
                                    dataPlanEnergy.setSystemTypeId(Constants.System_type.LOAD);
                                    DataPlanEnergy dataIn24Hours = dataPlanEnergyMapper.getData24Hours(schema, table,
                                        year, device.getDeviceId(), fromDate, toDate);
                                    if (dataIn24Hours != null) {
                                        if (dataIn24Hours.getEp() != null && dataFirst.getEp() != null) {
                                            Float ep = dataIn24Hours.getEp() - dataFirst.getEp();
                                            dataPlanEnergy.setEp(ep);
                                        } else {
                                            dataPlanEnergy.setEp(0f);
                                        }
                                    }
                                    // INSERT NEW DATA LOAD WHEN IS NO DATA LAST
                                    log.info("!!!INSERT NEW DATA LOAD WHEN IS NO DATA LAST.........");
                                    dataPlanEnergyMapper.saveData(schema, dataPlanEnergy);
                                }
                            } catch (Exception e) {
                                log.error("!!!DEVICEID [" + device.getDeviceId() + "] IS NOT DATA FIRST.........");
                                continue;
                            }
                        }
                    }
                } else {
                    log.info("!!!SIZE DEVICE IDS IS NOT 0");
                    HashSet<String> listId = new HashSet<String>();
                    for (Device device : devices) {
                        listId.add(String.valueOf(device.getDeviceId()));
                    }
                    List<String> listDataDel = new ArrayList<>();
                    for (String deviceId : lastDeviceIds) {
                        if (listId.contains(deviceId) == false) {
                            listDataDel.add(deviceId);
                        } else {
                            listId.remove(deviceId);
                        }
                    }

                    if (listDataDel.size() > 0) {
                        String ids = String.join(",", listDataDel);
                        List<String> listIdData = dataPlanEnergyMapper.getIdData(schema, projectId, ids);
                        String idss = String.join(",", listIdData);
                        dataPlanEnergyMapper.deleteData(schema, idss);
                    }

                    for (String id : listId) {
                        DataPlanEnergy dataPlanEnergy = new DataPlanEnergy();
                        Device device = dataPlanEnergyMapper.getDeviceByDeviceId(id);
                        if (device.getDeviceType() == 1) {
                            String table = "s3m_data_meter_1_" + year;
                            DataPlanEnergy dataFirst = dataPlanEnergyMapper.getFirstDataLoad(schema, table, year,
                                device.getDeviceId());
                            DataPlanEnergy dataLast = dataPlanEnergyMapper.getLastDataLoad(schema, table, year,
                                device.getDeviceId());
                            Date dateStart = new Date();
                            Date dateLast = new Date();
                            Calendar calendar = Calendar.getInstance();
                            String dateAfter24Hours = "";
                            try {
                                dateStart = formatter.parse(dataFirst.getTimeStart());
                                calendar.setTime(dateStart);
                                calendar.roll(Calendar.DATE, true);
                                dateAfter24Hours = formatter.format(calendar.getTime());
                                dateLast = formatter.parse(dataLast.getTimeEnd());

                                long day = (dateLast.getTime() - dateStart.getTime()) / (24 * 3600 * 1000);

                                if (day > 1) {
                                    String fromDate = dataFirst.getTimeStart();
                                    String toDate = dateAfter24Hours;
                                    dataPlanEnergy.setTimeStart(dataFirst.getTimeStart());
                                    dataPlanEnergy.setTimeEnd(dataLast.getTimeEnd());
                                    dataPlanEnergy.setDeviceId(device.getDeviceId());
                                    dataPlanEnergy.setProjectId(Integer.valueOf(projectId));
                                    dataPlanEnergy.setSystemTypeId(Constants.System_type.LOAD);
                                    DataPlanEnergy dataIn24Hours = dataPlanEnergyMapper.getData24Hours(schema, table,
                                        year, device.getDeviceId(), fromDate, toDate);
                                    if (dataIn24Hours != null) {
                                        if (dataIn24Hours.getEp() != null && dataFirst.getEp() != null) {
                                            Float ep = dataIn24Hours.getEp() - dataFirst.getEp();
                                            dataPlanEnergy.setEp(ep);
                                        } else {
                                            dataPlanEnergy.setEp(0f);
                                        }
                                    }
                                    // INSERT NEW DATA LOAD WHEN IS DATA LAST
                                    log.info("!!!INSERT NEW DATA LOAD WHEN IS DATA LAST.........");
                                    dataPlanEnergyMapper.saveData(schema, dataPlanEnergy);
                                }
                            } catch (Exception e) {
                                log.error("!!!DEVICEID [" + device.getDeviceId() + "] IS NOT DATA FIRST.........");
                                continue;
                            }
                        }
                    }
                }

            }

        }
    }

    public void processSolar(List<String> customerIds, boolean isNoData) {
        log.info("!!!READING PLAN ENERGY SOLAR.........");

        String year = DateUtils.toString(new Date(), "yyyy");

        for (String customerId : customerIds) {
            String schema = "s3m_plus_customer_" + customerId;
            List<String> projectIds = dataPlanEnergyMapper.getListProjectId(customerId);

            for (String projectId : projectIds) {
                List<Device> devices = dataPlanEnergyMapper.getListDevice(projectId, Constants.System_type.SOLAR);
                List<String> lastDeviceIds = null;

                lastDeviceIds = dataPlanEnergyMapper.getLastListDeviceIdData(schema, projectId,
                    Constants.System_type.SOLAR);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                if (lastDeviceIds.size() == 0) {
                    for (Device device : devices) {
                        DataPlanEnergy dataPlanEnergy = new DataPlanEnergy();
                        if (device.getDeviceType() == 1) {
                            String table = "s3m_data_inverter_1_" + year;
                            DataPlanEnergy dataFirst = dataPlanEnergyMapper.getFirstDataLoad(schema, table, year,
                                device.getDeviceId());
                            DataPlanEnergy dataLast = dataPlanEnergyMapper.getLastDataLoad(schema, table, year,
                                device.getDeviceId());
                            Date dateStart = new Date();
                            Date dateLast = new Date();
                            Calendar calendar = Calendar.getInstance();
                            String dateAfter24Hours = "";
                            try {
                                dateStart = formatter.parse(dataFirst.getTimeStart());
                                calendar.setTime(dateStart);
                                calendar.roll(Calendar.DATE, true);
                                dateAfter24Hours = formatter.format(calendar.getTime());
                                dateLast = formatter.parse(dataLast.getTimeEnd());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            long day = (dateLast.getTime() - dateStart.getTime()) / (24 * 3600 * 1000);

                            if (day > 1) {
                                String fromDate = dataFirst.getTimeStart();
                                String toDate = dateAfter24Hours;
                                dataPlanEnergy.setTimeStart(dataFirst.getTimeStart());
                                dataPlanEnergy.setTimeEnd(dataLast.getTimeEnd());
                                dataPlanEnergy.setDeviceId(device.getDeviceId());
                                dataPlanEnergy.setProjectId(Integer.valueOf(projectId));
                                dataPlanEnergy.setSystemTypeId(Constants.System_type.SOLAR);
                                DataPlanEnergy dataIn24Hours = dataPlanEnergyMapper.getData24Hours(schema, table, year,
                                    device.getDeviceId(), fromDate, toDate);
                                if (dataIn24Hours != null) {
                                    if (dataIn24Hours.getEp() != null && dataFirst.getEp() != null) {
                                        Float ep = dataIn24Hours.getEp() - dataFirst.getEp();
                                        dataPlanEnergy.setEp(ep);
                                    } else {
                                        dataPlanEnergy.setEp(0f);
                                    }
                                }
                                // INSERT NEW DATA SOLAR WHEN IS NO DATA LAST
                                dataPlanEnergyMapper.saveData(schema, dataPlanEnergy);
                            }
                        }
                    }
                } else {
                    HashSet<String> listId = new HashSet<String>();
                    for (Device device : devices) {
                        listId.add(String.valueOf(device.getDeviceId()));
                    }
                    List<String> listDataDel = new ArrayList<>();
                    for (String deviceId : lastDeviceIds) {
                        if (listId.contains(deviceId) == false) {
                            listDataDel.add(deviceId);
                        } else {
                            listId.remove(deviceId);
                        }
                    }

                    if (listDataDel.size() > 0) {
                        String ids = String.join(",", listDataDel);
                        List<String> listIdData = dataPlanEnergyMapper.getIdData(schema, projectId, ids);
                        String idss = String.join(",", listIdData);
                        dataPlanEnergyMapper.deleteData(schema, idss);
                    }

                    for (String id : listId) {
                        DataPlanEnergy dataPlanEnergy = new DataPlanEnergy();
                        Device device = dataPlanEnergyMapper.getDeviceByDeviceId(id);
                        if (device.getDeviceType() == 1) {
                            String table = "s3m_data_inverter_1_" + year;
                            DataPlanEnergy dataFirst = dataPlanEnergyMapper.getFirstDataLoad(schema, table, year,
                                device.getDeviceId());
                            DataPlanEnergy dataLast = dataPlanEnergyMapper.getLastDataLoad(schema, table, year,
                                device.getDeviceId());
                            Date dateStart = new Date();
                            Date dateLast = new Date();
                            Calendar calendar = Calendar.getInstance();
                            String dateAfter24Hours = "";
                            try {
                                dateStart = formatter.parse(dataFirst.getTimeStart());
                                calendar.setTime(dateStart);
                                calendar.roll(Calendar.DATE, true);
                                dateAfter24Hours = formatter.format(calendar.getTime());
                                dateLast = formatter.parse(dataLast.getTimeEnd());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            long day = (dateLast.getTime() - dateStart.getTime()) / (24 * 3600 * 1000);

                            if (day > 1) {
                                String fromDate = dataFirst.getTimeStart();
                                String toDate = dateAfter24Hours;
                                dataPlanEnergy.setTimeStart(dataFirst.getTimeStart());
                                dataPlanEnergy.setTimeEnd(dataLast.getTimeEnd());
                                dataPlanEnergy.setDeviceId(device.getDeviceId());
                                dataPlanEnergy.setProjectId(Integer.valueOf(projectId));
                                dataPlanEnergy.setSystemTypeId(Constants.System_type.SOLAR);
                                DataPlanEnergy dataIn24Hours = dataPlanEnergyMapper.getData24Hours(schema, table, year,
                                    device.getDeviceId(), fromDate, toDate);
                                if (dataIn24Hours != null) {
                                    if (dataIn24Hours.getEp() != null && dataFirst.getEp() != null) {
                                        Float ep = dataIn24Hours.getEp() - dataFirst.getEp();
                                        dataPlanEnergy.setEp(ep);
                                    } else {
                                        dataPlanEnergy.setEp(0f);
                                    }
                                }
                                // INSERT NEW DATA SOLAR WHEN IS DATA LAST
                                dataPlanEnergyMapper.saveData(schema, dataPlanEnergy);
                            }
                        }
                    }
                }
            }
        }

    }

    public void processGrid(List<String> customerIds, boolean isNoData) {

    }

}
