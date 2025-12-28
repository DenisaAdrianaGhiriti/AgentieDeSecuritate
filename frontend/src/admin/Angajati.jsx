import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from '../../apiClient'; // <-- Corect: Importăm apiClient
import "./Angajati.css";

export default function Angajati() {
    const [paznici, setPaznici] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [editUser, setEditUser] = useState(null);
    const [passwordUser, setPasswordUser] = useState(null);
    const [formData, setFormData] = useState({
        nume: "",
        prenume: "",
        email: "",
        telefon: "",
        // Adaugă câmpul nrLegitimatie pentru editarea profilului (dacă e necesar)
        nrLegitimatie: "", 
    });
    const [newPassword, setNewPassword] = useState("");
    const [searchTerm, setSearchTerm] = useState("");
    const navigate = useNavigate();

    // Funcția de preluare a paznicilor, acum refolosibilă
    const fetchPaznici = async () => {
        try {
            setLoading(true);
            // NOTĂ: Folosim /users/paznici care returnează SimpleUserDTO sau /users/list/paznic
            // Am lăsat /users/list/paznic (care returnează obiectul User complet) pentru că folosește email-ul.
            const { data } = await apiClient.get("/users/list/paznic");
            data.sort((a, b) => a.nume.localeCompare(b.nume));
            setPaznici(data);
        } catch (err) {
            setError(err.response?.data?.message || "Eroare la preluarea paznicilor");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchPaznici();
    }, []);

    const handleEdit = (user) => {
        setEditUser(user);
        setFormData({
            nume: user.nume,
            prenume: user.prenume,
            email: user.email,
            telefon: user.telefon || "",
            // Accesăm câmpul nrLegitimatie din profile (folosind camelCase)
            nrLegitimatie: user.profile?.nr_legitimatie || "", 
        });
    };

    const handleChangePassword = (user) => {
        setPasswordUser(user);
        setNewPassword("");
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSave = async () => {
        try {
            // Extragem ID-ul utilizatorului din obiectul editUser (care folosește .id)
            const userId = editUser.id; 
            
            // Construim payload-ul cu structura așteptată de UserUpdateRequest
            const payload = {
                nume: formData.nume,
                prenume: formData.prenume,
                email: formData.email,
                telefon: formData.telefon,
                // Trimitem obiectul profile cu câmpul nrLegitimatie
                profile: {
                    nr_legitimatie: formData.nrLegitimatie,
                }
            };

            // PUT /api/users/{id}
            await apiClient.put(`/users/${userId}`, payload);
            alert("Datele au fost salvate!");
            setEditUser(null);
            await fetchPaznici(); // Reîncărcăm lista actualizată
        } catch (err) {
            alert(`Eroare: ${err.response?.data?.message || "Nu s-au putut salva datele."}`);
        }
    };

    const handleDelete = async (userId) => {
        if (!window.confirm("Sunteți sigur că doriți să ștergeți acest angajat?")) return;
        try {
            // DELETE /api/users/{userId}
            await apiClient.delete(`/users/${userId}`);
            alert("Angajat șters cu succes!");
            // Filtrare folosind ID-ul corect
            setPaznici((prev) => prev.filter((u) => u.id !== userId)); 
        } catch (err) {
            alert(`Eroare: ${err.response?.data?.message || "Nu s-a putut șterge angajatul."}`);
        }
    };

    const handleSavePassword = async () => {
        if (newPassword.length < 6) {
            alert("Parola trebuie să aibă minim 6 caractere.");
            return;
        }
        try {
            // PUT /api/users/{id}/password
            // Folosim ID-ul corect: passwordUser.id
            await apiClient.put(`/users/${passwordUser.id}/password`, { newPassword }); 
            alert("Parola a fost schimbată cu succes!");
            setPasswordUser(null);
            setNewPassword("");
        } catch (err) {
            alert(`Eroare: ${err.response?.data?.message || "Nu s-a putut schimba parola."}`);
        }
    };

    const handleBack = () => {
        setEditUser(null);
        setPasswordUser(null);
    };

    const filteredPaznici = paznici.filter(
        (user) =>
            user.nume.toLowerCase().includes(searchTerm.toLowerCase()) ||
            user.prenume.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (loading) return <div className="loading" style={{textAlign: 'center', padding: '50px'}}>Se încarcă...</div>;
    if (error) return <div className="loading error-message" style={{textAlign: 'center', padding: '50px', color: 'red'}}>{error}</div>;

    if (passwordUser) {
        return (
            <div className="angajati-container edit-form-container">
                <h1>Schimbare Parolă pentru {passwordUser.nume} {passwordUser.prenume}</h1>
                <div className="form-group">
                    <label>Parola nouă (minim 6 caractere)</label>
                    <input type="password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} />
                </div>
                <button className="save-btn" onClick={handleSavePassword}>💾 Salvează parola</button>
                <button className="back-btn" onClick={handleBack}>⬅ Înapoi</button>
            </div>
        );
    }

    if (editUser) {
        return (
            <div className="angajati-container edit-form-container">
                <h1>Editare Agent de Securitate</h1>
                <div className="form-group"><label>Nume</label><input name="nume" value={formData.nume} onChange={handleChange} /></div>
                <div className="form-group"><label>Prenume</label><input name="prenume" value={formData.prenume} onChange={handleChange} /></div>
                <div className="form-group"><label>Email</label><input name="email" value={formData.email} onChange={handleChange} /></div>
                <div className="form-group"><label>Telefon</label><input name="telefon" value={formData.telefon} onChange={handleChange} /></div>
                {/* Adaugă câmpul pentru numărul de legitimație */}
                <div className="form-group"><label>Nr. legitimație</label><input name="nrLegitimatie" value={formData.nrLegitimatie} onChange={handleChange} /></div>
                
                <button className="save-btn" onClick={handleSave}>💾 Salvează</button>
                <button className="back-btn" onClick={handleBack}>⬅ Înapoi</button>
            </div>
        );
    }

    return (
        <div className="angajati-container">
            <h1>Lista Agenților de Securitate</h1>
            <div className="search-container">
                <input type="text" placeholder="Caută după nume sau prenume..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
            </div>
            <div className="table-responsive">
                <table className="angajati-table">
                    <thead>
                        <tr><th>Nume</th><th>Prenume</th><th>Email</th><th>Acțiuni</th></tr>
                    </thead>
                    <tbody>
                        {filteredPaznici.length > 0 ? (
                            filteredPaznici.map((user) => (
                                <tr key={user.id}> {/* FOLOSIM user.id (Spring) */}
                                    <td>{user.nume}</td>
                                    <td>{user.prenume}</td>
                                    <td>{user.email}</td>
                                    <td style={{display: 'flex', gap: '5px', flexWrap: 'wrap'}}>
                                        <button className="edit-btn" onClick={() => handleEdit(user)}>✏️ Editare</button>
                                        <button className="edit-btn" style={{ backgroundColor: "#ffc107" }} onClick={() => handleChangePassword(user)}>🔑 Schimbă parola</button>
                                        <button className="edit-btn" style={{ backgroundColor: "#dc3545" }} onClick={() => handleDelete(user.id)}>🗑️ Șterge</button> {/* FOLOSIM user.id */}
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr><td colSpan="4" style={{ textAlign: "center" }}>Nu există agenți de securitate care să corespundă căutării.</td></tr>
                        )}
                    </tbody>
                </table>
            </div>
            <button className="back-bottom-btn" onClick={() => navigate(-1)}>⬅ Înapoi</button>
        </div>
    );
}