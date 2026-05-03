import { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import './Navbar.css';

// Importación según tu estructura de carpetas: src/assets/MediMapa.png
import logoMediMapa from '../assets/MediMapa.png'; 

const Navbar = () => {
  const location = useLocation(); 
  const navigate = useNavigate();
  
  // 🔥 NUEVO ESTADO: Para saber el rol del usuario logueado
  const [rolUsuario, setRolUsuario] = useState<string | null>(null);

  // 🔥 NUEVA LÓGICA: Cada vez que cambia la URL, revisamos la "billetera" (localStorage)
  useEffect(() => {
    const rolGuardado = localStorage.getItem('usuarioRol');
    setRolUsuario(rolGuardado);
  }, [location]);

  // 🔥 NUEVA LÓGICA: Botón de Cerrar Sesión
  const manejarCerrarSesion = () => {
    localStorage.clear(); // Vaciamos la billetera
    setRolUsuario(null);
    navigate('/'); // Lo mandamos de vuelta al inicio
  };

  // Función para resaltar el link activo
  const isActive = (path: string) => {
    return location.pathname === path ? 'active-link' : '';
  };

  return (
    <nav className="navbar-container">
      <div className="navbar-content">
        
        {/* LADO IZQUIERDO: Logo oficial importado */}
        <Link to="/" className="navbar-logo">
          <img src={logoMediMapa} alt="MediMapa Logo" className="logo-imagen" />
          <span className="logo-text">MediMapa</span>
        </Link>

        {/* CENTRO: Navegación Principal */}
        <ul className="navbar-links">
          <li><Link to="/" className={isActive('/')}>Inicio</Link></li>
          <li><Link to="/catalogo" className={isActive('/catalogo')}>Catálogo</Link></li>
          <li><Link to="/bioequivalentes" className={isActive('/bioequivalentes')}>Bioequivalentes</Link></li>
          <li><Link to="/red-farmacias" className={isActive('/red-farmacias')}>Red de Farmacias</Link></li>
          <li><Link to="/faq" className={isActive('/faq')}>Preguntas Frecuentes</Link></li>
        </ul>

        {/* LADO DERECHO: Acciones Inteligentes según Sesión */}
        <div className="navbar-actions">
          
          {rolUsuario ? (
            // 🔥 SI EL USUARIO ESTÁ LOGUEADO: Mostrar su Rol, Botón al Panel y Cerrar Sesión
            <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
              <span style={{ 
                backgroundColor: '#fefce8', 
                color: '#854d0e', 
                padding: '6px 14px', 
                borderRadius: '20px', 
                fontWeight: 'bold', 
                fontSize: '0.85rem',
                border: '1px solid #ca8a04'
              }}>
                👤 {rolUsuario.toUpperCase()}
              </span>
              
              <Link to="/admin" className="nav-login-btn" style={{ backgroundColor: '#f8fafc', color: '#0ea5e9', border: '1px solid #e2e8f0', padding: '6px 12px', borderRadius: '6px' }}>
                Ir al Panel
              </Link>
              
              <button 
                onClick={manejarCerrarSesion} 
                className="nav-login-btn" 
                style={{ backgroundColor: 'transparent', color: '#ef4444', border: 'none', padding: '0', fontSize: '0.9rem', cursor: 'pointer' }}
              >
                Cerrar Sesión
              </button>
            </div>
          ) : (
            // 🔥 SI NADIE ESTÁ LOGUEADO: Mostrar lo clásico (Login y Registro)
            <>
              <Link to="/login" className="nav-login-btn">
                <span className="user-icon">👤</span> Iniciar Sesión
              </Link>
              <Link to="/registro-farmacia" className="nav-cta-btn">
                Inscribe tu Farmacia
              </Link>
            </>
          )}

        </div>
      </div>
    </nav>
  );
};

export default Navbar;