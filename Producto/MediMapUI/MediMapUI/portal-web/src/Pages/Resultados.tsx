import { useEffect, useState, useRef } from 'react';
import { useSearchParams, Link } from 'react-router-dom';

function Resultados() {
  const [searchParams] = useSearchParams();
  const query = searchParams.get('q') || ''; 

  const [precios, setPrecios] = useState<any[]>([]);
  const [error, setError] = useState<string>("");
  const [cargando, setCargando] = useState<boolean>(false);

  // 🔥 EL ESCUDO: Creamos una memoria para recordar la última búsqueda
  const ultimaBusqueda = useRef<string>("");

  useEffect(() => {
    if (!query) return;

    // 🔥 LA DEFENSA: Si React intenta buscar exactamente lo mismo dos veces seguidas, lo detenemos.
    if (ultimaBusqueda.current === query) return;
    
    // Registramos en la memoria que ya empezamos a buscar esta palabra
    ultimaBusqueda.current = query;

    setCargando(true);
    setError("");

    // 1. AHORA SÍ LLAMAMOS A LA RUTA DEL SCRAPER EN VIVO
    fetch(`http://localhost:8080/api/scraper/buscar?query=${encodeURIComponent(query)}`)
      .then(response => {
        if (!response.ok) {
          throw new Error("No se encontraron resultados para ese medicamento o principio activo.");
        }
        return response.json(); 
      })
      .then(data => {
        // 2. EL BACKEND AHORA DEVUELVE UN ARREGLO DIRECTO (List<Map>)
        if (Array.isArray(data) && data.length > 0) {
            setPrecios(data);
        } else {
            setPrecios([]);
            throw new Error("El motor no encontró stock o precios para este medicamento en las farmacias.");
        }
      })
      .catch(err => {
        setPrecios([]);
        setError(err.message);
      })
      .finally(() => {
        setCargando(false);
        // Opcional: Limpiamos la memoria al terminar por si el usuario quiere forzar la misma búsqueda después
        ultimaBusqueda.current = ""; 
      });
  }, [query]);

  return (
    <div style={{ padding: '2rem', fontFamily: "'Inter', sans-serif", maxWidth: '900px', margin: '0 auto' }}>
      <Link to="/" style={{ textDecoration: 'none', color: '#059669', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '5px' }}>
        ⬅ Volver al buscador
      </Link>
      
      <h1 style={{ marginTop: '1.5rem', color: '#0f172a' }}>🏥 Resultados para: "{query}"</h1>
      <p style={{ color: '#64748b', fontSize: '0.9rem' }}>Consulta procesada en vivo por el Motor Scraper MediMapa 🤖</p>

      {error && (
        <div style={{ backgroundColor: '#fef2f2', padding: '1.2rem', borderRadius: '12px', borderLeft: '6px solid #ef4444', marginTop: '1.5rem' }}>
          <p style={{ color: '#b91c1c', fontWeight: 'bold', margin: 0 }}>{error}</p>
          <p style={{ color: '#7f1d1d', fontSize: '0.85rem', marginTop: '5px' }}>Sugerencia: Intenta buscar por el nombre genérico o revisa la consola de Spring Boot.</p>
        </div>
      )}

      {cargando && (
        <div style={{ textAlign: 'center', marginTop: '3rem' }}>
           <p style={{ color: '#059669', fontWeight: 'bold', fontSize: '1.2rem' }}>⏳ Encendiendo motores y buscando en las farmacias...</p>
           <p style={{ color: '#64748b', fontSize: '0.9rem' }}>Esto puede tomar unos segundos.</p>
        </div>
      )}
      
      {!cargando && precios.length > 0 && (
        <div style={{ marginTop: '2rem' }}>
          <h3 style={{ color: '#1e293b', marginBottom: '1.5rem' }}>✅ Se encontraron {precios.length} opciones disponibles:</h3>
          
          <div style={{ display: 'grid', gap: '1.5rem' }}>
            {precios.map((item, index) => (
              <div key={index} style={{ 
                border: '1px solid #e2e8f0', 
                padding: '1.5rem', 
                borderRadius: '16px', 
                backgroundColor: '#ffffff', 
                boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                transition: 'transform 0.2s ease',
              }}>
                <div>
                  {/* 3. LEEMOS LOS DATOS EXACTOS QUE MANDA EL MAPA DE JAVA */}
                  <h4 style={{ margin: '0 0 0.5rem 0', color: '#0f172a', fontSize: '1.25rem', textTransform: 'capitalize' }}>💊 {item.medicamento}</h4>
                  <p style={{ margin: '0.2rem 0', color: '#475569' }}><strong>🏪 Farmacia:</strong> {item.farmacia}</p>
                  <p style={{ margin: '0.2rem 0', color: '#059669', fontSize: '0.85rem', fontWeight: 'bold' }}>✓ Precio verificado en tiempo real</p>
                </div>

                <div style={{ textAlign: 'right' }}>
                  <p style={{ margin: 0, color: '#059669', fontSize: '1.8rem', fontWeight: '800' }}>
                    ${item.precio?.toLocaleString('es-CL')}
                  </p>
                  <span style={{ fontSize: '0.8rem', color: '#94a3b8', fontWeight: 'bold' }}>CLP</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

export default Resultados;