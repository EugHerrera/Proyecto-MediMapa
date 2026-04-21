import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { apiUsuarios } from '../services/api'; // Importamos tu cartero de Axios
import './AdminPanel.css';

const AdminPanel = () => {
  const navigate = useNavigate();
  const [rolUsuario, setRolUsuario] = useState('');
  const [cargando, setCargando] = useState(false);
  
  useEffect(() => {
    const rolGuardado = localStorage.getItem('usuarioRol');
    if (!rolGuardado) {
      navigate('/login'); 
    } else {
      setRolUsuario(rolGuardado.toUpperCase());
    }
  }, [navigate]);

  const manejarCerrarSesion = () => {
    localStorage.clear();
    navigate('/login');
  };

  // --- LÓGICA DE APROBACIÓN (ADMIN) ---
  const manejarAprobacion = async (id: number) => {
    try {
      const resp = await apiUsuarios.patch(`/usuarios/aprobar/${id}`);
      alert(resp.data);
    } catch (err) {
      alert("Error: No tienes permisos o el servidor no responde.");
    }
  };

  // --- LÓGICA DE SUBIDA (FARMACÉUTICO) ---
  const [archivo, setArchivo] = useState<File | null>(null);
  const [inventario] = useState([
    { id: 1, nombre: 'Paracetamol 500mg', stock: 45, precio: 1200 },
  ]);

  const manejarSeleccionArchivo = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) setArchivo(e.target.files[0]);
  };

  const subirArchivoInventario = async () => {
    if (!archivo) return;
    setCargando(true);
    
    const formData = new FormData();
    formData.append('archivo', archivo);

    // 1. Sacamos el pasaporte (Token) de la memoria del navegador
    const token = localStorage.getItem('token'); 

    try {
      // 2. Se lo enviamos explícitamente en los headers (cabeceras)
      await apiUsuarios.post('/usuarios/inventario/subir', formData, {
        headers: { 
          'Content-Type': 'multipart/form-data',
          'Authorization': `Bearer ${token}` // <-- ¡Aquí está la magia!
        }
      });
      alert("✅ Inventario cargado con éxito en la Base de Datos.");
      setArchivo(null);
    } catch (err) {
      console.error(err);
      alert("❌ Error 403: El token no es válido o expiró. Intenta cerrar sesión y volver a entrar.");
    } finally {
      setCargando(false);
    }
  };

  const renderVistaAdmin = () => (
    <div className="admin-dashboard-grid">
      <div className="admin-card">
        <h2>🤖 Estado del Motor Scraper</h2>
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
        <h2>📋 Solicitudes de Farmacias Independientes</h2>
        <table className="admin-table">
            <thead>
                <tr><th>Farmacia</th><th>Dirección</th><th>Acción</th></tr>
            </thead>
            <tbody>
                <tr>
                    <td>Farmacia La Florida Centro</td>
                    <td>Av. Vicuña Mackenna 7000</td>
                    <td>
                        <button 
                          onClick={() => manejarAprobacion(1)} 
                          style={{ backgroundColor: '#0ea5e9', color: 'white', padding: '5px 10px', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                        >
                          Aprobar
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
      </div>
    </div>
  );

  const renderVistaFarmaceutico = () => (
    <>
      <div className="admin-card">
        <h2>📦 Actualización Masiva de Inventario</h2>
        <div className="dropzone">
          <span className="dropzone-icon">📄</span>
          <div className="dropzone-text">{archivo ? `Archivo: ${archivo.name}` : 'Sube tu Excel/CSV aquí'}</div>
          <input type="file" accept=".csv, .xlsx" onChange={manejarSeleccionArchivo} />
        </div>
        <button 
          className="btn-upload" 
          onClick={subirArchivoInventario} 
          disabled={!archivo || cargando}
        >
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
            {inventario.map((item) => (
              <tr key={item.id}>
                <td>{item.nombre}</td>
                <td>$ <input type="number" className="input-precio" defaultValue={item.precio} /></td>
                <td><button className="btn-guardar">Guardar</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );

  return (
    <div className="admin-container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <Link to="/" style={{ color: '#0ea5e9', textDecoration: 'none', fontWeight: 'bold' }}>
          ⬅ Volver al portal
        </Link>
        <button onClick={manejarCerrarSesion} className="btn-cerrar-sesion" style={{ backgroundColor: '#ef4444', color: 'white', padding: '8px 16px', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>
          🚪 Cerrar Sesión
        </button>
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