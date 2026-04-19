import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import './Navbar.css';

// Importación según tu estructura de carpetas: src/assets/MediMapa.png
import logoMediMapa from '../assets/MediMapa.png'; 

const Navbar = () => {
  const location = useLocation(); 

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

        {/* CENTRO: Navegación Principal (Ahora todo separado) */}
        <ul className="navbar-links">
          <li><Link to="/" className={isActive('/')}>Inicio</Link></li>
          
          {/* Catálogo y Bioequivalentes como links directos */}
          <li><Link to="/catalogo" className={isActive('/catalogo')}>Catálogo</Link></li>
          <li><Link to="/bioequivalentes" className={isActive('/bioequivalentes')}>Bioequivalentes</Link></li>
          
          <li><Link to="/red-farmacias" className={isActive('/red-farmacias')}>Red de Farmacias</Link></li>
          <li><Link to="/faq" className={isActive('/faq')}>Preguntas Frecuentes</Link></li>
        </ul>

        {/* LADO DERECHO: Acciones B2B (Inscripción) y Usuarios */}
        <div className="navbar-actions">
          <Link to="/login" className="nav-login-btn">
            <span className="user-icon">👤</span> Iniciar Sesión
          </Link>
          <Link to="/registro-farmacia" className="nav-cta-btn">
            Inscribe tu Farmacia
          </Link>
        </div>

      </div>
    </nav>
  );
};

export default Navbar;