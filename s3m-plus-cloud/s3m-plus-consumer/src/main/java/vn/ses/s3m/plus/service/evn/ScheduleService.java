package vn.ses.s3m.plus.service.evn;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.evn.Schedule;

public interface ScheduleService {
	
	List<Schedule> getSchedules(Map<String, String> condition);

    Schedule getSchedule(Map<String, String> condition);

    List<Schedule> getListSchedule();

    List<Schedule> getScheduleBySuperManagerId(Map<String, String> condition);
    
    List<Schedule> getSchedulesPlants(Map<String, Object> condition);

}
