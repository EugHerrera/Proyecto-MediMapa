import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Home.css'; 

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
        
        {/* COLUMNA IZQUIERDA: Textos y Buscador */}
        <div className="hero-content">
          <div className="badge-modern">✨ Nueva plataforma web</div>
          
          <h1 className="hero-title">
            Encuentra tu medicamento al <span className="highlight-green">mejor precio</span> en La Florida
          </h1>
          
          <p className="hero-subtitle">
            Comparamos los valores entre las principales cadenas y farmacias independientes para que cuides tu salud y tu bolsillo en segundos.
          </p>

          <form onSubmit={manejarBusqueda} className="modern-search-form">
            <div className="modern-input-wrapper">
              <span className="search-icon">🔍</span>
              <input
                type="text"
                placeholder="Ej. Paracetamol, Tapsin, Eutirox..."
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
                className="modern-search-input"
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
          
          {/* Trust Indicators (Logos simulados) */}
          <div className="trust-indicators">
            <p>Comparamos inventarios de:</p>
            <div className="trust-logos">
               {/* Aquí después puedes poner imágenes reales de los logos */}
               <span className="logo-placeholder">Salcobrand</span>
               <span className="logo-placeholder">Ahumada</span>
               <span className="logo-placeholder">Dr. Simi</span>
            </div>
          </div>
        </div>

        {/* COLUMNA DERECHA: Imagen/Ilustración */}
        <div className="hero-image-container">
          {/* Aquí iría tu imagen generada por IA. Por ahora ponemos un placeholder elegante */}
          <div className="image-placeholder">
            <span className="placeholder-icon">🏥</span>
            <p>Inserta aquí una ilustración 3D de una farmacia o elementos médicos</p>
          </div>
        </div>

      </div>
    </div>
  );
};

export default Home;