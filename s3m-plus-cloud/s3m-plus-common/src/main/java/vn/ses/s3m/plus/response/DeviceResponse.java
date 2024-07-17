package vn.ses.s3m.plus.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ses.s3m.plus.dto.Device;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResponse {

    private Long deviceId;

    private String deviceCode;

    private String deviceName;

    private Integer deviceType;

    private String deviceTypeName;

    private Integer systemTypeId;

    private String systemTypeName;

    private Integer managerId;

    private Integer areaId;

    private String systemMapName;

    private String address;

    private Double latitude;

    private Double longitude;

    private String simNo;

    private String ip;

    private Integer uid;

    private Float voltage;

    private Integer amperage;

    private Integer amperageString;

    private Integer cableIdA1;

    private Integer cableIdA2;

    private Integer cableIdA3;

    private Integer cableIdA4;

    private Integer cableIdA5;

    private Integer cableIdA6;

    private Integer cableIdA7;

    private Integer cableIdA8;

    private Integer cableIdA9;

    private Integer pMax;

    private Integer power;

    private Integer acPower;

    private Integer dcPower;

    private Double imccb;

    private Integer projectId;

    private Integer systemMapId;

    private Integer customerId;

    private String snGW;

    private Float n;

    private String description;

    private Integer dbId;

    private String model;

    private String customerName;

    private String projectName;

    private Timestamp updateDate;

    private Integer series_cell;

    private Integer parallel_cell;

    private Integer series_modul;

    private Integer parallel_modul;

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

    private String timeSet;

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
    private String areaName;

    private String managerName;

    private String weatherInfor;

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

    public DeviceResponse(final Device device) {
        this.deviceId = device.getDeviceId();
        this.deviceName = device.getDeviceName();
        this.deviceCode = device.getDeviceCode();
        this.deviceType = device.getDeviceType();
        this.deviceTypeName = device.getDeviceTypeName();
        this.systemTypeId = device.getSystemTypeId();
        this.systemTypeName = device.getSystemTypeName();
        this.customerName = device.getCustomerName();
        this.managerId = device.getManagerId();
        this.managerName = device.getManagerName();
        this.areaId = device.getAreaId();
        this.areaName = device.getAreaName();
        this.systemMapName = device.getSystemMapName();
        this.address = device.getAddress();
        this.longitude = device.getLongitude();
        this.latitude = device.getLatitude();
        this.simNo = device.getSimNo();
        this.voltage = device.getVoltage();
        this.amperage = device.getAmperage();
        this.amperageString = device.getAmperageString();
        this.cableIdA1 = device.getCableIdA1();
        this.cableIdA2 = device.getCableIdA2();
        this.cableIdA3 = device.getCableIdA3();
        this.cableIdA4 = device.getCableIdA4();
        this.cableIdA5 = device.getCableIdA5();
        this.cableIdA6 = device.getCableIdA6();
        this.cableIdA7 = device.getCableIdA7();
        this.cableIdA8 = device.getCableIdA8();
        this.cableIdA9 = device.getCableIdA9();
        this.pMax = device.getPMax();
        this.power = device.getPower();
        this.acPower = device.getAcPower();
        this.dcPower = device.getDcPower();
        this.imccb = device.getImccb();
        this.projectId = device.getProjectId();
        this.systemMapId = device.getSystemMapId();
        this.customerId = device.getCustomerId();
        this.snGW = device.getSnGW();
        this.n = device.getN();
        this.model = device.getModel();
        this.customerName = device.getCustomerName();
        this.projectName = device.getProjectName();
        this.ip = device.getIp();
        this.uid = device.getUid();
        this.description = device.getDescription();
        this.updateDate = device.getUpdateDate();
        this.series_cell = device.getNs();
        this.parallel_cell = device.getNp();
        this.isc = device.getIsco();
        this.voc = device.getVoco();
        this.imp = device.getImpo();
        this.aisc = device.getAIsc();
        this.aimp = device.getAImp();
        this.c0 = device.getC0();
        this.c1 = device.getC1();
        this.bvoc = device.getBVoc();
        this.mbvoc = device.getMBVoc();
        this.bvmp = device.getBVmp();
        this.mbvmp = device.getMBVmp();
        this.c2 = device.getC2();
        this.c3 = device.getC3();
        this.dtc = device.getDTc();
        this.fd = device.getFd();
        this.a = device.getA();
        this.b = device.getB();
        this.c4 = device.getC4();
        this.c5 = device.getC5();
        this.ix = device.getIx();
        this.ixx = device.getIxx();
        this.c6 = device.getC6();
        this.c7 = device.getC7();
        this.e0 = device.getE0();
        this.t0 = device.getT0();
        this.k = device.getK();
        this.q = device.getQ();
        this.airmass = device.getAirmass();
        this.aoi = device.getAoi();
        this.p_diffuse = device.getP_diffuse();
        this.rpX = device.getRpX();
        this.rpY = device.getRpY();
        this.rul = device.getRul();
        this.x = device.getX();
        this.v = device.getV();
        this.hs = device.getHs();
        this.r = device.getR();
        this.k11 = device.getK11();
        this.k21 = device.getK21();
        this.k22 = device.getK22();
        this.deltaH = device.getDeltaH();
        this.deltaHR = device.getDeltaHR();
        this.deltaAOMR = device.getDeltaAOMR();
        this.deltaTOMR = device.getDeltaTOMR();
        this.deltaBR = device.getDeltaBR();
        this.tauO = device.getTauO();
        this.tauW = device.getTauW();
        this.loadType = device.getLoadType();
        this.l = device.getL();
        this.r0 = device.getR0();
        this.pfMax = device.getPfMax();
        this.areaName = device.getAreaName();
        this.managerName = device.getManagerName();
        this.weatherInfor = device.getWeatherInfor();
        this.timeSet = device.getTimeSet();
        this.inverterInfor = device.getInverterInfor();
        this.series_modul = device.getMs();
        this.parallel_modul = device.getMp();
        this.vmpo = device.getVmpo();
        this.port = device.getPort();
        this.adeg = device.getADeg();
        this.pmpo = device.getPmpo();
        this.apmp = device.getAPmp();
        this.tempNOCT = device.getTempNOCT();
        this.eff0 = device.getEff0();
        this.s = device.getS();
        this.objectTypeId = device.getObjectTypeId();
        this.objectName = device.getObjectName();
        this.area  = device.getArea();
    }
}
