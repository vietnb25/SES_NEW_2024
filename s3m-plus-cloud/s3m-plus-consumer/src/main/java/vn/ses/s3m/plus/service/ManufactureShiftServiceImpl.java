package vn.ses.s3m.plus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ses.s3m.plus.dao.ManufactureShiftMapper;
import vn.ses.s3m.plus.dto.ManufactureShift;
import vn.ses.s3m.plus.dto.ManufactureShiftDetail;
import vn.ses.s3m.plus.dto.ManufactureShiftDevices;
import vn.ses.s3m.plus.dto.Production;

import java.util.List;
import java.util.Map;

@Service
public class ManufactureShiftServiceImpl implements ManufactureShiftService{
    @Autowired
    private ManufactureShiftMapper mapper;
    @Override
    public List<ManufactureShift> getListByProject(Map<String, Object> con) {
        return this.mapper.getListByProject(con);
    }

    @Override
    public List<ManufactureShiftDevices> getDeviceByManufactureShift(Map<String, Object> con) {
        return this.mapper.getDeviceByManufactureShift(con);
    }

    @Override
    public ManufactureShift addManufacturre(Map<String, Object> con) {
        return this.mapper.addManufacturre(con);
    }

    @Override
    public void addManufacturreDevices(Map<String, Object> con) {
        this.mapper.addManufacturreDevices(con);
    }

    @Override
    public ManufactureShiftDevices getEpByShift(Map<String, Object> con) {
        return this.mapper.getEpByShift(con);
    }

    @Override
    public ManufactureShift getManufactureNew(Map<String, Object> con) {
        return this.mapper.getManufactureNew(con);
    }

    @Override
    public void updateProductionNumber(Map<String, Object> con) {
        this.mapper.updateProductionNumber(con);
    }

    @Override
    public List<Production> getListManufactureByProductionStep(Map<String, Object> con) {
        return this.mapper.getListManufactureByProductionStep(con);
    }

    @Override
    public List<ManufactureShiftDetail> getListManufactureDetailByViewTimeAndManufacture(Map<String, Object> con) {
        return this.mapper.getListManufactureDetailByViewTimeAndManufacture(con);
    }

    @Override
    public void insertManufactureDetail(ManufactureShiftDetail manufactureShiftDetail, String schema) {
        this.mapper.insertManufactureDetail(manufactureShiftDetail, schema);
    }

    @Override
    public void updateManufactureDetail(Map<String, Object> con) {
        this.mapper.updateManufactureDetail(con);
    }

    @Override
    public void deleteManufacture(Map<String, Object> con) {
        this.mapper.deleteManufacture(con);
    }

    @Override
    public List<ManufactureShiftDetail> getReportManufacture(Map<String, Object> con) {
        return this.mapper.getReportManufacture(con);
    }

    @Override
    public void updateManufactureDetailRevenue(Map<String, Object> con) {
        this.mapper.updateManufactureDetailRevenue(con);
    }
}
