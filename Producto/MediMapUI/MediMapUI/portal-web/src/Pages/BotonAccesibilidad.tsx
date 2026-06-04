import { useState, useEffect } from 'react';

const BotonAccesibilidad = () => {
  // Inicializamos el estado leyendo el localStorage por si el usuario ya lo había cambiado antes
  const [nivelLetra, setNivelLetra] = useState<number>(() => {
    const guardado = localStorage.getItem('nivelLetra');
    return guardado ? parseInt(guardado) : 0;
  });

  // Este useEffect "vigila" el nivelLetra. Cada vez que cambia, altera el HTML real de la página
  useEffect(() => {
    const html = document.documentElement; // Esto selecciona la etiqueta <html>
    
    // Primero limpiamos las clases
    html.classList.remove('letra-grande', 'letra-extragrande');

    // Luego aplicamos la correspondiente
    if (nivelLetra === 1) {
      html.classList.add('letra-grande');
    } else if (nivelLetra === 2) {
      html.classList.add('letra-extragrande');
    }

    // Guardamos la preferencia en el navegador
    localStorage.setItem('nivelLetra', nivelLetra.toString());
  }, [nivelLetra]);

  const cambiarTamano = () => {
    // Ciclo de 3 pasos: 0 -> 1 -> 2 -> 0
    setNivelLetra((prev) => (prev >= 2 ? 0 : prev + 1));
  };

  // El texto del botón cambia según el nivel
  const textoBoton = nivelLetra === 0 ? 'A+' : nivelLetra === 1 ? 'A++' : 'A';

  return (
    <button 
      onClick={cambiarTamano} 
      className="btn-accesibilidad" 
      title="Cambiar tamaño de letra"
    >
      {textoBoton}
    </button>
  );
};

export default BotonAccesibilidad;