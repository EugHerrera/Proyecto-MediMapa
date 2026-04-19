import React, { useState } from 'react';
import './Catalogo.css';

const Catalogo: React.FC = () => {
  const [categoriaActiva, setCategoriaActiva] = useState('Todas');
  const [busqueda, setBusqueda] = useState('');

  // Categorías basadas en la imagen de referencia
  const categorias = [
    'Seleccionar todo',
    'Antibióticos y antibacterianos',
    'Antiinfecciosos',
    'Digestivo y gastrointestinal',
    'Oftalmología y dermatología',
    'Respiratorio',
    'Vitaminas y minerales',
    'Analgésicos y antiinflamatorios',
    'Anticonceptivos y salud sexual',
    'Cardiovascular y circulación',
    'Hormonas y endocrino',
    'Oncológicos e inmunoterapia',
    'Salud mental y neurológico',
    'Antialérgicos',
    'Antidiabéticos y metabolismo',
    'Corticoides e inmunológicos',
    'Insumos clínicos',
    'Urológico y próstata'
  ];

  const handleCategoriaClick = (cat: string) => {
    setCategoriaActiva(cat === 'Seleccionar todo' ? 'Todas' : cat);
  };

  return (
    <div className="catalogo-container">
      
      {/* BANNER PRINCIPAL */}
      <div className="catalogo-banner">
        <div className="banner-content">
          <div className="banner-title">
            <span className="banner-icon">📘</span>
            <h2>Catálogo Maestro MediMapa</h2>
          </div>
          <div className="banner-meta">
            <span>Fecha de actualización: <strong>{new Date().toLocaleDateString('es-CL')}</strong></span>
          </div>
        </div>
      </div>

      {/* CUADRO DE ADVERTENCIA / LEGAL */}
      <div className="catalogo-disclaimer">
        <div className="disclaimer-icon">💡</div>
        <div className="disclaimer-text">
          <h4>Aclaración sobre la información del catálogo:</h4>
          <p>
            El presente catálogo ha sido estructurado cruzando la oferta comercial de farmacias adheridas con los registros oficiales del <strong>Instituto de Salud Pública (ISP)</strong> y el <strong>MINSAL</strong>. Los datos extraídos (Web Scraping) tienen fines estrictamente informativos y de comparación referencial.
          </p>
          <p>
            MediMapa no realiza publicidad, promoción ni recomendación comercial, operando como un <strong>Agregador Informativo</strong>. Se recomienda consultar siempre a un profesional de la salud y confirmar el stock directamente en la sucursal física.
          </p>
        </div>
      </div>

      {/* SECCIÓN DE CATEGORÍAS (GRILLA) */}
      <div className="catalogo-categorias">
        <h3 className="section-title">Categoría Terapéutica</h3>
        <div className="categorias-grid">
          {categorias.map((cat, index) => (
            <button 
              key={index}
              className={`categoria-btn ${categoriaActiva === cat || (cat === 'Seleccionar todo' && categoriaActiva === 'Todas') ? 'activa' : ''}`}
              onClick={() => handleCategoriaClick(cat)}
            >
              {cat}
            </button>
          ))}
        </div>
      </div>

      {/* SECCIÓN DE BÚSQUEDA Y RESULTADOS */}
      <div className="catalogo-search-section">
        <div className="search-box">
          <label>Buscador de medicamento</label>
          <div className="input-with-icon">
            <span className="search-icon">🔍</span>
            <input 
              type="text" 
              placeholder="Escribe el principio activo o marca..." 
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
            />
          </div>
        </div>

        <div className="select-box">
          <label>Listado de medicamentos</label>
          <select>
            <option>Todos los disponibles</option>
            <option>Solo Bioequivalentes (ISP)</option>
            <option>Medicamentos Genéricos</option>
            <option>Medicamentos de Marca</option>
          </select>
        </div>
      </div>

      {/* TABLA DE RESULTADOS (SIMULADA) */}
      <div className="catalogo-resultados">
        <table className="tabla-catalogo">
          <thead>
            <tr>
              <th>Principio Activo</th>
              <th>Nombre Comercial (Ejemplo)</th>
              <th>Categoría</th>
              <th>Bioequivalente</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><strong>Paracetamol 500mg</strong></td>
              <td>Tapsin / Kitadol</td>
              <td>Analgésicos y antiinflamatorios</td>
              <td><span className="badge bio">Sí (ISP)</span></td>
            </tr>
            <tr>
              <td><strong>Levotiroxina 100mcg</strong></td>
              <td>Eutirox</td>
              <td>Hormonas y endocrino</td>
              <td><span className="badge bio">Sí (ISP)</span></td>
            </tr>
            <tr>
              <td><strong>Amoxicilina 500mg</strong></td>
              <td>Amoval</td>
              <td>Antibióticos y antibacterianos</td>
              <td><span className="badge bio">Sí (ISP)</span></td>
            </tr>
          </tbody>
        </table>
      </div>

    </div>
  );
};

export default Catalogo;