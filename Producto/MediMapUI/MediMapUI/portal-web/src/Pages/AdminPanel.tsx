import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { apiUsuarios } from '../services/api';
import './AdminPanel.css';

const AdminPanel = () => {
  const navigate = useNavigate();
  const [rolUsuario, setRolUsuario] = useState('');
  const [cargando, setCargando] = useState(false);
  
  // Estados para Farmacéutico
  const [inventario, setInventario] = useState<any[]>([]);
  const [totalMedicamentos, setTotalMedicamentos] = useState(0);
  const ID_SUCURSAL = 99;

  // 🔥 NUEVO ESTADO PARA EL ADMIN: Guardar las solicitudes
  const [solicitudes, setSolicitudes] = useState<any[]>([]);
  
  useEffect(() => {
    const rolGuardado = localStorage.getItem('usuarioRol');
    if (!rolGuardado) {
      navigate('/login'); 
    } else {
      setRolUsuario(rolGuardado.toUpperCase());
    }
  }, [navigate]);

  // 🔥 LÓGICA DE ADMIN: Traer las solicitudes pendientes
  const cargarSolicitudesPendientes = async () => {
    try {
      const token = localStorage.getItem('token');
      const respuesta = await apiUsuarios.get(`/usuarios/solicitudes/pendientes`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      setSolicitudes(respuesta.data); // Guardamos la lista real de la Base de Datos
    } catch (error) {
      console.error("Error al cargar solicitudes:", error);
    }
  };

  // Dependiendo del rol, cargamos una cosa u otra al entrar
  useEffect(() => {
    if (rolUsuario === 'FARMACEUTICO') {
      cargarInventarioReal(); 
    } else if (rolUsuario === 'ADMIN') {
      cargarSolicitudesPendientes();
    }
  }, [rolUsuario]);

  // 🔥 LÓGICA DE ADMIN: Botón Aprobar
  const manejarAprobacion = async (id: number) => {
    if (!window.confirm("¿Seguro que deseas APROBAR esta farmacia?")) return;
    try {
      const token = localStorage.getItem('token');
      const resp = await apiUsuarios.patch(`/usuarios/solicitudes/${id}/aprobar`, null, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      alert(resp.data);
      cargarSolicitudesPendientes(); // Recargamos la tabla para que desaparezca
    } catch (err) {
      alert("Error al aprobar la farmacia.");
    }
  };

  // 🔥 LÓGICA DE ADMIN: Botón Rechazar
  const manejarRechazo = async (id: number) => {
    if (!window.confirm("¿Seguro que deseas RECHAZAR esta solicitud?")) return;
    try {
      const token = localStorage.getItem('token');
      const resp = await apiUsuarios.patch(`/usuarios/solicitudes/${id}/rechazar`, null, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      alert(resp.data);
      cargarSolicitudesPendientes(); // Recargamos la tabla para que desaparezca
    } catch (err) {
      alert("Error al rechazar la farmacia.");
    }
  };

  // --- LÓGICA DE FARMACÉUTICO (Mantenida intacta) ---
  const cargarInventarioReal = async () => {
    try {
      const token = localStorage.getItem('token');
      const respuesta = await apiUsuarios.get(`/usuarios/inventario/listar/${ID_SUCURSAL}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      setInventario(respuesta.data); 
      setTotalMedicamentos(respuesta.data.length); 
    } catch (error: any) {
      console.error("Error al cargar el inventario real:", error);
    }
  };

  const [archivo, setArchivo] = useState<File | null>(null);
  const manejarSeleccionArchivo = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) setArchivo(e.target.files[0]);
  };

  const subirArchivoInventario = async () => {
    if (!archivo) return;
    setCargando(true);
    const formData = new FormData();
    formData.append('archivo', archivo);
    const token = localStorage.getItem('token'); 
    try {
      await apiUsuarios.post('/usuarios/inventario/subir', formData, {
        headers: { 'Content-Type': 'multipart/form-data', 'Authorization': `Bearer ${token}` }
      });
      alert("✅ Inventario cargado con éxito en la Base de Datos.");
      setArchivo(null);
      cargarInventarioReal();
    } catch (err) {
      alert("❌ Error: Verifica tu sesión o conexión.");
    } finally {
      setCargando(false);
    }
  };

  const guardarNuevoPrecio = async (nombreMedicamento: string, precioInputId: string) => {
    const inputElement = document.getElementById(precioInputId) as HTMLInputElement;
    const nuevoPrecio = inputElement?.value;
    if (!nuevoPrecio) return;
    try {
      const token = localStorage.getItem('token'); 
      await apiUsuarios.put(
        `/usuarios/inventario/actualizar-precio?idSucursal=${ID_SUCURSAL}&textoBusqueda=${encodeURIComponent(nombreMedicamento)}&nuevoPrecio=${nuevoPrecio}`, 
        null, { headers: { 'Authorization': `Bearer ${token}` } }
      );
      alert(`✅ Precio actualizado.`);
      cargarInventarioReal(); 
    } catch (error) {
      alert("❌ Error al guardar el precio.");
    }
  };

  // --- VISTAS ---
  const renderVistaAdmin = () => (
    <div className="admin-dashboard-grid">
      <div className="admin-card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h2>🤖 Estado del Motor Scraper</h2>
          <button style={{ backgroundColor: '#f59e0b', color: 'white', border: 'none', padding: '8px 15px', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>
            ⚡ Forzar Actualización
          </button>
        </div>
        <div style={{ display: 'flex', gap: '15px', marginTop: '20px' }}>
            <div style={{ padding: '15px', backgroundColor: '#f0fdf4', border: '1px solid #bbf7d0', borderRadius: '8px', flex: 1 }}>
                <strong style={{ color: '#166534' }}>Farmacias Ahumada</strong>
                <p style={{ margin: '5px 0 0 0', color: '#059669' }}>🟢 En línea</p>
            </div>
            <div style={{ padding: '15px', backgroundColor: '#f0fdf4', border: '1px solid #bbf7d0', borderRadius: '8px', flex: 1 }}>
                <strong style={{ color: '#166534' }}>Farmacias Dr. Simi</strong>
                <p style={{ margin: '5px 0 0 0', color: '#059669' }}>🟢 En línea</p>
            </div>
        </div>
      </div>

      <div className="admin-card">
        <h2>📋 Solicitudes Pendientes ({solicitudes.length})</h2>
        <table className="admin-table">
            <thead>
                <tr>
                  <th>Farmacia / Razón Social</th>
                  <th>SEREMI</th>
                  <th>Dirección</th>
                  <th>Acción</th>
                </tr>
            </thead>
            <tbody>
                {/* 🔥 DIBUJAMOS LA TABLA DINÁMICAMENTE */}
                {solicitudes.length === 0 ? (
                  <tr><td colSpan={4} style={{ textAlign: 'center', color: '#64748b' }}>No hay solicitudes pendientes en este momento.</td></tr>
                ) : (
                  solicitudes.map((sol) => (
                    <tr key={sol.id_solicitud}>
                        <td>
                          <strong>{sol.nombre_fantasia}</strong><br/>
                          <small style={{ color: '#64748b' }}>{sol.razon_social} (RUT: {sol.rut_empresa})</small>
                        </td>
                        <td><span style={{ backgroundColor: '#fef3c7', color: '#92400e', padding: '3px 8px', borderRadius: '4px', fontSize: '0.8rem' }}>{sol.resolucion_seremi}</span></td>
                        <td>{sol.direccion}, {sol.comuna}</td>
                        <td style={{ display: 'flex', gap: '5px' }}>
                            <button onClick={() => manejarAprobacion(sol.id_solicitud)} style={{ backgroundColor: '#10b981', color: 'white', padding: '6px 12px', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
                              ✓ Aprobar
                            </button>
                            <button onClick={() => manejarRechazo(sol.id_solicitud)} style={{ backgroundColor: '#ef4444', color: 'white', padding: '6px 12px', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
                              ✕ Rechazar
                            </button>
                        </td>
                    </tr>
                  ))
                )}
            </tbody>
        </table>
      </div>
    </div>
  );

  const renderVistaFarmaceutico = () => (
    <>
      <div style={{ display: 'flex', gap: '20px', marginBottom: '20px' }}>
        <div style={{ backgroundColor: '#eff6ff', border: '1px solid #bfdbfe', padding: '20px', borderRadius: '12px', flex: 1, display: 'flex', alignItems: 'center', gap: '15px' }}>
          <div style={{ fontSize: '2rem' }}>📦</div>
          <div>
            <h3 style={{ margin: 0, color: '#1e3a8a', fontSize: '1rem' }}>Medicamentos en Catálogo</h3>
            <p style={{ margin: 0, color: '#2563eb', fontSize: '1.5rem', fontWeight: 'bold' }}>{totalMedicamentos} activos</p>
          </div>
        </div>
      </div>

      <div className="admin-card">
        <h2>📥 Actualización Masiva de Inventario</h2>
        <div className="dropzone">
          <span className="dropzone-icon">📄</span>
          <div className="dropzone-text">{archivo ? `Archivo: ${archivo.name}` : 'Sube tu Excel/CSV aquí'}</div>
          <input type="file" accept=".csv, .xlsx" onChange={manejarSeleccionArchivo} />
        </div>
        <button className="btn-upload" onClick={subirArchivoInventario} disabled={!archivo || cargando}>
          {cargando ? 'Procesando...' : 'Procesar y Actualizar Precios'}
        </button>
      </div>

      <div className="admin-card">
        <h2>✏️ Ajuste Manual Rápido</h2>
        <table className="admin-table">
          <thead>
            <tr><th>Medicamento</th><th>Precio Vigente (CLP)</th><th>Acción</th></tr>
          </thead>
          <tbody>
            {inventario.map((item, index) => {
              const inputId = `precio-input-${index}`;
              return (
                <tr key={item.id}>
                  <td style={{ textTransform: 'capitalize' }}>{item.nombre}</td>
                  <td>$ <input id={inputId} type="number" className="input-precio" defaultValue={item.precio} /></td>
                  <td>
                    <button className="btn-guardar" onClick={() => guardarNuevoPrecio(item.nombre, inputId)}>
                      Guardar
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </>
  );

  return (
    <div className="admin-container" style={{ marginTop: '-80px', paddingBottom: '40px' }}>
      <div style={{ marginBottom: '20px' }}>
        <Link to="/" style={{ color: '#0ea5e9', textDecoration: 'none', fontWeight: 'bold' }}>⬅ Volver al portal</Link>
      </div>
      <div className="admin-header">
        <h1>{rolUsuario === 'ADMIN' ? 'Centro de Comando Súper Admin' : 'Panel de Gestión Farmacéutica'}</h1>
        <p>{rolUsuario === 'ADMIN' ? 'Monitoreo de sistema y aprobación de locales' : 'Actualiza tus precios y stock para La Florida'}</p>
      </div>
      {rolUsuario === 'ADMIN' && renderVistaAdmin()}
      {rolUsuario === 'FARMACEUTICO' && renderVistaFarmaceutico()}
    </div>
  );
};

export default AdminPanel;