import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
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
      // Llamada a tu Microservicio de Usuarios (Puerto 8085)
      const respuesta = await fetch('http://localhost:8085/api/usuarios/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ correo, password }),
      });

      const data = await respuesta.json();

      if (respuesta.ok) {
        // 1. Guardamos el rol y el correo en el almacenamiento local
        localStorage.setItem('usuarioRol', data.rol);
        localStorage.setItem('usuarioCorreo', correo); // Útil para mostrar su nombre en el panel
        
        // 2. REDIRECCIÓN INTELIGENTE SEGÚN EL ROL
        const rolUsuario = data.rol.toUpperCase(); // Lo pasamos a mayúsculas por seguridad

        if (rolUsuario === 'ADMIN' || rolUsuario === 'FARMACEUTICO') {
          // Si es dueño o farmacéutico, lo mandamos a su área de trabajo
          navigate('/admin'); 
        } else {
          // Si tienes roles de usuarios normales (clientes), los dejas en el Home
          navigate('/'); 
        }
        
      } else {
        setError(data.error || 'Correo o contraseña incorrectos');
      }
    } catch (err) {
      setError('❌ No se pudo conectar con el servidor (Puerto 8085).');
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