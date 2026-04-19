import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './RedFarmacias.css';

// Importación de activos según tu estructura de carpetas
import logoAhumada from '../assets/Ahumada.png'; 
import logoSimi from '../assets/Drsimi.png';
import logoSalcobrand from '../assets/Salcobrand.png';

const RedFarmacias: React.FC = () => {
  // Estados para simular la interacción de filtros
  const [comuna, setComuna] = useState('La Florida');
  const [tipoFarmacia, setTipoFarmacia] = useState('Todas');

  return (
    <div className="red-container">
      
      {/* SECCIÓN 1: CABECERA Y LOGOS DE REDES ADHERIDAS */}
      <header className="red-header">
        <h1>Red de Farmacias MediMapa</h1>
        <p>Consulta las cadenas y establecimientos independientes que integran nuestro ecosistema de transparencia de precios.</p>
        
        <div className="logos-grid">
          {/* Farmacias Ahumada */}
          <a href="https://www.farmaciasahumada.cl/" target="_blank" rel="noopener noreferrer" className="logo-card link-card">
            <img src={logoAhumada} alt="Farmacias Ahumada" />
          </a>

          {/* Dr. Simi */}
          <a href="https://www.drsimi.cl/" target="_blank" rel="noopener noreferrer" className="logo-card link-card">
            <img src={logoSimi} alt="Dr. Simi" />
          </a>

          {/* Salcobrand */}
          <a href="https://salcobrand.cl/" target="_blank" rel="noopener noreferrer" className="logo-card link-card">
            <img src={logoSalcobrand} alt="Salcobrand" />
          </a>

          {/* Módulo de Inclusión B2B: Farmacias Independientes */}
          <Link to="/registro-farmacia" className="logo-card independiente-card link-card">
            <span className="independiente-icon">🏪</span>
            <span>Farmacias Independientes</span>
            <small>Inscribe tu local aquí</small>
          </Link>
        </div>
      </header>

      {/* SECCIÓN 2: PANEL DE BÚSQUEDA Y MAPA (POSTGIS/MAPBOX READY) */}
      <main className="red-main-content">
        
        {/* PANEL LATERAL: Filtros de Búsqueda */}
        <aside className="red-sidebar">
          <h3>Busca tu farmacia</h3>
          
          <div className="filter-group">
            <label>Seleccione Comuna</label>
            <select value={comuna} onChange={(e) => setComuna(e.target.value)}>
              <option value="Todas">Todas las comunas</option>
              <option value="La Florida">La Florida</option>
              <option value="Puente Alto">Puente Alto</option>
              <option value="Macul">Macul</option>
            </select>
          </div>

          <div className="filter-group">
            <label>Tipo de Establecimiento</label>
            <select value={tipoFarmacia} onChange={(e) => setTipoFarmacia(e.target.value)}>
              <option value="Todas">Todas</option>
              <option value="Cadenas">Grandes Cadenas</option>
              <option value="Independientes">Independientes (Pymes)</option>
            </select>
          </div>

          <div className="filter-group">
            <label>Nombre o Dirección</label>
            <input type="text" placeholder="Ej. Vicuña Mackenna..." />
          </div>

          <button className="btn-limpiar">🧹 Limpiar Filtros</button>
        </aside>

        {/* ÁREA DE RESULTADOS: Mapa y Tabla Informativa */}
        <section className="red-results">
          
          {/* Placeholder para la integración futura de Mapbox */}
          <div className="mapa-placeholder">
            <div className="mapa-overlay">
              <span className="mapa-icono">📍</span>
              <p>Mapa Interactivo de {comuna}</p>
              <small>Consultando base de datos espacial PostGIS</small>
            </div>
          </div>

          <div className="lista-farmacias">
            <table className="tabla-farmacias">
              <thead>
                <tr>
                  <th>Nombre Farmacia</th>
                  <th>Dirección</th>
                  <th>Tipo</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td><strong>Dr. Simi</strong></td>
                  <td>Av. Vicuña Mackenna 7110</td>
                  <td><span className="badge cadena">Cadena</span></td>
                </tr>
                <tr>
                  <td><strong>Farmacia Ahumada</strong></td>
                  <td>Av. La Florida 8988</td>
                  <td><span className="badge cadena">Cadena</span></td>
                </tr>
                <tr>
                  <td><strong>Salcobrand</strong></td>
                  <td>Vicuña Mackenna 9100, La Florida</td>
                  <td><span className="badge cadena">Cadena</span></td>
                </tr>
                <tr>
                  <td><strong>Farmacia La Salud</strong></td>
                  <td>Rojas Magallanes 1234</td>
                  <td><span className="badge independiente">Independiente</span></td>
                </tr>
              </tbody>
            </table>
          </div>

        </section>
      </main>
    </div>
  );
};

export default RedFarmacias;