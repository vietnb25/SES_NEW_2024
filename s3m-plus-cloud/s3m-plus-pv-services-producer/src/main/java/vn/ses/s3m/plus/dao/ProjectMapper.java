package vn.ses.s3m.plus.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Project;

@Mapper
public interface ProjectMapper {
    Project getProjectById(Map<String, Object> condition);

    Double getACPowerByProjectId(Map<String, Object> condition);
}
