package vn.ses.s3m.plus.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.batch.dto.DataPlanEnergy;
import vn.ses.s3m.plus.batch.dto.Device;

@Mapper
public interface DataPlanEnergyMapper {

    List<String> getListProjectId(@Param ("customerId") String customerId);

    List<Device> getListDevice(@Param ("projectId") String projectId, @Param ("systemTypeId") int systemTypeId);

    List<String> getLastListDeviceIdData(@Param ("schema") String schema, @Param ("projectId") String projectId,
        @Param ("systemTypeId") int systemTypeId);

    DataPlanEnergy getFirstDataLoad(@Param ("schema") String schema, @Param ("table") String table,
        @Param ("year") String year, @Param ("deviceId") long deviceId);

    DataPlanEnergy getLastDataLoad(@Param ("schema") String schema, @Param ("table") String table,
        @Param ("year") String year, @Param ("deviceId") long deviceId);

    DataPlanEnergy getData24Hours(@Param ("schema") String schema, @Param ("table") String table,
        @Param ("year") String year, @Param ("deviceId") long deviceId, @Param ("fromDate") String fromDate,
        @Param ("toDate") String toDate);

    void saveData(@Param ("schema") String schema, @Param ("data") DataPlanEnergy data);

    void deleteData(@Param ("schema") String schema, @Param ("data") String data);

    Device getDeviceByDeviceId(@Param ("deviceId") String deviceId);

    List<String> getIdData(@Param ("schema") String schema, @Param ("projectId") String projectId,
        @Param ("data") String data);

}
