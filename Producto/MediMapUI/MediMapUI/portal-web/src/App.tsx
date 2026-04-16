import { useEffect, useState } from 'react'

function App() {
  // Ahora esperamos un Array (lista) de datos, no un objeto
  const [precios, setPrecios] = useState<any[]>([]);
  const [error, setError] = useState<string>("");

  useEffect(() => {
    // Le pegamos a la ruta real de tu Gateway
    fetch('http://localhost:8080/api/catalogo/precios')
      .then(response => {
        if (!response.ok) {
          throw new Error("Error en la red al intentar conectar");
        }
        return response.json(); // Convertimos ese texto de tu captura a un objeto de Javascript
      })
      .then(data => setPrecios(data))
      .catch(err => setError(err.message));
  }, []);

  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif', maxWidth: '800px', margin: '0 auto' }}>
      <h1>🏥 MediMapa Frontend</h1>
      <p>Conectado en tiempo real a Microservicios 🚦</p>

      {error && <p style={{ color: 'red', fontWeight: 'bold' }}>❌ Error: {error}</p>}
      
      {precios.length > 0 ? (
        <div style={{ marginTop: '2rem' }}>
          <h3>✅ Medicamentos Encontrados en BD: {precios.length}</h3>
          
          {/* Mapeamos la lista real que vimos en tu foto */}
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
        !error && <p>⏳ Cargando precios desde el API Gateway...</p>
      )}
    </div>
  )
}

export default App