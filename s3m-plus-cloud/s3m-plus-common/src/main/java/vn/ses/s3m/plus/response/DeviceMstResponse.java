package vn.ses.s3m.plus.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ses.s3m.plus.dto.DeviceMst;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceMstResponse {

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

    private Integer projectId;
    
    private String projectName;

    private Integer objectId;

    private String address;

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

    private Timestamp updateDate;
    
    private Integer fuelTypeId;
    
    private Integer fuelFormId;
    
    private Double pdm;

    public DeviceMstResponse(final DeviceMst device) {

        this.deviceId = device.getDeviceId();
        this.deviceName = device.getDeviceName();
        this.deviceCode = device.getDeviceCode();
        this.deviceTypeId = device.getDeviceTypeId();
        this.deviceTypeName = device.getDeviceTypeName();
        this.customerId = device.getCustomerId();
        this.customerName = device.getCustomerName();
        this.systemTypeId = device.getSystemTypeId();
        this.systemMapId = device.getSystemMapId();
        this.systemMapName = device.getSystemMapName();
        this.systemTypeName = device.getSystemTypeName();
        this.managerId = device.getManagerId();
        this.managerName = device.getManagerName();
        this.areaId = device.getAreaId();
        this.areaName = device.getAreaName();
        this.objectId = device.getObjectId();
        this.projectId = device.getProjectId();
        this.projectName = device.getProjectName();
        this.address = device.getAddress();
        this.longitude = device.getLongitude();
        this.latitude = device.getLatitude();
        this.priority_flag = device.getPriority_flag();
        this.location = device.getLocation();
        this.load_type_id = device.getLoad_type_id();
        this.manufacturer = device.getManufacturer();
        this.model = device.getModel();
        this.pn = device.getPn();
        this.in = device.getIn();
        this.vsc = device.getVsc();
        this.f = device.getF();
        this.delta_p0 = device.getDelta_p0();
        this.delta_pk = device.getDelta_pk();
        this.i0 = device.getI0();
        this.un = device.getUn();
        this.m_oil = device.getM_oil();
        this.m_all = device.getM_all();
        this.exp_oil = device.getExp_oil();
        this.exp_wind = device.getExp_wind();
        this.const_k11 = device.getConst_k11();
        this.const_k21 = device.getConst_k21();
        this.const_k22 = device.getConst_k22();
        this.hot_spot_temp = device.getHot_spot_temp();
        this.hot_spot_gradient = device.getHot_spot_gradient();
        this.avg_oil_temp_rise = device.getAvg_oil_temp_rise();
        this.top_oil_temp_rise = device.getTop_oil_temp_rise();
        this.bottom_oil_temp_rise = device.getBottom_oil_temp_rise();
        this.const_time_oil = device.getConst_time_oil();
        this.const_time_winding = device.getConst_time_winding();
        this.vn = device.getVn();
        this.cable_length = device.getCable_length();
        this.rho = device.getRho();
        this.inc = device.getInc();
        this.pdc_max = device.getPdc_max();
        this.vdc_max = device.getVdc_max();
        this.vdc_rate = device.getVdc_rate();
        this.vac_rate = device.getVac_rate();
        this.idc_max = device.getIdc_max();
        this.iac_rate = device.getIac_rate();
        this.iac_max = device.getIac_max();
        this.pac = device.getPac();
        this.eff = device.getEff();
        this.p_max = device.getP_max();
        this.vmp = device.getVmp();
        this.imp = device.getImp();
        this.voc = device.getVoc();
        this.isc = device.getIsc();
        this.gstc = device.getGstc();
        this.tstc = device.getTstc();
        this.gnoct = device.getGnoct();
        this.tnoct = device.getTnoct();
        this.cp_max = device.getCp_max();
        this.cvoc = device.getCvoc();
        this.cisc = device.getCisc();
        this.ns = device.getNs();
        this.sensor_radiation_id = device.getSensor_radiation_id();
        this.sensor_temperature_id = device.getSensor_temperature_id();
        this.sim_no = device.getSim_no();
        this.battery_capacity = device.getBattery_capacity();
        this.work_date = device.getWork_date();
        this.reference_device_id = device.getReference_device_id();
        this.uid = device.getUid();
        this.db_id = device.getDb_id();
        this.delete_flag = device.getDelete_flag();
        this.description = device.getDescription();
        this.updateDate = device.getUpdateDate();
        this.fuelTypeId = device.getFuelTypeId();
        this.fuelFormId = device.getFuelFormId();
        this.pdm = device.getPdm();
    }
}
