import CONS from "./constant";

const LOAD_TYPE = CONS.LOCAL_HOST + "common/loadType/";

const BASE_API_LOAD_TYPE = {
    LIST_LOAD_TYPE: {
        LIST: LOAD_TYPE + "list",
        ADD_LOAD_TYPE: LOAD_TYPE + "add/",
        UPDATE_LOAD_TYPE: LOAD_TYPE + "update/",
        GET_LOAD_TYPE_BY_ID: LOAD_TYPE + "getLoadTypeById",
        SEARCH: LOAD_TYPE + "search?keyword=",
        LIST_MST: LOAD_TYPE + "listLoadType",
        LIST_LOAD_BY_PROJECT_ID: LOAD_TYPE + "listLoad/",
        DELETE_LOAD_TYPE_BY_ID: LOAD_TYPE + "delete/",
        CHECK_LOAD_TYPE_DEVICE: LOAD_TYPE + "checkLoadTypeDevice?id=",
        LIST_LOAD_TYPE_BY_PROJECT_AND_SYSTEM_TYPE: LOAD_TYPE + "list-load-type-by-project",

    }
}

export default BASE_API_LOAD_TYPE;