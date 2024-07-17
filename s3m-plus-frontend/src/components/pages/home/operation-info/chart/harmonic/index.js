import React, { useState } from "react";
import { useEffect } from "react";
import { useParams } from "react-router-dom";
import operationInformationService from "../../../../../../services/OperationInformationService";
import * as am5 from "@amcharts/amcharts5";
import * as am5xy from "@amcharts/amcharts5/xy";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";

const Harmonic = () => {

    const idOfDevice = useParams();

    const [params, setParams] = useState({
        chartStyle: "1",
        chartChanelU: ["1", "2", "3"],
        chartChanelI: ["1", "2", "3"],
        chartViewPoint: "31",
        chartType: ["u", "i"]
    })

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

    const getDataChart = async () => {
        let res = await operationInformationService.getDataChartHarmonic(idOfDevice.deviceId, params);
        drawChart(res.data);
    };

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
                            tooltipText: "{name}, {valueY} [[%]]",
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
                            tooltipText: "{name}, {valueY} [[%]]",
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
                                    labelText: "{name}, {valueY} [[%]]"
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
                                    labelText: "{name}, {valueY} [[%]]"
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
                                    labelText: "{name}, {valueY} [[%]]"
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
                                    labelText: "{name}, {valueY} [[%]]"
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

    useEffect(() => {
        document.title = "Biểu đồ Harmonic";
        getDataChart();
    }, [params, idOfDevice.deviceId])

    return (

        <div className="tab-content">
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
        </div>
    )

}
export default Harmonic;