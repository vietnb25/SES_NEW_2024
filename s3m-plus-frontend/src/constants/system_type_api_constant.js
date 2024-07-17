import CONS from "./constant";

const SYSTEM_TYPE_HOST = CONS.LOCAL_HOST + "common/system-type/";

const BASE_API_SYSTEM_TYPE = {
    SYSTEMTYPE: {
        LIST: SYSTEM_TYPE_HOST + "list",
        DETAILS: SYSTEM_TYPE_HOST
    }
}

export default BASE_API_SYSTEM_TYPE;