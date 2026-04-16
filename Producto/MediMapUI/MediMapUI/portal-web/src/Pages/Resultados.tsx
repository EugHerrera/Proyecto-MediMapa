import { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';

function Resultados() {
  // Esto atrapa la palabra que el usuario escribió en el Home (ej: ?q=Paracetamol)
  const [searchParams] = useSearchParams();
  const query = searchParams.get('q') || ''; 

  const [precios, setPrecios] = useState<any[]>([]);
  const [error, setError] = useState<string>("");

  useEffect(() => {
    fetch('http://localhost:8080/api/catalogo/precios')
      .then(response => {
        if (!response.ok) {
          throw new Error("Error en la red al intentar conectar");
        }
        return response.json(); 
      })
      .then(data => {
        // Filtro rápido para mostrar solo lo que buscaste
        if (query) {
           const filtrados = data.filter((item: any) => 
             item.medicamento?.nombreCanonico.toLowerCase().includes(query.toLowerCase())
           );
           setPrecios(filtrados);
        } else {
           setPrecios(data);
        }
      })
      .catch(err => setError(err.message));
  }, [query]);

  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif', maxWidth: '800px', margin: '0 auto' }}>
      {/* Botón para volver al buscador */}
      <Link to="/" style={{ textDecoration: 'none', color: '#0ea5e9', fontWeight: 'bold' }}>
        ⬅ Volver al buscador
      </Link>
      
      <h1 style={{ marginTop: '1rem' }}>🏥 Resultados para: "{query}"</h1>
      <p>Conectado en tiempo real al API Gateway (Puerto 8080) 🚦</p>

      {error && <p style={{ color: 'red', fontWeight: 'bold' }}>❌ Error: {error}</p>}
      
      {precios.length > 0 ? (
        <div style={{ marginTop: '2rem' }}>
          <h3>✅ Opciones encontradas: {precios.length}</h3>
          
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {precios.map((item, index) => (
              <div key={index} style={{ border: '1px solid #ccc', padding: '1rem', borderRadius: '8px', backgroundColor: '#f9f9f9' }}>
                <h4 style={{ margin: '0 0 0.5rem 0', color: '#2c3e50' }}>💊 {item.medicamento?.nombreCanonico}</h4>
                <p style={{ margin: '0.2rem 0' }}><strong>📍 Sucursal:</strong> {item.sucursal?.nombreSucursal}</p>
                <p style={{ margin: '0.2rem 0' }}><strong>🗺️ Dirección:</strong> {item.sucursal?.direccion}</p>
                <p style={{ margin: '0.2rem 0', color: '#27ae60', fontSize: '1.2rem', fontWeight: 'bold' }}>
                  💰 Precio: ${item.precio_max_vta} {item.moneda}
                </p>
              </div>
            ))}
          </div>
        </div>
      ) : (
        !error && <p>⏳ Buscando los mejores precios...</p>
      )}
    </div>
  )
}

export default Resultados;