package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.UserMapper;
import vn.ses.s3m.plus.dao.WarningMapper;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.Warning;

@Service
public class WarningPVServiceImpl implements WarningPVService {

    @Autowired
    private WarningMapper warningPVMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * Lấy thông tin cảnh báo.
     *
     * @param condition Điều kiện lấy cảnh báo (cảnh báo theo ngày, theo projectId, ...).
     * @return Danh sách cảnh báo theo điều kiện.
     */
    @Override
    public List<Warning> getWarnings(final Map<String, Object> condition) {
        return warningPVMapper.getWarnings(condition);
    }

    /**
     * Tổng số lần xuất hiện cảnh báo.
     *
     * @param condition Điều kiện truy vấn (deviceId, projectId, fromDate, toDate,...).
     * @return Danh sách cảnh báo theo từng thiết bị.
     */
    @Override
    public List<Warning> getTotalWarningPV(final Map<String, Object> condition) {
        return warningPVMapper.getTotalWarningPV(condition);
    }

    /**
     * Tổng thiết bị bị cảnh báo.
     *
     * @param condition Điều kiện truy vấn.
     */
    @Override
    public Integer getAllDeviceHasWarningPV(final Map<String, Object> condition) {
        return warningPVMapper.getAllDeviceHasWarningPV(condition);
    }

    /**
     * Chi tiết cảnh báo theo warningType và deviceId.
     *
     * @param condition Điều kiện truy vấn (deviceId, projectId, fromDate, toDate, warningType).
     * @return Danh sách chi tiết của các cảnh báo theo thiết bị và warningType.
     */
    @Override
    public List<Warning> countWarningInverter(final Map<String, Object> condition) {
        return warningPVMapper.countWarningInverter(condition);
    }

    /**
     * Chi tiết cảnh báo theo warningType và deviceId.
     *
     * @param condition Điều kiện truy vấn (deviceId, projectId, fromDate, toDate, warningType).
     * @return Danh sách chi tiết của các cảnh báo theo thiết bị và warningType.
     */
    @Override
    public List<Warning> countWarningCombiner(final Map<String, Object> condition) {
        return warningPVMapper.countWarningCombiner(condition);
    }

    /**
     * Chi tiết cảnh báo theo warningType và deviceId.
     *
     * @param condition Điều kiện truy vấn (deviceId, projectId, fromDate, toDate, warningType).
     * @return Danh sách chi tiết của các cảnh báo theo thiết bị và warningType.
     */
    @Override
    public List<Warning> countWarningString(final Map<String, Object> condition) {
        return warningPVMapper.countWarningString(condition);
    }

    /**
     * Chi tiết cảnh báo theo warningType và deviceId.
     *
     * @param condition Điều kiện truy vấn (deviceId, projectId, fromDate, toDate, warningType).
     * @return Danh sách chi tiết của các cảnh báo theo thiết bị và warningType.
     */
    @Override
    public List<Warning> getDetailWarningType(final Map<String, Object> condition) {
        return warningPVMapper.getDetailWarningType(condition);
    }

    /**
     * Đếm cảnh báo theo thời gian.
     *
     * @param condition Điều kiện lấy mô tả cảnh báo.
     * @return Danh sách tổng cảnh báo.
     */
    @Override
    public List<Warning> countWarnings(final Map<String, Object> condition) {
        return warningPVMapper.countWarnings(condition);
    }

    /**
     * Tổng số cảnh báo theo thời gian và kiểu cảnh báo.
     *
     * @param condition Điều kiện truy vấn.
     */
    @Override
    public List<Warning> getListWarningByWarningType(final Map<String, Object> condition) {
        return warningPVMapper.getListWarningByWarningType(condition);
    }

    /**
     * Thông tin chi tiết cảnh báo.
     *
     * @param condition Điều kiện lấy cảnh báo.
     * @return Thông tin chi tiết cảnh báo.
     */
    @Override
    public Warning getDetailWarningCachePV(final Map<String, Object> condition) {
        return warningPVMapper.getDetailWarningCachePV(condition);
    }

    /**
     * Cập nhật thông tin cảnh báo.
     *
     * @param condition data cảnh báo.
     */
    @Override
    public boolean updateWarningCachePV(final Map<String, Object> condition) {
        User user = userMapper.getUserByUsername((String) condition.get("username"));
        if (user != null) {
            condition.put("userId", user.getId());
            warningPVMapper.updateWarningCachePV(condition);
            return true;
        }
        return false;
    }

}
