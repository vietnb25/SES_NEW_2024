import CONS from "./constant";

const PLANT_HOST = CONS.LOCAL_HOST + "common/evn/plant/";

const BASE_API_PLANT = {
    PLANT: {
        LIST: PLANT_HOST + "list",
        SEARCH: PLANT_HOST + "search",
    }
}
export default BASE_API_PLANT;