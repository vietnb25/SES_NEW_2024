package vn.ses.s3m.plus.service.evn;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.evn.ScheduleMapper;
import vn.ses.s3m.plus.dto.evn.Schedule;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Override
    public Schedule getSchedule(Map<String, String> condition) {
        return scheduleMapper.getSchedule(condition);
    }

    @Override
    public List<Schedule> getListSchedule() {
        // TODO Auto-generated method stub
        return scheduleMapper.getListSchedule();
    }

    @Override
    public List<Schedule> getScheduleBySuperManagerId(Map<String, String> condition) {
        // TODO Auto-generated method stub
        return scheduleMapper.getScheduleBySuperManagerId(condition);
    }

	@Override
	public List<Schedule> getSchedules(Map<String, String> condition) {
		return scheduleMapper.getSchedules(condition);
	}

	@Override
	public List<Schedule> getSchedulesPlants(Map<String, Object> condition) {
		// TODO Auto-generated method stub
		return scheduleMapper.getSchedulesPlants(condition);
	}
}
