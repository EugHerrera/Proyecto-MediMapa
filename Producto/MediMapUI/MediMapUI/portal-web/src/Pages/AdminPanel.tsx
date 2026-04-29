import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { apiUsuarios } from '../services/api';
import './AdminPanel.css';

const AdminPanel = () => {
  const navigate = useNavigate();
  const [rolUsuario, setRolUsuario] = useState('');
  const [cargando, setCargando] = useState(false);
  
  // --- Estados para Farmacéutico ---
  const [inventario, setInventario] = useState<any[]>([]);
  const [totalMedicamentos, setTotalMedicamentos] = useState(0);
  const ID_SUCURSAL = 99;

  // --- Estados para Admin ---
  const [solicitudes, setSolicitudes] = useState<any[]>([]);
  const [sucursalesMaster, setSucursalesMaster] = useState<any[]>([]);
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
      const [respSoli, respSuc] = await Promise.all([
        apiUsuarios.get(`/usuarios/solicitudes/pendientes`),
        apiUsuarios.get(`/usuarios/farmacias-admin`)
      ]);
      setSolicitudes(respSoli.data);
      setSucursalesMaster(respSuc.data);
    } catch (error) {
      console.error("Error al cargar datos de admin:", error);
    }
  };

  useEffect(() => {
    if (rolUsuario === 'FARMACEUTICO') {
      cargarInventarioReal(); 
    } else if (rolUsuario === 'ADMIN') {
      cargarDatosAdmin();
    }
  }, [rolUsuario]);

  // Lógica de Solicitudes
  const manejarAprobacion = async (id: number) => {
    if (!window.confirm("¿Seguro que deseas APROBAR esta farmacia?")) return;
    try {
      await apiUsuarios.patch(`/usuarios/solicitudes/${id}/aprobar`);
      alert("✅ Farmacia aprobada y cuenta de usuario creada.");
      cargarDatosAdmin(); 
    } catch (err) { alert("Error al aprobar."); }
  };

  const manejarRechazo = async (id: number) => {
    if (!window.confirm("¿Seguro que deseas RECHAZAR esta solicitud?")) return;
    try {
      await apiUsuarios.patch(`/usuarios/solicitudes/${id}/rechazar`);
      cargarDatosAdmin(); 
    } catch (err) { alert("Error al rechazar."); }
  };

  // CRUD Maestro de Sucursales (La "Futura Implementación")
  const eliminarSucursalMaster = async (id: number) => {
    if (!window.confirm("¿Eliminar esta sucursal del sistema permanentemente?")) return;
    try {
      await apiUsuarios.delete(`/usuarios/farmacias-admin/${id}`);
      cargarDatosAdmin();
    } catch (err) { alert("Error al eliminar sucursal."); }
  };

  // Lógica ISP
  const subirExcelIsp = async () => {
    if (!archivoIsp) return;
    setCargando(true);
    const formData = new FormData();
    formData.append('archivo', archivoIsp);
    try {
      const respuesta = await apiUsuarios.post('/usuarios/admin/subir-isp', formData);
      alert(respuesta.data); 
      setArchivoIsp(null);
    } catch (err) { alert("Error al procesar ISP."); }
    finally { setCargando(false); }
  };

  // Lógica Farmacéutico (Resumida para brevedad)
  const cargarInventarioReal = async () => {
    const res = await apiUsuarios.get(`/usuarios/inventario/listar/${ID_SUCURSAL}`);
    setInventario(res.data);
    setTotalMedicamentos(res.data.length);
  };

  const renderVistaAdmin = () => (
    <div className="admin-dashboard-grid">
      {/* SECCIÓN ISP */}
      <div className="admin-card" style={{ gridColumn: '1 / -1', borderLeft: '5px solid #ca8a04', backgroundColor: '#fefce8' }}>
        <h2>📜 Certificación Bioequivalentes (ISP)</h2>
        <div className="dropzone" style={{ marginBottom: '20px' }}>
          <input type="file" accept=".xlsx" onChange={(e) => e.target.files && setArchivoIsp(e.target.files[0])} />
          <div className="dropzone-text">{archivoIsp ? archivoIsp.name : 'Subir Excel ISP'}</div>
        </div>
        <button className="btn-premium" onClick={subirExcelIsp} disabled={!archivoIsp || cargando} style={{ width: '100%' }}>
          {cargando ? 'Certificando...' : 'Certificar Catálogo Maestro'}
        </button>
      </div>

      {/* SOLICITUDES PENDIENTES */}
      <div className="admin-card">
        <h2>📋 Solicitudes de Inscripción ({solicitudes.length})</h2>
        <table className="admin-table">
          <thead><tr><th>Farmacia</th><th>Estado</th><th>Acción</th></tr></thead>
          <tbody>
            {solicitudes.map(sol => (
              <tr key={sol.id_solicitud}>
                <td><strong>{sol.nombre_fantasia}</strong><br/><small>{sol.comuna}</small></td>
                <td><span className="badge" style={{background: '#fefce8', color: '#ca8a04'}}>{sol.estado_solicitud}</span></td>
                <td>
                  <button onClick={() => manejarAprobacion(sol.id_solicitud)} style={{color: 'green', border: 'none', background: 'none', cursor: 'pointer', fontWeight: 'bold'}}>✓</button>
                  <button onClick={() => manejarRechazo(sol.id_solicitud)} style={{color: 'red', border: 'none', background: 'none', cursor: 'pointer', fontWeight: 'bold', marginLeft: '10px'}}>✕</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* GESTIÓN DE SUCURSALES (CRUD MAESTRO - OCULTO) */}
      <div className="admin-card">
        <h2>📍 Sucursales Activas en Mapa ({sucursalesMaster.length})</h2>
        <div style={{maxHeight: '300px', overflowY: 'auto'}}>
          <table className="admin-table">
            <thead><tr><th>Nombre</th><th>Ubicación</th><th>Acción</th></tr></thead>
            <tbody>
              {sucursalesMaster.map(suc => (
                <tr key={suc.id_sucursal}>
                  <td>{suc.nombre_sucursal}</td>
                  <td><small>{suc.latitud}, {suc.longitud}</small></td>
                  <td>
                    <button onClick={() => eliminarSucursalMaster(suc.id_sucursal)} style={{background: 'none', border: 'none', cursor: 'pointer'}}>🗑️</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <button className="btn-premium" style={{marginTop: '20px', width: '100%', background: '#64748b'}}>
          + Agregar Sucursal Manualmente (Módulo Futuro)
        </button>
      </div>
    </div>
  );

  return (
    <div className="admin-container" style={{ marginTop: '-40px', paddingBottom: '80px' }}>
      <header className="admin-banner">
        <h1>{rolUsuario === 'ADMIN' ? 'Centro de Comando Súper Admin' : 'Panel de Gestión Farmacéutica'}</h1>
        <p>{rolUsuario === 'ADMIN' ? 'Monitoreo de sistema y aprobación de locales' : 'Actualiza tus precios y stock en tiempo real'}</p>
      </header>
      {rolUsuario === 'ADMIN' ? renderVistaAdmin() : <div className="admin-card">Cargando panel farmacéutico...</div>}
    </div>
  );
};

export default AdminPanel;