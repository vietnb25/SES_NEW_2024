import SettingCost from "../setting/setting-cost/setting-cost";
import ListSetting from "./list/index2";

const SettingRouters = [
    
    {
        component: ListSetting,
        path: '/:customerId/setting-warning',
        link: {
            title: "Danh sách cài đặt",
            icon: "fas fa-user-tie"
        },
        action: "READ"
    },{
        component: ListSetting,
        path: '/:customerId/:projectId/setting-warning',
        link: {
            title: "Danh sách cài đặt",
            icon: "fas fa-user-tie"
        },
        action: "READ"
    },
    {
        component: SettingCost,
        path: '/:customerId/:projectId/setting-cost',
        link: {
            title: "Danh sách cài đặt",
            icon: "fas fa-user-tie"
        },
        action: "READ"
    },
    {
        component: SettingCost,
        path: '/:customerId/setting-warning',
        link: {
            title: "Danh sách cài đặt",
            icon: "fas fa-user-tie"
        },
        action: "READ"
    },
];

export default SettingRouters;