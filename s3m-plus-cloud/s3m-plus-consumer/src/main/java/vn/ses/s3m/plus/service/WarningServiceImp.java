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
     * Đếm số cảnh báo theo project id và thời gian
     *
     * @param condition Điều kiện truy vấn theo projectId và thời gian.
     * @return Số cảnh báo theo điều kiện truy vấn.
     */
    @Override
    public Integer countProjectWarning(final Map<String, String> condition) {
        return warningMapper.countProjectWarning(condition);
    }

    /**
     * cảnh báo theo project id và thời gian
     *
     * @param condition Điều kiện truy vấn theo projectId và thời gian.
     * @return Số cảnh báo theo điều kiện truy vấn.
     */
    @Override
    public List<Warning> getWarningByProject(final Map<String, Object> condition) {
        return warningMapper.getWarningByProject(condition);
    }

    @Override
    public List<Warning> getWarningsByCustomerId(Map<String, Object> condition) {
        return warningMapper.getWarningsByCustomerId(condition);
    }

    @Override
    public List<Warning> getCountListWarning(Map<String, Object> condition) {
        return warningMapper.getCountListWarning(condition);
    }

    @Override
    public List<Warning> getWarningInstance(Map<String, String> condition) {
        return warningMapper.getWarningInstance(condition);
    }

    @Override
    public List<Warning> getTotalWarning(final Map<String, Object> condition) {
        return warningMapper.getTotalWarning(condition);
    }

    @Override
    public Integer getAllDeviceHasWarning(final Map<String, Object> condition) {
        return warningMapper.getAllDeviceHasWarning(condition);
    }

    @Override
    public Warning getListWarning(Map<String, String> condition) {
        return warningMapper.getListWarning(condition);
    }
}
