package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.DeviceMapper;
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

@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceMapper deviceMapper;

    /**
     * Lấy danh sách thiết thị
     *
     * @param condition Điều kiện lấy danh sách thiết bị
     * @return Danh sách thiết bị
     */
    @Override
    public List<DeviceMst> getDevices(final Map<String, String> condition) {
        return deviceMapper.getDevices(condition);
    }

    /**
     * Thêm mới thiết bị
     *
     * @param condition Dữ liệu thêm mới
     */
    @Override
    public void addDevice(final DeviceForm data) {
        deviceMapper.addDevice(data);
    }

    /**
     * Chỉnh sửa thiết bị
     *
     * @param condition Dữ liệu cập nhật
     */
    @Override
    public void updateDevice(final Map<String, Object> condition) {
        deviceMapper.updateDevice(condition);
    }

    /**
     * Xóa thiết bị
     *
     * @param condition Điều kiện xóa thiết bị
     */
    @Override
    public void deleteDevice(final Map<String, Object> condition) {
        deviceMapper.deleteDevice(condition);
    }

    /**
     * Lấy chi tiết thiết bị
     *
     * @param condition Điều kiện lấy thông tin thiết bị
     */
    @Override
    public DeviceMst getDeviceById(final Map<String, String> condition) {
        return deviceMapper.getDeviceById(condition);
    }

    /**
     * Lấy danh sách thiết bị theo projectId
     */
    @Override
    public List<Device> getDeviceByProjectId(final Map<String, String> condition) {
        return deviceMapper.getDeviceByProjectId(condition);
    }

    /**
     * Lấy thiết bị theo mã thiết bị
     *
     * @param condition Điều kiện lấy thiết bị
     */
    @Override
    public Device getDeviceByDeviceCode(final Map<String, String> condition) {
        return deviceMapper.getDeviceByDeviceCode(condition);
    }

    /**
     * Lấy tổng thiết bị theo dự án
     */
    @Override
    public Integer getCountDevice(final Integer customerId, final Integer superManagerId, final Integer managerId,
        final Integer areaId, final Integer projectId) {
        return deviceMapper.getCountDevice(customerId, superManagerId, managerId, areaId, projectId);
    }

    /**
     *
     */
    @Override
    public List<Device> getDevicesEmpty(final Map<String, String> condition) {
        return deviceMapper.getDevicesEmpty(condition);
    }

    /**
     *
     */
    @Override
    public List<Device> getDevicesAlReady(final Map<String, String> condition) {
        return deviceMapper.getDevicesAlReady(condition);
    }

    /**
     *
     */
    @Override
    public void updateDeviceTool(final Device device) {
        deviceMapper.updateDeviceTool(device);
    }

    /**
     *
     */
    @Override
    public void setDeviceEmpty(final Integer systemMapId) {
        deviceMapper.setDeviceEmpty(systemMapId);
    }

    /**
     *
     */
    @Override
    public List<Device> getIds(final String[] deviceIds, String schema) {
        return deviceMapper.getIds(deviceIds, schema);
    }

    /**
     *
     */
    @Override
    public List<Device> getDeviceBySuperManagerId(final Map<String, String> condition) {
        return deviceMapper.getDeviceBySuperManagerId(condition);
    }

    /**
     *
     */
    @Override
    public List<Device> getDeviceByAreaId(final Map<String, String> condition) {
        return deviceMapper.getDeviceByAreaId(condition);
    }

    /**
     *
     */
    @Override
    public List<Device> getDeviceByManagerId(final Map<String, String> condition) {
        return deviceMapper.getDeviceByManagerId(condition);
    }

    /**
     *
     */
    @Override
    public void setDeviceCaculator(final String[] deviceIds) {
        deviceMapper.setDeviceCaculator(deviceIds);

    }

    /**
     *
     */
    @Override
    public void removeDeviceCaculator(final String[] deviceIds, final Integer systemMapId) {
        deviceMapper.removeDeviceCaculator(deviceIds, systemMapId);

    }

    /**
     * Lấy ra id thiết bị theo id dự án
     */
    @Override
    public String[] getDeviceIdByProjectId(final Map<String, String> condition) {
        return deviceMapper.getDeviceIdByProjectId(condition);
    }

    /**
     * Lấy ra id thiết bị theo id khách hàng
     */
    @Override
    public String[] getDeviceIdByCustomerId(final Map<String, String> condition) {
        return deviceMapper.getDeviceIdByCustomerId(condition);
    }

    /**
     * Lấy ra id thiết bị theo id khách hàng và khu vực
     */
    @Override
    public String[] getDeviceBySuperManager(final Map<String, String> condition) {
        return deviceMapper.getDeviceBySuperManager(condition);
    }

    /**
     * Lấy ra id thiết bị theo id khách hàng, khu vực và tỉnh thành
     */
    @Override
    public String[] getDeviceByManager(final Map<String, String> condition) {
        return deviceMapper.getDeviceByManager(condition);
    }

    /**
     * Lấy ra id thiết bị theo id khách hàng, khu vực, tỉnh thành và quận huyện
     */
    @Override
    public String[] getDeviceByArea(final Map<String, String> condition) {
        return deviceMapper.getDeviceByArea(condition);
    }

    /**
     * Lấy ra tất cả id thiết bị
     */
    @Override
    public String[] getAllDeviceByCalculate() {
        return deviceMapper.getAllDeviceByCalculate();
    }

    @Override
    public List<Device> getDeviceBySuperManagerIds(Map<String, Object> condition) {
        return deviceMapper.getDeviceBySuperManagerIds(condition);
    }

    @Override
    public List<Device> getDeviceByManagerIds(Map<String, Object> condition) {
        return deviceMapper.getDeviceByManagerIds(condition);
    }

    @Override
    public List<Device> getDeviceByAreaIds(Map<String, Object> condition) {
        return deviceMapper.getDeviceByAreaIds(condition);
    }

    @Override
    public List<Device> getDeviceByProjectIds(Map<String, Object> condition) {
        return deviceMapper.getDeviceByProjectIds(condition);
    }

    /**
     * Tổng số thiết bị theo thành phần
     */
    @Override
    public Integer getCountDeviceBySystemType(final Map<String, String> condition) {
        return deviceMapper.getCountDeviceBySystemType(condition);
    }

    /**
     * Lấy ra id thiết bị theo dự án và thành phần
     */
    @Override
    public String[] getDeviceIdByProjectIdAndSystemTypeId(final Map<String, String> condition) {
        return deviceMapper.getDeviceIdByProjectIdAndSystemTypeId(condition);
    }

    /**
    *
    */
    @Override
    public void removeDeviceCaculatorEmpty() {
        deviceMapper.removeDeviceCaculatorEmpty();
    }

    @Override
    public String[] getDeviceIdBySystemType(final Map<String, Object> condition) {
        return deviceMapper.getDeviceIdBySystemType(condition);
    }

    @Override
    public String[] getDeviceIdBySystemMap(final Map<String, Object> condition) {
        return deviceMapper.getDeviceIdBySystemMap(condition);
    }

    @Override
    public List<Device> getDataByProjectId(Map<String, String> condition) {
        return deviceMapper.getDataByProjectId(condition);
    }

    @Override
    public List<ObjectType> getListObjectCustomerId(Map<String, String> condition) {
        return deviceMapper.getListObjectCustomerId(condition);
    }

    @Override
    public List<Device> getDeviceByObjectTypeId(Map<String, String> condition) {
        return deviceMapper.getDeviceByObjectTypeId(condition);
    }

    @Override
    public List<ObjectType> getListObjectProjectId(Map<String, String> condition) {
        return deviceMapper.getListObjectProjectId(condition);
    }

    @Override
    public List<ObjectType> getListObjectByCusSys(Map<String, String> condition) {
        return deviceMapper.getListObjectByCusSys(condition);
    }

    @Override
    public List<ObjectType> getListObjectByProSys(Map<String, String> condition) {
        return deviceMapper.getListObjectByProSys(condition);
    }

    @Override
    public Device getDataDeviceLoadByObjectType(Map<String, String> condition) {
        return deviceMapper.getDataDeviceLoadByObjectType(condition);
    }

    @Override
    public Device getDataDeviceInverterByObjectType(Map<String, String> condition) {
        return deviceMapper.getDataDeviceInverterByObjectType(condition);
    }

    @Override
    public Device getDataDeviceCombinerByObjectType(Map<String, String> condition) {
        return deviceMapper.getDataDeviceCombinerByObjectType(condition);
    }

    @Override
    public Device getDataDeviceStringByObjectType(Map<String, String> condition) {
        return deviceMapper.getDataDeviceStringByObjectType(condition);
    }

    @Override
    public List<Device> getDataDeviceRMUTwoLevelByObjectName(Map<String, String> condition) {
        return deviceMapper.getDataDeviceRMUTwoLevelByObjectName(condition);
    }

    @Override
    public List<Device> getDataDeviceMeterTwoLevelByObjectName(Map<String, String> condition) {
        return deviceMapper.getDataDeviceMeterTwoLevelByObjectName(condition);
    }

    @Override
    public Device getDataInstance(Map<String, String> condition) {
        return deviceMapper.getDataInstance(condition);
    }

    @Override
    public Device getDeviceByDeviceId(final Map<String, Object> condition) {
        return deviceMapper.getDeviceByDeviceId(condition);
    }

    @Override
    public List<Device> getListDeviceByProjectId(Map<String, String> condition) {
        return deviceMapper.getListDeviceByProjectId(condition);
    }

    @Override
    public Float sumPower(Map<String, Object> condition) {
        return deviceMapper.sumPower(condition);
    }

    @Override
    public Device getInfoDevice(Map<String, String> condition) {
        return deviceMapper.getInfoDevice(condition);
    }

    @Override
    public Device getDataInstanceDeviceLoad(Map<String, String> condition) {
        return deviceMapper.getDataInstanceDeviceLoad(condition);
    }

    @Override
    public Device getDataInstanceInverter(Map<String, String> condition) {
        return deviceMapper.getDataInstanceInverter(condition);
    }

    @Override
    public Device getDataInstanceDeviceCombiner(Map<String, String> condition) {
        return deviceMapper.getDataInstanceDeviceCombiner(condition);
    }

    @Override
    public Device getDataInstanceDeviceString(Map<String, String> condition) {
        return deviceMapper.getDataInstanceDeviceString(condition);
    }

    @Override
    public Device getDataInstanceDeviceRMU(Map<String, String> condition) {
        return deviceMapper.getDataInstanceDeviceRMU(condition);
    }

    @Override
    public List<Device> getListDataInstanceLoad(Map<String, String> condition) {
        return deviceMapper.getListDataInstanceLoad(condition);
    }

    @Override
    public List<Device> getListDataInstanceInverter(Map<String, String> condition) {
        return deviceMapper.getListDataInstanceInverter(condition);
    }

    @Override
    public List<Device> getListDataInstanceCombiner(Map<String, String> condition) {
        return deviceMapper.getListDataInstanceCombiner(condition);
    }

    @Override
    public List<Device> getListDataInstanceString(Map<String, String> condition) {
        return deviceMapper.getListDataInstanceString(condition);
    }

    @Override
    public List<Device> getListDataInstanceRMU(Map<String, String> condition) {
        return deviceMapper.getListDataInstanceRMU(condition);
    }

    /**
     * Lấy danh sách thiết thị có gắn flag
     *
     * @param condition Điều kiện lấy danh sách thiết bị
     * @return Danh sách thiết bị
     */
    @Override
    public List<Device> getDevicesCalculateFlag(final Map<String, String> condition) {
        return deviceMapper.getDevicesCalculateFlag(condition);
    }

    @Override
    public List<Device> getDevicesAllFlag(final Map<String, String> condition) {
        return deviceMapper.getDevicesAllFlag(condition);
    }

    @Override
    public List<Device> getDeviceByObjectName(Map<String, String> condition) {
        return deviceMapper.getDeviceByObjectName(condition);
    }

    @Override
    public List<Device> getDataDeviceByObjectTwoLevel(Map<String, String> condition) {
        return deviceMapper.getDataDeviceByObjectTwoLevel(condition);
    }

    @Override
    public List<Device> getDataEpLoad(Map<String, String> condition) {
        return deviceMapper.getDataEpLoad(condition);
    }

    @Override
    public List<Device> getDataEpInverter(Map<String, String> condition) {
        return deviceMapper.getDataEpInverter(condition);
    }

    @Override
    public List<Device> getDataEpCombiner(Map<String, String> condition) {
        return deviceMapper.getDataEpCombiner(condition);
    }

    @Override
    public List<Device> getDataEpString(Map<String, String> condition) {
        return deviceMapper.getDataEpString(condition);
    }

    @Override
    public List<Device> getDataEpRMU(Map<String, String> condition) {
        return deviceMapper.getDataEpRMU(condition);
    }

    @Override
    public Device getDeviceName(String deviceId) {
        return deviceMapper.getDeviceName(deviceId);
    }

    @Override
    public Device getDataInstanceRMU(Map<String, String> condition) {
        return deviceMapper.getDataInstanceRMU(condition);
    }

    @Override
    public Device getDataInstanceLoad(Map<String, String> condition) {
        return deviceMapper.getDataInstanceLoad(condition);
    }

    @Override
    public Device getDataDevice(Map<String, String> condition) {
        return deviceMapper.getDataDevice(condition);
    }

    //

    @Override
    public Float getPowerInstanceLoad(Map<String, String> condition) {
        return deviceMapper.getPowerInstanceLoad(condition);
    }

    @Override
    public Float getPowerInstanceInverter(Map<String, String> condition) {
        return deviceMapper.getPowerInstanceInverter(condition);
    }

    @Override
    public Float getPowerInstanceGrid(Map<String, String> condition) {
        return deviceMapper.getPowerInstanceGrid(condition);
    }

    @Override
    public Float getEnergyInDayLoad(Map<String, String> condition) {
        return deviceMapper.getEnergyInDayLoad(condition);
    }

    @Override
    public Float getEnergyInDayInverter(Map<String, String> condition) {
        // TODO Auto-generated method stub
        return deviceMapper.getEnergyInDayInverter(condition);
    }

    @Override
    public Float getEnergyInDayGrid(Map<String, String> condition) {
        return deviceMapper.getEnergyInDayGrid(condition);
    }

    @Override
    public Float getEnergyInMonthLoad(Map<String, String> condition) {
        return deviceMapper.getEnergyInMonthLoad(condition);
    }

    @Override
    public Float getEnergyInMonthInverter(Map<String, String> condition) {
        return deviceMapper.getEnergyInMonthInverter(condition);
    }

    @Override
    public Float getEnergyInMonthGrid(Map<String, String> condition) {
        return deviceMapper.getEnergyInMonthGrid(condition);
    }

    @Override
    public List<Device> getListDeviceLoad(Map<String, String> condition) {
        return deviceMapper.getListDeviceLoad(condition);
    }

    @Override
    public List<Device> getListDeviceInverter(Map<String, String> condition) {
        return deviceMapper.getListDeviceInverter(condition);
    }

    @Override
    public List<Device> getListDeviceRMU(Map<String, String> condition) {
        return deviceMapper.getListDeviceRMU(condition);
    }

    @Override
    public Float getEnergyInYearLoad(Map<String, String> condition) {
        return deviceMapper.getEnergyInYearLoad(condition);
    }

    @Override
    public Float getEnergyInYearInverter(Map<String, String> condition) {
        return deviceMapper.getEnergyInYearInverter(condition);
    }

    @Override
    public Float getEnergyInYearGrid(Map<String, String> condition) {
        return deviceMapper.getEnergyInYearGrid(condition);
    }

    @Override
    public Integer getEnergyTotalByDeviceId(Map<String, String> condition) {
        return deviceMapper.getEnergyTotalByDeviceId(condition);
    }

    @Override
    public List<DataPower> getListDataPowerLoadByDay(Map<String, String> condition) {
        return deviceMapper.getListDataPowerLoadByDay(condition);
    }

    @Override
    public List<DataPower> getListDataPowerSolarByDay(Map<String, String> condition) {
        return deviceMapper.getListDataPowerSolarByDay(condition);
    }

    @Override
    public List<DataPower> getListDataPowerGridByDay(Map<String, String> condition) {
        return deviceMapper.getListDataPowerGridByDay(condition);
    }

    @Override
    public List<DataPower> getListDataPowerLoadByMonth(Map<String, String> condition) {
        return deviceMapper.getListDataPowerLoadByMonth(condition);
    }

    @Override
    public List<DataPower> getListDataPowerSolarByMonth(Map<String, String> condition) {
        return deviceMapper.getListDataPowerSolarByMonth(condition);
    }

    @Override
    public List<DataPower> getListDataPowerGridByMonth(Map<String, String> condition) {
        return deviceMapper.getListDataPowerGridByMonth(condition);
    }

    @Override
    public List<DataPower> getListDataPowerLoadByYear(Map<String, String> condition) {
        return deviceMapper.getListDataPowerLoadByYear(condition);
    }

    @Override
    public List<DataPower> getListDataPowerSolarByYear(Map<String, String> condition) {
        return deviceMapper.getListDataPowerSolarByYear(condition);
    }

    @Override
    public List<DataPower> getListDataPowerGridByYear(Map<String, String> condition) {
        return deviceMapper.getListDataPowerGridByYear(condition);
    }

    @Override
    public List<DataPower> getListDataPowerLoadAll(Map<String, String> condition) {
        return deviceMapper.getListDataPowerLoadAll(condition);
    }

    @Override
    public List<DataPower> getListDataPowerSolarAll(Map<String, String> condition) {
        return deviceMapper.getListDataPowerSolarAll(condition);
    }

    @Override
    public List<DataPower> getListDataPowerGridAll(Map<String, String> condition) {
        return deviceMapper.getListDataPowerGridAll(condition);
    }

    @Override
    public String getObjectNameById(Map<String, Object> condition) {
        return deviceMapper.getObjectNameById(condition);
    }

    @Override
    public List<Device> getDeviceListByListId(Map<String, String> condition) {
        return this.deviceMapper.getDeviceListByListId(condition);
    }

    @Override
    public List<Device> getWarnedDevice(final Map<String, Object> condition) {
        return deviceMapper.getWarnedDevice(condition);
    }

    // @Override
    // public List<DataPower> getListEpLoadByDay(Map<String, String> condition) {
    // return deviceMapper.getListEpLoadByDay(condition);
    // }
    @Override
    public List<DataPower> getListEpLoadByDay(Map<String, String> condition) {
        return deviceMapper.getListEpLoadByDay(condition);
    }

    @Override
    public List<DataPower> getListEpLoadByMonth(Map<String, String> condition) {
        return deviceMapper.getListEpLoadByMonth(condition);
    }

    @Override
    public List<LandmarksPlansEnergy> getListEpLoadByMonthLandmark(Map<String, String> condition) {
        return deviceMapper.getListEpLoadByMonthLandmark(condition);
    }

    @Override
    public List<LandmarksPlansEnergy> getListEpLoadByYear(Map<String, String> condition) {
        return deviceMapper.getListEpLoadByYear(condition);
    }

    @Override
    public List<DataPower> getListEpLoadAll(Map<String, String> condition) {
        return deviceMapper.getListEpLoadAll(condition);
    }

    @Override
    public List<DataPower> getListEpSolarByDay(Map<String, String> condition) {
        return deviceMapper.getListEpSolarByDay(condition);
    }

    @Override
    public List<DataPower> getListEpGridByDay(Map<String, String> condition) {
        // TODO Auto-generated method stub
        return deviceMapper.getListEpGridByDay(condition);
    }

    @Override
    public List<DataPower> getListEpSolarByMonth(Map<String, String> condition) {
        // TODO Auto-generated method stub
        return deviceMapper.getListEpSolarByMonth(condition);
    }

    @Override
    public List<DataPower> getListEpGridByMonth(Map<String, String> condition) {
        // TODO Auto-generated method stub
        return deviceMapper.getListEpGridByMonth(condition);
    }

    @Override
    public List<DataPower> getListEpSolarByYear(Map<String, String> condition) {
        // TODO Auto-generated method stub
        return deviceMapper.getListEpSolarByYear(condition);
    }

    @Override
    public List<DataPower> getListEpGridByYear(Map<String, String> condition) {
        // TODO Auto-generated method stub
        return deviceMapper.getListEpGridByYear(condition);
    }

    @Override
    public List<DataPower> getListEpSolarAll(Map<String, String> condition) {
        // TODO Auto-generated method stub
        return deviceMapper.getListEpSolarAll(condition);
    }

    @Override
    public List<DataPower> getListEpGridAll(Map<String, String> condition) {
        // TODO Auto-generated method stub
        return deviceMapper.getListEpGridAll(condition);
    }

    @Override
    public void addDeviceMst(DeviceMstForm data) {
        deviceMapper.addDeviceMst(data);
    }

    @Override
    public DeviceMst checkDeviceByDeviceCode(Map<String, String> condition) {
        return deviceMapper.checkDeviceByDeviceCode(condition);
    }

    @Override
    public List<Device> getListByDeviceType(Map<String, String> condition) {
        return this.deviceMapper.getListByDeviceType(condition);
    }

    @Override
    public List<DeviceMst> getDeviceGateway() {
        return deviceMapper.getDeviceGateway();
    }

    @Override
    public void updateDeviceMst(Map<String, Object> condition) {
        deviceMapper.updateDeviceMst(condition);
    }

    @Override
    public void insertSettingCbnd(Map<String, Object> condition) {
        deviceMapper.insertSettingCbnd(condition);
    }

    @Override
    public void insertSettingCbtt(Map<String, Object> condition) {
        deviceMapper.insertSettingCbtt(condition);
    }

    @Override
    public void insertSettingInverter(Map<String, Object> condition) {
        deviceMapper.insertSettingInverter(condition);
    }

    @Override
    public void insertSettingMeter(Map<String, Object> condition) {
        deviceMapper.insertSettingMeter(condition);
    }

    @Override
    public List<Integer> getObjectByDeviceId(Map<String, String> condition) {
        return deviceMapper.getObjectByDeviceId(condition);
    }

    @Override
    public ObjectType getObjectTypeByObjId(Map<String, String> condition) {
        return deviceMapper.getObjectTypeByObjId(condition);
    }

    @Override
    public List<Integer> getListObjByObjectTypeId(Map<String, String> condition) {
        return deviceMapper.getListObjByObjectTypeId(condition);
    }

    @Override
    public ObjectType getObjectByObjId(Map<String, String> condition) {
        return deviceMapper.getObjectByObjId(condition);
    }

    @Override
    public List<Device> getDeviceByObjectType(Map<String, String> condition) {
        return this.deviceMapper.getDeviceByObjectType(condition);
    }

    @Override
    public List<Device> getDeviceByLoca(Map<String, String> condition) {
        return this.deviceMapper.getDeviceByLoca(condition);
    }

    @Override
    public Device getDataInstanceDeviceSensor(Map<String, String> condition) {
        return deviceMapper.getDataInstanceDeviceSensor(condition);
    }

    @Override
    public Device getDataInstanceSensor(Map<String, String> condition) {
        return deviceMapper.getDataInstanceSensor(condition);
    }

    @Override
    public Device getDataInstanceDeviceSensorStatus(Map<String, String> condition) {
        return deviceMapper.getDataInstanceDeviceSensorStatus(condition);
    }

    @Override
    public Device getDataInstanceSensorStatus(Map<String, String> condition) {
        return deviceMapper.getDataInstanceSensorStatus(condition);
    }

    @Override
    public List<Device> getListDataInstanceLoadFrame2(Map<String, String> condition) {
        return deviceMapper.getListDataInstanceLoadFrame2(condition);
    }

    @Override
    public List<Device> getListDataInstanceLoadHumidity(Map<String, String> condition) {
        return deviceMapper.getListDataInstanceLoadHumidity(condition);
    }

    @Override
    public Integer getObjectTypeIdByObjId(Map<String, String> condition) {
        return deviceMapper.getObjectTypeIdByObjId(condition);
    }

    @Override
    public DataPowerResult getCountObjectByObjectTypeId(Map<String, String> condition) {
        return deviceMapper.getCountObjectByObjectTypeId(condition);
    }

    @Override
    public List<Device> getDeviceByLoadType(Map<String, String> condition) {
        return this.deviceMapper.getDeviceByLoadType(condition);
    }

    @Override
    public String getSentDateInstanceLoad(Map<String, String> condition) {
        return deviceMapper.getSentDateInstanceLoad(condition);
    }

    @Override
    public Float getEnergyDeviceLoadInDay(Map<String, String> condition) {
        return deviceMapper.getEnergyDeviceLoadInDay(condition);
    }

    @Override
    public Float getEnergyDeviceLoadInMonth(Map<String, String> condition) {
        return deviceMapper.getEnergyDeviceLoadInMonth(condition);
    }

    @Override
    public List<DeviceLostSignalResponse> getWarnedDeviceLostSignal(Map<String, Object> condition) {
        return deviceMapper.getWarnedDeviceLostSignal(condition);
    }

    @Override
    public DeviceName getNameDevice(Map<String, Object> con) {
        return this.deviceMapper.getNameDevice(con);
    }

    @Override
    public Device getDataInstanceGateway(Map<String, Object> condition) {
        return deviceMapper.getDataInstanceGateway(condition);
    }

    @Override
    public Device getDataInstanceGatewayStatus(Map<String, String> condition) {
        return deviceMapper.getDataInstanceGatewayStatus(condition);
    }

    @Override
    public Device getDataInstanceHTR02(Map<String, String> condition) {
        return deviceMapper.getDataInstanceHTR02(condition);
    }

    @Override
    public Device getDataInstanceAMS01(Map<String, String> condition) {
        return deviceMapper.getDataInstanceAMS01(condition);
    }

    @Override
    public void insertSettingCbpd(Map<String, Object> condition) {
        deviceMapper.insertSettingCbpd(condition);
    }

    @Override
    public List<Device> getListDataInstanceRatioIndicator(Map<String, String> condition) {
        return deviceMapper.getListDataInstanceRatioIndicator(condition);
    }

    @Override
    public List<Device> getListDataInstanceGateway(Map<String, String> condition) {
        return deviceMapper.getListDataInstanceGateway(condition);
    }

    @Override
    public void insertSettingCbll(Map<String, Object> condition) {
        this.deviceMapper.insertSettingCbll(condition);
    }

    @Override
    public void insertSettingCbax(Map<String, Object> condition) {
        this.deviceMapper.insertSettingCbax(condition);
    }

	@Override
	public List<DataPower> getListTLoadByDay(Map<String, String> condition) {
		// TODO Auto-generated method stub
		return deviceMapper.getListTLoadByDay(condition);
	}

	@Override
	public List<DataPower> getListTLoadByMonth(Map<String, String> condition) {
		// TODO Auto-generated method stub
		return deviceMapper.getListTLoadByMonth(condition);
	}

	@Override
	public List<DataPower> getListTLoadByYear(Map<String, String> condition) {
		// TODO Auto-generated method stub
		return deviceMapper.getListTLoadByYear(condition);
	}

	public Device getDataInstancePressure(Map<String, String> condition) {
		return deviceMapper.getDataInstancePressure(condition);
	}

	@Override
	public Device getDataInstanceFlow(Map<String, String> condition) {
		return deviceMapper.getDataInstanceFlow(condition);
	}

	@Override
	public List<Device> getListDataInstancePressure(Map<String, String> condition) {
		 return deviceMapper.getListDataInstancePressure(condition);
	}

	@Override
	public List<Device> getListDataInstanceFlow(Map<String, String> condition) {
		 return deviceMapper.getListDataInstanceFlow(condition);
	}

	@Override
	public Device getDataInstanceFlowAccumulation(Map<String, String> condition) {
		return deviceMapper.getDataInstanceFlowAccumulation(condition);
	}

	
}
