import CONS from "./constant";

const STORE_API = {
    LIST_ALL: CONS.LOCAL_HOST + "common/storage/list?",
    LIST_BY_SUPERMANAGERID: CONS.LOCAL_HOST + "common/storage/listBySuperManagerId?",
    LIST_BY_MANAGERID: CONS.LOCAL_HOST + "common/storage/listByManagerId?",
    LIST_BY_AREAID: CONS.LOCAL_HOST + "common/storage/listByAreaId?"
}

export default STORE_API;