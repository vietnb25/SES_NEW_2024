import CONS from "./constant";

const BASE_API = CONS.LOCAL_HOST + "common/manufacture"

const MANUFACTURE_API = {
    GET_MANUFACTURES: BASE_API,
    GET_DATA_PQS_MANUFACTURES: BASE_API + "/getDataPqs",
    ADD_MANUFACTURES: BASE_API + "/add/",
    EXPORT_REPORT: BASE_API + "/export",
    GET_LIST_MANUFACTURE_SHIFT: BASE_API + "-shift/list",
    GET_LIST_MANUFACTURE_PRODUCTION: BASE_API + "-shift/list/production-step",
    INSERT_MANUFACTURE_PRODUCTION: BASE_API + "-shift/add-manufacture",
    GET_LIST_DATA_EP_BY_SHIFT_AND_VIEW_TIME: BASE_API + "-shift/list/ep-by-shift",
    GET_LIST_MANUFACTURE_DETAIL_BY_MANUFACTURE_AND_VIEW_TIME: BASE_API + "-shift/list/manufacture-detail",
    ADD_OR_UPDATE_MANUFACTURE_DETAIL: BASE_API + "-shift/add/manufacture-detail",
    UPDATE_OR_UPDATE_MANUFACTURE_DETAIL_REVENUE: BASE_API + "-shift/update/manufacture-detail/revenue",
    DELETE_MANUFACTURE: BASE_API + "-shift/delete/manufacture",
    DOWLOAD_MANUFACTURE_DETTAIL: BASE_API + "-shift/dowload-list-detail",
}

export default MANUFACTURE_API
