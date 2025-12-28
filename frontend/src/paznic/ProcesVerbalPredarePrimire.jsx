import React, { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import apiClient from '../../apiClient'; 
import './ProcesVerbalPredarePrimire.css';

export default function ProcesVerbalPredarePrimire() {
  const navigate = useNavigate();
  // pontajId este ObjectId (String) dacă vine din rută.
  const { pontajId } = useParams(); 

  const [formData, setFormData] = useState({
    // Starea React folosește camelCase (ex: dataIncheierii)
    dataIncheierii: new Date().toISOString().slice(0, 16),
    numeSefFormatie: '', // Numele celui care predă (Paznicul logat)
    numeReprezentantPrimire: '', // Numele reprezentantului Beneficiarului (cel care primește)
    obiectePredate: ''
  });
  
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // Construim Payload-ul mapând starea camelCase la cheile Mongoose underscore_case.
      const payload = { 
        // pontajId este deja un string/ObjectId, nu mai este necesară conversia la Number,
        // dar îl trimitem doar dacă este prezent, conform rutei.
        pontajId: pontajId || null, 
        
        // Mapare Mongoose (underscore_case) : React State (camelCase)
        data_incheierii: formData.dataIncheierii,
        nume_sef_formatie: formData.numeSefFormatie, // <-- Notă: Câmpul Mongoose este `nume_sef_formatie` (comentat în schemă), îl trimitem pentru siguranță.
        nume_reprezentant_primire: formData.numeReprezentantPrimire,
        obiecte_predate: formData.obiectePredate,
        
        // Câmpurile reprezentantBeneficiar și reprezentantVigilent sunt obligatorii
        // dar nu sunt colectate în acest formular. Le vom pune pe `formData.numeReprezentantPrimire` 
        // ca placeholder, bazat pe o lipsă de câmpuri în UI/starea curentă.
        // **ATENȚIE**: Acestea trebuie să fie ID-uri valide (ObjectIds) conform Mongoose.
        // Dacă nu ai câmpuri de selectare, trebuie să le hardcodezi/găsești:
        reprezentantBeneficiar: formData.numeReprezentantPrimire, // Placeholder
        reprezentantVigilent: formData.numeSefFormatie, // Placeholder (trebuie să fie ID)
      };
      
      // Dacă pontajId este null, înseamnă că nu este creat la checkout, ci manual.
      // Dacă Mongoose folosește un singur endpoint, acest payload ar trebui să fie de ajuns,
      // dar asigură-te că `reprezentantBeneficiar` și `reprezentantVigilent` sunt gestionate corect în Mongoose.

      // POST /proces-verbal-predare/create
      await apiClient.post('/proces-verbal-predare/create', payload);

      alert('✅ Proces verbal de predare-primire salvat cu succes!');
      navigate(-1);
    } catch (err) {
      setError(err.response?.data?.message || 'A apărut o eroare la salvare.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="pv-predare-container">
      <form onSubmit={handleSubmit} className="pv-predare-form">
        <h2>Proces Verbal de Predare-Primire</h2>
        <div className="form-group">
          {/* dataIncheierii (React state) -> data_incheierii (Mongoose payload) */}
          <label htmlFor="dataIncheierii">Data și Ora Încheierii</label>
          <input id="dataIncheierii" type="datetime-local" name="dataIncheierii" value={formData.dataIncheierii} onChange={handleChange} required />
        </div>
        <div className="form-group">
          {/* numeSefFormatie (React state) -> nume_sef_formatie (Mongoose payload) */}
          <label htmlFor="numeSefFormatie">Nume Reprezentant Predare (Dvs.)</label>
          <input id="numeSefFormatie" type="text" name="numeSefFormatie" value={formData.numeSefFormatie} onChange={handleChange} placeholder="Ex: Popescu Ion" required />
        </div>
        <div className="form-group">
          {/* numeReprezentantPrimire (React state) -> nume_reprezentant_primire (Mongoose payload) */}
          <label htmlFor="numeReprezentantPrimire">Nume Reprezentant Firma Beneficiar</label>
          <input id="numeReprezentantPrimire" type="text" name="numeReprezentantPrimire" value={formData.numeReprezentantPrimire} onChange={handleChange} placeholder="Ex: Ionescu Vasile" required />
        </div>
        <div className="form-group">
          {/* obiectePredate (React state) -> obiecte_predate (Mongoose payload) */}
          <label htmlFor="obiectePredate">Obiecte / Sarcini / Observații Predate</label>
          <textarea id="obiectePredate" name="obiectePredate" value={formData.obiectePredate} onChange={handleChange} rows="6" placeholder="Descrieți pe scurt..." required></textarea>
        </div>
        {error && <p className="error-message">{error}</p>}
        <div className="form-actions">
          <button type="button" className="back-btn" onClick={() => navigate(-1)} disabled={loading}>Înapoi</button>
          <button type="submit" className="submit-btn" disabled={loading}>{loading ? 'Se salvează...' : 'Salvează Proces Verbal'}</button>
        </div>
      </form>
    </div>
  );
}