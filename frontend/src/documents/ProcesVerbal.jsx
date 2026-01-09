// frontend/src/documents/ProcesVerbal.jsx
import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import apiClient from "../../apiClient";
import "./ProcesVerbal.css";
import SignaturePadWrapper from "../components/SignaturePad";

const emptyEveniment = {
  dataOraReceptionarii: "",
  tipulAlarmei: "",
  echipajAlarmat: "",
  oraSosirii: "",
  cauzeleAlarmei: "",
  modulDeSolutionare: "",
  observatii: "",
};

function normalizeDateTimeLocal(value) {
  if (!value) return value;
  return value.length === 16 ? `${value}:00` : value; // "YYYY-MM-DDTHH:mm" -> "...:ss"
}

export default function ProcesVerbal() {
  const navigate = useNavigate();
  const params = useParams();

  const [formData, setFormData] = useState({
    pontajId: "",
    beneficiaryId: "",
    punctDeLucru: "",

    reprezentantBeneficiar: "",
    oraDeclansareAlarma: "",
    oraPrezentareEchipaj: "",
    oraIncheiereMisiune: "",
    observatiiGenerale: "",

    evenimente: [{ ...emptyEveniment }],

    agentSignatureDataURL: "",
    beneficiarySignatureDataURL: "",
  });

  // IMPORTANT: aici NU mai folosim /users/beneficiari pentru puncte de lucru
  // folosim /posts/my-assigned-workpoints (ca în RaportEveniment)
  // Forma așteptată: [{ beneficiarId, numeCompanie, puncteDeLucru: [] }, ...]
  const [beneficiariCuPuncte, setBeneficiariCuPuncte] = useState([]);
  const [puncteFiltrate, setPuncteFiltrate] = useState([]);
  const [pontajActiv, setPontajActiv] = useState(null);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [signaturesSaved, setSignaturesSaved] = useState({ agent: false, beneficiary: false });

  const areAllSignaturesSaved = signaturesSaved.agent && signaturesSaved.beneficiary;

  useEffect(() => {
    const fetchInitialData = async () => {
      setLoading(true);
      setError("");

      try {
        // 1) Pontaj activ (pentru pontajId + preselect beneficiar)
        const pontajIdFromUrl = params?.pontajId;
        let pontaj;

        // dacă nu ai endpoint de pontaj by id, folosim active ca fallback
        if (pontajIdFromUrl && pontajIdFromUrl !== "nou") {
          pontaj = (await apiClient.get("/pontaj/active")).data;
        } else {
          pontaj = (await apiClient.get("/pontaj/active")).data;
        }

        if (!pontaj || pontaj.id == null) {
          throw new Error("Nu a fost detectată o tură activă (Pontaj).");
        }

        setPontajActiv(pontaj);
        setFormData((prev) => ({ ...prev, pontajId: String(pontaj.id) }));

        // 2) Beneficiari + puncte de lucru alocate paznicului
        const assigned = (await apiClient.get("/posts/my-assigned-workpoints")).data;
        const list = Array.isArray(assigned) ? assigned : [];
        setBeneficiariCuPuncte(list);

        // 3) Preselectăm beneficiarul din pontaj (dacă există)
        const pontajBeneficiaryId =
          pontaj.beneficiary?.id != null
            ? String(pontaj.beneficiary.id)
            : pontaj.beneficiaryId != null
              ? String(pontaj.beneficiaryId)
              : "";

        if (pontajBeneficiaryId) {
          const b = list.find((x) => String(x.beneficiarId) === String(pontajBeneficiaryId));

          setFormData((prev) => ({
            ...prev,
            beneficiaryId: String(pontajBeneficiaryId),
            punctDeLucru: "", // reset
          }));

          setPuncteFiltrate(Array.isArray(b?.puncteDeLucru) ? b.puncteDeLucru : []);
        } else {
          // dacă pontajul nu are beneficiar, punctele rămân goale până selectează manual
          setPuncteFiltrate([]);
        }
      } catch (err) {
        const msg =
          err?.response?.data?.message ||
          err?.response?.data ||
          err?.message ||
          "Nu s-au putut încărca datele (pontaj / beneficiari / puncte de lucru).";
        setError(String(msg));
      } finally {
        setLoading(false);
      }
    };

    fetchInitialData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleBeneficiarChange = (e) => {
    const selectedId = e.target.value;

    setFormData((prev) => ({
      ...prev,
      beneficiaryId: selectedId,
      punctDeLucru: "",
    }));

    const beneficiarSelectat = beneficiariCuPuncte.find(
      (b) => String(b.beneficiarId) === String(selectedId)
    );

    setPuncteFiltrate(
      Array.isArray(beneficiarSelectat?.puncteDeLucru) ? beneficiarSelectat.puncteDeLucru : []
    );
  };

  const handleChange = (e) => setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));

  const handleEventChange = (index, e) => {
    const updated = [...formData.evenimente];
    updated[index] = { ...updated[index], [e.target.name]: e.target.value };
    setFormData((prev) => ({ ...prev, evenimente: updated }));
  };

  const handleAddRow = () =>
    setFormData((prev) => ({ ...prev, evenimente: [...prev.evenimente, { ...emptyEveniment }] }));

  const handleRemoveRow = (index) => {
    if (formData.evenimente.length <= 1) return;
    setFormData((prev) => ({
      ...prev,
      evenimente: prev.evenimente.filter((_, i) => i !== index),
    }));
  };

  const handleSaveAgentSignature = (signature) => {
    setFormData((prev) => ({ ...prev, agentSignatureDataURL: signature }));
    setSignaturesSaved((prev) => ({ ...prev, agent: true }));
    alert("Semnătura agentului a fost salvată.");
  };

  const handleSaveBeneficiarySignature = (signature) => {
    setFormData((prev) => ({ ...prev, beneficiarySignatureDataURL: signature }));
    setSignaturesSaved((prev) => ({ ...prev, beneficiary: true }));
    alert("Semnătura beneficiarului a fost salvată.");
  };

  const beneficiariOptions = useMemo(() => {
    // normalizează pentru a evita crash dacă backend trimite altă cheie
    return (Array.isArray(beneficiariCuPuncte) ? beneficiariCuPuncte : []).map((b) => ({
      id: String(b.beneficiarId),
      label: b.numeCompanie || `Beneficiar ${b.beneficiarId}`,
    }));
  }, [beneficiariCuPuncte]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (!pontajActiv || !formData.pontajId) {
      setError("EROARE: Nu a fost detectată o tură activă (Pontaj).");
      return;
    }

    if (!formData.beneficiaryId || !formData.punctDeLucru) {
      setError("EROARE: Trebuie să selectați un beneficiar și un punct de lucru.");
      return;
    }

    if (!signaturesSaved.agent || !signaturesSaved.beneficiary) {
      setError("EROARE: Ambele semnături sunt obligatorii.");
      return;
    }

    setLoading(true);

    try {
      // Backend: CreateProcesVerbalRequest are snake_case pentru câmpurile principale
      const payload = {
        reprezentant_beneficiar: formData.reprezentantBeneficiar,
        ora_declansare_alarma: normalizeDateTimeLocal(formData.oraDeclansareAlarma),
        ora_prezentare_echipaj: normalizeDateTimeLocal(formData.oraPrezentareEchipaj),
        ora_incheiere_misiune: normalizeDateTimeLocal(formData.oraIncheiereMisiune),
        observatii_generale: formData.observatiiGenerale,

        evenimente: formData.evenimente.map((ev) => ({
          ...ev,
          dataOraReceptionarii: normalizeDateTimeLocal(ev.dataOraReceptionarii),
          oraSosirii: normalizeDateTimeLocal(ev.oraSosirii),
        })),

        // semnături (backend le poate ignora dacă nu le folosești)
        agentSignatureDataURL: formData.agentSignatureDataURL,
        beneficiarySignatureDataURL: formData.beneficiarySignatureDataURL,

        // aceste câmpuri NU sunt în DTO-ul tău Java => vor fi ignorate de backend
        // le păstrez ca info (nu strică), dar nu te baza pe ele în backend:
        beneficiaryId: Number(formData.beneficiaryId),
        punctDeLucru: formData.punctDeLucru,
      };

      await apiClient.post(`/proces-verbal/${formData.pontajId}`, payload);

      alert("✅ Proces verbal salvat cu succes! Documentul este gata.");
      navigate("/paznic/dashboard");
    } catch (err) {
      console.log("PV ERROR STATUS:", err.response?.status);
      console.log("PV ERROR DATA:", err.response?.data);

      const msg =
        err?.response?.data?.message ||
        err?.response?.data ||
        err?.message ||
        "A apărut o eroare la salvarea documentului.";

      setError(String(msg));
    } finally {
      setLoading(false);
    }
  };

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
                disabled={loading || beneficiariOptions.length === 0}
              >
                <option value="">-- Alege o firmă --</option>
                {beneficiariOptions.map((b) => (
                  <option key={b.id} value={b.id}>
                    {b.label}
                  </option>
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
                  <option key={index} value={p}>
                    {p}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {formData.beneficiaryId && puncteFiltrate.length === 0 && (
            <p style={{ marginTop: 8, color: "#b8860b" }}>
              ⚠️ Nu există puncte de lucru pentru acest beneficiar (sau nu sunt alocate paznicului curent).
            </p>
          )}
        </fieldset>

        <fieldset disabled={areAllSignaturesSaved || loading}>
          <legend>Detalii Principale Intervenție</legend>

          <div className="form-grid">
            <div className="form-group">
              <label htmlFor="oraDeclansareAlarma">Alarma declanșată la ora</label>
              <input
                id="oraDeclansareAlarma"
                type="datetime-local"
                name="oraDeclansareAlarma"
                value={formData.oraDeclansareAlarma}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="oraPrezentareEchipaj">Echipaj prezent la ora</label>
              <input
                id="oraPrezentareEchipaj"
                type="datetime-local"
                name="oraPrezentareEchipaj"
                value={formData.oraPrezentareEchipaj}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="oraIncheiereMisiune">Misiune încheiată la ora</label>
              <input
                id="oraIncheiereMisiune"
                type="datetime-local"
                name="oraIncheiereMisiune"
                value={formData.oraIncheiereMisiune}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="reprezentantBeneficiar">Nume Reprezentant Beneficiar</label>
              <input
                id="reprezentantBeneficiar"
                type="text"
                name="reprezentantBeneficiar"
                value={formData.reprezentantBeneficiar}
                onChange={handleChange}
                placeholder="Ex: Popescu Ion"
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="observatiiGenerale">Observații Generale (opțional)</label>
            <textarea
              id="observatiiGenerale"
              name="observatiiGenerale"
              value={formData.observatiiGenerale}
              onChange={handleChange}
              rows="3"
            />
          </div>
        </fieldset>

        <fieldset disabled={areAllSignaturesSaved || loading}>
          <legend>Tabel Evenimente Detaliate</legend>

          {formData.evenimente.map((event, index) => (
            <div key={index} className="event-row">
              <span className="event-row-number">{index + 1}.</span>

              <div className="event-grid">
                <input
                  type="datetime-local"
                  name="dataOraReceptionarii"
                  value={event.dataOraReceptionarii}
                  onChange={(e) => handleEventChange(index, e)}
                  required
                  title="Data și Ora Recepționării"
                />
                <input
                  type="text"
                  name="tipulAlarmei"
                  value={event.tipulAlarmei}
                  onChange={(e) => handleEventChange(index, e)}
                  placeholder="Tipul alarmei"
                  required
                />
                <input
                  type="text"
                  name="echipajAlarmat"
                  value={event.echipajAlarmat}
                  onChange={(e) => handleEventChange(index, e)}
                  placeholder="Echipaj alarmat"
                  required
                />
                <input
                  type="datetime-local"
                  name="oraSosirii"
                  value={event.oraSosirii}
                  onChange={(e) => handleEventChange(index, e)}
                  required
                  title="Ora Sosirii"
                />
                <input
                  type="text"
                  name="cauzeleAlarmei"
                  value={event.cauzeleAlarmei}
                  onChange={(e) => handleEventChange(index, e)}
                  placeholder="Cauzele alarmei"
                  required
                />
                <input
                  type="text"
                  name="modulDeSolutionare"
                  value={event.modulDeSolutionare}
                  onChange={(e) => handleEventChange(index, e)}
                  placeholder="Mod de soluționare"
                  required
                />
                <input
                  type="text"
                  name="observatii"
                  value={event.observatii}
                  onChange={(e) => handleEventChange(index, e)}
                  placeholder="Observații (opțional)"
                />
              </div>

              {formData.evenimente.length > 1 && (
                <button
                  type="button"
                  className="remove-row-btn"
                  onClick={() => handleRemoveRow(index)}
                >
                  Șterge
                </button>
              )}
            </div>
          ))}

          <button type="button" className="add-row-btn" onClick={handleAddRow}>
            + Adaugă Rând
          </button>
        </fieldset>

        <div className="signatures-grid">
          <fieldset>
            <legend>Semnătură Agent Intervenție</legend>
            {!signaturesSaved.agent ? (
              <SignaturePadWrapper onSave={handleSaveAgentSignature} />
            ) : (
              <div className="signature-display">
                <p className="signature-saved-text">✓ Semnat</p>
                <img
                  src={formData.agentSignatureDataURL}
                  alt="Semnatura Agent"
                  className="signature-image"
                />
              </div>
            )}
          </fieldset>

          <fieldset>
            <legend>Semnătură Beneficiar</legend>
            {!signaturesSaved.beneficiary ? (
              <SignaturePadWrapper onSave={handleSaveBeneficiarySignature} />
            ) : (
              <div className="signature-display">
                <p className="signature-saved-text">✓ Semnat</p>
                <img
                  src={formData.beneficiarySignatureDataURL}
                  alt="Semnatura Beneficiar"
                  className="signature-image"
                />
              </div>
            )}
          </fieldset>
        </div>

        <div className="form-actions">
          <button type="button" className="back-btn" onClick={() => navigate(-1)} disabled={loading}>
            Anulează
          </button>
          <button type="submit" className="submit-btn" disabled={loading || !areAllSignaturesSaved}>
            {loading ? "Se salvează..." : "Salvează și Generează PDF"}
          </button>
        </div>
      </form>
    </div>
  );
}
