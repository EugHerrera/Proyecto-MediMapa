import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import './Home.css'; 

import logoAhumada from '../assets/Ahumada.png'; 
import logoSimi from '../assets/Drsimi.png';
import logoSalcobrand from '../assets/Salcobrand.png';

const Home = () => {
  const [busqueda, setBusqueda] = useState('');
  const navigate = useNavigate();

  const manejarBusqueda = (e: React.FormEvent) => {
    e.preventDefault();
    if (busqueda.trim() !== '') {
      navigate(`/resultados?q=${encodeURIComponent(busqueda)}`);
    }
  };

  const sugerenciasRapidas = ['Paracetamol', 'Ibuprofeno', 'Loratadina', 'Kitadol'];

  return (
    <div className="home-modern-container">
      <div className="hero-section">
        
        <div className="hero-content">
          <div className="badge-modern">Nueva plataforma web Informativa y Geolocalizada</div>
          
          <h1 className="hero-title">
            Encuentra tu medicamento al <span className="highlight-green">mejor precio</span> en La Florida
          </h1>
          
          <p className="hero-subtitle">
            Comparamos los valores entre las principales cadenas y farmacias independientes para que cuides tu salud y tu bolsillo en segundos.
          </p>

          <form onSubmit={manejarBusqueda} className="modern-search-form">
            <div className="modern-input-wrapper" style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
              <span className="search-icon" style={{ position: 'absolute', left: '20px', fontSize: '1.2rem', color: '#94a3b8' }}>
                🔍
              </span>
              <input
                type="text"
                placeholder="Ej. Paracetamol, Tapsin, Eutirox..."
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
                className="modern-search-input"
                style={{ paddingLeft: '50px' }}
              />
              <button type="submit" className="modern-search-button">
                Buscar ahora
              </button>
            </div>
          </form>

          <div className="modern-suggestions">
            <p>Búsquedas frecuentes:</p>
            <div className="modern-pills">
              {sugerenciasRapidas.map((med, index) => (
                <button 
                  key={index} 
                  onClick={() => setBusqueda(med)}
                  className="modern-pill"
                  type="button"
                >
                  {med}
                </button>
              ))}
            </div>
          </div>
          
          <div className="trust-indicators">
            <p>Comparamos inventarios de:</p>
            <div className="trust-logos">
               <img src={logoSalcobrand} alt="Salcobrand" className="trust-logo-img" />
               <img src={logoAhumada} alt="Ahumada" className="trust-logo-img" />
               <img src={logoSimi} alt="Dr. Simi" className="trust-logo-img" />
               <span className="logo-placeholder-pyme">🏪 Farmacias Locales</span>
            </div>
          </div>
        </div>

      </div>

      {/* NUEVA SECCIÓN: LAS 3 TARJETAS CLÍNICAS */}
      <div className="home-cards-section">
        <Link to="/catalogo" className="action-card">
          <div className="card-icon-wrapper">
            <span className="card-icon">📘</span>
          </div>
          <h3>Catálogo Maestro ISP</h3>
          <p>Explora todos los medicamentos y descubre sus alternativas bioequivalentes certificadas.</p>
          <span className="card-link">Ver Catálogo →</span>
        </Link>

        <Link to="/red-farmacias" className="action-card">
          <div className="card-icon-wrapper">
            <span className="card-icon">📍</span>
          </div>
          <h3>Red de Farmacias</h3>
          <p>Conoce las cadenas y pymes locales de La Florida asociadas a nuestra plataforma.</p>
          <span className="card-link">Ver Mapa →</span>
        </Link>

        <Link to="/faq" className="action-card">
          <div className="card-icon-wrapper">
            <span className="card-icon">🩺</span>
          </div>
          <h3>Educación y Salud</h3>
          <p>Resuelve tus dudas sobre bioequivalencia, recetas y cómo usar MediMapa de forma segura.</p>
          <span className="card-link">Centro de Ayuda →</span>
        </Link>
      </div>

    </div>
  );
};

export default Home;