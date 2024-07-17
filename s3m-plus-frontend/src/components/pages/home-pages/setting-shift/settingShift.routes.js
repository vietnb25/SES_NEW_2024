import EditSettingShift from "./edit";
import ListSettingShift from "./list";

const SettingShiftRouters = [
    {
        component: EditSettingShift,
        path: '/:customerId/setting-shift/:id/edit',
        link: {
            title: "Cập nhật cài đặt",
            icon: "fas fa-user-tie"
        },
        action: "UPDATE"
    },
    {
        component: ListSettingShift,
        path: '/:customerId/setting-shift',
        link: {
            title: "Danh sách cài đặt",
            icon: "fas fa-user-tie"
        },
        action: "READ"
    }
];

export default SettingShiftRouters;