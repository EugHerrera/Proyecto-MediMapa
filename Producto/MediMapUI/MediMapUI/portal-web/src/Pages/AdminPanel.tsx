import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './AdminPanel.css';

const AdminPanel = () => {
  const navigate = useNavigate();
  // Leemos quién entró desde el Local Storage
  const [rolUsuario, setRolUsuario] = useState('');
  
  useEffect(() => {
    const rolGuardado = localStorage.getItem('usuarioRol');
    if (!rolGuardado) {
      navigate('/login'); // Si alguien intenta entrar sin hacer login, lo echamos
    } else {
      setRolUsuario(rolGuardado.toUpperCase());
    }
  }, [navigate]);

  // --- ESTADOS Y FUNCIONES DEL FARMACÉUTICO ---
  const [archivo, setArchivo] = useState<File | null>(null);
  const [inventario, setInventario] = useState([
    { id: 1, nombre: 'Paracetamol 500mg', stock: 45, precio: 1200 },
  ]);

  const manejarSeleccionArchivo = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) setArchivo(e.target.files[0]);
  };

  // --- VISTA 1: EL SÚPER ADMINISTRADOR (TÚ) ---
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
        <p style={{ color: '#64748b' }}>Tienes 1 nueva solicitud pendiente de aprobación.</p>
        <table className="admin-table">
            <thead>
                <tr><th>Farmacia</th><th>Dirección</th><th>Acción</th></tr>
            </thead>
            <tbody>
                <tr>
                    <td>Farmacia La Florida Centro</td>
                    <td>Av. Vicuña Mackenna 7000</td>
                    <td>
                        <button style={{ backgroundColor: '#0ea5e9', color: 'white', padding: '5px 10px', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Revisar</button>
                    </td>
                </tr>
            </tbody>
        </table>
      </div>
    </div>
  );

  // --- VISTA 2: EL FARMACÉUTICO ---
  const renderVistaFarmaceutico = () => (
    <>
      <div className="admin-card">
        <h2>📦 Actualización Masiva de Inventario</h2>
        <div className="dropzone">
          <span className="dropzone-icon">📄</span>
          <div className="dropzone-text">{archivo ? `Archivo: ${archivo.name}` : 'Sube tu Excel/CSV aquí'}</div>
          <input type="file" accept=".csv, .xlsx" onChange={manejarSeleccionArchivo} />
        </div>
        <button className="btn-upload" disabled={!archivo}>Procesar y Actualizar Precios</button>
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

  // --- RENDERIZADO PRINCIPAL ---
  return (
    <div className="admin-container">
      <Link to="/" style={{ color: '#0ea5e9', textDecoration: 'none', fontWeight: 'bold', marginBottom: '20px', display: 'inline-block' }}>
        ⬅ Volver al portal
      </Link>

      <div className="admin-header">
        <h1>{rolUsuario === 'ADMIN' ? 'Centro de Comando Súper Admin' : 'Panel de Gestión Farmacéutica'}</h1>
        <p>{rolUsuario === 'ADMIN' ? 'Monitoreo de sistema y aprobación de locales' : 'Actualiza tus precios y stock para La Florida'}</p>
      </div>

      {/* AQUÍ DECIDIMOS QUÉ MOSTRAR SEGÚN EL ROL */}
      {rolUsuario === 'ADMIN' && renderVistaAdmin()}
      {rolUsuario === 'FARMACEUTICO' && renderVistaFarmaceutico()}

    </div>
  );
};

export default AdminPanel;