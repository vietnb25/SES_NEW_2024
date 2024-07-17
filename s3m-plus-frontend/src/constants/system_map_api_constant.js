import CONS from "./constant";

const SYSTEM_TYPE_HOST = CONS.LOCAL_HOST + "common/systemMap/";

const BASE_API_SYSTEM_MAP = {
    ALL: SYSTEM_TYPE_HOST + "all",
    LIST: SYSTEM_TYPE_HOST + "list",
    ADD: SYSTEM_TYPE_HOST + "add",
    UPDATE: SYSTEM_TYPE_HOST + "update",
    DEVICE_EMPTY: SYSTEM_TYPE_HOST + "deviceEmpty",
    DEVICE_ALREADY: SYSTEM_TYPE_HOST + "deviceAlReady",
    DELETE: SYSTEM_TYPE_HOST + "delete",
    DATAJSON: SYSTEM_TYPE_HOST + "getDataJson",
    SYSTEM_INFO_TIME: SYSTEM_TYPE_HOST + "getSystemInfoTime",
    DETAILS: SYSTEM_TYPE_HOST,
    SYSTEM_MAP_BY_PROJECT: SYSTEM_TYPE_HOST + "/getSystemMapByProjectId",
    UPDATE_SYSTEM_DEVICE: SYSTEM_TYPE_HOST + "updateSystemDevice"
}

export default BASE_API_SYSTEM_MAP;