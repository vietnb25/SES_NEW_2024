package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.UserMapper;
import vn.ses.s3m.plus.dao.WarningMapper;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.Warning;

@Service
public class WarningServiceImp implements WarningService {
    @Autowired
    private WarningMapper warningMapper;

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
        return warningMapper.getWarnings(condition);
    }

    /**
     * Lấy danh sách deviceId.
     *
     * @param condition Điều kiện lấy list deviceId.
     * @return Danh sách deviceId.
     */
    @Override
    public List<String> getListDeviceId(final Map<String, Object> condition) {
        return warningMapper.getListDeviceId(condition);
    }

    /**
     * Tổng số lần xuất hiện cảnh báo.
     *
     * @param condition Điều kiện truy vấn (deviceId, projectId, fromDate, toDate,...).
     * @return Danh sách cảnh báo theo từng thiết bị.
     */
    @Override
    public List<Warning> getTotalWarning(final Map<String, Object> condition) {
        return warningMapper.getTotalWarning(condition);
    }

    /**
     * Chi tiết cảnh báo theo warningType và deviceId.
     *
     * @param condition Điều kiện truy vấn (deviceId, projectId, fromDate, toDate, warningType).
     * @return Danh sách chi tiết của các cảnh báo theo thiết bị và warningType.
     */
    @Override
    public List<Warning> getDetailWarningByWarningType(final Map<String, Object> condition) {
        return warningMapper.getDetailWarningByWarningType(condition);
    }

    /**
     * Lấy danh sách cảnh báo.
     *
     * @param condition Điều kiện lấy cảnh báo.
     * @return Danh sách cảnh báo.
     */
    @Override
    public List<Warning> getWarningList(final Map<String, Object> condition) {
        return warningMapper.getWarningList(condition);
    }

    /**
     * Lấy danh sách bộ đệm cảnh báo.
     *
     * @param condition Điều kiện lấy bộ đệm cảnh báo.
     * @return Danh sách bộ đệm cảnh báo.
     */
    @Override
    public List<Warning> getWarningCaches(final Map<String, Object> condition) {
        return warningMapper.getWarningCaches(condition);
    }

    /**
     * Lấy mô tả cảnh báo trạng thái.
     *
     * @param condition Điều kiện lấy mô tả cảnh báo.
     * @return Danh sách mô tả cảnh báo.
     */
    @Override
    public Warning getStatusWarningDescription(final Map<String, Object> condition) {
        return warningMapper.getStatusWarningDescription(condition);
    }

    /**
     * Lấy ra danh sách cảnh báo theo từng thiết bị
     *
     * @param condition Điều kiện lấy danh sách cảnh báo
     * @return Danh sách cảnh báo
     */
    @Override
    public List<Warning> getWarningByDevice(final Map<String, Object> condition) {
        return warningMapper.getWarningByDevice(condition);
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
     * Thông tin chi tiết cảnh báo.
     *
     * @param condition Điều kiện lấy cảnh báo.
     * @return Thông tin chi tiết cảnh báo.
     */
    @Override
    public Warning getDetailWarningCache(final Map<String, Object> condition) {
        return warningMapper.getDetailWarningCache(condition);
    }

    /**
     * Cập nhật thông tin cảnh báo.
     *
     * @param condition data cảnh báo.
     */
    @Override
    public boolean updateWarningCache(final Map<String, Object> condition) {
        User user = userMapper.getUserByUsername((String) condition.get("username"));
        if (user != null) {
            condition.put("userId", user.getId());
            warningMapper.updateWarningCache(condition);
            return true;
        }
        return false;
    }

    /**
     * Tổng thiết bị bị cảnh báo.
     *
     * @param condition Điều kiện truy vấn.
     */
    @Override
    public Integer getAllDeviceHasWarning(final Map<String, Object> condition) {
        return warningMapper.getAllDeviceHasWarning(condition);
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
