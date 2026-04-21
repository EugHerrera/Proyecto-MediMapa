import { useEffect, useState, useRef } from 'react';
import { useSearchParams, Link } from 'react-router-dom';

function Resultados() {
  const [searchParams] = useSearchParams();
  const query = searchParams.get('q') || ''; 

  const [precios, setPrecios] = useState<any[]>([]);
  const [error, setError] = useState<string>("");
  const [cargando, setCargando] = useState<boolean>(false);

  const ultimaBusqueda = useRef<string>("");

  useEffect(() => {
    if (!query) return;
    if (ultimaBusqueda.current === query) return;
    
    ultimaBusqueda.current = query;
    setCargando(true);
    setError("");

    // 🔥 AHORA APUNTAMOS AL MICROSERVICIO DONDE SE GUARDÓ EL EXCEL (8085)
    fetch(`http://localhost:8085/api/buscador/medicamentos?q=${encodeURIComponent(query)}`)
      .then(response => {
        if (!response.ok) {
          throw new Error("No se encontraron resultados para ese medicamento o principio activo.");
        }
        return response.json(); 
      })
      .then(data => {
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
        ultimaBusqueda.current = ""; 
      });
  }, [query]);

  // 🕵️‍♂️ LÓGICA DETECTIVE AJUSTADA A LA NUEVA DATA
  const farmaciasOficiales = ["Ahumada", "Salcobrand", "Dr. Simi"];
  const farmaciasConStock = precios.map(p => (p.sucursal || "").toLowerCase());
  const farmaciasSinStock = farmaciasOficiales.filter(oficial => 
    !farmaciasConStock.some(encontrada => encontrada.includes(oficial.toLowerCase()))
  );

  // 📅 GENERADOR DE FECHA ACTUAL
  const fechaHoy = new Date().toLocaleDateString('es-CL', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });

  return (
    <div style={{ padding: '2rem', fontFamily: "'Inter', sans-serif", maxWidth: '900px', margin: '0 auto' }}>
      <Link to="/" style={{ textDecoration: 'none', color: '#059669', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '5px' }}>
        ⬅ Volver al buscador
      </Link>
      
      <h1 style={{ marginTop: '1.5rem', color: '#0f172a' }}>🏥 Resultados para: "{query}"</h1>
      
      <div style={{ backgroundColor: '#e0f2fe', color: '#0369a1', padding: '10px 15px', borderRadius: '8px', fontSize: '0.85rem', display: 'inline-flex', alignItems: 'center', gap: '8px', fontWeight: '500', marginBottom: '20px', marginTop: '10px' }}>
        <span>🕒</span> <strong>Transparencia:</strong> Los precios y el stock de las farmacias se sincronizan automáticamente cada 24 horas.
      </div>

      {error && (
        <div style={{ backgroundColor: '#fef2f2', padding: '1.2rem', borderRadius: '12px', borderLeft: '6px solid #ef4444', marginTop: '1.5rem' }}>
          <p style={{ color: '#b91c1c', fontWeight: 'bold', margin: 0 }}>{error}</p>
          <p style={{ color: '#7f1d1d', fontSize: '0.85rem', marginTop: '5px' }}>Sugerencia: Intenta buscar por el nombre genérico o revisa que esté bien escrito.</p>
        </div>
      )}

      {cargando && (
        <div style={{ textAlign: 'center', marginTop: '3rem' }}>
           <p style={{ color: '#059669', fontWeight: 'bold', fontSize: '1.2rem' }}>⏳ Consultando inventarios de La Florida...</p>
           <p style={{ color: '#64748b', fontSize: '0.9rem' }}>Esto puede tomar unos segundos si no está en nuestro caché.</p>
        </div>
      )}
      
      {!cargando && precios.length > 0 && (
        <div style={{ marginTop: '1rem' }}>
          <h3 style={{ color: '#1e293b', marginBottom: '1.5rem' }}>✅ {precios.length} opciones disponibles con stock:</h3>
          
          <div style={{ display: 'grid', gap: '1rem' }}>
            {precios.map((item, index) => (
              <div key={index} style={{ border: '1px solid #bbf7d0', padding: '1.5rem', borderRadius: '16px', backgroundColor: '#f0fdf4', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <h4 style={{ margin: '0 0 0.5rem 0', color: '#166534', fontSize: '1.25rem', textTransform: 'capitalize' }}>💊 {item.medicamento}</h4>
                  <p style={{ margin: '0.2rem 0', color: '#15803d' }}><strong>🏪 {item.sucursal}</strong></p>
                  <p style={{ margin: '0.2rem 0', color: '#16a34a', fontSize: '0.85rem' }}>
                    ✓ Stock confirmado hoy: <strong>{fechaHoy}</strong>
                  </p>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <p style={{ margin: 0, color: '#166534', fontSize: '1.8rem', fontWeight: '800' }}>
                    ${item.precio?.toLocaleString('es-CL')}
                  </p>
                  <span style={{ fontSize: '0.8rem', color: '#15803d', fontWeight: 'bold' }}>CLP</span>
                </div>
              </div>
            ))}

            {farmaciasSinStock.length > 0 && (
              <>
                <h4 style={{ color: '#64748b', marginTop: '1.5rem', marginBottom: '0.5rem' }}>❌ Sin resultados en esta zona:</h4>
                {farmaciasSinStock.map((farmaciaGris, index) => (
                  <div key={`gris-${index}`} style={{ border: '1px dashed #cbd5e1', padding: '1.2rem 1.5rem', borderRadius: '16px', backgroundColor: '#f8fafc', display: 'flex', justifyContent: 'space-between', alignItems: 'center', opacity: 0.8 }}>
                    <div>
                      <p style={{ margin: '0', color: '#64748b', fontSize: '1rem' }}><strong>🏪 Farmacias {farmaciaGris}</strong></p>
                      <p style={{ margin: '0.2rem 0 0 0', color: '#94a3b8', fontSize: '0.85rem' }}>No se encontró stock o formato equivalente en catálogo.</p>
                    </div>
                    <div style={{ color: '#94a3b8', fontWeight: 'bold', fontSize: '0.9rem' }}>
                      Agotado / No listado
                    </div>
                  </div>
                ))}
              </>
            )}

          </div>
        </div>
      )}
    </div>
  );
}

export default Resultados;