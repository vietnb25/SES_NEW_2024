import React, { useEffect } from "react";
import { useParams } from "react-router-dom";
import $ from "jquery";
import * as am5 from "@amcharts/amcharts5";
import * as am5xy from "@amcharts/amcharts5/xy";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";
import OperationInformationService from "../../../../../../../services/OperationInformationService";
import CONS from "../../../../../../../constants/constant";

const ProbeAntenna = () => {

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
            if (item.lfbRatio) {
                item.lfbRatio = item.lfbRatio;
            }
            if (item.lfbEppc) {
                item.lfbEppc = item.lfbEppc;
            }
            if (item.mfbRatio) {
                item.mfbRatio = item.mfbRatio;
            }
            if (item.mlfbEppc) {
                item.mlfbEppc = item.mlfbEppc;
            }
            if (item.hlfbRatio) {
                item.hlfbRatio = item.hlfbRatio;
            }
            if (item.hlfbEppc) {
                item.hlfbEppc = item.hlfbEppc;
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

            if (chartType === CONS.CHART_TYPE_GRID.LFB_RATIO) {
                yAxis.children.moveValue(am5.Label.new(root, { text: "LFB Ratio [[dB]]", rotation: -90, y: am5.p50, centerX: am5.p50, fontWeight: "bold" }), 0);
            }

            if (chartType === CONS.CHART_TYPE_GRID.LFB_EPPC) {
                yAxis.children.moveValue(am5.Label.new(root, { text: "LFB EPPC [[peaks/cycle]]", rotation: -90, y: am5.p50, centerX: am5.p50, fontWeight: "bold" }), 0);
            }

            
            if (chartType === CONS.CHART_TYPE_GRID.MFB_RATIO) {
                yAxis.children.moveValue(am5.Label.new(root, { text: "MFB Ratio [[dB]]", rotation: -90, y: am5.p50, centerX: am5.p50, fontWeight: "bold" }), 0);
            }

            if (chartType === CONS.CHART_TYPE_GRID.MLFB_EPPC) {
                yAxis.children.moveValue(am5.Label.new(root, { text: "MFB EPPC [[peaks/cycle]]", rotation: -90, y: am5.p50, centerX: am5.p50, fontWeight: "bold" }), 0);
            }

            if (chartType === CONS.CHART_TYPE_GRID.HLFB_RATIO) {
                yAxis.children.moveValue(am5.Label.new(root, { text: "HFB Ratio [[dB]]", rotation: -90, y: am5.p50, centerX: am5.p50, fontWeight: "bold" }), 0);
            }

            if (chartType === CONS.CHART_TYPE_GRID.HLFB_EPPC) {
                yAxis.children.moveValue(am5.Label.new(root, { text: "HFB EPPC [[peaks/cycle]]", rotation: -90, y: am5.p50, centerX: am5.p50, fontWeight: "bold" }), 0);
            }

            if (chartType === CONS.CHART_TYPE_GRID.INDICATOR) {
                yAxis.children.moveValue(am5.Label.new(root, { text: "PDD Probe 1 Indicator", rotation: -90, y: am5.p50, centerX: am5.p50, fontWeight: "bold" }), 0);
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

                yRenderer.grid.template.set("strokeOpacity", 0.05);
                yRenderer.labels.template.set("grid", series.get("grid"));
                yRenderer.setAll({
                    forceHidden: false,
                    strokeOpacity: 1,
                    strokeWidth: 1,
                    stroke: am5.color(0x9E9E9E)
                });

                series.data.setAll(data);
            }


            if (chartType === CONS.CHART_TYPE_GRID.LFB_RATIO) {
                createSeries("LFB Ratio", "lfbRatio", "[dB]");
            }

            if (chartType === CONS.CHART_TYPE_GRID.LFB_EPPC) {
                createSeries("LFB EPPC", "lfbEppc", "[peaks/cycle]");
            }

            if (chartType === CONS.CHART_TYPE_GRID.MFB_RATIO) {
                createSeries("MFB Ratio", "mfbRatio", "[dB]");
            }

            if (chartType === CONS.CHART_TYPE_GRID.MLFB_EPPC) {
                createSeries("MFB EPPC", "mlfbEppc", "[peaks/cycle]");
            }

            if (chartType === CONS.CHART_TYPE_GRID.HLFB_RATIO) {
                createSeries("HFB Ratio", "hlfbRatio", "[dB]");
            }

            if (chartType === CONS.CHART_TYPE_GRID.HLFB_EPPC) {
                createSeries("HFB EPPC", "hlfbEppc", "[peaks/cycle]");
            }

            if (chartType === CONS.CHART_TYPE_GRID.INDICATOR) {
                createSeries("PDD Probe 1 Indicator", "indicator", "");
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

export default ProbeAntenna;