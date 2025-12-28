import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // Importă useNavigate
import './LoginPage.css';
import axios from 'axios';

// LoginPage primește o funcție onLogin ca prop
export default function LoginPage({ onLogin }) {
  // const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate(); // Hook pentru a redirecționa

    const handleLogin = async (event) => {
      event.preventDefault();
      try {
      
        const config = {
          headers: {
            'Content-Type': 'application/json',
          },
        };

      const { data } = await axios.post(
         'http://localhost:8081/api/auth/login', // <-- Schimbă portul în 8080
         { email, password }, 
          config
    );
    console.log("LOGIN RESPONSE:", data);

    // încearcă să găsești token-ul indiferent cum se numește
    const token = data.token || data.jwt || data.jwtToken || data.accessToken;
    if (token) {
      localStorage.setItem("token", token);
    }

    // păstrează și currentUser pentru rol/email/etc
    localStorage.setItem("currentUser", JSON.stringify(data));

    onLogin(data);
    // --- LOGICA DE REDIRECȚIONARE EXPLICITĂ ---
        let redirectPath = '/'; 
        
        // **Verificați dacă data.role există și este corect**
        switch (data.role) {
            case 'ADMINISTRATOR':
                redirectPath = '/administrator/dashboard';
                break;
            case 'ADMIN':
                redirectPath = '/admin/dashboard'; // <-- ACUM REDIRECȚIONĂM LA RUTA ADMIN
                break;
            case 'BENEFICIAR':
                redirectPath = '/beneficiar/dashboard';
                break;
            case 'PAZNIC':
                redirectPath = '/paznic/dashboard';
                break;
            default:
                redirectPath = '/';
        }
        
        navigate(redirectPath);
      } catch (err) {
      setError(err.response?.data?.message || 'A apărut o eroare. Vă rugăm să încercați din nou.');
    }
  };

  return (
    <div className="login-page">
      <div className="login-container">
        <h2>Log In</h2>
        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              placeholder="Introdu email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}

            />
          </div>
          <div className="form-group">
            <label htmlFor="password">Parolă</label>
            <input
              type="password"
              id="password"
              name="password"
              placeholder="Introdu parola"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          {error && <p className="error-message">{error}</p>}
          <button type="submit" className="submit-btn">
            Log In
          </button>
        </form>
      </div>
    </div>
  );
}