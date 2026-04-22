import { useEffect, useState, useRef } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { MapContainer, TileLayer, Marker, Popup, useMap, Circle } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

import icon from 'leaflet/dist/images/marker-icon.png';
import iconShadow from 'leaflet/dist/images/marker-shadow.png';
let DefaultIcon = L.icon({ iconUrl: icon, shadowUrl: iconShadow, iconSize: [25, 41], iconAnchor: [12, 41] });
L.Marker.prototype.options.icon = DefaultIcon;

// 🔥 FÓRMULA MATEMÁTICA PARA MEDIR DISTANCIAS REALES (Haversine)
function calcularDistanciaKm(lat1: number, lon1: number, lat2: number, lon2: number) {
  const R = 6371; // Radio de la Tierra en km
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
  
  const [preciosTarjetas, setPreciosTarjetas] = useState<any[]>([]); // Los 4 campeones
  const [todasLasSucursales, setTodasLasSucursales] = useState<any[]>([]); // TODOS los pines
  
  const [cargando, setCargando] = useState<boolean>(false);
  const [ubicacion, setUbicacion] = useState<{lat: number, lng: number} | null>(null);
  const [estadoUbicacion, setEstadoUbicacion] = useState<string>("");
  
  // 🔥 NUEVO ESTADO: Radio de búsqueda en Kilómetros
  const [radioKm, setRadioKm] = useState<number>(3); 

  useEffect(() => {
    if (!query) return;
    setCargando(true);
    fetch(`http://localhost:8082/api/scraper/buscar?query=${encodeURIComponent(query)}`)
      .then(r => r.json())
      .then(data => {
        const mejores: Record<string, any> = {};
        const pinesMapa: any[] = []; // Guardaremos TODOS aquí

        data.forEach((item: any) => {
          const cadena = (item.farmacia?.toLowerCase().includes("ahumada")) ? "Farmacias Ahumada" : 
                         (item.farmacia?.toLowerCase().includes("salco")) ? "Salcobrand" : 
                         (item.farmacia?.toLowerCase().includes("simi")) ? "Dr. Simi" : "Independiente";

          let lat = item.lat;
          let lng = item.lng;

          // Plan B si el robot viene sin GPS
          if (!lat || !lng) {
            if (cadena === "Farmacias Ahumada") { lat = -33.5413; lng = -70.5630; }
            else if (cadena === "Salcobrand") { lat = -33.5192; lng = -70.5975; }
            else if (cadena === "Dr. Simi") { lat = -33.5188; lng = -70.5984; }
            else { lat = -33.5255; lng = -70.5950; }
          }

          const sucursalLista = { ...item, lat, lng, cadenaOficial: cadena };
          
          // 1. Guardamos TODO para el mapa
          pinesMapa.push(sucursalLista);

          // 2. Filtramos solo los 4 más baratos para las tarjetas
          if (!mejores[cadena] || item.precio < mejores[cadena].precio) {
            mejores[cadena] = sucursalLista;
          }
        });

        setPreciosTarjetas(Object.values(mejores));
        setTodasLasSucursales(pinesMapa); // Enviamos los 16+ pines al mapa
        setCargando(false);
      });
  }, [query]);

  const pedirUbicacion = () => {
    setEstadoUbicacion("Buscando tu ubicación GPS... 🛰️");
    if (!navigator.geolocation) {
      setEstadoUbicacion("❌ Tu navegador no soporta geolocalización.");
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setUbicacion({ lat: position.coords.latitude, lng: position.coords.longitude });
        setEstadoUbicacion("✅ Ubicación encontrada. Mapa actualizado.");
      },
      (error) => {
        console.error(error);
        setEstadoUbicacion("❌ Permiso denegado. Asegúrate de darle permiso al navegador.");
      }
    );
  };

  const mapLat = ubicacion ? ubicacion.lat : -33.5212;
  const mapLng = ubicacion ? ubicacion.lng : -70.5973;
  const fechaHoy = new Date().toLocaleDateString('es-CL', { day: '2-digit', month: '2-digit', year: 'numeric' });

  // 🔥 LÓGICA DEL RADAR: Filtramos las sucursales que están dentro del radio elegido
  const sucursalesEnRango = todasLasSucursales.filter(p => {
    if (!ubicacion) return true; // Si no hay GPS, las mostramos todas
    const distancia = calcularDistanciaKm(ubicacion.lat, ubicacion.lng, p.lat, p.lng);
    return distancia <= radioKm;
  });

  return (
    <div style={{ padding: '2rem', fontFamily: 'Inter, sans-serif', maxWidth: '1000px', margin: '0 auto' }}>
      <Link to="/" style={{ textDecoration: 'none', color: '#059669', fontWeight: 'bold' }}>⬅ Volver</Link>
      <h1 style={{ color: '#0f172a' }}>🏥 Resultados para: "{query}"</h1>
      
      {cargando && <p style={{ color: '#059669', fontWeight: 'bold', textAlign: 'center' }}>⏳ Buscando precios y ubicaciones...</p>}
      
      {!cargando && preciosTarjetas.length > 0 && (
        <div style={{ marginTop: '1rem' }}>
          <h3 style={{ color: '#1e293b' }}>✅ Precios más bajos por cadena:</h3>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1rem' }}>
            {preciosTarjetas.map((item, i) => (
              <div key={i} style={{ border: '1px solid #bbf7d0', padding: '1.5rem', borderRadius: '16px', backgroundColor: '#f0fdf4', position: 'relative' }}>
                {item.esBioequivalente && (
                  <div style={{ backgroundColor: '#fde047', color: '#854d0e', fontSize: '0.6rem', padding: '3px 8px', borderRadius: '10px', fontWeight: '900', position: 'absolute', top: '10px', right: '10px', border: '1px solid #ca8a04' }}>
                    BIOEQUIVALENTE
                  </div>
                )}
                <h4 style={{ margin: '0 0 0.5rem 0', color: '#166534' }}>💊 {item.medicamento}</h4>
                <p style={{ fontSize: '0.9rem' }}><strong>🏪 {item.farmacia}</strong></p>
                <p style={{ margin: '0.2rem 0', color: '#16a34a', fontSize: '0.8rem' }}>✓ Stock: <strong>{fechaHoy}</strong></p>
                <p style={{ fontSize: '1.5rem', fontWeight: '800', color: '#166534', marginTop: '10px' }}>${item.precio?.toLocaleString('es-CL')} CLP</p>
              </div>
            ))}
          </div>

          <div style={{ marginTop: '3rem', borderTop: '2px solid #e2e8f0', paddingTop: '2rem' }}>
            
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '15px' }}>
              <div>
                <h3 style={{ color: '#1e293b', margin: '0 0 5px 0' }}>📍 Farmacias cercanas a tu ubicación</h3>
                <p style={{ color: '#64748b', fontSize: '0.9rem', margin: 0 }}>
                  Mostrando <strong>{sucursalesEnRango.length}</strong> sucursales a tu alrededor.
                </p>
              </div>
              <button onClick={pedirUbicacion} style={{ backgroundColor: '#2563eb', color: 'white', border: 'none', padding: '10px 20px', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold' }}>
                🎯 Activar mi GPS
              </button>
            </div>

            {/* 🔥 CONTROL DEL RADAR (Rango de búsqueda) */}
            {ubicacion && (
              <div style={{ marginTop: '20px', padding: '15px', backgroundColor: '#f8fafc', borderRadius: '12px', border: '1px solid #e2e8f0' }}>
                <label style={{ fontWeight: 'bold', color: '#334155', display: 'block', marginBottom: '10px' }}>
                  🔭 Radio de búsqueda: {radioKm} Kilómetros
                </label>
                <input 
                  type="range" 
                  min="1" max="15" step="1" 
                  value={radioKm} 
                  onChange={(e) => setRadioKm(Number(e.target.value))}
                  style={{ width: '100%', cursor: 'pointer' }}
                />
              </div>
            )}

            {estadoUbicacion && <div style={{ marginTop: '10px', fontSize: '0.9rem', color: '#059669', fontWeight: '500' }}>{estadoUbicacion}</div>}
            
            <div style={{ width: '100%', height: '450px', borderRadius: '16px', overflow: 'hidden', border: '1px solid #cbd5e1', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)', marginTop: '20px' }}>
              <MapContainer center={[mapLat, mapLng]} zoom={13} style={{ height: '100%' }}>
                <VolarAlCentro lat={mapLat} lng={mapLng} />
                <TileLayer url="https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png" />
                
                {/* Dibuja un círculo azul tenue alrededor del usuario para mostrar el área de búsqueda */}
                {ubicacion && (
                  <>
                    <Marker position={[ubicacion.lat, ubicacion.lng]}>
                      <Popup>🙋‍♂️ <strong>¡Tú estás aquí!</strong></Popup>
                    </Marker>
                    <Circle center={[ubicacion.lat, ubicacion.lng]} radius={radioKm * 1000} pathOptions={{ color: '#3b82f6', fillColor: '#3b82f6', fillOpacity: 0.1 }} />
                  </>
                )}

                {/* Dibuja solo las sucursales que pasaron el filtro del radar */}
                {sucursalesEnRango.map((p, i) => (
                  <Marker key={`pin-${i}`} position={[p.lat, p.lng]}>
                    <Popup>
                      <strong>{p.farmacia}</strong><br/>
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