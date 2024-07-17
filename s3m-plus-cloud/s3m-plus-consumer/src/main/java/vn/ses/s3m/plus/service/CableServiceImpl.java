package vn.ses.s3m.plus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.CableMapper;
import vn.ses.s3m.plus.dto.Cable;

/**
 * Xử lý lấy thông tin cáp từ database
 *
 * @author Arius Vietnam JSC
 * @since 2022-01-01
 */
@Service
public class CableServiceImpl implements CableService {

    @Autowired
    private CableMapper cableMapper;

    /**
     * Lấy ra danh sách thông tin cáp
     *
     * @return Danh sách cáp
     */
    @Override
    public List<Cable> getCables() {
        return cableMapper.getCables();
    }

    /**
     * Lấy thông tin cáp theo id
     *
     * @param id Mã cáp
     * @return Thông tin cáp theo id
     */
    @Override
    public Cable getCableById(final int id) {
        return cableMapper.getCableById(id);
    }

    /**
     * Thêm mới thông tin cáp
     *
     * @param cable Đối tượng cáp được thêm vào
     */
    @Override
    public void insertCable(final Cable cable) {
        cableMapper.insertCable(cable);
    }

    /**
     * Chỉnh sửa thông tin cáp
     *
     * @param cable Đối tượng cáp được chỉnh sửa
     */
    @Override
    public void updateCable(final Cable cable) {
        cableMapper.updateCable(cable);
    }

    /**
     * Xóa thông tin cáp
     *
     * @param cableId Mã cáp
     */
    @Override
    public void deleteCable(final int cableId) {
        cableMapper.deleteCable(cableId);
    }

    /**
     * Tìm kiếm thông tin cáp
     *
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách cáp được tìm kiếm
     */
    @Override
    public List<Cable> searchCables(final String keyword) {
        return cableMapper.searchCables(keyword);
    }

    /**
     * Lấy thông tin cáp theo tên cáp
     *
     * @param cableName Tên cáp
     * @return Thông tin cáp được lấy theo tên
     */
    @Override
    public Cable getCableByCableName(final String cableName) {
        return cableMapper.getCableByCableName(cableName);
    }

}
