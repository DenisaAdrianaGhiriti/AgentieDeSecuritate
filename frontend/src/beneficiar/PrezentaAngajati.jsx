import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from '../../apiClient'; 
import './PrezentaAngajati.css';

// Funcție ajutătoare pentru a extrage valoarea corectă din Pontaj (Spring)
const extractPontajData = (p) => {
    // Proprietățile Pontajului sunt camelCase: oraIntrare, oraIesire
    const paznic = p.paznic; 

    return {
        // Proprietăți Pontaj:
        id: p._id,
        oraIntrare: p.ora_intrare, 
        oraIesire: p.ora_iesire, 
        
        // Proprietăți Paznic (User):
        paznicId: paznic?._id,
        paznicNume: paznic?.nume,
        paznicPrenume: paznic?.prenume,
        // Paznicul complet este stocat pentru afișare/filtrare
        paznic: paznic, 
    };
};

export default function PrezentaAngajati() {
  const [angajatiActivi, setAngajatiActivi] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [view, setView] = useState("prezenta");
  const [istoricPontaje, setIstoricPontaje] = useState([]);
  // ID-ul selectat este numeric (Long)
  const [selectedPaznicId, setSelectedPaznicId] = useState(null); 
  const [searchName, setSearchName] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchAngajatiActivi = async () => {
      setLoading(true);
      setError('');
      try {
        // GET /pontaj/angajati-activi-beneficiar returnează List<Pontaj>
        const { data: rawData } = await apiClient.get("/pontaj/angajati-activi-beneficiar");
        
        // Mapăm datele pentru a folosi convențiile corecte
        const mappedData = rawData.map(extractPontajData);
        setAngajatiActivi(mappedData);
      } catch (err) {
        setError(err.response?.data?.message || "Eroare la preluarea angajaților activi.");
      } finally {
        setLoading(false);
      }
    };
    if (view === "prezenta") {
        fetchAngajatiActivi();
    }
  }, [view]);

  useEffect(() => {
    const fetchIstoric = async () => {
      if (view === "istoric") {
        setLoading(true);
        setError('');
        try {
          // GET /pontaj/istoric-60zile-beneficiar returnează List<Pontaj>
          const { data: rawData } = await apiClient.get("/pontaj/istoric-60zile-beneficiar");
            
            // Mapăm datele pentru a folosi convențiile corecte
            const mappedData = rawData.map(extractPontajData);
          setIstoricPontaje(mappedData);
        } catch (err) {
          setError(err.response?.data?.message || "Eroare la preluarea istoricului.");
        } finally {
          setLoading(false);
        }
      }
    };
    fetchIstoric();
  }, [view]);

  // Calculul paznicilor unici pentru lista Istoric (folosind ID-ul paznicului)
  const pazniciUniciMap = new Map();
  istoricPontaje.forEach(p => {
    if (p.paznicId && !pazniciUniciMap.has(p.paznicId)) {
        pazniciUniciMap.set(p.paznicId, p);
    }
  });
  const pazniciUniciIstoric = Array.from(pazniciUniciMap.values());

  // Filtrarea pe baza numelui/prenumelui
  const filteredPazniciIstoric = pazniciUniciIstoric.filter(p => 
    p.paznicNume.toLowerCase().includes(searchName.toLowerCase()) || 
    p.paznicPrenume.toLowerCase().includes(searchName.toLowerCase())
  );
  
  if (loading) return <div style={{textAlign: 'center', padding: '50px'}}>Se încarcă...</div>;
  if (error) return <div style={{textAlign: 'center', padding: '50px', color: 'red'}}>Eroare: {error}</div>;

  return (
    <div className="angajati-container">
      <h1>Prezență Angajați</h1>
      <div className="view-options">
        <label><input type="radio" name="view" value="prezenta" checked={view === "prezenta"} onChange={() => setView("prezenta")}/> Prezență curentă</label>
        <label><input type="radio" name="view" value="istoric" checked={view === "istoric"} onChange={() => { setView("istoric"); setSelectedPaznicId(null); setSearchName(""); }}/> Istoric prezență</label>
      </div>

      {view === "prezenta" && (
        <div className="table-responsive">
          <table className="angajati-table">
            <thead>
              <tr><th>Nume</th><th>Prenume</th><th>Ora Intrare</th><th>Locație</th></tr>
            </thead>
            <tbody>
              {angajatiActivi.length > 0 ? angajatiActivi.map((p) => (
                <tr key={p.id}> {/* CORECȚIE: Folosim ID-ul Pontajului (p.id) */}
                  <td>{p.paznicNume}</td>
                  <td>{p.paznicPrenume}</td>
                  <td>{new Date(p.oraIntrare).toLocaleString('ro-RO')}</td> {/* CORECȚIE: ora_intrare -> oraIntrare */}
                  <td><button className="btn-urmarire" onClick={() => navigate(`/urmarire/${p.paznicId}`)}>📍 Urmărire</button></td>
                </tr>
              )) : (
                <tr><td colSpan="4" style={{ textAlign: "center" }}>Niciun angajat în tură acum.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {view === "istoric" && !selectedPaznicId && (
        <div className="table-responsive">
          <div className="filter-container">
            <label htmlFor="searchName">Caută după nume: </label>
            <input id="searchName" type="text" value={searchName} onChange={(e) => setSearchName(e.target.value)} />
          </div>
          <table className="angajati-table">
            <thead>
              <tr><th>Nume</th><th>Prenume</th><th>Vezi Istoric</th></tr>
            </thead>
            <tbody>
              {filteredPazniciIstoric.length > 0 ? filteredPazniciIstoric.map(p => (
                  <tr key={p.paznicId}> {/* CORECȚIE: Folosim ID-ul Paznicului (p.paznicId) */}
                    <td>{p.paznicNume}</td>
                    <td>{p.paznicPrenume}</td>
                    <td><button className="btn-alege" onClick={() => setSelectedPaznicId(p.paznicId)}>Alege</button></td>
                  </tr>
                )) : (
                <tr><td colSpan="3" style={{ textAlign: "center" }}>Nicio pontare în ultimele 60 de zile.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {selectedPaznicId && (
        <div className="table-responsive">
          <button onClick={() => setSelectedPaznicId(null)} className="back-btn" style={{position: 'static', marginBottom: '10px'}}>⬅ Înapoi la lista agenților de securitate</button>
          <table className="angajati-table">
            <thead>
              <tr><th>Data</th><th>Check-in</th><th>Check-out</th></tr>
            </thead>
            <tbody>
              {istoricPontaje
                .filter(p => p.paznicId === selectedPaznicId)
                .sort((a, b) => new Date(b.oraIntrare) - new Date(a.oraIntrare))
                .map(p => (
                  <tr key={p.id}> {/* CORECȚIE: Folosim ID-ul Pontajului (p.id) */}
                    <td>{new Date(p.oraIntrare).toLocaleDateString('ro-RO')}</td>
                    <td>{new Date(p.oraIntrare).toLocaleString('ro-RO')}</td>
                    <td>{p.oraIesire ? new Date(p.oraIesire).toLocaleString('ro-RO') : "-"}</td> {/* CORECȚIE: ora_iesire -> oraIesire */}
                  </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      {!selectedPaznicId && <button className="back-bottom-btn" onClick={() => navigate(-1)}>⬅ Înapoi</button>}
    </div>
  );
}