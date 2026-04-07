import { useState } from 'react';
import { Link } from 'react-router-dom';

const Navbar = () => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <nav className="bg-gray-800 p-4 text-white">
      <div className="container mx-auto flex justify-between items-center">
        {/* Logo */}
        <Link to="/" className="text-2xl font-bold">MiSitio</Link>

        {/* Botón Hamburguesa (Responsivo) */}
        <button onClick={() => setIsOpen(!isOpen)} className="md:hidden">
          {isOpen ? '✕' : '☰'}
        </button>

        {/* Enlaces (Escritorio) */}
        <div className={`md:flex ${isOpen ? 'block' : 'hidden'} absolute md:relative top-16 md:top-0 left-0 w-full md:w-auto bg-gray-800 md:bg-transparent p-4 md:p-0`}>
          <Link to="/" className="block md:inline-block p-2">Inicio</Link>
          <Link to="/productos" className="block md:inline-block p-2">Productos</Link>
          <Link to="/contacto" className="block md:inline-block p-2">Contacto</Link>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;