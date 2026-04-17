import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import './Home.css'; 

const Login = () => {
  const [correo, setCorreo] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [cargando, setCargando] = useState(false); // Para mostrar un estado de carga
  const navigate = useNavigate();

  const manejarLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setCargando(true);

    try {
      // Aquí hacemos la llamada a tu Microservicio de Usuarios en el puerto 8085
      const respuesta = await fetch('http://localhost:8085/api/usuarios/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ correo, password }), // Mandamos el DTO
      });

      const data = await respuesta.json();

      if (respuesta.ok) {
        // ¡Las credenciales son correctas!
        alert(`¡Bienvenido! Has ingresado con el rol de: ${data.rol}`);
        
        // Aquí le decimos al navegador que recuerde que este usuario ya inició sesión
        localStorage.setItem('usuarioRol', data.rol);
        
        // Lo redirigimos al buscador (después lo mandaremos al panel de control)
        navigate('/'); 
      } else {
        // El backend nos devolvió un error (ej: clave incorrecta)
        setError(data.error || 'Correo o contraseña incorrectos');
      }
    } catch (err) {
      // Este error salta si el backend está apagado o hay un problema de red
      setError('❌ No se pudo conectar con el servidor (Puerto 8085).');
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="home-container">
      <div className="search-card" style={{ maxWidth: '400px' }}>
        <div className="logo-container">
          <span className="logo-icon">🔐</span>
          <h1 className="logo-text" style={{ fontSize: '2rem' }}>Acceso</h1>
        </div>
        
        <p className="subtitle" style={{ marginBottom: '1.5rem' }}>
          Portal para Farmacias y Administradores
        </p>

        {error && <p style={{ color: 'red', fontWeight: 'bold', marginBottom: '1rem', backgroundColor: '#fee2e2', padding: '10px', borderRadius: '8px' }}>{error}</p>}

        <form onSubmit={manejarLogin} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
          <input
            type="email"
            placeholder="Correo electrónico"
            value={correo}
            onChange={(e) => setCorreo(e.target.value)}
            className="search-input"
            style={{ paddingLeft: '20px' }}
            required
          />
          <input
            type="password"
            placeholder="Contraseña"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="search-input"
            style={{ paddingLeft: '20px' }}
            required
          />
          <button 
            type="submit" 
            className="search-button" 
            style={{ marginTop: '10px', opacity: cargando ? 0.7 : 1 }}
            disabled={cargando}
          >
            {cargando ? 'Verificando...' : 'Iniciar Sesión'}
          </button>
        </form>

        <div style={{ marginTop: '2rem', fontSize: '0.9rem' }}>
          <Link to="/" style={{ color: '#0ea5e9', textDecoration: 'none', fontWeight: 'bold' }}>
            ⬅ Volver al buscador
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Login;