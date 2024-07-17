package vn.ses.s3m.plus.grid.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dto.DataRmuDrawer1;
import vn.ses.s3m.plus.grid.dao.DataRmuDrawer1Mapper;

@Service
public class OperationRmuDrawerServiceImpl implements OperationRmuDrawerService {

    @Autowired
    private DataRmuDrawer1Mapper dataRmuDrawer1Mapper;

    /**
     * Lấy dữ liệu tức thời khoang tủ RMU
     */
    @Override
    public DataRmuDrawer1 getInstantOperationRmuDrawerGrid(Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.getInstantOperationRmuDrawerGrid(condition);
    }

    /**
     * Lấy danh sách dữ liệu khoang tủ RMU
     */
    @Override
    public List<DataRmuDrawer1> getOperationRmuDrawerGrid(Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.getOperationRmuDrawerGrid(condition);
    }

    /**
     * Lấy tổng số dữ liệu khoang tủ RMU
     */
    @Override
    public Integer countTotalDataRmuDrawerGrid(Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.countTotalDataRmuDrawerGrid(condition);
    }

    /**
     * Lấy dữ liệu điện năng khoang tủ RMU
     */
    @Override
    public List<DataRmuDrawer1> getDataPQSByMonthRmuDrawerGrid(Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.getDataPQSByMonthRmuDrawerGrid(condition);
    }

    /**
     * Lấy tổng số tổng năng lượng tiêu thụ khoang tủ RMU
     */
    @Override
    public List<DataRmuDrawer1> getRmuEveryYearByDeviceId(Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.getRmuEveryYearByDeviceId(condition);
    }

    /**
     * Lấy tổng số tổng năng lượng tiêu thụ trong ngày khoang tủ RMU
     */
    @Override
    public DataRmuDrawer1 getRmuInDayByDeviceId(Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.getRmuInDayByDeviceId(condition);
    }

    /**
     * Lấy tổng số tổng năng lượng tiêu thụ trong tháng khoang tủ RMU
     */
    @Override
    public DataRmuDrawer1 getRmuInMonthByDeviceId(Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.getRmuInMonthByDeviceId(condition);
    }

    /**
     * Lấy tổng số tổng năng lượng tiêu thụ trong năm khoang tủ RMU
     */
    @Override
    public DataRmuDrawer1 getRmuInYearByDeviceId(Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.getRmuInYearByDeviceId(condition);
    }

    /**
     * Lấy tổng số tổng năng lượng tiêu thụ trong ngày hôm trước khoang tủ RMU
     */
    @Override
    public DataRmuDrawer1 getRmuInPrevDayByDeviceId(Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.getRmuInPrevDayByDeviceId(condition);
    }

    /**
     * Lấy tổng số tổng năng lượng tiêu thụ trong tháng trước khoang tủ RMU
     */
    @Override
    public DataRmuDrawer1 getRmuInPrevMonthByDeviceId(Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.getRmuInPrevMonthByDeviceId(condition);
    }

    /**
     * Lấy tổng số tổng năng lượng tiêu thụ trong năm trước khoang tủ RMU
     */
    @Override
    public DataRmuDrawer1 getRmuInPrevYearByDeviceId(Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.getRmuInPrevYearByDeviceId(condition);
    }

    /**
     * Lấy danh sách dữ liệu và thông tin khoang tủ RMU
     */
    @Override
    public List<DataRmuDrawer1> getDataRmuDrawerGrid(Map<String, Object> condition) {
        return dataRmuDrawer1Mapper.getDataRmuDrawerGrid(condition);
    }

}
