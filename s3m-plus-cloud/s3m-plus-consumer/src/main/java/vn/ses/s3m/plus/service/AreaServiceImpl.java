package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.AreaMapper;
import vn.ses.s3m.plus.dao.SelectDeviceMapper;
import vn.ses.s3m.plus.dto.Area;
import vn.ses.s3m.plus.dto.SelectDevice;
import vn.ses.s3m.plus.response.AreaResponse;

@Service
public class AreaServiceImpl implements AreaService {
    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private SelectDeviceMapper mapper;

    /**
     * Lấy ra danh sách quận huyện theo mã người quản lý .
     *
     * @param managerId Mã quản lý
     * @return Trả về danh sách quận huyện theo mã người quản lý
     */
    @Override
    public List<Area> getListArea() {
        return areaMapper.getListArea();
    }

    /**
     * Lấy ra danh sách quận huyện theo tham số truyền vào .
     *
     * @param condition Tham số truyền vào
     * @return Trả về danh sách quận huyện theo tham số truyền vào
     */
    @Override
    public Area getArea(final Map<String, String> condition) {
        return areaMapper.getArea(condition);
    }

    /**
     * Lấy ra danh sách quận huyện theo tham số truyền vào.
     *
     * @param condition Tham số truyền vào
     * @return Trả về danh sách quận huyện theo tham số truyền vào
     */
    @Override
    public List<Area> getAreas(final Map<String, String> condition) {
        return areaMapper.getAreas(condition);
    }

    /**
     * Thêm mới quận huyện .
     *
     * @param area Thông tin quận huyện
     * @return Thêm mới quận huyện vào danh sách
     */
    @Override
    public void addArea(final AreaResponse area) {
        areaMapper.addArea(area);
    }

    /**
     * Tìm kiếm quận huyện .
     *
     * @param condition Từ khóa người dùng nhập vào
     * @return Trả về danh sách quận huyện theo từ khóa tìm kiếm
     */
    @Override
    public List<AreaResponse> searchArea(final Map<String, String> condition) {
        return areaMapper.searchArea(condition);
    }

    /**
     * Chỉnh sửa thông tin quận huyện
     *
     * @param area Thông tin quận huyện
     * @return Trả về thông tin quận huyện đã được cập nhật
     */
    @Override
    public void editArea(final AreaResponse area) {
        areaMapper.editArea(area);
    }

    /**
     * Xóa thông tin quận huyện
     *
     * @param id Mã thông tin quận huyện
     * @return Trả về thông tin quận huyện đã được xóa
     */

    @Override
    public void deleteArea(final Integer id) {
        areaMapper.deleteArea(id);
    }

    /**
     * Lấy thông tin quận huyện theo mã khách hàng
     *
     * @param customerId Mã khách hàng
     * @return Trả về thông tin quận huyện theo mã khách hàng
     */
    @Override
    public List<AreaResponse> getAreaByCustomerId(final Integer customerId) {
        return areaMapper.getAreaByCustomerId(customerId);
    }

    /**
     * Lấy thông tin quận huyện theo mã dự án
     *
     * @param projectId Mã dự án
     * @return Trả về thông tin quận huyện theo mã dự án
     */
    @Override
    public List<Area> getAreaByProjectId(final Integer projectId) {
        return areaMapper.getAreaByProjectId(projectId);
    }

    /**
     * Lấy thông tin quận huyện theo điều kiện.
     *
     * @param condition Điều kiện lấy thông tin quận huyện
     * @return Danh sách quận huyện.
     */
    @Override
    public List<Area> getAreasActive(final Map<String, String> condition) {
        return areaMapper.getAreasActive(condition);

    }

    /**
     * Kiểm tra sự phụ thuộc quận huyện với khách hàng
     *
     * @param id Mã quận huyện
     * @return Trả về count của mã quận huyện
     */
    @Override
    public List<Area> checkDependentAreaByUser(final Integer id) {
        return areaMapper.checkDependentAreaByUser(id);
    }

    /**
     * Kiểm tra sự phụ thuộc quận huyện với dự án
     *
     * @param id Mã quận huyện
     * @return Trả về count của mã quận huyện
     */
    @Override
    public List<Area> checkDependentAreaByProject(final Integer id) {
        return areaMapper.checkDependentAreaByProject(id);
    }

    /**
     * Lấy ra danh sách quận huyện theo nhiều id
     */
    @Override
    public List<Area> getAreasByIds(final Map<String, String> condition) {
        return areaMapper.getAreasByIds(condition);
    }

    /**
     * Lấy ra tổng công suất theo mã quận huyện
     *
     * @param areaId Mã quận huyện
     * @return Tổng công suất theo mã quận huyện
     */
    @Override
    public Long getPowerByAreaId(final Integer customerId, final Integer superManagerId, final Integer managerId,
        final Integer areaId) {
        return areaMapper.getPowerByAreaId(customerId, superManagerId, managerId, areaId);
    }

    /**
     * Lấy ra danh sách quận huyện theo nhiều id
     */
    @Override
    public Area getAreaDownload(final Map<String, String> map) {
        return areaMapper.getAreaDownload(map);
    }

    /**
     * Lấy ra danh sách quận huyện
     */
    @Override
    public List<Area> getAreaByCustomerIdAndManagerId(final Map<String, Object> condition) {
        return areaMapper.getAreaByCustomerIdAndManagerId(condition);
    }

    /**
     * Lấy ra thông tin quận huyện
     */
    @Override
    public Map<String, String> getInformationArea(final Map<String, String> condition) {
        return areaMapper.getInformationArea(condition);
    }

    @Override
    public List<SelectDevice> getLocationSelectDevice(Map<String, String> con) {
        return this.mapper.getLocationSelectDevice(con);
    }


}
