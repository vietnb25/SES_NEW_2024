package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.WarningMapper;
import vn.ses.s3m.plus.dto.Warning;

@Service
public class WarningServiceImp implements WarningService {
    @Autowired
    private WarningMapper warningMapper;

    /**
     * Lấy thông tin cảnh báo.
     *
     * @param condition Điều kiện lấy cảnh báo (cảnh báo theo ngày, theo projectId, ...).
     * @return Danh sách cảnh báo theo điều kiện.
     */
    @Override
    public List<Warning> getWarnings(final Map<String, Object> condition) {
        return warningMapper.getWarnings(condition);
    }

    /**
     * Tổng số lần xuất hiện cảnh báo.
     *
     * @param condition Điều kiện truy vấn (deviceId, projectId, fromDate, toDate,...).
     * @return Danh sách cảnh báo theo từng thiết bị.
     */
    @Override
    public List<Warning> getTotalWarningPV(final Map<String, Object> condition) {
        return warningMapper.getTotalWarningPV(condition);
    }

    /**
     * Tổng thiết bị bị cảnh báo.
     *
     * @param condition Điều kiện truy vấn.
     */
    @Override
    public Integer getAllDeviceHasWarningPV(final Map<String, Object> condition) {
        return warningMapper.getAllDeviceHasWarningPV(condition);
    }

    /**
     * Chi tiết cảnh báo theo warningType và deviceId.
     *
     * @param condition Điều kiện truy vấn (deviceId, projectId, fromDate, toDate, warningType).
     * @return Danh sách chi tiết của các cảnh báo theo thiết bị và warningType.
     */
    @Override
    public List<Warning> getDetailWarningType(final Map<String, Object> condition) {
        return warningMapper.getDetailWarningType(condition);
    }

    /**
     * Đếm cảnh báo theo thời gian.
     *
     * @param condition Điều kiện lấy mô tả cảnh báo.
     * @return Danh sách tổng cảnh báo.
     */
    @Override
    public List<Warning> countWarnings(final Map<String, Object> condition) {
        return warningMapper.countWarnings(condition);
    }

    /**
     * Tổng số cảnh báo theo thời gian và kiểu cảnh báo.
     *
     * @param condition Điều kiện truy vấn.
     */
    @Override
    public Integer countWarningByWarningType(final Map<String, Object> condition) {
        return warningMapper.countWarningByWarningType(condition);
    }

}
