import React from "react";
import overviewPVService from "../../../../../services/OverviewPVService";
import OperationInformationService from "../../../../../services/OperationInformationService";
import * as am5 from "@amcharts/amcharts5";
import * as am5xy from "@amcharts/amcharts5/xy";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";
import { useParams } from "react-router";
import { useState } from "react";
import { useEffect } from "react";
import { Link, useHistory } from "react-router-dom";
import moment from 'moment';
import $ from "jquery";
import CONS from "../../../../../constants/constant";
import { Calendar } from 'primereact/calendar';
import authService from "../../../../../services/AuthService";


const OverviewSolar = ({ projectInfo }) => {
    const param = useParams();
    const history = useHistory();
    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [viewData, setViewData] = useState([]);

    let date = new Date();

    let dateStr =
        ("00" + date.getDate()).slice(-2) + "-" +
        ("00" + (date.getMonth() + 1)).slice(-2) + "-" +
        date.getFullYear() + " " +
        ("00" + date.getHours()).slice(-2) + ":" +
        ("00" + date.getMinutes()).slice(-2)

    const [powers, setPower] = useState([]);
    const [powersWarning, setPowersWarning] = useState([]);
    const [powerDef, setPowerDef] = useState([]);
    const [state, setState] = useState(0);
    const [totalEnergy, setTotalEnergy] = useState(0);
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

    const getPowers = async () => {
        let keyword = document.getElementById('keyword').value;
        let res = await overviewPVService.getPower(param.customerId, param.projectId, keyword);
        if (res.status === 200) {
            console.log(res.data);
            setPower(res.data);
            setPowersWarning(res.data);
            setPowerDef(res.data)
        }
    };

    const handleSetViewTypeChart = (data) => {
        let values = [];

        data.forEach(item => {
            if (item.energy && item.energy > 0) {
                values.push(item.energy);
            }
            if (item.power && item.power > 0) {
                values.push(item.power);
            }
            if (item.forecast && item.forecast > 0) {
                values.push(item.forecast);
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
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let res = await overviewPVService.exportExcel(param.customerId, param.projectId, fDate, tDate, authService.getUserName());
        if (res.status === 501) {
            $.alert("Không có dữ liệu");
        }
    }

    const getDataChart = async () => {
        $('#chartdiv').hide();
        $('#loading').show();
        let fDate = moment(fromDate).format("YYYY-MM-DD");
        let tDate = moment(toDate).format("YYYY-MM-DD");
        let res = await overviewPVService.getDataChart(param.customerId, param.projectId, fDate, tDate);
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
                opposite: false
            });

            let yAxis = chart.yAxes.push(
                am5xy.ValueAxis.new(root, {
                    maxDeviation: 1,
                    min: 0,
                    renderer: yRenderer
                })
            );
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
                    fill: am5.color(0xA7C942)
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
                    maxDeviation: 1,
                    min: 0,
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
                        fill: am5.color("#FF0000"),
                        strokeWidth: 3,
                        radius: 5,
                    })
                });
            });

            if (parseInt(state) === 1) {
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
                            fill: am5.color(0xFCD202),
                            strokeWidth: 3,
                            radius: 5,
                        })
                    });
                });
            }

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
            series1.appear();

            xAxis.events.once("datavalidated", function (ev) {
                ev.target.zoomToIndexes(data.length - 20, data.length);
            });

        });
    };
    const getOperationInformation = async (deviceId, deviceType, customerId) => {
        setViewData([])
        let res = null;
        if (deviceType === 1) {
            res = await OperationInformationService.getInstantOperationInverterPV(customerId, deviceId);
            if (res.status === 200) {
                setInstantOperationInfo(res.data);
                // convert
            }
        } else if (deviceType === 2) {
            res = await OperationInformationService.getInstantOperationWeatherPV(customerId, deviceId);
            if (res.status === 200) {
                setViewData(res.data);
            }
        } else if (deviceType === 3) {
            res = await OperationInformationService.getInstantOperationCombinerPV(customerId, deviceId);
            if (res.status === 200) {
                setViewData(res.data);
            }
        } else if (deviceType === 4) {
            res = await OperationInformationService.getInstantOperationStringPV(customerId, deviceId);
            if (res.status === 200) {
                setViewData(res.data);
            }
        } else if (deviceType === 5) {
            res = await OperationInformationService.getInstantOperationPanelPV(customerId, deviceId);
            if (res.status === 200) {
                setViewData(res.data);

            }
        }


    };
    const warningTo = () => {
        history.push({
            pathname: "/home/solar/" + param.customerId + "/" + param.projectId + "/warning",
            state: {
                status: 200,
                message: "warning_all"
            }
        }
        );
    }

    const getDeviceByError = () => {
        let deviceError = powerDef?.filter(d => {
            return d.loadStatus.includes("error")
        })
        setPower(deviceError);
    }

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

    const getTotalEnergy = async () => {
        let res = await overviewPVService.getTotalEnergy(param.customerId, param.projectId);
        if (res.status === 200) {
            console.log(res.data);
            setTotalEnergy(res.data);
        }
    }

    useEffect(() => {
        document.title = "Overview PV";
        getPowers();
        getDataChart();
        getTotalEnergy();
    }, [param.projectId]);

    return (
        <>
            <div className="tab-content">
                <div className="tab-title load-title mt-2" style={{ display: "flex", height: "35px" }}>
                    <div className="project" style={{ display: "flex", flexDirection: "column" }}>
                        <div className="latest-time" style={{ padding: "0px 10px", display: "block" }}>
                            <i className="fa-regular fa-clock"></i>&nbsp; {dateStr}
                        </div>
                        <div className="project-infor" style={{ padding: "0px 10px", display: "block", marginTop: "10px" }}>
                            <span className="project-tree">{projectInfo}</span>
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
                            <button type="button" className="btn btn-danger w-180px ml-2" onClick={() => getDeviceByError()}>
                                <i className="fa-solid fa-bug"></i> &nbsp;Mất tín hiệu: {countError.length}
                            </button>
                            <button type="button" className="btn btn-warning w-150px ml-2" onClick={() => warningTo()} >
                                <i className="fa-solid fa-triangle-exclamation"></i> &nbsp;Cảnh báo: {countWarning.length}

                            </button>
                        </div>
                    </div>
                </div>

                <table className="table" style={{ width: "calc(100% - 20px)", marginLeft: "10px" }}>
                    <tbody>
                        <tr>
                            <th style={{ width: "65px" }}>Bức xạ</th>
                            <td>{totalEnergy.rad ? totalEnergy.rad : 0} [W/m²]</td>
                            <th style={{ width: "120px" }}>Nhiệt độ tấm pin</th>
                            <td>{totalEnergy.temp ? totalEnergy.tstr : 0} [°C]</td>
                            <th style={{ width: "80px" }}>Nhiệt độ</th>
                            <td>{totalEnergy.temp ? totalEnergy.temp : 0} [°C]</td>
                        </tr>
                    </tbody>
                </table>

                <table className="table tbl-overview tbl-tsd">
                    <thead>
                        <tr>
                            <th colSpan={7} className="tbl-title">Sản lượng điện năng [kWh]</th>
                        </tr>
                    </thead>
                    <tbody>
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
                            <td className="text-right">{totalEnergy.wh ? totalEnergy.wh : 0}</td>
                            <td className="text-right">{totalEnergy.whPrevDay ? totalEnergy.whPrevDay : 0}</td>
                            <td className="text-right">{totalEnergy.whDay ? totalEnergy.whDay : 0}</td>
                            <td className="text-right">{totalEnergy.whPrevMonth ? totalEnergy.whPrevMonth : 0}</td>
                            <td className="text-right">{totalEnergy.whMonth ? totalEnergy.whMonth : 0}</td>
                            <td className="text-right">{totalEnergy.whPrevYear ? totalEnergy.whPrevYear : 0}</td>
                            <td className="text-right">{totalEnergy.whYear ? totalEnergy.whYear : 0}</td>
                        </tr>
                    </tbody>
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
                    <thead style={powers?.length > 5 ? { display: "table", width: "calc(100% - 10px)", tableLayout: "fixed" } : { display: "table", width: "100%", tableLayout: "fixed" }}>
                        <tr>
                            <th width="40px">TT</th>
                            <th>Thành Phần</th>
                            <th width="150px">P <sub>AC</sub> [kW]</th>
                            <th width="150px">P <sub>DC</sub> [kW]</th>
                            <th width="150px">Hiệu suất [%]</th>
                            <th width="150px">Trạng Thái</th>
                            <th width="250px">Vị trí</th>
                            <th width="70px"><i className="fa-regular fa-hand"></i></th>
                        </tr>
                    </thead>
                    <tbody style={{ display: "block", maxHeight: "115px", overflow: "auto" }}>
                        {
                            powers?.map(
                                (power, index) => <tr key={index} style={{ display: "table", width: "100%", tableLayout: "fixed" }}>
                                    <td width="40px" style={{ textAlign: "center" }}>{index + 1}</td>
                                    <td>{power.deviceName}</td>
                                    {power.w != null ? <td width="150px" style={{ textAlign: "center" }}>{power.w}</td> : <td width="150px" style={{ textAlign: "center" }}>-</td>}
                                    {power.dcw != null ? <td width="150px" style={{ textAlign: "center" }}>{power.dcw}</td> : <td width="150px" style={{ textAlign: "center" }}>-</td>}
                                    {power.efficiency ? <td width="150px" style={{ textAlign: "center" }}>{(power.efficiency).toFixed(1)}</td> : <td width="150px" style={{ textAlign: "center" }}>-</td>}
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
                                            <td width="250px">
                                                <Link to={`/home/solar/${param.customerId}/${power.projectId}/systemMap/${power.systemMapId}?deviceId=${power.deviceId}`}>
                                                    Layer {power.layer} {`>`} {power.systemMapName}
                                                </Link>
                                            </td>
                                            : <td width="250px" style={{ textAlign: "center" }} >-</td>
                                    }

                                    <td width="70px" style={{ padding: "3px 15px" }} >
                                        <a className="button-icon text-left" data-toggle="modal" data-target={"#model-" + (index + 1)} onClick={() => getOperationInformation(power.deviceId, power.deviceType, param.customerId)}>
                                            <img height="16px" src="/resources/image/icon-info.png" title="View Info" alt="view-info" />
                                        </a>
                                        {
                                            power.systemMapId > 0 &&
                                            <Link to={`/home/solar/${param.customerId}/${power.projectId}/systemMap/${power.systemMapId}?deviceId=${power.deviceId}`} className="button-icon float-right" title="View Grid">
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
                                                    {(power.deviceType === 1) &&
                                                        <div className="modal-body">
                                                            <table className="table text-center tbl-overview table-oper-info-tool">
                                                                <thead>
                                                                    <tr>
                                                                        <th style={{ backgroundColor: "white" }}><i className="fa-regular fa-clock mr-1" style={{ color: "revert" }}></i>{instantOperationInfo?.sentDate != null ? moment(instantOperationInfo.sentDate).format(CONS.DATE_FORMAT_OPERATE) : ""}</th>
                                                                        <th width="100px">Phase A</th>
                                                                        <th width="100px">Phase B</th>
                                                                        <th width="100px">Phase C</th>
                                                                        <th width="100px">Total</th>
                                                                    </tr>
                                                                </thead>
                                                                <tbody>
                                                                    <tr>
                                                                        <th scope="row">U<sub>AC</sub> [V]</th>
                                                                        <td>{instantOperationInfo?.va != null ? instantOperationInfo.va : "-"}</td>
                                                                        <td>{instantOperationInfo?.vb != null ? instantOperationInfo.vb : "-"}</td>
                                                                        <td>{instantOperationInfo?.vc != null ? instantOperationInfo.vc : "-"}</td>
                                                                        <td>-</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">I<sub>AC</sub> [A]</th>
                                                                        <td>{instantOperationInfo?.ia != null ? instantOperationInfo.ia : "-"}</td>
                                                                        <td>{instantOperationInfo?.ib != null ? instantOperationInfo.ib : "-"}</td>
                                                                        <td>{instantOperationInfo?.ic != null ? instantOperationInfo.ic : "-"}</td>
                                                                        <td>-</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">P<sub>AC</sub> [kW]</th>
                                                                        <td>{instantOperationInfo?.pa != null ? instantOperationInfo.pa : "-"}</td>
                                                                        <td>{instantOperationInfo?.pb != null ? instantOperationInfo.pb : "-"}</td>
                                                                        <td>{instantOperationInfo?.pc != null ? instantOperationInfo.pc : "-"}</td>
                                                                        <td>{instantOperationInfo?.ptotal != null ? instantOperationInfo?.ptotal : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">U<sub>DC</sub> [V]</th>
                                                                        <td>-</td>
                                                                        <td>-</td>
                                                                        <td>-</td>
                                                                        <td>{instantOperationInfo?.udc != null ? instantOperationInfo?.udc : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">I<sub>DC</sub> [A]</th>
                                                                        <td>-</td>
                                                                        <td>-</td>
                                                                        <td>-</td>
                                                                        <td>{instantOperationInfo?.idc != null ? instantOperationInfo?.idc : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">P<sub>DC</sub> [kW]</th>
                                                                        <td>-</td>
                                                                        <td>-</td>
                                                                        <td>-</td>
                                                                        <td>{instantOperationInfo?.pdc != null ? instantOperationInfo?.pdc : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">PF</th>
                                                                        <td>{instantOperationInfo?.pfa != null ? instantOperationInfo?.pfa : "-"}</td>
                                                                        <td>{instantOperationInfo?.pfb != null ? instantOperationInfo?.pfb : "-"}</td>
                                                                        <td>{instantOperationInfo?.pfc != null ? instantOperationInfo?.pfc : "-"}</td>
                                                                        <td>{instantOperationInfo?.pf != null ? instantOperationInfo?.pf : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">Hiệu suất [%]</th>
                                                                        <td>-</td>
                                                                        <td>-</td>
                                                                        <td>-</td>
                                                                        <td>{instantOperationInfo?.pdc != null && instantOperationInfo?.pdc != 0 && instantOperationInfo?.ptotal != null ?(instantOperationInfo?.ptotal / instantOperationInfo?.pdc ).toFixed(1)  : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">Yield [kWh]</th>
                                                                        <td>-</td>
                                                                        <td>-</td>
                                                                        <td>-</td>
                                                                        <td>{instantOperationInfo?.ep != null ? instantOperationInfo?.ep : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">Frequency [Hz]</th>
                                                                        <td>-</td>
                                                                        <td>-</td>
                                                                        <td>-</td>
                                                                        <td>{instantOperationInfo?.hz != null ? instantOperationInfo?.hz : "-"}</td>
                                                                    </tr>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    }
                                                    {(power.deviceType === 2) &&
                                                        <div className="modal-body">
                                                            <table className="table text-center tbl-overview table-oper-info-tool">
                                                                <thead>
                                                                    <tr>
                                                                        <th style={{ backgroundColor: "white" }}><i className="fa-regular fa-clock mr-1" style={{ color: "revert" }}></i>{viewData?.sentDate != null ? moment(viewData.sentDate).format(CONS.DATE_FORMAT_OPERATE) : ""}</th>
                                                                        <th width="350px">Giá trị</th>
                                                                    </tr>
                                                                </thead>
                                                                <tbody>
                                                                    <tr>
                                                                        <th scope="row">Nhiệt độ môi trường [°C]</th>
                                                                        <td>{viewData?.temp != null ? viewData?.temp : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">Bức xạ [W/m²]</th>
                                                                        <td>{viewData?.rad != null ? viewData?.rad : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">Độ ẩm [%]</th>
                                                                        <td>{viewData?.h != null ? viewData?.h : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">Tốc độ gió [m/s]</th>
                                                                        <td>{viewData?.wind_sp != null ? viewData?.wind_sp : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">Áp suất [atm]</th>
                                                                        <td>{viewData?.atmos != null ? viewData?.atmos : "-"}</td>
                                                                    </tr>
                                                                </tbody>
                                                            </table>
                                                        </div>

                                                    }
                                                    {(power.deviceType === 3) &&
                                                        <div className="modal-body">
                                                            <table className="table text-center tbl-overview table-oper-info-tool">
                                                                <thead>
                                                                    <tr>
                                                                        <th style={{ backgroundColor: "white" }}><i className="fa-regular fa-clock mr-1" style={{ color: "revert" }}></i>{viewData?.sentDate != null ? moment(viewData.sentDate).format(CONS.DATE_FORMAT_OPERATE) : ""}</th>
                                                                        <th width="350px">Giá trị</th>
                                                                    </tr>
                                                                </thead>
                                                                <tbody>
                                                                    <tr>
                                                                        <th scope="row">U<sub>DC</sub> [V]</th>
                                                                        <td>{viewData?.vdcCombiner != null ? viewData?.vdcCombiner : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">I<sub>DC</sub> [A]</th>
                                                                        <td>{viewData?.idcCombiner != null ? viewData?.idcCombiner : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">P [kW]</th>
                                                                        <td>{viewData?.pdcCombiner != null ? viewData?.pdcCombiner : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">E [kWh]</th>
                                                                        <td>{viewData?.epCombiner != null ? viewData?.epCombiner : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">Temp [°C]</th>
                                                                        <td>{viewData?.t != null ? viewData?.t : "-"}</td>
                                                                    </tr>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    }
                                                    {(power.deviceType === 4) &&
                                                        <div className="modal-body">
                                                            <table className="table text-center tbl-overview table-oper-info-tool">
                                                                <thead>
                                                                    <tr>
                                                                        <th style={{ backgroundColor: "white" }}><i className="fa-regular fa-clock mr-1" style={{ color: "revert" }}></i>{viewData?.sentDate != null ? moment(viewData.sentDate).format(CONS.DATE_FORMAT_OPERATE) : ""}</th>
                                                                        <th width="350px">Giá trị</th>
                                                                    </tr>
                                                                </thead>
                                                                <tbody>
                                                                    <tr>
                                                                        <th scope="row">U<sub>DC</sub> [V]</th>
                                                                        <td>{viewData?.vdcStr != null ? viewData?.vdcStr : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">I<sub>DC</sub> [A]</th>
                                                                        <td>{viewData?.idcStr != null ? viewData?.idcStr : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">P [kW]</th>
                                                                        <td>{viewData?.pdcStr != null ? viewData?.pdcStr : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">E [kWh]</th>
                                                                        <td>{viewData?.epStr != null ? viewData?.epStr : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">Temp [°C]</th>
                                                                        <td>{viewData?.tstr != null ? viewData?.tstr : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">Hiệu suất [%]</th>
                                                                        <td>-</td>
                                                                    </tr>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    }
                                                    {(power.deviceType === 5) &&
                                                        <div className="modal-body">
                                                            <table className="table text-center tbl-overview table-oper-info-tool">
                                                                <thead>
                                                                    <tr>
                                                                        <th style={{ backgroundColor: "white" }}><i className="fa-regular fa-clock mr-1" style={{ color: "revert" }}></i>{viewData?.sentDate != null ? moment(viewData.sentDate).format(CONS.DATE_FORMAT_OPERATE) : ""}</th>
                                                                        <th width="350px">Giá trị</th>
                                                                    </tr>
                                                                </thead>
                                                                <tbody>
                                                                    <tr>
                                                                        <th scope="row">U<sub>DC</sub> [V]</th>
                                                                        <td>{viewData?.u != null ? viewData?.u : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">I<sub>DC</sub> [A]</th>
                                                                        <td>{viewData?.i != null ? viewData?.i : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">P [kW]</th>
                                                                        <td>{viewData?.p != null ? viewData?.p : "-"}</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">E [kWh]</th>
                                                                        <td>-</td>
                                                                    </tr>
                                                                    <tr>
                                                                        <th scope="row">Temp [°C]</th>
                                                                        <td>{viewData?.t != null ? viewData?.t : "-"}</td>
                                                                    </tr>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                    }
                                                    <div className="modal-footer">
                                                        <button type="button" className="btn btn-outline-primary" onClick={() => $('#model-' + (index + 1)).hide()}>Đóng</button>
                                                        <button className="btn btn-primary" onClick={() => {
                                                            history.push("/home/solar/" + param.customerId + "/" + param.projectId + "/device-information/" + power.deviceId)
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
                    <div className="form-group mt-2 mb-0 ml-2" >
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
                                <i className="fa-solid fa-download"></i>
                            </button>
                        </div>
                    </div>
                    <div className="loading" id="loading" style={{ marginLeft: "50%" }}>
                        <img height="60px" src="/resources/image/loading.gif" alt="loading" />
                    </div>
                    <div id="chartdiv" style={{ height: "380px" }}></div>

                </div>
            </div>
        </>
    );
}
export default OverviewSolar;
