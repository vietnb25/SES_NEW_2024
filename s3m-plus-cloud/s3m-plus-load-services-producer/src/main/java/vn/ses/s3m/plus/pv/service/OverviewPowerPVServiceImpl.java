package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.OverviewPowerMapper;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.Forecast;
import vn.ses.s3m.plus.dto.OverviewLoadTotalPower;
import vn.ses.s3m.plus.dto.OverviewPVPower;
import vn.ses.s3m.plus.dto.OverviewPVTotalPower;

@Service
public class OverviewPowerPVServiceImpl implements OverviewPowerPVService {

    @Autowired
    private OverviewPowerMapper overviewPowerMapper;

    /**
     * Lấy thông tin công suất trong ngày của thiết bị Inverter
     *
     * @param condition Thông tin ID dự án, ID kiểu hệ thống
     * @return thông tin công suất
     */
    @Override
    public List<OverviewPVPower> getPowerPV(final Map<String, Object> condition) {
        return overviewPowerMapper.getOverviewPowerPV(condition);
    }

    /**
     * Lấy thông tin tổng công suất , năng lượng của các thiết bị trong 1 ngày thuộc dự án Load
     *
     * @param condition Thông tin ID dự án, ID kiểu hệ thống, khoảng thời gian trong ngày
     * @return thông tin công suất, năng lượng.
     */
    @Override
    public OverviewLoadTotalPower getTotalPowerInDay(final Map<String, Object> condition) {
        return overviewPowerMapper.getTotalPowerInDay(condition);
    }

    /**
     * Lấy thông tin tổng công suất , năng lượng của các thiết bị trong 1 ngày thuộc dự án PV
     *
     * @param condition Thông tin ID dự án, ID kiểu hệ thống, khoảng thời gian trong ngày
     * @return thông tin công suất, năng lượng.
     */
    @Override
    public OverviewPVTotalPower getTotalPowerPVInDay(Map<String, Object> condition) {
        return overviewPowerMapper.getTotalPowerPVInDay(condition);
    }

    /**
     * Lấy thông tin khách hàng, vùng miền, tỉnh thành, quận huyện theo projectId
     *
     * @param condition ID dự án
     * @return Thông tin tên khách hàng, vùng miền, tỉnh thành, quận huyện
     */
    @Override
    public Map<String, String> getInformationProject(final Map<String, Object> condition) {
        return overviewPowerMapper.getInformationProject(condition);
    }

    /**
     * Lấy thông tin danh sách thiết bị thuộc hệ thống LOAD theo projectId
     *
     * @param condition ID dự án
     * @return Danh sách thông tin thiết bị
     */
    @Override
    public List<Device> getListDeviceLoadByProjectId(final Map<String, Object> condition) {
        return overviewPowerMapper.getListDeviceLoadByProjectId(condition);
    }

    /**
     * Lấy các giá trị dự báo
     *
     * @param condition Điều kiện lấy giá trị dự báo
     * @return Thông tin các giá trị dự báo
     */
    @Override
    public Forecast getForecast(final Map<String, Object> condition) {
        return overviewPowerMapper.getForecast(condition);
    }

    /**
     * Thêm dữ liệu dự báo
     *
     * @param condition Dữ liệu dự báo
     */
    @Override
    public void insertForecast(final Map<String, Object> condition) {
        overviewPowerMapper.insertForecast(condition);
    }

    /**
     * Cập nhật dữ liệu dự báo
     *
     * @param condition Điều kiện lấy dữ liệu
     */
    @Override
    public void updateForecast(final Map<String, Object> condition) {
        overviewPowerMapper.updateForecast(condition);
    }

    @Override
    public List<OverviewPVPower> getLayer(Map<String, Object> condition) {
        return overviewPowerMapper.getLayerPV(condition);
    }

    @Override
    public List<OverviewPVPower> getDeviceHasWarning(Map<String, Object> condition) {
        return overviewPowerMapper.getDevicesHasWarningPV(condition);
    }

    /**
     * Lấy danh sách dữ liệu dự báo
     *
     * @param condition Điều kiện lấy dữ liệu
     * @return Danh sách dữ liệu dự báo
     */
    @Override
    public List<Forecast> getForecasts(final Map<String, Object> condition) {
        return overviewPowerMapper.getForecasts(condition);
    }

    /**
     * Lấy danh sách tổng số lịch sử thay đổi dữ liệu thông số dự báo
     *
     * @param condition Điều kiện truy vấn
     * @return Số dữ liệu lịch sử thông số dự báo
     */
    @Override
    public Integer countTotalForecasts(final Map<String, Object> condition) {
        return overviewPowerMapper.countTotalForecasts(condition);
    }

    /**
     * Lưu dữ liệu thay đổi thông số
     *
     * @param condition Dữ liệu lưu
     */
    @Override
    public void insertForecastHistory(final Map<String, Object> condition) {
        overviewPowerMapper.insertForecastHistory(condition);
    }

    @Override
    public List<OverviewPVTotalPower> getListPowerInDay(Map<String, Object> condition) {
        return overviewPowerMapper.getListPowerPVInDay(condition);
    }

    @Override
    public List<OverviewPVTotalPower> getListPowerCombinerInDay(Map<String, Object> condition) {
        return overviewPowerMapper.getListPowerCombinerInDay(condition);
    }

    @Override
    public List<OverviewPVTotalPower> getListPowerStringInDay(Map<String, Object> condition) {
        return overviewPowerMapper.getListPowerStringInDay(condition);
    }

    @Override
    public List<Forecast> getListForecast(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return overviewPowerMapper.getListForecast(condition);
    }

    @Override
    public List<OverviewPVPower> getPowerCombiner(Map<String, Object> condition) {
        return overviewPowerMapper.getOverviewPowerCombiner(condition);
    }

    @Override
    public List<OverviewPVPower> getPowerString(Map<String, Object> condition) {
        return overviewPowerMapper.getOverviewPowerString(condition);
    }

    @Override
    public List<OverviewPVPower> getOverviewPowerWeather(Map<String, Object> condition) {
        return overviewPowerMapper.getOverviewPowerWeather(condition);
    }

}
