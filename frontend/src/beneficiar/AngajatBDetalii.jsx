import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import apiClient from '../../apiClient'; 
import "./AngajatBDetalii.css";

export default function AngajatBDetalii() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [angajat, setAngajat] = useState(null);
  const [punctLucru, setPunctLucru] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  // ID-ul paznicului din URL, convertit la tipul numeric așteptat de backend/stare
  const paznicId = Number(id);

  useEffect(() => {
  const fetchDetalii = async () => {
    setLoading(true);
    setError("");

    try {
      const angajatRes = await apiClient.get(`/users/${paznicId}`);
      setAngajat(angajatRes.data);

      try {
        const beneficiarRes = await apiClient.get("/users/profile");
        const beneficiarData = beneficiarRes.data;

        const assignedItems = beneficiarData.profile?.assignedPaznici || [];
        const assignedItemGasit = assignedItems.find(item =>
          (item.paznici || []).some(p => (typeof p === "object" ? p.id : p) === paznicId)
        );

        setPunctLucru(assignedItemGasit ? assignedItemGasit.punct : "Nespecificat");
      } catch {
        // dacă /profile nu merge, nu mai blocăm toată pagina
        setPunctLucru("Nespecificat");
      }

    } catch (err) {
      setError(err.response?.data?.message || "Eroare la încărcarea detaliilor.");
    } finally {
      setLoading(false);
    }
  };

  fetchDetalii();
}, [paznicId]);

  if (loading) return <div style={{textAlign: 'center', padding: '50px'}}>Se încarcă...</div>;
  if (error) return <div style={{textAlign: 'center', padding: '50px', color: 'red'}}>{error}</div>;

  return (
    <div className="angajatB-detalii">
      <h1>Detalii Angajat</h1>
      
      {angajat ? (
        <div className="detalii-box">
          <p><strong>Nume:</strong> {angajat.nume}</p>
          <p><strong>Prenume:</strong> {angajat.prenume}</p>
          <p><strong>Email:</strong> {angajat.email}</p>
          <p><strong>Telefon:</strong> {angajat.telefon || 'N/A'}</p>
          <p><strong>Punct de lucru:</strong> {punctLucru}</p>
        </div>
      ) : (
        <p>Nu s-au găsit detalii pentru acest angajat.</p>
      )}

      <button className="back-btn" onClick={() => navigate(-1)}>Înapoi</button>
    </div>
  );
}