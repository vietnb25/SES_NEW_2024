import React, { useState, useEffect } from "react";
import overviewLoadService from "../../../services/OverviewLoadService";
import * as am5 from "@amcharts/amcharts5";
import * as am5xy from "@amcharts/amcharts5/xy";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";
import Pickadate from 'pickadate/builds/react-dom';
import './pickadate.css';

const OverviewLoad = () => {

    var date = new Date().toISOString().slice(0, 10);

    const [powers, setPower] = useState([]);

    const getPowers = async () => {
        let res = await overviewLoadService.getPower(1, 1);
        if (res.status === 200) {
            setPower(res.data);
        }
    };

    const getDataChart = async () => {
        let res = await overviewLoadService.getDataChart("2022-11-18", 1, 1);
        if (res.status === 200) {
            //console.log(res.data);
            return res.data;
        }
    };

    const drawChart = async () => {
        let dataChart = await getDataChart();
        am5.ready(function () {
            // Create root element
            // https://www.amcharts.com/docs/v5/getting-started/#Root_element
            var root = am5.Root.new("chartdiv");

            // Set themes
            // https://www.amcharts.com/docs/v5/concepts/themes/
            root.setThemes([am5themes_Animated.new(root)]);

            // Create chart
            // https://www.amcharts.com/docs/v5/charts/xy-chart/
            var chart = root.container.children.push(
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

            var data = dataChart;

            // Create axes
            // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
            var xAxis = chart.xAxes.push(
                am5xy.CategoryAxis.new(root, {
                    categoryField: "time",
                    renderer: am5xy.AxisRendererX.new(root, { minGridDistance: 10 }),
                    tooltip: am5.Tooltip.new(root, {})
                })
            );

            xAxis.data.setAll(data);

            var yRenderer = am5xy.AxisRendererY.new(root, {
                opposite: false
            });

            var yAxis = chart.yAxes.push(
                am5xy.ValueAxis.new(root, {
                    maxDeviation: 1,
                    renderer: yRenderer
                })
            );
            yAxis.children.moveValue(am5.Label.new(root, { text: "Energy(kWh)", rotation: -90, y: am5.p50, centerX: am5.p50 }), 0);

            var series1 = chart.series.push(
                am5xy.ColumnSeries.new(root, {
                    name: "Điện năng (kWh)",
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: "energy",
                    categoryXField: "time",
                    tooltip: am5.Tooltip.new(root, {
                        pointerOrientation: "horizontal",
                        labelText: "{valueY} {info}"
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
            var yAxis2 = chart.yAxes.push(
                am5xy.ValueAxis.new(root, {
                    maxDeviation: 1,
                    renderer: yRenderer,
                })
            );
            yAxis2.children.moveValue(am5.Label.new(root, { text: "Power(kW)", rotation: 90, y: am5.p50, centerX: am5.p50 }), 1);

            // Add series
            // https://www.amcharts.com/docs/v5/charts/xy-chart/series/

            var series2 = chart.series.push(
                am5xy.LineSeries.new(root, {
                    name: "Công suất (kW)",
                    xAxis: xAxis,
                    yAxis: yAxis2,
                    valueYField: "power",
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

            series2.bullets.push(function () {
                return am5.Bullet.new(root, {
                    sprite: am5.Circle.new(root, {
                        strokeWidth: 3,
                        stroke: series2.get("stroke"),
                        radius: 5,
                        fill: root.interfaceColors.get("background")
                    })
                });
            });

            // Add cursor
            // https://www.amcharts.com/docs/v5/charts/xy-chart/cursor/
            var cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
                xAxis: xAxis,
                behavior: "none"
            }));
            cursor.lineY.set("visible", false);

            // Add legend
            // https://www.amcharts.com/docs/v5/charts/xy-chart/legend-xy-series/
            var legend = chart.children.push(
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


        }); // end am5.ready()
    };

    const getDay = () => {
    }

    useEffect(() => {
        getPowers();
        drawChart();

    }, [])

    document.title = "Overview Load";
    return (
        <div id="page-body">

            <div id="main-content">
                <div id="project-info">
                    <div className="tab-container">
                        <ul className="menu">
                            <li>
                                <a href="#"><i className="fas fa-home"></i>&nbsp; <span>Tổng quan</span></a>
                            </li>
                            <li>
                                <a href="#"><i className="fas fa-diagram-project"></i>&nbsp; <span>Sơ đồ</span></a>
                            </li>
                            <li>
                                <a href="#"><i className="fas fa-triangle-exclamation"></i>&nbsp; <span>Cảnh báo</span></a>
                            </li>
                            <li>
                                <a href="#"><i className="fas fa-wrench"></i>&nbsp; <span>Điều khiển</span></a>
                            </li>
                            <li>
                                <a href="#"><i className="fas fa-file-contract"></i>&nbsp; <span>Báo cáo</span></a>
                            </li>
                            <div className="line"></div>
                        </ul>
                    </div>

                    <div className="tab-content">
                        <div className="tab-title">
                            <div className="latest-time">
                                <i className="fa-regular fa-clock"></i>&nbsp; 29-09-2022 17:02
                            </div>
                            <div className="latest-warning">
                                <button type="button" className="btn btn-danger w-120px">
                                    <i className="fa-solid fa-bug"></i> &nbsp;Error: 0
                                </button>
                                <button type="button" className="btn btn-warning w-120px">
                                    <i className="fa-solid fa-triangle-exclamation"></i> &nbsp;Warning: 10
                                </button>
                            </div>
                        </div>

                        <div id="main-search">
                            <div className="input-group search-item mb-3 float-left">
                                <div className="input-group-prepend">
                                    <span className="input-group-text" id="inputGroup-sizing-default">Tìm kiếm</span>
                                </div>
                                <input type="text" id="keyword" className="form-control" aria-label="Mô tả" aria-describedby="inputGroup-sizing-sm" />
                            </div>

                            <div className="search-buttons float-left">
                                <button type="button" className="btn btn-outline-secondary" >
                                    <i className="fa-solid fa-magnifying-glass"></i>
                                </button>
                            </div>
                        </div>

                        <table className="table tbl-overview">
                            <thead>
                                <tr>
                                    <th width="40px">TT</th>
                                    <th>Thành Phần</th>
                                    <th width="150px">Công Suất</th>
                                    <th width="150px">Tiêu Thụ</th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    powers?.map(
                                        (power, index) => <tr key={index}>
                                            <td>{index + 1}</td>
                                            <td>{power.deviceName}</td>
                                            <td>{power.ptotal}</td>
                                            <td>{power.ep}</td>
                                        </tr>
                                    )
                                }
                            </tbody>
                        </table>

                        <div className={"tab-chart"}>
                            <div className="form-group mt-2 mb-0 ml-2">
                                <div className="input-group float-left mr-1" style={{ width: "250px" }}>
                                    <div className="input-group-prepend">
                                        <span className="input-group-text pickericon">
                                            <span className="far fa-calendar"></span>
                                        </span>
                                    </div>
                                    <Pickadate.InputPicker id="chart-date" className="form-control pickadate" style={{ fontSize: "13px" }} readOnly="" aria-haspopup="true"
                                        aria-readonly="false" aria-owns="from-date_root" autoComplete="off" 
                                        initialState = {{
                                            selected: new Date(),
                                            template: "YYYY-M-D"
                                          }}
                                
                                        />


                                </div>
                                <div>
                                    <button type="button" className="btn btn-outline-secondary" onClick={getDay}>
                                        <i className="fa-solid fa-magnifying-glass"></i>
                                    </button>
                                    <button type="button" className="btn btn-outline-warning" >
                                        <i className="fa-solid fa-download"></i>
                                    </button>
                                </div>
                            </div>
                            <div id="chartdiv"></div>
                        </div>

                    </div>
                </div>
            </div>
        </div>

    )
};
export default OverviewLoad;
