package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.dto.DataPower;
import vn.ses.s3m.plus.dto.DataPowerResult;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.DeviceMst;
import vn.ses.s3m.plus.dto.DeviceName;
import vn.ses.s3m.plus.dto.LandmarksPlansEnergy;
import vn.ses.s3m.plus.dto.ObjectType;
import vn.ses.s3m.plus.form.DeviceForm;
import vn.ses.s3m.plus.form.DeviceMstForm;
import vn.ses.s3m.plus.response.DeviceLostSignalResponse;

@Mapper
public interface DeviceMapper {

    List<DeviceLostSignalResponse> getWarnedDeviceLostSignal(Map<String, Object> condition);

    Device getDataInstanceGateway(Map<String, Object> condition);

    List<Device> getIds(@Param ("deviceIds") String[] deviceIds, @Param ("schema") String schema);

    Device getDevice(@Param ("deviceId") Integer deviceId);

    DeviceMst getDeviceById(Map<String, String> condition);

    Device getDeviceByDeviceCode(Map<String, String> condition);

    List<DeviceMst> getDevices(Map<String, String> condition);

    Device getDeviceName(String deviceId);

    Device getDeviceByDeviceName(String deviceName);

    void updateDevice(Map<String, Object> condition);

    void updateDeviceMst(Map<String, Object> condition);

    void deleteDevice(Map<String, Object> condition);

    void addDevice(DeviceForm data);

    void addDeviceMst(DeviceMstForm data);

    List<Device> getDeviceByManagerId(Map<String, String> condition);

    List<Device> getDeviceByAreaId(Map<String, String> condition);

    List<Device> getDeviceByProjectId(Map<String, String> condition);

    List<Device> getDeviceBySuperManagerId(Map<String, String> condition);

    List<Device> getDeviceList();

    List<Device> getDeviceListByListId(Map<String, String> condition);

    Integer getCountDevice(@Param ("customerId") Integer customerId, @Param ("superManagerId") Integer superManagerId,
        @Param ("managerId") Integer managerId, @Param ("areaId") Integer areaId,
        @Param ("projectId") Integer projectId);

    List<Device> getDevicesEmpty(Map<String, String> condition);

    List<Device> getDevicesAlReady(Map<String, String> condition);

    void updateDeviceTool(@Param ("device") Device device);

    void setDeviceEmpty(@Param ("systemMapId") Integer systemMapId);

    void setDeviceCaculator(@Param ("deviceIds") String[] deviceIds);

    void removeDeviceCaculator(@Param ("deviceIds") String[] deviceIds, @Param ("systemMapId") Integer systemMapId);

    String[] getDeviceIdByProjectId(Map<String, String> condition);

    String[] getDeviceIdByCustomerId(Map<String, String> condition);

    String[] getDeviceBySuperManager(Map<String, String> condition);

    String[] getDeviceByManager(Map<String, String> condition);

    String[] getDeviceByArea(Map<String, String> condition);

    String[] getAllDeviceByCalculate();

    List<Device> getDeviceBySuperManagerIds(Map<String, Object> condition);

    List<Device> getDeviceByManagerIds(Map<String, Object> condition);

    List<Device> getDeviceByAreaIds(Map<String, Object> condition);

    List<Device> getDeviceByProjectIds(Map<String, Object> condition);

    Integer getCountDeviceBySystemType(Map<String, String> condition);

    String[] getDeviceIdByProjectIdAndSystemTypeId(Map<String, String> condition);

    void removeDeviceCaculatorEmpty();

    String[] getDeviceIdBySystemType(Map<String, Object> condition);

    String[] getDeviceIdBySystemMap(Map<String, Object> condition);

    List<Device> getDataByProjectId(Map<String, String> condition);

    List<ObjectType> getListObjectCustomerId(Map<String, String> condition);

    List<Device> getDeviceByObjectTypeId(Map<String, String> condition);

    List<ObjectType> getListObjectProjectId(Map<String, String> condition);

    List<ObjectType> getListObjectByCusSys(Map<String, String> condition);

    List<ObjectType> getListObjectByProSys(Map<String, String> condition);

    Device getDataDeviceLoadByObjectType(Map<String, String> condition);

    Device getDataDeviceInverterByObjectType(Map<String, String> condition);

    Device getDataDeviceCombinerByObjectType(Map<String, String> condition);

    Device getDataDeviceStringByObjectType(Map<String, String> condition);

    List<Device> getDataDeviceRMUTwoLevelByObjectName(Map<String, String> condition);

    List<Device> getDataDeviceMeterTwoLevelByObjectName(Map<String, String> condition);

    Device getDataInstance(Map<String, String> condition);

    List<Device> getListDeviceByProjectId(Map<String, String> condition);

    Device getDeviceByDeviceId(Map<String, Object> condition);

    Float sumPower(Map<String, Object> condition);

    Device getInfoDevice(Map<String, String> condition);

    Device getDataInstanceDeviceLoad(Map<String, String> condition);

    Device getDataInstanceDeviceSensor(Map<String, String> condition);

    Device getDataInstanceDeviceSensorStatus(Map<String, String> condition);

    Device getDataInstanceInverter(Map<String, String> condition);

    Device getDataInstanceDeviceCombiner(Map<String, String> condition);

    Device getDataInstanceDeviceString(Map<String, String> condition);

    Device getDataInstanceDeviceRMU(Map<String, String> condition);

    List<Device> getListDataInstanceLoad(Map<String, String> condition);

    List<Device> getListDataInstanceInverter(Map<String, String> condition);

    List<Device> getListDataInstanceCombiner(Map<String, String> condition);

    List<Device> getListDataInstanceString(Map<String, String> condition);

    List<Device> getListDataInstanceRMU(Map<String, String> condition);

    List<Device> getDevicesCalculateFlag(Map<String, String> condition);

    List<Device> getDevicesAllFlag(Map<String, String> condition);

    List<Device> getDeviceByObjectType(Map<String, String> condition);

    List<Device> getDeviceByLoca(Map<String, String> condition);

    List<Device> getDeviceByLoadType(Map<String, String> condition);

    List<Device> getDeviceByObjectName(Map<String, String> condition);

    List<Device> getDataDeviceByObjectTwoLevel(Map<String, String> condition);

    List<Device> getDataEpLoad(Map<String, String> condition);

    List<Device> getDataEpInverter(Map<String, String> condition);

    List<Device> getDataEpCombiner(Map<String, String> condition);

    List<Device> getDataEpString(Map<String, String> condition);

    List<Device> getDataEpRMU(Map<String, String> condition);

    Device getDataInstanceRMU(Map<String, String> condition);

    Device getDataInstanceLoad(Map<String, String> condition);

    Device getDataInstanceSensor(Map<String, String> condition);

    Device getDataDevice(Map<String, String> condition);

    Float getPowerInstanceLoad(Map<String, String> condition);

    Device getDataInstanceSensorStatus(Map<String, String> condition);

    Float getPowerInstanceInverter(Map<String, String> condition);

    Float getPowerInstanceGrid(Map<String, String> condition);

    Float getEnergyInDayLoad(Map<String, String> condition);

    Float getEnergyInDayInverter(Map<String, String> condition);

    Float getEnergyInDayGrid(Map<String, String> condition);

    Float getEnergyInMonthLoad(Map<String, String> condition);

    Float getEnergyInMonthInverter(Map<String, String> condition);

    Float getEnergyInMonthGrid(Map<String, String> condition);

    Float getEnergyInYearLoad(Map<String, String> condition);

    Float getEnergyInYearInverter(Map<String, String> condition);

    Float getEnergyInYearGrid(Map<String, String> condition);

    List<Device> getListDeviceLoad(Map<String, String> condition);

    List<Device> getListDeviceInverter(Map<String, String> condition);

    List<Device> getListDeviceRMU(Map<String, String> condition);

    Integer getEnergyTotalByDeviceId(Map<String, String> condition);

    List<DataPower> getListDataPowerLoadByDay(Map<String, String> condition);

    List<DataPower> getListDataPowerSolarByDay(Map<String, String> condition);

    List<DataPower> getListDataPowerGridByDay(Map<String, String> condition);

    List<DataPower> getListDataPowerLoadByMonth(Map<String, String> condition);

    List<DataPower> getListDataPowerSolarByMonth(Map<String, String> condition);

    List<DataPower> getListDataPowerGridByMonth(Map<String, String> condition);

    List<DataPower> getListDataPowerLoadByYear(Map<String, String> condition);

    List<DataPower> getListDataPowerSolarByYear(Map<String, String> condition);

    List<DataPower> getListDataPowerGridByYear(Map<String, String> condition);

    List<DataPower> getListDataPowerLoadAll(Map<String, String> condition);

    List<DataPower> getListDataPowerSolarAll(Map<String, String> condition);

    List<DataPower> getListDataPowerGridAll(Map<String, String> condition);

    String getObjectNameById(Map<String, Object> condition);

    List<Device> getWarnedDevice(Map<String, Object> condition);

    // List<DataPower> getListEpLoadByDay(Map<String, String> condition);
    List<DataPower> getListEpLoadByDay(Map<String, String> condition);

    List<DataPower> getListEpSolarByDay(Map<String, String> condition);

    List<DataPower> getListEpGridByDay(Map<String, String> condition);

    List<DataPower> getListEpLoadByMonth(Map<String, String> condition);
    
    List<DataPower> getListEpSolarByMonth(Map<String, String> condition);

    List<DataPower> getListEpGridByMonth(Map<String, String> condition);

    List<LandmarksPlansEnergy> getListEpLoadByYear(Map<String, String> condition);
    
    List<DataPower> getListTLoadByDay(Map<String, String> condition);
    
    List<DataPower> getListTLoadByMonth(Map<String, String> condition);
    
    List<DataPower> getListTLoadByYear(Map<String, String> condition);

    List<DataPower> getListEpSolarByYear(Map<String, String> condition);

    List<DataPower> getListEpGridByYear(Map<String, String> condition);

    List<DataPower> getListEpLoadAll(Map<String, String> condition);

    List<DataPower> getListEpSolarAll(Map<String, String> condition);

    List<DataPower> getListEpGridAll(Map<String, String> condition);

    DeviceMst checkDeviceByDeviceCode(Map<String, String> condition);

    List<Device> getListByDeviceType(Map<String, String> condition);

    List<DeviceMst> getDeviceGateway();

    void insertSettingCbnd(Map<String, Object> condition);

    void insertSettingCbtt(Map<String, Object> condition);

    void insertSettingInverter(Map<String, Object> condition);

    void insertSettingMeter(Map<String, Object> condition);

    void insertSettingCbpd(Map<String, Object> condition);

    List<Integer> getObjectByDeviceId(Map<String, String> condition);

    ObjectType getObjectTypeByObjId(Map<String, String> condition);

    List<Integer> getListObjByObjectTypeId(Map<String, String> condition);

    ObjectType getObjectByObjId(Map<String, String> condition);

    List<Device> getListDataInstanceLoadFrame2(Map<String, String> condition);

    List<Device> getListDataInstanceLoadHumidity(Map<String, String> condition);

    Integer getObjectTypeIdByObjId(Map<String, String> condition);

    DataPowerResult getCountObjectByObjectTypeId(Map<String, String> condition);

    String getSentDateInstanceLoad(Map<String, String> condition);

    Float getEnergyDeviceLoadInDay(Map<String, String> condition);

    Float getEnergyDeviceLoadInMonth(Map<String, String> condition);

    DeviceName getNameDevice(Map<String, Object> con);

    Device getDataInstanceGatewayStatus(Map<String, String> condition);

    List<LandmarksPlansEnergy> getListEpLoadByMonthLandmark(Map<String, String> condition);

    Device getDataInstanceHTR02(Map<String, String> condition);

    Device getDataInstanceAMS01(Map<String, String> condition);

    List<Device> getListDataInstanceRatioIndicator(Map<String, String> condition);

    List<Device> getListDataInstanceGateway(Map<String, String> condition);
    
    void insertSettingCbll(Map<String, Object> condition);
    
    void insertSettingCbax(Map<String, Object> condition);

    Device getDataInstancePressure(Map<String, String> condition);

    Device getDataInstanceFlow(Map<String, String> condition);
    
    List<Device> getListDataInstancePressure(Map<String, String> condition);
    
    List<Device> getListDataInstanceFlow(Map<String, String> condition);
    
    Device getDataInstanceFlowAccumulation(Map<String, String> condition);

}
