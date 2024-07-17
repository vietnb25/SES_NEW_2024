package vn.ses.s3m.plus.batch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ses.s3m.plus.batch.dto.DataAccumulations;
import vn.ses.s3m.plus.batch.dto.DataInstant;
import vn.ses.s3m.plus.batch.dto.Device;
import vn.ses.s3m.plus.batch.mapper.DataAccumulationsMapper;
import vn.ses.s3m.plus.batch.mapper.DataPqsMapper;

import javax.xml.validation.Schema;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Service
public class DataAccumulationsService {
    private static final Logger log = LoggerFactory.getLogger(DataAccumulationsService.class);

    @Autowired
    private DataAccumulationsMapper dataAccumulationsMapper;

    @Autowired
    private DataPqsMapper dataPqsMapper;

    public boolean doProcess() {
        log.info("DataAccumulationsService Start!");
        try {
            List<String> schemas = dataPqsMapper.getCustomerList("s3m_plus", "s3m_customer");
            this.process(schemas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("DataAccumulationsService END!");
        return true;
    }

    //  Xử lý tính data
    public void process(List<String> schemas) {
        String customer = "s3m_plus_customer";
        for (String schema : schemas) {
            try {
                boolean a = checkTableExistence((customer + "_" + schema), "s3m_data_accumulations");
                if (!a) {
                    createTable((customer + "_" + schema));
                }
//                Lấy ra danh sách thiết bị có trong customer rồi lấy ia, ib, ic từ bảng s3m_data_instant
//                và điện trở rho, chiều dài dây dẫn trong bảng s3m_device;
                List<Device> devices = this.dataAccumulationsMapper.getDeviceByCustomer(schema);
                for (Device d : devices) {
                    DataInstant instant = getInstansData(String.valueOf(d.getDeviceId()), (customer + "_" + schema));
                    if (instant != null) {
                        Double ep15 = EpAccumulations15min(instant, d.getCableLength(), d.getRho());
                        String schema1 = (customer + "_" + schema);
//                        Thêm vào 15 phút
                        this.insertDataAccumulations(ep15, schema1, instant, 1, "yyyy-MM-dd HH:mm", null);
//                       Thêm vào giờ
                        DataAccumulations dataHour = this.dataAccumulationsMapper.getDataAccumulationsHour(schema1, instant.getDeviceId(),convertDay("yyyy-MM-dd HH", instant.getSentDate()));
                        this.insertDataAccumulations(EpAccumulations1Hour(instant, ep15, dataHour != null ? dataHour : null), schema1, instant, 2, "yyyy-MM-dd HH", dataHour != null ? dataHour.getId() : null);
//                      Thêm vào ngày
                        DataAccumulations dataDay = this.dataAccumulationsMapper.getDataAccumulationsDay(schema1, instant.getDeviceId(),convertDay("yyyy-MM-dd", instant.getSentDate()));
                        this.insertDataAccumulations(EpAccumulations1Day(instant, ep15, dataDay != null ? dataDay : null), schema1, instant, 3, "yyyy-MM-dd", dataDay != null ? dataDay.getId() : null);
//                      Thêm vào tháng
                        DataAccumulations dataMonth = this.dataAccumulationsMapper.getDataAccumulationsMoth(schema1, instant.getDeviceId(),convertDay("yyyy-MM", instant.getSentDate()));
                        this.insertDataAccumulations(EpAccumulations1Month(instant, ep15, dataMonth != null ? dataMonth : null), schema1, instant, 4, "yyyy-MM", dataMonth != null ? dataMonth.getId() : null);

//                      Thêm vào năm
                        DataAccumulations dataYear = this.dataAccumulationsMapper.getDataAccumulationsYear(schema1, instant.getDeviceId(),convertDay("yyyy", instant.getSentDate()));
                        this.insertDataAccumulations(EpAccumulations1Year(instant, ep15, dataYear != null ? dataYear : null), schema1, instant, 5, "yyyy", dataYear != null ? dataYear.getId() : null);

                    }
                }
                continue;
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }


    public Boolean checkTableExistence(String schema, String table) {
        String tablename = this.dataAccumulationsMapper.checkTableExistence(schema, table);
        if (tablename != null) {
            return true;
        } else {
            return false;
        }
    }

    public void createTable(String schema) {
        this.dataAccumulationsMapper.createTable(schema);
    }

    //    Hàm này để tính ep tích lũy
    private Double EpAccumulations15min(DataInstant instant, Double cableLength, Double rho) {
        Double ia = null, ib = null, ic = null;
        Double ep = 0.0;
        if (instant == null || cableLength == null || rho == null || cableLength == 0 || rho == 0) {
            return null;
        }
        if (instant.getIa() == null) {
            ia = 0.0;
        } else {
            ia = instant.getIa();
        }
        if (instant.getIb() == null) {
            ib = 0.0;
        } else {
            ib = instant.getIb();
        }
        if (instant.getIc() == null) {
            ic = 0.0;
        } else {
            ic = instant.getIc();
        }
        ep = (Math.pow(ia, 2.0) + Math.pow(ib, 2.0) + Math.pow(ic, 2.0)) * cableLength * rho * 1 / 4 * 0.001;
        return roundToDecimalPlaces(ep, 2);
    }

    private Double EpAccumulations1Hour(DataInstant instant, Double ep, DataAccumulations dataOld) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        if (dataOld == null || !dataOld.getViewTime().equals(sdf.format(instant.getSentDate()))) {
            return roundToDecimalPlaces(ep, 2);
        }
        return roundToDecimalPlaces(dataOld.getEp() + ep, 2);
    }

    private Double EpAccumulations1Day(DataInstant instant, Double ep, DataAccumulations dataOld) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (dataOld == null || !dataOld.getViewTime().equals(sdf.format(instant.getSentDate()))) {
            return roundToDecimalPlaces(ep, 2);
        }
        return roundToDecimalPlaces(dataOld.getEp() + ep, 2);
    }

    private Double EpAccumulations1Month(DataInstant instant, Double ep, DataAccumulations dataOld) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        if (dataOld == null || !dataOld.getViewTime().equals(sdf.format(instant.getSentDate()))) {
            return roundToDecimalPlaces(ep, 2);
        } else {
            return roundToDecimalPlaces(dataOld.getEp() + ep, 2);
        }
    }

    private Double EpAccumulations1Year(DataInstant instant, Double ep, DataAccumulations dataOld) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        if (dataOld == null || !dataOld.getViewTime().equals(sdf.format(instant.getSentDate()))) {
            return roundToDecimalPlaces(ep, 2);
        } else {
            return roundToDecimalPlaces(dataOld.getEp() + ep, 2);
        }
    }

    //    Lấy ra ia,ib,ic
    private DataInstant getInstansData(String device, String schema) {
        DataInstant dataInstant = this.dataAccumulationsMapper.getDataInstansData(device, schema);
        return dataInstant;
    }


    public Double roundToDecimalPlaces(double value, int decimalPlaces) {
        double scale = Math.pow(10, decimalPlaces);
        return Math.floor(value * scale) / scale;
    }

    private void insertDataAccumulations(Double ep, String schema, DataInstant dataInstant, Integer typeTime, String pattern, Long id) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        DataAccumulations data = new DataAccumulations();
        data.setEp(ep);
        data.setViewTime(sdf.format(dataInstant.getSentDate()));
        data.setDeviceId(dataInstant.getDeviceId());
        data.setSentDate(dataInstant.getSentDate());
        data.setTypeTime(typeTime);
        if (id == null) {
            this.dataAccumulationsMapper.insertDataAccumulations(data, schema);
        } else {
            data.setId(id);
            this.dataAccumulationsMapper.updateDataAccumulations(data, schema);
        }
        log.info("DataAccumulations -> INSERT SUCCESS");
    }

    public String convertDay(String patten, Timestamp day) {
        SimpleDateFormat sdf = new SimpleDateFormat(patten);
        return sdf.format(day);
    }

}
