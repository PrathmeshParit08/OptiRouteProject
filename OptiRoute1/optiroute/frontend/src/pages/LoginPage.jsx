import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import Popup from '../components/Popup';

const LoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [popup, setPopup] = useState({ message: '', type: '' });
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await api.post('/auth/login', { username, password });
            localStorage.setItem('token', response.data.token);
            setPopup({ message: "Login successful", type: 'success' });
            setTimeout(() => navigate('/search'), 1500);
        } catch (err) {
            let errorMsg = typeof err.response?.data === 'string'
                ? err.response.data
                : err.response?.data?.message || "Invalid credentials";

            // Remove "Error: " prefix if present for a cleaner UI
            if (errorMsg.startsWith("Error: ")) {
                errorMsg = errorMsg.replace("Error: ", "");
            }

            setPopup({ message: errorMsg, type: 'error' });
        }
    };

    return (
        <>
            <Popup
                message={popup.message}
                type={popup.type}
                onClose={() => setPopup({ message: '', type: '' })}
            />
            <div className="auth-container">
                <div className="auth-header">
                    <h2>Welcome Back</h2>
                    <p>Sign in to continue to OptiRoute</p>
                </div>

                <form onSubmit={handleLogin}>
                    <div className="form-group">
                        <label>Email</label>
                        <input
                            className="form-input"
                            type="email"
                            placeholder="Enter your email"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>Password</label>
                        <input
                            className="form-input"
                            type="password"
                            placeholder="Enter your password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    <button type="submit" className="btn-primary">Sign In</button>
                </form>

                <div className="auth-footer">
                    <p>Don't have an account? <a href="/register">Register here</a></p>
                    <p><a href="/forgot-password" style={{ fontSize: '0.9rem', color: '#a78bfa' }}>Forgot Password?</a></p>
                </div>
            </div>
        </>
    );
};

export default LoginPage;
