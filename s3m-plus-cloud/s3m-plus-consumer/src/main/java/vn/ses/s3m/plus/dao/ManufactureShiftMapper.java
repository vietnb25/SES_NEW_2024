package vn.ses.s3m.plus.dao;

import org.apache.ibatis.annotations.Mapper;
import vn.ses.s3m.plus.dto.ManufactureShift;
import vn.ses.s3m.plus.dto.ManufactureShiftDetail;
import vn.ses.s3m.plus.dto.ManufactureShiftDevices;
import vn.ses.s3m.plus.dto.Production;

import java.util.List;
import java.util.Map;

@Mapper
public interface ManufactureShiftMapper {
    List<ManufactureShift> getListByProject(Map<String, Object> con);
    List<ManufactureShiftDevices> getDeviceByManufactureShift(Map<String, Object> con);

    ManufactureShift addManufacturre (Map<String, Object> con);
    ManufactureShift getManufactureNew (Map<String, Object> con);
    void addManufacturreDevices(Map<String, Object> con);
    void updateManufactureDetailRevenue(Map<String, Object> con);
    void updateProductionNumber(Map<String, Object> con);
    void insertManufactureDetail(ManufactureShiftDetail manufactureShiftDetail, String schema);
    void updateManufactureDetail(Map<String, Object> con);
    void deleteManufacture(Map<String, Object> con);

    ManufactureShiftDevices getEpByShift(Map<String, Object> con);

    List<Production> getListManufactureByProductionStep(Map<String, Object> con);
    List<ManufactureShiftDetail> getListManufactureDetailByViewTimeAndManufacture(Map<String, Object> con);
    List<ManufactureShiftDetail> getReportManufacture(Map<String, Object> con);
}
