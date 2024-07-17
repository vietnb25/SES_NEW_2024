package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.OverviewPowerMapper;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.Forecast;
import vn.ses.s3m.plus.dto.OverviewLoadPower;
import vn.ses.s3m.plus.dto.OverviewLoadTotalPower;

@Service
public class OverviewPowerServiceImpl implements OverviewPowerService {

    @Autowired
    private OverviewPowerMapper overviewPowerMapper;

    /**
     * Lấy thông tin công suất trong ngày của thiết bị
     *
     * @param condition Thông tin ID dự án, ID kiểu hệ thống
     * @return thông tin công suất
     */
    @Override
    public OverviewLoadPower getPowers(final Map<String, Object> condition) {
        return overviewPowerMapper.getOverviewPowers(condition);
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
    public List<OverviewLoadPower> getLayer(Map<String, Object> condition) {
        return overviewPowerMapper.getLayer(condition);
    }

    @Override
    public List<OverviewLoadPower> getDeviceHasWarning(Map<String, Object> condition) {
        return overviewPowerMapper.getDevicesHasWarning(condition);
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
    public List<OverviewLoadTotalPower> getListPowerInDay(Map<String, Object> condition) {
        return overviewPowerMapper.getListPowerInDay(condition);
    }

    @Override
    public List<Forecast> getListForecast(Map<String, Object> condition) {

        return overviewPowerMapper.getListForecast(condition);
    }

    @Override
    public Long getSumEnergy(Map<String, Object> condition) {

        return overviewPowerMapper.getSumEnergy(condition);
    }

    @Override
    public Long getSumEnergyByYear(Map<String, Object> condition) {

        return overviewPowerMapper.getSumEnergyByYear(condition);
    }

    @Override
    public Long getSumEnergyByMonth(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return overviewPowerMapper.getSumEnergyByMonth(condition);
    }

    @Override
    public Long getSumEnergyByDay(Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return overviewPowerMapper.getSumEnergyByDay(condition);
    }

}
