// import React, { useState, useEffect } from 'react';
// import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
// import AdminDashboard from './admin/AdminDashboard';
// import Header from './admin/Header';
// import HomePage from './admin/HomePage';
// import LoginPage from './admin/LoginPage';
// import Solicitari  from './admin/Solicitari';
// import SolicitariDetalii from './admin/SolicitariDetalii';
// import BeneficiarDashboard from './beneficiar/BeneficiarDashboard';
// import SolicitariB from './beneficiar/SolicitariB';
// import AdaugaSolicitare from './beneficiar/AdaugaSolicitare';
// import PaznicDashboard from './paznic/PaznicDashboard';
// import AdaugaAngajat from "./admin/AdaugaAngajat";
// import AdaugaFirma from "./admin/AdaugaFirma";
// import PontarePage from './paznic/PontarePage';
// import ProcesVerbal from './documents/ProcesVerbal';

// function Dashboard({ user }) {
//   let content;

//   switch (user.role) {
//     case 'ADMINISTRATOR':
//       content = <AdminDashboard/>
//       break;
//     case 'ADMIN':
//       content = <AdminDashboard />;
//       break;
//     case 'BENEFICIAR':
//       content = <BeneficiarDashboard />;
//       break;
//     case 'PAZNIC':
//       content = <PaznicDashboard />;
//       break;
//     default:
//       content = (
//         <p style={{ padding: '50px', textAlign: 'center' }}>
//           Rol necunoscut.
//         </p>
//       );
//   }

//   return (
//     <div>
//       {content}
//       {/* <div style={{ textAlign: 'center', marginTop: '20px' }}>
//         <button onClick={onLogout}>Deconectare</button>
//       </div> */}
//     </div>
//   );
// }

// export default function App() {
//   const [currentUser, setCurrentUser] = useState(null);

//   const [solicitari, setSolicitari] = useState({ prelucrata: [], inCurs: [], rezolvata: [] });

//   const [solicitariBeneficiar, setSolicitariBeneficiar] = useState([]);

//    // La pornirea aplicației, verificăm dacă există user salvat în localStorage
//   useEffect(() => {
//     const savedUser = localStorage.getItem("currentUser");
//     if (savedUser) {
//       setCurrentUser(JSON.parse(savedUser));
//     }
//   }, []);

//   const handleLogin = (user) => {
//     setCurrentUser(user);
//     localStorage.setItem("currentUser", JSON.stringify(user)); // salvare în localStorage
//   };

//   const handleLogout = () => {
//     setCurrentUser(null);
//     localStorage.removeItem("currentUser"); // ștergere din localStorage
//   };

//   return (
//     <Router>
//       <Header user={currentUser} onLogout={handleLogout} />
//       <Routes>
//         <Route path="/" element={currentUser ? <Dashboard user={currentUser} /> : <HomePage />} />
//         <Route path="/login" element={<LoginPage onLogin={handleLogin} />} />

//         {/* ✅ Ruta pentru Pontare */}
//         <Route 
//           path="/pontare" 
//           element={
//             currentUser && currentUser.role === "PAZNIC" 
//               ? <PontarePage /> 
//               : <p style={{ padding: "50px", textAlign: "center" }}>
//                   Acces interzis.
//                 </p>
//           } 
//         />

//         <Route path="/solicitari" element={<Solicitari solicitari={solicitari} setSolicitari={setSolicitari} />} />
//         <Route path="/solicitari/:id" element={<SolicitariDetalii solicitari={solicitari} setSolicitari={setSolicitari} />} />
//         <Route path="/adauga-angajat" element={<AdaugaAngajat />} />
//         <Route path="/adauga-firma" element={<AdaugaFirma />} />
//         {/* Rutele pentru Beneficiar (protejate simplu) */}
//         <Route path="/beneficiar" element={ currentUser?.role === "beneficiar" ? <BeneficiarDashboard /> : <p>Acces interzis.</p> } />
//         <Route path="/solicitariB" element={ currentUser?.role === "beneficiar" ? <SolicitariB solicitari={solicitariBeneficiar} /> : <p>Acces interzis.</p> } />
//         <Route path="/adauga-solicitare" element={ currentUser?.role === "beneficiar" ? <AdaugaSolicitare setSolicitari={setSolicitariBeneficiar} /> : <p>Acces interzis.</p> } />

//         {/* Ruta pentru Proces Verbal */}
//         <Route path="/proces-verbal/:pontajId" element={<ProcesVerbal />} />

//         <Route 
//           path="/admin/dashboard" 
//           element={currentUser?.role === 'administrator' ? <AdminDashboard /> : <p>Acces interzis. Doar pentru Administrator.</p>} 
//         />

//         <Route 
//           path="/paznic/dashboard" 
//           element={currentUser?.role === 'administrator' ? <PaznicDashboard /> : <p>Acces interzis. Doar pentru Administrator.</p>} 
//       />
//       </Routes>
//     </Router>
//   );
// }
import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

/* -------------------- IMPORTURI ADMIN -------------------- */
import Header from './admin/Header';
import HomePage from './admin/HomePage';
import LoginPage from './admin/LoginPage'; // <-- Am păstrat doar acest LoginPage
import AdminDashboard from './admin/AdminDashboard';
import Solicitari from './admin/Solicitari';
import SolicitariDetalii from './admin/SolicitariDetalii';
import AdaugaAngajat from "./admin/AdaugaAngajat";
import AdaugaFirma from "./admin/AdaugaFirma";
import Incidente from './admin/Incidente';
import IstoricIncidente from './admin/IstoricIncidente';
import Documente from './admin/Documente';
import Angajati from "./admin/Angajati";
import Firmacolaboratoare from "./admin/Firmacolaboratoare";
import AngajatiInTura from './admin/AngajatiInTura';
import AlocarePaznici from './admin/AlocarePaznici';
import UrmarireAngajat from './admin/UrmarireAngajat';

/* -------------------- IMPORTURI ADMINISTRATOR -------------------- */
import AdministratorDashboard from './administrator/AdministratorDashboard';
import AdaugaAdmin from './administrator/AdaugaAdmin';
import GestionareAdmini from './administrator/GestionareAdmini';

/* -------------------- IMPORTURI BENEFICIAR ----------------------- */
import BeneficiarDashboard from './beneficiar/BeneficiarDashboard';
import SolicitariB from './beneficiar/SolicitariB';
import AdaugaSolicitare from './beneficiar/AdaugaSolicitare';
// NOTĂ: 'SesizariB' și 'AdaugaSesizare' au fost eliminate/comentate, deoarece nu apar în structura de fișiere.
import PrezentaAngajati from './beneficiar/PrezentaAngajati';
import AngajatiB from './beneficiar/AngajatiB';
import DetaliiAngajatB from "./beneficiar/AngajatBDetalii";
import IncidenteB from './beneficiar/IncidenteB';

/* -------------------- IMPORTURI PAZNIC --------------------------- */
import PaznicDashboard from './paznic/PaznicDashboard';
// import LoginPageP from './paznic/LoginPageP'; // <-- Eliminat: Folosim LoginPage din admin
import PontarePage from './paznic/PontarePage';
import ProcesVerbal from './documents/ProcesVerbal';
import ProcesVerbalPredarePrimire from './paznic/ProcesVerbalPredarePrimire';
import RaportEveniment from './paznic/RaportEveniment';

/* ----------------------------------------------------------------- */
/* ----------------------- COMPONENTE UTILE ------------------------ */
/* ----------------------------------------------------------------- */

function Dashboard({ user }) {
  switch (user.role) {
    case 'ADMINISTRATOR': return <AdministratorDashboard />;
    case 'ADMIN': return <AdminDashboard />;
    case 'BENEFICIAR': return <BeneficiarDashboard />;
    case 'PAZNIC': return <PaznicDashboard />;
    default: 
      return <p style={{ padding: '50px', textAlign: 'center' }}>Rol necunoscut.</p>;
  }
}

function ProtectedRoute({ user, allowedRoles, children }) {
  if (!user || !allowedRoles.includes(user.role)) {
    return <p style={{ padding: "50px", textAlign: "center" }}>Acces interzis.</p>;
  }
  return children;
}

/* ----------------------------------------------------------------- */
/* ------------------------------ APP ------------------------------ */
/* ----------------------------------------------------------------- */

export default function App() {
  const [currentUser, setCurrentUser] = useState(null);

  useEffect(() => {
    const savedUser = localStorage.getItem("currentUser");
    if (savedUser) setCurrentUser(JSON.parse(savedUser));
  }, []);

  const handleLogin = (user) => {
    setCurrentUser(user);
    localStorage.setItem("currentUser", JSON.stringify(user));
  };

  const handleLogout = () => {
    setCurrentUser(null);
    localStorage.removeItem("currentUser");
  };

  return (
    <Router>
      <Header user={currentUser} onLogout={handleLogout} />

      <Routes>

        {/* -------------------- RUTE PUBLICE -------------------- */}
        <Route path="/" element={currentUser ? <Dashboard user={currentUser} /> : <HomePage />} />
        <Route path="/login" element={<LoginPage onLogin={handleLogin} />} />
        {/* CORECȚIE: Ruta /loginP folosește componenta unică de login */}
{/*         <Route path="/loginP" element={<LoginPage onLogin={handleLogin} />} />  */}

        {/* -------------------- DASHBOARD-URI -------------------- */}
        <Route path="/administrator/dashboard" element={
                    <ProtectedRoute user={currentUser} allowedRoles={['ADMINISTRATOR']}>
                        <AdministratorDashboard /> 
                    </ProtectedRoute>
        } />
                
        {/* Ruta specifică ADMIN (Sub-Admin/Manager) */}
        <Route path="/admin/dashboard" element={
            <ProtectedRoute user={currentUser} allowedRoles={['ADMIN', 'ADMINISTRATOR']}>
                <AdminDashboard /> 
            </ProtectedRoute>
        } />

        <Route path="/beneficiar/dashboard" element={
          <ProtectedRoute user={currentUser} allowedRoles={['BENEFICIAR', 'ADMINISTRATOR']}>
            <BeneficiarDashboard />
          </ProtectedRoute>
        } />

        <Route path="/paznic/dashboard" element={
          <ProtectedRoute user={currentUser} allowedRoles={['PAZNIC', 'ADMINISTRATOR']}>
            <PaznicDashboard />
          </ProtectedRoute>
        } />

        {/* ---------------- ADMINISTRATOR ---------------- */}
        <Route path="/administrator/adauga-admin" element={
          <ProtectedRoute user={currentUser} allowedRoles={['ADMINISTRATOR']}>
            <AdaugaAdmin />
          </ProtectedRoute>
        } />

        <Route path="/administrator/gestionare-admini" element={
          <ProtectedRoute user={currentUser} allowedRoles={['ADMINISTRATOR']}>
            <GestionareAdmini />
          </ProtectedRoute>
        } />

        {/* ------------------------ ADMIN ---------------------- */}
        <Route path="/adauga-angajat" element={
          <ProtectedRoute user={currentUser} allowedRoles={['ADMIN', 'ADMINISTRATOR']}>
            <AdaugaAngajat />
          </ProtectedRoute>
        } />

        <Route path="/adauga-firma" element={
          <ProtectedRoute user={currentUser} allowedRoles={['ADMIN', 'ADMINISTRATOR']}>
            <AdaugaFirma />
          </ProtectedRoute>
        } />

        <Route path="/solicitari" element={
          <ProtectedRoute user={currentUser} allowedRoles={['ADMIN', 'ADMINISTRATOR']}>
            <Solicitari />
          </ProtectedRoute>
        } />

        <Route path="/solicitari/:id" element={
          <ProtectedRoute user={currentUser} allowedRoles={['ADMIN', 'ADMINISTRATOR']}>
            <SolicitariDetalii />
          </ProtectedRoute>
        } />

        <Route path="/incidente" element={
          <ProtectedRoute user={currentUser} allowedRoles={['ADMIN', 'ADMINISTRATOR']}>
            <Incidente />
          </ProtectedRoute>
        } />

        <Route path="/istoric-incidente" element={
          <ProtectedRoute user={currentUser} allowedRoles={['ADMIN', 'ADMINISTRATOR']}>
            <IstoricIncidente />
          </ProtectedRoute>
        } />

        <Route path="/documente" element={
          <ProtectedRoute user={currentUser} allowedRoles={['ADMIN', 'ADMINISTRATOR']}>
            <Documente />
          </ProtectedRoute>
        } />

        <Route path="/angajati" element={
          <ProtectedRoute user={currentUser} allowedRoles={['ADMIN', 'ADMINISTRATOR']}>
            <Angajati />
          </ProtectedRoute>
        } />

        <Route path="/firmacolaboratoare" element={
          <ProtectedRoute user={currentUser} allowedRoles={['ADMIN', 'ADMINISTRATOR']}>
            <Firmacolaboratoare />
          </ProtectedRoute>
        } />

        <Route path="/angajati-in-tura" element={
          <ProtectedRoute user={currentUser} allowedRoles={['ADMIN', 'ADMINISTRATOR']}>
            <AngajatiInTura />
          </ProtectedRoute>
        } />

        <Route path="/alocare-paznici" element={
          <ProtectedRoute user={currentUser} allowedRoles={['ADMIN', 'ADMINISTRATOR']}>
            <AlocarePaznici />
          </ProtectedRoute>
        } />

        <Route path="/urmarire/:id" element={
          <ProtectedRoute user={currentUser} allowedRoles={['ADMIN', 'ADMINISTRATOR', 'BENEFICIAR']}>
            <UrmarireAngajat />
          </ProtectedRoute>
        } />

        {/* ---------------- BENEFICIAR ---------------- */}
        <Route path="/solicitariB" element={
          <ProtectedRoute user={currentUser} allowedRoles={['BENEFICIAR', 'ADMINISTRATOR']}>
            <SolicitariB />
          </ProtectedRoute>
        } />

        <Route path="/solicitariB/adauga" element={
          <ProtectedRoute user={currentUser} allowedRoles={['BENEFICIAR']}>
            <AdaugaSolicitare />
          </ProtectedRoute>
        } />

        <Route path="/prezentaAngajati" element={
          <ProtectedRoute user={currentUser} allowedRoles={['BENEFICIAR', 'ADMINISTRATOR']}>
            <PrezentaAngajati />
          </ProtectedRoute>
        } />

        <Route path="/angajatiB" element={
          <ProtectedRoute user={currentUser} allowedRoles={['BENEFICIAR', 'ADMINISTRATOR']}>
            <AngajatiB />
          </ProtectedRoute>
        } />

        <Route path="/angajatiB/:id" element={
          <ProtectedRoute user={currentUser} allowedRoles={['BENEFICIAR', 'ADMINISTRATOR']}>
            <DetaliiAngajatB />
          </ProtectedRoute>
        } />

        <Route path="/incidenteB" element={
          <ProtectedRoute user={currentUser} allowedRoles={['BENEFICIAR', 'ADMINISTRATOR']}>
            <IncidenteB />
          </ProtectedRoute>
        } />

        {/* ---------------- PAZNIC ---------------- */}
        <Route path="/pontare/:qrCode" element={
          <ProtectedRoute user={currentUser} allowedRoles={['PAZNIC', 'ADMINISTRATOR']}>
            <PontarePage />
          </ProtectedRoute>
        } />

        <Route path="/proces-verbal/:pontajId" element={
          <ProtectedRoute user={currentUser} allowedRoles={['PAZNIC', 'ADMINISTRATOR']}>
            <ProcesVerbal />
          </ProtectedRoute>
        } />

        <Route path="/proces-verbal-predare/:pontajId" element={
          <ProtectedRoute user={currentUser} allowedRoles={['PAZNIC', 'ADMINISTRATOR']}>
            <ProcesVerbalPredarePrimire />
          </ProtectedRoute>
        } />

        <Route path="/raport-eveniment" element={
          <ProtectedRoute user={currentUser} allowedRoles={['PAZNIC', 'ADMINISTRATOR']}>
            <RaportEveniment />
          </ProtectedRoute>
        } />

      </Routes>
    </Router>
  );
}
