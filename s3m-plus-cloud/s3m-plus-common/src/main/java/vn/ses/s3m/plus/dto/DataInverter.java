package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class DataInverter {

    private Integer id;

    private Long device_id;

    private String device_name;

    private Integer device_type;

    private String device_code;

    private Float I;

    private Float Ia;

    private Float Ib;

    private Float Ic;

    private Float Vab;

    private Float Vbc;

    private Float Vca;

    private Float Va;

    private Float Vb;

    private Float Vc;

    private Float F;

    private Float PF;

    private Float Idc;

    private Float Udc;

    private Float Pdc;

    private Float TmpCab;

    private Float TmpSnk;

    private Float TmpTrns;

    private Float TmpOt;

    private Float St;

    private Float StVnd;

    private Float Evt1;

    private Float Evt2;

    private Float EvtVnd1;

    private Float EvtVnd2;

    private Float EvtVnd3;

    private Float EvtVnd4;

    private Float TmSrc;

    private Float Tms;

    private Float CRC16;

    private Float PVConn;

    private Float ECPConn;

    private Float Conn;

    private Float Wmax;

    private Float Vref;

    private Float VRefofs;

    private Float VAMax;

    private Float VAMaxQ1;

    private Float VAMaxQ2;

    private Float VAMaxQ3;

    private Float VAMaxQ4;

    private Float F_normal;

    private Float WMaxLimPct;

    private Float WMaxLimPct_WinTms;

    private Float WMaxLimPct_RvrtTms;

    private Float WMaxLimPct_RmpTms;

    private Float WMaxLim_Ena;

    private Float OutPFSet;

    private Float OutPFSet_WinTms;

    private Float OutPFSet_RvrtTms;

    private Float OutPFSet_RmpTms;

    private Float OutPFSet_Ena;

    private Float VArWMaxPct;

    private Float VArMaxPct;

    private Float VArAvalPct;

    private Float VArPct_WinTms;

    private Float VArPct_RvrtTms;

    private Float VArPct_RmpTms;

    private Float VArPct_Mod;

    private Float VArPct_Ena;

    private Float Ptotal;

    private Float Pa;

    private Float Pb;

    private Float Pc;

    private Float Stotal;

    private Float Sa;

    private Float Sb;

    private Float Sc;

    private Float Qtotal;

    private Float Qa;

    private Float Qb;

    private Float Qc;

    private Float PFa;

    private Float PFb;

    private Float PFc;

    private Float Ep;

    private Float EpDC;

    private String sent_date;

    private Long transaction_date;

    private String viewTime;

}
