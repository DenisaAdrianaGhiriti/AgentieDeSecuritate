import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from '../../apiClient'; 
import "./IstoricIncidente.css";

export default function IstoricIncidente() {
    const [istoric, setIstoric] = useState([]);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchIstoric = async () => {
            setLoading(true);
            setError("");
            try {
                // GET /incidente/istoric returnează List<Incident>
                const { data } = await apiClient.get("/incidente/istoric");
                
                // Proprietatea createdAt este deja camelCase în Spring
                data.sort((a, b) => {
                const da = new Date(a.dataIncident);
                const db = new Date(b.dataIncident);
                const ta = isNaN(da.getTime()) ? 0 : da.getTime();
                const tb = isNaN(db.getTime()) ? 0 : db.getTime();
                return tb - ta;
                });
                setIstoric(data);
            } catch (err) {
                setError("Nu s-a putut încărca istoricul incidentelor.");
            } finally {
                setLoading(false);
            }
        };

        fetchIstoric();
    }, []);

    if (loading) return <div style={{textAlign: 'center', padding: '50px'}}>Se încarcă istoricul...</div>;
    if (error) return <div style={{textAlign: 'center', padding: '50px', color: 'red'}}>{error}</div>;

    const formatDate = (value) => {
    if (!value) return "—"; // dacă e null/undefined

    // dacă backend-ul trimite data ca array: [yyyy, mm, dd, hh, min, ss, nanos]
    if (Array.isArray(value)) {
        const [y, m, d, h = 0, min = 0, s = 0] = value;
        return new Date(y, m - 1, d, h, min, s).toLocaleString("ro-RO");
    }

    const d = new Date(value);
    return isNaN(d.getTime()) ? "—" : d.toLocaleString("ro-RO");
    };

    return (
        <div className="istoric-container">
            <h1>📜 Istoric Incidente</h1>
            
            {istoric.length > 0 ? (
                <div className="istoric-list">
                    {istoric.map((inc) => (
                    <div
                        key={inc.id}
                        className={`istoric-card ${inc.restabilit ? "verde" : "rosu"}`}
                    >
                        {inc.titlu} – <b>{inc.punctDeLucru}</b>
                        <div style={{ fontSize: "0.8em", color: "#555", marginTop: "5px" }}>
                        Data: {formatDate(inc.dataIncident)}
                        </div>
                    </div>
                    ))}
                </div>
            ) : (
                <p style={{textAlign: 'center'}}>Nu există incidente în istoric.</p>
            )}

            <button className="back-bottom-btn" onClick={() => navigate(-1)}>
                ⬅ Înapoi
            </button>
        </div>
    );
}