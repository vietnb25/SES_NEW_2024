package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.LoadTypeMapper;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.LoadType;

@Service
public class LoadTypeServiceImpl implements LoadTypeService {

    @Autowired
    private LoadTypeMapper mapper;

    @Override
    public List<LoadType> getListLoadType() {
        return mapper.getListLoadType();
    }

    @Override
    public List<LoadType> getListLoadBySystemTypeIdAndProjectId(Map<String, Object> condition) {
        return mapper.getListLoadBySystemTypeIdAndProjectId(condition);
    }

    @Override
    public List<LoadType> getListLoadTypeByListId(Map<String, Object> condition) {
        return mapper.getListLoadTypeByListId(condition);
    }

	@Override
	public List<LoadType> getAllLoadType(Map<String, Object> conditon) {
		return mapper.getAllLoadType(conditon);
	}

	@Override
	public void addLoadType(String schema, LoadType loadType) {
		mapper.addLoadType(schema, loadType);
	}

	@Override
	public void updateLoadType(String schema, LoadType loadType) {
		mapper.updateLoadType(schema, loadType);
	}

	@Override
	public void deleteLoadTypeById(Integer id) {
		mapper.deleteLoadTypeById(id);
	}

	@Override
	public LoadType getLoadTypeById(String schema, Integer id) {
		return mapper.getLoadTypeById(schema, id);
	}
    @Override
    public List<LoadType> getListLoadTypeMst() {
        return mapper.getListLoadTypeMst();
    }

	@Override
	public List<Device> checkLoadTypeDevice( Integer id) {
		return mapper.checkLoadTypeDevice(id);
	}

	@Override
	public List<LoadType> getLoadTypeByProjectAndSystemType(Map<String, Object> conditon) {
		return this.mapper.getLoadTypeByProjectAndSystemType(conditon);
	}

}
