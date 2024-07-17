import CONS from "./constant";

const CABLE = CONS.LOCAL_HOST + "common/cable/"

const CABLE_API = {
    CABLE : {
        LIST: CABLE + "list",
        ADD: CABLE + "add",
        GETBYID: CABLE + "/",
        EDIT : CABLE + "update/",
        DELETE: CABLE + "delete/",
        SEARCH : CABLE + "search?keyword="
    }
}

export default CABLE_API;