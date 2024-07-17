package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.SuperManagerMapper;
import vn.ses.s3m.plus.dto.SuperManager;
import vn.ses.s3m.plus.form.SuperManagerForm;

/**
 * Service Xử lý khu vực
 *
 * @author Arius Vietnam JSC
 * @since 28 thg 10, 2022
 */
@Service
public class SuperManagerServiceImpl implements SuperManagerService {

    @Autowired
    private SuperManagerMapper smMapper;

    /**
     * Danh sách khu vực/miền
     *
     * @return Danh sách khu vực
     */
    @Override
    public List<SuperManager> getListSuperManager() {
        return smMapper.getListSuperManager();
    }

    /**
     * Thêm mới khu vực/miền
     *
     * @param superManager Đối tượng thêm mới
     */
    @Override
    public void addSuperManager(final SuperManagerForm superManager) {
        smMapper.addSuperManager(superManager);
    }

    /**
     * Cập nhật khu vực/miền
     *
     * @param superManager Dữ liệu đã chỉnh sửa
     */
    @Override
    public void updateSuperManager(final SuperManager superManager) {
        smMapper.updateSuperManager(superManager);
    }

    /**
     * Xóa khu vực/miền
     *
     * @param id Mã khu vực
     */
    @Override
    public void deleteSuperManager(final Long id) {
        smMapper.deleteSuperManager(id);
    }

    /**
     * Lấy khu vực/miền theo Id
     *
     * @param id Mã khu vực
     * @return Đối tượng khu vực
     */
    @Override
    public SuperManager getSuperManagerById(final Long id) {
        return smMapper.getSuperManagerById(id);
    }

    /**
     * Tìm kiếm khu vực/miền
     *
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách khu vực
     */
    @Override
    public List<SuperManager> getListSuperManagerByName(final String keyword) {
        return smMapper.getListSuperManagerByName(keyword);
    }

    /**
     * Lấy khu vực theo tên
     *
     * @param superManagerName Tên khu vực
     * @return Đối tượng khu vực
     */
    @Override
    public SuperManager getSuperManagerByName(final String superManagerName) {
        return smMapper.getSuperManagerByName(superManagerName);
    }

    /**
     * Lấy danh sách khu vực.
     */
    @Override
    public List<SuperManager> getSuperManagers(final Map<String, String> map) {
        return smMapper.getSuperManagers(map);
    }

    /**
     * Lấy danh sách khu vực theo điều kiện.
     */
    @Override
    public List<SuperManager> getSuperManagersActive(final Map<String, String> map) {
        return smMapper.getSuperManagersActive(map);
    }

    /**
     * Lấy ra danh sách khu vực theo khách hàng
     */
    @Override
    public List<SuperManager> getPowerByCustomerId(final Map<String, String> codition) {
        return smMapper.getPowerByCustomerId(codition);
    }

    /**
     *
     */
    @Override
    public List<SuperManager> getIds(final String[] superManagerIds) {
        return smMapper.getIds(superManagerIds);
    }

    /**
     * Lấy ra danh sách khu vực theo khách hàng
     */
    @Override
    public List<SuperManager> getListSuperManagerByCustomerId(final Map<String, String> condition) {
        return smMapper.getListSuperManagerByCustomerId(condition);
    }

    /**
     * Lấy ra tổng công suất Load theo khu vực
     *
     * @param superManagerId Mã khu vực
     * @return Tổng công suất Load
     */
    @Override
    public Long getTotalPowerBySuperManagerId(final Map<String, String> map) {
        return smMapper.getTotalPowerBySuperManagerId(map);
    }

    /**
     * Lấy ra miền theo khách hàng
     */
    @Override
    public SuperManager getSuperManagerByCustomerId(final Map<String, String> map) {
        return smMapper.getSuperManagerByCustomerId(map);
    }

    /**
     * Lấy ra thông tin miền/khu vực
     */
    @Override
    public Map<String, String> getInformationSuperManager(final Map<String, String> condition) {
        return smMapper.getInformationSuperManager(condition);
    }

}
