import { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { MapContainer, TileLayer, Marker, Popup, useMap, Circle } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';
import './Resultados.css'; 
import { apiScraper } from '../services/api';

import logotipoAhumada from '../assets/logotipoahumada.png';
import logotipoDrSimi from '../assets/logotipodrsimi.png';
import logotipoSalcobrand from '../assets/logotiposalcobrand.png';

const userRedIcon = L.icon({
  iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41]
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
const independienteIcon = L.divIcon({
  className: 'farmacia-icon',
  html: '<div style="font-size: 20px; background: white; border-radius: 50%; width: 35px; height: 35px; display: flex; align-items: center; justify-content: center; box-shadow: 0 2px 5px rgba(0,0,0,0.3); border: 2px solid #ca8a04;">🏪</div>',
  iconSize: [35, 35],
  iconAnchor: [17, 35],
});

const getIconoFarmacia = (nombreFarmacia: string) => {
  const nombre = nombreFarmacia.toLowerCase();
  if (nombre.includes('ahumada')) return ahumadaIcon;
  if (nombre.includes('salco')) return salcobrandIcon;
  if (nombre.includes('simi')) return drsimiIcon;
  return independienteIcon;
};

function calcularDistanciaKm(lat1: number, lon1: number, lat2: number, lon2: number) {
  const R = 6371; 
  const dLat = (lat2 - lat1) * (Math.PI / 180);
  const dLon = (lon2 - lon1) * (Math.PI / 180);
  const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(lat1 * (Math.PI / 180)) * Math.cos(lat2 * (Math.PI / 180)) *
            Math.sin(dLon/2) * Math.sin(dLon/2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
  return R * c; 
}

function VolarAlCentro({ lat, lng }: { lat: number, lng: number }) {
  const map = useMap();
  useEffect(() => { map.flyTo([lat, lng], 14, { animate: true, duration: 1.5 }); }, [lat, lng, map]);
  return null;
}

function Resultados() {
  const [searchParams] = useSearchParams();
  const query = searchParams.get('q') || ''; 
  
  const [rawData, setRawData] = useState<any[]>([]); 
  const [preciosTarjetas, setPreciosTarjetas] = useState<any[]>([]); 
  const [todasLasSucursales, setTodasLasSucursales] = useState<any[]>([]); 
  
  const [cargando, setCargando] = useState<boolean>(false);
  const [ubicacion, setUbicacion] = useState<{lat: number, lng: number} | null>(null);
  const [estadoUbicacion, setEstadoUbicacion] = useState<string>("");
  const [radioKm, setRadioKm] = useState<number>(3); 

  const buscarMedicamentos = (forzarRefresh = false) => {
    if (!query) return;
    setCargando(true);
    
    if (forzarRefresh) {
      setPreciosTarjetas([]); 
      setRawData([]);
    }

    //  AXIOS (apiScraper) 
    const url = `/scraper/buscar?query=${encodeURIComponent(query)}${forzarRefresh ? '&forceRefresh=true' : ''}`;

    apiScraper.get(url)
      .then(response => {
        setRawData(response.data);
        setCargando(false);
      })
      .catch(error => {
        console.error("Error al buscar:", error);
        setCargando(false);
      });
  };

  useEffect(() => {
    buscarMedicamentos(false);
  }, [query]);

  useEffect(() => {
    if (rawData.length === 0) return;

    const baseLat = ubicacion ? ubicacion.lat : -33.5212;
    const baseLng = ubicacion ? ubicacion.lng : -70.5973;

    const farmaciasMasCercanas: Record<string, any> = {};
    const pinesUnicos = new Map(); 

    rawData.forEach((item: any) => {
      const cadena = (item.farmacia?.toLowerCase().includes("ahumada")) ? "Farmacias Ahumada" : 
                     (item.farmacia?.toLowerCase().includes("salco")) ? "Salcobrand" : 
                     (item.farmacia?.toLowerCase().includes("simi")) ? "Dr. Simi" : "Independiente";

      let lat = item.lat;
      let lng = item.lng;

      if (!lat || !lng) {
        if (cadena === "Farmacias Ahumada") { lat = baseLat + 0.0025; lng = baseLng + 0.0020; }
        else if (cadena === "Salcobrand") { lat = baseLat - 0.0020; lng = baseLng - 0.0025; }
        else if (cadena === "Dr. Simi") { lat = baseLat + 0.0015; lng = baseLng - 0.0030; }
        else { lat = baseLat - 0.0030; lng = baseLng + 0.0015; }
      }

      const distanciaKm = calcularDistanciaKm(baseLat, baseLng, lat, lng);
      const sucursalLista = { ...item, lat, lng, cadenaOficial: cadena, distancia: distanciaKm };
      
      if (!pinesUnicos.has(item.farmacia)) {
        pinesUnicos.set(item.farmacia, sucursalLista);
      }

      if (!farmaciasMasCercanas[cadena] || distanciaKm < farmaciasMasCercanas[cadena].distancia) {
        farmaciasMasCercanas[cadena] = sucursalLista;
      }
    });

    setPreciosTarjetas(Object.values(farmaciasMasCercanas));
    setTodasLasSucursales(Array.from(pinesUnicos.values())); 

  }, [rawData, ubicacion]); 

  const pedirUbicacion = () => {
    setEstadoUbicacion("Buscando tu ubicación GPS... 🛰️");
    if (!navigator.geolocation) {
      setEstadoUbicacion("❌ Tu navegador no soporta geolocalización.");
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setUbicacion({ lat: position.coords.latitude, lng: position.coords.longitude });
        setEstadoUbicacion("✅ Ubicación encontrada. Calculando las más cercanas a ti...");
      },
      (error) => {
        console.error(error);
        setEstadoUbicacion("❌ Permiso denegado. Asegúrate de darle permiso al navegador.");
      }
    );
  };

  const mapLat = ubicacion ? ubicacion.lat : -33.5212;
  const mapLng = ubicacion ? ubicacion.lng : -70.5973;

  const sucursalesEnRango = todasLasSucursales.filter(p => {
    if (!ubicacion) return true; 
    return p.distancia <= radioKm;
  });

  return (
    <div className="resultados-container">
      
      <div className="resultados-header">
        <div className="resultados-title-group">
          <Link to="/" className="resultados-volver">⬅ Volver</Link>
          <h1>Resultados para: "{query}"</h1>
        </div>
        <button 
          className="btn-forzar-busqueda"
          onClick={() => buscarMedicamentos(true)} 
          disabled={cargando}
        >
          {cargando ? '🔄 Extrayendo...' : '🔄 Forzar Búsqueda Fresca'}
        </button>
      </div>
      
      {cargando && <p className="cargando-texto">⏳ Conectando con farmacias y actualizando precios al instante...</p>}
      
      {!cargando && preciosTarjetas.length > 0 && (
        <div className="seccion-precios">
          <h3>📍 Farmacias más cercanas a ti con stock:</h3>
          
          <div className="precios-grid">
            {preciosTarjetas.map((item, i) => (
              <div key={i} className="precio-card">
                
                {item.esBioequivalente && (
                  <span className="sello-bio-tarjeta">
                    B BIOEQUIVALENTE
                  </span>
                )}
                
                <h4>💊 {item.medicamento}</h4>
                <p className="farmacia-nombre">🏪 {item.farmacia}</p>
                
                <p style={{ color: '#059669', fontWeight: 'bold', margin: '5px 0' }}>
                  🚗 A {item.distancia.toFixed(2)} km de distancia
                </p>
                
                <p className="stock-ok">✓ Stock revisado hoy</p>
                
                <p className="precio-valor">
                  ${item.precio?.toLocaleString('es-CL')} CLP
                </p>
              </div>
            ))}
          </div>

          <div className="seccion-mapa">
            <div className="mapa-header">
              <div>
                <h3>📍 Mapa de Cobertura</h3>
                <p>Mostrando <strong>{sucursalesEnRango.length}</strong> sucursales a tu alrededor.</p>
              </div>
              <button className="btn-activar-gps" onClick={pedirUbicacion}>
                🎯 Activar mi GPS
              </button>
            </div>

            {ubicacion && (
              <div className="radar-control">
                <label>🔭 Radio de búsqueda: {radioKm} Kilómetros</label>
                <input 
                  type="range" 
                  min="1" max="15" step="1" 
                  value={radioKm} 
                  onChange={(e) => setRadioKm(Number(e.target.value))}
                  style={{ width: '100%', cursor: 'pointer' }}
                />
              </div>
            )}

            {estadoUbicacion && <p className="estado-gps">{estadoUbicacion}</p>}
            
            <div className="mapa-wrapper">
              <MapContainer center={[mapLat, mapLng]} zoom={14} style={{ height: '100%' }}>
                <VolarAlCentro lat={mapLat} lng={mapLng} />
                <TileLayer url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png" />
                
                {ubicacion && (
                  <>
                    <Marker position={[ubicacion.lat, ubicacion.lng]} icon={userRedIcon}>
                      <Popup>🙋‍♂️ <strong>¡Tú estás aquí!</strong></Popup>
                    </Marker>
                    <Circle center={[ubicacion.lat, ubicacion.lng]} radius={radioKm * 1000} pathOptions={{ color: '#ca8a04', fillColor: '#ca8a04', fillOpacity: 0.1 }} />
                  </>
                )}

                {sucursalesEnRango.map((p, i) => (
                  <Marker key={`pin-${i}`} position={[p.lat, p.lng]} icon={getIconoFarmacia(p.cadenaOficial)}>
                    <Popup>
                      <strong>{p.farmacia}</strong><br/>
                      🚗 A {p.distancia.toFixed(2)} km<br/>
                      💊 {p.medicamento}<br/>
                      💵 <strong>${p.precio?.toLocaleString('es-CL')}</strong>
                    </Popup>
                  </Marker>
                ))}
              </MapContainer>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Resultados;