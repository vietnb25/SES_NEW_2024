package vn.ses.s3m.plus.dto;

import java.util.List;

import lombok.Data;

@Data
public class DataPowerResult {

    private String name;

    private List<DataPower> listDataPower;

    private List<LandmarksPlansEnergy> listDataPowerTab2;

    private Float dataPower;

    private List<DataPowerResult> listDataModule;

    private Integer countDevice;

    private Integer countDeviceWarning;

    private Integer countDeviceOffline;

    private Integer countDeviceOnline;
    
    private List<Chart> listDataCost;
    
    private Object dataPowerClass2;
}
