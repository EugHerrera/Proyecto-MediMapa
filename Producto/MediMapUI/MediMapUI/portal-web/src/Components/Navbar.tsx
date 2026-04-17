import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  const location = useLocation(); // Para saber en qué página estamos
  const [menuAbierto, setMenuAbierto] = useState(false);

  // Función para saber si un link está activo y pintarlo de azul
  const isActive = (path: string) => {
    return location.pathname === path ? 'active-link' : '';
  };

  return (
    <nav className="navbar-container">
      <div className="navbar-content">
        
        {/* LADO IZQUIERDO: Logo */}
        <Link to="/" className="navbar-logo">
          <span className="logo-icon">💊</span>
          <span className="logo-text">MediMapa</span>
        </Link>

        {/* CENTRO: Enlaces de Navegación */}
        <ul className="navbar-links">
          <li><Link to="/" className={isActive('/')}>Inicio</Link></li>
          
          {/* Menú Desplegable del Catálogo */}
          <li 
            className="dropdown-container"
            onMouseEnter={() => setMenuAbierto(true)}
            onMouseLeave={() => setMenuAbierto(false)}
          >
            <span className={`dropdown-trigger ${isActive('/catalogo') || isActive('/bioequivalentes') ? 'active-link' : ''}`}>
              Catálogo ▾
            </span>
            {menuAbierto && (
              <div className="dropdown-menu">
                <Link to="/catalogo" className="dropdown-item">Todos los medicamentos</Link>
                <Link to="/bioequivalentes" className="dropdown-item highlight-item">✨ Alternativas Bioequivalentes</Link>
              </div>
            )}
          </li>

          <li><Link to="/red-farmacias" className={isActive('/red-farmacias')}>Red de Farmacias</Link></li>
        </ul>

        {/* LADO DERECHO: Acciones */}
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