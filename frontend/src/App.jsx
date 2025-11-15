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

function Dashboard({ user, onLogout }) {
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
      content = <p style={{ padding: '50px', textAlign: 'center' }}>
        Rol necunoscut.
      </p>;
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

  const [solicitari, setSolicitari] = useState({
    prelucrata: [
      { id: 1, titlu: "Sesizare exemplu", data: "14/08/2025", firma: "Firma A", descriere: "O defecțiune a fost raportată la sistemul de supraveghere video de la poarta de nord.", pasi: "S-a contactat firma de mentenanță.", dataFinalizare: null }
    ],
    inCurs: [
      { id: 2, titlu: "Incident minor", data: "13/08/2025", firma: "Firma B", descriere: "Un vizitator neînregistrat a încercat să intre în clădire.", pasi: "Agentul de pază a reținut persoana și a anunțat poliția.", dataFinalizare: null }
    ],
    rezolvata: [
      { id: 3, titlu: "Alarmă falsă", data: "12/08/2025", firma: "Firma A", descriere: "Alarma de incendiu a pornit din cauza aburului de la bucătărie.", pasi: "S-a resetat sistemul de alarmă.", dataFinalizare: "12/08/2025" }
    ]
  });

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
        <Route
          path="/"
          element={currentUser ? <Dashboard user={currentUser} /> : <HomePage />}
        />
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

        <Route 
          path="/solicitari" 
          element={<Solicitari solicitari={solicitari} setSolicitari={setSolicitari} />} 
        />
        {/* Adăugăm noua rută pentru detalii */}
        <Route 
          path="/solicitari/:id" 
          element={<SolicitariDetalii solicitari={solicitari} setSolicitari={setSolicitari} />} 
        />
               {/* --- BENEFICIAR --- */}
        
        <Route path="/adauga-angajat" element={<AdaugaAngajat />} />   {/* ✅ mutat în interiorul Routes */}
        <Route path="/adauga-firma" element={<AdaugaFirma />} />   {/* ✅ mutat în interiorul Routes */}
        <Route 
          path="/beneficiar" 
          element={
            currentUser && currentUser.role === "BENEFICIAR" 
              ? <BeneficiarDashboard /> 
              : <p style={{ padding: "50px", textAlign: "center" }}>
                  Acces interzis.
                </p>
          } 
        />
        {/* --- ADAUGĂ NOILE RUTE PENTRU SOLICITĂRI BENEFICIAR --- */}
        <Route
        path="/solicitariB"
        element={
          currentUser && currentUser.role === "BENEFICIAR"
            // MODIFICAT: Folosim SolicitariB și trimitem doar prop-ul necesar
            ? <SolicitariB solicitari={solicitariBeneficiar} /> 
            : <p style={{ padding: "50px", textAlign: "center" }}>
                Acces interzis.
              </p>
        }
      />

      {/* Ruta pentru adăugarea unei solicitări (rămâne la fel) */}
      <Route
        path="/adauga-solicitare"
        element={
          currentUser && currentUser.role === "BENEFICIAR"
          ? <AdaugaSolicitare 
              setSolicitari={setSolicitariBeneficiar} 
              currentUser={currentUser} 
            />
          : <p style={{ padding: "50px", textAlign: "center" }}>
              Acces interzis.
            </p>
        }
      />
      </Routes>
    </Router>
  );
}