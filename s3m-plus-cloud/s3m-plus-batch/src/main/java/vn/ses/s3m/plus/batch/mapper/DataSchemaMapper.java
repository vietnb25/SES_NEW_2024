package vn.ses.s3m.plus.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface DataSchemaMapper {
    
    void insertNewTables(@Param("schema") String schema, @Param("year") int year);

}
