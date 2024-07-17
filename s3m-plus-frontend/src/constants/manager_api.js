// const HOST = "http://localhost:9999/";

import CONS from './constant';

const MANAGER_HOST = "common/manager/"

const MANAGER_API = {
    LIST: CONS.LOCAL_HOST + MANAGER_HOST  + "list",
    LIST_BY_SUPER_MANAGER: CONS.LOCAL_HOST + MANAGER_HOST  + "list/",
    SEARCH: CONS.LOCAL_HOST + MANAGER_HOST  + "search?keyword=",
    ADD : CONS.LOCAL_HOST + MANAGER_HOST + "add",
    DETAIL: CONS.LOCAL_HOST + MANAGER_HOST + "",
    UPDATE: CONS.LOCAL_HOST + MANAGER_HOST + "edit/",
    DELETE: CONS.LOCAL_HOST + MANAGER_HOST  + "delete/",
    LIST_BY_CUSTOMER: CONS.LOCAL_HOST + MANAGER_HOST + "listManager/"
}

export default MANAGER_API;