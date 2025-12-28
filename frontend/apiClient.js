import axios from 'axios';

// --- BLOC MODIFICAT PENTRU ROBUSTEȚE ---

let baseURL;

// Verificăm dacă suntem în modul de producție.
// Vite setează automat `import.meta.env.PROD` la `true` la build.
if (import.meta.env.PROD) {
  // Suntem în producție (pe Railway)
  // Folosim variabila de mediu setată pe server.
  baseURL = `${import.meta.env.VITE_API_BASE_URL}/api`;
} else {
  // Suntem în dezvoltare (localhost)
  // OCOLIM PROXY-UL VITE ȘI SETĂM URL-ul direct către Spring Boot (8081)
  baseURL = 'http://localhost:8081/api'; 
}

// --- SFÂRȘIT BLOC MODIFICAT ---

const apiClient = axios.create({
  baseURL: baseURL,
});

// Interceptor-ul pentru token rămâne neschimbat
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  console.log("API TOKEN:", token);

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default apiClient;