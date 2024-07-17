import { Route, Switch } from 'react-router-dom';
import CustomerTool from './customer-page';
import ProjectTool from './project-page';
import Tool from './tool-page';
import AddSystemMap from './tool-page/add';

const ToolPage = () => {

    return (
        <div>
            <Switch>
                <Route path="/category/tool-page/egrid-page-add/:customerId/:projectId/:systemTypeId" component={AddSystemMap} />
                <Route path="/category/tool-page/project-page/:customerId" component={ProjectTool} />
                <Route path="/category/tool-page/egrid-page/:customerId/:projectId/:systemTypeId/:editType/systemMap/:systemMapId?deviceId=:deviceId" component={Tool} />
                <Route path="/category/tool-page/egrid-page/:customerId/:projectId/:systemTypeId/:editType/systemMap/:systemMapId" component={Tool} />
                <Route path="/category/tool-page/egrid-page/:customerId/:projectId/:systemTypeId" component={Tool} />
                <Route path="/category/tool-page" component={CustomerTool} />
            </Switch>
        </div>
    )
}

export default ToolPage;