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
      setError('');
      try {
        // Obținem simultan detaliile angajatului și ale beneficiarului logat
        const [angajatRes, beneficiarRes] = await Promise.all([
          // GET /users/{id} (pentru a obține detaliile paznicului)
          apiClient.get(`/users/${paznicId}`),
          // GET /users/profile (pentru a obține assignedPazniciItems ale Beneficiarului)
          apiClient.get('/users/profile') 
        ]);

        const angajatData = angajatRes.data;
        const beneficiarData = beneficiarRes.data;
        
        setAngajat(angajatData);

        // CORECȚIE LOGICĂ DE ALOCARE (pe baza AssignedPazniciItem din Spring):
        // BeneficiarulData.assignedPazniciItems este o listă de AssignedPazniciItem
        // Fiecare Item are: { id, punct, paznici: [User1, User2, ...] }
        
        // Căutăm Item-ul în care lista 'paznici' conține User-ul cu ID-ul nostru.
        const assignedItems = beneficiarData.profile?.assignedPaznici || [];

        const assignedItemGasit = assignedItems
          .find(item => 
            // Căutăm în lista 'paznici' (obiecte User) dacă există un ID care se potrivește
            item.paznici.some(paznicObjectId => paznicObjectId === paznicId)
          );
          
        // Proprietatea punct este deja camelCase
        setPunctLucru(assignedItemGasit ? assignedItemGasit.punct : "Nespecificat"); 
      } catch (err) {
        setError(err.response?.data?.message || "Eroare la încărcarea detaliilor.");
      } finally {
        setLoading(false);
      }
    };
    // Folosim paznicId în dependențe, deoarece e o valoare derivată din id
    fetchDetalii(); 
  }, [paznicId, navigate]);

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