import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import AdminDashboard from './admin/AdminDashboard';
import Header from './admin/Header';
import HomePage from './admin/HomePage';
import LoginPage from './admin/LoginPage';
import Solicitari  from './admin/Solicitari';
import SolicitariDetalii from './admin/SolicitariDetalii';
import BeneficiarDashboard from './beneficiar/BeneficiarDashboard';
import SolicitariB from './beneficiar/SolicitariB';
import AdaugaSolicitare from './beneficiar/AdaugaSolicitare';
import PaznicDashboard from './paznic/PaznicDashboard';
import AdaugaAngajat from "./admin/AdaugaAngajat";
import AdaugaFirma from "./admin/AdaugaFirma";
import PontarePage from './paznic/PontarePage';
import ProcesVerbal from './documents/ProcesVerbal';

function Dashboard({ user }) {
  let content;

  switch (user.role) {
    case 'ADMINISTRATOR':
      content = <AdminDashboard/>
      break;
    case 'ADMIN':
      content = <AdminDashboard />;
      break;
    case 'BENEFICIAR':
      content = <BeneficiarDashboard />;
      break;
    case 'PAZNIC':
      content = <PaznicDashboard />;
      break;
    default:
      content = (
        <p style={{ padding: '50px', textAlign: 'center' }}>
          Rol necunoscut.
        </p>
      );
  }

  return (
    <div>
      {content}
      {/* <div style={{ textAlign: 'center', marginTop: '20px' }}>
        <button onClick={onLogout}>Deconectare</button>
      </div> */}
    </div>
  );
}

export default function App() {
  const [currentUser, setCurrentUser] = useState(null);

  const [solicitari, setSolicitari] = useState({ prelucrata: [], inCurs: [], rezolvata: [] });

  const [solicitariBeneficiar, setSolicitariBeneficiar] = useState([]);

   // La pornirea aplicației, verificăm dacă există user salvat în localStorage
  useEffect(() => {
    const savedUser = localStorage.getItem("currentUser");
    if (savedUser) {
      setCurrentUser(JSON.parse(savedUser));
    }
  }, []);

  const handleLogin = (user) => {
    setCurrentUser(user);
    localStorage.setItem("currentUser", JSON.stringify(user)); // salvare în localStorage
  };

  const handleLogout = () => {
    setCurrentUser(null);
    localStorage.removeItem("currentUser"); // ștergere din localStorage
  };

  return (
    <Router>
      <Header user={currentUser} onLogout={handleLogout} />
      <Routes>
        <Route path="/" element={currentUser ? <Dashboard user={currentUser} /> : <HomePage />} />
        <Route path="/login" element={<LoginPage onLogin={handleLogin} />} />

        {/* ✅ Ruta pentru Pontare */}
        <Route 
          path="/pontare" 
          element={
            currentUser && currentUser.role === "PAZNIC" 
              ? <PontarePage /> 
              : <p style={{ padding: "50px", textAlign: "center" }}>
                  Acces interzis.
                </p>
          } 
        />

        <Route path="/solicitari" element={<Solicitari solicitari={solicitari} setSolicitari={setSolicitari} />} />
        <Route path="/solicitari/:id" element={<SolicitariDetalii solicitari={solicitari} setSolicitari={setSolicitari} />} />
        <Route path="/adauga-angajat" element={<AdaugaAngajat />} />
        <Route path="/adauga-firma" element={<AdaugaFirma />} />
        {/* Rutele pentru Beneficiar (protejate simplu) */}
        <Route path="/beneficiar" element={ currentUser?.role === "beneficiar" ? <BeneficiarDashboard /> : <p>Acces interzis.</p> } />
        <Route path="/solicitariB" element={ currentUser?.role === "beneficiar" ? <SolicitariB solicitari={solicitariBeneficiar} /> : <p>Acces interzis.</p> } />
        <Route path="/adauga-solicitare" element={ currentUser?.role === "beneficiar" ? <AdaugaSolicitare setSolicitari={setSolicitariBeneficiar} /> : <p>Acces interzis.</p> } />

        {/* Ruta pentru Proces Verbal */}
        <Route path="/proces-verbal/:pontajId" element={<ProcesVerbal />} />

        <Route 
          path="/admin/dashboard" 
          element={currentUser?.role === 'administrator' ? <AdminDashboard /> : <p>Acces interzis. Doar pentru Administrator.</p>} 
        />

        <Route 
          path="/paznic/dashboard" 
          element={currentUser?.role === 'administrator' ? <PaznicDashboard /> : <p>Acces interzis. Doar pentru Administrator.</p>} 
      />
      </Routes>
    </Router>
  );
}