import { createContext, useContext, useState } from 'react';

const UserContext = createContext();

export const UserProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [aesKey, setAesKey] = useState(null);

    
    const login = (userData) => {
        setUser(userData);
    };

    const setAesBase64Key = (base64Key) =>{
        setAesKey(base64Key);
    }

    const logout = () => {
        setUser(null);
        setAesKey(null)
    };

    return (
        <UserContext.Provider value={{ user, login, logout, aesKey,setAesBase64Key }}>
            {children}
        </UserContext.Provider>
    );
};

export const useUser = () => {
    return useContext(UserContext);
};