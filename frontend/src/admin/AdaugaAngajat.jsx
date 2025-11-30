import React, { useState } from "react";
import { useNavigate } from "react-router-dom"; 
import axios from 'axios'; 
import "./AdaugaAngajat.css";

export default function AdaugaAngajat() {
  const [formData, setFormData] = useState({
    nume: "",
    prenume: "",
    email: "",
    password: "",
    telefon: "",
    nr_legitimatie: ""
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
        setLoading(true);
    setError('');

    try {
        const userInfo = JSON.parse(localStorage.getItem('currentUser'));
        if (!userInfo || !userInfo.token) {
            throw new Error("Utilizator neautentificat!");
        }

        const config = {
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${userInfo.token}`,
            },
        };

        const payload = {
            nume: formData.nume,
            prenume: formData.prenume,
            email: formData.email,
            password: formData.password,
            telefon: formData.telefon,
            role: 'paznic', 
            profile: {
                nr_legitimatie: formData.nr_legitimatie
            }
        };

        await axios.post('http://localhost:8081/api/users/create', payload, config);

        alert("✅ Angajat (Paznic) adăugat cu succes!");
        navigate(-1); 

    } catch (err) {
        setError(err.response?.data?.message || 'A apărut o eroare. Vă rugăm să încercați din nou.');
    } finally {
        setLoading(false);
    }
  };

  return (
    <div className="adauga-angajat">
      <h2>Adaugă Angajat</h2>
      <form onSubmit={handleSubmit} className="form-container">
        <div className="form-group">
          <label htmlFor="nume">Nume:</label>
          <input id="nume" type="text" name="nume" value={formData.nume} onChange={handleChange} required className="form-input"/>
        </div>

        <div className="form-group">
          <label htmlFor="prenume">Prenume:</label>
          <input id="prenume" type="text" name="prenume" value={formData.prenume} onChange={handleChange} required className="form-input"/>
        </div>

        <div className="form-group">
          <label htmlFor="email">Email:</label>
          <input id="email" type="email" name="email" value={formData.email} onChange={handleChange} required className="form-input"/>
        </div>

        <div className="form-group">
          <label htmlFor="password">Parolă:</label>
          <input id="password" type="password" name="password" value={formData.password} onChange={handleChange} required className="form-input"/>
        </div>

        <div className="form-group">
          <label htmlFor="telefon">Telefon:</label>
          <input id="telefon" type="tel" name="telefon" value={formData.telefon} onChange={handleChange} className="form-input"/>
        </div>

        <div className="form-group">
          <label htmlFor="nr_legitimatie">Nr. legitimație:</label>
          <input id="nr_legitimatie" type="text" name="nr_legitimatie" value={formData.nr_legitimatie} onChange={handleChange} className="form-input"/>
        </div>
        
        {/* Afișare eroare */}
        {error && <p className="error-message" style={{ color: 'red', textAlign: 'center' }}>{error}</p>}

        {/* 🔹 Butoane pe același rând */}
        <div className="buttons">
          <button type="button" className="back-btn" onClick={() => navigate(-1)} disabled={loading}>
            ⬅ Înapoi
          </button>
          <button type="submit" className="save-btn" disabled={loading}>
            {loading ? 'Se salvează...' : 'Salvează'}
          </button>
        </div>
      </form>
    </div>
  );
}