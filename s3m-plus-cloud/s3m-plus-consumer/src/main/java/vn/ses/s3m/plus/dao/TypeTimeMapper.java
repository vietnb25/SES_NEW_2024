package vn.ses.s3m.plus.dao;

import org.apache.ibatis.annotations.Mapper;
import vn.ses.s3m.plus.dto.TypeTime;

import java.util.List;

@Mapper
public interface TypeTimeMapper {
    List<TypeTime> getTypeTime();
}
