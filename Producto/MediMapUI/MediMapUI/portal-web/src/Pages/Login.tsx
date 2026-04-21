import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { apiUsuarios } from '../services/api'; // <-- 1. IMPORTAMOS NUESTRO CARTERO
import './Login.css'; 

const Login = () => {
  const [correo, setCorreo] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [cargando, setCargando] = useState(false);
  const navigate = useNavigate();

  const manejarLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setCargando(true);

    try {
      // 🔥 2. MAGIA DE AXIOS: URL limpia y petición directa
      // Ya sabe que debe ir a http://localhost:8085/api gracias al .env
      const respuesta = await apiUsuarios.post('/usuarios/login', {
        correo: correo,
        password: password
      });

      // 3. Con Axios, la respuesta JSON siempre viene dentro de ".data"
      const data = respuesta.data;

      console.log("¡Respuesta de Java completa!:", data);
      console.log("¿Me dio un pasaporte JWT?:", data.token);

      // Guardamos el rol, el correo y EL TOKEN
      localStorage.setItem('usuarioRol', data.rol);
      localStorage.setItem('usuarioCorreo', correo); 
      localStorage.setItem('token', data.token); 
      
      // Redirección inteligente
      const rolUsuario = data.rol.toUpperCase(); 

      if (rolUsuario === 'ADMIN' || rolUsuario === 'FARMACEUTICO') {
        navigate('/admin'); 
      } else {
        navigate('/'); 
      }
        
    } catch (err: any) {
      // 4. Axios maneja los errores automáticamente. Si Java manda un 401 (No Autorizado), cae aquí.
      if (err.response && err.response.data && err.response.data.error) {
        setError(err.response.data.error); // Mensaje específico de tu backend
      } else {
        setError('❌ No se pudo conectar con el servidor.');
      }
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="login-page-container">
      <div className="login-card">
        <h1>Acceso</h1>
        <p>Portal para Farmacias y Administradores</p>

        {error && (
          <div style={{ 
            color: '#991b1b', 
            backgroundColor: '#fee2e2', 
            padding: '12px', 
            borderRadius: '8px', 
            marginBottom: '20px',
            fontSize: '0.9rem',
            fontWeight: '600',
            border: '1px solid #fecaca'
          }}>
            {error}
          </div>
        )}

        <form onSubmit={manejarLogin} className="login-form">
          <div className="input-group-login">
            <label>Correo electrónico</label>
            <input
              type="email"
              placeholder="ejemplo@farmacia.cl"
              value={correo}
              onChange={(e) => setCorreo(e.target.value)}
              required
            />
          </div>

          <div className="input-group-login">
            <label>Contraseña</label>
            <input
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button 
            type="submit" 
            className="btn-login-submit" 
            disabled={cargando}
            style={{ opacity: cargando ? 0.7 : 1 }}
          >
            {cargando ? 'Verificando...' : 'Iniciar Sesión'}
          </button>
        </form>

        <Link to="/" className="btn-volver-buscador">
          ← Volver al buscador
        </Link>
      </div>
    </div>
  );
};

export default Login;
