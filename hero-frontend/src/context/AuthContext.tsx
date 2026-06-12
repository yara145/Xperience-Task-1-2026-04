import React, { createContext, useContext, useState } from 'react';
import { AuthAPI } from '../services/api';

interface AuthUser {
  hostId: number;
  name: string;
  email: string;
  token: string;
}

interface AuthContextType {
  user: AuthUser | null;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function useAuth(): AuthContextType {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => {
    const stored = localStorage.getItem('auth');
    return stored ? JSON.parse(stored) : null;
  });

  const login = async (email: string, password: string) => {
    const data = await AuthAPI.login({ email, password });
    const authUser: AuthUser = { hostId: data.hostId, name: data.name, email: data.email, token: data.token };
    localStorage.setItem('auth', JSON.stringify(authUser));
    setUser(authUser);
  };

  const register = async (name: string, email: string, password: string) => {
    const data = await AuthAPI.register({ name, email, password });
    const authUser: AuthUser = { hostId: data.hostId, name: data.name, email: data.email, token: data.token };
    localStorage.setItem('auth', JSON.stringify(authUser));
    setUser(authUser);
  };

  const logout = () => {
    AuthAPI.logout().catch(() => {});
    localStorage.removeItem('auth');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
