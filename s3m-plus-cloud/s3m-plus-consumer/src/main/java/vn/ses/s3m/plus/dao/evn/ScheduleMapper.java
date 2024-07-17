package vn.ses.s3m.plus.dao.evn;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.dto.evn.Schedule;

@Mapper
public interface ScheduleMapper {
    List<Schedule> getListSchedule();

    Schedule getSchedule(Map<String, String> condition);

    List<Schedule> getSchedules(Map<String, String> condition);

    List<Schedule> getScheduleBySuperManagerId(Map<String, String> condition);

    List<Schedule> getSchedulesByDeviceIds(Map<String, String> condition);

    void add(Map<String, String> condition);

    void addSchedule(Map<String, String> condition);

    void update(Map<String, String> condition);

    void deleteHistoryIds(@Param ("deleteIdList") List<Integer> deleteIdList);
    
    List<Schedule> getSchedulesPlants(Map<String, Object> condition);

}
