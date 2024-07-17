import CONS from "./constant";

const BASE_API = CONS.LOCAL_HOST + "common/"

const MATERIAL_API = {
    LIST_MATERIAL_TYPE: BASE_API + "material-type/list",
    LIST_MATERIAL_VALUE: BASE_API + "material-value/list-by-project-and-material-type",
    ADD_OR_UPDATE_MATERIAL_VALUE: BASE_API + "material-value/add-or-update",
}

export default MATERIAL_API;
