package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Manager;

public interface ManagerService {

    List<Manager> getManagers(Map<String, String> condition);

    List<Manager> getListManager();

    void addManager(Manager manager);

    Manager getManagerById(int id);

    void deleteManager(int id);

    void updateManager(Manager manager);

    List<Manager> searchManager(String keyword);

    List<Manager> getManagerByCustomerId(Map<String, String> condition);

    Manager getManagerByManagerName(String managerName);

    List<Manager> getManagersActive(Map<String, String> condition);

    List<Manager> getManagerByCustomerIdAndSuperManagerId(Map<String, String> condition);

    Long getPowerByMangerId(Integer customerId, Integer superManagerId, Integer managerId);

    Manager getManagerDownload(Map<String, String> map);

    Map<String, String> getInformationManager(Map<String, String> condition);

}
