import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import apiClient from '../../apiClient';
import "./Solicitari.css";

// Definirea constantelor pentru StatusSesizare din Spring
// Enum-ul este în format UPPER_CASE: PRELUCRATA, IN_CURS, REZOLVATA
const STATUS_PRELUATA = "PRELUCRATA";
const STATUS_IN_CURS = "IN_CURS";
const STATUS_REZOLVATA = "REZOLVATA";

export default function Solicitari() {
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [solicitari, setSolicitari] = useState({
        [STATUS_PRELUATA]: [],
        [STATUS_IN_CURS]: [],
        [STATUS_REZOLVATA]: []
    });
    const [termenCautare, setTermenCautare] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        const fetchSolicitari = async () => {
            setLoading(true);
            setError("");
            try {
                // GET /sesizari returnează lista de Sesizări
                const { data } = await apiClient.get("/sesizari");

                const toate = data.map(s => ({
                    // CORECȚIE ID: _id -> id
                    id: s.id, 
                    titlu: s.titlu,
                    descriere: s.descriere,
                    // CORECȚIE FIRMĂ: createdByBeneficiary (User) are profile.numeFirma (camelCase)
                    firma: s.createdByBeneficiary?.profile?.nume_companie || "N/A", 
                    // Statusul este returnat ca String (ex: "PRELUCRATA")
                    status: s.status, 
                    // CORECȚIE CAMELCASE: pasiRezolvare
                    pasi: s.pasiRezolvare || "",
                    // CORECȚIE CAMELCASE: createdAt
                    data: s.createdAt ? new Date(s.createdAt).toLocaleDateString('ro-RO') : "N/A",
                    dataFinalizare: s.dataFinalizare
                }));

                const grouped = {
                    [STATUS_PRELUATA]: toate.filter(s => s.status === STATUS_PRELUATA),
                    [STATUS_IN_CURS]: toate.filter(s => s.status === STATUS_IN_CURS),
                    [STATUS_REZOLVATA]: toate.filter(s => s.status === STATUS_REZOLVATA)
                };
                setSolicitari(grouped);
            } catch (err) {
                setError(err.response?.data?.message || "Nu s-au putut încărca datele.");
            } finally {
                setLoading(false);
            }
        };
        fetchSolicitari();
    }, []);

    const mutaSesizare = async (id, from, to) => {
        if (!window.confirm("Sunteți sigur că doriți să schimbați statusul?")) return;

        try {
            // PATCH /sesizari/{id}/status, trimitem statusul în format Spring (UPPER_CASE)
            await apiClient.patch(`/sesizari/${id}/status`, { status: to });
            setSolicitari(prev => {
                const itemToMove = prev[from].find(s => s.id === id);
                if (!itemToMove) return prev;
                return {
                    ...prev,
                    [from]: prev[from].filter(s => s.id !== id),
                    [to]: [...prev[to], { ...itemToMove, status: to }]
                };
            });
            alert("Status actualizat cu succes!");
        } catch (err) {
            alert(`Eroare: ${err.response?.data?.message || "Nu s-a putut actualiza statusul."}`);
        }
    };

    const handleDelete = async (id, statusColoana) => {
        if (!window.confirm("Sunteți sigur că doriți să ștergeți definitiv solicitarea?")) return;
        
        try {
            // DELETE /sesizari/{id}
            await apiClient.delete(`/sesizari/${id}`);
            setSolicitari(prev => ({
                ...prev,
                [statusColoana]: prev[statusColoana].filter(item => item.id !== id)
            }));
            alert("Solicitarea a fost ștearsă!");
        } catch (error) {
            alert(`Eroare: ${error.response?.data?.message || "Nu s-a putut șterge solicitarea."}`);
        }
    };

    const coloane = [
        // CORECȚIE: Cheile folosesc constantele din Spring
        { key: STATUS_PRELUATA, label: "Preluată" },
        { key: STATUS_IN_CURS, label: "În Curs de Rezolvare" },
        { key: STATUS_REZOLVATA, label: "Rezolvată" }
    ];

    const solicitariFiltrate = {};
    for (const key in solicitari) {
        solicitariFiltrate[key] = solicitari[key].filter(s =>
            s.firma && s.firma.toLowerCase().includes(termenCautare.toLowerCase())
        );
    }

    if (loading) return <div style={{ textAlign: 'center', padding: '50px' }}>Se încarcă solicitările...</div>;
    if (error) return <div style={{ color: 'red', textAlign: 'center', padding: '50px' }}>Eroare: {error}</div>;

    return (
        <div className="solicitari-container">
            <div style={{ marginBottom: "15px" }}>
                <button className="back-btn" style={{position: 'static', backgroundColor: '#6c757d', color: 'white'}} onClick={() => navigate("/admin/dashboard")}>
                    ⬅ Înapoi
                </button>
            </div>
            <h1>Panou Solicitări Beneficiari</h1>
            <div className="search-section">
                <input type="text" placeholder="Caută după firmă..." value={termenCautare} onChange={(e) => setTermenCautare(e.target.value)} />
            </div>
            <div className="solicitari-grid">
                {coloane.map((col, index) => (
                    <div className="solicitari-column" key={col.key}>
                        <h2>{col.label}</h2>
                        <table>
                            <thead>
                                <tr><th>Titlu</th><th>Data</th><th>Firma</th><th>Acțiuni</th></tr>
                            </thead>
                            <tbody>
                                {solicitariFiltrate[col.key].length > 0 ? (
                                    solicitariFiltrate[col.key].map(s => (
                                        <tr key={s.id}>
                                            <td>{s.titlu}</td>
                                            <td>{s.data}</td>
                                            <td>{s.firma}</td>
                                            <td>
                                                <div className="actiuni-container">
                                                    {/* Mută în stânga */}
                                                    {index > 0 && <button className="btn-mic mutare" title={`Mută la "${coloane[index - 1].label}"`} onClick={() => mutaSesizare(s.id, col.key, coloane[index - 1].key)}>⬅</button>}
                                                    
                                                    <Link to={`/solicitari/${s.id}`} className="detalii-btn">Detalii</Link>
                                                    
                                                    {/* Mută în dreapta */}
                                                    {index < coloane.length - 1 && <button className="btn-mic mutare" title={`Mută la "${coloane[index + 1].label}"`} onClick={() => mutaSesizare(s.id, col.key, coloane[index + 1].key)}>➡</button>}
                                                    
                                                    {/* Ștergere doar din ultima coloană */}
                                                    {col.key === STATUS_REZOLVATA && <button className="sterge-btn" title="Șterge definitiv" onClick={() => handleDelete(s.id, col.key)}>🗑️</button>}
                                                </div>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr><td colSpan="4">Nicio solicitare.</td></tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                ))}
            </div>
        </div>
    );
}