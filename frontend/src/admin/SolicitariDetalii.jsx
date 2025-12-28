import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import apiClient from '../../apiClient'; 
import './SolicitariDetalii.css';

// Am redenumit componenta pentru a se potrivi cu importul din App.jsx
export default function SolicitariDetalii({ solicitari, setSolicitari }) {
    const { id } = useParams();
    const [solicitare, setSolicitare] = useState(null);
    const [pasiRezolvare, setPasiRezolvare] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    // Convertim ID-ul din string în număr, pentru a se potrivi cu Long-ul din Spring
    const solicitareId = Number(id); 

    const findSolicitareInProps = useCallback(() => {
        if (!solicitari) return null;
        for (const key in solicitari) {
            // Căutăm după ID-ul numeric (Long)
            const item = solicitari[key].find(s => s.id === solicitareId); 
            if (item) return item;
        }
        return null;
    }, [solicitari, solicitareId]);

    const fetchSolicitare = useCallback(async () => {
        setLoading(true);
        setError('');
        try {
            // Preluăm toate sesizările. O rută mai bună ar fi /sesizari/{id}, dacă este disponibilă.
            const { data } = await apiClient.get('/sesizari');
            
            // CORECȚIE 1: Folosim s.id (Long) și solicitareId (Number)
            const gasit = data.find(s => Number(s.id) === solicitareId);
            
            if (gasit) {
                const item = {
                    // CORECȚIE 2: Folosim gasit.id în loc de gasit._id
                    id: gasit.id, 
                    titlu: gasit.titlu,
                    descriere: gasit.descriere,
                    // CORECȚIE 3: createdByBeneficiary (obiect) și numeFirma (camelCase)
                    firma: gasit.createdByBeneficiary?.profile?.nume_companie || "N/A", 
                    status: gasit.status,
                    // CORECȚIE 4: pasiRezolvare este deja camelCase în Spring
                    pasi: gasit.pasiRezolvare || "", 
                    data: new Date(gasit.createdAt).toLocaleDateString('ro-RO'),
                    dataFinalizare: gasit.dataFinalizare
                };
                setSolicitare(item);
                setPasiRezolvare(item.pasi);
            } else {
                setError("Solicitarea nu a fost găsită.");
            }
        } catch (err) {
            setError("Eroare la preluarea detaliilor solicitării.");
        } finally {
            setLoading(false);
        }
    }, [solicitareId]);

    useEffect(() => {
        const itemFromProps = findSolicitareInProps();
        if (itemFromProps) {
            setSolicitare(itemFromProps);
            setPasiRezolvare(itemFromProps.pasi || '');
            setLoading(false);
        } else {
            fetchSolicitare(); // Caută pe server dacă nu găsește în props
        }
    }, [fetchSolicitare, findSolicitareInProps]);

    const handleSave = async () => {
        if (!solicitare) return;
        setLoading(true);
        try {
            // Patch-ul trimite câmpul corect 'pasiRezolvare' (camelCase)
            await apiClient.patch(`/sesizari/${solicitare.id}`, { pasiRezolvare });

            // Actualizăm starea globală (dacă setSolicitari este disponibil)
            if (setSolicitari && solicitare.status) {
                setSolicitari(prev => {
                    const updatedSolicitari = { ...prev };
                    const index = updatedSolicitari[solicitare.status].findIndex(s => s.id === solicitare.id);
                    if (index > -1) {
                        updatedSolicitari[solicitare.status][index].pasi = pasiRezolvare;
                    }
                    return updatedSolicitari;
                });
            }
            
            alert("Pașii de rezolvare au fost salvați!");
            navigate('/solicitari');
        } catch (error) {
            alert("Nu s-au putut salva pașii de rezolvare.");
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div style={{textAlign: 'center', padding: '50px'}}>Se încarcă detaliile...</div>;
    if (error) return <div style={{textAlign: 'center', padding: '50px', color: 'red'}}>{error}</div>;

    // Afișăm un mesaj dacă solicitarea nu a fost găsită deloc
    if (!solicitare) {
        return (
            <div className="detalii-container">
                <h1>Solicitare negăsită</h1>
                <Link to="/solicitari" className="back-btn">Înapoi la listă</Link>
            </div>
        );
    }

    return (
        <div className="detalii-container">
            <h1>Detalii Solicitare #{solicitare.id}</h1>
            <div className="detalii-card">
                <p><strong>Titlu:</strong> {solicitare.titlu}</p>
                <p><strong>Descriere:</strong> {solicitare.descriere}</p>
                <p><strong>Firma:</strong> {solicitare.firma}</p>
                <p><strong>Data creare:</strong> {solicitare.data}</p>
                {/* DataFinalizare este deja camelCase */}
                <p><strong>Data finalizare:</strong> {solicitare.dataFinalizare ? new Date(solicitare.dataFinalizare).toLocaleString('ro-RO') : '—'}</p>

                <div className="pasi-rezolvare">
                    <label htmlFor="pasi"><strong>Pași de rezolvare:</strong></label>
                    <textarea 
                        id="pasi" 
                        rows="6" 
                        value={pasiRezolvare} 
                        onChange={(e) => setPasiRezolvare(e.target.value)} 
                        placeholder="Introduceți pașii efectuați..."
                    ></textarea>
                </div>

                <div className="butoane-container">
                    <Link to="/solicitari" className="back-btn">Înapoi</Link>
                    <button onClick={handleSave} className="save-btn" disabled={loading}>
                        {loading ? 'Se salvează...' : 'Salvează'}
                    </button>
                </div>
            </div>
        </div>
    );
}