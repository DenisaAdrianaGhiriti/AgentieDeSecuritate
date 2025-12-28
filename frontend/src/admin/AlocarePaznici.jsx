import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../../apiClient'; 
import './AlocarePaznici.css';

export default function AlocarePaznici() {
    const [beneficiari, setBeneficiari] = useState([]);
    const [paznici, setPaznici] = useState([]);
    const [selectedBeneficiarId, setSelectedBeneficiarId] = useState('');
    const [selectedPunct, setSelectedPunct] = useState('');
    // DTO-ul AssignedPaznicDTO are ID-ul ca 'id', nu '_id'
    const [assignedPaznici, setAssignedPaznici] = useState([]); 

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const navigate = useNavigate();

    // Fetch inițial + reîncărcare date
    const fetchAllData = useCallback(async () => {
        setLoading(true);
        setError('');
        try {
            const [beneficiariRes, pazniciRes] = await Promise.all([
                // NOTĂ: Dacă API-ul returnează SimpleUserDTO, folosește 'numeCompanie'
                apiClient.get('/users/beneficiari'), 
                apiClient.get('/users/paznici')
            ]);
            
            // Verificăm dacă ruta /users/list/beneficiar returnează aceeași structură ca /users/beneficiari.
            // Presupunem că folosești rutele mai simple din UserController.java
            setBeneficiari(beneficiariRes.data); 
            setPaznici(pazniciRes.data);

            // Dacă există beneficiar și punct selectat → aducem paznicii alocați
            if (selectedBeneficiarId && selectedPunct) {
                const beneficiaryId = Number(selectedBeneficiarId);
                const { data: assignedData } = await apiClient.get(
                    `/assignments/${selectedBeneficiarId}/paznici?punct=${encodeURIComponent(selectedPunct)}`
                );
                // DTO-ul AssignedPaznicDTO returnează 'id', 'nume', 'prenume', 'nrLegitimatie'
                setAssignedPaznici(assignedData);
            } else {
                setAssignedPaznici([]);
            }
        } catch (err) {
            console.error("Error fetching data:", err);
            setError('Eroare la preluarea datelor. Asigură-te că ești logat ca Admin/Administrator.');
        } finally {
            setLoading(false);
        }
    }, [selectedBeneficiarId, selectedPunct]);

    useEffect(() => {
        fetchAllData();
    }, [fetchAllData]);

    // Când schimbăm beneficiarul → resetăm punctul și alocările
    const handleBeneficiarChange = (e) => {
        const beneficiaryId = e.target.value;
        setSelectedBeneficiarId(beneficiaryId);
        setSelectedPunct('');
        setAssignedPaznici([]);
    };

    // Când schimbăm punctul → reîncărcăm paznicii alocați
    const handlePunctChange = (e) => {
        const punct = e.target.value;
        setSelectedPunct(punct);
        // Reîncărcarea se face automat prin fetchAllData la schimbarea selectedPunct
    };

    // ALOCARE
    const handleAssign = async (paznicId) => {
        if (!selectedBeneficiarId || !selectedPunct) {
            setError("Selectează mai întâi beneficiarul și punctul de lucru.");
            return;
        }
        setLoading(true);
        setError('');
        try {
            const payload = { 
                beneficiaryId: selectedBeneficiarId, 
                punct: selectedPunct, 
                pazniciIds: [paznicId] 
            };
            await apiClient.post('/assignments/assign', payload);
            // Reîncărcăm datele pentru a actualiza listele
            await fetchAllData();
        } catch (err) {
            console.error("Error assigning agent de securitate:", err.response?.data?.message || err.message);
            setError(err.response?.data?.message || 'Eroare la alocarea agentului de securitate.');
        } finally {
            setLoading(false);
        }
    };

    // DEZALOCARE
    const handleUnassign = async (paznicId) => {
        if (!selectedBeneficiarId || !selectedPunct) {
            setError("Selectează mai întâi beneficiarul și punctul de lucru.");
            return;
        }
        setLoading(true);
        setError('');
        try {
            const payload = { 
                beneficiaryId: Number(selectedBeneficiarId), 
                punct: selectedPunct, 
                pazniciIds: [paznicId] 
            };
            await apiClient.post('/assignments/unassign', payload);
            // Reîncărcăm datele pentru a actualiza listele
            await fetchAllData();
        } catch (err) {
            console.error("Error unassigning agentului de securitate:", err.response?.data?.message || err.message);
            setError(err.response?.data?.message || 'Eroare la dezalocarea agentului de securitate.');
        } finally {
            setLoading(false);
        }
    };

    // Filtrează paznicii care NU sunt în lista celor alocați
    const availablePaznici = paznici.filter(paznic =>
        !assignedPaznici.some(assigned => assigned.id === paznic.id)
    );
    
    // Găsim beneficiarul selectat pentru a accesa lista de puncte de lucru
    const selectedBeneficiar = beneficiari.find(b => String(b.id) === selectedBeneficiarId);

    return (
        <div className="assignment-page">
            <div className="assignment-header">
                <h1>Alocare Agenți de Securitate la Beneficiari</h1>
                <button onClick={() => navigate(-1)} className="back-btn-assignment">⬅ Înapoi</button>
            </div>

            {error && <p className="error-message" style={{color: 'red', textAlign: 'center'}}>{error}</p>}

            {/* Selectare beneficiar */}
            <div className="beneficiary-selector">
                <label htmlFor="beneficiar">Selectează un Beneficiar:</label>
                <select id="beneficiar" value={selectedBeneficiarId} onChange={handleBeneficiarChange} disabled={loading}>
                    <option value="">-- Alege o firmă --</option>
                    {beneficiari.map(b => (
                        <option key={b.id} value={b.id}>
                            {/* Folosim numele companiei din SimpleUserDTO (numeCompanie) */}
                            {b.numeCompanie} ({b.nume} {b.prenume})
                        </option>
                    ))}
                </select>
            </div>

            {/* Selectare punct de lucru */}
            {selectedBeneficiarId && selectedBeneficiar && (
                <div className="punct-selector">
                    <label htmlFor="punct">Selectează punct de lucru:</label>
                    <select id="punct" value={selectedPunct} onChange={handlePunctChange} disabled={loading}>
                        <option value="">-- Alege un punct de lucru --</option>
                        {/* Accesăm punctele de lucru (List<String>) din Profile */}
                        {/* DTO-ul pentru lista de beneficiari ar trebui să includă profile.puncteDeLucru */}
                        {selectedBeneficiar.puncteDeLucru?.map((p, idx) => (
                        <option key={idx} value={p}>{p}</option>
                        ))}
                        {/* ALTERNATIV: Dacă endpoint-ul /users/beneficiari returnează SimpleUserDTO, ar trebui să folosești: */}
                        {/* {selectedBeneficiar.puncteDeLucru?.map((p, idx) => (<option key={idx} value={p}>{p}</option>))} */}
                        
                    </select>
                </div>
            )}

            {loading && <p style={{textAlign: 'center'}}>Se încarcă...</p>}

            {/* Listele de paznici doar dacă avem firmă + punct selectat */}
            {selectedBeneficiarId && selectedPunct && !loading && (
                <div className="assignment-columns">
                    {/* Coloana Paznici Disponibili */}
                    <div className="column">
                        <h2>Agenți de Securitate Disponibili</h2>
                        <ul className="paznic-list">
                            {availablePaznici.length > 0 ? (
                                availablePaznici.map(p => (
                                    <li key={p.id}>
                                        {/* Accesăm DTO-ul Paznicului (SimpleUserDTO) */}
                                        <span>{p.nume} {p.prenume} ({p.nrLegitimatie || 'N/A'})</span>
                                        <button onClick={() => handleAssign(p.id)} className="assign-btn">Alocă ➡</button>
                                    </li>
                                ))
                            ) : <p>Toți agenții de securitate sunt alocați.</p>}
                        </ul>
                    </div>

                    {/* Coloana Paznici Alocați */}
                    <div className="column">
                        <h2>Agenți de Securitate Alocați la {selectedPunct}</h2>
                        <ul className="paznic-list">
                            {assignedPaznici.length > 0 ? (
                                assignedPaznici.map(p => (
                                    <li key={p.id}>
                                        {/* DTO-ul AssignedPaznicDTO are 'id' și 'nrLegitimatie' */}
                                        <span>{p.nume} {p.prenume} ({p.nrLegitimatie || 'N/A'})</span>
                                        <button onClick={() => handleUnassign(p.id)} className="unassign-btn">⬅ Dezalocă</button>
                                    </li>
                                ))
                            ) : <p>Niciun agent de securitate alocat.</p>}
                        </ul>
                    </div>
                </div>
            )}
        </div>
    );
}