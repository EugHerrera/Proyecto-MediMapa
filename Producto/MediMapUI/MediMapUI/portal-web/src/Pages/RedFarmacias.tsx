import React, { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { MapContainer, TileLayer, Marker, Popup, useMap, Circle } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';
import './RedFarmacias.css';

import icon from 'leaflet/dist/images/marker-icon.png';
import iconShadow from 'leaflet/dist/images/marker-shadow.png';
import logoAhumada from '../assets/Ahumada.png';
import logoSimi from '../assets/Drsimi.png';
import logoSalcobrand from '../assets/Salcobrand.png';
import logotipoAhumada from '../assets/logotipoahumada.png';
import logotipoDrSimi from '../assets/logotipodrsimi.png';
import logotipoSalcobrand from '../assets/logotiposalcobrand.png';
let DefaultIcon = L.icon({ iconUrl: icon, shadowUrl: iconShadow, iconSize: [25, 41], iconAnchor: [12, 41] });
L.Marker.prototype.options.icon = DefaultIcon;

const farmaciaIcon = L.divIcon({
  className: 'farmacia-icon',
  html: '<span>🏪</span>',
  iconSize: [32, 32],
  iconAnchor: [16, 32],
});

const crearIconoCircular = (logoUrl: string, borderColor: string) => {
  return L.divIcon({
    className: 'cadena-icon',
    html: `<div style="width: 40px; height: 40px; border-radius: 50%; background: white; border: 3px solid ${borderColor}; display: flex; align-items: center; justify-content: center; box-shadow: 0 2px 8px rgba(0,0,0,0.3); overflow: hidden;"><img src="${logoUrl}" style="width: 36px; height: 36px; object-fit: contain;" /></div>`,
    iconSize: [40, 40],
    iconAnchor: [20, 40],
  });
};

const ahumadaIcon = crearIconoCircular(logotipoAhumada, '#c41e3a');
const salcobrandIcon = crearIconoCircular(logotipoSalcobrand, '#0066cc');
const drsimiIcon = crearIconoCircular(logotipoDrSimi, '#0066cc');

const getIconoFarmacia = (cadenaNombre: string) => {
  switch (cadenaNombre) {
    case 'Ahumada':
      return ahumadaIcon;
    case 'Salcobrand':
      return salcobrandIcon;
    case 'Dr Simi':
      return drsimiIcon;
    default:
      return farmaciaIcon;
  }
};

interface SucursalGeo {
  id_sucursal: number;
  nombre_sucursal: string;
  direccion: string;
  latitud?: number;
  longitud?: number;
  ubicacion?: {
    type?: string;
    coordinates?: [number, number];
    x?: number;
    y?: number;
  };
  comuna: { nombreCom: string } | null;
}

function calcularDistanciaKm(lat1: number, lon1: number, lat2: number, lon2: number) {
  const R = 6371;
  const dLat = (lat2 - lat1) * (Math.PI / 180);
  const dLon = (lon2 - lon1) * (Math.PI / 180);
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(lat1 * (Math.PI / 180)) * Math.cos(lat2 * (Math.PI / 180)) *
    Math.sin(dLon / 2) * Math.sin(dLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}

function VolarAlCentro({ lat, lng }: { lat: number, lng: number }) {
  const map = useMap();
  useEffect(() => { map.flyTo([lat, lng], 13, { animate: true, duration: 1.5 }); }, [lat, lng, map]);
  return null;
}

const RedFarmacias: React.FC = () => {
  const [comuna, setComuna] = useState('Todas');
  const [tipoFarmacia, setTipoFarmacia] = useState('Todas');
  const [radioMetros, setRadioMetros] = useState(3500);
  const [modoUbicacion, setModoUbicacion] = useState<'gps' | 'direccion'>('gps');
  const [direccionTexto, setDireccionTexto] = useState('');
  const [buscandoDireccion, setBuscandoDireccion] = useState(false);
  const [errorDireccion, setErrorDireccion] = useState('');
  const [ubicacion, setUbicacion] = useState<{ lat: number; lng: number } | null>(null);
  const [estadoUbicacion, setEstadoUbicacion] = useState('');
  const [farmacias, setFarmacias] = useState<SucursalGeo[]>([]);
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState('');
  const [filtroAhumada, setFiltroAhumada] = useState(true);
  const [filtroSalcobrand, setFiltroSalcobrand] = useState(true);
  const [filtroDrSimi, setFiltroDrSimi] = useState(true);

  const pedirUbicacion = () => {
    setModoUbicacion('gps');
    setEstadoUbicacion('Buscando tu ubicación en el dispositivo...');
    setError('');
    setErrorDireccion('');

    if (!navigator.geolocation) {
      setEstadoUbicacion('Tu navegador no soporta geolocalización. Usa la opción de dirección.');
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const coords = {
          lat: position.coords.latitude,
          lng: position.coords.longitude,
        };
        setUbicacion(coords);
        setEstadoUbicacion('Ubicación obtenida. Buscando farmacias cercanas...');
      },
      () => {
        setUbicacion(null);
        setEstadoUbicacion('No se pudo obtener la ubicación. Verifica permisos del navegador.');
      },
      {
        enableHighAccuracy: true,
        timeout: 12000,
        maximumAge: 0,
      }
    );
  };

  const buscarDireccion = async () => {
    if (!direccionTexto.trim()) {
      setErrorDireccion('Escribe una dirección para buscar.');
      return;
    }

    setModoUbicacion('direccion');
    setBuscandoDireccion(true);
    setErrorDireccion('');
    setEstadoUbicacion('Buscando ubicación por dirección...');

    try {
      const respuesta = await fetch(
        `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(direccionTexto)}&limit=1`
      );
      const datos = await respuesta.json();

      if (!datos || datos.length === 0) {
        setErrorDireccion('No se encontró la dirección. Prueba otra ubicación.');
        setEstadoUbicacion('No se encontró la dirección.');
        return;
      }

      const primerResultado = datos[0];
      setUbicacion({ lat: Number(primerResultado.lat), lng: Number(primerResultado.lon) });
      setEstadoUbicacion('Ubicación de dirección encontrada. Mostrando farmacias cercanas.');
    } catch (err) {
      console.error(err);
      setErrorDireccion('Error al buscar la dirección. Intenta nuevamente.');
      setEstadoUbicacion('Error en la búsqueda de dirección.');
    } finally {
      setBuscandoDireccion(false);
    }
  };

  useEffect(() => {
    pedirUbicacion();
  }, []);

  useEffect(() => {
    if (!ubicacion) return;

    setCargando(true);
    setError('');

    const url = `http://localhost:8080/api/v1/geolocalizacion/sucursales?lat=${ubicacion.lat}&lon=${ubicacion.lng}&radio=${radioMetros}`;

    fetch(url)
      .then((response) => {
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        return response.json();
      })
      .then((payload) => {
        const lista = Array.isArray(payload)
          ? payload
          : Array.isArray(payload?.data)
            ? payload.data
            : Array.isArray(payload?.sucursales)
              ? payload.sucursales
              : [];

        if (!Array.isArray(lista)) {
          console.warn('Geolocalización: response no es un array', payload);
        }

        setFarmacias(lista);
        setCargando(false);
        setEstadoUbicacion('Mostrando sucursales cercanas en el mapa. No guardamos tu ubicación.');
      })
      .catch((err) => {
        console.error('Error geolocalizacion:', err);
        setError('Error al cargar farmacias cercanas. Revisa la API de geolocalización.');
        setCargando(false);
      });
  }, [ubicacion, radioMetros]);

  const cadenasSeleccionadas = useMemo(() => {
    const seleccionadas: string[] = [];
    if (filtroAhumada) seleccionadas.push('Ahumada');
    if (filtroSalcobrand) seleccionadas.push('Salcobrand');
    if (filtroDrSimi) seleccionadas.push('Dr Simi');
    return seleccionadas;
  }, [filtroAhumada, filtroSalcobrand, filtroDrSimi]);

  const farmaciasProcesadas = useMemo(() => {
    const lista = farmacias.map((sucursal) => {
      const lat = sucursal.latitud ?? sucursal.ubicacion?.coordinates?.[1] ?? sucursal.ubicacion?.y;
      const lon = sucursal.longitud ?? sucursal.ubicacion?.coordinates?.[0] ?? sucursal.ubicacion?.x;
      const distancia = ubicacion && lat != null && lon != null
        ? calcularDistanciaKm(ubicacion.lat, ubicacion.lng, Number(lat), Number(lon))
        : 0;
      const nombre = sucursal.nombre_sucursal || 'Farmacia cercana';
      const cadenaNombre = nombre.toLowerCase().includes('ahumada')
        ? 'Ahumada'
        : nombre.toLowerCase().includes('salco')
          ? 'Salcobrand'
          : nombre.toLowerCase().includes('simi')
            ? 'Dr Simi'
            : 'Independiente';
      const tipo = cadenaNombre === 'Independiente' ? 'Independiente' : 'Cadena';
      return {
        ...sucursal,
        latitud: lat,
        longitud: lon,
        distancia,
        tipo,
        cadenaNombre,
        comunaNombre: sucursal.comuna?.nombreCom || 'Desconocida',
      };
    });

    return lista
      .filter((sucursal) => comuna === 'Todas' || sucursal.comunaNombre === comuna)
      .filter((sucursal) => {
        if (tipoFarmacia === 'Independiente') {
          return sucursal.tipo === 'Independiente';
        }
        if (tipoFarmacia === 'Cadena') {
          return sucursal.tipo === 'Cadena' && cadenasSeleccionadas.includes(sucursal.cadenaNombre);
        }
        if (sucursal.tipo === 'Independiente') {
          return true;
        }
        return cadenasSeleccionadas.length === 0 || cadenasSeleccionadas.includes(sucursal.cadenaNombre);
      })
      .sort((a, b) => a.distancia - b.distancia);
  }, [farmacias, comuna, tipoFarmacia, ubicacion, cadenasSeleccionadas]);

  const comunasDisponibles = useMemo(() => {
    const nombres = Array.from(new Set(farmacias.map((sucursal) => sucursal.comuna?.nombreCom || 'Desconocida')))
      .filter((nombre) => nombre !== 'Desconocida');
    return ['Todas', ...nombres];
  }, [farmacias]);

  const mapCenter: [number, number] = ubicacion ? [ubicacion.lat, ubicacion.lng] : [-33.5212, -70.5973];

  return (
    <div className="red-container">
      
      {/* 🔥 AQUÍ ESTÁ EL CAMBIO PRINCIPAL: .red-banner para heredar el estilo Premium 🔥 */}
      <header className="red-banner">
        <h1>Red de Farmacias MediMapa</h1>
        <p>Usa tu ubicación para ver las farmacias más cercanas en un mapa interactivo.</p>

        <div className="logos-grid">
          <a href="https://www.farmaciasahumada.cl/" target="_blank" rel="noopener noreferrer" className="logo-card link-card">
            <img src={logoAhumada} alt="Farmacias Ahumada" />
          </a>
          <a href="https://www.drsimi.cl/" target="_blank" rel="noopener noreferrer" className="logo-card link-card">
            <img src={logoSimi} alt="Dr. Simi" />
          </a>
          <a href="https://salcobrand.cl/" target="_blank" rel="noopener noreferrer" className="logo-card link-card">
            <img src={logoSalcobrand} alt="Salcobrand" />
          </a>
          <Link to="/registro-farmacia" className="logo-card independiente-card link-card">
            <span className="independiente-icon">🏪</span>
            <span>Farmacias Independientes</span>
            <small>Inscribe tu local aquí</small>
          </Link>
        </div>
      </header>

      <main className="red-main-content">
        <aside className="red-sidebar">
          <h3>Busca tu farmacia</h3>

          <div className="filter-group">
            <label>Comuna</label>
            <select value={comuna} onChange={(e) => setComuna(e.target.value)}>
              {comunasDisponibles.map((nombre) => (
                <option key={nombre} value={nombre}>{nombre}</option>
              ))}
            </select>
          </div>

          <div className="filter-group">
            <label>Ubicación</label>
            <div className="ubicacion-mode">
              <button type="button" className={modoUbicacion === 'gps' ? 'active' : ''} onClick={() => setModoUbicacion('gps')}>
                Usar mi ubicación
              </button>
              <button type="button" className={modoUbicacion === 'direccion' ? 'active' : ''} onClick={() => setModoUbicacion('direccion')}>
                Escribir dirección
              </button>
            </div>
          </div>

          {modoUbicacion === 'direccion' && (
            <div className="filter-group">
              <label>Dirección</label>
              <input
                type="text"
                placeholder="Ej. Vicuña Mackenna 1234, La Florida"
                value={direccionTexto}
                onChange={(e) => setDireccionTexto(e.target.value)}
              />
              <button type="button" className="btn-buscar-direccion" onClick={buscarDireccion} disabled={buscandoDireccion}>
                {buscandoDireccion ? 'Buscando...' : 'Buscar dirección'}
              </button>
              {errorDireccion && <p className="error-ubicacion">{errorDireccion}</p>}
            </div>
          )}

          <div className="filter-group">
            <label>Tipo de Establecimiento</label>
            <select value={tipoFarmacia} onChange={(e) => setTipoFarmacia(e.target.value)}>
              <option value="Todas">Todas</option>
              <option value="Cadena">Cadenas</option>
              <option value="Independiente">Independientes</option>
            </select>
          </div>

          <div className="filter-group">
            <label>Cadena</label>
            <div className="chain-selector">
              <label>
                <input type="checkbox" checked={filtroAhumada} onChange={(e) => setFiltroAhumada(e.target.checked)} />
                Ahumada
              </label>
              <label>
                <input type="checkbox" checked={filtroSalcobrand} onChange={(e) => setFiltroSalcobrand(e.target.checked)} />
                Salcobrand
              </label>
              <label>
                <input type="checkbox" checked={filtroDrSimi} onChange={(e) => setFiltroDrSimi(e.target.checked)} />
                Dr Simi
              </label>
            </div>
            <small>Elige las cadenas que quieras mostrar.</small>
          </div>

          <div className="filter-group">
            <label>Radio de búsqueda</label>
            <input
              type="range"
              min={1000}
              max={7000}
              step={500}
              value={radioMetros}
              onChange={(e) => setRadioMetros(Number(e.target.value))}
            />
            <span>{Math.round(radioMetros / 100) / 10} km</span>
          </div>

          <button className="btn-ubicacion" type="button" onClick={pedirUbicacion}>
            📍 Actualizar ubicación
          </button>

          {estadoUbicacion && <p className="estado-ubicacion">{estadoUbicacion}</p>}
          {error && <p className="error-ubicacion">{error}</p>}
        </aside>

        <section className="red-results">
          <div className="mapa-real">
            <MapContainer center={mapCenter} zoom={13} style={{ height: '100%', width: '100%' }}>
              <VolarAlCentro lat={mapCenter[0]} lng={mapCenter[1]} />
              <TileLayer url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png" />
              
              {ubicacion && (
                <>
                  <Marker position={[ubicacion.lat, ubicacion.lng]} icon={DefaultIcon}>
                    <Popup>Tú estás aquí</Popup>
                  </Marker>
                  <Circle center={[ubicacion.lat, ubicacion.lng]} radius={radioMetros} pathOptions={{ color: '#ca8a04', fillColor: '#ca8a04', fillOpacity: 0.1 }} />
                </>
              )}

              {farmaciasProcesadas
                .filter((sucursal) => sucursal.latitud != null && sucursal.longitud != null)
                .map((sucursal) => (
                  <Marker key={sucursal.id_sucursal} position={[Number(sucursal.latitud), Number(sucursal.longitud)]} icon={getIconoFarmacia(sucursal.cadenaNombre)}>
                    <Popup>
                      <strong>{sucursal.nombre_sucursal}</strong><br/>
                      {sucursal.direccion}<br/>
                      <small>{sucursal.comunaNombre}</small><br/>
                      <small>{sucursal.distancia?.toFixed(2)} km</small>
                    </Popup>
                  </Marker>
                ))}
            </MapContainer>
          </div>

          <div className="lista-farmacias lista-cercanas">
            <div className="lista-header">
              <h3>Farmacias más cercanas</h3>
              <span>{farmaciasProcesadas.length} resultados</span>
            </div>
            <table className="tabla-farmacias">
              <thead>
                <tr>
                  <th>Nombre</th>
                  <th>Dirección</th>
                  <th>Comuna</th>
                  <th>Tipo</th>
                  <th>Distancia</th>
                </tr>
              </thead>
              <tbody>
                {cargando ? (
                  <tr><td colSpan={5}>Cargando resultados cercanos...</td></tr>
                ) : farmaciasProcesadas.length === 0 ? (
                  <tr><td colSpan={5}>No hay farmacias dentro del radio seleccionado.</td></tr>
                ) : (
                  farmaciasProcesadas.map((sucursal) => (
                    <tr key={sucursal.id_sucursal}>
                      <td>{sucursal.nombre_sucursal}</td>
                      <td>{sucursal.direccion}</td>
                      <td>{sucursal.comunaNombre}</td>
                      <td>
                        <span className={`badge ${sucursal.tipo === 'Cadena' ? 'cadena' : 'independiente'}`}>
                          {sucursal.tipo}
                        </span>
                      </td>
                      <td>{sucursal.distancia?.toFixed(2)} km</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </section>
      </main>
    </div>
  );
};

export default RedFarmacias;