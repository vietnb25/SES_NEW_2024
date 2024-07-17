import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import plantService from "../../../../services/PlantService";
import './index.css';
import authService from "../../../../services/AuthService";
import moment from "moment/moment";
import * as am5 from "@amcharts/amcharts5";
import * as am5xy from "@amcharts/amcharts5/xy";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";
import { Calendar } from 'primereact/calendar';

const MonitoringPower = () => {
    const [dateNow, setDateNow] = useState(null);
    const [listPlant, setListPlant] = useState([]);
    const location = useLocation();
    const [dateForm, setDateForm] = useState({
        fromDate: new Date(),
        toDate: new Date()
    });


    const [checkLoadingPlant, setCheckLoadingPlant] = useState(false);

    const searchPlant = async () => {
        let userName = authService.getUserName();
        let pathName = location.search.substring(1);
        let dataSend = {
            "fromDate": moment(dateForm.fromDate).format('YYYY-MM-DD'),
            "toDate": moment(dateForm.toDate).format('YYYY-MM-DD'),
            "typeScropTree": pathName,
            "userName": userName,
        }
        let response = await plantService.searchPlant(dataSend);
        console.log('data: ', response.data);

        if (response.status === 200) {
            setCheckLoadingPlant(true);
            drawChart(response.data);
        }
    }

    const list = async () => {

        let userName = authService.getUserName();
        console.log('userName', userName);
        let pathName = location.search.substring(1);
        let response = await plantService.getListPlant(pathName, userName);
        if (response.status === 200) {
            setCheckLoadingPlant(true);
            setListPlant(response.data.Plants);
            drawChart(response.data.chartData);
        }
        console.log('response: ', response.data.chartData);
    }

    const drawChart = (dataChart) => {
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
            let xRenderer = am5xy.AxisRendererX.new(root, { minGridDistance: 80 });
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
                opposite: false,
                strokeOpacity: 1
            });


            let yAxis = chart.yAxes.push(
                am5xy.ValueAxis.new(root, {
                    maxDeviation: 1,
                    min: 0,
                    renderer: yRenderer
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
            yAxis.children.moveValue(am5.Label.new(root, { text: "Energy(kWh)", rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);

            let series1 = chart.series.push(
                am5xy.ColumnSeries.new(root, {
                    name: "Sản Lượng Công Suất",
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: "wh",
                    categoryXField: "time",
                    tooltip: am5.Tooltip.new(root, {
                        pointerOrientation: "horizontal",
                        labelText: "{valueY} {info}"
                    }),
                    fill: am5.color(0x3399CC)
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
            yAxis2.children.moveValue(am5.Label.new(root, { text: "Power(kW)", rotation: 90, y: am5.p50, centerX: am5.p50 }), 1);

            // Add series
            // https://www.amcharts.com/docs/v5/charts/xy-chart/series/

            let series2 = chart.series.push(
                am5xy.LineSeries.new(root, {
                    name: "Công Suất Hiện Tại",
                    xAxis: xAxis,
                    yAxis: yAxis2,
                    valueYField: "w",
                    categoryXField: "time",
                    tooltip: am5.Tooltip.new(root, {
                        pointerOrientation: "horizontal",
                        labelText: "{valueY} {info}"
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

            //

            let series3 = chart.series.push(
                am5xy.LineSeries.new(root, {
                    name: "Công Suất Cho Phép",
                    xAxis: xAxis,
                    yAxis: yAxis2,
                    valueYField: "congSuatChoPhep",
                    categoryXField: "time",
                    tooltip: am5.Tooltip.new(root, {
                        pointerOrientation: "horizontal",
                        labelText: "{valueY} {info}"
                    }),
                    stroke: am5.color(0x00FF00)
                })
            );

            series3.strokes.template.setAll({
                strokeWidth: 1,
                templateField: "strokeSettings",
                strokeWidth: 3,
                strokeDasharray: [10, 5, 2, 5]
            });
            series3.set("fill", am5.color("#FF0000"));

            yRenderer.grid.template.set("strokeOpacity", 0.05);
            yRenderer.labels.template.set("fill", series3.get("fill"));
            yRenderer.setAll({
                stroke: series3.get("fill"),
                strokeOpacity: 1,
                opacity: 1
            });

            series3.data.setAll(data);


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
                ev.target.zoomToIndexes(dataChart.length - 20, dataChart.length);
            });

        });
    }

    const setDataNow = () => {
        var m = new Date();
        var dateString =
            m.getFullYear() + "/" +
            ("0" + (m.getMonth() + 1)).slice(-2) + "/" +
            ("0" + m.getDate()).slice(-2) + " " +
            ("0" + m.getHours()).slice(-2) + ":" +
            ("0" + m.getMinutes()).slice(-2) + ":" +
            ("0" + m.getSeconds()).slice(-2);
        setDateNow(dateString);
    }
    useEffect(() => {
        setDataNow();
        list();
    }, [location]);

    return (
        <>
            {checkLoadingPlant ?
                <>
                    <div id="system-datetime">
                        <span className="fa fa-clock"></span> &nbsp;
                        <span id="timeNow">{dateNow}</span>
                    </div>
                    <div>
                        <div className="tab-content">
                            <table className="tbl-overview" style={{ margin: '0 8px' }}>
                                <thead>
                                    <tr>
                                        <th width="100px">Vị Trí</th>
                                        <th width="150px">Tổng công suất lắp đặt (MW)</th>
                                        <th width="150px">Tổng công suất tiết giảm (MW)</th>
                                        <th width="150px">Tổng công suất phát cho phép (MW)</th>
                                        <th width="150px">Tổng công suất phát hiện tại (MW)</th>
                                        <th width="100px">Trạng thái</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {
                                        listPlant.map(
                                            (plant, index) => {
                                                return (
                                                    <tr key={index}>
                                                        <td style={{ wordWrap: "break-word" }}>{plant.name}</td>
                                                        <td className="text-right" style={{ wordWrap: "break-word" }}>{plant.congSuatLapDat}</td>
                                                        <td className="text-right" style={{ wordWrap: "break-word" }}>{plant.congSuatTietGiam}</td>
                                                        <td className="text-right" style={{ wordWrap: "break-word" }}>{plant.congSuatChoPhepPhat}</td>
                                                        <td className="text-right" style={{ wordWrap: "break-word" }}>{plant.congSuatHienTai}</td>
                                                        {plant.status == 'đỏ' &&
                                                            <td className="text-center" style={{ wordWrap: "break-word" , color: "red"}}>&#11044;</td>
                                                        }
                                                        {plant.status == 'xám' &&
                                                            <td className="text-center" style={{ wordWrap: "break-word" , color: "grey"}}>&#11044;</td>
                                                        }
                                                        {plant.status == 'xanh' &&
                                                            <td className="text-center" style={{ wordWrap: "break-word" , color: "blue"}}>&#11044;</td>
                                                        }
                                                        {plant.status == 'vàng' &&
                                                            <td className="text-center" style={{ wordWrap: "break-word" , color: "yellow"}}>&#11044;</td>
                                                        }
                                                        {plant.status == null &&
                                                            <td className="text-center" style={{ wordWrap: "break-word" , color: "grey"}}>&#45;</td>
                                                        }
                                                    </tr>
                                                )
                                            }

                                        )
                                    }

                                    <tr style={{ display: listPlant.length === 0 ? "contents" : "none" }}>
                                        <td className="text-center" colSpan={6}>Không có dữ liệu</td>
                                    </tr>

                                </tbody>

                            </table>

                        </div>

                    </div>
                    <div className="form-group mt-2 mb-0 ml-2">
                        <div className="input-group float-left mr-1" style={{ width: "270px" }}>
                            <div className="input-group-prepend">
                                <span className="input-group-text pickericon">
                                    <span className="far fa-calendar"></span>
                                </span>
                            </div>
                            <Calendar
                                value={dateForm.fromDate}
                                id="chart-date-from"
                                className="celendar-picker"
                                dateFormat="yy-mm-dd"
                                onChange={e => setDateForm({ ...dateForm, fromDate: e.value })}
                            />
                        </div>
                        <div className="input-group float-left mr-1" style={{ width: "270px" }}>
                            <div className="input-group-prepend">
                                <span className="input-group-text pickericon">
                                    <span className="far fa-calendar"></span>
                                </span>
                            </div>
                            <Calendar
                                value={dateForm.toDate}
                                id="chart-date-to"
                                className="celendar-picker"
                                dateFormat="yy-mm-dd"
                                onChange={e => setDateForm({ ...dateForm, toDate: e.value })}
                            />
                        </div>
                        <div>
                            <button type="button" className="btn btn-outline-warning" style={{ height: '33.5px', marginTop: '-1px' }} onClick={searchPlant}>
                                <i className="fa fa-search"></i>
                            </button>
                        </div>
                    </div>
                </>
                : <img src="/resources/image/loading.gif " alt="loading"></img>
            }
            <div id="chartdiv" style={{height: '500px'}}></div>
        </>
    )

}

export default MonitoringPower;