package vn.ses.s3m.plus.grid.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.DataRmuDrawer1;

@Mapper
public interface DataRmuDrawer1Mapper {

    DataRmuDrawer1 getInstantOperationRmuDrawerGrid(Map<String, Object> condition);

    List<DataRmuDrawer1> getOperationRmuDrawerGrid(Map<String, Object> condition);

    Integer countTotalDataRmuDrawerGrid(Map<String, Object> condition);

    List<DataRmuDrawer1> getDataPQSByMonthRmuDrawerGrid(Map<String, Object> condition);
    List<DataRmuDrawer1> getRmuEveryYearByDeviceId(Map<String, Object> condition);

    DataRmuDrawer1 getRmuInDayByDeviceId(Map<String, Object> condition);

    DataRmuDrawer1 getRmuInMonthByDeviceId(Map<String, Object> condition);

    DataRmuDrawer1 getRmuInYearByDeviceId(Map<String, Object> condition);

    DataRmuDrawer1 getRmuInPrevDayByDeviceId(Map<String, Object> condition);

    DataRmuDrawer1 getRmuInPrevMonthByDeviceId(Map<String, Object> condition);

    DataRmuDrawer1 getRmuInPrevYearByDeviceId(Map<String, Object> condition);

    List<DataRmuDrawer1> getDataRmuDrawerGrid(Map<String, Object> condition);
}
