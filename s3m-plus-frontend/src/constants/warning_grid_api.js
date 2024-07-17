import CONS from "./constant";

const BASE_API = CONS.LOCAL_HOST + "grid/warning"

const WARNING_GRID_API = {
    GET_WARNINGS : BASE_API,
    GET_WARNINGS_BY_WARNING_TYPE: BASE_API + "/type/",
    GET_INFOR_BY_DEVICEID: BASE_API + "/detail",
    UPDATE: BASE_API + "/update",
    DOWNLOAD: BASE_API + "/download",
    GET_WARNINGS_DEVICE_BY_WARNING_TYPE: BASE_API + "/operation/type/"
}

export default WARNING_GRID_API
