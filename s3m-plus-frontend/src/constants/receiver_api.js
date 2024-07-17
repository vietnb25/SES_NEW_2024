// const HOST = "http://localhost:9999/";
import CONS from "./constant";

const RECEIVER_HOST = CONS.LOCAL_HOST + "common/receiver/";

const RECEIVER_API = {
    PV: {
        LIST: CONS.LOCAL_HOST + "pv/receivers",
        SAVE: CONS.LOCAL_HOST + "pv/receiver/save",
        GET: CONS.LOCAL_HOST + "pv/receiver/getWarning",
        ADD: CONS.LOCAL_HOST + "pv/receiver/add",
        UPDATE: CONS.LOCAL_HOST + "pv/receiver/update",
        DELETE: CONS.LOCAL_HOST + "pv/receiver/delete"
    },

    LOAD: {
        LIST: CONS.LOCAL_HOST + "load/receivers",
        SAVE: CONS.LOCAL_HOST + "load/receiver/save",
        GET: CONS.LOCAL_HOST + "load/receiver/getWarning",
        ADD: CONS.LOCAL_HOST + "load/receiver/add",
        UPDATE: CONS.LOCAL_HOST + "load/receiver/update",
        DELETE: CONS.LOCAL_HOST + "load/receiver/delete"
    },

    GRID: {
        LIST: CONS.LOCAL_HOST + "grid/receivers",
        SAVE: CONS.LOCAL_HOST + "grid/receiver/save",
        GET: CONS.LOCAL_HOST + "grid/receiver/getWarning",
        ADD: CONS.LOCAL_HOST + "grid/receiver/add",
        UPDATE: CONS.LOCAL_HOST + "grid/receiver/update",
        DELETE: CONS.LOCAL_HOST + "grid/receiver/delete"
    },

    LIST: RECEIVER_HOST + "",
    SAVE: RECEIVER_HOST + "save",
    GET: RECEIVER_HOST + "getWarning",
    ADD: RECEIVER_HOST + "add",
    UPDATE: RECEIVER_HOST + "update",
    DELETE: RECEIVER_HOST + "delete"
}

export default RECEIVER_API;

