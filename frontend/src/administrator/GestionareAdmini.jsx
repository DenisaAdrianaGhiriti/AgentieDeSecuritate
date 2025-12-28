import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from '../../apiClient'; 
import "../admin/Angajati.css";

export default function GestionareAdmini() {
  const [admini, setAdmini] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchAdmini = async () => {
      setLoading(true);
      try {
        // GET /users/list/admin returnează User DTO-uri (cu 'id' și câmpuri camelCase)
        const { data } = await apiClient.get("/users/list/admin");
        setAdmini(data);
      } catch (err) {
        setError(err.response?.data?.message || "Eroare la preluarea listei de admini.");
      } finally {
        setLoading(false);
      }
    };
    fetchAdmini();
  }, []);

  const handleDelete = async (userId) => {
    if (!window.confirm("Sunteți sigur că doriți să ștergeți acest cont de admin? Acțiunea este ireversibilă.")) return;

    try {
      // DELETE /users/{userId}
      await apiClient.delete(`/users/${userId}`);
      alert("Cont de admin șters cu succes!");
      // CORECȚIE: Filtrare după ID-ul corect (user.id)
      setAdmini(prev => prev.filter(user => user.id !== userId));
    } catch (err) {
      alert(`❌ Eroare: ${err.response?.data?.message || "Nu s-a putut șterge contul."}`);
    }
  };

  if (loading) return <div className="loading" style={{textAlign: 'center', padding: '50px'}}>Se încarcă...</div>;
  if (error) return <div className="loading error-message" style={{textAlign: 'center', padding: '50px', color: 'red'}}>{error}</div>;

  return (
    <div className="angajati-container">
      <h1>Gestionare Conturi Admin</h1>
      <div className="table-responsive">
        <table className="angajati-table">
          <thead>
            <tr>
              <th>Nume</th>
              <th>Prenume</th>
              <th>Email</th>
              <th>Acțiuni</th>
            </tr>
          </thead>
          <tbody>
            {admini.length > 0 ? (
              admini.map((user) => (
                <tr key={user.id}> {/* CORECȚIE: Folosim user.id */}
                  <td>{user.nume}</td>
                  <td>{user.prenume}</td>
                  <td>{user.email}</td>
                  <td>
                    <button
                      className="edit-btn"
                      style={{ backgroundColor: "#dc3545" }}
                      onClick={() => handleDelete(user.id)}
                    >
                      🗑️ Șterge
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr><td colSpan="4" style={{textAlign: 'center'}}>Nu există alte conturi de admin create.</td></tr>
            )}
          </tbody>
        </table>
      </div>
      <button className="back-bottom-btn" onClick={() => navigate(-1)}>
        ⬅ Înapoi
      </button>
    </div>
  );
}