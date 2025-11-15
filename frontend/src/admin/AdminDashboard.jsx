import React from "react";
import { Link } from "react-router-dom";
import "./AdminDashboard.css";

export default function AdminDashboard() {
  return (
    <div className="admin-dashboard">

      <main>
        <h1 className="page-title">Home</h1>
        <div className="cards-container">
          <Link to="/solicitari" className="card link-card">📄<p>Solicitări</p></Link>
          <div className="card">🚨<p>Incidente</p></div>
          <div className="card">👤<p>Angajați</p></div>
          <Link to="/adauga-angajat" className="card link-card">➕<p>Adăugare Angajat</p></Link>
          <div className="card">✅<p>Prezență Angajați</p></div>
          <div className="card">🏢<p>Firme colaboratoare</p></div>
          <Link to="/adauga-firma" className="card link-card">💼<p>Adăugare Firmă Colaboratoare</p></Link>
        </div>
      </main>
    </div>
  );
}
