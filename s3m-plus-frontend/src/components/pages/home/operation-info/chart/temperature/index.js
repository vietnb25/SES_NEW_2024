import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import OperationInformationService from "../../../../../../services/OperationInformationService";
import $ from "jquery";
import Pickadate from 'pickadate/builds/react-dom';
import * as am5 from "@amcharts/amcharts5";
import * as am5xy from "@amcharts/amcharts5/xy";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";

const Temperature = () => {

    const param = useParams();

    const getOperationInformation = async () => {

        $('#loading').show();

        let res = await OperationInformationService.getDataChart(param.deviceId, param.fromDate, param.toDate, 0, param.chartType);

        if (res.status === 200 && parseInt(res.data.length) > 0) {
            $('#loading').hide();
            $('#no-data').hide();
            drawChart(res.data);
        } else {
            $('#loading').hide();
            $('#no-data').show();
            $('#chartdiv').html("");
        }
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


            chart.get("colors").set("colors", [
                am5.color(0xFF6600),
                am5.color(0xFCD202),
                am5.color(0xB0DE09)
            ]);

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
                    categoryField: "date",
                    renderer: xRenderer,
                    tooltip: am5.Tooltip.new(root, {})
                })
            );

            xAxis.data.setAll(data);

            let yRenderer = am5xy.AxisRendererY.new(root, {
                opposite: false,
                strokeWidth: 3
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
                strokeOpacity: 3,
                strokeWidth: 5,
                stroke: am5.color(0x9E9E9E)
            });

            yAxis.children.moveValue(am5.Label.new(root, { text: "Nhiệt độ (°C)", rotation: -90, y: am5.p50, centerX: am5.p50, fontWeight: "bold" }), 0);

            // // Add series
            // // https://www.amcharts.com/docs/v5/charts/xy-chart/series/


            const createSeries = async (name, field, icon) => {

                let series = chart.series.push(
                    am5xy.LineSeries.new(root, {
                        name: name,
                        xAxis: xAxis,
                        yAxis: yAxis,
                        valueYField: field,
                        categoryXField: "date",
                        tooltip: am5.Tooltip.new(root, {
                            pointerOrientation: "horizontal",
                            labelText: "{valueY} [[°C]] {info}"
                        })
                    })
                );

                series.strokes.template.setAll({
                    strokeWidth: 3,
                    templateField: "strokeSettings"
                });

                yRenderer.grid.template.set("strokeOpacity", 0.05);
                yRenderer.labels.template.set("grid", series.get("grid"));
                yRenderer.setAll({
                    forceHidden: false,
                    strokeOpacity: 3,
                    strokeWidth: 2,
                    stroke: am5.color(0x9E9E9E)
                });

                series.data.setAll(data);

                switch (icon) {
                    case "circle":
                        series.bullets.push(function () {
                            return am5.Bullet.new(root, {
                                sprite: am5.Circle.new(root, {
                                    strokeWidth: 3,
                                    stroke: series.get("stroke"),
                                    radius: 5,
                                    fill: root.interfaceColors.get("background")
                                })
                            });
                        });
                        break;
                    case "triangle":
                        series.bullets.push(function () {
                            return am5.Bullet.new(root, {
                                sprite: am5.Triangle.new(root, {
                                    fill: am5.color(0xFCD202),
                                    rotation: 0,
                                    width: 15,
                                    height: 15
                                })
                            });
                        });
                        break;
                    case "square":
                        series.bullets.push(function () {
                            return am5.Graphics.new(root, {
                                sprite: am5.Graphics.new(root, {
                                    strokeWidth: 3,
                                    stroke: series.get("stroke"),
                                    radius: 5,
                                    centerX: am5.percent(50),
                                    centerY: am5.percent(50),
                                    draw: function (display) {
                                        display.drawRect(0, 0, 10, 10)
                                    },
                                    fill: root.interfaceColors.get("background")
                                })
                            });
                        });
                        break;
                    default:
                        break;
                }
            }

            createSeries("Nhiệt độ pha A", "t1", "circle");
            createSeries("Nhiệt độ pha B", "t2", "triangle");
            createSeries("Nhiệt độ pha C", "t3", "square");

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
                ev.target.zoomToIndexes(dataChart.length - 20, dataChart.length);
            });

        });
    };

    document.title = "Thông tin thiết bị - Biểu đồ";

    useEffect(() => {
        getOperationInformation();
    }, [param.deviceId, param.fromDate, param.toDate, param.chartType])
    return (
        <>
            <div className="text-center loading" id="loading">
                <img height="60px" className="mt-0.5" src="/resources/image/loading.gif" />
            </div>
            <table className="table tbl-overview ml-0 mr-0" id="no-data" style={{ width: "-webkit-fill-available", display: "none" }}>
                <tbody>
                    <tr className="w-100">
                        <td height={30} className="text-center w-100" style={{ border: "none", background: "#D5D6D1" }}> Không có dữ liệu</td>
                    </tr>
                </tbody>
            </table>
            <div id="chartdiv" style={{ height: "600px" }}></div>
        </>
    )
}

export default Temperature;