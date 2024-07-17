package vn.ses.s3m.plus.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.batch.dto.DataRmuDrawer1;

@Mapper
public interface DataRmuDrawer1Mapper {

    List<DataRmuDrawer1> selectNewRecord(@Param ("schema") String schema, @Param ("table") String table,
        @Param ("id") String id);
}
