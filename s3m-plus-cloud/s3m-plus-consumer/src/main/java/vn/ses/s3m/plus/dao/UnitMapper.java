package vn.ses.s3m.plus.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Unit;

@Mapper
public interface UnitMapper {
    List<Unit> getListUnit();
}
