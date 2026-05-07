import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
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
  const ID_SUCURSAL = 99; // ID de la sucursal que creamos en PostgreSQL

  // ==========================================
  // ESTADOS DEL SÚPER ADMIN
  // ==========================================
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
    } else if (rolUsuario === 'FARMACEUTICO') {
      cargarInventarioReal();
    }
  }, [rolUsuario]);

  const cargarDatosAdmin = async () => {
    try {
      const respMed = await apiUsuarios.get(`/usuarios/medicamentos-admin`);
      setMedicamentosMaster(respMed.data);
    } catch (error) { console.error("Error al cargar datos maestros:", error); }
  };

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
    if (!window.confirm("⚠️ ¿Iniciar extracción masiva? Esto tomará varios minutos.")) return;
    setCargando(true);
    try {
      // 🔥 CORRECCIÓN: PUERTO 8080 PARA PASAR POR EL GATEWAY 🔥
      const respuesta = await fetch('http://localhost:8080/api/scraper/forzar-masivo', { method: 'POST' });
      const mensaje = await respuesta.text();
      alert(mensaje);
    } catch (error) { alert("❌ Error al contactar al motor de Scraping."); } finally { setCargando(false); }
  };

  const manejarSubidaIsp = async () => {
    if (!archivoIsp) return;
    setCargando(true);
    const formData = new FormData();
    formData.append('archivo', archivoIsp);
    try {
      const respuesta = await apiUsuarios.post('/usuarios/admin/subir-isp', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      alert(respuesta.data); setArchivoIsp(null); cargarDatosAdmin(); 
    } catch (error) { alert("❌ Error al procesar Excel ISP."); } finally { setCargando(false); }
  };

  // ==========================================
  // FUNCIONES DEL FARMACÉUTICO
  // ==========================================
  const subirArchivoInventario = async () => {
    if (!archivo) return;
    setCargando(true);
    const formData = new FormData(); 
    formData.append('archivo', archivo);
    formData.append('idSucursal', ID_SUCURSAL.toString());
    try {
      await apiUsuarios.post('/usuarios/inventario/subir', formData, { headers: { 'Content-Type': 'multipart/form-data' } });
      alert("✅ Inventario actualizado."); setArchivo(null); cargarInventarioReal();
    } catch (err) { alert("❌ Error al subir archivo."); } finally { setCargando(false); }
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

  const corregirNombre = async (idMed: number, nombreActual: string) => {
    const nuevoNombre = window.prompt(`Corregir nombre para "${nombreActual}":`, nombreActual);
    if (!nuevoNombre || nuevoNombre === nombreActual) return;
    try {
      await apiUsuarios.patch(`/usuarios/inventario/actualizar-nombre?idMedicamento=${idMed}&nuevoNombre=${encodeURIComponent(nuevoNombre)}`);
      alert("✅ Nombre corregido."); cargarInventarioReal();
    } catch (error) { alert("❌ Error al corregir el nombre."); }
  };

  const agregarMedicamentoManual = async () => {
    if (!nuevoNombre || !nuevoPrecio || !nuevoLaboratorio) { 
      alert("⚠️ Completa Nombre, Laboratorio y Precio."); return; 
    }
    setCargando(true);
    try {
      await apiUsuarios.post(`/usuarios/inventario/agregar-manual`, { 
        idSucursal: ID_SUCURSAL, 
        nombre: nuevoNombre, 
        laboratorio: nuevoLaboratorio, 
        precio: Number(nuevoPrecio) 
      });
      alert("✅ Agregado con éxito.");
      setNuevoNombre(''); setNuevoLaboratorio(''); setNuevoPrecio(''); cargarInventarioReal(); 
    } catch (error: any) { alert(`❌ Error al guardar en el servidor.`); } finally { setCargando(false); }
  };

  const eliminarMedicamento = async (nombreMedicamento: string) => {
    if (!window.confirm(`¿Eliminar "${nombreMedicamento}"?`)) return;
    try {
      await apiUsuarios.delete(`/usuarios/inventario/eliminar?idSucursal=${ID_SUCURSAL}&nombreMedicamento=${encodeURIComponent(nombreMedicamento)}`);
      alert(`✅ Eliminado.`); cargarInventarioReal(); 
    } catch (error) { alert("❌ Error al eliminar."); }
  };

  const renderVistaAdmin = () => (
    <div className="admin-dashboard-grid" style={{ display: 'grid', gap: '20px' }}>
      <div className="admin-card" style={{ gridColumn: '1 / -1', padding: '15px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h3 style={{margin: 0}}>🤖 Motor Scraper</h3>
          <button className="btn-premium" onClick={manejarScrapingMasivo} disabled={cargando}>
            {cargando ? '⏳ Procesando...' : '⚡ Forzar Actualización Masiva'}
          </button>
        </div>
      </div>

      <div className="admin-card" style={{ gridColumn: '1 / -1', borderLeft: '5px solid #ca8a04', backgroundColor: '#fefce8' }}>
        <h2 style={{ color: '#854d0e' }}>📘 Gestión del Catálogo Maestro</h2>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '30px' }}>
          <div>
            <p><strong>Certificación ISP (Excel):</strong></p>
            <input type="file" accept=".xlsx" onChange={(e) => e.target.files && setArchivoIsp(e.target.files[0])} />
            <button className="btn-premium" onClick={manejarSubidaIsp} disabled={!archivoIsp || cargando} style={{ marginTop: '10px', width: '100%' }}>
              {cargando ? 'Procesando...' : 'Actualizar Sellos'}
            </button>
          </div>
          <div style={{ borderLeft: '1px solid #fde047', paddingLeft: '30px' }}>
            <p><strong>Estadísticas:</strong></p>
            <div style={{ fontSize: '1.2rem', color: '#ca8a04' }}>
              💊 {medicamentosMaster.length} fármacos <br />
              🧬 {medicamentosMaster.filter(m => m.es_bioequivalente).length} bioequivalentes
            </div>
          </div>
        </div>
      </div>

      <div className="admin-card" style={{ gridColumn: '1 / -1' }}>
        <h2>🔍 Catálogo Global</h2>
        <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
          <table className="admin-table">
            <thead>
              <tr><th>Nombre</th><th>Principio Activo</th><th>Acción</th></tr>
            </thead>
            <tbody>
              {medicamentosMaster.map(med => (
                <tr key={med.id_medicamento}>
                  <td><strong>{med.nombre_canonico}</strong></td>
                  <td>{med.principio_activo}</td>
                  <td><button onClick={() => {}} style={{ background: 'none', border: 'none' }}>🗑️</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );

  const renderVistaFarmaceutico = () => (
    <>
      <div style={{ display: 'flex', gap: '20px', marginBottom: '20px' }}>
        <div className="stat-card" style={{ backgroundColor: '#fefce8', padding: '25px', borderRadius: '12px', flex: 1, display: 'flex', alignItems: 'center', gap: '20px' }}>
          <div style={{ fontSize: '3rem' }}>📦</div>
          <div>
            <h3 style={{ margin: 0, color: '#854d0e' }}>Tu Inventario</h3>
            <p style={{ margin: 0, color: '#ca8a04', fontSize: '2rem', fontWeight: '900' }}>{totalMedicamentos} <span style={{fontSize: '1rem'}}>ítems</span></p>
          </div>
        </div>
      </div>

      <div className="admin-card">
        <h2>📄 Carga Masiva</h2>
        <div className="dropzone" style={{ border: '2px dashed #cbd5e1', padding: '20px', textAlign: 'center', borderRadius: '12px', marginBottom: '10px' }}>
          <input type="file" accept=".xlsx" onChange={(e) => e.target.files && setArchivo(e.target.files[0])} />
          <p>{archivo ? `Listo: ${archivo.name}` : 'Arrastra tu Excel de inventario aquí'}</p>
        </div>
        <button className="btn-premium" onClick={subirArchivoInventario} disabled={!archivo || cargando}>
          {cargando ? 'Procesando...' : 'Actualizar Precios por Excel'}
        </button>
      </div>

      <div className="admin-card">
        <h2>⚡ Ajuste Manual Rápido</h2>
        <div style={{ backgroundColor: '#f8fafc', padding: '20px', borderRadius: '12px', marginBottom: '20px', display: 'flex', gap: '10px' }}>
          <input type="text" placeholder="Nombre" value={nuevoNombre} onChange={(e) => setNuevoNombre(e.target.value)} style={{flex: 2, padding: '10px'}} />
          <input type="text" placeholder="Laboratorio" value={nuevoLaboratorio} onChange={(e) => setNuevoLaboratorio(e.target.value)} style={{flex: 1.5, padding: '10px'}} />
          <input type="number" placeholder="Precio" value={nuevoPrecio} onChange={(e) => setNuevoPrecio(e.target.value)} style={{flex: 1, padding: '10px'}} />
          <button onClick={agregarMedicamentoManual} className="btn-premium" style={{margin: 0}}>Guardar</button>
        </div>

        <table className="admin-table">
          <thead>
            <tr><th>Medicamento</th><th>Laboratorio</th><th>Precio (CLP)</th><th>Acción</th></tr>
          </thead>
          <tbody>
            {inventario.map((item, index) => {
              const inputId = `precio-input-${index}`;
              return (
                <tr key={item.id || index}>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <strong>{item.nombre}</strong>
                      <button onClick={() => corregirNombre(item.id, item.nombre)} style={{background: 'none', border: 'none', cursor: 'pointer'}}>✏️</button>
                    </div>
                  </td>
                  <td style={{ color: '#64748b' }}>{item.laboratorio || 'No especificado'}</td>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                      <span style={{color: '#059669'}}>$</span>
                      <input id={inputId} type="number" className="input-precio" defaultValue={item.precio} style={{width: '80px'}} />
                    </div>
                  </td>
                  <td>
                    <div style={{ display: 'flex', gap: '10px' }}>
                      <button className="btn-guardar" onClick={() => guardarNuevoPrecio(item.nombre, inputId)}>Actualizar</button>
                      <button onClick={() => eliminarMedicamento(item.nombre)} style={{ backgroundColor: '#ef4444', color: 'white', border: 'none', padding: '5px 10px', borderRadius: '5px', cursor: 'pointer' }}>🗑️</button>
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
    <div className="admin-container" style={{ padding: '40px' }}>
      <header className="admin-banner" style={{ marginBottom: '30px' }}>
        <h1>{rolUsuario === 'ADMIN' ? 'Panel Súper Admin' : 'Gestión Farmacéutica'}</h1>
        <p>Administración de MediMapa</p>
      </header>

      {rolUsuario === 'ADMIN' ? renderVistaAdmin() : null}
      {rolUsuario === 'FARMACEUTICO' ? renderVistaFarmaceutico() : null}
    </div>
  );
};

export default AdminPanel;