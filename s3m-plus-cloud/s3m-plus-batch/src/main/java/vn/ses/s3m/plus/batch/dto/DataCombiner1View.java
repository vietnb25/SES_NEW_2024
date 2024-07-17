package vn.ses.s3m.plus.batch.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table
public class DataCombiner1View implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Long deviceId;

    private Integer viewType;

    private String viewTime;

    private Integer POWER;

    private Float PdcCombiner;

    private Float EpCombiner;

    private String sentDate;

}
