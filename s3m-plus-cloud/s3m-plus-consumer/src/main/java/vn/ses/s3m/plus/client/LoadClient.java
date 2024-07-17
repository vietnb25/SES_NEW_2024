package vn.ses.s3m.plus.client;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import vn.ses.s3m.plus.dto.Control;
import vn.ses.s3m.plus.dto.DataEnergyPV;
import vn.ses.s3m.plus.dto.DataString1;
import vn.ses.s3m.plus.dto.Forecast;
import vn.ses.s3m.plus.dto.History;
import vn.ses.s3m.plus.dto.OverviewGridPower;
import vn.ses.s3m.plus.dto.OverviewGridTotalPower;
import vn.ses.s3m.plus.dto.OverviewLoadTotalPower;
import vn.ses.s3m.plus.dto.OverviewPVPower;
import vn.ses.s3m.plus.dto.OverviewPVTotalPower;
import vn.ses.s3m.plus.dto.Receiver;
import vn.ses.s3m.plus.dto.SystemMap;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.Warning;
import vn.ses.s3m.plus.form.HarmonicForm;
import vn.ses.s3m.plus.form.UpdateWarningForm;
import vn.ses.s3m.plus.response.DataHarmonicPeriod;
import vn.ses.s3m.plus.response.DeviceResponse;
import vn.ses.s3m.plus.response.OperationInformationResponse;
import vn.ses.s3m.plus.response.PowerQualityResponse;

@FeignClient ("load-services-producer")
public interface LoadClient {

    @GetMapping ("/load/power/{customerId}/{projectId}")
    ResponseEntity<Map<String, Object>> getPowerInDay(@PathVariable String customerId, @PathVariable String projectId,
        @RequestParam (required = false) String keyword);

    @GetMapping ("/load/powerTotal/{customerId}/{projectId}")
    ResponseEntity<List<OverviewLoadTotalPower>> getTotalPowerEnergy(@PathVariable String customerId,
        @PathVariable String projectId, @RequestParam String fromDate, @RequestParam String toDate);

    @GetMapping ("/load/exportToExcel/{day}/{projectId}")
    ResponseEntity<Resource> exportTotalPowerEnergyToExcel(@PathVariable String day, @PathVariable String projectId);

    @GetMapping ("/load/warning/")
    ResponseEntity<?> getWarnings(@RequestParam String fromDate, @RequestParam String toDate,
        @RequestParam String projectId, @RequestParam String customerId);

    @GetMapping ("/load/warning/type/{warningType}")
    ResponseEntity<?> detailWarningByType(@PathVariable String warningType, @RequestParam String fromDate,
        @RequestParam String toDate, @RequestParam String projectId, @RequestParam String customerId,
        @RequestParam Integer page);

    @GetMapping ("/load/warning/detail")
    ResponseEntity<?> showDataWarningByDevice(@RequestParam String warningType, @RequestParam String fromDate,
        @RequestParam String toDate, @RequestParam String deviceId, @RequestParam String customerId,
        @RequestParam Integer page);

    @GetMapping ("/load/warning/download")
    ResponseEntity<Resource> downloadWarningOperation(@RequestParam ("warningType") String warningType,
        @RequestParam ("fromDate") String fromDate, @RequestParam ("toDate") String toDate,
        @RequestParam ("deviceId") String deviceId, @RequestParam ("customerId") String customerId,
        @RequestParam ("userName") String userName);

    @GetMapping ("/load/warning/update/{warningId}")
    ResponseEntity<?> getWarningCache(@PathVariable ("warningId") Integer warningId,
        @RequestParam ("customerId") String customerId);

    @PostMapping ("/load/warning/update/{warningId}/{customerId}")
    ResponseEntity<?> updateWarningCache(@RequestBody UpdateWarningForm form,
        @PathVariable ("warningId") Integer warningId, @PathVariable ("customerId") String customerId);

    @SuppressWarnings ("rawtypes")
    @GetMapping ("/load/report/{userName}/{projectId}")
    ResponseEntity<Map<String, List>> loadReport(@PathVariable String userName, @PathVariable String projectId);

    // CHECKSTYLE:OFF
    @PostMapping ("/load/report/generateReports/{customerId}/{deviceId}/{reportType}/{date}/{dateType}"
        + "/{userName}/{projectId}")
    ResponseEntity<?> generateReports(@PathVariable String customerId, @PathVariable String deviceId,
        @PathVariable String reportType, @PathVariable String date, @PathVariable String dateType,
        @PathVariable String userName, @PathVariable String projectId, @RequestBody User user);

    // CHECKSTYLE:ON
    @GetMapping ("/operation/instant/{customerId}/{deviceId}")
    ResponseEntity<OperationInformationResponse> getInstantOperationInformation(@PathVariable Integer customerId,
        @PathVariable Long deviceId);

    @GetMapping ("/operation/{customerId}/{deviceId}/{page}")
    ResponseEntity<?> getOperationInformation(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate, @PathVariable String page);

    @GetMapping ("/load/report/download")
    ResponseEntity<Resource> downloadReport(@RequestParam String path);

    @DeleteMapping ("/load/report/delete/{id}")
    ResponseEntity<Void> deleteReport(@PathVariable Integer id);

    // CHECKSTYLE:OFF
    @GetMapping ("/operation/operating-warning/{customerId}/{deviceId}")
    ResponseEntity<List<Warning>> getWarningOperation(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam (required = false) String warningType, @RequestParam (required = false) String fromDate,
        @RequestParam (required = false) String toDate);
    // CHECKSTYLE:ON

    @GetMapping ("/operation/operating-warning/detail/{customerId}")
    ResponseEntity<?> showDataWarning(@PathVariable Integer customerId, @RequestParam String warningType,
        @RequestParam String fromDate, @RequestParam String toDate, @RequestParam String deviceId,
        @RequestParam Integer page);

    @GetMapping ("/operation/operating-warning/update/{customerId}/{warningId}")
    ResponseEntity<?> getOperatingWarningCache(@PathVariable Integer customerId,
        @PathVariable ("warningId") Integer warningId);

    @PostMapping ("/operation/operating-warning/update/{customerId}/{warningId}")
    ResponseEntity<?> updateOperatingWarningCache(@RequestBody UpdateWarningForm form, @PathVariable Integer customerId,
        @PathVariable ("warningId") Integer warningId);

    @GetMapping ("/operation/operating-warning/download/{customerId}")
    ResponseEntity<Resource> downloadOperatingWarning(@PathVariable Integer customerId,
        @RequestParam ("warningType") String warningType, @RequestParam ("fromDate") String fromDate,
        @RequestParam ("toDate") String toDate, @RequestParam ("deviceId") String deviceId,
        @RequestParam ("userName") String userName);

    @GetMapping ("/operation/operating-warning/downloadWarning/{customerId}")
    ResponseEntity<Resource> downloadWarningDevice(@PathVariable Integer customerId,
        @RequestParam ("warningType") String warningType, @RequestParam ("fromDate") String fromDate,
        @RequestParam ("toDate") String toDate, @RequestParam ("deviceId") String deviceId,
        @RequestParam String userName);

    @GetMapping ("/operation/chart/{customerId}/{deviceId}")
    ResponseEntity<?> getDataChart(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam (required = false) String fromDate, @RequestParam (required = false) String toDate,
        @RequestParam Integer pqsViewType, @RequestParam Integer chartType);

    @PostMapping ("/operation/chart-harmonic/{customerId}/{deviceId}")
    ResponseEntity<Map<String, Object>> getDataPowerResponse(@PathVariable Integer customerId,
        @PathVariable String deviceId, @RequestBody HarmonicForm harmonicForm);

    @PostMapping ("/operation/chart-harmonic/day/{customerId}/{deviceId}")
    ResponseEntity<?> getDataHarmonicByDay(@PathVariable Integer customerId, @PathVariable String deviceId,
        @RequestBody HarmonicForm harmonicForm, @RequestParam String day);

    @GetMapping ("/operation/chart-harmonic/period/{customerId}/{deviceId}")
    ResponseEntity<List<DataHarmonicPeriod>> getDataHarmonicPeriod(@PathVariable Integer customerId,
        @PathVariable String deviceId, @RequestParam String fromDate, @RequestParam String toDate);

    @GetMapping ("/operation/power-quality/instant/{customerId}/{deviceId}")
    ResponseEntity<PowerQualityResponse> getInstantPowerQuality(@PathVariable Integer customerId,
        @PathVariable Long deviceId);

    @GetMapping ("/operation/power-quality/{customerId}/{deviceId}")
    ResponseEntity<?> getPowerQualities(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam (required = false) String fromDate, @RequestParam (required = false) String toDate,
        @RequestParam String page);

    @GetMapping ("/operation/download/electrical-param/{customerId}/{deviceId}")
    ResponseEntity<Resource> downloadElectricalParam(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate, @RequestParam String userName);

    @GetMapping ("/operation/download/temperature/{customerId}/{deviceId}")
    ResponseEntity<Resource> downloadTemperature(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate, @RequestParam String userName);

    @GetMapping ("/operation/download/power-quality/{customerId}/{deviceId}")
    ResponseEntity<Resource> downloadPowerQuality(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate, @RequestParam String userName);

    @GetMapping ("/load/report/addReport/{customerId}/{deviceId}/{reportType}/{date}/{userName}/{projectId}/{dateType}")
    ResponseEntity<Void> addReport(@PathVariable String customerId, @PathVariable String deviceId,
        @PathVariable String reportType, @PathVariable String date, @PathVariable String userName,
        @PathVariable String projectId, @PathVariable String dateType);

    @GetMapping ("/operation/chart/download/{customerId}/{deviceId}")
    ResponseEntity<Resource> downloadDataChart(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam (required = false) String fromDate, @RequestParam (required = false) String toDate,
        @RequestParam Integer pqsViewType, @RequestParam Integer chartType, @RequestParam String userName);

    @GetMapping ("/operation/devices/{projectId}/{systemTypeId}")
    ResponseEntity<List<DeviceResponse>> getDevicesByProject(@PathVariable Long projectId,
        @PathVariable Integer systemTypeId);

    @GetMapping ("/load/powerTotal/forecast/{customerId}/{projectId}/{systemTypeId}")
    ResponseEntity<Forecast> getForecast(@PathVariable Integer customerId,
        @PathVariable (required = false) String projectId, @PathVariable (required = false) String systemTypeId);

    @PostMapping ("/load/powerTotal/forecast/save/{customerId}")
    ResponseEntity<?> saveForecast(@RequestBody Forecast forecast, @PathVariable Integer customerId);

    @PostMapping ("/load/powerTotal/download/{customerId}/{projectId}")
    ResponseEntity<Resource> downloadPowerTotal(@PathVariable String customerId, @PathVariable String projectId,
        @RequestParam (required = false) String fromDate, @RequestParam (required = false) String toDate,
        @RequestBody User user);

    @GetMapping ("/load/forecasts/{customerId}/{projectId}/{systemTypeId}/{page}")
    ResponseEntity<?> getForecasts(@PathVariable Integer customerId, @PathVariable Long projectId,
        @PathVariable Long systemTypeId, @PathVariable Integer page);

    // START: Thông tin vận hành PV
    @GetMapping ("/pv/operation/instant/inverter/{customerId}/{deviceId}")
    ResponseEntity<?> getInstantOperationInverterPV(@PathVariable Integer customerId, @PathVariable Long deviceId);

    @GetMapping ("/pv/operation/instant/weather/{customerId}/{deviceId}")
    ResponseEntity<?> getInstantOperationWeatherPV(@PathVariable Integer customerId, @PathVariable Long deviceId);

    @GetMapping ("/pv/operation/instant/combiner/{customerId}/{deviceId}")
    ResponseEntity<?> getInstantOperationCombinerPV(@PathVariable Integer customerId, @PathVariable Long deviceId);

    @GetMapping ("/pv/operation/instant/string/{customerId}/{deviceId}")
    ResponseEntity<?> getInstantOperationStringPV(@PathVariable Integer customerId, @PathVariable Long deviceId);

    @GetMapping ("/pv/operation/instant/panel/{customerId}/{deviceId}")
    ResponseEntity<?> getInstantOperationPanelPV(@PathVariable Integer customerId, @PathVariable Long deviceId);

    @GetMapping ("/pv/operation/inverter/{customerId}/{deviceId}/{page}")
    ResponseEntity<?> getOperationInverterPV(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate, @PathVariable Integer page);

    @GetMapping ("/pv/operation/weather/{customerId}/{deviceId}/{page}")
    ResponseEntity<?> getOperationWeatherPV(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate, @PathVariable Integer page);

    @GetMapping ("/pv/operation/combiner/{customerId}/{deviceId}/{page}")
    ResponseEntity<?> getOperationCombinerPV(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate, @PathVariable Integer page);

    @GetMapping ("/pv/operation/string/{customerId}/{deviceId}/{page}")
    ResponseEntity<?> getOperationStringPV(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate, @PathVariable Integer page);

    @GetMapping ("/pv/operation/panel/{customerId}/{deviceId}/{page}")
    ResponseEntity<?> getOperationPanelPV(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate, @PathVariable Integer page);

    @GetMapping ("/pv/operation/chart/inverter/{customerId}/{deviceId}")
    ResponseEntity<?> getChartInverterPV(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate);

    @GetMapping ("/pv/operation/chart/electrical-power/inverter/{customerId}/{deviceId}")
    ResponseEntity<?> getChartElectricalPowerInverterPV(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String date, @RequestParam Integer type);

    @GetMapping ("/pv/operation/chart/weather/{customerId}/{deviceId}")
    ResponseEntity<?> getChartWeatherPV(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate);

    @GetMapping ("/pv/operation/chart/combiner/{customerId}/{deviceId}")
    ResponseEntity<?> getCharCombinerPV(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate);

    @GetMapping ("/pv/operation/chart/string/{customerId}/{deviceId}")
    ResponseEntity<?> getCharStringPV(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate);

    @GetMapping ("/pv/operation/chart/panel/{customerId}/{deviceId}")
    ResponseEntity<?> getCharPanelPV(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate);

    @GetMapping ("/pv/operation/download/device-parameter/inverter/{customerId}/{deviceId}")
    ResponseEntity<Resource> downloadDeviceParameterInverterPV(@PathVariable Integer customerId,
        @PathVariable Long deviceId, @RequestParam String fromDate, @RequestParam String toDate,
        @RequestParam String userName);

    @GetMapping ("/pv/operation/download/device-parameter/weather/{customerId}/{deviceId}")
    ResponseEntity<Resource> downloadDeviceParameterWeatherPV(@PathVariable Integer customerId,
        @PathVariable Long deviceId, @RequestParam String fromDate, @RequestParam String toDate,
        @RequestParam String userName);

    @GetMapping ("/pv/operation/download/device-parameter/combiner/{customerId}/{deviceId}")
    ResponseEntity<Resource> downloadDeviceParameterCombinerPV(@PathVariable Integer customerId,
        @PathVariable Long deviceId, @RequestParam String fromDate, @RequestParam String toDate,
        @RequestParam String userName);

    @GetMapping ("/pv/operation/download/device-parameter/string/{customerId}/{deviceId}")
    ResponseEntity<Resource> downloadDeviceParameterStringPV(@PathVariable Integer customerId,
        @PathVariable Long deviceId, @RequestParam String fromDate, @RequestParam String toDate,
        @RequestParam String userName);

    @GetMapping ("/pv/operation/download/device-parameter/panel/{customerId}/{deviceId}")
    ResponseEntity<Resource> downloadDeviceParameterPanelPV(@PathVariable Integer customerId,
        @PathVariable Long deviceId, @RequestParam String fromDate, @RequestParam String toDate,
        @RequestParam String userName);

    @GetMapping ("/pv/operation/download/chart/inverter/{customerId}/{deviceId}/{type}")
    ResponseEntity<Resource> downloadChartInverter(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate, @PathVariable Integer type,
        @RequestParam String userName);

    @GetMapping ("/pv/operation/download/chart/inverter/electrical-power/{customerId}/{deviceId}/{type}")
    ResponseEntity<Resource> downloadChartElectricalPowerInverter(@PathVariable Integer customerId,
        @PathVariable Long deviceId, @RequestParam String date, @PathVariable Integer type,
        @RequestParam String userName);

    @GetMapping ("/pv/operation/download/chart/weather/{customerId}/{deviceId}/{type}")
    ResponseEntity<Resource> downloadChartWeather(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate, @PathVariable Integer type,
        @RequestParam String userName);

    @GetMapping ("/pv/operation/download/chart/combiner/{customerId}/{deviceId}/{type}")
    ResponseEntity<Resource> downloadChartCombinerPV(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate, @PathVariable Integer type,
        @RequestParam String userName);

    @GetMapping ("/pv/operation/download/chart/string/{customerId}/{deviceId}/{type}")
    ResponseEntity<Resource> downloadChartStringPV(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate, @PathVariable Integer type,
        @RequestParam String userName);

    @GetMapping ("/pv/operation/download/chart/panel/{customerId}/{deviceId}/{type}")
    ResponseEntity<Resource> downloadChartPanelPV(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate, @PathVariable Integer type,
        @RequestParam String userName);

    @GetMapping ("/pv/operation/setting/inverter/{customerId}/{deviceId}")
    ResponseEntity<?> getOperationSettingInverter(@PathVariable Integer customerId, @PathVariable Long deviceId);

    // END: Thông tin vận hành PV
    // START: điều khiển
    @GetMapping ("/pv/control/getSystem/{projectId}")
    ResponseEntity<List<SystemMap>> getListSystemMapByProject(@PathVariable String projectId);

    @GetMapping ("/pv/control/{projectId}")
    ResponseEntity<?> getControl(@PathVariable String projectId);

    @PostMapping ("/pv/control/device")
    ResponseEntity<?> detailControl(@RequestBody History control);

    @PostMapping ("pv/control/save")
    ResponseEntity<?> saveControl(@RequestBody List<Control> controls);

    @GetMapping ("pv/control/system/{systemMapId}")
    ResponseEntity<?> getControlSystem(@PathVariable String systemMapId);
    // END: điều khiển

    // Lấy thông tin của báo cáo.
    @GetMapping ("/pv/report/{customerId}/{userName}/{projectId}")
    ResponseEntity<Map<String, List>> loadReportPV(@PathVariable String customerId, @PathVariable String userName,
        @PathVariable String projectId);

    // Thêm mới yêu cầu tạo báo cáo.
    @GetMapping ("/pv/report/addReport/{customerId}/{reportType}/{date}/{userName}/{projectId}/{deviceId}")
    ResponseEntity<Void> addReportPV(@PathVariable String customerId, @PathVariable String reportType,
        @PathVariable String date, @PathVariable String userName, @PathVariable String projectId,
        @PathVariable String deviceId);

    // Tạo báo cáo.
    @PostMapping ("/pv/report/generateReports/{customerId}/{reportType}/{date}/{userName}/{projectId}/{deviceId}")
    ResponseEntity<Void> generateReportsPV(@PathVariable String customerId, @PathVariable String reportType,
        @PathVariable String date, @PathVariable String userName, @PathVariable String projectId,
        @PathVariable String deviceId, @RequestBody User user);

    // Xóa báo cáo
    @DeleteMapping ("/pv/report/delete/{id}")
    ResponseEntity<Void> deleteReportPV(@PathVariable int id);

    // Tải báo cáo
    @GetMapping ("/pv/report/download")
    ResponseEntity<Resource> downloadReportPV(@RequestParam String path);

    @GetMapping ("/pv/forecasts/{customerId}/{projectId}/{systemTypeId}/{page}")
    ResponseEntity<?> getForecastsPV(@PathVariable Integer customerId, @PathVariable Long projectId,
        @PathVariable Long systemTypeId, @PathVariable Integer page);

    @GetMapping ("/pv/forecast/{customerId}/{projectId}/{systemTypeId}")
    ResponseEntity<?> getForecastPV(@PathVariable Integer customerId, @PathVariable String projectId,
        @PathVariable String systemTypeId);

    @PostMapping ("/pv/forecast/save/{customerId}")
    ResponseEntity<?> saveForecastPV(@RequestBody Forecast forecast, @PathVariable Integer customerId);

    // START: cảnh báo
    @GetMapping ("/pv/warning/")
    ResponseEntity<?> getWarningsPV(@RequestParam String fromDate, @RequestParam String toDate,
        @RequestParam Integer customerId, @RequestParam String projectId);

    @GetMapping ("/pv/warning/type/{warningType}")
    ResponseEntity<?> detailWarningByTypePV(@PathVariable String warningType, @RequestParam String fromDate,
        @RequestParam String toDate, @RequestParam Integer customerId, @RequestParam String projectId,
        @RequestParam Integer page);

    @GetMapping ("/pv/warning/operation/type/{warningType}")
    ResponseEntity<?> detailWarningOperationByTypePV(@PathVariable String warningType, @RequestParam String fromDate,
        @RequestParam String toDate, @RequestParam Integer customerId, @RequestParam String deviceId,
        @RequestParam Integer page);

    @GetMapping ("/pv/warning/detail")
    ResponseEntity<?> showDataWarningByDevicePV(@RequestParam String warningType, @RequestParam String fromDate,
        @RequestParam String toDate, @RequestParam Integer customerId, @RequestParam String deviceId,
        @RequestParam Integer page);

    @GetMapping ("/pv/warning/update/{warningType}/{deviceId}")
    ResponseEntity<?> getWarningCachePV(@PathVariable ("warningType") String warningType,
        @PathVariable ("deviceId") String deviceId, @RequestParam String fromDate, @RequestParam String toDate,
        @RequestParam Integer customerId);

    @PostMapping ("/pv/warning/update/{warningId}")
    ResponseEntity<?> updateWarningCachePV(@RequestBody UpdateWarningForm form,
        @PathVariable ("warningId") Integer warningId);

    @GetMapping ("/pv/warning/download")
    ResponseEntity<Resource> downloadWarningOperationPV(@RequestParam ("warningType") String warningType,
        @RequestParam ("fromDate") String fromDate, @RequestParam ("toDate") String toDate,
        @RequestParam ("customerId") Integer customerId, @RequestParam ("deviceId") String deviceId,
        @RequestParam String userName);

    // END
    @GetMapping ("/pv/power/{customerId}/{projectId}")
    ResponseEntity<List<OverviewPVPower>> getPowerPVInDay(@PathVariable String customerId,
        @PathVariable String projectId, @RequestParam (required = false) String keyword);

    @GetMapping ("/pv/powerTotal/{customerId}/{projectId}")
    ResponseEntity<List<OverviewPVTotalPower>> getTotalPowerEnergyPV(@PathVariable String customerId,
        @PathVariable String projectId, @RequestParam String fromDate, @RequestParam String toDate);

    @GetMapping ("/pv/energy/{customerId}/{projectId}")
    ResponseEntity<DataEnergyPV> getTotalACEnergy(@PathVariable String customerId, @PathVariable String projectId);

    @PostMapping ("/pv/powerTotal/download/{customerId}/{projectId}")
    ResponseEntity<Resource> downloadPowerTotalPV(@PathVariable String customerId, @PathVariable String projectId,
        @RequestParam (required = false) String fromDate, @RequestParam (required = false) String toDate,
        @RequestBody User user);

    @GetMapping ("/pv/operation/instant/string/combiner/{deviceId}")
    ResponseEntity<List<DataString1>> getInstantOperationStringInCombinerPV(@PathVariable Long deviceId);

    // Client Grid

    // START: Thông tin vận hành Grid
    @GetMapping ("/grid/operation/instant/rmu-drawer/{customerId}/{deviceId}")
    ResponseEntity<?> getInstantOperationRmuDrawerGrid(@PathVariable Integer customerId, @PathVariable Long deviceId);

    @GetMapping ("/grid/operation/rmu-drawer/{customerId}/{deviceId}/{page}")
    ResponseEntity<?> getOperationRmuDrawerGrid(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam String fromDate, @RequestParam String toDate, @PathVariable Integer page);

    @GetMapping ("/grid/operation/download/device-parameter/rmu-drawer/{customerId}/{deviceId}/{type}")
    ResponseEntity<Resource> downloadDeviceParameterRmuDrawerGrid(@PathVariable Integer customerId,
        @PathVariable Long deviceId, @PathVariable Integer type, @RequestParam String fromDate,
        @RequestParam String toDate, @RequestParam String userName);

    @GetMapping ("/grid/operation/chart/rmu-drawer/{customerId}/{deviceId}")
    ResponseEntity<?> getDataChartRmuDrawerGrid(@PathVariable Integer customerId, @PathVariable Long deviceId,
        @RequestParam (required = false) String fromDate, @RequestParam (required = false) String toDate);

    @GetMapping ("/grid/operation/chart/electrical-power/rmu-drawer/{customerId}/{deviceId}")
    ResponseEntity<?> getChartElectricalPowerRmuDrawerGrid(@PathVariable Integer customerId,
        @PathVariable Long deviceId, @RequestParam String date, @RequestParam Integer type);

    @GetMapping ("/grid/operation/chart/download/rmu-drawer/{customerId}/{deviceId}")
    ResponseEntity<Resource> downloadDataChartRmuDrawerGrid(@PathVariable Integer customerId,
        @PathVariable Long deviceId, @RequestParam (required = false) String fromDate,
        @RequestParam (required = false) String toDate, @RequestParam Integer pqsViewType,
        @RequestParam Integer chartType, @RequestParam String userName);

    // END: Thông tin vận hành Grid

    @SuppressWarnings ("rawtypes")
    @GetMapping ("/grid/report/{userName}/{projectId}")
    ResponseEntity<Map<String, List>> loadReportGrid(@PathVariable String userName, @PathVariable String projectId);

    @DeleteMapping ("/grid/report/delete/{id}")
    ResponseEntity<Void> deleteReportGrid(@PathVariable int id);

    @GetMapping ("/grid/report/download")
    ResponseEntity<Resource> downloadReportGrid(@RequestParam String path) throws Exception;

    @GetMapping ("/grid/report/addReport/{customerId}/{deviceId}/{reportType}/{date}/{userName}/{projectId}/{dateType}")
    ResponseEntity<Void> addReportGrid(@PathVariable String customerId, @PathVariable String deviceId,
        @PathVariable String reportType, @PathVariable String date, @PathVariable String userName,
        @PathVariable String projectId, @PathVariable String dateType) throws ParseException;

    // CHECKSTYLE:OFF
    @PostMapping ("/grid/report/generateReports/{customerId}/{deviceId}/{reportType}/{date}/{dateType}"
        + "/{userName}/{projectId}")
    ResponseEntity<?> generateReportsGrid(@PathVariable String customerId, @PathVariable String deviceId,
        @PathVariable String reportType, @PathVariable String date, @PathVariable String dateType,
        @PathVariable String userName, @PathVariable String projectId, @RequestBody User user);

    // CHECKSTYLE:ON
    @GetMapping ("/grid/power/{customerId}/{projectId}")
    ResponseEntity<List<OverviewGridPower>> getPowerGridInDay(@PathVariable String customerId,
        @PathVariable String projectId, @RequestParam (required = false) String keyword);

    @GetMapping ("/grid/powerTotal/{customerId}/{projectId}")
    ResponseEntity<List<OverviewGridTotalPower>> getTotalPowerEnergyGrid(@PathVariable String customerId,
        @PathVariable String projectId, @RequestParam String fromDate, @RequestParam String toDate);

    @GetMapping ("/grid/energy/{customerId}/{projectId}")
    ResponseEntity<DataEnergyPV> getTotalEnergyGrid(@PathVariable String customerId, @PathVariable String projectId);

    @PostMapping ("/grid/powerTotal/download/{customerId}/{projectId}")
    ResponseEntity<Resource> downloadPowerTotalGrid(@PathVariable String customerId, @PathVariable String projectId,
        @RequestParam (required = false) String fromDate, @RequestParam (required = false) String toDate,
        @RequestBody User user);

    // END: Thông tin vận hành Grid

    // START: cảnh báo Grid
    @GetMapping ("/grid/warning/")
    ResponseEntity<?> getWarningsGrid(@RequestParam String fromDate, @RequestParam String toDate,
        @RequestParam Integer customerId, @RequestParam String projectId);

    @GetMapping ("/grid/warning/type/{warningType}")
    ResponseEntity<?> detailWarningByTypeGrid(@PathVariable String warningType, @RequestParam String fromDate,
        @RequestParam String toDate, @RequestParam Integer customerId, @RequestParam String projectId,
        @RequestParam Integer page);

    @GetMapping ("/grid/warning/operation/type/{warningType}")
    ResponseEntity<?> detailWarningOperationInformationByTypeGrid(@PathVariable String warningType,
        @RequestParam String fromDate, @RequestParam String toDate, @RequestParam Integer customerId,
        @RequestParam String deviceId, @RequestParam Integer page);

    @PostMapping ("/grid/warning/update/{warningId}")
    ResponseEntity<?> updateWarningCacheGrid(@RequestBody UpdateWarningForm form,
        @PathVariable ("warningId") Integer warningId);

    @GetMapping ("/grid/warning/update/{warningType}/{deviceId}")
    ResponseEntity<?> getWarningCacheGrid(@PathVariable ("warningType") String warningType,
        @PathVariable ("deviceId") String deviceId, @RequestParam String fromDate, @RequestParam String toDate,
        @RequestParam Integer customerId);

    @GetMapping ("/grid/warning/detail")
    ResponseEntity<?> showDataWarningByDeviceGrid(@RequestParam String warningType, @RequestParam String fromDate,
        @RequestParam String toDate, @RequestParam Integer projectId, @RequestParam Integer customerId,
        @RequestParam String deviceId, @RequestParam Integer page);

    @GetMapping ("/grid/warning/download")
    ResponseEntity<Resource> downloadWarningOperationGrid(@RequestParam ("warningType") String warningType,
        @RequestParam ("fromDate") String fromDate, @RequestParam ("toDate") String toDate,
        @RequestParam ("customerId") Integer customerId, @RequestParam ("deviceId") String deviceId,
        @RequestParam String userName);

    // END

    // pv
    @GetMapping ("/pv/receivers")
    ResponseEntity<?> getListReceiver(@RequestParam (value = "projectId") String projectId,
        @RequestParam (value = "systemType") String systemType);

    @PostMapping ("/pv/receiver/save/{systemType}/{customerId}/{projectId}/{deviceId}/{receiverId}")
    ResponseEntity<?> saveWarningToSent(@RequestBody String data, @PathVariable String systemType,
        @PathVariable String customerId, @PathVariable String projectId, @PathVariable String deviceId,
        @PathVariable String receiverId);

    @GetMapping ("/pv/receiver/getWarning/{receiverId}/{deviceId}")
    ResponseEntity<?> getWarningInfor(@PathVariable (value = "receiverId") String receiverId,
        @PathVariable (value = "deviceId") String deviceId);

    @PostMapping ("/pv/receiver/add/{projectId}/{systemType}")
    ResponseEntity<?> addNewReceiver(@RequestBody Receiver receiver,
        @PathVariable (value = "projectId") String projectId, @PathVariable (value = "systemType") String systemType);

    @PostMapping ("/pv/receiver/update")
    ResponseEntity<?> updateReceiver(@RequestBody Receiver receiver);

    @GetMapping ("/pv/receiver/delete")
    ResponseEntity<?> deleteReceiver(@RequestParam (value = "receiverId") String receiverId);

    // load
    @GetMapping ("/load/receivers")
    ResponseEntity<?> getListReceiverLoad(@RequestParam (value = "projectId") String projectId,
        @RequestParam (value = "systemType") String systemType);

    @PostMapping ("/load/receiver/save/{systemType}/{customerId}/{projectId}/{deviceId}/{receiverId}")
    ResponseEntity<?> saveWarningToSentLoad(@RequestBody String data, @PathVariable String systemType,
        @PathVariable String customerId, @PathVariable String projectId, @PathVariable String deviceId,
        @PathVariable String receiverId);

    @GetMapping ("/load/receiver/getWarning/{receiverId}/{deviceId}")
    ResponseEntity<?> getWarningInforLoad(@PathVariable (value = "receiverId") String receiverId,
        @PathVariable (value = "deviceId") String deviceId);

    @PostMapping ("/load/receiver/add/{projectId}/{systemType}")
    ResponseEntity<?> addNewReceiverLoad(@RequestBody Receiver receiver,
        @PathVariable (value = "projectId") String projectId, @PathVariable (value = "systemType") String systemType);

    @PostMapping ("/load/receiver/update")
    ResponseEntity<?> updateReceiverLoad(@RequestBody Receiver receiver);

    @GetMapping ("/load/receiver/delete")
    ResponseEntity<?> deleteReceiverLoad(@RequestParam (value = "receiverId") String receiverId);

    // grid
    @GetMapping ("/grid/receivers")
    ResponseEntity<?> getListReceiverGrid(@RequestParam (value = "projectId") String projectId,
        @RequestParam (value = "systemType") String systemType);

    @PostMapping ("/grid/receiver/save/{systemType}/{customerId}/{projectId}/{deviceId}/{receiverId}")
    ResponseEntity<?> saveWarningToSentGrid(@RequestBody String data, @PathVariable String systemType,
        @PathVariable String customerId, @PathVariable String projectId, @PathVariable String deviceId,
        @PathVariable String receiverId);

    @GetMapping ("/grid/receiver/getWarning/{receiverId}/{deviceId}")
    ResponseEntity<?> getWarningInforGrid(@PathVariable (value = "receiverId") String receiverId,
        @PathVariable (value = "deviceId") String deviceId);

    @PostMapping ("/grid/receiver/add/{projectId}/{systemType}")
    ResponseEntity<?> addNewReceiverGrid(@RequestBody Receiver receiver,
        @PathVariable (value = "projectId") String projectId, @PathVariable (value = "systemType") String systemType);

    @PostMapping ("/grid/receiver/update")
    ResponseEntity<?> updateReceiverGrid(@RequestBody Receiver receiver);

    @GetMapping ("/grid/receiver/delete")
    ResponseEntity<?> deleteReceiverGrid(@RequestParam (value = "receiverId") String receiverId);
}
