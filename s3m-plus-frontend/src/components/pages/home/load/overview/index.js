import React, { useEffect, useState } from "react";
import overviewLoadService from "../../../../../services/OverviewLoadService";
import OperationInformationService from "../../../../../services/OperationInformationService";
import * as am5 from "@amcharts/amcharts5";
import * as am5xy from "@amcharts/amcharts5/xy";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";
import { useParams } from "react-router";
import { Link, useHistory } from "react-router-dom";
import moment from 'moment';
import $ from "jquery";
import CONS from "../../../../../constants/constant";
import { Calendar } from 'primereact/calendar';
import Converter from "../../../../../common/converter";
import authService from "../../../../../services/AuthService";

const OverviewLoad = () => {

    const param = useParams();
    const history = useHistory();
    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [statusDownload, setStatusDownload] = useState(false);

    let date = new Date();

    let dateStr =
        ("00" + date.getDate()).slice(-2) + "-" +
        ("00" + (date.getMonth() + 1)).slice(-2) + "-" +
        date.getFullYear() + " " +
        ("00" + date.getHours()).slice(-2) + ":" +
        ("00" + date.getMinutes()).slice(-2)

    const [powers, setPower] = useState([]);
    const [powerDef, setPowerDef] = useState([]);
    const [powersWarning, setPowersWarning] = useState([]);
    const [state, setState] = useState(0);
    const [totalEnergy, setTotalEnergy] = useState();
    const [totalEnergyCurYear, setTotalEnergyCurYear] = useState();
    const [totalEnergyPreYear, setTotalEnergyPreYear] = useState();
    const [totalEnergyCurMonth, setTotalEnergyCurMonth] = useState();
    const [totalEnergyPreMonth, setTotalEnergyPreMonth] = useState();
    const [totalEnergyCurDay, setTotalEnergyCurDay] = useState();
    const [totalEnergyPreDay, setTotalEnergyPreDay] = useState();
    const [instantOperationInfo, setInstantOperationInfo] = useState({
        id: "",
        deviceId: "",
        deviceName: "",
        uab: "",
        ubc: "",
        uca: "",
        ull: "",
        uan: "",
        ubn: "",
        ucn: "",
        uln: "",
        ia: "",
        ib: "",
        ic: "",
        in: "",
        iavg: "",
        pa: "",
        pb: "",
        pc: "",
        ptotal: "",
        qa: "",
        qb: "",
        qc: "",
        qtotal: "",
        sa: "",
        sb: "",
        sc: "",
        stotal: "",
        pfa: "",
        pfb: "",
        pfc: "",
        pfavg: "",
        thdVab: "",
        thdVbc: "",
        thdVca: "",
        thdVan: "",
        thdIa: "",
        thdIb: "",
        thdIc: "",
        thdIn: "",
        t1: "",
        t2: "",
        t3: "",
        ep: "",
        epR: "",
        epDr: "",
        epDrr: "",
        eq: "",
        eqR: "",
        eqDr: "",
        eqDrr: "",
        sentDate: "",
        transactionDate: ""
    });
    const [projectInfor, setProjectInfor] = useState();



    const getPowers = async () => {
        let keyword = document.getElementById('keyword').value;
        let res = await overviewLoadService.getPower(param.customerId, param.projectId, keyword);
        if (res.status === 200) {
            setPower(res.data.deviceList);
            setPowersWarning(res.data.deviceList);
            setPowerDef(res.data.deviceList)
            setTotalEnergyCurDay(res.data.sumEnergyToday >= 0 && res.data.sumEnergyToday < 4000000000 && res.data.sumEnergyToday !=null ? res.data.sumEnergyToday : "-");
            setTotalEnergyCurMonth(res.data.sumEnergyCurMonth >= 0 && res.data.sumEnergyCurMonth < 4000000000 && res.data.sumEnergyCurMonth !=null ? res.data.sumEnergyCurMonth : "-");
            setTotalEnergyCurYear(res.data.sumEnergyCurrentYear >= 0 && res.data.sumEnergyCurrentYear < 4000000000 && res.data.sumEnergyCurrentYear !=null ? res.data.sumEnergyCurrentYear : "-");
            setTotalEnergyPreDay(res.data.sumEnergyPreday >= 0 && res.data.sumEnergyPreday < 4000000000 && res.data.sumEnergyPreday !=null ? res.data.sumEnergyPreday : "-");
            setTotalEnergyPreMonth(res.data.sumEnergyPreMonth >= 0 && res.data.sumEnergyPreMonth < 4000000000 && res.data.sumEnergyPreMonth !=null ? res.data.sumEnergyPreMonth : "-");
            setTotalEnergyPreYear(res.data.sumEnergyLastYear >=0 && res.data.sumEnergyLastYear < 4000000000 && res.data.sumEnergyLastYear !=null ? res.data.sumEnergyLastYear : "-");
            setTotalEnergy(res.data.sumEnergy >= 0 && res.data.sumEnergy < 4000000000 && res.data.sumEnergy !== null ? res.data.sumEnergy : "-");
            setProjectInfor(res.data.projectInfor + "LOAD");
        }
    };

    const handleSetViewTypeChart = (data) => {
        data.forEach(item => {
            if (item.energy) {
                item.energy = item.energy;
            }
            if (item.power) {
                item.power = item.power ;
            }
            if (item.forecast) {
                item.forecast = item.forecast ;
            }
        });

        drawChart(data, 1);
    }


    let countError = powerDef?.filter(d => {
        return d.loadStatus.includes("error")
    })

    let countWarning = powersWarning?.filter(d => {
        return d.loadStatus.includes("warning")
    })

    let countOnline = powerDef?.filter(d => {
        return d.loadStatus.includes("active")
    }).length + countWarning.length

    const exportToExcel = async () => {
        setStatusDownload(true);
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let res = await overviewLoadService.exportExcel(param.customerId, param.projectId, fDate, tDate, authService.getUserName());
        if (res.status === 501) {
            $.alert("Không có dữ liệu");
        }
        setStatusDownload(false);
    }

    const getDataChart = async () => {
        $('#chartdiv').hide();
        $('#loading').show();
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let res = await overviewLoadService.getDataChart(param.customerId, param.projectId, fDate, tDate);
        if (res.status === 200) {
            handleSetViewTypeChart(res.data);
            $('#loading').hide();
            $('#chartdiv').show();
        }
    };

    const drawChart = (dataChart, state) => {
        am5.array.each(am5.registry.rootElements, function (root) {
            if (root) {
                if (root.dom.id == "chartdiv") {
                    root.dispose();
                }
            }
        });
        am5.ready(function () {
            let root = am5.Root.new("chartdiv");
            // Create root element
            // https://www.amcharts.com/docs/v5/getting-started/#Root_element

            // Set themes
            // https://www.amcharts.com/docs/v5/concepts/themes/
            root.setThemes([am5themes_Animated.new(root)]);

            // Create chart
            // https://www.amcharts.com/docs/v5/charts/xy-chart/
            let chart = root.container.children.push(
                am5xy.XYChart.new(root, {

                    panX: true,
                    panY: false,
                    wheelX: "panX",
                    layout: root.verticalLayout
                })
            );

            // Add scrollbar
            // https://www.amcharts.com/docs/v5/charts/xy-chart/scrollbars/
            chart.set(
                "scrollbarX",
                am5.Scrollbar.new(root, {
                    orientation: "horizontal"
                })
            );
            let scrollbarX = chart.get("scrollbarX");

            scrollbarX.thumb.setAll({
                fill: am5.color(0x550000),
                fillOpacity: 0.1
            });

            scrollbarX.startGrip.setAll({
                visible: true
            });

            scrollbarX.endGrip.setAll({
                visible: true
            });
            let data = dataChart;

            // Create axes
            // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/

            let xRenderer = am5xy.AxisRendererX.new(root, { minGridDistance: 10 });
            xRenderer.labels.template.setAll({
                rotation: -70,
                paddingTop: -20,
                paddingRight: 10
            });

            let xAxis = chart.xAxes.push(
                am5xy.CategoryAxis.new(root, {
                    categoryField: "time",
                    renderer: xRenderer,
                    tooltip: am5.Tooltip.new(root, {})
                })
            );

            xAxis.data.setAll(data);

            let yRenderer = am5xy.AxisRendererY.new(root, {
                minGridDistance: 30,
                opposite: false
            });

            let yAxis = chart.yAxes.push(
                am5xy.ValueAxis.new(root, {
                    min: 0,
                    renderer: yRenderer,
                    maxDeviation: 0,
                })
            );

            // function createRange(value, color) {
            //     var rangeDataItem = yAxis.makeDataItem({
            //         value: value
            //     });
            //     var range = yAxis.createAxisRange(rangeDataItem);
            //     range.get("grid").setAll({
            //         stroke: color,
            //         strokeOpacity: 1,
            //         location: 1
            //     });
            // };
            // createRange(0, am5.color(0x797D7F));

            yAxis.children.moveValue(am5.Label.new(root, { text: `Điện năng [[kWh]]`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);

            let series1 = chart.series.push(
                am5xy.ColumnSeries.new(root, {
                    name: `Điện năng [kWh]`,
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: "energy",
                    categoryXField: "time",
                    tooltip: am5.Tooltip.new(root, {
                        pointerOrientation: "horizontal",
                        labelText: `Điện năng: {valueY} [[kWh]]`
                    }),
                    fill: am5.color(0xDDE5B6)
                })
            );

            yRenderer.grid.template.set("strokeOpacity", 0.05);
            yRenderer.labels.template.set("fill", series1.get("fill"));
            yRenderer.setAll({
                stroke: series1.get("fill"),
                strokeOpacity: 1,
                opacity: 1
            });

            series1.columns.template.setAll({
                tooltipY: am5.percent(10),
                templateField: "columnSettings"
            });

            series1.data.setAll(data);

            yRenderer = am5xy.AxisRendererY.new(root, {
                opposite: true
            });
            let yAxis2 = chart.yAxes.push(
                am5xy.ValueAxis.new(root, {
                    min: 0,
                    maxDeviation: 0,
                    renderer: yRenderer,
                })
            );
            yAxis2.children.moveValue(am5.Label.new(root, { text: `Công suất [[kW]]`, rotation: 90, y: am5.p50, centerX: am5.p50 }), 1);

            // Add series
            // https://www.amcharts.com/docs/v5/charts/xy-chart/series/

            let series2 = chart.series.push(
                am5xy.LineSeries.new(root, {
                    name: `Công suất [kW]`,
                    xAxis: xAxis,
                    yAxis: yAxis2,
                    valueYField: "power",
                    categoryXField: "time",
                    tooltip: am5.Tooltip.new(root, {
                        pointerOrientation: "horizontal",
                        labelText: `Công suất: {valueY} [[kW]]`
                    }),
                    stroke: am5.color(0xFF0000)
                })
            );

            series2.strokes.template.setAll({
                strokeWidth: 1,
                templateField: "strokeSettings"
            });
            series2.set("fill", am5.color("#FF0000"));

            yRenderer.grid.template.set("strokeOpacity", 0.05);
            yRenderer.labels.template.set("fill", series2.get("fill"));
            yRenderer.setAll({
                stroke: series2.get("fill"),
                strokeOpacity: 1,
                opacity: 1
            });

            series2.data.setAll(data);

            series2.bullets.push(function () {
                return am5.Bullet.new(root, {
                    sprite: am5.Circle.new(root, {
                        strokeWidth: 4,
                        stroke: series2.get("stroke"),
                        radius: 2,
                        fill: root.interfaceColors.get("background")
                    })
                });
            });


            let series3 = chart.series.push(
                am5xy.LineSeries.new(root, {
                    name: `Công suất dự báo [kW]`,
                    xAxis: xAxis,
                    yAxis: yAxis2,
                    valueYField: "forecast",
                    categoryXField: "time",
                    tooltip: am5.Tooltip.new(root, {
                        pointerOrientation: "horizontal",
                        labelText: `Công suất dự báo: {valueY} [[kW]]`
                    }),
                    stroke: am5.color(0xFCD202)
                })
            );

            series3.strokes.template.setAll({
                strokeWidth: 1,
                templateField: "strokeSettings"
            });
            series3.set("fill", am5.color("#FCD202"));

            yRenderer.grid.template.set("strokeOpacity", 0.05);
            yRenderer.labels.template.set("fill", series3.get("fill"));
            yRenderer.setAll({
                stroke: series3.get("fill"),
                strokeOpacity: 1,
                opacity: 1
            });

            series3.data.setAll(data);
            series3.bullets.push(function () {
                return am5.Bullet.new(root, {
                    sprite: am5.Circle.new(root, {
                        strokeWidth: 4,
                        stroke: series3.get("stroke"),
                        radius: 2,
                        fill: root.interfaceColors.get("background")
                    })
                });
            });

            // Add cursor
            // https://www.amcharts.com/docs/v5/charts/xy-chart/cursor/
            let cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
                xAxis: xAxis,
                behavior: "none"
            }));
            cursor.lineY.set("visible", false);

            // Add legend
            // https://www.amcharts.com/docs/v5/charts/xy-chart/legend-xy-series/
            let legend = chart.children.push(
                am5.Legend.new(root, {
                    centerX: am5.p50,
                    x: am5.p50
                })
            );
            legend.data.setAll(chart.series.values);

            // Make stuff animate on load
            // https://www.amcharts.com/docs/v5/concepts/animations/
            chart.appear(1000, 100);

            xAxis.events.once("datavalidated", function (ev) {
                ev.target.zoomToIndexes(data.length - 20, data.length);
            });

        });
    };

    const getOperationInformation = async (deviceId) => {
        let res = await OperationInformationService.getInstantOperationInformation(param.customerId, deviceId);
        if (res.status === 200) {
            setInstantOperationInfo(res.data);
        }
    };
    const warningTo = () => {
        history.push({
            pathname: "/home/load/" + param.customerId + "/" + param.projectId + "/warning",
            state: {
                status: 200,
                message: "warning_all"
            }
        }
        );
    };
    const getDeviceByError = () => {
        let deviceError = powers?.filter(d => {
            return d.loadStatus.includes("error")
        });
        setPower(deviceError);
    };

    const getDeviceOnline = () => {
        let deviceOnline = powerDef?.filter(d => {
            return d.loadStatus.includes("active")
        })
        let deviceWarning = powerDef?.filter(d => {
            return d.loadStatus.includes("warning")
        })
        let alldeviceOnline = deviceOnline.concat(deviceWarning)
        setPower(alldeviceOnline);
    }


    useEffect(() => {
        document.title = "Overview Load";
        getPowers();
        getDataChart();
    }, [param.customerId,param.projectId]);

    return (
        <>
            <div className="tab-content">
                <div className="tab-title load-title mt-2" style={{ display: "flex", height: "35px" }}>
                    <div className="project" style={{ display: "flex", flexDirection: "column" }}>
                        <div className="latest-time" style={{ padding: "0px 10px", display: "block" }}>
                            <i className="fa-regular fa-clock"></i>&nbsp; {dateStr}
                        </div>
                        <div className="project-infor" style={{ padding: "0px 10px", display: "block", marginTop: "10px" }}>
                            <span className="project-tree">{projectInfor}</span>
                        </div>

                    </div>
                    <div className="waning-error-button" style={{ marginLeft: "auto" }}>
                        <div className="latest-warning load" style={{ margin: "0px" }}>
                            <button type="button" className="btn btn-outline-info mr-2" onClick={() => getPowers()}>
                                <i className="fa-solid fa-xmark"></i>
                            </button>
                            <button type="button" className="btn w-180px" style={{ backgroundColor: "#A7C942" }} onClick={() => getDeviceOnline()}>
                                <i className="fa-solid fa-wifi"></i> &nbsp;Đang hoạt động: {countOnline}
                            </button>
                            <button type="button" className="btn btn-danger w-150px ml-2" onClick={() => getDeviceByError()}>
                                <i className="fa-solid fa-bug"></i> &nbsp;Mất tín hiệu: {countError.length}
                            </button>
                            <button type="button" className="btn btn-warning w-150px ml-2" onClick={() => warningTo()} >
                                <i className="fa-solid fa-triangle-exclamation"></i> &nbsp;Cảnh báo: {countWarning.length}

                            </button>
                        </div>
                    </div>
                </div>

                <table className="table tbl-overview tbl-tsd">
                    <thead>
                        <tr>
                            <th colSpan={7} className="tbl-title">Điện năng tiêu thụ [kWh] </th>
                        </tr>
                        <tr>
                            <th className="tbl-title">Tổng</th>
                            <th>Hôm qua</th>
                            <th>Hôm nay</th>
                            <th>Tháng trước</th>
                            <th>Tháng này</th>
                            <th>Năm trước</th>
                            <th>Năm nay</th>
                        </tr>
                        <tr>
                            <td className="text-right">{totalEnergy}</td>
                            <td className="text-right">{totalEnergyPreDay}</td>
                            <td className="text-right">{totalEnergyCurDay}</td>
                            <td className="text-right">{totalEnergyPreMonth}</td>
                            <td className="text-right">{totalEnergyCurMonth}</td>
                            <td className="text-right">{totalEnergyPreYear}</td>
                            <td className="text-right">{totalEnergyCurYear}</td>
                        </tr>
                    </thead>
                </table>

                <div id="main-search">
                    <div className="input-group search-item mb-3 float-left">
                        <div className="input-group-prepend">
                            <span className="input-group-text" id="inputGroup-sizing-default">Tìm kiếm</span>
                        </div>
                        <input type="text" id="keyword" className="form-control" aria-label="Mô tả" aria-describedby="inputGroup-sizing-sm" onKeyDown={e => e.key === "Enter" && getPowers(e)} />
                    </div>

                    <div className="search-buttons float-left">
                        <button type="button" className="btn btn-outline-secondary" onClick={getPowers}>
                            <i className="fa-solid fa-magnifying-glass"></i>
                        </button>
                    </div>
                </div>

                <table className="table tbl-overview tbl-power">
                    <thead style={powers?.length >= 5 ? { display: "table", width: "calc(100% - 10px)", tableLayout: "fixed" } : { display: "table", width: "100%", tableLayout: "fixed" }}>
                        <tr>
                            <th width="40px">TT</th>
                            <th>Thành Phần</th>
                            <th width="150px">Công Suất [kW]</th>
                            <th width="150px">Trạng Thái</th>
                            <th width="150px">Vị trí</th>
                            <th width="70px"><i className="fa-regular fa-hand"></i></th>
                        </tr>
                    </thead>
                    <tbody style={{ display: "block", maxHeight: "115px", overflow: "auto" }}>
                        {
                            powers?.map(
                                (power, index) => <tr key={index} style={{ display: "table", width: "100%", tableLayout: "fixed" }}>
                                    <td width="40px" style={{ textAlign: "center" }}>{index + 1}</td>
                                    <td>{power.deviceName}</td>
                                    {power.ptotal != null && power.ptotal >= -6000000 && power.ptotal <= 6000000 ? <td width="150px" style={{ textAlign: "center" }}>{power.ptotal }</td> : <td width="150px" style={{ textAlign: "center" }}>-</td>}
                                    {
                                        (power.loadStatus == "active") &&
                                        <td className="text-center device-status active" width="150px"><i className="fa-solid fa-circle"></i></td>
                                    }
                                    {
                                        (power.loadStatus == "error") &&
                                        <td className="text-center device-status offline" width="150px"><i className="fa-solid fa-circle"></i></td>
                                    }
                                    {
                                        (power.loadStatus == "warning") &&
                                        <td className="text-center device-status warning" width="150px"><i className="fa-solid fa-circle"></i></td>
                                    }
                                    {
                                        power.layer ?
                                            <td width="150px">
                                                <Link to={`/home/load/${param.customerId}/${power.projectId}/systemMap/${power.systemMapId}?deviceId=${power.deviceId}`}>
                                                    Layer {power.layer} {`>`} {power.systemMapName}
                                                </Link>
                                            </td>
                                            : <td width="150px" style={{ textAlign: "center" }} >-</td>
                                    }

                                    <td width="70px" style={{ padding: "3px 15px" }} >
                                        <a className="button-icon text-left" data-toggle="modal" data-target={"#model-" + (index + 1)} onClick={() => getOperationInformation(power.deviceId)}>
                                            <img height="16px" src="/resources/image/icon-info.png" title="View Info" alt="view-info" />
                                        </a>
                                        {
                                            power.systemMapId > 0 &&
                                            <Link to={`/home/load/${param.customerId}/${power.projectId}/systemMap/${power.systemMapId}?deviceId=${power.deviceId}`} className="button-icon float-right" title="View Grid">
                                                <img height="16px" className="mt-0.5" src="/resources/image/icon-grid.png" alt="system-map-icon" />
                                            </Link>
                                        }
                                        <div className="modal fade" id={"model-" + (index + 1)} tabIndex="-1" role="dialog" aria-labelledby="exampleModalLabel"
                                            aria-hidden="true">
                                            <div className="modal-dialog modal-lg" role="document">
                                                <div className="modal-content">
                                                    <div className="modal-header" style={{ backgroundColor: "#052274", height: "44px" }}>
                                                        <h5 className="modal-title" id="myModalLabel" style={{ color: "white", fontSize: "15px", fontWeight: "bold" }}>{power.deviceName}</h5>
                                                    </div>
                                                    <div className="modal-body">
                                                        <table className="table text-center tbl-overview table-oper-info-tool">
                                                            <thead>
                                                                <tr>
                                                                    <th style={{ backgroundColor: "white" }}><i className="fa-regular fa-clock mr-1" style={{ color: "revert" }}></i>{instantOperationInfo?.sentDate != null ? moment(instantOperationInfo.sentDate).format(CONS.DATE_FORMAT_OPERATE) : ""}</th>
                                                                    <th width="100px">Phase A</th>
                                                                    <th width="100px">Phase B</th>
                                                                    <th width="100px">Phase C</th>
                                                                    <th width="100px">Phase N</th>
                                                                    <th width="100px">Total</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <tr>
                                                                    <th scope="row">U<sub>LL</sub> [V]</th>
                                                                    <td>{instantOperationInfo?.uab != null ? instantOperationInfo.uab : "-"}</td>
                                                                    <td>{instantOperationInfo?.ubc != null ? instantOperationInfo.ubc : "-"}</td>
                                                                    <td>{instantOperationInfo?.uca != null ? instantOperationInfo.uca : "-"}</td>
                                                                    <td>-</td>
                                                                    <td>-</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">U<sub>LN</sub> [V]</th>
                                                                    <td>{instantOperationInfo?.uan != null ? instantOperationInfo.uan : "-"}</td>
                                                                    <td>{instantOperationInfo?.ubn != null ? instantOperationInfo.ubn : "-"}</td>
                                                                    <td>{instantOperationInfo?.ucn != null ? instantOperationInfo.ucn : "-"}</td>
                                                                    <td>-</td>
                                                                    <td>-</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">I [A]</th>
                                                                    <td>{instantOperationInfo?.ia != null ? instantOperationInfo.ia : "-"}</td>
                                                                    <td>{instantOperationInfo?.ib != null ? instantOperationInfo.ib : "-"}</td>
                                                                    <td>{instantOperationInfo?.ic != null ? instantOperationInfo.ic : "-"}</td>
                                                                    <td>{instantOperationInfo?.in != null ? instantOperationInfo.in : "-"}</td>
                                                                    <td>-</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">P [kW]</th>
                                                                    <td>{instantOperationInfo?.pa != null ? instantOperationInfo.pa  : "-"}</td>
                                                                    <td>{instantOperationInfo?.pb != null ? instantOperationInfo.pb  : "-"}</td>
                                                                    <td>{instantOperationInfo?.pc != null ? instantOperationInfo.pc  : "-"}</td>
                                                                    <td>-</td>
                                                                    <td>{instantOperationInfo?.ptotal != null ? instantOperationInfo.ptotal  : "-"}</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">Q [kVAr]</th>
                                                                    <td>{instantOperationInfo?.qa != null ? instantOperationInfo.qa  : "-"}</td>
                                                                    <td>{instantOperationInfo?.qb != null ? instantOperationInfo.qb  : "-"}</td>
                                                                    <td>{instantOperationInfo?.qc != null ? instantOperationInfo.qc  : "-"}</td>
                                                                    <td>-</td>
                                                                    <td>{instantOperationInfo?.qtotal != null ? instantOperationInfo.qtotal  : "-"}</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">S [kVA]</th>
                                                                    <td>{instantOperationInfo?.sa != null ? instantOperationInfo.sa  : "-"}</td>
                                                                    <td>{instantOperationInfo?.sb != null ? instantOperationInfo.sb  : "-"}</td>
                                                                    <td>{instantOperationInfo?.sc != null ? instantOperationInfo.sc  : "-"}</td>
                                                                    <td>-</td>
                                                                    <td>{instantOperationInfo?.stotal != null ? instantOperationInfo.stotal  : "-"}</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">PF</th>
                                                                    <td>{instantOperationInfo?.pfa != null ? instantOperationInfo.pfa : "-"}</td>
                                                                    <td>{instantOperationInfo?.pfb != null ? instantOperationInfo.pfb : "-"}</td>
                                                                    <td>{instantOperationInfo?.pfc != null ? instantOperationInfo.pfc : "-"}</td>
                                                                    <td>-</td>
                                                                    <td>-</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">THD U [%]</th>
                                                                    <td>{instantOperationInfo?.thdVab != null ? instantOperationInfo.thdVab : "-"}</td>
                                                                    <td>{instantOperationInfo?.thdVbc != null ? instantOperationInfo.thdVbc : "-"}</td>
                                                                    <td>{instantOperationInfo?.thdVca != null ? instantOperationInfo.thdVca : "-"}</td>
                                                                    <td>{instantOperationInfo?.thdVan != null ? instantOperationInfo.thdVan : "-"}</td>
                                                                    <td>-</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">THD I [%]</th>
                                                                    <td>{instantOperationInfo?.thdIa != null ? instantOperationInfo.thdIa : "-"}</td>
                                                                    <td>{instantOperationInfo?.thdIb != null ? instantOperationInfo.thdIb : "-"}</td>
                                                                    <td>{instantOperationInfo?.thdIc != null ? instantOperationInfo.thdIc : "-"}</td>
                                                                    <td>{instantOperationInfo?.thdIn != null ? instantOperationInfo.thdIn : "-"}</td>
                                                                    <td>-</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">Temp [°C]</th>
                                                                    <td>{instantOperationInfo?.t1 != null ? instantOperationInfo.t1 : "-"}</td>
                                                                    <td>{instantOperationInfo?.t2 != null ? instantOperationInfo.t2 : "-"}</td>
                                                                    <td>{instantOperationInfo?.t3 != null ? instantOperationInfo.t3 : "-"}</td>
                                                                    <td>-</td>
                                                                    <td>-</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">E<sub>P</sub> [kWh]</th>
                                                                    <td colSpan={5}>{instantOperationInfo?.ep != null ? instantOperationInfo.ep : "-"}</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">E<sub>Q</sub> [kVArh]</th>
                                                                    <td colSpan={5}>{instantOperationInfo?.eq != null ? instantOperationInfo.eq : "-"}</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">Trạng Thái MCCB</th>
                                                                    <td colSpan={5}></td>
                                                                </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                    <div className="modal-footer">
                                                        <button type="button" className="btn btn-outline-primary" onClick={() => $('#model-' + (index + 1)).hide()}>Đóng</button>
                                                        <button className="btn btn-primary" onClick={() => {
                                                            history.push("/home/load/" + param.customerId + "/" + param.projectId + "/device-information/" + power.deviceId)
                                                        }}>Chi tiết</button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            )
                        }
                    </tbody>
                </table>
                <div className="tab-chart">
                    <div className="form-group mt-2 mb-0 ml-2">
                        <div className="input-group float-left mr-1" style={{ width: "270px" }}>
                            <div className="input-group-prepend">
                                <span className="input-group-text pickericon">
                                    <span className="far fa-calendar"></span>
                                </span>
                            </div>
                            <Calendar
                                id="from-value"
                                className="celendar-picker"
                                dateFormat="yy-mm-dd"
                                value={fromDate}
                                onChange={e => setFromDate(e.value)}
                            />
                        </div>
                        <div className="input-group float-left mr-1" style={{ width: "270px" }}>
                            <div className="input-group-prepend">
                                <span className="input-group-text pickericon">
                                    <span className="far fa-calendar"></span>
                                </span>
                            </div>
                            <Calendar
                                id="to-value"
                                className=""
                                dateFormat="yy-mm-dd"
                                value={toDate}
                                onChange={e => setToDate(e.value)}
                            />
                        </div>
                        <div>
                            <button type="button" className="btn btn-outline-secondary mr-1" onClick={() => getDataChart()}>
                                <i className="fa-solid fa-magnifying-glass"></i>
                            </button>
                            <button type="button" className="btn btn-outline-secondary" onClick={exportToExcel} >
                                {
                                    !statusDownload &&
                                    <i className="fa-solid fa-download" />
                                }
                                {
                                    statusDownload &&
                                    <i className="fa-solid fa-down-long icon-aniamation-download" />
                                }
                            </button>
                        </div>
                    </div>
                    <div className="loading" id="loading" style={{ marginLeft: "50%" }}>
                        <img height="60px" src="/resources/image/loading.gif" alt="loading" />
                    </div>
                    <div id="chartdiv" style={{ height: "420px" }}></div>

                </div>
            </div>
        </>
    );
}

export default OverviewLoad;