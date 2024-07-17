package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.History;
import vn.ses.s3m.plus.dto.SystemMap;

@Mapper
public interface ControlMapper {
    List<SystemMap> getSystemMapPVByProject(Map<String, String> condition);

    List<History> getHistories(Map<String, String> codition);

    History getHistoryLastestById(Map<String, Object> condition);
}
