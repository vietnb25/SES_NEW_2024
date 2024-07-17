package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.SystemMapMapper;
import vn.ses.s3m.plus.dto.SystemMap;

@Service
public class SystemMapServiceImp implements SystemMapService {
    @Autowired
    private SystemMapMapper systemMapMapper;

    @Override
    public List<SystemMap> getListSystemMapByProjectId(Integer projectId) {
        return systemMapMapper.getListSystemMapByProjectId(projectId);
    }

    @Override
    public List<SystemMap> getListSystemMapByProjectIdAndSystemTypeId(Map<String, String> condition) {
        return systemMapMapper.getListSystemMapByProjectIdAndSystemTypeId(condition);
    }

    @Override
    public void addSystemMap(SystemMap condition) {
        systemMapMapper.addSystemMap(condition);
    }

    @Override
    public void updateSystemMap(SystemMap condition) {
        systemMapMapper.updateSystemMap(condition);
    }

    @Override
    public void deleteSystemMap(String systemMapId) {
        systemMapMapper.deleteSystemMap(systemMapId);

    }

    @Override
    public List<SystemMap> getListSystemMapById(Map<String, String> condition) {
        return systemMapMapper.getListSystemMapById(condition);
    }

    @Override
    public SystemMap getMainSystem(Map<String, String> condition) {
        return systemMapMapper.getMainSystem(condition);
    }

    @Override
    public SystemMap getSystemMapById(Integer systemMapId) {
        return systemMapMapper.getSystemMapById(systemMapId);
    }

    @Override
    public List<SystemMap> getSystemMapByProject(Map<String, String> condition) {
        return systemMapMapper.getSystemMapByProject(condition);
    }

    @Override
    public List<SystemMap> getSystemMapByCustomer(Map<String, String> condition) {
        return systemMapMapper.getSystemMapByCustomer(condition);
    }

    @Override
    public List<SystemMap> getAllSystemMap() {
        return systemMapMapper.getAllSystemMap();
    }

    @Override
    public List<SystemMap> checkSystemMap(Map<String, String> condition) {
        return systemMapMapper.checkSystemMap(condition);
    }

    @Override
    public List<SystemMap> getSystemMapByCustomerAndProject(Map<String, String> condition) {
        return systemMapMapper.getSystemMapByCustomerAndProject(condition);
    }

    @Override
    public List<SystemMap> getSystemMapByProjectId() {
        return systemMapMapper.getSystemMapByProjectId();
    }

    @Override
    public int getCountModun(Map<String, Object> condition) {
        return systemMapMapper.getCountModun(condition);
    }

    @Override
    public int getCountDeviceModule(Map<String, String> condition) {
        return systemMapMapper.getCountDeviceModule(condition);
    }

}
