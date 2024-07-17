package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Manager;

/**
 * Interface xử lí tỉnh thành
 *
 * @author Arius Vietnam JSC
 * @since 31 thg 10, 2022
 */
@Mapper
public interface ManagerMapper {
    List<Manager> getManagers(Map<String, String> condition);

    List<Manager> getListManager();

    void addManager(Manager manager);

    void updateManager(Manager manager);

    void deleteManager(int id);

    Manager getManagerById(int id);

    List<Manager> searchManager(String keyword);

    List<Manager> getManagerByCustomerId(Map<String, String> condition);

    Manager getManagerByManagerName(String managerName);

    List<Manager> getManagersActive(Map<String, String> condition);

    Long getPowerByMangerId(Integer customerId, Integer superManagerId, Integer managerId);

    List<Manager> getManagerByCustomerIdAndSuperManagerId(Map<String, String> condition);

    Manager getManagerDownload(Map<String, String> map);

    Map<String, String> getInformationManager(Map<String, String> condition);

}
