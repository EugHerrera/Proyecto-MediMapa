import React, { useState, useEffect } from 'react';
import './Catalogo.css';

// 1. ESPEJO DEL DTO DE JAVA
interface MedicamentoResponseDTO {
  idMedicamento: number;
  nombreCanonico: string;
  principioActivo: string;
  categoria: string | null;
  esBioequivalente: boolean;
}

const Catalogo: React.FC = () => {
  const [categoriaActiva, setCategoriaActiva] = useState('Todas');
  const [busqueda, setBusqueda] = useState('');
  
  // 2. NUEVOS ESTADOS PARA LOS DATOS REALES
  const [medicamentos, setMedicamentos] = useState<MedicamentoResponseDTO[]>([]);
  const [cargando, setCargando] = useState(false);

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

  // 3. EL CEREBRO: Llamada a tu backend en Spring Boot (Puerto 8081)
  useEffect(() => {
    const fetchMedicamentos = async () => {
      setCargando(true);
      try {
        let url = 'http://localhost:8081/api/medicamentos'; // Endpoint base (listar todos)

        // Si hay texto en la búsqueda, usamos el endpoint de buscar (ignora la categoría)
        if (busqueda.trim() !== '') {
          url = `http://localhost:8081/api/medicamentos/buscar?q=${busqueda}`;
        } 
        // Si no hay texto, pero se seleccionó una categoría específica
        else if (categoriaActiva !== 'Todas') {
          url = `http://localhost:8081/api/medicamentos/categoria?nombre=${categoriaActiva}`;
        }

        const respuesta = await fetch(url);
        if (respuesta.ok) {
          const data = await respuesta.json();
          setMedicamentos(data);
        } else {
          console.error("Error al obtener datos del servidor");
        }
      } catch (error) {
        console.error("Error de conexión:", error);
      } finally {
        setCargando(false);
      }
    };

    // Usamos un pequeño 'debounce' (retraso) automático al escribir para no saturar el backend
    const timeoutId = setTimeout(() => {
      fetchMedicamentos();
    }, 300);

    return () => clearTimeout(timeoutId);

  }, [busqueda, categoriaActiva]); // Se ejecuta cada vez que estos dos cambian

  const handleCategoriaClick = (cat: string) => {
    setCategoriaActiva(cat === 'Seleccionar todo' ? 'Todas' : cat);
    setBusqueda(''); // Limpia la búsqueda por texto si hace clic en un botón
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

      {/* TABLA DE RESULTADOS DINÁMICA */}
      <div className="catalogo-resultados">
        {cargando ? (
           <div style={{ padding: '20px', textAlign: 'center', color: '#059669', fontWeight: 'bold' }}>
             Cargando catálogo...
           </div>
        ) : medicamentos.length === 0 ? (
           <div style={{ padding: '20px', textAlign: 'center', color: '#64748b' }}>
             No se encontraron medicamentos para esta búsqueda o categoría.
           </div>
        ) : (
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
              {/* 4. RENDERIZADO DEL MAP (Iteramos sobre los datos del backend) */}
              {medicamentos.map((med) => (
                <tr key={med.idMedicamento}>
                  <td><strong>{med.principioActivo || med.nombreCanonico}</strong></td>
                  <td>{med.nombreCanonico}</td>
                  <td>{med.categoria || 'Sin clasificar'}</td>
                  <td>
                    {med.esBioequivalente ? (
                      <span className="badge bio">Sí (ISP)</span>
                    ) : (
                      <span style={{color: '#94a3b8', fontSize: '0.85rem'}}>-</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

    </div>
  );
};

export default Catalogo;