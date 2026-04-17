import { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';

function Resultados() {
  const [searchParams] = useSearchParams();
  const query = searchParams.get('q') || ''; 

  const [precios, setPrecios] = useState<any[]>([]);
  const [error, setError] = useState<string>("");
  const [cargando, setCargando] = useState<boolean>(false);

  useEffect(() => {
    if (!query) return;

    setCargando(true);
    setError("");

    fetch(`http://localhost:8080/api/catalogo/precios?nombre=${encodeURIComponent(query)}`)
      .then(response => {
        if (!response.ok) {
          throw new Error("No se encontraron resultados para ese medicamento o principio activo.");
        }
        return response.json(); 
      })
      .then(data => {
        if (data.resultados && data.resultados.length > 0) {
            setPrecios(data.resultados);
        } else {
            setPrecios([]);
            throw new Error("No hay precios registrados actualmente.");
        }
      })
      .catch(err => {
        setPrecios([]);
        setError(err.message);
      })
      .finally(() => setCargando(false));
  }, [query]);

  return (
    <div style={{ padding: '2rem', fontFamily: "'Inter', sans-serif", maxWidth: '900px', margin: '0 auto' }}>
      <Link to="/" style={{ textDecoration: 'none', color: '#0ea5e9', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '5px' }}>
        ⬅ Volver al buscador
      </Link>
      
      <h1 style={{ marginTop: '1.5rem', color: '#0f172a' }}>🏥 Resultados para: "{query}"</h1>
      <p style={{ color: '#64748b', fontSize: '0.9rem' }}>Consulta procesada por API Gateway MediMapa 🚦</p>

      {error && (
        <div style={{ backgroundColor: '#fef2f2', padding: '1.2rem', borderRadius: '12px', borderLeft: '6px solid #ef4444', marginTop: '1.5rem' }}>
          <p style={{ color: '#b91c1c', fontWeight: 'bold', margin: 0 }}>{error}</p>
          <p style={{ color: '#7f1d1d', fontSize: '0.85rem', marginTop: '5px' }}>Sugerencia: Intenta buscar por el nombre genérico o revisa si el Scraper está activo.</p>
        </div>
      )}

      {cargando && <p style={{ textAlign: 'center', marginTop: '3rem', color: '#0ea5e9', fontWeight: 'bold' }}>⏳ Buscando los mejores precios...</p>}
      
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
                alignItems: 'center'
              }}>
                <div>
                  <h4 style={{ margin: '0 0 0.5rem 0', color: '#0f172a', fontSize: '1.25rem' }}>💊 {item.medicamento?.nombreCanonico}</h4>
                  <p style={{ margin: '0.2rem 0', color: '#475569' }}><strong>🏪 Farmacia:</strong> {item.sucursal?.nombreSucursal}</p>
                  <p style={{ margin: '0.2rem 0', color: '#64748b', fontSize: '0.9rem' }}>📍 {item.sucursal?.direccion}</p>
                </div>

                <div style={{ textAlign: 'right' }}>
                  <p style={{ margin: 0, color: '#059669', fontSize: '1.8rem', fontWeight: '800' }}>
                    ${(item.precioMaxVta || item.precio_max_vta).toLocaleString('es-CL')}
                  </p>
                  <span style={{ fontSize: '0.8rem', color: '#94a3b8', fontWeight: 'bold' }}>{item.moneda || 'CLP'}</span>
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