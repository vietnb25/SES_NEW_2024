import React, { useState } from "react";
import { useEffect } from "react";
import { useParams } from "react-router-dom";
import * as am5 from "@amcharts/amcharts5";
import * as am5xy from "@amcharts/amcharts5/xy";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";
import OperationInformationService from "../../../../../../../services/OperationInformationService";
import $ from "jquery";
import { toDate } from "@amcharts/amcharts5/.internal/core/util/Type";
import moment from "moment";
import CONS from "../../../../../../../constants/constant";

const Harmonic = () => {

    const idOfDevice = useParams();

    const param = useParams();

    const [params, setParams] = useState({
        chartStyle: "1",
        chartChanelU: ["1", "2", "3"],
        chartChanelI: ["1", "2", "3"],
        chartViewPoint: "31",
        chartType: ["u", "i"]
    });

    const [data, setData] = useState();

    const [time, setTime] = useState();

    const changeChartStyle = e => {
        setParams({
            ...params,
            chartStyle: e.currentTarget.value
        })
    };

    const changeChartChannelU = e => {
        setParams({
            ...params,
            chartChanelU: e.currentTarget.checked ? params.chartChanelU.concat(e.currentTarget.value) : params.chartChanelU.filter(item => item !== e.currentTarget.value)
        })
    };

    const changeChartChannelI = e => {
        setParams({
            ...params,
            chartChanelI: e.currentTarget.checked ? params.chartChanelI.concat(e.currentTarget.value) : params.chartChanelI.filter(item => item !== e.currentTarget.value)
        })
    };

    const changeChartViewPoint = e => {
        setParams({
            ...params,
            chartViewPoint: e.currentTarget.value
        })
    };

    const changeChartType = e => {
        setParams({
            ...params,
            chartType: e.currentTarget.checked ? params.chartType.concat(e.currentTarget.value) : params.chartType.filter(item => item !== e.currentTarget.value)
        });
    };

    const getDataChart = async (type) => {
        $('#no-data').hide();
        $('#chart-harmonic').hide();
        $('#loading').show();
        console.log(1111111111111);
        if (parseInt(type) == 1) {
            let res = await OperationInformationService.getDataChartHarmonic(param.customerId, idOfDevice.deviceId, params);
            console.log(res.data);
            if (res.status === 200) {
                $('#loading').hide();
                $('#no-data').hide();
                $('#chart-harmonic').show();
                setTime(res.data.time);
                drawChart(res.data);
            } else {
                $('#loading').hide();
                $('#no-data').hide();
            }

        } else {
            $('#loading').hide();
            let res = await OperationInformationService.getDataChartHarmonicByDay(param.customerId, idOfDevice.deviceId, params, param.fromDate);
            drawChart(res.data);
        }

    };

    const getChartHarmonicPeriod = async () => {
        $('#no-data').hide();
        $('#chartdiv').html("");
        $('#loading').show();
        let res = await OperationInformationService.getDataChartHarmonicPeriod(param.customerId, idOfDevice.deviceId, param.fromDate, param.toDate);
        if (res.status === 200 && res.data?.length > 0) {
            $('#loading').hide();
            $('#no-data').hide();
            drawChartPeriod(res.data, 2, 'chartdiv-period-u');
            drawChartPeriod(res.data, 3, 'chartdiv-period-i');
        } else {
            $('#loading').hide();
            $('#chartdiv').html("");
            drawChartPeriod(res.data, 2, 'chartdiv-period-u');
            drawChartPeriod(res.data, 3, 'chartdiv-period-i');
        }
    }

    const drawChart = (dataChart) => {

        //vẽ chart cột
        const columnChart = (dataChart) => {

            const drawChartU = (dataChart) => {
                am5.array.each(am5.registry.rootElements, function (root) {
                    if (root) {
                        if (root.dom.id == "chartdiv-u") {
                            root.dispose();
                        }
                    }
                });
                am5.ready(function () {
                    let root = am5.Root.new("chartdiv-u");
                    // Create root element
                    // https://www.amcharts.com/docs/v5/getting-started/#Root_element

                    // Set themes
                    // https://www.amcharts.com/docs/v5/concepts/themes/
                    root.setThemes([am5themes_Animated.new(root)]);

                    // Create chart
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/
                    let chart = root.container.children.push(
                        am5xy.XYChart.new(root, {
                            panX: false,
                            panY: false,
                            wheelX: "panX",
                            wheelY: "none",
                            layout: root.verticalLayout
                        })
                    );

                    //màu cột
                    let harmornicsUColors = {
                        'U1': "#E00304",
                        'U2': "#FFFF00",
                        'U3': "#0075FF"

                    };

                    //label chart
                    chart.children.unshift(am5.Label.new(root, {
                        text: "Harmonic (U)",
                        fontSize: 15,
                        textAlign: "center",
                        x: am5.percent(50),
                        y: am5.percent(100),
                        paddingTop: 0,
                        paddingBottom: 0
                    }));

                    let data = dataChart;

                    // Create axes
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
                    let xAxis = chart.xAxes.push(
                        am5xy.CategoryAxis.new(root, {
                            categoryField: "harmonicsNo",
                            renderer: am5xy.AxisRendererX.new(root, {
                                cellStartLocation: 0.1,
                                cellEndLocation: 0.9,
                                minGridDistance: 10
                            }),
                        })
                    );

                    xAxis.data.setAll(data);

                    let yRenderer = am5xy.AxisRendererY.new(root, {
                    });

                    let yAxis = chart.yAxes.push(
                        am5xy.ValueAxis.new(root, {
                            min: 0,
                            renderer: yRenderer
                        })
                    );
                    yAxis.children.moveValue(am5.Label.new(root, { text: "THD U [[%]]", rotation: -90, y: am5.p50, centerX: am5.p50 }), 1);

                    const createSeries = (value, name) => {
                        var series = chart.series.push(
                            am5xy.ColumnSeries.new(root, {
                                name: name,
                                xAxis: xAxis,
                                yAxis: yAxis,
                                valueYField: value,
                                categoryXField: "harmonicsNo",
                                fill: am5.color(harmornicsUColors[name])
                            })
                        );
                        series.columns.template.setAll({
                            tooltipText: "{name}: {valueY} [[%]]",
                            width: am5.percent(60),
                            tooltipY: 0
                        });

                        series.data.setAll(data);
                    };

                    createSeries('vabH', 'U1');
                    createSeries('vbcH', 'U2');
                    createSeries('vcaH', 'U3');

                    // Make stuff animate on load
                    // https://www.amcharts.com/docs/v5/concepts/animations/
                    chart.appear(1000, 100);
                    // createSeries.appear();

                });
            };

            const drawChartI = (dataChart) => {
                am5.array.each(am5.registry.rootElements, function (root) {
                    if (root) {
                        if (root.dom.id == "chartdiv-i") {
                            root.dispose();
                        }
                    }
                });
                am5.ready(function () {
                    let root = am5.Root.new("chartdiv-i");
                    // Create root element
                    // https://www.amcharts.com/docs/v5/getting-started/#Root_element

                    // Set themes
                    // https://www.amcharts.com/docs/v5/concepts/themes/
                    root.setThemes([am5themes_Animated.new(root)]);

                    // Create chart
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/
                    let chart = root.container.children.push(
                        am5xy.XYChart.new(root, {
                            panX: false,
                            panY: false,
                            wheelX: "panX",
                            wheelY: "none",
                            layout: root.verticalLayout
                        })
                    );

                    //màu cột
                    let harmornicsUColors = {
                        'I1': "#FF5559",
                        'I2': "#FFFF66",
                        'I3': "#8DC4FD"
                    };

                    //label chart
                    chart.children.unshift(am5.Label.new(root, {
                        text: "Harmonic (I)",
                        fontSize: 15,
                        textAlign: "center",
                        x: am5.percent(50),
                        y: am5.percent(100),
                        paddingTop: 0,
                        paddingBottom: 0
                    }));

                    let data = dataChart;

                    // Create axes
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
                    let xAxis = chart.xAxes.push(
                        am5xy.CategoryAxis.new(root, {
                            categoryField: "harmonicsNo",
                            renderer: am5xy.AxisRendererX.new(root, {
                                cellStartLocation: 0.1,
                                cellEndLocation: 0.9,
                                minGridDistance: 10
                            }),
                        })
                    );

                    xAxis.data.setAll(data);

                    let yRenderer = am5xy.AxisRendererY.new(root, {
                    });

                    let yAxis = chart.yAxes.push(
                        am5xy.ValueAxis.new(root, {
                            min: 0,
                            renderer: yRenderer
                        })
                    );
                    // label trục y
                    yAxis.children.moveValue(am5.Label.new(root, { text: "THD I [[%]]", rotation: -90, y: am5.p50, centerX: am5.p50 }), 1);

                    const createSeries = (value, name) => {
                        var series = chart.series.push(
                            am5xy.ColumnSeries.new(root, {
                                name: name,
                                xAxis: xAxis,
                                yAxis: yAxis,
                                valueYField: value,
                                categoryXField: "harmonicsNo",
                                fill: am5.color(harmornicsUColors[name])
                            })
                        );
                        series.columns.template.setAll({
                            tooltipText: "{name}: {valueY} [[%]]",
                            width: am5.percent(60),
                            tooltipY: 0
                        });

                        series.data.setAll(data);
                    };

                    createSeries('iaH', 'I1');
                    createSeries('ibH', 'I2');
                    createSeries('icH', 'I3');

                    // Make stuff animate on load
                    // https://www.amcharts.com/docs/v5/concepts/animations/
                    chart.appear(1000, 100);
                    // createSeries.appear();
                });
            };

            if (params.chartType.includes("u")) {
                drawChartU(dataChart.dataChartU);
            }

            if (params.chartType.includes("i")) {
                drawChartI(dataChart.dataChartI);
            }
        };

        //vẽ chart đường
        const lineChart = (dataChart) => {

            const drawChartU = (dataChart) => {
                am5.array.each(am5.registry.rootElements, function (root) {
                    if (root) {
                        if (root.dom.id == "chartdiv-u") {
                            root.dispose();
                        }
                    }
                });
                am5.ready(function () {
                    let root = am5.Root.new("chartdiv-u");
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
                            panY: true,
                            wheelX: "panX",
                            wheelY: "none",
                            layout: root.verticalLayout
                        })
                    );

                    //màu cột
                    let harmornicsColors = {
                        'U1': "#FF5559",
                        'U2': "#E1FF55",
                        'U3': "#8DC4FD"

                    };

                    //label chart
                    chart.children.unshift(am5.Label.new(root, {
                        text: "Harmonic (U)",
                        fontSize: 15,
                        textAlign: "center",
                        x: am5.percent(50),
                        y: am5.percent(100),
                        paddingTop: 0,
                        paddingBottom: 0
                    }));

                    let data = dataChart;

                    // Create axes
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
                    let xAxis = chart.xAxes.push(
                        am5xy.CategoryAxis.new(root, {
                            categoryField: "harmonicsNo",
                            renderer: am5xy.AxisRendererX.new(root, {
                                minGridDistance: 10
                            }),
                            tooltip: am5.Tooltip.new(root, {})
                        })
                    );

                    xAxis.data.setAll(data);

                    let yRenderer = am5xy.AxisRendererY.new(root, {
                    });

                    let yAxis = chart.yAxes.push(
                        am5xy.ValueAxis.new(root, {
                            min: 0,
                            renderer: yRenderer
                        })
                    );

                    // label trục y
                    yAxis.children.moveValue(am5.Label.new(root, { text: "THD U [[%]]", rotation: -90, y: am5.p50, centerX: am5.p50 }), 1);

                    const createSeries = (value, name) => {
                        var series = chart.series.push(
                            am5xy.LineSeries.new(root, {
                                name: name,
                                xAxis: xAxis,
                                yAxis: yAxis,
                                valueYField: value,
                                categoryXField: "harmonicsNo",
                                stroke: am5.color(harmornicsColors[name]),
                                fill: am5.color(harmornicsColors[name]),
                                seriesTooltipTarget: "bullet",
                                showTooltipOn: "always",
                                tooltip: am5.Tooltip.new(root, {
                                    pointerOrientation: "horizontal",
                                    labelText: "{name}: {valueY} [[%]]"
                                })
                            })
                        );

                        series.data.setAll(data);

                        series.bullets.push(function () {
                            return am5.Bullet.new(root, {
                                sprite: am5.Circle.new(root, {
                                    strokeWidth: 2,
                                    stroke: series.get("stroke"),
                                    radius: 4,
                                    fill: series.get("fill"),
                                })
                            });
                        });

                    };

                    let cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
                        xAxis: xAxis,
                        behavior: "none"
                    }));
                    cursor.lineY.set("visible", false);

                    createSeries('vabH', 'U1');
                    createSeries('vbcH', 'U2');
                    createSeries('vcaH', 'U3');

                    // Make stuff animate on load
                    // https://www.amcharts.com/docs/v5/concepts/animations/
                    chart.appear(1000, 100);
                    // createSeries.appear();

                });
            };

            const drawChartI = (dataChart) => {
                am5.array.each(am5.registry.rootElements, function (root) {
                    if (root) {
                        if (root.dom.id == "chartdiv-i") {
                            root.dispose();
                        }
                    }
                });
                am5.ready(function () {
                    let root = am5.Root.new("chartdiv-i");
                    // Create root element
                    // https://www.amcharts.com/docs/v5/getting-started/#Root_element

                    // Set themes
                    // https://www.amcharts.com/docs/v5/concepts/themes/
                    root.setThemes([am5themes_Animated.new(root)]);

                    // Create chart
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/
                    let chart = root.container.children.push(
                        am5xy.XYChart.new(root, {
                            panX: false,
                            panY: false,
                            wheelX: "panX",
                            wheelY: "none",
                            layout: root.verticalLayout
                        })
                    );

                    //màu cột
                    let harmornicsColors = {
                        'I1': "#FF5559",
                        'I2': "#E1FF55",
                        'I3': "#8DC4FD"

                    };

                    //label chart
                    chart.children.unshift(am5.Label.new(root, {
                        text: "Harmonic (I)",
                        fontSize: 15,
                        textAlign: "center",
                        x: am5.percent(50),
                        y: am5.percent(100),
                        paddingTop: 0,
                        paddingBottom: 0
                    }));

                    let data = dataChart;

                    // Create axes
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
                    let xAxis = chart.xAxes.push(
                        am5xy.CategoryAxis.new(root, {
                            categoryField: "harmonicsNo",
                            renderer: am5xy.AxisRendererX.new(root, {
                                minGridDistance: 10
                            })
                        })
                    );

                    xAxis.data.setAll(data);

                    let yRenderer = am5xy.AxisRendererY.new(root, {
                    });

                    let yAxis = chart.yAxes.push(
                        am5xy.ValueAxis.new(root, {
                            min: 0,
                            renderer: yRenderer
                        })
                    );

                    // label trục y
                    yAxis.children.moveValue(am5.Label.new(root, { text: "THD I [[%]]", rotation: -90, y: am5.p50, centerX: am5.p50 }), 1);

                    const createSeries = (value, name) => {
                        var series = chart.series.push(
                            am5xy.LineSeries.new(root, {
                                name: name,
                                xAxis: xAxis,
                                yAxis: yAxis,
                                valueYField: value,
                                categoryXField: "harmonicsNo",
                                tooltip: am5.Tooltip.new(root, {
                                    pointerOrientation: "horizontal",
                                    labelText: "{name}: {valueY} [[%]]"
                                }),
                                stroke: am5.color(harmornicsColors[name]),
                                fill: am5.color(harmornicsColors[name])
                            })
                        );
                        series.bullets.push(function () {
                            return am5.Bullet.new(root, {
                                sprite: am5.Circle.new(root, {
                                    strokeWidth: 2,
                                    stroke: series.get("stroke"),
                                    radius: 4,
                                    fill: series.get("fill"),
                                })
                            });
                        });

                        series.data.setAll(data);
                    };

                    let cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
                        xAxis: xAxis,
                        behavior: "none"
                    }));
                    cursor.lineY.set("visible", false);

                    createSeries('iaH', 'I1');
                    createSeries('ibH', 'I2');
                    createSeries('icH', 'I3');

                    // Make stuff animate on load
                    // https://www.amcharts.com/docs/v5/concepts/animations/
                    chart.appear(1000, 100);
                    // createSeries.appear();

                });
            };

            if (params.chartType.includes("u")) {
                drawChartU(dataChart.dataChartU);
            }
            if (params.chartType.includes("i")) {
                drawChartI(dataChart.dataChartI);
            }

        };

        //vẽ chart đường vùng
        const lineRangeChart = (dataChart) => {

            const drawChartU = (dataChart) => {
                am5.array.each(am5.registry.rootElements, function (root) {
                    if (root) {
                        if (root.dom.id == "chartdiv-u") {
                            root.dispose();
                        }
                    }
                });
                am5.ready(function () {
                    let root = am5.Root.new("chartdiv-u");
                    // Create root element
                    // https://www.amcharts.com/docs/v5/getting-started/#Root_element

                    // Set themes
                    // https://www.amcharts.com/docs/v5/concepts/themes/
                    root.setThemes([am5themes_Animated.new(root)]);

                    // Create chart
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/
                    let chart = root.container.children.push(
                        am5xy.XYChart.new(root, {
                            panX: false,
                            panY: false,
                            wheelX: "panX",
                            wheelY: "none",
                            layout: root.verticalLayout
                        })
                    );

                    //màu cột
                    let harmornicsColors = {
                        'U1': "#FF5559",
                        'U2': "#E1FF55",
                        'U3': "#8DC4FD"
                    };

                    //label chart
                    chart.children.unshift(am5.Label.new(root, {
                        text: "Harmonic (U)",
                        fontSize: 15,
                        textAlign: "center",
                        x: am5.percent(50),
                        y: am5.percent(100),
                        paddingTop: 0,
                        paddingBottom: 0
                    }));

                    let data = dataChart;

                    var maxVabH = Math.max(...dataChart.map(item => item.vabH));
                    var maxVbcH = Math.max(...dataChart.map(item => item.vbcH));
                    var maxVcaH = Math.max(...dataChart.map(item => item.vcaH));
                    var maxY = Math.max(maxVabH, maxVbcH, maxVcaH);



                    // Create axes
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
                    let xAxis = chart.xAxes.push(
                        am5xy.CategoryAxis.new(root, {
                            categoryField: "harmonicsNo",
                            renderer: am5xy.AxisRendererX.new(root, {
                                minGridDistance: 10
                            })
                        })
                    );

                    xAxis.data.setAll(data);

                    let yRenderer = am5xy.AxisRendererY.new(root, {
                    });

                    let yAxis = chart.yAxes.push(
                        am5xy.ValueAxis.new(root, {
                            min: 0,
                            renderer: yRenderer
                        })
                    );

                    // label trục y
                    yAxis.children.moveValue(am5.Label.new(root, { text: "THD U [[%]]", rotation: -90, y: am5.p50, centerX: am5.p50 }), 1);

                    let maxData = yAxis.makeDataItem({
                        value: maxY,

                    });
                    let maxDataLine = yAxis.createAxisRange(maxData);
                    maxDataLine.get("grid").setAll({
                        forceHidden: false,
                        strokeOpacity: 1,
                        strokeDasharray: [6, 7]
                    })

                    const createSeries = (value, name) => {
                        var series = chart.series.push(
                            am5xy.LineSeries.new(root, {
                                name: name,
                                xAxis: xAxis,
                                yAxis: yAxis,
                                valueYField: value,
                                categoryXField: "harmonicsNo",
                                tooltip: am5.Tooltip.new(root, {
                                    pointerOrientation: "horizontal",
                                    labelText: "{name}: {valueY} [[%]]"
                                }),
                                stroke: am5.color(harmornicsColors[name]),
                                fill: am5.color(harmornicsColors[name])
                            })
                        );
                        series.bullets.push(function () {
                            return am5.Bullet.new(root, {
                                sprite: am5.Circle.new(root, {
                                    strokeWidth: 2,
                                    stroke: series.get("stroke"),
                                    radius: 4,
                                    fill: series.get("fill"),
                                })
                            });
                        });

                        series.data.setAll(data);
                    };

                    let cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
                        xAxis: xAxis,
                        behavior: "none"
                    }));
                    cursor.lineY.set("visible", false);

                    createSeries('vabH', 'U1');
                    createSeries('vbcH', 'U2');
                    createSeries('vcaH', 'U3');

                    // Make stuff animate on load
                    // https://www.amcharts.com/docs/v5/concepts/animations/
                    chart.appear(1000, 100);
                    // createSeries.appear();

                });
            };

            const drawChartI = (dataChart) => {
                am5.array.each(am5.registry.rootElements, function (root) {
                    if (root) {
                        if (root.dom.id == "chartdiv-i") {
                            root.dispose();
                        }
                    }
                });
                am5.ready(function () {
                    let root = am5.Root.new("chartdiv-i");
                    // Create root element
                    // https://www.amcharts.com/docs/v5/getting-started/#Root_element

                    // Set themes
                    // https://www.amcharts.com/docs/v5/concepts/themes/
                    root.setThemes([am5themes_Animated.new(root)]);

                    // Create chart
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/
                    let chart = root.container.children.push(
                        am5xy.XYChart.new(root, {
                            panX: false,
                            panY: false,
                            wheelX: "panX",
                            wheelY: "none",
                            layout: root.verticalLayout
                        })
                    );

                    //màu cột
                    let harmornicsColors = {
                        'I1': "#FF5559",
                        'I2': "#E1FF55",
                        'I3': "#8DC4FD"
                    };

                    //label chart
                    chart.children.unshift(am5.Label.new(root, {
                        text: "Harmonic (I)",
                        fontSize: 15,
                        textAlign: "center",
                        x: am5.percent(50),
                        y: am5.percent(100),
                        paddingTop: 0,
                        paddingBottom: 0
                    }));

                    let data = dataChart;

                    var maxiaH = Math.max(...dataChart.map(item => item.iaH));
                    var maxibH = Math.max(...dataChart.map(item => item.ibH));
                    var maxicH = Math.max(...dataChart.map(item => item.icH));
                    var maxY = Math.max(maxiaH, maxibH, maxicH);



                    // Create axes
                    // https://www.amcharts.com/docs/v5/charts/xy-chart/axes/
                    let xAxis = chart.xAxes.push(
                        am5xy.CategoryAxis.new(root, {
                            categoryField: "harmonicsNo",
                            renderer: am5xy.AxisRendererX.new(root, {
                                minGridDistance: 10
                            })
                        })
                    );

                    xAxis.data.setAll(data);

                    let yRenderer = am5xy.AxisRendererY.new(root, {
                    });

                    let yAxis = chart.yAxes.push(
                        am5xy.ValueAxis.new(root, {
                            min: 0,
                            renderer: yRenderer
                        })
                    );

                    // label trục y
                    yAxis.children.moveValue(am5.Label.new(root, { text: "THD I [[%]]", rotation: -90, y: am5.p50, centerX: am5.p50 }), 1);

                    let maxData = yAxis.makeDataItem({
                        value: maxY,

                    });
                    let maxDataLine = yAxis.createAxisRange(maxData);
                    maxDataLine.get("grid").setAll({
                        forceHidden: false,
                        strokeOpacity: 1,
                        strokeDasharray: [6, 7]
                    })

                    const createSeries = (value, name) => {
                        var series = chart.series.push(
                            am5xy.LineSeries.new(root, {
                                name: name,
                                xAxis: xAxis,
                                yAxis: yAxis,
                                valueYField: value,
                                categoryXField: "harmonicsNo",
                                tooltip: am5.Tooltip.new(root, {
                                    pointerOrientation: "horizontal",
                                    labelText: "{name}: {valueY} [[%]]"
                                }),
                                stroke: am5.color(harmornicsColors[name]),
                                fill: am5.color(harmornicsColors[name])
                            })
                        );
                        series.bullets.push(function () {
                            return am5.Bullet.new(root, {
                                sprite: am5.Circle.new(root, {
                                    strokeWidth: 2,
                                    stroke: series.get("stroke"),
                                    radius: 4,
                                    fill: series.get("fill"),
                                })
                            });
                        });

                        series.data.setAll(data);
                    };

                    let cursor = chart.set("cursor", am5xy.XYCursor.new(root, {
                        xAxis: xAxis,
                        behavior: "none"
                    }));
                    cursor.lineY.set("visible", false);

                    createSeries('iaH', 'I1');
                    createSeries('ibH', 'I2');
                    createSeries('icH', 'I3');

                    // Make stuff animate on load
                    // https://www.amcharts.com/docs/v5/concepts/animations/
                    chart.appear(1000, 100);
                    // createSeries.appear();

                });
            };

            if (params.chartType.includes("u")) {
                drawChartU(dataChart.dataChartU);
            }
            if (params.chartType.includes("i")) {
                drawChartI(dataChart.dataChartI);
            }
        };

        if (params.chartStyle === "1") {
            columnChart(dataChart);
        } else if (params.chartStyle === "2") {
            lineChart(dataChart);
        } else if (params.chartStyle === "3") {
            lineRangeChart(dataChart);
        }
    };

    const drawChartPeriod = (dataChart, type, idChart) => {

        am5.array.each(am5.registry.rootElements, function (root) {
            if (root) {
                if (root.dom.id == idChart) {
                    root.dispose();
                }
            }
        });
        am5.ready(function () {
            let root = am5.Root.new(idChart);
            // Create root element
            // https://www.amcharts.com/docs/v5/getting-started/#Root_element

            // Set themes
            // https://www.amcharts.com/docs/v5/concepts/themes/
            root.setThemes([am5themes_Animated.new(root)]);

            // Create chart
            // https://www.amcharts.com/docs/v5/charts/xy-chart/
            let chart = root.container.children.push(
                am5xy.XYChart.new(root, {
                    panX: false,
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
            let scrollbarX = chart.get("scrollbarX");

            chart.get("colors").set("colors", [
                am5.color(0xFF5559),
                am5.color(0xE1FF55),
                am5.color(0x8DC4FD)
            ]);

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
            let xRenderer = am5xy.AxisRendererX.new(root, { minGridDistance: 25 });
            xRenderer.labels.template.setAll({
                rotation: -70,
                paddingTop: -20,
                paddingRight: 10
            });
            let xAxis = chart.xAxes.push(
                am5xy.CategoryAxis.new(root, {
                    categoryField: "sentDate",
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

            if (parseInt(type) === 2) {
                yAxis.children.moveValue(am5.Label.new(root, { text: "Sóng hài dòng điện [[%]]", rotation: -90, y: am5.p50, centerX: am5.p50, fontWeight: "bold" }), 0);
            } else if (parseInt(type) === 3) {
                yAxis.children.moveValue(am5.Label.new(root, { text: "Sóng hài điện áp [[%]]", rotation: -90, y: am5.p50, centerX: am5.p50, fontWeight: "bold" }), 0);
            }

            // // Add serieslabelText
            // // https://www.amcharts.com/docs/v5/charts/xy-chart/series/


            const createSeries = (name, field) => {

                let series = chart.series.push(
                    am5xy.LineSeries.new(root, {
                        name: name,
                        xAxis: xAxis,
                        yAxis: yAxis,
                        valueYField: field,
                        categoryXField: "sentDate",
                        tooltip: am5.Tooltip.new(root, {
                            pointerOrientation: "horizontal",
                            labelText: "{name}: {valueY} [[%]] {info}"
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

                series.bullets.push(function () {
                    return am5.Bullet.new(root, {
                        sprite: am5.Circle.new(root, {
                            strokeWidth: 1,
                            stroke: series.get("stroke"),
                            radius: 4,
                            fill: series.get("fill"),
                        })
                    });
                });
            }

            if (parseInt(type) == 2) {
                createSeries("Sóng hài dòng điện pha A", "thdIa");
                createSeries("Sóng hài dòng điện pha B", "thdIb");
                createSeries("Sóng hài dòng điện pha C", "thdIc");
            } else if (parseInt(type) == 3) {
                createSeries("Sóng hài điện áp pha A", "thdVab");
                createSeries("Sóng hài điện áp pha B", "thdVbc");
                createSeries("Sóng hài điện áp pha C", "thdVca");
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
    }

    useEffect(() => {
        document.title = "Biểu đồ Harmonic";
        if (parseInt(param.chartType) === CONS.CHART_TYPE_LOAD.SONG_HAI_TUC_THOI) {
            $('#loading').hide();
            $('#no-data').hide();
            getDataChart(1);
        } else if (parseInt(param.chartType) === CONS.CHART_TYPE_LOAD.SONG_HAI_THOI_DIEM) {
            $('#loading').hide();
            $('#no-data').hide();
            getDataChart(2);
        } else if (parseInt(param.chartType) === CONS.CHART_TYPE_LOAD.SONG_HAI_KHOANG_THOI_GIAN) {
            getChartHarmonicPeriod();
        }
    }, [params, idOfDevice.deviceId, param.fromDate, param.toDate, param.chartType, param.customerId]);

    return (
        <div className="tab-content">
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
            {parseInt(param.chartType) === CONS.CHART_TYPE_LOAD.SONG_HAI_TUC_THOI && <div id="chart-harmonic">
                <form>
                    <div id="harmonics-options">
                        <div className="lastest-time" style={{ position: "relative", top: "-12px" }}>
                            <span className="float-right">
                                <i className="fa-regular fa-clock"></i>&nbsp;{moment(time).format(CONS.DATE_FORMAT_OPERATE)}
                            </span>
                        </div>
                        <div id="harmonics-display-mode">
                            <span>Display</span>
                            <div className="switch-field">
                                <input type="radio" name="chart_type" id="chart-type-01" value="1" checked={params.chartStyle === "1"} onChange={e => changeChartStyle(e)} />
                                <label htmlFor="chart-type-01" id="lbl-chart-type-01"></label>
                                <input type="radio" name="chart_type" id="chart-type-02" value="2" checked={params.chartStyle === "2"} onChange={e => changeChartStyle(e)} />
                                <label htmlFor="chart-type-02" id="lbl-chart-type-02"></label>
                                <input type="radio" name="chart_type" id="chart-type-03" value="3" checked={params.chartStyle === "3"} onChange={e => changeChartStyle(e)} />
                                <label htmlFor="chart-type-03" id="lbl-chart-type-03"></label>
                            </div>
                        </div>

                        <div id="harmonics-channels">
                            <span>Channels</span>
                            <div>
                                <fieldset>
                                    <input name="chb-u" type='checkbox' id="channels-chk-u-1" value='1' style={{ verticalAlign: "middle" }} checked={params.chartChanelU?.includes("1")} onChange={e => changeChartChannelU(e)} />
                                    <div style={{ display: "inline-block", width: "30px", height: "15px", background: "#DC0A0E", margin: "0 0 0 5px", verticalAlign: "middle" }}></div>
                                    <label style={{ display: "inline-block", marginTop: "0 !important", marginBottom: "0", verticalAlign: "middle" }} htmlFor="chk-channel-1">&nbsp;U1</label>
                                </fieldset>

                                <fieldset>
                                    <input name="chb-u" type='checkbox' id="channels-chk-u-2" value='2' style={{ verticalAlign: "middle" }} checked={params.chartChanelU?.includes("2")} onChange={e => changeChartChannelU(e)} />
                                    <div style={{ display: "inline-block", width: "30px", height: "15px", background: "#FFFF00", margin: "0 0 0 5px", verticalAlign: "middle" }}></div>
                                    <label style={{ display: "inline-block", marginTop: "0 !important", marginBottom: "0", verticalAlign: "middle" }} htmlFor="chk-channel-1">&nbsp;U2</label>
                                </fieldset>

                                <fieldset>
                                    <input name="chb-u" type='checkbox' id="channels-chk-u-3" value='3' style={{ verticalAlign: "middle" }} checked={params.chartChanelU?.includes("3")} onChange={e => changeChartChannelU(e)} />
                                    <div style={{ display: "inline-block", width: "30px", height: "15px", background: "#0074FF", margin: "0 0 0 5px", verticalAlign: "middle" }}></div>
                                    <label style={{ display: "inline-block", marginTop: "0 !important", marginBottom: "0", verticalAlign: "middle" }} htmlFor="chk-channel-1">&nbsp;U3</label>
                                </fieldset>
                            </div>

                            <div>
                                <fieldset>
                                    <input name="chb-i" type='checkbox' id="channels-chk-i-1" value='1' style={{ verticalAlign: "middle" }} checked={params.chartChanelI?.includes("1")} onChange={e => changeChartChannelI(e)} />
                                    <div style={{ display: "inline-block", width: "30px", height: "15px", background: "#FE5A57", margin: "0 0 0 5px", verticalAlign: "middle" }}></div>
                                    <label style={{ display: "inline-block", marginTop: "0 !important", marginBottom: "0", verticalAlign: "middle" }} htmlFor="chk-channel-1">&nbsp;I1</label>
                                </fieldset>

                                <fieldset>
                                    <input name="chb-i" type='checkbox' id="channels-chk-i-2" value='2' style={{ verticalAlign: "middle" }} checked={params.chartChanelI?.includes("2")} onChange={e => changeChartChannelI(e)} />
                                    <div style={{ display: "inline-block", width: "30px", height: "15px", background: "#FFFF66", margin: "0 0 0 5px", verticalAlign: "middle" }}></div>
                                    <label style={{ display: "inline-block", marginTop: "0 !important", marginBottom: "0", verticalAlign: "middle" }} htmlFor="chk-channel-1">&nbsp;I2</label>
                                </fieldset>

                                <fieldset>
                                    <input name="chb-i" type='checkbox' id="channels-chk-i-3" value='3' style={{ verticalAlign: "middle" }} checked={params.chartChanelI?.includes("3")} onChange={e => changeChartChannelI(e)} />
                                    <div style={{ display: "inline-block", width: "30px", height: "15px", background: "#8FC1FE", margin: "0 0 0 5px", verticalAlign: "middle" }}></div>
                                    <label style={{ display: "inline-block", marginTop: "0 !important", marginBottom: "0", verticalAlign: "middle" }} htmlFor="chk-channel-1">&nbsp;I3</label>
                                </fieldset>
                            </div>
                        </div>

                        <div id="harmonics-views">
                            <span>Views</span>
                            <select id="select-harmonics" style={{ borderRadius: "0" }} className="custom-select" value={params.chartViewPoint} onChange={e => changeChartViewPoint(e)}>
                                <option value="1">1</option>
                                <option value="2">2</option>
                                <option value="3">3</option>
                                <option value="4">4</option>
                                <option value="5">5</option>
                                <option value="6">6</option>
                                <option value="7">7</option>
                                <option value="8">8</option>
                                <option value="9">9</option>
                                <option value="10">10</option>
                                <option value="11">11</option>
                                <option value="12">12</option>
                                <option value="13">13</option>
                                <option value="14">14</option>
                                <option value="15">15</option>
                                <option value="16">16</option>
                                <option value="17">17</option>
                                <option value="18">18</option>
                                <option value="19">19</option>
                                <option value="20">20</option>
                                <option value="21">21</option>
                                <option value="22">22</option>
                                <option value="23">23</option>
                                <option value="24">24</option>
                                <option value="25">25</option>
                                <option value="26">26</option>
                                <option value="27">27</option>
                                <option value="28">28</option>
                                <option value="29">29</option>
                                <option value="30">30</option>
                                <option value="31">31</option>
                            </select>
                            <fieldset>
                                <input name="chk-harmonics" type='checkbox' id="view-chk-u-1" value='u' style={{ verticalAlign: "middle" }} checked={params.chartType.includes("u")} onChange={e => changeChartType(e)} />
                                <label style={{ display: "inline-block", marginTop: "0 !important", marginBottom: "0", verticalAlign: "middle" }} htmlFor="chk-channel-1">&nbsp;Harmonics (U)</label>
                            </fieldset>
                            <fieldset>
                                <input name="chk-harmonics" type='checkbox' id="view-chk-u-2" value='i' style={{ verticalAlign: "middle" }} checked={params.chartType.includes("i")} onChange={e => changeChartType(e)} />
                                <label style={{ display: "inline-block", marginTop: "0 !important", marginBottom: "0", verticalAlign: "middle" }} htmlFor="chk-channel-2">&nbsp;Harmonics (I)</label>
                            </fieldset>
                        </div>
                    </div>
                </form>

                <div id="system-data-power">
                    {params.chartType.includes("u") && <div id="chartdiv-u"></div>}

                    {params.chartType.includes("i") && <div id="chartdiv-i"></div>}

                </div>
            </div>
            }
            {parseInt(param.chartType) === CONS.CHART_TYPE_LOAD.SONG_HAI_THOI_DIEM && <>
                <form>
                    <div id="harmonics-options">
                        <div id="harmonics-display-mode">
                            <span>Display</span>
                            <div className="switch-field">
                                <input type="radio" name="chart_type" id="chart-type-01" value="1" checked={params.chartStyle === "1"} onChange={e => changeChartStyle(e)} />
                                <label htmlFor="chart-type-01" id="lbl-chart-type-01"></label>
                                <input type="radio" name="chart_type" id="chart-type-02" value="2" checked={params.chartStyle === "2"} onChange={e => changeChartStyle(e)} />
                                <label htmlFor="chart-type-02" id="lbl-chart-type-02"></label>
                                <input type="radio" name="chart_type" id="chart-type-03" value="3" checked={params.chartStyle === "3"} onChange={e => changeChartStyle(e)} />
                                <label htmlFor="chart-type-03" id="lbl-chart-type-03"></label>
                            </div>
                        </div>

                        <div id="harmonics-channels">
                            <span>Channels</span>
                            <div>
                                <fieldset>
                                    <input name="chb-u" type='checkbox' id="channels-chk-u-1" value='1' style={{ verticalAlign: "middle" }} checked={params.chartChanelU?.includes("1")} onChange={e => changeChartChannelU(e)} />
                                    <div style={{ display: "inline-block", width: "30px", height: "15px", background: "#DC0A0E", margin: "0 0 0 5px", verticalAlign: "middle" }}></div>
                                    <label style={{ display: "inline-block", marginTop: "0 !important", marginBottom: "0", verticalAlign: "middle" }} htmlFor="chk-channel-1">&nbsp;U1</label>
                                </fieldset>

                                <fieldset>
                                    <input name="chb-u" type='checkbox' id="channels-chk-u-2" value='2' style={{ verticalAlign: "middle" }} checked={params.chartChanelU?.includes("2")} onChange={e => changeChartChannelU(e)} />
                                    <div style={{ display: "inline-block", width: "30px", height: "15px", background: "#FFFF00", margin: "0 0 0 5px", verticalAlign: "middle" }}></div>
                                    <label style={{ display: "inline-block", marginTop: "0 !important", marginBottom: "0", verticalAlign: "middle" }} htmlFor="chk-channel-1">&nbsp;U2</label>
                                </fieldset>

                                <fieldset>
                                    <input name="chb-u" type='checkbox' id="channels-chk-u-3" value='3' style={{ verticalAlign: "middle" }} checked={params.chartChanelU?.includes("3")} onChange={e => changeChartChannelU(e)} />
                                    <div style={{ display: "inline-block", width: "30px", height: "15px", background: "#0074FF", margin: "0 0 0 5px", verticalAlign: "middle" }}></div>
                                    <label style={{ display: "inline-block", marginTop: "0 !important", marginBottom: "0", verticalAlign: "middle" }} htmlFor="chk-channel-1">&nbsp;U3</label>
                                </fieldset>
                            </div>

                            <div>
                                <fieldset>
                                    <input name="chb-i" type='checkbox' id="channels-chk-i-1" value='1' style={{ verticalAlign: "middle" }} checked={params.chartChanelI?.includes("1")} onChange={e => changeChartChannelI(e)} />
                                    <div style={{ display: "inline-block", width: "30px", height: "15px", background: "#FE5A57", margin: "0 0 0 5px", verticalAlign: "middle" }}></div>
                                    <label style={{ display: "inline-block", marginTop: "0 !important", marginBottom: "0", verticalAlign: "middle" }} htmlFor="chk-channel-1">&nbsp;I1</label>
                                </fieldset>

                                <fieldset>
                                    <input name="chb-i" type='checkbox' id="channels-chk-i-2" value='2' style={{ verticalAlign: "middle" }} checked={params.chartChanelI?.includes("2")} onChange={e => changeChartChannelI(e)} />
                                    <div style={{ display: "inline-block", width: "30px", height: "15px", background: "#FFFF66", margin: "0 0 0 5px", verticalAlign: "middle" }}></div>
                                    <label style={{ display: "inline-block", marginTop: "0 !important", marginBottom: "0", verticalAlign: "middle" }} htmlFor="chk-channel-1">&nbsp;I2</label>
                                </fieldset>

                                <fieldset>
                                    <input name="chb-i" type='checkbox' id="channels-chk-i-3" value='3' style={{ verticalAlign: "middle" }} checked={params.chartChanelI?.includes("3")} onChange={e => changeChartChannelI(e)} />
                                    <div style={{ display: "inline-block", width: "30px", height: "15px", background: "#8FC1FE", margin: "0 0 0 5px", verticalAlign: "middle" }}></div>
                                    <label style={{ display: "inline-block", marginTop: "0 !important", marginBottom: "0", verticalAlign: "middle" }} htmlFor="chk-channel-1">&nbsp;I3</label>
                                </fieldset>
                            </div>
                        </div>

                        <div id="harmonics-views">
                            <span>Views</span>
                            <select id="select-harmonics" style={{ borderRadius: "0" }} className="custom-select" value={params.chartViewPoint} onChange={e => changeChartViewPoint(e)}>
                                <option value="1">1</option>
                                <option value="2">2</option>
                                <option value="3">3</option>
                                <option value="4">4</option>
                                <option value="5">5</option>
                                <option value="6">6</option>
                                <option value="7">7</option>
                                <option value="8">8</option>
                                <option value="9">9</option>
                                <option value="10">10</option>
                                <option value="11">11</option>
                                <option value="12">12</option>
                                <option value="13">13</option>
                                <option value="14">14</option>
                                <option value="15">15</option>
                                <option value="16">16</option>
                                <option value="17">17</option>
                                <option value="18">18</option>
                                <option value="19">19</option>
                                <option value="20">20</option>
                                <option value="21">21</option>
                                <option value="22">22</option>
                                <option value="23">23</option>
                                <option value="24">24</option>
                                <option value="25">25</option>
                                <option value="26">26</option>
                                <option value="27">27</option>
                                <option value="28">28</option>
                                <option value="29">29</option>
                                <option value="30">30</option>
                                <option value="31">31</option>
                            </select>
                            <fieldset>
                                <input name="chk-harmonics" type='checkbox' id="view-chk-u-1" value='u' style={{ verticalAlign: "middle" }} checked={params.chartType.includes("u")} onChange={e => changeChartType(e)} />
                                <label style={{ display: "inline-block", marginTop: "0 !important", marginBottom: "0", verticalAlign: "middle" }} htmlFor="chk-channel-1">&nbsp;Harmonics (U)</label>
                            </fieldset>
                            <fieldset>
                                <input name="chk-harmonics" type='checkbox' id="view-chk-u-2" value='i' style={{ verticalAlign: "middle" }} checked={params.chartType.includes("i")} onChange={e => changeChartType(e)} />
                                <label style={{ display: "inline-block", marginTop: "0 !important", marginBottom: "0", verticalAlign: "middle" }} htmlFor="chk-channel-2">&nbsp;Harmonics (I)</label>
                            </fieldset>
                        </div>
                    </div>
                </form>

                <div id="system-data-power">
                    {params.chartType.includes("u") && <div id="chartdiv-u"></div>}

                    {params.chartType.includes("i") && <div id="chartdiv-i"></div>}

                </div>
            </>
            }
            {parseInt(param.chartType) === CONS.CHART_TYPE_LOAD.SONG_HAI_KHOANG_THOI_GIAN &&
                <>
                    <div id="chartdiv-period-u" style={{ height: "320px" }}></div>
                    <div id="chartdiv-period-i" style={{ height: "320px" }}></div>
                </>
            }
        </div>
    )

}
export default Harmonic;