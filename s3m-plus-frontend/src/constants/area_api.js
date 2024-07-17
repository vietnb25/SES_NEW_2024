
import CONS from "./constant";

const AREA_API = {
    LIST: CONS.LOCAL_HOST + "common/area/list",
    LIST_BY_CUSTOMER_AND_MANAGER: CONS.LOCAL_HOST + "common/area/getAreaByCustomerIdAndManagerId/",
    ADD : CONS.LOCAL_HOST + "common/area/add",
    SEARCH: CONS.LOCAL_HOST + "common/area/search?keyword=",
    GET: CONS.LOCAL_HOST + "common/area/",
    UPDATE: CONS.LOCAL_HOST +"common/area/update/",
    DELETE: CONS.LOCAL_HOST +"common/area/delete/",
    AREA_SELECT_DEVICE: CONS.LOCAL_HOST + "common/select-device/area",
}

export default AREA_API;