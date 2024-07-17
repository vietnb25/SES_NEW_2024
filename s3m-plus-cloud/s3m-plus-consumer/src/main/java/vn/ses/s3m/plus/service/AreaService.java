package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Area;
import vn.ses.s3m.plus.dto.SelectDevice;
import vn.ses.s3m.plus.response.AreaResponse;

public interface AreaService {

    List<Area> getListArea();

    Area getArea(Map<String, String> condition);

    List<Area> getAreas(Map<String, String> condition);

    void addArea(AreaResponse area);

    List<AreaResponse> searchArea(Map<String, String> condition);

    void editArea(AreaResponse area);

    void deleteArea(Integer id);

    List<AreaResponse> getAreaByCustomerId(Integer customerId);

    List<Area> getAreaByProjectId(Integer projectId);

    List<Area> getAreasActive(Map<String, String> condition);

    List<Area> checkDependentAreaByUser(Integer id);

    List<Area> checkDependentAreaByProject(Integer id);

    List<Area> getAreasByIds(Map<String, String> condition);

    List<Area> getAreaByCustomerIdAndManagerId(Map<String, Object> condition);

    Long getPowerByAreaId(Integer customerId, Integer superManagerId, Integer managerId, Integer areaId);

    Area getAreaDownload(Map<String, String> map);

    Map<String, String> getInformationArea(Map<String, String> condition);

    List<SelectDevice> getLocationSelectDevice(Map<String, String> con);

}
