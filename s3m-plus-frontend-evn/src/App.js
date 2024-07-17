import './App.css';
import { Route, Switch } from 'react-router-dom';
import AccessDenied from './components/pages/access-denied/AccessDenied';
import ProtectedRouter from './components/pages/protected-route/index';
import Login from './components/pages/login/index';
import Home from './components/pages/home/index';
import ForgotPassword from './components/pages/auth/forgot-password/index';
import ResetPassword from './components/pages/auth/reset-password/index';
import ChangePasswordFirstLogin from './components/pages/auth/change-password-first-login/index';

function App() {
  return (
    <Switch>
      <Route path="/login" component={Login} />
      <Route path="/forgot-password" component={ForgotPassword} />
      <Route path="/reset-password/:token" component={ResetPassword} />
      <Route path="/access-denied" component={AccessDenied} />
      <Route path="/change-password-first-login" component={ChangePasswordFirstLogin} />
      <ProtectedRouter path="/" component={Home} />
    </Switch>
  );
}



export default App;
