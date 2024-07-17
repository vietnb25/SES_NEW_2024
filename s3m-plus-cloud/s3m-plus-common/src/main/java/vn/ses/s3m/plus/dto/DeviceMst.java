package vn.ses.s3m.plus.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class DeviceMst {

    private Long id;

    private Long deviceId;

    private String deviceCode;

    private String deviceName;

    private Integer deviceTypeId;

    private String deviceTypeName;

    private Integer customerId;

    private String customerName;
    
    private Integer systemMapId;

    private String systemMapName;
    
    private Integer superManagerId;

    private String superManagerName;

    private Integer systemTypeId;

    private String systemTypeName;

    private Integer managerId;

    private String managerName;

    private Integer areaId;

    private String areaName;

    private String address;

    private Integer objectId;

    private Integer projectId;

    private String projectName;
    
    private Double latitude;

    private Double longitude;

    private Integer priority_flag;

    private String location;

    private Integer load_type_id;

    private String manufacturer;

    private String model;

    private Double pn;

    private Double in;

    private Double vsc;

    private Double vpr;

    private Double f;

    private Double delta_p0;

    private Double delta_pk;

    private Double i0;

    private Double un;

    private Double m_oil;

    private Double m_all;

    private Double exp_oil;

    private Double exp_wind;

    private Double hot_spot_factor;

    private Double loss_ratio;

    private Double const_k11;

    private Double const_k21;

    private Double const_k22;

    private Double hot_spot_temp;

    private Double hot_spot_gradient;

    private Double avg_oil_temp_rise;

    private Double top_oil_temp_rise;

    private Double bottom_oil_temp_rise;

    private Double const_time_oil;

    private Double const_time_winding;

    private Double vn;

    private Double cable_length;

    private Double rho;

    private Double inc;

    private Double pdc_max;

    private Double vdc_max;

    private Double vdc_rate;

    private Double vac_rate;

    private Double idc_max;

    private Double iac_rate;

    private Double iac_max;

    private Double pac;

    private Double eff;

    private Double p_max;

    private Double vmp;

    private Double imp;

    private Double voc;

    private Double isc;

    private Double gstc;

    private Double tstc;

    private Double gnoct;

    private Double tnoct;

    private Double cp_max;

    private Double cvoc;

    private Double cisc;

    private Double ns;

    private Integer sensor_radiation_id;

    private Integer sensor_temperature_id;

    private String sim_no;

    private Double battery_capacity;

    private String work_date;

    private Integer reference_device_id;

    private Integer uid;

    private Integer db_id;

    private Integer delete_flag;

    private String description;

    private Integer createId;

    private Timestamp createDate;

    private Integer updateId;

    private Timestamp updateDate;
    
    private Integer fuelTypeId;
    
    private Integer fuelFormId;
    
    private Double pdm;
}
