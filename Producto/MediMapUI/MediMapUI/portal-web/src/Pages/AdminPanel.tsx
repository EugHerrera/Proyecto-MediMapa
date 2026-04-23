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

  // 🔥 NUEVOS ESTADOS: Para agregar un medicamento manual
  const [nuevoNombre, setNuevoNombre] = useState('');
  const [nuevoLaboratorio, setNuevoLaboratorio] = useState('');
  const [nuevoPrecio, setNuevoPrecio] = useState('');

  // --- Estados para Admin ---
  const [solicitudes, setSolicitudes] = useState<any[]>([]);
  
  useEffect(() => {
    const rolGuardado = localStorage.getItem('usuarioRol');
    if (!rolGuardado) {
      navigate('/login'); 
    } else {
      setRolUsuario(rolGuardado.toUpperCase());
    }
  }, [navigate]);

  const cargarSolicitudesPendientes = async () => {
    try {
      // Sin headers manuales, el interceptor lo hace
      const respuesta = await apiUsuarios.get(`/usuarios/solicitudes/pendientes`);
      setSolicitudes(respuesta.data); 
    } catch (error) {
      console.error("Error al cargar solicitudes:", error);
    }
  };

  useEffect(() => {
    if (rolUsuario === 'FARMACEUTICO') {
      cargarInventarioReal(); 
    } else if (rolUsuario === 'ADMIN') {
      cargarSolicitudesPendientes();
    }
  }, [rolUsuario]);

  const manejarAprobacion = async (id: number) => {
    if (!window.confirm("¿Seguro que deseas APROBAR esta farmacia?")) return;
    try {
      const resp = await apiUsuarios.patch(`/usuarios/solicitudes/${id}/aprobar`);
      alert(resp.data);
      cargarSolicitudesPendientes(); 
    } catch (err) {
      alert("Error al aprobar la farmacia.");
    }
  };

  const manejarRechazo = async (id: number) => {
    if (!window.confirm("¿Seguro que deseas RECHAZAR esta solicitud?")) return;
    try {
      const resp = await apiUsuarios.patch(`/usuarios/solicitudes/${id}/rechazar`);
      alert(resp.data);
      cargarSolicitudesPendientes(); 
    } catch (err) {
      alert("Error al rechazar la farmacia.");
    }
  };

  // ==========================================
  // 🔥 LÓGICA DE FARMACÉUTICO (CRUD LIMPIO)
  // ==========================================
  
  const cargarInventarioReal = async () => {
    try {
      const respuesta = await apiUsuarios.get(`/usuarios/inventario/listar/${ID_SUCURSAL}`);
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
    try {
      await apiUsuarios.post('/usuarios/inventario/subir', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      alert("✅ Inventario cargado con éxito.");
      setArchivo(null);
      cargarInventarioReal();
    } catch (err) {
      alert("❌ Error: Verifica tu sesión o conexión.");
    } finally {
      setCargando(false);
    }
  };

  // 🔥 ACTUALIZAR PRECIO (U del CRUD)
  const guardarNuevoPrecio = async (nombreMedicamento: string, precioInputId: string) => {
    const inputElement = document.getElementById(precioInputId) as HTMLInputElement;
    const nuevoPrecio = inputElement?.value;
    if (!nuevoPrecio) return;
    try {
      await apiUsuarios.put(
        `/usuarios/inventario/actualizar-precio?idSucursal=${ID_SUCURSAL}&textoBusqueda=${encodeURIComponent(nombreMedicamento)}&nuevoPrecio=${nuevoPrecio}`
      );
      alert(`✅ Precio actualizado.`);
      cargarInventarioReal(); 
    } catch (error) {
      alert("❌ Error al guardar el precio.");
    }
  };

  // 🔥 ELIMINAR MEDICAMENTO (D del CRUD)
  const eliminarMedicamento = async (nombreMedicamento: string) => {
    if (!window.confirm(`¿Estás seguro de eliminar "${nombreMedicamento}" de tu inventario?`)) return;
    try {
      await apiUsuarios.delete(
        `/usuarios/inventario/eliminar?idSucursal=${ID_SUCURSAL}&nombreMedicamento=${encodeURIComponent(nombreMedicamento.toLowerCase())}`
      );
      alert(`✅ Medicamento eliminado.`);
      cargarInventarioReal(); 
    } catch (error) {
      alert("❌ Error al eliminar el medicamento.");
    }
  };

  // 🔥 AGREGAR MEDICAMENTO MANUAL (C del CRUD)
  const agregarMedicamentoManual = async () => {
    if (!nuevoNombre || !nuevoPrecio || !nuevoLaboratorio) {
      alert("⚠️ Por favor completa el Nombre, Laboratorio y Precio.");
      return;
    }
    setCargando(true);
    try {
      await apiUsuarios.post(
        `/usuarios/inventario/agregar-manual`,
        {
          idSucursal: ID_SUCURSAL,
          nombre: nuevoNombre,
          laboratorio: nuevoLaboratorio,
          precio: Number(nuevoPrecio)
        }
      );
      
      alert("✅ Medicamento agregado exitosamente.");
      
      setNuevoNombre('');
      setNuevoLaboratorio('');
      setNuevoPrecio('');
      
      cargarInventarioReal(); 
    } catch (error: any) {
      const serverMsg = error?.response?.data || error?.message || error;
      alert(`❌ Error al agregar el medicamento en el servidor: ${serverMsg}`);
      console.error('Error agregarMedicamentoManual:', error);
    } finally {
      setCargando(false);
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
        <h2>Actualización Masiva de Inventario</h2>
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
        <h2>Ajuste Manual Rápido</h2>
        
        {/* 🔥 FORMULARIO PARA AGREGAR MEDICAMENTO NUEVO */}
        <div style={{ backgroundColor: '#f8fafc', padding: '20px', borderRadius: '8px', marginBottom: '25px', border: '1px solid #e2e8f0' }}>
          <h3 style={{ margin: '0 0 15px 0', color: '#0f172a', fontSize: '1.1rem' }}>➕ Ingresar Nuevo Medicamento</h3>
          <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
            <input
              type="text"
              placeholder="Ej. Paracetamol 500mg"
              value={nuevoNombre}
              onChange={(e) => setNuevoNombre(e.target.value)}
              style={{ flex: 2, padding: '10px', borderRadius: '6px', border: '1px solid #cbd5e1' }}
            />
            <input
              type="text"
              placeholder="Laboratorio (Ej. Andrómaco)"
              value={nuevoLaboratorio}
              onChange={(e) => setNuevoLaboratorio(e.target.value)}
              style={{ flex: 1.5, padding: '10px', borderRadius: '6px', border: '1px solid #cbd5e1' }}
            />
            <input
              type="number"
              placeholder="Precio ($)"
              value={nuevoPrecio}
              onChange={(e) => setNuevoPrecio(e.target.value)}
              style={{ flex: 1, padding: '10px', borderRadius: '6px', border: '1px solid #cbd5e1' }}
            />
            <button
              onClick={agregarMedicamentoManual}
              disabled={cargando}
              style={{ backgroundColor: '#0ea5e9', color: 'white', border: 'none', padding: '10px 20px', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}
            >
              {cargando ? 'Guardando...' : 'Guardar'}
            </button>
          </div>
        </div>

        {/* 🔥 LA TABLA CON LAS ACCIONES DE ACTUALIZAR Y ELIMINAR */}
        <table className="admin-table">
          <thead>
            <tr>
              <th>Medicamento</th>
              <th>Laboratorio / Marca</th>
              <th>Precio Vigente (CLP)</th>
              <th>Acción</th>
            </tr>
          </thead>
          <tbody>
            {inventario.map((item, index) => {
              const inputId = `precio-input-${index}`;
              return (
                <tr key={item.id || index}>
                  <td style={{ textTransform: 'capitalize' }}>
                    <strong>{item.nombre}</strong>
                  </td>
                  <td style={{ color: '#64748b', fontSize: '0.9rem' }}>
                    {item.laboratorio ? item.laboratorio : 'No especificado'}
                  </td>
                  <td>$ <input id={inputId} type="number" className="input-precio" defaultValue={item.precio} style={{ width: '100px' }} /></td>
                  <td style={{ display: 'flex', gap: '10px' }}>
                    <button className="btn-guardar" onClick={() => guardarNuevoPrecio(item.nombre, inputId)}>
                      Actualizar
                    </button>
                    <button 
                      style={{ backgroundColor: '#ef4444', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', padding: '0 10px', fontWeight: 'bold' }} 
                      onClick={() => eliminarMedicamento(item.nombre)}
                      title="Eliminar Medicamento"
                    >
                      🗑️
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