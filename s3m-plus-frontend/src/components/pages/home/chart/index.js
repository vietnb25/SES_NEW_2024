import moment from "moment";
import React, { useEffect, useState } from "react";
import { Calendar } from 'primereact/calendar';
import { useParams, useLocation, useHistory, Link } from 'react-router-dom';
import HomeService from "../../../../services/HomeService";
// chart
import * as am5 from "@amcharts/amcharts5";
import * as am5xy from "@amcharts/amcharts5/xy";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";
import * as am5percent from "@amcharts/amcharts5/percent";
import converter from "../../../../common/converter";
import DataLoadFrame1Service from "../../../../services/DataLoadFrame1Service";
import AuthService from "../../../../services/AuthService";
import CONS from "../../../../constants/constant";

const $ = window.$;

const ChartHome = () => {
    const { customerId, projectId } = useParams();
    const location = useLocation();
    const history = useHistory();

    const [chartType, setChartType] = useState(3);
    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [noData, setNoData] = useState(false);
    const [project, setProject] = useState([]);
    const [viewTypeTable, setViewTypeTable] = useState(null);
    const [dataLoadFrame1, setDataLoadFrame1] = useState([]);
    const { sentDate } = dataLoadFrame1;
    const [waitingDownload, setDownload] = useState(false);
    const [projectInfo, setProjectInfor] = useState();

    const getDataSystemType = async () => {
        let res = await HomeService.getSystemTypeByProject(customerId, projectId);
        if (res.status === 200) {
            setProject(res.data);
            let data = res.data;
            setProjectInfor(data[0].jsonData);
        }
    }

    const getDateNewDevice = async () => {
        let res = await DataLoadFrame1Service.getDateNewDevice(customerId);
        if (res.status === 200) {
            setDataLoadFrame1(res.data);
        }
    }

    const getDataChart = async () => {
        $('#title').hide();
        $('#loading').show();
        let toDay = toDate.getDate() >= 10 ? toDate.getDate() : `0${toDate.getDate()}`;
        let toMonth = toDate.getMonth() + 1 < 10 ? `0${toDate.getMonth() + 1}` : toDate.getMonth() + 1;
        let toYear = toDate.getFullYear();
        let fromDay = fromDate.getDate() >= 10 ? fromDate.getDate() : `0${fromDate.getDate()}`;
        let fromMonth = fromDate.getMonth() + 1 < 10 ? `0${fromDate.getMonth() + 1}` : fromDate.getMonth() + 1;
        let fromYear = fromDate.getFullYear();
        let _fromDate = "";
        let _toDate = "";
        if (chartType === 3) {
            _fromDate = `${fromYear}-${fromMonth}-${fromDay}`;
            _toDate = `${toYear}-${toMonth}-${toDay}`;
        } else if (chartType === 2) {
            _fromDate = `${fromYear}-${fromMonth}`;
            _toDate = `${toYear}-${toMonth}`;
        } else if (chartType === 1) {
            _fromDate = `${fromYear}`;
            _toDate = `${toYear}`;
        }

        let data = {
            timeType: chartType,
            customerId,
            projectId,
            fromDate: _fromDate,
            toDate: _toDate
        }
        let res = await HomeService.drawChart(data);

        if (res.status === 200 && parseInt(res.data.length) > 0) {
            $('#loading').hide();
            $('#title').show();
            let data = res.data;
            let typeValue = handleSetViewTypeChart(data);
            drawChart(data, typeValue);
            drawChartPie(data);
            setNoData(false);
        } else {
            $('#title').hide();
            $('#loading').hide();
            setNoData(true);
            return;
        }
    }

    const handleSetViewTypeChart = (data) => {
        let values = [];

        data.forEach(item => {
            if (item.ev && item.ev > 0) {
                values.push(item.ev);
            }
            if (item.load && item.load > 0) {
                values.push(item.load);
            }
            if (item.pv && item.pv > 0) {
                values.push(item.pv);
            }
            if (item.grid && item.grid > 0) {
                values.push(item.grid);
            }
            if (item.wind && item.wind > 0) {
                values.push(item.wind);
            }
        });

        let min = Math.min(...values);

        let typeValue = converter.setViewType(values.length > 0 ? min : 0)

        return typeValue;
    }

    const drawChart = (data, typeValue) => {
        //convert data before drawing chart
        data.forEach(item => {
            if (item.ev && item.ev > 0) {
                item.ev = item.ev;
            }
            if (item.load && item.load > 0) {
                item.load = item.load;
            }
            if (item.pv && item.pv > 0) {
                item.pv = item.pv;
            }
            if (item.grid && item.grid > 0) {
                item.grid = item.grid;
            }
            if (item.wind && item.wind > 0) {
                item.wind = item.wind;
            }
        });
        
        am5.array.each(am5.registry.rootElements, function (root) {
            if (root) {
                if (root.dom.id === "chart") {
                    root.dispose();
                }
            }
        });

        // draw chart
        am5.ready(function () {
            let root = am5.Root.new("chart");
            root.setThemes([am5themes_Animated.new(root)]);
            // create chart object
            let chart = root.container.children.push(
                am5xy.XYChart.new(root, {
                    panX: true,
                    panY: false,
                    wheelX: "panX",
                    layout: root.verticalLayout
                })
            );
            // scroll bar
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

            let xAxisRenderer = am5xy.AxisRendererX.new(root, {});

            xAxisRenderer.labels.template.setAll({
                rotation: -30,
            });

            let xAxis = chart.xAxes.push(
                am5xy.CategoryAxis.new(root, {
                    categoryField: "time",
                    renderer: xAxisRenderer,
                    // renderer: am5xy.AxisRendererX.new(root, { minGridDistance: 10 }),
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

            yAxis.children.moveValue(am5.Label.new(root, { text: `Điện năng [${converter.convertLabelElectricPower(typeValue, "Wh")}]`, rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);
            // xAxis.children.moveValue(am5.Label.new(root, { text: "Thời gian", rotation: 0, y: am5.p50, centerX: am5.p50 }), 0);

            let series1 = chart.series.push(
                am5xy.ColumnSeries.new(root, {
                    name: "LOAD",
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: "load",
                    categoryXField: "time",
                    tooltip: am5.Tooltip.new(root, {
                        pointerOrientation: "horizontal",
                        labelText: `LOAD: {valueY} {info} [${converter.convertLabelElectricPower(typeValue, "Wh")}]`
                    }),
                    fill: am5.color("#0072BD")
                })
            );

            let yFill = yAxis.makeDataItem({
                value: 0,
            });

            let yFillLine = yAxis.createAxisRange(yFill);
            yFillLine.get("grid").setAll({
                forceHidden: false,
                strokeOpacity: 1,
                strokeWidth: 1,
                stroke: am5.color(0x9E9E9E)
            });

            yRenderer.grid.template.set("strokeOpacity", 0.05);
            yRenderer.labels.template.set("fill", am5.color(0x9E9E9E));
            yRenderer.setAll({
                stroke: am5.color(0x9E9E9E),
                strokeOpacity: 1,
                opacity: 1
            });

            series1.columns.template.setAll({
                tooltipY: am5.percent(10)
            });

            series1.data.setAll(data);

            let series2 = chart.series.push(
                am5xy.ColumnSeries.new(root, {
                    name: "Grid",
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: "grid",
                    categoryXField: "time",
                    tooltip: am5.Tooltip.new(root, {
                        pointerOrientation: "horizontal",
                        labelText: `Grid: {valueY} {info}[${converter.convertLabelElectricPower(typeValue, "Wh")}]`
                    }),
                    fill: am5.color("#D95319")
                })
            );


            series2.columns.template.setAll({
                tooltipY: am5.percent(10)
            });

            series2.data.setAll(data);

            let series3 = chart.series.push(
                am5xy.ColumnSeries.new(root, {
                    name: "PV",
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: "pv",
                    categoryXField: "time",
                    tooltip: am5.Tooltip.new(root, {
                        pointerOrientation: "horizontal",
                        labelText: `PV: {valueY} {info}[${converter.convertLabelElectricPower(typeValue, "Wh")}]`
                    }),
                    fill: am5.color("#EDB120")
                })
            );

            series3.columns.template.setAll({
                tooltipY: am5.percent(10)
            });

            series3.data.setAll(data);

            let series4 = chart.series.push(
                am5xy.ColumnSeries.new(root, {
                    name: "Wind",
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: "wind",
                    categoryXField: "time",
                    tooltip: am5.Tooltip.new(root, {
                        pointerOrientation: "horizontal",
                        labelText: `Wind: {valueY} {info}[${converter.convertLabelElectricPower(typeValue, "Wh")}]`
                    }),
                    fill: am5.color("#77AC30")
                })
            );

            series4.columns.template.setAll({
                tooltipY: am5.percent(10)
            });

            series4.data.setAll(data);

            let series5 = chart.series.push(
                am5xy.ColumnSeries.new(root, {
                    name: "Battery",
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: "ev",
                    categoryXField: "time",
                    tooltip: am5.Tooltip.new(root, {
                        pointerOrientation: "horizontal",
                        labelText: `Battery: {valueY} {info}[${converter.convertLabelElectricPower(typeValue, "Wh")}]`
                    }),
                    fill: am5.color("#7E2F8E")
                })
            );

            series5.columns.template.setAll({
                tooltipY: am5.percent(10)
            });

            series5.data.setAll(data);

            // Add cursor
            // https://www.amcharts.com/docs/v5/charts/xy-chart/cursor/
            chart.set("cursor", am5xy.XYCursor.new(root, {}));

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

            // xAxis.events.once("datavalidated", function (ev) {
            //     ev.target.zoomToIndexes(data.length - 20, data.length);
            // });
        });
    }

    const drawChartPie = (data) => {
        let load = 0;
        let pv = 0;
        let wind = 0;
        let grid = 0;
        let ev = 0;

        data.forEach((item, index) => {
            load += item.load ? item.load : 0;
            pv += item.pv ? item.pv : 0;
            wind += item.wind ? item.wind : 0;
            grid += item.grid ? item.grid : 0;
            ev += item.ev ? item.ev : 0;
        });

        let dataChart = [{
            name: "LOAD",
            value: load,
            color: am5.color("#0072BD")
        }, {
            name: "Grid",
            value: grid,
            color: am5.color("#D95319")
        }, {
            name: "PV",
            value: pv,
            color: am5.color("#EDB120")
        }, {
            name: "Wind",
            value: wind,
            color: am5.color("#77AC30")
        }, {
            name: "Battery",
            value: ev,
            color: am5.color("#7E2F8E")
        }];

        am5.array.each(am5.registry.rootElements, function (root) {
            if (root) {
                if (root.dom.id === "chart2") {
                    root.dispose();
                }
            }
        });

        // Create root and chart
        let root = am5.Root.new("chart2");
        let chart = root.container.children.push(
            am5percent.PieChart.new(root, {
                layout: root.verticalLayout
            })
        );

        let seriesLoad = chart.series.push(
            am5percent.PieSeries.new(root, {
                name: "Chart",
                valueField: "value",
                categoryField: "name",
                fillField: "color"
            })
        );

        seriesLoad.data.setAll(dataChart);


        // Add legend
        let legend = chart.children.push(am5.Legend.new(root, {
            centerX: am5.percent(50),
            x: am5.percent(50),
            layout: root.horizontalLayout
        }));

        legend.data.setAll(seriesLoad.dataItems);
    }

    const handleChangeChartType = (value) => {
        setChartType(parseInt(value));
    }

    const handleRedirect = () => {
        history.push({
            pathname: "/home/",
            search: `?customerId=${customerId}&projectId=${projectId}`
        });
    }

    const download = async () => {
        let toDay = toDate.getDate() >= 10 ? toDate.getDate() : `0${toDate.getDate()}`;
        let toMonth = toDate.getMonth() + 1 < 10 ? `0${toDate.getMonth() + 1}` : toDate.getMonth() + 1;
        let toYear = toDate.getFullYear();
        let fromDay = fromDate.getDate() >= 10 ? fromDate.getDate() : `0${fromDate.getDate()}`;
        let fromMonth = fromDate.getMonth() + 1 < 10 ? `0${fromDate.getMonth() + 1}` : fromDate.getMonth() + 1;
        let fromYear = fromDate.getFullYear();
        let _fromDate = "";
        let _toDate = "";
        if (chartType === 3) {
            _fromDate = `${fromYear}-${fromMonth}-${fromDay}`;
            _toDate = `${toYear}-${toMonth}-${toDay}`;
        } else if (chartType === 2) {
            _fromDate = `${fromYear}-${fromMonth}`;
            _toDate = `${toYear}-${toMonth}`;
        } else if (chartType === 1) {
            _fromDate = `${fromYear}`;
            _toDate = `${toYear}`;
        }

        let data = {
            timeType: chartType,
            customerId,
            projectId,
            fromDate: _fromDate,
            toDate: _toDate,
            userName: AuthService.getUserName()
        }
        $('#download').hide();
        setDownload(true)
        let res = await HomeService.downloadDataChart(data);
        if (res.status === 200) {
            setDownload(false)
            $('#download').show();
        }
        else {
            setDownload(false)
            $('#download').show();
            $.alert("Không có dữ liệu.");
        }
    }

    const getSystemTypeName = systemTypeId => {
        let systemTypeName = "";
        switch (systemTypeId) {
            case 1:
                systemTypeName = "load";
                break;
            case 2:
                systemTypeName = "solar";
                break;
            case 3:
                systemTypeName = "load";
                break;
            case 4:
                systemTypeName = "load";
                break;
            case 5:
                systemTypeName = "grid";
                break;
            default:
                break;
        }
        return systemTypeName;
    }

    const handleRedirected = (systemTypeName) => {
        let node = $(".p-highlight").closest("li");
        let find = systemTypeName.toUpperCase();

        $("span:contains(" + find + ")").each(function () {
            let element = node.find(this).closest("div");
            $(element).click()
        });

    }

    useEffect(() => {
        document.title = "Biểu đồ";
        getDataChart();
        getDateNewDevice();
        getDataSystemType();
    }, []);

    return (
        <div className="tab-content">
            <div className="select-tab" style={{ position: "absolute", top: 0, zIndex: 1 }}>
                {
                    location.pathname.includes("/chart") &&
                    <button onClick={handleRedirect} className={`btn`} style={{ backgroundColor: "#bab8b8", color: "black", width: 85, height: 30, borderRadius: 0, padding: 0 }}>Bản đồ</button>
                }

                <button className={`btn selected`} style={{ backgroundColor: "#fff", color: "black", width: 85, height: 30, borderRadius: 0, padding: 0 }}>Biểu đồ</button>
            </div>
            <div className="tab-title" style={{ display: "inline-grid" }}>
                <div className="latest-time mt-4">
                    <i className="fa-regular fa-clock"></i>&nbsp;{moment(sentDate).format(CONS.DATE_FORMAT_OPERATE)}
                </div>
                <div className="project-infor" style={{ padding: "0px 10px", display: "block" }}>
                    <span className="project-tree">{projectInfo}</span>
                </div>

            </div>
            <table class="table tbl-overview mt-4">
                <thead>
                    <tr>
                        <th width="100px" className="text-center">TT</th>
                        <th className="text-center">Thành phần</th>
                        <th className="text-center">Số thiết bị</th>
                        <th className="text-center">Công suất [kW]</th>
                        <th className="text-center">Điện năng hôm nay [kWh]</th>
                    </tr>
                </thead>
                <tbody>
                    {
                        project.map((item, index) => (
                            <tr key={index}>
                                <td className="text-center">{index + 1}</td>
                                <td className="text-left">
                                    <Link to={"/home/" + getSystemTypeName(item.systemTypeId) + "/" + customerId + "/" + projectId} onClick={(e) => {
                                        e.preventDefault();
                                        handleRedirected(item.systemTypeName)
                                    }}>{item.systemTypeName}</Link>
                                </td>
                                <td className="text-right">{item.deviceNumber}</td>
                                <td className="text-right">{item.powerTotal }</td>
                                <td className="text-right">{item.energyTotal}</td>
                            </tr>
                        ))
                    }
                </tbody>

            </table>

            <div className="tab-chart m-0" style={{ border: "none", width: "calc(100% - 300px)" }}>
                <div className="form-group mt-4 mb-0 ml-2">
                    <div className="input-group float-left" style={{ width: "270px" }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="far fa-calendar"></span>
                            </span>
                        </div>
                        {
                            chartType === 3 &&
                            <Calendar
                                id="from-value"
                                className="celendar-picker"
                                dateFormat="yy-mm-dd"
                                value={fromDate}
                                onChange={e => setFromDate(e.value)} />
                        }
                        {
                            chartType === 2 &&
                            <Calendar
                                id="from-value"
                                className=""
                                view={"month"}
                                dateFormat="yy-mm"
                                value={fromDate}
                                onChange={e => setFromDate(e.value)} />
                        }
                        {
                            chartType === 1 &&
                            <Calendar
                                id="from-value"
                                className=""
                                view={"year"}
                                dateFormat="yy"
                                value={fromDate}
                                onChange={e => setFromDate(e.value)} />
                        }
                    </div>
                    <div className="input-group float-left" style={{ width: "270px" }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="far fa-calendar"></span>
                            </span>
                        </div>
                        {
                            chartType === 3 &&
                            <Calendar
                                id="to-value"
                                className=""
                                dateFormat="yy-mm-dd"
                                value={toDate}
                                onChange={e => setToDate(e.value)} />
                        }
                        {
                            chartType === 2 &&
                            <Calendar
                                id="to-value"
                                className=""
                                view={"month"}
                                dateFormat="yy-mm"
                                value={toDate}
                                onChange={e => setToDate(e.value)} />
                        }
                        {
                            chartType === 1 &&
                            <Calendar
                                id="to-value"
                                className=""
                                view={"year"}
                                dateFormat="yy"
                                value={toDate}
                                onChange={e => setToDate(e.value)} />
                        }
                    </div>
                    <div className="input-group float-left mr-1" style={{ width: "100px", height: 31.25 }}>
                        <select className="form-control" onChange={(e) => handleChangeChartType(e.target.value)} style={{ backgroundColor: "#FFA87D", borderRadius: 0, border: "1px solid #FFA87D" }}>
                            <option value={3}>Ngày</option>
                            <option value={2}>Tháng</option>
                            <option value={1}>Năm</option>
                        </select>
                    </div>
                    <div>
                        <button type="button" className="btn btn-outline-secondary" onClick={getDataChart} style={{ height: 31.25, padding: 0 }}>
                            <i className="fa-solid fa-magnifying-glass"></i>
                        </button>
                        <button type="button" className="btn btn-outline-warning ml-1" style={{ height: 31.25, padding: 0 }} onClick={download}>
                            <i className="fa-solid fa-download" id="download"></i>
                            {
                                waitingDownload &&
                                <span className="spinner-border spinner-border-sm" style={{ color: "#FFA87D" }} role="status" aria-hidden="true"></span>
                            }
                        </button>

                    </div>
                </div>
            </div>
            {
                noData &&
                <table className="table tbl-overview mt-3" id="no-data" style={{ width: "-webkit-fill-available" }}>
                    <tbody>
                        <tr className="w-100">
                            <td className="text-center w-100 p-2" style={{ border: "none", background: "#D5D6D1" }}> Không có dữ liệu</td>
                        </tr>
                    </tbody>
                </table>
            }
            <div className="loading" id="loading" style={{ marginLeft: "50%" }}>
                <img height="60px" src="/resources/image/loading.gif" alt="loading" />
            </div>
            <div className="dataChart" id="title" style={{ display: "flex" }}>
                <div id="chart" style={{ width: "100%", height: "345px" }}></div>
                <div id="chart2" style={{ width: "100%", height: "345px" }}></div>
            </div>
        </div>
    )
}

export default ChartHome;