package vn.ses.s3m.plus.batch.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table (name = "s3m_data_load_frame_1_2022")
public class DataLoadFrame1 {

    @Id
    @Column (name = "id")
    private Integer id;

    @Column (name = "device_id")
    private Long deviceId;

    @Column
    private Float uab;

    @Column
    private Float ubc;

    @Column
    private Float uca;

    @Column
    private Float ull;

    @Column
    private Float uan;

    @Column
    private Float ubn;

    @Column
    private Float ucn;

    @Column
    private Float uln;

    @Column
    private Float ia;

    @Column
    private Float ib;

    @Column
    private Float ic;

    @Column
    private Float in;

    @Column
    private Float ig;

    @Column
    private Float iavg;

    @Column
    private Float pa;

    @Column
    private Float pb;

    @Column
    private Float pc;

    @Column (name = "p_total")
    private Float pTotal;

    @Column
    private Float qa;

    @Column
    private Float qb;

    @Column
    private Float qc;

    @Column (name = "q_total")
    private Float qTotal;

    @Column
    private Float sa;

    @Column
    private Float sb;

    @Column
    private Float sc;

    @Column (name = "s_total")
    private Float sTotal;

    @Column
    private Float pfa;

    @Column
    private Float pfb;

    @Column
    private Float pfc;

    @Column
    private Float pfavg;

    @Column
    private Float f;

    @Column
    private int ep;

    @Column (name = "ep_r")
    private Integer epR;

    @Column (name = "ep_dr")
    private Integer epDr;

    @Column (name = "ep_drr")
    private Integer epDrr;

    @Column
    private Integer eq;

    @Column (name = "eq_r")
    private Integer eqR;

    @Column (name = "eq_dr")
    private Integer eqDr;

    @Column (name = "eq_drr")
    private Integer eqDrr;

    @Column
    private Integer es;

    @Column (name = "es_r")
    private Integer esR;

    @Column (name = "es_dr")
    private Integer esDr;

    @Column (name = "es_drr")
    private Integer esDrr;

    @Column
    private Float t1;

    @Column
    private Float t2;

    @Column
    private Float t3;

    @Column (name = "command_data1")
    private Float commandData1;

    @Column (name = "command_data2")
    private Float commandData2;

    @Column (name = "command_data3")
    private Float commandData3;

    @Column (name = "command_data4")
    private Float commandData4;

    @Column (name = "command_data5")
    private Float commandData5;

    @Column (name = "command_data6")
    private Float commandData6;

    @Column (name = "thd_ia")
    private Float thdIa;

    @Column (name = "thd_ib")
    private Float thdIb;

    @Column (name = "thd_ic")
    private Float thdIc;

    @Column (name = "thd_in")
    private Float thdIn;

    @Column (name = "thd_ig")
    private Float thdIg;

    @Column (name = "thd_vab")
    private Float thdVab;

    @Column (name = "thd_vbc")
    private Float thdVbc;

    @Column (name = "thd_vca")
    private Float thdVca;

    @Column (name = "thd_vll")
    private Float thdVll;

    @Column (name = "thd_van")
    private Float thdVan;

    @Column (name = "thd_vbn")
    private Float thdVbn;

    @Column (name = "thd_vcn")
    private Float thdVcn;

    @Column (name = "thd_vln")
    private Float thdVln;

    @Column (name = "sent_date")
    private String sentDate;

    @Column (name = "transaction_date")
    private Long transactionDate;
}
