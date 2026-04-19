import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from './Components/Navbar'; 
import Home from './Pages/Home';
import Resultados from './Pages/Resultados';
import Login from './Pages/Login';
import RegistroFarmacia from './Pages/RegistroFarmacia';
import FAQ from './Pages/FAQ';
import Catalogo from './Pages/Catalogo';
import RedFarmacias from './Pages/RedFarmacias'; 

function App() {
  return (
    <BrowserRouter>
      {/* La Navbar se mantiene fija en todas las pantallas */}
      <Navbar /> 
      
      <Routes>
        {/* Pantalla de Inicio */}
        <Route path="/" element={<Home />} />
        
        {/* Pantalla de Resultados de Búsqueda */}
        <Route path="/resultados" element={<Resultados />} />
        
        {/* Módulo B2B y Login */}
        <Route path="/login" element={<Login />} />
        <Route path="/registro-farmacia" element={<RegistroFarmacia />} />
        
        {/* Red de Farmacias (La que tiene los logos y el mapa) */}
        <Route path="/red-farmacias" element={<RedFarmacias />} />
        
        {/* Preguntas Frecuentes (FAQ) */}
        <Route path="/faq" element={<FAQ />} />

        {/* CATÁLOGO REAL: Eliminamos la versión "en construcción" para que use tu componente */}
        <Route path="/catalogo" element={<Catalogo />} />

        {/* Bioequivalentes: Por ahora lo dejamos como aviso, 
            pero puedes crear un 'Bioequivalentes.tsx' similar al catálogo más adelante */}
        <Route path="/bioequivalentes" element={
          <div style={{padding: '100px', textAlign: 'center'}}>
            <h2>✨ Sección de Bioequivalentes en desarrollo</h2>
            <p>Aquí mostraremos solo las alternativas certificadas por el ISP.</p>
          </div>
        } />
        
      </Routes>
    </BrowserRouter>
  );
}

export default App;