import React, { useEffect } from "react";
import { useParams } from "react-router-dom";
import $ from "jquery";
import * as am5 from "@amcharts/amcharts5";
import * as am5xy from "@amcharts/amcharts5/xy";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";
import OperationInformationService from "../../../../../../../services/OperationInformationService";
import CONS from "../../../../../../../constants/constant";

const Temperature = () => {

    const param = useParams();

    const chartType = parseInt(param.chartType);

    const getOperationInformation = async () => {
        $('#chartdiv').html("");
        $('#no-data').hide();
        $('#loading').show();

        let deviceType = parseInt(param.deviceType);

        if (deviceType === CONS.DEVICE_TYPE_GRID.RMU_DRAWER) {
            let res = await OperationInformationService.getDataChartRmuDrawerGrid(param.customerId, param.deviceId, param.fromDate, param.toDate);
            if (res.status === 200 && parseInt(res.data.length) > 0) {
                $('#loading').hide();
                $('#no-data').hide();
                drawChart(res.data);
            } else {
                $('#loading').hide();
                $('#no-data').show();
                $('#chartdiv').html("");
            }
        } else {
            $('#loading').hide();
            $('#no-data').show();
            $('#chartdiv').html("");
        }
    }

    const drawChart = (dataChart) => {

        dataChart.forEach(item => {
            if (item.sawId1 && parseInt(item.sawId1) > -500 && parseInt(item.sawId1) < 1800) {
                item.sawId1 = item.sawId1;
            } else {
                item.sawId1 = null;
            }
            if (item.sawId2 && parseInt(item.sawId2) > -500 && parseInt(item.sawId2) < 1800) {
                item.sawId2 = item.sawId2;
            } else {
                item.sawId2 = null;
            }
            if (item.sawId3 && parseInt(item.sawId3) > -500 && parseInt(item.sawId3) < 1800) {
                item.sawId3 = item.sawId3;
            } else {
                item.sawId3 = null;
            }
            if (item.sawId4 && parseInt(item.sawId4) > -500 && parseInt(item.sawId4) < 1800) {
                item.sawId4 = item.sawId4;
            } else {
                item.sawId4 = null;
            }
            if (item.sawId5 && parseInt(item.sawId5) > -500 && parseInt(item.sawId5) < 1800) {
                item.sawId5 = item.sawId5;
            } else {
                item.sawId5 = null;
            }
            if (item.sawId6 && parseInt(item.sawId6) > -500 && parseInt(item.sawId6) < 1800) {
                item.sawId6 = item.sawId6;
            } else {
                item.sawId6 = null;
            }
            
            if (item.t && parseInt(item.t) > -500 && parseInt(item.t) < 1800) {
                item.t = item.t;
            } else {
                item.t = null;
            }

            if (item.h && parseInt(item.h) > -500 && parseInt(item.h) < 1800) {
                item.h = item.h;
            } else {
                item.h = null;
            }
            

        });

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
                    wheelY: "zoomX",
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
                am5.color(0xB0DE09),
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
            let xRenderer = am5xy.AxisRendererX.new(root, { minGridDistance: 80 });
            xRenderer.labels.template.setAll({
                rotation: -70,
                paddingTop: -20,
                paddingRight: 10
            });
            let xAxis = chart.xAxes.push(
                am5xy.CategoryAxis.new(root, {
                    maxDeviation: 0,
                    categoryField: "sentDate",
                    renderer: xRenderer,
                    tooltip: am5.Tooltip.new(root, {})
                })
            );

            xAxis.data.setAll(data);

            let yRenderer = am5xy.AxisRendererY.new(root, {
                opposite: false,
                strokeWidth: 1
            });

            let yAxis = chart.yAxes.push(
                am5xy.ValueAxis.new(root, {
                    maxDeviation: 1,
                    renderer: yRenderer
                })
            );

            let yFill = yAxis.makeDataItem({
                min: chartType === CONS.CHART_TYPE_GRID.NHIET_DO_CUC || chartType === CONS.CHART_TYPE_GRID.NHIET_DO_KHOANG ? -400 : 0,
            });
            let yFillLine = yAxis.createAxisRange(yFill);
            yFillLine.get("grid").setAll({
                forceHidden: false,
                strokeOpacity: 1,
                strokeWidth: 1,
                stroke: am5.color(0x9E9E9E)
            });

            if (chartType === CONS.CHART_TYPE_GRID.NHIET_DO_CUC) {
                yAxis.children.moveValue(am5.Label.new(root, { text: "Nhiệt độ cực [[°C]]", rotation: -90, y: am5.p50, centerX: am5.p50, fontWeight: "bold" }), 0);
            }

            if (chartType === CONS.CHART_TYPE_GRID.NHIET_DO_KHOANG) {
                yAxis.children.moveValue(am5.Label.new(root, { text: "Nhiệt độ khoang [[°C]]", rotation: -90, y: am5.p50, centerX: am5.p50, fontWeight: "bold" }), 0);
            }

            if (chartType === CONS.CHART_TYPE_GRID.DO_AM) {
                yAxis.children.moveValue(am5.Label.new(root, { text: "Độ ẩm [[%rH]]", rotation: -90, y: am5.p50, centerX: am5.p50, fontWeight: "bold" }), 0);
            }
            // // Add series
            // // https://www.amcharts.com/docs/v5/charts/xy-chart/series/


            const createSeries = async (name, field, unit) => {

                let series = chart.series.push(
                    am5xy.LineSeries.new(root, {
                        name: name,
                        xAxis: xAxis,
                        yAxis: yAxis,
                        valueYField: field,
                        categoryXField: "sentDate",
                        tooltip: am5.Tooltip.new(root, {
                            pointerOrientation: "horizontal",
                            labelText: "{name}: {valueY} [" + unit + "] {info}"
                        })
                    })
                );

                series.strokes.template.setAll({
                    strokeWidth: 1,
                    templateField: "strokeSettings"
                });

                yRenderer.grid.template.set("strokeOpacity", 0.2);
                yRenderer.labels.template.set("grid", series.get("grid"));
                yRenderer.setAll({
                    forceHidden: false,
                    strokeOpacity: 1,
                    strokeWidth: 1,
                    stroke: am5.color(0x9E9E9E)
                });

                series.data.setAll(data);
            }

            if (chartType === CONS.CHART_TYPE_GRID.NHIET_DO_CUC) {
                createSeries("Nhiệt độ cực trên pha A [°C]", "sawId1", "[°C]");
                createSeries("Nhiệt độ cực trên pha B [°C]", "sawId2", "[°C]");
                createSeries("Nhiệt độ cực trên pha C [°C]", "sawId3", "[°C]");
                createSeries("Nhiệt độ cực dưới pha A [°C]", "sawId4", "[°C]");
                createSeries("Nhiệt độ cực dưới pha B [°C]", "sawId5", "[°C]");
                createSeries("Nhiệt độ cực dưới pha C [°C]", "sawId6", "[°C]");
            }

            if (chartType === CONS.CHART_TYPE_GRID.NHIET_DO_KHOANG) {
                createSeries("Nhiệt độ khoang", "t", "[°C]");
            }

            if (chartType === CONS.CHART_TYPE_GRID.DO_AM) {
                createSeries("Độ ẩm", "h", "[%rH]");
            }

            // Add cursor
            // https://www.amcharts.com/docs/v5/charts/xy-chart/cursor/
            let cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
                xAxis: xAxis,
                behavior: "zoomX"
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
    }, [param.deviceId, param.fromDate, param.toDate, chartType])
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