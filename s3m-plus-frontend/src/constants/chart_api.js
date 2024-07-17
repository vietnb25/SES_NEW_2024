import CONS from "./constant";

const BASE_API = CONS.LOCAL_HOST + "common/chart"

const CHART_API = {
    GET_CHARTS: BASE_API,
    GET_CHART_LOAD: BASE_API + "/load",
    GET_CHART_SOLAR: BASE_API + "/solar",
    GET_CHART_GRID: BASE_API + "/grid",
    GET_CHART_LOAD_COST: BASE_API + "/load-cost",
    GET_CHART_SOLAR_COST: BASE_API + "/solar-cost",
    GET_CHART_GRID_COST: BASE_API + "/grid-cost",
    GET_CHART_LOAD_HEAT: BASE_API + "/load-heat",
    GET_CHART_SOLAR_HEAT: BASE_API + "/solar-heat",
    GET_CHART_GRID_HEAT: BASE_API + "/grid-heat",
    GET_CHART_LOAD_POWER: BASE_API + "/load-power",
    GET_CHART_SOLAR_POWER: BASE_API + "/solar-power",
    GET_CHART_GRID_POWER: BASE_API + "/grid-power",
    EXPORT_DATA_CHART_LOAD: BASE_API + "/export-data-chart-load",
    EXPORT_DATA_CHART_POWER: BASE_API + "/export-data-chart-power",
    EXPORT_DATA_CHART_COST: BASE_API + "/export-data-chart-cost",
    GET_CHART_TEMPERATURE: BASE_API + "/temperature",
    GET_CHART_SANKEY: BASE_API + "/sankey",
    GET_CHART_DISCHARGE_INDICATOR: BASE_API + "/discharge-indicator",
    GET_CHART_LOAD_COMPARE: BASE_API + "/load-compare",
    GET_CHART_ENERGY_PLAN: BASE_API + "/energy-plan",
}

export default CHART_API
