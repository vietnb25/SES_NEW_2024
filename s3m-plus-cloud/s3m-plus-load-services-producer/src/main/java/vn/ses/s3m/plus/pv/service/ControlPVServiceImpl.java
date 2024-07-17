package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.DeviceMapper;
import vn.ses.s3m.plus.dao.ProjectMapper;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.History;
import vn.ses.s3m.plus.dto.Project;
import vn.ses.s3m.plus.dto.SchedulePV;
import vn.ses.s3m.plus.dto.SystemMap;
import vn.ses.s3m.plus.pv.dao.ControlMapper;

@Service
public class ControlPVServiceImpl implements ControlPVService {

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

        return deviceMapper.getDevicesControl(condition);
    }

    @Override
    public History getHistory(Map<String, Object> condition) {

        return controlMapper.getHistory(condition);
    }

    @Override
    public Device getDeviceById(Map<String, Object> condition) {

        return deviceMapper.getDeviceById(condition);
    }

    @Override
    public void addControl(Map<String, Object> condition) {

        controlMapper.add(condition);
    }

    @Override
    public void updateControl(Map<String, Object> condition) {

        controlMapper.update(condition);
    }

    @Override
    public void addSchedule(Map<String, Object> condition) {

        controlMapper.addSchedule(condition);
    }

    @Override
    public void updateSchedule(Map<String, Object> condition) {

        controlMapper.updateSchedule(condition);
    }

    @Override
    public void updateSendStatusById(Map<String, Object> condition) {

        controlMapper.updateSendStatusById(condition);
    }

    @Override
    public void updateStatus(Map<String, Object> condition) {

        controlMapper.updateStatus(condition);
    }

    @Override
    public List<SchedulePV> getSchedules(Map<String, Object> condition) {

        return controlMapper.getSchedules(condition);
    }

    @Override
    public List<History> getDeviceControl(Map<String, Object> condition) {

        return controlMapper.getDeviceControl(condition);
    }

}
