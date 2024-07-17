import { Route, Switch } from 'react-router-dom';
import SettingRouters from "./infomation.routes";

const SettingComponent = () => {
    return (
        <Switch>
        {
             SettingRouters.map((route, i) => {
                return <Route key={i} path={route.path} component={route.component} />
            })
        }
        </Switch>
    )
}
export default SettingComponent;