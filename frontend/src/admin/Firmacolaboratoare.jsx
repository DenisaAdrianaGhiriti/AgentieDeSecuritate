import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from '../../apiClient'; 
import "./Firmacolaboratoare.css";

export default function Firmacolaboratoare() {
  const [beneficiari, setBeneficiari] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [editUser, setEditUser] = useState(null);
  const [passwordUser, setPasswordUser] = useState(null);
  const [formData, setFormData] = useState({
    nume: "",
    prenume: "",
    email: "",
    telefon: "",
    // CORECȚIE: nume_companie -> numeFirma (CamelCase)
    numeFirma: "", 
    // CORECȚIE: punct_de_lucru -> puncteDeLucru (CamelCase)
    puncteDeLucru: [],
  });
  const [newPassword, setNewPassword] = useState("");
  const navigate = useNavigate();

  const fetchBeneficiari = async () => {
    setLoading(true);
    try {
      // GET /users/beneficiari returnează SimpleUserDTO (unde numeFirma este la nivelul superior)
      const { data } = await apiClient.get("/users/beneficiari"); 
      setBeneficiari(data);
    } catch (err) {
      setError(err.response?.data?.message || "Eroare la preluarea beneficiarilor");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBeneficiari();
  }, []);

  const handleEdit = (user) => {
  setEditUser(user);
  setFormData({
    nume: user.nume || "",
    prenume: user.prenume || "",
    email: user.email || "",
    telefon: user.telefon || "",
    numeFirma: user.numeCompanie || "",
    puncteDeLucru: Array.isArray(user.puncteDeLucru) ? user.puncteDeLucru : [],
  });
};

  const handleChangePassword = (user) => {
    setPasswordUser(user);
    setNewPassword("");
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // CORECȚIE: funcția folosește puncteDeLucru
  const handlePunctDeLucruChange = (index, value) => {
    const updated = [...formData.puncteDeLucru];
    updated[index] = value;
    setFormData({ ...formData, puncteDeLucru: updated });
  };

  // CORECȚIE: funcția folosește puncteDeLucru
  const addPunctDeLucru = () => {
    setFormData({ ...formData, puncteDeLucru: [...formData.puncteDeLucru, ""] });
  };

  // CORECȚIE: funcția folosește puncteDeLucru
  const removePunctDeLucru = (index) => {
    const updated = formData.puncteDeLucru.filter((_, i) => i !== index);
    setFormData({ ...formData, puncteDeLucru: updated });
  };

  const handleDelete = async (userId) => {
    if (!window.confirm("Sunteți sigur că doriți să ștergeți acest beneficiar?")) return;
    try {
      // DELETE /api/users/{userId}
      await apiClient.delete(`/users/${userId}`); 
      alert("Beneficiar șters cu succes!");
      // CORECȚIE: Filtrare după ID-ul corect (u.id)
      setBeneficiari((prev) => prev.filter((u) => u.id !== userId)); 
    } catch (err) {
      alert(`Eroare: ${err.response?.data?.message || "Nu s-a putut șterge beneficiarul."}`);
    }
  };

  const handleSave = async () => {
    try {
      const updatedUser = {
        nume: formData.nume,
        prenume: formData.prenume,
        email: formData.email,
        telefon: formData.telefon,
        profile: {
            numeFirma: formData.numeFirma,
            puncteDeLucru: formData.puncteDeLucru.filter(Boolean),
        },
      };
      // CORECȚIE: Folosim editUser.id
      await apiClient.put(`/users/${editUser.id}`, updatedUser); 
      alert("Datele au fost salvate!");
      setEditUser(null);
      await fetchBeneficiari();
    } catch (err) {
      alert(`Eroare: ${err.response?.data?.message || "Nu s-au putut salva datele."}`);
    }
  };

  const handleSavePassword = async () => {
    if (newPassword.length < 6) {
      alert("Parola trebuie să aibă minim 6 caractere.");
      return;
    }
    try {
      // CORECȚIE: Folosim passwordUser.id
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

  if (loading) return <div className="loading" style={{textAlign: 'center', padding: '50px'}}>Se încarcă...</div>;
  if (error) return <div className="loading error-message" style={{textAlign: 'center', padding: '50px', color: 'red'}}>Eroare: {error}</div>;

  if (passwordUser) {
    return (
      <div className="beneficiari-container edit-form-container">
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
      <div className="beneficiari-container edit-form-container">
        <h1>Editare Beneficiar</h1>
        <div className="form-group"><label>Nume contact</label><input name="nume" value={formData.nume} onChange={handleChange} /></div>
        <div className="form-group"><label>Prenume contact</label><input name="prenume" value={formData.prenume} onChange={handleChange} /></div>
        <div className="form-group"><label>Email</label><input name="email" value={formData.email} onChange={handleChange} /></div>
        <div className="form-group"><label>Telefon</label><input name="telefon" value={formData.telefon} onChange={handleChange} /></div>
        {/* CORECȚIE: nume_companie -> numeFirma */}
        <div className="form-group"><label>Nume Companie</label><input name="numeFirma" value={formData.numeFirma} onChange={handleChange} /></div>
        <div className="form-group">
          <label>Puncte de lucru</label>
          {/* CORECȚIE: punct_de_lucru -> puncteDeLucru */}
          {formData.puncteDeLucru.map((punct, index) => (
            <div key={index} style={{ display: "flex", marginBottom: "5px", gap: "5px" }}>
              <input value={punct} onChange={(e) => handlePunctDeLucruChange(index, e.target.value)} style={{ flex: 1 }} />
              <button type="button" onClick={() => removePunctDeLucru(index)} style={{ backgroundColor: "#dc3545", color: "white" }}>❌</button>
            </div>
          ))}
          <button type="button" onClick={addPunctDeLucru}>➕ Adaugă punct de lucru</button>
        </div>
        <button className="save-btn" onClick={handleSave}>💾 Salvează</button>
        <button className="back-btn" onClick={handleBack}>⬅ Înapoi</button>
      </div>
    );
  }

  return (
    <div className="beneficiari-container">
      <h1>Lista Firmelor Colaboratoare</h1>
      <div className="table-responsive">
        <table className="beneficiari-table">
          <thead>
            <tr><th>Nume Contact</th><th>Companie</th><th>Email</th><th>Acțiuni</th></tr>
          </thead>
          <tbody>
            {beneficiari.length > 0 ? (
              beneficiari.map((user) => (
                <tr key={user.id}> {/* CORECȚIE: Folosim user.id */}
                  <td>{user.nume} {user.prenume}</td>
                  <td>{user.numeCompanie || "N/A"}</td> {/* CORECȚIE: Folosim user.numeCompanie din SimpleUserDTO */}
                  <td>{user.email}</td>
                  <td style={{display: 'flex', gap: '5px', flexWrap: 'wrap'}}>
                    <button className="edit-btn" onClick={() => handleEdit(user)}>✏️ Editare</button>
                    <button className="edit-btn" style={{ backgroundColor: "#ffc107" }} onClick={() => handleChangePassword(user)}>🔑 Schimbă parola</button>
                    <button className="edit-btn" style={{ backgroundColor: "#dc3545" }} onClick={() => handleDelete(user.id)}>🗑️ Șterge</button> {/* CORECȚIE: Folosim user.id */}
                  </td>
                </tr>
              ))
            ) : (
              <tr><td colSpan="4" style={{ textAlign: "center" }}>Nu există beneficiari înregistrați.</td></tr>
            )}
          </tbody>
        </table>
      </div>
      <button className="back-bottom-btn" onClick={() => navigate(-1)}>⬅ Înapoi</button>
    </div>
  );
}