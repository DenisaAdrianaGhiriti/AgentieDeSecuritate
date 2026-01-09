import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../../apiClient";
import "./PontarePage.css";
import SignaturePadWrapper from "../components/SignaturePad";

// --- Modal pentru Spring (camelCase + Long) ---
const ProcesVerbalModal = ({ pontajId, onSubmit, onCancel, loading }) => {
  const [formData, setFormData] = useState({
    dataIncheierii: new Date().toISOString().slice(0, 16), // "YYYY-MM-DDTHH:mm"
    numeReprezentantPrimire: "",
    obiectePredate: "",
    reprezentantBeneficiar: "", // String (nume companie)
    reprezentantVigilentId: "", // Long (id numeric)
    signatureDataURL: "",
  });

  const [beneficiari, setBeneficiari] = useState([]);
  const [paznici, setPaznici] = useState([]);
  const [signatureSaved, setSignatureSaved] = useState(false);

  useEffect(() => {
    const fetchBeneficiari = async () => {
      try {
        const { data } = await apiClient.get("/users/beneficiari");
        setBeneficiari(Array.isArray(data) ? data : []);
      } catch (err) {
        console.error("Eroare la încărcarea beneficiarilor:", err);
        setBeneficiari([]);
      }
    };

    const fetchPaznici = async () => {
      try {
        const { data } = await apiClient.get("/users/paznici");
        setPaznici(Array.isArray(data) ? data : []);
      } catch (err) {
        console.error("Eroare la încărcarea paznicilor:", err);
        setPaznici([]);
      }
    };

    fetchBeneficiari();
    fetchPaznici();
  }, []);

  const handleChange = (e) => {
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSaveSignature = (signature) => {
    setFormData((prev) => ({ ...prev, signatureDataURL: signature }));
    setSignatureSaved(true);
    alert("Semnătura a fost salvată. Puteți încheia tura.");
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (!formData.signatureDataURL) {
      alert("EROARE: Trebuie să semnați procesul verbal înainte de a încheia tura!");
      return;
    }

    // Spring LocalDateTime e mai safe cu secunde
    const dt =
      formData.dataIncheierii && formData.dataIncheierii.length === 16
        ? `${formData.dataIncheierii}:00`
        : formData.dataIncheierii;

    const payload = {
      pontajId: pontajId,
      dataIncheierii: dt,
      numeReprezentantPrimire: formData.numeReprezentantPrimire,
      obiectePredate: formData.obiectePredate,
      reprezentantBeneficiar: formData.reprezentantBeneficiar, // string
      reprezentantVigilentId: Number(formData.reprezentantVigilentId), // Long
    };

    onSubmit(payload);
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <form onSubmit={handleSubmit}>
          <h2>Proces Verbal de Predare-Primire</h2>

          <fieldset disabled={signatureSaved}>
            <div className="modal-form-group">
              <label htmlFor="dataIncheierii">Data și Ora Încheierii</label>
              <input
                id="dataIncheierii"
                type="datetime-local"
                name="dataIncheierii"
                value={formData.dataIncheierii}
                onChange={handleChange}
                required
              />
            </div>

            <div className="modal-form-group">
              <label htmlFor="reprezentantVigilentId">Reprezentant Vigilent (Primire)</label>
              <select
                id="reprezentantVigilentId"
                name="reprezentantVigilentId"
                value={formData.reprezentantVigilentId}
                onChange={handleChange}
                required
              >
                <option value="">-- Selectează un angajat --</option>
                {paznici.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.nume} {p.prenume}
                  </option>
                ))}
              </select>
            </div>

            <div className="modal-form-group">
              <label htmlFor="numeReprezentantPrimire">Nume Reprezentant Firmă Beneficiar</label>
              <input
                id="numeReprezentantPrimire"
                type="text"
                name="numeReprezentantPrimire"
                value={formData.numeReprezentantPrimire}
                onChange={handleChange}
                placeholder="Ex: Ionescu Vasile"
                required
              />
            </div>

            <div className="modal-form-group">
              <label htmlFor="reprezentantBeneficiar">Firmă Beneficiar</label>
              <select
                id="reprezentantBeneficiar"
                name="reprezentantBeneficiar"
                value={formData.reprezentantBeneficiar}
                onChange={handleChange}
                required
              >
                <option value="">-- Selectează --</option>
                {beneficiari.map((b) => {
                  // ideal: b.numeCompanie (ai câmp în SimpleUserDTO)
                  const numeCompanie = b.numeCompanie || b.profile?.nume_companie || "";
                  return (
                    <option key={b.id} value={numeCompanie}>
                      {numeCompanie} - {b.nume} {b.prenume}
                    </option>
                  );
                })}
              </select>
            </div>

            <div className="modal-form-group">
              <label htmlFor="obiectePredate">Obiecte / Sarcini Predate</label>
              <textarea
                id="obiectePredate"
                name="obiectePredate"
                value={formData.obiectePredate}
                onChange={handleChange}
                rows="5"
                placeholder="Descrieți pe scurt ce se predă..."
                required
              />
            </div>
          </fieldset>

          <fieldset>
            <legend>Semnătură Predare</legend>
            {!signatureSaved ? (
              <SignaturePadWrapper onSave={handleSaveSignature} />
            ) : (
              <div style={{ textAlign: "center" }}>
                <p style={{ color: "green", fontWeight: "bold" }}>✓ Semnat</p>
                <img
                  src={formData.signatureDataURL}
                  alt="Semnatura"
                  style={{
                    border: "1px solid #ccc",
                    borderRadius: "5px",
                    maxWidth: "200px",
                  }}
                />
              </div>
            )}
          </fieldset>

          <div className="modal-actions">
            <button type="button" className="cancel-btn" onClick={onCancel} disabled={loading}>
              Anulează
            </button>
            <button type="submit" className="submit-pv-btn" disabled={loading || !signatureSaved}>
              {loading ? "Se procesează..." : "Salvează și Încheie Tura"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

// --- Pagina principală ---
export default function PontarePage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");
  const [activePontaj, setActivePontaj] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  useEffect(() => {
    const fetchActivePontaj = async () => {
      setLoading(true);
      try {
        const { data } = await apiClient.get("/pontaj/active");
        setActivePontaj(data);
      } catch {
        setMessage("Eroare la preluarea statusului turei.");
      } finally {
        setLoading(false);
      }
    };
    fetchActivePontaj();
  }, []);

  const handleIncepeTura = () => {
    setLoading(true);
    setMessage("");

    navigator.geolocation.getCurrentPosition(
      async (position) => {
        try {
          const { latitude, longitude } = position.coords;
          const { data } = await apiClient.post("/pontaj/check-in", { latitude, longitude });
          setActivePontaj(data.pontaj);
          setMessage(`✅ ${data.message}`);
        } catch (err) {
          setMessage(`❌ ${err.response?.data?.message || "Eroare la check-in."}`);
        } finally {
          setLoading(false);
        }
      },
      () => {
        setMessage("❌ Nu se poate începe tura. Permiteți accesul la locație.");
        setLoading(false);
      }
    );
  };

  const handleFinalizeShift = async (procesVerbalData) => {
    setLoading(true);
    setMessage("Se salvează procesul verbal...");

    try {
      await apiClient.post("/proces-verbal-predare/create", procesVerbalData);
      setMessage("Proces verbal salvat. Se încheie tura...");

      const { data: checkoutData } = await apiClient.post("/pontaj/check-out");

      setActivePontaj(null);
      setIsModalOpen(false);
      setMessage(`✅ ${checkoutData.message}`);
    } catch (err) {
      setMessage(`❌ ${err.response?.data?.message || "A apărut o eroare."}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="pontare-page">
      {isModalOpen && activePontaj && (
        <ProcesVerbalModal
          pontajId={activePontaj.id}
          onCancel={() => setIsModalOpen(false)}
          onSubmit={handleFinalizeShift}
          loading={loading}
        />
      )}

      <div className="pontare-container">
        <h2>Pontare</h2>

        <div className="pontaj-info">
          {activePontaj ? (
            <p>
              <b>Tură activă începută la:</b>{" "}
              {new Date(activePontaj.oraIntrare).toLocaleString("ro-RO")}
            </p>
          ) : (
            <p>Nu aveți nicio tură activă.</p>
          )}
        </div>

        <div className="buttons">
          <button className="start-btn" onClick={handleIncepeTura} disabled={loading || activePontaj}>
            Începe Tura
          </button>
          <button className="end-btn" onClick={() => setIsModalOpen(true)} disabled={loading || !activePontaj}>
            Termină Tura
          </button>
        </div>

        {loading && !isModalOpen && <p>Se procesează...</p>}
        {message && (
          <div className="pontaj-info">
            <p>
              <b>Status:</b> {message}
            </p>
          </div>
        )}

        <button className="back-btn" onClick={() => navigate("/")}>
          Înapoi la Dashboard
        </button>
      </div>
    </div>
  );
}
