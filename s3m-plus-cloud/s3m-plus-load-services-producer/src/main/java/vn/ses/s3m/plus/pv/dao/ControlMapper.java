package vn.ses.s3m.plus.pv.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.History;
import vn.ses.s3m.plus.dto.SchedulePV;
import vn.ses.s3m.plus.dto.SystemMap;

@Mapper
public interface ControlMapper {
    List<SystemMap> getSystemMapPVByProject(Map<String, String> condition);

    List<History> getHistories(Map<String, String> codition);

    History getHistoryLastestById(Map<String, Object> condition);

    History getHistory(Map<String, Object> condition);

    void add(Map<String, Object> condition);

    void update(Map<String, Object> condition);

    void addSchedule(Map<String, Object> condition);

    void updateSchedule(Map<String, Object> condition);

    void updateSendStatusById(Map<String, Object> condition);

    void updateStatus(Map<String, Object> condition);

    List<SchedulePV> getSchedules(Map<String, Object> condition);

    List<History> getDeviceControl(Map<String, Object> condition);
}
