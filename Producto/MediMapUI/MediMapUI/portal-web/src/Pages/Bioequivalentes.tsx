import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
// 🔥 IMPORTAMOS AXIOS DESDE NUESTRO ARCHIVO CENTRAL
import { apiUsuarios } from '../services/api';
import './Catalogo.css'; 

import SelloBioequivalente from '../assets/sello-bioequivalente.svg';

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

  useEffect(() => {
    const fetchMedicamentos = async () => {
      setCargando(true);
      try {
        let url = '/medicamentos';
        if (busqueda.trim() !== '') {
          url = `/medicamentos/buscar?q=${busqueda}`;
        }

        // AXIOS
        const respuesta = await apiUsuarios.get(url);
        setMedicamentos(respuesta.data);
      } catch (error) {
        console.error("Error de conexión con el catálogo:", error);
      } finally {
        setCargando(false);
      }
    };

    const timeoutId = setTimeout(fetchMedicamentos, 300);
    return () => clearTimeout(timeoutId);
  }, [busqueda]);

  const bioequivalentesFiltrados = medicamentos.filter((med) => med.esBioequivalente);

  return (
    <div className="catalogo-container">
      
      <div className="catalogo-banner" style={{ background: 'linear-gradient(90deg, #ca8a04 0%, #059669 100%)' }}>
        <div className="banner-content">
          <div className="banner-title" style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
            <img src={SelloBioequivalente} alt="Sello Bio" style={{ height: '60px', width: 'auto' }} />
            <div>
              <h2 style={{ margin: 0 }}>Alternativas Bioequivalentes</h2>
              <p style={{ margin: 0, opacity: 0.9, fontSize: '0.9rem' }}>Certificación oficial del Instituto de Salud Pública (ISP)</p>
            </div>
          </div>
        </div>
      </div>

      <div className="catalogo-disclaimer" style={{ borderLeftColor: '#ca8a04', backgroundColor: '#fefce8' }}>
        <div className="disclaimer-icon">💡</div>
        <div className="disclaimer-text">
          <h4 style={{ color: '#854d0e', margin: '0 0 5px 0' }}>¿Por qué elegir un Bioequivalente?</h4>
          <p style={{ margin: 0, fontSize: '0.9rem', color: '#713f12' }}>
            Son fármacos que contienen el mismo principio activo y han demostrado científicamente tener la 
            <strong> misma eficacia y seguridad</strong> que el medicamento de marca, pero a un precio 
            considerablemente menor.
          </p>
        </div>
      </div>

      <div className="catalogo-search-section" style={{ marginTop: '2rem' }}>
        <div className="search-box">
          <label style={{ color: '#ca8a04' }}>Buscar por Principio Activo</label>
          <div className="input-with-icon">
            <span className="search-icon">🔍</span>
            <input 
              type="text" 
              placeholder="Ej: Paracetamol, Losartán, Atorvastatina..." 
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              style={{ borderColor: '#fde047' }}
            />
          </div>
        </div>
      </div>

      <div className="catalogo-resultados">
        {cargando ? (
           <div style={{ padding: '40px', textAlign: 'center', color: '#ca8a04' }}>Consultando registros...</div>
        ) : bioequivalentesFiltrados.length === 0 ? (
           <div style={{ padding: '40px', textAlign: 'center', color: '#64748b' }}>
             <h3>No se encontraron bioequivalentes</h3>
             <p>Intenta buscar por el componente principal del medicamento.</p>
           </div>
        ) : (
          <table className="tabla-catalogo">
            <thead>
              <tr>
                <th>Principio Activo</th>
                <th>Nombre Comercial</th>
                <th>Categoría</th>
                <th>Sello Calidad</th>
              </tr>
            </thead>
            <tbody>
              {bioequivalentesFiltrados.map((med) => (
                <tr 
                  key={med.idMedicamento}
                  onClick={() => navigate(`/resultados?q=${encodeURIComponent(med.nombreCanonico)}`)}
                  style={{ cursor: 'pointer' }}
                >
                  <td><strong>{med.principioActivo || med.nombreCanonico}</strong></td>
                  <td>{med.nombreCanonico}</td>
                  <td>{med.categoria || 'General'}</td>
                  <td>
                    <div style={{ 
                      display: 'inline-flex', 
                      alignItems: 'center', 
                      backgroundColor: '#fef08a', 
                      padding: '4px 12px', 
                      borderRadius: '20px', 
                      fontSize: '0.75rem', 
                      fontWeight: 'bold',
                      color: '#854d0e',
                      border: '1px solid #ca8a04'
                    }}>
                      <img src={SelloBioequivalente} alt="B" style={{ height: '14px', marginRight: '6px' }} />
                      BIOEQUIVALENTE
                    </div>
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