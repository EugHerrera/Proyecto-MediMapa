import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { apiUsuarios } from '../services/api';
import './AdminPanel.css';

const AdminPanel = () => {
  const navigate = useNavigate();
  const [rolUsuario, setRolUsuario] = useState('');
  const [cargando, setCargando] = useState(false);
  
  // ==========================================
  // ESTADOS DEL FARMACÉUTICO
  // ==========================================
  const [inventario, setInventario] = useState<any[]>([]);
  const [totalMedicamentos, setTotalMedicamentos] = useState(0);
  const [archivo, setArchivo] = useState<File | null>(null);
  const [nuevoNombre, setNuevoNombre] = useState('');
  const [nuevoLaboratorio, setNuevoLaboratorio] = useState('');
  const [nuevoPrecio, setNuevoPrecio] = useState('');
  const ID_SUCURSAL = 99; // ID simulado para el farmacéutico

  // ==========================================
  // ESTADOS DEL SÚPER ADMIN
  // ==========================================
  const [solicitudes, setSolicitudes] = useState<any[]>([]);
  const [sucursalesMaster, setSucursalesMaster] = useState<any[]>([]);
  const [medicamentosMaster, setMedicamentosMaster] = useState<any[]>([]);
  const [archivoIsp, setArchivoIsp] = useState<File | null>(null);

  // ==========================================
  // CONTROL DE SESIÓN Y CARGA
  // ==========================================
  useEffect(() => {
    const rolGuardado = localStorage.getItem('usuarioRol');
    if (!rolGuardado) {
      navigate('/login'); 
    } else {
      setRolUsuario(rolGuardado.toUpperCase());
    }
  }, [navigate]);

  useEffect(() => {
    if (rolUsuario === 'ADMIN') {
      cargarDatosAdmin();
      cargarSolicitudesPendientes();
    } else if (rolUsuario === 'FARMACEUTICO') {
      cargarInventarioReal();
    }
  }, [rolUsuario]);

  // --- Lógica de Carga Admin ---
  const cargarDatosAdmin = async () => {
    try {
      const [respSuc, respMed] = await Promise.all([
        apiUsuarios.get(`/usuarios/farmacias-admin`),
        apiUsuarios.get(`/usuarios/medicamentos-admin`)
      ]);
      setSucursalesMaster(respSuc.data);
      setMedicamentosMaster(respMed.data);
    } catch (error) { console.error("Error al cargar datos maestros:", error); }
  };

  const cargarSolicitudesPendientes = async () => {
    try {
      const respuesta = await apiUsuarios.get(`/usuarios/solicitudes/pendientes`);
      setSolicitudes(respuesta.data); 
    } catch (error) { console.error("Error al cargar solicitudes:", error); }
  };

  // --- Lógica de Carga Farmacéutico ---
  const cargarInventarioReal = async () => {
    try {
      const respuesta = await apiUsuarios.get(`/usuarios/inventario/listar/${ID_SUCURSAL}`);
      setInventario(respuesta.data); 
      setTotalMedicamentos(respuesta.data.length); 
    } catch (error: any) { console.error("Error al cargar el inventario real:", error); }
  };

  // ==========================================
  // FUNCIONES DEL SÚPER ADMIN
  // ==========================================
  const manejarScrapingMasivo = async () => {
    if (!window.confirm("⚠️ ¿Iniciar extracción masiva de TODOS los medicamentos? Esto abrirá el navegador en el servidor y tomará varios minutos.")) return;
    setCargando(true);
    try {
      const respuesta = await fetch('http://localhost:8082/api/scraper/forzar-masivo', { method: 'POST' });
      const mensaje = await respuesta.text();
      alert(mensaje);
    } catch (error) { alert("❌ Error al contactar al motor de Scraping."); } finally { setCargando(false); }
  };

  const manejarAprobacion = async (id: number) => {
    if (!window.confirm("¿Seguro que deseas APROBAR esta farmacia?")) return;
    try {
      const resp = await apiUsuarios.patch(`/usuarios/solicitudes/${id}/aprobar`);
      alert(resp.data); cargarSolicitudesPendientes(); 
    } catch (err) { alert("Error al aprobar la farmacia."); }
  };

  const manejarRechazo = async (id: number) => {
    if (!window.confirm("¿Seguro que deseas RECHAZAR esta solicitud?")) return;
    try {
      const resp = await apiUsuarios.patch(`/usuarios/solicitudes/${id}/rechazar`);
      alert(resp.data); cargarSolicitudesPendientes(); 
    } catch (err) { alert("Error al rechazar la farmacia."); }
  };

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

  // 🔥 NUEVA FUNCIÓN: ENVIAR EXCEL DEL ISP 🔥
  const manejarSubidaIsp = async () => {
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
      cargarDatosAdmin(); // Refresca los datos para ver los nuevos bioequivalentes
    } catch (error) {
      console.error("Error al subir archivo ISP:", error);
      alert("❌ Error al procesar el archivo Excel. Revisa la consola de Java.");
    } finally {
      setCargando(false);
    }
  };

  // ==========================================
  // FUNCIONES DEL FARMACÉUTICO
  // ==========================================
  const manejarSeleccionArchivo = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) setArchivo(e.target.files[0]);
  };

  const subirArchivoInventario = async () => {
    if (!archivo) return;
    setCargando(true);
    const formData = new FormData(); formData.append('archivo', archivo);
    try {
      await apiUsuarios.post('/usuarios/inventario/subir', formData, { headers: { 'Content-Type': 'multipart/form-data' } });
      alert("✅ Inventario cargado con éxito."); setArchivo(null); cargarInventarioReal();
    } catch (err) { alert("❌ Error: Verifica tu sesión o conexión."); } finally { setCargando(false); }
  };

  const guardarNuevoPrecio = async (nombreMedicamento: string, precioInputId: string) => {
    const inputElement = document.getElementById(precioInputId) as HTMLInputElement;
    const nuevoPrecio = inputElement?.value;
    if (!nuevoPrecio) return;
    try {
      await apiUsuarios.put(`/usuarios/inventario/actualizar-precio?idSucursal=${ID_SUCURSAL}&textoBusqueda=${encodeURIComponent(nombreMedicamento)}&nuevoPrecio=${nuevoPrecio}`);
      alert(`✅ Precio actualizado.`); cargarInventarioReal(); 
    } catch (error) { alert("❌ Error al guardar el precio."); }
  };

  const eliminarMedicamento = async (nombreMedicamento: string) => {
    if (!window.confirm(`¿Estás seguro de eliminar "${nombreMedicamento}" de tu inventario?`)) return;
    try {
      await apiUsuarios.delete(`/usuarios/inventario/eliminar?idSucursal=${ID_SUCURSAL}&nombreMedicamento=${encodeURIComponent(nombreMedicamento.toLowerCase())}`);
      alert(`✅ Medicamento eliminado.`); cargarInventarioReal(); 
    } catch (error) { alert("❌ Error al eliminar el medicamento."); }
  };

  const agregarMedicamentoManual = async () => {
    if (!nuevoNombre || !nuevoPrecio || !nuevoLaboratorio) { alert("⚠️ Por favor completa el Nombre, Laboratorio y Precio."); return; }
    setCargando(true);
    try {
      await apiUsuarios.post(`/usuarios/inventario/agregar-manual`, { idSucursal: ID_SUCURSAL, nombre: nuevoNombre, laboratorio: nuevoLaboratorio, precio: Number(nuevoPrecio) });
      alert("✅ Medicamento agregado exitosamente.");
      setNuevoNombre(''); setNuevoLaboratorio(''); setNuevoPrecio(''); cargarInventarioReal(); 
    } catch (error: any) { alert(`❌ Error al agregar el medicamento en el servidor.`); } finally { setCargando(false); }
  };

  // ==========================================
  // VISTA 1: SÚPER ADMIN
  // ==========================================
  const renderVistaAdmin = () => (
    <div className="admin-dashboard-grid" style={{ display: 'grid', gap: '20px' }}>
      
      <div className="admin-card" style={{ gridColumn: '1 / -1', padding: '15px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h3 style={{margin: 0}}>🤖 Motor Scraper</h3>
          <button className="btn-premium" style={{ margin: 0, padding: '8px 15px' }} onClick={manejarScrapingMasivo} disabled={cargando}>
            {cargando ? '⏳ Procesando catálogo...' : '⚡ Forzar Actualización Masiva'}
          </button>
        </div>
      </div>

      <div className="admin-card" style={{ gridColumn: '1 / -1', borderLeft: '5px solid #ca8a04', backgroundColor: '#fefce8' }}>
        <h2 style={{ color: '#854d0e' }}>📘 Gestión del Catálogo Maestro</h2>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '30px' }}>
          <div>
            <p><strong>Certificación ISP (Masiva):</strong></p>
            <input type="file" accept=".xlsx" onChange={(e) => e.target.files && setArchivoIsp(e.target.files[0])} />
            
            {/* 🔥 AQUÍ ESTÁ EL BOTÓN CONECTADO A LA FUNCIÓN 🔥 */}
            <button className="btn-premium" onClick={manejarSubidaIsp} disabled={!archivoIsp || cargando} style={{ marginTop: '10px', width: '100%' }}>
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
                      style={{ background: med.es_bioequivalente ? '#fef08a' : '#f1f5f9', border: '1px solid #ca8a04', borderRadius: '15px', padding: '2px 10px', fontSize: '0.7rem', fontWeight: 'bold', cursor: 'pointer' }}
                    >
                      {med.es_bioequivalente ? '✅ SÍ' : '❌ NO'}
                    </button>
                  </td>
                  <td>
                    <button onClick={() => eliminarMedicamentoMaster(med.id_medicamento, med.nombre_canonico)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}>🗑️</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="admin-card">
        <h2>📋 Solicitudes Pendientes ({solicitudes.length})</h2>
        <table className="admin-table">
          <thead><tr><th>Farmacia</th><th>Acción</th></tr></thead>
          <tbody>
            {solicitudes.map(sol => (
              <tr key={sol.id_solicitud}>
                <td><strong>{sol.nombre_fantasia}</strong></td>
                <td style={{ display: 'flex', gap: '8px' }}>
                  <button onClick={() => manejarAprobacion(sol.id_solicitud)} style={{ backgroundColor: '#059669', color: 'white', padding: '8px 14px', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>✓ Aprobar</button>
                  <button onClick={() => manejarRechazo(sol.id_solicitud)} style={{ backgroundColor: '#ef4444', color: 'white', padding: '8px 14px', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>✕ Rechazar</button>
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

  // ==========================================
  // VISTA 2: FARMACÉUTICO
  // ==========================================
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
            <input type="text" placeholder="Ej. Paracetamol 500mg" value={nuevoNombre} onChange={(e) => setNuevoNombre(e.target.value)} style={{ flex: 2, padding: '12px', borderRadius: '8px', border: '1px solid #cbd5e1' }} />
            <input type="text" placeholder="Laboratorio (Ej. Andrómaco)" value={nuevoLaboratorio} onChange={(e) => setNuevoLaboratorio(e.target.value)} style={{ flex: 1.5, padding: '12px', borderRadius: '8px', border: '1px solid #cbd5e1' }} />
            <input type="number" placeholder="Precio ($)" value={nuevoPrecio} onChange={(e) => setNuevoPrecio(e.target.value)} style={{ flex: 1, padding: '12px', borderRadius: '8px', border: '1px solid #cbd5e1' }} />
            <button onClick={agregarMedicamentoManual} disabled={cargando} className="btn-premium" style={{ margin: 0 }}>
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
                  <td style={{ textTransform: 'capitalize' }}><strong style={{color: '#0f172a', fontSize: '1.05rem'}}>{item.nombre}</strong></td>
                  <td style={{ color: '#64748b', fontSize: '0.95rem' }}>{item.laboratorio ? item.laboratorio : 'No especificado'}</td>
                  <td style={{display: 'flex', alignItems: 'center', gap: '5px', height: '100%', paddingTop: '18px'}}>
                    <span style={{color: '#059669', fontWeight: 'bold'}}>$</span>
                    <input id={inputId} type="number" className="input-precio" defaultValue={item.precio} />
                  </td>
                  <td>
                    <div style={{ display: 'flex', gap: '10px' }}>
                      <button className="btn-guardar" onClick={() => guardarNuevoPrecio(item.nombre, inputId)}>Actualizar</button>
                      <button style={{ backgroundColor: '#ef4444', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', padding: '0 12px', fontSize: '1.2rem' }} onClick={() => eliminarMedicamento(item.nombre)} title="Eliminar">🗑️</button>
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
        <p>{rolUsuario === 'ADMIN' ? 'Control total del Catálogo Maestro y Red de Farmacias' : 'Actualiza tus precios y stock en tiempo real'}</p>
      </header>

      {/* RENDERIZADO CONDICIONAL */}
      {rolUsuario === 'ADMIN' ? renderVistaAdmin() : null}
      {rolUsuario === 'FARMACEUTICO' ? renderVistaFarmaceutico() : null}

    </div>
  );
};

export default AdminPanel;