import './App.css';
import { Route, Switch } from 'react-router-dom';
import AccessDenied from './components/pages/access-denied/AccessDenied';
import ProtectedRouter from './components/pages/protected-route/index';
import Login from './components/pages/login/index';
import HomePage from './components/pages/home/index';
import Home from './components/pages/home-pages/index';
import Category from './components/pages/category';
import Profile from './components/pages/category/profile/index'
import ForgotPassword from './components/pages/auth/forgot-password/index';
import ResetPassword from './components/pages/auth/reset-password/index';
import ChangePasswordFirstLogin from './components/pages/auth/change-password-first-login/index';
import OverviewLoad from './components/pages/test/overview';

import Modal from 'react-modal';
import Permission from './components/pages/permission/index';

Modal.setAppElement("#root");

function App() {
  // const appUserData = useAppStore(state => state.appUserData);
  // const [role] = useState(AuthService.getRoleName());

  return (
    <Switch>
      <Route path="/login" component={Login} />
      <Route path="/forgot-password" component={ForgotPassword} />
      <Route path="/reset-password/:token" component={ResetPassword} />
      <Route path="/change-password-first-login" component={ChangePasswordFirstLogin} />
      <ProtectedRouter path="/category" component={Category} />
      <ProtectedRouter path="/permission/:userId" component={Permission} />
      <ProtectedRouter path="/profile" component={Profile} />
      <ProtectedRouter path="/test" component={OverviewLoad} />
      <ProtectedRouter path="/home" component={HomePage} />
      <ProtectedRouter path="/" component={Home} />
      <Route path="*" component={AccessDenied} />
    </Switch>
  );
}



export default App;
