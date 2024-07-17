package vn.ses.s3m.plus.batch.mapper;

import org.apache.ibatis.annotations.*;
import vn.ses.s3m.plus.batch.dto.DataAccumulations;
import vn.ses.s3m.plus.batch.dto.DataInstant;
import vn.ses.s3m.plus.batch.dto.Device;

import java.util.List;

@Mapper
public interface DataAccumulationsMapper {
    @Select("SHOW TABLES FROM ${schema} LIKE #{table}")
    String checkTableExistence(@Param("schema") String schema, @Param("table") String table);


    @Select("CREATE TABLE ${schema}.s3m_data_accumulations (\n" +
            "           `id` BIGINT NOT NULL AUTO_INCREMENT,\n" +
            "            `ep` DOUBLE NOT NULL,\n" +
            "              `device_id` INT NOT NULL,\n" +
            "             `type_time` INT NOT NULL,\n" +
            "            `view_time` VARCHAR(100) NOT NULL,\n" +
            "             sent_date TIMESTAMP NOT NULL,\n" +
            "            PRIMARY KEY (`id`)\n" +
            "        );")
    void createTable(@Param("schema")String schema);

    @Select("select * from s3m_device where customer_id = #{customer} and delete_flag = 0")
    List<Device> getDeviceByCustomer(@Param("customer") String customer);

    @Select("SELECT A.id,A.device_id,A.ia,A.ib,A.ic,A.sent_date FROM ${schema}.s3m_instant_data A where A.device_id = ${device}")
    DataInstant getDataInstansData(@Param("device") String device,@Param("schema") String schema);

    @Insert("INSERT INTO ${schema}.s3m_data_accumulations (`ep`,`device_id` ,`type_time`, `view_time`,`sent_date`) VALUES (#{data.ep}, #{data.deviceId},#{data.typeTime}, #{data.viewTime},current_timestamp());\n")
    void insertDataAccumulations(@Param("data")DataAccumulations data,@Param("schema") String schema);
    @Update("UPDATE ${schema}.s3m_data_accumulations set " +
            "ep = #{data.ep} WHERE id = #{data.id}")
    void updateDataAccumulations(@Param("data")DataAccumulations data,@Param("schema") String schema);
    @Select("select * from `${schema}`.`s3m_data_accumulations` where device_id = #{deviceId} and type_time = 2 and view_time like #{viewTime} order by sent_date desc limit 1;")
    DataAccumulations getDataAccumulationsHour(@Param("schema") String schema, @Param("deviceId") Integer deviceId,@Param("viewTime") String viewTime);
    @Select("select * from `${schema}`.`s3m_data_accumulations` where device_id = #{deviceId} and type_time = 3 and view_time like #{viewTime} order by sent_date desc limit 1;")
    DataAccumulations getDataAccumulationsDay(@Param("schema") String schema, @Param("deviceId") Integer deviceId,@Param("viewTime") String viewTime);
    @Select("select * from `${schema}`.`s3m_data_accumulations` where device_id = #{deviceId} and type_time = 4 and view_time like #{viewTime} order by sent_date desc limit 1;")
    DataAccumulations getDataAccumulationsMoth(@Param("schema") String schema, @Param("deviceId") Integer deviceId,@Param("viewTime") String viewTime);
    @Select("select * from `${schema}`.`s3m_data_accumulations` where device_id = #{deviceId} and type_time = 5 and view_time like #{viewTime} order by sent_date desc limit 1;")
    DataAccumulations getDataAccumulationsYear(@Param("schema") String schema, @Param("deviceId") Integer deviceId,@Param("viewTime") String viewTime);


}
