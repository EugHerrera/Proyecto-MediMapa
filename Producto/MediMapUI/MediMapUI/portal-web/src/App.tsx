import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Home from './Pages/Home';
import Resultados from './Pages/Resultados';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/resultados" element={<Resultados />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;