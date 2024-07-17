package vn.ses.s3m.plus.dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

@Data
public class Device {

    private int stt;

    private Long id;

    private Long deviceId;

    private String deviceCode;

    private String deviceName;

    private Integer deviceType;

    private String deviceTypeName;

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

    private Double latitude;

    private Double longitude;

    private String simNo;

    private Integer power;

    private String ip;

    private Integer uid;

    private Integer acPower;

    private Integer priorityFlag;

    private Integer dcPower;

    private Double imccb;

    private Integer state;

    private String model;

    private Float voltage;

    private Integer amperage;

    private Integer amperageString;

    private Float uab;

    private Float ubc;

    private Float uca;

    private Float ull;

    private Float uan;

    private Float ubn;

    private Float ucn;

    private Float uln;

    private Float ia;

    private Float ib;

    private Float ic;

    private Float in;

    private Float ig;

    private Float iavg;

    private Float pa;

    private Float pb;

    private Float pc;

    private Float pTotal;

    private Float qa;

    private Float qb;

    private Float qc;

    private Float qTotal;

    private Float sa;

    private Float sb;

    private Float sc;

    private Float sTotal;

    private Float pfa;

    private Float pfb;

    private Float pfc;

    private Float pfavg;

    private Float f;

    private Float ep;

    private Float epR;

    private Float epDr;

    private Float epDrr;

    private Float eq;

    private Float eqR;

    private Float eqDr;

    private Float eqDrr;

    private Float es;

    private Float esR;

    private Float esDr;

    private Float esDrr;

    private Float t1;

    private Float t2;

    private Float t3;

    private Float commanData1;

    private Float commanData2;

    private Float commanData3;

    private Float commanData4;

    private Float commanData5;

    private Float commanData6;

    private Float thdIa;

    private Float thdIb;

    private Float thdIc;

    private Float thdIn;

    private Float thdIg;

    private Float thdVab;

    private Float thdVbc;

    private Float thdVca;

    private Float thVll;

    private Float thdVan;

    private Float thdVbn;

    private Float thdVcn;

    private Float thdVln;

    private Integer cableIdA1;

    private Integer cableIdA2;

    private Integer cableIdA3;

    private Integer cableIdA4;

    private Integer cableIdA5;

    private Integer cableIdA6;

    private Integer cableIdA7;

    private Integer cableIdA8;

    private Integer cableIdA9;

    private Integer dbId;

    private String description;

    private Integer createId;

    private Timestamp createDate;

    private Integer updateId;

    private Timestamp updateDate;

    private String projectName;

    private String systemMapName;

    private String customerName;

    private Integer projectId;

    private Integer customerId;

    private Integer pMax;

    private String snGW;

    private Float n;

    private Integer Ns;

    private Integer Np;

    private Float Isco;

    private Float Voco;

    private Float Impo;

    private Float Vmpo;

    private Float aIsc;

    private Float aImp;

    private Float C0;

    private Float C1;

    private Float bVoc;

    private Float mBVoc;

    private Float bVmp;

    private Float mBVmp;

    private Float C2;

    private Float C3;

    private Float dTc;

    private Float fd;

    private Float a;

    private Float b;

    private Float C4;

    private Float C5;

    private Float Ix;

    private Float Ixx;

    private Float C6;

    private Float C7;

    private Float E0;

    private Float T0;

    // Hằng số Boltsmann
    private Double k;

    // Hằng số diện tích nguyên tố
    private Double q;

    private Float airmass;

    private Float aoi;

    private Float P_diffuse;

    private Integer status;

    private Timestamp sendDate;

    private Float ppvPhA;

    private Float ppvPhB;

    private Float ppvPhC;

    private Float aPhaA;

    private Float aPhaB;

    private Float aPhaC;

    private Float w;

    private Float t;

    private Float h;

    private Float indicator;

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

    private Integer Ms;

    private Integer Mp;

    private String timeSet;

    private String inverterInfor;

    private Integer port;

    private Float aDeg;

    private Float Pmpo;

    private Float aPmp;

    private Float TempNOCT;

    private Float Eff0;

    private Float S;

    private Float va;

    private Float vb;

    private Float vc;

    private Float iaI;

    private Float ibI;

    private Float icI;

    private Float VdcCombiner;

    private Float IdcCombiner;

    private Float PdcCombiner;

    private Float VdcStr;

    private Float IdcStr;

    private Float U;

    private Float I;

    private Float TEMP;

    private Float Rad;

    private Float PdcStr;

    private Float InDCPR;

    private Integer objectTypeId;

    private Integer count;

    private String operatingStatus;

    private Float epdc;

    private Float udc;

    private Float idc;

    private Float pdc;

    private Float tmpCab;

    private Float tmpSnk;

    private Float tmpTrns;

    private Float tmpOt;

    private Float sawId1;

    private Float sawId2;

    private Float sawId3;

    private Float sawId4;

    private Float sawId5;

    private Float sawId6;

    private Float alarmStatusBit;

    private Float ratio;

    private Float eppc;

    private Float pdLevel;

    private Integer notifier;

    private Float lfbRatio;

    private Float lfbEppc;

    private Float mfbRatio;

    private Float mfbEppc;

    private Float hfbRatio;

    private Float hfbEppc;

    private Float meanRatio;

    private Float meanEppc;

    private Float ratioEppcHi;

    private Float ratioEppcLo;

    private Long dayOnline;

    private String statusDevice;

    private Integer typeClass;

    private String objectName;

    private String objectTypeName;

    private String area;

    private String img;

    private List<Warning> listWarning;

    private Float iaH1;

    private Float iaH2;

    private Float iaH3;

    private Float iaH4;

    private Float iaH5;

    private Float iaH6;

    private Float iaH7;

    private Float iaH8;

    private Float iaH9;

    private Float iaH10;

    private Float iaH11;

    private Float iaH12;

    private Float iaH13;

    private Float iaH14;

    private Float iaH15;

    private Float iaH16;

    private Float iaH17;

    private Float iaH18;

    private Float iaH19;

    private Float iaH20;

    private Float iaH21;

    private Float iaH22;

    private Float iaH23;

    private Float iaH24;

    private Float iaH25;

    private Float iaH26;

    private Float iaH27;

    private Float iaH28;

    private Float iaH29;

    private Float iaH30;

    private Float iaH31;

    private Float ibH1;

    private Float ibH2;

    private Float ibH3;

    private Float ibH4;

    private Float ibH5;

    private Float ibH6;

    private Float ibH7;

    private Float ibH8;

    private Float ibH9;

    private Float ibH10;

    private Float ibH11;

    private Float ibH12;

    private Float ibH13;

    private Float ibH14;

    private Float ibH15;

    private Float ibH16;

    private Float ibH17;

    private Float ibH18;

    private Float ibH19;

    private Float ibH20;

    private Float ibH21;

    private Float ibH22;

    private Float ibH23;

    private Float ibH24;

    private Float ibH25;

    private Float ibH26;

    private Float ibH27;

    private Float ibH28;

    private Float ibH29;

    private Float ibH30;

    private Float ibH31;

    private Float icH1;

    private Float icH2;

    private Float icH3;

    private Float icH4;

    private Float icH5;

    private Float icH6;

    private Float icH7;

    private Float icH8;

    private Float icH9;

    private Float icH10;

    private Float icH11;

    private Float icH12;

    private Float icH13;

    private Float icH14;

    private Float icH15;

    private Float icH16;

    private Float icH17;

    private Float icH18;

    private Float icH19;

    private Float icH20;

    private Float icH21;

    private Float icH22;

    private Float icH23;

    private Float icH24;

    private Float icH25;

    private Float icH26;

    private Float icH27;

    private Float icH28;

    private Float icH29;

    private Float icH30;

    private Float icH31;

    private Float vAbH1;

    private Float vAbH2;

    private Float vAbH3;

    private Float vAbH4;

    private Float vAbH5;

    private Float vAbH6;

    private Float vAbH7;

    private Float vAbH8;

    private Float vAbH9;

    private Float vAbH10;

    private Float vAbH11;

    private Float vAbH12;

    private Float vAbH13;

    private Float vAbH14;

    private Float vAbH15;

    private Float vAbH16;

    private Float vAbH17;

    private Float vAbH18;

    private Float vAbH19;

    private Float vAbH20;

    private Float vAbH21;

    private Float vAbH22;

    private Float vAbH23;

    private Float vAbH24;

    private Float vAbH25;

    private Float vAbH26;

    private Float vAbH27;

    private Float vAbH28;

    private Float vAbH29;

    private Float vAbH30;

    private Float vAbH31;

    private Float vBcH1;

    private Float vBcH2;

    private Float vBcH3;

    private Float vBcH4;

    private Float vBcH5;

    private Float vBcH6;

    private Float vBcH7;

    private Float vBcH8;

    private Float vBcH9;

    private Float vBcH10;

    private Float vBcH11;

    private Float vBcH12;

    private Float vBcH13;

    private Float vBcH14;

    private Float vBcH15;

    private Float vBcH16;

    private Float vBcH17;

    private Float vBcH18;

    private Float vBcH19;

    private Float vBcH20;

    private Float vBcH21;

    private Float vBcH22;

    private Float vBcH23;

    private Float vBcH24;

    private Float vBcH25;

    private Float vBcH26;

    private Float vBcH27;

    private Float vBcH28;

    private Float vBcH29;

    private Float vBcH30;

    private Float vBcH31;

    private Float vCaH1;

    private Float vCaH2;

    private Float vCaH3;

    private Float vCaH4;

    private Float vCaH5;

    private Float vCaH6;

    private Float vCaH7;

    private Float vCaH8;

    private Float vCaH9;

    private Float vCaH10;

    private Float vCaH11;

    private Float vCaH12;

    private Float vCaH13;

    private Float vCaH14;

    private Float vCaH15;

    private Float vCaH16;

    private Float vCaH17;

    private Float vCaH18;

    private Float vCaH19;

    private Float vCaH20;

    private Float vCaH21;

    private Float vCaH22;

    private Float vCaH23;

    private Float vCaH24;

    private Float vCaH25;

    private Float vCaH26;

    private Float vCaH27;

    private Float vCaH28;

    private Float vCaH29;

    private Float vCaH30;

    private Float vCaH31;

    private Float vAnH1;

    private Float tSensor;

    private Float vAnH2;

    private Float vAnH3;

    private Float vAnH4;

    private Float vAnH5;

    private Float vAnH6;

    private Float vAnH7;

    private Float vAnH8;

    private Float vAnH9;

    private Float vAnH10;

    private Float vAnH11;

    private Float vAnH12;

    private Float vAnH13;

    private Float vAnH14;

    private Float vAnH15;

    private Float vAnH16;

    private Float vAnH17;

    private Float vAnH18;

    private Float vAnH19;

    private Float vAnH20;

    private Float vAnH21;

    private Float vAnH22;

    private Float vAnH23;

    private Float vAnH24;

    private Float vAnH25;

    private Float vAnH26;

    private Float vAnH27;

    private Float vAnH28;

    private Float vAnH29;

    private Float vAnH30;

    private Float vAnH31;

    private Float vBnH1;

    private Float vBnH2;

    private Float vBnH3;

    private Float vBnH4;

    private Float vBnH5;

    private Float vBnH6;

    private Float vBnH7;

    private Float vBnH8;

    private Float vBnH9;

    private Float vBnH10;

    private Float vBnH11;

    private Float vBnH12;

    private Float vBnH13;

    private Float vBnH14;

    private Float vBnH15;

    private Float vBnH16;

    private Float vBnH17;

    private Float vBnH18;

    private Float vBnH19;

    private Float vBnH20;

    private Float vBnH21;

    private Float vBnH22;

    private Float vBnH23;

    private Float vBnH24;

    private Float vBnH25;

    private Float vBnH26;

    private Float vBnH27;

    private Float vBnH28;

    private Float vBnH29;

    private Float vBnH30;

    private Float vBnH31;

    private Float vCnH1;

    private Float vCnH2;

    private Float vCnH3;

    private Float vCnH4;

    private Float vCnH5;

    private Float vCnH6;

    private Float vCnH7;

    private Float vCnH8;

    private Float vCnH9;

    private Float vCnH10;

    private Float vCnH11;

    private Float vCnH12;

    private Float vCnH13;

    private Float vCnH14;

    private Float vCnH15;

    private Float vCnH16;

    private Float vCnH17;

    private Float vCnH18;

    private Float vCnH19;

    private Float vCnH20;

    private Float vCnH21;

    private Float vCnH22;

    private Float vCnH23;

    private Float vCnH24;

    private Float vCnH25;

    private Float vCnH26;

    private Float vCnH27;

    private Float vCnH28;

    private Float vCnH29;

    private Float vCnH30;

    private Float vCnH31;

    private Float epDay;

    private Float epMonth;

    private String sentDateInstance;

    private String deviceCount;

    private String nrtu;

    private String sentDate;

    private String nmbtcp;

    private String nplc;

    private String nws;

    private String sim;

    private Float fs;

    private Float p;

    private String location;

    private String loadTypeName;
    
    private Integer fuelTypeId;
    
    private String fm;
    
    private Float tAccumulationDay;
    
    private Float tAccumulationMonth;

}
