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

  // --- Estados para agregar un medicamento manual ---
  const [nuevoNombre, setNuevoNombre] = useState('');
  const [nuevoLaboratorio, setNuevoLaboratorio] = useState('');
  const [nuevoPrecio, setNuevoPrecio] = useState('');

  // --- Estados para Admin ---
  const [solicitudes, setSolicitudes] = useState<any[]>([]);
  
  // 🔥 ESTADOS RESCATADOS: Para el Excel del ISP 🔥
  const [archivoIsp, setArchivoIsp] = useState<File | null>(null);

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
  // 🔥 LÓGICA DE SÚPER ADMIN (ISP) RESCATADA 🔥
  // ==========================================
  const manejarSeleccionIsp = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) setArchivoIsp(e.target.files[0]);
  };

  const subirExcelIsp = async () => {
    if (!archivoIsp) return;
    setCargando(true);
    const formData = new FormData();
    formData.append('archivo', archivoIsp);
    
    try {
      const respuesta = await apiUsuarios.post('/usuarios/admin/subir-isp', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      alert(respuesta.data); 
      setArchivoIsp(null);
    } catch (err: any) {
      const serverMsg = err?.response?.data || err?.message || "Error desconocido";
      alert(`❌ Error al subir Excel ISP: ${serverMsg}`);
    } finally {
      setCargando(false);
    }
  };

  // ==========================================
  // LÓGICA DE FARMACÉUTICO (CRUD LIMPIO)
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
    } finally {
      setCargando(false);
    }
  };

  // --- VISTAS ---
  const renderVistaAdmin = () => (
    <div className="admin-dashboard-grid">

      {/* 🔥 LA CAJA DORADA DEL ISP RECARGADA CON EL ESTILO NUEVO 🔥 */}
      <div className="admin-card" style={{ gridColumn: '1 / -1', borderLeft: '5px solid #ca8a04', backgroundColor: '#fefce8' }}>
        <h2 style={{ color: '#854d0e', marginTop: 0, borderBottom: '2px solid #fde047', paddingBottom: '10px' }}>
          📜 Certificación Bioequivalentes (ISP)
        </h2>
        <p style={{ color: '#713f12', fontSize: '0.95rem', marginBottom: '20px' }}>
          Sube el Excel oficial del Instituto de Salud Pública para marcar automáticamente los medicamentos con el sello de bioequivalencia.
        </p>
        <div className="dropzone" style={{ borderColor: '#fde047', backgroundColor: '#fffbeb', marginBottom: '20px' }}>
          <span className="dropzone-icon">📊</span>
          <div className="dropzone-text" style={{ color: '#854d0e' }}>
            {archivoIsp ? `Listo para procesar: ${archivoIsp.name}` : 'Seleccionar Excel del ISP (.xlsx)'}
          </div>
          <div className="dropzone-hint" style={{ color: '#a16207' }}>Haz clic o arrastra el archivo purificado.</div>
          <input type="file" accept=".xlsx, .xls" onChange={manejarSeleccionIsp} />
        </div>
        <button 
          className="btn-premium" 
          onClick={subirExcelIsp} 
          disabled={!archivoIsp || cargando}
          style={{ width: '100%', fontSize: '1.1rem' }}
        >
          {cargando ? 'Analizando filas en el servidor...' : 'Certificar Catálogo'}
        </button>
      </div>

      <div className="admin-card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h2>🤖 Estado del Motor Scraper</h2>
          <button className="btn-premium" style={{ margin: 0, padding: '8px 15px' }}>
            ⚡ Forzar Actualización
          </button>
        </div>
        <div style={{ display: 'flex', gap: '15px', marginTop: '20px' }}>
            <div style={{ padding: '15px', backgroundColor: '#f0fdf4', border: '1px solid #bbf7d0', borderRadius: '8px', flex: 1 }}>
                <strong style={{ color: '#166534' }}>Farmacias Ahumada</strong>
                <p style={{ margin: '5px 0 0 0', color: '#059669', fontWeight: 'bold' }}>🟢 En línea</p>
            </div>
            <div style={{ padding: '15px', backgroundColor: '#f0fdf4', border: '1px solid #bbf7d0', borderRadius: '8px', flex: 1 }}>
                <strong style={{ color: '#166534' }}>Farmacias Dr. Simi</strong>
                <p style={{ margin: '5px 0 0 0', color: '#059669', fontWeight: 'bold' }}>🟢 En línea</p>
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
                          <strong style={{color: '#0f172a'}}>{sol.nombre_fantasia}</strong><br/>
                          <small style={{ color: '#64748b' }}>{sol.razon_social} (RUT: {sol.rut_empresa})</small>
                        </td>
                        <td><span style={{ backgroundColor: '#fefce8', color: '#ca8a04', padding: '4px 10px', borderRadius: '6px', fontSize: '0.85rem', fontWeight: 'bold', border: '1px solid #fef08a' }}>{sol.resolucion_seremi}</span></td>
                        <td>{sol.direccion}, {sol.comuna}</td>
                        <td style={{ display: 'flex', gap: '8px' }}>
                            <button onClick={() => manejarAprobacion(sol.id_solicitud)} style={{ backgroundColor: '#059669', color: 'white', padding: '8px 14px', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', transition: 'opacity 0.2s' }} onMouseEnter={e => e.currentTarget.style.opacity = '0.8'} onMouseLeave={e => e.currentTarget.style.opacity = '1'}>
                              ✓ Aprobar
                            </button>
                            <button onClick={() => manejarRechazo(sol.id_solicitud)} style={{ backgroundColor: '#ef4444', color: 'white', padding: '8px 14px', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', transition: 'opacity 0.2s' }} onMouseEnter={e => e.currentTarget.style.opacity = '0.8'} onMouseLeave={e => e.currentTarget.style.opacity = '1'}>
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
        <div style={{ backgroundColor: '#fefce8', border: '1px solid #fef08a', padding: '25px', borderRadius: '12px', flex: 1, display: 'flex', alignItems: 'center', gap: '20px', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)' }}>
          <div style={{ fontSize: '3rem', backgroundColor: 'white', padding: '10px', borderRadius: '50%', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>📦</div>
          <div>
            <h3 style={{ margin: '0 0 5px 0', color: '#854d0e', fontSize: '1.1rem' }}>Medicamentos en Catálogo</h3>
            <p style={{ margin: 0, color: '#ca8a04', fontSize: '2rem', fontWeight: '900' }}>{totalMedicamentos} <span style={{fontSize: '1rem', fontWeight: 'normal'}}>activos</span></p>
          </div>
        </div>
      </div>

      <div className="admin-card">
        <h2>📄 Actualización Masiva de Inventario</h2>
        <div className="dropzone">
          <span className="dropzone-icon">📊</span>
          <div className="dropzone-text">{archivo ? `Archivo listo: ${archivo.name}` : 'Sube tu archivo Excel o CSV aquí'}</div>
          <div className="dropzone-hint">Haz clic o arrastra el archivo para actualizar tus precios en bloque.</div>
          <input type="file" accept=".csv, .xlsx" onChange={manejarSeleccionArchivo} />
        </div>
        <button className="btn-premium" onClick={subirArchivoInventario} disabled={!archivo || cargando}>
          {cargando ? 'Procesando archivo...' : 'Procesar y Actualizar Precios'}
        </button>
      </div>

      <div className="admin-card">
        <h2>⚡ Ajuste Manual Rápido</h2>
        
        <div style={{ backgroundColor: '#f8fafc', padding: '25px', borderRadius: '12px', marginBottom: '30px', border: '1px solid #e2e8f0' }}>
          <h3 style={{ margin: '0 0 15px 0', color: '#0f172a', fontSize: '1.1rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{color: '#ca8a04'}}>➕</span> Ingresar Nuevo Medicamento
          </h3>
          <div style={{ display: 'flex', gap: '15px', flexWrap: 'wrap' }}>
            <input
              type="text"
              placeholder="Ej. Paracetamol 500mg"
              value={nuevoNombre}
              onChange={(e) => setNuevoNombre(e.target.value)}
              style={{ flex: 2, padding: '12px', borderRadius: '8px', border: '1px solid #cbd5e1', outline: 'none' }}
              onFocus={e => e.target.style.borderColor = '#ca8a04'}
              onBlur={e => e.target.style.borderColor = '#cbd5e1'}
            />
            <input
              type="text"
              placeholder="Laboratorio (Ej. Andrómaco)"
              value={nuevoLaboratorio}
              onChange={(e) => setNuevoLaboratorio(e.target.value)}
              style={{ flex: 1.5, padding: '12px', borderRadius: '8px', border: '1px solid #cbd5e1', outline: 'none' }}
              onFocus={e => e.target.style.borderColor = '#ca8a04'}
              onBlur={e => e.target.style.borderColor = '#cbd5e1'}
            />
            <input
              type="number"
              placeholder="Precio ($)"
              value={nuevoPrecio}
              onChange={(e) => setNuevoPrecio(e.target.value)}
              style={{ flex: 1, padding: '12px', borderRadius: '8px', border: '1px solid #cbd5e1', outline: 'none' }}
              onFocus={e => e.target.style.borderColor = '#ca8a04'}
              onBlur={e => e.target.style.borderColor = '#cbd5e1'}
            />
            <button
              onClick={agregarMedicamentoManual}
              disabled={cargando}
              className="btn-premium"
              style={{ margin: 0 }}
            >
              {cargando ? 'Guardando...' : 'Guardar'}
            </button>
          </div>
        </div>

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
                    <strong style={{color: '#0f172a', fontSize: '1.05rem'}}>{item.nombre}</strong>
                  </td>
                  <td style={{ color: '#64748b', fontSize: '0.95rem' }}>
                    {item.laboratorio ? item.laboratorio : 'No especificado'}
                  </td>
                  <td style={{display: 'flex', alignItems: 'center', gap: '5px', height: '100%', paddingTop: '18px'}}>
                    <span style={{color: '#059669', fontWeight: 'bold'}}>$</span>
                    <input id={inputId} type="number" className="input-precio" defaultValue={item.precio} />
                  </td>
                  <td>
                    <div style={{ display: 'flex', gap: '10px' }}>
                      <button className="btn-guardar" onClick={() => guardarNuevoPrecio(item.nombre, inputId)}>
                        Actualizar
                      </button>
                      <button 
                        style={{ backgroundColor: '#ef4444', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', padding: '0 12px', fontSize: '1.2rem', transition: 'opacity 0.2s' }} 
                        onClick={() => eliminarMedicamento(item.nombre)}
                        title="Eliminar Medicamento"
                        onMouseEnter={e => e.currentTarget.style.opacity = '0.8'} 
                        onMouseLeave={e => e.currentTarget.style.opacity = '1'}
                      >
                        🗑️
                      </button>
                    </div>
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
    <div className="admin-container" style={{ marginTop: '-40px', paddingBottom: '80px' }}>
      <div style={{ marginBottom: '20px' }}>
        <Link to="/" style={{ color: '#ca8a04', textDecoration: 'none', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '5px' }}>
          ⬅ Volver al portal
        </Link>
      </div>
      
      <header className="admin-banner">
        <h1>{rolUsuario === 'ADMIN' ? 'Centro de Comando Súper Admin' : 'Panel de Gestión Farmacéutica'}</h1>
        <p>{rolUsuario === 'ADMIN' ? 'Monitoreo de sistema y aprobación de locales' : 'Actualiza tus precios y stock en tiempo real'}</p>
      </header>

      {rolUsuario === 'ADMIN' && renderVistaAdmin()}
      {rolUsuario === 'FARMACEUTICO' && renderVistaFarmaceutico()}
    </div>
  );
};

export default AdminPanel;