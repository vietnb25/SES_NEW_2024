import Overview from "./overview";
import Chart from "./chart";
import Diagnose from "./diagnose";
import ReceiverWarning from "./receiver-warning";
import Report from "./report";
import SettingWaring from "././setting-warning";
import SettingShift from "././setting-shift";
import { supports } from "@amcharts/amcharts5/.internal/core/util/Utils";
import SystemMap from "./systemMap";
import Warning from "./warning";
import DeviceInfor from "././device-information";
import SystemMapComponent from "./systemMap/systemMap";
import Support from "./support";
import Plan from "./plan";
import DataSimulation from "./setting/data-simulation";
import Report2 from "./report/index2";
import LandmarksEnergyPlans from "./setting/landmarks-energy-plans/landmarks-energy-plans";
import SettingCost from "./setting/setting-cost/setting-cost";
import SettingComponent from "./setting/setting-component";
import Manufacture2 from "./manufacture/manufacture";

const HomeRouters = [
  {
    component: Chart,
    path: "/:customerId/chart",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: SettingComponent,
    path: "/:customerId/setting",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: SettingComponent,
    path: "/:customerId/:projectId/setting",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: Chart,
    path: "/:customerId/:projectId/chart",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: Manufacture2,
    path: "/:customerId/manufacture",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: Manufacture2,
    path: "/:customerId/:projectId/manufacture",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: ReceiverWarning,
    path: "/:customerId/receiver-warning",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: Report2,
    path: "/:customerId/:projectId/report",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: Report2,
    path: "/:customerId/report",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: Support,
    path: "/:customerId/support",
    children: [],
    link: {
      title: "Trợ giúp",
      icon: "",
    },
    actions: [],
  },
  {
    component: Support,
    path: "/:customerId/:projectId/support",
    children: [],
    link: {
      title: "Trợ giúp",
      icon: "",
    },
    actions: [],
  },
  {
    component: SystemMap,
    path: "/:customerId/system-map",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: SystemMap,
    path: "/:customerId/:projectId/system-map",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: SystemMapComponent,
    path: "/:customerId/:projectSearchId/system-map/:type/:projectId",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: SystemMapComponent,
    path: "/:customerId/:projectSearchId/system-map/:type/:projectId/:systemMapId",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: SystemMapComponent,
    path: "/:customerId/system-map/:type/:projectId",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: SystemMapComponent,
    path: "/:customerId/system-map/:type/:projectId/:systemMapId",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: Warning,
    path: "/:customerId/warning",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: Warning,
    path: "/:customerId/:projectId/warning",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: SettingCost,
    path: "/:customerId/setting-cost",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: SettingCost,
    path: "/:customerId/:projectId/setting-cost",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: DeviceInfor,
    path: "/:customerId/:projectId/device-information",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: DeviceInfor,
    path: "/:customerId/device-information",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: Plan,
    path: "/:customerId/plan",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },

  {
    component: Plan,
    path: "/:customerId/:projectId/plan",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: Overview,
    path: "/:customerId/:projectId/",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: Overview,
    path: "/:customerId",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },
  {
    component: Overview,
    path: "/",
    children: [],
    link: {
      title: "",
      icon: "",
    },
    actions: [],
  },

];

export default HomeRouters;
