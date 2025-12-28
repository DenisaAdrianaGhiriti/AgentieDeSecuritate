import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
// Schimbăm importul de la axios la apiClient, care gestionează automat token-ul și baseURL
import apiClient from '../../apiClient'; 
import "./AdaugaFirma.css";
// Folosim componenta PasswordInput pentru conformitate
import PasswordInput from '../components/PasswordInput'; 

export default function AdaugaFirma() {
    const [formData, setFormData] = useState({
        nume: "", 
        prenume: "",
        email: "",
        password: "",
        passwordConfirm: "", // Adăugat pentru validare
        telefon: "",
        // Corectat din nume_companie la numeFirma pentru a se potrivi cu DTO-ul Spring (Profile.numeFirma)
        numeFirma: "", 
        // Va fi folosit fie ca punct inițial, fie ca punct nou de adăugat
        punctDeLucru: "" 
    });

    // Stările noi preluate din al doilea fișier
    const [adaugPunct, setAdaugPunct] = useState(false);
    const [companii, setCompanii] = useState([]);
    const [selectedCompanie, setSelectedCompanie] = useState("");
    
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    // --- LOGICA DE ÎNCĂRCARE A COMPANIILOR (Beneficiarilor) ---
    useEffect(() => {
        const fetchCompanii = async () => {
            try {
                // Endpoint-ul pentru listarea beneficiarilor (Admin/Administrator)
                const { data } = await apiClient.get("/users/beneficiari");
                console.log("BENEFICIARI RAW:", data);
                console.log("PRIMUL BENEFICIAR:", data?.[0]);
                console.log("PRIMUL PROFILE:", data?.[0]?.profile);
                setCompanii(data);
            } catch (err) {
                console.error("Eroare la încărcarea companiilor:", err);
                // Setează eroarea vizibilă doar dacă nu e o eroare de rețea/autorizare
                if (err.response) {
                    setError(err.response.data.message || "Eroare la încărcarea listei de firme.");
                }
            }
        };
        if (adaugPunct) {
            fetchCompanii();
        } else {
            // Curățăm lista și selecția când ne întoarcem la "Adaugă firmă nouă"
            setCompanii([]);
            setSelectedCompanie("");
        }
    }, [adaugPunct]);


    const handleChange = (e) => {
        // Casing-ul proprietăților din formData a fost corectat la camelCase (ex: numeFirma)
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            if (adaugPunct) {
                // --- LOGICA ADAUGĂ PUNCT DE LUCRU LA FIRMA EXISTENTĂ ---

                if (!selectedCompanie) throw new Error("Trebuie să selectați o companie.");
                if (!formData.punctDeLucru) throw new Error("Trebuie să introduceți un punct de lucru nou.");

                // Notă: Endpoint-ul backend-ului Spring nu știe să adauge la o listă @ElementCollection
                // direct din PUT. Aici presupunem că DTO-ul de actualizare include logica de append
                // în UserService.java, care primește o listă (sau cel puțin știe să trateze o singură valoare).
                // În Spring, Profile este @Embeddable, deci trimitem tot Profile-ul.
                
                // Trimiterea doar a punctului de lucru în DTO către PUT /users/{id}
                const updatePayload = {
                    profile: {
                        // Trimitem lista de puncte care ar trebui să fie tratată pe backend
                        // Ca workaround, trimitem valoarea ca un array cu un singur element
                        // care va fi adăugat la lista existentă de pe backend
                        puncteDeLucru: [formData.punctDeLucru]
                    }
                };

                // PUT /api/users/{selectedCompanie}
                await apiClient.put(`/users/${selectedCompanie}`, updatePayload);
                
                alert("✅ Punct de lucru adăugat cu succes!");
                
            } else {
                // --- LOGICA ADĂUGA FIRMĂ NOUĂ (Beneficiar) ---
                
                if (formData.password !== formData.passwordConfirm) {
                    throw new Error("Parolele nu se potrivesc!");
                }
                if (formData.password.length < 6) {
                    throw new Error('Parola trebuie să conțină cel puțin 6 caractere.');
                }
                
                const payload = {
                    nume: formData.nume,
                    prenume: formData.prenume,
                    email: formData.email,
                    password: formData.password,
                    telefon: formData.telefon,
                    role: 'BENEFICIAR',
                    profile: {
                        // Casing corect pentru Spring DTO (Profile.numeFirma)
                        numeFirma: formData.numeFirma, 
                        // Punctele de lucru sunt o listă în Spring (List<String> puncteDeLucru)
                        puncteDeLucru: formData.punctDeLucru ? [formData.punctDeLucru] : [],
                        // CUI poate fi adăugat dacă este necesar de backend: cui: "" 
                    }
                };
                
                // POST /api/users/create
                await apiClient.post('/users/create', payload);
                alert("✅ Firmă (Beneficiar) adăugată cu succes!");
            }
            navigate(-1);
            
        } catch (err) {
            // Gestionarea erorilor returnate de apiClient (care poate conține err.response)
            setError(err.response?.data?.message || err.message || 'A apărut o eroare necunoscută.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="form-page-container">
            <div className="form-card">
                <h2>Adaugă Firmă Beneficiar</h2>
                
                {/* Checkbox pentru schimbarea modului */}
                <div className="form-group" style={{ textAlign: 'center' }}>
                    <label>
                        <input 
                            type="checkbox" 
                            checked={adaugPunct} 
                            onChange={() => setAdaugPunct(!adaugPunct)} 
                        />
                        Doresc să adaug un punct de lucru la o firmă existentă
                    </label>
                </div>
                <hr />
                
                <form onSubmit={handleSubmit}>
                    {!adaugPunct ? (
                        <>
                            {/* Câmpuri Adaugă Firmă Nouă */}
                            <div className="form-group">
                                <label htmlFor="nume">Nume contact:</label>
                                <input id="nume" type="text" name="nume" value={formData.nume} onChange={handleChange} required className="form-input"/>
                            </div>
                            <div className="form-group">
                                <label htmlFor="prenume">Prenume contact:</label>
                                <input id="prenume" type="text" name="prenume" value={formData.prenume} onChange={handleChange} required className="form-input"/>
                            </div>
                            <div className="form-group">
                                <label htmlFor="email">Email:</label>
                                <input id="email" type="email" name="email" value={formData.email} onChange={handleChange} required className="form-input"/>
                            </div>
                            
                            {/* Componenta PasswordInput */}
                            <PasswordInput label="Parolă:" id="password" name="password" value={formData.password} onChange={handleChange} required className="form-input" />
                            <PasswordInput label="Confirmă Parola:" id="passwordConfirm" name="passwordConfirm" value={formData.passwordConfirm} onChange={handleChange} required className="form-input" />

                            <div className="form-group">
                                <label htmlFor="telefon">Telefon:</label>
                                <input id="telefon" type="tel" name="telefon" value={formData.telefon} onChange={handleChange} className="form-input"/>
                            </div>
                            <div className="form-group">
                                <label htmlFor="numeFirma">Nume Companie:</label>
                                {/* Câmpul numeFirma (camelCase) */}
                                <input id="numeFirma" type="text" name="numeFirma" value={formData.numeFirma} onChange={handleChange} required className="form-input"/>
                            </div>
                            <div className="form-group">
                                <label htmlFor="punctDeLucru">Punct de lucru inițial (opțional):</label>
                                {/* Câmpul punctDeLucru (camelCase) */}
                                <input id="punctDeLucru" type="text" name="punctDeLucru" value={formData.punctDeLucru} onChange={handleChange} className="form-input"/>
                            </div>
                        </>
                    ) : (
                        <>
                            {/* Câmpuri Adaugă Punct de Lucru */}
                            <div className="form-group">
                                <label htmlFor="companieSelect">Selectează compania existentă:</label>
                                <select 
                                    id="companieSelect" 
                                    value={selectedCompanie} 
                                    onChange={(e) => setSelectedCompanie(e.target.value)} 
                                    required 
                                    className="form-input"
                                >
                                    <option value="">-- Alege compania --</option>
                                    {/* Mapează folosind proprietatea corectă din DTO (numeCompanie) */}
                                    {companii.map((c) => (
                                    <option key={c.id} value={c.id}>
                                    {c.numeCompanie || `${c.nume} ${c.prenume}` || "Fără nume"}
                                    </option>
                                    ))}
                                </select>
                            </div>
                            <div className="form-group">
                                <label htmlFor="punctDeLucru">Adaugă punct de lucru nou:</label>
                                {/* Câmpul punctDeLucru (camelCase) */}
                                <input 
                                    id="punctDeLucru" 
                                    type="text" 
                                    name="punctDeLucru" 
                                    value={formData.punctDeLucru} 
                                    onChange={handleChange} 
                                    required 
                                    className="form-input"
                                />
                            </div>
                        </>
                    )}

                    {error && <p className="error-message" style={{color: 'red'}}>{error}</p>}

                    <div className="form-actions">
                        <button type="button" className="form-button back-btn" onClick={() => navigate(-1)} disabled={loading}>
                            ⬅ Înapoi
                        </button>
                        <button type="submit" className="form-button submit-btn" disabled={loading}>
                            {loading ? 'Se salvează...' : 'Salvează'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}