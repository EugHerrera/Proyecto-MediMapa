import axios from 'axios';

// Agregamos el /api al final para que el Gateway reconozca las rutas
const GATEWAY_URL = 'https://232308a2bcb7b5.lhr.life/api';

export const apiUsuarios = axios.create({
  baseURL: GATEWAY_URL,
});

export const apiScraper = axios.create({
  baseURL: GATEWAY_URL,
});

export const apiGeo = axios.create({
  baseURL: GATEWAY_URL,
});

// Interceptor para inyectar el Token JWT en ms-usuarios
apiUsuarios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);