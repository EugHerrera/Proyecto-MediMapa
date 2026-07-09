import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiUsuarios } from '../services/api';
import './Catalogo.css';

interface MedicamentoResponseDTO {
  idMedicamento: number;
  nombreCanonico: string;
  principioActivo: string;
  categoria: string | null;
  esBioequivalente: boolean;
}

// Función para capitalizar solo la primera letra
const formatearTexto = (texto: string | null) => {
  if (!texto) return '';
  return texto.charAt(0).toUpperCase() + texto.slice(1).toLowerCase();
};

const Catalogo: React.FC = () => {
  const navigate = useNavigate();
  const [categoriaActiva, setCategoriaActiva] = useState('Todas');
  const [busqueda, setBusqueda] = useState('');
  
  const [filtroTipo, setFiltroTipo] = useState('Todos los disponibles'); 

  const [medicamentos, setMedicamentos] = useState<MedicamentoResponseDTO[]>([]);
  const [cargando, setCargando] = useState(false);
  const [paginaActual, setPaginaActual] = useState(1);

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
    'Urológico y próstata'
  ];

  useEffect(() => {
    const fetchMedicamentos = async () => {
      setCargando(true);
      try {
        let url = '/medicamentos';

        if (busqueda.trim() !== '') {
          url = `/medicamentos/buscar?q=${busqueda}`;
        } 
        else if (categoriaActiva !== 'Todas') {
          url = `/medicamentos/categoria?nombre=${categoriaActiva}`;
        }

        // AXIOS 
        const respuesta = await apiUsuarios.get(url);
        setMedicamentos(respuesta.data);
      } catch (error) {
        console.error("Error de conexión:", error);
      } finally {
        setCargando(false);
      }
    };

    const timeoutId = setTimeout(() => {
      fetchMedicamentos();
    }, 300);

    return () => clearTimeout(timeoutId);

  }, [busqueda, categoriaActiva]); 

  // Resetear página al cambiar búsqueda, categoría o tipo de filtro
  useEffect(() => {
    setPaginaActual(1);
  }, [busqueda, categoriaActiva, filtroTipo]);

  const handleCategoriaClick = (cat: string) => {
    setCategoriaActiva(cat === 'Seleccionar todo' ? 'Todas' : cat);
    setBusqueda(''); 
  };

  const medicamentosFiltrados = medicamentos.filter((med) => {
    if (filtroTipo === 'Solo Bioequivalentes (ISP)') {
      return med.esBioequivalente === true;
    }
    if (filtroTipo === 'Medicamentos Genéricos') {
      return med.nombreCanonico?.toLowerCase() === med.principioActivo?.toLowerCase();
    }
    if (filtroTipo === 'Medicamentos de Marca') {
      return med.nombreCanonico?.toLowerCase() !== med.principioActivo?.toLowerCase();
    }
    return true; 
  });

  // Paginación de 100 elementos
  const ITEMS_POR_PAGINA = 100;
  const totalPaginas = Math.ceil(medicamentosFiltrados.length / ITEMS_POR_PAGINA);
  const medicamentosPaginados = medicamentosFiltrados.slice(
    (paginaActual - 1) * ITEMS_POR_PAGINA,
    paginaActual * ITEMS_POR_PAGINA
  );

  return (
    <div className="catalogo-container">
      
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
          <select 
            value={filtroTipo} 
            onChange={(e) => setFiltroTipo(e.target.value)}
          >
            <option>Todos los disponibles</option>
            <option>Solo Bioequivalentes (ISP)</option>
            <option>Medicamentos Genéricos</option>
            <option>Medicamentos de Marca</option>
          </select>
        </div>
      </div>

      <div className="catalogo-resultados">
        {cargando ? (
           <div style={{ padding: '20px', textAlign: 'center', color: '#ca8a04', fontWeight: 'bold' }}>
             Cargando catálogo...
           </div>
        ) : medicamentosFiltrados.length === 0 ? (
           <div style={{ padding: '20px', textAlign: 'center', color: '#64748b' }}>
             No se encontraron medicamentos para esta búsqueda o filtro.
           </div>
        ) : (
          <>
            <table className="tabla-catalogo">
              <thead>
                <tr>
                  <th>Principio Activo</th>
                  <th>Nombre Comercial (Ejemplo)</th>
                  <th>Categoría</th>
                  <th>Sello Calidad</th>
                </tr>
              </thead>
              <tbody>
                {medicamentosPaginados.map((med) => (
                  <tr 
                    key={med.idMedicamento}
                    onClick={() => navigate(`/resultados?q=${encodeURIComponent(med.nombreCanonico)}`)}
                    style={{ cursor: 'pointer', transition: 'background 0.2s' }}
                    onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#fefce8'}
                    onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                  >
                    {/* AQUÍ ESTÁN APLICADOS LOS CAMBIOS DE FORMATO */}
                    <td><strong>{formatearTexto(med.principioActivo || med.nombreCanonico)}</strong></td>
                    <td>{formatearTexto(med.nombreCanonico)}</td>
                    <td>{formatearTexto(med.categoria) || 'Sin clasificar'}</td>
                    <td>
                      {med.esBioequivalente ? (
                        <span className="badge bio-gold">
                          <span className="icon-b">B</span> BIOEQUIVALENTE
                        </span>
                      ) : (
                        <span style={{color: '#94a3b8', fontSize: '0.85rem'}}>-</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            {totalPaginas > 1 && (
              <div className="paginacion-container">
                <button 
                  className="paginacion-btn"
                  onClick={() => {
                    setPaginaActual(prev => Math.max(prev - 1, 1));
                    window.scrollTo({ top: 400, behavior: 'smooth' });
                  }}
                  disabled={paginaActual === 1}
                >
                  ⬅ Anterior
                </button>
                <span className="paginacion-info">
                  Página <strong>{paginaActual}</strong> de <strong>{totalPaginas}</strong> (Total: {medicamentosFiltrados.length} medicamentos)
                </span>
                <button 
                  className="paginacion-btn"
                  onClick={() => {
                    setPaginaActual(prev => Math.min(prev + 1, totalPaginas));
                    window.scrollTo({ top: 400, behavior: 'smooth' });
                  }}
                  disabled={paginaActual === totalPaginas}
                >
                  Siguiente ➡
                </button>
              </div>
            )}
          </>
        )}
      </div>

    </div>
  );
};

export default Catalogo;