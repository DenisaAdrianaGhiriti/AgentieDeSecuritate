// frontend/src/admin/Documente.jsx

import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../../apiClient";
import "./Documente.css";

export default function Documente() {
  const [documente, setDocumente] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const navigate = useNavigate();

  // IMPORTANT:
  // - pentru <a href> ai nevoie de URL complet (nu proxy /api)
  // - ex: VITE_API_BASE_URL=http://localhost:8081
  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8081";

  useEffect(() => {
    const fetchDocumente = async () => {
      setLoading(true);
      setMessage("");

      try {
        // Vrei DOAR:
        // 1) Proces Verbal Predare-Primire
        // 2) Raport Eveniment
        //
        // Folosim allSettled ca să nu pice pagina dacă un endpoint dă 403/500.
        const results = await Promise.allSettled([
          apiClient.get("/proces-verbal-predare/documente"),
          apiClient.get("/raport-eveniment/documente"),
        ]);

        const [predareRes, rapoarteRes] = results;

        const predareRaw =
          predareRes.status === "fulfilled" && Array.isArray(predareRes.value.data)
            ? predareRes.value.data
            : [];

        const rapoarteRaw =
          rapoarteRes.status === "fulfilled" && Array.isArray(rapoarteRes.value.data)
            ? rapoarteRes.value.data
            : [];

        // Normalizăm:
        // - id: Spring e de obicei "id" (nu "_id"). Totuși punem fallback.
        // - createdAt: uneori poate fi "createdAt" sau "dataIncheierii"/"dataRaport"
        const normalizeDate = (doc) =>
          doc.createdAt || doc.dataIncheierii || doc.dataRaport || doc.updatedAt || null;

        const predareDocs = predareRaw.map((doc) => ({
          ...doc,
          id: doc.id ?? doc._id ?? `${doc.pontajId ?? "pvpr"}-${doc.caleStocarePDF ?? Math.random()}`,
          tip: "Predare-Primire",
          createdAt: normalizeDate(doc),
        }));

        const rapoarteDocs = rapoarteRaw.map((doc) => ({
          ...doc,
          id: doc.id ?? doc._id ?? `${doc.numarRaport ?? "rap"}-${doc.caleStocarePDF ?? Math.random()}`,
          tip: "Raport Eveniment",
          createdAt: normalizeDate(doc),
        }));

        const allDocs = [...predareDocs, ...rapoarteDocs].filter(Boolean);

        // Sortare desc după dată (dacă lipsește, cade la final)
        allDocs.sort((a, b) => {
          const da = a.createdAt ? new Date(a.createdAt).getTime() : 0;
          const db = b.createdAt ? new Date(b.createdAt).getTime() : 0;
          return db - da;
        });

        setDocumente(allDocs);

        // Mesaje utile dacă un endpoint a picat
        const errors = [];
        if (predareRes.status === "rejected") errors.push("Predare-Primire");
        if (rapoarteRes.status === "rejected") errors.push("Raport Eveniment");

        if (errors.length && allDocs.length === 0) {
          setMessage(`❌ Nu s-au putut încărca documentele: ${errors.join(", ")}.`);
        } else if (errors.length) {
          setMessage(`⚠️ Unele documente nu au putut fi încărcate: ${errors.join(", ")}.`);
        }
      } catch (err) {
        console.error("Eroare la preluarea documentelor:", err);
        setMessage("❌ Nu s-au putut încărca documentele.");
      } finally {
        setLoading(false);
      }
    };

    fetchDocumente();
  }, []);

  const getNumeRelevant = (doc) => {
    switch (doc.tip) {
      case "Predare-Primire":
        // La tine în backend PVPRResponse are: id, pontajId, caleStocarePDF, dataIncheierii, reprezentantBeneficiar, numeReprezentantPrimire
        // Deci aici e mai relevant: firma + nume reprezentant primire (dacă există)
        if (doc.reprezentantBeneficiar && doc.numeReprezentantPrimire) {
          return `${doc.reprezentantBeneficiar} - ${doc.numeReprezentantPrimire}`;
        }
        return doc.reprezentantBeneficiar || doc.numeReprezentantPrimire || "N/A";

      case "Raport Eveniment":
        // În RaportEveniment, tu trimiți beneficiaryId; în listare depinde ce DTO întorci.
        // Încercăm mai multe variante:
        return (
          doc.numeCompanie ||
          doc.beneficiary?.profile?.nume_companie ||
          doc.beneficiaryId?.profile?.nume_companie ||
          doc.beneficiaryId?.profile?.numeCompanie ||
          doc.beneficiar?.profile?.nume_companie ||
          "N/A"
        );

      default:
        return "N/A";
    }
  };

  const filteredDocumente = useMemo(() => {
    const term = searchTerm.trim().toLowerCase();
    if (!term) return documente;
    return documente.filter((doc) => getNumeRelevant(doc).toLowerCase().includes(term));
  }, [documente, searchTerm]);

  const formatDate = (value) => {
    if (!value) return "N/A";
    const d = new Date(value);
    if (Number.isNaN(d.getTime())) return "N/A";
    return d.toLocaleString("ro-RO");
  };

  const buildPdfUrl = (doc) => {
    // dacă backend returnează deja cale absolută, o folosim ca atare
    if (!doc?.caleStocarePDF) return null;
    if (doc.caleStocarePDF.startsWith("http://") || doc.caleStocarePDF.startsWith("https://")) {
      return doc.caleStocarePDF;
    }
    // altfel, lipim la base url
    return `${apiBaseUrl}${doc.caleStocarePDF}`;
  };

  return (
    <main style={{ padding: "20px" }}>
      <h1 className="page-title">Documente Generate</h1>

      <div className="search-container">
        <input
          type="text"
          placeholder="Caută după nume sau companie..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
      </div>

      {loading && <p style={{ textAlign: "center" }}>Se încarcă documentele...</p>}
      {message && <p style={{ textAlign: "center", color: message.startsWith("❌") ? "red" : "#b8860b" }}>{message}</p>}

      {!loading && !filteredDocumente.length && (
        <p style={{ textAlign: "center" }}>Nu există documente disponibile care să corespundă căutării.</p>
      )}

      {!loading && filteredDocumente.length > 0 && (
        <div className="table-responsive">
          <table className="documente-table">
            <thead>
              <tr>
                <th>Tip Document</th>
                <th>Nume / Companie</th>
                <th>Data Creare</th>
                <th>Acțiune</th>
              </tr>
            </thead>

            <tbody>
              {filteredDocumente.map((doc) => {
                const pdfUrl = buildPdfUrl(doc);

                return (
                  <tr key={doc.id}>
                    <td>{doc.tip}</td>
                    <td>{getNumeRelevant(doc)}</td>
                    <td>{formatDate(doc.createdAt)}</td>
                    <td>
                      {pdfUrl ? (
                        <a href={pdfUrl} target="_blank" rel="noopener noreferrer">
                          Deschide PDF
                        </a>
                      ) : (
                        <span style={{ color: "#999" }}>PDF indisponibil</span>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>

          <p style={{ marginTop: 10, fontSize: 12, color: "#666" }}>
            Notă: dacă „Deschide PDF” dă 401/403 în tab nou, înseamnă că endpoint-ul PDF e protejat și link-ul nu trimite
            token-ul. Atunci trebuie ori să expui fișierele static (ex: /uploads/** permitAll), ori să deschizi PDF-ul prin
            fetch + blob cu Authorization.
          </p>
        </div>
      )}

      <button style={{ position: "fixed", bottom: 20, left: 20 }} onClick={() => navigate(-1)}>
        ⬅ Înapoi
      </button>
    </main>
  );
}
