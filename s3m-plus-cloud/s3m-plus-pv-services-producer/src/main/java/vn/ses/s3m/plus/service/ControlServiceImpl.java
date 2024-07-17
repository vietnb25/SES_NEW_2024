package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.ControlMapper;
import vn.ses.s3m.plus.dao.DeviceMapper;
import vn.ses.s3m.plus.dao.ProjectMapper;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.History;
import vn.ses.s3m.plus.dto.Project;
import vn.ses.s3m.plus.dto.SystemMap;

@Service
public class ControlServiceImpl implements ControlService {

    @Autowired
    private ControlMapper controlMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public List<SystemMap> getSystemMapPVByProject(Map<String, String> condition) {

        return controlMapper.getSystemMapPVByProject(condition);
    }

    @Override
    public List<History> getHistories(Map<String, String> condition) {

        return controlMapper.getHistories(condition);
    }

    @Override
    public History getHistoryLastestById(Map<String, Object> condition) {

        return controlMapper.getHistoryLastestById(condition);
    }

    @Override
    public Project getProjectById(Map<String, Object> condition) {

        return projectMapper.getProjectById(condition);
    }

    @Override
    public List<Device> getDevicesByProjectAndSystem(Map<String, Object> condtion) {

        return deviceMapper.getDevicesByProjectIdAndSystemTypeId(condtion);
    }

    @Override
    public Double getACPowerByProjectId(Map<String, Object> condition) {

        return projectMapper.getACPowerByProjectId(condition);
    }

    @Override
    public List<Device> getDevices(Map<String, Object> condition) {

        return deviceMapper.getDevices(condition);
    }

}
