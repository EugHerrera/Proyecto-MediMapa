import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { apiUsuarios } from '../services/api';
import './AdminPanel.css';

const AdminPanel = () => {
  const navigate = useNavigate();
  const [rolUsuario, setRolUsuario] = useState('');
  const [cargando, setCargando] = useState(false);
  
  // --- Estados para Admin ---
  const [solicitudes, setSolicitudes] = useState<any[]>([]);
  const [sucursalesMaster, setSucursalesMaster] = useState<any[]>([]);
  const [medicamentosMaster, setMedicamentosMaster] = useState<any[]>([]);
  const [archivoIsp, setArchivoIsp] = useState<File | null>(null);

  useEffect(() => {
    const rolGuardado = localStorage.getItem('usuarioRol');
    if (!rolGuardado) {
      navigate('/login'); 
    } else {
      setRolUsuario(rolGuardado.toUpperCase());
    }
  }, [navigate]);

  const cargarDatosAdmin = async () => {
    try {
      const [respSoli, respSuc, respMed] = await Promise.all([
        apiUsuarios.get(`/usuarios/solicitudes/pendientes`),
        apiUsuarios.get(`/usuarios/farmacias-admin`),
        apiUsuarios.get(`/usuarios/medicamentos-admin`)
      ]);
      setSolicitudes(respSoli.data);
      setSucursalesMaster(respSuc.data);
      setMedicamentosMaster(respMed.data);
    } catch (error) {
      console.error("Error al cargar datos de admin:", error);
    }
  };

  useEffect(() => {
    if (rolUsuario === 'ADMIN') {
      cargarDatosAdmin();
    }
  }, [rolUsuario]);

  // Lógica de Medicamentos
  const eliminarMedicamentoMaster = async (id: number, nombre: string) => {
    if (!window.confirm(`¿Seguro que deseas eliminar "${nombre}" del catálogo global?`)) return;
    try {
      await apiUsuarios.delete(`/usuarios/medicamentos-admin/${id}`);
      cargarDatosAdmin();
    } catch (err) { alert("Error al eliminar medicamento."); }
  };

  const toggleBioequivalencia = async (med: any) => {
    try {
      const nuevoEstado = { ...med, es_bioequivalente: !med.es_bioequivalente };
      await apiUsuarios.put(`/usuarios/medicamentos-admin/${med.id_medicamento}`, nuevoEstado);
      cargarDatosAdmin();
    } catch (err) { alert("Error al actualizar sello."); }
  };

  const renderVistaAdmin = () => (
    <div className="admin-dashboard-grid" style={{ display: 'grid', gap: '20px' }}>
      
      {/* 1. SECCIÓN ISP Y CATÁLOGO (Control Total) */}
      <div className="admin-card" style={{ gridColumn: '1 / -1', borderLeft: '5px solid #ca8a04', backgroundColor: '#fefce8' }}>
        <h2 style={{ color: '#854d0e' }}>📘 Gestión del Catálogo Maestro</h2>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '30px' }}>
          <div>
            <p><strong>Certificación ISP (Masiva):</strong></p>
            <input type="file" accept=".xlsx" onChange={(e) => e.target.files && setArchivoIsp(e.target.files[0])} />
            <button className="btn-premium" onClick={() => {}} disabled={!archivoIsp || cargando} style={{ marginTop: '10px', width: '100%' }}>
              {cargando ? 'Procesando...' : 'Actualizar Sellos con Excel'}
            </button>
          </div>
          <div style={{ borderLeft: '1px solid #fde047', paddingLeft: '30px' }}>
            <p><strong>Estadísticas de Salud:</strong></p>
            <div style={{ fontSize: '1.2rem', color: '#ca8a04' }}>
              💊 {medicamentosMaster.length} fármacos registrados <br />
              🧬 {medicamentosMaster.filter(m => m.es_bioequivalente).length} bioequivalentes
            </div>
          </div>
        </div>
      </div>

      {/* 2. TABLA DE MEDICAMENTOS (CRUD) */}
      <div className="admin-card" style={{ gridColumn: '1 / -1' }}>
        <h2>🔍 Buscador y Edición de Fármacos</h2>
        <div style={{ maxHeight: '400px', overflowY: 'auto', border: '1px solid #e2e8f0', borderRadius: '8px' }}>
          <table className="admin-table">
            <thead style={{ position: 'sticky', top: 0, backgroundColor: 'white', zIndex: 10 }}>
              <tr>
                <th>Nombre Canónico</th>
                <th>Principio Activo</th>
                <th>Bioequivalente</th>
                <th>Acción</th>
              </tr>
            </thead>
            <tbody>
              {medicamentosMaster.map(med => (
                <tr key={med.id_medicamento}>
                  <td><strong>{med.nombre_canonico}</strong></td>
                  <td>{med.principio_activo}</td>
                  <td>
                    <button 
                      onClick={() => toggleBioequivalencia(med)}
                      style={{ 
                        background: med.es_bioequivalente ? '#fef08a' : '#f1f5f9', 
                        border: '1px solid #ca8a04',
                        borderRadius: '15px',
                        padding: '2px 10px',
                        fontSize: '0.7rem',
                        fontWeight: 'bold',
                        cursor: 'pointer'
                      }}
                    >
                      {med.es_bioequivalente ? '✅ SÍ' : '❌ NO'}
                    </button>
                  </td>
                  <td>
                    <button 
                      onClick={() => eliminarMedicamentoMaster(med.id_medicamento, med.nombre_canonico)}
                      style={{ background: 'none', border: 'none', cursor: 'pointer' }}
                    >🗑️</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* 3. SOLICITUDES Y SUCURSALES (Abajo) */}
      <div className="admin-card">
        <h2>📋 Solicitudes Pendientes ({solicitudes.length})</h2>
        <table className="admin-table">
          <thead><tr><th>Farmacia</th><th>Acción</th></tr></thead>
          <tbody>
            {solicitudes.map(sol => (
              <tr key={sol.id_solicitud}>
                <td><strong>{sol.nombre_fantasia}</strong></td>
                <td>
                  <button onClick={() => {}} style={{ color: 'green', border: 'none', background: 'none', fontWeight: 'bold' }}>✓</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="admin-card">
        <h2>📍 Sucursales en Mapa ({sucursalesMaster.length})</h2>
        <div style={{ maxHeight: '200px', overflowY: 'auto' }}>
          <table className="admin-table">
            <tbody>
              {sucursalesMaster.map(suc => (
                <tr key={suc.id_sucursal}>
                  <td>{suc.nombre_sucursal}</td>
                  <td><button onClick={() => {}} style={{ background: 'none', border: 'none' }}>🗑️</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

    </div>
  );

  return (
    <div className="admin-container" style={{ marginTop: '-40px', paddingBottom: '80px' }}>
      <header className="admin-banner">
        <h1>Centro de Comando Súper Admin</h1>
        <p>Control total del Catálogo Maestro y Red de Farmacias</p>
      </header>
      {renderVistaAdmin()}
    </div>
  );
};

export default AdminPanel;