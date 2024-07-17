package vn.ses.s3m.plus.batch.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import vn.ses.s3m.plus.batch.dto.DataRmuDrawer1View;

@Mapper
public interface DataRmuDrawer1ViewMapper {

    List<DataRmuDrawer1View> selectByTypeLastTime(@Param ("schema") String schema);

    DataRmuDrawer1View selectLastestTime(@Param ("schema") String schema);

    void updateLastTime(@Param ("schema") String schema, @Param ("viewTime") String viewTime,
        @Param ("sentDate") String sentDate);

    void update(@Param ("schema") String schema, DataRmuDrawer1View data);

    void saveAll(@Param ("schema") String schema, List<DataRmuDrawer1View> datas);

    List<String> getCustomerList(@Param ("schema") String schema, @Param ("table") String table);
}
