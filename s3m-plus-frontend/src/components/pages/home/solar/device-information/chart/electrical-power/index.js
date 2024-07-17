import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import $ from "jquery";
import * as am5 from "@amcharts/amcharts5";
import * as am5xy from "@amcharts/amcharts5/xy";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";
import OperationInformationService from "../../../../../../../services/OperationInformationService";
import converter from "../../../../../../../common/converter";
import CONS from "../../../../../../../constants/constant";

const ElectricalPower = () => {

    const param = useParams();

    const getOperationInformation = async () => {
        $('#no-data').hide();
        $('#loading').show();
        $('#chartdiv').html("");

        let deviceType = parseInt(param.deviceType);
        if (deviceType === CONS.DEVICE_TYPE_PV.INVERTER) {
            let res = await OperationInformationService.getDataChartElectricalPowerInverterPV(param.customerId, param.deviceId, param.date, param.typePqs);
            if (res.status === 200 && parseInt(res.data.length) > 0) {
                $('#loading').hide();
                $('#no-data').hide();
                let data = res.data;
                let typeValue = handleSetViewTypeChart(data);
                drawChart(data, typeValue);
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

    const handleSetViewTypeChart = (data) => {
        let values = [];

        data.forEach(item => {
            if (item.low && item.low > 0) {
                values.push(item.low);
            } else {
                item.low = null;
            }
            if (item.normal && item.normal > 0) {
                values.push(item.normal);
            } else  {
                item.normal = null;
            }
            if (item.high && item.high > 0) {
                values.push(item.high);
            } else {
                item.high = null;
            }
        });

        let min = Math.min(...values);

        let typeValue = converter.setViewType(values.length > 0 ? min : 0);

        return typeValue;
    }

    const drawChart = (dataChart, typeValue) => {
        //convert data before drawing chart

        am5.array.each(am5.registry.rootElements, function (root) {
            if (root) {
                if (root.dom.id == "chartdiv") {
                    root.dispose();
                }
            }
        });
        am5.ready(function () {

            // Create root element
            // https://www.amcharts.com/docs/v5/getting-started/#Root_element
            var root = am5.Root.new("chartdiv");

            // Set themes
            // https://www.amcharts.com/docs/v5/concepts/themes/
            root.setThemes([
                am5themes_Animated.new(root)
            ]);

            // Create chart
            // https://www.amcharts.com/docs/v5/charts/xy-chart/
            var chart = root.container.children.push(am5xy.XYChart.new(root, {
                panX: false,
                panY: false,
                wheelX: "panX",
                wheelY: "zoomX",
                layout: root.verticalLayout
            }));

            // Add scrollbar
            // https://www.amcharts.com/docs/v5/charts/xy-chart/scrollbars/
            chart.set(
                "scrollbarX",
                am5.Scrollbar.new(root, {
                    orientation: "horizontal"
                })
            );

            chart.get("colors").set("colors", [
                am5.color(0xB0DE09),
                am5.color(0xFCD202),
                am5.color(0xFF6600)
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

            // Add legend
            // https://www.amcharts.com/docs/v5/charts/xy-chart/legend-xy-series/
            var legend = chart.children.push(
                am5.Legend.new(root, {
                    centerX: am5.p50,
                    x: am5.p50
                })
            );

            // Create axes
            // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
            let xRenderer = am5xy.AxisRendererX.new(root, { minGridDistance: 10 });
            xRenderer.labels.template.setAll({
                rotation: -70,
                paddingTop: -15,
                paddingRight: 10
            });

            var xAxis = chart.xAxes.push(am5xy.CategoryAxis.new(root, {
                categoryField: "sentDate",
                renderer: xRenderer,
                tooltip: am5.Tooltip.new(root, {})
            }));

            xAxis.data.setAll(dataChart);

            let yRenderer = am5xy.AxisRendererY.new(root, {
                opposite: false,
                strokeWidth: 3
            });

            var yAxis = chart.yAxes.push(am5xy.ValueAxis.new(root, {
                calculateTotals: true,
                min: 0,
                extraMax: 0.1,
                renderer: yRenderer
            }));

            yAxis.children.moveValue(am5.Label.new(root, { text: `Điện năng [${converter.convertLabelElectricPower(typeValue, "Wh")}]`, rotation: -90, y: am5.p50, centerX: am5.p50, fontWeight: "bold" }), 0);

            // Add series
            // https://www.amcharts.com/docs/v5/charts/xy-chart/series/
            function makeSeries(name, fieldName, showTotal) {
                var series = chart.series.push(am5xy.ColumnSeries.new(root, {
                    name: name,
                    xAxis: xAxis,
                    yAxis: yAxis,
                    valueYField: fieldName,
                    categoryXField: "sentDate",
                    stacked: true,
                    maskBullets: false
                }));

                series.columns.template.setAll({
                    tooltipText: `{name} - {categoryX}: {valueY} [${converter.convertLabelElectricPower(typeValue, "Wh")}]`,
                    width: am5.percent(90),
                    tooltipY: 0
                });

                yRenderer.grid.template.set("strokeOpacity", 0.05);
                yRenderer.labels.template.set("grid", series.get("grid"));
                yRenderer.setAll({
                    forceHidden: false,
                    strokeOpacity: 3,
                    strokeWidth: 2,
                    stroke: am5.color(0x9E9E9E)
                });

                if (showTotal) {
                    series.bullets.push(function () {
                        return am5.Bullet.new(root, {
                            locationY: 1,
                            sprite: am5.Label.new(root, {
                                text: "{valueYTotal}",
                                fontWeight: "bold",
                                fill: am5.color(0x000000),
                                centerY: am5.p100,
                                centerX: am5.p50,
                                populateText: true
                            })
                        });
                    });
                } else {
                    series.bullets.push(function () {
                        return am5.Bullet.new(root, {
                            locationY: 0.5,
                            sprite: am5.Label.new(root, {
                                text: "{valueY}",
                                fill: root.interfaceColors.get("alternativeText"),
                                centerY: am5.percent(50),
                                centerX: am5.percent(50),
                                populateText: true
                            })
                        });
                    });
                }

                series.data.setAll(dataChart);
                series.appear();

                if (!showTotal) {
                    legend.data.push(series);
                }
            }

            makeSeries("Giờ thấp điểm", "low", null);
            makeSeries("Giờ bình thường", "normal", null);
            makeSeries("Giờ cao điểm", "high", null);
            makeSeries("", "param", true);


            // Make stuff animate on load
            // https://www.amcharts.com/docs/v5/concepts/animations/
            chart.appear(1000, 100);

        }); // end am5.ready()
    };

    document.title = "Thông tin thiết bị - Biểu đồ";

    useEffect(() => {
        getOperationInformation();
    }, [param.deviceId, param.typePqs, param.date, param.chartType]);
    return (
        <>
            <div className="text-center loading" id="loading">
                <img height="60px" className="mt-0.5" src="/resources/image/loading.gif" alt="icon-loading" />
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

export default ElectricalPower;