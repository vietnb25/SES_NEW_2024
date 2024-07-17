package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.ManagerMapper;
import vn.ses.s3m.plus.dto.Manager;

/**
 * Xử lý lấy thông tin tỉnh thành
 *
 * @author Arius Vietnam JSC
 * @since 2022-01-01
 */
@Service
public class ManagerServiceImpl implements ManagerService {
    @Autowired
    private ManagerMapper managerMapper;

    /**
     * Lấy ra danh sách tỉnh thành theo điều kiện
     *
     * @param codition Điều kiện truyền vào hàm
     * @return Danh sách tỉnh theo được lọc theo điều kiện
     */
    @Override
    public List<Manager> getManagers(final Map<String, String> condition) {
        return managerMapper.getManagers(condition);
    }

    /**
     * Lấy ra danh sách tỉnh thành
     *
     * @return Danh sách tỉnh thành
     */
    @Override
    public List<Manager> getListManager() {
        return managerMapper.getListManager();
    }

    /**
     * Thêm mới thông tin tỉnh thành
     *
     * @param manager Đối tượng tỉnh thành thêm vào
     */
    @Override
    public void addManager(final Manager manager) {
        managerMapper.addManager(manager);
    }

    /**
     * Chỉnh sửa thông tin tỉnh thành
     *
     * @param manager Đối tượng tỉnh thành được chỉnh sửa
     */
    @Override
    public void updateManager(final Manager manager) {
        managerMapper.updateManager(manager);
    }

    /**
     * Xóa thông tin tỉnh thành
     *
     * @param id Mã tỉnh thành
     */
    @Override
    public void deleteManager(final int id) {
        managerMapper.deleteManager(id);
    }

    /**
     * Lấy ra tỉnh thành theo id của tỉnh thành đó
     */
    @Override
    public Manager getManagerById(final int id) {
        return managerMapper.getManagerById(id);
    }

    /**
     * Tìm kiếm tỉnh thành
     *
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách tỉnh thành được tìm kiếm
     */
    @Override
    public List<Manager> searchManager(final String keyword) {
        return managerMapper.searchManager(keyword);
    }

    /**
     * Lấy ra danh sách tỉnh thành theo khách hàng
     *
     * @param condition Điều kiện truyền vào hàm
     * @return Danh sách tỉnh thành được lấy ra
     */
    @Override
    public List<Manager> getManagerByCustomerId(final Map<String, String> condition) {
        return managerMapper.getManagerByCustomerId(condition);
    }

    /**
     * Lấy ra tỉnh thành theo tên tỉnh thành
     *
     * @param managerName Tên tỉnh thành
     * @return Tỉnh thành được lấy theo tên
     */
    @Override
    public Manager getManagerByManagerName(final String managerName) {
        return managerMapper.getManagerByManagerName(managerName);
    }

    /**
     * Lấy ra tỉnh thành theo điều kiện.
     */
    @Override
    public List<Manager> getManagersActive(final Map<String, String> condition) {
        return managerMapper.getManagersActive(condition);
    }

    /**
     * Lấy ra danh sách tổng công suất theo khu vực
     */
    @Override
    public Long getPowerByMangerId(final Integer customerId, final Integer superManagerId, final Integer managerId) {
        return managerMapper.getPowerByMangerId(customerId, superManagerId, managerId);
    }

    /**
     * Lấy ra danh sách tỉnh thành theo khách hàng và khu vực
     */
    @Override
    public List<Manager> getManagerByCustomerIdAndSuperManagerId(final Map<String, String> condition) {
        return managerMapper.getManagerByCustomerIdAndSuperManagerId(condition);
    }

    /**
     * Lấy ra danh sách tỉnh thành theo khách hàng và khu vực
     */
    @Override
    public Manager getManagerDownload(final Map<String, String> map) {
        return managerMapper.getManagerDownload(map);
    }

    /**
     * Lấy ra thông tin tỉnh thành
     */
    @Override
    public Map<String, String> getInformationManager(final Map<String, String> condition) {
        return managerMapper.getInformationManager(condition);
    }

}
