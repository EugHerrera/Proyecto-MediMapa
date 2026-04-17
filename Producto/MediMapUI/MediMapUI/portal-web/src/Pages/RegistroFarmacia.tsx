
import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import './RegistroFarmacia.css';

const RegistroFarmacia = () => {
  const navigate = useNavigate();
  const [aceptoTerminos, setAceptoTerminos] = useState(false);
  const [cargando, setCargando] = useState(false);

  // Estados para capturar los datos de la imagen
  const [datosEstablecimiento, setDatosEstablecimiento] = useState({
    nombre: '', rut: '', region: '', comuna: '', direccion: '', tamano: '', inicioActividades: '', resolucion: ''
  });

  const [datosRepresentante, setDatosRepresentante] = useState({
    nombre: '', run: '', correo: '', fijo: '', movil: ''
  });

  const [datosQuimico, setDatosQuimico] = useState({
    nombre: '', run: '', correo: '', fijo: '', movil: ''
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!aceptoTerminos) return;

    setCargando(true);
    try {
      // Aquí conectarás con tu microservicio 8085 o el Gateway 8080
      console.log("Enviando datos:", { datosEstablecimiento, datosRepresentante, datosQuimico });
      
      // Simulación de envío
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      alert("Solicitud de afiliación enviada con éxito. Revisaremos los datos legales.");
      navigate('/login');
    } catch (error) {
      alert("Error al enviar el formulario.");
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="registro-container">
      <div className="form-wrapper">
        <header className="form-header">
          <h1>Paso 1: Formulario de Afiliación</h1>
          <p>Complete los datos para unir su farmacia a la red MediMapa</p>
        </header>

        <form onSubmit={handleSubmit}>
          {/* SECCIÓN 1: DATOS DEL ESTABLECIMIENTO */}
          <section className="form-section">
            <h2 className="section-title">Datos del Establecimiento</h2>
            <div className="grid-container">
              <div className="input-group">
                <label>Nombre farmacia (*)</label>
                <input type="text" required value={datosEstablecimiento.nombre} onChange={e => setDatosEstablecimiento({...datosEstablecimiento, nombre: e.target.value})} />
              </div>
              <div className="input-group">
                <label>Rut farmacia (*)</label>
                <input type="text" placeholder="12.345.678-5" required value={datosEstablecimiento.rut} onChange={e => setDatosEstablecimiento({...datosEstablecimiento, rut: e.target.value})} />
              </div>
              <div className="input-group">
                <label>Región (*)</label>
                <select required onChange={e => setDatosEstablecimiento({...datosEstablecimiento, region: e.target.value})}>
                  <option value="">--Seleccione región--</option>
                  <option value="RM">Región Metropolitana</option>
                </select>
              </div>
              <div className="input-group">
                <label>Comuna (*)</label>
                <select required onChange={e => setDatosEstablecimiento({...datosEstablecimiento, comuna: e.target.value})}>
                  <option value="">--Seleccione comuna--</option>
                  <option value="La Florida">La Florida</option>
                </select>
              </div>
              <div className="input-group full-width">
                <label>Dirección (*)</label>
                <input type="text" required value={datosEstablecimiento.direccion} onChange={e => setDatosEstablecimiento({...datosEstablecimiento, direccion: e.target.value})} />
              </div>
            </div>
          </section>

          {/* SECCIÓN 2: DATOS DEL REPRESENTANTE LEGAL */}
          <section className="form-section">
            <h2 className="section-title">Datos del Representante Legal</h2>
            <div className="grid-container">
              <div className="input-group">
                <label>Nombre completo (*)</label>
                <input type="text" required value={datosRepresentante.nombre} onChange={e => setDatosRepresentante({...datosRepresentante, nombre: e.target.value})} />
              </div>
              <div className="input-group">
                <label>Run (*)</label>
                <input type="text" required value={datosRepresentante.run} onChange={e => setDatosRepresentante({...datosRepresentante, run: e.target.value})} />
              </div>
              <div className="input-group">
                <label>Correo (*)</label>
                <input type="email" required value={datosRepresentante.correo} onChange={e => setDatosRepresentante({...datosRepresentante, correo: e.target.value})} />
              </div>
              <div className="input-group">
                <label>Teléfono móvil</label>
                <input type="text" value={datosRepresentante.movil} onChange={e => setDatosRepresentante({...datosRepresentante, movil: e.target.value})} />
              </div>
            </div>
          </section>

          {/* SECCIÓN 3: DATOS DEL QUÍMICO FARMACÉUTICO */}
          <section className="form-section">
            <h2 className="section-title">Datos del Químico Farmacéutico</h2>
            <div className="grid-container">
              <div className="input-group">
                <label>Nombre completo (*)</label>
                <input type="text" required value={datosQuimico.nombre} onChange={e => setDatosQuimico({...datosQuimico, nombre: e.target.value})} />
              </div>
              <div className="input-group">
                <label>Run (*)</label>
                <input type="text" required value={datosQuimico.run} onChange={e => setDatosQuimico({...datosQuimico, run: e.target.value})} />
              </div>
              <div className="input-group">
                <label>Correo (*)</label>
                <input type="email" required value={datosQuimico.correo} onChange={e => setDatosQuimico({...datosQuimico, correo: e.target.value})} />
              </div>
            </div>
          </section>

          {/* CLÁUSULA LEY 21.719 */}
          <div className="ley-box">
            <label className="checkbox-label">
              <input type="checkbox" checked={aceptoTerminos} onChange={e => setAceptoTerminos(e.target.checked)} />
              <span>
                <strong>Cumplimiento Ley 21.719:</strong> Acepto el tratamiento de mis datos personales y profesionales 
                exclusivamente para fines de validación sanitaria y operativa de MediMapa.
              </span>
            </label>
          </div>

          <div className="form-actions">
            <button type="submit" className="btn-save" disabled={!aceptoTerminos || cargando}>
              {cargando ? 'Guardando...' : 'Guardar Formulario, ir al paso 2 ➡️'}
            </button>
            <Link to="/login" className="btn-back">Cancelar</Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RegistroFarmacia;