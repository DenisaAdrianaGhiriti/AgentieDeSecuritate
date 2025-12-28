// C:\Users\HP\OneDrive\Desktop\SecuritySpring\AgentieDeSecuritate\frontend\src\admin\Incidente.jsx
import React, { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../../apiClient";
import "./Incidente.css";

export default function Incidente() {
  const [firme, setFirme] = useState([]);
  const [incidente, setIncidente] = useState([]);

  // selectie
  const [selectedFirmaId, setSelectedFirmaId] = useState(null); // Long
  const [selectedPunct, setSelectedPunct] = useState("");
  const [showForm, setShowForm] = useState(false);

  // detalii firma (User complet cu profile.puncteDeLucru)
  const [firmaDetalii, setFirmaDetalii] = useState(null);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const navigate = useNavigate();

  // --- fetch lista firme (DTO) ---
  const fetchFirme = useCallback(async () => {
    try {
      const { data } = await apiClient.get("/users/beneficiari"); // SimpleUserDTO[]
      setFirme(Array.isArray(data) ? data : []);
    } catch (err) {
      setError("Nu s-au putut încărca firmele.");
    }
  }, []);

  // --- fetch incidente ---
  const fetchIncidente = useCallback(async () => {
    try {
      const { data } = await apiClient.get("/incidente");
      const list = Array.isArray(data) ? data : [];
      setIncidente(list.filter((inc) => !inc.istoric));
    } catch (err) {
      setError("Nu s-au putut încărca incidentele.");
    }
  }, []);

  useEffect(() => {
    fetchFirme();
    fetchIncidente();
  }, [fetchFirme, fetchIncidente]);

  // --- select firma -> ia detalii complete ---
  const handleFirmaChange = async (e) => {
    const id = Number(e.target.value) || null;

    setSelectedFirmaId(id);
    setSelectedPunct("");
    setFirmaDetalii(null);

    if (!id) return;

    setLoading(true);
    setError("");
    try {
      // GET /api/users/{id} -> User complet (are profile.puncteDeLucru)
      const { data } = await apiClient.get(`/users/${id}`);
      setFirmaDetalii(data || null);
    } catch (err) {
      setError(err.response?.data?.message || "Nu am putut încărca detaliile firmei selectate.");
      setFirmaDetalii(null);
    } finally {
      setLoading(false);
    }
  };

  const puncteLucru = firmaDetalii?.profile?.puncteDeLucru || [];

  const handleSave = async () => {
    if (!selectedFirmaId || !selectedPunct) {
      alert("Selectați compania și punctul de lucru!");
      return;
    }

    setLoading(true);
    setError("");
    try {
      // găsim firma selectată din lista DTO (pt numeCompanie în titlu)
      const firmaDTO = firme.find((f) => f.id === selectedFirmaId);

      const numeFirma =
        firmaDTO?.numeCompanie ||
        firmaDetalii?.profile?.numeFirma ||
        `${firmaDTO?.nume || ""} ${firmaDTO?.prenume || ""}`.trim() ||
        "Firmă";

      const payload = {
        titlu: `Incident la ${numeFirma}`,
        descriere: "Buton de panică activat de admin",
        companieId: selectedFirmaId,
        punctDeLucru: selectedPunct,
      };

      const { data: newIncident } = await apiClient.post("/incidente", payload);

      setIncidente((prev) => [...prev, newIncident]);
      setShowForm(false);
      setSelectedFirmaId(null);
      setSelectedPunct("");
      setFirmaDetalii(null);
    } catch (err) {
      setError(err.response?.data?.message || "Eroare la salvarea incidentului.");
    } finally {
      setLoading(false);
    }
  };

  const handleRestabilire = async (id) => {
    try {
      const { data: incidentNou } = await apiClient.post(`/incidente/${id}/restabilire`);

      setIncidente((prev) => {
        const old = prev.find((inc) => inc.id === id);
        const oldUpdated = old ? { ...old, istoric: true } : null;

        return [...prev.filter((inc) => inc.id !== id), ...(oldUpdated ? [oldUpdated] : []), incidentNou].filter(
          Boolean
        );
      });

      alert("Incidentul a fost marcat ca restabilit. Va fi mutat în istoric în 10 secunde.");

      setTimeout(() => {
        setIncidente((prev) => prev.filter((inc) => inc.id !== id && inc.id !== incidentNou?.id));
        navigate("/istoric-incidente");
      }, 10000);
    } catch (err) {
      setError(err.response?.data?.message || "Eroare la restabilirea incidentului.");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Sunteți sigur că doriți să ștergeți acest incident?")) return;
    try {
      await apiClient.delete(`/incidente/${id}`);
      setIncidente((prev) => prev.filter((inc) => inc.id !== id));
    } catch (err) {
      setError(err.response?.data?.message || "Eroare la ștergerea incidentului.");
    }
  };

  return (
    <div className="incidente-container">
      <h1>Incidente Active</h1>
      {error && (
        <p className="error-message" style={{ color: "red" }}>
          {error}
        </p>
      )}

      <div style={{ display: "flex", gap: "10px", flexWrap: "wrap" }}>
        <button className="add-btn" onClick={() => setShowForm(!showForm)}>
          {showForm ? "Ascunde formular" : "➕ Adaugă incident"}
        </button>
        <button className="history-btn" onClick={() => navigate("/istoric-incidente")}>
          📜 Istoric Incidente
        </button>
      </div>

      {showForm && (
        <div className="incident-form">
          <div className="form-group">
            <label>Companie</label>
            <select value={selectedFirmaId || ""} onChange={handleFirmaChange} disabled={loading}>
              <option value="">-- Selectează compania --</option>
              {firme.map((firma) => (
                <option key={firma.id} value={firma.id}>
                  {firma.numeCompanie || `${firma.nume} ${firma.prenume}`}
                </option>
              ))}
            </select>
          </div>

          {selectedFirmaId && (
            <div className="form-group">
              <label>Punct de lucru</label>
              <select
                value={selectedPunct}
                onChange={(e) => setSelectedPunct(e.target.value)}
                disabled={!puncteLucru.length || loading}
              >
                <option value="">-- Selectează punctul de lucru --</option>
                {puncteLucru.map((punct, i) => (
                  <option key={`${punct}-${i}`} value={punct}>
                    {punct}
                  </option>
                ))}
              </select>

              {!loading && selectedFirmaId && !puncteLucru.length && (
                <p style={{ marginTop: 6, fontSize: 13, color: "#b02a37" }}>
                  Firma selectată nu are puncte de lucru salvate.
                </p>
              )}
            </div>
          )}

          <button className="save-btn" onClick={handleSave} disabled={loading}>
            {loading ? "Se salvează..." : "💾 Salvează Incident"}
          </button>
        </div>
      )}

      <div className="incidente-list">
        {incidente.filter((inc) => !inc.istoric).length > 0 ? (
          incidente
            .filter((inc) => !inc.istoric)
            .map((inc) => {
              const firmaDTO = firme.find((f) => f.id === inc.companieId);

              return (
                <div
                  key={inc.id}
                  className="incident-card"
                  style={{ backgroundColor: inc.restabilit ? "#d4edda" : "#f8d7da" }}
                >
                  {inc.titlu} - <b>{inc.punctDeLucru}</b> -{" "}
                  <b>{firmaDTO?.numeCompanie || "Necunoscut"}</b>
                  <div style={{ marginTop: "10px", display: "flex", gap: "10px" }}>
                    {!inc.restabilit && (
                      <button className="restabilire-btn" onClick={() => handleRestabilire(inc.id)}>
                        ♻ Restabilire
                      </button>
                    )}
                    <button
                      className="delete-btn"
                      onClick={() => handleDelete(inc.id)}
                      style={{ backgroundColor: "#dc3545", color: "white" }}
                    >
                      🗑️ Șterge
                    </button>
                  </div>
                </div>
              );
            })
        ) : (
          <p style={{ textAlign: "center", marginTop: "20px" }}>Nu există incidente active.</p>
        )}
      </div>

      <button className="back-bottom-btn" onClick={() => navigate(-1)}>
        ⬅ Înapoi
      </button>
    </div>
  );
}
