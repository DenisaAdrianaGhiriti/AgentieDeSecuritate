// frontend/src/paznic/ProcesVerbal.jsx

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../../apiClient';
import './ProcesVerbal.css';
import SignaturePadWrapper from '../components/SignaturePad';

export default function ProcesVerbal() {
  const navigate = useNavigate();
  
  const [formData, setFormData] = useState({
    // Starea React folosește camelCase (corect)
    pontajId: '', 
    beneficiaryId: '',
    punctDeLucru: '', 
    reprezentantBeneficiar: '', 
    oraDeclansareAlarma: '', 
    oraPrezentareEchipaj: '', 
    oraIncheiereMisiune: '', 
    observatiiGenerale: '', 
    // Evenimentele interne folosesc de asemenea camelCase în Mongoose (evenimentSchema)
    evenimente: [{ dataOraReceptionarii: '', tipulAlarmei: '', echipajAlarmat: '', oraSosirii: '', cauzeleAlarmei: '', modulDeSolutionare: '', observatii: '' }],
    agentSignatureDataURL: '',
    beneficiarySignatureDataURL: '',
  });
  
  // Folosim o structură simplificată: List<SimpleUserDTO>
  const [beneficiariAlocati, setBeneficiariAlocati] = useState([]);
  const [puncteFiltrate, setPuncteFiltrate] = useState([]);
  // Stocăm Pontajul activ (dacă există) pentru a obține ID-ul.
  const [pontajActiv, setPontajActiv] = useState(null); 
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [signaturesSaved, setSignaturesSaved] = useState({ agent: false, beneficiary: false });

  useEffect(() => {
    const fetchInitialData = async () => {
      setLoading(true);
      try {
        // 1. Obținem Pontajul activ (ID-ul Mongoose este _id)
        const { data: pontajRes } = await apiClient.get('/pontaj/active'); // CORECȚIE: /pontaj/active

        if (!pontajRes) {
          throw new Error('Nu aveți o tură activă (Pontaj) pentru a crea Procese Verbale.');
        }
        
        setPontajActiv(pontajRes);
        // Setăm ID-ul pontajului pentru a fi trimis (folosim _id de la Mongoose)
        setFormData(prev => ({ ...prev, pontajId: pontajRes._id })); // CORECȚIE: ._id

        // 2. Obținem Beneficiarii alocați paznicului logat
        // Presupunem că User returnează _id și profile.nume_companie / profile.punct_de_lucru
        const { data: beneficiariRes } = await apiClient.get('/users/beneficiari'); 

        // Filtrăm beneficiarii care au puncte de lucru asociate în profil (profile.punct_de_lucru)
        const beneficiariCuPuncte = beneficiariRes.filter(b => b.profile?.punct_de_lucru?.length > 0); // CORECȚIE: punct_de_lucru
        
        setBeneficiariAlocati(beneficiariCuPuncte);

      } catch (err) {
        setError(err.response?.data?.message || 'Nu s-au putut încărca datele de alocare sau tura activă.');
      } finally {
        setLoading(false);
      }
    };
    fetchInitialData();
  }, []);

  // Logica pentru a selecta punctele de lucru când se alege beneficiarul
  const handleBeneficiarChange = (e) => {
    const selectedId = e.target.value; // ID-ul Mongoose este string (ObjectId)
    setFormData(prev => ({ ...prev, beneficiaryId: selectedId, punctDeLucru: '' }));
    
    // Căutăm beneficiarul în lista completă (folosind ._id)
    const beneficiarSelectat = beneficiariAlocati.find(b => b._id === selectedId); // CORECȚIE: ._id
    
    // Accesăm punctele de lucru din profilul Beneficiarului (profile.punct_de_lucru)
    setPuncteFiltrate(beneficiarSelectat?.profile?.punct_de_lucru || []); // CORECȚIE: punct_de_lucru
  };

  const handleChange = (e) => setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  const handleEventChange = (index, e) => {
    const updatedEvenimente = [...formData.evenimente];
    updatedEvenimente[index][e.target.name] = e.target.value;
    setFormData(prev => ({ ...prev, evenimente: updatedEvenimente }));
  };
  const handleAddRow = () => setFormData(prev => ({ ...prev, evenimente: [...prev.evenimente, { dataOraReceptionarii: '', tipulAlarmei: '', echipajAlarmat: '', oraSosirii: '', cauzeleAlarmei: '', modulDeSolutionare: '', observatii: '' }]}));
  const handleRemoveRow = (index) => {
    if (formData.evenimente.length > 1) {
      setFormData(prev => ({ ...prev, evenimente: prev.evenimente.filter((_, i) => i !== index) }));
    }
  };
  const handleSaveAgentSignature = (signature) => {
    setFormData(prev => ({ ...prev, agentSignatureDataURL: signature }));
    setSignaturesSaved(prev => ({ ...prev, agent: true }));
    alert('Semnătura agentului a fost salvată.');
  };
  const handleSaveBeneficiarySignature = (signature) => {
    setFormData(prev => ({ ...prev, beneficiarySignatureDataURL: signature }));
    setSignaturesSaved(prev => ({ ...prev, beneficiary: true }));
    alert('Semnătura beneficiarului a fost salvată.');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    if (!pontajActiv || !formData.pontajId) {
      setError('EROARE: Nu a fost detectată o tură activă (Pontaj).');
      return;
    }
    if (!formData.beneficiaryId || !formData.punctDeLucru) {
      setError('EROARE: Trebuie să selectați un beneficiar și un punct de lucru.');
      return;
    }
    if (!signaturesSaved.agent || !signaturesSaved.beneficiary) {
      setError('EROARE: Ambele semnături sunt obligatorii.');
      return;
    }
    
    setLoading(true);

    try {
      // Construim Payload-ul aliniat la underscore_case din Mongoose
      const payload = {
        // Chei Mongoose: underscore_case
        pontajId: formData.pontajId,
        beneficiaryId: formData.beneficiaryId,
        punctDeLucru: formData.punctDeLucru, // Acesta este deja camelCase în model
        
        // Mapează din camelCase (formData) în underscore_case (Mongoose)
        reprezentant_beneficiar: formData.reprezentantBeneficiar,
        ora_declansare_alarma: formData.oraDeclansareAlarma,
        ora_prezentare_echipaj: formData.oraPrezentareEchipaj,
        ora_incheiere_misiune: formData.oraIncheiereMisiune,
        observatii_generale: formData.observatiiGenerale,

        // Acestea sunt deja camelCase în modelul Eveniment Mongoose
        evenimente: formData.evenimente, 
        
        // Semnăturile (dacă sunt așteptate direct în payload)
        agentSignatureDataURL: formData.agentSignatureDataURL,
        beneficiarySignatureDataURL: formData.beneficiarySignatureDataURL,
      };
        
      // Ruta POST /proces-verbal/:pontajId este puțin neobișnuită, 
      // dar o vom folosi conform specificației tale (chiar dacă trimitem ID-ul și în body, Mongoose îl așteaptă în URL).
      await apiClient.post(`/proces-verbal/${formData.pontajId}`, payload);
      
      alert('✅ Proces verbal salvat cu succes! Documentul este gata.');
      navigate('/paznic/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'A apărut o eroare la salvarea documentului.');
    } finally {
      setLoading(false);
    }
  };
  
  const areAllSignaturesSaved = signaturesSaved.agent && signaturesSaved.beneficiary;

  return (
    <div className="pv-container">
      <h1>Completare Proces Verbal de Intervenție</h1>
      <p className="pv-subtitle">Documentul va fi generat automat pe baza datelor introduse.</p>
      
      {error && <p className="error-message">{error}</p>}

      <form onSubmit={handleSubmit} className="pv-form">
        <fieldset disabled={areAllSignaturesSaved || loading}>
            <legend>Selectare Obiectiv</legend>
            <div className="form-grid">
                <div className="form-group">
                    <label htmlFor="beneficiaryId">Selectează Beneficiarul</label>
                    <select 
                        id="beneficiaryId" 
                        name="beneficiaryId" 
                        value={formData.beneficiaryId} 
                        onChange={handleBeneficiarChange} 
                        required 
                        disabled={loading || !beneficiariAlocati.length}
                    >
                        <option value="">-- Alege o firmă --</option>
                        {beneficiariAlocati.map(b => (
                            // CORECȚIE: Folosim b._id și profile.nume_companie
                            <option key={b._id} value={b._id}>{b.profile?.nume_companie || b.profile?.nume_firma}</option>
                        ))}
                    </select>
                </div>
                <div className="form-group">
                    <label htmlFor="punctDeLucru">Selectează Punctul de Lucru</label>
                    <select 
                        id="punctDeLucru" 
                        name="punctDeLucru" 
                        value={formData.punctDeLucru} 
                        onChange={handleChange} 
                        required 
                        disabled={!formData.beneficiaryId}
                    >
                        <option value="">-- Alege un punct --</option>
                        {puncteFiltrate.map((p, index) => (
                            <option key={index} value={p}>{p}</option>
                        ))}
                    </select>
                </div>
            </div>
        </fieldset>
        
        <fieldset disabled={areAllSignaturesSaved || loading}>
            <legend>Detalii Principale Intervenție</legend>
            <div className="form-grid">
               <div className="form-group">
                  <label htmlFor="oraDeclansareAlarma">Alarma declanșată la ora</label>
                  <input id="oraDeclansareAlarma" type="datetime-local" name="oraDeclansareAlarma" value={formData.oraDeclansareAlarma} onChange={handleChange} required />
                </div>
                <div className="form-group">
                  <label htmlFor="oraPrezentareEchipaj">Echipaj prezent la ora</label>
                  <input id="oraPrezentareEchipaj" type="datetime-local" name="oraPrezentareEchipaj" value={formData.oraPrezentareEchipaj} onChange={handleChange} required />
                </div>
                <div className="form-group">
                  <label htmlFor="oraIncheiereMisiune">Misiune încheiată la ora</label>
                  <input id="oraIncheiereMisiune" type="datetime-local" name="oraIncheiereMisiune" value={formData.oraIncheiereMisiune} onChange={handleChange} required />
                </div>
                <div className="form-group">
                  <label htmlFor="reprezentantBeneficiar">Nume Reprezentant Beneficiar</label>
                  <input id="reprezentantBeneficiar" type="text" name="reprezentantBeneficiar" value={formData.reprezentantBeneficiar} onChange={handleChange} placeholder="Ex: Popescu Ion" />
                </div>
            </div>
                <div className="form-group">
                    <label htmlFor="observatiiGenerale">Observații Generale (opțional)</label>
                    <textarea id="observatiiGenerale" name="observatiiGenerale" value={formData.observatiiGenerale} onChange={handleChange} rows="3" />
                </div>
        </fieldset>

        <fieldset disabled={areAllSignaturesSaved || loading}>
            <legend>Tabel Evenimente Detaliate</legend>
            {formData.evenimente.map((event, index) => (
              <div key={index} className="event-row">
                <span className="event-row-number">{index + 1}.</span>
                <div className="event-grid">
                  <input type="datetime-local" name="dataOraReceptionarii" value={event.dataOraReceptionarii} onChange={(e) => handleEventChange(index, e)} required title="Data și Ora Recepționării"/>
                  <input type="text" name="tipulAlarmei" value={event.tipulAlarmei} onChange={(e) => handleEventChange(index, e)} placeholder="Tipul alarmei" required />
                  <input type="text" name="echipajAlarmat" value={event.echipajAlarmat} onChange={(e) => handleEventChange(index, e)} placeholder="Echipaj alarmat" required />
                  <input type="datetime-local" name="oraSosirii" value={event.oraSosirii} onChange={(e) => handleEventChange(index, e)} required title="Ora Sosirii"/>
                  <input type="text" name="cauzeleAlarmei" value={event.cauzeleAlarmei} onChange={(e) => handleEventChange(index, e)} placeholder="Cauzele alarmei" required />
                  <input type="text" name="modulDeSolutionare" value={event.modulDeSolutionare} onChange={(e) => handleEventChange(index, e)} placeholder="Mod de soluționare" required />
                  <input type="text" name="observatii" value={event.observatii} onChange={(e) => handleEventChange(index, e)} placeholder="Observații (opțional)" />
                </div>
                {formData.evenimente.length > 1 && (<button type="button" className="remove-row-btn" onClick={() => handleRemoveRow(index)}>Șterge</button>)}
              </div>
            ))}
            <button type="button" className="add-row-btn" onClick={handleAddRow}>+ Adaugă Rând</button>
        </fieldset>
        
        <div className="signatures-grid">
            <fieldset>
                <legend>Semnătură Agent Intervenție</legend>
                {!signaturesSaved.agent ? (<SignaturePadWrapper onSave={handleSaveAgentSignature} />) : (<div className="signature-display"><p className="signature-saved-text">✓ Semnat</p><img src={formData.agentSignatureDataURL} alt="Semnatura Agent" className="signature-image" /></div>)}
            </fieldset>
            <fieldset>
                <legend>Semnătură Beneficiar</legend>
                {!signaturesSaved.beneficiary ? (<SignaturePadWrapper onSave={handleSaveBeneficiarySignature} />) : (<div className="signature-display"><p className="signature-saved-text">✓ Semnat</p><img src={formData.beneficiarySignatureDataURL} alt="Semnatura Beneficiar" className="signature-image" /></div>)}
            </fieldset>
        </div>
        
        <div className="form-actions">
            <button type="button" className="back-btn" onClick={() => navigate(-1)} disabled={loading}>Anulează</button>
            <button type="submit" className="submit-btn" disabled={loading || !areAllSignaturesSaved}>
              {loading ? 'Se salvează...' : 'Salvează și Generează PDF'}
            </button>
        </div>
      </form>
    </div>
  );
}