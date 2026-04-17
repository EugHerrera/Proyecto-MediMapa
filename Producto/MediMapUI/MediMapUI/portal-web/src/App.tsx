import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from './Components/Navbar'; // <-- IMPORTAMOS LA NAVBAR AQUÍ
import Home from './Pages/Home';
import Resultados from './Pages/Resultados';
import Login from './Pages/Login';
import RegistroFarmacia from './Pages/RegistroFarmacia';

function App() {
  return (
    <BrowserRouter>
      {/* La Navbar va aquí: ¡Así se mantiene fija en todas las pantallas! */}
      <Navbar /> 
      
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/resultados" element={<Resultados />} />
        <Route path="/login" element={<Login />} />
        <Route path="/registro-farmacia" element={<RegistroFarmacia />} />
        
        {/* Agregamos estas rutas provisorias para que los botones nuevos de la Navbar funcionen al hacer clic */}
        <Route path="/catalogo" element={<div style={{padding: '50px', textAlign: 'center', marginTop: '20px'}}><h2>Catálogo en construcción... 🚧</h2></div>} />
        <Route path="/bioequivalentes" element={<div style={{padding: '50px', textAlign: 'center', marginTop: '20px'}}><h2>Alternativas Bioequivalentes en construcción... 🚧</h2></div>} />
        <Route path="/red-farmacias" element={<div style={{padding: '50px', textAlign: 'center', marginTop: '20px'}}><h2>Red de Farmacias en construcción... 🚧</h2></div>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;