package vn.ses.s3m.plus.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.batch.dto.DataRmuDarwer1ViewCache;

@Mapper
public interface DataRmuDrawer1ViewCacheMapper {

    List<DataRmuDarwer1ViewCache> selectAll(@Param ("schema") String schema);

    void update(@Param ("schema") String schema, @Param ("data") DataRmuDarwer1ViewCache data);

    void saveAll(@Param ("schema") String schema, List<DataRmuDarwer1ViewCache> datas);

}
