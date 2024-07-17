package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.SuperManager;
import vn.ses.s3m.plus.form.SuperManagerForm;

/**
 * Interface Service Xử lý khu vực
 *
 * @author Arius Vietnam JSC
 * @since 28 thg 10, 2022
 */
public interface SuperManagerService {

    void addSuperManager(SuperManagerForm superManager);

    SuperManager getSuperManagerById(Long id);

    void updateSuperManager(SuperManager superManager);

    void deleteSuperManager(Long id);

    SuperManager getSuperManagerByName(String superManagerName);

    List<SuperManager> getListSuperManager();

    List<SuperManager> getListSuperManagerByName(String keyword);

    List<SuperManager> getSuperManagers(Map<String, String> map);

    List<SuperManager> getSuperManagersActive(Map<String, String> map);

    List<SuperManager> getPowerByCustomerId(Map<String, String> codition);

    List<SuperManager> getIds(String[] superManagerIds);

    List<SuperManager> getListSuperManagerByCustomerId(Map<String, String> condition);

    Long getTotalPowerBySuperManagerId(Map<String, String> map);

    SuperManager getSuperManagerByCustomerId(Map<String, String> map);

    Map<String, String> getInformationSuperManager(Map<String, String> condition);

}
