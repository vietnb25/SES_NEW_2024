package vn.ses.s3m.plus.grid.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.UserMapper;
import vn.ses.s3m.plus.dao.WarningMapper;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.Warning;

@Service
public class WarningGridServiceImpl implements WarningGridService {

    @Autowired
    private WarningMapper warningGridMapper;

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
        return warningGridMapper.getWarnings(condition);
    }

    /**
     * Tổng số lần xuất hiện cảnh báo.
     *
     * @param condition Điều kiện truy vấn (deviceId, projectId, fromDate, toDate,...).
     * @return Danh sách cảnh báo theo từng thiết bị.
     */
    @Override
    public List<Warning> getTotalWarningGrid(final Map<String, Object> condition) {
        return warningGridMapper.getTotalWarningGrid(condition);
    }

    /**
     * Tổng thiết bị bị cảnh báo.
     *
     * @param condition Điều kiện truy vấn.
     */
    @Override
    public Integer getAllDeviceHasWarningGrid(final Map<String, Object> condition) {
        return warningGridMapper.getAllDeviceHasWarningGrid(condition);
    }

    /**
     * Chi tiết cảnh báo theo warningType và deviceId.
     *
     * @param condition Điều kiện truy vấn (deviceId, projectId, fromDate, toDate, warningType).
     * @return Danh sách chi tiết của các cảnh báo theo thiết bị và warningType.
     */
    @Override
    public List<Warning> getDetailWarningType(final Map<String, Object> condition) {
        return warningGridMapper.getDetailWarningType(condition);
    }

    /**
     * Số lần cảnh báo
     *
     * @param condition Điều kiện truy vấn (deviceId, projectId, fromDate, toDate, warningType).
     * @return Danh sách chi tiết của các cảnh báo theo thiết bị và warningType.
     */
    @Override
    public List<Warning> countWarningRMU(final Map<String, Object> condition) {
        return warningGridMapper.countWarningRMU(condition);
    }

    /**
     * Tổng số cảnh báo theo thời gian và kiểu cảnh báo.
     *
     * @param condition Điều kiện truy vấn.
     */
    @Override
    public List<Warning> getListWarningByWarningType(final Map<String, Object> condition) {
        return warningGridMapper.getListWarningByWarningType(condition);
    }

    /**
     * Thông tin chi tiết cảnh báo.
     *
     * @param condition Điều kiện lấy cảnh báo.
     * @return Thông tin chi tiết cảnh báo.
     */
    @Override
    public Warning getDetailWarningCacheGrid(final Map<String, Object> condition) {
        return warningGridMapper.getDetailWarningCachePV(condition);
    }

    /**
     * Cập nhật thông tin cảnh báo.
     *
     * @param condition data cảnh báo.
     */
    @Override
    public boolean updateWarningCacheGrid(final Map<String, Object> condition) {
        User user = userMapper.getUserByUsername((String) condition.get("username"));
        if (user != null) {
            condition.put("userId", user.getId());
            warningGridMapper.updateWarningCachePV(condition);
            return true;
        }
        return false;
    }
}
