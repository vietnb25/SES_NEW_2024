package vn.ses.s3m.plus.controllers.evn;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.dto.Area;
import vn.ses.s3m.plus.dto.Device;

import vn.ses.s3m.plus.dto.Manager;
import vn.ses.s3m.plus.dto.Project;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.evn.Schedule;
import vn.ses.s3m.plus.dto.evn.HistoryEVN;
import vn.ses.s3m.plus.dto.evn.HistoryForm;
import vn.ses.s3m.plus.service.AreaService;
import vn.ses.s3m.plus.service.DeviceService;
import vn.ses.s3m.plus.service.evn.HistoryService;
import vn.ses.s3m.plus.service.ManagerService;
import vn.ses.s3m.plus.service.ProjectService;
import vn.ses.s3m.plus.service.UserService;
import vn.ses.s3m.plus.service.evn.ScheduleService;

@RestController
@RequestMapping("/common/storage")
public class StorageController {
	@Autowired
	private UserService userService;
	@Autowired
	private ManagerService managerService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private ScheduleService scheduleService;
	@Autowired
	private AreaService areaService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private DeviceService deviceService;
	// lấy list tổng dựa trên thông tin user login(controller)

	@GetMapping("/list")
	public ResponseEntity<Map<String, Object>> getListStorage(@RequestParam("username") String username,
			@RequestParam(name = "fromDate", defaultValue = "") String fromDate,
			@RequestParam(name = "toDate", defaultValue = "") String toDate) {

		User user = userService.getUserByUsername(username);

		Map<String, Object> data = getAllDataByUser(user, fromDate, toDate);

		return new ResponseEntity<Map<String, Object>>(data, HttpStatus.OK);
	}

	// lấy list tổng dựa trên thông tin user login(method)
	public Map<String, Object> getAllDataByUser(User user, String fromDate, String toDate) {
		Map<String, String> conditionSearch = new HashMap<String, String>();
		if (fromDate.length() > 3) {
			conditionSearch.put("fromDate", fromDate);
		} else {
			conditionSearch.put("fromDate", null);
		}
		if (toDate.length() > 3) {
			conditionSearch.put("toDate", toDate);
		} else {
			conditionSearch.put("toDate", null);
		}
		System.out.println("conditionSearch:" + conditionSearch);

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateNow = formatter.format(cal.getTime());

		Map<String, Object> data = new HashMap<>();
		if (StringUtils.equals(String.valueOf(user.getUserType()), "3")) {
			conditionSearch.put("typeScrop", "5");
			conditionSearch.put("toDate", dateNow);
			List<HistoryEVN> listHistorys = historyService.getHistorys(conditionSearch);
			List<HistoryEVN> historys = new ArrayList<HistoryEVN>();
			if (!listHistorys.isEmpty()) {
				for (HistoryEVN historyEVN : listHistorys) {
					HistoryEVN h = new HistoryEVN();
					h.setHistoryId(historyEVN.getHistoryId());
					h.setFromDate(historyEVN.getFromDate());
					h.setToDate(historyEVN.getToDate());
					h.setTimeFrame(historyEVN.getTimeFrame());
					h.setCongSuatDinhMuc(historyEVN.getCongSuatDinhMuc());
					h.setCongSuatChoPhep(historyEVN.getCongSuatChoPhep());
					h.setCongSuatTietGiam(historyEVN.getCongSuatTietGiam());
					h.setTimeInsert(historyEVN.getTimeInsert());
					h.setDeleteFlag(historyEVN.getDeleteFlag());
					historys.add(h);
				}
			}
			List<Schedule> listSchedules = scheduleService.getListSchedule();
			data.put("storage", formatHistory(historys));
			data.put("schedules", formatSchedule(listSchedules));
		}
		if (StringUtils.equals(String.valueOf(user.getUserType()), "4")) {

			conditionSearch.put("toDate", dateNow);
			conditionSearch.put("typeScrop", "0");
			conditionSearch.put("stt", String.valueOf(user.getTargetId()));

			List<HistoryEVN> historyList = historyService.getHistorys(conditionSearch);
			List<HistoryEVN> historys = new ArrayList<HistoryEVN>();
			if (!historyList.isEmpty()) {
				for (HistoryEVN history : historyList) {
					HistoryEVN h = new HistoryEVN();
					h.setHistoryId(history.getHistoryId());
					h.setFromDate(history.getFromDate());
					h.setToDate(history.getToDate());
					h.setTimeFrame(history.getTimeFrame());
					h.setCongSuatDinhMuc(history.getCongSuatDinhMuc());
					h.setCongSuatChoPhep(history.getCongSuatChoPhep());
					h.setCongSuatTietGiam(history.getCongSuatTietGiam());
					h.setTimeInsert(history.getTimeInsert());
					h.setDeleteFlag(history.getDeleteFlag());
					historys.add(h);
				}
			}

			List<Schedule> listSchedules = new ArrayList<>(
					getSchedulesBySuperManagerID(String.valueOf(user.getSuperManagerId())));

			data.put("storage", formatHistory(historys));
			data.put("schedules", formatSchedule(listSchedules));
		}
		if (StringUtils.equals(String.valueOf(user.getUserType()), "5")) {

			conditionSearch.put("toDate", dateNow);
			conditionSearch.put("typeScrop", "1");
			conditionSearch.put("stt", String.valueOf(user.getTargetId()));

			List<HistoryEVN> historyList = historyService.getHistorys(conditionSearch);
			List<HistoryEVN> historys = new ArrayList<HistoryEVN>();
			if (!historyList.isEmpty()) {
				for (HistoryEVN history : historyList) {
					HistoryEVN h = new HistoryEVN();
					h.setHistoryId(history.getHistoryId());
					h.setFromDate(history.getFromDate());
					h.setToDate(history.getToDate());
					h.setTimeFrame(history.getTimeFrame());
					h.setCongSuatDinhMuc(history.getCongSuatDinhMuc());
					h.setCongSuatChoPhep(history.getCongSuatChoPhep());
					h.setCongSuatTietGiam(history.getCongSuatTietGiam());
					h.setTimeInsert(history.getTimeInsert());
					h.setDeleteFlag(history.getDeleteFlag());
					historys.add(h);
				}
			}

			List<Schedule> listSchedules = new ArrayList<>(
					getSchedulesByManagerID(String.valueOf(user.getManagerId())));

			data.put("storage", formatHistory(historys));
			data.put("schedules", formatSchedule(listSchedules));
		}
		if (StringUtils.equals(String.valueOf(user.getUserType()), "6")) {

			conditionSearch.put("toDate", dateNow);
			conditionSearch.put("typeScrop", "2");
			conditionSearch.put("stt", String.valueOf(user.getTargetId()));

			List<HistoryEVN> historyList = historyService.getHistorys(conditionSearch);
			List<HistoryEVN> historys = new ArrayList<HistoryEVN>();
			if (!historyList.isEmpty()) {
				for (HistoryEVN history : historyList) {
					HistoryEVN h = new HistoryEVN();
					h.setHistoryId(history.getHistoryId());
					h.setFromDate(history.getFromDate());
					h.setToDate(history.getToDate());
					h.setTimeFrame(history.getTimeFrame());
					h.setCongSuatDinhMuc(history.getCongSuatDinhMuc());
					h.setCongSuatChoPhep(history.getCongSuatChoPhep());
					h.setCongSuatTietGiam(history.getCongSuatTietGiam());
					h.setTimeInsert(history.getTimeInsert());
					h.setDeleteFlag(history.getDeleteFlag());
					historys.add(h);
				}
			}

			List<Schedule> listSchedules = new ArrayList<>(getSchedulesByAreaID(String.valueOf(user.getAreaId())));

			data.put("storage", formatHistory(historys));
			data.put("schedules", formatSchedule(listSchedules));
		}
//		if (StringUtils.equals(String.valueOf(user.getUserType()), "7")) {
//			conditionSearch.put("toDate", dateNow);
//			conditionSearch.put("typeScrop", "3");
//			conditionSearch.put("stt", String.valueOf(user.getSuperManagerId()));
//
//			List<HistoryEVN> historyList = historyService.getHistorys(conditionSearch);
//			List<HistoryEVN> historys = new ArrayList<HistoryEVN>();
//			if (!historyList.isEmpty()) {
//				for (HistoryEVN history : historyList) {
//					HistoryEVN h = new HistoryEVN();
//					h.setHistoryId(history.getHistoryId());
//					h.setFromDate(history.getFromDate());
//					h.setToDate(history.getToDate());
//					h.setTimeFrame(history.getTimeFrame());
//					h.setCongSuatDinhMuc(history.getCongSuatDinhMuc());
//					h.setCongSuatChoPhep(history.getCongSuatChoPhep());
//					h.setCongSuatTietGiam(history.getCongSuatTietGiam());
//					h.setTimeInsert(history.getTimeInsert());
//					h.setDeleteFlag(history.getDeleteFlag());
//					historys.add(h);
//				}
//			}
//			List<Schedule> listSchedules = new ArrayList<>(getSchedulesByAreaID(String.valueOf(user.getAreaId())));
//			data.put("storage", formatHistory(historys));
//			data.put("schedules", formatSchedule(listSchedules));
//		}
		return data;
	}

	// lấy list Historys bằng SupperManagerId
	public List<HistoryEVN> getHistorysBySuperManagerID(String superManagerId, String fromDate, String toDate) {
		Map<String, String> conditionManager = new HashMap<String, String>();
		conditionManager.put("superManagerId", superManagerId);
		List<Manager> managers = managerService.getManagers(conditionManager);
		List<HistoryEVN> historys = new ArrayList<HistoryEVN>();
		for (Manager manager : managers) {
			Map<String, String> conditionHistory = new HashMap<String, String>();
			conditionHistory.put("managerId", String.valueOf(manager.getManagerId()));
			conditionHistory.put("typeScrop", "1");
			if (fromDate.length() > 3) {
				conditionHistory.put("fromDate", fromDate);
			} else {
				conditionHistory.put("fromDate", null);
			}
			if (toDate.length() > 3) {
				conditionHistory.put("toDate", toDate);
			} else {
				conditionHistory.put("toDate", null);
			}
			List<HistoryEVN> listHistorys = historyService.getListHistoryBySuperManagerId(conditionHistory);
			historys.addAll(listHistorys);
		}
		return historys;
	}

	// lấy list Schedules bằng SupperManagerId
	public List<Schedule> getSchedulesBySuperManagerID(String superManagerId) {
		Map<String, String> conditionManager = new HashMap<String, String>();
		conditionManager.put("superManagerId", superManagerId);

		List<Manager> managers = managerService.getManagers(conditionManager);

		List<Schedule> schedules = new ArrayList<Schedule>();
		for (Manager manager : managers) {
			Map<String, String> conditionHistory = new HashMap<String, String>();
			conditionHistory.put("managerId", String.valueOf(manager.getManagerId()));
			conditionHistory.put("typeScrop", "1");
			List<Schedule> listSchedules = scheduleService.getScheduleBySuperManagerId(conditionManager);
			schedules.addAll(listSchedules);
		}

		return schedules;
	}

	// lấy list Historys bằng ManagerId
	public List<HistoryEVN> getHistorysByManagerID(String managerId, String fromDate, String toDate) {
		Map<String, String> conditionArea = new HashMap<String, String>();
		conditionArea.put("managerId", managerId);

		List<Area> areas = areaService.getAreas(conditionArea);

		List<HistoryEVN> historys = new ArrayList<HistoryEVN>();
		for (Area area : areas) {
			Map<String, String> conditionHistory = new HashMap<String, String>();
			conditionHistory.put("managerId", String.valueOf(area.getAreaId()));
			conditionHistory.put("typeScrop", "2");

			List<HistoryEVN> listHistorys = historyService.getListHistoryBySuperManagerId(conditionHistory);
			if (fromDate.length() > 3) {
				conditionHistory.put("fromDate", fromDate);
			} else {
				conditionHistory.put("fromDate", null);
			}
			if (toDate.length() > 3) {
				conditionHistory.put("toDate", toDate);
			} else {
				conditionHistory.put("toDate", null);
			}
			historys.addAll(listHistorys);
		}
		return historys;
	}

	// lấy list Schedules bằng ManagerId
	public List<Schedule> getSchedulesByManagerID(String managerId) {
		Map<String, String> conditionArea = new HashMap<String, String>();
		conditionArea.put("managerId", managerId);

		List<Area> areas = areaService.getAreas(conditionArea);

		List<Schedule> schedules = new ArrayList<Schedule>();
		for (Area area : areas) {
			Map<String, String> conditionHistory = new HashMap<String, String>();
			conditionHistory.put("managerId", String.valueOf(area.getAreaId()));
			conditionHistory.put("typeScrop", "2");

			List<Schedule> listSchedules = scheduleService.getScheduleBySuperManagerId(conditionHistory);

			schedules.addAll(listSchedules);
		}

		return schedules;
	}

	// lấy list Historys bằng AreaId
	public List<HistoryEVN> getHistorysByAreaID(String AreaId, String fromDate, String toDate) {
		Map<String, String> conditionProject = new HashMap<String, String>();
		conditionProject.put("areaId", AreaId);

		List<Project> projects = projectService.getProjectList(conditionProject);

		List<HistoryEVN> historys = new ArrayList<HistoryEVN>();
		for (Project project : projects) {
			Map<String, String> conditionHistory = new HashMap<String, String>();
			conditionHistory.put("areaId", String.valueOf(project.getAreaId()));
			conditionHistory.put("typeScrop", "3");
			if (fromDate.length() > 3) {
				conditionHistory.put("fromDate", fromDate);
			} else {
				conditionHistory.put("fromDate", null);
			}
			if (toDate.length() > 3) {
				conditionHistory.put("toDate", toDate);
			} else {
				conditionHistory.put("toDate", null);
			}
			List<HistoryEVN> listHistorys = historyService.getListHistoryBySuperManagerId(conditionHistory);

			historys.addAll(listHistorys);
		}

		return historys;
	}

	// lấy list Schedules bằng AreaId
	public List<Schedule> getSchedulesByAreaID(String AreaId) {
		Map<String, String> conditionProject = new HashMap<String, String>();
		conditionProject.put("projectId", AreaId);

		List<Project> projects = projectService.getProjectList(conditionProject);

		List<Schedule> schedules = new ArrayList<Schedule>();
		for (Project project : projects) {
			Map<String, String> conditionHistory = new HashMap<String, String>();
			conditionHistory.put("areaId", String.valueOf(project.getAreaId()));
			conditionHistory.put("typeScrop", "3");

			List<Schedule> listSchedules = scheduleService.getScheduleBySuperManagerId(conditionHistory);

			schedules.addAll(listSchedules);
		}

		return schedules;
	}

	// lấy list Historys bằng projectId
	public List<HistoryEVN> getHistorysByProjectID(String projectId, String fromDate, String toDate) {
		Map<String, String> conditionDevice = new HashMap<String, String>();
		conditionDevice.put("projectId", projectId);

		List<Device> devices = deviceService.getDeviceByProjectId(conditionDevice);

		List<HistoryEVN> historys = new ArrayList<HistoryEVN>();
		for (Device device : devices) {
			Map<String, String> conditionHistory = new HashMap<String, String>();
			conditionHistory.put("areaId", String.valueOf(device.getProjectId()));
			conditionHistory.put("typeScrop", "4");
			if (fromDate.length() > 3) {
				conditionHistory.put("fromDate", fromDate);
			} else {
				conditionHistory.put("fromDate", null);
			}
			if (toDate.length() > 3) {
				conditionHistory.put("toDate", toDate);
			} else {
				conditionHistory.put("toDate", null);
			}
			List<HistoryEVN> listHistorys = historyService.getListHistoryBySuperManagerId(conditionHistory);

			historys.addAll(listHistorys);
		}

		return historys;
	}

	// lấy list Schedules bằng ProjectId
	public List<Schedule> getSchedulesByProjectID(String projectId) {
		Map<String, String> conditionDevice = new HashMap<String, String>();
		conditionDevice.put("projectId", projectId);

		List<Device> devices = deviceService.getDeviceByProjectId(conditionDevice);

		List<Schedule> schedules = new ArrayList<Schedule>();
		for (Device devide : devices) {
			Map<String, String> conditionHistory = new HashMap<String, String>();
			conditionHistory.put("areaId", String.valueOf(devide.getProjectId()));
			conditionHistory.put("typeScrop", "4");

			List<Schedule> listSchedules = scheduleService.getScheduleBySuperManagerId(conditionHistory);

			schedules.addAll(listSchedules);
		}

		return schedules;
	}

	// lấy list bằng SupperManagerId(controller)
	@GetMapping("/listBySuperManagerId")
	public ResponseEntity<Map<String, Object>> getListStorageBySuperManagerId(
			@RequestParam("superManagerId") String superManagerId,
			@RequestParam(name = "fromDate", defaultValue = "") String fromDate,
			@RequestParam(name = "toDate", defaultValue = "") String toDate) {
		Map<String, String> condition = new HashMap<String, String>();
		Map<String, Object> data = new HashMap<>();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateNow = formatter.format(cal.getTime());

		condition.put("typeScrop", "0");
		condition.put("stt", superManagerId);
		if (fromDate.length() > 3 && toDate.length() > 3) {
			condition.put("fromDate", fromDate);
			condition.put("toDate", toDate);

			List<HistoryEVN> historys = historyService.searchHistory(condition);

			data.put("storage", formatHistory(historys));
		} else {
			condition.put("toDate", dateNow);

			List<HistoryEVN> historys = historyService.getHistorys(condition);
			data.put("storage", formatHistory(historys));
		}

		System.out.println("condition:" + condition);
		condition.put("superManagerId", superManagerId);
		List<Schedule> schedules = scheduleService.getScheduleBySuperManagerId(condition);
		data.put("schedules", formatSchedule(schedules));
		return new ResponseEntity<Map<String, Object>>(data, HttpStatus.OK);
	}

	// lấy list bằng ManagerId(controller)
	@GetMapping("/listByManagerId")
	public ResponseEntity<Map<String, Object>> getListStorageByManagerId(@RequestParam("managerId") String managerId,
			@RequestParam(name = "fromDate", defaultValue = "") String fromDate,
			@RequestParam(name = "toDate", defaultValue = "") String toDate) {
		Map<String, String> condition = new HashMap<String, String>();
		Map<String, Object> data = new HashMap<>();

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateNow = formatter.format(cal.getTime());
		
		condition.put("stt", managerId);
		condition.put("typeScrop", "1");
		if (fromDate.length() > 3 && toDate.length() > 3) {
			condition.put("fromDate", fromDate);
			condition.put("toDate", toDate);

			List<HistoryEVN> historys = historyService.searchHistory(condition);

			data.put("storage", formatHistory(historys));
		} else {
			condition.put("toDate", dateNow);
			List<HistoryEVN> historys = historyService.getHistorys(condition);
			data.put("storage", formatHistory(historys));
		}
		
		condition.put("managerId", managerId);
		List<Schedule> schedules = scheduleService.getScheduleBySuperManagerId(condition);
		data.put("schedules", formatSchedule(schedules));
		return new ResponseEntity<Map<String, Object>>(data, HttpStatus.OK);
	}

	// lấy list bằng AreaId(controller)
	@GetMapping("/listByAreaId")
	public ResponseEntity<Map<String, Object>> getListStorageByAreaId(@RequestParam("areaId") String areaId,
			@RequestParam(name = "fromDate", defaultValue = "") String fromDate,
			@RequestParam(name = "toDate", defaultValue = "") String toDate) {
		Map<String, String> condition = new HashMap<String, String>();
		Map<String, Object> data = new HashMap<>();
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateNow = formatter.format(cal.getTime());
		
		condition.put("stt", areaId);
		condition.put("typeScrop", "2");
		
        if (fromDate.length() > 3 && toDate.length() > 3) {
            condition.put("fromDate", fromDate);
        } else {
        	condition.put("toDate", dateNow);
        	List<HistoryEVN> historys = historyService.getHistorys(condition);
        	data.put("storage", formatHistory(historys));
        }
        if (toDate.length() > 3) {
            condition.put("toDate", toDate);
        } else {
            condition.put("toDate", null);
        }
        
        condition.put("areaId", areaId);
		List<Schedule> schedules = scheduleService.getScheduleBySuperManagerId(condition);
		data.put("schedules", formatSchedule(schedules));
		return new ResponseEntity<Map<String, Object>>(data, HttpStatus.OK);
	}

	// lấy list bằng ProjectId(controller)
	@GetMapping("/listByProjectId")
	public ResponseEntity<Map<String, Object>> getListStorageByProjectId(@RequestParam("projectId") String projectId,
			@RequestParam(name = "fromDate", defaultValue = "") String fromDate,
			@RequestParam(name = "toDate", defaultValue = "") String toDate) {
		Map<String, String> condition = new HashMap<String, String>();
		Map<String, Object> data = new HashMap<>();
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateNow = formatter.format(cal.getTime());
		
		condition.put("stt", projectId);
		condition.put("typeScrop", "3");
        if (fromDate.length() > 3 && toDate.length() > 3) {
            condition.put("fromDate", fromDate);
        } else {
        	condition.put("toDate", dateNow);
        	List<HistoryEVN> historys = historyService.getHistorys(condition);
        	data.put("storage", formatHistory(historys));
        }
        if (toDate.length() > 3) {
            condition.put("toDate", toDate);
        } else {
            condition.put("toDate", null);
        }
		
        condition.put("projectId", projectId);
		List<Schedule> schedules = scheduleService.getScheduleBySuperManagerId(condition);
		data.put("schedules", formatSchedule(schedules));
		return new ResponseEntity<Map<String, Object>>(data, HttpStatus.OK);
	}

	public List<HistoryForm> formatHistory(List<HistoryEVN> listhistoryRaw) {
		List<HistoryForm> listResult = new ArrayList<HistoryForm>();
		for (HistoryEVN historyRaw : listhistoryRaw) {
			HistoryForm result = new HistoryForm();
			result.setHistoryId(historyRaw.getHistoryId());
			result.setFromDate(historyRaw.getFromDate());
			result.setToDate(historyRaw.getToDate());
			result.setTimeFrame(historyRaw.getTimeFrame());
			result.setCongSuatDinhMuc(historyRaw.getCongSuatDinhMuc() / 1000000);
			result.setCongSuatChoPhep(historyRaw.getCongSuatChoPhep() / 1000000);
			result.setCongSuatTietGiam(historyRaw.getCongSuatTietGiam() / 1000000);
			Timestamp time = historyRaw.getTimeInsert();
			result.setTimeInsert(time.toString());
			result.setDeleteFlag(historyRaw.getDeleteFlag());
			result.setStatus(historyRaw.getStatus());
			listResult.add(result);
		}
		return listResult;
	}

	public List<Schedule> formatSchedule(List<Schedule> listscheduleRaw) {
		List<Schedule> listResult = new ArrayList<Schedule>();
		for (Schedule scheduleRaw : listscheduleRaw) {
			Schedule schedule = scheduleRaw;

			schedule.setCongSuatChoPhep(scheduleRaw.getCongSuatChoPhep() / 1000000);
			schedule.setCongSuatTietGiam(scheduleRaw.getCongSuatTietGiam() / 1000000);
			listResult.add(schedule);
		}
		return listResult;
	}

}
