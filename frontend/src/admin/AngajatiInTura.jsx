import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { jsPDF } from "jspdf";
import autoTable from "jspdf-autotable";
import apiClient from '../../apiClient'; 
import "./AngajatiInTura.css";

// Funcție ajutătoare pentru a extrage valoarea corectă din Pontaj (Spring)
// Exemplu de DTO Pontaj (parțial): { id: 1, paznic: { id: 10, nume: "Ion", email: "..." }, beneficiary: {...}, oraIntrare: "..." }

// Accesăm proprietățile User-ului Paznic și Beneficiar de pe obiectul Pontaj
const extractPontajData = (p) => {
    // Proprietățile Pontajului sunt camelCase
    const paznic = p.paznic; 
    const beneficiary = p.beneficiary;

    return {
        // Proprietăți Pontaj:
        id: p._id,
        oraIntrare: p.ora_intrare, // Spring returnează ISO String (LocalDateTime)
        oraIesire: p.ora_iesire,   // Spring returnează ISO String (LocalDateTime)
        
        // Proprietăți Paznic (User):
        paznicId: paznic?._id,
        paznicNume: paznic?.nume,
        paznicPrenume: paznic?.prenume,
        paznicEmail: paznic?.email,
        paznicTelefon: paznic?.telefon,
        
        // Proprietăți Beneficiar (User):
        beneficiaryId: beneficiary?._id,
        // Profile.numeFirma este camelCase în Spring
        numeCompanie: beneficiary?.profile?.nume_companie || "N/A"
    };
};


export default function AngajatiInTura() {
    const [angajati, setAngajati] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [beneficiari, setBeneficiari] = useState([]);
    const [selectedBeneficiar, setSelectedBeneficiar] = useState("");
    const [view, setView] = useState("prezenta");
    const [istoricPontaje, setIstoricPontaje] = useState([]);
    const [selectedPaznic, setSelectedPaznic] = useState(null); // Folosim ID-ul paznicului (Long)

    const navigate = useNavigate();

    // Fetch angajați activi
    useEffect(() => {
        const fetchAngajati = async () => {
            setLoading(true);
            try {
                // GET /pontaj/angajati-activi (returnează List<Pontaj>)
                const { data: rawData } = await apiClient.get("/pontaj/angajati-activi");
                
                // Mapăm datele pentru a folosi convențiile corecte și a extrage firmele
                const mappedData = rawData.map(extractPontajData);
                setAngajati(mappedData);

                // Extragem firmele unice din Pontaj.beneficiary.profile.numeFirma
                const firmeUnice = Array.from(
                    new Set(mappedData.map((p) => p.numeCompanie).filter(c => c !== "N/A"))
                );
                setBeneficiari(["Toate firmele", ...firmeUnice]); // Adaugam optiunea default
            } catch (err) {
                setError(err.response?.data?.message || "Eroare la preluarea angajaților activi");
            } finally {
                setLoading(false);
            }
        };

        if (view === "prezenta") {
            fetchAngajati();
        }
    }, [view]);

    // Fetch istoric pontaje
    useEffect(() => {
        const fetchIstoric = async () => {
            if (view === "istoric") {
                setLoading(true);
                try {
                    // GET /pontaj/istoric-60zile (returnează List<Pontaj>)
                    const { data: rawData } = await apiClient.get("/pontaj/istoric-60zile");
                    
                    const mappedData = rawData.map(extractPontajData);
                    setIstoricPontaje(mappedData);

                    // Extragem firmele unice
                    const firmeUnice = Array.from(
                        new Set(mappedData.map((p) => p.numeCompanie).filter(c => c !== "N/A"))
                    );
                    setBeneficiari(["Toate firmele", ...firmeUnice]);

                } catch (err) {
                    setError(err.response?.data?.message || "Eroare la preluarea istoricului");
                } finally {
                    setLoading(false);
                }
            }
        };

        if (view === "istoric") {
            fetchIstoric();
        }
    }, [view]);

    // Filtrare angajati activi (în funcție de selectedBeneficiar)
    const filteredAngajati = selectedBeneficiar && selectedBeneficiar !== "Toate firmele"
        ? angajati.filter((p) => p.numeCompanie === selectedBeneficiar)
        : angajati;

    // Calculul paznicilor unici pentru istoricul pontajelor
    const pazniciUniciMap = new Map();
    istoricPontaje
        .filter(p => !selectedBeneficiar || selectedBeneficiar === "Toate firmele" || p.numeCompanie === selectedBeneficiar)
        .forEach(p => {
            if (p.paznicId) {
                // Folosim ID-ul paznicului ca cheie
                if (!pazniciUniciMap.has(p.paznicId)) {
                    // Stocăm datele paznicului și ultima companie
                    pazniciUniciMap.set(p.paznicId, {
                        id: p.paznicId,
                        nume: p.paznicNume,
                        prenume: p.paznicPrenume,
                        ultimaCompanie: p.numeCompanie
                    });
                }
            }
        });
    const pazniciUnici = Array.from(pazniciUniciMap.values());


    // --- GENERARE PDF ---
    const handleDownloadPDF = () => {
        if (!selectedPaznic) return;

        // Găsim datele paznicului (folosim un obiect din pazniciUnici pentru nume/prenume)
        const paznicInfo = pazniciUnici.find(p => p.id === selectedPaznic);
        if (!paznicInfo) return;

        const doc = new jsPDF();
        doc.setFontSize(16);
        doc.text("Istoric prezență angajat", 14, 20);
        doc.setFontSize(12);
        doc.text(`Nume: ${paznicInfo.nume} ${paznicInfo.prenume}`, 14, 30);
        doc.text(`Data descărcării: ${new Date().toLocaleDateString('ro-RO')}`, 14, 36);

        const tableData = istoricPontaje
            .filter(p => p.paznicId === selectedPaznic && (!selectedBeneficiar || selectedBeneficiar === "Toate firmele" || p.numeCompanie === selectedBeneficiar))
            .map(p => [
                new Date(p.oraIntrare).toLocaleDateString('ro-RO'),
                new Date(p.oraIntrare).toLocaleTimeString('ro-RO'),
                // Ora Ieșire este opțională
                p.oraIesire ? new Date(p.oraIesire).toLocaleTimeString('ro-RO') : "-",
                p.numeCompanie
            ]);

        autoTable(doc, {
            startY: 45,
            head: [["Data", "Check-in", "Check-out", "Companie"]],
            body: tableData,
        });

        doc.save(`${paznicInfo.nume}_${paznicInfo.prenume}_istoric.pdf`);
    };

    if (loading) return <div style={{textAlign: 'center', padding: '50px'}}>Se încarcă...</div>;
    if (error) return <div style={{textAlign: 'center', padding: '50px', color: 'red'}}>Eroare: {error}</div>;

    return (
        <div className="angajati-container">
            <h1>Gestionare Prezență Angajați</h1>

            <div className="view-options">
                <div>
                    <label><input type="radio" name="view" value="prezenta" checked={view === "prezenta"} onChange={() => { setView("prezenta"); setSelectedPaznic(null); }} /> Prezență curentă</label>
                </div>
                <div>
                    <label><input type="radio" name="view" value="istoric" checked={view === "istoric"} onChange={() => { setView("istoric"); setSelectedPaznic(null); }} /> Istoric prezență</label>
                </div>
                {/* Selectarea filtrului de beneficiar este afișată dacă nu e selectat un paznic specific */}
                {!selectedPaznic && beneficiari.length > 0 && (
                    <div className="filter-container">
                        <label htmlFor="beneficiarSelect">Filtrează după firmă: </label>
                        <select id="beneficiarSelect" value={selectedBeneficiar} onChange={(e) => setSelectedBeneficiar(e.target.value)}>
                            {beneficiari.map((firma, idx) => (<option key={idx} value={firma}>{firma}</option>))}
                        </select>
                    </div>
                )}
            </div>

            {view === "prezenta" && !selectedPaznic && (
                <div className="table-responsive">
                    <table className="angajati-table">
                        <thead>
                            <tr><th>Nume</th><th>Prenume</th><th>Email</th><th>Telefon</th><th>Beneficiar</th><th>Ora Intrare</th><th>Locație</th></tr>
                        </thead>
                        <tbody>
                            {filteredAngajati.length > 0 ? (
                                filteredAngajati.map((p) => (
                                    <tr key={p.id}> {/* FOLOSIM p.id (ID-ul Pontajului) */}
                                        <td>{p.paznicNume}</td>
                                        <td>{p.paznicPrenume}</td>
                                        <td>{p.paznicEmail}</td>
                                        <td>{p.paznicTelefon}</td>
                                        <td>{p.numeCompanie}</td>
                                        <td>{new Date(p.oraIntrare).toLocaleString('ro-RO')}</td>
                                        <td><button className="btn-urmarire" onClick={() => navigate(`/urmarire/${p.paznicId}`)}>📍 Urmărire</button></td>
                                    </tr>
                                ))
                            ) : (
                                <tr><td colSpan="7" style={{ textAlign: "center" }}>Niciun angajat în tură.</td></tr>
                            )}
                        </tbody>
                    </table>
                </div>
            )}

            {view === "istoric" && !selectedPaznic && (
                <div className="table-responsive">
                    <table className="angajati-table">
                        <thead>
                            <tr><th>Nume</th><th>Prenume</th><th>Ultima Companie</th><th>Vezi Istoric</th></tr>
                        </thead>
                        <tbody>
                            {pazniciUnici.length > 0 ? (
                                pazniciUnici.map((p) => {
                                    return (
                                        <tr key={p.id}> {/* FOLOSIM p.id (ID-ul Paznicului) */}
                                            <td>{p.nume}</td>
                                            <td>{p.prenume}</td>
                                            <td>{p.ultimaCompanie}</td>
                                            <td><button className="btn-alege" onClick={() => setSelectedPaznic(p.id)}>Alege</button></td>
                                        </tr>
                                    );
                                })
                            ) : (
                                <tr><td colSpan="4" style={{ textAlign: "center" }}>Nicio pontare în ultimele 60 de zile.</td></tr>
                            )}
                        </tbody>
                    </table>
                </div>
            )}

            {selectedPaznic && (
                <div className="table-responsive">
                    <button onClick={handleDownloadPDF} className="download-btn">⬇ Descarcă PDF</button>
                    <button onClick={() => setSelectedPaznic(null)} className="back-btn" style={{position: 'static', marginLeft: '10px'}}>⬅ Înapoi la listă</button>
                    <table className="angajati-table" style={{marginTop: '10px'}}>
                        <thead>
                            <tr><th>Data</th><th>Check-in</th><th>Check-out</th><th>Companie</th></tr>
                        </thead>
                        <tbody>
                            {istoricPontaje
                                // Filtrează pe ID-ul Paznicului și Beneficiarul selectat
                                .filter(p => p.paznicId === selectedPaznic && (!selectedBeneficiar || selectedBeneficiar === "Toate firmele" || p.numeCompanie === selectedBeneficiar))
                                .map((p) => (
                                    <tr key={p.id}> {/* FOLOSIM p.id (ID-ul Pontajului) */}
                                        <td>{new Date(p.oraIntrare).toLocaleDateString('ro-RO')}</td>
                                        <td>{new Date(p.oraIntrare).toLocaleTimeString('ro-RO')}</td>
                                        <td>{p.oraIesire ? new Date(p.oraIesire).toLocaleTimeString('ro-RO') : "-"}</td>
                                        <td>{p.numeCompanie}</td>
                                    </tr>
                                ))}
                        </tbody>
                    </table>
                </div>
            )}

            {!selectedPaznic && <button className="back-bottom-btn" onClick={() => navigate(-1)}>⬅ Înapoi</button>}
        </div>
    );
}