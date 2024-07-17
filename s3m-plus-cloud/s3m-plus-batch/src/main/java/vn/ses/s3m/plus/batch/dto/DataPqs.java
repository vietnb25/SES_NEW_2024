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
public class DataPqs implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Long deviceId;

    private Integer systemTypeId;

    private Integer deviceType;

    private Integer viewType;

    private String viewTime;

    private Float pTotal;

    private Float ep;

    private Float epCache;

    private Float epAtATime;

    private Float lowEp = 0f;

    private Float normalEp = 0f;

    private Float highEp = 0f;

    private Float lowCostIn = 0f;

    private Float normalCostIn = 0f;

    private Float highCostIn = 0f;

    private Float lowCostOut = 0f;

    private Float normalCostOut = 0f;

    private Float highCostOut = 0f;

    private Double t;

    private Double tCache;

    private Double tAtATime;

    private String sentDate;

    private Integer amountOfPeople;

    private Double emissionFactorCo2Electric;

    private Double emissionFactorCo2Gasoline;

    private Double emissionFactorCo2Charcoal;

    private Double areaOfFloor;

}
