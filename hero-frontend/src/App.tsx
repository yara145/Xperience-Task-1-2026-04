import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate, useNavigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Home from './components/Home';
import EventCreation from './components/EventCreation';
import Dashboard from './components/Dashboard';
import RSVPResponse from './components/RSVPResponse';
import Login from './components/Login';
import Register from './components/Register';
import './App.css';

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  return <>{children}</>;
}

function NavBar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="nav-container">
        <Link to="/" className="nav-brand">
          📅 Event RSVP Manager
        </Link>
        <ul className="nav-menu">
          {user ? (
            <>
              <li><Link to="/">My Events</Link></li>
              <li><Link to="/create">Create Event</Link></li>
              <li className="nav-user">Hello, {user.name}</li>
              <li>
                <button className="nav-logout-btn" onClick={handleLogout}>
                  Logout
                </button>
              </li>
            </>
          ) : (
            <>
              <li><Link to="/login">Login</Link></li>
              <li><Link to="/register">Register</Link></li>
            </>
          )}
        </ul>
      </div>
    </nav>
  );
}

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="app">
          <NavBar />

          <main className="main-content">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/" element={<ProtectedRoute><Home /></ProtectedRoute>} />
              <Route path="/create" element={<ProtectedRoute><EventCreation /></ProtectedRoute>} />
              <Route path="/event/:eventId" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
              <Route path="/rsvp/:token" element={<RSVPResponse />} />
            </Routes>
          </main>

          <footer className="footer">
            <p>&copy; 2026 Event RSVP Manager. All rights reserved.</p>
          </footer>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
