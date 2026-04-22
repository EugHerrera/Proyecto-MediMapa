import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Catalogo.css'; 

interface MedicamentoResponseDTO {
  idMedicamento: number;
  nombreCanonico: string;
  principioActivo: string;
  categoria: string | null;
  esBioequivalente: boolean;
}

const Bioequivalentes: React.FC = () => {
  const navigate = useNavigate();
  const [busqueda, setBusqueda] = useState('');
  const [medicamentos, setMedicamentos] = useState<MedicamentoResponseDTO[]>([]);
  const [cargando, setCargando] = useState(false);

  // Llamada a tu backend en Spring Boot (Puerto 8081)
  useEffect(() => {
    const fetchMedicamentos = async () => {
      setCargando(true);
      try {
        let url = 'http://localhost:8081/api/medicamentos';

        if (busqueda.trim() !== '') {
          url = `http://localhost:8081/api/medicamentos/buscar?q=${busqueda}`;
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

    const timeoutId = setTimeout(() => {
      fetchMedicamentos();
    }, 300);

    return () => clearTimeout(timeoutId);

  }, [busqueda]);

  // Filtro forzado a Bioequivalentes
  const bioequivalentesFiltrados = medicamentos.filter((med) => med.esBioequivalente === true);

  return (
    <div className="catalogo-container">
      
      {/* BANNER ESPECÍFICO DE BIOEQUIVALENTES */}
      <div className="catalogo-banner" style={{ background: 'linear-gradient(90deg, #0f766e 0%, #059669 100%)' }}>
        <div className="banner-content">
          <div className="banner-title">
            <span className="banner-icon">🛡️</span>
            <h2>Alternativas Bioequivalentes (ISP)</h2>
          </div>
          <div className="banner-meta">
            <span>Solo medicamentos certificados por el Ministerio de Salud</span>
          </div>
        </div>
      </div>

      <div className="catalogo-search-section" style={{ marginTop: '2rem' }}>
        <div className="search-box">
          <label>Buscar bioequivalente</label>
          <div className="input-with-icon">
            <span className="search-icon">🔍</span>
            <input 
              type="text" 
              placeholder="Escribe el principio activo (ej. Paracetamol)..." 
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
            />
          </div>
        </div>
      </div>

      {/* TABLA DE RESULTADOS */}
      <div className="catalogo-resultados">
        {cargando ? (
           <div style={{ padding: '20px', textAlign: 'center', color: '#059669', fontWeight: 'bold' }}>
             Consultando registros del ISP...
           </div>
        ) : bioequivalentesFiltrados.length === 0 ? (
           <div style={{ padding: '40px', textAlign: 'center', color: '#64748b' }}>
             <h3>No encontramos resultados</h3>
             <p>No hay alternativas bioequivalentes registradas para esta búsqueda actual.</p>
           </div>
        ) : (
          <table className="tabla-catalogo">
            <thead>
              <tr>
                <th>Principio Activo</th>
                <th>Nombre Comercial</th>
                <th>Categoría</th>
                <th>Certificación</th>
              </tr>
            </thead>
            <tbody>
              {bioequivalentesFiltrados.map((med) => (
                <tr 
                  key={med.idMedicamento}
                  onClick={() => navigate(`/resultados?q=${encodeURIComponent(med.nombreCanonico)}`)}
                  style={{ cursor: 'pointer', transition: 'background 0.2s' }}
                  onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#f0fdf4'}
                  onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                >
                  <td><strong>{med.principioActivo || med.nombreCanonico}</strong></td>
                  <td>{med.nombreCanonico}</td>
                  <td>{med.categoria || 'Sin clasificar'}</td>
                  <td>
                    <span className="badge bio" style={{ backgroundColor: '#10b981', color: 'white', padding: '4px 8px', borderRadius: '4px', fontSize: '0.85rem' }}>
                      ✓ Certificado (ISP)
                    </span>
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

export default Bioequivalentes;