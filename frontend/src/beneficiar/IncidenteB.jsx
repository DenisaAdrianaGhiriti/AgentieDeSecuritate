import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../../apiClient";
import "./IncidenteB.css";

export default function IncidenteB() {
  const [incidente, setIncidente] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  // funcție sigură pentru parsarea datei
  const parseDate = (value) => {
    if (!value) return null;

    if (typeof value === "number") {
      const d = new Date(value);
      return isNaN(d) ? null : d;
    }

    if (typeof value === "string") {
      // ISO sau fallback pentru "YYYY-MM-DD HH:mm:ss"
      const iso = new Date(value);
      if (!isNaN(iso)) return iso;

      const fixed = value.replace(" ", "T");
      const d2 = new Date(fixed);
      return isNaN(d2) ? null : d2;
    }

    return null;
  };

  useEffect(() => {
    const fetchIncidente = async () => {
      setLoading(true);
      setError("");

      try {
        const { data } = await apiClient.get("/incidente/beneficiar");

        data.sort((a, b) => {
          const da = parseDate(a.dataIncident);
          const db = parseDate(b.dataIncident);
          return (db?.getTime() ?? 0) - (da?.getTime() ?? 0);
        });

        setIncidente(data);
      } catch (err) {
        setError(err.response?.data?.message || "Eroare la încărcarea incidentelor.");
      } finally {
        setLoading(false);
      }
    };

    fetchIncidente();
  }, []);

  if (loading)
    return <div style={{ textAlign: "center", padding: "50px" }}>Se încarcă...</div>;

  if (error)
    return (
      <div style={{ textAlign: "center", padding: "50px", color: "red" }}>
        {error}
      </div>
    );

  return (
    <div className="incidente-container">
      <h1>Incidente Raportate la Obiectivele Beneficiarului</h1>

      {incidente.length === 0 ? (
        <p style={{ textAlign: "center" }}>
          Nu există incidente raportate pentru firma ta.
        </p>
      ) : (
        <div className="incidente-list">
          {incidente.map((inc) => {
            const data = parseDate(inc.dataIncident);

            return (
              <div
                key={inc.id ?? inc.id}
                className="incident-card"
                style={{
                  backgroundColor: inc.restabilit ? "#d4edda" : "#f8d7da",
                }}
              >
                <div>
                  <b>{inc.titlu}</b> – {inc.punctDeLucru}
                  <div
                    style={{
                      fontSize: "0.8em",
                      color: "#555",
                      marginTop: "5px",
                    }}
                  >
                    Data: {data ? data.toLocaleString("ro-RO") : "—"}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      <button className="back-bottom-btn" onClick={() => navigate(-1)}>
        Înapoi
      </button>
    </div>
  );
}
