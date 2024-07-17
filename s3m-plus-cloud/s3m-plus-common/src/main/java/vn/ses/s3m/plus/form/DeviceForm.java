package vn.ses.s3m.plus.form;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import vn.ses.s3m.plus.common.Constants;

@Data
public class DeviceForm {

    private Long deviceId;

    private String deviceCode;

    @NotBlank (message = Constants.DeviceValidation.DEVICE_NAME_NOT_BLANK)
    @Size (max = Constants.DeviceValidation.DEVICE_NAME_MAX_SIZE,
        message = Constants.DeviceValidation.DEVICE_NAME_MAX_SIZE_ERROR)
    private String deviceName;

    private Integer deviceType;

    private Integer systemTypeId;

    private Integer systemMapId;

    private Integer state;

    @NotNull (message = Constants.DeviceValidation.UID_NOT_BLANK)
    @Min (value = Constants.DeviceValidation.UID_MIN_VALUE, message = Constants.DeviceValidation.UID_MIN_VALUE_ERROR)
    @Max (value = Constants.DeviceValidation.UID_MAX_VALUE, message = Constants.DeviceValidation.UID_MAX_VALUE_ERROR)
    private Long uid;

    private Integer pMax;

    private Integer power;

    private String address;

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

    @NotBlank (message = Constants.DeviceValidation.SIM_NO_NOT_BLANK)
    @Size (max = Constants.DeviceValidation.SIM_NO_MAX_SIZE, message = Constants.DeviceValidation.SIM_NO_MAX_SIZE_ERROR)
    private String simNo;

    private Float voltage;

    private Integer amperage;

    private Integer amperageString;

    private Integer acPower;

    private Integer dcPower;

    private Double imccb;

    private Integer projectId;

    private Integer customerId;

    private Integer cableId;

    private String snGW;

    private String description;

    private Integer dbId;

    @Size (max = Constants.DeviceValidation.MODEL_MAX_SIZE, message = Constants.DeviceValidation.MODEL_MAX_SIZE_ERROR)
    private String model;

    private Float n;

    private Float isc;

    private Float voc;

    private Float imp;

    private Float aisc;

    private Float aimp;

    private Float c0;

    private Float c1;

    private Float bvoc;

    private Float mbvoc;

    private Float bvmp;

    private Float mbvmp;

    private Float c2;

    private Float c3;

    private Float dtc;

    private Float fd;

    private Float a;

    private Float b;

    private Float c4;

    private Float c5;

    private Float ix;

    private Float ixx;

    private Float c6;

    private Float c7;

    private Float e0;

    private Float t0;

    private Float vmpo;

    // Hằng số Boltsmann
    private Double k;

    // Hằng số diện tích nguyên tố
    private Double q;

    private Float airmass;

    private Float aoi;

    private Float p_diffuse;

    private Integer rpX;

    private Integer rpY;

    private Float rul;

    private Float x;

    private Float v;

    private Float hs;

    private Float r;

    private Float k11;

    private Float k21;

    private Float k22;

    private Float deltaH;

    private Float deltaHR;

    private Float deltaAOMR;

    private Float deltaTOMR;

    private Float deltaBR;

    private Float tauO;

    private Float tauW;

    private Integer loadType;

    private Float l;

    private Float r0;

    private Float pfMax;

    private String weatherInfor;

    private String timeSet;

    private String inverterInfor;

    private Integer port;

    private Float adeg;

    private Float pmpo;

    private Float apmp;

    private Float tempNOCT;

    private Float eff0;

    private Float s;

    private Integer objectTypeId;

    private String objectName;

    private String area;
    
    private Integer fuelTypeId;
}
