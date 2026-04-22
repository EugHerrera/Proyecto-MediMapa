import axios from 'axios';

// 1. Instancia para el microservicio de Usuarios (Puerto 8085)
export const apiUsuarios = axios.create({
  baseURL: import.meta.env.VITE_API_USUARIOS,
});

// 2. Instancia para el microservicio del Scraper (Puerto 8080)
export const apiScraper = axios.create({
  baseURL: import.meta.env.VITE_API_SCRAPER,
});

// 🔥 EL INTERCEPTOR MÁGICO 🔥
// Este código atrapa CADA petición que sale hacia tu backend de Usuarios antes de que viaje
apiUsuarios.interceptors.request.use(
  (config) => {
    // Busca el pasaporte en el almacenamiento local de React
    const token = localStorage.getItem('token');
    
    // Si hay un pasaporte guardado, lo inyecta en la cabecera (Header) de seguridad
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);