import AreaComponent from "./area";
import Cable from "./cable";
import Customer from "./customer";
import DeviceComponent from "./device";
import ManagerComponent from "./manager";
import ProjectComponent from "./project";
import SettingComponent from "./setting";
import SuperManager from "./super-manager";
import UserComponent from "./user";
import ToolPage from './customer-tool/index';
import i18n from '../../../i18n';
import ObjectType from "./object-type";
import LoadType from "./load-type";
import ObjectTypeComponent from "./object-type";
import ObjectTypeMstComponent from "./object-type-mst";
import DeviceTypeComponent from "./device-type";

const CategoryRouters = [
    {
        component: Customer,
        path: '/category/customer',
        children: [

        ],
        link: {
            title: i18n.t('content.customer'),
            icon: "fas fa-user-tie"
        },
        actions: []
    },
    {
        component: SuperManager,
        path: '/category/super-manager',
        children: [

        ],
        link: {
            title: i18n.t('content.super_manager'),
            icon: "far fa-compass"
        },
        actions: []
    },
    {
        component: ManagerComponent,
        path: '/category/manager',
        children: [

        ],
        link: {
            title: i18n.t('content.manager'),
            icon: "far fa-building"
        },
        actions: []
    },
    {
        component: AreaComponent,
        path: '/category/area',
        children: [

        ],
        link: {
            title: i18n.t('content.area'),
            icon: "far fa-clone"
        },
        actions: []
    },
    {
        component: ProjectComponent,
        path: '/category/project',
        children: [

        ],
        link: {
            title: "Dự án",
            icon: "far fa-file-lines"
        },
        actions: []
    },
    {
        component: DeviceComponent,
        path: '/category/device',
        children: [

        ],
        link: {
            title: "Điểm đo",
            icon: "fas fa-server"
        },
        actions: []
    },
    {
        component: UserComponent,
        path: '/category/users',
        children: [

        ],
        link: {
            title: "Tài khoản",
            icon: "far fa-circle-user"
        },
        actions: []
    },

    {
        component: Cable,
        path: '/category/cables',
        children: [

        ],
        link: {
            title: "Cáp",
            icon: "fas fa-lines-leaning"
        },
        actions: []
    },
    {
        component: ToolPage,
        path: '/category/tool-page',
        children: [

        ],
        link: {
            title: "Sơ đồ 1 sợi",
            icon: "fas fa-pen-ruler"
        },
        actions: []
    },
    {
        component: ObjectTypeComponent,
        path: '/category/object-type',
        children: [

        ],
        link: {
            title: "Đối tượng giám sát",
            icon: "fas fa-gear"
        },
        actions: []
    },
    {
        component: ObjectTypeMstComponent,
        path: '/category/object-type-mst',
        children: [

        ],
        link: {
            title: "Loại đối tượng giám sát",
            icon: "fas fa-gear"
        },
        actions: []
    },
    {
        component: DeviceTypeComponent,
        path: '/category/device-type',
        children: [

        ],
        link: {
            title: "Thiết bị đo lường",
            icon: "fas fa-gear"
        },
        actions: []
    },
    {
        component: LoadType,
        path: '/category/load-type',
        children: [

        ],
        link: {
            title: "Loại phụ tải",
            icon: "fa-solid fa-microchip"
        },
        actions: []
    },
    {
        component: SettingComponent,
        path: '/category/setting',
        children: [

        ],
        link: {
            title: "Cài đặt",
            icon: "fas fa-gear"
        },
        actions: []
    }
];

export default CategoryRouters;