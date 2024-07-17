// const HOST = "http://localhost:9999/";
import CONS from "./constant";

const PROJECT_API = {
    LIST: CONS.LOCAL_HOST + "common/project/list",
    SEARCH: CONS.LOCAL_HOST + "common/project/search/",
    ADD: CONS.LOCAL_HOST + "common/project/add",
    GET: CONS.LOCAL_HOST + "common/project/",
    UPDATE: CONS.LOCAL_HOST + "common/project/edit",
    DELETE: CONS.LOCAL_HOST + "common/project/delete/",
    LIST_TOOL:CONS.LOCAL_HOST + "common/project/list-tool",
    LIST_TOOL_SEARCH:CONS.LOCAL_HOST + "common/project/list-search",
    UPDATE_IMAGE:CONS.LOCAL_HOST + "common/project/update-image/",
    LIST_IDS:CONS.LOCAL_HOST + "common/project/listIds",
    IDS:CONS.LOCAL_HOST + "common/project/ids",
}

export default PROJECT_API;