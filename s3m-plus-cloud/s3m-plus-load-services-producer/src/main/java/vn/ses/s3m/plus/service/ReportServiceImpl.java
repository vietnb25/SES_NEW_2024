package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.ReportMapper;
import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.DataLoadFrame2;
import vn.ses.s3m.plus.dto.Report;
import vn.ses.s3m.plus.dto.Warning;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    ReportMapper reportMapper;

    /**
     * Lấy ra danh sách thiết bị.
     *
     * @return Danh sách thiết bị.
     */
    @Override
    public List<Map<String, Object>> getDevice(Map<String, String> condition) {
        return reportMapper.getDevice(condition);
    }

    /**
     * Lấy ra danh sách báo cáo .
     *
     * @param userId Mã người dùng.
     * @return Danh sách báo cáo.
     */
    @Override
    public List<Report> getReport(Map<String, Object> condition) {
        return reportMapper.getReport(condition);
    }

    /**
     * Lấy ra thông tin báo để làm việc với báo cáo của tất cả thiết bị.
     *
     * @param condition Thông tin truyền vào.
     * @return Danh sách báo cáo.
     */
    @Override
    public List<DataLoadFrame1> getAllDataLoadFrame1(Map<String, String> condition) {
        return reportMapper.getAllDataLoadFrame1(condition);
    }

    /**
     * Lấy ra thông tin báo để làm việc với báo cáo theo mã thiết bị.
     *
     * @param condition Thông tin truyền vào.
     * @return Thông tin báo cáo.
     */
    @Override
    public List<DataLoadFrame1> getDataLoadFrame1ById(Map<String, String> condition) {
        return reportMapper.getDataLoadFrame1ById(condition);
    }

    /**
     * Thêm thông tin báo cáo .
     *
     * @param report Thông tin báo cáo.
     * @return Báo cáo được thêm vào.
     */
    @Override
    public void addReport(Report report) {
        reportMapper.addReport(report);
    }

    /**
     * Xóa thông tin báo cáo .
     *
     * @param id Mã báo cáo.
     * @return Báo cáo được xóa.
     */
    @Override
    public void deleteReport(Integer id) {
        reportMapper.deleteReport(id);
    }

    /**
     * Cập nhật tiến độ tải báo cáo.
     *
     * @param condition Thông tin & báo cáo.
     * @return Tiến độ tải báo cáo.
     */
    @Override
    public void updatePercent(Map<String, String> condition) {
        reportMapper.updatePercent(condition);
    }

    /**
     * Lấy mã người dùng theo tên người dùng.
     *
     * @param userName Tên người dùng.
     * @return Mã người dùng.
     */
    @Override
    public Integer getUserId(String userName) {
        return reportMapper.getUserId(userName);
    }

    /**
     * Lấy thông tin của bản tin báo cáo
     *
     * @param condition Thông tin truyền vào.
     * @return Thông tin của bản tin báo cáo.
     */
    @Override
    public List<DataLoadFrame2> getDataLoadFrame2ById(Map<String, String> condition) {
        return reportMapper.getDataLoadFrame2ById(condition);
    }

    /**
     * Xóa thông tin của bản tin báo theo url
     *
     * @param url Đường dẫn tới file báo cáo.
     */
    @Override
    public void deleteReportByUrl(String url) {
        reportMapper.deleteReportByUrl(url);
    }

    /**
     * Lấy ra danh sách báo cáo.
     *
     * @param map Thông tin truyền vào (mã người dùng).
     * @return Danh sách báo cáo.
     */
    @Override
    public List<Report> getListByLimit(Map<String, Integer> map) {
        return reportMapper.getListByLimit(map);
    }

    /**
     * Cập nhật trạng thái báo cáo.
     *
     * @param id Mã báo cáo.
     */
    @Override
    public void updateStatus(Integer id) {
        reportMapper.updateStatus(id);
    }

    /**
     * Cập nhật thời gian cập nhật báo cáo.
     *
     * @param id Mã báo cáo.
     */
    @Override
    public void updateTimeFinish(Integer id) {
        reportMapper.updateTimeFinish(id);
    }

    /**
     * Lấy thông tin của bản tin báo cáo.
     *
     * @param condition Thông tin truyền vào.
     * @return Thông tin của bản tin báo cáo.
     */
    @Override
    public List<DataLoadFrame2> getAllDataLoadFrame2(Map<String, String> condition) {
        return reportMapper.getAllDataLoadFrame2(condition);
    }

    @Override
    public List<Warning> getDetailWarningByWarningType(Map<String, Object> condition) {
        return reportMapper.getDetailWarningByWarningType(condition);
    }

    @Override
    public List<DataLoadFrame1> getDataLoadFrame1ByIdLimit(Map<String, String> condition) {
        return reportMapper.getDataLoadFrame1ByIdLimit(condition);
    }

    @Override
    public List<Warning> getTotalWarningByLimit(Map<String, Object> condition) {
        return reportMapper.getTotalWarningByLimit(condition);
    }

    @Override
    public List<Warning> getDetailWarningByWarningTypeByLimit(Map<String, Object> condition) {
        return reportMapper.getDetailWarningByWarningTypeByLimit(condition);
    }

    @Override
    public List<Map<String, Object>> getDeviceHasWaring(Map<String, Object> condition) {
        return reportMapper.getDeviceHasWaring(condition);
    }

    @Override
    public List<DataLoadFrame1> getElectricalPower(Map<String, String> condition) {
        return reportMapper.getElectricalPower(condition);
    }

    @Override
    public Long getElectricalPowerInHour(Map<String, String> condition) {
        return reportMapper.getElectricalPowerInHour(condition);
    }

    @Override
    public List<DataLoadFrame1> getDataLoadFrame1Limit(Map<String, String> condition) {
        return reportMapper.getDataLoadFrame1Limit(condition);
    }

    @Override
    public String getProjectName(String projectId) {
        return reportMapper.getProjectName(projectId);
    }

    @Override
    public List<Map<String, String>> getWarningLoad(Map<String, String> condition) {
        return reportMapper.getWarningLoad(condition);
    }

    @Override
    public String getTotalWarningLoad(Map<String, String> condition) {
        return reportMapper.getTotalWarningLoad(condition);
    }

    @Override
    public List<DataLoadFrame1> getElectricalPowerInMonth(Map<String, String> condition) {
        return reportMapper.getElectricalPowerInMonth(condition);
    }

    @Override
    public List<DataLoadFrame1> getElectricalPowerInDay(Map<String, String> condition) {
        return reportMapper.getElectricalPowerInDay(condition);
    }

    @Override
    public List<DataLoadFrame1> getElectricalPowerInYear(Map<String, String> condition) {
        return reportMapper.getElectricalPowerInYear(condition);
    }

}
