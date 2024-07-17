import CONS from "./constant";

const SUPER_MANAGER_HOST = CONS.LOCAL_HOST + "common/super-manager/";

const BASE_API_SUPER_MANAGER = {
    SUPERMANAGER: {
        DETAILS: SUPER_MANAGER_HOST,
        LIST: SUPER_MANAGER_HOST + "list",
        ADD: SUPER_MANAGER_HOST + "add",
        UPDATE: SUPER_MANAGER_HOST + "update/",
        SEARCH: SUPER_MANAGER_HOST + "search?keyword=",
        DELETE: SUPER_MANAGER_HOST + "delete/"
    }
}

export default BASE_API_SUPER_MANAGER;