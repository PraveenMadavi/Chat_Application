// routes/AppRoutes.jsx
import { Routes, Route } from 'react-router-dom';
import AuthForms from '../Components/AuthForms';
import AppChatBox from '../Components/AppChatBox';

const AppRoutes = () => {
    return (
        <Routes>
            <Route exact path="/AppChatBox" element={<AppChatBox />} />
             <Route exact path="/" element={<AuthForms />} />
            {/* Add more routes as needed */}
        </Routes>
    );
};

export default AppRoutes;
