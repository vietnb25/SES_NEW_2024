package vn.ses.s3m.plus.pv.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.History;
import vn.ses.s3m.plus.dto.Project;
import vn.ses.s3m.plus.dto.SchedulePV;
import vn.ses.s3m.plus.dto.SystemMap;

public interface ControlPVService {

    List<SystemMap> getSystemMapPVByProject(Map<String, String> condition);

    List<History> getHistories(Map<String, String> condition);

    History getHistoryLastestById(Map<String, Object> condition);

    Project getProjectById(Map<String, Object> condition);

    List<Device> getDevicesByProjectAndSystem(Map<String, Object> condtion);

    Double getACPowerByProjectId(Map<String, Object> condition);

    List<Device> getDevices(Map<String, Object> condition);

    History getHistory(Map<String, Object> condition);

    Device getDeviceById(Map<String, Object> condition);

    void addControl(Map<String, Object> condition);

    void updateControl(Map<String, Object> condition);

    void addSchedule(Map<String, Object> condition);

    void updateSchedule(Map<String, Object> condition);

    void updateSendStatusById(Map<String, Object> condition);

    void updateStatus(Map<String, Object> condition);

    List<SchedulePV> getSchedules(Map<String, Object> condition);

    List<History> getDeviceControl(Map<String, Object> condition);
}
