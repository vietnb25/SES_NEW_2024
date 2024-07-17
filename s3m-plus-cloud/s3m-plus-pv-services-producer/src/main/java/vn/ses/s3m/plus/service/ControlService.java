package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.History;
import vn.ses.s3m.plus.dto.Project;
import vn.ses.s3m.plus.dto.SystemMap;

public interface ControlService {

    List<SystemMap> getSystemMapPVByProject(Map<String, String> condition);

    List<History> getHistories(Map<String, String> condition);

    History getHistoryLastestById(Map<String, Object> condition);

    Project getProjectById(Map<String, Object> condition);

    List<Device> getDevicesByProjectAndSystem(Map<String, Object> condtion);

    Double getACPowerByProjectId(Map<String, Object> condition);

    List<Device> getDevices(Map<String, Object> condition);
}
