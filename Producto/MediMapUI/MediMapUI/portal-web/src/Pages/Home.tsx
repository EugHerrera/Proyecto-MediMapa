import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Home.css'; // ¡Aquí conectamos la magia!

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
    <div className="home-container">
      <div className="search-card">
        <div className="logo-container">
          <span className="logo-icon">💊</span>
          <h1 className="logo-text">MediMapa</h1>
        </div>
        
        <h2 className="main-title">Encuentra tu medicamento al mejor precio en La Florida</h2>
        <p className="subtitle">Compara precios entre Salcobrand, Ahumada y Dr. Simi en segundos.</p>

        <form onSubmit={manejarBusqueda} className="search-form">
          <div className="input-wrapper">
            <span className="search-icon">🔍</span>
            <input
              type="text"
              placeholder="Ej. Paracetamol, Tapsin..."
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              className="search-input"
            />
          </div>
          <button type="submit" className="search-button">
            Buscar
          </button>
        </form>

        <div className="suggestions-container">
          <p className="suggestions-title">Búsquedas frecuentes:</p>
          <div className="pills-wrapper">
            {sugerenciasRapidas.map((med, index) => (
              <button 
                key={index} 
                onClick={() => setBusqueda(med)}
                className="suggestion-pill"
                type="button"
              >
                {med}
              </button>
            ))}
          </div>
        </div>
      </div>
      
      <div className="footer-credits">
        <p>Proyecto de Título - Duoc UC | Arquitectura de Microservicios</p>
      </div>
    </div>
  );
};

export default Home;