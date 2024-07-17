package vn.ses.s3m.plus.form;

import java.sql.Timestamp;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;
import vn.ses.s3m.plus.common.Constants;

@Data
public class DeviceMstForm {

    private Long id;

    private Long deviceId;

    private String deviceCode;

    @NotBlank (message = Constants.DeviceValidation.DEVICE_NAME_NOT_BLANK)
    @Size (max = Constants.DeviceValidation.DEVICE_NAME_MAX_SIZE,
        message = Constants.DeviceValidation.DEVICE_NAME_MAX_SIZE_ERROR)
    private String deviceName;

    private Integer deviceTypeId;

    private String deviceTypeName;

    private Integer customerId;

    private Integer systemMapId;

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

    @Min (value = Constants.DeviceValidation.LATITUDE_MIN_VALUE,
        message = Constants.DeviceValidation.LATITUDE_MIN_VALUE_ERROR)
    @Max (value = Constants.DeviceValidation.LATITUDE_MAX_VALUE,
        message = Constants.DeviceValidation.LATITUDE_MAX_VALUE_ERROR)
    private Double latitude;

    @Min (value = Constants.DeviceValidation.LONGITUDE_MIN_VALUE,
        message = Constants.DeviceValidation.LONGITUDE_MIN_VALUE_ERROR)
    @Max (value = Constants.DeviceValidation.LONGITUDE_MAX_VALUE,
        message = Constants.DeviceValidation.LATITUDE_MAX_VALUE_ERROR)
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

    @Size (max = Constants.DeviceValidation.SIM_NO_MAX_SIZE, message = Constants.DeviceValidation.SIM_NO_MAX_SIZE_ERROR)
    private String sim_no;

    private Double battery_capacity;

    private String work_date;

    private Integer reference_device_id;

    @Min (value = Constants.DeviceValidation.UID_MIN_VALUE, message = Constants.DeviceValidation.UID_MIN_VALUE_ERROR)
    @Max (value = Constants.DeviceValidation.UID_MAX_VALUE, message = Constants.DeviceValidation.UID_MAX_VALUE_ERROR)
    private Long uid;

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
